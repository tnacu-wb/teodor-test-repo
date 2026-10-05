package uk.co.whitbread.ohip.infrastructure.rest.controller.deposit;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.validation.Valid;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import uk.co.whitbread.ohip.domain.model.reservation.out.DepositFoliosResponse;
import uk.co.whitbread.ohip.domain.ports.primary.DepositFoliosInPort;
import uk.co.whitbread.ohip.infrastructure.exceptions.OhipBadRequestException;
import uk.co.whitbread.ohip.infrastructure.exceptions.OhipInternalException;
import uk.co.whitbread.ohip.infrastructure.exceptions.OhipNotFoundException;
import uk.co.whitbread.ohip.infrastructure.rest.controller.deposit.mapper.DepositFoliosMapper;
import uk.co.whitbread.ohip.infrastructure.rest.controller.deposit.model.in.DepositFoliosRequestDto;
import uk.co.whitbread.ohip.infrastructure.rest.controller.deposit.model.out.DepositFoliosResponseDto;

@RestController
@RequiredArgsConstructor
@RequestMapping("/v1")
public class DepositFoliosController {

  private final DepositFoliosInPort depositFoliosInPort;

  private final DepositFoliosMapper depositFoliosMapper;

  @Operation(summary = "createDepositFolios")
  @ApiResponse(responseCode = "201", description = "Success")
  @ApiResponse(responseCode = "400", description = "Error Occurred ", content = {
      @Content(mediaType = "application/json",
          schema = @Schema(implementation = OhipBadRequestException.class))})
  @ApiResponse(responseCode = "404", description = "Resource Not Found", content = {
      @Content(mediaType = "application/json",
          schema = @Schema(implementation = OhipNotFoundException.class))})
  @ApiResponse(responseCode = "500", description = "Internal Server Error", content = {
      @Content(mediaType = "application/json",
          schema = @Schema(implementation = OhipInternalException.class))})
  @PostMapping(
      value = "/reservations/deposit-folios", produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<Void> saveDepositFolios(
      @RequestBody @Valid DepositFoliosRequestDto depositFoliosRequestDto) {

    final var depositFolios = depositFoliosMapper.toModel(depositFoliosRequestDto);
    depositFoliosInPort.createDepositFolios(depositFolios);

    return ResponseEntity.status(HttpStatus.CREATED).build();
  }

  @Operation(summary = "Get generated deposit folios by reservation IDs")
  @ApiResponse(responseCode = "200", description = "Success", content = {
      @Content(mediaType = "application/json",
          schema = @Schema(implementation = DepositFoliosResponseDto.class))})
  @ApiResponse(responseCode = "400", description = "Error Occurred ", content = {
      @Content(mediaType = "application/json",
          schema = @Schema(implementation = OhipBadRequestException.class))})
  @ApiResponse(responseCode = "404", description = "Resource Not Found", content = {
      @Content(mediaType = "application/json",
          schema = @Schema(implementation = OhipNotFoundException.class))})
  @ApiResponse(responseCode = "500", description = "Internal Server Error", content = {
      @Content(mediaType = "application/json",
          schema = @Schema(implementation = OhipInternalException.class))})
  @GetMapping(value = "/reservations/deposit-folios", produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<DepositFoliosResponseDto> getDepositFolioForReservations(
      @RequestParam("hotelId") String hotelId,
      @RequestParam("reservationIds") Set<String> reservationIds) {

    DepositFoliosResponse depositFolios = depositFoliosInPort
        .getDepositFolios(hotelId, reservationIds);
    DepositFoliosResponseDto depositFoliosResponseDto = depositFoliosMapper.toDto(depositFolios);
    return ResponseEntity.status(HttpStatus.OK).body(depositFoliosResponseDto);
  }

}
