package uk.co.whitbread.basket.infrastructure.rest.client.reservation;

import static uk.co.whitbread.basket.utils.SanitizingUtils.sanitize;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import uk.co.whitbread.basket.domain.model.basket.in.ReservationGuestRequest;
import uk.co.whitbread.basket.domain.model.basket.in.ReservationPackagesRequest;
import uk.co.whitbread.basket.domain.model.basket.out.ConfirmReservationRequest;
import uk.co.whitbread.basket.domain.model.basket.out.ConfirmReservationResponse;
import uk.co.whitbread.basket.domain.model.basket.out.ReservationProfiles;
import uk.co.whitbread.basket.domain.model.business.in.BusinessItems;
import uk.co.whitbread.basket.domain.model.payments.in.CompanyQuestionAndAnswerDetails;
import uk.co.whitbread.basket.domain.model.payments.in.DiscountRequest;
import uk.co.whitbread.basket.domain.model.reservation.in.DepositFoliosRequest;
import uk.co.whitbread.basket.domain.model.reservation.in.ReservationAlertsRequest;
import uk.co.whitbread.basket.domain.model.reservation.out.DepositFoliosResponse;
import uk.co.whitbread.basket.domain.model.reservation.out.DepositsResponse;
import uk.co.whitbread.basket.domain.model.reservation.out.MarketingPreferencesResponse;
import uk.co.whitbread.basket.domain.model.reservation.out.ReservationByBasketRefResponse;
import uk.co.whitbread.basket.domain.ports.secondary.HotelReservationOutPort;
import uk.co.whitbread.basket.generated.models.reservation.BusinessItemsRequestDto;
import uk.co.whitbread.basket.generated.models.reservation.ConfirmReservationRequestDto;
import uk.co.whitbread.basket.generated.models.reservation.ReservationByBasketRefRequestDto;
import uk.co.whitbread.basket.generated.models.reservation.ReservationGuestRequestDto;
import uk.co.whitbread.basket.generated.models.reservation.ReservationPackagesRequestDto;
import uk.co.whitbread.basket.generated.models.reservation.SpecialRequestsDto;
import uk.co.whitbread.basket.generated.models.reservation.UpdateDiscountRequestDto;
import uk.co.whitbread.basket.generated.models.reservation.UpdateReservationSingleCallRequestDto;
import uk.co.whitbread.basket.infrastructure.rest.client.payments.properties.InitiatePaymentProperties;
import uk.co.whitbread.basket.infrastructure.rest.client.reservation.mapper.AttachReservationProfileRequestMapper;
import uk.co.whitbread.basket.infrastructure.rest.client.reservation.mapper.BusinessItemsMapper;
import uk.co.whitbread.basket.infrastructure.rest.client.reservation.mapper.CompanyQuestionAndAnswerDetailsMapper;
import uk.co.whitbread.basket.infrastructure.rest.client.reservation.mapper.DepositsResponseMapper;
import uk.co.whitbread.basket.infrastructure.rest.client.reservation.mapper.MarketingPreferencesResponseMapper;
import uk.co.whitbread.basket.infrastructure.rest.client.reservation.mapper.ReservationAlertsMapper;
import uk.co.whitbread.basket.infrastructure.rest.client.reservation.mapper.ReservationResponseMapper;
import uk.co.whitbread.basket.infrastructure.rest.client.reservation.service.ReservationClient;
import uk.co.whitbread.basket.infrastructure.rest.controller.basket.mapper.UpdateReservationRequestMapper;
import uk.co.whitbread.basket.infrastructure.rest.controller.payments.model.in.CompanyQuestionAndAnswerDetailsRequestDto;
import uk.co.whitbread.basket.infrastructure.rest.utils.CacheHelper;

@Slf4j
@RequiredArgsConstructor
public class HotelReservationOutPortImpl implements HotelReservationOutPort {

