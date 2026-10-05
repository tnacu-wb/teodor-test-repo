package uk.co.whitbread.company.controller;

import static uk.co.whitbread.company.utils.HeadersUtil.makeNotCacheableNoContent;

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
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import uk.co.whitbread.common.exceptions.entity.ErrorResponse;
import uk.co.whitbread.company.model.BookingAlerts;
import uk.co.whitbread.company.model.BookingAllowances;
import uk.co.whitbread.company.service.cdh.CdhBookingAlertsService;
import uk.co.whitbread.company.service.cdh.CdhBookingAllowancesService;
import uk.co.whitbread.shared.auth.account.CdhEmployeeDetails;
import uk.co.whitbread.shared.auth.service.TokenService;

@RestController
@RequestMapping("/companies")
@Tag(name = "Companies Controller")
@Slf4j
@RequiredArgsConstructor
public class CompaniesController {
    private final CdhBookingAlertsService cdhBookingAlertsService;
    private final CdhBookingAllowancesService cdhBookingAllowancesService;
    private final TokenService authTokenService;

    @Operation(summary = "", description = "Update Company Booking Allowances")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Success. No content."),
            @ApiResponse(responseCode = "400", description = "Bad Request: please check your input", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "500", description = "Internal Server Error", content = @Content(schema = @Schema(implementation = ErrorResponse.class)))})
    @PutMapping(value = "admin/{companyId}/booking-allowances",
            consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public ResponseEntity<Void> updateBookingAllowances(
            @Parameter @RequestHeader(value = "Authorization", required = false) String authorization,
            @Parameter(required = true) @PathVariable("companyId") String companyId,
            @Parameter(required = true, name = "request", description = "The booking allowance JSON payload") @Valid @RequestBody BookingAllowances bookingAllowances) {
        String sanitizedCompanyId = companyId.replaceAll("[^a-zA-Z0-9]", "");
        log.info("Called PUT /companies/admin/{}/booking-allowances", sanitizedCompanyId);
            CdhEmployeeDetails cdhEmployeeDetails =
                authTokenService.retrieveCdhEmployeeDetailsAndVerifyToken(authorization);
            String userEmail = cdhEmployeeDetails.getUserEmail();
            cdhBookingAllowancesService.updateBookingAllowances(companyId, bookingAllowances, userEmail);
            return makeNotCacheableNoContent();
    }


    @Operation(summary = "", description = "Update Company Booking Alerts")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Success. No content."),
            @ApiResponse(responseCode = "400", description = "Bad Request: please check your input", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "500", description = "Internal Server Error", content = @Content(schema = @Schema(implementation = ErrorResponse.class)))})
    @PutMapping(value = "admin/{companyId}/booking-alerts",
            consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public ResponseEntity<Void> updateBookingAlerts(
            @Parameter @RequestHeader(value = "Authorization", required = false) String authorization,
            @Parameter(required = true) @PathVariable("companyId") String companyId,
            @Parameter(required = true, name = "request", description = "The booking Alerts JSON payload") @Valid @RequestBody BookingAlerts bookingAlerts) {
        log.info("Called PUT /companies/admin/{}/booking-alerts", companyId);
            CdhEmployeeDetails cdhEmployeeDetails =
                authTokenService.retrieveCdhEmployeeDetailsAndVerifyToken(authorization);
            String userEmail = cdhEmployeeDetails.getUserEmail();
            cdhBookingAlertsService.updateBookingAlerts(companyId, bookingAlerts, userEmail);
            return makeNotCacheableNoContent();

    }
}
