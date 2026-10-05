package uk.co.whitbread.company.employee.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.validation.Valid;
import lombok.Data;
import lombok.EqualsAndHashCode;

import jakarta.validation.constraints.NotNull;

@JsonInclude(JsonInclude.Include.NON_NULL)
@EqualsAndHashCode(callSuper = true)
@Data
public class Employee extends EmployeeSummary {

    private String ghNumber;

    private String phoneNumber;

    private String mobileNumber;

    private String password;

    @NotNull
    @Valid
    private Address address;

    private String centralCardId;

    private String dialingCode;

    private EmployeeAnswers employeeAnswers;

    private String position;

    private Boolean innBusiness;

    private UpdatePreferencesRequest updatePreferencesRequest;

}
