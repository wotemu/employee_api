package com.example.employee_api.service;

import com.example.employee_api.dto.EmployeeDTO;
import com.example.employee_api.dto.EmployeeRequest;
import com.example.employee_api.dto.PageResponse;
import com.example.employee_api.exception.ResourceNotFoundException;
import com.example.employee_api.mapper.EmployeeMapper;
import com.example.employee_api.model.Employee;
import com.example.employee_api.repository.EmployeeRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verifyNoInteractions;

import java.util.List;
import java.util.Optional;
import static org.mockito.ArgumentMatchers.any;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;
import org.springframework.data.domain.Pageable;

@ExtendWith(MockitoExtension.class)
public class EmployeeServiceTest {

    @Mock
    private EmployeeRepository repository;

    @InjectMocks
    EmployeeService service;

    @Mock
    private EmployeeMapper mapper;

    @Test
    void shouldCreateEmployee() {

        EmployeeRequest request = new EmployeeRequest();

        request.setName("John");
        request.setEmail("john@test.com");
        request.setDepartment("Engineering");
        request.setSalary(5000.0);
        request.setPhoneNumber("+3584757788");

        Employee employee = new Employee();

        employee.setName("John");
        employee.setEmail("john@test.com");
        employee.setDepartment("Engineering");
        employee.setSalary(5000.0);
        employee.setPhoneNumber("+3584757788");

        when(repository.save(any(Employee.class)))
                .thenReturn(employee);

        Employee saved =
                service.addEmployee(request);

        assertEquals("John", saved.getName());
        assertEquals("john@test.com", saved.getEmail());
        assertEquals("Engineering", saved.getDepartment());
        assertEquals(5000.0, saved.getSalary());
        assertEquals("+3584757788", saved.getPhoneNumber());

        verify(repository).save(any(Employee.class));
    }

    @Test
    void shouldGetEmployeeDTO() {

        Employee employee = new Employee();
        employee.setId(2L);
        employee.setName("John");
        employee.setEmail("john@test.com");

        EmployeeDTO dto = new EmployeeDTO();
        dto.setId(2L);
        dto.setName("John");
        dto.setEmail("john@test.com");

        when(repository.findById(2L))
                .thenReturn(Optional.of(employee));

        when(mapper.toDTO(employee))
                .thenReturn(dto);

        EmployeeDTO result = service.getEmployeeDTO(2L);

        assertEquals(2L, result.getId());
        assertEquals("John", result.getName());
        assertEquals("john@test.com", result.getEmail());

        verify(repository).findById(2L);
        verify(mapper).toDTO(employee);
    }

    @Test
    void shouldThrowExceptionWhenEmployeeNotFound() {

        when(repository.findById(9999L))
                .thenReturn(Optional.empty());

        assertThrows(
                ResourceNotFoundException.class,
                () -> service.getEmployeeDTO(9999L)
        );

        verify(repository).findById(9999L);
        verifyNoInteractions(mapper);
    }

    @Test
    void shouldReturnPaginatedEmployees() {

        Employee employee1 = new Employee();
        employee1.setName("John");
        employee1.setEmail("john@test.com");

        Employee employee2 = new Employee();
        employee2.setName("Mande");
        employee2.setEmail("mande@test.com");

        List<Employee> employees = List.of(employee1, employee2);

        Page<Employee> page =
                new PageImpl<>(employees, PageRequest.of(0, 2), 2);

        when(repository.findAll(any(Pageable.class)))
                .thenReturn(page);

        PageResponse<Employee> result =
                service.getEmployees(0, 2, "id", "asc");

        assertEquals(2, result.getContent().size());
        assertEquals(0, result.getPage());
        assertEquals(2, result.getSize());
        assertEquals(2, result.getTotalElements());
        assertEquals(1, result.getTotalPages());

        verify(repository).findAll(any(Pageable.class));
    }

    @Test
    void shouldReturnEmployeeDTO() {

        Employee employee = new Employee();
        employee.setId(1L);
        employee.setName("John");
        employee.setEmail("john@test.com");

        EmployeeDTO dto = new EmployeeDTO();
        dto.setId(1L);
        dto.setName("John");
        dto.setEmail("john@test.com");

        when(repository.findById(1L))
                .thenReturn(Optional.of(employee));

        when(mapper.toDTO(employee))
                .thenReturn(dto);

        EmployeeDTO result = service.getEmployeeDTO(1L);

        assertEquals(1L, result.getId());
        assertEquals("John", result.getName());
        assertEquals("john@test.com", result.getEmail());

        verify(repository).findById(1L);
        verify(mapper).toDTO(employee);
    }

    @Test
    void shouldThrowExceptionWhenEmployeeDoesNotExist() {

        when(repository.findById(999L))
                .thenReturn(Optional.empty());

        assertThrows(
                ResourceNotFoundException.class,
                () -> service.getEmployeeDTO(999L)
        );

        verify(repository).findById(999L);
    }

    @Test
    void shouldUpdateEmployee() {

        Employee existing = new Employee();
        existing.setId(1L);
        existing.setName("Old Name");
        existing.setEmail("old@test.com");

        Employee updated = new Employee();
        updated.setName("New Name");
        updated.setEmail("new@test.com");

        when(repository.findById(1L))
                .thenReturn(Optional.of(existing));

        when(repository.save(any(Employee.class)))
                .thenReturn(existing);

        Employee result =
                service.updateEmployee(1L, updated);

        assertEquals("New Name", result.getName());
        assertEquals("new@test.com", result.getEmail());

        verify(repository).findById(1L);
        verify(repository).save(existing);
    }

    @Test
    void shouldThrowExceptionWhenUpdatingMissingEmployee() {

        Employee updated = new Employee();

        when(repository.findById(999L))
                .thenReturn(Optional.empty());

        assertThrows(
                ResourceNotFoundException.class,
                () -> service.updateEmployee(999L, updated)
        );

        verify(repository).findById(999L);
    }

    @Test
    void shouldDeleteEmployee() {

        Employee employee = new Employee();
        employee.setId(2L);
        employee.setName("John");

        when(repository.findById(2L))
                .thenReturn(Optional.of(employee));

        service.deleteEmployee(2L);

        verify(repository).findById(2L);
        verify(repository).delete(employee);
    }

    @Test
    void shouldThrowExceptionWhenDeletingMissingEmployee() {

        when(repository.findById(999L))
                .thenReturn(Optional.empty());

        assertThrows(
                ResourceNotFoundException.class,
                () -> service.deleteEmployee(999L)
        );

        verify(repository).findById(999L);
        verify(repository, never()).delete(any(Employee.class));
    }

}
