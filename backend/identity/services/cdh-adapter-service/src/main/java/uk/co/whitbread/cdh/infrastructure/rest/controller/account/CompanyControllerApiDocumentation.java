package uk.co.whitbread.cdh.infrastructure.rest.controller.account;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import uk.co.whitbread.cdh.infrastructure.rest.controller.account.model.in.AccessRequestDto;
import uk.co.whitbread.cdh.infrastructure.rest.controller.account.model.in.CompanySearchCriteriaDto;
import uk.co.whitbread.cdh.infrastructure.rest.controller.account.model.out.CompanyDto;
import uk.co.whitbread.cdh.infrastructure.rest.controller.account.model.out.CompanySearchResponseDto;
import uk.co.whitbread.cdh.infrastructure.rest.controller.account.model.out.CompanySuppressRatesDto;
import uk.co.whitbread.commons.exceptions.model.ErrorResponse;

public interface CompanyControllerApiDocumentation {

  @Operation(summary = "Get company by account ID from CDH Account Services API")
  @ApiResponse(responseCode = "200", description = "Success",
      content = @Content(mediaType = "application/json",
          schema = @Schema(implementation = CompanyDto.class)))
  @ApiResponse(responseCode = "404", description = "Not found",
      content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class)))
  @ApiResponse(responseCode = "400", description = "Bad request",
      content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class)))
  @ApiResponse(responseCode = "401", description = "Unauthorized",
      content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class)))
  @ApiResponse(responseCode = "500", description = "Internal Server Error",
      content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class)))
  CompanyDto getCompany(String companyAccountId, AccessRequestDto accessRequestDto);

  @Operation(summary = "Search companies from CDH Account Services API V2")
  @ApiResponse(responseCode = "200", description = "Success",
      content = @Content(mediaType = "application/json",
          schema = @Schema(implementation = CompanySearchResponseDto.class)))
  @ApiResponse(responseCode = "404", description = "Not found",
      content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class)))
  @ApiResponse(responseCode = "400", description = "Bad request",
      content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class)))
  @ApiResponse(responseCode = "401", description = "Unauthorized",
      content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class)))
  @ApiResponse(responseCode = "500", description = "Internal Server Error",
      content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class)))
  CompanySearchResponseDto getCompanies(CompanySearchCriteriaDto companySearchCriteriaDto);

  @Operation(summary = "Get suppress rates for a company by account ID from CDH Account Services API")
  @ApiResponse(responseCode = "200", description = "Success",
      content = @Content(mediaType = "application/json",
          schema = @Schema(implementation = CompanySuppressRatesDto.class)))
  @ApiResponse(responseCode = "404", description = "Not found",
      content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class)))
  @ApiResponse(responseCode = "400", description = "Bad request",
      content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class)))
  @ApiResponse(responseCode = "401", description = "Unauthorized",
      content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class)))
  @ApiResponse(responseCode = "500", description = "Internal Server Error",
      content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class)))
  CompanySuppressRatesDto getCompanySuppressRates(String companyAccountId, AccessRequestDto accessRequestDto);

}
