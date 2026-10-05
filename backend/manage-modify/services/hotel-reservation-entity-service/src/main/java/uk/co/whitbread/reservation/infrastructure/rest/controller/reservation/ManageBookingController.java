package uk.co.whitbread.reservation.infrastructure.rest.controller.reservation;

import static uk.co.whitbread.reservation.domain.utils.SanitizingUtils.sanitize;

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
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import uk.co.whitbread.commons.exceptions.model.ErrorResponse;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.OhipBadRequestException;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.OhipInternalException;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.OhipNotFoundException;
import uk.co.whitbread.reservation.domain.model.in.BookingChannel;
import uk.co.whitbread.reservation.domain.model.out.FindBookingResponse;
import uk.co.whitbread.reservation.domain.ports.primary.ManageBookingInPort;
import uk.co.whitbread.reservation.infrastructure.rest.controller.reservation.mapper.BookingChannelRequestMapper;
import uk.co.whitbread.reservation.infrastructure.rest.controller.reservation.mapper.FindBookingRequestMapper;
import uk.co.whitbread.reservation.infrastructure.rest.controller.reservation.mapper.FindBookingResponseMapper;
import uk.co.whitbread.reservation.infrastructure.rest.controller.reservation.mapper.ManageBookingResponseMapper;
import uk.co.whitbread.reservation.infrastructure.rest.controller.reservation.mapper.SearchBookingsRequestMapper;
import uk.co.whitbread.reservation.infrastructure.rest.controller.reservation.mapper.SearchBookingsResponseMapper;
import uk.co.whitbread.reservation.infrastructure.rest.controller.reservation.mapper.UdfsDomainMapper;
import uk.co.whitbread.reservation.infrastructure.rest.controller.reservation.model.in.BookingChannelDto;
import uk.co.whitbread.reservation.infrastructure.rest.controller.reservation.model.in.FindBookingKioskRequestDto;
import uk.co.whitbread.reservation.infrastructure.rest.controller.reservation.model.in.FindBookingRequestDto;
import uk.co.whitbread.reservation.infrastructure.rest.controller.reservation.model.in.GetCancelInformationRequestDto;
import uk.co.whitbread.reservation.infrastructure.rest.controller.reservation.model.in.SearchBookingsRequestDto;
import uk.co.whitbread.reservation.infrastructure.rest.controller.reservation.model.in.UdfsRequestDto;
import uk.co.whitbread.reservation.infrastructure.rest.controller.reservation.model.out.FindBookingResponseDto;
import uk.co.whitbread.reservation.infrastructure.rest.controller.reservation.model.out.ManageBookingResponseDto;
import uk.co.whitbread.reservation.infrastructure.rest.controller.reservation.model.out.SearchBookingsResponseDto;

@RestController
@RequiredArgsConstructor
@RequestMapping("/v1")
@Slf4j
public class ManageBookingController {
  private final ManageBookingInPort manageBookingInPort;
  private final ManageBookingResponseMapper manageBookingResponseMapper;
  private final BookingChannelRequestMapper bookingChannelRequestMapper;
  private final FindBookingResponseMapper findBookingResponseMapper;
  private final FindBookingRequestMapper findBookingRequestMapper;
  private final SearchBookingsRequestMapper searchBookingsRequestMapper;
  private final SearchBookingsResponseMapper searchBookingsResponseMapper;
  private final UdfsDomainMapper udfsDomainMapper;

