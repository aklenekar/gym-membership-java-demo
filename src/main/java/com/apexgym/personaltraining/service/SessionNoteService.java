package com.apexgym.personaltraining.service;

import com.apexgym.personaltraining.dto.CreateSessionNoteRequest;
import com.apexgym.personaltraining.dto.SessionNoteDTO;
import com.apexgym.personaltraining.persistence.*;
import com.apexgym.staff.persistence.Trainer;
import com.apexgym.staff.persistence.TrainerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SessionNoteService {

    private final SessionNoteRepository noteRepository;
    private final PTSessionRepository sessionRepository;
    private final TrainerRepository trainerRepository;

    @Transactional
    public SessionNoteDTO addNote(String trainerEmail, Long sessionId, CreateSessionNoteRequest request) {
        Trainer trainer = trainerRepository.findByUserEmail(trainerEmail)
                .orElseThrow(() -> new RuntimeException("Trainer not found"));

        PTSession session = sessionRepository.findById(sessionId)
                .orElseThrow(() -> new RuntimeException("Session not found"));

        if (!session.getTrainer().getId().equals(trainer.getId())) {
            throw new AccessDeniedException("Unauthorized");
        }

        SessionNote note = SessionNote.builder()
                .session(session)
                .content(request.content())
                .build();

        return toDto(noteRepository.save(note));
    }

    @Transactional(readOnly = true)
    public List<SessionNoteDTO> getNotesForSession(Long sessionId) {
        return noteRepository.findBySessionIdOrderByCreatedAtDesc(sessionId)
                .stream().map(this::toDto).collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<SessionNoteDTO> getNotesForUser(Long userId) {
        return noteRepository.findBySession_User_IdOrderByCreatedAtDesc(userId)
                .stream().map(this::toDto).collect(Collectors.toList());
    }

    private SessionNoteDTO toDto(SessionNote n) {
        return SessionNoteDTO.builder()
                .id(n.getId())
                .sessionId(n.getSession().getId())
                .content(n.getContent())
                .createdAt(n.getCreatedAt())
                .build();
    }
}