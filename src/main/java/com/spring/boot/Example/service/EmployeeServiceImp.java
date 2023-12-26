package com.spring.boot.Example.service;

import com.spring.boot.Example.Models.Employee;
import com.spring.boot.Example.repo.EmployeeRepository;
import io.micrometer.common.util.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.data.domain.Pageable;
import java.util.Optional;


@Service
@Validated
public class EmployeeServiceImp implements EmployeeService  {

    @Autowired
    private EmployeeRepository employeeRepository;
    @Autowired
    private EmailService emailService;

    @Override
    public String addEmployee(Employee employee) {
        try {

            if (employee.getEmail() != null && employeeRepository.existsByEmail(employee.getEmail())) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "User with the same email already exists");
            }

            // Generate a unique ID for the Employee
//            String uuid = UUID.randomUUID().toString();
//            employee.setId(uuid);

            notifyLevel1Manager(employee);
            employeeRepository.save(employee);

            return employee.getId();
        } catch (ResponseStatusException e) {
            throw e;
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Error adding employee", e);
        }
    }

private void notifyLevel1Manager(Employee employee) {

    String reportsToId = employee.getReportsTo();
    Optional<Employee> manager = employeeRepository.findById(reportsToId);

    manager.ifPresent(m -> {
        emailService.sendNewEmployeeNotification(
                m.getEmail(),
                employee.getEmployeeName(),
                employee.getPhoneNumber(),
                employee.getEmail()
        );
    });
}
    private String getLevel1ManagerEmail(String reportsTo) {
        Employee manager = employeeRepository.findById(reportsTo).orElse(null);

        // Check if manager is found and return the email
        return (manager != null) ? manager.getEmail() : null;
    }
    public Page<Employee> getAllEmployees(Pageable pageable) {
        return employeeRepository.findAll(pageable);
    }


    public void deleteEmployee(String employeeId) {
        try {
            // Check if the employee exists
            System.out.println(employeeRepository.existsById(employeeId));
            if (!employeeRepository.existsById(employeeId)) {
                throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Employee not found");
            }
            // Delete the employee
            employeeRepository.deleteById(employeeId);
        } catch (EmptyResultDataAccessException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Employee not found", e);
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Error deleting employee", e);
        }
    }

    @Override
    public void updateEmployee(String employeeId,  Employee updatedEmployee) {
        try {
            // Check if the employee exists
            Employee existingEmployee = employeeRepository.findById(employeeId)
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Employee not found"));

            // Validate updatedEmployee data as needed
            validateUpdatedEmployeeData(updatedEmployee);
            updatedEmployee.setId(employeeId);

            employeeRepository.save(updatedEmployee);
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Error updating employee", e);
        }
    }

    private void validateUpdatedEmployeeData(Employee updatedEmployee) {

        if (StringUtils.isEmpty(updatedEmployee.getEmployeeName())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Employee name cannot be null or empty");
        }

        if (StringUtils.isEmpty(updatedEmployee.getPhoneNumber()) || !updatedEmployee.getPhoneNumber().matches("\\d{10}")) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid phone number format");
        }

        if (!isValidEmail(updatedEmployee.getEmail())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid email address");
        }
    }

    private boolean isValidEmail(String email) {
        return email != null && email.matches("^[\\w-]+(\\.[\\w-]+)*@[\\w-]+(\\.[\\w-]+)*(\\.[a-z]{2,})$");
    }

    public Employee getNthLevelManager( String employeeId, int level) {
        try {
            // Validate level
            if (level < 1) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Level must be greater than or equal to 1");
            }

            // Retrieve the employee
            Employee employee = employeeRepository.findById(employeeId)
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Employee not found"));

            return getNthLevelManagerHelper(employee, level);
        } catch (ResponseStatusException e) {
            throw e;
        } catch (EmptyResultDataAccessException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Employee not found", e);
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Error getting nth level manager", e);
        }
    }

    private Employee getNthLevelManagerHelper( Employee employee, int level) {
        if (level <= 0) {
            return employee;
        }

        // If reportsTo is null or empty, there is no manager
        if (StringUtils.isEmpty(employee.getReportsTo())) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Manager not found at level " + level);
        }

        // Recursive call to get the nth level manager
        try {
            return getNthLevelManagerHelper(
                    employeeRepository.findById(employee.getReportsTo())
                            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Manager not found")),
                    level - 1
            );
        } catch (ResponseStatusException e) {
            throw e;
        } catch (EmptyResultDataAccessException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Manager not found", e);
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Error getting nth level manager", e);
        }
    }
}
