package com.asset.asset_management.controller;

import com.asset.asset_management.model.Department;
import com.asset.asset_management.model.Employee;
import com.asset.asset_management.repository.EmployeeRepository;
import com.asset.asset_management.repository.DepartmentRepository;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = {
        "http://localhost:3000",
        "https://asset-frontend-xi.vercel.app"
})
public class EmployeeController {

    private final EmployeeRepository employeeRepo;
    private final DepartmentRepository departmentRepo;

    public EmployeeController(EmployeeRepository employeeRepo, DepartmentRepository departmentRepo) {
        this.employeeRepo = employeeRepo;
        this.departmentRepo = departmentRepo;
    }

    // ✅ CREATE EMPLOYEE
    @PostMapping("/employees")
    public Employee createEmployee(@RequestBody Employee employee) {
        if (employee.getDepartment() != null && employee.getDepartment().getId() != null) {
            Department dept = departmentRepo.findById(employee.getDepartment().getId())
                    .orElseThrow(() -> new RuntimeException("Department not found"));
            employee.setDepartment(dept);
        }
        return employeeRepo.save(employee);
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

    // ✅ UPDATE EMPLOYEE — now saves password AND status
    @PutMapping("/employees/{id}")
    public Employee updateEmployee(@PathVariable Long id, @RequestBody Employee updated) {

        Employee emp = employeeRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Employee not found: " + id));

        // Name
        if (updated.getEmployeeName() != null) {
            emp.setEmployeeName(updated.getEmployeeName());
        }

        // Email
        if (updated.getEmail() != null) {
            emp.setEmail(updated.getEmail());
        }

        // Role
        if (updated.getRole() != null) {
            emp.setRole(updated.getRole());
        }

        // Password — only overwrite if a non-blank value was sent
        if (updated.getPassword() != null && !updated.getPassword().isBlank()) {
            emp.setPassword(updated.getPassword());
        }

        // Status — persist the status field (Active / Inactive / Removed)
        if (updated.getStatus() != null) {
            emp.setStatus(updated.getStatus());
        }

        // Department — resolve and set
        if (updated.getDepartment() != null && updated.getDepartment().getId() != null) {
            Department dept = departmentRepo.findById(updated.getDepartment().getId())
                    .orElseThrow(() -> new RuntimeException("Department not found"));
            emp.setDepartment(dept);
        }

        return employeeRepo.save(emp);
    }

    // ✅ DELETE EMPLOYEE
    @DeleteMapping("/employees/{id}")
    public String deleteEmployee(@PathVariable Long id) {
        Employee emp = employeeRepo.findById(id).orElse(null);
        if (emp == null) {
            return "❌ Employee not found";
        }
        try {
            employeeRepo.delete(emp);
            return "✅ Employee deleted successfully";
        } catch (Exception e) {
            e.printStackTrace();
            return "❌ Cannot delete employee (linked data exists). Use Edit → set status to Removed instead.";
        }
    }
}