package com.apexgym.personaltraining.service;

import com.apexgym.auth.persistence.User;
import com.apexgym.auth.persistence.UserRepository;
import com.apexgym.personaltraining.dto.*;
import com.apexgym.personaltraining.persistence.*;
import com.apexgym.profile.persistence.Membership;
import com.apexgym.profile.persistence.MembershipPlan;
import com.apexgym.profile.persistence.MembershipRepository;
import com.apexgym.staff.persistence.Trainer;
import com.apexgym.staff.persistence.TrainerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PersonalTrainingService {

    private static final int ELITE_MONTHLY_SESSION_LIMIT = 4;

    private final PTSessionRepository sessionRepository;
    private final TrainerRepository trainerRepository;
    private final UserRepository userRepository;
    private final MembershipRepository membershipRepository;

    @Transactional
    public PTSessionDTO bookSession(String userEmail, BookSessionRequest request) {
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Membership membership = membershipRepository.findByUserId(user.getId())
                .orElseThrow(() -> new AccessDeniedException("ELITE membership required for personal training"));

        if (membership.getPlan() != MembershipPlan.ELITE) {
            throw new AccessDeniedException("ELITE membership required for personal training");
        }

        LocalDateTime monthStart = LocalDateTime.now().withDayOfMonth(1).withHour(0).withMinute(0).withSecond(0);
        long bookedThisMonth = sessionRepository.countBookedThisMonth(user.getId(), monthStart);
        if (bookedThisMonth >= ELITE_MONTHLY_SESSION_LIMIT) {
            throw new IllegalStateException("Monthly personal training session limit reached");
        }

        Trainer trainer = trainerRepository.findById(request.trainerId())
                .orElseThrow(() -> new RuntimeException("Trainer not found"));

        int duration = request.durationMinutes() != null ? request.durationMinutes() : 60;
        LocalDateTime end = request.scheduledAt().plusMinutes(duration);

        List<PTSession> overlaps = sessionRepository.findOverlapping(
                trainer.getId(), request.scheduledAt(), end);
        if (!overlaps.isEmpty()) {
            throw new IllegalStateException("Trainer is not available at this time");
        }

        PTSession session = PTSession.builder()
                .trainer(trainer)
                .user(user)
                .scheduledAt(request.scheduledAt())
                .durationMinutes(duration)
                .goalFocus(request.goalFocus())
                .status(SessionStatus.SCHEDULED)
                .build();

        return toDto(sessionRepository.save(session));
    }

    @Transactional
    public void cancelSession(String userEmail, Long sessionId) {
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new RuntimeException("User not found"));

        PTSession session = sessionRepository.findById(sessionId)
                .orElseThrow(() -> new RuntimeException("Session not found"));

        if (!session.getUser().getId().equals(user.getId())) {
            throw new AccessDeniedException("Unauthorized");
        }
        if (session.getStatus() != SessionStatus.SCHEDULED) {
            throw new IllegalStateException("Session cannot be cancelled");
        }

        session.setStatus(SessionStatus.CANCELLED);
        session.setCancelledAt(LocalDateTime.now());
        sessionRepository.save(session);
    }

    @Transactional
    public PTSessionDTO completeSession(String trainerEmail, Long sessionId) {
        Trainer trainer = trainerRepository.findByUserEmail(trainerEmail)
                .orElseThrow(() -> new RuntimeException("Trainer not found"));

        PTSession session = sessionRepository.findById(sessionId)
                .orElseThrow(() -> new RuntimeException("Session not found"));

        if (!session.getTrainer().getId().equals(trainer.getId())) {
            throw new AccessDeniedException("Unauthorized");
        }

        session.setStatus(SessionStatus.COMPLETED);
        return toDto(sessionRepository.save(session));
    }

    @Transactional(readOnly = true)
    public List<PTSessionDTO> getMySessions(String userEmail) {
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new RuntimeException("User not found"));
        return sessionRepository.findByUserIdOrderByScheduledAtDesc(user.getId())
                .stream().map(this::toDto).collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<PTSessionDTO> getTrainerSessions(String trainerEmail) {
        Trainer trainer = trainerRepository.findByUserEmail(trainerEmail)
                .orElseThrow(() -> new RuntimeException("Trainer not found"));
        return sessionRepository.findByTrainerIdOrderByScheduledAtDesc(trainer.getId())
                .stream().map(this::toDto).collect(Collectors.toList());
    }

    private PTSessionDTO toDto(PTSession s) {
        return PTSessionDTO.builder()
                .id(s.getId())
                .trainerId(s.getTrainer().getId())
                .trainerName(s.getTrainer().getFullName())
                .userId(s.getUser().getId())
                .userName(s.getUser().getFirstName() + " " + s.getUser().getLastName())
                .scheduledAt(s.getScheduledAt())
                .durationMinutes(s.getDurationMinutes())
                .status(s.getStatus().name())
                .goalFocus(s.getGoalFocus())
                .build();
    }
}