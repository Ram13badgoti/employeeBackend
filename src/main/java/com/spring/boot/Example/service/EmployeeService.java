package com.spring.boot.Example.service;


import com.spring.boot.Example.Models.Employee;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.data.domain.Pageable;


@Service
public interface EmployeeService {
    public String addEmployee(Employee employee);
    public Page<Employee> getAllEmployees(Pageable pageable);
    public void deleteEmployee(String employeeId);
    public void updateEmployee(String employeeId, Employee updatedEmployee);
    public Employee getNthLevelManager(String employeeId,int level);
}