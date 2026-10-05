package uk.co.whitbread.refund.processor.infrastructure.rest.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import uk.co.whitbread.commons.exceptions.model.ErrorResponse;
import uk.co.whitbread.refund.processor.infrastructure.rest.controller.model.in.TokenRefundRequestDto;
import uk.co.whitbread.refund.processor.infrastructure.rest.controller.model.out.RefundResponseDto;

public interface RefundRequestProcessorApiDocumentation {

  @Operation(summary = "Initiate partial refund payment process through REST.")
  @ApiResponse(responseCode = "202", description = "Accepted", content = {
      @Content(mediaType = "application/json", schema = @Schema(implementation = RefundResponseDto.class))})
  @ApiResponse(responseCode = "400", description = "Error Occurred ", content = {
      @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))})
  @ApiResponse(responseCode = "404", description = "Resource Not Found", content = {
      @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))})
  @ApiResponse(responseCode = "500", description = "Internal Server Error", content = {
      @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))})
  ResponseEntity<RefundResponseDto> refund(
            @RequestBody TokenRefundRequestDto tokenRefundRequestDto);

}
