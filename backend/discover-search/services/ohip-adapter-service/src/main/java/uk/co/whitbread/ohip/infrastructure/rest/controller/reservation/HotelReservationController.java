package uk.co.whitbread.ohip.infrastructure.rest.controller.reservation;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.validation.Valid;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import uk.co.whitbread.commons.exceptions.model.ErrorResponse;
import uk.co.whitbread.ohip.domain.model.reservation.in.AttachReservationProfileRequest;
import uk.co.whitbread.ohip.domain.model.reservation.in.BillingAddressRequest;
import uk.co.whitbread.ohip.domain.model.reservation.in.CancelReservationRequest;
import uk.co.whitbread.ohip.domain.model.reservation.in.PreCheckInRequest;
import uk.co.whitbread.ohip.domain.model.reservation.in.ReservationFileAttachmentRequest;
import uk.co.whitbread.ohip.domain.model.reservation.in.ReservationGuestRequest;
import uk.co.whitbread.ohip.domain.model.reservation.in.ReservationPreferencesRequest;
import uk.co.whitbread.ohip.domain.model.reservation.out.CancelReservationResponse;
import uk.co.whitbread.ohip.domain.model.reservation.out.PreCheckInResponse;
import uk.co.whitbread.ohip.domain.model.reservation.out.ReservationGuestResponse;
import uk.co.whitbread.ohip.domain.model.reservation.out.ReservationsPaymentCardType;
import uk.co.whitbread.ohip.domain.ports.primary.HotelReservationInPort;
import uk.co.whitbread.ohip.infrastructure.exceptions.OhipBadRequestException;
import uk.co.whitbread.ohip.infrastructure.exceptions.OhipInternalException;
import uk.co.whitbread.ohip.infrastructure.exceptions.OhipNotFoundException;
import uk.co.whitbread.ohip.infrastructure.rest.controller.reservation.mapper.AttachReservationProfileRequestMapper;
import uk.co.whitbread.ohip.infrastructure.rest.controller.reservation.mapper.BookerDetailsCnpRequestMapper;
import uk.co.whitbread.ohip.infrastructure.rest.controller.reservation.mapper.BookingAllowancesResponseMapper;
import uk.co.whitbread.ohip.infrastructure.rest.controller.reservation.mapper.BusinessItemsRequestMapper;
import uk.co.whitbread.ohip.infrastructure.rest.controller.reservation.mapper.CancelReservationRequestMapper;
import uk.co.whitbread.ohip.infrastructure.rest.controller.reservation.mapper.CancelReservationResponseMapper;
import uk.co.whitbread.ohip.infrastructure.rest.controller.reservation.mapper.CancelResponseMapper;
import uk.co.whitbread.ohip.infrastructure.rest.controller.reservation.mapper.CancellationPoliciesResponseMapper;
import uk.co.whitbread.ohip.infrastructure.rest.controller.reservation.mapper.CompanyQuestionAndAnswerRequestMapper;
import uk.co.whitbread.ohip.infrastructure.rest.controller.reservation.mapper.ConfirmAmendForSingleRequestMapper;
import uk.co.whitbread.ohip.infrastructure.rest.controller.reservation.mapper.ConfirmAmendRequestMapper;
import uk.co.whitbread.ohip.infrastructure.rest.controller.reservation.mapper.ConfirmReservationRequestMapper;
import uk.co.whitbread.ohip.infrastructure.rest.controller.reservation.mapper.ConfirmReservationResponseMapper;
import uk.co.whitbread.ohip.infrastructure.rest.controller.reservation.mapper.CopyReservationsRequestMapper;
import uk.co.whitbread.ohip.infrastructure.rest.controller.reservation.mapper.CopyReservationsResponseMapper;
import uk.co.whitbread.ohip.infrastructure.rest.controller.reservation.mapper.DepositsResponseMapper;
import uk.co.whitbread.ohip.infrastructure.rest.controller.reservation.mapper.LinkReservationToLeisureCustomerRequestMapper;
import uk.co.whitbread.ohip.infrastructure.rest.controller.reservation.mapper.MarketingPreferencesResponseMapper;
import uk.co.whitbread.ohip.infrastructure.rest.controller.reservation.mapper.MemosMapper;
import uk.co.whitbread.ohip.infrastructure.rest.controller.reservation.mapper.PreCheckInRequestMapper;
import uk.co.whitbread.ohip.infrastructure.rest.controller.reservation.mapper.RatePlanChangeRequestMapper;
import uk.co.whitbread.ohip.infrastructure.rest.controller.reservation.mapper.ReservationAmountsMapper;
import uk.co.whitbread.ohip.infrastructure.rest.controller.reservation.mapper.ReservationByBasketRefResponseMapper;
import uk.co.whitbread.ohip.infrastructure.rest.controller.reservation.mapper.ReservationDetailsEnhancedResponseMapper;
import uk.co.whitbread.ohip.infrastructure.rest.controller.reservation.mapper.ReservationDetailsResponseMapper;
import uk.co.whitbread.ohip.infrastructure.rest.controller.reservation.mapper.ReservationFileAttachmentRequestMapper;
import uk.co.whitbread.ohip.infrastructure.rest.controller.reservation.mapper.ReservationGuestRequestMapper;
import uk.co.whitbread.ohip.infrastructure.rest.controller.reservation.mapper.ReservationGuestResponseMapper;
import uk.co.whitbread.ohip.infrastructure.rest.controller.reservation.mapper.ReservationLightweightResponseMapper;
import uk.co.whitbread.ohip.infrastructure.rest.controller.reservation.mapper.ReservationPreferencesRequestMapper;
import uk.co.whitbread.ohip.infrastructure.rest.controller.reservation.mapper.ReservationRequestMapper;
import uk.co.whitbread.ohip.infrastructure.rest.controller.reservation.mapper.ReservationResponseMapper;
import uk.co.whitbread.ohip.infrastructure.rest.controller.reservation.mapper.ReservationsPackagesResponseMapper;
import uk.co.whitbread.ohip.infrastructure.rest.controller.reservation.mapper.RoomTypeChangeRequestMapper;
import uk.co.whitbread.ohip.infrastructure.rest.controller.reservation.mapper.SearchBookingsRequestMapper;
import uk.co.whitbread.ohip.infrastructure.rest.controller.reservation.mapper.SearchBookingsResponseMapper;
import uk.co.whitbread.ohip.infrastructure.rest.controller.reservation.mapper.SpecialRequestsMapper;
import uk.co.whitbread.ohip.infrastructure.rest.controller.reservation.mapper.UpdateBookerEmailRequestMapper;
import uk.co.whitbread.ohip.infrastructure.rest.controller.reservation.mapper.UpdateCancellationRequestMapper;
import uk.co.whitbread.ohip.infrastructure.rest.controller.reservation.mapper.UpdateCustomReferenceNumberRequestMapper;
import uk.co.whitbread.ohip.infrastructure.rest.controller.reservation.mapper.UpdateDiscountRequestMapper;
import uk.co.whitbread.ohip.infrastructure.rest.controller.reservation.mapper.UpdateReasonForStayRequestMapper;
import uk.co.whitbread.ohip.infrastructure.rest.controller.reservation.mapper.UpdateReasonForStayResponseMapper;
import uk.co.whitbread.ohip.infrastructure.rest.controller.reservation.mapper.UpdateReservationCcAgentIdRequestMapper;
import uk.co.whitbread.ohip.infrastructure.rest.controller.reservation.mapper.UpdateReservationOverrideReasonsRequestMapper;
import uk.co.whitbread.ohip.infrastructure.rest.controller.reservation.mapper.UpdateReservationRequestMapper;
import uk.co.whitbread.ohip.infrastructure.rest.controller.reservation.model.in.AttachReservationProfileRequestDto;
import uk.co.whitbread.ohip.infrastructure.rest.controller.reservation.model.in.BillingAddressCaptRequestDto;
import uk.co.whitbread.ohip.infrastructure.rest.controller.reservation.model.in.BookerDetailsCnpRequestDto;
import uk.co.whitbread.ohip.infrastructure.rest.controller.reservation.model.in.BusinessItemsRequestDto;
import uk.co.whitbread.ohip.infrastructure.rest.controller.reservation.model.in.CancelReservationRequestDto;
import uk.co.whitbread.ohip.infrastructure.rest.controller.reservation.model.in.CompanyQuestionAndAnswerDetailsRequestDto;
import uk.co.whitbread.ohip.infrastructure.rest.controller.reservation.model.in.ConfirmAmendForSingleRequestDto;
import uk.co.whitbread.ohip.infrastructure.rest.controller.reservation.model.in.ConfirmAmendOnReservationsRequestDto;
import uk.co.whitbread.ohip.infrastructure.rest.controller.reservation.model.in.ConfirmReservationRequestDto;
import uk.co.whitbread.ohip.infrastructure.rest.controller.reservation.model.in.CopyReservationsRequestDto;
import uk.co.whitbread.ohip.infrastructure.rest.controller.reservation.model.in.CreateMemoRequestDto;
import uk.co.whitbread.ohip.infrastructure.rest.controller.reservation.model.in.LinkReservationToLeisureCustomerRequestDto;
import uk.co.whitbread.ohip.infrastructure.rest.controller.reservation.model.in.PreCheckInRequestDto;
import uk.co.whitbread.ohip.infrastructure.rest.controller.reservation.model.in.RatePlanChangeRequestDto;
import uk.co.whitbread.ohip.infrastructure.rest.controller.reservation.model.in.ReservationFileAttachmentRequestDto;
import uk.co.whitbread.ohip.infrastructure.rest.controller.reservation.model.in.ReservationGuestRequestDto;
import uk.co.whitbread.ohip.infrastructure.rest.controller.reservation.model.in.ReservationPackagesRequestDto;
import uk.co.whitbread.ohip.infrastructure.rest.controller.reservation.model.in.ReservationPreferencesRequestDto;
import uk.co.whitbread.ohip.infrastructure.rest.controller.reservation.model.in.ReservationProfilesDto;
import uk.co.whitbread.ohip.infrastructure.rest.controller.reservation.model.in.ReservationRequestDto;
import uk.co.whitbread.ohip.infrastructure.rest.controller.reservation.model.in.ReservationScheduledPackagesRequestDto;
import uk.co.whitbread.ohip.infrastructure.rest.controller.reservation.model.in.RoomTypeChangeRequestDto;
import uk.co.whitbread.ohip.infrastructure.rest.controller.reservation.model.in.SearchBookingsRequestDto;
import uk.co.whitbread.ohip.infrastructure.rest.controller.reservation.model.in.SpecialRequestsDto;
import uk.co.whitbread.ohip.infrastructure.rest.controller.reservation.model.in.UpdateBookerEmailRequestDto;
import uk.co.whitbread.ohip.infrastructure.rest.controller.reservation.model.in.UpdateCancellationPoliciesRequestDto;
import uk.co.whitbread.ohip.infrastructure.rest.controller.reservation.model.in.UpdateCancellationPolicyRequestDto;
import uk.co.whitbread.ohip.infrastructure.rest.controller.reservation.model.in.UpdateCustomReferenceNumberRequestDto;
import uk.co.whitbread.ohip.infrastructure.rest.controller.reservation.model.in.UpdateDiscountRequestDto;
import uk.co.whitbread.ohip.infrastructure.rest.controller.reservation.model.in.UpdateReasonForStayRequestDto;
import uk.co.whitbread.ohip.infrastructure.rest.controller.reservation.model.in.UpdateReservationAlertsRequestDto;
import uk.co.whitbread.ohip.infrastructure.rest.controller.reservation.model.in.UpdateReservationCcAgentIdRequestDto;
import uk.co.whitbread.ohip.infrastructure.rest.controller.reservation.model.in.UpdateReservationOverrideReasonsRequestDto;
import uk.co.whitbread.ohip.infrastructure.rest.controller.reservation.model.in.UpdateReservationSingleCallRequestDto;
import uk.co.whitbread.ohip.infrastructure.rest.controller.reservation.model.in.UpdateReservationsRequestDto;
import uk.co.whitbread.ohip.infrastructure.rest.controller.reservation.model.out.BillingAddressResponseDto;
import uk.co.whitbread.ohip.infrastructure.rest.controller.reservation.model.out.BookingAllowancesResponseDto;
import uk.co.whitbread.ohip.infrastructure.rest.controller.reservation.model.out.CancelInformationResponseDto;
import uk.co.whitbread.ohip.infrastructure.rest.controller.reservation.model.out.CancelReservationResponseDto;
import uk.co.whitbread.ohip.infrastructure.rest.controller.reservation.model.out.CancellationPoliciesResponseDto;
import uk.co.whitbread.ohip.infrastructure.rest.controller.reservation.model.out.ConfirmReservationResponseDto;
import uk.co.whitbread.ohip.infrastructure.rest.controller.reservation.model.out.CopyReservationsResponseDto;
import uk.co.whitbread.ohip.infrastructure.rest.controller.reservation.model.out.DepositsResponseDto;
import uk.co.whitbread.ohip.infrastructure.rest.controller.reservation.model.out.MarketingPreferencesResponseDto;
import uk.co.whitbread.ohip.infrastructure.rest.controller.reservation.model.out.MemosResponseDto;
import uk.co.whitbread.ohip.infrastructure.rest.controller.reservation.model.out.ReservationAmountsDto;
import uk.co.whitbread.ohip.infrastructure.rest.controller.reservation.model.out.ReservationByBasketRefResponseDto;
import uk.co.whitbread.ohip.infrastructure.rest.controller.reservation.model.out.ReservationDetailsEnhancedDto;
import uk.co.whitbread.ohip.infrastructure.rest.controller.reservation.model.out.ReservationGuestResponseDto;
import uk.co.whitbread.ohip.infrastructure.rest.controller.reservation.model.out.ReservationIdDetailsDto;
import uk.co.whitbread.ohip.infrastructure.rest.controller.reservation.model.out.ReservationLightweightResponseDto;
import uk.co.whitbread.ohip.infrastructure.rest.controller.reservation.model.out.ReservationResponseDto;
import uk.co.whitbread.ohip.infrastructure.rest.controller.reservation.model.out.ReservationsDetailsResponseDto;
import uk.co.whitbread.ohip.infrastructure.rest.controller.reservation.model.out.ReservationsPackagesResponseDto;
import uk.co.whitbread.ohip.infrastructure.rest.controller.reservation.model.out.RoomTypeChangeResponseDto;
import uk.co.whitbread.ohip.infrastructure.rest.controller.reservation.model.out.SearchBookingsResponseDto;
import uk.co.whitbread.ohip.infrastructure.rest.controller.reservation.model.out.UpdateReasonForStayResponseDto;

