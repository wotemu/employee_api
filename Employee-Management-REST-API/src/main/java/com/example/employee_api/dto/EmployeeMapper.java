package com.example.employee_api.dto;

import com.example.employee_api.model.Employee;
import org.springframework.stereotype.Component;

@Component
public class EmployeeMapper {
    // Map Entity → DTO
    public EmployeeDTO toDTO(Employee employee) {

        return new EmployeeDTO(
                employee.getId(),
                employee.getName(),
                employee.getEmail(),
                employee.getDepartment(),
                employee.getSalary(),
                employee.getPhoneNumber()
        );
    }

    //Map DTO → Entity
    public Employee toEntity(EmployeeDTO dto) {

        Employee employee = new Employee();

        employee.setId(dto.getId());
        employee.setName(dto.getName());
        employee.setEmail(dto.getEmail());
        employee.setDepartment(dto.getDepartment());
        employee.setSalary(dto.getSalary());
        employee.setPhoneNumber(dto.getPhoneNumber());

        return employee;
    }
}
