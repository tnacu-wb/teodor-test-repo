package uk.co.whitbread.company.infrastructure.rest.controller.company;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import uk.co.whitbread.commons.exceptions.model.ErrorResponse;
import uk.co.whitbread.company.infrastructure.rest.controller.company.model.in.CompaniesProfileRequestDto;
import uk.co.whitbread.company.infrastructure.rest.controller.company.model.in.CompaniesRequestDto;
import uk.co.whitbread.company.infrastructure.rest.controller.company.model.out.CompaniesResponseDto;
import uk.co.whitbread.company.infrastructure.rest.controller.company.model.out.CompanyResponseDto;


public interface CompaniesControllerApi {

  @Operation(summary = "Retrieves Companies as a result")
  @ApiResponse(responseCode = "200", description = "Success", content = {
      @Content(mediaType = "application/json",
          schema = @Schema(implementation = CompaniesResponseDto.class))})
  @ApiResponse(responseCode = "400", description = "Error Occurred ", content = {
      @Content(mediaType = "application/json",
          schema = @Schema(implementation = ErrorResponse.class))})
  @ApiResponse(responseCode = "404", description = "Resource Not Found", content = {
      @Content(mediaType = "application/json",
          schema = @Schema(implementation = ErrorResponse.class))})
  @ApiResponse(responseCode = "500", description = "Internal Server Error", content = {
      @Content(mediaType = "application/json",
          schema = @Schema(implementation = ErrorResponse.class))})
  CompaniesResponseDto getCompaniesFromCdh(
      @ParameterObject @Valid CompaniesRequestDto companiesRequestDto);

  @Operation(summary = "Retrieve Company details for given company id")
  @ApiResponse(responseCode = "200", description = "Success", content = {
      @Content(mediaType = "application/json",
          schema = @Schema(implementation = CompanyResponseDto.class))})
  @ApiResponse(responseCode = "400", description = "Error Occurred ", content = {
      @Content(mediaType = "application/json",
          schema = @Schema(implementation = ErrorResponse.class))})
  @ApiResponse(responseCode = "404", description = "Resource Not Found", content = {
      @Content(mediaType = "application/json",
          schema = @Schema(implementation = ErrorResponse.class))})
  @ApiResponse(responseCode = "500", description = "Internal Server Error", content = {
      @Content(mediaType = "application/json",
          schema = @Schema(implementation = ErrorResponse.class))})
  CompanyResponseDto getCompany(
      @PathVariable @NotNull String companyId,
      @RequestParam(required = false) Boolean excludeNegotiatedRates);

  @Operation(summary = "Retrieve Company details for given company id")
  @ApiResponse(responseCode = "200", description = "Success", content = {
      @Content(mediaType = "application/json",
          schema = @Schema(implementation = CompaniesResponseDto.class))})
  @ApiResponse(responseCode = "400", description = "Error Occurred ", content = {
      @Content(mediaType = "application/json",
          schema = @Schema(implementation = ErrorResponse.class))})
  @ApiResponse(responseCode = "404", description = "Resource Not Found", content = {
      @Content(mediaType = "application/json",
          schema = @Schema(implementation = ErrorResponse.class))})
  @ApiResponse(responseCode = "500", description = "Internal Server Error", content = {
      @Content(mediaType = "application/json",
          schema = @Schema(implementation = ErrorResponse.class))})
  CompaniesResponseDto getCompaniesProfile(
      @ParameterObject @Validated CompaniesProfileRequestDto requestDto);

}
