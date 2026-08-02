package com.example.employee_api.controller;

import com.example.employee_api.dto.EmployeeDTO;
import com.example.employee_api.model.Employee;
import com.example.employee_api.service.EmployeeService;
import jakarta.websocket.server.PathParam;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/employees")
public class EmployeeController {
    private final EmployeeService service;

    public EmployeeController(EmployeeService service) {
        this.service = service;
    }

    @GetMapping
    public List<Employee> getEmployees(){
        return service.getAllEmployees();
    }

    @GetMapping("/{id}")
    public EmployeeDTO getEmployee(@PathVariable Long id) {
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
    public Employee createEmployee(@RequestBody Employee employee){
        return service.addEmployee(employee);
    }

    @PutMapping("/{id}")
    public Employee updateEmployee(@RequestBody Employee employee, @PathVariable Long id){
        return service.updateEmployee(id, employee);
    }

    @DeleteMapping("/{id}")
    public void deleteEmployee(@PathVariable Long id){
        service.deleteEmployee(id);
    }

    @GetMapping
    public Page<Employee> getEmployees(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "asc") String direction) {

        return service.getEmployees(page, size, sortBy, direction);
    }
}