@RestController
@RequiredArgsConstructor
@RequestMapping("/v1")
public class HotelReservationController {

  private final HotelReservationInPort hotelReservationPort;
  private final ReservationResponseMapper reservationResponseMapper;
  private final ReservationDetailsResponseMapper reservationDetailsResponseMapper;
  private final ReservationRequestMapper reservationRequestMapper;
  private final ConfirmReservationRequestMapper confirmReservationRequestMapper;
  private final ConfirmReservationResponseMapper confirmReservationResponseMapper;
  private final ReservationByBasketRefResponseMapper reservationByBasketRefResponseMapper;
  private final ReservationDetailsEnhancedResponseMapper reservationDetailsEnhancedResponseMapper;
  private final ReservationGuestRequestMapper reservationGuestRequestMapper;
  private final ReservationGuestResponseMapper reservationGuestResponseMapper;
  private final UpdateReservationRequestMapper updateReservationRequestMapper;
  private final RatePlanChangeRequestMapper reservationRateChangeRequestMapper;
  private final RoomTypeChangeRequestMapper reservationRoomTypeChangeRequestMapper;
  private final ReservationsPackagesResponseMapper reservationsPackagesResponseMapper;
  private final CancelReservationRequestMapper cancelReservationRequestMapper;
  private final CancelReservationResponseMapper cancelReservationResponseMapper;
  private final CancelResponseMapper cancelResponseMapper;
  private final UpdateDiscountRequestMapper updateDiscountRequestMapper;
  private final UpdateReasonForStayRequestMapper updateReasonForStayRequestMapper;
  private final UpdateReasonForStayResponseMapper updateReasonForStayResponseMapper;
  private final SearchBookingsRequestMapper searchBookingsRequestMapper;
  private final SearchBookingsResponseMapper searchBookingsResponseMapper;
  private final BusinessItemsRequestMapper businessItemsRequestMapper;
  private final CompanyQuestionAndAnswerRequestMapper companyQuestionAndAnswerRequestMapper;
  private final SpecialRequestsMapper specialRequestsMapper;
  private final UpdateReservationOverrideReasonsRequestMapper updateReservationOverrideReasonsRequestMapper;
  private final UpdateReservationCcAgentIdRequestMapper updateReservationCcAgentIdRequestMapper;
  private final DepositsResponseMapper depositsResponseMapper;
  private final CancellationPoliciesResponseMapper cancellationPoliciesResponseMapper;
  private final UpdateCancellationRequestMapper updateCancellationRequestMapper;
  private final MarketingPreferencesResponseMapper marketingPreferencesResponseMapper;
  private final CopyReservationsRequestMapper copyReservationsRequestMapper;
  private final CopyReservationsResponseMapper copyReservationsResponseMapper;
  private final BookingAllowancesResponseMapper bookingAllowancesResponseMapper;
  private final ConfirmAmendRequestMapper confirmAmendRequestMapper;
  private final ConfirmAmendForSingleRequestMapper confirmAmendForSingleRequestMapper;
  private final BookerDetailsCnpRequestMapper bookerDetailsCnpRequestMapper;
  private final UpdateBookerEmailRequestMapper updateBookerEmailRequestMapper;
  private final MemosMapper memosMapper;
  private final AttachReservationProfileRequestMapper attachReservationProfileRequestMapper;
  private final UpdateCustomReferenceNumberRequestMapper updateCustomReferenceNumberRequestMapper;
  private final ReservationFileAttachmentRequestMapper reservationFileAttachmentRequestMapper;
  private final PreCheckInRequestMapper preCheckInRequestMapper;
  private final LinkReservationToLeisureCustomerRequestMapper linkReservationToLeisureCustomerRequestMapper;
  private final ReservationPreferencesRequestMapper reservationPreferencesRequestMapper;
  private final ReservationAmountsMapper reservationAmountsMapper;
  private final ReservationLightweightResponseMapper reservationLightweightResponseMapper;

  @Operation(summary = "createReservation")
  @ApiResponse(responseCode = "201", description = "Success", content = {
      @Content(mediaType = "application/json",
          schema = @Schema(implementation = ReservationResponseDto.class))})
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
      value = "/reservations", produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<ReservationResponseDto> createReservation(
      @RequestBody @Valid ReservationRequestDto createReservationRequestDto) {

    final var createReservationRequest =
        reservationRequestMapper.toReservationRequestModel(createReservationRequestDto);
    final var reservationResponse =
        hotelReservationPort.processCreateReservation(createReservationRequest);
    final var reservationResponseDto =
        reservationResponseMapper.toReservationResponseDto(reservationResponse);

    return ResponseEntity.status(HttpStatus.CREATED).body(reservationResponseDto);
  }

