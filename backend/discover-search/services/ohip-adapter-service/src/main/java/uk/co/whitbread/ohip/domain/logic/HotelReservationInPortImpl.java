package uk.co.whitbread.ohip.domain.logic;

import static java.util.Objects.nonNull;
import static uk.co.whitbread.ohip.ErrorCode.DIGITAL_NO_RATES_EXCEPTION;
import static uk.co.whitbread.ohip.domain.utils.SanitizingUtils.sanitize;
import static uk.co.whitbread.ohip.infrastructure.rest.client.ohip.config.OhipConstants.BUSINESS_BOOKER_CHANNEL;
import static uk.co.whitbread.ohip.infrastructure.rest.client.ohip.config.OhipConstants.CCUI_CHANNEL;
import static uk.co.whitbread.ohip.infrastructure.rest.client.ohip.config.OhipConstants.DISTRIBUTION_CHANNEL;
import static uk.co.whitbread.ohip.infrastructure.rest.client.ohip.config.OhipConstants.LANGUAGE_ENGLISH;
import static uk.co.whitbread.ohip.infrastructure.rest.client.ohip.config.OhipConstants.PI_CHANNEL;
import static uk.co.whitbread.ohip.infrastructure.rest.client.ohip.config.OhipConstants.UDFC_16;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import uk.co.whitbread.commons.exceptions.utils.ExceptionLogger;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.ChangeReservation;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.ResGuestType;
import uk.co.whitbread.ohip.ErrorCode;
import uk.co.whitbread.ohip.domain.exceptions.PolicyCodeMismatchException;
import uk.co.whitbread.ohip.domain.exceptions.UnavailableRatesException;
import uk.co.whitbread.ohip.domain.logic.utils.GuestInfoUtils;
import uk.co.whitbread.ohip.domain.model.availability.in.AvailabilityRoomSearchCriteria;
import uk.co.whitbread.ohip.domain.model.availability.out.AvailabilityRoomPriceBreakdown;
import uk.co.whitbread.ohip.domain.model.checkin.out.CharacterUDFs;
import uk.co.whitbread.ohip.domain.model.checkin.out.UserDefinedFields;
import uk.co.whitbread.ohip.domain.model.feature.FeatureFlag;
import uk.co.whitbread.ohip.domain.model.feature.UnleashWrapper;
import uk.co.whitbread.ohip.domain.model.reservation.in.AmountType;
import uk.co.whitbread.ohip.domain.model.reservation.in.AttachReservationProfileRequest;
import uk.co.whitbread.ohip.domain.model.reservation.in.BillingAddressRequest;
import uk.co.whitbread.ohip.domain.model.reservation.in.BookerDetailsCnpRequest;
import uk.co.whitbread.ohip.domain.model.reservation.in.BookingChannel;
import uk.co.whitbread.ohip.domain.model.reservation.in.BookingChannelSingleCall;
import uk.co.whitbread.ohip.domain.model.reservation.in.BookingSearchCriteria;
import uk.co.whitbread.ohip.domain.model.reservation.in.BusinessItemsRequest;
import uk.co.whitbread.ohip.domain.model.reservation.in.CancelReservationRequest;
import uk.co.whitbread.ohip.domain.model.reservation.in.CompanyQuestionAndAnswerDetailsRequest;
import uk.co.whitbread.ohip.domain.model.reservation.in.ConfirmAmendForSingleRequest;
import uk.co.whitbread.ohip.domain.model.reservation.in.ConfirmAmendOnReservationsRequest;
import uk.co.whitbread.ohip.domain.model.reservation.in.ConfirmReservationRequest;
import uk.co.whitbread.ohip.domain.model.reservation.in.CopyReservationsRequest;
import uk.co.whitbread.ohip.domain.model.reservation.in.CreateMemoRequest;
import uk.co.whitbread.ohip.domain.model.reservation.in.CustomerType;
import uk.co.whitbread.ohip.domain.model.reservation.in.CustomerTypeSingleCall;
import uk.co.whitbread.ohip.domain.model.reservation.in.EmailInfoType;
import uk.co.whitbread.ohip.domain.model.reservation.in.EmailInfoTypeSingleCall;
import uk.co.whitbread.ohip.domain.model.reservation.in.EmailType;
import uk.co.whitbread.ohip.domain.model.reservation.in.LinkReservationToLeisureCustomerRequest;
import uk.co.whitbread.ohip.domain.model.reservation.in.PersonNameType;
import uk.co.whitbread.ohip.domain.model.reservation.in.PersonNameTypeSingleCall;
import uk.co.whitbread.ohip.domain.model.reservation.in.PersonNameTypeType;
import uk.co.whitbread.ohip.domain.model.reservation.in.PreCheckInRequest;
import uk.co.whitbread.ohip.domain.model.reservation.in.ProfileInfo;
import uk.co.whitbread.ohip.domain.model.reservation.in.ProfileInfoSingleCall;
import uk.co.whitbread.ohip.domain.model.reservation.in.ProfileType;
import uk.co.whitbread.ohip.domain.model.reservation.in.ProfileTypeEmails;
import uk.co.whitbread.ohip.domain.model.reservation.in.ProfileTypeEmailsSingleCall;
import uk.co.whitbread.ohip.domain.model.reservation.in.ProfileTypeSingleCall;
import uk.co.whitbread.ohip.domain.model.reservation.in.RatePlanRoomTypeChangeRequest;
import uk.co.whitbread.ohip.domain.model.reservation.in.RateType;
import uk.co.whitbread.ohip.domain.model.reservation.in.Reservation;
import uk.co.whitbread.ohip.domain.model.reservation.in.ReservationFileAttachmentRequest;
import uk.co.whitbread.ohip.domain.model.reservation.in.ReservationGuestRequest;
import uk.co.whitbread.ohip.domain.model.reservation.in.ReservationGuests;
import uk.co.whitbread.ohip.domain.model.reservation.in.ReservationGuestsSingleCall;
import uk.co.whitbread.ohip.domain.model.reservation.in.ReservationPackagesRequest;
import uk.co.whitbread.ohip.domain.model.reservation.in.ReservationPreferencesRequest;
import uk.co.whitbread.ohip.domain.model.reservation.in.ReservationProfiles;
import uk.co.whitbread.ohip.domain.model.reservation.in.ReservationRequest;
import uk.co.whitbread.ohip.domain.model.reservation.in.ReservationType;
import uk.co.whitbread.ohip.domain.model.reservation.in.RoomOccupancy;
import uk.co.whitbread.ohip.domain.model.reservation.in.RoomRate;
import uk.co.whitbread.ohip.domain.model.reservation.in.RoomStay;
import uk.co.whitbread.ohip.domain.model.reservation.in.RoomsSelections;
import uk.co.whitbread.ohip.domain.model.reservation.in.RoomsSelectionsByReservationId;
import uk.co.whitbread.ohip.domain.model.reservation.in.SpecialRequests;
import uk.co.whitbread.ohip.domain.model.reservation.in.TotalType;
import uk.co.whitbread.ohip.domain.model.reservation.in.UpdateBookerEmailRequest;
import uk.co.whitbread.ohip.domain.model.reservation.in.UpdateCancellationPoliciesRequest;
import uk.co.whitbread.ohip.domain.model.reservation.in.UpdateCancellationPolicyRequest;
import uk.co.whitbread.ohip.domain.model.reservation.in.UpdateCustomReferenceNumberRequest;
import uk.co.whitbread.ohip.domain.model.reservation.in.UpdateDiscountRequest;
import uk.co.whitbread.ohip.domain.model.reservation.in.UpdateReasonForStayRequest;
import uk.co.whitbread.ohip.domain.model.reservation.in.UpdateReservationAlertsRequest;
import uk.co.whitbread.ohip.domain.model.reservation.in.UpdateReservationCcAgentIdRequest;
import uk.co.whitbread.ohip.domain.model.reservation.in.UpdateReservationOverrideReasonsRequest;
import uk.co.whitbread.ohip.domain.model.reservation.in.UpdateReservationPackagesByIdRequest;
import uk.co.whitbread.ohip.domain.model.reservation.in.UpdateReservationRequest;
import uk.co.whitbread.ohip.domain.model.reservation.in.UpdateReservationRequestSingleCall;
import uk.co.whitbread.ohip.domain.model.reservation.in.UpdateReservationsRequest;
import uk.co.whitbread.ohip.domain.model.reservation.in.UpdateReservationsRequestSingleCall;
import uk.co.whitbread.ohip.domain.model.reservation.in.UpdateRoomRateRequest;
import uk.co.whitbread.ohip.domain.model.reservation.in.UpdateRoomStayRequest;
import uk.co.whitbread.ohip.domain.model.reservation.out.BookingAllowancesResponse;
import uk.co.whitbread.ohip.domain.model.reservation.out.CancelInformationResponse;
import uk.co.whitbread.ohip.domain.model.reservation.out.CancelReservationResponse;
import uk.co.whitbread.ohip.domain.model.reservation.out.CancellationPoliciesResponse;
import uk.co.whitbread.ohip.domain.model.reservation.out.ConfirmReservationResponse;
import uk.co.whitbread.ohip.domain.model.reservation.out.CopyReservationsResponse;
import uk.co.whitbread.ohip.domain.model.reservation.out.CurrencyAmountType;
import uk.co.whitbread.ohip.domain.model.reservation.out.DepositPolicies;
import uk.co.whitbread.ohip.domain.model.reservation.out.DepositPoliciesResponseSingleCall;
import uk.co.whitbread.ohip.domain.model.reservation.out.DepositsResponse;
import uk.co.whitbread.ohip.domain.model.reservation.out.Guarantee;
import uk.co.whitbread.ohip.domain.model.reservation.out.GuestAddress;
import uk.co.whitbread.ohip.domain.model.reservation.out.MarketingPreferencesResponse;
import uk.co.whitbread.ohip.domain.model.reservation.out.MemosResponse;
import uk.co.whitbread.ohip.domain.model.reservation.out.PreCheckInResponse;
import uk.co.whitbread.ohip.domain.model.reservation.out.RateInfo;
import uk.co.whitbread.ohip.domain.model.reservation.out.RateInfoDetails;
import uk.co.whitbread.ohip.domain.model.reservation.out.RateInfoSummary;
import uk.co.whitbread.ohip.domain.model.reservation.out.RateInfoSummarySingleCall;
import uk.co.whitbread.ohip.domain.model.reservation.out.RatePerNight;
import uk.co.whitbread.ohip.domain.model.reservation.out.ResCashieringType;
import uk.co.whitbread.ohip.domain.model.reservation.out.ReservationAmounts;
import uk.co.whitbread.ohip.domain.model.reservation.out.ReservationBooker;
import uk.co.whitbread.ohip.domain.model.reservation.out.ReservationBookerSingleCall;
import uk.co.whitbread.ohip.domain.model.reservation.out.ReservationByBasketRefResponse;
import uk.co.whitbread.ohip.domain.model.reservation.out.ReservationById;
import uk.co.whitbread.ohip.domain.model.reservation.out.ReservationByIdGuestsResponse;
import uk.co.whitbread.ohip.domain.model.reservation.out.ReservationByIdResponseSingleCall;
import uk.co.whitbread.ohip.domain.model.reservation.out.ReservationDetailsEnhancedResponse;
import uk.co.whitbread.ohip.domain.model.reservation.out.ReservationEmailNotifications;
import uk.co.whitbread.ohip.domain.model.reservation.out.ReservationEventPreference;
import uk.co.whitbread.ohip.domain.model.reservation.out.ReservationGuest;
import uk.co.whitbread.ohip.domain.model.reservation.out.ReservationGuestResponse;
import uk.co.whitbread.ohip.domain.model.reservation.out.ReservationIdDetailsResponse;
import uk.co.whitbread.ohip.domain.model.reservation.out.ReservationLightweightResponse;
import uk.co.whitbread.ohip.domain.model.reservation.out.ReservationPackagesResponse;
import uk.co.whitbread.ohip.domain.model.reservation.out.ReservationPaymentCardType;
import uk.co.whitbread.ohip.domain.model.reservation.out.ReservationPaymentCardTypeSingleCall;
import uk.co.whitbread.ohip.domain.model.reservation.out.ReservationResponse;
import uk.co.whitbread.ohip.domain.model.reservation.out.ReservationTaxTypeInfo;
import uk.co.whitbread.ohip.domain.model.reservation.out.ReservationsDetailsResponse;
import uk.co.whitbread.ohip.domain.model.reservation.out.ReservationsPaymentCardType;
import uk.co.whitbread.ohip.domain.model.reservation.out.RoomStayByIdResponse;
import uk.co.whitbread.ohip.domain.model.reservation.out.SearchBookingsResponse;
import uk.co.whitbread.ohip.domain.model.reservation.out.UpdateReasonForStayResponse;
import uk.co.whitbread.ohip.domain.model.rules.model.out.RoomSubstitution;
import uk.co.whitbread.ohip.domain.ports.primary.HotelReservationInPort;
import uk.co.whitbread.ohip.domain.ports.secondary.HotelAvailabilityOutPort;
import uk.co.whitbread.ohip.domain.ports.secondary.HotelReservationOutPort;

