package uk.co.whitbread.reservation.infrastructure.rest.client.ohip;

import static java.util.stream.Collectors.toMap;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Base64;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.pdfbox.Loader;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDDocumentInformation;
import org.springframework.util.CollectionUtils;
import reactor.core.publisher.Flux;
import uk.co.whitbread.cdh.adapter.service.generated.models.reservationSearch.AddressDto;
import uk.co.whitbread.cdh.adapter.service.generated.models.reservationSearch.GetEmployeeResponseDto;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.ClassificationsDto;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.DepositFoliosRequestDto;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.PackagesResponseDto;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.RatePlanDto;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.RatePlansResponseDto;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.ReservationPreferencesRequestDto;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.UpdateReservationsRequestDto;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.basket.BasketDto;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.basket.BasketItemDto;
import uk.co.whitbread.reservation.domain.model.availability.in.HotelAvailabilityByIdsRequest;
import uk.co.whitbread.reservation.domain.model.availability.in.HotelAvailabilityByIdsV2Request;
import uk.co.whitbread.reservation.domain.model.availability.out.HotelAvailabilityByIds;
import uk.co.whitbread.reservation.domain.model.availability.out.HotelAvailabilityByIdsV2;
import uk.co.whitbread.reservation.domain.model.feature.FeatureFlag;
import uk.co.whitbread.reservation.domain.model.feature.UnleashWrapper;
import uk.co.whitbread.reservation.domain.model.in.AmendDistributionSingleCallRequest;
import uk.co.whitbread.reservation.domain.model.in.AmendSummaryAmountRequest;
import uk.co.whitbread.reservation.domain.model.in.AttachReservationProfileRequest;
import uk.co.whitbread.reservation.domain.model.in.BookerDetailsCnpRequest;
import uk.co.whitbread.reservation.domain.model.in.BookingChannel;
import uk.co.whitbread.reservation.domain.model.in.BusinessItemsRequest;
import uk.co.whitbread.reservation.domain.model.in.CancelReservationRequest;
import uk.co.whitbread.reservation.domain.model.in.CompanyQuestionAndAnswerDetailsRequest;
import uk.co.whitbread.reservation.domain.model.in.ConfirmAmendOnReservationsRequest;
import uk.co.whitbread.reservation.domain.model.in.ConfirmReservationRequest;
import uk.co.whitbread.reservation.domain.model.in.CopyReservationsRequest;
import uk.co.whitbread.reservation.domain.model.in.CreateMemoRequest;
import uk.co.whitbread.reservation.domain.model.in.DepositFoliosRequest;
import uk.co.whitbread.reservation.domain.model.in.LinkReservationToLeisureCustomerRequest;
import uk.co.whitbread.reservation.domain.model.in.PackagesRequest;
import uk.co.whitbread.reservation.domain.model.in.PreCheckInRequest;
import uk.co.whitbread.reservation.domain.model.in.ReservationFileAttachmentRequest;
import uk.co.whitbread.reservation.domain.model.in.ReservationGuestRequest;
import uk.co.whitbread.reservation.domain.model.in.ReservationPackagesRequest;
import uk.co.whitbread.reservation.domain.model.in.ReservationPackagesScheduledRequest;
import uk.co.whitbread.reservation.domain.model.in.ReservationPreferencesRequest;
import uk.co.whitbread.reservation.domain.model.in.ReservationProfiles;
import uk.co.whitbread.reservation.domain.model.in.ReservationRequest;
import uk.co.whitbread.reservation.domain.model.in.SearchBookingsRequest;
import uk.co.whitbread.reservation.domain.model.in.SpecialRequests;
import uk.co.whitbread.reservation.domain.model.in.StayingGuest;
import uk.co.whitbread.reservation.domain.model.in.UpdateBookerEmailRequest;
import uk.co.whitbread.reservation.domain.model.in.UpdateCancellationPoliciesRequest;
import uk.co.whitbread.reservation.domain.model.in.UpdateDiscountRequest;
import uk.co.whitbread.reservation.domain.model.in.UpdateReasonForStayRequest;
import uk.co.whitbread.reservation.domain.model.in.UpdateRequest;
import uk.co.whitbread.reservation.domain.model.in.UpdateReservationAlertsRequest;
import uk.co.whitbread.reservation.domain.model.in.UpdateReservationCcAgentIdRequest;
import uk.co.whitbread.reservation.domain.model.in.UpdateReservationOverrideReasonsRequest;
import uk.co.whitbread.reservation.domain.model.in.UpdateReservationPackagesByIdRequest;
import uk.co.whitbread.reservation.domain.model.in.UpdateReservationSingleCallRequest;
import uk.co.whitbread.reservation.domain.model.in.UpdateReservationUdfsRequest;
import uk.co.whitbread.reservation.domain.model.in.UpdateReservationsRequest;
import uk.co.whitbread.reservation.domain.model.out.AmendSummaryAmountResponse;
import uk.co.whitbread.reservation.domain.model.out.BookingAllowancesResponse;
import uk.co.whitbread.reservation.domain.model.out.CancelInformationResponse;
import uk.co.whitbread.reservation.domain.model.out.CancelReservationResponse;
import uk.co.whitbread.reservation.domain.model.out.CancellationPoliciesResponse;
import uk.co.whitbread.reservation.domain.model.out.ConfirmReservationResponse;
import uk.co.whitbread.reservation.domain.model.out.CopyReservationsResponse;
import uk.co.whitbread.reservation.domain.model.out.DepositFoliosResponse;
import uk.co.whitbread.reservation.domain.model.out.DepositsResponse;
import uk.co.whitbread.reservation.domain.model.out.DonationPackagesResponse;
import uk.co.whitbread.reservation.domain.model.out.HotelInformationResponse;
import uk.co.whitbread.reservation.domain.model.out.MarketingPreferencesResponse;
import uk.co.whitbread.reservation.domain.model.out.MemosResponse;
import uk.co.whitbread.reservation.domain.model.out.OhipReservationResponse;
import uk.co.whitbread.reservation.domain.model.out.PackagesResponse;
import uk.co.whitbread.reservation.domain.model.out.PackagesSelection;
import uk.co.whitbread.reservation.domain.model.out.PreCheckInResponse;
import uk.co.whitbread.reservation.domain.model.out.RatePlansResponse;
import uk.co.whitbread.reservation.domain.model.out.ReservationByBasketRefResponse;
import uk.co.whitbread.reservation.domain.model.out.ReservationByIdDetailsResponse;
import uk.co.whitbread.reservation.domain.model.out.ReservationByIdResponse;
import uk.co.whitbread.reservation.domain.model.out.ReservationsDetailsEnhancedResponse;
import uk.co.whitbread.reservation.domain.model.out.ReservationsDetailsResponse;
import uk.co.whitbread.reservation.domain.model.out.ReservationsPackagesResponse;
import uk.co.whitbread.reservation.domain.model.out.RoomsSelectionsByReservation;
import uk.co.whitbread.reservation.domain.model.out.SaveReservationResponse;
import uk.co.whitbread.reservation.domain.model.out.SearchBookingsResponse;
import uk.co.whitbread.reservation.domain.ports.secondary.HotelReservationOhipOutPort;
import uk.co.whitbread.reservation.infrastructure.rest.client.basket.service.BasketClient;
import uk.co.whitbread.reservation.infrastructure.rest.client.cdh.mapper.CdhAddressMapper;
import uk.co.whitbread.reservation.infrastructure.rest.client.cdh.mapper.CdhEmployeeMapper;
import uk.co.whitbread.reservation.infrastructure.rest.client.cdh.service.CdhAdapterClient;
import uk.co.whitbread.reservation.infrastructure.rest.client.ohip.mapper.AmendDistributionSingleCallRequestOhipMapper;
import uk.co.whitbread.reservation.infrastructure.rest.client.ohip.mapper.AttachReservationProfileRequestOhipMapper;
import uk.co.whitbread.reservation.infrastructure.rest.client.ohip.mapper.BusinessItemsRequestOhipMapper;
import uk.co.whitbread.reservation.infrastructure.rest.client.ohip.mapper.CancelReservationRequestOhipMapper;
import uk.co.whitbread.reservation.infrastructure.rest.client.ohip.mapper.CancelReservationResponseOhipMapper;
import uk.co.whitbread.reservation.infrastructure.rest.client.ohip.mapper.CancelResponseOhipMapper;
import uk.co.whitbread.reservation.infrastructure.rest.client.ohip.mapper.CancellationPoliciesOhipMapper;
import uk.co.whitbread.reservation.infrastructure.rest.client.ohip.mapper.CompanyQuestionAndAnswerOhipMapper;
import uk.co.whitbread.reservation.infrastructure.rest.client.ohip.mapper.ConfirmAmendRequestOhipMapper;
import uk.co.whitbread.reservation.infrastructure.rest.client.ohip.mapper.ConfirmReservationRequestOhipMapper;
import uk.co.whitbread.reservation.infrastructure.rest.client.ohip.mapper.ConfirmReservationResponseOhipMapper;
import uk.co.whitbread.reservation.infrastructure.rest.client.ohip.mapper.CopyReservationsRequestOhipMapper;
import uk.co.whitbread.reservation.infrastructure.rest.client.ohip.mapper.CopyReservationsResponseOhipMapper;
import uk.co.whitbread.reservation.infrastructure.rest.client.ohip.mapper.DepositFoliosResponseOhipMapper;
import uk.co.whitbread.reservation.infrastructure.rest.client.ohip.mapper.DepositsResponseOhipMapper;
import uk.co.whitbread.reservation.infrastructure.rest.client.ohip.mapper.DonationPackagesResponseOhipMapper;
import uk.co.whitbread.reservation.infrastructure.rest.client.ohip.mapper.HotelAvailabilityMapper;
import uk.co.whitbread.reservation.infrastructure.rest.client.ohip.mapper.HotelAvailabilityRequestMapper;
import uk.co.whitbread.reservation.infrastructure.rest.client.ohip.mapper.HotelInformationOhipMapper;
import uk.co.whitbread.reservation.infrastructure.rest.client.ohip.mapper.LinkReservationToLeisureCustomerRequestOhipMapper;
import uk.co.whitbread.reservation.infrastructure.rest.client.ohip.mapper.MarketingPreferencesResponseOhipMapper;
import uk.co.whitbread.reservation.infrastructure.rest.client.ohip.mapper.MemosOhipMapper;
import uk.co.whitbread.reservation.infrastructure.rest.client.ohip.mapper.PreCheckInRequestOhipMapper;
import uk.co.whitbread.reservation.infrastructure.rest.client.ohip.mapper.RatePlansResponseOhipMapper;
import uk.co.whitbread.reservation.infrastructure.rest.client.ohip.mapper.ReservationBookerRequestOhipMapper;
import uk.co.whitbread.reservation.infrastructure.rest.client.ohip.mapper.ReservationByBasketRefOhipResponseMapper;
import uk.co.whitbread.reservation.infrastructure.rest.client.ohip.mapper.ReservationFileAttachmentRequestOhipMapper;
import uk.co.whitbread.reservation.infrastructure.rest.client.ohip.mapper.ReservationGuestRequestOhipMapper;
import uk.co.whitbread.reservation.infrastructure.rest.client.ohip.mapper.ReservationOhipMapper;
import uk.co.whitbread.reservation.infrastructure.rest.client.ohip.mapper.ReservationRequestOhipMapper;
import uk.co.whitbread.reservation.infrastructure.rest.client.ohip.mapper.ReservationResponseOhipMapper;
import uk.co.whitbread.reservation.infrastructure.rest.client.ohip.mapper.ReservationsPackagesOhipMapper;
import uk.co.whitbread.reservation.infrastructure.rest.client.ohip.mapper.SearchBookingsRequestOhipMapper;
import uk.co.whitbread.reservation.infrastructure.rest.client.ohip.mapper.SearchBookingsResponseOhipMapper;
import uk.co.whitbread.reservation.infrastructure.rest.client.ohip.mapper.SpecialRequestsOhipMapper;
import uk.co.whitbread.reservation.infrastructure.rest.client.ohip.mapper.UpdateBookerEmailRequestOhipMapper;
import uk.co.whitbread.reservation.infrastructure.rest.client.ohip.mapper.UpdateDiscountRequestOhipMapper;
import uk.co.whitbread.reservation.infrastructure.rest.client.ohip.mapper.UpdatePackagesRequestOhipMapper;
import uk.co.whitbread.reservation.infrastructure.rest.client.ohip.mapper.UpdatePreferencesRequestOhipMapper;
import uk.co.whitbread.reservation.infrastructure.rest.client.ohip.mapper.UpdateRateCodeRequestOhipMapper;
import uk.co.whitbread.reservation.infrastructure.rest.client.ohip.mapper.UpdateReasonForStayOhipMapper;
import uk.co.whitbread.reservation.infrastructure.rest.client.ohip.mapper.UpdateReservationCcAgentIdRequestOhipMapper;
import uk.co.whitbread.reservation.infrastructure.rest.client.ohip.mapper.UpdateReservationOverrideReasonsRequestOhipMapper;
import uk.co.whitbread.reservation.infrastructure.rest.client.ohip.mapper.UpdateReservationRequestOhipMapper;
import uk.co.whitbread.reservation.infrastructure.rest.client.ohip.mapper.UpdateReservationScheduledMapper;
import uk.co.whitbread.reservation.infrastructure.rest.client.ohip.mapper.UpdateRoomTypeOhipMapper;
import uk.co.whitbread.reservation.infrastructure.rest.client.ohip.service.CacheReservationResponseHelper;
import uk.co.whitbread.reservation.infrastructure.rest.client.ohip.service.HandleCiolRevertHelper;
import uk.co.whitbread.reservation.infrastructure.rest.client.ohip.service.OhipAdapterClient;
import uk.co.whitbread.reservation.infrastructure.rest.client.ohip.service.OhipAdapterTimeoutConfiguredClient;
import uk.co.whitbread.reservation.infrastructure.rest.client.packages.mapper.PackagesRequestMapper;
import uk.co.whitbread.reservation.infrastructure.rest.client.packages.mapper.PackagesResponseMapper;
import uk.co.whitbread.reservation.infrastructure.rest.client.rules.service.properties.RulesAdapterProperties;
import uk.co.whitbread.reservation.infrastructure.rest.controller.amend.model.out.HotelAvailabilityByIdsDto;
import uk.co.whitbread.reservation.infrastructure.rest.controller.amend.model.out.HotelAvailabilityDto;
import uk.co.whitbread.reservation.infrastructure.rest.controller.amend.model.out.RoomRateDto;

