package uk.co.whitbread.company.employee.model;

import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.Schema.RequiredMode;
import lombok.Data;

import jakarta.validation.constraints.NotNull;

@Data
public class GetEmployeesRequest {

    @Schema(example = "me")
    private String searchCriteria;
    @Schema(example = "false")
    private String awaitingApproval;
    @NotNull
    @Schema(requiredMode = RequiredMode.REQUIRED, example = "20")
    private Integer size;
    @Schema(example = "1")
    private Integer page;
    @Schema
    private String pageToken;
    @Schema(example = "false", defaultValue = "true")
    private boolean shouldFilterEmployees = true;
}