  @Operation(summary = "Get cancel information by reservation id.")
  @ApiResponse(responseCode = "200", description = "Success", content = {
      @Content(mediaType = "application/json",
          schema = @Schema(implementation = ManageBookingResponseDto.class))})
  @ApiResponse(responseCode = "400", description = "Error Occurred ", content = {
      @Content(mediaType = "application/json",
          schema = @Schema(implementation = ErrorResponse.class))})
  @ApiResponse(responseCode = "404", description = "Resource Not Found", content = {
      @Content(mediaType = "application/json",
          schema = @Schema(implementation = ErrorResponse.class))})
  @ApiResponse(responseCode = "500", description = "Internal Server Error", content = {
      @Content(mediaType = "application/json",
          schema = @Schema(implementation = ErrorResponse.class))})
  @GetMapping(value = "/reservations/cancel", produces = MediaType.APPLICATION_JSON_VALUE)
  @PreAuthorize("@reservationPermissionEvaluator.hasAccess(#bookingChannel.channel)")
  public ResponseEntity<ManageBookingResponseDto> getManageBookingInformation(
          @Valid @ParameterObject GetCancelInformationRequestDto request,
          @Valid @ParameterObject BookingChannelDto bookingChannel) {

    final var manageBookingResponse = manageBookingInPort.getManageBookingInformation(
        request.getHotelId(),
        request.getBasketReference(), request.getUserDateTime(), request.getToken(),
        bookingChannelRequestMapper.toModel(bookingChannel),
        true, null);
    var manageBookingResponseDto = manageBookingResponseMapper.toDto(manageBookingResponse);

    return ResponseEntity.status(HttpStatus.OK).body(manageBookingResponseDto);
  }

