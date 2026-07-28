package com.apexgym.equipment.persistence;

import com.apexgym.equipment.entity.MaintenanceRecord;
import com.apexgym.equipment.entity.enums.MaintenanceStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Repository
public interface MaintenanceRecordRepository extends JpaRepository<MaintenanceRecord, Long> {

    List<MaintenanceRecord> findByEquipmentId(Long equipmentId);

    List<MaintenanceRecord> findByStatus(MaintenanceStatus status);

    @Query("SELECT m FROM MaintenanceRecord m WHERE m.scheduledDate < :cutoffDate AND m.status = 'SCHEDULED'")
    List<MaintenanceRecord> findOverdue(@Param("cutoffDate") LocalDate cutoffDate);

    long countByStatus(MaintenanceStatus status);

    @Query("SELECT COALESCE(SUM(m.cost), 0) FROM MaintenanceRecord m WHERE m.completedDate >= :startDate AND m.status = 'COMPLETED'")
    BigDecimal sumCostSince(@Param("startDate") LocalDate startDate);
}