  private static final String CONTROL_CHARACTER_REGEX = "\\p{Cntrl}";
  private final ReservationClient reservationClient;
  private final ReservationResponseMapper responseMapper;
  private final BusinessItemsMapper businessItemsMapper;
  private final CompanyQuestionAndAnswerDetailsMapper companyQuestionAndAnswerDetailsMapper;
  private final DepositsResponseMapper depositsMapper;
  private final MarketingPreferencesResponseMapper marketingPreferencesResponseMapper;
  private final AttachReservationProfileRequestMapper attachReservationProfileRequestMapper;
  private final UpdateReservationRequestMapper updateReservationRequestMapper;
  private final ReservationAlertsMapper reservationAlertsMapper;
  private final InitiatePaymentProperties initiatePaymentProperties;
  private final CacheHelper cacheHelper;

  @Override
  public ReservationByBasketRefResponse getReservationsByBasketReference(String basketReference,
      String priceBreakDownNeeded, boolean rateInfoNeeded, Boolean useCache) {

    if (Boolean.TRUE.equals(useCache)) {
      var cachedValue = cacheHelper.getCacheValue(initiatePaymentProperties.getReservationsCache(), basketReference,
          ReservationByBasketRefResponse.class);
      if (cachedValue != null) {
        log.info("Reservations cache hit for basketReference {}", sanitize(basketReference));
        return cachedValue;
      }
      log.info("Reservations cache missed for basketReference {}, calling REST API...", sanitize(basketReference));
    }

    return getReservationsByBasketReference(basketReference, priceBreakDownNeeded, rateInfoNeeded);
  }

  @Override
  public ReservationByBasketRefResponse getReservationsByBasketReference(String basketReference,
      String priceBreakDownNeeded, boolean rateInfoNeeded) {
    var basketReferenceSanitized = Optional.ofNullable(basketReference)
        .map(in -> in.replaceAll(CONTROL_CHARACTER_REGEX, "")).orElse("null");
    log.info("Entered getReservationsByBasketReference with basketReference={}",
        basketReferenceSanitized);
    var requestDto = new ReservationByBasketRefRequestDto();
    requestDto.setPriceBreakDownNeeded(priceBreakDownNeeded);
    requestDto.setRateInfoNeeded(rateInfoNeeded);
    var reservationResponse = reservationClient.getReservationsByBasketReference(basketReference, requestDto);
    return responseMapper.toModel(reservationResponse);
  }

  @Override
  public void updateDiscount(DiscountRequest discountRequest,
      List<String> reservationIds, String hotelId, String currencyCode) {
    var updateDiscountRequest = createModel(
        discountRequest.getDiscountAmount(), reservationIds, hotelId, currencyCode);
    reservationClient.sendPutUpdateDiscount(updateDiscountRequest);
  }

  @Override
  public void updateCompanyQuestionAndAnswerDetails(
      final CompanyQuestionAndAnswerDetails companyQuestionAndAnswerDetails,
      final Set<String> reservationIds, final String hotelId) {
    var companyQuestionAndAnswerDetailsRequestDto =
        createCompanyQuestionAndAnswerDetailsModel(companyQuestionAndAnswerDetails,
        reservationIds, hotelId);
    reservationClient.sendUpdateCompanyQuestionAndAnswerDetailsRequests(companyQuestionAndAnswerDetailsRequestDto);
  }

  @Override
  public void updateBusinessItems(BusinessItems businessItems, List<String> reservationIds,
      String hotelId, String companyId, String channel) {
    var businessItemsRequest = createBusinessModel(businessItems, reservationIds, hotelId, companyId, channel);
    reservationClient.sendPutUpdateBusinessItems(businessItemsRequest);
  }

  @Override
  public void updateSpecialRequests(List<String> specialRequests, List<String> bookingNotes,
      List<String> reservationIds,
      String hotelId) {
    var specialRequestsDto = createSpecialRequests(specialRequests, bookingNotes, reservationIds,
        hotelId);
    reservationClient.sendPutReservationsSpecialRequests(specialRequestsDto);
  }

