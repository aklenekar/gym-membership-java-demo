package com.apexgym.service;

import com.apexgym.dto.TrainerDTO;
import com.apexgym.dto.TrainersResponseDTO;
import com.apexgym.dto.admin.*;
import com.apexgym.entity.*;
import com.apexgym.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Service
@RequiredArgsConstructor
public class AdminService {

    private final UserRepository userRepository;
    private final MembershipRepository membershipRepository;
    private final GymClassRepository gymClassRepository;
    private final ClassBookingRepository classBookingRepository;
    private final TrainerRepository trainerRepository;

    // ============================================================
    // MEMBERS
    // ============================================================

    public AdminMembersResponseDTO getMembers(String search, String planStr, String statusStr, int page) {
        PageRequest pageRequest = PageRequest.of(page, 10);
        MembershipPlan plan = null;
        if (planStr != null && !planStr.isEmpty() && !"All".equals(planStr)) {
            plan = MembershipPlan.valueOf(planStr.toUpperCase());
        }

        MembershipStatus status = null;
        if (statusStr != null && !statusStr.isEmpty() && !"All".equals(statusStr)) {
            status = MembershipStatus.valueOf(statusStr.toUpperCase());
        }
        Page<User> usersPage = userRepository.findAllWithFilters(plan, status, search, pageRequest);

        LocalDateTime weekAgo = LocalDateTime.now().minusDays(7);

        return AdminMembersResponseDTO.builder()
                .members(usersPage.getContent().stream()
                        .map(this::convertToAdminMemberDTO)
                        .collect(Collectors.toList()))
                .totalMembers(usersPage.getTotalElements())
                .activeMembers(membershipRepository.countByStatus(MembershipStatus.ACTIVE))
                .expiredMembers(membershipRepository.countByStatus(MembershipStatus.EXPIRED))
                .newThisWeek(userRepository.countNewMembersSince(weekAgo))
                .currentPage(page)
                .totalPages(usersPage.getTotalPages())
                .build();
    }

    private AdminMemberDTO convertToAdminMemberDTO(User user) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMM dd, yyyy");
        Membership membership = membershipRepository.findByUserId(user.getId()).orElse(null);

