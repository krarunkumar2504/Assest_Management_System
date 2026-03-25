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

        Department dept = departmentRepo.findById(employee.getDepartment().getId())
                .orElseThrow(() -> new RuntimeException("Department not found"));

        employee.setDepartment(dept);

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

    // ✅ UPDATE EMPLOYEE
    @PutMapping("/employees/{id}")
    public Employee updateEmployee(@PathVariable Long id, @RequestBody Employee updated) {

        Employee emp = employeeRepo.findById(id).orElse(null);

        if (emp != null) {

            emp.setEmployeeName(updated.getEmployeeName());
            emp.setEmail(updated.getEmail());
            emp.setRole(updated.getRole());

            // handle department update safely
            if (updated.getDepartment() != null) {
                Department dept = departmentRepo.findById(updated.getDepartment().getId())
                        .orElseThrow(() -> new RuntimeException("Department not found"));
                emp.setDepartment(dept);
            }
        }

        return employeeRepo.save(emp);
    }

    // ✅ DELETE EMPLOYEE
    @DeleteMapping("/employees/{id}")
    public String deleteEmployee(@PathVariable Long id) {

        if (!employeeRepo.existsById(id)) {
            return "❌ Employee not found";
        }

        employeeRepo.deleteById(id);
        return "✅ Employee deleted successfully";
    }
}