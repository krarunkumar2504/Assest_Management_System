package com.asset.asset_management.controller;

import com.asset.asset_management.model.Department;
import com.asset.asset_management.repository.DepartmentRepository;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/api")
public class DepartmentController {

    private final DepartmentRepository departmentRepo;

    public DepartmentController(DepartmentRepository departmentRepo) {
        this.departmentRepo = departmentRepo;
    }

    // ✅ GET ALL DEPARTMENTS
    @GetMapping("/departments")
    public List<Department> getAllDepartments() {
        return departmentRepo.findAll();
    }
}