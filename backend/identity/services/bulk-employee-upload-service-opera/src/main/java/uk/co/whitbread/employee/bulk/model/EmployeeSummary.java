package uk.co.whitbread.employee.bulk.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

@JsonInclude(JsonInclude.Include.NON_NULL)
@Data
public class EmployeeSummary {

    protected String id;

    @NotEmpty
    protected String title;

    @NotEmpty
    protected String firstName;

    @NotEmpty
    protected String lastName;

    @NotEmpty
    protected String emailAddress;

    protected boolean textConfirmation;

    protected AccessLevel accessLevel;

    protected EmployeeStatus employeeStatus;

    protected boolean lockedForEditing;
}

