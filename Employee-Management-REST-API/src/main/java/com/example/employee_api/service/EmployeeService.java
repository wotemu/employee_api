package com.example.employee_api.service;

import com.example.employee_api.dto.EmployeeDTO;
import com.example.employee_api.mapper.EmployeeMapper;
import com.example.employee_api.exception.EmployeeNotFoundException;
import com.example.employee_api.model.Employee;
import com.example.employee_api.repository.EmployeeRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class EmployeeService {

    private final EmployeeRepository repository;
    private final EmployeeMapper mapper;

    private static final Logger logger =
            LoggerFactory.getLogger(EmployeeService.class);

    public EmployeeService(EmployeeRepository repository, EmployeeMapper mapper) {
        this.repository = repository;
        this.mapper = mapper;
    }

    public List<Employee> getAllEmployees() {
        return repository.findAll();
    }

    public EmployeeDTO getEmployeeDTO(Long id) {
        Employee employee = repository.findById(id)
                .orElseThrow(() -> new EmployeeNotFoundException(id));

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

        logger.info("Creating employee with email: {}", employee.getEmail());

        Employee savedEmployee = repository.save(employee);

        logger.info("Employee created successfully with id: {}", savedEmployee.getId());

        return savedEmployee;
    }

    @Transactional
    public Employee updateEmployee(Long id, Employee updatedEmployee) {

        logger.info("Updating employee with id: {}", id);

        Employee employee = repository.findById(id)
                .orElseThrow(() -> new EmployeeNotFoundException(id));

        employee.setName(updatedEmployee.getName());
        employee.setEmail(updatedEmployee.getEmail());
        employee.setDepartment(updatedEmployee.getDepartment());
        employee.setSalary(updatedEmployee.getSalary());
        employee.setPhoneNumber(updatedEmployee.getPhoneNumber());

        repository.save(employee);

        logger.info("Employee updated successfully.");

        return employee;
    }


    public void deleteEmployee(Long id) {
        repository.deleteById(id);
    }

    // <---pagination and sorting -->
    public Page<Employee> getEmployees(
            int page,
            int size,
            String sortBy,
            String direction) {

        Sort sort = direction.equalsIgnoreCase("desc")
                ? Sort.by(sortBy).descending()
                : Sort.by(sortBy).ascending();

        Pageable pageable = PageRequest.of(page, size, sort);

        return repository.findAll(pageable);
    }
}