  @Operation(summary = "Get reservations by external reference ids")
  @ApiResponse(responseCode = "200", description = "Success", content = {
      @Content(mediaType = "application/json",
          schema = @Schema(implementation = ReservationsDetailsResponseDto.class))})
  @ApiResponse(responseCode = "400", description = "Error Occurred ", content = {
      @Content(mediaType = "application/json",
          schema = @Schema(implementation = OhipBadRequestException.class))})
  @ApiResponse(responseCode = "404", description = "Resource Not Found", content = {
      @Content(mediaType = "application/json",
          schema = @Schema(implementation = OhipNotFoundException.class))})
  @ApiResponse(responseCode = "500", description = "Internal Server Error", content = {
      @Content(mediaType = "application/json",
          schema = @Schema(implementation = OhipInternalException.class))})
  @GetMapping(value = "/reservations", produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<ReservationsDetailsResponseDto> getReservationsByExternalReferenceIds(
      @RequestParam("hotelId") String hotelId,
      @RequestParam("externalReferenceIds") List<String> externalReferenceIds,
      @RequestParam(value = "limit", defaultValue = "20") int limit,
      @RequestParam(value = "offset", defaultValue = "0") int offset) {

    final var reservationsDetailsResponse = hotelReservationPort
        .getReservationsByExternalReferenceIds(hotelId, externalReferenceIds, limit, offset);
    final var reservationsDetailsResponseDto = reservationResponseMapper
        .toReservationsDetailsResponseDto(
            reservationsDetailsResponse);
    return ResponseEntity.status(HttpStatus.OK).body(reservationsDetailsResponseDto);
  }

  @Operation(summary = "confirmReservation")
  @ApiResponse(responseCode = "200", description = "Success", content = {
      @Content(mediaType = "application/json",
          schema = @Schema(implementation = ConfirmReservationResponseDto.class))})
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
      value = "/reservations/confirm", produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<ConfirmReservationResponseDto> confirmReservation(
      @RequestBody @Valid ConfirmReservationRequestDto confirmReservationRequestDto) {

    final var confirmReservationRequest =
        confirmReservationRequestMapper
            .toConfirmReservationRequestModel(confirmReservationRequestDto);
    final var reservationResponse =
        hotelReservationPort.confirmReservation(confirmReservationRequest);
    final var reservationResponseDto =
        confirmReservationResponseMapper.toConfirmReservationResponseDto(reservationResponse);

    return ResponseEntity.status(HttpStatus.OK).body(reservationResponseDto);
  }

  @Operation(summary = "Get reservations by ids")
  @ApiResponse(responseCode = "200", description = "Success", content = {
      @Content(mediaType = "application/json",
          schema = @Schema(implementation = ReservationByBasketRefResponseDto.class))})
  @ApiResponse(responseCode = "400", description = "Error Occurred ", content = {
      @Content(mediaType = "application/json",
          schema = @Schema(implementation = OhipBadRequestException.class))})
  @ApiResponse(responseCode = "404", description = "Resource Not Found", content = {
      @Content(mediaType = "application/json",
          schema = @Schema(implementation = OhipNotFoundException.class))})
  @ApiResponse(responseCode = "500", description = "Internal Server Error", content = {
      @Content(mediaType = "application/json",
          schema = @Schema(implementation = OhipInternalException.class))})
  @GetMapping(value = "/reservations/basket", produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<ReservationByBasketRefResponseDto> getReservationsByIds(
      @RequestParam("hotelId") String hotelId,
      @RequestParam("reservationIds") Set<String> reservationIds,
      @RequestParam(value = "priceBreakdownNeeded", defaultValue = "false") Boolean priceBreakdownNeeded,
      @RequestParam(value = "rateInfoNeeded", defaultValue = "true") boolean rateInfoNeeded,
      @RequestParam(value = "operaUiCreatedRsv", defaultValue = "false") Boolean operaUiCreatedRsv) {

    final var reservationByBasketRefResponse = hotelReservationPort.getReservationsByIds(hotelId,
        reservationIds, priceBreakdownNeeded, rateInfoNeeded, operaUiCreatedRsv);
    var reservationByBasketRefResponseDto = reservationByBasketRefResponseMapper.toDto(
        reservationByBasketRefResponse);
    return ResponseEntity.status(HttpStatus.OK).body(reservationByBasketRefResponseDto);
  }

  @Operation(summary = "Get reservations by ids")
  @ApiResponse(responseCode = "200", description = "Success", content = {
      @Content(mediaType = "application/json",
          schema = @Schema(implementation = ReservationLightweightResponseDto.class))})
  @ApiResponse(responseCode = "400", description = "Error Occurred ", content = {
      @Content(mediaType = "application/json",
          schema = @Schema(implementation = OhipBadRequestException.class))})
  @ApiResponse(responseCode = "404", description = "Resource Not Found", content = {
      @Content(mediaType = "application/json",
          schema = @Schema(implementation = OhipNotFoundException.class))})
  @ApiResponse(responseCode = "500", description = "Internal Server Error", content = {
      @Content(mediaType = "application/json",
          schema = @Schema(implementation = OhipInternalException.class))})
  @GetMapping(value = "/reservations/ids", produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<ReservationLightweightResponseDto> getLightweightReservationsByIds(
      @RequestParam("hotelId") String hotelId,
      @RequestParam("reservationIds") Set<String> reservationIds) {

    final var reservationResponse = hotelReservationPort.getReservationsByIds(hotelId, reservationIds);
    var reservationResponseDto = reservationLightweightResponseMapper.toDto(
        reservationResponse);
    return ResponseEntity.status(HttpStatus.OK).body(reservationResponseDto);
  }

  @Operation(summary = "updateReservationPackages")
  @ApiResponse(responseCode = "200", description = "Success", content = {
      @Content(mediaType = "application/json")})
  @ApiResponse(responseCode = "400", description = "Error Occurred ", content = {
      @Content(mediaType = "application/json",
          schema = @Schema(implementation = OhipBadRequestException.class))})
  @ApiResponse(responseCode = "404", description = "Resource Not Found", content = {
      @Content(mediaType = "application/json",
          schema = @Schema(implementation = OhipNotFoundException.class))})
  @ApiResponse(responseCode = "500", description = "Internal Server Error", content = {
      @Content(mediaType = "application/json",
          schema = @Schema(implementation = OhipInternalException.class))})
  @PutMapping(value = "reservations/ancillaries", produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<ReservationPackagesRequestDto> savePackages(
      @RequestBody @Valid ReservationPackagesRequestDto reservationPackagesRequestDto) {

    final var updateReservationRequest = updateReservationRequestMapper.toModel(
        reservationPackagesRequestDto);

    hotelReservationPort.updateReservationPackages(updateReservationRequest);

    return new ResponseEntity<>(HttpStatus.OK);
  }

  @Operation(summary = "Update reservations with Reference & Custom questions and answers")
  @ApiResponse(responseCode = "200", description = "Success", content = {
      @Content(mediaType = "application/json")})
  @ApiResponse(responseCode = "400", description = "Error Occurred ", content = {
      @Content(mediaType = "application/json",
          schema =
          @Schema(implementation
              = OhipBadRequestException.class))})
  @ApiResponse(responseCode = "404", description = "Resource Not Found", content = {
      @Content(mediaType = "application/json",
          schema = @Schema(implementation = OhipNotFoundException.class))})
  @ApiResponse(responseCode = "500", description = "Internal Server Error", content = {
      @Content(mediaType = "application/json",
          schema = @Schema(implementation = OhipInternalException.class))})
  @PutMapping(value = "/reservations/questions-and-answers", produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<Void> updateQuestionsAndAnswers(
      @RequestBody @Valid CompanyQuestionAndAnswerDetailsRequestDto companyQuestionAndAnswerDetailsRequestDto) {

    var companyQuestionAndAnswerDetailsRequest = companyQuestionAndAnswerRequestMapper
        .toModel(companyQuestionAndAnswerDetailsRequestDto);

    hotelReservationPort.updateCompanyQuestionAndAnswerDetails(
        companyQuestionAndAnswerDetailsRequest);

    return ResponseEntity.status(HttpStatus.OK).build();
  }

  @Operation(summary = "Update reservations with business specific items")
  @ApiResponse(responseCode = "200", description = "Success", content = {
      @Content(mediaType = "application/json")})
  @ApiResponse(responseCode = "400", description = "Error Occurred ", content = {
      @Content(mediaType = "application/json",
          schema = @Schema(implementation = OhipBadRequestException.class))})
  @ApiResponse(responseCode = "404", description = "Resource Not Found", content = {
      @Content(mediaType = "application/json",
          schema = @Schema(implementation = OhipNotFoundException.class))})
  @ApiResponse(responseCode = "500", description = "Internal Server Error", content = {
      @Content(mediaType = "application/json",
          schema = @Schema(implementation = OhipInternalException.class))})
  @PutMapping(value = "/reservations/business", produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<Void> updateBusinessItems(
      @RequestBody @Valid BusinessItemsRequestDto businessItemsRequestDto) {

    var businessItemsRequest = businessItemsRequestMapper.toModel(businessItemsRequestDto);

    hotelReservationPort.updateBusinessItems(businessItemsRequest);

    return ResponseEntity.status(HttpStatus.OK).build();
  }

  @Operation(summary = "Update customReferenceNumber")
  @ApiResponse(responseCode = "200", description = "Success", content = {
      @Content(mediaType = "application/json")})
  @ApiResponse(responseCode = "400", description = "Error Occurred ", content = {
      @Content(mediaType = "application/json",
          schema = @Schema(implementation = OhipBadRequestException.class))})
  @ApiResponse(responseCode = "404", description = "Resource Not Found", content = {
      @Content(mediaType = "application/json",
          schema = @Schema(implementation = OhipNotFoundException.class))})
  @ApiResponse(responseCode = "500", description = "Internal Server Error", content = {
      @Content(mediaType = "application/json",
          schema = @Schema(implementation = OhipInternalException.class))})
  @PutMapping(value = "/reservations/customReferenceNumber", produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<Void> updateCustomReferenceNumber(
      @RequestBody @Valid UpdateCustomReferenceNumberRequestDto updateCustomReferenceNumberRequestDto) {

    var model = updateCustomReferenceNumberRequestMapper.toModel(updateCustomReferenceNumberRequestDto);

    hotelReservationPort.updateCustomReferenceNumber(model);

    return ResponseEntity.status(HttpStatus.OK).build();
  }

  @Operation(summary = "Update reservations details")
  @ApiResponse(responseCode = "200", description = "Success", content = {
      @Content(mediaType = "application/json")})
  @ApiResponse(responseCode = "400", description = "Error Occurred ", content = {
      @Content(mediaType = "application/json",
          schema = @Schema(implementation = OhipBadRequestException.class))})
  @ApiResponse(responseCode = "404", description = "Resource Not Found", content = {
      @Content(mediaType = "application/json",
          schema = @Schema(implementation = OhipNotFoundException.class))})
  @ApiResponse(responseCode = "500", description = "Internal Server Error", content = {
      @Content(mediaType = "application/json",
          schema = @Schema(implementation = OhipInternalException.class))})
  @PutMapping(value = "/reservations/update", produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<ConfirmReservationResponseDto> updateReservationSingleCall(
      @Valid @RequestBody UpdateReservationSingleCallRequestDto updateReservationRequestDto) {

    var businessItemsRequest = businessItemsRequestMapper.toModel(
        updateReservationRequestDto.getBusinessItem());
    var specialRequests = specialRequestsMapper.toModel(
        updateReservationRequestDto.getSpecialRequests());
    var guestReservationRequest = reservationGuestRequestMapper.toModel(
        updateReservationRequestDto.getReservationGuestDetails());
    var updateReservationRequest = updateReservationRequestMapper.toModel(
        updateReservationRequestDto.getReservationPackages());
    var confirmReservationRequest = confirmReservationRequestMapper.toConfirmReservationRequestModel(
        updateReservationRequestDto.getPaymentDetails());

    final var reservationResponse = hotelReservationPort.updateReservationSingleCall(
        businessItemsRequest, specialRequests,
        guestReservationRequest, updateReservationRequest, confirmReservationRequest);
    final var reservationResponseDto =
        confirmReservationResponseMapper.toConfirmReservationResponseDto(reservationResponse);
    return ResponseEntity.status(HttpStatus.OK).body(reservationResponseDto);
  }

  @Operation(summary = "Update reservations special requests")
  @ApiResponse(responseCode = "200", description = "Success", content = {
      @Content(mediaType = "application/json")})
  @ApiResponse(responseCode = "400", description = "Error Occurred ", content = {
      @Content(mediaType = "application/json",
          schema = @Schema(implementation = OhipBadRequestException.class))})
  @ApiResponse(responseCode = "404", description = "Resource Not Found", content = {
      @Content(mediaType = "application/json",
          schema = @Schema(implementation = OhipNotFoundException.class))})
  @ApiResponse(responseCode = "500", description = "Internal Server Error", content = {
      @Content(mediaType = "application/json",
          schema = @Schema(implementation = OhipInternalException.class))})
  @PutMapping(
      value = "/reservations/special-requests", produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<Void> updateReservationsSpecialRequests(
      @RequestBody @Valid SpecialRequestsDto specialRequestsDto) {

    var specialRequests = specialRequestsMapper.toModel(specialRequestsDto);

    hotelReservationPort.updateSpecialRequests(specialRequests);

    return ResponseEntity.status(HttpStatus.OK).build();
  }

  @Operation(summary = "Update reservations discount")
  @ApiResponse(responseCode = "200", description = "Success", content = {
      @Content(mediaType = "application/json")})
  @ApiResponse(responseCode = "400", description = "Error Occurred ", content = {
      @Content(mediaType = "application/json",
          schema = @Schema(implementation = OhipBadRequestException.class))})
  @ApiResponse(responseCode = "404", description = "Resource Not Found", content = {
      @Content(mediaType = "application/json",
          schema = @Schema(implementation = OhipNotFoundException.class))})
  @ApiResponse(responseCode = "500", description = "Internal Server Error", content = {
      @Content(mediaType = "application/json",
          schema = @Schema(implementation = OhipInternalException.class))})
  @PutMapping(value = "reservations/discount", produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<Void> updateDiscount(
      @RequestBody @Valid UpdateDiscountRequestDto updateDiscountRequestDto) {

    var updateDiscountRequest = updateDiscountRequestMapper.toModel(updateDiscountRequestDto);

    hotelReservationPort.updateDiscount(updateDiscountRequest);

    return ResponseEntity.status(HttpStatus.OK).build();
  }

  @Operation(summary = "Create a reservation guest")
  @ApiResponse(responseCode = "201", description = "Success", content = {
      @Content(mediaType = "application/json", schema = @Schema(implementation = ReservationGuestResponseDto.class))})
  @ApiResponse(responseCode = "400", description = "Error Occurred ", content = {
      @Content(mediaType = "application/json",
          schema = @Schema(implementation = OhipBadRequestException.class))})
  @ApiResponse(responseCode = "404", description = "Resource Not Found", content = {
      @Content(mediaType = "application/json",
          schema = @Schema(implementation = OhipNotFoundException.class))})
  @ApiResponse(responseCode = "500", description = "Internal Server Error", content = {
      @Content(mediaType = "application/json",
          schema = @Schema(implementation = OhipInternalException.class))})
  @PostMapping(value = "/reservations/guests", produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<ReservationGuestResponseDto> createReservationGuest(
      @RequestBody @Valid ReservationGuestRequestDto reservationGuestRequestDto) {

    final ReservationGuestRequest guestReservationRequest =
        reservationGuestRequestMapper.toModel(reservationGuestRequestDto);
    final ReservationGuestResponse reservationGuestResponse =
        hotelReservationPort.createReservationGuest(guestReservationRequest);
    final var guestReservationResponseDto =
        reservationGuestResponseMapper.toDto(reservationGuestResponse);
    return ResponseEntity.status(HttpStatus.CREATED).body(guestReservationResponseDto);
  }

  @Operation(summary = "Attach a profile to reservations")
  @ApiResponse(responseCode = "200", description = "Success", content = {
      @Content(mediaType = "application/json")})
  @ApiResponse(responseCode = "400", description = "Error Occurred ", content = {
      @Content(mediaType = "application/json",
          schema = @Schema(implementation = OhipBadRequestException.class))})
  @ApiResponse(responseCode = "404", description = "Resource Not Found", content = {
      @Content(mediaType = "application/json",
          schema = @Schema(implementation = OhipNotFoundException.class))})
  @ApiResponse(responseCode = "500", description = "Internal Server Error", content = {
      @Content(mediaType = "application/json",
          schema = @Schema(implementation = OhipInternalException.class))})
  @PostMapping(value = "/reservations/profiles", produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<Void> attachProfileToReservations(
      @RequestBody @Valid AttachReservationProfileRequestDto attachReservationProfileRequestDto) {

    final AttachReservationProfileRequest attachReservationProfileRequest =
        attachReservationProfileRequestMapper.toModel(attachReservationProfileRequestDto);

    hotelReservationPort.attachProfileToReservations(attachReservationProfileRequest);

    return new ResponseEntity<>(HttpStatus.OK);
  }

  @Operation(summary = "Upgrade a reservation to a different rate plan")
  @ApiResponse(responseCode = "201", description = "Success", content = {
      @Content(mediaType = "application/json")})
  @ApiResponse(responseCode = "400", description = "Error Occurred ", content = {
      @Content(mediaType = "application/json",
          schema = @Schema(implementation = OhipBadRequestException.class))})
  @ApiResponse(responseCode = "404", description = "Resource Not Found", content = {
      @Content(mediaType = "application/json",
          schema = @Schema(implementation = OhipNotFoundException.class))})
  @ApiResponse(responseCode = "500", description = "Internal Server Error", content = {
      @Content(mediaType = "application/json",
          schema = @Schema(implementation = OhipInternalException.class))})
  @PutMapping(value =
      "/reservations/rate-code", produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<RatePlanChangeRequestDto> updateReservationRatePlanCode(
      @RequestBody @Valid RatePlanChangeRequestDto reservationRequest) {

    final var ratePlanChangeRequest = reservationRateChangeRequestMapper
        .toModel(reservationRequest);
    hotelReservationPort.changeReservationRatePlan(ratePlanChangeRequest);

    return new ResponseEntity<>(HttpStatus.OK);
  }

  @Operation(summary = "Upgrade a reservation to a different room type")
  @ApiResponse(responseCode = "201", description = "Success", content = {
      @Content(mediaType = "application/json")})
  @ApiResponse(responseCode = "400", description = "Error Occurred ", content = {
      @Content(mediaType = "application/json",
          schema = @Schema(implementation = OhipBadRequestException.class))})
  @ApiResponse(responseCode = "404", description = "Resource Not Found", content = {
      @Content(mediaType = "application/json",
          schema = @Schema(implementation = OhipNotFoundException.class))})
  @ApiResponse(responseCode = "500", description = "Internal Server Error", content = {
      @Content(mediaType = "application/json",
          schema = @Schema(implementation = OhipInternalException.class))})
  @PutMapping(value =
      "/reservations/roomTypeUpdate", produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<RoomTypeChangeResponseDto> updateReservationRoomType(
      @RequestBody @Valid RoomTypeChangeRequestDto reservationRequest) {

    final var roomTypeChangeRequest = reservationRoomTypeChangeRequestMapper
        .toModel(reservationRequest);
    hotelReservationPort.changeReservationRoomType(roomTypeChangeRequest);

    return new ResponseEntity<>(
        new RoomTypeChangeResponseDto(reservationRequest.getBasketReferenceId()), HttpStatus.OK);
  }

  @Operation(summary = "Get reservations packages by reservations ids.")
  @ApiResponse(responseCode = "200", description = "Success", content = {
      @Content(mediaType = "application/json",
          schema = @Schema(implementation = ReservationsPackagesResponseDto.class))})
  @ApiResponse(responseCode = "400", description = "Error Occurred ", content = {
      @Content(mediaType = "application/json",
          schema = @Schema(implementation = OhipBadRequestException.class))})
  @ApiResponse(responseCode = "404", description = "Resource Not Found", content = {
      @Content(mediaType = "application/json",
          schema = @Schema(implementation = OhipNotFoundException.class))})
  @ApiResponse(responseCode = "500", description = "Internal Server Error", content = {
      @Content(mediaType = "application/json",
          schema = @Schema(implementation = OhipInternalException.class))})
  @GetMapping(value = "/reservations/ancillaries", produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<ReservationsPackagesResponseDto> getReservationsPackagesByIds(
      @RequestParam("hotelId") String hotelId,
      @RequestParam("reservationIds") Set<String> reservationIds,
      @RequestParam(value = "mealInclusiveRate", defaultValue = "false") boolean mealInclusiveRate) {

    final var reservationsPackagesByReservationsIdsResponse =
        mealInclusiveRate ? hotelReservationPort
            .getReservationsPackagesMealInclusiveRateByReservationsIds(hotelId, reservationIds)
            : hotelReservationPort.getReservationsPackagesByReservationsIds(
                hotelId, reservationIds);
    var reservationsPackages = reservationsPackagesResponseMapper
        .toDto(reservationsPackagesByReservationsIdsResponse);

    return ResponseEntity.status(HttpStatus.OK).body(reservationsPackages);
  }

  @Operation(summary = "Cancels a reservation")
  @ApiResponse(responseCode = "200", description = "Success", content = {
      @Content(mediaType = "application/json", schema = @Schema(implementation = CancelReservationResponseDto.class))})
  @ApiResponse(responseCode = "400", description = "Error Occurred ", content = {
      @Content(mediaType = "application/json",
          schema = @Schema(implementation = OhipBadRequestException.class))})
  @ApiResponse(responseCode = "404", description = "Resource Not Found", content = {
      @Content(mediaType = "application/json",
          schema = @Schema(implementation = OhipNotFoundException.class))})
  @ApiResponse(responseCode = "500", description = "Internal Server Error", content = {
      @Content(mediaType = "application/json",
          schema = @Schema(implementation = OhipInternalException.class))})
  @PostMapping(value = "/reservations/cancellations", produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<CancelReservationResponseDto> cancelReservation(
      @RequestBody @Valid CancelReservationRequestDto cancelReservationRequestDto) {

    final CancelReservationRequest cancelReservationRequest =
        cancelReservationRequestMapper.toModel(cancelReservationRequestDto);
    final CancelReservationResponse cancelReservationResponse =
        hotelReservationPort.cancelReservation(cancelReservationRequest);
    final var cancelReservationResponseDto =
        cancelReservationResponseMapper.toDto(cancelReservationResponse);
    return ResponseEntity.status(HttpStatus.OK).body(cancelReservationResponseDto);
  }


  @Operation(summary = "Get cancel information by reservation id.")
  @ApiResponse(responseCode = "200", description = "Success", content = {
      @Content(mediaType = "application/json",
          schema = @Schema(implementation = CancelInformationResponseDto.class))})
  @ApiResponse(responseCode = "400", description = "Error Occurred ", content = {
      @Content(mediaType = "application/json",
          schema = @Schema(implementation = OhipBadRequestException.class))})
  @ApiResponse(responseCode = "404", description = "Resource Not Found", content = {
      @Content(mediaType = "application/json",
          schema = @Schema(implementation = OhipNotFoundException.class))})
  @ApiResponse(responseCode = "500", description = "Internal Server Error", content = {
      @Content(mediaType = "application/json",
          schema = @Schema(implementation = OhipInternalException.class))})
  @GetMapping(value = "/reservations/cancel", produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<CancelInformationResponseDto> getCancelInformation(
      @RequestParam("hotelId") String hotelId,
      @RequestParam("reservationIds") Set<String> reservationIds,
      @RequestParam("userDateTime") String userDateTime) {

    final var cancelInformationResponse = hotelReservationPort
        .getCancelInformation(hotelId, reservationIds, userDateTime);
    var cancelInformationResponseDto = cancelResponseMapper
        .toDto(cancelInformationResponse);

    return ResponseEntity.status(HttpStatus.OK).body(cancelInformationResponseDto);
  }

  @Operation(summary = "Update cancellation policy")
  @ApiResponse(responseCode = "200", description = "Success", content = {
      @Content(mediaType = "application/json")})
  @ApiResponse(responseCode = "400", description = "Error Occurred ", content = {
      @Content(mediaType = "application/json",
          schema = @Schema(implementation = OhipBadRequestException.class))})
  @ApiResponse(responseCode = "404", description = "Resource Not Found", content = {
      @Content(mediaType = "application/json",
          schema = @Schema(implementation = OhipNotFoundException.class))})
  @ApiResponse(responseCode = "500", description = "Internal Server Error", content = {
      @Content(mediaType = "application/json",
          schema = @Schema(implementation = OhipInternalException.class))})
  @PutMapping(value = "reservations/cancel", produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<Void> updateCancellationPolicy(
      @RequestBody @Valid UpdateCancellationPolicyRequestDto requestDto) {
    var updateCancellationRequest = updateCancellationRequestMapper.toModel(requestDto);

    hotelReservationPort.updateCancellationPolicy(updateCancellationRequest);

    return ResponseEntity.status(HttpStatus.OK).build();
  }

  @Operation(summary = "Update cancellation policies")
  @ApiResponse(responseCode = "200", description = "Success", content = {
      @Content(mediaType = "application/json")})
  @ApiResponse(responseCode = "400", description = "Error Occurred ", content = {
      @Content(mediaType = "application/json",
          schema = @Schema(implementation = OhipBadRequestException.class))})
  @ApiResponse(responseCode = "404", description = "Resource Not Found", content = {
      @Content(mediaType = "application/json",
          schema = @Schema(implementation = OhipNotFoundException.class))})
  @ApiResponse(responseCode = "500", description = "Internal Server Error", content = {
      @Content(mediaType = "application/json",
          schema = @Schema(implementation = OhipInternalException.class))})
  @PutMapping(value = "reservations/cancel-policies", produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<Void> updateCancellationPolicies(
      @RequestBody @Valid UpdateCancellationPoliciesRequestDto requestDto) {

    var updateCancellationPoliciesRequest = updateCancellationRequestMapper.toModel(requestDto);
    hotelReservationPort.updateCancellationPolicies(updateCancellationPoliciesRequest);
    return ResponseEntity.status(HttpStatus.OK).build();
  }

  @Operation(summary = "Get reservations by external reference id")
  @ApiResponse(responseCode = "200", description = "Success", content = {
      @Content(mediaType = "application/json",
          schema = @Schema(implementation = ReservationDetailsEnhancedDto.class))})
  @ApiResponse(responseCode = "400", description = "Error Occurred ", content = {
      @Content(mediaType = "application/json",
          schema = @Schema(implementation = OhipBadRequestException.class))})
  @ApiResponse(responseCode = "404", description = "Resource Not Found", content = {
      @Content(mediaType = "application/json",
          schema = @Schema(implementation = OhipNotFoundException.class))})
  @ApiResponse(responseCode = "500", description = "Internal Server Error", content = {
      @Content(mediaType = "application/json",
          schema = @Schema(implementation = OhipInternalException.class))})
  @GetMapping(value = "/reservations/external", produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<ReservationDetailsEnhancedDto> getReservationsByExternalId(
      @RequestParam("externalReferenceId") String externalReferenceId) {

    final var reservationDetailsEnhanced =
        hotelReservationPort.getReservationsByExternalRefId(externalReferenceId);
    if (Objects.isNull(reservationDetailsEnhanced)) {
      return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
    }
    var reservationDetailsEnhancedDto =
        reservationDetailsEnhancedResponseMapper.toDto(reservationDetailsEnhanced);
    return ResponseEntity.status(HttpStatus.OK).body(reservationDetailsEnhancedDto);
  }


  @Operation(summary = "Get reservations by reservation id")
  @ApiResponse(responseCode = "200", description = "Success", content = {
      @Content(mediaType = "application/json",
          schema = @Schema(implementation = ReservationIdDetailsDto.class))})
  @ApiResponse(responseCode = "400", description = "Error Occurred ", content = {
      @Content(mediaType = "application/json",
          schema = @Schema(implementation = OhipBadRequestException.class))})
  @ApiResponse(responseCode = "404", description = "Resource Not Found", content = {
      @Content(mediaType = "application/json",
          schema = @Schema(implementation = OhipNotFoundException.class))})
  @ApiResponse(responseCode = "500", description = "Internal Server Error", content = {
      @Content(mediaType = "application/json",
          schema = @Schema(implementation = OhipInternalException.class))})
  @GetMapping(value = "/reservation/reservationId", produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<ReservationIdDetailsDto> getReservationsByReservationId(
      @RequestParam("hotelId") String hotelId,
      @RequestParam("reservationId") String reservationId) {

    final var reservationIdDetails =
        hotelReservationPort.getReservationsByReservationId(hotelId, reservationId);
    if (Objects.isNull(reservationIdDetails)) {
      return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
    }
    var reservationIdDetailsDto =
        reservationDetailsResponseMapper.toDto(reservationIdDetails);
    return ResponseEntity.status(HttpStatus.OK).body(reservationIdDetailsDto);
  }

  @Operation(summary = "Search reservations")
  @ApiResponse(responseCode = "200", description = "Success", content = {
      @Content(mediaType = "application/json",
          schema = @Schema(implementation = SearchBookingsResponseDto.class))})
  @ApiResponse(responseCode = "400", description = "Error Occurred ", content = {
      @Content(mediaType = "application/json",
          schema = @Schema(implementation = OhipBadRequestException.class))})
  @ApiResponse(responseCode = "404", description = "Resource Not Found", content = {
      @Content(mediaType = "application/json",
          schema = @Schema(implementation = OhipNotFoundException.class))})
  @ApiResponse(responseCode = "500", description = "Internal Server Error", content = {
      @Content(mediaType = "application/json",
          schema = @Schema(implementation = OhipInternalException.class))})
  @GetMapping(value = "/reservations/search", produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<SearchBookingsResponseDto> searchReservations(
      @ParameterObject SearchBookingsRequestDto searchBookingsRequestDto) {

    final var domainBookingSearchCritera = searchBookingsRequestMapper.toBookingSearchCriteriaModel(
        searchBookingsRequestDto);
    final var bookingSearchResponse =
        hotelReservationPort.searchBookings(domainBookingSearchCritera);

    final var bookingSearchResponseDto = searchBookingsResponseMapper
        .toSearchBookingsResponseDto(bookingSearchResponse);
    return ResponseEntity.status(HttpStatus.OK).body(bookingSearchResponseDto);
  }

  @Operation(summary = "Update reason for stay")
  @ApiResponse(responseCode = "200", description = "Success", content = {
      @Content(mediaType = "application/json",
          schema = @Schema(implementation = UpdateReasonForStayResponseDto.class))})
  @ApiResponse(responseCode = "400", description = "Error Occurred ", content = {
      @Content(mediaType = "application/json",
          schema = @Schema(implementation = OhipBadRequestException.class))})
  @ApiResponse(responseCode = "404", description = "Resource Not Found", content = {
      @Content(mediaType = "application/json",
          schema = @Schema(implementation = OhipNotFoundException.class))})
  @ApiResponse(responseCode = "500", description = "Internal Server Error", content = {
      @Content(mediaType = "application/json",
          schema = @Schema(implementation = OhipInternalException.class))})
  @PutMapping(value = "/reservations/reasonForStay", produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<UpdateReasonForStayResponseDto> updateReasonForStay(
      @RequestBody @Valid UpdateReasonForStayRequestDto updateReasonForStayRequestDto) {

    final var updateReasonForStayRequest = updateReasonForStayRequestMapper.toModel(
        updateReasonForStayRequestDto);

    final var updateReasonForStayResponse = hotelReservationPort.updateReasonForStay(
        updateReasonForStayRequest);

    final var updateReasonForStayResponseDto = updateReasonForStayResponseMapper.toDto(
        updateReasonForStayResponse);

    return ResponseEntity.ok(updateReasonForStayResponseDto);

  }

  @Operation(summary = "Update reservation override reasons")
  @ApiResponse(responseCode = "200", description = "Success", content = {
      @Content(mediaType = "application/json")})
  @ApiResponse(responseCode = "400", description = "Error Occurred ", content = {
      @Content(mediaType = "application/json",
          schema = @Schema(implementation = OhipBadRequestException.class))})
  @ApiResponse(responseCode = "404", description = "Resource Not Found", content = {
      @Content(mediaType = "application/json",
          schema = @Schema(implementation = OhipNotFoundException.class))})
  @ApiResponse(responseCode = "500", description = "Internal Server Error", content = {
      @Content(mediaType = "application/json",
          schema = @Schema(implementation = OhipInternalException.class))})
  @PutMapping(value = "/reservations/overrideReasons", produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<Void> updateReservationOverrideReasons(
      @RequestBody @Valid UpdateReservationOverrideReasonsRequestDto updateReservationOverrideReasonsRequestDto) {

    final var updateReservationOverrideReasons =
        updateReservationOverrideReasonsRequestMapper.toModel(
            updateReservationOverrideReasonsRequestDto);

    hotelReservationPort.updateReservationOverrideReasons(updateReservationOverrideReasons);

    return ResponseEntity.status(HttpStatus.OK).build();
  }

  @Operation(summary = "Update reservation CC agent ID")
  @ApiResponse(responseCode = "200", description = "Success", content = {
      @Content(mediaType = "application/json")})
  @ApiResponse(responseCode = "400", description = "Error Occurred ", content = {
      @Content(mediaType = "application/json",
          schema = @Schema(implementation = OhipBadRequestException.class))})
  @ApiResponse(responseCode = "404", description = "Resource Not Found", content = {
      @Content(mediaType = "application/json",
          schema = @Schema(implementation = OhipNotFoundException.class))})
  @ApiResponse(responseCode = "500", description = "Internal Server Error", content = {
      @Content(mediaType = "application/json",
          schema = @Schema(implementation = OhipInternalException.class))})
  @PutMapping(value = "/reservations/ccAgentId", produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<Void> updateReservationCcAgentId(
      @RequestBody @Valid UpdateReservationCcAgentIdRequestDto updateReservationCcAgentIdRequestDto) {

    final var updateReservationCcAgentId = updateReservationCcAgentIdRequestMapper.toModel(
        updateReservationCcAgentIdRequestDto);

    hotelReservationPort.updateReservationCcAgentId(updateReservationCcAgentId);

    return ResponseEntity.status(HttpStatus.OK).build();
  }

  @Operation(summary = "copyReservations")
  @ApiResponse(responseCode = "200", description = "Success", content = {
      @Content(mediaType = "application/json",
          schema = @Schema(implementation = CopyReservationsResponseDto.class))})
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
      value = "/reservations/copy", produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<CopyReservationsResponseDto> copyReservations(
      @RequestBody @Valid CopyReservationsRequestDto copyReservationsRequestDto) {

    final var copyReservationsRequest =
        copyReservationsRequestMapper.toModel(copyReservationsRequestDto);
    final var reservations = hotelReservationPort.copyReservations(copyReservationsRequest);
    final var copyReservationsResponseDto = copyReservationsResponseMapper.toDto(reservations);

    return ResponseEntity.status(HttpStatus.OK).body(copyReservationsResponseDto);
  }

  @Operation(summary = "Get deposits information by reservation id.")
  @ApiResponse(responseCode = "200", description = "Success", content = {
      @Content(mediaType = "application/json",
          schema = @Schema(implementation = DepositsResponseDto.class))})
  @ApiResponse(responseCode = "400", description = "Error Occurred ", content = {
      @Content(mediaType = "application/json",
          schema = @Schema(implementation = OhipBadRequestException.class))})
  @ApiResponse(responseCode = "404", description = "Resource Not Found", content = {
      @Content(mediaType = "application/json",
          schema = @Schema(implementation = OhipNotFoundException.class))})
  @ApiResponse(responseCode = "500", description = "Internal Server Error", content = {
      @Content(mediaType = "application/json",
          schema = @Schema(implementation = OhipInternalException.class))})
  @GetMapping(value = "/reservations/deposits", produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<DepositsResponseDto> getDepositsForReservationId(
      @RequestParam("hotelId") String hotelId,
      @RequestParam("reservationId") String reservationId) {
    final var depositsResponse = hotelReservationPort.getDepositsForReservationId(hotelId, reservationId);
    final var depositsResponseDto = depositsResponseMapper.toDto(depositsResponse);
    return ResponseEntity.status(HttpStatus.OK).body(depositsResponseDto);
  }


  @Operation(summary = "Get cancellation policies by external reference id or rate plane")
  @ApiResponse(responseCode = "200", description = "Success", content = {
      @Content(mediaType = "application/json",
          schema = @Schema(implementation = CancellationPoliciesResponseDto.class))})
  @ApiResponse(responseCode = "400", description = "Error Occurred ", content = {
      @Content(mediaType = "application/json",
          schema = @Schema(implementation = OhipBadRequestException.class))})
  @ApiResponse(responseCode = "404", description = "Resource Not Found", content = {
      @Content(mediaType = "application/json",
          schema = @Schema(implementation = OhipNotFoundException.class))})
  @ApiResponse(responseCode = "500", description = "Internal Server Error", content = {
      @Content(mediaType = "application/json",
          schema = @Schema(implementation = OhipInternalException.class))})
  @GetMapping(value = "/reservations/cancellationPolicies", produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<CancellationPoliciesResponseDto> getCancellationPolicies(
      @RequestParam(value = "reservationIds") Set<String> reservationIds,
      @RequestParam("hotelId") String hotelId,
      @RequestParam(value = "ratePlanCode") String ratePlan,
      @RequestParam(value = "arrivalDate") String arrivalDate) {
    final var cancellationPoliciesResponse = hotelReservationPort.getCancellationPolicies(
        reservationIds, hotelId, ratePlan, arrivalDate);

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
          schema = @Schema(implementation = OhipBadRequestException.class))})
  @ApiResponse(responseCode = "404", description = "Resource Not Found", content = {
      @Content(mediaType = "application/json",
          schema = @Schema(implementation = OhipNotFoundException.class))})
  @ApiResponse(responseCode = "500", description = "Internal Server Error", content = {
      @Content(mediaType = "application/json",
          schema = @Schema(implementation = OhipInternalException.class))})
  @GetMapping(value = "/reservations/marketingPreferences", produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<MarketingPreferencesResponseDto> getMarketingPreferences(
      @RequestParam("hotelId") String hotelId,
      @RequestParam("reservationId") String reservationId) {
    final var marketingPreferencesResponse = hotelReservationPort.getMarketingPreferences(
        hotelId, reservationId);
    final var marketingPreferencesResponseDto = marketingPreferencesResponseMapper.toDto(
        marketingPreferencesResponse);
    return ResponseEntity.status(HttpStatus.OK).body(marketingPreferencesResponseDto);
  }

  @Operation(summary = "Delete a reservation")
  @ApiResponse(responseCode = "204", description = "No Content")
  @ApiResponse(responseCode = "400", description = "Error Occurred ", content = {
      @Content(mediaType = "application/json",
          schema = @Schema(implementation = OhipBadRequestException.class))})
  @ApiResponse(responseCode = "500", description = "Internal Server Error", content = {
      @Content(mediaType = "application/json",
          schema = @Schema(implementation = OhipInternalException.class))})
  @ResponseStatus(HttpStatus.NO_CONTENT)
  @DeleteMapping("/reservations")
  public void deleteReservation(@RequestParam("hotelId") String hotelId,
      @RequestParam("reservationId") String reservationId) {
    hotelReservationPort.deleteReservation(hotelId, reservationId);
  }

  @Operation(summary = "Get booking allowances by reservation id")
  @ApiResponse(responseCode = "200", description = "Success", content = {
      @Content(mediaType = "application/json",
          schema = @Schema(implementation = BookingAllowancesResponseDto.class))})
  @ApiResponse(responseCode = "400", description = "Error Occurred ", content = {
      @Content(mediaType = "application/json",
          schema = @Schema(implementation = OhipBadRequestException.class))})
  @ApiResponse(responseCode = "404", description = "Resource Not Found", content = {
      @Content(mediaType = "application/json",
          schema = @Schema(implementation = OhipNotFoundException.class))})
  @ApiResponse(responseCode = "500", description = "Internal Server Error", content = {
      @Content(mediaType = "application/json",
          schema = @Schema(implementation = OhipInternalException.class))})
  @GetMapping(value = "/reservations/bookingAllowances",
      produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<BookingAllowancesResponseDto> getBookingAllowances(
      @RequestParam("hotelId") String hotelId,
      @RequestParam("reservationId") String reservationId,
      @RequestParam(value = "bookingAllowanceIds", required = false) List<String> bookingAllowances) {
    final var bookingAllowancesResponse = hotelReservationPort.getBookingAllowances(hotelId,
        reservationId, bookingAllowances);
    final var bookingAllowancesResponseDto = bookingAllowancesResponseMapper.toDto(
        bookingAllowancesResponse);
    return ResponseEntity.status(HttpStatus.OK).body(bookingAllowancesResponseDto);
  }

  @Operation(summary = "Update reservation - multi-part update")
  @ApiResponse(responseCode = "200", description = "Success", content = {
      @Content(mediaType = "application/json")})
  @ApiResponse(responseCode = "400", description = "Error Occurred ", content = {
      @Content(mediaType = "application/json",
          schema = @Schema(implementation = OhipBadRequestException.class))})
  @ApiResponse(responseCode = "404", description = "Resource Not Found", content = {
      @Content(mediaType = "application/json",
          schema = @Schema(implementation = OhipNotFoundException.class))})
  @ApiResponse(responseCode = "500", description = "Internal Server Error", content = {
      @Content(mediaType = "application/json",
          schema = @Schema(implementation = OhipInternalException.class))})
  @PutMapping(value = "/reservations", produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<Void> updateReservation(
      @RequestBody @Valid UpdateReservationsRequestDto updateReservationsRequestDto) {

    final var updateReservationRequest =
        updateReservationRequestMapper.toUpdateReservationsRequestModel(
            updateReservationsRequestDto);

    hotelReservationPort.updateReservations(updateReservationRequest);
    return ResponseEntity.status(HttpStatus.OK).build();
  }

  @Operation(summary = "Update reservation - add external reference for opera ui reservations")
  @ApiResponse(responseCode = "200", description = "Success", content = {
      @Content(mediaType = "application/json")})
  @ApiResponse(responseCode = "400", description = "Error Occurred ", content = {
      @Content(mediaType = "application/json",
                  schema = @Schema(implementation = OhipBadRequestException.class))})
  @ApiResponse(responseCode = "404", description = "Resource Not Found", content = {
      @Content(mediaType = "application/json",
                  schema = @Schema(implementation = OhipNotFoundException.class))})
  @ApiResponse(responseCode = "500", description = "Internal Server Error", content = {
      @Content(mediaType = "application/json",
                  schema = @Schema(implementation = OhipInternalException.class))})
  @PutMapping(value = "/reservations/externalRef", produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<Void> updateReservationsWithExternalRef(
          @RequestParam("hotelId") String hotelId, @RequestParam("reservationIds") Set<String> reservationIds,
          @RequestParam("externalReference") String externalReference) {

    hotelReservationPort.updateReservationsWithExternalRef(hotelId, reservationIds, externalReference);
    return ResponseEntity.status(HttpStatus.OK).build();
  }

  @Operation(summary = "Update routing instructions - update payee info")
  @ApiResponse(responseCode = "200", description = "Success", content = {
      @Content(mediaType = "application/json")})
  @ApiResponse(responseCode = "400", description = "Error Occurred ", content = {
      @Content(mediaType = "application/json",
                  schema = @Schema(implementation = OhipBadRequestException.class))})
  @ApiResponse(responseCode = "404", description = "Resource Not Found", content = {
      @Content(mediaType = "application/json",
                  schema = @Schema(implementation = OhipNotFoundException.class))})
  @ApiResponse(responseCode = "500", description = "Internal Server Error", content = {
      @Content(mediaType = "application/json",
                  schema = @Schema(implementation = OhipInternalException.class))})
  @PutMapping(value = "/reservations/instructions/payeeInfo", produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<Void> updateRoutingInstructionsWithPayeeInfo(
          @RequestParam("hotelId") String hotelId, @RequestParam("reservationIds") Set<String> reservationIds) {

    hotelReservationPort.updateRoutingInstructionsWithPayeeInfo(hotelId, reservationIds);
    return ResponseEntity.status(HttpStatus.OK).build();
  }


  @Operation(summary = "Confirm amend changes")
  @ApiResponse(responseCode = "200", description = "Success", content = {
      @Content(mediaType = "application/json",
          schema = @Schema(implementation = ReservationByBasketRefResponseDto.class))})
  @ApiResponse(responseCode = "400", description = "Error Occurred ", content = {
      @Content(mediaType = "application/json",
          schema = @Schema(implementation = OhipBadRequestException.class))})
  @ApiResponse(responseCode = "404", description = "Resource Not Found", content = {
      @Content(mediaType = "application/json",
          schema = @Schema(implementation = OhipNotFoundException.class))})
  @ApiResponse(responseCode = "500", description = "Internal Server Error", content = {
      @Content(mediaType = "application/json",
          schema = @Schema(implementation = OhipInternalException.class))})
  @PutMapping(value = "/reservations/confirmAmend", produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<ReservationByBasketRefResponseDto> confirmAmend(
      @RequestBody @Valid ConfirmAmendOnReservationsRequestDto confirmAmendOnReservationsRequestDto) {
    var confirmAmendOnReservationsRequest = confirmAmendRequestMapper.toRequestModel(
        confirmAmendOnReservationsRequestDto);
    var confirmAmendResponse = hotelReservationPort.confirmAmend(confirmAmendOnReservationsRequest);
    var confirmAmendResponseDto = reservationByBasketRefResponseMapper.toDto(
        confirmAmendResponse);
    return ResponseEntity.status(HttpStatus.OK).body(confirmAmendResponseDto);
  }

  @Operation(summary = "Confirm amend changes")
  @ApiResponse(responseCode = "200", description = "Success", content = {
      @Content(mediaType = "application/json",
          schema = @Schema(implementation = ReservationByBasketRefResponseDto.class))})
  @ApiResponse(responseCode = "400", description = "Error Occurred ", content = {
      @Content(mediaType = "application/json",
          schema = @Schema(implementation = OhipBadRequestException.class))})
  @ApiResponse(responseCode = "404", description = "Resource Not Found", content = {
      @Content(mediaType = "application/json",
          schema = @Schema(implementation = OhipNotFoundException.class))})
  @ApiResponse(responseCode = "500", description = "Internal Server Error", content = {
      @Content(mediaType = "application/json",
          schema = @Schema(implementation = OhipInternalException.class))})
  @PutMapping(value = "/reservations/confirmAmendSingleCall", produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<ReservationByBasketRefResponseDto> confirmAmendForSingleCall(
          @RequestBody @Valid ConfirmAmendForSingleRequestDto confirmAmendForSingleRequestDto) {
    var confirmAmendForSingleRequest = confirmAmendForSingleRequestMapper.toRequestModel(
            confirmAmendForSingleRequestDto);
    var confirmAmendResponse = hotelReservationPort.confirmAmendForSingleCall(confirmAmendForSingleRequest);
    var confirmAmendResponseDto = reservationByBasketRefResponseMapper.toDto(
            confirmAmendResponse);
    return ResponseEntity.status(HttpStatus.OK).body(confirmAmendResponseDto);
  }

  @Operation(summary = "Move reservation payment data from window1 to window2")
  @ApiResponse(responseCode = "200", description = "Success", content = {
      @Content(mediaType = "application/json")})
  @ApiResponse(responseCode = "400", description = "Error Occurred ", content = {
      @Content(mediaType = "application/json",
          schema = @Schema(implementation = OhipBadRequestException.class))})
  @ApiResponse(responseCode = "404", description = "Resource Not Found", content = {
      @Content(mediaType = "application/json",
          schema = @Schema(implementation = OhipNotFoundException.class))})
  @ApiResponse(responseCode = "500", description = "Internal Server Error", content = {
      @Content(mediaType = "application/json",
          schema = @Schema(implementation = OhipInternalException.class))})
  @PutMapping(value = "/reservations/movePaymentDetails", produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<Void> movePaymentDetails(
      @RequestParam(value = "reservationIds") Set<String> reservationIds,
      @RequestParam("hotelId") String hotelId) {

    hotelReservationPort.movePaymentDetails(hotelId, reservationIds);
    return ResponseEntity.status(HttpStatus.OK).build();
  }

  @Operation(summary = "Update booker details")
  @ApiResponse(responseCode = "200", description = "Success", content = {
      @Content(mediaType = "application/json")})
  @ApiResponse(responseCode = "400", description = "Error Occurred ", content = {
      @Content(mediaType = "application/json",
          schema = @Schema(implementation = OhipBadRequestException.class))})
  @ApiResponse(responseCode = "404", description = "Resource Not Found", content = {
      @Content(mediaType = "application/json",
          schema = @Schema(implementation = OhipNotFoundException.class))})
  @ApiResponse(responseCode = "500", description = "Internal Server Error", content = {
      @Content(mediaType = "application/json",
          schema = @Schema(implementation = OhipInternalException.class))})
  @PutMapping(value = "/reservations/booker", produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<Void> updateBookerDetails(
      @RequestBody @Valid BookerDetailsCnpRequestDto bookerDetailsCnpRequestDto) {
    var bookerDetailsCnpRequest = bookerDetailsCnpRequestMapper.toModel(bookerDetailsCnpRequestDto);
    hotelReservationPort.updateBookersDetails(bookerDetailsCnpRequest);
    return ResponseEntity.status(HttpStatus.OK).build();
  }

  @Operation(summary = "Update booker email address")
  @ApiResponse(responseCode = "200", description = "Success", content = {
      @Content(mediaType = "application/json")})
  @ApiResponse(responseCode = "400", description = "Error Occurred ", content = {
      @Content(mediaType = "application/json",
          schema = @Schema(implementation = OhipBadRequestException.class))})
  @ApiResponse(responseCode = "404", description = "Resource Not Found", content = {
      @Content(mediaType = "application/json",
          schema = @Schema(implementation = OhipNotFoundException.class))})
  @ApiResponse(responseCode = "500", description = "Internal Server Error", content = {
      @Content(mediaType = "application/json",
          schema = @Schema(implementation = OhipInternalException.class))})
  @PutMapping(value = "/reservations/email", produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<Void> updateBookerEmail(
      @RequestBody @Valid UpdateBookerEmailRequestDto updateBookerEmailRequestDto) {
    var updateBookerEmail = updateBookerEmailRequestMapper.toModel(updateBookerEmailRequestDto);
    hotelReservationPort.updateBookerEmail(updateBookerEmail);
    return ResponseEntity.status(HttpStatus.OK).build();
  }

  @Operation(summary = "Delete all existing routing instructions.")
  @ApiResponse(responseCode = "204", description = "Success", content = {
      @Content(mediaType = "application/json",
          schema = @Schema())})
  @ApiResponse(responseCode = "400", description = "Error Occurred ", content = {
      @Content(mediaType = "application/json",
          schema = @Schema(implementation = OhipBadRequestException.class))})
  @ApiResponse(responseCode = "404", description = "Resource Not Found", content = {
      @Content(mediaType = "application/json",
          schema = @Schema(implementation = OhipNotFoundException.class))})
  @ApiResponse(responseCode = "500", description = "Internal Server Error", content = {
      @Content(mediaType = "application/json",
          schema = @Schema(implementation = OhipInternalException.class))})
  @DeleteMapping(value = "/reservations/routingInstructions", produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<Void> deleteRoutingInstructions(
      @RequestParam("hotelId") String hotelId,
      @RequestParam("reservationIds") Set<String> reservationIds) {
    hotelReservationPort.deleteRoutingInstruction(hotelId, reservationIds);
    return ResponseEntity.noContent().build();
  }

  @Operation(summary = "Create memo for reservations")
  @ApiResponse(responseCode = "201", description = "Created", content = {
      @Content(mediaType = "application/json",
          schema = @Schema(implementation = MemosResponseDto.class))})
  @ApiResponse(responseCode = "400", description = "Error Occurred ", content = {
      @Content(mediaType = "application/json",
          schema = @Schema(implementation = OhipBadRequestException.class))})
  @ApiResponse(responseCode = "500", description = "Internal Server Error", content = {
      @Content(mediaType = "application/json",
          schema = @Schema(implementation = OhipInternalException.class))})
  @PostMapping(value = "/reservations/memos", produces = MediaType.APPLICATION_JSON_VALUE)
  @ResponseStatus(HttpStatus.CREATED)
  public MemosResponseDto createMemo(
      @RequestBody @Valid CreateMemoRequestDto createMemoRequestDto) {
    var createMemoRequest = memosMapper.toModel(createMemoRequestDto);
    return memosMapper.toDto(hotelReservationPort.createMemo(createMemoRequest));
  }

  @Operation(summary = "Get memos for reservations")
  @ApiResponse(responseCode = "200", description = "Success", content = {
      @Content(mediaType = "application/json",
          schema = @Schema(implementation = MemosResponseDto.class))})
  @ApiResponse(responseCode = "400", description = "Error Occurred ", content = {
      @Content(mediaType = "application/json",
          schema = @Schema(implementation = OhipBadRequestException.class))})
  @ApiResponse(responseCode = "500", description = "Internal Server Error", content = {
      @Content(mediaType = "application/json",
          schema = @Schema(implementation = OhipInternalException.class))})
  @GetMapping(value = "/reservations/memos", produces = MediaType.APPLICATION_JSON_VALUE)
  @ResponseStatus(HttpStatus.OK)
  public MemosResponseDto getMemos(
      @RequestParam("hotelId") String hotelId,
      @RequestParam("reservationIds") Set<String> reservationIds) {
    return memosMapper.toDto(hotelReservationPort.getMemos(hotelId, reservationIds));
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
    final var resProfiles = hotelReservationPort.createProfiles(guestReservationRequest);
    return ResponseEntity.status(HttpStatus.OK)
        .body(reservationResponseMapper.toCreateProfileDto(resProfiles));
  }

  @Operation(summary = "Update Billing Address on the basis of Address Type")
  @ApiResponse(responseCode = "204", description = "Success", content = {
      @Content(mediaType = "application/json", schema = @Schema(implementation = BillingAddressResponseDto.class))})
  @ApiResponse(responseCode = "400", description = "Error Occurred ", content = {
      @Content(mediaType = "application/json",
          schema = @Schema(implementation = OhipBadRequestException.class))})
  @ApiResponse(responseCode = "404", description = "Resource Not Found", content = {
      @Content(mediaType = "application/json",
          schema = @Schema(implementation = OhipNotFoundException.class))})
  @ApiResponse(responseCode = "500", description = "Internal Server Error", content = {
      @Content(mediaType = "application/json",
          schema = @Schema(implementation = OhipInternalException.class))})
  @PutMapping(value = "/reservation/updateBillingAddress", produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<Void> updateBillingAddress(
      @RequestBody @Valid BillingAddressCaptRequestDto billingAddressCaptRequestDto) {

    final BillingAddressRequest billingAddressRequest =
        reservationGuestRequestMapper.toModel(billingAddressCaptRequestDto);

    hotelReservationPort.updateBillingAddress(billingAddressRequest);

    return ResponseEntity.noContent().build();
  }

  @Operation(summary = "Add attachment to a Reservation")
  @ApiResponse(responseCode = "201", description = "Success", content = {
      @Content(mediaType = "application/json", schema = @Schema(implementation = PreCheckInResponse.class))})
  @ApiResponse(responseCode = "400", description = "Error Occurred ", content = {
      @Content(mediaType = "application/json",
          schema = @Schema(implementation = OhipBadRequestException.class))})
  @ApiResponse(responseCode = "404", description = "Resource Not Found", content = {
      @Content(mediaType = "application/json",
          schema = @Schema(implementation = OhipNotFoundException.class))})
  @ApiResponse(responseCode = "500", description = "Internal Server Error", content = {
      @Content(mediaType = "application/json",
          schema = @Schema(implementation = OhipInternalException.class))})
  @PostMapping(value = "/reservations/attachments", produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<PreCheckInResponse> addAttachmentToReservation(
      @RequestBody @Valid ReservationFileAttachmentRequestDto reservationFileAttachmentRequestDto) {

    final ReservationFileAttachmentRequest reservationFileAttachmentRequest =
        reservationFileAttachmentRequestMapper.toModel(reservationFileAttachmentRequestDto);
    final PreCheckInResponse reservationFileAttachmentResponse =
        hotelReservationPort.addAttachmentToReservation(reservationFileAttachmentRequest);
    return ResponseEntity.status(HttpStatus.OK).body(reservationFileAttachmentResponse);
  }

  @Operation(summary = "Save Reservation Pre-CheckIn status")
  @ApiResponse(responseCode = "201", description = "Success", content = {
      @Content(mediaType = "application/json", schema = @Schema(implementation = PreCheckInResponse.class))})
  @ApiResponse(responseCode = "400", description = "Error Occurred ", content = {
      @Content(mediaType = "application/json",
          schema = @Schema(implementation = OhipBadRequestException.class))})
  @ApiResponse(responseCode = "404", description = "Resource Not Found", content = {
      @Content(mediaType = "application/json",
          schema = @Schema(implementation = OhipNotFoundException.class))})
  @ApiResponse(responseCode = "500", description = "Internal Server Error", content = {
      @Content(mediaType = "application/json",
          schema = @Schema(implementation = OhipInternalException.class))})
  @PostMapping(value = "/reservations/pre-checkin", produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<PreCheckInResponse> saveReservationPreCheckIn(
      @RequestBody @Valid PreCheckInRequestDto preCheckInRequestDto) {

    final PreCheckInRequest preCheckInRequest =
        preCheckInRequestMapper.toModel(preCheckInRequestDto);
    final PreCheckInResponse preCheckInResponse =
        hotelReservationPort.saveReservationPreCheckIn(preCheckInRequest);
    return ResponseEntity.status(HttpStatus.OK).body(preCheckInResponse);
  }

  @Operation(summary = "Deleting Reservation Pre-CheckIn")
  @ApiResponse(responseCode = "204", description = "No Content")
  @ApiResponse(responseCode = "400", description = "Error Occurred ", content = {
      @Content(mediaType = "application/json",
          schema = @Schema(implementation = OhipBadRequestException.class))})
  @ApiResponse(responseCode = "500", description = "Internal Server Error", content = {
      @Content(mediaType = "application/json",
          schema = @Schema(implementation = OhipInternalException.class))})
  @ResponseStatus(HttpStatus.NO_CONTENT)
  @DeleteMapping("/reservations/pre-checkin")
  public void deleteReservationPreCheckIn(@RequestParam("hotelId") String hotelId,
      @RequestParam("reservationId") String reservationId) {
    hotelReservationPort.deleteReservationPreCheckIn(hotelId, reservationId);
  }

  @Operation(summary = "Deleting Reg Card attachment of a reservation")
  @ApiResponse(responseCode = "204", description = "No Content")
  @ApiResponse(responseCode = "400", description = "Error Occurred ", content = {
      @Content(mediaType = "application/json",
          schema = @Schema(implementation = OhipBadRequestException.class))})
  @ApiResponse(responseCode = "500", description = "Internal Server Error", content = {
      @Content(mediaType = "application/json",
          schema = @Schema(implementation = OhipInternalException.class))})
  @ResponseStatus(HttpStatus.NO_CONTENT)
  @DeleteMapping("/reservations/attachments")
  public void deleteRegCardAttachment(@RequestParam("hotelId") String hotelId,
      @RequestParam("reservationId") String reservationId) {
    hotelReservationPort.deleteRegCardAttachment(hotelId, reservationId);
  }

  @Operation(summary = "create/remove packages based on a schedule")
  @ApiResponse(responseCode = "201", description = "Success", content = {
      @Content(mediaType = "application/json")})
  @ApiResponse(responseCode = "400", description = "Error Occurred ", content = {
      @Content(mediaType = "application/json", schema = @Schema(implementation = OhipBadRequestException.class))})
  @ApiResponse(responseCode = "404", description = "Resource Not Found", content = {
      @Content(mediaType = "application/json", schema = @Schema(implementation = OhipNotFoundException.class))})
  @ApiResponse(responseCode = "500", description = "Internal Server Error", content = {
      @Content(mediaType = "application/json", schema = @Schema(implementation = OhipInternalException.class))})
  @PutMapping(value = "reservations/ancillaries/scheduled", produces = MediaType.APPLICATION_JSON_VALUE)
  @ResponseStatus(HttpStatus.CREATED)
  public void updateScheduledPackages(
      @RequestBody @Valid ReservationScheduledPackagesRequestDto reservationPackagesRequestDto) {

    final var updateReservationScheduleRequest = updateReservationRequestMapper.toModel(
        reservationPackagesRequestDto);

    hotelReservationPort.updateReservationPackages(updateReservationScheduleRequest);
  }

  @Operation(summary = "Link reservation to leisure customer account")
  @ApiResponse(responseCode = "200", description = "Success", content = @Content())
  @ApiResponse(responseCode = "400", description = "Bad request", content = {
      @Content(mediaType = "application/json",
          schema = @Schema(implementation = OhipBadRequestException.class))})
  @ApiResponse(responseCode = "500", description = "Internal Server Error", content = {
      @Content(mediaType = "application/json",
          schema = @Schema(implementation = OhipInternalException.class))})
  @PutMapping(value = "/reservations/link-leisure-customer", produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<Void> linkReservationToLeisureCustomer(
      @RequestBody @Valid LinkReservationToLeisureCustomerRequestDto linkReservationToLeisureCustomerRequestDto) {

    final var linkReservationToLeisureCustomerRequest =
        linkReservationToLeisureCustomerRequestMapper.toModel(
            linkReservationToLeisureCustomerRequestDto);

    hotelReservationPort.linkReservationToLeisureCustomer(linkReservationToLeisureCustomerRequest);

    return ResponseEntity.status(HttpStatus.OK).build();
  }

  @Operation(summary = "Update preferences")
  @ApiResponse(responseCode = "204", description = "Success", content = {
      @Content(mediaType = "application/json")})
  @ApiResponse(responseCode = "400", description = "Error Occurred ", content = {
      @Content(mediaType = "application/json",
          schema = @Schema(implementation = OhipBadRequestException.class))})
  @ApiResponse(responseCode = "404", description = "Resource Not Found", content = {
      @Content(mediaType = "application/json",
          schema = @Schema(implementation = OhipNotFoundException.class))})
  @ApiResponse(responseCode = "500", description = "Internal Server Error", content = {
      @Content(mediaType = "application/json",
          schema = @Schema(implementation = OhipInternalException.class))})
  @PutMapping(value = "/reservations/preferences", produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<Void> updatePreferences(
      @RequestBody @Valid ReservationPreferencesRequestDto reservationPreferencesRequestDto) {

    final ReservationPreferencesRequest reservationPreferencesRequest =
        reservationPreferencesRequestMapper.toModel(reservationPreferencesRequestDto);

    hotelReservationPort.updateReservationPreferences(reservationPreferencesRequest);

    return ResponseEntity.noContent().build();
  }

  @Operation(summary = "Add/update an alerts for reservations")
  @ApiResponse(responseCode = "204", description = "Success", content = {
      @Content(mediaType = "application/json")})
  @ApiResponse(responseCode = "400", description = "Error Occurred ", content = {
      @Content(mediaType = "application/json",
          schema = @Schema(implementation = OhipBadRequestException.class))})
  @ApiResponse(responseCode = "404", description = "Resource Not Found", content = {
      @Content(mediaType = "application/json",
          schema = @Schema(implementation = OhipNotFoundException.class))})
  @ApiResponse(responseCode = "500", description = "Internal Server Error", content = {
      @Content(mediaType = "application/json",
          schema = @Schema(implementation = OhipInternalException.class))})
  @PutMapping(value = "/reservations/alerts", produces = MediaType.APPLICATION_JSON_VALUE)
  @ResponseStatus(HttpStatus.NO_CONTENT)
  public void updateReservationAlerts(
      @RequestBody @Valid UpdateReservationAlertsRequestDto updateReservationAlertsRequestDto) {

    final var updateAlertsModel =
        updateReservationRequestMapper.toAlertsModel(updateReservationAlertsRequestDto);

    hotelReservationPort.updateReservationAlerts(updateAlertsModel);
  }

  @Operation(summary = "Get Reservation Amounts")
  @ApiResponse(responseCode = "200", description = "Success", content = {
      @Content(mediaType = "application/json",
          schema = @Schema(implementation = ReservationAmountsDto.class))})
  @ApiResponse(responseCode = "400", description = "Bad Request - Invalid Client Request",
      content = {@Content(mediaType = "application/json",
          schema = @Schema(implementation = OhipBadRequestException.class))})
  @ApiResponse(responseCode = "404", description = "Resource Not Found", content = {
      @Content(mediaType = "application/json",
          schema = @Schema(implementation = OhipNotFoundException.class))})
  @ApiResponse(responseCode = "500", description = "Internal Server Error", content = {
      @Content(mediaType = "application/json",
          schema = @Schema(implementation = OhipInternalException.class))})
  @GetMapping(value = "/reservations/amounts", produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<ReservationAmountsDto> getReservationAmounts(@RequestParam("hotelId") String hotelId,
      @RequestParam("reservationIds") Set<String> reservationIds) {
    final var reservationAmounts = hotelReservationPort.getReservationAmounts(hotelId, reservationIds);
    return ResponseEntity.status(HttpStatus.OK).body(reservationAmountsMapper.toDto(reservationAmounts));
  }

  @Operation(summary = "Get payment type of the reservations by external reference ids")
  @ApiResponse(responseCode = "200", description = "Success", content = {
      @Content(mediaType = "application/json",
          schema = @Schema(implementation = ReservationsDetailsResponseDto.class))})
  @ApiResponse(responseCode = "400", description = "Error Occurred ", content = {
      @Content(mediaType = "application/json",
          schema = @Schema(implementation = OhipBadRequestException.class))})
  @ApiResponse(responseCode = "404", description = "Resource Not Found", content = {
      @Content(mediaType = "application/json",
          schema = @Schema(implementation = OhipNotFoundException.class))})
  @ApiResponse(responseCode = "500", description = "Internal Server Error", content = {
      @Content(mediaType = "application/json",
          schema = @Schema(implementation = OhipInternalException.class))})
  @GetMapping(value = "/reservations/paymentType", produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<List<ReservationsPaymentCardType>> getPaymentTypeReservationsByReservationIds(
      @RequestParam("hotelId") String hotelId,
      @RequestParam("reservationIds") List<String> reservationIds) {

    final List<ReservationsPaymentCardType> paymentMethods =
        hotelReservationPort.getReservationMethodPayment(hotelId, reservationIds);
    return ResponseEntity.status(HttpStatus.OK)
        .body(paymentMethods);
  }

  @Operation(summary = "Save Reservation Pre-Register status")
  @ApiResponse(responseCode = "201", description = "Success", content = {
      @Content(mediaType = "application/json", schema = @Schema(implementation = PreCheckInResponse.class))})
  @ApiResponse(responseCode = "400", description = "Error Occurred ", content = {
      @Content(mediaType = "application/json", schema = @Schema(implementation = OhipBadRequestException.class))})
  @ApiResponse(responseCode = "404", description = "Resource Not Found", content = {
      @Content(mediaType = "application/json", schema = @Schema(implementation = OhipNotFoundException.class))})
  @ApiResponse(responseCode = "500", description = "Internal Server Error", content = {
      @Content(mediaType = "application/json", schema = @Schema(implementation = OhipInternalException.class))})
  @PostMapping(value = "/reservations/pre-register", produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<PreCheckInResponse> saveReservationPreRegister(
          @RequestBody @Valid PreCheckInRequestDto preCheckInRequestDto) {
    final PreCheckInRequest preCheckInRequest = preCheckInRequestMapper.toModel(preCheckInRequestDto);
    final PreCheckInResponse preCheckInResponse = hotelReservationPort.saveReservationPreRegister(preCheckInRequest);
    return ResponseEntity.status(HttpStatus.OK).body(preCheckInResponse);
  }
}
