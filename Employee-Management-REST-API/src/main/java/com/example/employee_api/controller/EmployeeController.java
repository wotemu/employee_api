package com.example.employee_api.controller;

import com.example.employee_api.dto.EmployeeDTO;
import com.example.employee_api.dto.EmployeeRequest;
import com.example.employee_api.dto.PageResponse;
import com.example.employee_api.model.Employee;
import com.example.employee_api.service.EmployeeService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/employees")
@SecurityRequirement(name = "bearerAuth")
public class EmployeeController {
    private final EmployeeService service;
    private static final Logger logger =
            LoggerFactory.getLogger(EmployeeController.class);

    public EmployeeController(EmployeeService service) {
        this.service = service;
    }

   // @GetMapping
    //public List<Employee> getEmployees(){
      //  return service.getAllEmployees();
    //}

    @GetMapping("/{id}")
    public EmployeeDTO getEmployee(@PathVariable Long id) {
        logger.info("GET /employees/{}", id);

        return service.getEmployeeDTO(id);
    }

    @GetMapping("/department/{department}")
    public List<Employee> getEmployeesByDepartment(@PathVariable String department){
        return service.employeeByDepartment(department);
    }

    @GetMapping("/email/{email}")
    public Employee getEmployeeByEmail(@PathVariable String email){
        return service.employeeByEmail(email);
    }

    @GetMapping("/search")
    public List<Employee>  getEmployeesByDepartmentAndSalary(@RequestParam() String department, @RequestParam Double salary){
        return service.employeeByDepartmentAndSalaryGreaterThan(department, salary);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Employee createEmployee(@Valid @RequestBody EmployeeRequest request) {
        return service.addEmployee(request);
    }
    @PutMapping("/{id}")
    public Employee updateEmployee(
            @Valid @RequestBody Employee employee,
            @PathVariable Long id) {

        return service.updateEmployee(id, employee);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteEmployee(@PathVariable Long id) {

        service.deleteEmployee(id);

        return ResponseEntity.noContent().build();
    }

    @GetMapping
    public PageResponse<Employee> getEmployees(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "asc") String direction) {

        return service.getEmployees(
                page,
                size,
                sortBy,
                direction);
    }
}
