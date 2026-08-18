 package com.example.employee_api.controller;

import com.example.employee_api.dto.EmployeeRequest;
import com.example.employee_api.exception.ResourceNotFoundException;
import com.example.employee_api.model.Employee;
import com.example.employee_api.security.JwtAuthenticationFilter;
import com.example.employee_api.service.EmployeeService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.mockito.ArgumentMatchers.eq;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.mockito.ArgumentMatchers.any;

import com.example.employee_api.dto.PageResponse;
import com.example.employee_api.dto.EmployeeDTO;

import java.util.List;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;

import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;

@WebMvcTest(EmployeeController.class)
@AutoConfigureMockMvc(addFilters = false)
class EmployeeControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private EmployeeService service;

    @MockitoBean
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    @Test
    void shouldReturn400WhenEmployeeIsInvalid() throws Exception {

        String request = """
                {
                    "name": "",
                    "email": "wrong-email",
                    "department": "",
                    "salary": -100,
                    "phoneNumber": ""
                }
                """;

        mockMvc.perform(
                        post("/employees")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(request)
                )
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldCreateEmployee() throws Exception {

        String request = """
            {
                "name": "John",
                "email": "john@test.com",
                "department": "Engineering",
                "salary": 5000,
                "phoneNumber": "0401234567"
            }
            """;

        Employee employee = new Employee();
        employee.setName("John");
        employee.setEmail("john@test.com");
        employee.setDepartment("Engineering");
        employee.setSalary(5000.0);
        employee.setPhoneNumber("0401234567");

        when(service.addEmployee(any(EmployeeRequest.class)))
                .thenReturn(employee);

        mockMvc.perform(
                        post("/employees")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(request)
                )
                .andExpect(status().isOk());
    }

    @Test
    void shouldGetEmployees() throws Exception {

        Employee employee = new Employee();
        employee.setId(1L);
        employee.setName("John");
        employee.setEmail("john@test.com");
        employee.setDepartment("Engineering");
        employee.setSalary(5000.0);
        employee.setPhoneNumber("0401234567");

        PageResponse<Employee> response = new PageResponse<>(
                List.of(employee),
                0,
                5,
                1,
                1
        );

        when(service.getEmployees(0, 5, "id", "asc"))
                .thenReturn(response);

        mockMvc.perform(
                        get("/employees")
                                .param("page", "0")
                                .param("size", "5")
                                .param("sortBy", "id")
                                .param("direction", "asc")
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].name")
                        .value("John"))
                .andExpect(jsonPath("$.content[0].email")
                        .value("john@test.com"));
    }

    @Test
    void shouldGetEmployeeById() throws Exception {

        EmployeeDTO employee = new EmployeeDTO();

        employee.setId(1L);
        employee.setName("John");
        employee.setEmail("john@test.com");
        employee.setDepartment("Engineering");
        employee.setSalary(5000.0);
        employee.setPhoneNumber("0401234567");

        when(service.getEmployeeDTO(1L))
                .thenReturn(employee);

        mockMvc.perform(
                        get("/employees/1")
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("John"))
                .andExpect(jsonPath("$.email").value("john@test.com"));
    }

    @Test
    void shouldUpdateEmployee() throws Exception {

        Employee updatedEmployee = new Employee();
        updatedEmployee.setId(1L);
        updatedEmployee.setName("Updated Name");
        updatedEmployee.setEmail("updated@test.com");
        updatedEmployee.setDepartment("Engineering");
        updatedEmployee.setSalary(5000.0);
        updatedEmployee.setPhoneNumber("0401234567");

        when(service.updateEmployee(
                eq(1L),
                any(Employee.class)
        )).thenReturn(updatedEmployee);

        String request = """
            {
                "name": "Updated Name",
                "email": "updated@test.com",
                "department": "Engineering",
                "salary": 5000,
                "phoneNumber": "0401234567"
            }
            """;

        mockMvc.perform(
                        put("/employees/1")
                                .with(csrf())
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(request)
                )
                .andExpect(status().isOk());
    }

    @Test
    void shouldReturn404WhenUpdatingMissingEmployee() throws Exception {

        when(service.updateEmployee(
                eq(999L),
                any(Employee.class)
        )).thenThrow(
                new ResourceNotFoundException(
                        "Employee not found with id: 999"
                )
        );

        String request = """
            {
                "name": "Updated Name",
                "email": "updated@test.com",
                "department": "Engineering",
                "salary": 5000,
                "phoneNumber": "0401234567"
            }
            """;

        mockMvc.perform(
                        put("/employees/999")
                                .with(csrf())
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(request)
                )
                .andExpect(status().isNotFound());
    }

    @Test
    void shouldReturn400WhenUpdatingEmployeeWithInvalidData() throws Exception {

        String request = """
            {
                "name": "",
                "email": "wrong-email",
                "department": "",
                "salary": -100,
                "phoneNumber": ""
            }
            """;

        mockMvc.perform(
                        put("/employees/1")
                                .with(csrf())
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(request)
                )
                .andExpect(status().isBadRequest());

        verifyNoInteractions(service);
    }

    @Test
    void shouldDeleteEmployee() throws Exception {

        doNothing().when(service).deleteEmployee(2L);

        mockMvc.perform(
                        delete("/employees/2")
                                .with(csrf())
                )
                .andExpect(status().isNoContent());

        verify(service).deleteEmployee(2L);
    }

    @Test
    void shouldReturn404WhenDeletingMissingEmployee() throws Exception {

        doThrow(new ResourceNotFoundException(
                "Employee not found with id: 999"
        )).when(service).deleteEmployee(999L);

        mockMvc.perform(
                        delete("/employees/999")
                                .with(csrf())
                )
                .andExpect(status().isNotFound());

        verify(service).deleteEmployee(999L);
    }


}