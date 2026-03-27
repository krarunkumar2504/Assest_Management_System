package com.asset.asset_management.controller;

import com.asset.asset_management.model.Department;
import com.asset.asset_management.model.Employee;
import com.asset.asset_management.repository.EmployeeRepository;
import com.asset.asset_management.repository.DepartmentRepository;
import com.asset.asset_management.service.AuditLogService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = {
        "http://localhost:3000",
        "https://asset-frontend-xi.vercel.app"
})
public class EmployeeController {

    private final EmployeeRepository  employeeRepo;
    private final DepartmentRepository departmentRepo;
    private final AuditLogService      auditLogService;

    public EmployeeController(EmployeeRepository employeeRepo,
                              DepartmentRepository departmentRepo,
                              AuditLogService auditLogService) {
        this.employeeRepo    = employeeRepo;
        this.departmentRepo  = departmentRepo;
        this.auditLogService = auditLogService;
    }

    // ✅ CREATE EMPLOYEE
    @PostMapping("/employees")
    public Employee createEmployee(@RequestBody Employee employee) {
        if (employee.getDepartment() != null && employee.getDepartment().getId() != null) {
            Department dept = departmentRepo.findById(employee.getDepartment().getId())
                    .orElseThrow(() -> new RuntimeException("Department not found"));
            employee.setDepartment(dept);
        }
        Employee saved = employeeRepo.save(employee);

        // ── Audit log ──────────────────────────────────────
        auditLogService.saveLog(
                "CREATE_EMPLOYEE",
                "Created employee " + saved.getEmployeeName() + " (ID: " + saved.getId() + ")",
                resolvePerformedBy(employee)
        );

        return saved;
    }

    // ✅ GET ALL EMPLOYEES
    @GetMapping("/employees")
    public List<Employee> getAllEmployees() {
        return employeeRepo.findAll();
    }

    // ✅ GET EMPLOYEE BY ID
    @GetMapping("/employees/{id}")
    public Employee getEmployeeById(@PathVariable Long id) {
        return employeeRepo.findById(id).orElse(null);
    }

    // ✅ UPDATE EMPLOYEE
    @PutMapping("/employees/{id}")
    public Employee updateEmployee(@PathVariable Long id, @RequestBody Employee updated) {
        Employee emp = employeeRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Employee not found: " + id));

        if (updated.getEmployeeName() != null) emp.setEmployeeName(updated.getEmployeeName());
        if (updated.getEmail()        != null) emp.setEmail(updated.getEmail());
        if (updated.getRole()         != null) emp.setRole(updated.getRole());
        if (updated.getStatus()       != null) emp.setStatus(updated.getStatus());
        if (updated.getPassword() != null && !updated.getPassword().isBlank())
            emp.setPassword(updated.getPassword());
        if (updated.getDepartment() != null && updated.getDepartment().getId() != null) {
            Department dept = departmentRepo.findById(updated.getDepartment().getId())
                    .orElseThrow(() -> new RuntimeException("Department not found"));
            emp.setDepartment(dept);
        }

        Employee saved = employeeRepo.save(emp);

        // ── Audit log ──────────────────────────────────────
        auditLogService.saveLog(
                "UPDATE_EMPLOYEE",
                "Updated employee " + saved.getEmployeeName() + " (ID: " + saved.getId() + ")",
                resolvePerformedBy(updated)
        );

        return saved;
    }

    // ✅ DELETE EMPLOYEE
    @DeleteMapping("/employees/{id}")
    public String deleteEmployee(@PathVariable Long id) {
        Employee emp = employeeRepo.findById(id).orElse(null);
        if (emp == null) return "❌ Employee not found";

        try {
            String name = emp.getEmployeeName();
            employeeRepo.delete(emp);

            // ── Audit log ──────────────────────────────────
            auditLogService.saveLog(
                    "DELETE_EMPLOYEE",
                    "Deleted employee " + name + " (ID: " + id + ")",
                    "Admin"
            );

            return "✅ Employee deleted successfully";
        } catch (Exception e) {
            e.printStackTrace();
            return "❌ Cannot delete employee (linked data exists). Use Edit → set status to Removed instead.";
        }
    }

    /** Best-effort: use email as performed-by; fall back to "Admin". */
    private String resolvePerformedBy(Employee e) {
        if (e.getEmail() != null && !e.getEmail().isBlank()) return e.getEmail();
        return "Admin";
    }
}