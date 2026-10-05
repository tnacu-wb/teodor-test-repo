package uk.co.whitbread.company.employee.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

@JsonInclude(JsonInclude.Include.NON_NULL)
@Data
public class EmployeeSummary {

    protected String id;

    protected String employeeId;

    @NotBlank
    protected String title;

    @NotNull
    @NotEmpty
    @Pattern(regexp = "^\\D+$", message = "must not contain numbers")
    protected String firstName;

    @NotNull
    @NotEmpty
    @Pattern(regexp = "^\\D+$", message = "must not contain numbers")
    protected String lastName;

    @NotNull
    @NotEmpty
    @Email
    protected String emailAddress;

    protected boolean textConfirmation;

    protected AccessLevel accessLevel;
    protected EmployeeStatus employeeStatus;

    protected String guestHistoryNumber;

    protected boolean lockedForEditing;
}

