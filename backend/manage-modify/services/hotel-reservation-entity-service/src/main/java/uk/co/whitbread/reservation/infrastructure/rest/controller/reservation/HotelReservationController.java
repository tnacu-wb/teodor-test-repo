package uk.co.whitbread.reservation.infrastructure.rest.controller.reservation;

import static uk.co.whitbread.reservation.domain.utils.SanitizingUtils.sanitize;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.validation.Valid;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import uk.co.whitbread.commons.exceptions.model.ErrorResponse;
import uk.co.whitbread.commons.exceptions.utils.ExceptionLogger;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.DepositFoliosResponseDto;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.OhipBadRequestException;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.OhipInternalException;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.OhipNotFoundException;
import uk.co.whitbread.reservation.ErrorCode;
import uk.co.whitbread.reservation.domain.exceptions.InvalidTokenException;
import uk.co.whitbread.reservation.domain.model.in.AmendSummaryRequest;
import uk.co.whitbread.reservation.domain.model.in.AttachReservationProfileRequest;
import uk.co.whitbread.reservation.domain.model.in.CancelReservationRequest;
import uk.co.whitbread.reservation.domain.model.in.PreCheckInRequest;
import uk.co.whitbread.reservation.domain.model.in.ReservationFileAttachmentRequest;
import uk.co.whitbread.reservation.domain.model.in.ReservationGuestRequest;
import uk.co.whitbread.reservation.domain.model.in.ReservationPreferencesRequest;
import uk.co.whitbread.reservation.domain.model.in.ReservationRequest;
import uk.co.whitbread.reservation.domain.model.in.UpdateCnpReservationRequest;
import uk.co.whitbread.reservation.domain.model.in.UpdateEmailReservationRequest;
import uk.co.whitbread.reservation.domain.model.out.CancelReservationResponse;
import uk.co.whitbread.reservation.domain.model.out.ConfirmReservationResponse;
import uk.co.whitbread.reservation.domain.model.out.DepositFoliosResponse;
import uk.co.whitbread.reservation.domain.model.out.PreCheckInResponse;
import uk.co.whitbread.reservation.domain.model.out.ReservationGuestResponse;
import uk.co.whitbread.reservation.domain.model.out.ReservationResponse;
import uk.co.whitbread.reservation.domain.model.out.ReservationsDetailsResponse;
import uk.co.whitbread.reservation.domain.model.out.UpdateCnpReservationResponse;
import uk.co.whitbread.reservation.domain.ports.primary.AmendLogicInPort;
import uk.co.whitbread.reservation.domain.ports.primary.CdhSearchBookingInPort;
import uk.co.whitbread.reservation.domain.ports.primary.HotelReservationInPort;
import uk.co.whitbread.reservation.infrastructure.rest.client.basket.mapper.DepositFoliosMapper;
import uk.co.whitbread.reservation.infrastructure.rest.controller.reservation.mapper.AddNewRoomRequestMapper;
import uk.co.whitbread.reservation.infrastructure.rest.controller.reservation.mapper.AmendDistributionPackagesMappper;
import uk.co.whitbread.reservation.infrastructure.rest.controller.reservation.mapper.AmendDistributionRequestMapper;
import uk.co.whitbread.reservation.infrastructure.rest.controller.reservation.mapper.AmendStayDatesRequestMapper;
import uk.co.whitbread.reservation.infrastructure.rest.controller.reservation.mapper.AmendStayDatesResponseMapper;
import uk.co.whitbread.reservation.infrastructure.rest.controller.reservation.mapper.AmendSummaryDetailsMapper;
import uk.co.whitbread.reservation.infrastructure.rest.controller.reservation.mapper.AttachReservationProfileRequestMapper;
import uk.co.whitbread.reservation.infrastructure.rest.controller.reservation.mapper.BookerDetailsMapper;
import uk.co.whitbread.reservation.infrastructure.rest.controller.reservation.mapper.BookingAllowancesResponseMapper;
import uk.co.whitbread.reservation.infrastructure.rest.controller.reservation.mapper.BookingChannelRequestMapper;
import uk.co.whitbread.reservation.infrastructure.rest.controller.reservation.mapper.BusinessItemsRequestMapper;
import uk.co.whitbread.reservation.infrastructure.rest.controller.reservation.mapper.CancelReservationRequestMapper;
import uk.co.whitbread.reservation.infrastructure.rest.controller.reservation.mapper.CancelReservationResponseMapper;
import uk.co.whitbread.reservation.infrastructure.rest.controller.reservation.mapper.CancellationPoliciesResponseMapper;
import uk.co.whitbread.reservation.infrastructure.rest.controller.reservation.mapper.CdhSearchBookingRequestMapper;
import uk.co.whitbread.reservation.infrastructure.rest.controller.reservation.mapper.CdhSearchBookingResponseMapper;
import uk.co.whitbread.reservation.infrastructure.rest.controller.reservation.mapper.CompanyQuestionAndAnswerRequestMapper;
import uk.co.whitbread.reservation.infrastructure.rest.controller.reservation.mapper.ConfirmAmendRequestMapper;
import uk.co.whitbread.reservation.infrastructure.rest.controller.reservation.mapper.ConfirmReservationRequestMapper;
import uk.co.whitbread.reservation.infrastructure.rest.controller.reservation.mapper.ConfirmReservationResponseMapper;
import uk.co.whitbread.reservation.infrastructure.rest.controller.reservation.mapper.CopyBookingRequestMapper;
import uk.co.whitbread.reservation.infrastructure.rest.controller.reservation.mapper.CopyBookingResponseMapper;
import uk.co.whitbread.reservation.infrastructure.rest.controller.reservation.mapper.CreateReservationResponseMapper;
import uk.co.whitbread.reservation.infrastructure.rest.controller.reservation.mapper.DepositsResponseMapper;
import uk.co.whitbread.reservation.infrastructure.rest.controller.reservation.mapper.EditRoomRequestMapper;
import uk.co.whitbread.reservation.infrastructure.rest.controller.reservation.mapper.LinkReservationToLeisureCustomerRequestMapper;
import uk.co.whitbread.reservation.infrastructure.rest.controller.reservation.mapper.MarketingPreferencesResponseMapper;
import uk.co.whitbread.reservation.infrastructure.rest.controller.reservation.mapper.MemosMapper;
import uk.co.whitbread.reservation.infrastructure.rest.controller.reservation.mapper.PreCheckInRequestMapper;
import uk.co.whitbread.reservation.infrastructure.rest.controller.reservation.mapper.ReservationByBasketRefResponseMapper;
import uk.co.whitbread.reservation.infrastructure.rest.controller.reservation.mapper.ReservationFileAttachmentRequestMapper;
import uk.co.whitbread.reservation.infrastructure.rest.controller.reservation.mapper.ReservationGuestRequestMapper;
import uk.co.whitbread.reservation.infrastructure.rest.controller.reservation.mapper.ReservationGuestResponseMapper;
import uk.co.whitbread.reservation.infrastructure.rest.controller.reservation.mapper.ReservationPreferencesRequestMapper;
import uk.co.whitbread.reservation.infrastructure.rest.controller.reservation.mapper.ReservationRequestMapper;
import uk.co.whitbread.reservation.infrastructure.rest.controller.reservation.mapper.ReservationResponseMapper;
import uk.co.whitbread.reservation.infrastructure.rest.controller.reservation.mapper.ReservationsPackagesResponseMapper;
import uk.co.whitbread.reservation.infrastructure.rest.controller.reservation.mapper.SearchBookingsResponseMapper;
import uk.co.whitbread.reservation.infrastructure.rest.controller.reservation.mapper.SpecialRequestsMapper;
import uk.co.whitbread.reservation.infrastructure.rest.controller.reservation.mapper.TempBookingRefResponseMapper;
import uk.co.whitbread.reservation.infrastructure.rest.controller.reservation.mapper.UpdateCnpReservationRequestMapper;
import uk.co.whitbread.reservation.infrastructure.rest.controller.reservation.mapper.UpdateCnpReservationResponseMapper;
import uk.co.whitbread.reservation.infrastructure.rest.controller.reservation.mapper.UpdateDiscountRequestMapper;
import uk.co.whitbread.reservation.infrastructure.rest.controller.reservation.mapper.UpdateEmailReservationRequestMapper;
import uk.co.whitbread.reservation.infrastructure.rest.controller.reservation.mapper.UpdateReasonForStayRequestMapper;
import uk.co.whitbread.reservation.infrastructure.rest.controller.reservation.mapper.UpdateReasonForStayResponseMapper;
import uk.co.whitbread.reservation.infrastructure.rest.controller.reservation.mapper.UpdateReservationOverrideReasonsRequestMapper;
import uk.co.whitbread.reservation.infrastructure.rest.controller.reservation.mapper.UpdateReservationOverrideReasonsResponseMapper;
import uk.co.whitbread.reservation.infrastructure.rest.controller.reservation.mapper.UpdateReservationRequestMapper;
import uk.co.whitbread.reservation.infrastructure.rest.controller.reservation.model.in.AddNewRoomRequestDto;
import uk.co.whitbread.reservation.infrastructure.rest.controller.reservation.model.in.AmendDistributionRequestDto;
import uk.co.whitbread.reservation.infrastructure.rest.controller.reservation.model.in.AmendStayDatesRequestDto;
import uk.co.whitbread.reservation.infrastructure.rest.controller.reservation.model.in.AttachReservationProfileRequestDto;
import uk.co.whitbread.reservation.infrastructure.rest.controller.reservation.model.in.BookingChannelDto;
import uk.co.whitbread.reservation.infrastructure.rest.controller.reservation.model.in.BusinessItemsRequestDto;
import uk.co.whitbread.reservation.infrastructure.rest.controller.reservation.model.in.CancelReservationRequestDto;
import uk.co.whitbread.reservation.infrastructure.rest.controller.reservation.model.in.CdhSearchBookingsRequestDto;
import uk.co.whitbread.reservation.infrastructure.rest.controller.reservation.model.in.CompanyQuestionAndAnswerDetailsRequestDto;
import uk.co.whitbread.reservation.infrastructure.rest.controller.reservation.model.in.ConfirmAmendRequestDto;
import uk.co.whitbread.reservation.infrastructure.rest.controller.reservation.model.in.ConfirmReservationRequestDto;
import uk.co.whitbread.reservation.infrastructure.rest.controller.reservation.model.in.CopyBookingRequestDto;
import uk.co.whitbread.reservation.infrastructure.rest.controller.reservation.model.in.CreateMemoRequestDto;
import uk.co.whitbread.reservation.infrastructure.rest.controller.reservation.model.in.DepositFoliosRequestDto;
import uk.co.whitbread.reservation.infrastructure.rest.controller.reservation.model.in.EditRoomRequestDto;
import uk.co.whitbread.reservation.infrastructure.rest.controller.reservation.model.in.LinkReservationToLeisureCustomerRequestDto;
import uk.co.whitbread.reservation.infrastructure.rest.controller.reservation.model.in.PreCheckInRequestDto;
import uk.co.whitbread.reservation.infrastructure.rest.controller.reservation.model.in.ReservationFileAttachmentRequestDto;
import uk.co.whitbread.reservation.infrastructure.rest.controller.reservation.model.in.ReservationGuestRequestDto;
import uk.co.whitbread.reservation.infrastructure.rest.controller.reservation.model.in.ReservationPackagesRequestByIdDto;
import uk.co.whitbread.reservation.infrastructure.rest.controller.reservation.model.in.ReservationPackagesRequestDto;
import uk.co.whitbread.reservation.infrastructure.rest.controller.reservation.model.in.ReservationPackagesScheduledRequestDto;
import uk.co.whitbread.reservation.infrastructure.rest.controller.reservation.model.in.ReservationPreferencesRequestDto;
import uk.co.whitbread.reservation.infrastructure.rest.controller.reservation.model.in.ReservationProfilesDto;
import uk.co.whitbread.reservation.infrastructure.rest.controller.reservation.model.in.ReservationRequestDto;
import uk.co.whitbread.reservation.infrastructure.rest.controller.reservation.model.in.SpecialRequestsDto;
import uk.co.whitbread.reservation.infrastructure.rest.controller.reservation.model.in.UpdateCnpReservationRequestDto;
import uk.co.whitbread.reservation.infrastructure.rest.controller.reservation.model.in.UpdateDiscountRequestDto;
import uk.co.whitbread.reservation.infrastructure.rest.controller.reservation.model.in.UpdateEmailReservationRequestDto;
import uk.co.whitbread.reservation.infrastructure.rest.controller.reservation.model.in.UpdateRateCodeRequestDto;
import uk.co.whitbread.reservation.infrastructure.rest.controller.reservation.model.in.UpdateReasonForStayRequestDto;
import uk.co.whitbread.reservation.infrastructure.rest.controller.reservation.model.in.UpdateReservationAlertsRequestDto;
import uk.co.whitbread.reservation.infrastructure.rest.controller.reservation.model.in.UpdateReservationOverrideReasonsRequestDto;
import uk.co.whitbread.reservation.infrastructure.rest.controller.reservation.model.in.UpdateReservationSingleCallRequestDto;
import uk.co.whitbread.reservation.infrastructure.rest.controller.reservation.model.in.UpdateRoomTypeRequestDto;
import uk.co.whitbread.reservation.infrastructure.rest.controller.reservation.model.out.AmendStayDatesResponseDto;
import uk.co.whitbread.reservation.infrastructure.rest.controller.reservation.model.out.AmendSummaryDetailsDto;
import uk.co.whitbread.reservation.infrastructure.rest.controller.reservation.model.out.BookingAllowancesResponseDto;
import uk.co.whitbread.reservation.infrastructure.rest.controller.reservation.model.out.CancelReservationResponseDto;
import uk.co.whitbread.reservation.infrastructure.rest.controller.reservation.model.out.CancellationPoliciesResponseDto;
import uk.co.whitbread.reservation.infrastructure.rest.controller.reservation.model.out.CdhSearchBookingsResponseDto;
import uk.co.whitbread.reservation.infrastructure.rest.controller.reservation.model.out.ConfirmReservationResponseDto;
import uk.co.whitbread.reservation.infrastructure.rest.controller.reservation.model.out.CopyBookingResponseDto;
import uk.co.whitbread.reservation.infrastructure.rest.controller.reservation.model.out.DepositsResponseDto;
import uk.co.whitbread.reservation.infrastructure.rest.controller.reservation.model.out.MarketingPreferencesResponseDto;
import uk.co.whitbread.reservation.infrastructure.rest.controller.reservation.model.out.MemosResponseDto;
import uk.co.whitbread.reservation.infrastructure.rest.controller.reservation.model.out.ReservationByBasketRefResponseDto;
import uk.co.whitbread.reservation.infrastructure.rest.controller.reservation.model.out.ReservationGuestResponseDto;
import uk.co.whitbread.reservation.infrastructure.rest.controller.reservation.model.out.ReservationResponseDto;
import uk.co.whitbread.reservation.infrastructure.rest.controller.reservation.model.out.ReservationsDetailsResponseDto;
import uk.co.whitbread.reservation.infrastructure.rest.controller.reservation.model.out.ReservationsPackagesResponseDto;
import uk.co.whitbread.reservation.infrastructure.rest.controller.reservation.model.out.SaveReservationResponseDto;
import uk.co.whitbread.reservation.infrastructure.rest.controller.reservation.model.out.TempBookingRefResponseDto;
import uk.co.whitbread.reservation.infrastructure.rest.controller.reservation.model.out.UpdateCnpReservationResponseDto;
import uk.co.whitbread.reservation.infrastructure.rest.controller.reservation.model.out.UpdateReasonForStayResponseDto;
import uk.co.whitbread.reservation.infrastructure.rest.controller.reservation.model.out.UpdateReservationOverrideReasonsResponseDto;

