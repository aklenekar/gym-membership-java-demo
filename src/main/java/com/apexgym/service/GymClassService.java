package com.apexgym.service;

import com.apexgym.dto.GymClassDTO;
import com.apexgym.entity.BookingStatus;
import com.apexgym.entity.ClassBooking;
import com.apexgym.entity.GymClass;
import com.apexgym.entity.User;
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

    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
    private final GymClassRepository gymClassRepository;
    private final ClassBookingRepository classBookingRepository;
    private final UserRepository userRepository;

    /**
     * -----------------------------------------------------------------
     * READ
     * -----------------------------------------------------------------
     */
    public List<GymClassDTO> findAll() {
        return gymClassRepository.findAll().stream().map(this::toDTO).collect(Collectors.toList());
    }


    public List<GymClass> findUpcomingClasses(LocalDateTime now) {
        return gymClassRepository.findUpcomingClasses(now);
    }

    /**
     * Retrieve classes with optional filters.
     *
     * @param category   filter by category (e.g. "HIIT") – pass null/empty for no filter
     * @param instructor filter by instructor name – pass null/empty for no filter
     * @param day        "today", "tomorrow", "week" or null/empty
     */
    public List<GymClassDTO> findByFilters(String category,
                                           String instructor,
                                           String day, String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Specification<GymClass> spec = Specification.where(null);
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
            // 1. Declare variables that will be assigned once
            LocalDateTime finalStart;
            LocalDateTime finalEnd;
            // Use temporary variables for calculation if needed,
            // or just ensure the paths below only assign the variables ONCE.
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

            // 2. Because finalStart and finalEnd are assigned exactly once,
            // they are "effectively final" and safe for the Lambda.
            if (finalStart != null) {
                spec = spec.and((root, query, cb) ->
                        cb.between(root.get("classDate"), finalStart, finalEnd)
                );
            }
        }

        List<GymClass> gymClasses = gymClassRepository.findAll(spec);

        // Get user's bookings
        List<ClassBooking> userBookings = classBookingRepository
                .findByUserIdAndStatusAndGymClass_ClassDateAfterOrderByGymClass_ClassDate(user.getId(), BookingStatus.BOOKED, currentTime);

        Map<Long, ClassBooking> bookingClassIds = userBookings.stream()
                .collect(Collectors.toMap(cb -> cb.getGymClass().getId(), cb -> cb));

        List<GymClassDTO> gymClassDTOS =  gymClasses.stream().map(this::toDTO).toList();
        gymClassDTOS.forEach(gymClassDTO -> {
            if (bookingClassIds.containsKey(gymClassDTO.getId())) {
                gymClassDTO.setIsBooked(true);
                gymClassDTO.setBookingId(bookingClassIds.get(gymClassDTO.getId()).getId());
            }
        });
        return gymClassDTOS;
    }

    public GymClassDTO getById(Long id) {
        return gymClassRepository.findById(id).map(this::toDTO)
                .orElseThrow(() -> new EntityNotFoundException("Class not found with id " + id));
    }

    private GymClassDTO toDTO(GymClass entity) {
        if (entity == null) {
            return null;
        }

        // Calculate spots remaining for the "spotsInfo" field
        int remaining = entity.getMaxCapacity() - entity.getCurrentBookings();
        String spotsInfo = remaining + " spots left";

        return GymClassDTO.builder()
                .id(entity.getId()) // Converting Long to Integer
                .name(entity.getName())
                .instructor(entity.getInstructorName())
                .location(entity.getLocation())
                .startTime(entity.getClassDate() != null ? entity.getClassDate().format(FORMATTER) : null)
                .durationMin(entity.getDurationMinutes() + " mins")
                .capacity(String.valueOf(entity.getMaxCapacity()))
                .booked(String.valueOf(entity.getCurrentBookings()))
                .spotsInfo(spotsInfo)
                .category(entity.getCategory().name())
                .build();
    }

}
