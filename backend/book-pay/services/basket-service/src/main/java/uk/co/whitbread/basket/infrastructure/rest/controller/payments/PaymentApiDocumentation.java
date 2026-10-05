package uk.co.whitbread.basket.infrastructure.rest.controller.payments;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import org.springframework.http.ResponseEntity;
import uk.co.whitbread.basket.infrastructure.rest.controller.basket.model.out.BasketDto;
import uk.co.whitbread.basket.infrastructure.rest.controller.payments.model.in.PaymentRequestDto;
import uk.co.whitbread.basket.infrastructure.rest.controller.payments.model.in.PaymentsConfirmationDto;
import uk.co.whitbread.basket.infrastructure.rest.controller.payments.model.in.ProcessAmendRequestDto;
import uk.co.whitbread.basket.infrastructure.rest.controller.payments.model.in.RefundRequestDto;
import uk.co.whitbread.basket.infrastructure.rest.controller.payments.model.out.InitiatePaymentResponseDto;
import uk.co.whitbread.basket.infrastructure.rest.controller.payments.model.out.PaymentConfirmationDto;
import uk.co.whitbread.basket.infrastructure.rest.controller.payments.model.out.RefundResponseDto;
import uk.co.whitbread.commons.exceptions.model.ErrorResponse;

public interface PaymentApiDocumentation {

  String BASKET_PATH = "v1/baskets";

  @Operation(summary = "Initiate payment process.")
  @ApiResponse(responseCode = "201", description = "Created", content = {
      @Content(mediaType = "application/json", schema = @Schema(implementation = InitiatePaymentResponseDto.class))})
  @ApiResponse(responseCode = "400", description = "Error Occurred ", content = {
      @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))})
  @ApiResponse(responseCode = "500", description = "Internal Server Error", content = {
      @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))})
  ResponseEntity<InitiatePaymentResponseDto> initiatePaymentProcess(@NotNull final String basketReference,
                                                                    @Valid PaymentRequestDto paymentRequestDto);

  @Operation(summary = "Initiate paypal payment process.")
  @ApiResponse(responseCode = "201", description = "Created", content = {
      @Content(mediaType = "application/json", schema = @Schema(implementation = InitiatePaymentResponseDto.class))})
  @ApiResponse(responseCode = "400", description = "Error Occurred ", content = {
      @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))})
  @ApiResponse(responseCode = "500", description = "Internal Server Error", content = {
      @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))})
  ResponseEntity<InitiatePaymentResponseDto> initiatePaypalPaymentProcess(@NotNull final String basketReference,
                                                                    @Valid PaymentRequestDto paymentRequestDto);

  @Operation(summary = "Initiate refund payment process.")
  @ApiResponse(responseCode = "202", description = "Accepted", content = {
      @Content(mediaType = "application/json", schema = @Schema(implementation = RefundResponseDto.class))})
  @ApiResponse(responseCode = "400", description = "Error Occurred ", content = {
      @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))})
  @ApiResponse(responseCode = "404", description = "Resource Not Found", content = {
      @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))})
  @ApiResponse(responseCode = "500", description = "Internal Server Error", content = {
      @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))})
  ResponseEntity<RefundResponseDto> refund(@NotNull final String basketReference, RefundRequestDto refundRequestDto);

  @Operation(summary = "Confirm the processing of a payment.")
  @ApiResponse(responseCode = "202", description = "Accepted", content = {
      @Content(mediaType = "application/json", schema = @Schema(implementation = BasketDto.class))})
  @ApiResponse(responseCode = "400", description = "Bad Request", content = {
      @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))})
  @ApiResponse(responseCode = "401", description = "Unauthorized", content = {
      @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))})
  @ApiResponse(responseCode = "404", description = "Resource Not Found", content = {
      @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))})
  @ApiResponse(responseCode = "500", description = "Internal Server Error", content = {
      @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))})
  ResponseEntity<PaymentConfirmationDto> paymentWebhook(@NotNull final String basketReference,
      PaymentsConfirmationDto paymentsConfirmationDto);


  @Operation(summary = "Initiate the process of an amend.")
  @ApiResponse(responseCode = "202", description = "Accepted", content = {
      @Content(mediaType = "application/json", schema = @Schema(implementation = BasketDto.class))})
  @ApiResponse(responseCode = "400", description = "Bad Request", content = {
      @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))})
  @ApiResponse(responseCode = "401", description = "Unauthorized", content = {
      @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))})
  @ApiResponse(responseCode = "404", description = "Resource Not Found", content = {
      @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))})
  @ApiResponse(responseCode = "500", description = "Internal Server Error", content = {
      @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))})
  void processAmend(@NotNull final String basketReference,
                    ProcessAmendRequestDto processAmendRequestDto);
}