@RestController
@RequiredArgsConstructor
@RequestMapping("/v1")
@Slf4j
public class HotelReservationController {

  private static final String TOKEN = "WB-token";
  private static final String CONTROL_CHARACTER_REGEX = "[^A-Za-z0-9_-]";
  private final HotelReservationInPort reservationPortBusinessCase;
  private final ReservationResponseMapper reservationResponseMapper;
  private final CreateReservationResponseMapper createReservationResponseMapper;
  private final ReservationRequestMapper reservationRequestMapper;
  private final ConfirmReservationResponseMapper confirmReservationResponseMapper;
  private final ConfirmReservationRequestMapper confirmReservationRequestMapper;
  private final ReservationByBasketRefResponseMapper reservationByBasketRefResponseMapper;
  private final ReservationGuestRequestMapper reservationGuestRequestMapper;
  private final ReservationGuestResponseMapper reservationGuestResponseMapper;
  private final UpdateReservationRequestMapper updateReservationRequestMapper;
  private final ReservationsPackagesResponseMapper reservationsPackagesResponseMapper;
  private final CancelReservationRequestMapper cancelReservationRequestMapper;
  private final CancelReservationResponseMapper cancelReservationResponseMapper;
  private final UpdateDiscountRequestMapper updateDiscountRequestMapper;
  private final BookingChannelRequestMapper bookingChannelRequestMapper;
  private final UpdateReasonForStayRequestMapper updateReasonForStayRequestMapper;
  private final UpdateReasonForStayResponseMapper updateReasonForStayResponseMapper;
  private final BusinessItemsRequestMapper businessItemsRequestMapper;
  private final CompanyQuestionAndAnswerRequestMapper companyQuestionAndAnswerRequestMapper;
  private final UpdateCnpReservationRequestMapper updateCnpReservationRequestMapper;
  private final UpdateCnpReservationResponseMapper updateCnpReservationResponseMapper;
  private final UpdateEmailReservationRequestMapper updateEmailReservationRequestMapper;
  private final UpdateReservationOverrideReasonsRequestMapper updateReservationOverrideReasonsRequestMapper;
  private final UpdateReservationOverrideReasonsResponseMapper updateReservationOverrideReasonsResponseMapper;
  private final CancellationPoliciesResponseMapper cancellationPoliciesResponseMapper;
  private final DepositsResponseMapper depositsResponseMapper;
  private final MarketingPreferencesResponseMapper marketingPreferencesResponseMapper;
  private final AddNewRoomRequestMapper addNewRoomRequestMapper;
  private final TempBookingRefResponseMapper tempBookingRefResponseMapper;
  private final CopyBookingRequestMapper copyBookingRequestMapper;
  private final CopyBookingResponseMapper copyBookingResponseMapper;
  private final AmendStayDatesRequestMapper amendStayDatesRequestMapper;
  private final ConfirmAmendRequestMapper confirmAmendRequestMapper;
  private final AmendStayDatesResponseMapper amendStayDatesResponseMapper;
  private final EditRoomRequestMapper editRoomRequestMapper;
  private final BookingAllowancesResponseMapper bookingAllowancesResponseMapper;
  private final AmendDistributionPackagesMappper amendDistributionPackagesMappper;
  private final AmendLogicInPort amendLogicInPort;
  private final AmendSummaryDetailsMapper amendSummaryDetailsMapper;
  private final AmendDistributionRequestMapper amendDistributionRequestMapper;
  private final SpecialRequestsMapper specialRequestsMapper;
  private final BookerDetailsMapper bookerDetailsMapper;
  private final MemosMapper memosMapper;
  private final CdhSearchBookingRequestMapper cdhSearchBookingRequestMapper;
  private final CdhSearchBookingResponseMapper cdhSearchBookingResponseMapper;
  private final CdhSearchBookingInPort cdhSearchBookingInPort;
  private final AttachReservationProfileRequestMapper attachReservationProfileRequestMapper;
  private final ReservationFileAttachmentRequestMapper reservationFileAttachmentRequestMapper;
  private final PreCheckInRequestMapper preCheckInRequestMapper;
  private final LinkReservationToLeisureCustomerRequestMapper linkReservationToLeisureCustomerRequestMapper;
  private final ReservationPreferencesRequestMapper reservationPreferencesRequestMapper;
  private final DepositFoliosMapper depositFoliosMapper;

  @Operation(summary = "Create a reservation")
  @ApiResponse(responseCode = "200", description = "Success", content = {
      @Content(mediaType = "application/json", schema = @Schema())})
  @ApiResponse(responseCode = "400", description = "Error Occurred ", content = {
      @Content(mediaType = "application/json", schema =
      @Schema(implementation = ErrorResponse.class))})
  @ApiResponse(responseCode = "404", description = "Resource Not Found", content = {
      @Content(mediaType = "application/json", schema =
      @Schema(implementation = ErrorResponse.class))})
  @ApiResponse(responseCode = "500", description = "Internal Server Error", content = {
      @Content(mediaType = "application/json", schema =
      @Schema(implementation = ErrorResponse.class))})
  @PostMapping(value = "/reservations", produces = MediaType.APPLICATION_JSON_VALUE)
  @PreAuthorize("@reservationPermissionEvaluator.hasAccess(#createReservationRequestDto)")
  public ResponseEntity<ReservationResponseDto> createReservation(
      @RequestBody @Valid ReservationRequestDto createReservationRequestDto) {

    final ReservationRequest reservationRequest = reservationRequestMapper.toModel(
        createReservationRequestDto);
    final ReservationResponse reservationResponse = reservationPortBusinessCase.createReservation(
        reservationRequest);
    final var reservationResponseDto = createReservationResponseMapper.toDto(reservationResponse);
    return ResponseEntity.status(HttpStatus.CREATED).body(reservationResponseDto);
  }

