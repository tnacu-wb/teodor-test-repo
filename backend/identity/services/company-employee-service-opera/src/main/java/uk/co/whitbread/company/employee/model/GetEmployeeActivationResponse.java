package uk.co.whitbread.company.employee.model;

import lombok.Data;

@Data
public class GetEmployeeActivationResponse extends GetEmployeeResponse {
    private String sessionId;
    private String companyId;
}