@Slf4j
@RequiredArgsConstructor
public class HotelReservationOhipOutPortImpl implements HotelReservationOhipOutPort {

  private static final int MAX_FILE_SIZE_MB = 10;
  private static final String CONTROL_CHARACTER_REGEX = "[\\\\p{Cntrl}]+";
  private static final int MAX_FILE_SIZE_BYTES = MAX_FILE_SIZE_MB * 1024 * 1024;
  private static final List<String> EXTRAS_PACKAGES = Arrays.asList("HSCKIN", "HSCOU2", "FI24HR", "DBPROS",
          "HSCKIF", "FHSCOU2", "FIFR24");
  private final OhipAdapterClient ohipAdapterClient;
  private final CdhAdapterClient cdhAdapterClient;
  private final ReservationResponseOhipMapper reservationResponseOhipMapper;
  private final ReservationRequestOhipMapper reservationRequestOhipMapper;
  private final ConfirmReservationResponseOhipMapper confirmReservationResponseOhipMapper;
  private final ConfirmReservationRequestOhipMapper confirmReservationRequestOhipMapper;
  private final ReservationByBasketRefOhipResponseMapper reservationByBasketRefOhipResponseMapper;
  private final ReservationGuestRequestOhipMapper reservationGuestRequestOhipMapper;
  private final BasketClient basketClient;
  private final UpdatePackagesRequestOhipMapper updatePackagesRequestOhipMapper;
  private final UpdateRateCodeRequestOhipMapper updateRateCodeRequestOhipMapper;
  private final ReservationsPackagesOhipMapper reservationsPackagesOhipMapper;
  private final CancelResponseOhipMapper cancelResponseOhipMapper;
  private final CancelReservationRequestOhipMapper cancelReservationOhipRequestMapper;
  private final CancelReservationResponseOhipMapper cancelReservationOhipResponseMapper;
  private final UpdateDiscountRequestOhipMapper updateDiscountRequestOhipMapper;
  private final HotelInformationOhipMapper hotelInformationOhipMapper;
  private final SearchBookingsRequestOhipMapper searchBookingsRequestOhipMapper;
  private final SearchBookingsResponseOhipMapper searchBookingsResponseOhipMapper;
  private final UpdateReasonForStayOhipMapper updateReasonForStayOhipMapper;
  private final CompanyQuestionAndAnswerOhipMapper companyQuestionAndAnswerOhipMapper;
  private final BusinessItemsRequestOhipMapper businessItemsRequestOhipMapper;
  private final SpecialRequestsOhipMapper specialRequestsOhipMapper;
  private final UpdateReservationOverrideReasonsRequestOhipMapper updateReservationOverrideReasonsRequestOhipMapper;
  private final UpdateReservationCcAgentIdRequestOhipMapper updateReservationCcAgentIdRequestOhipMapper;
  private final DepositsResponseOhipMapper depositsResponseOhipMapper;
  private final CancellationPoliciesOhipMapper cancellationPoliciesOhipMapper;
  private final MarketingPreferencesResponseOhipMapper marketingPreferencesResponseOhipMapper;
  private final CopyReservationsRequestOhipMapper copyReservationsRequestOhipMapper;
  private final CopyReservationsResponseOhipMapper copyReservationsResponseOhipMapper;
  private final ConfirmAmendRequestOhipMapper confirmAmendRequestOhipMapper;
  private final AmendDistributionSingleCallRequestOhipMapper amendDistributionSingleCallRequestOhipMapper;
  private final UpdateReservationRequestOhipMapper updateReservationRequestOhipMapper;
  private final ReservationOhipMapper reservationOhipMapper;
  private final RatePlansResponseOhipMapper ratePlansResponseOhipMapper;
  private final DonationPackagesResponseOhipMapper donationPackagesResponseOhipMapper;
  private final PackagesRequestMapper packagesRequestMapper;
  private final PackagesResponseMapper packagesResponseMapper;
  private final ReservationBookerRequestOhipMapper reservationBookerRequestOhipMapper;
  private final DepositFoliosResponseOhipMapper depositFoliosResponseOhipMapper;
  private final RulesAdapterProperties rulesAdapterProperties;
  private final UpdateBookerEmailRequestOhipMapper updateBookerEmailRequestOhipMapper;
  private final MemosOhipMapper memosOhipMapper;
  private final AttachReservationProfileRequestOhipMapper attachReservationProfileRequestOhipMapper;
  private final ReservationFileAttachmentRequestOhipMapper reservationFileAttachmentRequestOhipMapper;
  private final PreCheckInRequestOhipMapper preCheckInRequestOhipMapper;
  private final HotelAvailabilityRequestMapper requestMapper;
  private final HotelAvailabilityMapper availabilityMapper;
  private final OhipAdapterTimeoutConfiguredClient ohipAdapterTimeoutConfiguredClient;
  private final UpdateReservationScheduledMapper updateReservationScheduledMapper;
  private final LinkReservationToLeisureCustomerRequestOhipMapper linkReservationToLeisureCustomerRequestOhipMapper;
  private final UpdatePreferencesRequestOhipMapper updatePreferencesRequestOhipMapper;
  private final CdhEmployeeMapper cdhEmployeeMapper;
  private final CdhAddressMapper cdhAddressMapper;
  private final HandleCiolRevertHelper handleCiolRevertHelper;
  private final CacheReservationResponseHelper cacheReservationResponseHelper;
  private final UpdateRoomTypeOhipMapper updateRoomTypeOhipMapper;
  private final UnleashWrapper<FeatureFlag> unleashWrapper;

