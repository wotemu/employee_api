package com.example.employee_api;

import com.example.employee_api.repository.EmployeeRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;

@SpringBootTest
@AutoConfigureMockMvc
class EmployeeApiIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private EmployeeRepository repository;

    @Test
    void shouldCreateEmployeeThroughApi() throws Exception {

        String request = """
                {
                    "name": "API Test",
                    "email": "api@test.com",
                    "department": "Engineering",
                    "salary": 5000,
                    "phoneNumber": "0405555555"
                }
                """;

        mockMvc.perform(
                        post("/employees")
                                .with(csrf())
                                .with(user("admin").roles("ADMIN"))
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(request)
                )
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.name").value("API Test"))
                .andExpect(jsonPath("$.email").value("api@test.com"))
                .andExpect(jsonPath("$.department").value("Engineering"))
                .andExpect(jsonPath("$.salary").value(5000.0))
                .andExpect(jsonPath("$.phoneNumber").value("0405555555"));
    }

    @Test
    void shouldReturn400WhenCreatingEmployeeWithInvalidData() throws Exception {

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
                                .with(user("admin").roles("ADMIN"))
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(request)
                )
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.name")
                        .value("Name is required"))
                .andExpect(jsonPath("$.email")
                        .value("Email must be valid"))
                .andExpect(jsonPath("$.department")
                        .value("Department is required"))
                .andExpect(jsonPath("$.salary")
                        .value("Salary must be greater than zero"))
                .andExpect(jsonPath("$.phoneNumber")
                        .value("Phone number must be 10 to 12 digits and may start with '+'"));
    }

    @Test
    void shouldReturn403WhenUserTriesToCreateEmployee() throws Exception {

        String requestBody = """
        {
            "name": "API Test",
            "email": "user@test.com",
            "department": "Engineering",
            "salary": 5000,
            "phoneNumber": "0405555555"
        }
        """;

        mockMvc.perform(
                        post("/employees")
                                .with(user("testuser").roles("USER"))
                                .with(csrf())
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(requestBody)
                )
                .andExpect(status().isForbidden());
    }

    @Test
    void shouldReturn403WhenUnauthenticatedUserTriesToCreateEmployee() throws Exception {

        String requestBody = """
        {
            "name": "API Test",
            "email": "anonymous@test.com",
            "department": "Engineering",
            "salary": 5000,
            "phoneNumber": "0405555555"
        }
        """;

        mockMvc.perform(
                        post("/employees")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(requestBody)
                )
                .andExpect(status().isForbidden());
    }

    @Test
    void shouldAllowUserToGetEmployees() throws Exception {

        mockMvc.perform(
                        get("/employees")
                                .with(user("testuser").roles("USER"))
                )
                .andExpect(status().isOk());
    }

    @Test
    void shouldAllowAdminToGetEmployees() throws Exception {

        mockMvc.perform(
                        get("/employees")
                                .with(user("admin").roles("ADMIN"))
                )
                .andExpect(status().isOk());
    }

    @Test
    void shouldRejectUnauthenticatedUserWhenGettingEmployees() throws Exception {

        mockMvc.perform(
                        get("/employees")
                )
                .andExpect(status().isForbidden());
    }

    @Test
    void shouldGetEmployeeById() throws Exception {

        mockMvc.perform(
                        get("/employees/1")
                                .with(user("user").roles("USER"))
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1));
    }

    @Test
    void shouldReturn404WhenEmployeeDoesNotExist() throws Exception {

        mockMvc.perform(
                        get("/employees/9999")
                                .with(user("user").roles("USER"))
                )
                .andExpect(status().isNotFound());
    }

    @Test
    void shouldAllowAdminToUpdateEmployee() throws Exception {

        String requestBody = """
        {
            "name": "Updated Employee",
            "email": "updated@test.com",
            "department": "Engineering",
            "salary": 6000,
            "phoneNumber": "0405555555"
        }
        """;

        mockMvc.perform(
                        put("/employees/1")
                                .with(user("admin").roles("ADMIN"))
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(requestBody)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Updated Employee"))
                .andExpect(jsonPath("$.email").value("updated@test.com"));
    }

    @Test
    void shouldRejectUserWhenUpdatingEmployee() throws Exception {

        String requestBody = """
        {
            "name": "Updated Employee",
            "email": "updated@test.com",
            "department": "Engineering",
            "salary": 6000,
            "phoneNumber": "0405555555"
        }
        """;

        mockMvc.perform(
                        put("/employees/1")
                                .with(user("user").roles("USER"))
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(requestBody)
                )
                .andExpect(status().isForbidden());
    }

    @Test
    void shouldRejectUnauthenticatedUserWhenUpdatingEmployee()
            throws Exception {

        String requestBody = """
        {
            "name": "Updated Employee",
            "email": "updated@test.com",
            "department": "Engineering",
            "salary": 6000,
            "phoneNumber": "0405555555"
        }
        """;

        mockMvc.perform(
                        put("/employees/1")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(requestBody)
                )
                .andExpect(status().isForbidden());
    }

    @Test
    void shouldReturn400WhenUpdatingEmployeeWithInvalidData()
            throws Exception {

        String requestBody = """
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
                                .with(user("admin").roles("ADMIN"))
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(requestBody)
                )
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldReturn404WhenUpdatingNonExistingEmployee()
            throws Exception {

        String requestBody = """
        {
            "name": "Updated Employee",
            "email": "updated@test.com",
            "department": "Engineering",
            "salary": 6000,
            "phoneNumber": "0405555555"
        }
        """;

        mockMvc.perform(
                        put("/employees/9999")
                                .with(user("admin").roles("ADMIN"))
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(requestBody)
                )
                .andExpect(status().isNotFound());
    }
}