@Slf4j
@RequiredArgsConstructor
public class HotelReservationInPortImpl implements HotelReservationInPort {

  private final HotelReservationOutPort hotelReservationOhipPort;
  private final HotelAvailabilityOutPort hotelAvailabilityOutPort;
  private final UnleashWrapper<FeatureFlag> unleashWrapper;

  @Override
  public ReservationResponse processCreateReservation(ReservationRequest createReservationRequest) {
    if (getAmendReservations(createReservationRequest).isEmpty()) {
      return createReservation(createReservationRequest);
    } else {
      return createReservationForAmend(createReservationRequest);
    }
  }

  public ReservationResponse createReservation(ReservationRequest createReservationRequest) {
    log.debug("Entered createReservation with {} reservations",
            (createReservationRequest.getReservations() == null ? 0 :
                    createReservationRequest.getReservations().size()));
    validateRates(createReservationRequest);

    var reservationResponse = hotelReservationOhipPort.createReservation(createReservationRequest);
    if (createReservationRequest.isGetReservationsByIds()) {

      List<DepositPolicies> policies = getDepositPoliciesCreateReservation(reservationResponse);

      if (!policies.isEmpty() && !areAllUnique(policies, DepositPolicies::getPolicyCode)) {
        var exception = new PolicyCodeMismatchException(
            ErrorCode.DIGITAL_POLICY_CODE_MISMATCH_EXCEPTION,
            "Policy code is not unique across reservations");
        ExceptionLogger.log(log, exception);
        throw exception;
      }

      reservationResponse.setTotalCost(computeSum(policies, DepositPolicies::getAmountDue));
    }
    return reservationResponse;
  }

  private ReservationResponse createReservationForAmend(ReservationRequest createReservationRequest) {
    createReservationRequest.getReservations().forEach(resReq -> {
      var roomTypeSubstituted = getPmsRoomType(resReq.getHotelId(), resReq.getArrival(), resReq.getDeparture(),
          resReq.getRoomRates().getRoomType(), resReq.getAdults(), resReq.getChildren(),
          createReservationRequest.getBookingChannel().getChannel());

      resReq.getRoomRates().setRoomType(roomTypeSubstituted.getType());
    });

    return createReservation(createReservationRequest);
  }

  private List<Reservation> getAmendReservations(ReservationRequest createReservationRequest) {
    List<String> wbRoomTypes = hotelReservationOhipPort.getWbRoomTypes();
    return createReservationRequest.getReservations().stream().filter(reservation ->
                    wbRoomTypes.contains(reservation.getRoomRates().getRoomType()))
            .toList();
  }

  /**
   * Validate rates & amounts for the given ratePlanCode actually exists and are not 0.
   *
   * @param createReservationRequest The reservation request to check.
   */
  private void validateRates(ReservationRequest createReservationRequest) {
    var rooms = createReservationRequest.getReservations().stream()
        .map(this::mapAvailabilityRoomSearchCriteria)
        .toList();

    var ratesInfo = hotelAvailabilityOutPort.getRatesInfo(rooms);

    if (ratesInfo.size() != rooms.size() || ratesInfo.stream().anyMatch(this::ratePredicate)) {
      var ex = new UnavailableRatesException(ErrorCode.DIGITAL_VALIDATE_RATES_EXCEPTION,
          "Unavailable rates");
      ExceptionLogger.log(log, ex);
      throw ex;
    }
  }

  private boolean ratePredicate(AvailabilityRoomPriceBreakdown breakdown) {
    return BigDecimal.ZERO.compareTo(breakdown.getTotalNetAmount()) == 0
        || breakdown.getDailyPrices().isEmpty();
  }

  private AvailabilityRoomSearchCriteria mapAvailabilityRoomSearchCriteria(
      Reservation reservation) {
    return AvailabilityRoomSearchCriteria.builder()
        .hotelId(reservation.getHotelId())
        .arrivalDate(reservation.getArrival())
        .departureDate(reservation.getDeparture())
        .roomType(reservation.getRoomRates().getRoomType())
        .adults(reservation.getAdults())
        .children(reservation.getChildren())
        .ratePlanCode(reservation.getRoomRates().getRatePlanCode())
        .build();
  }

  @Override
  public ReservationsDetailsResponse getReservationsByExternalReferenceIds(String hotelId,
      List<String> externalReferenceIds, int limit, int offset) {
    log.debug("Entered getReservationsByExternalReferenceIds for hotelId={}", sanitize(hotelId));
    return hotelReservationOhipPort.getReservationsByExternalReferenceIds(hotelId,
        externalReferenceIds, limit, offset);
  }

  @Override
  public List<ReservationsPaymentCardType> getReservationMethodPayment(String hotelId,
      List<String> reservationId) {
    return hotelReservationOhipPort.getReservationPaymentMethod(hotelId, reservationId);
  }

  @Override
  public ConfirmReservationResponse confirmReservation(
      ConfirmReservationRequest confirmReservationRequest) {
    log.debug("Entered confirmReservation for hotelId={} and reservationId={}",
        confirmReservationRequest.getHotelId(), confirmReservationRequest.getReservationId());
    return hotelReservationOhipPort.confirmReservation(confirmReservationRequest);
  }

  @Override
  public void updateReservationsToPayOnArrival(String hotelId, Set<String> reservationIds) {
    log.debug("Entered updateReservationsToPayOnArrival for hotelId={} and reservationIds={}",
        hotelId, String.join(",", reservationIds));

    var basketReservations = hotelReservationOhipPort.getReservationsByIds(hotelId,
        reservationIds, false, false, false);

    hotelReservationOhipPort.updateReservationsToPayOnArrival(basketReservations.getReservationByIdList());
  }

  @Override
  public void updateBookersDetails(BookerDetailsCnpRequest bookerDetailsCnpRequest) {
    hotelReservationOhipPort.updateBookerDetails(bookerDetailsCnpRequest);
  }

  @Override
  public void deleteRoutingInstruction(String hotelId, Set<String> reservationIds) {
    hotelReservationOhipPort.deleteRoutingInstruction(hotelId, reservationIds);
  }

  @Override
  public ReservationByBasketRefResponse getReservationsByIds(String hotelId,
      Set<String> reservationIds, Boolean priceBreakdownNeeded, boolean rateInfoNeeded, Boolean operaUiCreatedRsv) {
    String sanitizedHotelId = hotelId == null ? null : hotelId.replaceAll("\\p{Cntrl}", "");
    log.debug("Entered getReservationsByIds for hotelId={} and {} reservations",
        sanitizedHotelId, reservationIds == null ? 0 : reservationIds.size());
    var basketReservations = hotelReservationOhipPort.getReservationsByIds(hotelId,
        reservationIds, priceBreakdownNeeded, rateInfoNeeded, operaUiCreatedRsv);

    List<DepositPolicies> policies = getDepositPolicies(basketReservations);
    String policyCode = "";
    if (policies.isEmpty()) {
      return basketReservations;
    } else {
      Optional<DepositPolicies> depositPolicies = policies.stream().findFirst();
      if (!depositPolicies.isEmpty()) {
        policyCode = depositPolicies.get().getPolicyCode();
      }
    }

    var idContext = basketReservations.getIdContext();
    String sourceCode = null;
    if (!(basketReservations.getReservationByIdList().isEmpty()) && basketReservations.getReservationByIdList().get(0)
            != null && basketReservations.getReservationByIdList().get(0).getRoomStay() != null) {
      sourceCode = basketReservations.getReservationByIdList().get(0).getRoomStay().getSourceCode();
    }

    if (!areAllUnique(policies, DepositPolicies::getPolicyCode)
            && !Boolean.TRUE.equals(is3rdPartyBooking(idContext, sourceCode))) {
      var exception = new PolicyCodeMismatchException(
          ErrorCode.DIGITAL_POLICY_CODE_NOT_UNIQUE_EXCEPTION,
          "Policy code is not unique across reservations");
      ExceptionLogger.log(log, exception);
      throw exception;
    }
    basketReservations.setPolicyCode(policyCode);
    return basketReservations;
  }

  @Override
  public ReservationLightweightResponse getReservationsByIds(String hotelId, Set<String> reservationIds) {
    String safeHotelId = hotelId == null ? null : hotelId.replaceAll("\\p{Cntrl}", "");
    log.debug("Entered lightweight getReservationsByIds for hotelId={} and {} reservations",
        safeHotelId, reservationIds == null ? 0 : reservationIds.size());
    return hotelReservationOhipPort.getReservationsByIds(safeHotelId, reservationIds);
  }

  public Boolean is3rdPartyBooking(final String idContext, final String sourceCode) {
    if (unleashWrapper.isEnabled(unleashWrapper.featureFlag().getMobileAcceptsOtaBooking())) {
      if (isDesktopBooking(idContext, sourceCode)) {
        return false;
      }
      if (isDistributionBooking(idContext, sourceCode)) {
        return true;
      }
      return !isDigitalBooking(idContext, sourceCode);
    }
    return false;
  }

  public boolean isDigitalBooking(final String idContext, final String sourceCode) {
    return "WB_DIGITAL".equals(idContext) && (sourceCode != null) && (!sourceCode.equals("35"))
            && (!sourceCode.equals("38")) && (!sourceCode.equals("43"));
  }

  public boolean isDesktopBooking(final String idContext, final String sourceCode) {
    return Objects.isNull(idContext) && (sourceCode != null) && (sourceCode.equals("01") || sourceCode.equals("31"));
  }

  public boolean isDistributionBooking(final String idContext, final String sourceCode) {
    return "WB_DIGITAL".equals(idContext) && (sourceCode != null) && (sourceCode.equals("35") || sourceCode.equals("38")
            || sourceCode.equals("43"));
  }

  @Override
  public void updateReservationPackages(ReservationPackagesRequest reservationPackagesRequest) {
    log.debug("Entered updateReservationPackages for hotelId={} and {} reservations",
        reservationPackagesRequest.getHotelId(),
        (reservationPackagesRequest.getReservationsId() == null ? 0 :
            reservationPackagesRequest.getReservationsId().size()));
    if (shouldUpdatePackages(reservationPackagesRequest)) {
      hotelReservationOhipPort.updateReservationPackages(reservationPackagesRequest);
    }
  }

  @Override
  public MemosResponse createMemo(CreateMemoRequest createMemoRequest) {
    log.debug("Entered createMemo for createMemoRequest = {}", createMemoRequest);
    return hotelReservationOhipPort.createMemo(createMemoRequest);
  }

