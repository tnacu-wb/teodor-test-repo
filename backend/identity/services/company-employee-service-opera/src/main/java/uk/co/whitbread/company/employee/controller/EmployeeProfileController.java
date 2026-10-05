package uk.co.whitbread.company.employee.controller;

import static uk.co.whitbread.company.employee.utils.SanitizingUtils.sanitize;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import uk.co.whitbread.common.exceptions.entity.ErrorResponse;
import uk.co.whitbread.company.employee.model.BookingPreference;
import uk.co.whitbread.company.employee.model.PasswordChange;
import uk.co.whitbread.company.employee.model.UpdateRegistrationRequest;
import uk.co.whitbread.company.employee.service.EmployeeProfileService;
import uk.co.whitbread.company.employee.utils.BookingChannel;
import uk.co.whitbread.shared.auth.account.CdhEmployeeDetails;
import uk.co.whitbread.shared.auth.service.TokenService;

@Slf4j
@RequestMapping("/companies")
@RestController
@Tag(name = "Company Employee Profile operations")
@RequiredArgsConstructor
public class EmployeeProfileController {
    private final EmployeeProfileService employeeProfileService;
    private final TokenService authTokenService;

    @Operation(summary = "getEmployeeBookingPreferences", description = "Get employee booking preferences")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "All good",
                content = @Content(schema = @Schema(implementation = BookingPreference.class))),
            @ApiResponse(responseCode = "400", description= "Error Occurred: Please check your input ",
                content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "500", description = "Server Error: our bad something went wrong on our side",
                content = @Content(schema = @Schema(implementation = ErrorResponse.class)))})
    @RequestMapping(value = "/{companyId}/employees/{employeeId}/bookingpreferences", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.OK)
    public BookingPreference getEmployeeBookingPreferences(
            @Parameter(required = true) @PathVariable("companyId") String companyId,
            @Parameter(required = true) @PathVariable("employeeId") String employeeId,
            @Parameter @RequestHeader(name = "Authorization", required = false) String authorization,
            @Parameter @RequestHeader(name = "bookingChannel", required = false, defaultValue = "CBT") BookingChannel bookingChannel) {

        log.info("Called GET /companies/{}/employees/{}/bookingpreferences", sanitize(companyId), sanitize(employeeId));
        final CdhEmployeeDetails cdhEmployeeDetails =
                authTokenService.retrieveCdhEmployeeDetailsAndVerifyToken(authorization);
        String userEmail = cdhEmployeeDetails.getUserEmail();
        return employeeProfileService.getCdhEmployeeBookingPreferences(companyId, employeeId, userEmail);
    }

    @Operation(summary = "updateEmployeeBookingPreferences", description = "Update employee booking preferences")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Success. No content."),
            @ApiResponse(responseCode = "400", description = "Bad Request: please check your input",
                content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "500", description = "Error Occurred: Our bad something went wrong on our side",
                content = @Content(schema = @Schema(implementation = ErrorResponse.class)))})
    @RequestMapping(value = "/{companyId}/employees/{employeeId}/bookingpreferences", method = RequestMethod.PATCH,
            consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public ResponseEntity<Void> updateEmployeeBookingPreferences(
            @Parameter @RequestHeader(name = "Authorization", required = false) String authorization,
            @Parameter(required = true) @PathVariable("companyId") String companyId,
            @Parameter(required = true) @PathVariable("employeeId") String employeeId,
            @Parameter(required = true, name = "payload", description = "The Booking Preference JSON payload") @Valid @RequestBody BookingPreference bookingPreference) {
        log.info("Called PATCH /companies/{}/employees/{}/bookingpreferences",
            sanitize(companyId),
            sanitize(employeeId));

        final CdhEmployeeDetails cdhEmployeeDetails =
            authTokenService.retrieveCdhEmployeeDetailsAndVerifyToken(authorization);
        String userEmail = cdhEmployeeDetails.getUserEmail();
        employeeProfileService.updateCdhEmployeeBookingPreferences(companyId, employeeId, bookingPreference, userEmail);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @Operation(summary = "approveRejectEmployee", description = "Approve or Reject an employee")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Success. No Content"),
            @ApiResponse(responseCode = "400", description = "Error Occurred: Please check your input",
                content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "500", description = "Server Error: Our bad something went wrong on our side",
                content = @Content(schema = @Schema(implementation = ErrorResponse.class)))})
    @RequestMapping(value = "/admin/{companyId}/employees/approvereject", method = RequestMethod.PATCH,
            consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public ResponseEntity<Void> approveRejectEmployee(
            @Parameter @RequestHeader(name = "country", required = false, defaultValue = "gb") String countryCode,
            @Parameter @RequestHeader(name = "language", required = false, defaultValue = "en") String languageCode,
            @Parameter @RequestHeader(name = "Authorization", required = false) String authorization,
            @Parameter(required = true) @PathVariable("companyId") String companyId,
            @Parameter(name = "payload", description = "The Invite JSON payload") @RequestBody UpdateRegistrationRequest payload) {
        log.info("Called POST /companies/admin/{}/employees/approvereject",
            sanitize(companyId));

        final CdhEmployeeDetails travelManagerDetails =
            authTokenService.retrieveCdhEmployeeDetailsAndVerifyToken(authorization);
        employeeProfileService.updateCdhRegistration(payload, travelManagerDetails, languageCode);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}


