package uk.co.whitbread.company.employee.model;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import jakarta.validation.constraints.NotNull;

@Data
public class UpdateStatusRequest {

    @NotNull
    @Schema(required = true)
    private EmployeeStatus employeeStatus;

}
