package com.example.employee_api.repository;

import com.example.employee_api.model.Employee;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface EmployeeRepository extends JpaRepository<Employee, Long> {
    List<Employee> findByDepartment(String department);
    Optional<Employee> findByEmail(String email);
   List<Employee> findByDepartmentAndSalaryGreaterThan(String department, Double salary);
}