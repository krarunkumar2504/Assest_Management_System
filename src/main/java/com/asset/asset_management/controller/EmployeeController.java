package com.asset.asset_management.controller;

import com.asset.asset_management.model.Department;
import com.asset.asset_management.model.Employee;
import com.asset.asset_management.repository.EmployeeRepository;
import com.asset.asset_management.repository.DepartmentRepository;
import com.asset.asset_management.service.AuditLogService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = {
        "http://localhost:3000",
        "https://asset-frontend-xi.vercel.app"
})
public class EmployeeController {

    private final EmployeeRepository   employeeRepo;
    private final DepartmentRepository departmentRepo;
    private final AuditLogService      auditLogService;

    public EmployeeController(EmployeeRepository employeeRepo,
                              DepartmentRepository departmentRepo,
                              AuditLogService auditLogService) {
        this.employeeRepo    = employeeRepo;
        this.departmentRepo  = departmentRepo;
        this.auditLogService = auditLogService;
    }

    // ── CREATE EMPLOYEE ───────────────────────────────────────────────────
    @PostMapping("/employees")
    public ResponseEntity<?> createEmployee(
            @RequestBody Employee employee,
            @RequestHeader(value = "X-Performed-By", required = false) String performedByHeader) {

        if (employee.getDepartment() != null && employee.getDepartment().getId() != null) {
            Department dept = departmentRepo.findById(employee.getDepartment().getId())
                    .orElseThrow(() -> new RuntimeException("Department not found"));
            employee.setDepartment(dept);
        }

        // Handle joined_date sent as string field "joined_date" from frontend
        // (Spring auto-deserialises LocalDate from ISO "yyyy-MM-dd" via Jackson)

        Employee saved = employeeRepo.save(employee);

        String performer = resolvePerformedBy(employee, performedByHeader);
        auditLogService.saveLog(
                "CREATE_EMPLOYEE",
                "Created employee: " + saved.getEmployeeName()
                        + " | Email: " + saved.getEmail()
                        + " | Role: " + saved.getRole()
                        + " | ID: #" + saved.getId(),
                performer
        );

        return ResponseEntity.ok(saved);
    }

    // ── GET ALL EMPLOYEES ────────────────────────────────────────────────
    @GetMapping("/employees")
    public List<Employee> getAllEmployees() {
        return employeeRepo.findAll();
    }

    // ── GET EMPLOYEE BY ID ───────────────────────────────────────────────
    @GetMapping("/employees/{id}")
    public ResponseEntity<?> getEmployeeById(@PathVariable Long id) {
        return employeeRepo.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // ── UPDATE EMPLOYEE ───────────────────────────────────────────────────
    @PutMapping("/employees/{id}")
    public ResponseEntity<?> updateEmployee(
            @PathVariable Long id,
            @RequestBody Employee updated,
            @RequestHeader(value = "X-Performed-By", required = false) String performedByHeader) {

        Employee emp = employeeRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Employee not found: " + id));

        if (updated.getEmployeeName() != null) emp.setEmployeeName(updated.getEmployeeName());
        if (updated.getEmail()        != null) emp.setEmail(updated.getEmail());
        if (updated.getRole()         != null) emp.setRole(updated.getRole());
        if (updated.getStatus()       != null) emp.setStatus(updated.getStatus());
        if (updated.getJoinedDate()   != null) emp.setJoinedDate(updated.getJoinedDate());
        if (updated.getPassword() != null && !updated.getPassword().isBlank())
            emp.setPassword(updated.getPassword());
        if (updated.getDepartment() != null && updated.getDepartment().getId() != null) {
            Department dept = departmentRepo.findById(updated.getDepartment().getId())
                    .orElseThrow(() -> new RuntimeException("Department not found"));
            emp.setDepartment(dept);
        }

        Employee saved = employeeRepo.save(emp);

        String performer = resolvePerformedBy(updated, performedByHeader);
        auditLogService.saveLog(
                "UPDATE_EMPLOYEE",
                "Updated employee: " + saved.getEmployeeName()
                        + " | Email: " + saved.getEmail()
                        + " | Role: " + saved.getRole()
                        + " | Status: " + saved.getStatus()
                        + " | ID: #" + saved.getId(),
                performer
        );

        return ResponseEntity.ok(saved);
    }

    // ── DELETE EMPLOYEE ───────────────────────────────────────────────────
    @DeleteMapping("/employees/{id}")
    public ResponseEntity<?> deleteEmployee(
            @PathVariable Long id,
            @RequestHeader(value = "X-Performed-By", required = false) String performedByHeader) {

        Employee emp = employeeRepo.findById(id).orElse(null);
        if (emp == null) {
            return ResponseEntity.status(404).body(
                    Map.of("message", "Employee not found"));
        }

        String name      = emp.getEmployeeName();
        String email     = emp.getEmail();
        String performer = (performedByHeader != null && !performedByHeader.isBlank())
                ? performedByHeader
                : "Admin";

        try {
            employeeRepo.delete(emp);

            // Log AFTER successful delete so count is accurate
            auditLogService.saveLog(
                    "DELETE_EMPLOYEE",
                    "Deleted employee: " + name
                            + " | Email: " + email
                            + " | ID: #" + id,
                    performer
            );

            return ResponseEntity.ok(
                    Map.of("message", "Employee deleted successfully"));

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(409).body(
                    Map.of("message",
                            "Cannot delete employee (linked asset assignments exist). "
                                    + "Use Edit → set Status to 'Removed' instead."));
        }
    }

    // ── HELPER ────────────────────────────────────────────────────────────
    /**
     * Priority: X-Performed-By header → employee email → "Admin"
     */
    private String resolvePerformedBy(Employee e, String header) {
        if (header != null && !header.isBlank()) return header;
        if (e != null && e.getEmail() != null && !e.getEmail().isBlank()) return e.getEmail();
        return "Admin";
    }
}