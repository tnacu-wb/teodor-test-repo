package uk.co.whitbread.basket.infrastructure.rest.controller.basket;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.validation.Valid;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import uk.co.whitbread.basket.infrastructure.rest.controller.basket.model.in.DepositFoliosRequestDto;
import uk.co.whitbread.basket.infrastructure.rest.controller.basket.model.in.PrepaidDepositsDto;
import uk.co.whitbread.basket.infrastructure.rest.controller.basket.model.in.PrepaidDepositsRequestDto;
import uk.co.whitbread.commons.exceptions.model.ErrorResponse;

public interface BasketDepositFolioControllerApiDocumentation {

  @Operation(summary = "Takes the charges of a list of reservations and saves them in the DB")
  @ApiResponse(responseCode = "201", description = "Created")
  @ApiResponse(responseCode = "400", description = "Error Occurred ", content = {
      @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))})
  @ApiResponse(responseCode = "500", description = "Internal Server Error", content = {
      @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))})
  @PostMapping(value = "/deposit-folios", produces = MediaType.APPLICATION_JSON_VALUE)
  ResponseEntity<Void> saveCharges(
      @RequestBody @Valid PrepaidDepositsRequestDto prepaidDepositsRequestDto);

  @Operation(summary = "Takes the charges for a reservation by reservation id from DB")
  @ApiResponse(responseCode = "201", description = "Created")
  @ApiResponse(responseCode = "400", description = "Error Occurred ", content = {
      @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))})
  @ApiResponse(responseCode = "500", description = "Internal Server Error", content = {
      @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))})
  @GetMapping(value = "/deposit-folios", produces = MediaType.APPLICATION_JSON_VALUE)
  ResponseEntity<PrepaidDepositsDto> getCharges(
      @RequestBody @Valid String reservationId);

  @Operation(summary = "Takes the charges for multiple reservations by reservation id from DB")
  @ApiResponse(responseCode = "201", description = "Created")
  @ApiResponse(responseCode = "400", description = "Error Occurred ", content = {
      @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))})
  @ApiResponse(responseCode = "500", description = "Internal Server Error", content = {
      @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))})
  @GetMapping(value = "/deposit-folios", produces = MediaType.APPLICATION_JSON_VALUE)
  ResponseEntity<PrepaidDepositsDto> getChargesForReservations(
      @ParameterObject @Valid DepositFoliosRequestDto depositFoliosRequestDto);

}
