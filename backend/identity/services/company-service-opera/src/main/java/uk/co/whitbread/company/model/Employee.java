package uk.co.whitbread.company.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Employee {
    private String id;
    private String ghNumber;
    @NotEmpty
    private String emailAddress;
    @NotEmpty
    private String position;
    @NotEmpty
    private String phoneNumber;
    @NotEmpty
    private String mobileNumber;
    private boolean textConfirmation;
    @NotEmpty
    private String title;
    @NotEmpty
    private String firstName;
    @NotEmpty
    private String lastName;
    private String centralCardId;
    private Address address;
    private AccessLevel accessLevel;
    private EmployeeStatus employeeStatus;
    private String dialingCode;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private EmployeeAnswers employeeAnswers;
}
