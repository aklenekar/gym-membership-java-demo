package com.apexgym.repository;

import com.apexgym.entity.MembershipPlan;
import com.apexgym.entity.MembershipStatus;
import com.apexgym.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByEmail(String email);

    boolean existsByEmail(String email);

    // Admin Members - NEW queries to add
    @Query("""
                SELECT u FROM User u
                JOIN Membership m ON m.user.id = u.id
                WHERE (:plan IS NULL OR m.plan = :plan)
                AND (:status IS NULL OR m.status = :status)
                AND (:search IS NULL OR LOWER(u.firstName) LIKE LOWER(CONCAT('%', :search, '%'))
                    OR LOWER(u.lastName) LIKE LOWER(CONCAT('%', :search, '%'))
                    OR LOWER(u.email) LIKE LOWER(CONCAT('%', :search, '%')))
                ORDER BY u.createdAt DESC
            """)
    Page<User> findAllWithFilters(
            @Param("plan") MembershipPlan plan,
            @Param("status") MembershipStatus status,   // ← was String
            @Param("search") String search,
            Pageable pageable
    );

    @Query("SELECT COUNT(u) FROM User u WHERE u.createdAt >= :since")
    Long countNewMembersSince(@Param("since") LocalDateTime since);
}
