package com.example.employee_api.service;

import com.example.employee_api.model.Employee;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class EmployeeService {
    private List<Employee> employees = new ArrayList<>();

    public List<Employee> getALLEmployees() {
        return employees;
    }

    public Employee getEmployee(Long id) {
        return employees.stream()
                .filter(e -> e.getId().equals(id))
                .findFirst()
                .orElse(null);
    }

    public Employee updateEmployee(Long id, Employee updateEmployee){
        Employee employee = getEmployee(id);

        if (employee != null){
            employee.setName(updateEmployee.getName());
            employee.setEmail(updateEmployee.getEmail());
            employee.setDepartment(updateEmployee.getDepartment());
        }
        return employee;
    }

    public void deleteEmployee(Long id){
        employees.removeIf(e -> e.getId().equals(id));
    }
}