  @Operation(summary = "Find reservation by booking reference, surname and arrivalDate")
  @ApiResponse(responseCode = "200", description = "Success", content = {
      @Content(mediaType = "application/json",
          schema = @Schema(implementation = FindBookingResponseDto.class))})
  @ApiResponse(responseCode = "400", description = "Error Occurred ", content = {
      @Content(mediaType = "application/json",
          schema = @Schema(implementation = ErrorResponse.class))})
  @ApiResponse(responseCode = "404", description = "Resource Not Found", content = {
      @Content(mediaType = "application/json",
          schema = @Schema(implementation = ErrorResponse.class))})
  @ApiResponse(responseCode = "500", description = "Internal Server Error", content = {
      @Content(mediaType = "application/json",
          schema = @Schema(implementation = ErrorResponse.class))})
  @GetMapping(value = "/reservations/find", produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<FindBookingResponseDto> findBooking(
          @Valid @ParameterObject FindBookingRequestDto findBookingRequestDto,
          @ParameterObject BookingChannelDto bookingChannelDto) {

    log.debug("Request to find booking: {}", sanitize(findBookingRequestDto.getResNo()));
    final FindBookingResponse reservation =
            manageBookingInPort.findBooking(
                    findBookingRequestMapper.toModel(findBookingRequestDto),
                    bookingChannelRequestMapper.toModel(bookingChannelDto));
    final var reservationsDto = findBookingResponseMapper.toDto(reservation);
    return ResponseEntity.ok(reservationsDto);
  }

  @Operation(summary = "Find reservation by booking reference for Kiosk")
  @ApiResponse(responseCode = "200", description = "Success", content = {
      @Content(mediaType = "application/json",
          schema = @Schema(implementation = FindBookingResponseDto.class))})
  @ApiResponse(responseCode = "400", description = "Error Occurred ", content = {
      @Content(mediaType = "application/json",
          schema = @Schema(implementation = ErrorResponse.class))})
  @ApiResponse(responseCode = "404", description = "Resource Not Found", content = {
      @Content(mediaType = "application/json",
          schema = @Schema(implementation = ErrorResponse.class))})
  @ApiResponse(responseCode = "500", description = "Internal Server Error", content = {
      @Content(mediaType = "application/json",
          schema = @Schema(implementation = ErrorResponse.class))})
  @GetMapping(value = "/reservations/find/kiosk", produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<FindBookingResponseDto> findBookingForKiosk(
      @Valid @ParameterObject FindBookingKioskRequestDto findBookingKioskRequestDto) {

    log.debug("Request to find booking for Kiosk: {}", sanitize(findBookingKioskRequestDto.getResNo()));
    final FindBookingResponse reservation =
        manageBookingInPort.findBooking(
            findBookingRequestMapper.toModel(findBookingKioskRequestDto),
            BookingChannel.builder()
                .channel(BookingChannel.KIOSK_BOOKING_CHANNEL)
                .subchannel(BookingChannel.WEB_SUBCHANNEL).build());
    final var reservationsDto = findBookingResponseMapper.toDto(reservation);
    return ResponseEntity.ok(reservationsDto);
  }

  @Operation(summary = "Search bookings")
  @ApiResponse(responseCode = "200", description = "Success", content = {
      @Content(mediaType = "application/json",
          schema = @Schema(implementation = SearchBookingsResponseMapper.class))})
  @ApiResponse(responseCode = "400", description = "Error Occurred ", content = {
      @Content(mediaType = "application/json",
          schema = @Schema(implementation = ErrorResponse.class))})
  @ApiResponse(responseCode = "404", description = "Resource Not Found", content = {
      @Content(mediaType = "application/json",
          schema = @Schema(implementation = ErrorResponse.class))})
  @ApiResponse(responseCode = "500", description = "Internal Server Error", content = {
      @Content(mediaType = "application/json",
          schema = @Schema(implementation = ErrorResponse.class))})
  @GetMapping(value = "/reservations/search", produces = MediaType.APPLICATION_JSON_VALUE)
  @PreAuthorize("isAuthenticated()")
  public ResponseEntity<SearchBookingsResponseDto> searchBookings(
          @ParameterObject @Valid SearchBookingsRequestDto searchBookingsRequestDto) {

    log.debug(
        "Request to search bookings with parameters: bookingReference={}, bookerLastName={}, "
            + "guestLastName={}, bookerPostcode={}, hotelId={}, bookerEmail={}, bookerPhone={}, "
            + "arrivalDate={}, cancellationDate={}, companyName={}, thirdPartyBookingReferenceNumber={}, "
            + "language={}, country={}, offset={}, limit={}",
        sanitize(searchBookingsRequestDto.getBookingReference()),
        sanitize(searchBookingsRequestDto.getBookerLastName()),
        sanitize(searchBookingsRequestDto.getGuestLastName()),
        sanitize(searchBookingsRequestDto.getBookerPostcode()),
        sanitize(searchBookingsRequestDto.getHotelId()),
        sanitize(searchBookingsRequestDto.getBookerEmail()),
        sanitize(searchBookingsRequestDto.getBookerPhone()),
        sanitize(searchBookingsRequestDto.getArrivalDate()),
        sanitize(searchBookingsRequestDto.getCancellationDate()),
        sanitize(searchBookingsRequestDto.getCompanyName()),
        sanitize(searchBookingsRequestDto.getThirdPartyBookingReferenceNumber()),
        sanitize(searchBookingsRequestDto.getLanguage()),
        sanitize(searchBookingsRequestDto.getCountry()),
        searchBookingsRequestDto.getOffset(),
        searchBookingsRequestDto.getLimit()
    );

    final var searchBookingsRequest = searchBookingsRequestMapper.toModel(searchBookingsRequestDto);

    final var searchBookingsResponse =
            manageBookingInPort.searchBookings(searchBookingsRequest);

    final var searchBookingsResponseDto =
            searchBookingsResponseMapper.toDto(searchBookingsResponse);

    return ResponseEntity.ok(searchBookingsResponseDto);
  }

  @Operation(summary = "Update the UDFC20 field value in Opera.")
  @ApiResponse(responseCode = "204", description = "Success")
  @ApiResponse(responseCode = "400", description = "Bad Request - Invalid Client Request",
      content = {@Content(mediaType = "application/json",
          schema = @Schema(implementation = OhipBadRequestException.class))})
  @ApiResponse(responseCode = "404", description = "Resource Not Found", content = {
      @Content(mediaType = "application/json",
          schema = @Schema(implementation = OhipNotFoundException.class))})
  @ApiResponse(responseCode = "500", description = "Internal Server Error", content = {
      @Content(mediaType = "application/json",
          schema = @Schema(implementation = OhipInternalException.class))})
  @PutMapping(value = "/reservations/updateUdfc20", produces = MediaType.APPLICATION_JSON_VALUE)
  public void updateUdfc20(@Valid @RequestBody UdfsRequestDto udfsRequestDto) {
    log.info("Request to update UDFC20: ReservationIds = {}, CiolStatus = {}",
            sanitize(udfsRequestDto.getReservationIds()), udfsRequestDto.getCiolStatus());
    manageBookingInPort.updateUdfc20(udfsDomainMapper.toDomainModel(udfsRequestDto));
  }

}
