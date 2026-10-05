package uk.co.whitbread.cdh.infrastructure.rest.controller.account;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import uk.co.whitbread.cdh.infrastructure.rest.controller.account.model.in.EmployeeSearchCriteriaDto;
import uk.co.whitbread.cdh.infrastructure.rest.controller.account.model.in.GetEmployeeRequestDto;
import uk.co.whitbread.cdh.infrastructure.rest.controller.account.model.out.employee.GetEmployeeResponseDto;
import uk.co.whitbread.cdh.infrastructure.rest.controller.account.model.out.employee.GetEmployeesResponseDto;
import uk.co.whitbread.commons.exceptions.model.ErrorResponse;

public interface EmployeeControllerApiDocumentation {

  @Operation(summary = "Fetches the employee details from CDH")
  @ApiResponse(responseCode = "200", description = "Success",
      content = @Content(mediaType = "application/json",
          schema = @Schema(implementation = GetEmployeeResponseDto.class)))
  @ApiResponse(responseCode = "404", description = "Not found",
      content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class)))
  @ApiResponse(responseCode = "400", description = "Bad request",
      content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class)))
  @ApiResponse(responseCode = "401.", description = "Unauthorized",
      content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class)))
  @ApiResponse(responseCode = "500", description = "Internal Server Error",
      content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class)))
  GetEmployeeResponseDto getEmployee(@Validated GetEmployeeRequestDto getEmployeeRequestDto);


  @Operation(summary = "Fetches the employees from CDH")
  @ApiResponse(responseCode = "200", description = "Success",
      content = @Content(mediaType = "application/json",
          schema = @Schema(implementation = GetEmployeesResponseDto.class)))
  @ApiResponse(responseCode = "404", description = "Not found",
      content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class)))
  @ApiResponse(responseCode = "400", description = "Bad request",
      content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class)))
  @ApiResponse(responseCode = "401.", description = "Unauthorized",
      content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class)))
  @ApiResponse(responseCode = "500", description = "Internal Server Error",
      content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class)))
  GetEmployeesResponseDto getEmployees(
      @Validated EmployeeSearchCriteriaDto employeeSearchCriteriaDto);
  
  @Operation(summary = "Fetches company employees based on given filters from CDH")
  @ApiResponse(responseCode = "200", description = "Success",
      content = @Content(mediaType = "application/json",
          schema = @Schema(implementation = GetEmployeesResponseDto.class)))
  @ApiResponse(responseCode = "404", description = "Not found",
      content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class)))
  @ApiResponse(responseCode = "400", description = "Bad request",
      content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class)))
  @ApiResponse(responseCode = "401.", description = "Unauthorized",
      content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class)))
  @ApiResponse(responseCode = "500", description = "Internal Server Error",
      content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class)))
  GetEmployeesResponseDto getCompanyEmployees(
      @PathVariable String companyAccountId, @RequestParam String accessedBy,
      @RequestParam Integer pageSize, @RequestParam(name = "pageToken", required = false) String pageToken,
      @RequestParam(name = "accessContext", required = false) String accessContext,
      @RequestParam(name = "awaitingApproval", required = false) Boolean awaitingApproval);
}
