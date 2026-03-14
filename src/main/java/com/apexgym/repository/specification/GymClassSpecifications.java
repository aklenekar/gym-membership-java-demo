package com.apexgym.repository.specification;

import com.apexgym.entity.GymClass;
import com.apexgym.entity.GymClassCategory;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

public class GymClassSpecifications {

    public static Specification<GymClass> withFilters(String category, String instructor, String day) {
        return (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();
            LocalDateTime currentTime = LocalDateTime.now();

            if (category != null && !category.isBlank() && !"all".equalsIgnoreCase(category)) {
                try {
                    GymClassCategory gymClassCategory = GymClassCategory.valueOf(category.toUpperCase());
                    predicates.add(criteriaBuilder.equal(root.get("category"), gymClassCategory));
                } catch (IllegalArgumentException e) {
                    // Ignore invalid category
                }
            }

            if (instructor != null && !instructor.isBlank() && !"all".equalsIgnoreCase(instructor)) {
                predicates.add(criteriaBuilder.like(criteriaBuilder.lower(root.get("instructorName")), "%" + instructor.toLowerCase() + "%"));
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
                    predicates.add(criteriaBuilder.between(root.get("classDate"), finalStart, finalEnd));
                }
            }

            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
    }
}