  @Override
  public MemosResponse getMemos(String hotelId, Set<String> reservationIds) {
    log.debug("Entered getMemos for hotelId={} and reservationIds={}",
        sanitize(hotelId), sanitize(reservationIds));
    return hotelReservationOhipPort.getMemos(hotelId, reservationIds);
  }

  @Override
  public void attachProfileToReservations(
      AttachReservationProfileRequest attachReservationProfileRequest) {
    hotelReservationOhipPort.attachProfileToReservations(attachReservationProfileRequest);
  }

  private boolean shouldUpdatePackages(ReservationPackagesRequest reservationPackagesRequest) {
    if (Objects.isNull(reservationPackagesRequest.getReservationsId())
        || reservationPackagesRequest.getReservationsId().isEmpty()) {
      return false;
    }
    long toSaveCount = 0;
    long toRemoveCount = 0;
    if (nonNull(reservationPackagesRequest.getRoomsSelections())) {
      toSaveCount = reservationPackagesRequest.getRoomsSelections().stream()
          .filter(
              r -> nonNull(r.getPackagesSelection()) && !r.getPackagesSelection().isEmpty())
          .count();
    }
    if (nonNull(reservationPackagesRequest.getPreviousRoomsSelections())) {
      toRemoveCount = reservationPackagesRequest.getPreviousRoomsSelections().stream()
          .filter(
              r -> nonNull(r.getPackagesSelection()) && !r.getPackagesSelection().isEmpty())
          .count();
    }
    return (toSaveCount > 0 || toRemoveCount > 0);
  }

  @Override
  public void updateDiscount(UpdateDiscountRequest updateDiscountRequest) {
    log.debug("Entered updateDiscount for hotelId={} and reservationIds={}",
        updateDiscountRequest.getHotelId(),
        String.join(",", updateDiscountRequest.getReservationIds()));
    hotelReservationOhipPort.updateDiscount(updateDiscountRequest);
  }

  @Override
  public void updateCompanyQuestionAndAnswerDetails(
      CompanyQuestionAndAnswerDetailsRequest companyQuestionAndAnswerDetailsRequest) {
    log.debug("Entered companyQuestionAndAnswerDetailsRequest for hotelId = {} and reservationIds ="
            + " {}",
        companyQuestionAndAnswerDetailsRequest.getHotelId(),
        companyQuestionAndAnswerDetailsRequest.getReservationIds());
    hotelReservationOhipPort.updateCompanyQuestionAndAnswerDetails(companyQuestionAndAnswerDetailsRequest);
  }

  @Override
  public void updateBusinessItems(BusinessItemsRequest businessItemsRequest) {
    log.debug("Entered updateBusinessItems for hotelId = {} and businessItems = {}",
        businessItemsRequest.getHotelId(),
        nonNull(businessItemsRequest.getBusinessItems()) ? businessItemsRequest.getBusinessItems().toString() : null);

    hotelReservationOhipPort.updateBusinessItems(businessItemsRequest);
  }

  @Override
  public void updateCustomReferenceNumber(
      UpdateCustomReferenceNumberRequest updateCustomReferenceNumberRequest) {

    hotelReservationOhipPort.updateCustomReferenceNumber(updateCustomReferenceNumberRequest);
  }

  @Override
  public void updateSpecialRequests(SpecialRequests specialRequests) {
    log.debug("Entered updateSpecialRequests for hotelId = {} and specialRequests = {}",
        specialRequests.getHotelId(), specialRequests.getSpecialRequests().toString());

    hotelReservationOhipPort.updateSpecialRequests(specialRequests);
  }

  @Override
  public UpdateReasonForStayResponse updateReasonForStay(
      UpdateReasonForStayRequest updateReasonForStayRequest) {
    log.debug("Entered updateReasonForStay for hotelId={} and reservationIds={}",
        updateReasonForStayRequest.getHotelId(),
        String.join(", ", updateReasonForStayRequest.getReservationIds()));

    return hotelReservationOhipPort.updateReasonForStay(updateReasonForStayRequest);
  }

  @Override
  public void updateReservationOverrideReasons(
      UpdateReservationOverrideReasonsRequest updateReservationOverrideReasonsRequest) {
    log.debug("Entered updateReservationOverrideReasons for hotelId={} and reservationIds={}",
        updateReservationOverrideReasonsRequest.getHotelId(),
        String.join(", ", updateReservationOverrideReasonsRequest.getReservationIds()));

    hotelReservationOhipPort.updateReservationOverrideReasons(updateReservationOverrideReasonsRequest);
  }

  @Override
  public void updateReservationCcAgentId(
      UpdateReservationCcAgentIdRequest updateReservationCcAgentId) {
    log.debug("Entered updateReservationCcAgentId for hotelId={} and reservationIds={}",
        updateReservationCcAgentId.getHotelId(),
        String.join(", ", updateReservationCcAgentId.getReservationIds()));

    hotelReservationOhipPort.updateReservationCcAgentId(updateReservationCcAgentId, false);
  }

  @Override
  public CancellationPoliciesResponse getCancellationPolicies(
          Set<String> reservationIds, String hotelId, String rateCode, String arrivalDate) {
    log.debug("Entered getCancellationPolicies for hotelId={} and reservationIds={} or rateCode={}",
        sanitize(hotelId), sanitize(reservationIds), sanitize(rateCode));
    return hotelReservationOhipPort.getCancellationPolicies(reservationIds, hotelId, rateCode, arrivalDate, null);
  }

  @Override
  public void updateCancellationPolicy(
      UpdateCancellationPolicyRequest updateCancellationPolicyRequest) {
    log.debug(
        "Entered updateCancellationPolicy for hotelId={}, reservationId={} and absoluteDeadline={}",
        updateCancellationPolicyRequest.getHotelId(),
        updateCancellationPolicyRequest.getReservationId(),
        updateCancellationPolicyRequest.getAbsoluteDeadline());
    hotelReservationOhipPort.updateCancellationPolicy(updateCancellationPolicyRequest);
  }

  @Override
  public void updateCancellationPolicies(
      UpdateCancellationPoliciesRequest updateCancellationPoliciesRequest) {
    log.debug(
        "Entered updateCancellationPolicies for hotelId={} and reservationIds={}",
        updateCancellationPoliciesRequest.getHotelId(),
        updateCancellationPoliciesRequest.getReservationIds());
    hotelReservationOhipPort.updateAbsoluteDeadline(updateCancellationPoliciesRequest);
  }

  @Override
  public CopyReservationsResponse copyReservations(
      CopyReservationsRequest copyReservationsRequest) {
    log.debug("Entered copyReservations for hotelId={} and reservationIds={}",
        copyReservationsRequest.getHotelId(),
        String.join(", ", copyReservationsRequest.getReservationIds()));

    return hotelReservationOhipPort.copyReservations(copyReservationsRequest);
  }

  @Override
  public void updateReservations(UpdateReservationsRequest updateReservationsRequest) {

    var reservationIds = updateReservationsRequest.getUpdateReservationsRequest().parallelStream()
        .map(updateReservationRequest -> updateReservationRequest.getReservationType().getId())
        .collect(Collectors.toSet());

    reservationIds.forEach(reservationId -> {
      ReservationById currentTempReservationById = updateReservationsRequest.getTempReservations()
              .getReservationByIdList().stream().filter(currentReservationId ->
                currentReservationId.getReservationId().equals(reservationId))
              .findAny().get();

      if (Objects.isNull(currentTempReservationById)) {
        currentTempReservationById = hotelReservationOhipPort.getReservationsByIds(
            updateReservationsRequest.getUpdateReservationsRequest().get(0).getHotelId(),
            Set.of(reservationId), false, false, false).getReservationByIdList().get(0);
      }

      var sourceCode = currentTempReservationById.getRoomStay().getSourceCode();
      var updReservationOptional =
          updateReservationsRequest.getUpdateReservationsRequest().parallelStream()
              .filter(updateReservationRequest -> reservationId.equals(
                  updateReservationRequest.getReservationType().getId())).findFirst();
      ReservationById finalCurrentTempReservationById = currentTempReservationById;
      updReservationOptional.ifPresent(
          updateReservationRequest -> {
            if (updateReservationRequest.getRoomStay().getRoomRates() == null) {
              hotelReservationOhipPort.updateReservations(
                  mapMissingInfoForAmendStayDates(finalCurrentTempReservationById,
                      updateReservationRequest, updateReservationsRequest),
                  true, sourceCode, true, null, null);
            } else {
              hotelReservationOhipPort.updateReservations(
                  mapMissingInfoForEditRoom(finalCurrentTempReservationById,
                      updateReservationRequest, updateReservationsRequest.getBookingChannel(),
                      updateReservationsRequest.getCompanyId(),
                      updateReservationsRequest.getDistributionIATANumber()), false, sourceCode);
            }
          });
    });
  }

  @Override
  public void updateReservationsWithExternalRef(String hotelId, Set<String> reservationIds, String externalReference) {
    hotelReservationOhipPort.updateReservationsWithExternalRef(hotelId, reservationIds, externalReference);
  }

  @Override
  public List<ChangeReservation> updateStayDateOrEditRoom(
          UpdateReservationsRequestSingleCall updateReservationsRequestSingleCall) {

    // reservations -> updateReservationRequest
    // tempReservation -> original reservations

    return updateReservationsRequestSingleCall.getReservations().parallelStream()
            .map(UpdateReservationRequestSingleCall::getReservationId)
            .distinct()
            .map(reservationId -> {
              ReservationByIdResponseSingleCall originalReservation = updateReservationsRequestSingleCall
                      .getTempReservations()
                      .getReservationByIdList().stream().filter(currentReservationId ->
                              currentReservationId.getReservationId().equals(reservationId))
                      .findAny().get();
              var sourceCode = originalReservation.getRoomStay().getSourceCode();
              var updatedReservation =
                      updateReservationsRequestSingleCall.getReservations().parallelStream()
                              .filter(updateReservationRequest -> reservationId.equals(
                                      updateReservationRequest.getReservationId())).findFirst();
              return updatedReservation.stream().map(tempUpdateReservationRequest -> {
                UpdateReservationRequest updateReservationRequest =
                        convertUpdateReservationSingleCallToUpdateReservation(tempUpdateReservationRequest);
                return getChangeReservationForStayDateOrEditRoom(
                        updateReservationRequest,
                        originalReservation,
                        updateReservationsRequestSingleCall,
                        sourceCode
                );
              }).toList().get(0);
            }).toList();

  }

  private ChangeReservation getChangeReservationForStayDateOrEditRoom(
          UpdateReservationRequest updateReservationRequest,
          ReservationByIdResponseSingleCall originalReservation,
          UpdateReservationsRequestSingleCall updateReservationsRequestSingleCall,
          String sourceCode) {
    ChangeReservation changeReservation = null;
    if (updateReservationRequest.getRoomStay().getRoomRates() == null) {
      changeReservation =
              hotelReservationOhipPort.getStayDateChangeReservation(
                      mapMissingInfoForAmendStayDatesSingleCall(originalReservation,
                              updateReservationRequest, updateReservationsRequestSingleCall),
                      true, sourceCode, true, null, null
              ).get(0);
    } else {
      ReservationById reservationById = convertReservationByIdResponseSingleCallToReservationById(
              originalReservation);
      changeReservation = hotelReservationOhipPort
              .getEditRoomChangeReservations(
                      mapMissingInfoForEditRoom(reservationById,
                              updateReservationRequest,
                              getBookingChannel(updateReservationsRequestSingleCall),
                              updateReservationsRequestSingleCall.getCompanyId(),
                              updateReservationsRequestSingleCall.getDistributionIATANumber()),
                      false, sourceCode).get(0);
    }
    return changeReservation;
  }

  @Override
  public void updateRoutingInstructionsWithPayeeInfo(String hotelId, Set<String> reservationIds) {
    hotelReservationOhipPort.updateRoutingInstructionsWithPayeeInfo(hotelId, reservationIds);
  }

