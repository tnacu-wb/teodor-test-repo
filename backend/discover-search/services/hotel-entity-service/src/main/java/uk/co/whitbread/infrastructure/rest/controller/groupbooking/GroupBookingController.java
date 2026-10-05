package uk.co.whitbread.infrastructure.rest.controller.groupbooking;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import uk.co.whitbread.domain.ports.primary.GroupBookingInPort;
import uk.co.whitbread.infrastructure.rest.client.groupbooking.exception.GroupBookingException;
import uk.co.whitbread.infrastructure.rest.controller.groupbooking.mapper.GroupBookingRequestDtoMapper;
import uk.co.whitbread.infrastructure.rest.controller.groupbooking.mapper.GroupBookingResponseDtoMapper;
import uk.co.whitbread.infrastructure.rest.controller.groupbooking.model.in.GroupBookingRequestDto;
import uk.co.whitbread.infrastructure.rest.controller.groupbooking.model.out.GroupBookingResponseDto;

/**
 * Group Booking REST Controller. Used to forward the group booking request form data to D365, which will create a
 * ticket and issue ticket number in return
 */
@RestController
@Slf4j
@RequiredArgsConstructor
public class GroupBookingController {

  private final GroupBookingRequestDtoMapper groupBookingRequestDtoMapper;
  private final GroupBookingInPort groupBookingInboundPort;
  private final GroupBookingResponseDtoMapper groupBookingResponseMapper;

  @Operation(summary = "Sends the Group Booking form data to D365 and Retrieves a ticket number")
  @ApiResponse(responseCode = "200", description = "Success", content = {
      @Content(mediaType = "application/json",
          schema = @Schema(implementation = GroupBookingResponseDto.class))})
  @ApiResponse(responseCode = "400", description = "Error Occurred ", content = {
      @Content(mediaType = "application/json",
          schema = @Schema(implementation = GroupBookingException.class))})
  @ApiResponse(responseCode = "404", description = "Resource Not Found", content = {
      @Content(mediaType = "application/json",
          schema = @Schema(implementation = GroupBookingException.class))})
  @ApiResponse(responseCode = "500", description = "Internal Server Error", content = {
      @Content(mediaType = "application/json",
          schema = @Schema(implementation = GroupBookingException.class))})
  @PostMapping(
      value = "v1/hotels/{hotelCode}/group/bookingForm", produces = MediaType.APPLICATION_JSON_VALUE)
  public GroupBookingResponseDto createGroupBooking(
      @PathVariable("hotelCode") String hotelCode,
      @Valid @RequestBody GroupBookingRequestDto groupBookingRequestDto) {

    var request = groupBookingRequestDtoMapper.toModel(hotelCode, groupBookingRequestDto);
    var groupBookingResponse = groupBookingInboundPort.createGroupBooking(request);

    return groupBookingResponseMapper.toDto(groupBookingResponse);
  }

}