  private SpecialRequestsDto createSpecialRequests(List<String> specialRequests,
      List<String> bookingNotes,
      List<String> reservationIds, String hotelId) {

    var specialRequestsDto = new SpecialRequestsDto();
    specialRequestsDto.setSpecialRequests(specialRequests);
    specialRequestsDto.setBookingNotes(bookingNotes);
    specialRequestsDto.setReservationIds(reservationIds);
    specialRequestsDto.setHotelId(hotelId);
    return specialRequestsDto;

  }

  @Override
  public DepositsResponse getDepositsForReservationId(String hotelId, String reservationId) {
    var retrievedDeposits = reservationClient.getDepositsForReservationId(hotelId, reservationId);
    return depositsMapper.toModel(retrievedDeposits);
  }

  @Override
  public MarketingPreferencesResponse getMarketingPreferences(String hotelId, String reservationId) {
    log.info("Entered getMarketingPreferencesResponse with hotelId={} and reservationId={}", hotelId, reservationId);
    var marketingPreferencesResponseDto = reservationClient.getMarketingPreferences(hotelId, reservationId);
    return marketingPreferencesResponseMapper.toModel(marketingPreferencesResponseDto);
  }

  @Override
  public void attachProfileToReservations(String hotelId, String profileId,
      Set<String> reservationIds) {
    reservationClient.attachProfileToReservations(attachReservationProfileRequestMapper
        .toDto(hotelId, profileId, reservationIds));
  }

  @Override
  public void deleteRoutingInstructions(String hotelId, Set<String> reservationIds) {
    reservationClient.deleteRoutingInstructions(hotelId, reservationIds);
  }

  @Override
  public ConfirmReservationResponse updateReservationRequest(ReservationGuestRequest guestReservationRequest,
      ReservationPackagesRequest updatePackageReservationRequest, BusinessItems finalBusinessItems,
      List<String> specialRequests, List<String> bookingNotes, String hotelId,
      String reservationId, ConfirmReservationRequest confirmationPaymentDetails) {
    log.debug("Entered UpdateReservationRequest");
    var guestRequestDto = updateReservationRequestMapper.toDto(guestReservationRequest);
    var packagesRequestDto = updateReservationRequestMapper.toDto(updatePackageReservationRequest);
    var specialRequestDto = createSpecialRequests(specialRequests, bookingNotes,
        List.of(reservationId), hotelId);
    var businessItemsRequestDto = createBusinessModel(finalBusinessItems, List.of(reservationId),
        hotelId, null, "DISTR");
    var paymentRequestDto = updateReservationRequestMapper.toDto(confirmationPaymentDetails);
    var updateReservationReq = createUpdateReservationRequest(guestRequestDto, packagesRequestDto,
        specialRequestDto, businessItemsRequestDto, paymentRequestDto);
    log.debug("Final Requests before sending to hotel-reservation = {}",
        updateReservationReq);
    var confirmReservationResp = reservationClient.sendPutUpdateReservation(updateReservationReq);
    return updateReservationRequestMapper.toModel(confirmReservationResp);
  }

  @Override
  public ReservationProfiles createProfileIds(ReservationGuestRequest guestReservationRequest) {
    var guestRequestDto = updateReservationRequestMapper.toDto(guestReservationRequest);
    var profileIds = reservationClient.createProfileIds(guestRequestDto);
    return updateReservationRequestMapper.toModel(profileIds);
  }

  @Override
  public void updateReservationAlerts(ReservationAlertsRequest reservationUdfRequest) {
    var request = reservationAlertsMapper.toDto(reservationUdfRequest);
    reservationClient.updateReservationAlerts(request);
  }


