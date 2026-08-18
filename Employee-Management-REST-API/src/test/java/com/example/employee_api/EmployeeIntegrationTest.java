package com.example.employee_api;

import com.example.employee_api.dto.EmployeeDTO;
import com.example.employee_api.model.Employee;
import com.example.employee_api.repository.EmployeeRepository;
import com.example.employee_api.service.EmployeeService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import com.example.employee_api.dto.EmployeeRequest;
import com.example.employee_api.exception.ResourceNotFoundException;

import static org.junit.jupiter.api.Assertions.assertThrows;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class EmployeeIntegrationTest {

    @Autowired
    private EmployeeService service;

    @Autowired
    private EmployeeRepository repository;

    @Test
    void shouldCreateEmployeeUsingService() {

        EmployeeRequest request = new EmployeeRequest();

        request.setName("Integration Test");
        request.setEmail("integration@test.com");
        request.setDepartment("Engineering");
        request.setSalary(5000.0);
        request.setPhoneNumber("0401234567");

        Employee saved = service.addEmployee(request);

        assertNotNull(saved.getId());

        Employee found = repository.findById(saved.getId())
                .orElseThrow();

        assertEquals("Integration Test", found.getName());
        assertEquals("integration@test.com", found.getEmail());
        assertEquals("Engineering", found.getDepartment());
    }

    @Test
    void shouldGetEmployeeById() {

        Employee employee = new Employee();

        employee.setName("Get Test");
        employee.setEmail("get@test.com");
        employee.setDepartment("IT");
        employee.setSalary(6000.0);
        employee.setPhoneNumber("0401111111");

        Employee saved = repository.save(employee);

        EmployeeDTO result = service.getEmployeeDTO(saved.getId());

        assertNotNull(result);
        assertEquals(saved.getId(), result.getId());
        assertEquals("Get Test", result.getName());
        assertEquals("get@test.com", result.getEmail());
    }

    @Test
    void shouldThrowExceptionWhenEmployeeDoesNotExist() {

        Long nonExistingId = 99999L;

        assertThrows(
                ResourceNotFoundException.class,
                () -> service.getEmployeeDTO(nonExistingId)
        );
    }

    @Test
    void shouldUpdateEmployee() {

        Employee employee = new Employee();

        employee.setName("Before Update");
        employee.setEmail("before@test.com");
        employee.setDepartment("IT");
        employee.setSalary(4000.0);
        employee.setPhoneNumber("0402222222");

        Employee saved = repository.save(employee);

        Employee updatedEmployee = new Employee();

        updatedEmployee.setName("After Update");
        updatedEmployee.setEmail("after@test.com");
        updatedEmployee.setDepartment("Engineering");
        updatedEmployee.setSalary(6000.0);
        updatedEmployee.setPhoneNumber("0403333333");

        Employee result =
                service.updateEmployee(saved.getId(), updatedEmployee);

        assertEquals("After Update", result.getName());
        assertEquals("after@test.com", result.getEmail());
        assertEquals("Engineering", result.getDepartment());
        assertEquals(6000.0, result.getSalary());
        assertEquals("0403333333", result.getPhoneNumber());

        Employee fromDatabase =
                repository.findById(saved.getId())
                        .orElseThrow();

        assertEquals("After Update", fromDatabase.getName());
        assertEquals("after@test.com", fromDatabase.getEmail());
        assertEquals("Engineering", fromDatabase.getDepartment());
    }

    @Test
    void shouldDeleteEmployee() {

        Employee employee = new Employee();

        employee.setName("Delete Test");
        employee.setEmail("delete@test.com");
        employee.setDepartment("IT");
        employee.setSalary(4000.0);
        employee.setPhoneNumber("0404444444");

        Employee saved = repository.save(employee);

        Long id = saved.getId();

        service.deleteEmployee(id);

        assertFalse(repository.findById(id).isPresent());
    }
}