  @Override
  public ReservationByBasketRefResponse confirmAmend(
      ConfirmAmendOnReservationsRequest confirmAmendRequest) {
    List<String> originalReservationIds = confirmAmendRequest.getOriginalReservations();

    var updateReservationsRequest = UpdateReservationsRequest.builder()
        .updateReservationsRequest(new LinkedList<>())
        .bookingChannel(confirmAmendRequest.getBookingChannel())
        .clearCcAgentIdUdf(confirmAmendRequest.getClearCcAgentIdUdf())
        .build();
    var language = LANGUAGE_ENGLISH;
    if (confirmAmendRequest.getBookingChannel() != null) {
      language = confirmAmendRequest.getBookingChannel().getLanguage();
    }

    List<uk.co.whitbread.hotel.ohip.adapter.generated.models.Reservation> orig = Collections.emptyList();
    for (String reservationId : originalReservationIds) {

      orig = hotelReservationOhipPort.getReservationsByIdsLight(confirmAmendRequest.getHotelId(),
          Set.of(reservationId));

      var tempReservationId = confirmAmendRequest.getLinkAmendReservations().get(reservationId);
      if (tempReservationId != null) {

        var tempReservation = hotelReservationOhipPort.getReservationsByIds(
            confirmAmendRequest.getHotelId(), Set.of(tempReservationId), false, false,
                false);
        var updateReservationRequest = getUpdateReservationRequest(
            reservationId,
            confirmAmendRequest.getHotelId(),
            tempReservation.getReservationByIdList().get(0),
            tempReservation.getCurrencyCode(),
            confirmAmendRequest.getSendEmailConfirmation(),
            confirmAmendRequest.getSendEmailInvoice(),
            language,
            getLeadGuestProfileId(orig),
            confirmAmendRequest.getBookingChannel().getChannel());

        updateReservationsRequest.getUpdateReservationsRequest().add(updateReservationRequest);
        UserDefinedFields userDefinedFields = tempReservation.getReservationByIdList().get(0)
            .getUserDefinedFields();
        if (userDefinedFields != null) {
          Optional<CharacterUDFs> distributionIATANumber = userDefinedFields.getCharacterUDFs().stream()
              .filter(characterUDFs -> UDFC_16.equals(characterUDFs.getName())).findFirst();
          distributionIATANumber.ifPresent(
              characterUDFs -> updateReservationsRequest.setDistributionIATANumber(characterUDFs
                  .getValue()));
        }
      }
    }

    //var sourceCode = originalReservations.getReservationByIdList().get(0).getRoomStay().getSourceCode();
    var sourceCode =  orig.get(0).getReservations().getReservation().get(0)
        .getRoomStay().getRoomRates().get(0).getSourceCode();
    hotelReservationOhipPort.updateReservations(updateReservationsRequest, true, sourceCode,
            false, originalReservationIds.get(0), orig);

    if (confirmAmendRequest.getMarkAsPayOnArrival() != null && confirmAmendRequest.getMarkAsPayOnArrival()) {
      updateReservationsToPayOnArrival(confirmAmendRequest.getHotelId(),
              new HashSet<>(confirmAmendRequest.getOriginalReservations()));
    }

    return hotelReservationOhipPort.getReservationsByIds(
        confirmAmendRequest.getHotelId(),
        Set.copyOf(confirmAmendRequest.getOriginalReservations()), false, true, false);
  }

  @Override
  public ReservationByBasketRefResponse confirmAmendForSingleCall(
          ConfirmAmendForSingleRequest confirmAmendForSingleRequest) {

    log.info("Entered confirmAmendForSingleCall() with ConfirmAmendForSingleRequest - " + confirmAmendForSingleRequest);

    //stay date update
    UpdateReservationsRequestSingleCall stayDateUpdateRequest = confirmAmendForSingleRequest.getStayDateUpdateRequest();
    List<ChangeReservation> stayDateChangeReservations;
    if (stayDateUpdateRequest != null) {
      stayDateUpdateRequest.getTempReservations().getReservationByIdList().forEach(res -> {
        ReservationByIdResponseSingleCall first = stayDateUpdateRequest.getTempReservations()
                .getReservationByIdList()
                .stream().filter(reservationByIdResponse ->
                        reservationByIdResponse.getReservationId().equals(res.getReservationId()))
                .findFirst().get();
        res.getRoomStay().setAdultsNumber(first.getRoomStay().getAdultsNumber());
        res.getRoomStay().setChildrenNumber(first.getRoomStay().getChildrenNumber());
      });

      stayDateChangeReservations = updateStayDateOrEditRoom(stayDateUpdateRequest);
    } else {
      stayDateChangeReservations = null;
    }

    // edit room
    List<List<ChangeReservation>> editRoomChangeReservations;
    List<UpdateReservationsRequestSingleCall> editRoomRequests = confirmAmendForSingleRequest.getEditRoomRequest();
    if (nonNull(editRoomRequests)) {
      editRoomChangeReservations = editRoomRequests.stream().map(this::updateStayDateOrEditRoom).toList();
    } else {
      editRoomChangeReservations = null;
    }

    // update package
    UpdateReservationPackagesByIdRequest updateReservationPackagesByIdRequest = confirmAmendForSingleRequest
            .getUpdateReservationPackagesByIdRequest();
    ReservationPackagesRequest reservationPackagesRequest =
        convertReservationPackages(updateReservationPackagesByIdRequest);
    boolean shouldUpdatePackage = false;
    if (nonNull(reservationPackagesRequest)) {
      shouldUpdatePackage = shouldUpdatePackages(reservationPackagesRequest);
    }

    //update booker details
    if (nonNull(confirmAmendForSingleRequest.getBookerDetailsCnpRequest())) {
      hotelReservationOhipPort.updateBookerDetails(confirmAmendForSingleRequest.getBookerDetailsCnpRequest());
      //add profile
      hotelReservationOhipPort.addBookerDetails(confirmAmendForSingleRequest.getBookerDetailsCnpRequest());
    }

    return hotelReservationOhipPort.confirmAmendSingleCall(
            confirmAmendForSingleRequest.getSpecialRequests(),
            confirmAmendForSingleRequest.getBookerDetailsCnpRequest(),
            stayDateChangeReservations,
            editRoomChangeReservations,
            confirmAmendForSingleRequest.getBookingAllowancesRequest(),
            reservationPackagesRequest,
            shouldUpdatePackage);

  }

  private ReservationPackagesRequest convertReservationPackages(
          UpdateReservationPackagesByIdRequest updateReservationRequest) {
    if (!nonNull(updateReservationRequest)) {
      return null;
    }
    List<RoomsSelections> roomsSelections = updateReservationRequest.getRoomsSelections().stream().map(
            roomSelection -> new RoomsSelections(roomSelection.getPackagesSelection())).toList();
    List<RoomsSelections> previousRoomsSelections = updateReservationRequest.getPreviousRoomsSelections().stream().map(
            previousRoomSelection -> new RoomsSelections(previousRoomSelection.getPackagesSelection())).toList();

    List<String> reservationIds = updateReservationRequest.getPreviousRoomsSelections().stream().map(
            RoomsSelectionsByReservationId::getReservationId).toList();

    return ReservationPackagesRequest.builder()
            .reservationsId(reservationIds)
            .hotelId(updateReservationRequest.getHotelId())
            .arrival(updateReservationRequest.getArrival())
            .departure(updateReservationRequest.getDeparture())
            .roomsSelections(roomsSelections)
            .previousRoomsSelections(previousRoomsSelections).build();
  }

  private UpdateReservationRequest getUpdateReservationRequest(
      String reservationId,
      String  hotelId,
      ReservationById tempResInfo,
      String currencyCode,
      Boolean sendEmailConfirmation,
      Boolean sendEmailInvoice, String language, String originalProfileId, String channel) {
    var guestList = tempResInfo.getReservationGuestList().stream()
        .map(reservationGuest -> ReservationGuests.builder()
            .profileInfo(GuestInfoUtils.toProfileType(reservationGuest,
                tempResInfo.getReservationBooker(), language))
            .build())
        .toList();

    // implement to prevent duplication of profile
    if (!DISTRIBUTION_CHANNEL.equalsIgnoreCase(channel) && isNoDuplicateProfileCreationEnabled(channel)) {
      String tempProfileId = tempResInfo.getReservationGuestList()
          .stream()
          .filter(reservationGuest -> "Primary".equals(reservationGuest.getType()))
          .toList().get(0)
          .getProfileId();

      if (originalProfileId.equals(tempProfileId)) {
        //set guestList with null to prevent duplication of profile 
        guestList = null;

      } else if (hotelReservationOhipPort.isProfileUpdated(hotelId, originalProfileId, tempProfileId)) {
        //set guestList with null to prevent duplication of profile
        guestList = null;
      }
    }

    var roomOccupancy = RoomOccupancy.builder()
        .adultCount(tempResInfo.getRoomStay().getAdultCount())
        .childCount(tempResInfo.getRoomStay().getChildCount())
        .build();

    var roomRates = tempResInfo.getRoomStay().getRatesPerNight().stream()
        .map(ratePerNight -> RoomRate.builder()
            .roomType(tempResInfo.getRoomStay().getRoomType())
            .rates(RateType.builder()
                    .rate(List.of(AmountType.builder()
                            .base(TotalType.builder()
                                    .amountBeforeTax(
                                        getNightRateFromTempRes(tempResInfo, ratePerNight).getPricePerNight())
                                    .currencyCode(currencyCode)
                                    .build())
                            .start(ratePerNight.getStartDate())
                            .end(ratePerNight.getStartDate())
                            .build()))
                    .build())
            .ratePlanCode(tempResInfo.getRoomStay().getRatePlanCode())
            .fixedRate(true)
            .roomOccupancy(roomOccupancy)
            .startDate(ratePerNight.getStartDate())
            .endDate(ratePerNight.getStartDate())
            .build()).toList();

    var roomStay = RoomStay.builder()
        .arrivalDate(tempResInfo.getRoomStay().getArrivalDate().toString())
        .departureDate(tempResInfo.getRoomStay().getDepartureDate().toString())
        .roomOccupancy(roomOccupancy)
        .roomRates(roomRates)
        .build();

    var reservationType = ReservationType.builder()
        .type("STAY")
        .id(reservationId)
        .build();

    var specialRequests = Optional.ofNullable(tempResInfo.getPreferences()).orElseGet(Collections::emptyList).stream()
        .filter(Objects::nonNull).map(ReservationEventPreference::getCode).toList();

    return UpdateReservationRequest.builder()
        .hotelId(hotelId)
        .reservationType(reservationType)
        .roomStay(roomStay)
        .reservationGuests(guestList)
        .sendEmailConfirmation(sendEmailConfirmation)
        .sendEmailInvoice(sendEmailInvoice)
        .specialRequests(specialRequests)
        .build();
  }

  private RatePerNight getNightRateFromTempRes(ReservationById tempReservation,
      RatePerNight ratePerNight) {
    return tempReservation.getRoomStay().getRatesPerNight().stream()
        .filter(nightRate -> nightRate.getStartDate().equals(ratePerNight.getStartDate()))
        .findFirst()
        .orElseThrow(() -> {
          var exception = new UnavailableRatesException(DIGITAL_NO_RATES_EXCEPTION,
              String.format("No rate was found for date %s", ratePerNight.getStartDate()));
          ExceptionLogger.log(log, exception);
          return exception;
        });
  }

