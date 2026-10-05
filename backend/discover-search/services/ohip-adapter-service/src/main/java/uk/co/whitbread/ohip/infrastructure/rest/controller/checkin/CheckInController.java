package uk.co.whitbread.ohip.infrastructure.rest.controller.checkin;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import uk.co.whitbread.ohip.domain.ports.primary.CheckInInPort;
import uk.co.whitbread.ohip.infrastructure.exceptions.OhipBadRequestException;
import uk.co.whitbread.ohip.infrastructure.exceptions.OhipInternalException;
import uk.co.whitbread.ohip.infrastructure.exceptions.OhipNotFoundException;
import uk.co.whitbread.ohip.infrastructure.rest.controller.checkin.mapper.CheckInRequestMapper;
import uk.co.whitbread.ohip.infrastructure.rest.controller.checkin.mapper.CheckInResponseMapper;
import uk.co.whitbread.ohip.infrastructure.rest.controller.checkin.mapper.CommentDetailsRequestMapper;
import uk.co.whitbread.ohip.infrastructure.rest.controller.checkin.model.in.CheckInDetailsDto;
import uk.co.whitbread.ohip.infrastructure.rest.controller.checkin.model.in.CommentDetailsDto;
import uk.co.whitbread.ohip.infrastructure.rest.controller.checkin.model.out.CheckInResponseDto;

@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("v1/kiosk")
public class CheckInController {

  private final CheckInRequestMapper checkInRequestMapper;
  private final CheckInResponseMapper checkInResponseMapper;
  private final CheckInInPort checkInInPort;
  private final CommentDetailsRequestMapper commentDetailsRequestMapper;

  @Operation(summary = "Get CheckIn Details")
  @ApiResponse(responseCode = "200", description = "Success", content = {
      @Content(mediaType = "application/json",
          schema = @Schema(implementation = CheckInResponseDto.class))})
  @ApiResponse(responseCode = "400", description = "Bad Request - Invalid Client Request",
      content = {@Content(mediaType = "application/json",
          schema = @Schema(implementation = OhipBadRequestException.class))})
  @ApiResponse(responseCode = "404", description = "Resource Not Found", content = {
      @Content(mediaType = "application/json",
          schema = @Schema(implementation = OhipNotFoundException.class))})
  @ApiResponse(responseCode = "500", description = "Internal Server Error", content = {
      @Content(mediaType = "application/json",
          schema = @Schema(implementation = OhipInternalException.class))})
  @PostMapping(value = "/checkIn", produces = MediaType.APPLICATION_JSON_VALUE)
  public CheckInResponseDto getCheckIn(@RequestBody @Valid CheckInDetailsDto request) {
    final var checkInRequest = checkInRequestMapper.toCheckInInputRequestModel(request);
    final var checkInResponse = checkInInPort.getCheckInResponse(checkInRequest,
        request.getHotelId(),
        request.getReservationNumber());
    return checkInResponseMapper.toDto(checkInResponse);
  }

  @Operation(summary = "Update the Car Registration Comments")
  @ApiResponse(responseCode = "200", description = "Success", content = {
      @Content(mediaType = "application/json")})
  @ApiResponse(responseCode = "400", description = "Bad Request - Invalid Client Request",
      content = {@Content(mediaType = "application/json",
          schema = @Schema(implementation = OhipBadRequestException.class))})
  @ApiResponse(responseCode = "404", description = "Resource Not Found", content = {
      @Content(mediaType = "application/json",
          schema = @Schema(implementation = OhipNotFoundException.class))})
  @ApiResponse(responseCode = "500", description = "Internal Server Error", content = {
      @Content(mediaType = "application/json",
          schema = @Schema(implementation = OhipInternalException.class))})
  @PostMapping(value = "/updateComments", produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<Object> updateCarRegistrationComment(
      @RequestParam("reservationId") String reservationId,
      @RequestParam("hotelId") String hotelId,
      @RequestBody @Valid CommentDetailsDto commentDetailsDto) {

    final var commentDetails = commentDetailsRequestMapper.toModel(commentDetailsDto);
    checkInInPort.updateReservationComment(reservationId, hotelId, commentDetails);
    return new ResponseEntity<>(HttpStatus.CREATED);

  }

}
