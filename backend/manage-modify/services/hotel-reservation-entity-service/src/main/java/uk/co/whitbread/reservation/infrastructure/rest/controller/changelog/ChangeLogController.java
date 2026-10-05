package uk.co.whitbread.reservation.infrastructure.rest.controller.changelog;

import static uk.co.whitbread.reservation.domain.utils.SanitizingUtils.sanitize;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import uk.co.whitbread.commons.exceptions.model.ErrorResponse;
import uk.co.whitbread.reservation.domain.ports.primary.ChangeLogInPort;
import uk.co.whitbread.reservation.infrastructure.rest.controller.changelog.mapper.ChangeLogResponseMapper;
import uk.co.whitbread.reservation.infrastructure.rest.controller.changelog.model.out.ChangeLogResponseDto;

@RestController
@RequiredArgsConstructor
@RequestMapping("/v1")
@Slf4j
public class ChangeLogController {

  private final ChangeLogInPort changeLogInPort;
  private final ChangeLogResponseMapper changeLogResponseMapper;

  @Operation(summary = "Retrieve change log")
  @ApiResponse(responseCode = "200", description = "Success", content = {
      @Content(mediaType = "application/json",
          schema = @Schema(implementation = ChangeLogResponseDto.class))})
  @ApiResponse(responseCode = "400", description = "Error Occurred ", content = {
      @Content(mediaType = "application/json",
          schema = @Schema(implementation = ErrorResponse.class))})
  @ApiResponse(responseCode = "404", description = "Resource Not Found", content = {
      @Content(mediaType = "application/json",
          schema = @Schema(implementation = ErrorResponse.class))})
  @ApiResponse(responseCode = "500", description = "Internal Server Error", content = {
      @Content(mediaType = "application/json",
          schema = @Schema(implementation = ErrorResponse.class))})
  @GetMapping(value = "/reservations/changeLog", produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<ChangeLogResponseDto> getChangeLog(
      @RequestParam("hotelId") String hotelId,
      @RequestParam("reservationId") String reservationId,
      @RequestParam(value = "limit", required = false) Integer limit,
      @RequestParam(value = "offset", required = false) Integer offset) {

    log.debug("Entered get changelog for hotelId {} and reservationId {}",
        sanitize(hotelId),
        sanitize(reservationId));
    var changeLogResponse = changeLogInPort.getChangeLog(hotelId, reservationId, limit, offset);
    var changeLogResponseDto = changeLogResponseMapper.toDto(changeLogResponse);
    return ResponseEntity.ok(changeLogResponseDto);
  }
}
