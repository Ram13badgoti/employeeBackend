package com.spring.boot.Example.controller;


import com.spring.boot.Example.Models.Employee;

import com.spring.boot.Example.service.EmployeeService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
@RequestMapping("/api/employees")
public class HomeController {

    @Autowired(required=true)
    private EmployeeService employeeService;


    @PostMapping("/add")
    public ResponseEntity<String> addEmployee(@Valid @RequestBody Employee employee) {
        try {
           String str =employeeService.addEmployee(employee);
           return ResponseEntity.ok("Employee add successful of employeeId  "+str);
        } catch (ResponseStatusException e) {
            throw e;
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Internal Server Error", e);
        }
    }

    @GetMapping
    public ResponseEntity<Page<Employee>> getAllEmployees( @RequestParam(defaultValue = "0") int page,
                                                           @RequestParam(defaultValue = "10") int size,
                                                           @RequestParam(defaultValue = "employeeName") String sortBy) {
        try {
            Pageable pageable = PageRequest.of(page, size, Sort.by(sortBy));

            Page<Employee> employees = employeeService.getAllEmployees(pageable);
            return  ResponseEntity.ok(employees);
        } catch (ResponseStatusException e) {
            throw e;
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Internal Server Error", e);
        }
    }

    @DeleteMapping("/{employeeId}")
    public ResponseEntity<String> deleteEmployee(@PathVariable String employeeId) {
        try {
            employeeService.deleteEmployee(employeeId);
            return ResponseEntity.ok("Employee deleted successfully");
        }  catch (EmptyResultDataAccessException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Employee not found");
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Error deleting employee", e);
        }
    }

    @PutMapping("/{employeeId}")
    public ResponseEntity<String> updateEmployee(@PathVariable String employeeId, @Valid @RequestBody Employee updatedEmployee) {
        try {
            employeeService.updateEmployee(employeeId, updatedEmployee);
           return ResponseEntity.ok("Employee updated successfully");
        } catch (ResponseStatusException e) {
            throw e;
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Internal Server Error", e);
        }
    }

    @GetMapping("/{employeeId}/manager/{level}")
    public ResponseEntity<Employee> getNthLevelManager(
            @PathVariable @Valid String employeeId,
            @PathVariable @Valid @Min(value = 1, message = "Level must be greater than or equal to 1") int level
    ) {
        try {
           Employee employee= employeeService.getNthLevelManager(employeeId, level);
           return ResponseEntity.ok(employee);
        } catch (ResponseStatusException e) {
            throw e;
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Internal Server Error", e);
        }
    }

}