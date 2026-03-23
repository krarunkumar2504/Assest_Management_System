package com.asset.asset_management.repository;


import com.asset.asset_management.model.Asset;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.Repository;

import java.util.List;

public interface ReportRepository extends Repository<Asset, Long> {

    // 1. High Maintenance Assets
    @Query(value = """
    SELECT a.asset_name, a.asset_type, a.current_value,
           SUM(m.cost) AS total_maintenance_cost
    FROM assets a
    LEFT JOIN maintenance_records m ON a.id = m.asset_id
    GROUP BY a.id, a.asset_name, a.asset_type, a.current_value
    HAVING SUM(m.cost) > (a.current_value * 0.1)
    """, nativeQuery = true)
    List<Object[]> getHighMaintenanceAssets();


    // 2. Department Summary
    @Query(value = """
    SELECT d.department_name,
           COUNT(DISTINCT a.id),
           SUM(a.current_value)
    FROM departments d
    LEFT JOIN employees e ON d.id = e.department_id
    LEFT JOIN asset_assignments aa ON e.id = aa.employee_id
    LEFT JOIN assets a ON aa.asset_id = a.id
    GROUP BY d.department_name
    """, nativeQuery = true)
    List<Object[]> getDepartmentSummary();


    // 3. Upcoming Maintenance
    @Query(value = """
    SELECT a.asset_name, m.next_due_date
    FROM assets a
    JOIN maintenance_records m ON a.id = m.asset_id
    WHERE m.next_due_date BETWEEN CURDATE() AND DATE_ADD(CURDATE(), INTERVAL 30 DAY)
    """, nativeQuery = true)
    List<Object[]> getUpcomingMaintenance();
}