  @Override
  public OhipReservationResponse createReservation(String hotelId,
      ReservationRequest createReservationRequest, String basketReference) {

    var ohipResponse = ohipAdapterClient.createReservation(
        reservationRequestOhipMapper.toDto(createReservationRequest));
    cacheReservationResponseHelper.cacheReservationRequest(createReservationRequest, basketReference, hotelId);
    return reservationResponseOhipMapper.toModel(ohipResponse);
  }

  @Override
  public ReservationsDetailsResponse getReservationsByBasketReference(
      String hotelId,
      String basketReference, int limit, int offset) {
    var ohipResponse = ohipAdapterClient.getReservationsByBasketReference(hotelId, basketReference,
        limit, offset);
    return reservationResponseOhipMapper.toModel(ohipResponse);
  }

  @Override
  public ReservationsPackagesResponse getReservationsPackagesByBasketRef(String hotelId,
      String basketReferenceId,
      boolean mealInclusiveRate) {
    log.debug(
        "Entered getReservationsPackagesByBasketRef with hotelId='{}', basketReference='{}' (user input)",
        sanitize(hotelId, CONTROL_CHARACTER_REGEX),
        sanitize(basketReferenceId, CONTROL_CHARACTER_REGEX));

    var basketResponse = basketClient.sendGetBasket(basketReferenceId).getT1();
    List<String> reservationIds = new ArrayList<>(extractReservationIds(basketResponse));

    var reservationsPackagesResponseDto = ohipAdapterClient
        .getReservationsPackagesByIdsRequest(hotelId, reservationIds,
            mealInclusiveRate);

    var reservationsPackagesResponse =
        reservationsPackagesOhipMapper.toModel(reservationsPackagesResponseDto);
    if (Objects.nonNull(reservationsPackagesResponse.getRoomsSelections())) {
      List<String> reservationIdsList = new ArrayList<>(reservationIds);
      List<RoomsSelectionsByReservation> roomsSelectionsList = reservationsPackagesResponse.getRoomsSelections();
      int minSize = Math.min(reservationIdsList.size(), roomsSelectionsList.size());
      for (int i = 0; i < minSize; i++) {
        roomsSelectionsList.get(i).setReservationId(reservationIdsList.get(i));
      }
    }

    //workaround for ECI/LCO Amend https://whitbreadis.atlassian.net/browse/DNRQ-72637
    if (Objects.nonNull(basketResponse.getLinkAmendReservations())
        && !basketResponse.getLinkAmendReservations().isEmpty()) {
      var roomsSelectionsAmendExtras = getExtrasPackagesBeforeAmend(basketResponse, hotelId, reservationIds);
      reservationsPackagesResponse.setRoomsSelectionsAmendExtras(roomsSelectionsAmendExtras);
    }

    return reservationsPackagesResponse;
  }

