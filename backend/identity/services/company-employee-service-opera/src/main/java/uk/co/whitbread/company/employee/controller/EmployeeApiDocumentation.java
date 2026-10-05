package uk.co.whitbread.company.employee.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.headers.Header;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.net.URI;
import org.springframework.http.ResponseEntity;
import uk.co.whitbread.common.exceptions.entity.ErrorResponse;
import uk.co.whitbread.company.employee.model.Employee;
import uk.co.whitbread.company.employee.model.GetEmployeesRequest;
import uk.co.whitbread.company.employee.model.GetEmployeesResponse;
import uk.co.whitbread.company.employee.model.InviteRequest;
import uk.co.whitbread.company.employee.model.UpdateAccessLevelRequest;
import uk.co.whitbread.company.employee.utils.BookingChannel;

@Tag(name = "Company Employee operations")
public interface EmployeeApiDocumentation {

  @Operation(summary = "updateEmployee", description = "Update an employee in a company")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "204", description = "Success. No content."),
      @ApiResponse(responseCode = "400", description = "Bad Request: please check your input",
          content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
      @ApiResponse(responseCode = "500", description = "Error Occurred: Our bad something went wrong on our side",
          content = @Content(schema = @Schema(implementation = ErrorResponse.class)))})
  ResponseEntity<Void> updateEmployee(
      @Parameter String sessionId,
      @Parameter String authorization,
      @Parameter String countryCode,
      @Parameter String languageCode,
      @Parameter(required = true) String companyId,
      @Parameter(required = true) String employeeId,
      @Parameter BookingChannel bookingChannel,
      @Parameter String activationKey,
      @Parameter(required = true, name = "payload", description = "The employee JSON payload") Employee employee);

  @Operation(summary = "updateEmployeeAccessLevel", description = "Update employee access level")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "204", description = "All good. No Content"),
      @ApiResponse(responseCode = "400", description = "Error Occurred: Please check your input ",
          content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
      @ApiResponse(responseCode = "500", description = "Server Error: Our bad something went wrong on our side",
          content = @Content(schema = @Schema(implementation = ErrorResponse.class)))})
  ResponseEntity<Void> updateEmployeeAccessLevel(
      @Parameter String sessionId,
      @Parameter String authorization,
      @Parameter String countryCode,
      @Parameter String languageCode,
      @Parameter(required = true) String companyId,
      @Parameter(required = true) String employeeId,
      @Parameter BookingChannel bookingChannel,
      @Parameter(required = true, name = "payload", description= "The Booking Preference JSON payload") UpdateAccessLevelRequest request);

  @Operation(summary = "getEmployees", description = "Get Employees")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Success"),
      @ApiResponse(responseCode = "400", description = "Error Occurred ",
          content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
      @ApiResponse(responseCode = "500", description = "Internal Server Error",
          content = @Content(schema = @Schema(implementation = ErrorResponse.class)))})
  @Parameters({
      @Parameter(in = ParameterIn.PATH, name = "companyId", description = "CompanyId",
          required = true, content = @Content(schema = @Schema(type = "string", defaultValue = "46"))),
      @Parameter(in = ParameterIn.QUERY, name = "searchCriteria", description = "SearchCriteria",
          content = @Content(schema = @Schema(type = "string", defaultValue = "me"))),
      @Parameter(in = ParameterIn.QUERY,name = "awaitingApproval", description = "AwaitingApproval",
          content = @Content(schema = @Schema(type = "boolean", defaultValue = "false"))),
      @Parameter(in = ParameterIn.QUERY, name = "size", description = "Maximum number of results to return per page. Should return this many unless on last page.",
          required = true, content = @Content(schema = @Schema(type = "integer", defaultValue = "10"))),
      @Parameter(in = ParameterIn.QUERY, name = "page", description = "The requested page number.",
          required = false, content = @Content(schema = @Schema(type = "integer", defaultValue = "1"))),
      @Parameter(in = ParameterIn.QUERY, name = "pageToken", description = "The requested page token.",
          required = false, content = @Content(schema = @Schema(type = "string"))),
      @Parameter(in = ParameterIn.HEADER, name = "bookingChannel", description = "The Booking-Channel to use.",
          content = @Content(schema = @Schema(type = "string", defaultValue = "CBT", allowableValues = {"CBT", "MOBILE", "WEB", "WEB_DE"})))
  })
  GetEmployeesResponse getEmployees(
      @Parameter String sessionId,
      @Parameter String authorization,
      @Parameter(required = true) String companyId,
      @Parameter BookingChannel bookingChannel,
      GetEmployeesRequest findEmployeesRequest);

  @Operation(summary = "", description = "Get Employee")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Success",
          headers =
          @Header(name = "session-id", schema = @Schema(implementation = String.class),
              description = "BART session ID, returned only after the activation is triggered")),
      @ApiResponse(responseCode = "400", description = "Error Occurred ",
          content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
      @ApiResponse(responseCode = "500", description = "Internal Server Error",
          content = @Content(schema = @Schema(implementation = ErrorResponse.class)))})
  ResponseEntity<Employee> getEmployee(@Parameter String sessionId,
      @Parameter String authorization,
      @Parameter(required = true) String companyId,
      @Parameter(required = true) String employeeId,
      @Parameter BookingChannel bookingChannel,
      @Parameter String activationKey);

  @Operation(summary = "addEmployee", description = "Add an employee to a company")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "201", description = "Created",
          headers =
          @Header(name = "Location", schema = @Schema(implementation = URI.class),
              description = "Unique identifier for the employee created")),
      @ApiResponse(responseCode = "400", description = "Bad Request: please check your input",
          content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
      @ApiResponse(responseCode = "500", description = "Error Occurred: Our bad something went wrong on our side",
          content = @Content(schema = @Schema(implementation = ErrorResponse.class)))})
  ResponseEntity<Void> addEmployee(
      @Parameter String sessionId,
      @Parameter(required = true) String companyId,
      @Parameter String countryCode,
      @Parameter String languageCode,
      @Parameter BookingChannel bookingChannel,
      @Parameter String authorization,
      @Parameter(required = true, name = "payload", description = "The employee JSON payload") Employee employee);

  @Operation(summary = "inviteEmployee", description = "End point to send an invite to an employee")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "201", description = "Created"),
      @ApiResponse(responseCode = "400", description = "Error Occurred: Please check your input",
          content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
      @ApiResponse(responseCode = "500", description = "Server Error: Our bad something went wrong on our side",
          content = @Content(schema = @Schema(implementation = ErrorResponse.class)))})
  ResponseEntity<Void> inviteEmployee(
      @Parameter String sessionId,
      @Parameter String authorization,
      @Parameter(required = true) String companyId,
      @Parameter String countryCode,
      @Parameter String languageCode,
      @Parameter BookingChannel bookingChannel,
      @Parameter(required = true, name = "payload", description = "The Invite JSON payload") InviteRequest inviteRequest);

  @Operation(summary = "", description = "Get Employee Activation Details")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Success",
          headers = {
              @Header(name = "session-id", schema = @Schema(implementation = String.class),
                  description = "BART session ID"),
              @Header(name = "company-id", schema = @Schema(implementation = String.class),
                  description = "Company ID")
          }),
      @ApiResponse(responseCode = "400", description = "Error Occurred ",
          content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
      @ApiResponse(responseCode = "500", description = "Internal Server Error",
          content = @Content(schema = @Schema(implementation = ErrorResponse.class)))})
  ResponseEntity<Employee> getActivationDetails(
      @Parameter BookingChannel bookingChannel,
      @Parameter String countryCode,
      @Parameter String languageCode,
      @Parameter(required = true) String activationKey);

  @Operation(summary = "", description = "Activate Travel Manager's Account")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Success",
          headers = {
              @Header(name = "session-id", schema = @Schema(implementation = String.class),
                  description = "BART session ID"),
              @Header(name = "company-id", schema = @Schema(implementation = String.class),
                  description = "Company ID")
          }),
      @ApiResponse(responseCode = "400", description = "Error Occurred ",
          content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
      @ApiResponse(responseCode = "500", description = "Internal Server Error",
          content = @Content(schema = @Schema(implementation = ErrorResponse.class)))})
  ResponseEntity<Employee> activationTravelManagerAccount(
      @Parameter BookingChannel bookingChannel,
      @Parameter String countryCode,
      @Parameter String languageCode,
      @Parameter(required = true) String activationKey);
}
