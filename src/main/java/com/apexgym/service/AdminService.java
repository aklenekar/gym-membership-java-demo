package com.apexgym.service;

import com.apexgym.dto.TrainerDTO;
import com.apexgym.dto.TrainersResponseDTO;
import com.apexgym.dto.admin.*;
import com.apexgym.entity.*;
import com.apexgym.mapper.AdminMapper;
import com.apexgym.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
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
    private final AdminMapper adminMapper;

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
                        .map(user -> {
                            Membership membership = membershipRepository.findByUserId(user.getId()).orElse(null);
                            return adminMapper.toAdminMemberDTO(user, membership);
                        })
                        .collect(Collectors.toList()))
                .totalMembers(usersPage.getTotalElements())
                .activeMembers(membershipRepository.countByStatus(MembershipStatus.ACTIVE))
                .expiredMembers(membershipRepository.countByStatus(MembershipStatus.EXPIRED))
                .newThisWeek(userRepository.countNewMembersSince(weekAgo))
                .currentPage(page)
                .totalPages(usersPage.getTotalPages())
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

        if (day == null || day.isEmpty() || "all".equals(day)) {
            day = null;
        }

        List<GymClass> classes = gymClassRepository.findAllWithFilters(
                categoryEnum, day, search,
                todayStart, tomorrowStart, weekStart, weekEnd
        );

        long totalBookings = classBookingRepository.countTotalActiveBookings();
        long classesThisWeek = gymClassRepository.countClassesBetween(weekStart, weekEnd);
        long classesToday = gymClassRepository.countClassesBetween(
                now.withHour(0).withMinute(0),
                now.withHour(23).withMinute(59)
        );

        int avgCapacity = classes.isEmpty() ? 0 : (int) classes.stream()
                .mapToDouble(c -> {
                    long booked = classBookingRepository.countBookingsByClassId(c.getId());
                    return c.getMaxCapacity() > 0 ? (booked * 100.0 / c.getMaxCapacity()) : 0;
                })
                .average()
                .orElse(0.0);

        return AdminClassesResponseDTO.builder()
                .classes(classes.stream()
                        .map(gymClass -> {
                            long booked = classBookingRepository.countBookingsByClassId(gymClass.getId());
                            return adminMapper.toAdminClassDTO(gymClass, booked, calculateClassStatus(gymClass, booked));
                        })
                        .collect(Collectors.toList()))
                .classesThisWeek(classesThisWeek)
                .totalBookings(totalBookings)
                .avgCapacityPercent(avgCapacity)
                .classesToday(classesToday)
                .build();
    }

    private String calculateClassStatus(GymClass gymClass, long booked) {
        double fillRate = gymClass.getMaxCapacity() > 0 ? (double) booked / gymClass.getMaxCapacity() : 0;
        if (fillRate >= 1.0) return "FULL";
        else if (fillRate >= 0.8) return "ALMOST_FULL";
        else return "AVAILABLE";
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
                    .filter(t -> status.equalsIgnoreCase("active") == t.getIsActive())
                    .collect(Collectors.toList());
        }

        List<TrainerDTO> trainers = all.stream()
                .map(adminMapper::toTrainerDTO)
                .collect(Collectors.toList());

        return TrainersResponseDTO.builder()
                .trainers(trainers)
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
        long total = membershipRepository.count();
        long active = membershipRepository.countByStatus(MembershipStatus.ACTIVE);
        long renewals = membershipRepository.countRenewalsSince(since.toLocalDate());
        long cancellations = membershipRepository.countCancellationsSince(since);
        double retentionRate = total > 0 ? (active * 100.0 / total) : 0.0;

        return MembershipAnalyticsDTO.builder()
                .totalMembers(total)
                .newMembers(userRepository.countNewMembersSince(since))
                .renewals(renewals)
                .cancellations(cancellations)
                .retentionRate(Math.round(retentionRate * 10.0) / 10.0)
                .build();
    }

    private ClassAnalyticsDTO buildClassAnalytics(LocalDateTime since, LocalDateTime now) {
        long totalClasses = gymClassRepository.countClassesBetween(since, now);
        long totalBookings = classBookingRepository.countTotalActiveBookings();
        long cancellations = classBookingRepository.countCancellationsSince(since);
        long waitlisted = classBookingRepository.countWaitlisted();

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
                classBookingRepository.countBookingsByClassId(topClasses.getFirst().getId());

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
        AtomicInteger ranking = new AtomicInteger(0);
        return trainerRepository.findByIsActiveTrueOrderByIsHeadCoachDescYearsExperienceDesc()
                .stream()
                .limit(5)
                .map((trainer) -> TrainerRankingDTO.builder()
                        .rank(ranking.addAndGet(1))
                        .name(trainer.getFullName())
                        .rating(trainer.getRating())
                        .imageUrl("/" + trainer.getInitials() + ".jpg")
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

        Map<String, Double> labelAmounts = Arrays.stream(labels)
                .collect(Collectors.toMap(
                        label -> label,
                        label -> Math.round(baseRevenue * (0.8 + Math.random() * 0.4) * 100.0) / 100.0
                ));

        double maxAmount = labelAmounts.values().stream().max(Double::compare).orElse(1.0);

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

    private QuickStatsDTO buildQuickStats() {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime monthStart = now.withDayOfMonth(1).withHour(0).withMinute(0);
        long totalMembers = userRepository.count();
        long lastMonthMembers = userRepository.count() -
                userRepository.countNewMembersSince(monthStart);
        String membersTrend = calculateTrend(totalMembers, lastMonthMembers, "this month");

        Double monthlyRevenue = membershipRepository.calculateMembershipRevenue();
        String revenueTrend = "+8% this month";

        LocalDateTime weekStart = now.with(DayOfWeek.MONDAY).withHour(0).withMinute(0);
        LocalDateTime lastWeekStart = weekStart.minusWeeks(1);
        long classesThisWeek = gymClassRepository.countClassesBetween(weekStart, now);
        long classesLastWeek = gymClassRepository.countClassesBetween(lastWeekStart, weekStart);
        String classesTrend = classesThisWeek == classesLastWeek
                ? "Same as last week"
                : calculateTrend(classesThisWeek, classesLastWeek, "this week");

        long activeTrainers = trainerRepository.countByIsActiveTrue();
        String trainersTrend = "+2 new hires";

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

    private List<RecentMemberDTO> buildRecentMembers() {
        return userRepository.findTop5ByOrderByCreatedAtDesc()
                .stream()
                .filter(user -> user.getRole().equals(Role.USER))
                .map(user -> {
                    Membership membership = membershipRepository.findByUserId(user.getId())
                            .orElse(null);
                    return adminMapper.toRecentMemberDTO(user, membership);
                })
                .collect(Collectors.toList());
    }

    private List<TodayClassDTO> buildTodayClasses() {
        LocalDateTime todayStart = LocalDateTime.now().toLocalDate().atStartOfDay();
        LocalDateTime todayEnd = todayStart.plusDays(1);

        return gymClassRepository.findByClassDateBetween(todayStart, todayEnd)
                .stream()
                .sorted(Comparator.comparing(GymClass::getClassDate))
                .limit(4)
                .map(gymClass -> {
                    long booked = classBookingRepository.countBookingsByClassId(gymClass.getId());
                    return adminMapper.toTodayClassDTO(gymClass, booked, calculateClassStatus(gymClass, booked));
                })
                .collect(Collectors.toList());
    }

    private List<MembershipDistributionDTO> buildMembershipDistribution() {
        long total = membershipRepository.count();

        return List.of(
                buildDistribution(MembershipPlan.STARTER, total),
                buildDistribution(MembershipPlan.PRO, total),
                buildDistribution(MembershipPlan.ELITE, total)
        );
    }

    private MembershipDistributionDTO buildDistribution(MembershipPlan plan, long total) {
        long count = membershipRepository.countByPlan(plan);
        int percentage = total > 0 ? (int) (count * 100 / total) : 0;

        return MembershipDistributionDTO.builder()
                .plan(plan.name())
                .count(count)
                .percentage(percentage)
                .build();
    }

    private List<TopTrainerDTO> buildTopTrainers() {
        AtomicInteger ranking = new AtomicInteger(0);
        return trainerRepository.findTop3ByIsActiveTrueOrderByRatingDesc()
                .stream()
                .map((trainer) -> {
                    TopTrainerDTO dto = adminMapper.toTopTrainerDTO(trainer);
                    return TopTrainerDTO.builder()
                            .rank(ranking.addAndGet(1))
                            .name(dto.name())
                            .specialty(dto.specialty())
                            .rating(dto.rating())
                            .initials(dto.initials())
                            .imageUrl(dto.imageUrl())
                            .build();
                })
                .collect(Collectors.toList());
    }

    private String calculateTrend(long current, long previous, String period) {
        if (previous == 0) return "+" + current + " " + period;
        double change = ((double) (current - previous) / previous) * 100;
        String sign = change >= 0 ? "+" : "";
        return String.format("%s%.0f%% %s", sign, change, period);
    }
}