  @Override
  public ConfirmReservationResponse confirmReservation(
      ConfirmReservationRequest confirmReservationRequest) {
    var ohipResponse = ohipAdapterClient.sendConfirmReservationRequest(
        confirmReservationRequestOhipMapper.toDto(confirmReservationRequest));
    return confirmReservationResponseOhipMapper.toModel(ohipResponse);
  }

  @Override
  public ReservationByBasketRefResponse getReservationsByIds(
      String hotelId, List<String> reservationsIds,
      Boolean priceBreakdownNeeded) {
    return getReservationsByIds(hotelId, reservationsIds, priceBreakdownNeeded, false);
  }

  @Override
  public ReservationByBasketRefResponse getReservationsByIds(
      String hotelId, List<String> reservationsIds,
      Boolean priceBreakdownNeeded, Boolean operaUiCreated) {
    return getReservationsByIds(hotelId, reservationsIds, priceBreakdownNeeded, operaUiCreated, true);
  }

  @Override
  public ReservationByBasketRefResponse getReservationsByIds(String hotelId,
      List<String> reservationsIds, Boolean priceBreakdownNeeded, Boolean operaUiCreated,
      Boolean rateInfoNeeded) {
    var ohipResponse = ohipAdapterClient.sendGetReservationsByIds(hotelId, reservationsIds,
        priceBreakdownNeeded, operaUiCreated, rateInfoNeeded);
    var ohipModelResponse = reservationByBasketRefOhipResponseMapper.toModel(ohipResponse);
    if (!unleashWrapper.isEnabled(
        unleashWrapper.featureFlag().getMobilePreRegisteredRepurpose())) {
      ohipModelResponse.getReservationByIdList().forEach(r -> r.setDeRegCardCompleted(false));
    }
    return ohipModelResponse;
  }

  @Override
  public SaveReservationResponse updateReservationPackages(
      ReservationPackagesRequest reservationPackagesRequest) {
    var basketResponse = basketClient.sendGetBasket(
        reservationPackagesRequest.getBasketReference()).getT1();

    var saveReservationResponse = new SaveReservationResponse(
        reservationPackagesRequest.getBasketReference());

    ohipAdapterClient.sendUpdateReservationPackagesRequest(
        updatePackagesRequestOhipMapper.toDto(reservationPackagesRequest, basketResponse));
    return saveReservationResponse;
  }

  @Override
  public SaveReservationResponse updateReservationPackagesByReservationId(
      UpdateReservationPackagesByIdRequest updateReservationPackagesByIdRequest) {

    var saveReservationResponse = new SaveReservationResponse(
        updateReservationPackagesByIdRequest.getBasketReference());

    var reservationPackagesRequestDto = updatePackagesRequestOhipMapper.toDto(
        updateReservationPackagesByIdRequest);
    ohipAdapterClient.sendUpdateReservationPackagesRequest(reservationPackagesRequestDto);

    return saveReservationResponse;
  }

  @Override
  public void createReservationGuest(ReservationGuestRequest reservationGuestRequest) {
    populateGuestAddresses(reservationGuestRequest);
    ohipAdapterClient.sendReservationGuestRequest(
        reservationGuestRequestOhipMapper.toDto(reservationGuestRequest));
  }

  private void populateGuestAddresses(ReservationGuestRequest reservationGuestRequest) {
    String companyAccountId = reservationGuestRequest.getCompanyAccountId();
    if (Objects.nonNull(companyAccountId)) {
      reservationGuestRequest.getStayingGuests().stream().map(StayingGuest::getStayingGuestDetails)
          .forEach(guest -> {
            String employeeAccountId = guest.getEmployeeAccountId();
            if (StringUtils.isNotBlank(employeeAccountId) && Objects.isNull(guest.getAddress())) {
              GetEmployeeResponseDto employee = cdhAdapterClient.getEmployee(
                  cdhEmployeeMapper.toGetEmployeeRequestDto(BookingChannel.BB_BOOKING_CHANNEL,
                      reservationGuestRequest.getBooker().getEmailAddress(), companyAccountId,
                      employeeAccountId));
              AddressDto address = employee.getAddress();
              if (Objects.nonNull(address)) {
                guest.setAddress(cdhAddressMapper.toStayingGuestAddressModel(address));
              }
            }
          });
    }
  }

  @Override
  public CancelInformationResponse getCancelInformation(
      String hotelId, Set<String> reservationsIds,
      String userDateTime) {
    var cancelResponse = ohipAdapterClient.sendGetCancelInformationRequest(hotelId, reservationsIds,
        userDateTime);
    return cancelResponseOhipMapper.toModel(cancelResponse);
  }


  @Override
  public SaveReservationResponse updateReservationRateCode(
      UpdateRequest updateRateCodeRequest) {

    var basketResponse = basketClient.sendGetBasket(updateRateCodeRequest.getBasketReferenceId())
        .getT1();
    ohipAdapterClient.sendUpdateRateCodeRequest(
        updateRateCodeRequestOhipMapper.toDto(updateRateCodeRequest, basketResponse));
    return new SaveReservationResponse(updateRateCodeRequest.getBasketReferenceId());
  }

