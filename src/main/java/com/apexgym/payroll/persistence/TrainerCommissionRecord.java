package com.apexgym.payroll.persistence;

import com.apexgym.staff.persistence.Trainer;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "trainer_commission_records")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TrainerCommissionRecord {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "trainer_id", nullable = false)
    private Trainer trainer;

    @Column(name = "session_title", nullable = false)
    private String sessionTitle;

    @Column(name = "session_date", nullable = false)
    private LocalDate sessionDate;

    @Column(name = "session_type")
    @Builder.Default
    private String sessionType = "GROUP_CLASS";

    @Column(name = "amount", precision = 10, scale = 2, nullable = false)
    private BigDecimal amount;

    @Column(name = "status")
    @Builder.Default
    private String status = "PENDING"; // PENDING, APPROVED, PAID

    @Column(name = "notes", length = 500)
    private String notes;
}
