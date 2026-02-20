package com.apexgym.service;

import com.apexgym.dto.TrainerDTO;
import com.apexgym.dto.TrainersResponseDTO;
import com.apexgym.dto.admin.*;
import com.apexgym.entity.*;
import com.apexgym.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
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
        PageRequest pageRequest = PageRequest.of(page, 100);
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
        if (category != null && !category.isEmpty() && !"all".equalsIgnoreCase(category)) {
            categoryEnum = GymClassCategory.valueOf(category);
        }

        if (day.isEmpty() || "all".equals(day)) {
            day = null;
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
        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm");
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
                .fullStartTime(gymClass.getClassDate().format(formatter))
                .startDate(gymClass.getClassDate().format(dateFormatter))
                .startTime(gymClass.getClassDate().format(timeFormatter))
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

        if (specialty != null && !specialty.isEmpty() && !"all".equals(specialty)) {
            all = all.stream()
                    .filter(t -> t.getSpecialty().toLowerCase().contains(specialty.toLowerCase()))
                    .collect(Collectors.toList());
        }

        if (status != null && !status.isEmpty() && !"all".equals(status)) {
            all = all.stream()
                    .filter(t -> status.equalsIgnoreCase("active") ? t.getIsActive() : !t.getIsActive())
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
                .topTrainers(buildTopTrainersRanking())
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

    private List<TrainerRankingDTO> buildTopTrainersRanking() {
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
        String[] labels = switch (period) {
            case "WEEK" -> new String[]{"Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun"};
            case "QUARTER" -> new String[]{"Q1", "Q2", "Q3", "Q4"};
            case "YEAR" -> new String[]{"Jan", "Feb", "Mar", "Apr", "May", "June", "July", "Aug", "Sept", "Oct", "Nov", "Dec"};
            default -> new String[]{"W1", "W2", "W3", "W4"};
        };

        double baseRevenue = membershipRepository.calculateMembershipRevenue() / labels.length;

        // Generate amounts
        Map<String, Double> labelAmounts = Arrays.stream(labels)
                .collect(Collectors.toMap(
                        label -> label,
                        label -> Math.round(baseRevenue * (0.8 + Math.random() * 0.4) * 100.0) / 100.0
                ));

        // Find max
        double maxAmount = labelAmounts.values().stream().max(Double::compare).orElse(1.0);

        // Build result with height percentages
        return labelAmounts.entrySet().stream()
                .map(entry -> RevenueStatDTO.builder()
                        .label(entry.getKey())
                        .amount(entry.getValue())
                        .heightPercent((int) ((entry.getValue() / maxAmount) * 100))
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

    public AdminDashboardResponseDTO getDashboard() {
        return AdminDashboardResponseDTO.builder()
                .quickStats(buildQuickStats())
                .recentMembers(buildRecentMembers())
                .todayClasses(buildTodayClasses())
                .revenueChart(buildRevenueChart("WEEK"))
                .membershipDistribution(buildMembershipDistribution())
                .topTrainers(buildTopTrainers())
                .build();
    }

    // ============================================================
    // Quick Stats
    // ============================================================

    private QuickStatsDTO buildQuickStats() {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime monthStart = now.withDayOfMonth(1).withHour(0).withMinute(0);
        LocalDateTime lastMonthStart = monthStart.minusMonths(1);
        LocalDateTime weekStart = now.with(DayOfWeek.MONDAY).withHour(0).withMinute(0);
        LocalDateTime lastWeekStart = weekStart.minusWeeks(1);

        // Total Members
        Long totalMembers = userRepository.count();
        Long lastMonthMembers = userRepository.count() -
                userRepository.countNewMembersSince(monthStart);
        String membersTrend = calculateTrend(totalMembers, lastMonthMembers, "this month");

        // Revenue
        Double monthlyRevenue = membershipRepository.calculateMembershipRevenue();
        String revenueTrend = "+8% this month"; // Placeholder

        // Classes
        Long classesThisWeek = gymClassRepository.countClassesBetween(weekStart, now);
        Long classesLastWeek = gymClassRepository.countClassesBetween(lastWeekStart, weekStart);
        String classesTrend = classesThisWeek.equals(classesLastWeek)
                ? "Same as last week"
                : calculateTrend(classesThisWeek, classesLastWeek, "this week");

        // Trainers
        Long activeTrainers = trainerRepository.countByIsActiveTrue();
        String trainersTrend = "+2 new hires"; // Placeholder

        return QuickStatsDTO.builder()
                .totalMembers(totalMembers)
                .membersTrend(membersTrend)
                .monthlyRevenue(monthlyRevenue)
                .revenueTrend(revenueTrend)
                .classesThisWeek(classesThisWeek)
                .classesTrend(classesTrend)
                .activeTrainers(activeTrainers)
                .trainersTrend(trainersTrend)
                .build();
    }

    // ============================================================
    // Recent Members
    // ============================================================

    private List<RecentMemberDTO> buildRecentMembers() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMM dd, yyyy");

        return userRepository.findTop5ByOrderByCreatedAtDesc()
                .stream()
                .filter(user -> user.getRole().equals(Role.USER))
                .map(user -> {
                    Membership membership = membershipRepository.findByUserId(user.getId())
                            .orElse(null);

                    return RecentMemberDTO.builder()
                            .id(user.getId())
                            .name(user.getFirstName() + " " + user.getLastName())
                            .email(user.getEmail())
                            .plan(membership != null ? membership.getPlan().name() : "NONE")
                            .joinedDate(formatTimeAgo(user.getCreatedAt()))
                            .initials(getInitials(user))
                            .build();
                })
                .collect(Collectors.toList());
    }

    // ============================================================
    // Today's Classes
    // ============================================================

    private List<TodayClassDTO> buildTodayClasses() {
        LocalDateTime todayStart = LocalDateTime.now().toLocalDate().atStartOfDay();
        LocalDateTime todayEnd = todayStart.plusDays(1);
        DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("h:mm a");

        return gymClassRepository.findByClassDateBetween(todayStart, todayEnd)
                .stream()
                .sorted((a, b) -> a.getClassDate().compareTo(b.getClassDate()))
                .limit(4)
                .map(gymClass -> {
                    Long booked = classBookingRepository.countBookingsByClassId(gymClass.getId());
                    String capacity = booked + "/" + gymClass.getMaxCapacity();
                    String status = booked >= gymClass.getMaxCapacity() ? "FULL" : "AVAILABLE";

                    return TodayClassDTO.builder()
                            .id(gymClass.getId())
                            .time(gymClass.getClassDate().format(timeFormatter))
                            .name(gymClass.getName())
                            .trainer(gymClass.getInstructorName())
                            .capacity(capacity)
                            .status(status)
                            .build();
                })
                .collect(Collectors.toList());
    }

    // ============================================================
    // Revenue Chart (Last 7 Days)
    // ============================================================

    private List<RevenueChartDTO> buildRevenueChart() {
        LocalDateTime now = LocalDateTime.now();
        DateTimeFormatter dayFormatter = DateTimeFormatter.ofPattern("EEE");

        Double baseRevenue = membershipRepository.calculateMembershipRevenue() / 7;

        return List.of(
                buildRevenueDay(now.minusDays(6), dayFormatter, baseRevenue, 0.85),
                buildRevenueDay(now.minusDays(5), dayFormatter, baseRevenue, 0.92),
                buildRevenueDay(now.minusDays(4), dayFormatter, baseRevenue, 0.78),
                buildRevenueDay(now.minusDays(3), dayFormatter, baseRevenue, 1.05),
                buildRevenueDay(now.minusDays(2), dayFormatter, baseRevenue, 0.95),
                buildRevenueDay(now.minusDays(1), dayFormatter, baseRevenue, 1.10),
                buildRevenueDay(now, dayFormatter, baseRevenue, 0.88)
        );
    }

    private RevenueChartDTO buildRevenueDay(LocalDateTime date, DateTimeFormatter formatter,
                                            Double base, Double multiplier) {
        return RevenueChartDTO.builder()
                .day(date.format(formatter))
                .amount(Math.round(base * multiplier * 100.0) / 100.0)
                .build();
    }

    // ============================================================
    // Membership Distribution
    // ============================================================

    private List<MembershipDistributionDTO> buildMembershipDistribution() {
        Long total = membershipRepository.count();

        return List.of(
                buildDistribution(MembershipPlan.STARTER, total),
                buildDistribution(MembershipPlan.PRO, total),
                buildDistribution(MembershipPlan.ELITE, total)
        );
    }

    private MembershipDistributionDTO buildDistribution(MembershipPlan plan, Long total) {
        Long count = membershipRepository.countByPlan(plan);
        Integer percentage = total > 0 ? (int) (count * 100 / total) : 0;

        return MembershipDistributionDTO.builder()
                .plan(plan.name())
                .count(count)
                .percentage(percentage)
                .build();
    }

    // ============================================================
    // Top Trainers
    // ============================================================

    private List<TopTrainerDTO> buildTopTrainers() {
        return trainerRepository.findTop3ByIsActiveTrueOrderByRatingDesc()
                .stream()
                .map((trainer) -> TopTrainerDTO.builder()
                        .rank(1)
                        .name(trainer.getFullName())
                        .specialty(trainer.getSpecialty())
                        .rating(trainer.getRating())
                        .initials(trainer.getInitials())
                        .build())
                .collect(Collectors.toList());
    }

    // ============================================================
    // Helper Methods
    // ============================================================

    private String calculateTrend(Long current, Long previous, String period) {
        if (previous == 0) return "+" + current + " " + period;

        double change = ((double) (current - previous) / previous) * 100;
        String sign = change >= 0 ? "+" : "";
        return String.format("%s%.0f%% %s", sign, change, period);
    }

    private String formatTimeAgo(LocalDateTime dateTime) {
        if (dateTime == null) return "Unknown";

        long days = ChronoUnit.DAYS.between(dateTime, LocalDateTime.now());

        if (days == 0) return "Today";
        if (days == 1) return "1 day ago";
        if (days < 7) return days + " days ago";
        if (days < 14) return "1 week ago";
        if (days < 30) return (days / 7) + " weeks ago";

        return (days / 30) + " months ago";
    }

}