  @Override
  public SaveReservationResponse updateRoomType(
      UpdateRequest updateRoomTypeRequest) {

    ohipAdapterClient.sendUpdateRoomTypeRequest(
        updateRoomTypeOhipMapper.toDto(updateRoomTypeRequest));
    return new SaveReservationResponse(updateRoomTypeRequest.getBasketReferenceId());
  }

  @Override
  public CancelReservationResponse cancelReservation(
      CancelReservationRequest cancelReservationRequest,
      List<DepositFoliosResponse> prepaidDeposits) {
    var cancelReservationResponseDto = ohipAdapterClient.sendCancelReservationRequest(
        cancelReservationOhipRequestMapper.toDto(cancelReservationRequest, prepaidDeposits));

    return cancelReservationOhipResponseMapper.toModel(cancelReservationResponseDto,
        CollectionUtils.isEmpty(cancelReservationResponseDto.getCancellationIds())
            ? null
            : cancelReservationRequest.getBasketReference());
  }

  @Override
  public HotelInformationResponse getHotelInformation(String hotelId) {
    var hotelInformationResponseDto = ohipAdapterClient.sendGetHotelInformationRequest(hotelId);
    return hotelInformationOhipMapper.toModel(hotelInformationResponseDto);
  }

  private Set<String> extractReservationIds(BasketDto basketDto) {
    List<BasketItemDto> basketDtoItems = basketDto.getItems();
    log.debug("Entered extractReservationIds for items with size={}", basketDtoItems.size());

    if (CollectionUtils.isEmpty(basketDtoItems)) {
      return Collections.emptySet();
    }
    var reservationIds = Optional.ofNullable(basketDtoItems)
        .map(item -> item.stream()
            .map(BasketItemDto::getSourceId)
            .collect(Collectors.toSet()))
        .orElse(Collections.emptySet());
    reservationIds.forEach(sourceId -> log.trace("Extracted reservationId={}", sourceId));
    return reservationIds;
  }

  @Override
  public ReservationsDetailsEnhancedResponse getReservationsByExternalId(String resNo) {
    var ohipResponse = ohipAdapterClient.sendGetReservationsByExternalReferenceId(resNo);
    return reservationResponseOhipMapper.toModel(ohipResponse);
  }

  @Override
  public ReservationByIdDetailsResponse getReservationsByReservationId(String resId,
                                                                       String hotelId) {
    var ohipResponse = ohipAdapterClient.sendGetReservationsByReservationId(resId, hotelId);
    return reservationResponseOhipMapper.toModel(ohipResponse);
  }

  @Override
  public void updateDiscount(UpdateDiscountRequest updateDiscountRequest) {
    ohipAdapterClient.sendUpdateDiscountRequest(
        updateDiscountRequestOhipMapper.toDto(updateDiscountRequest));
  }

  @Override
  public void updateCompanyQuestionAndAnswerDetails(
      CompanyQuestionAndAnswerDetailsRequest
          companyQuestionAndAnswerDetailsRequest) {
    ohipAdapterClient.sendUpdateCompanyQuestionAndAnswerDetailsRequests(
        companyQuestionAndAnswerOhipMapper.toDto(companyQuestionAndAnswerDetailsRequest));
  }

  @Override
  public void updateBusinessItems(BusinessItemsRequest businessItemsRequest) {
    ohipAdapterClient.sendUpdateBusinessItemsRequest(
        businessItemsRequestOhipMapper.toDto(businessItemsRequest));
  }

  @Override
  public void updateSpecialRequests(SpecialRequests specialRequestsEntity) {
    ohipAdapterClient.sendUpdateSpecialRequests(
        specialRequestsOhipMapper.toDto(specialRequestsEntity));
  }

  @Override
  public SearchBookingsResponse searchBookings(SearchBookingsRequest searchBookingsRequest) {
    var searchBookingResponse = ohipAdapterClient.sendSearchBookings(
        searchBookingsRequestOhipMapper.toDto(searchBookingsRequest));
    return searchBookingsResponseOhipMapper.toModel(searchBookingResponse);
  }

  public void updateReasonForStay(UpdateReasonForStayRequest updateReasonForStayRequest) {
    ohipAdapterClient.sendUpdateReasonForStayRequest(
        updateReasonForStayOhipMapper.toDto(updateReasonForStayRequest));
  }

  @Override
  public void updateReservationOverrideReasons(
      UpdateReservationOverrideReasonsRequest updateReservationOverrideReasonsRequest) {
    ohipAdapterClient.sendUpdateReservationOverrideReasons(
        updateReservationOverrideReasonsRequestOhipMapper.toDto(
            updateReservationOverrideReasonsRequest));
  }

  @Override
  public void updateReservationCcAgentId(
      UpdateReservationCcAgentIdRequest updateReservationCcAgentIdRequest) {
    ohipAdapterClient.sendUpdateReservationCcAgentId(
        updateReservationCcAgentIdRequestOhipMapper.toDto(
            updateReservationCcAgentIdRequest));
  }


  @Override
  public DepositsResponse getDepositsForReservationId(String hotelId, String reservationId) {
    var retrievedDeposits = ohipAdapterClient
        .getDepositsByReservationId(hotelId, reservationId);
    return depositsResponseOhipMapper.toModel(retrievedDeposits);
  }

  @Override
  public CancellationPoliciesResponse getCancellationPolicies(
      Set<String> reservationIds, String hotelId, String rateCode, String arrivalDate) {
    var cancellationPoliciesResponse = ohipAdapterClient.getCancellationPolicies(
        reservationIds, hotelId, rateCode, arrivalDate);

    return cancellationPoliciesOhipMapper.toModel(cancellationPoliciesResponse);
  }

  public MarketingPreferencesResponse getMarketingPreferences(String hotelId,
                                                              String reservationId) {
    var marketingPreferencesResponseDto = ohipAdapterClient
        .getMarketingPreferences(hotelId, reservationId);
    return marketingPreferencesResponseOhipMapper.toModel(marketingPreferencesResponseDto);
  }

  @Override
  public CopyReservationsResponse copyReservations(
      CopyReservationsRequest copyReservationsRequest) {
    var reservations = ohipAdapterClient.sendCopyReservationsRequest(
        copyReservationsRequestOhipMapper.toDto(copyReservationsRequest));
    return copyReservationsResponseOhipMapper.toModel(reservations);
  }

  @Override
  public AmendSummaryAmountResponse getAmendSummaryDetails(
      AmendSummaryAmountRequest amendSummaryAmountRequest) {
    return ohipAdapterClient.getAmendSummaryDetails(amendSummaryAmountRequest);
  }

  @Override
  public HotelAvailabilityByIdsV2 getHotelAvailabilityV2(
      HotelAvailabilityByIdsV2Request request) {

    var ohipRequest = requestMapper.toAvailabilityByIdsOhipV2Model(request);
    var hotelAvailabilityByIds = ohipAdapterClient.getHotelAvailabilityByIdsV2(ohipRequest);
    return availabilityMapper.toAvailabilityByIdsV2Model(hotelAvailabilityByIds);
  }

