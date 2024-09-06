package com.booleanuk.api.departments;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.sql.SQLException;
import java.util.List;

@RestController
@RequestMapping("/departments")
public class DepartmentController {
    private DepartmentRepository repository;

    public DepartmentController() throws SQLException {
        this.repository = new DepartmentRepository();
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<Department> getAll() throws SQLException {
        return this.repository.getAll();
    }

    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public Department get(@PathVariable (name = "id") int id) throws SQLException {
        return this.repository.get(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Department create(@RequestBody Department department) throws SQLException {
        return this.repository.add(department);
    }
}