  private UpdateReservationsRequest mapMissingInfoForAmendStayDates(ReservationById currentReservation,
          UpdateReservationRequest currentUpdateResRequest, UpdateReservationsRequest updateReservationsRequest) {
    
    List<UpdateReservationRequest> updateReservationRequestList = new ArrayList<>();
    
    String originalReservationId = updateReservationsRequest.getLinkAmendReservations().entrySet()
        .stream().filter(tempReservation -> tempReservation.getValue()
            .equals(currentReservation.getReservationId()))
        .map(Map.Entry::getKey).findFirst()
        .orElseGet(() -> {
          log.info("No link found for reservationId %s", currentReservation.getReservationId());
          return updateReservationsRequest.getLinkAmendReservations().entrySet()
              .stream()
              .map(Map.Entry::getKey)
              .findFirst()
              .get();
        });
    
    if (originalReservationId.isBlank() || originalReservationId.isEmpty()) {
      return updateReservationsRequest;
    }

    ReservationByBasketRefResponse reservations = hotelReservationOhipPort.getReservationsByIds(
            updateReservationsRequest.getUpdateReservationsRequest().get(0).getHotelId(),
            Set.of(originalReservationId), false, false, false);

    var currentRoomOccupancy = RoomOccupancy.builder()
        .adultCount(currentReservation.getRoomStay().getAdultCount())
        .childCount(currentReservation.getRoomStay().getChildCount())
        .build();

    var currentRoomRates =
        createRoomRatesWithPredefinedPrices(currentUpdateResRequest, reservations, currentRoomOccupancy,
            currentReservation.getRoomStay().getRoomType(),
            currentReservation.getRoomStay().getRatePlanCode(),
            updateReservationsRequest.getNewRatesReservation());

    currentUpdateResRequest.getRoomStay().setRoomRates(currentRoomRates);

    updateReservationRequestList.add(currentUpdateResRequest);

    return UpdateReservationsRequest.builder()
        .updateReservationsRequest(updateReservationRequestList)
        .bookingChannel(updateReservationsRequest.getBookingChannel())
        .linkAmendReservations(updateReservationsRequest.getLinkAmendReservations())
        .distributionIATANumber(updateReservationsRequest.getDistributionIATANumber())
        .build();
  }

  private UpdateReservationsRequest mapMissingInfoForAmendStayDatesSingleCall(
          ReservationByIdResponseSingleCall originalReservation,
          UpdateReservationRequest updatedReservation,
          UpdateReservationsRequestSingleCall updateReservationsRequest) {

    List<UpdateReservationRequest> updateReservationRequestList = new ArrayList<>();

    String originalReservationId = originalReservation.getReservationId();

    ReservationByBasketRefResponse reservations = hotelReservationOhipPort.getReservationsByIds(
            updateReservationsRequest.getReservations().get(0).getHotelId(),
            Set.of(originalReservationId), false, false, false);

    var currentRoomOccupancy = RoomOccupancy.builder()
            .adultCount(originalReservation.getRoomStay().getAdultsNumber())
            .childCount(originalReservation.getRoomStay().getChildrenNumber())
            .build();

    var currentRoomRates =
            createRoomRates(updatedReservation, reservations, currentRoomOccupancy,
                    originalReservation.getRoomStay().getRoomType(),
                    originalReservation.getRoomStay().getRatePlanCode());

    updatedReservation.getRoomStay().setRoomRates(currentRoomRates);

    updateReservationRequestList.add(updatedReservation);

    return UpdateReservationsRequest.builder()
            .updateReservationsRequest(updateReservationRequestList)
            .bookingChannel(getBookingChannel(updateReservationsRequest))
            .linkAmendReservations(updateReservationsRequest.getLinkAmendReservations())
            .build();
  }

  private BookingChannel getBookingChannel(UpdateReservationsRequestSingleCall updateReservationsRequestSingleCall) {
    BookingChannelSingleCall bookingChannel = updateReservationsRequestSingleCall.getBookingChannel();
    return BookingChannel.builder()
            .channel(bookingChannel.getChannel())
            .subchannel(bookingChannel.getSubchannel())
            .language(bookingChannel.getLanguage()).build();
  }

  private ReservationById convertReservationByIdResponseSingleCallToReservationById(
          ReservationByIdResponseSingleCall reservationByIdSingleCall
  ) {

    ReservationById reservationById = ReservationById.builder()
        .reservationId(reservationByIdSingleCall.getReservationId())
        .reservationCompany(reservationByIdSingleCall.getReservationCompany())
        .additionalGuestInfo(reservationByIdSingleCall.getAdditionalGuestInfo())
        .billing(reservationByIdSingleCall.getBilling())
        .reservationPackageList(reservationByIdSingleCall.getReservationPackageList())
        .reservationOverrideReasons(reservationByIdSingleCall.getReservationOverrideReasons())
        .reservationOverridden(reservationByIdSingleCall.isReservationOverridden())
        .reservationStatus(reservationByIdSingleCall.getReservationStatus())
        .guarantee(Guarantee.builder().guaranteeCode(reservationByIdSingleCall.getGuaranteeCode()).build())
        .balanceAmount(reservationByIdSingleCall.getBalanceAmount())
        .gdsReferenceNumber(reservationByIdSingleCall.getGdsReferenceNumber())
        .build();

    ResCashieringType cashiering = ResCashieringType.builder()
            .taxType(ReservationTaxTypeInfo.builder()
                    .code(reservationByIdSingleCall.getCashiering().getTaxType().getCode()).build()).build();
    reservationById.setCashiering(cashiering);

    if (nonNull(reservationByIdSingleCall.getReservationEmailNotifications())) {
      ReservationEmailNotifications reservationEmailNotifications = ReservationEmailNotifications.builder()
              .sendEmailInvoice(reservationByIdSingleCall.getReservationEmailNotifications().isSendEmailInvoice())
              .sendEmailConfirmation(reservationByIdSingleCall.getReservationEmailNotifications()
                      .isSendEmailConfirmation())
              .build();
      reservationById.setReservationEmailNotifications(reservationEmailNotifications);
    }

    ReservationPaymentCardTypeSingleCall paymentCard = reservationByIdSingleCall.getPaymentCard();
    ReservationPaymentCardType reservationPaymentCardType = null;
    if (nonNull(paymentCard)) {
      reservationPaymentCardType = ReservationPaymentCardType.builder()
              .cardType(paymentCard.getCardType())
              .cardNumberMasked(paymentCard.getCardNumberMasked())
              .expirationDate(paymentCard.getExpirationDate())
              .cardHolderName(paymentCard.getCardHolderName())
              .paymentMethod(paymentCard.getPaymentMethod())
              .token(paymentCard.getToken()).build();
    }
    reservationById.setPaymentCard(reservationPaymentCardType);

    setDepositPolicies(reservationByIdSingleCall, reservationById);

    setRateInfo(reservationByIdSingleCall, reservationById);

    RoomStayByIdResponse roomStay = reservationByIdSingleCall.getRoomStay();
    uk.co.whitbread.ohip.domain.model.reservation.out.RoomStay rStay = null;
    if (nonNull(roomStay)) {
      rStay = uk.co.whitbread.ohip.domain.model.reservation.out.RoomStay.builder()
              .adultCount(roomStay.getAdultsNumber())
              .childCount(roomStay.getChildrenNumber())
              .cot(roomStay.getCot())
              .roomType(roomStay.getRoomType())
              .ratePlanCode(roomStay.getRatePlanCode())
              .arrivalDate(LocalDate.parse(roomStay.getArrivalDate()))
              .departureDate(LocalDate.parse(roomStay.getDepartureDate()))
              .checkInTime(roomStay.getCheckInTime())
              .checkOutTime(roomStay.getCheckOutTime())
              .roomPrice(roomStay.getRoomPrice())
              .sourceCode(roomStay.getSourceCode())
              .ratesPerNight(roomStay.getRatesPerNight())
              .cellCode(roomStay.getCellCode())
              .roomNumber(roomStay.getRoomNumber())
              .bookingChannel(roomStay.getBookingChannel()).build();
    }
    reservationById.setRoomStay(rStay);

    ReservationBookerSingleCall rbSingleCall = reservationByIdSingleCall.getReservationBooker();
    ReservationBooker reservationBooker = null;
    if (nonNull(rbSingleCall)) {
      reservationBooker = ReservationBooker.builder()
              .address(rbSingleCall.getAddress())
              .email(rbSingleCall.getEmail())
              .firstName(rbSingleCall.getFirstName())
              .lastName(rbSingleCall.getLastName())
              .landline(rbSingleCall.getLandline())
              .title(rbSingleCall.getTitle())
              .mobile(rbSingleCall.getMobile()).build();
    }
    reservationById.setReservationBooker(reservationBooker);

    setReservationGuestList(reservationByIdSingleCall, reservationById);

    return reservationById;
  }

  private static void setReservationGuestList(ReservationByIdResponseSingleCall reservationByIdSingleCall,
      ReservationById reservationById) {
    List<ReservationByIdGuestsResponse> rgList = reservationByIdSingleCall.getReservationGuestList();
    List<ReservationGuest> reservationGuests = null;
    if (nonNull(rgList)) {
      reservationGuests = rgList.stream().map(rg -> {
        GuestAddress ga = null;
        if (nonNull(rg.getAddress())) {
          ga = GuestAddress.builder()
                  .addressType(rg.getAddress().getAddressType())
                  .postalCode(rg.getAddress().getPostalCode())
                  .addressLine1(rg.getAddress().getAddressLine1())
                  .addressLine2(rg.getAddress().getAddressLine2())
                  .addressLine3(rg.getAddress().getAddressLine3())
                  .addressLine4(rg.getAddress().getAddressLine4())
                  .countryCode(rg.getAddress().getCountryCode())
                  .cityName(rg.getAddress().getCityName()).build();
        }
        return ReservationGuest.builder()
                .givenName(rg.getGivenName())
                .surname(rg.getSurName())
                .nameTitle(rg.getNameTitle())
                .email(rg.getEmail())
                .type(rg.getType())
                .address(ga).build();
      }).toList();
    }
    reservationById.setReservationGuestList(reservationGuests);
  }

  private static void setRateInfo(ReservationByIdResponseSingleCall reservationByIdSingleCall,
      ReservationById reservationById) {
    RateInfo rateInfo = null;
    if (nonNull(reservationByIdSingleCall.getRateInfo()) && nonNull(
            reservationByIdSingleCall.getRateInfo().getSummary())) {

      RateInfoSummary rateInfoSummary = setRateInfoSummaryCall(
          reservationByIdSingleCall);

      rateInfo = new RateInfo(rateInfoSummary);
    }
    reservationById.setRateInfo(rateInfo);
  }

  private static RateInfoSummary setRateInfoSummaryCall(ReservationByIdResponseSingleCall reservationByIdSingleCall) {
    RateInfoSummarySingleCall rateInfoSummarySingleCall = reservationByIdSingleCall.getRateInfo().getSummary();
    List<RateInfoDetails> rateInfoDetails = rateInfoSummarySingleCall.getDetails().stream().map(detail ->
            RateInfoDetails.builder()
                    .summaryDate(detail.getSummaryDate())
                    .revenue(detail.getRevenue())
                    .packageDetails(detail.getPackageDetails())
                    .tax(detail.getTax())
                    .gross(detail.getGross())
                    .net(detail.getNet())
                    .ratePlanCode(detail.getRatePlanCode())
                    .currencyCode(detail.getCurrencyCode()).build()).toList();
    return new RateInfoSummary(
            rateInfoDetails,
            rateInfoSummarySingleCall.getGross(),
            rateInfoSummarySingleCall.getNet(),
            rateInfoSummarySingleCall.getDeposit(),
            rateInfoSummarySingleCall.getTotalCostOfStay(),
            rateInfoSummarySingleCall.getOutStandingCostOfStay(),
            rateInfoSummarySingleCall.getGuestPay(),
            rateInfoSummarySingleCall.getRouting(),
            rateInfoSummarySingleCall.getCurrencyCode(),
            rateInfoSummarySingleCall.getStart(),
            rateInfoSummarySingleCall.getEnd(),
            rateInfoSummarySingleCall.getHasSuppressedRate()
    );
  }

