package uk.co.whitbread.company.employee.model;

import lombok.Data;

@Data
public class UpdateRegistrationRequest {

    private AccessLevel accessLevel;
    private boolean approved;
    private String emailAddress;
}
