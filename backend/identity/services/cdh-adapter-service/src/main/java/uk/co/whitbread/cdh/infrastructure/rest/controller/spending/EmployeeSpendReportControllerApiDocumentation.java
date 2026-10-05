package uk.co.whitbread.cdh.infrastructure.rest.controller.spending;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import java.util.List;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.web.bind.annotation.PathVariable;
import uk.co.whitbread.cdh.infrastructure.rest.controller.spending.model.in.EmployeeSpendRequestDto;
import uk.co.whitbread.cdh.infrastructure.rest.controller.spending.model.out.EmployeeSpendResponseDto;
import uk.co.whitbread.commons.exceptions.model.ErrorResponse;

public interface EmployeeSpendReportControllerApiDocumentation {

  @Operation(summary = "Get employee spend report",
      description = "Retrieves spending report for a specific employee within a date range from CDH. "
          + "Returns a list of monthly spend records.")
  @ApiResponse(responseCode = "200", description = "Success - Employee spend report retrieved successfully",
      content = @Content(mediaType = "application/json",
          array = @ArraySchema(schema = @Schema(implementation = EmployeeSpendResponseDto.class))))
  @ApiResponse(responseCode = "400", description = "Bad request - Invalid parameters (e.g., incorrect date format)",
      content = @Content(mediaType = "application/json", 
          schema = @Schema(implementation = ErrorResponse.class)))
  @ApiResponse(responseCode = "401", description = "Unauthorized - Authentication failure",
      content = @Content(mediaType = "application/json", 
          schema = @Schema(implementation = ErrorResponse.class)))
  @ApiResponse(responseCode = "404", description = "Not found - Employee or company not found",
      content = @Content(mediaType = "application/json", 
          schema = @Schema(implementation = ErrorResponse.class)))
  @ApiResponse(responseCode = "500", description = "Internal Server Error",
      content = @Content(mediaType = "application/json", 
          schema = @Schema(implementation = ErrorResponse.class)))
  List<EmployeeSpendResponseDto> getEmployeeSpendReport(
      @Parameter(description = "Company account ID", required = true, example = "COMP123")
      @PathVariable("companyAccountId") @NotNull String companyAccountId,
      @Parameter(description = "Employee account ID", required = true, example = "EMP456")
      @PathVariable("employeeAccountId") @NotNull String employeeAccountId,
      @Valid @ParameterObject EmployeeSpendRequestDto employeeSpendRequestDto);
}
