package com.example.employee_api.service;

import com.example.employee_api.model.Employee;
import com.example.employee_api.repository.EmployeeRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class EmployeeServiceTest {

    @Mock
    private EmployeeRepository repository;

    @InjectMocks
    EmployeeService service;

    @Test
    void shouldCreateEmployee(){

        Employee employee = new Employee();

        employee.setName("John");
        employee.setEmail("john@test.com");

        when(repository.save(employee))
                .thenReturn(employee);

        Employee saved =
                service.addEmployee(employee);

        assertEquals("John", saved.getName());
        assertEquals("john@test.com", saved.getEmail());
        verify(repository)
                .save(employee);
    }
}