  private static void setDepositPolicies(ReservationByIdResponseSingleCall reservationByIdSingleCall,
      ReservationById reservationById) {
    List<DepositPoliciesResponseSingleCall> depositPoliciesSingleCall = reservationByIdSingleCall.getDepositPolicies();
    List<DepositPolicies> depositPolicies = null;
    if (nonNull(depositPoliciesSingleCall)) {
      depositPolicies = depositPoliciesSingleCall.stream().map(depositPolicy -> DepositPolicies.builder()
              .amountPaid(
                      CurrencyAmountType.builder()
                              .amount(depositPolicy.getAmountPaid().getAmount())
                              .currencyCode(depositPolicy.getAmountPaid().getCurrencyCode())
                              .build())
              .amountDue(
                      CurrencyAmountType.builder()
                              .amount(depositPolicy.getAmountDue().getAmount())
                              .currencyCode(depositPolicy.getAmountDue().getCurrencyCode())
                              .build()).build()).toList();
    }
    reservationById.setDepositPolicies(depositPolicies);
  }

  private UpdateReservationRequest convertUpdateReservationSingleCallToUpdateReservation(
          UpdateReservationRequestSingleCall updateReservationRequestSingleCall) {
    UpdateReservationRequest updateReservationRequest = UpdateReservationRequest.builder()
            .reservationType(
                    ReservationType.builder().id(updateReservationRequestSingleCall.getReservationId()).build())
            .hotelId(updateReservationRequestSingleCall.getHotelId())
            .build();

    UpdateRoomStayRequest roomStaySingleCall = updateReservationRequestSingleCall.getRoomStay();
    var roomStay = RoomStay.builder()
            .roomOccupancy(roomStaySingleCall.getRoomOccupancy())
            .arrivalDate(roomStaySingleCall.getArrivalDate())
            .departureDate(roomStaySingleCall.getDepartureDate()).build();

    List<UpdateRoomRateRequest> roomRatesSingleCall = roomStaySingleCall.getRoomRates();
    if (nonNull(roomRatesSingleCall)) {
      List<RoomRate> roomRates = new ArrayList<>();
      roomRatesSingleCall.forEach(roomRateSingleCall -> roomRates.add(
              RoomRate.builder()
                      .roomOccupancy(roomRateSingleCall.getRoomOccupancy())
                      .startDate(roomRateSingleCall.getStartDate())
                      .endDate(roomRateSingleCall.getEndDate())
                      .roomType(roomRateSingleCall.getRoomType())
                      .ratePlanCode(roomRateSingleCall.getRatePlanCode()).build()
      ));
      roomStay.setRoomRates(roomRates);
    }


    updateReservationRequest.setRoomStay(roomStay);

    List<ReservationGuestsSingleCall> reservationGuestsSingleCall = updateReservationRequestSingleCall
            .getReservationGuests();
    if (nonNull(reservationGuestsSingleCall)) {
      List<ReservationGuests> reservationGuests = new ArrayList<>();
      reservationGuestsSingleCall.forEach(guest -> {
        List<EmailInfoTypeSingleCall> emailInfoTypeSingleCall =
            Optional.ofNullable(guest)
                .map(ReservationGuestsSingleCall::getProfileInfo)
                .map(ProfileInfoSingleCall::getProfile)
                .map(ProfileTypeSingleCall::getEmails)
                .map(ProfileTypeEmailsSingleCall::getEmailInfo)
                .orElse(Collections.emptyList());

        List<PersonNameTypeSingleCall> personNameTypeSingleCall =
            Optional.ofNullable(guest)
                .map(ReservationGuestsSingleCall::getProfileInfo)
                .map(ProfileInfoSingleCall::getProfile)
                .map(ProfileTypeSingleCall::getCustomer)
                .map(CustomerTypeSingleCall::getPersonName)
                .orElse(Collections.emptyList());
        List<EmailInfoType> emailInfoType = emailInfoTypeSingleCall.stream().map(emailInfoSingleCall ->
                EmailInfoType.builder()
                        .email(
                                EmailType.builder()
                                        .emailAddress(emailInfoSingleCall.getEmail().getEmailAddress()).build()).build()
        ).toList();

        List<PersonNameType> personNameType = extractPersonNameType(
            personNameTypeSingleCall);
        CustomerType customerType = CustomerType.builder().personName(personNameType).build();

        var reservationGuest = ReservationGuests.builder()
                .profileInfo(
                        ProfileInfo.builder()
                                .profile(
                                        ProfileType.builder()
                                                .emails(
                                                        ProfileTypeEmails.builder().emailInfo(emailInfoType).build())
                                                .customer(customerType).build()
                                ).build()
                ).build();

        reservationGuests.add(reservationGuest);
      });
      updateReservationRequest.setReservationGuests(reservationGuests);
    }

    return updateReservationRequest;

  }

  private static List<PersonNameType> extractPersonNameType(
      List<PersonNameTypeSingleCall> personNameTypeSingleCall) {
    return personNameTypeSingleCall.stream().map(pnType -> {
      PersonNameTypeType primary;
      if (null == pnType.getNameType()) {
        primary = null;
      } else {
        primary = switch (pnType.getNameType().toUpperCase()) {
          case "PRIMARY" -> PersonNameTypeType.PRIMARY;
          case "ALTERNATE" -> PersonNameTypeType.ALTERNATE;
          case "INCOGNITO" -> PersonNameTypeType.INCOGNITO;
          case "EXTERNAL" -> PersonNameTypeType.EXTERNAL;
          case "PHONETIC" -> PersonNameTypeType.PHONETIC;
          default -> null;
        };
      }
      return PersonNameType.builder()
              .givenName(pnType.getGivenName())
              .surname(pnType.getSurname())
              .nameTitle(pnType.getNameTitle())
              .nameType(primary)
              .build();
    }
    ).toList();
  }

  private UpdateReservationsRequest mapMissingInfoForEditRoom(ReservationById currentReservation,
          UpdateReservationRequest currentUpdateResRequest, BookingChannel bookingChannel, String companyId,
      String distributionIATANumber) {

    // map RoomStay
    mapRoomStay(currentReservation, currentUpdateResRequest, bookingChannel);

    // map CustomerType
    mapCustomerType(currentReservation, currentUpdateResRequest);

    // map email if exists
    mapEmailType(currentReservation, currentUpdateResRequest);

    List<UpdateReservationRequest> updateReservationRequestList = new ArrayList<>();

    updateReservationRequestList.add(currentUpdateResRequest);

    return UpdateReservationsRequest.builder()
            .updateReservationsRequest(updateReservationRequestList)
            .bookingChannel(bookingChannel)
            .distributionIATANumber(distributionIATANumber)
            .build();
  }

  private void mapCustomerType(ReservationById currentReservation, UpdateReservationRequest currentUpdateResRequest) {
    var personName = currentUpdateResRequest.getReservationGuests().get(0).getProfileInfo().getProfile().getCustomer()
            .getPersonName()
            .get(0);
    personName.setNameType(PersonNameTypeType.fromValue(currentReservation.getReservationGuestList()
            .get(0).getType()));
  }

  private void mapRoomStay(ReservationById currentReservation, UpdateReservationRequest currentRequest,
                           BookingChannel bookingChannel) {
    String arrivalDate = currentRequest.getRoomStay().getArrivalDate();
    String departureDate = currentRequest.getRoomStay().getDepartureDate();
    var ratePlanCode = currentReservation.getRoomStay().getRatePlanCode();
    List<RoomRate> roomRates = new ArrayList<>();

    var currentRoomRate = currentRequest.getRoomStay().getRoomRates().get(0);
    var pmsRoomType = roomSubstitutionForReservationsToUpdate(currentRoomRate, bookingChannel,
        currentRequest.getHotelId(), arrivalDate, departureDate);
    var specialRequests = currentRequest.getSpecialRequests();
    if (specialRequests == null || specialRequests.isEmpty()) {
      Optional
          .ofNullable(pmsRoomType.getSpecialRequest())
          .ifPresent(p -> currentRequest.setSpecialRequests(List.of(p)));
    }

    if (isRoomStayUpdated(currentReservation, currentRequest, bookingChannel)) {
      currentReservation.getRoomStay().getRatesPerNight().forEach(ratePerNight -> {
        var roomRateWithPrices = RoomRate.builder()
                .rates(RateType.builder()
                        .rate(List.of(AmountType.builder()
                        .base(TotalType.builder()
                                .amountBeforeTax(ratePerNight.getPricePerNight())
                                .currencyCode(currentReservation.getRateInfo().getSummary().getCurrencyCode())
                                .build())
                        .start(ratePerNight.getStartDate())
                        .end(ratePerNight.getStartDate())
                        .build()))
                        .build())
                .ratePlanCode(ratePlanCode)
                .roomOccupancy(currentRequest.getRoomStay().getRoomOccupancy())
                .startDate(ratePerNight.getStartDate())
                .endDate(ratePerNight.getStartDate())
                .fixedRate(true)
                .build();
        roomRateWithPrices.setRoomType(pmsRoomType.getType());
        roomRates.add(roomRateWithPrices);
      });
    } else {
      currentReservation.getRoomStay().getRatesPerNight().forEach(ratePerNight -> {
        var roomRateWithoutPrices = RoomRate.builder()
                .ratePlanCode(ratePlanCode)
                .roomOccupancy(currentRequest.getRoomStay().getRoomOccupancy())
                .startDate(ratePerNight.getStartDate())
                .endDate(ratePerNight.getStartDate())
                .roomType(currentReservation.getRoomStay().getRoomType())
                .fixedRate(true)
                .build();
        roomRates.add(roomRateWithoutPrices);
      });
    }
    currentRequest.getRoomStay().setRoomRates(roomRates);
  }

  private boolean isRoomStayUpdated(ReservationById currentReservation, UpdateReservationRequest currentRequest,
                                    BookingChannel bookingChannel) {
    String arrivalDate = currentReservation.getRoomStay().getArrivalDate().toString();
    String departureDate = currentReservation.getRoomStay().getDepartureDate().toString();
    var currentRoomRate = currentRequest.getRoomStay().getRoomRates().get(0);
    var roomType = roomSubstitutionForReservationsToUpdate(currentRoomRate, bookingChannel,
            currentRequest.getHotelId(), arrivalDate, departureDate);

    if (currentReservation.getRoomStay().getAdultCount()
            == currentRequest.getRoomStay().getRoomOccupancy().getAdultCount()
        && currentReservation.getRoomStay().getChildCount()
            == currentRequest.getRoomStay().getRoomOccupancy().getChildCount()
        && currentReservation.getRoomStay().getRoomType().equals(roomType.getType())) {
      return false;
    } else {
      return true;
    }
  }

  private void mapEmailType(ReservationById currentReservation, UpdateReservationRequest currentUpdateResRequest) {

    Optional<ProfileTypeEmails> emailsOptional = Optional.ofNullable(currentUpdateResRequest)
        .map(UpdateReservationRequest::getReservationGuests)
        .filter(guests -> !guests.isEmpty())
        .map(guests -> guests.get(0))
        .map(ReservationGuests::getProfileInfo)
        .map(ProfileInfo::getProfile)
        .map(ProfileType::getEmails);

    Optional<String> emailFromCurrentReservationOptional = Optional.ofNullable(currentReservation)
        .map(ReservationById::getReservationGuestList)
        .filter(guestList -> !guestList.isEmpty())
        .map(guestList -> guestList.get(0))
        .map(ReservationGuest::getEmail);

    emailsOptional
        .map(ProfileTypeEmails::getEmailInfo)
        .filter(emailInfoList -> !emailInfoList.isEmpty())
        .map(emailInfoList -> emailInfoList.get(0).getEmail())
        .ifPresent(emailType -> emailFromCurrentReservationOptional.ifPresent(emailFromReservation -> {
          if (emailType.getEmailAddress() == null) {
            emailType.setEmailAddress(emailFromReservation);
          }
        }));
  }