        return AdminMemberDTO.builder()
                .id(user.getId())
                .memberId("#M-" + String.format("%04d", user.getId()))
                .fullName(user.getFirstName() + " " + user.getLastName())
                .email(user.getEmail())
                .plan(membership != null ? membership.getPlan().name() : "NONE")
                .status(membership != null ? membership.getStatus().name() : "INACTIVE")
                .joinDate(user.getCreatedAt() != null ? user.getCreatedAt().format(formatter) : "N/A")
                .expiryDate(membership != null && membership.getNextBillingDate() != null
                        ? membership.getNextBillingDate().format(formatter) : "N/A")
                .initials(getInitials(user))
                .build();
    }

    // ============================================================
    // CLASSES
    // ============================================================

    public AdminClassesResponseDTO getClasses(String search, String category, String day) {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime todayStart    = now.toLocalDate().atStartOfDay();
        LocalDateTime tomorrowStart = todayStart.plusDays(1);
        LocalDateTime weekStart     = todayStart.plusDays(2);
        LocalDateTime weekEnd       = todayStart.plusDays(8);

        GymClassCategory categoryEnum = null;
        if (category != null && !category.isEmpty()) {
            categoryEnum = GymClassCategory.valueOf(category.toUpperCase());
        }

        List<GymClass> classes = gymClassRepository.findAllWithFilters(
                categoryEnum, day, search,
                todayStart, tomorrowStart, weekStart, weekEnd
        );

        Long totalBookings = classBookingRepository.countTotalActiveBookings();
        Long classesThisWeek = gymClassRepository.countClassesBetween(weekStart, weekEnd);
        Long classesToday = gymClassRepository.countClassesBetween(
                now.withHour(0).withMinute(0),
                now.withHour(23).withMinute(59)
        );

        int avgCapacity = classes.isEmpty() ? 0 : (int) classes.stream()
                .mapToDouble(c -> {
                    long booked = classBookingRepository.countBookingsByClassId(c.getId());
                    return c.getMaxCapacity() > 0 ? (booked * 100.0 / c.getMaxCapacity()) : 0;
                })
                .average()
                .orElse(0);

        return AdminClassesResponseDTO.builder()
                .classes(classes.stream().map(this::convertToAdminClassDTO).collect(Collectors.toList()))
                .classesThisWeek(classesThisWeek)
                .totalBookings(totalBookings)
                .avgCapacityPercent(avgCapacity)
                .classesToday(classesToday)
                .build();
    }

    private AdminClassDTO convertToAdminClassDTO(GymClass gymClass) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("EEE, h:mm a");
        long booked = classBookingRepository.countBookingsByClassId(gymClass.getId());

        String status;
        double fillRate = gymClass.getMaxCapacity() > 0 ? (double) booked / gymClass.getMaxCapacity() : 0;
        if (fillRate >= 1.0) status = "FULL";
        else if (fillRate >= 0.8) status = "ALMOST_FULL";
        else status = "AVAILABLE";

        return AdminClassDTO.builder()
                .id(gymClass.getId())
                .name(gymClass.getName())
                .category(gymClass.getCategory().name())
                .instructor(gymClass.getInstructorName())
                .location(gymClass.getLocation())
                .startTime(gymClass.getClassDate().format(formatter))
                .durationMinutes(gymClass.getDurationMinutes())
                .capacity(gymClass.getMaxCapacity())
                .bookedCount((int) booked)
                .status(status)
                .build();
    }

    // ============================================================
    // TRAINERS
    // ============================================================

    public TrainersResponseDTO getAdminTrainers(String search, String specialty, String status) {
        List<Trainer> all = trainerRepository.findByIsActiveTrueOrderByIsHeadCoachDescYearsExperienceDesc();

        if (search != null && !search.isEmpty()) {
            String lower = search.toLowerCase();
            all = all.stream()
                    .filter(t -> t.getFullName().toLowerCase().contains(lower)
                            || t.getSpecialty().toLowerCase().contains(lower))
                    .collect(Collectors.toList());
        }

        if (specialty != null && !specialty.isEmpty()) {
            all = all.stream()
                    .filter(t -> t.getSpecialty().toLowerCase().contains(specialty.toLowerCase()))
                    .collect(Collectors.toList());
        }

        List<TrainerDTO> trainers = all.stream()
                .map(this::convertToTrainerDTO)
                .collect(Collectors.toList());

        return TrainersResponseDTO.builder()
                .trainers(trainers)
                .build();
    }

    private TrainerDTO convertToTrainerDTO(Trainer trainer) {
        return TrainerDTO.builder()
                .id(trainer.getId())
                .fullName(trainer.getFullName())
                .initials(trainer.getInitials())
                .specialty(trainer.getSpecialty())
                .bio(trainer.getBio())
                .certifications(trainer.getCertifications())
                .yearsExperience(trainer.getYearsExperience())
                .clientsTrained(trainer.getClientsTrained())
                .rating(trainer.getRating())
                .isHeadCoach(trainer.getIsHeadCoach())
                .email(trainer.getEmail())
                .build();
    }

    // ============================================================
    // REPORTS
    // ============================================================

    public AdminReportsResponseDTO getReports(String period) {
        LocalDateTime since = getSinceDate(period);
        LocalDateTime now = LocalDateTime.now();

        return AdminReportsResponseDTO.builder()
                .totalRevenue(membershipRepository.calculateMembershipRevenue())
                .membershipRevenue(membershipRepository.calculateMembershipRevenue())
                .trainingRevenue(0.0)
                .revenueChart(buildRevenueChart(period))
                .membershipAnalytics(buildMembershipAnalytics(since))
                .classAnalytics(buildClassAnalytics(since, now))
                .popularClasses(buildPopularClasses())
                .topTrainers(buildTopTrainers())
                .peakHours(buildPeakHours())
                .build();
    }

    private MembershipAnalyticsDTO buildMembershipAnalytics(LocalDateTime since) {
        Long total = membershipRepository.count();
        Long active = membershipRepository.countByStatus(MembershipStatus.ACTIVE);
        Long renewals = membershipRepository.countRenewalsSince(since.toLocalDate());
        Long cancellations = membershipRepository.countCancellationsSince(since);
        Double retentionRate = total > 0 ? (active * 100.0 / total) : 0;

        return MembershipAnalyticsDTO.builder()
                .totalMembers(total)
                .newMembers(userRepository.countNewMembersSince(since))
                .renewals(renewals)
                .cancellations(cancellations)
                .retentionRate(Math.round(retentionRate * 10.0) / 10.0)
                .build();
    }

    private ClassAnalyticsDTO buildClassAnalytics(LocalDateTime since, LocalDateTime now) {
        Long totalClasses = gymClassRepository.countClassesBetween(since, now);
        Long totalBookings = classBookingRepository.countTotalActiveBookings();
        Long cancellations = classBookingRepository.countCancellationsSince(since);
        Long waitlisted = classBookingRepository.countWaitlisted();

        int cancellationRate = totalBookings > 0
                ? (int) (cancellations * 100 / totalBookings) : 0;

        return ClassAnalyticsDTO.builder()
                .totalClasses(totalClasses)
                .totalBookings(totalBookings)
                .avgAttendancePercent(82)
                .cancellationRatePercent(cancellationRate)
                .waitlistCount(waitlisted)
                .build();
    }

    private List<ClassRankingDTO> buildPopularClasses() {
        List<GymClass> topClasses = gymClassRepository.findTopClassesByBookings(PageRequest.of(0, 5));
        long maxBookings = topClasses.isEmpty() ? 1 :
                classBookingRepository.countBookingsByClassId(topClasses.get(0).getId());

        return IntStream.range(0, topClasses.size())
                .mapToObj(i -> {
                    GymClass c = topClasses.get(i);
                    long bookings = classBookingRepository.countBookingsByClassId(c.getId());
                    return ClassRankingDTO.builder()
                            .rank(i + 1)
                            .className(c.getName())
                            .bookings(bookings)
                            .fillPercent((int) (bookings * 100 / maxBookings))
                            .build();
                })
                .collect(Collectors.toList());
    }

    private List<TrainerRankingDTO> buildTopTrainers() {
        return trainerRepository.findByIsActiveTrueOrderByIsHeadCoachDescYearsExperienceDesc()
                .stream()
                .limit(5)
                .map((trainer) -> TrainerRankingDTO.builder()
                        .rank(1)
                        .name(trainer.getFullName())
                        .rating(trainer.getRating())
                        .classCount(0L)
                        .build())
                .collect(Collectors.toList());
    }

    private List<RevenueStatDTO> buildRevenueChart(String period) {
        String[] labels = period.equals("WEEK")
                ? new String[]{"Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun"}
                : new String[]{"W1", "W2", "W3", "W4"};

        double baseRevenue = membershipRepository.calculateMembershipRevenue() / labels.length;

        return java.util.Arrays.stream(labels)
                .map(label -> RevenueStatDTO.builder()
                        .label(label)
                        .amount(Math.round(baseRevenue * (0.8 + Math.random() * 0.4) * 100.0) / 100.0)
                        .build())
                .collect(Collectors.toList());
    }

    private List<PeakHourDTO> buildPeakHours() {
        return List.of(
                PeakHourDTO.builder().hour("6AM").occupancyPercent(25).build(),
                PeakHourDTO.builder().hour("7AM").occupancyPercent(40).build(),
                PeakHourDTO.builder().hour("8AM").occupancyPercent(55).build(),
                PeakHourDTO.builder().hour("9AM").occupancyPercent(45).build(),
                PeakHourDTO.builder().hour("12PM").occupancyPercent(50).build(),
                PeakHourDTO.builder().hour("5PM").occupancyPercent(95).build(),
                PeakHourDTO.builder().hour("6PM").occupancyPercent(100).build(),
                PeakHourDTO.builder().hour("7PM").occupancyPercent(85).build(),
                PeakHourDTO.builder().hour("8PM").occupancyPercent(65).build()
        );
    }

    private LocalDateTime getSinceDate(String period) {
        return switch (period != null ? period.toUpperCase() : "MONTH") {
            case "TODAY" -> LocalDateTime.now().withHour(0).withMinute(0);
            case "WEEK" -> LocalDateTime.now().minusDays(7);
            case "QUARTER" -> LocalDateTime.now().minusMonths(3);
            case "YEAR" -> LocalDateTime.now().minusYears(1);
            default -> LocalDateTime.now().minusMonths(1);
        };
    }

    private String getInitials(User user) {
        String first = user.getFirstName() != null ? user.getFirstName().substring(0, 1) : "";
        String last = user.getLastName() != null ? user.getLastName().substring(0, 1) : "";
        return (first + last).toUpperCase();
    }
}