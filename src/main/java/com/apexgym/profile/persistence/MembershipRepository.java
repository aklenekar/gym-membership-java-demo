package com.apexgym.profile.persistence;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Optional;

@Repository
public interface MembershipRepository extends JpaRepository<Membership, Long> {
    Optional<Membership> findByUserId(Long userId);

    // Admin - NEW queries to add
    Long countByStatus(MembershipStatus status);

    @Query("SELECT COUNT(m) FROM Membership m WHERE m.memberSince >= :since")
    Long countRenewalsSince(@Param("since") LocalDate since);

    @Query("SELECT COUNT(m) FROM Membership m WHERE m.status = com.apexgym.profile.persistence.MembershipStatus.EXPIRED AND m.updatedAt >= :since")
    Long countCancellationsSince(@Param("since") LocalDateTime since);

    @Query("""
                SELECT SUM(
                    CASE m.plan
                        WHEN com.apexgym.profile.persistence.MembershipPlan.STARTER THEN 29.0
                        WHEN com.apexgym.profile.persistence.MembershipPlan.PRO THEN 49.0
                        WHEN com.apexgym.profile.persistence.MembershipPlan.ELITE THEN 79.0
                    END
                ) FROM Membership m WHERE m.status = com.apexgym.profile.persistence.MembershipStatus.ACTIVE
            """)
    Double calculateMembershipRevenue();

    Long countByPlan(MembershipPlan plan);
}