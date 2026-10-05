package uk.co.whitbread.basket.infrastructure.rest.controller.ccui.payment;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.validation.constraints.NotNull;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import uk.co.whitbread.basket.infrastructure.rest.controller.ccui.payment.model.in.CcuiPaymentRequestDto;
import uk.co.whitbread.basket.infrastructure.rest.controller.ccui.payment.model.in.UpdateDiscountRequestDto;
import uk.co.whitbread.basket.infrastructure.rest.controller.ccui.payment.model.out.PaymentCcuiResponseDto;
import uk.co.whitbread.commons.exceptions.model.ErrorResponse;

public interface CcuiPaymentApiDocumentation {

  @Operation(summary = "Initiate payment.")
  @ApiResponse(responseCode = "200", description = "OK", content = {
      @Content(mediaType = "application/json", schema = @Schema(implementation = PaymentCcuiResponseDto.class))})
  @ApiResponse(responseCode = "400", description = "Error Occurred ", content = {
      @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))})
  @ApiResponse(responseCode = "404", description = "Resource Not Found", content = {
      @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))})
  @ApiResponse(responseCode = "500", description = "Internal Server Error", content = {
      @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))})
  ResponseEntity<PaymentCcuiResponseDto> initiatePaymentProcess(
      @PathVariable("basket-reference") @NotNull final String basketReference,
      @RequestBody final CcuiPaymentRequestDto paymentRequestDto);

  @Operation(summary = "Update discount.")
  @ApiResponse(responseCode = "200", description = "OK", content = {
      @Content(mediaType = "application/json")})
  @ApiResponse(responseCode = "400", description = "Error Occurred ", content = {
      @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))})
  @ApiResponse(responseCode = "404", description = "Resource Not Found", content = {
      @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))})
  @ApiResponse(responseCode = "500", description = "Internal Server Error", content = {
      @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))})
  ResponseEntity<Void> updateDiscount(
      @RequestBody final UpdateDiscountRequestDto updateDiscountRequestDto);
}
