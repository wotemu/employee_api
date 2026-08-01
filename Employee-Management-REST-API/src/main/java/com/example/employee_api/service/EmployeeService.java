package com.example.employee_api.service;

import com.example.employee_api.model.Employee;
import com.example.employee_api.repository.EmployeeRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class EmployeeService {

    private final EmployeeRepository repository;

    public EmployeeService(EmployeeRepository repository) {
        this.repository = repository;
    }

    public List<Employee> getAllEmployees() {
        return repository.findAll();
    }

    public Employee getEmployee(Long id) {
        return repository.findById(id).orElse(null);
    }

    public List<Employee> employeeByDepartment(String department) {
        return repository.findByDepartment(department);
    }

    public Employee employeeByEmail(String email) {
        return repository.findByEmail(email);
    }
    public List<Employee> employeeByDepartmentAndSalaryGreaterThan(String department, Double salary) {
        return repository.findByDepartmentAndSalaryGreaterThan(department, salary);
    }

    public Employee addEmployee(Employee employee) {
        return repository.save(employee);
    }

    public Employee updateEmployee(Long id, Employee updatedEmployee) {
        Employee employee = repository.findById(id).orElse(null);

        if (employee == null) {
            return null;
        }

        employee.setName(updatedEmployee.getName());
        employee.setEmail(updatedEmployee.getEmail());
        employee.setDepartment(updatedEmployee.getDepartment());
        employee.setSalary(updatedEmployee.getSalary());
        employee.setPhoneNumber(updatedEmployee.getPhoneNumber());

        return repository.save(employee);
    }

    public void deleteEmployee(Long id) {
        repository.deleteById(id);
    }
}