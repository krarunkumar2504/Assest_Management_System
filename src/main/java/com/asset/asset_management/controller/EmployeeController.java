package com.asset.asset_management.controller;

import com.asset.asset_management.model.Department;
import com.asset.asset_management.model.Employee;
import com.asset.asset_management.repository.EmployeeRepository;
import com.asset.asset_management.repository.DepartmentRepository;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin(origins = {
        "http://localhost:3000",
        "https://asset-frontend-xi.vercel.app"
})
@RestController
@RequestMapping("/api")
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

        // Get department from DB
        Department dept = departmentRepo.findById(employee.getDepartment().getId())
                .orElseThrow(() -> new RuntimeException("Department not found"));

        employee.setDepartment(dept);

        return employeeRepo.save(employee);
    }

    // ✅ GET ALL EMPLOYEES (for future table)
    @GetMapping("/employees")
    public List<Employee> getAllEmployees() {
        return employeeRepo.findAll();
    }
}