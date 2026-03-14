package com.apexgym.service;

import com.apexgym.dto.ClassAttendance;
import com.apexgym.dto.GymClassDTO;
import com.apexgym.entity.BookingStatus;
import com.apexgym.entity.ClassBooking;
import com.apexgym.entity.GymClass;
import com.apexgym.entity.User;
import com.apexgym.mapper.GymClassMapper;
import com.apexgym.repository.ClassBookingRepository;
import com.apexgym.repository.GymClassRepository;
import com.apexgym.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class GymClassService {

    private final GymClassRepository gymClassRepository;
    private final ClassBookingRepository classBookingRepository;
    private final UserRepository userRepository;
    private final GymClassMapper gymClassMapper;

    public List<GymClassDTO> findAll() {
        return gymClassRepository.findAll().stream()
                .map(gymClassMapper::toDTO)
                .collect(Collectors.toList());
    }

    public List<GymClass> findUpcomingClasses(LocalDateTime now) {
        return gymClassRepository.findUpcomingClasses(now);
    }

    public List<GymClassDTO> findByFilters(String category, String instructor, String day, String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Specification<GymClass> spec = (root, query, criteriaBuilder) -> criteriaBuilder.conjunction();
        LocalDateTime currentTime = LocalDateTime.now();

        if (category != null && !category.isBlank() && !"all".equalsIgnoreCase(category)) {
            spec = spec.and((root, query, cb) ->
                    cb.equal(cb.lower(root.get("category")), category.toLowerCase()));
        }

        if (instructor != null && !instructor.isBlank() && !"all".equalsIgnoreCase(instructor)) {
            spec = spec.and((root, query, cb) ->
                    cb.equal(cb.lower(root.get("instructorName")), instructor.toLowerCase()));
        }

        if (day != null && !day.isBlank() && !"all".equalsIgnoreCase(day)) {
            LocalDateTime finalStart;
            LocalDateTime finalEnd;
            switch (day.toLowerCase()) {
                case "today" -> {
                    finalStart = currentTime.with(LocalTime.MIN);
                    finalEnd = finalStart.with(LocalTime.MAX);
                }
                case "tomorrow" -> {
                    finalStart = currentTime.plusDays(1).with(LocalTime.MIN);
                    finalEnd = finalStart.with(LocalTime.MAX);
                }
                case "week" -> {
                    finalStart = currentTime.with(LocalTime.MIN);
                    finalEnd = finalStart.plusDays(7).with(LocalTime.MAX);
                }
                default -> {
                    finalStart = null;
                    finalEnd = null;
                }
            }

            if (finalStart != null) {
                spec = spec.and((root, query, cb) ->
                        cb.between(root.get("classDate"), finalStart, finalEnd)
                );
            }
        }

        List<GymClass> gymClasses = gymClassRepository.findAll(spec);

        List<ClassBooking> userBookings = classBookingRepository
                .findByUserIdAndStatusAndGymClass_ClassDateAfterOrderByGymClass_ClassDate(user.getId(), BookingStatus.BOOKED, currentTime);

        Map<Long, ClassBooking> bookingClassIds = userBookings.stream()
                .collect(Collectors.toMap(cb -> cb.getGymClass().getId(), cb -> cb));

        return gymClasses.stream().map(entity -> {
            GymClassDTO dto = gymClassMapper.toDTO(entity);
            if (bookingClassIds.containsKey(dto.id())) {
                return GymClassDTO.builder()
                        .id(dto.id())
                        .name(dto.name())
                        .instructor(dto.instructor())
                        .location(dto.location())
                        .startTime(dto.startTime())
                        .durationMin(dto.durationMin())
                        .capacity(dto.capacity())
                        .booked(dto.booked())
                        .spotsInfo(dto.spotsInfo())
                        .category(dto.category())
                        .isBooked(true)
                        .bookingId(bookingClassIds.get(dto.id()).getId())
                        .build();
            }
            return dto;
        }).toList();
    }

    public GymClassDTO getById(Long id) {
        return gymClassRepository.findById(id).map(gymClassMapper::toDTO)
                .orElseThrow(() -> new EntityNotFoundException("Class not found with id " + id));
    }

    public List<ClassAttendance> getAttendanceHistory(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        LocalDateTime thirtyDaysAgo = LocalDateTime.now().minusDays(30);

        return classBookingRepository.findByUserIdAndStatusAndBookedAtAfter(
                        user.getId(),
                        BookingStatus.COMPLETED,
                        thirtyDaysAgo
                )
                .stream()
                .map(booking -> ClassAttendance.builder()
                        .className(booking.getGymClass().getName())
                        .category(booking.getGymClass().getCategory().getType())
                        .instructor(booking.getGymClass().getInstructorName())
                        .attendedAt(booking.getBookedAt())
                        .build())
                .collect(Collectors.toList());
    }

}
