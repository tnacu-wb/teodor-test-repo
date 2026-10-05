package uk.co.whitbread.company.employee.model;

import lombok.Data;

@Data
public class GetEmployeeResponse {
    private boolean success;
    private Employee employee;
}