  @Override
  public HotelAvailabilityByIds getHotelAvailability(
      HotelAvailabilityByIdsRequest request) {
    log.debug("Entered getHotelAvailabilitiesByIds with hotelAvailabilitiesByIdsRequest={}",
        request);

    var ohipRequest = requestMapper.toAvailabilityByIdsOhipModel(request);
    var hotelAvailabilityByIds = ohipAdapterClient.getHotelAvailabilityByIds(ohipRequest);

    setHotelAvailabilityByIdsDisplaySets(hotelAvailabilityByIds);
    return availabilityMapper.toAvailabilityByIdsModel(hotelAvailabilityByIds);
  }

  private void setHotelAvailabilityByIdsDisplaySets(
      HotelAvailabilityByIdsDto hotelAvailabilityByIdsDto) {

    // Set for each each ratePlan the corresponding rateDisplaySet
    Flux.fromIterable(hotelAvailabilityByIdsDto.getHotelAvailability())
        .flatMap(
            hotelAvailabilityDto -> ohipAdapterClient.getRatePlans(getRatePlansString(hotelAvailabilityDto),
                hotelAvailabilityDto.getHotelId()))
        .collectList()
        .map(ratePlansResponseDtos -> {
          hotelAvailabilityByIdsDto.getHotelAvailability()
              .forEach(
                  hotelAvailabilityDto -> hotelAvailabilityDto.getRoomRates()
                      .forEach(roomRateDto -> roomRateDto.setRateDisplaySet(
                          getDisplaySetsByRatePlans(ratePlansResponseDtos,
                              hotelAvailabilityDto).getOrDefault(roomRateDto.getRatePlanCode(),
                                new ClassificationsDto()).getDisplaySet()))
              );
          return hotelAvailabilityByIdsDto;
        }).block();

  }

  private List<String> getRatePlansString(HotelAvailabilityDto hotelAvailabilityDto) {
    return hotelAvailabilityDto.getRoomRates().stream()
        .map(RoomRateDto::getRatePlanCode).toList();
  }

  private Map<String, ClassificationsDto> getDisplaySetsByRatePlans(
      List<RatePlansResponseDto> ratePlansResponseDto, HotelAvailabilityDto hotelAvailabilityDto) {

    return ratePlansResponseDto.stream()
        .map(RatePlansResponseDto::getRatePlans)
        .flatMap(List::stream)
        .filter(ratePlanDto -> ratePlanDto.getHotelId().equals(hotelAvailabilityDto.getHotelId()))
        .collect(toMap(RatePlanDto::getRatePlanCode, RatePlanDto::getClassifications));

  }

  @Override
  public BookingAllowancesResponse getBookingAllowances(String hotelId, String reservationId,
      List<String> basketBookingAllowances) {
    return ohipAdapterClient.getBookingAllowances(hotelId, reservationId, basketBookingAllowances);
  }

  @Override
  public void amendEditRoom(UpdateReservationsRequest updateReservationRequest) {
    var updateReservationRequestDto =
        updateReservationRequestOhipMapper.toDto(updateReservationRequest);
    ohipAdapterClient.sendUpdateReservationAmend(updateReservationRequestDto);
  }

  @Override
  public ReservationByBasketRefResponse confirmAmend(
      ConfirmAmendOnReservationsRequest confirmAmendOnReservationsRequest) {
    return reservationByBasketRefOhipResponseMapper.toModel(ohipAdapterClient.sendConfirmAmend(
        confirmAmendRequestOhipMapper.toRequestDto(confirmAmendOnReservationsRequest)));
  }

  @Override
  public ReservationByBasketRefResponse confirmAmendForSingleCall(
          AmendDistributionSingleCallRequest amendDistributionSingleCallRequest) {
    return reservationByBasketRefOhipResponseMapper.toModel(ohipAdapterClient.sendConfirmAmendForSingleCall(
            amendDistributionSingleCallRequestOhipMapper.toDto(amendDistributionSingleCallRequest)));
  }

  @Override
  public void deleteReservation(String hotelId, String reservationId) {
    ohipAdapterClient.deleteReservationRequest(hotelId, reservationId);
  }

  @Override
  public void updateReservations(UpdateReservationsRequest updateReservationsRequest) {
    log.info("Entered updateReservations with updateReservationsRequest");
    UpdateReservationsRequestDto updateReservationsRequestDto = reservationOhipMapper.toUpdateReservationsRequestDto(
        updateReservationsRequest);
    updateReservationsRequestDto.getTempReservations().getReservationByIdList().forEach(res -> {
      ReservationByIdResponse first = updateReservationsRequest.getTempReservations()
          .getReservationByIdList()
          .stream().filter(reservationByIdResponse ->
              reservationByIdResponse.getReservationId().equals(res.getReservationId()))
          .findFirst().get();
      res.getRoomStay().setAdults(first.getRoomStay().getAdultsNumber());
      res.getRoomStay().setChildren(first.getRoomStay().getChildrenNumber());
    });
    ohipAdapterClient.updateReservations(updateReservationsRequestDto);
  }


  @Override
  public void updateReservationsSingleCall(UpdateReservationsRequest updateReservationsRequest) {
    log.info("Entered updateReservationsSingleCall with updateReservationsRequest={} ",
        updateReservationsRequest);
    UpdateReservationsRequestDto updateReservationsRequestDto = reservationOhipMapper.toUpdateReservationsRequestDto(
        updateReservationsRequest);
    updateReservationsRequestDto.getTempReservations().getReservationByIdList().forEach(res -> {
      ReservationByIdResponse first = updateReservationsRequest.getTempReservations()
          .getReservationByIdList()
          .stream().filter(reservationByIdResponse ->
              reservationByIdResponse.getReservationId().equals(res.getReservationId()))
          .findFirst().get();
      res.getRoomStay().setAdults(first.getRoomStay().getAdultsNumber());
      res.getRoomStay().setChildren(first.getRoomStay().getChildrenNumber());
    });
    Map<String, String> linkAmendReservations = new HashMap<>();
    updateReservationsRequest.getReservations()
        .forEach(reservation -> linkAmendReservations.put(
            reservation.getReservationId(),
            reservation.getReservationId()));
    updateReservationsRequestDto.setLinkAmendReservations(linkAmendReservations);
    ohipAdapterClient.updateReservations(updateReservationsRequestDto);
  }

  @Override
  public RatePlansResponse getRatePlans(List<String> ratePlanCodes, String hotelId) {
    var ratePlansResponseDto = ohipAdapterClient.sendGetRatePlansRequest(ratePlanCodes, hotelId);
    return ratePlansResponseOhipMapper.toModel(ratePlansResponseDto);
  }

  @Override
  public List<String> getWbRoomTypes() {
    return rulesAdapterProperties.getWbRoomTypes();
  }

