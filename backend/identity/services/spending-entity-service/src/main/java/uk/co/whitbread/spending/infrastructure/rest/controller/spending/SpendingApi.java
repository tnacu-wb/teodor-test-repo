package uk.co.whitbread.spending.infrastructure.rest.controller.spending;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import java.util.List;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestHeader;
import uk.co.whitbread.commons.exceptions.model.ErrorResponse;
import uk.co.whitbread.spending.infrastructure.rest.controller.spending.model.in.AccountSpendingRequestDto;
import uk.co.whitbread.spending.infrastructure.rest.controller.spending.model.in.CompanySpendingRequestDto;
import uk.co.whitbread.spending.infrastructure.rest.controller.spending.model.in.EmployeeSpendRequestDto;
import uk.co.whitbread.spending.infrastructure.rest.controller.spending.model.in.PaymentInfoQueryParamsDto;
import uk.co.whitbread.spending.infrastructure.rest.controller.spending.model.out.AccountSpendingResponseDto;
import uk.co.whitbread.spending.infrastructure.rest.controller.spending.model.out.CompanySpendingResponseDto;
import uk.co.whitbread.spending.infrastructure.rest.controller.spending.model.out.EmployeeSpendResponseDto;
import uk.co.whitbread.spending.infrastructure.rest.controller.spending.model.out.PaymentInfoResponseDto;
import uk.co.whitbread.spending.infrastructure.rest.controller.spending.model.out.UpcomingSpendingResponseDto;

interface SpendingApi {
  String WB_AUTHORIZATION = "WB-Authorization";

  @Operation(summary = "Retrieves company spending information")
  @ApiResponse(responseCode = "200", description = "Success", content = {
      @Content(mediaType = "application/json",
          schema = @Schema(implementation = CompanySpendingResponseDto.class))})
  @ApiResponse(responseCode = "400", description = "Error Occurred ", content = {
      @Content(mediaType = "application/json",
          schema = @Schema(implementation = ErrorResponse.class))})
  @ApiResponse(responseCode = "404", description = "Resource Not Found", content = {
      @Content(mediaType = "application/json",
          schema = @Schema(implementation = ErrorResponse.class))})
  @ApiResponse(responseCode = "500", description = "Internal Server Error", content = {
      @Content(mediaType = "application/json",
          schema = @Schema(implementation = ErrorResponse.class))})
  ResponseEntity<CompanySpendingResponseDto> getCompanySpending(
        @NotEmpty String authorization,
        @Valid CompanySpendingRequestDto companySpendingRequestDto);

  @Operation(summary = "Retrieves account spending information")
  @ApiResponse(responseCode = "200", description = "Success", content = {
      @Content(mediaType = "application/json",
          schema = @Schema(implementation = AccountSpendingResponseDto.class))})
  @ApiResponse(responseCode = "400", description = "Error Occurred ", content = {
      @Content(mediaType = "application/json",
          schema = @Schema(implementation = ErrorResponse.class))})
  @ApiResponse(responseCode = "404", description = "Resource Not Found", content = {
      @Content(mediaType = "application/json",
          schema = @Schema(implementation = ErrorResponse.class))})
  @ApiResponse(responseCode = "500", description = "Internal Server Error", content = {
      @Content(mediaType = "application/json",
          schema = @Schema(implementation = ErrorResponse.class))})
  ResponseEntity<Object> getAccountSpending(
      @NotEmpty String authorization,
      @Valid AccountSpendingRequestDto accountSpendingRequestDto,
      HttpServletRequest httpServletRequest);

  @Operation(summary = "Retrieves employee spending information")
  @ApiResponse(responseCode = "200", description = "Success", content = {
      @Content(mediaType = "application/json",
          schema = @Schema(implementation = EmployeeSpendResponseDto.class))})
  @ApiResponse(responseCode = "400", description = "Error Occurred ", content = {
      @Content(mediaType = "application/json",
          schema = @Schema(implementation = ErrorResponse.class))})
  @ApiResponse(responseCode = "404", description = "Resource Not Found", content = {
      @Content(mediaType = "application/json",
          schema = @Schema(implementation = ErrorResponse.class))})
  @ApiResponse(responseCode = "500", description = "Internal Server Error", content = {
      @Content(mediaType = "application/json",
          schema = @Schema(implementation = ErrorResponse.class))})
  ResponseEntity<List<EmployeeSpendResponseDto>> getEmployeeSpend(
      @NotEmpty @Parameter(in = ParameterIn.HEADER, name = WB_AUTHORIZATION,
          example = "Bearer ===", required = true, schema = @Schema(type = "string"))
      @RequestHeader(value = WB_AUTHORIZATION) String authorization,
      @ParameterObject @Valid EmployeeSpendRequestDto employeeSpendRequestDto);

  @Operation(summary = "Retrieves upcoming spending information for specified account")
  @ApiResponse(responseCode = "200", description = "Success", content = {
      @Content(mediaType = "application/json",
          schema = @Schema(implementation = AccountSpendingResponseDto.class))})
  @ApiResponse(responseCode = "400", description = "Error Occurred ", content = {
      @Content(mediaType = "application/json",
          schema = @Schema(implementation = ErrorResponse.class))})
  @ApiResponse(responseCode = "404", description = "Resource Not Found", content = {
      @Content(mediaType = "application/json",
          schema = @Schema(implementation = ErrorResponse.class))})
  @ApiResponse(responseCode = "500", description = "Internal Server Error", content = {
      @Content(mediaType = "application/json",
          schema = @Schema(implementation = ErrorResponse.class))})
  ResponseEntity<UpcomingSpendingResponseDto> getUpcomingSpending(
        @NotEmpty String authorization,
        @NotEmpty String accountId,
        String tetheredUserGuid,
        HttpServletRequest httpServletRequest);

  @Operation(summary = "Retrieves payment info information for specified account")
  @ApiResponse(responseCode = "200", description = "Success", content = {
      @Content(mediaType = "application/json",
          schema = @Schema(implementation = PaymentInfoResponseDto.class))})
  @ApiResponse(responseCode = "400", description = "Error Occurred ", content = {
      @Content(mediaType = "application/json",
          schema = @Schema(implementation = ErrorResponse.class))})
  @ApiResponse(responseCode = "404", description = "Resource Not Found", content = {
      @Content(mediaType = "application/json",
          schema = @Schema(implementation = ErrorResponse.class))})
  @ApiResponse(responseCode = "500", description = "Internal Server Error", content = {
      @Content(mediaType = "application/json",
          schema = @Schema(implementation = ErrorResponse.class))})
  PaymentInfoResponseDto getPaymentInfo(
      @NotEmpty @Parameter(in = ParameterIn.HEADER, name = WB_AUTHORIZATION,
          example = "Bearer ===", required = true, schema = @Schema(type = "string"))
      @RequestHeader(value = WB_AUTHORIZATION) String authorization,
      @ParameterObject @Valid PaymentInfoQueryParamsDto paymentInfoQueryParamsDto,
      HttpServletRequest httpServletRequest);
}