  @Operation(summary = "Get reservations by basket reference")
  @ApiResponse(responseCode = "200", description = "Success", content = {
      @Content(mediaType = "application/json", schema =
      @Schema(implementation = ReservationsDetailsResponseDto.class))})
  @ApiResponse(responseCode = "400", description = "Error Occurred ", content = {
      @Content(mediaType = "application/json", schema =
      @Schema(implementation = ErrorResponse.class))})
  @ApiResponse(responseCode = "404", description = "Resource Not Found", content = {
      @Content(mediaType = "application/json", schema =
      @Schema(implementation = ErrorResponse.class))})
  @ApiResponse(responseCode = "500", description = "Internal Server Error", content = {
      @Content(mediaType = "application/json", schema =
      @Schema(implementation = ErrorResponse.class))})
  @GetMapping(value = "/reservations", produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<ReservationsDetailsResponseDto> getReservationsByBasketReference(
      @RequestParam("hotelId") String hotelId,
      @RequestParam("basketReference") String basketReference,
      @RequestParam(value = "limit", defaultValue = "20") int limit,
      @RequestParam(value = "offset", defaultValue = "0") int offset) {

    final ReservationsDetailsResponse reservations = reservationPortBusinessCase
        .getReservationsByBasketReference(hotelId, basketReference, limit, offset);
    final var reservationsDto = reservationResponseMapper.toDto(reservations);
    return ResponseEntity.ok(reservationsDto);
  }

  @Operation(summary = "confirmReservation")
  @ApiResponse(responseCode = "200", description = "Success", content = {
      @Content(mediaType = "application/json", schema =
      @Schema(implementation = ConfirmReservationResponseDto.class))})
  @ApiResponse(responseCode = "400", description = "Error Occurred ", content = {
      @Content(mediaType = "application/json", schema =
      @Schema(implementation = ErrorResponse.class))})
  @ApiResponse(responseCode = "404", description = "Resource Not Found", content = {
      @Content(mediaType = "application/json", schema =
      @Schema(implementation = ErrorResponse.class))})
  @ApiResponse(responseCode = "500", description = "Internal Server Error", content = {
      @Content(mediaType = "application/json", schema =
      @Schema(implementation = ErrorResponse.class))})
  @PostMapping(value = "/reservations/confirm", produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<ConfirmReservationResponseDto> confirmReservation(
      @RequestBody @Valid ConfirmReservationRequestDto confirmReservationRequestDto) {

    final var confirmReservationRequest = confirmReservationRequestMapper.toModel(
        confirmReservationRequestDto);
    final var reservationResponse = reservationPortBusinessCase.confirmReservation(
        confirmReservationRequest, Optional.empty());
    final var reservationResponseDto = confirmReservationResponseMapper.toDto(
        reservationResponse);
    return ResponseEntity.status(HttpStatus.OK).body(reservationResponseDto);
  }

  @Operation(summary = "Get all reservations just by basket reference")
  @ApiResponse(responseCode = "200", description = "Success", content = {
      @Content(mediaType = "application/json", schema =
      @Schema(implementation = ReservationByBasketRefResponseDto.class))})
  @ApiResponse(responseCode = "400", description = "Error Occurred ", content = {
      @Content(mediaType = "application/json", schema =
      @Schema(implementation = ErrorResponse.class))})
  @ApiResponse(responseCode = "404", description = "Resource Not Found", content = {
      @Content(mediaType = "application/json", schema =
      @Schema(implementation = ErrorResponse.class))})
  @ApiResponse(responseCode = "500", description = "Internal Server Error", content = {
      @Content(mediaType = "application/json", schema =
      @Schema(implementation = ErrorResponse.class))})
  @GetMapping(value = "/reservations/basket/{basketReference}",
      produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<ReservationByBasketRefResponseDto> getAllReservationsJustByBasketReference(
      @PathVariable("basketReference") String basketReference,
      @RequestParam(value = "priceBreakdownNeeded", defaultValue = "false") String priceBreakdownNeeded) {
    final var reservationsByBasketRefResponse = reservationPortBusinessCase
        .getAllReservationsJustByBasketReference(basketReference, Boolean.parseBoolean(priceBreakdownNeeded));
    final var reservationByBasketRefResponseDto = reservationByBasketRefResponseMapper.toDto(
        reservationsByBasketRefResponse);
    return ResponseEntity.ok(reservationByBasketRefResponseDto);
  }

  @Operation(summary = "Get all reservations just by basket reference authenticated")
  @ApiResponse(responseCode = "200", description = "Success", content = {
      @Content(mediaType = "application/json", schema =
      @Schema(implementation = ReservationByBasketRefResponseDto.class))})
  @ApiResponse(responseCode = "400", description = "Error Occurred ", content = {
      @Content(mediaType = "application/json", schema =
      @Schema(implementation = ErrorResponse.class))})
  @ApiResponse(responseCode = "404", description = "Resource Not Found", content = {
      @Content(mediaType = "application/json", schema =
      @Schema(implementation = ErrorResponse.class))})
  @ApiResponse(responseCode = "500", description = "Internal Server Error", content = {
      @Content(mediaType = "application/json", schema =
      @Schema(implementation = ErrorResponse.class))})
  @GetMapping(value = "/reservations/basket/{bookingReference}/authenticated",
      produces = MediaType.APPLICATION_JSON_VALUE)
  @PreAuthorize("isAuthenticated()")
  public ResponseEntity<ReservationByBasketRefResponseDto> getAllReservationsJustByBookingReferenceAuthenticated(
      @PathVariable("bookingReference") String bookingReference) {
    final var reservationsByBookingRefResponse = reservationPortBusinessCase
        .getAllReservationsJustByBookingReferenceAuthenticated(bookingReference, true);
    final var reservationByBookingRefResponseDto = reservationByBasketRefResponseMapper.toDto(
        reservationsByBookingRefResponse);
    return ResponseEntity.ok(reservationByBookingRefResponseDto);
  }

  @Operation(summary = "Get all reservations just by basket reference authenticated with token")
  @ApiResponse(responseCode = "200", description = "Success", content = {
      @Content(mediaType = "application/json", schema =
      @Schema(implementation = ReservationByBasketRefResponseDto.class))})
  @ApiResponse(responseCode = "400", description = "Error Occurred ", content = {
      @Content(mediaType = "application/json", schema =
      @Schema(implementation = ErrorResponse.class))})
  @ApiResponse(responseCode = "404", description = "Resource Not Found", content = {
      @Content(mediaType = "application/json", schema =
      @Schema(implementation = ErrorResponse.class))})
  @ApiResponse(responseCode = "500", description = "Internal Server Error", content = {
      @Content(mediaType = "application/json", schema =
      @Schema(implementation = ErrorResponse.class))})
  @GetMapping(value = "/reservations/basket/{bookingReference}/authenticatedWithToken",
      produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<ReservationByBasketRefResponseDto> getAllReservationsJustByBookingReferenceWithToken(
      @RequestHeader HttpHeaders headers,
      @PathVariable("bookingReference") String bookingReference) {
    validateWbToken(headers);
    final var reservationsByBookingRefResponse = reservationPortBusinessCase
        .getAllReservationsJustByBookingReferenceAuthenticated(bookingReference, false);
    final var reservationByBookingRefResponseDto = reservationByBasketRefResponseMapper.toDto(
        reservationsByBookingRefResponse);
    return ResponseEntity.ok(reservationByBookingRefResponseDto);
  }

  @Operation(summary = "Get reservations packages by reservations ids.")
  @ApiResponse(responseCode = "200", description = "Success", content = {
      @Content(mediaType = "application/json", schema =
      @Schema(implementation = ReservationsPackagesResponseDto.class))})
  @ApiResponse(responseCode = "400", description = "Error Occurred ", content = {
      @Content(mediaType = "application/json", schema =
      @Schema(implementation = ErrorResponse.class))})
  @ApiResponse(responseCode = "404", description = "Resource Not Found", content = {
      @Content(mediaType = "application/json", schema =
      @Schema(implementation = ErrorResponse.class))})
  @ApiResponse(responseCode = "500", description = "Internal Server Error", content = {
      @Content(mediaType = "application/json", schema =
      @Schema(implementation = ErrorResponse.class))})
  @GetMapping(value = "/reservations/ancillaries", produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<ReservationsPackagesResponseDto> getReservationsPackagesByIds(
      @RequestParam("hotelId") String hotelId,
      @RequestParam("basketReferenceId") String basketReferenceId,
      @RequestParam(value = "mealInclusiveRate", defaultValue = "false") boolean mealInclusiveRate) {

    final var reservationsPackagesByReservationsIds =
        reservationPortBusinessCase.getReservationsPackagesByBasketRef(hotelId, basketReferenceId, mealInclusiveRate);
    var reservationsPackages = reservationsPackagesResponseMapper.toDto(
        reservationsPackagesByReservationsIds);

    return ResponseEntity.status(HttpStatus.OK).body(reservationsPackages);
  }

  @Operation(summary = "updateReservationPackages")
  @ApiResponse(responseCode = "201", description = "Success", content = {
      @Content(mediaType = "application/json", schema =
      @Schema(implementation = SaveReservationResponseDto.class))})
  @ApiResponse(responseCode = "400", description = "Error Occurred ", content = {
      @Content(mediaType = "application/json", schema =
      @Schema(implementation = ErrorResponse.class))})
  @ApiResponse(responseCode = "404", description = "Resource Not Found", content = {
      @Content(mediaType = "application/json", schema =
      @Schema(implementation = ErrorResponse.class))})
  @ApiResponse(responseCode = "500", description = "Internal Server Error", content = {
      @Content(mediaType = "application/json", schema =
      @Schema(implementation = ErrorResponse.class))})
  @PutMapping(value = "/reservations/ancillaries", produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<SaveReservationResponseDto> saveReservation(
      @RequestBody @Valid ReservationPackagesRequestDto reservationPackagesRequestDto) {

    final var updateReservationRequest = updateReservationRequestMapper.toModel(
        reservationPackagesRequestDto);
    final var reservationResponse = reservationPortBusinessCase.updateReservationPackages(
        updateReservationRequest);
    final var reservationResponseDto = reservationResponseMapper.toDto(reservationResponse);

    return ResponseEntity.status(HttpStatus.OK).body(reservationResponseDto);
  }

  @Operation(summary = "updateReservationPackagesByReservationId")
  @ApiResponse(responseCode = "201", description = "Success", content = {
      @Content(mediaType = "application/json", schema =
      @Schema(implementation = SaveReservationResponseDto.class))})
  @ApiResponse(responseCode = "400", description = "Error Occurred ", content = {
      @Content(mediaType = "application/json", schema =
      @Schema(implementation = ErrorResponse.class))})
  @ApiResponse(responseCode = "404", description = "Resource Not Found", content = {
      @Content(mediaType = "application/json", schema =
      @Schema(implementation = ErrorResponse.class))})
  @ApiResponse(responseCode = "500", description = "Internal Server Error", content = {
      @Content(mediaType = "application/json", schema =
      @Schema(implementation = ErrorResponse.class))})
  @PutMapping(value = "/reservations/ancillaries/reservation-id", produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<SaveReservationResponseDto> saveReservationPackagesByReservationId(
      @RequestBody @Valid ReservationPackagesRequestByIdDto reservationPackagesRequestByIdDto) {

    final var updateReservationRequest = updateReservationRequestMapper.toModel(
        reservationPackagesRequestByIdDto);
    final var reservationResponse = reservationPortBusinessCase.updateReservationPackagesById(
        updateReservationRequest, false);
    final var reservationResponseDto = reservationResponseMapper.toDto(reservationResponse);

    return ResponseEntity.status(HttpStatus.OK).body(reservationResponseDto);
  }

  @Operation(summary = "Create a reservation guest")
  @ApiResponse(responseCode = "201", description = "Success", content = {
      @Content(mediaType = "application/json", schema = @Schema())})
  @ApiResponse(responseCode = "400", description = "Error Occurred ", content = {
      @Content(mediaType = "application/json", schema =
      @Schema(implementation = ErrorResponse.class))})
  @ApiResponse(responseCode = "404", description = "Resource Not Found", content = {
      @Content(mediaType = "application/json", schema =
      @Schema(implementation = ErrorResponse.class))})
  @ApiResponse(responseCode = "500", description = "Internal Server Error", content = {
      @Content(mediaType = "application/json", schema =
      @Schema(implementation = ErrorResponse.class))})
  @PostMapping(value = "/reservations/guests", produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<ReservationGuestResponseDto> createReservationGuest(
      @RequestBody @Valid ReservationGuestRequestDto reservationGuestRequestDto) {

    final ReservationGuestRequest guestReservationRequest = reservationGuestRequestMapper.toModel(
        reservationGuestRequestDto);
    final ReservationGuestResponse reservationGuestResponse = reservationPortBusinessCase
        .createReservationGuest(guestReservationRequest.getBasketReference(),
            guestReservationRequest);
    final var guestReservationResponseDto = reservationGuestResponseMapper.toDto(
        reservationGuestResponse);
    return ResponseEntity.status(HttpStatus.CREATED).body(guestReservationResponseDto);
  }

  @Operation(summary = "Update reservations details")
  @ApiResponse(responseCode = "201", description = "Success", content = {
      @Content(mediaType = "application/json", schema = @Schema())})
  @ApiResponse(responseCode = "400", description = "Error Occurred ", content = {
      @Content(mediaType = "application/json", schema =
      @Schema(implementation = ErrorResponse.class))})
  @ApiResponse(responseCode = "404", description = "Resource Not Found", content = {
      @Content(mediaType = "application/json", schema =
      @Schema(implementation = ErrorResponse.class))})
  @ApiResponse(responseCode = "500", description = "Internal Server Error", content = {
      @Content(mediaType = "application/json", schema =
      @Schema(implementation = ErrorResponse.class))})
  @PutMapping(value = "/reservations/update", produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<ConfirmReservationResponse> updateReservation(
      @RequestBody UpdateReservationSingleCallRequestDto updateReservationRequestDto) {

    final var updateReservationRequest = updateReservationRequestMapper.toModel(
        updateReservationRequestDto);
    final var reservationResponse = reservationPortBusinessCase.updateReservation(
        updateReservationRequest);
    return ResponseEntity.status(HttpStatus.OK).body(reservationResponse);
  }

  @Operation(summary = "Upgrade a reservation to a different rate plan")
  @ApiResponse(responseCode = "201", description = "Success", content = {
      @Content(mediaType = "application/json")})
  @ApiResponse(responseCode = "400", description = "Error Occurred ", content = {
      @Content(mediaType = "application/json", schema =
      @Schema(implementation = ErrorResponse.class))})
  @ApiResponse(responseCode = "404", description = "Resource Not Found", content = {
      @Content(mediaType = "application/json", schema =
      @Schema(implementation = ErrorResponse.class))})
  @ApiResponse(responseCode = "500", description = "Internal Server Error", content = {
      @Content(mediaType = "application/json", schema =
      @Schema(implementation = ErrorResponse.class))})
  @PutMapping(value = "/reservations/rate-code", produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<SaveReservationResponseDto> updateReservationRatePlanCode(
      @RequestBody @Valid UpdateRateCodeRequestDto updateRateCodeRequestDto) {

    final var updateReservationRequest = updateReservationRequestMapper.toModel(
        updateRateCodeRequestDto);
    final var reservationResponse = reservationPortBusinessCase.updateReservationRateCode(
        updateReservationRequest);
    final var reservationResponseDto = reservationResponseMapper.toDto(reservationResponse);

    return ResponseEntity.status(HttpStatus.OK).body(reservationResponseDto);
  }

  @Operation(summary = "Upgrade room to a different type.")
  @ApiResponse(responseCode = "201", description = "Success", content = {
      @Content(mediaType = "application/json")})
  @ApiResponse(responseCode = "400", description = "Error Occurred ", content = {
      @Content(mediaType = "application/json", schema =
      @Schema(implementation = ErrorResponse.class))})
  @ApiResponse(responseCode = "404", description = "Resource Not Found", content = {
      @Content(mediaType = "application/json", schema =
      @Schema(implementation = ErrorResponse.class))})
  @ApiResponse(responseCode = "500", description = "Internal Server Error", content = {
      @Content(mediaType = "application/json", schema =
      @Schema(implementation = ErrorResponse.class))})
  @PutMapping(value = "/reservations/roomTypeUpdate", produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<SaveReservationResponseDto> updateRoomType(
      @RequestBody @Valid UpdateRoomTypeRequestDto updateRoomTypeRequestDto) {

    final var updateRoomTypeRequest = updateReservationRequestMapper.toModel(
        updateRoomTypeRequestDto);
    final var roomTypeResponse = reservationPortBusinessCase.updateRoomType(
        updateRoomTypeRequest);
    final var roomTypeResponseDto = reservationResponseMapper.toDto(roomTypeResponse);
    return ResponseEntity.status(HttpStatus.OK).body(roomTypeResponseDto);
  }

  @Operation(summary = "Cancel a reservation")
  @ApiResponse(responseCode = "200", description = "Success", content = {
      @Content(mediaType = "application/json", schema = @Schema())})
  @ApiResponse(responseCode = "400", description = "Error Occurred ", content = {
      @Content(mediaType = "application/json", schema =
      @Schema(implementation = ErrorResponse.class))})
  @ApiResponse(responseCode = "404", description = "Resource Not Found", content = {
      @Content(mediaType = "application/json", schema =
      @Schema(implementation = ErrorResponse.class))})
  @ApiResponse(responseCode = "500", description = "Internal Server Error", content = {
      @Content(mediaType = "application/json", schema =
      @Schema(implementation = ErrorResponse.class))})
  @PostMapping(value = "/reservations/cancellations", produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<CancelReservationResponseDto> cancelReservation(
      @RequestBody @Valid CancelReservationRequestDto cancelReservationRequestDto) {

    final CancelReservationRequest cancelReservationRequest = cancelReservationRequestMapper
        .toModel(cancelReservationRequestDto);
    final CancelReservationResponse cancelReservationResponse = reservationPortBusinessCase
        .cancelReservation(cancelReservationRequest);
    final var cancelReservationResponseDto = cancelReservationResponseMapper
        .toDto(cancelReservationResponse);
    return ResponseEntity.status(HttpStatus.OK).body(cancelReservationResponseDto);
  }

  @Operation(summary = "Cancel an on hold reservation.")
  @ApiResponse(responseCode = "200", description = "Success", content = {
      @Content(mediaType = "application/json", schema = @Schema())})
  @ApiResponse(responseCode = "400", description = "Error Occurred ", content = {
      @Content(mediaType = "application/json", schema =
      @Schema(implementation = ErrorResponse.class))})
  @ApiResponse(responseCode = "404", description = "Resource Not Found", content = {
      @Content(mediaType = "application/json", schema =
      @Schema(implementation = ErrorResponse.class))})
  @ApiResponse(responseCode = "500", description = "Internal Server Error", content = {
      @Content(mediaType = "application/json", schema =
      @Schema(implementation = ErrorResponse.class))})
  @PutMapping(value = "/reservations/cancellations/on-hold", produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<CancelReservationResponseDto> cancelOnHoldReservation(
      @RequestBody @Valid CancelReservationRequestDto cancelOnHoldReservationRequestDto) {

    final CancelReservationRequest cancelOnHoldReservationRequest =
        cancelReservationRequestMapper.toModel(cancelOnHoldReservationRequestDto);
    final CancelReservationResponse cancelReservationResponse = reservationPortBusinessCase
        .cancelOnHoldReservation(cancelOnHoldReservationRequest);
    final var cancelReservationResponseDto = cancelReservationResponseMapper
        .toDto(cancelReservationResponse);
    return ResponseEntity.status(HttpStatus.OK).body(cancelReservationResponseDto);
  }

  @Operation(summary = "Rollback a reservation")
  @ApiResponse(responseCode = "200", description = "Success", content = {
      @Content(mediaType = "application/json", schema = @Schema())})
  @ApiResponse(responseCode = "400", description = "Error Occurred ", content = {
      @Content(mediaType = "application/json", schema =
      @Schema(implementation = ErrorResponse.class))})
  @ApiResponse(responseCode = "404", description = "Resource Not Found", content = {
      @Content(mediaType = "application/json", schema =
      @Schema(implementation = ErrorResponse.class))})
  @ApiResponse(responseCode = "500", description = "Internal Server Error", content = {
      @Content(mediaType = "application/json", schema =
      @Schema(implementation = ErrorResponse.class))})
  @PostMapping(value = "/reservations/cancellations/rollback", produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<CancelReservationResponseDto> rollbackReservation(
      @RequestBody @Valid CancelReservationRequestDto rollbackReservationRequestDto) {

    final CancelReservationRequest cancelReservationRequest = cancelReservationRequestMapper
        .toModel(rollbackReservationRequestDto);
    final CancelReservationResponse cancelReservationResponse = reservationPortBusinessCase
        .rollbackReservation(cancelReservationRequest);
    final var cancelReservationResponseDto = cancelReservationResponseMapper
        .toDto(cancelReservationResponse);
    return ResponseEntity.status(HttpStatus.OK).body(cancelReservationResponseDto);
  }

  @Operation(summary = "Update discount")
  @ApiResponse(responseCode = "200", description = "Success", content = {
      @Content(mediaType = "application/json")})
  @ApiResponse(responseCode = "400", description = "Error Occurred ", content = {
      @Content(mediaType = "application/json", schema =
      @Schema(implementation = ErrorResponse.class))})
  @ApiResponse(responseCode = "404", description = "Resource Not Found", content = {
      @Content(mediaType = "application/json", schema =
      @Schema(implementation = ErrorResponse.class))})
  @ApiResponse(responseCode = "500", description = "Internal Server Error", content = {
      @Content(mediaType = "application/json", schema =
      @Schema(implementation = ErrorResponse.class))})
  @PutMapping(value = "/reservations/discount", produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<Void> updateDiscount(
      @RequestBody @Valid UpdateDiscountRequestDto updateDiscountRequestDto) {

    reservationPortBusinessCase.updateDiscount(
        updateDiscountRequestMapper.toModel(updateDiscountRequestDto));

    return ResponseEntity.status(HttpStatus.OK).build();

  }

  @Operation(summary = "Update reservations with Reference & Custom questions and answers")
  @ApiResponse(responseCode = "200", description = "Success", content = {
      @Content(mediaType = "application/json")})
  @ApiResponse(responseCode = "400", description = "Error Occurred ", content = {
      @Content(mediaType = "application/json",
          schema = @Schema(implementation = ErrorResponse.class))})
  @ApiResponse(responseCode = "404", description = "Resource Not Found", content = {
      @Content(mediaType = "application/json",
          schema = @Schema(implementation = ErrorResponse.class))})
  @ApiResponse(responseCode = "500", description = "Internal Server Error", content = {
      @Content(mediaType = "application/json",
          schema = @Schema(implementation = ErrorResponse.class))})
  @PutMapping(value = "/reservations/questions-and-answers", produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<Void> updateQuestionsAndAnswers(
      @RequestBody @Valid CompanyQuestionAndAnswerDetailsRequestDto companyQuestionAndAnswerDetailsRequestDto) {

    var companyQuestionAndAnswerDetailsRequest = companyQuestionAndAnswerRequestMapper
        .toModel(companyQuestionAndAnswerDetailsRequestDto);

    reservationPortBusinessCase.updateCompanyQuestionAndAnswerDetails(
        companyQuestionAndAnswerDetailsRequest);

    return ResponseEntity.status(HttpStatus.OK).build();
  }

  @Operation(summary = "Update business items")
  @ApiResponse(responseCode = "200", description = "Success", content = {
      @Content(mediaType = "application/json")})
  @ApiResponse(responseCode = "400", description = "Error Occurred ", content = {
      @Content(mediaType = "application/json", schema =
      @Schema(implementation = ErrorResponse.class))})
  @ApiResponse(responseCode = "404", description = "Resource Not Found", content = {
      @Content(mediaType = "application/json", schema =
      @Schema(implementation = ErrorResponse.class))})
  @ApiResponse(responseCode = "500", description = "Internal Server Error", content = {
      @Content(mediaType = "application/json", schema =
      @Schema(implementation = ErrorResponse.class))})
  @PutMapping(value = "/reservations/business", produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<Void> updateBusinessItems(
      @RequestBody @Valid BusinessItemsRequestDto businessItemsRequestDto) {

    var businessItemsRequest = businessItemsRequestMapper.toModel(businessItemsRequestDto);

    reservationPortBusinessCase.updateBusinessItems(businessItemsRequest);

    return ResponseEntity.status(HttpStatus.OK).build();
  }

  @Operation(summary = "Update reservations special requests")
  @ApiResponse(responseCode = "200", description = "Success", content = {
      @Content(mediaType = "application/json")})
  @ApiResponse(responseCode = "400", description = "Error Occurred ", content = {
      @Content(mediaType = "application/json",
          schema = @Schema(implementation = ErrorResponse.class))})
  @ApiResponse(responseCode = "404", description = "Resource Not Found", content = {
      @Content(mediaType = "application/json",
          schema = @Schema(implementation = ErrorResponse.class))})
  @ApiResponse(responseCode = "500", description = "Internal Server Error", content = {
      @Content(mediaType = "application/json",
          schema = @Schema(implementation = ErrorResponse.class))})
  @PutMapping(
      value = "/reservations/special-requests", produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<Void> updateReservationsSpecialRequests(
      @RequestBody @Valid SpecialRequestsDto specialRequestsDto) {

    var sanitizedReservationIds = specialRequestsDto.getReservationIds().stream()
        .map(id -> id.replaceAll(CONTROL_CHARACTER_REGEX, ""))
        .toArray();
    log.info("Updating reservations with Ids {} with special requests", sanitizedReservationIds);

    var specialRequestsEntity = specialRequestsMapper.toModel(specialRequestsDto);

    reservationPortBusinessCase.updateReservationSpecialRequests(specialRequestsEntity);

    return ResponseEntity.status(HttpStatus.OK).build();
  }

  @Operation(summary = "Get booking allowances by basket reference")
  @ApiResponse(responseCode = "200", description = "Success", content = {
      @Content(mediaType = "application/json",
          schema = @Schema(implementation = BookingAllowancesResponseDto.class))})
  @ApiResponse(responseCode = "400", description = "Error Occurred ", content = {
      @Content(mediaType = "application/json",
          schema = @Schema(implementation = ErrorResponse.class))})
  @ApiResponse(responseCode = "404", description = "Resource Not Found", content = {
      @Content(mediaType = "application/json",
          schema = @Schema(implementation = ErrorResponse.class))})
  @ApiResponse(responseCode = "500", description = "Internal Server Error", content = {
      @Content(mediaType = "application/json",
          schema = @Schema(implementation = ErrorResponse.class))})
  @GetMapping(value = "/reservations/basket/{basketReference}/allowances",
      produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<BookingAllowancesResponseDto> getBookingAllowancesByBasketReference(
      @PathVariable("basketReference") String basketReference) {
    final var bookingAllowancesResponse =
        reservationPortBusinessCase.getBookingAllowances(basketReference);
    final var bookingAllowancesResponseDto = bookingAllowancesResponseMapper.toDto(
        bookingAllowancesResponse);
    return ResponseEntity.status(HttpStatus.OK).body(bookingAllowancesResponseDto);
  }

  @Operation(summary = "Update reason for stay")
  @ApiResponse(responseCode = "200", description = "Success", content = {
      @Content(mediaType = "application/json", schema = @Schema(implementation = UpdateReasonForStayRequestDto.class))})
  @ApiResponse(responseCode = "400", description = "Error Occurred ", content = {
      @Content(mediaType = "application/json",
          schema = @Schema(implementation = ErrorResponse.class))})
  @ApiResponse(responseCode = "404", description = "Resource Not Found", content = {
      @Content(mediaType = "application/json",
          schema = @Schema(implementation = ErrorResponse.class))})
  @ApiResponse(responseCode = "500", description = "Internal Server Error", content = {
      @Content(mediaType = "application/json",
          schema = @Schema(implementation = ErrorResponse.class))})
  @PutMapping(value = "/reservations/reasonForStay", produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<UpdateReasonForStayResponseDto> updateReasonForStay(
      @RequestBody @Valid UpdateReasonForStayRequestDto updateReasonForStayRequestDto) {

    final var updateReasonForStayRequest = updateReasonForStayRequestMapper.toModel(
        updateReasonForStayRequestDto);

    final var updateReasonForStayResponse = reservationPortBusinessCase.updateReasonForStay(
        updateReasonForStayRequest);

    final var updateReasonForStayResponseDto = updateReasonForStayResponseMapper.toDto(
        updateReasonForStayResponse);

    return ResponseEntity.ok(updateReasonForStayResponseDto);
  }

  @Operation(summary = "Update reservation override reasons")
  @ApiResponse(responseCode = "200", description = "Success", content = {
      @Content(mediaType = "application/json")})
  @ApiResponse(responseCode = "400", description = "Error Occurred ", content = {
      @Content(mediaType = "application/json", schema =
      @Schema(implementation = ErrorResponse.class))})
  @ApiResponse(responseCode = "404", description = "Resource Not Found", content = {
      @Content(mediaType = "application/json", schema =
      @Schema(implementation = ErrorResponse.class))})
  @ApiResponse(responseCode = "500", description = "Internal Server Error", content = {
      @Content(mediaType = "application/json", schema =
      @Schema(implementation = ErrorResponse.class))})
  @PutMapping(value = "/reservations/overrideReasons", produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<UpdateReservationOverrideReasonsResponseDto> updateReservationOverrideReasons(
      @RequestBody @Valid UpdateReservationOverrideReasonsRequestDto updateReservationOverrideReasonsRequestDto) {

    final var updateReservationOverrideReasonsResponse =
        reservationPortBusinessCase.updateReservationOverrideReasons(
            updateReservationOverrideReasonsRequestMapper.toModel(
                updateReservationOverrideReasonsRequestDto));

    final var updateReservationOverrideReasonsResponseDto =
        updateReservationOverrideReasonsResponseMapper.toDto(
            updateReservationOverrideReasonsResponse);

    return ResponseEntity.ok(updateReservationOverrideReasonsResponseDto);
  }

  @Operation(summary = "Get deposits information by reservation id.")
  @ApiResponse(responseCode = "200", description = "Success", content = {
      @Content(mediaType = "application/json",
          schema = @Schema(implementation = DepositsResponseDto.class))})
  @ApiResponse(responseCode = "400", description = "Error Occurred ", content = {
      @Content(mediaType = "application/json",
          schema = @Schema(implementation = ErrorResponse.class))})
  @ApiResponse(responseCode = "404", description = "Resource Not Found", content = {
      @Content(mediaType = "application/json",
          schema = @Schema(implementation = ErrorResponse.class))})
  @ApiResponse(responseCode = "500", description = "Internal Server Error", content = {
      @Content(mediaType = "application/json",
          schema = @Schema(implementation = ErrorResponse.class))})
  @GetMapping(value = "/reservations/deposits", produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<DepositsResponseDto> getDepositsForReservationId(
      @RequestParam("hotelId") String hotelId,
      @RequestParam("reservationId") String reservationId) {
    final var depositsResponse = reservationPortBusinessCase.getDepositsForReservationId(hotelId,
        reservationId);
    final var depositsResponseDto = depositsResponseMapper.toDto(depositsResponse);
    return ResponseEntity.status(HttpStatus.OK).body(depositsResponseDto);
  }

  @Operation(summary = "Get cancellation policies by external reference id or rate plane")
  @ApiResponse(responseCode = "200", description = "Success", content = {
      @Content(mediaType = "application/json",
          schema = @Schema(implementation = CancellationPoliciesResponseDto.class))})
  @ApiResponse(responseCode = "400", description = "Error Occurred ", content = {
      @Content(mediaType = "application/json",
          schema = @Schema(implementation = ErrorResponse.class))})
  @ApiResponse(responseCode = "404", description = "Resource Not Found", content = {
      @Content(mediaType = "application/json",
          schema = @Schema(implementation = ErrorResponse.class))})
  @ApiResponse(responseCode = "500", description = "Internal Server Error", content = {
      @Content(mediaType = "application/json",
          schema = @Schema(implementation = ErrorResponse.class))})
  @GetMapping(value = "/reservations/cancellationPolicies", produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<CancellationPoliciesResponseDto> getCancellationPolicies(
      @RequestParam(value = "basketReference") String basketReference,
      @RequestParam("hotelId") String hotelId,
      @RequestParam(value = "ratePlanCode") String ratePlan,
      @RequestParam(value = "arrivalDate") String arrivalDate) {
    final var cancellationPoliciesResponse =
        reservationPortBusinessCase.getCancellationPolicies(basketReference, hotelId, ratePlan,
            arrivalDate);

    final var cancellationPoliciesResponseDto =
        cancellationPoliciesResponseMapper.toDto(cancellationPoliciesResponse);

    return ResponseEntity.status(HttpStatus.OK).body(cancellationPoliciesResponseDto);
  }

  @Operation(summary = "Get marketing preferences information by reservation id.")
  @ApiResponse(responseCode = "200", description = "Success", content = {
      @Content(mediaType = "application/json",
          schema = @Schema(implementation = MarketingPreferencesResponseDto.class))})
  @ApiResponse(responseCode = "400", description = "Error Occurred ", content = {
      @Content(mediaType = "application/json",
          schema = @Schema(implementation = ErrorResponse.class))})
  @ApiResponse(responseCode = "404", description = "Resource Not Found", content = {
      @Content(mediaType = "application/json",
          schema = @Schema(implementation = ErrorResponse.class))})
  @ApiResponse(responseCode = "500", description = "Internal Server Error", content = {
      @Content(mediaType = "application/json",
          schema = @Schema(implementation = ErrorResponse.class))})
  @GetMapping(value = "/reservations/marketingPreferences", produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<MarketingPreferencesResponseDto> getMarketingPreferences(
      @RequestParam("hotelId") String hotelId,
      @RequestParam("reservationId") String reservationId) {
    final var marketingPreferencesResponse = reservationPortBusinessCase.getMarketingPreferences(
        hotelId,
        reservationId);
    final var marketingPreferencesResponseDto = marketingPreferencesResponseMapper.toDto(
        marketingPreferencesResponse);
    return ResponseEntity.status(HttpStatus.OK).body(marketingPreferencesResponseDto);
  }

  @Operation(summary = "Update CNP Reservations based on basket reference")
  @ApiResponse(responseCode = "200", description = "Success", content = {
      @Content(mediaType = "application/json",
          schema = @Schema(implementation = UpdateCnpReservationResponseDto.class))})
  @ApiResponse(responseCode = "400", description = "Error Occurred ", content = {
      @Content(mediaType = "application/json",
          schema = @Schema(implementation = ErrorResponse.class))})
  @ApiResponse(responseCode = "404", description = "Resource Not Found", content = {
      @Content(mediaType = "application/json",
          schema = @Schema(implementation = ErrorResponse.class))})
  @ApiResponse(responseCode = "500", description = "Internal Server Error", content = {
      @Content(mediaType = "application/json",
          schema = @Schema(implementation = ErrorResponse.class))})
  @PutMapping(value = "/reservations/amend/cnp/{basketRef}", produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<UpdateCnpReservationResponseDto> updateCnpReservations(
      @PathVariable String basketRef,
      @RequestBody @Valid UpdateCnpReservationRequestDto updateCnpReservationRequestDto) {
    UpdateCnpReservationRequest updateCnpReservationRequest =
        updateCnpReservationRequestMapper.toModel(updateCnpReservationRequestDto);
    UpdateCnpReservationResponse reservationUpdateResponse =
        reservationPortBusinessCase.updateCnpReservation(basketRef, updateCnpReservationRequest);
    UpdateCnpReservationResponseDto updateCnpReservationResponseDto =
        updateCnpReservationResponseMapper.toDto(reservationUpdateResponse);
    return ResponseEntity.status(HttpStatus.OK).body(updateCnpReservationResponseDto);
  }

  @Operation(summary = "Update booker email on reservation based on basket reference")
  @ApiResponse(responseCode = "200", description = "Success", content = {
      @Content(mediaType = "application/json",
          schema = @Schema(implementation = String.class))})
  @ApiResponse(responseCode = "400", description = "Error Occurred ", content = {
      @Content(mediaType = "application/json",
          schema = @Schema(implementation = ErrorResponse.class))})
  @ApiResponse(responseCode = "404", description = "Resource Not Found", content = {
      @Content(mediaType = "application/json",
          schema = @Schema(implementation = ErrorResponse.class))})
  @ApiResponse(responseCode = "500", description = "Internal Server Error", content = {
      @Content(mediaType = "application/json",
          schema = @Schema(implementation = ErrorResponse.class))})
  @PutMapping(value = "/reservations/email/{basketReference}", produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<String> updateEmailReservations(
      @PathVariable("basketReference") String basketReference,
      @RequestBody @Valid UpdateEmailReservationRequestDto updateEmailReservationRequestDto) {
    UpdateEmailReservationRequest updateEmailReservationRequest =
        updateEmailReservationRequestMapper.toModel(updateEmailReservationRequestDto);
    reservationPortBusinessCase.updateEmailReservation(basketReference,
        updateEmailReservationRequest);
    return ResponseEntity.status(HttpStatus.OK).body(basketReference);
  }

  @Operation(summary = "Create a reservation for a new room")
  @ApiResponse(responseCode = "200", description = "Success", content = {
      @Content(mediaType = "application/json", schema = @Schema())})
  @ApiResponse(responseCode = "400", description = "Error Occurred ", content = {
      @Content(mediaType = "application/json",
          schema = @Schema(implementation = ErrorResponse.class))})
  @ApiResponse(responseCode = "404", description = "Resource Not Found", content = {
      @Content(mediaType = "application/json",
          schema = @Schema(implementation = ErrorResponse.class))})
  @ApiResponse(responseCode = "500", description = "Internal Server Error", content = {
      @Content(mediaType = "application/json",
          schema = @Schema(implementation = ErrorResponse.class))})
  @PostMapping(value = "/reservations/amend/addNewRoom", produces = MediaType.APPLICATION_JSON_VALUE)
  public TempBookingRefResponseDto addNewRoomToExistingBasket(
      @RequestBody @Valid AddNewRoomRequestDto addNewRoomRequestDto) {

    final ReservationRequest reservationRequest = addNewRoomRequestMapper.toModel(
        addNewRoomRequestDto);
    var addNewRoomResponse = reservationPortBusinessCase.addNewRoomToExistingBasket(
        addNewRoomRequestDto.getTempBookingRef(), reservationRequest, null);
    return tempBookingRefResponseMapper.toDto(addNewRoomResponse);
  }

  @Operation(summary = "Retrieving amend summary details")
  @ApiResponse(responseCode = "200", description = "Success", content = {
      @Content(mediaType = "application/json", schema = @Schema(
          implementation = AmendSummaryDetailsDto.class
      ))})
  @ApiResponse(responseCode = "400", description = "Error Occurred ", content = {
      @Content(mediaType = "application/json", schema =
      @Schema(implementation = ErrorResponse.class))})
  @ApiResponse(responseCode = "404", description = "Resource Not Found", content = {
      @Content(mediaType = "application/json", schema =
      @Schema(implementation = ErrorResponse.class))})
  @ApiResponse(responseCode = "500", description = "Internal Server Error", content = {
      @Content(mediaType = "application/json", schema =
      @Schema(implementation = ErrorResponse.class))})
  @GetMapping(value = "/reservations/amend/summary", produces = MediaType.APPLICATION_JSON_VALUE)
  public AmendSummaryDetailsDto getAmendSummaryDetails(
      @Valid @ParameterObject AmendSummaryRequest amendSummaryRequest) {
    var amendSummaryDetails = amendLogicInPort.getAmendSummaryDetails(amendSummaryRequest);
    return amendSummaryDetailsMapper.toDto(amendSummaryDetails);
  }

  @Operation(summary = "copyBooking")
  @ApiResponse(responseCode = "200", description = "Success", content = {
      @Content(mediaType = "application/json", schema =
      @Schema(implementation = CopyBookingResponseDto.class))})
  @ApiResponse(responseCode = "400", description = "Error Occurred ", content = {
      @Content(mediaType = "application/json", schema =
      @Schema(implementation = ErrorResponse.class))})
  @ApiResponse(responseCode = "404", description = "Resource Not Found", content = {
      @Content(mediaType = "application/json", schema =
      @Schema(implementation = ErrorResponse.class))})
  @ApiResponse(responseCode = "500", description = "Internal Server Error", content = {
      @Content(mediaType = "application/json", schema =
      @Schema(implementation = ErrorResponse.class))})
  @PostMapping(value = "/reservations/copy", produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<CopyBookingResponseDto> copyBooking(
      @RequestBody @Valid CopyBookingRequestDto copyBookingRequestDto) {

    final var copyBookingRequest = copyBookingRequestMapper.toModel(
        copyBookingRequestDto);
    final var reservationResponse = reservationPortBusinessCase.copyBooking(
        copyBookingRequest);
    final var temporaryBasketReference = copyBookingResponseMapper.toDto(
        reservationResponse);
    return ResponseEntity.status(HttpStatus.OK).body(temporaryBasketReference);
  }

  @Operation(summary = "Amend stay dates for an existing basket")
  @ApiResponse(responseCode = "200", description = "Success", content = {
      @Content(mediaType = "application/json",
          schema = @Schema(implementation = AmendStayDatesResponseDto.class))})
  @ApiResponse(responseCode = "400", description = "Error Occurred ", content = {
      @Content(mediaType = "application/json",
          schema = @Schema(implementation = ErrorResponse.class))})
  @ApiResponse(responseCode = "404", description = "Resource Not Found", content = {
      @Content(mediaType = "application/json",
          schema = @Schema(implementation = ErrorResponse.class))})
  @ApiResponse(responseCode = "500", description = "Internal Server Error", content = {
      @Content(mediaType = "application/json",
          schema = @Schema(implementation = ErrorResponse.class))})
  @PostMapping(value = "/reservations/amendStayDates", produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<AmendStayDatesResponseDto> amendStayDates(
      @RequestBody @Valid AmendStayDatesRequestDto amendStayDatesRequestDto) {
    var amendStayDatesRequest = amendStayDatesRequestMapper.toModel(
        amendStayDatesRequestDto);
    var amendStayDatesResponse = reservationPortBusinessCase.amendStayDates(
        amendStayDatesRequest, null);
    return ResponseEntity.status(HttpStatus.OK)
        .body(amendStayDatesResponseMapper.toDto(amendStayDatesResponse));
  }

  @Operation(summary = "Update room information")
  @ApiResponse(responseCode = "200", description = "Success", content = {
      @Content(mediaType = "application/json")})
  @ApiResponse(responseCode = "400", description = "Error Occurred ", content = {
      @Content(mediaType = "application/json", schema =
      @Schema(implementation = ErrorResponse.class))})
  @ApiResponse(responseCode = "404", description = "Resource Not Found", content = {
      @Content(mediaType = "application/json", schema =
      @Schema(implementation = ErrorResponse.class))})
  @ApiResponse(responseCode = "500", description = "Internal Server Error", content = {
      @Content(mediaType = "application/json", schema =
      @Schema(implementation = ErrorResponse.class))})
  @PutMapping(value = "/reservations/amend/editRoom", produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<TempBookingRefResponseDto> amendEditRoom(
      @RequestBody @Valid EditRoomRequestDto editRoomRequestDto) {
    var updateReservationRequest = editRoomRequestMapper.toModel(editRoomRequestDto);
    var bookingChannel = bookingChannelRequestMapper.toModel(
        editRoomRequestDto.getBookingChannel());

    var editRoomResponse = reservationPortBusinessCase.editRoom(updateReservationRequest,
        editRoomRequestDto.getTempBookingRef(), bookingChannel, null, null);

    var editRoomResponseModel = tempBookingRefResponseMapper.toDto(editRoomResponse);
    return ResponseEntity.status(HttpStatus.OK).body(editRoomResponseModel);
  }


  @Operation(summary = "Confirm amend changes")
  @ApiResponse(responseCode = "200", description = "Success", content = {
      @Content(mediaType = "application/json",
          schema = @Schema(implementation = ReservationByBasketRefResponseDto.class))})
  @ApiResponse(responseCode = "400", description = "Error Occurred ", content = {
      @Content(mediaType = "application/json", schema =
      @Schema(implementation = ErrorResponse.class))})
  @ApiResponse(responseCode = "404", description = "Resource Not Found", content = {
      @Content(mediaType = "application/json", schema =
      @Schema(implementation = ErrorResponse.class))})
  @ApiResponse(responseCode = "500", description = "Internal Server Error", content = {
      @Content(mediaType = "application/json", schema =
      @Schema(implementation = ErrorResponse.class))})
  @PutMapping(value = "/reservations/amend/confirmAmend", produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<ReservationByBasketRefResponseDto> confirmAmend(
      @RequestBody @Valid ConfirmAmendRequestDto confirmAmendRequestDto) {

    var confirmAmendRequest = confirmAmendRequestMapper.toModel(confirmAmendRequestDto);
    var reservationResponse = reservationPortBusinessCase.confirmAmend(confirmAmendRequest, true);
    final var reservationResponseDto = reservationByBasketRefResponseMapper.toDto(
        reservationResponse);
    return ResponseEntity.status(HttpStatus.CREATED).body(reservationResponseDto);
  }

  @Operation(summary = "Remove a room based on basket ref and reservation id")
  @ApiResponse(responseCode = "200", description = "Success", content = {
      @Content(mediaType = "application/json",
          schema = @Schema(implementation = TempBookingRefResponseDto.class))})
  @ApiResponse(responseCode = "400", description = "Error Occurred ", content = {
      @Content(mediaType = "application/json",
          schema = @Schema(implementation = ErrorResponse.class))})
  @ApiResponse(responseCode = "500", description = "Internal Server Error", content = {
      @Content(mediaType = "application/json",
          schema = @Schema(implementation = ErrorResponse.class))})
  @PostMapping(value = "/reservations/rooms/delete", produces = MediaType.APPLICATION_JSON_VALUE)
  public TempBookingRefResponseDto removeRoom(
      @RequestParam("tempBookingRef") String tempBookingRef,
      @RequestParam("reservationId") String reservationId,
      @RequestParam("token") String token,
      @Valid @ParameterObject BookingChannelDto bookingChannelDto) {
    var bookingChannel = bookingChannelRequestMapper.toModel(bookingChannelDto);
    return tempBookingRefResponseMapper.toDto(
        reservationPortBusinessCase.removeRoom(tempBookingRef, reservationId, token, true,
            bookingChannel, null));
  }

  @Operation(summary = "Amend Distribution")
  @ApiResponse(responseCode = "200", description = "Success", content = {
      @Content(mediaType = "application/json",
          schema = @Schema(implementation = ReservationByBasketRefResponseDto.class))})
  @ApiResponse(responseCode = "400", description = "Error Occurred ", content = {
      @Content(mediaType = "application/json",
          schema = @Schema(implementation = ErrorResponse.class))})
  @ApiResponse(responseCode = "500", description = "Internal Server Error", content = {
      @Content(mediaType = "application/json",
          schema = @Schema(implementation = ErrorResponse.class))})
  @PostMapping(value = "/reservations/amendDistribution/{basketReference}", produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<ReservationByBasketRefResponseDto> amendDistribution(
      @PathVariable("basketReference") String basketReference,
      @RequestBody @Valid AmendDistributionRequestDto amendDistributionRequestDto) {

    var amendDistributionRequest = amendDistributionRequestMapper.toReservationRequestModel(
        amendDistributionRequestDto);
    var updateReservationsRequest = amendDistributionRequestMapper.toUpdateReservationsDistrModel(
        amendDistributionRequestDto);
    var reservationPackagesRequest = amendDistributionPackagesMappper.toModel(
        amendDistributionRequestDto);
    var bookerDetails = bookerDetailsMapper.toModel(amendDistributionRequestDto.getBookerDetails());
    var distributionReservationResponse =
        reservationPortBusinessCase.amendDistribution(basketReference, amendDistributionRequest,
            updateReservationsRequest, reservationPackagesRequest, bookerDetails);
    final var reservationResponseDto =
        reservationByBasketRefResponseMapper.toDto(distributionReservationResponse);
    return ResponseEntity.ok(reservationResponseDto);
  }

  @Operation(summary = "Create memo for a basket")
  @ApiResponse(responseCode = "201", description = "Created", content = {
      @Content(mediaType = "application/json",
          schema = @Schema(implementation = MemosResponseDto.class))})
  @ApiResponse(responseCode = "400", description = "Error Occurred ", content = {
      @Content(mediaType = "application/json",
          schema = @Schema(implementation = ErrorResponse.class))})
  @ApiResponse(responseCode = "500", description = "Internal Server Error", content = {
      @Content(mediaType = "application/json",
          schema = @Schema(implementation = ErrorResponse.class))})
  @PostMapping(value = "/reservations/memos", produces = MediaType.APPLICATION_JSON_VALUE)
  @ResponseStatus(HttpStatus.CREATED)
  public MemosResponseDto createMemo(
      @RequestBody @Valid CreateMemoRequestDto createMemoRequestDto) {
    var createMemoRequest = memosMapper.toModel(createMemoRequestDto);
    return memosMapper.toDto(reservationPortBusinessCase.createMemo(createMemoRequest));
  }

  @Operation(summary = "Get memos for a basket")
  @ApiResponse(responseCode = "200", description = "Success", content = {
      @Content(mediaType = "application/json",
          schema = @Schema(implementation = MemosResponseDto.class))})
  @ApiResponse(responseCode = "400", description = "Error Occurred ", content = {
      @Content(mediaType = "application/json",
          schema = @Schema(implementation = ErrorResponse.class))})
  @ApiResponse(responseCode = "500", description = "Internal Server Error", content = {
      @Content(mediaType = "application/json",
          schema = @Schema(implementation = ErrorResponse.class))})
  @GetMapping(value = "/reservations/basket/{basketReference}/memos", produces = MediaType.APPLICATION_JSON_VALUE)
  @ResponseStatus(HttpStatus.OK)
  public MemosResponseDto getMemos(@PathVariable("basketReference") String basketReference) {
    return memosMapper.toDto(reservationPortBusinessCase.getMemos(basketReference));
  }

  @Operation(summary = "Search booking from CDH")
  @ApiResponse(responseCode = "200", description = "Success", content = {
      @Content(mediaType = "application/json",
          schema = @Schema(implementation = SearchBookingsResponseMapper.class))})
  @ApiResponse(responseCode = "400", description = "Error Occurred ",
      content = {
          @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))})
  @ApiResponse(responseCode = "404", description = "Resource Not Found",
      content = {
          @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))})
  @ApiResponse(responseCode = "500", description = "Internal Server Error",
      content = {
          @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))})
  @GetMapping(value = "reservations/search/booking/cdh", produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<CdhSearchBookingsResponseDto> searchBookingFromCdh(
      @Valid @ParameterObject CdhSearchBookingsRequestDto requestDto) {

    log.debug("Request to search bookings from CDH: {}", toSafeLogString(requestDto));

    final var cdhSearchBookingsRequest = cdhSearchBookingRequestMapper.toModel(requestDto);

    final var cdhSearchBookingsResponse =
        cdhSearchBookingInPort.searchBookingsFromCdh(cdhSearchBookingsRequest);

    final var cdhSearchBookingsResponseDto =
        cdhSearchBookingResponseMapper.toDto(cdhSearchBookingsResponse);

    log.debug("Response to search booking from CDH : {}", cdhSearchBookingsResponseDto);

    return ResponseEntity.ok(cdhSearchBookingsResponseDto);
  }

  @Operation(summary = "Attach a profile to reservations")
  @ApiResponse(responseCode = "200", description = "Success")
  @ApiResponse(responseCode = "400", description = "Error Occurred ", content = {
      @Content(mediaType = "application/json",
          schema = @Schema(implementation = ErrorResponse.class))})
  @ApiResponse(responseCode = "404", description = "Resource Not Found", content = {
      @Content(mediaType = "application/json",
          schema = @Schema(implementation = ErrorResponse.class))})
  @ApiResponse(responseCode = "500", description = "Internal Server Error", content = {
      @Content(mediaType = "application/json",
          schema = @Schema(implementation = ErrorResponse.class))})
  @PostMapping(value = "/reservations/profiles", produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<Void> attachProfileToReservations(
      @RequestBody @Valid AttachReservationProfileRequestDto attachReservationProfileRequestDto) {

    final AttachReservationProfileRequest attachReservationProfileRequest =
        attachReservationProfileRequestMapper.toModel(attachReservationProfileRequestDto);

    reservationPortBusinessCase.attachProfileToReservations(attachReservationProfileRequest);

    return new ResponseEntity<>(HttpStatus.OK);
  }

  @Operation(summary = "Delete all existing routing instructions.")
  @ApiResponse(responseCode = "204", description = "Success", content = {
      @Content(mediaType = "application/json",
          schema = @Schema())})
  @ApiResponse(responseCode = "400", description = "Error Occurred ",
      content = {
          @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))})
  @ApiResponse(responseCode = "404", description = "Resource Not Found",
      content = {
          @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))})
  @ApiResponse(responseCode = "500", description = "Internal Server Error", content = {
      @Content(mediaType = "application/json",
          schema = @Schema(implementation = ErrorResponse.class))})
  @DeleteMapping(value = "/reservations/routingInstructions", produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<Void> deleteRoutingInstructions(
      @RequestParam("hotelId") String hotelId,
      @RequestParam("reservationIds") Set<String> reservationIds) {
    reservationPortBusinessCase.deleteRoutingInstruction(hotelId, reservationIds);
    return ResponseEntity.noContent().build();
  }

  @Operation(summary = "Create a booker/ company Profile")
  @ApiResponse(responseCode = "200", description = "Success")
  @ApiResponse(responseCode = "400", description = "Error Occurred ", content = {
      @Content(mediaType = "application/json",
          schema = @Schema(implementation = ErrorResponse.class))})
  @ApiResponse(responseCode = "404", description = "Resource Not Found", content = {
      @Content(mediaType = "application/json",
          schema = @Schema(implementation = ErrorResponse.class))})
  @ApiResponse(responseCode = "500", description = "Internal Server Error", content = {
      @Content(mediaType = "application/json",
          schema = @Schema(implementation = ErrorResponse.class))})
  @PostMapping(value = "/reservations/create/profile", produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<ReservationProfilesDto> createProfiles(
      @RequestBody @Valid ReservationGuestRequestDto reservationGuestRequestDto) {
    final ReservationGuestRequest guestReservationRequest = reservationGuestRequestMapper.toModel(
        reservationGuestRequestDto);
    final var resProfiles = reservationPortBusinessCase.createProfiles(guestReservationRequest);
    return ResponseEntity.status(HttpStatus.OK)
        .body(reservationResponseMapper.toCreateProfileDto(resProfiles));
  }

  @Operation(summary = "Add attachment to a Reservation")
  @ApiResponse(responseCode = "200", description = "Success", content = {
      @Content(mediaType = "application/json",
          schema = @Schema(implementation = PreCheckInResponse.class))})
  @ApiResponse(responseCode = "400", description = "Error Occurred ", content = {
      @Content(mediaType = "application/json",
          schema = @Schema(implementation = ErrorResponse.class))})
  @ApiResponse(responseCode = "404", description = "Resource Not Found", content = {
      @Content(mediaType = "application/json",
          schema = @Schema(implementation = ErrorResponse.class))})
  @ApiResponse(responseCode = "500", description = "Internal Server Error", content = {
      @Content(mediaType = "application/json",
          schema = @Schema(implementation = ErrorResponse.class))})
  @PostMapping(value = "/reservations/attachments", produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<PreCheckInResponse> addAttachmentToReservation(
      @RequestBody @Valid ReservationFileAttachmentRequestDto reservationFileAttachmentRequestDto) {
    final ReservationFileAttachmentRequest reservationFileAttachmentRequest =
        reservationFileAttachmentRequestMapper.toModel(reservationFileAttachmentRequestDto);
    final PreCheckInResponse reservationFileAttachmentResponse = reservationPortBusinessCase.addAttachmentToReservation(
        reservationFileAttachmentRequest);
    return ResponseEntity.status(HttpStatus.OK).body(reservationFileAttachmentResponse);
  }

  @Operation(summary = "Save Pre-CheckIn status of a Reservation")
  @ApiResponse(responseCode = "200", description = "Success", content = {
      @Content(mediaType = "application/json",
          schema = @Schema(implementation = PreCheckInResponse.class))})
  @ApiResponse(responseCode = "400", description = "Error Occurred ", content = {
      @Content(mediaType = "application/json",
          schema = @Schema(implementation = ErrorResponse.class))})
  @ApiResponse(responseCode = "404", description = "Resource Not Found", content = {
      @Content(mediaType = "application/json",
          schema = @Schema(implementation = ErrorResponse.class))})
  @ApiResponse(responseCode = "500", description = "Internal Server Error", content = {
      @Content(mediaType = "application/json",
          schema = @Schema(implementation = ErrorResponse.class))})
  @PostMapping(value = "/reservations/pre-checkin", produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<PreCheckInResponse> saveReservationPreCheckIn(
      @RequestBody @Valid PreCheckInRequestDto preCheckInRequestDto) {
    final PreCheckInRequest preCheckInRequest =
        preCheckInRequestMapper.toModel(preCheckInRequestDto);
    final PreCheckInResponse preCheckInResponse = reservationPortBusinessCase.saveReservationPreCheckIn(
        preCheckInRequest);
    return ResponseEntity.status(HttpStatus.OK).body(preCheckInResponse);
  }

  @Operation(summary = "add/remove reservation packages by specifying the schedule")
  @ApiResponse(responseCode = "201", description = "Success", content = {
      @Content(mediaType = "application/json", schema =
      @Schema(implementation = SaveReservationResponseDto.class))})
  @ApiResponse(responseCode = "400", description = "Error Occurred ", content = {
      @Content(mediaType = "application/json", schema =
      @Schema(implementation = ErrorResponse.class))})
  @ApiResponse(responseCode = "404", description = "Resource Not Found", content = {
      @Content(mediaType = "application/json", schema =
      @Schema(implementation = ErrorResponse.class))})
  @ApiResponse(responseCode = "500", description = "Internal Server Error", content = {
      @Content(mediaType = "application/json", schema =
      @Schema(implementation = ErrorResponse.class))})
  @PutMapping(value = "/reservations/ancillaries/scheduled", produces = MediaType.APPLICATION_JSON_VALUE)
  @ResponseStatus(HttpStatus.CREATED)
  public SaveReservationResponseDto updateReservationPackages(
      @RequestBody @Valid ReservationPackagesScheduledRequestDto updateReservationPackagesDto) {

    final var updateReservationRequest = updateReservationRequestMapper.toModel(
        updateReservationPackagesDto);
    final var reservationResponse = reservationPortBusinessCase.updateReservationPackageScheduled(
        updateReservationRequest);
    return reservationResponseMapper.toDto(reservationResponse);
  }

  @Operation(summary = "Link reservation to leisure customer account")
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
  @PutMapping(value = "/reservations/link-leisure-customer", produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<Void> linkReservationToLeisureCustomer(
      @RequestBody @Valid LinkReservationToLeisureCustomerRequestDto linkReservationToLeisureCustomerRequestDto) {

    final var linkReservationToLeisureCustomerRequest =
        linkReservationToLeisureCustomerRequestMapper.toModel(
            linkReservationToLeisureCustomerRequestDto);
    reservationPortBusinessCase.linkReservationToLeisureCustomer(linkReservationToLeisureCustomerRequest);

    return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
  }

  @Operation(summary = "Update preferences")
  @ApiResponse(responseCode = "204", description = "Success", content = {
      @Content(mediaType = "application/json")})
  @ApiResponse(responseCode = "400", description = "Error Occurred ", content = {
      @Content(mediaType = "application/json",
          schema = @Schema(implementation = ErrorResponse.class))})
  @ApiResponse(responseCode = "404", description = "Resource Not Found", content = {
      @Content(mediaType = "application/json",
          schema = @Schema(implementation = ErrorResponse.class))})
  @ApiResponse(responseCode = "500", description = "Internal Server Error", content = {
      @Content(mediaType = "application/json",
          schema = @Schema(implementation = ErrorResponse.class))})
  @PutMapping(value = "/reservations/preferences", produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<Void> updatePreferences(
      @RequestBody @Valid ReservationPreferencesRequestDto reservationPreferencesRequestDto) {

    final ReservationPreferencesRequest reservationPreferencesRequest =
        reservationPreferencesRequestMapper.toModel(reservationPreferencesRequestDto);

    reservationPortBusinessCase.updateReservationPreferences(reservationPreferencesRequest);

    return ResponseEntity.noContent().build();
  }

  @Operation(summary = "Update udfs for a reservation")
  @ApiResponse(responseCode = "204", description = "Success", content = {
      @Content(mediaType = "application/json")})
  @ApiResponse(responseCode = "400", description = "Error Occurred ", content = {
      @Content(mediaType = "application/json",
          schema = @Schema(implementation = ErrorResponse.class))})
  @ApiResponse(responseCode = "404", description = "Resource Not Found", content = {
      @Content(mediaType = "application/json",
          schema = @Schema(implementation = ErrorResponse.class))})
  @ApiResponse(responseCode = "500", description = "Internal Server Error", content = {
      @Content(mediaType = "application/json",
          schema = @Schema(implementation = ErrorResponse.class))})
  @PutMapping(value = "/reservations/alerts", produces = MediaType.APPLICATION_JSON_VALUE)
  @ResponseStatus(HttpStatus.NO_CONTENT)
  public void updateReservationAlerts(
      @RequestBody @Valid UpdateReservationAlertsRequestDto updateReservationAlertsRequestDto) {

    final var updateUdfModel = reservationRequestMapper.toAlertModel(
        updateReservationAlertsRequestDto);

    reservationPortBusinessCase.updateReservationAlerts(updateUdfModel);
  }

  private void validateWbToken(HttpHeaders headers) {
    if (Objects.isNull(headers.getFirst(TOKEN)) || headers.getFirst(TOKEN).isEmpty()) {
      var ex = new InvalidTokenException(ErrorCode.DIGITAL_INVALID_WBTOKEN, "Invalid WB-token");
      ExceptionLogger.log(log, ex);
      throw ex;
    }
  }

  private static String toSafeLogString(CdhSearchBookingsRequestDto dto) {
    if (dto == null) {
      return "null";
    }
    return "CdhSearchBookingsRequestDto{"
        + "bookingReference='" + sanitizeLog(dto.getBookingReference()) + '\''
        + ", bookerLastName='" + sanitizeLog(dto.getBookerLastName()) + '\''
        + ", guestLastName='" + sanitizeLog(dto.getGuestLastName()) + '\''
        + ", bookerPostcode='" + sanitizeLog(dto.getBookerPostcode()) + '\''
        + ", hotelId='" + sanitizeLog(dto.getHotelId()) + '\''
        + ", bookerEmail='" + sanitizeLog(dto.getBookerEmail()) + '\''
        + ", bookerPhone='" + sanitizeLog(dto.getBookerPhone()) + '\''
        + ", arrivalDateFrom='" + sanitizeLog(dto.getArrivalDateFrom()) + '\''
        + ", arrivalDateTo='" + sanitizeLog(dto.getArrivalDateTo()) + '\''
        + ", cancellationDate='" + sanitizeLog(dto.getCancellationDate()) + '\''
        + ", companyName='" + sanitizeLog(dto.getCompanyName()) + '\''
        + ", thirdPartyBookingReferenceNumber='" + sanitizeLog(
        dto.getThirdPartyBookingReferenceNumber()) + '\''
        + ", bookingsDatabaseSearch=" + dto.isBookingsDatabaseSearch()
        + ", pageSize=" + dto.getPageSize()
        + ", pageNumber=" + dto.getPageNumber()
        + ", continuationToken='<masked>'"
        + '}';
  }

  private static String sanitizeLog(String value) {
    if (value == null) {
      return "null";
    }
    return value.replaceAll("[^a-zA-Z0-9 _@.+-]", "");
  }

  @Operation(summary = "Preview generated deposit folios for reservations")
  @ApiResponse(responseCode = "200", description = "Success", content = {
      @Content(mediaType = "application/json", schema = @Schema(implementation = DepositFoliosResponseDto.class))})
  @ApiResponse(responseCode = "400", description = "Error Occurred ", content = {
      @Content(mediaType = "application/json", schema = @Schema(implementation = OhipBadRequestException.class))})
  @ApiResponse(responseCode = "404", description = "Resource Not Found", content = {
      @Content(mediaType = "application/json", schema = @Schema(implementation = OhipNotFoundException.class))})
  @ApiResponse(responseCode = "500", description = "Internal Server Error", content = {
      @Content(mediaType = "application/json", schema = @Schema(implementation = OhipInternalException.class))})
  @GetMapping(value = "/reservations/preview-deposits", produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<DepositFoliosResponseDto> getDepositFolioForReservations(
          @RequestParam("hotelId") String hotelId, @RequestParam("reservationIds") Set<String> reservationIds) {
    DepositFoliosResponse depositFolios = reservationPortBusinessCase
            .getGeneratedDepositFolios(hotelId, reservationIds);
    DepositFoliosResponseDto depositFoliosResponseDto = depositFoliosMapper.toDto(depositFolios);
    return ResponseEntity.status(HttpStatus.OK).body(depositFoliosResponseDto);
  }

  @Operation(summary = "create Deposit Folios for reservations")
  @ApiResponse(responseCode = "201", description = "Success")
  @ApiResponse(responseCode = "400", description = "Error Occurred ", content = {
      @Content(mediaType = "application/json", schema = @Schema(implementation = OhipBadRequestException.class))})
  @ApiResponse(responseCode = "404", description = "Resource Not Found", content = {
      @Content(mediaType = "application/json", schema = @Schema(implementation = OhipNotFoundException.class))})
  @ApiResponse(responseCode = "500", description = "Internal Server Error", content = {
      @Content(mediaType = "application/json", schema = @Schema(implementation = OhipInternalException.class))})
  @PostMapping(value = "/reservations/save-deposit-folios", produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<Void> saveDepositFolios(@RequestBody @Valid DepositFoliosRequestDto depositFoliosRequestDto) {
    final var depositFoliosRequest = depositFoliosMapper.toDto(depositFoliosRequestDto);
    reservationPortBusinessCase.saveDepositFolios(depositFoliosRequest);
    return ResponseEntity.status(HttpStatus.CREATED).build();
  }
}
