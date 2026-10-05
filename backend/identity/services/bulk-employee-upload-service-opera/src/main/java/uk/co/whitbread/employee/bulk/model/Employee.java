package uk.co.whitbread.employee.bulk.model;

import com.fasterxml.jackson.annotation.JsonInclude;
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

    @NotNull
    private Address address;

    private String centralCardId;

    private EmployeeAnswers employeeAnswers;

    private String position;
}
