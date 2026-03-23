package com.asset.asset_management.repository;

import com.asset.asset_management.model.MaintenanceRecord;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MaintenanceRepository extends JpaRepository<MaintenanceRecord, Long> {
}