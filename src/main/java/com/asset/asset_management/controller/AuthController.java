package com.asset.asset_management.controller;




import com.asset.asset_management.model.Employee;
import com.asset.asset_management.repository.EmployeeRepository;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/api")
public class AuthController {

    private final EmployeeRepository repository;

    public AuthController(EmployeeRepository repository) {
        this.repository = repository;
    }

    // 🔐 LOGIN API
    @PostMapping("/login")
    public Employee login(@RequestBody Employee loginData) {

        Employee emp = repository.findByEmail(loginData.getEmail());

        if (emp != null && emp.getPassword().equals(loginData.getPassword())) {
            return emp;
        } else {
            throw new RuntimeException("Invalid credentials");
        }
    }
}