  private List<RoomRate> createRoomRates(UpdateReservationRequest updateReservationRequest,
      ReservationByBasketRefResponse reservations, RoomOccupancy roomOccupancy,
      String roomType, String ratePlanCode) {

    ReservationById originalResById = reservations.getReservationByIdList().get(0);
    LocalDate newArrivalDate = getLocalDateFromString(
        updateReservationRequest.getRoomStay().getArrivalDate());
    LocalDate newDepartureDate = getLocalDateFromString(
        updateReservationRequest.getRoomStay().getDepartureDate());
    LocalDate initialArrivalDate = originalResById.getRoomStay().getArrivalDate();
    LocalDate initialDepartureDate = originalResById.getRoomStay().getDepartureDate();

    List<RoomRate> currentRoomRates = new ArrayList<>();

    extractCurrentRoomRatesFromOriginalReservation(reservations, roomOccupancy, roomType, ratePlanCode,
        originalResById, newArrivalDate, newDepartureDate, currentRoomRates);

    createCurrentRoomRatesForNonPredefinedRatesNights(currentRoomRates, roomOccupancy, roomType, ratePlanCode,
        newArrivalDate, newDepartureDate, initialArrivalDate, initialDepartureDate);

    return getUpdateReservationRoomRates(updateReservationRequest, roomOccupancy, roomType, ratePlanCode,
        currentRoomRates);
  }

  private List<RoomRate> createRoomRatesWithPredefinedPrices(UpdateReservationRequest updateReservationRequest,
      ReservationByBasketRefResponse reservations, RoomOccupancy roomOccupancy, String roomType, 
      String ratePlanCode, List<Reservation> newRateReservations) {

    ReservationById originalResById = reservations.getReservationByIdList().get(0);
    LocalDate newArrivalDate = getLocalDateFromString(
        updateReservationRequest.getRoomStay().getArrivalDate());
    LocalDate newDepartureDate = getLocalDateFromString(
        updateReservationRequest.getRoomStay().getDepartureDate());
    List<RoomRate> currentRoomRates = new ArrayList<>();

    extractCurrentRoomRatesFromOriginalReservation(reservations, roomOccupancy, roomType, ratePlanCode, originalResById,
        newArrivalDate, newDepartureDate, currentRoomRates);

    extractRoomRatesFromPredefinedRates(newRateReservations, roomOccupancy, roomType, ratePlanCode, reservations,
        newArrivalDate, newDepartureDate, currentRoomRates);

    LocalDate initialArrivalDate = originalResById.getRoomStay().getArrivalDate();
    LocalDate initialDepartureDate = originalResById.getRoomStay().getDepartureDate();

    createCurrentRoomRatesForNonPredefinedRatesNights(currentRoomRates, roomOccupancy, roomType, ratePlanCode,
        newArrivalDate, newDepartureDate, initialArrivalDate, initialDepartureDate);

    return getUpdateReservationRoomRates(updateReservationRequest, roomOccupancy, roomType, ratePlanCode,
        currentRoomRates);
  }

  private void extractRoomRatesFromPredefinedRates(List<Reservation> newRateReservations,
      RoomOccupancy roomOccupancy, String roomType, String ratePlanCode, ReservationByBasketRefResponse reservations,
      LocalDate newArrivalDate, LocalDate newDepartureDate, List<RoomRate> currentRoomRates) {
    if (nonNull(newRateReservations)) {
      newRateReservations.stream()
          .map(Reservation::getRoomRates)
          .filter(rate -> rate.getRoomType().equals(roomType))
          .flatMap(rate ->
              Optional.ofNullable(rate.getRatePrices())
                  .orElseGet(List::of).stream()
          )
          .forEach(ratePerNight -> {
            LocalDate current = ratePerNight.getPriceStartDate();
            if (containsDate(current, newArrivalDate, newDepartureDate)) {
              var roomRate = createRoomRate(reservations, roomOccupancy, roomType, ratePlanCode,
                  ratePerNight.getAmount(), ratePerNight.getPriceStartDate().toString());
              currentRoomRates.add(roomRate);
            }
          });
    }
  }

  private void extractCurrentRoomRatesFromOriginalReservation(ReservationByBasketRefResponse reservations,
      RoomOccupancy roomOccupancy, String roomType, String ratePlanCode, ReservationById originalResById,
      LocalDate newArrivalDate, LocalDate newDepartureDate, List<RoomRate> currentRoomRates) {
    originalResById.getRoomStay().getRatesPerNight().forEach(ratePerNight -> {
      LocalDate current = getLocalDateFromString(ratePerNight.getStartDate());
      if (containsDate(current, newArrivalDate, newDepartureDate)) {
        var roomRate = createRoomRate(reservations, roomOccupancy, roomType, ratePlanCode,
            ratePerNight.getPricePerNight(), ratePerNight.getStartDate());
        currentRoomRates.add(roomRate);
      }
    });
  }

  private static List<RoomRate> getUpdateReservationRoomRates(UpdateReservationRequest updateReservationRequest,
      RoomOccupancy roomOccupancy, String roomType, String ratePlanCode, List<RoomRate> currentRoomRates) {
    if (currentRoomRates.isEmpty()) {
      return List.of(createRoomRate(roomOccupancy, roomType, ratePlanCode,
          updateReservationRequest.getRoomStay().getArrivalDate(),
          updateReservationRequest.getRoomStay().getDepartureDate(), false));
    }

    return currentRoomRates;
  }

  private static void createCurrentRoomRatesForNonPredefinedRatesNights(List<RoomRate> currentRoomRates,
      RoomOccupancy roomOccupancy, String roomType, String ratePlanCode, LocalDate newArrivalDate,
      LocalDate newDepartureDate, LocalDate initialArrivalDate, LocalDate initialDepartureDate) {
    
    if (newArrivalDate.isBefore(initialArrivalDate) && newDepartureDate.compareTo(initialArrivalDate) > 0) {
      int noOfNightsAdded = initialArrivalDate.compareTo(newArrivalDate);
      for (int i = noOfNightsAdded; i >= 1; i--) {
        var startDate = initialArrivalDate.minusDays(i).toString();
        var roomRate = createRoomRate(roomOccupancy, roomType, ratePlanCode, startDate, startDate, false);
        currentRoomRates.add(roomRate);
      }
    }

    if (newDepartureDate.isAfter(initialDepartureDate) && newArrivalDate.compareTo(initialDepartureDate) < 0) {
      int noOfNightsAdded = newDepartureDate.compareTo(initialDepartureDate);
      for (int i = 1; i <= noOfNightsAdded; i++) {
        var startDate = initialDepartureDate.plusDays((long) i - 1).toString();
        var roomRate = createRoomRate(roomOccupancy, roomType, ratePlanCode, startDate, startDate, false);
        currentRoomRates.add(roomRate);
      }
    }
  }

  private static RoomRate createRoomRate(ReservationByBasketRefResponse reservations, RoomOccupancy roomOccupancy,
                                   String roomType, String ratePlanCode, BigDecimal pricePerNght, String startDate) {
    return RoomRate.builder()
        .rates(RateType.builder().rate(List.of(AmountType.builder()
            .base(TotalType.builder()
                .amountBeforeTax(pricePerNght)
                .currencyCode(reservations.getCurrencyCode())
                .build())
            .start(startDate)
            .end(startDate)
            .build())).build())
        .roomType(roomType)
        .ratePlanCode(ratePlanCode)
        .roomOccupancy(roomOccupancy)
        .startDate(startDate)
        .endDate(startDate)
        .fixedRate(true)
        .build();
  }

  private static RoomRate createRoomRate(RoomOccupancy roomOccupancy, String roomType, String ratePlanCode,
                                      String startDate, String endDate, boolean fixedRate) {
    return RoomRate.builder()
        .roomType(roomType)
        .ratePlanCode(ratePlanCode)
        .roomOccupancy(roomOccupancy)
        .startDate(startDate)
        .endDate(endDate)
        .fixedRate(fixedRate)
        .build();
  }

  private LocalDate getLocalDateFromString(String stringDate) {
    return LocalDate.parse(stringDate, DateTimeFormatter.ofPattern("yyyy-MM-dd"));
  }

  private boolean containsDate(LocalDate current, LocalDate start, LocalDate end) {
    if (current.equals(start) || current.equals(end.minusDays(1))) {
      return true;
    }
    return (current.isAfter(start)) && (current.isBefore(end));
  }

  private RoomSubstitution roomSubstitutionForReservationsToUpdate(RoomRate roomRate, BookingChannel bookingChannel,
      String hotelId, String arrivalDate, String departureDate) {
    log.debug("Entered roomSubstitutionForReservationsToUpdate");

    if (hotelReservationOhipPort.getWbRoomTypes().contains(roomRate.getRoomType())) {
      return getPmsRoomType(hotelId, arrivalDate, departureDate, roomRate.getRoomType(),
              roomRate.getRoomOccupancy().getAdultCount(), roomRate.getRoomOccupancy().getChildCount(),
              bookingChannel.getChannel());
    } else {
      return RoomSubstitution.builder().type(roomRate.getRoomType()).build();
    }
  }

  private List<DepositPolicies> getDepositPolicies(
      ReservationByBasketRefResponse basketReservations) {
    return Optional.ofNullable(
            basketReservations.getReservationByIdList())
        .orElseGet(Collections::emptyList)
        .stream()
        .filter(Objects::nonNull)
        .flatMap(reservation -> reservation.getDepositPolicies().stream())
        .toList();
  }

  private List<DepositPolicies> getDepositPoliciesCreateReservation(
      ReservationResponse reservationResponse) {
    return Optional.ofNullable(
            reservationResponse.getReservations())
        .orElseGet(Collections::emptyList)
        .stream()
        .filter(Objects::nonNull)
        .flatMap(reservation -> reservation.getDepositPolicies().stream())
        .toList();
  }

  private boolean areAllUnique(List<DepositPolicies> depositPolicies,
      Function<DepositPolicies, String> mapper) {
    return depositPolicies
        .stream()
        .filter(Objects::nonNull)
        .map(mapper)
        .distinct()
        .count() == 1;
  }

  private BigDecimal computeSum(List<DepositPolicies> depositPolicies,
      Function<DepositPolicies, CurrencyAmountType> depositPoliciesMapper) {
    return depositPolicies
        .stream()
        .filter(Objects::nonNull)
        .map(depositPoliciesMapper)
        .filter(Objects::nonNull)
        .map(CurrencyAmountType::getAmount)
        .filter(Objects::nonNull)
        .reduce(BigDecimal.ZERO, BigDecimal::add);
  }

  /**
   * In case of adding a new room through amend flow, the request will come from FE with a bart roomType.
   * In this case, we have to convert it to a pmsRoomType.
   */
  private RoomSubstitution getPmsRoomType(String hotelId, String arrivalDate, String departureDate,
                                String roomType, int adultsNumber, int childrenNumber,
                                String bookingChannel) {

    var roomTypesFromSubstitution = hotelReservationOhipPort.getSubstitutionListFromRule(
        roomType, adultsNumber, childrenNumber, bookingChannel);

    var roomTypesFromInventory = hotelAvailabilityOutPort.getRoomTypesFromHotelInventory(
        hotelId, arrivalDate, departureDate, roomTypesFromSubstitution.size());

    var pmsRoomType = roomTypesFromSubstitution
        .stream()
        .filter(p -> roomTypesFromInventory.contains(p.getType()))
        .toList()
        .get(0);

    log.debug("Room substitution for room type is roomType={} and pmsRoomType={}", roomType,
        pmsRoomType);

    return pmsRoomType;
  }

  @Override
  public ReservationGuestResponse createReservationGuest(
      ReservationGuestRequest reservationGuestRequest) {
    log.debug("Entered createReservationGuest for hotelId={} and {} guests",
        reservationGuestRequest.getHotelId(),
        (reservationGuestRequest.getStayingGuests() == null ? 0 :
            reservationGuestRequest.getStayingGuests().size()));
    return hotelReservationOhipPort.createReservationGuest(reservationGuestRequest);
  }

