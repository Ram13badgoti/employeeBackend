package com.spring.boot.Example.repo;
import com.spring.boot.Example.Models.Employee;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;



public interface EmployeeRepository extends MongoRepository<Employee, String> {
    boolean existsByEmail(String email);
    Page<Employee> findAll(Pageable pageable);

}
