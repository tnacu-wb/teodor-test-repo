package uk.co.whitbread.reservation.infrastructure.rest.controller.amend;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import uk.co.whitbread.commons.exceptions.model.ErrorResponse;
import uk.co.whitbread.reservation.domain.ports.primary.AmendLogicInPort;
import uk.co.whitbread.reservation.infrastructure.rest.controller.amend.mapper.AmendConfirmationPricesRequestMapper;
import uk.co.whitbread.reservation.infrastructure.rest.controller.amend.mapper.AmendConfirmationPricesResponseMapper;
import uk.co.whitbread.reservation.infrastructure.rest.controller.amend.mapper.AmendPaymentPageRequestMapper;
import uk.co.whitbread.reservation.infrastructure.rest.controller.amend.mapper.AmendPaymentPageResponseMapper;
import uk.co.whitbread.reservation.infrastructure.rest.controller.amend.mapper.ConfirmAmendLogicRequestMapper;
import uk.co.whitbread.reservation.infrastructure.rest.controller.amend.mapper.ConfirmAmendLogicResponseMapper;
import uk.co.whitbread.reservation.infrastructure.rest.controller.amend.model.in.AmendConfirmationPricesRequestDto;
import uk.co.whitbread.reservation.infrastructure.rest.controller.amend.model.in.AmendPaymentPageRequestDto;
import uk.co.whitbread.reservation.infrastructure.rest.controller.amend.model.in.ConfirmAmendLogicRequestDto;
import uk.co.whitbread.reservation.infrastructure.rest.controller.amend.model.out.AmendConfirmationPricesResponseDto;
import uk.co.whitbread.reservation.infrastructure.rest.controller.amend.model.out.AmendPaymentPageResponseDto;
import uk.co.whitbread.reservation.infrastructure.rest.controller.amend.model.out.ConfirmAmendLogicResponseDto;

@RestController
@RequiredArgsConstructor
@RequestMapping("/v1")
@Slf4j
public class AmendController {

  private final AmendLogicInPort amendLogicInPort;
  private final ConfirmAmendLogicRequestMapper confirmAmendLogicRequestMapper;
  private final ConfirmAmendLogicResponseMapper confirmAmendLogicResponseMapper;
  private final AmendConfirmationPricesRequestMapper amendConfirmationPricesRequestMapper;
  private final AmendConfirmationPricesResponseMapper amendConfirmationPricesResponseMapper;
  private final AmendPaymentPageRequestMapper amendPaymentPageRequestMapper;
  private final AmendPaymentPageResponseMapper amendPaymentPageResponseMapper;


  @Operation(summary = "Confirm amend logic")
  @ApiResponse(responseCode = "200", description = "Success", content = {
      @Content(mediaType = "application/json",
          schema = @Schema(implementation = ConfirmAmendLogicResponseDto.class))})
  @ApiResponse(responseCode = "400", description = "Error Occurred ", content = {
      @Content(mediaType = "application/json", schema =
      @Schema(implementation = ErrorResponse.class))})
  @ApiResponse(responseCode = "404", description = "Resource Not Found", content = {
      @Content(mediaType = "application/json", schema =
      @Schema(implementation = ErrorResponse.class))})
  @ApiResponse(responseCode = "500", description = "Internal Server Error", content = {
      @Content(mediaType = "application/json", schema =
      @Schema(implementation = ErrorResponse.class))})
  @PutMapping(value = "/reservations/amend/confirmAmendLogic", produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<ConfirmAmendLogicResponseDto> confirmAmendLogic(
      @RequestBody @Valid ConfirmAmendLogicRequestDto confirmAmendLogicRequestDto) {
    var confirmAmendLogicRequest = confirmAmendLogicRequestMapper.toModel(
        confirmAmendLogicRequestDto);
    var confirmAmendLogicResponse = amendLogicInPort.confirmAmendLogic(confirmAmendLogicRequest);
    final var confirmAmendLogicResponseDto = confirmAmendLogicResponseMapper.toDto(
        confirmAmendLogicResponse);
    return ResponseEntity.status(HttpStatus.OK).body(confirmAmendLogicResponseDto);
  }

  @Operation(summary = "Amend confirmation prices")
  @ApiResponse(responseCode = "200", description = "Success", content = {
      @Content(mediaType = "application/json",
          schema = @Schema(implementation = ConfirmAmendLogicResponseDto.class))})
  @ApiResponse(responseCode = "400", description = "Error Occurred ", content = {
      @Content(mediaType = "application/json", schema =
      @Schema(implementation = ErrorResponse.class))})
  @ApiResponse(responseCode = "404", description = "Resource Not Found", content = {
      @Content(mediaType = "application/json", schema =
      @Schema(implementation = ErrorResponse.class))})
  @ApiResponse(responseCode = "500", description = "Internal Server Error", content = {
      @Content(mediaType = "application/json", schema =
      @Schema(implementation = ErrorResponse.class))})
  @GetMapping(value = "/reservations/amend/amendConfirmationPrices", produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<AmendConfirmationPricesResponseDto> amendConfirmationPrices(
      @Valid @ParameterObject AmendConfirmationPricesRequestDto amendConfirmationPricesRequestDto) {

    var amendConfirmationPricesRequest =
        amendConfirmationPricesRequestMapper.toModel(amendConfirmationPricesRequestDto);
    var amendConfirmationPricesResponse = amendLogicInPort.getAmendConfirmationPrices(amendConfirmationPricesRequest);
    final var amendConfirmationPricesResponseDto =
        amendConfirmationPricesResponseMapper.toDto(amendConfirmationPricesResponse);
    return ResponseEntity.status(HttpStatus.OK).body(amendConfirmationPricesResponseDto);
  }

  @Operation(summary = "Amend payment page")
  @ApiResponse(responseCode = "200", description = "Success", content = {
      @Content(mediaType = "application/json",
          schema = @Schema(implementation = ConfirmAmendLogicResponseDto.class))})
  @ApiResponse(responseCode = "400", description = "Error Occurred ", content = {
      @Content(mediaType = "application/json", schema =
      @Schema(implementation = ErrorResponse.class))})
  @ApiResponse(responseCode = "404", description = "Resource Not Found", content = {
      @Content(mediaType = "application/json", schema =
      @Schema(implementation = ErrorResponse.class))})
  @ApiResponse(responseCode = "500", description = "Internal Server Error", content = {
      @Content(mediaType = "application/json", schema =
      @Schema(implementation = ErrorResponse.class))})
  @GetMapping(value = "/reservations/amend/paymentOptions", produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<AmendPaymentPageResponseDto> getAmendPaymentPage(
      @Valid @ParameterObject AmendPaymentPageRequestDto amendPaymentPageRequestDto) {

    var amendPaymentPageRequest =
        amendPaymentPageRequestMapper.toModel(amendPaymentPageRequestDto);
    var amendPaymentPageResponse =
        amendLogicInPort.amendPaymentPage(amendPaymentPageRequest);
    final var amendPaymentPageResponseDto =
        amendPaymentPageResponseMapper.toDto(amendPaymentPageResponse);
    return ResponseEntity.status(HttpStatus.OK).body(amendPaymentPageResponseDto);
  }
}
