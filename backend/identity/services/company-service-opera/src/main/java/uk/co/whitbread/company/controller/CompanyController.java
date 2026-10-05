package uk.co.whitbread.company.controller;

import static uk.co.whitbread.company.utils.HeadersUtil.makeNotCacheable;
import static uk.co.whitbread.company.utils.Utils.sanitizeInputString;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
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
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import uk.co.whitbread.common.exceptions.entity.ErrorResponse;
import uk.co.whitbread.company.model.Address;
import uk.co.whitbread.company.model.CheckCompanyResponse;
import uk.co.whitbread.company.model.CompanyDetailsResponse;
import uk.co.whitbread.company.model.CompanySummary;
import uk.co.whitbread.company.service.cdh.CdhCompanyDetailsService;
import uk.co.whitbread.shared.auth.account.CdhEmployeeDetails;
import uk.co.whitbread.shared.auth.service.TokenService;

@Slf4j
@RestController
@RequiredArgsConstructor
@Tag(name = "Company related operations")
public class CompanyController {
  private final TokenService authTokenService;
  private final CdhCompanyDetailsService cdhCompanyDetailsService;

  @Operation(summary = "/company/check", description = "Search for a company by companyName and address")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Success", content = @Content(schema = @Schema(implementation = CheckCompanyResponse.class))),
      @ApiResponse(responseCode = "400", description = "Error Occurred ", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
      @ApiResponse(responseCode = "500", description = "Internal Server Error", content = @Content(schema = @Schema(implementation = ErrorResponse.class)))})
  @Parameter(in = ParameterIn.QUERY, name = "companyName", description = "CompanyName",
      content = @Content(schema = @Schema(type = "string", defaultValue = "Whitbread")))
  @Parameter(in = ParameterIn.QUERY, name = "addressLine1", description = "AddressLine1",
      content = @Content(schema = @Schema(type = "string", defaultValue = "120 Holborn")))
  @Parameter(in = ParameterIn.QUERY, name = "addressLine2", description = "AddressLine2",
      content = @Content(schema = @Schema(type = "string")))
  @Parameter(in = ParameterIn.QUERY, name = "addressLine3", description = "AddressLine3",
      content = @Content(schema = @Schema(type = "string")))
  @Parameter(in = ParameterIn.QUERY, name = "addressLine4", description = "AddressLine4",
      content = @Content(schema = @Schema(type = "string")))
  @Parameter(in = ParameterIn.QUERY, name = "addressLine5", description = "AddressLine5",
      content = @Content(schema = @Schema(type = "string")))
  @Parameter(in = ParameterIn.QUERY, name = "postresponseCode", description = "PostresponseCode",
      content = @Content(schema = @Schema(type = "string", defaultValue = "EC1N 2TD")))
  @Parameter(in = ParameterIn.QUERY, name = "country", description = "Country",
      content = @Content(schema = @Schema(type = "string", defaultValue = "GB")))
  @GetMapping(value = "/company/check", produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<CheckCompanyResponse> searchByCompanyNameAndAddress(
      @RequestParam("companyName") String companyName, @Parameter(hidden = true) Address address) {
    log.info("Called /company/check with companyName & address");
      return makeNotCacheable(
          cdhCompanyDetailsService.searchByCompanyNameAndAddress(companyName, address));
  }

  @Operation(summary = "Retrieve company details", description = "Retreive all company details")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Success", content = @Content(schema = @Schema(implementation = CompanyDetailsResponse.class))),
      @ApiResponse(responseCode = "400", description = "Error Occurred ", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
      @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
      @ApiResponse(responseCode = "403", description = "Bad Request", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
      @ApiResponse(responseCode = "500", description = "Internal Server Error", content = @Content(schema = @Schema(implementation = ErrorResponse.class)))})
  @Parameter(in = ParameterIn.HEADER, name = "Authorization", description = "Required for authorization",
      content = @Content(schema = @Schema(type = "string")))
  @GetMapping(value = "/company/{companyId}", produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<CompanyDetailsResponse> retrieveCompanyDetails(
      @Parameter @RequestHeader(name = "Authorization", required = false) String authorization,
      @PathVariable("companyId") String companyId) {

    log.info("Called GET /company/{} with token", sanitizeInputString(companyId));
    CdhEmployeeDetails cdhEmployeeDetails = authTokenService.retrieveCdhEmployeeDetailsAndVerifyToken(
        authorization);
    return makeNotCacheable(
        cdhCompanyDetailsService.getCompanyDetails(companyId, cdhEmployeeDetails.getUserEmail()));
  }

  @Operation(summary = "Update company details", description = "Update company details")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "204", description = "No content. Operation succeeded."),
      @ApiResponse(responseCode = "400", description = "Error Occurred ", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
      @ApiResponse(responseCode = "500", description = "Internal Server Error", content = @Content(schema = @Schema(implementation = ErrorResponse.class)))})
  @ResponseStatus(HttpStatus.NO_CONTENT)
  @PutMapping(value = "/company/admin/{companyId}",
      consumes = MediaType.APPLICATION_JSON_VALUE,
      produces = MediaType.APPLICATION_JSON_VALUE
  )
  public ResponseEntity<Void> updateCompanyDetails(
      @Parameter @RequestHeader(value = "Authorization", required = false) String authorization,
      @Parameter(required = true) @PathVariable("companyId") String companyId,
      @Parameter(required = true, name = "request", description = "Update company request") @Valid @RequestBody CompanySummary companySummary) {
    log.info("Called PUT /company/admin/{companyId} with company id={}",
        sanitizeInputString(companyId));
      CdhEmployeeDetails cdhEmployeeDetails =
          authTokenService.retrieveCdhEmployeeDetailsAndVerifyToken(authorization);
      String userEmail = cdhEmployeeDetails.getUserEmail();
      cdhCompanyDetailsService.updateCompany(companyId, companySummary, userEmail);
      return new ResponseEntity<>(HttpStatus.NO_CONTENT);
  }

}
