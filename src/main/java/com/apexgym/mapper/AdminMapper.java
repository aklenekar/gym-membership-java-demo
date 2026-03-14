package com.apexgym.mapper;

import com.apexgym.dto.TrainerDTO;
import com.apexgym.dto.admin.*;
import com.apexgym.entity.GymClass;
import com.apexgym.entity.Membership;
import com.apexgym.entity.Trainer;
import com.apexgym.entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.ReportingPolicy;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface AdminMapper {

    @Mapping(target = "id", source = "user.id")
    @Mapping(target = "fullName", expression = "java(user.getFirstName() + \" \" + user.getLastName())")
    @Mapping(target = "memberId", expression = "java(\"#M-\" + String.format(\"%04d\", user.getId()))")
    @Mapping(target = "plan", expression = "java(membership != null ? membership.getPlan().name() : \"NONE\")")
    @Mapping(target = "status", expression = "java(membership != null ? membership.getStatus().name() : \"INACTIVE\")")
    @Mapping(target = "joinDate", source = "user.createdAt", dateFormat = "MMM dd, yyyy")
    @Mapping(target = "expiryDate", expression = "java(membership != null && membership.getNextBillingDate() != null ? membership.getNextBillingDate().format(java.time.format.DateTimeFormatter.ofPattern(\"MMM dd, yyyy\")) : \"N/A\")")
    @Mapping(target = "initials", source = "user", qualifiedByName = "getInitials")
    AdminMemberDTO toAdminMemberDTO(User user, Membership membership);

    @Mapping(target = "id", source = "user.id")
    @Mapping(target = "name", expression = "java(user.getFirstName() + \" \" + user.getLastName())")
    @Mapping(target = "plan", expression = "java(membership != null ? membership.getPlan().name() : \"NONE\")")
    @Mapping(target = "joinedDate", source = "user.createdAt", qualifiedByName = "formatTimeAgo")
    @Mapping(target = "initials", source = "user", qualifiedByName = "getInitials")
    RecentMemberDTO toRecentMemberDTO(User user, Membership membership);

    @Mapping(target = "id", source = "gymClass.id")
    @Mapping(target = "category", source = "gymClass.category")
    @Mapping(target = "instructor", source = "gymClass.instructorName")
    @Mapping(target = "fullStartTime", source = "gymClass.classDate", dateFormat = "EEE, h:mm a")
    @Mapping(target = "startDate", source = "gymClass.classDate", dateFormat = "yyyy-MM-dd")
    @Mapping(target = "startTime", source = "gymClass.classDate", dateFormat = "HH:mm")
    @Mapping(target = "capacity", source = "gymClass.maxCapacity")
    @Mapping(target = "bookedCount", source = "bookedCount")
    @Mapping(target = "status", source = "status")
    AdminClassDTO toAdminClassDTO(GymClass gymClass, Long bookedCount, String status);

    @Mapping(target = "id", source = "gymClass.id")
    @Mapping(target = "time", source = "gymClass.classDate", dateFormat = "h:mm a")
    @Mapping(target = "trainer", source = "gymClass.instructorName")
    @Mapping(target = "capacity", expression = "java(bookedCount + \"/\" + gymClass.getMaxCapacity())")
    @Mapping(target = "status", source = "status")
    TodayClassDTO toTodayClassDTO(GymClass gymClass, Long bookedCount, String status);

    @Mapping(target = "imageUrl", expression = "java(\"/\" + trainer.getInitials() + \".jpg\")")
    TrainerDTO toTrainerDTO(Trainer trainer);

    @Mapping(target = "rank", ignore = true)
    @Mapping(target = "imageUrl", expression = "java(\"/\" + trainer.getInitials() + \".jpg\")")
    @Mapping(target = "name", source = "fullName")
    TopTrainerDTO toTopTrainerDTO(Trainer trainer);

    @Named("getInitials")
    default String getInitials(User user) {
        String first = user.getFirstName() != null && !user.getFirstName().isEmpty() ? user.getFirstName().substring(0, 1) : "";
        String last = user.getLastName() != null && !user.getLastName().isEmpty() ? user.getLastName().substring(0, 1) : "";
        return (first + last).toUpperCase();
    }

    @Named("formatTimeAgo")
    default String formatTimeAgo(LocalDateTime dateTime) {
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