  /**
   * Retrieves preview deposit folios (proposed charges) for one or more reservations.
   *
   * <p>This method queries the hotel reservation service to obtain an estimate of deposits
   * that would be charged if a payment is processed for the specified reservations.
   * The response is mapped from the DTO to the domain model for further processing.
   *
   * @param hotelId       the hotel identifier (required, must be valid)
   * @param reservationIds set of reservation IDs to fetch preview deposits for (required, non-empty)
   * @return DepositFoliosResponse containing preview deposit details per reservation,
   *         or null if the reservation service returns an empty response
   * @throws HotelReservationException if the reservation service returns an error or if
   *         the HTTP request fails
   */
  @Override
  public DepositFoliosResponse getPreviewDepositsForReservationId(String hotelId,
      Set<String> reservationIds) {
    var retrievedDeposits = reservationClient.getPreviewDepositsForReservationId(hotelId,
        reservationIds);
    return depositsMapper.toDto(retrievedDeposits);
  }

  /**
   * Saves deposit folios to the hotel reservation system (Opera).
   *
   * <p>This method sends proposed deposit folio charges to the hotel reservation system
   * for persistence and folio synchronization. The domain model is converted to DTO
   * format before transmission.
   *
   * @param depositFoliosRequest the deposit folios payload containing charges to save
   * @throws HotelReservationException if the save operation fails or if the HTTP request fails
   */
  @Override
  public void saveDepositFolios(DepositFoliosRequest depositFoliosRequest) {
    var requestDto = depositsMapper.toDepositModel(depositFoliosRequest);
    reservationClient.saveDepositsFolios(requestDto);
  }

  private UpdateReservationSingleCallRequestDto createUpdateReservationRequest(
      ReservationGuestRequestDto guestRequestDto,
      ReservationPackagesRequestDto packagesRequestDto, SpecialRequestsDto specialRequestDto,
      BusinessItemsRequestDto businessItemsRequestDto,
      ConfirmReservationRequestDto paymentRequestDto) {
    UpdateReservationSingleCallRequestDto updateReservationSingleCallRequestDto = new
        UpdateReservationSingleCallRequestDto();
    updateReservationSingleCallRequestDto.setReservationGuestDetails(guestRequestDto);
    updateReservationSingleCallRequestDto.setReservationPackages(packagesRequestDto);
    updateReservationSingleCallRequestDto.setBusinessItem(businessItemsRequestDto);
    updateReservationSingleCallRequestDto.setSpecialRequests(specialRequestDto);
    updateReservationSingleCallRequestDto.setPaymentDetails(paymentRequestDto);
    return updateReservationSingleCallRequestDto;
  }

  private UpdateDiscountRequestDto createModel(BigDecimal discountAmount,
      List<String> reservationIds,
      String hotelId,
      String currency) {
    UpdateDiscountRequestDto dto = new UpdateDiscountRequestDto();
    dto.currency(currency);
    dto.hotelId(hotelId);
    dto.discountAmount(discountAmount);
    dto.reservationIds(reservationIds);
    return dto;
  }

  private CompanyQuestionAndAnswerDetailsRequestDto createCompanyQuestionAndAnswerDetailsModel(
      final CompanyQuestionAndAnswerDetails companyQuestionAndAnswerDetails,
      final Set<String> reservationIds, final String hotelId) {

    CompanyQuestionAndAnswerDetailsRequestDto request =
        new CompanyQuestionAndAnswerDetailsRequestDto();

    request.setHotelId(hotelId);
    request.setReservationIds(reservationIds);
    request.setCompanyQuestionAndAnswerDetails(companyQuestionAndAnswerDetailsMapper
        .toDto(companyQuestionAndAnswerDetails));

    return request;
  }

  private BusinessItemsRequestDto createBusinessModel(BusinessItems businessItems,
      List<String> reservationIds, String hotelId, String companyId, String channel) {
    BusinessItemsRequestDto businessItemsRequestDto = new BusinessItemsRequestDto();

    businessItemsRequestDto.reservationIds(reservationIds);
    businessItemsRequestDto.hotelId(hotelId);
    businessItemsRequestDto.companyId(companyId);
    businessItemsRequestDto.businessItems(businessItemsMapper.toDto(businessItems));
    businessItemsRequestDto.channel(channel);

    return businessItemsRequestDto;
  }

}
