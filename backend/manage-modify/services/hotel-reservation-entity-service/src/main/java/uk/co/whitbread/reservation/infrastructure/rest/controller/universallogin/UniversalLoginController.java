package uk.co.whitbread.reservation.infrastructure.rest.controller.universallogin;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import uk.co.whitbread.commons.exceptions.model.ErrorResponse;
import uk.co.whitbread.reservation.domain.ports.primary.HotelReservationInPort;
import uk.co.whitbread.reservation.infrastructure.rest.controller.reservation.mapper.LinkReservationToLeisureCustomerRequestMapper;
import uk.co.whitbread.reservation.infrastructure.rest.controller.reservation.model.in.LinkReservationToLeisureCustomerRequestDto;
import uk.co.whitbread.reservation.infrastructure.security.ApiKeyProtected;

@RestController
@RequiredArgsConstructor
@RequestMapping("/v1/reservations/universal-login")
@Slf4j
public class UniversalLoginController {

  private final HotelReservationInPort reservationPortBusinessCase;
  private final LinkReservationToLeisureCustomerRequestMapper linkReservationToLeisureCustomerRequestMapper;

  @Operation(summary = "Link reservation to leisure customer account - Universal Login")
  @Parameter(in = ParameterIn.HEADER, name = "X-UL-API-KEY",
      description = "Universal Login API key for authentication", required = true,
      example = "your-api-key-value")
  @ApiResponse(responseCode = "204", description = "Success", content = @Content())
  @ApiResponse(responseCode = "400", description = "Bad request", content = {
      @Content(mediaType = "application/json",
          schema = @Schema(implementation = ErrorResponse.class))})
  @ApiResponse(responseCode = "404", description = "Resource Not Found", content = {
      @Content(mediaType = "application/json", schema =
      @Schema(implementation = ErrorResponse.class))})
  @ApiResponse(responseCode = "500", description = "Internal Server Error", content = {
      @Content(mediaType = "application/json",
          schema = @Schema(implementation = ErrorResponse.class))})
  @ApiKeyProtected
  @PutMapping(value = "/link-leisure-customer", produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<Void> linkReservationPostRegistration(
      @RequestBody @Valid LinkReservationToLeisureCustomerRequestDto linkReservationToLeisureCustomerRequestDto) {

    final var linkReservationToLeisureCustomerRequest =
        linkReservationToLeisureCustomerRequestMapper.toModel(
            linkReservationToLeisureCustomerRequestDto);
    reservationPortBusinessCase.linkReservationToLeisureCustomer(linkReservationToLeisureCustomerRequest);

    return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
  }
}