  @Override
  public ReservationsPackagesResponse getReservationsPackagesByIds(String hotelId,
                                                                   List<String> reservationIds) {
    log.info("Entered getReservationsPackagesByIds with hotelID={}, reservationIds={} ",
        hotelId, reservationIds);
    var reservationsPackagesResponseDto = ohipAdapterClient.getReservationsPackagesByIdsRequest(
        hotelId, reservationIds, false);
    var reservationsPackagesResponse = reservationsPackagesOhipMapper.toModel(
        reservationsPackagesResponseDto);

    List<String> reservationIdsList = new ArrayList<>(reservationIds);
    reservationsPackagesResponse.getRoomsSelections().forEach(roomsSelections -> {
      var reservationIdForPackages = reservationIdsList.get(
          reservationsPackagesResponse.getRoomsSelections().indexOf(roomsSelections));

      roomsSelections.setReservationId(reservationIdForPackages);
    });

    return reservationsPackagesResponse;
  }

  @Override
  public DonationPackagesResponse getCharityPackagesDetails(String hotelId,
                                                            List<String> packageCodes) {
    log.info("Entered getCharityPackagesDetails with hotelID={}, packageCodes={} ",
        hotelId, packageCodes);
    if (CollectionUtils.isEmpty(packageCodes)) {
      return null;
    }
    var donationPackagesResponseDto = ohipAdapterClient.sendCharityPackagesDetailsRequest(hotelId,
        packageCodes);
    return donationPackagesResponseOhipMapper.toModel(donationPackagesResponseDto);
  }

  @Override
  public void movePaymentDetails(String hotelId, Set<String> reservationIds) {
    log.info("Entered movePaymentDetails with hotelID={}, reservationIds={} ",
        hotelId, reservationIds);
    ohipAdapterClient.movePaymentDetails(hotelId, reservationIds);
  }

  @Override
  public DepositFoliosResponse getGeneratedDepositFolios(String hotelId,
                                                         Set<String> reservationIds) {
    log.info("Entered getGeneratedDepositFolios hotelID={}, reservationIds={} ",
        hotelId, reservationIds);
    var depositFolios = ohipAdapterClient.getGeneratedDepositFolios(hotelId, reservationIds);
    return depositFoliosResponseOhipMapper.toModel(depositFolios);

  }

  @Override
  public void updateReservationBooker(BookerDetailsCnpRequest bookerDetailsCnpRequest) {
    log.info("Entered updateReservationBooker with hotelId={}, reservationIds={} ",
        bookerDetailsCnpRequest.getHotelId(), bookerDetailsCnpRequest.getReservationIds());
    var bookerDetailsDto = reservationBookerRequestOhipMapper.toDto(bookerDetailsCnpRequest);
    ohipAdapterClient.sendUpdateBookerDetailsRequest(bookerDetailsDto);
  }

  @Override
  public void updateBookerEmail(UpdateBookerEmailRequest updateBookerEmailRequest) {
    log.info("Entered updateBookerEmail with hotelId={}, reservationIds={} ",
        updateBookerEmailRequest.getHotelId(), updateBookerEmailRequest.getReservationIds());
    var bookerDetailsDto = updateBookerEmailRequestOhipMapper.toDto(updateBookerEmailRequest);
    ohipAdapterClient.sendUpdateBookerEmailRequest(bookerDetailsDto);
  }

  @Override
  public void deleteRoutingInstructions(String hotelId, Set<String> reservationIds) {
    log.info("Entered deleteRoutingInstructions with hotelId={}, reservationIds={} ",
        sanitize(hotelId, CONTROL_CHARACTER_REGEX),
        sanitize(reservationIds.toString(), CONTROL_CHARACTER_REGEX));
    ohipAdapterClient.deleteRoutingInstructions(hotelId, reservationIds);
  }

  public MemosResponse createMemo(CreateMemoRequest createMemoRequest) {
    log.info("Entered createMemo with createMemoRequest={} ", createMemoRequest);
    var memos = ohipAdapterClient.createMemo(memosOhipMapper.toDto(createMemoRequest));
    return memosOhipMapper.toModel(memos);
  }

  public MemosResponse getMemos(String hotelId, Set<String> reservationIds) {
    log.info("Entered getMemos with hotelId={}, reservationIds={} ", hotelId, reservationIds);
    var memos = ohipAdapterClient.getMemos(hotelId, reservationIds);
    return memosOhipMapper.toModel(memos);
  }

  @Override
  public void saveCharges(DepositFoliosResponse depositFolios) {
    DepositFoliosRequestDto dto = depositFoliosResponseOhipMapper.toDto(depositFolios);
    ohipAdapterClient.saveCharges(dto);
  }

  @Override
  public void attachProfileToReservations(
      AttachReservationProfileRequest attachReservationProfileRequest) {
    ohipAdapterClient.attachProfileToReservations(
        attachReservationProfileRequestOhipMapper.toDto(attachReservationProfileRequest));
  }

  @Override
  public PackagesResponse getPackages(PackagesRequest packagesRequest) {
    PackagesResponseDto packagesResponseDto = ohipAdapterClient
        .getPackages(packagesRequestMapper.toDto(packagesRequest));
    return packagesResponseMapper.toModel(packagesResponseDto);
  }

  @Override
  public ConfirmReservationResponse updateReservation(
      UpdateReservationSingleCallRequest updateReservationRequest) {
    var finalReq = reservationRequestOhipMapper.toUpdateReservationDto(updateReservationRequest);
    log.debug("Final Update Reservation Request to OHIP: {}", finalReq);
    var ohipResponse = ohipAdapterClient.sendUpdateReservation(finalReq);
    return confirmReservationResponseOhipMapper.toModel(ohipResponse);
  }

  @Override
  public ReservationProfiles createProfiles(ReservationGuestRequest reservationGuestRequest) {
    var profileIds = ohipAdapterClient.sendCreateProfiles(
        reservationGuestRequestOhipMapper.toDto(reservationGuestRequest));
    return reservationRequestOhipMapper.toCreateProfileModel(profileIds);
  }

  @Override
  public PreCheckInResponse addAttachmentToReservation(
      ReservationFileAttachmentRequest reservationFileAttachmentRequest) {
    log.info("Received request to add attachment to reservation: {}",
        reservationFileAttachmentRequest.getReservationId());

    var roomIds = reservationFileAttachmentRequest.getReservationId();
    var hotelId = reservationFileAttachmentRequest.getHotelId();
    byte[] fileAttachmentBytes = decodeBase64(reservationFileAttachmentRequest.getFileAttachment());
    if (ObjectUtils.isEmpty(fileAttachmentBytes)) {
      handleCiolRevertHelper.initiateCiolRevert(List.of(roomIds), hotelId);
      return buildPreCheckInResponse(
          "File attachment is not a valid base64 string");
    }
    if (fileAttachmentBytes.length > MAX_FILE_SIZE_BYTES) {
      handleCiolRevertHelper.initiateCiolRevert(List.of(roomIds), hotelId);
      return buildPreCheckInResponse(
          "File size exceeds the maximum limit of 10MB");
    }
    if (!isPdfFile(fileAttachmentBytes)) {
      handleCiolRevertHelper.initiateCiolRevert(List.of(roomIds), hotelId);
      return buildPreCheckInResponse(
          "File is not a valid PDF");
    }
    var request = reservationFileAttachmentRequestOhipMapper.toDto(
        reservationFileAttachmentRequest);

    try {
      return ohipAdapterTimeoutConfiguredClient.sendAddFileAttachmentToReservationRequest(request);
    } catch (Exception e) {
      handleCiolRevertHelper.initiateCiolRevert(List.of(request.getReservationId()),
          request.getHotelId());
      throw e;
    }
  }

