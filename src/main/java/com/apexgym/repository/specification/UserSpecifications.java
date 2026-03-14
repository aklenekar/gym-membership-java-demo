package com.apexgym.repository.specification;

import com.apexgym.entity.MembershipPlan;
import com.apexgym.entity.MembershipStatus;
import com.apexgym.entity.User;
import jakarta.persistence.criteria.JoinType;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.List;

public class UserSpecifications {

    public static Specification<User> withAdminFilters(MembershipPlan plan, MembershipStatus status, String search) {
        return (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (plan != null || status != null) {
                // Join with membership only if filtering by plan or status
                var membershipJoin = root.join("memberships", JoinType.LEFT);
                if (plan != null) {
                    predicates.add(criteriaBuilder.equal(membershipJoin.get("plan"), plan));
                }
                if (status != null) {
                    predicates.add(criteriaBuilder.equal(membershipJoin.get("status"), status));
                }
            }

            if (search != null && !search.isBlank()) {
                String likePattern = "%" + search.toLowerCase() + "%";
                predicates.add(criteriaBuilder.or(
                        criteriaBuilder.like(criteriaBuilder.lower(root.get("firstName")), likePattern),
                        criteriaBuilder.like(criteriaBuilder.lower(root.get("lastName")), likePattern),
                        criteriaBuilder.like(criteriaBuilder.lower(root.get("email")), likePattern)
                ));
            }

            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
    }
}
