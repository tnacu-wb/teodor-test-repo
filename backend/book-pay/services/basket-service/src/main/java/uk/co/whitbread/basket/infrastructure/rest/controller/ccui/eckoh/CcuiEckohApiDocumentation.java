package uk.co.whitbread.basket.infrastructure.rest.controller.ccui.eckoh;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import uk.co.whitbread.basket.infrastructure.rest.controller.ccui.eckoh.model.in.EckohPaymentRequestDto;
import uk.co.whitbread.basket.infrastructure.rest.controller.ccui.eckoh.model.out.EckohPaymentResponseDto;
import uk.co.whitbread.basket.infrastructure.rest.controller.ccui.eckoh.model.out.PaymentStatusResponseDto;
import uk.co.whitbread.commons.exceptions.model.ErrorResponse;

public interface CcuiEckohApiDocumentation {

  @Operation(summary = "Initiate a call to Eckoh payment.")
  @ApiResponse(responseCode = "200", description = "Success", content = {
      @Content(mediaType = "application/json", schema = @Schema(implementation = EckohPaymentResponseDto.class))})
  @ApiResponse(responseCode = "400", description = "Error Occurred ", content = {
      @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))})
  @ApiResponse(responseCode = "404", description = "Resource Not Found", content = {
      @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))})
  @ApiResponse(responseCode = "500", description = "Internal Server Error", content = {
      @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))})
  ResponseEntity<EckohPaymentResponseDto> initiateEckohPayment(
      @PathVariable("basket-reference") @NotNull String reference,
      @RequestBody @Valid EckohPaymentRequestDto eckohPaymentRequestDto);

  @Operation(summary = "Endpoint for polling Eckoh card details recording status.")
  @ApiResponse(responseCode = "200", description = "Success", content = {
      @Content(mediaType = "application/json", schema = @Schema(implementation = PaymentStatusResponseDto.class))})
  @ApiResponse(responseCode = "404", description = "Resource Not Found", content = {
      @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))})
  @ApiResponse(responseCode = "500", description = "Internal Server Error", content = {
      @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))})
  ResponseEntity<PaymentStatusResponseDto> getEckohRecordingStatus(
      @PathVariable("basket-reference") @NotNull String reference);
}