  private byte[] decodeBase64(String base64String) {
    try {
      return Base64.getDecoder().decode(base64String);
    } catch (IllegalArgumentException e) {
      log.error("Error while trying to decode Base64 string", e);
      return new byte[0];
    }
  }

  private boolean isPdfFile(byte[] fileBytes) {
    if (fileBytes == null || fileBytes.length == 0) {
      return false;
    }
    try (PDDocument document = Loader.loadPDF(fileBytes)) {
      PDDocumentInformation info = document.getDocumentInformation();
      document.close();
      return true;
    } catch (IOException e) {
      log.error("Error loading PDF: {}", e.getMessage());
      return false;
    }
  }

  private PreCheckInResponse buildPreCheckInResponse(String errorMessage) {
    return PreCheckInResponse.builder()
        .status("Error")
        .message(errorMessage)
        .build();
  }

  @Override
  public PreCheckInResponse saveReservationPreCheckIn(PreCheckInRequest preCheckInRequest) {
    var request = preCheckInRequestOhipMapper.toDto(preCheckInRequest);
    try {
      return ohipAdapterClient.sendSaveReservationPreCheckInStatus(request);
    } catch (Exception ex) {
      handleCiolRevertHelper.initiateCiolRevert(List.of(request.getReservationId()),
          request.getHotelId());
      throw ex;
    }
  }

  @Override
  public void updateReservationExternalReference(String hotelId, List<String> reservationIds,
      String externalReference) {

    ohipAdapterClient.updateReservationExternalReference(hotelId, reservationIds, externalReference);
  }

  @Override
  public void deleteRegCardAttachment(String hotelId, String reservationId) {
    ohipAdapterClient.sendDeleteRegCardAttachment(hotelId, reservationId);
  }

  @Override
  public void deleteReservationPreCheckIn(String hotelId, String reservationId) {
    ohipAdapterClient.sendDeleteReservationPreCheckInStatus(hotelId, reservationId);
  }

  @Override
  public void updateReservationPackagesScheduled(
      ReservationPackagesScheduledRequest reservationPackagesRequest, String arrival,
      String departure) {

    var ohipRequest = updateReservationScheduledMapper.toDto(reservationPackagesRequest, arrival,
        departure);
    ohipAdapterClient.sendUpdateReservationPackagesScheduledRequest(ohipRequest);
  }

  @Override
  public void linkReservationToLeisureCustomer(
      LinkReservationToLeisureCustomerRequest linkReservationToLeisureCustomerRequest) {
    ohipAdapterClient.sendLinkReservationToLeisureCustomer(
        linkReservationToLeisureCustomerRequestOhipMapper.toDto(
            linkReservationToLeisureCustomerRequest));
  }

  private List<RoomsSelectionsByReservation> getExtrasPackagesBeforeAmend(BasketDto basketResponse, String hotelId,
      List<String> temporaryReservationIds) {

    List<String> initialReservationIds = getLinkAmendReservations(
            basketResponse.getLinkAmendReservations(), temporaryReservationIds);
    var initialReservationsOhipPackages = ohipAdapterClient
            .getReservationsPackagesByIdsRequest(hotelId, initialReservationIds, false);

    var initialReservationsPackages = reservationsPackagesOhipMapper.toModel(initialReservationsOhipPackages);
    var initialRoomSelections = initialReservationsPackages.getRoomsSelections();

    if (initialRoomSelections == null) {
      return Collections.emptyList();
    }

    List<RoomsSelectionsByReservation> roomsSelectionsAmendExtras = new ArrayList<>(initialRoomSelections);
    for (int i = 0; i < initialRoomSelections.size(); i++) {
      List<PackagesSelection> extraPackages = new ArrayList<>();

      if (Objects.nonNull(initialRoomSelections.get(i).getPackagesSelection())) {
        for (PackagesSelection packageSelection : initialRoomSelections.get(i).getPackagesSelection()) {
          if (EXTRAS_PACKAGES.contains(packageSelection.getId())) {
            extraPackages.add(packageSelection);
          }
        }
      }

      roomsSelectionsAmendExtras.get(i).setPackagesSelection(extraPackages);
      roomsSelectionsAmendExtras.get(i).setReservationId(temporaryReservationIds.get(i));
    }

    return roomsSelectionsAmendExtras;
  }

  private List<String> getLinkAmendReservations(
          Map<String, String> linkAmendReservations, List<String> temporaryReservationIds) {
    List<String> reservationIds = new ArrayList<>();
    for (String reservationId : temporaryReservationIds) {
      linkAmendReservations
              .entrySet()
              .stream()
              .filter(entry -> reservationId.equals(entry.getValue()))
              .map(Map.Entry::getKey)
              .findFirst()
              .ifPresent(reservationIds::add);
    }
    return reservationIds;
  }

  @Override
  public void updateReservationPreferences(
      ReservationPreferencesRequest reservationPreferencesRequest) {
    ReservationPreferencesRequestDto reservationPreferencesRequestDto =
        updatePreferencesRequestOhipMapper.toDto(reservationPreferencesRequest);
    ohipAdapterClient.updateReservationPreferences(reservationPreferencesRequestDto);
  }

  @Override
  public void updateReservationAlerts(UpdateReservationAlertsRequest updateReservationAlertsRequest) {
    ohipAdapterClient.updateReservationAlerts(updateReservationAlertsRequest);
  }

  @Override
  public void updateCancellationPolicies(
      UpdateCancellationPoliciesRequest updateCancellationPoliciesRequest) {

    ohipAdapterClient.updateCancellationPolicies(
        cancellationPoliciesOhipMapper.toDto(updateCancellationPoliciesRequest));
  }

  private String sanitize(final String input, final String regex) {
    return Optional.ofNullable(input)
        .map(c -> c.replaceAll(regex, "").replaceAll("[^A-Za-z0-9_-]", ""))
        .orElse("");
  }

  @Override
  public void updateUdfc20(UpdateReservationUdfsRequest updateReservationUdfsRequest) {
    ohipAdapterClient.updateUdfc20(updateReservationUdfsRequest);
  }

  @Override
  public void saveDepositFolios(DepositFoliosRequest depositFoliosRequest) {
    DepositFoliosRequestDto dto = depositFoliosResponseOhipMapper.toDto(depositFoliosRequest);
    ohipAdapterClient.saveCharges(dto);
  }
}
