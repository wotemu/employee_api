package com.example.employee_api.service;

import com.example.employee_api.dto.EmployeeDTO;
import com.example.employee_api.dto.EmployeeMapper;
import com.example.employee_api.model.Employee;
import com.example.employee_api.repository.EmployeeRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class EmployeeService {

    private final EmployeeRepository repository;
    private final EmployeeMapper mapper;

    public EmployeeService(EmployeeRepository repository, EmployeeMapper mapper) {
        this.repository = repository;
        this.mapper = mapper;
    }

    public List<Employee> getAllEmployees() {
        return repository.findAll();
    }

    public EmployeeDTO getEmployeeDTO(Long id) {
        Employee employee = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Employee not found"));

        return mapper.toDTO(employee);
    }

    public List<Employee> employeeByDepartment(String department) {
        return repository.findByDepartment(department);
    }

    public Employee employeeByEmail(String email) {
        return repository.findByEmail(email).orElse(null);
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