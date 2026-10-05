package uk.co.whitbread.company.employee.controller;

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
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import uk.co.whitbread.common.exceptions.entity.ErrorResponse;
import uk.co.whitbread.company.employee.mapper.EmployeeMapper;
import uk.co.whitbread.company.employee.model.EmployeeActivationResponse;
import uk.co.whitbread.company.employee.model.innbusiness.ApproveRejectRequest;
import uk.co.whitbread.company.employee.model.innbusiness.SendActivationRequest;
import uk.co.whitbread.company.employee.service.EmployeeActivationService;
import uk.co.whitbread.company.employee.service.InnBusinessService;
import uk.co.whitbread.company.employee.utils.BookingChannel;

@Slf4j
@RequestMapping("/v1/company-employee-service")
@RestController
@Tag(name = "InnBusiness related actions")
@RequiredArgsConstructor
public class InnBusinessController {

  private final InnBusinessService innBusinessService;
  private final EmployeeActivationService employeeActivationService;
  private final EmployeeMapper employeeMapper;

  @Operation(summary = "sendActivationEmail", description = "Send activation email to travel manager")
  @ApiResponses(value = {
        @ApiResponse(responseCode = "204", description = "Success. No content."),
        @ApiResponse(responseCode = "400", description = "Bad Request: please check your input",
              content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
        @ApiResponse(responseCode = "500", description = "Error Occurred: Our bad something went wrong on our side",
              content = @Content(schema = @Schema(implementation = ErrorResponse.class)))})
  @PostMapping(value = "/innbusiness/travelManager/activationEmail", consumes = MediaType.APPLICATION_JSON_VALUE,
        produces = MediaType.APPLICATION_JSON_VALUE)
  @ResponseStatus(HttpStatus.OK)
  public ResponseEntity<Void> sendActivationEmail(
        @Parameter(required = true, name = "payload", description = "The activation request") @Valid @RequestBody SendActivationRequest sendActivationRequest) {
    String sanitizedCompanyName = sendActivationRequest.getCompanyName()
          .replace("\n", "").replace("\r", "");
    String sanitizedLanguage = sendActivationRequest.getLanguage()
          .replace("\n", "").replace("\r", "");
    log.info("Called POST /v1/company-employee-service/innbusiness/travelManager/activationEmail with companyName={}, language={}, email=*.",
          sanitizedCompanyName, sanitizedLanguage);
    sendActivationRequest.setCompanyName(sanitizedCompanyName);
    sendActivationRequest.setLanguage(sanitizedLanguage);

    innBusinessService.sendActivationEmail(sendActivationRequest);

    return new ResponseEntity<>(HttpStatus.OK);
  }

  @Operation(summary = "acceptRejectEmployee", description = "Approve or reject an employee for InnBusiness")
  @ApiResponses(value = {
        @ApiResponse(responseCode = "204", description = "Success. No content."),
        @ApiResponse(responseCode = "400", description = "Bad Request: please check your input",
              content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
        @ApiResponse(responseCode = "500", description = "Error Occurred: Our bad something went wrong on our side",
              content = @Content(schema = @Schema(implementation = ErrorResponse.class)))})
  @PostMapping(value = "/innbusiness/travelManager/approvereject", consumes = MediaType.APPLICATION_JSON_VALUE,
        produces = MediaType.APPLICATION_JSON_VALUE)
  @ResponseStatus(HttpStatus.OK)
  public ResponseEntity<Void> approveRejectEmployee(
        @Parameter @RequestHeader(name = "WB-Authorization", required = true) String authorization,
        @Parameter(required = true, name = "payload", description = "The approve reject request") @Valid @RequestBody ApproveRejectRequest approveRejectRequest) {
    String sanitizedLanguage = approveRejectRequest.getLanguage().replace("\n", "").replace("\r", "");
    log.info("Called POST /v1/company-employee-service/innbusiness/travelManager/approvereject with approved={}, accessLevel={}, language={}, email=*.",
          approveRejectRequest.getApproved(), approveRejectRequest.getAccessLevel(), sanitizedLanguage);

    innBusinessService.approveRejectEmployee(approveRejectRequest, authorization);

    return new ResponseEntity<>(HttpStatus.OK);
  }

  @GetMapping(value = "/innbusiness/employees/activation-details", produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<EmployeeActivationResponse> getInnBusinessActivationDetails(
      @RequestHeader(name = "bookingChannel", required = false, defaultValue = "CBT") BookingChannel bookingChannel,
      @RequestHeader(name = "country", required = false, defaultValue = "gb") String countryCode,
      @RequestHeader(name = "language", required = false, defaultValue = "en") String languageCode,
      @RequestParam(name = "activation-key") String activationKey) {
    String sanitizedActivationKey = activationKey.replace("\n", "").replace("\r", "");
    log.info("Called GET /v1/company-employee-service/innbusiness/employees/activation-details with activation-key: {}",
        sanitizedActivationKey);

    var response = employeeActivationService
        .getInnBusinessEmployeeActivationResponse(activationKey);
    return ResponseEntity.ok().body(employeeMapper
        .toEmployeeActivationResponse(response));
  }
}