  @Override
  public void updateBillingAddress(
      BillingAddressRequest billingAddressRequest) {
    log.debug("Entered updateBillingAddress for hotelId={}",
        billingAddressRequest.getHotelId());
    if ("ACCOUNT_COMPANY".equals(billingAddressRequest.getPaymentOption())) {
      hotelReservationOhipPort.updateBillingAddressCcui(billingAddressRequest);
    } else {
      hotelReservationOhipPort.updateBillingAddress(billingAddressRequest);
    }
  }


  @Override
  public void changeReservationRatePlan(RatePlanRoomTypeChangeRequest reservation) {
    log.debug("Entered changeReservation for hotelId={} and basketReference={}",
        reservation.getHotelId(), reservation.getBasketReferenceId());
    hotelReservationOhipPort.changeReservationRatePlan(reservation);
  }

  @Override
  public void changeReservationRoomType(RatePlanRoomTypeChangeRequest reservation) {
    log.debug("Entered changeReservationRoomType for hotelId={} and basketReference={}",
        reservation.getHotelId(), reservation.getBasketReferenceId());
    hotelReservationOhipPort.changeReservationRoomType(reservation);
  }

  @Override
  public ReservationPackagesResponse getReservationsPackagesByReservationsIds(String hotelId,
      Set<String> reservationIds) {
    log.debug(
        "Entered getReservationsPackagesByReservationsIds for hotelId={} and {} reservation ids",
        sanitize(hotelId), (reservationIds == null ? 0 : reservationIds.size()));
    return hotelReservationOhipPort.getReservationsPackagesByIds(hotelId, reservationIds);
  }

  @Override
  public ReservationPackagesResponse getReservationsPackagesMealInclusiveRateByReservationsIds(String hotelId,
      Set<String> reservationIds) {
    log.debug(
        "Entered getReservationsPackagesMealInclusiveRateByReservationsIds for hotelId={} and {} reservation ids",
        sanitize(hotelId), (reservationIds == null ? 0 : reservationIds.size()));
    return hotelReservationOhipPort.getReservationsPackagesMealInclusiveRateByReservationsIds(hotelId, reservationIds);
  }

  @Override
  public CancelReservationResponse cancelReservation(
      CancelReservationRequest cancelReservationRequest) {
    log.debug("Entered cancelReservation for hotelId={} and {} reservation ids",
        cancelReservationRequest.getHotelId(),
        (cancelReservationRequest.getReservationIds() == null ? 0 :
            cancelReservationRequest.getReservationIds().size()));
    return hotelReservationOhipPort.cancelReservation(cancelReservationRequest);
  }

  @Override
  public CancelInformationResponse getCancelInformation(String hotelId, Set<String> reservationIds,
      String userDateTime) {
    log.debug("Entered getCancelInformation for hotelId={} and {} reservation ids",
        sanitize(hotelId), reservationIds == null ? 0 : reservationIds.size());
    return hotelReservationOhipPort.getCancelInformation(hotelId, reservationIds, userDateTime);
  }

  @Override
  public ReservationDetailsEnhancedResponse getReservationsByExternalRefId(String externalReferenceId) {
    log.debug("Entered getReservationsByExternalReferenceIds for externalReferenceId={}",
        sanitize(externalReferenceId));
    return hotelReservationOhipPort.getReservationsByExternalRefId(addWildCardToReferenceId(externalReferenceId));
  }

  @Override
  public ReservationIdDetailsResponse getReservationsByReservationId(String hotelId,
      String reservationId) {
    log.debug("Entered getReservationsByReservationId for reservationId={}", sanitize(reservationId));
    return hotelReservationOhipPort.getReservationsByReservationId(hotelId, reservationId);
  }

  private String addWildCardToReferenceId(String externalReferenceId) {
    return externalReferenceId.charAt(3) == 'R'
        ? externalReferenceId + "-%" :
        externalReferenceId;
  }

  @Override
  public SearchBookingsResponse searchBookings(BookingSearchCriteria bookingSearchCriteria) {
    if (StringUtils.isNotEmpty(bookingSearchCriteria.getBookingReference())) {
      bookingSearchCriteria.setBookingReference(addWildCardToReferenceId(bookingSearchCriteria.getBookingReference()));
    }
    return hotelReservationOhipPort.searchBookings(bookingSearchCriteria);
  }

  @Override
  public DepositsResponse getDepositsForReservationId(String hotelId, String reservationId) {
    log.info("Entered getDepositsForReservationId for hotelId={} and reservationIds={}",
            sanitize(hotelId), sanitize(reservationId));
    return hotelReservationOhipPort.getDepositsForReservationId(hotelId, reservationId);
  }

  @Override
  public MarketingPreferencesResponse getMarketingPreferences(String hotelId, String reservationId) {
    log.info("Entered getMarketingPreferences for hotelId={} and reservationIds={}",
        sanitize(hotelId), sanitize(reservationId));
    return hotelReservationOhipPort.getMarketingPreferences(hotelId, reservationId);
  }

  @Override
  public BookingAllowancesResponse getBookingAllowances(String hotelId, String reservationId,
      List<String> basketBookingAllowances) {
    log.info(
        "Entered getBookingAllowances for hotelId={} and reservationIds={} and basketBookingAllowances={}",
        sanitize(hotelId), sanitize(reservationId), sanitize(basketBookingAllowances));
    return hotelReservationOhipPort.getBookingAllowances(hotelId, reservationId, basketBookingAllowances);
  }

  @Override
  public void deleteReservation(String hotelId, String reservationId) {
    log.info("Entered deleteReservation for hotelId={} and reservationId={}",
        sanitize(hotelId), sanitize(reservationId));
    hotelReservationOhipPort.deleteReservation(hotelId, reservationId);
  }

  @Override
  public void movePaymentDetails(String hotelId, Set<String> reservationIds) {
    log.info("Entered movePaymentDetails for hotelId={} and reservationId={}",
        sanitize(hotelId), sanitize(reservationIds));
    hotelReservationOhipPort.movePaymentDetails(hotelId, reservationIds);
  }

  @Override
  public void updateBookerEmail(UpdateBookerEmailRequest updateBookerEmailRequest) {
    hotelReservationOhipPort.updateBookerEmail(updateBookerEmailRequest);
  }

  @Override
  public ConfirmReservationResponse updateReservationSingleCall(
      BusinessItemsRequest businessItemsRequest,
      SpecialRequests specialRequests, ReservationGuestRequest guestReservationRequest,
      ReservationPackagesRequest updateReservationPackageRequest,
      ConfirmReservationRequest confirmReservationRequest) {
    log.debug(
        "Entered updateReservation Single Call Logic");
    var shouldUpdatePackages = Objects.isNull(updateReservationPackageRequest) ? false
        : shouldUpdatePackages(updateReservationPackageRequest);
    return hotelReservationOhipPort.updateReservationSingleCall(businessItemsRequest, specialRequests,
        guestReservationRequest, updateReservationPackageRequest, confirmReservationRequest,
        shouldUpdatePackages);
  }

  @Override
  public ReservationProfiles createProfiles(ReservationGuestRequest guestReservationRequest) {
    return hotelReservationOhipPort.createProfiles(guestReservationRequest);
  }

  @Override
  public PreCheckInResponse addAttachmentToReservation(
      ReservationFileAttachmentRequest reservationFileAttachmentRequest) {
    log.debug("Entered addAttachmentToReservation for hotelId = {} and reservationId = {}",
        reservationFileAttachmentRequest.getHotelId(),
        reservationFileAttachmentRequest.getReservationId());
    return hotelReservationOhipPort.addAttachmentToReservation(reservationFileAttachmentRequest);
  }

  @Override
  public PreCheckInResponse saveReservationPreCheckIn(PreCheckInRequest preCheckInRequest) {
    log.debug("Entered saveReservationPreCheckIn for hotelId = {} and reservationId = {}",
        preCheckInRequest.getHotelId(), preCheckInRequest.getReservationId());
    return hotelReservationOhipPort.saveReservationPreCheckIn(preCheckInRequest);
  }

  @Override
  public void deleteReservationPreCheckIn(String hotelId, String reservationId) {
    log.debug("Entered deleteReservationPreCheckIn for hotelId = {} and reservationId = {}",
        sanitize(hotelId), sanitize(reservationId));
    hotelReservationOhipPort.deleteReservationPreCheckIn(hotelId, reservationId);
  }

  @Override
  public void deleteRegCardAttachment(String hotelId, String reservationId) {
    log.debug("Entered deleteRegCardAttachment for hotelId = {} and reservationId = {}",
        sanitize(hotelId), sanitize(reservationId));
    hotelReservationOhipPort.deleteRegCardAttachment(hotelId, reservationId);
  }

  @Override
  public void linkReservationToLeisureCustomer(
      LinkReservationToLeisureCustomerRequest linkReservationToLeisureCustomerRequest) {
    log.debug("Entered linkReservationToLeisureCustomer for hotelId={} and reservationIds={}",
        linkReservationToLeisureCustomerRequest.getHotelId(),
        String.join(", ", linkReservationToLeisureCustomerRequest.getReservationIds()));

    hotelReservationOhipPort.linkReservationToLeisureCustomer(linkReservationToLeisureCustomerRequest);
  }

  @Override
  public void updateReservationPreferences(
      ReservationPreferencesRequest reservationPreferencesRequest) {
    String hotelId = reservationPreferencesRequest.getHotelId();
    List<String> reservationsIds = reservationPreferencesRequest.getReservationsIds();
    log.debug("Entered updateReservationPreferences for hotelId = {} and reservationId = {}", hotelId,
        reservationsIds);
    hotelReservationOhipPort.updateReservationPreferences(reservationPreferencesRequest);
  }

  @Override
  public void updateReservationAlerts(
      UpdateReservationAlertsRequest updateReservationUdfRequestDto) {

    hotelReservationOhipPort.updateReservationAlerts(updateReservationUdfRequestDto);
  }

  @Override
  public ReservationAmounts getReservationAmounts(String hotelId, Set<String> reservationIds) {
    return hotelReservationOhipPort.getReservationAmounts(hotelId, reservationIds);
  }

  private String getLeadGuestProfileId(
      List<uk.co.whitbread.hotel.ohip.adapter.generated.models.Reservation> reservations) {
    return reservations.stream()
        .filter(reservation -> reservation.getReservations()
            .getReservation()
            .get(0)
            .getReservationGuests()
            .stream()
            .anyMatch(ResGuestType::getPrimary))
        .map(reservation -> reservation.getReservations().getReservation().get(0).getReservationGuests().get(0)
            .getProfileInfo().getProfileIdList().get(0).getId()).findFirst().orElse("");
  }

  private boolean isNoDuplicateProfileCreationEnabled(String channel) {
    return (PI_CHANNEL.equals(channel)
        && unleashWrapper.isEnabled(unleashWrapper.featureFlag().getNoDuplicateProfileCreationPi()))
        || (BUSINESS_BOOKER_CHANNEL.equals(channel)
        && unleashWrapper.isEnabled(unleashWrapper.featureFlag().getNoDuplicateProfileCreationBb()))
        || (CCUI_CHANNEL.equals(channel)
        && unleashWrapper.isEnabled(unleashWrapper.featureFlag().getNoDuplicateProfileCreationCcui()));
  }

  @Override
  public PreCheckInResponse saveReservationPreRegister(PreCheckInRequest preCheckInRequest) {
    log.debug("Entered saveReservationPreRegister for hotelId = {} and reservationId = {}",
            preCheckInRequest.getHotelId(), preCheckInRequest.getReservationId());
    return hotelReservationOhipPort.saveReservationPreRegister(preCheckInRequest);
  }
}
