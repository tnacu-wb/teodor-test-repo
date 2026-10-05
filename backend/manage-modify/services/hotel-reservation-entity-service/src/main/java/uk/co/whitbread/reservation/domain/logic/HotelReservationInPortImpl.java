package uk.co.whitbread.reservation.domain.logic;

import static java.time.temporal.ChronoUnit.DAYS;
import static java.util.Collections.emptyList;
import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;
import static java.util.Optional.ofNullable;
import static uk.co.whitbread.hotel.ohip.adapter.generated.models.basket.BasketDto.StatusEnum.OPEN;
import static uk.co.whitbread.reservation.ErrorCode.DIGITAL_UPDATE_ROOM_EXCEPTION;
import static uk.co.whitbread.reservation.domain.constants.HotelReservationConstants.BB_BOOKING_CHANNEL;
import static uk.co.whitbread.reservation.domain.constants.HotelReservationConstants.BD;
import static uk.co.whitbread.reservation.domain.constants.HotelReservationConstants.BU;
import static uk.co.whitbread.reservation.domain.constants.HotelReservationConstants.CCUI_BOOKING_CHANNEL;
import static uk.co.whitbread.reservation.domain.constants.HotelReservationConstants.CNP_ALERT_AREA;
import static uk.co.whitbread.reservation.domain.constants.HotelReservationConstants.CNP_ALERT_CODE;
import static uk.co.whitbread.reservation.domain.constants.HotelReservationConstants.CNP_ALERT_DESCRIPTION;
import static uk.co.whitbread.reservation.domain.constants.HotelReservationConstants.COUNTRY_CODE_GB;
import static uk.co.whitbread.reservation.domain.constants.HotelReservationConstants.COUNTRY_GB;
import static uk.co.whitbread.reservation.domain.constants.HotelReservationConstants.CREDIT_CARD_TYPE_AEM_ID;
import static uk.co.whitbread.reservation.domain.constants.HotelReservationConstants.DISTR_BOOKING_CHANNEL;
import static uk.co.whitbread.reservation.domain.constants.HotelReservationConstants.FOLIO_VIEW_CNP;
import static uk.co.whitbread.reservation.domain.constants.HotelReservationConstants.FOLIO_VIEW_CP;
import static uk.co.whitbread.reservation.domain.constants.HotelReservationConstants.ID_CONTEXT;
import static uk.co.whitbread.reservation.domain.constants.HotelReservationConstants.LANGUAGE_EN;
import static uk.co.whitbread.reservation.domain.constants.HotelReservationConstants.PIBA_CARD_TYPE_AEM_ID;
import static uk.co.whitbread.reservation.domain.logic.AmendLogicInPortImpl.DEFAULT_LANGUAGE_EN;
import static uk.co.whitbread.reservation.domain.logic.AmendLogicInPortImpl.getTotal;
import static uk.co.whitbread.reservation.domain.logic.utils.UpdatePackagesUtils.getOptionalPackageForReservation;
import static uk.co.whitbread.reservation.domain.logic.utils.UpdatePackagesUtils.getPackageForReservation;
import static uk.co.whitbread.reservation.domain.logic.utils.UpdatePackagesUtils.getRoomSelectionsElement;
import static uk.co.whitbread.reservation.domain.model.in.BookingChannel.PI_BOOKING_CHANNEL;
import static uk.co.whitbread.reservation.domain.model.in.PaymentOption.ACCOUNT_COMPANY;
import static uk.co.whitbread.reservation.domain.model.in.PaymentOption.PAY_NOW;
import static uk.co.whitbread.reservation.domain.model.in.PaymentOption.PAY_ON_ARRIVAL;
import static uk.co.whitbread.reservation.domain.model.in.PaymentOption.RESERVE_WITHOUT_CARD;
import static uk.co.whitbread.reservation.domain.utils.SanitizingUtils.sanitize;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.Strings;
import org.springframework.security.access.AccessDeniedException;
import uk.co.whitbread.commons.exceptions.utils.ExceptionLogger;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.basket.BasketDto.StatusEnum;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.basket.BasketError;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.basket.BasketError.TypeEnum;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.basket.CreateBasketRequestDto.BasketStatusEnum;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.basket.CreateBasketRequestDto.PaymentOptionEnum;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.basket.PromoKind;
import uk.co.whitbread.promo.service.generated.models.promotion.PromoCodeStatus;
import uk.co.whitbread.reservation.ErrorCode;
import uk.co.whitbread.reservation.domain.constants.HotelReservationConstants;
import uk.co.whitbread.reservation.domain.constants.ReasonForStay;
import uk.co.whitbread.reservation.domain.exceptions.AmendErrorCode;
import uk.co.whitbread.reservation.domain.exceptions.AmendReservationException;
import uk.co.whitbread.reservation.domain.exceptions.AmendStayDateException;
import uk.co.whitbread.reservation.domain.exceptions.BasketNotFoundException;
import uk.co.whitbread.reservation.domain.exceptions.CancelReservationException;
import uk.co.whitbread.reservation.domain.exceptions.GenericBadRequestException;
import uk.co.whitbread.reservation.domain.exceptions.GenericReservationException;
import uk.co.whitbread.reservation.domain.exceptions.HotelReservationNotFoundException;
import uk.co.whitbread.reservation.domain.exceptions.HotelReservationOhipException;
import uk.co.whitbread.reservation.domain.exceptions.InvalidSourceCodeException;
import uk.co.whitbread.reservation.domain.exceptions.PromotionException;
import uk.co.whitbread.reservation.domain.exceptions.ReservationNotFoundException;
import uk.co.whitbread.reservation.domain.exceptions.RulesAgentBadRequestException;
import uk.co.whitbread.reservation.domain.exceptions.SchedulePackageException;
import uk.co.whitbread.reservation.domain.logic.utils.BusinessAllowancesUtils;
import uk.co.whitbread.reservation.domain.logic.utils.ManageBookingUtils;
import uk.co.whitbread.reservation.domain.logic.utils.PaymentUtils;
import uk.co.whitbread.reservation.domain.logic.utils.ReservationUtils;
import uk.co.whitbread.reservation.domain.logic.utils.UserDefinedFieldsConstants;
import uk.co.whitbread.reservation.domain.model.amend.in.AmendOnHoldInterval;
import uk.co.whitbread.reservation.domain.model.amend.in.DepositFolioComputationResult;
import uk.co.whitbread.reservation.domain.model.availability.in.CorporateRate;
import uk.co.whitbread.reservation.domain.model.availability.in.HotelAvailabilityByIdsV2Request;
import uk.co.whitbread.reservation.domain.model.availability.in.Rate;
import uk.co.whitbread.reservation.domain.model.availability.in.Room;
import uk.co.whitbread.reservation.domain.model.basket.allowances.UpdateAllowancesRequest;
import uk.co.whitbread.reservation.domain.model.feature.FeatureFlag;
import uk.co.whitbread.reservation.domain.model.feature.UnleashWrapper;
import uk.co.whitbread.reservation.domain.model.in.Alert;
import uk.co.whitbread.reservation.domain.model.in.AmendDistributionSingleCallRequest;
import uk.co.whitbread.reservation.domain.model.in.AmendDistributionStayDatesRequest;
import uk.co.whitbread.reservation.domain.model.in.AmendStayDatesRequest;
import uk.co.whitbread.reservation.domain.model.in.AttachReservationProfileRequest;
import uk.co.whitbread.reservation.domain.model.in.BookerAddressCnp;
import uk.co.whitbread.reservation.domain.model.in.BookerDetailsCnp;
import uk.co.whitbread.reservation.domain.model.in.BookerDetailsCnpRequest;
import uk.co.whitbread.reservation.domain.model.in.BookingChannel;
import uk.co.whitbread.reservation.domain.model.in.BusinessAllowance;
import uk.co.whitbread.reservation.domain.model.in.BusinessItems;
import uk.co.whitbread.reservation.domain.model.in.BusinessItemsRequest;
import uk.co.whitbread.reservation.domain.model.in.CancelReservationRequest;
import uk.co.whitbread.reservation.domain.model.in.CdhSearchBookingsRequest;
import uk.co.whitbread.reservation.domain.model.in.CompanyQuestionAndAnswerDetailsRequest;
import uk.co.whitbread.reservation.domain.model.in.ConfirmAmendOnReservationsRequest;
import uk.co.whitbread.reservation.domain.model.in.ConfirmAmendRequest;
import uk.co.whitbread.reservation.domain.model.in.ConfirmReservationRequest;
import uk.co.whitbread.reservation.domain.model.in.CopyBookingRequest;
import uk.co.whitbread.reservation.domain.model.in.CopyReservationsRequest;
import uk.co.whitbread.reservation.domain.model.in.CreateMemoRequest;
import uk.co.whitbread.reservation.domain.model.in.DepositFoliosRequest;
import uk.co.whitbread.reservation.domain.model.in.EmailRequest;
import uk.co.whitbread.reservation.domain.model.in.LinkReservationToLeisureCustomerRequest;
import uk.co.whitbread.reservation.domain.model.in.OperationType;
import uk.co.whitbread.reservation.domain.model.in.PackagesRequest;
import uk.co.whitbread.reservation.domain.model.in.PackagesSelection;
import uk.co.whitbread.reservation.domain.model.in.PackagesSelectionScheduled;
import uk.co.whitbread.reservation.domain.model.in.PaymentCard;
import uk.co.whitbread.reservation.domain.model.in.PaymentOption;
import uk.co.whitbread.reservation.domain.model.in.PaymentOptionFolioViewEnum;
import uk.co.whitbread.reservation.domain.model.in.PreCheckInRequest;
import uk.co.whitbread.reservation.domain.model.in.RatePrice;
import uk.co.whitbread.reservation.domain.model.in.Reservation;
import uk.co.whitbread.reservation.domain.model.in.ReservationFileAttachmentRequest;
import uk.co.whitbread.reservation.domain.model.in.ReservationGuestRequest;
import uk.co.whitbread.reservation.domain.model.in.ReservationPackagesRequest;
import uk.co.whitbread.reservation.domain.model.in.ReservationPackagesScheduledRequest;
import uk.co.whitbread.reservation.domain.model.in.ReservationPreferencesRequest;
import uk.co.whitbread.reservation.domain.model.in.ReservationProfiles;
import uk.co.whitbread.reservation.domain.model.in.ReservationRequest;
import uk.co.whitbread.reservation.domain.model.in.RoomRate;
import uk.co.whitbread.reservation.domain.model.in.RoomReservationPackagesScheduledRequest;
import uk.co.whitbread.reservation.domain.model.in.RoomsSelections;
import uk.co.whitbread.reservation.domain.model.in.RoomsSelectionsByReservationId;
import uk.co.whitbread.reservation.domain.model.in.SpecialRequests;
import uk.co.whitbread.reservation.domain.model.in.UpdateBookerEmailRequest;
import uk.co.whitbread.reservation.domain.model.in.UpdateCancellationPoliciesRequest;
import uk.co.whitbread.reservation.domain.model.in.UpdateCnpReservationRequest;
import uk.co.whitbread.reservation.domain.model.in.UpdateDiscountRequest;
import uk.co.whitbread.reservation.domain.model.in.UpdateEmailReservationRequest;
import uk.co.whitbread.reservation.domain.model.in.UpdateReasonForStayRequest;
import uk.co.whitbread.reservation.domain.model.in.UpdateRequest;
import uk.co.whitbread.reservation.domain.model.in.UpdateReservationAlertsRequest;
import uk.co.whitbread.reservation.domain.model.in.UpdateReservationCcAgentIdRequest;
import uk.co.whitbread.reservation.domain.model.in.UpdateReservationOverrideReasonsRequest;
import uk.co.whitbread.reservation.domain.model.in.UpdateReservationPackagesByIdRequest;
import uk.co.whitbread.reservation.domain.model.in.UpdateReservationRequest;
import uk.co.whitbread.reservation.domain.model.in.UpdateReservationSingleCallRequest;
import uk.co.whitbread.reservation.domain.model.in.UpdateReservationsRequest;
import uk.co.whitbread.reservation.domain.model.in.UpdateRoomStayRequest;
import uk.co.whitbread.reservation.domain.model.in.UpdatedReservationsDistribution;
import uk.co.whitbread.reservation.domain.model.index.header.data.out.HotelPaymentInformation;
import uk.co.whitbread.reservation.domain.model.out.AmendStayDatesResponse;
import uk.co.whitbread.reservation.domain.model.out.BasketItemResponse;
import uk.co.whitbread.reservation.domain.model.out.BasketResponse;
import uk.co.whitbread.reservation.domain.model.out.BookingAllowance;
import uk.co.whitbread.reservation.domain.model.out.BookingAllowancesResponse;
import uk.co.whitbread.reservation.domain.model.out.CancelReservationResponse;
import uk.co.whitbread.reservation.domain.model.out.CancellationPoliciesResponse;
import uk.co.whitbread.reservation.domain.model.out.CdhResults;
import uk.co.whitbread.reservation.domain.model.out.ChannelRuleRequestDetails;
import uk.co.whitbread.reservation.domain.model.out.ChannelRuleResponse;
import uk.co.whitbread.reservation.domain.model.out.CharacterUDFs;
import uk.co.whitbread.reservation.domain.model.out.ConfirmReservationResponse;
import uk.co.whitbread.reservation.domain.model.out.ConfirmationCustomer;
import uk.co.whitbread.reservation.domain.model.out.ConfirmationRoomStay;
import uk.co.whitbread.reservation.domain.model.out.CopyBookingResponse;
import uk.co.whitbread.reservation.domain.model.out.CopyReservationResponse;
import uk.co.whitbread.reservation.domain.model.out.CopyReservationsResponse;
import uk.co.whitbread.reservation.domain.model.out.CurrencyAmount;
import uk.co.whitbread.reservation.domain.model.out.CurrencyAmountType;
import uk.co.whitbread.reservation.domain.model.out.DepositFolio;
import uk.co.whitbread.reservation.domain.model.out.DepositFolioCharge;
import uk.co.whitbread.reservation.domain.model.out.DepositFoliosResponse;
import uk.co.whitbread.reservation.domain.model.out.Deposits;
import uk.co.whitbread.reservation.domain.model.out.DepositsResponse;
import uk.co.whitbread.reservation.domain.model.out.HotelInfoResponse;
import uk.co.whitbread.reservation.domain.model.out.MarketingPreferencesResponse;
import uk.co.whitbread.reservation.domain.model.out.Meal;
import uk.co.whitbread.reservation.domain.model.out.MemosResponse;
import uk.co.whitbread.reservation.domain.model.out.OhipReservationCreationResponse;
import uk.co.whitbread.reservation.domain.model.out.OhipReservationResponse;
import uk.co.whitbread.reservation.domain.model.out.PackagesResponse;
import uk.co.whitbread.reservation.domain.model.out.PreCheckInResponse;
import uk.co.whitbread.reservation.domain.model.out.ReservationBooker;
import uk.co.whitbread.reservation.domain.model.out.ReservationBookerAddress;
import uk.co.whitbread.reservation.domain.model.out.ReservationByBasketRefResponse;
import uk.co.whitbread.reservation.domain.model.out.ReservationByIdDetailsResponse;
import uk.co.whitbread.reservation.domain.model.out.ReservationByIdResponse;
import uk.co.whitbread.reservation.domain.model.out.ReservationDetails;
import uk.co.whitbread.reservation.domain.model.out.ReservationGuestResponse;
import uk.co.whitbread.reservation.domain.model.out.ReservationId;
import uk.co.whitbread.reservation.domain.model.out.ReservationIdDetailsResponse;
import uk.co.whitbread.reservation.domain.model.out.ReservationInfo;
import uk.co.whitbread.reservation.domain.model.out.ReservationPackagesDetailsResponse;
import uk.co.whitbread.reservation.domain.model.out.ReservationPaymentCardType;
import uk.co.whitbread.reservation.domain.model.out.ReservationResponse;
import uk.co.whitbread.reservation.domain.model.out.ReservationsDetailsEnhancedResponse;
import uk.co.whitbread.reservation.domain.model.out.ReservationsDetailsResponse;
import uk.co.whitbread.reservation.domain.model.out.ReservationsPackagesResponse;
import uk.co.whitbread.reservation.domain.model.out.RoomRates;
import uk.co.whitbread.reservation.domain.model.out.RoomStay;
import uk.co.whitbread.reservation.domain.model.out.RoomStayByIdResponse;
import uk.co.whitbread.reservation.domain.model.out.Rooms;
import uk.co.whitbread.reservation.domain.model.out.RoomsSelectionsByReservation;
import uk.co.whitbread.reservation.domain.model.out.SaveReservationResponse;
import uk.co.whitbread.reservation.domain.model.out.TempBookingRefResponse;
import uk.co.whitbread.reservation.domain.model.out.UniqueIDType;
import uk.co.whitbread.reservation.domain.model.out.UpdateCnpReservationResponse;
import uk.co.whitbread.reservation.domain.model.out.UpdateReasonForStayResponse;
import uk.co.whitbread.reservation.domain.model.out.UpdateReservationOverrideReasonsResponse;
import uk.co.whitbread.reservation.domain.model.out.UserDefinedFields;
import uk.co.whitbread.reservation.domain.model.promotion.out.PromoKindResponse;
import uk.co.whitbread.reservation.domain.model.searchrules.out.RoomOccupancy;
import uk.co.whitbread.reservation.domain.ports.primary.AmendDistributionLogicInPort;
import uk.co.whitbread.reservation.domain.ports.primary.AmendLogicInPort;
import uk.co.whitbread.reservation.domain.ports.primary.CdhSearchBookingInPort;
import uk.co.whitbread.reservation.domain.ports.primary.HotelReservationInPort;
import uk.co.whitbread.reservation.domain.ports.primary.ManageBookingInPort;
import uk.co.whitbread.reservation.domain.ports.secondary.BasketOutPort;
import uk.co.whitbread.reservation.domain.ports.secondary.ContentOutPort;
import uk.co.whitbread.reservation.domain.ports.secondary.HotelAccountOutPort;
import uk.co.whitbread.reservation.domain.ports.secondary.HotelAvailabilityOutPort;
import uk.co.whitbread.reservation.domain.ports.secondary.HotelReservationOhipOutPort;
import uk.co.whitbread.reservation.domain.ports.secondary.PromotionOutPort;
import uk.co.whitbread.reservation.domain.ports.secondary.RulesOutPort;
import uk.co.whitbread.reservation.domain.properties.BusinessBookerConfigProperties;
import uk.co.whitbread.reservation.domain.properties.CompanyProperties;
import uk.co.whitbread.reservation.domain.properties.DistributionProperties;
import uk.co.whitbread.reservation.domain.properties.PackageProperties;
import uk.co.whitbread.reservation.domain.properties.PromotionProperties;
import uk.co.whitbread.shared.auth.account.Account;
import uk.co.whitbread.shared.auth.security.AuthenticatedUserService;
import uk.co.whitbread.shared.commons.logging.trace.ConcurrentTracer;

@Slf4j
@RequiredArgsConstructor
public class HotelReservationInPortImpl implements HotelReservationInPort {

  public static final String OPERA = "Opera";
  public static final String EXT_REF_CONTENT_ID = "BART_OHIP";
  public static final String BOOKING_TYPE_ANON = "ANON";
  public static final String BOOKING_TYPE_EMPTY = "";
  public static final List<String> BUSINESS_BOOKER = List.of("42", "92", "91", "93");
  public static final String CANCELLED_STATUS = "Cancelled";
  public static final String CHECKED_IN_STATUS = "InHouse";
  public static final String CHECKED_OUT_STATUS = "CheckedOut";
  public static final String NO_SHOW_STATUS = "NoShow";
  public static final String CITYTAX = "CITYTAX";
  public static final String HSATWN = "HSATWN";
  private static final Set<PaymentOption> NO_CARD_OPTIONS = EnumSet.of(ACCOUNT_COMPANY, RESERVE_WITHOUT_CARD);
  private static final Integer MINUTES_OF_AVAILABLE_BASKET = 30;
  private static final String EMPLOYEE_RATE_PLAN = "EMPLOYEE";
  private static final String ZCHRY = "ZCHRY";
  private static final String CHRTY = "CHRTY";
  private static final String ZCHR10 = "ZCHR10";
  private static final String ZCHR11 = "ZCHR11";
  private static final String ZCHR12 = "ZCHR12";
  private static final String ZCHR13 = "ZCHR13";
  private static final String HUB_BRAND = "HUB";
  private static final String DELIMITER = "|";
  public static final int DISTRIBUTION_IATA_NUMBER_LENGTH = 8;
  public static final String RESERVATION_PACKAGES_MUST_BE_IN_THE_SAME_ORDER =
      "The reservation ids for RoomSelection Packages must be in the same order!";
  public static final String BOOKING_ARRIVAL = "arrival";
  public static final String BOOKING_DEPARTURE = "departure";
  protected static final String BOOKING_FLOW_CLAIM = "bookingFlow";

  private final HotelReservationOhipOutPort hotelReservationOhipOutPort;
  private final HotelAvailabilityOutPort hotelAvailabilityOutPort;
  private final BasketOutPort basketOutPort;
  private final RulesOutPort rulesOutPort;
  private final CdhSearchBookingInPort cdhSearchBookingInPort;
  private final ContentOutPort contentOutPort;
  private final HotelAccountOutPort hotelAccountOutPort;
  private final AmendLogicInPort amendLogic;
  private final ManageBookingInPort manageBookingInPort;
  private final AuthenticatedUserService authenticatedUserService;
  private final BusinessBookerConfigProperties businessBookerConfigProperties;
  private final AmendDistributionLogicInPort amendDistributionLogicInPort;
  private final ReservationCleanup reservationCleanUp;
  private final AmendPayNowLogic amendPayNowLogic;
  private final CompanyProperties companyProperties;
  private final DistributionProperties distributionProperties;
  private final ConcurrentTracer concurrentTracer;
  private final boolean isCcuiBicOperaConfirmation;
  private final UnleashWrapper<FeatureFlag> unleashWrapper;
  private final PromotionProperties promotionProperties;
  private final PackageProperties packageProperties;
  private final ManageBookingLogic manageBookingLogic;
  private final PromotionOutPort promotionOutPort;

  @Override
  public ReservationResponse createReservation(ReservationRequest createReservationRequest) {
    log.info("Entered createReservation with reservation list size={}",
        createReservationRequest.getReservations().size());

    if (!createReservationRequest.getReservations().isEmpty()) {
      validateDistributionIataNumber(createReservationRequest);
    }

    String hotelId = createReservationRequest.getReservations().get(0).getHotelId();

    var basket = basketOutPort
        .createBasket(hotelId, createReservationRequest.getBookingChannel().getChannel(),
                createReservationRequest.getBookingChannel().getSubchannel());

    createReservationRequest.getReservations()
        .forEach(r -> r.setExternalReferenceId(basket.getBookingReference()));

    if (authenticatedUserService.isUserAuthenticated()) {
      authenticatedUserService.getCurrentUserAccount().ifPresent(account -> {
        if (createReservationRequest.getBookingChannel().isBb()) {
          createReservationRequest.getReservations()
              .forEach(r -> {
                r.setUserAccountId(account.getEmployeeId());
                r.setCompanyAccountId(account.getCompanyId());
                r.setOperaCompanyId(account.getOperaCompanyId());
              });
        } else if (createReservationRequest.getBookingChannel().isPi()) {
          createReservationRequest.getReservations()
              .forEach(r -> r.setUserAccountId(account.getCustomerId()));
        } else if (createReservationRequest.getBookingChannel().isCcui()
                && !unleashWrapper.isEnabled(unleashWrapper.featureFlag().getCcuiAgentIdLog())) {
          createReservationRequest.getReservations()
              .forEach(r -> r.setCcuiUserEmailId(account.getEmail()));
        }
      });
    }

    createReservationRequest.getReservations()
        .forEach(
            reservation -> reservation.setBookingType(findBookingType(createReservationRequest)));

    if (!isAllowedFixedRates(createReservationRequest.getBookingChannel())
        && createReservationRequest.getReservations().stream().anyMatch(res ->
        CollectionUtils.isNotEmpty(res.getRoomRates().getRatePrices()))) {
      log.info("Removing fixed rate prices as request is not authorized for such operations. Channel: {}, user: {}",
          createReservationRequest.getBookingChannel().getChannel(), authenticatedUserService.isUserAuthenticated()
              ? authenticatedUserService.getCurrentUserAccount().map(Account::getEmail).orElse("ANON")
              : "ANON");
      createReservationRequest.getReservations().forEach(res -> res.getRoomRates().setRatePrices(null));
    }

    int noOfRooms = createReservationRequest.getReservations().size();
    if (unleashWrapper.isEnabled(unleashWrapper.featureFlag().getApplyOccupancySupplement()) && noOfRooms > 1) {
      createReservationRequest.setGetReservationsByIds(true);
    }

    //Update the reason for stay to NTLEI if we want to remove the city tax from OPERA
    if (unleashWrapper.isEnabled(unleashWrapper.featureFlag().getReleasePiCcuiCityTaxUk())) {
      if (isCityTaxSupportedChannel(createReservationRequest.getBookingChannel())) {
        String reasonForStay = getReasonForStay(hotelId, COUNTRY_GB, LANGUAGE_EN, ReasonForStay.LEI.name(),
            createReservationRequest.getReservations().get(0).getArrival());
        createReservationRequest.setReasonForStay(reasonForStay);
      }
    }

    var promoBasket = resolveUniquePromo(createReservationRequest.getReservations(), basket);
    var internalReservations = hotelReservationOhipOutPort
        .createReservation(hotelId, createReservationRequest, basket.getReference());

    Integer noOfAdults = null;
    if (unleashWrapper.isEnabled(unleashWrapper.featureFlag().getApplyOccupancySupplement()) && noOfRooms == 1) {
      noOfAdults = createReservationRequest.getReservations().get(0).getAdultsNumber();
    }

    boolean isOta = Boolean.TRUE.equals(createReservationRequest.getIsOta());

    basketOutPort.addReservationsToBasket(
        basket.getReference(), promoBasket.getETag(), internalReservations, false,
        isOccupancySupplementApplicable(hotelId), noOfAdults, isOta);

    return ReservationResponse.builder()
        .basketReference(basket.getReference())
        .reservations(internalReservations.getReservations())
        .totalCost(internalReservations.getTotalCost())
        .hotelId(internalReservations.getHotelId())
        .currencyCode(internalReservations.getCurrencyCode())
        .build();
  }

  private BasketResponse resolveUniquePromo(List<Reservation> reservationList,
      BasketResponse basket) {

    if (!isPromoApplicable(reservationList, basket)) {
      return basket;
    }

    RoomRate roomRate = reservationList.get(0).getRoomRates();
    PromoKind promoKind = roomRate.getPromoKind();
    String promoCode = roomRate.getPromotionCode();

    if (promoKind != PromoKind.UNIQUE) {
      return addPromoToBasket(basket, promoCode, promoKind);
    }

    PromoKindResponse promoResponse = fetchAndValidateUniquePromo(promoCode);

    String resolvedPromoCode =
        resolveOperaPromoCode(promoResponse, promoCode);

    updateReservationsWithResolvedPromo(
        reservationList, promoCode, resolvedPromoCode);

    return addPromoToBasket(basket, promoCode, promoKind);
  }

  private boolean isPromoApplicable(List<Reservation> reservations,
      BasketResponse basket) {

    if (CollectionUtils.isEmpty(reservations) || basket == null) {
      return false;
    }

    RoomRate roomRate = reservations.get(0).getRoomRates();
    if (roomRate == null) {
      return false;
    }

    return roomRate.getPromoKind() != null
        && StringUtils.isNotBlank(roomRate.getPromotionCode());
  }

  private BasketResponse addPromoToBasket(BasketResponse basket,
      String promoCode, PromoKind promoKind) {

    return basketOutPort.addPromotionToBasket(
        basket.getReference(),
        promoCode,
        promoKind,
        basket.getETag()
    );
  }

  private PromoKindResponse fetchAndValidateUniquePromo(String promoCode) {

    PromoKindResponse response =
        promotionOutPort.getPromoKind(promoCode);

    if (ObjectUtils.isEmpty(response)) {
      throwAndLogPromotionException(
          ErrorCode.DIGITAL_PROMOTION_NOT_FOUND,
          String.format(
              "No promotion details present for the unique promo code '%s'",
              promoCode
          )
      );
    }

    PromoCodeStatus status = response.getUniquePromoCodeStatus();

    switch (status) {
      case REDEEMED:
        throwAndLogPromotionException(
            ErrorCode.DIGITAL_PROMOTION_ALREADY_USED_EXCEPTION,
            String.format(
                "The promotion code '%s' has already been used.",
                promoCode
            )
        );
        break;

      case EXPIRED:
        throwAndLogPromotionException(
            ErrorCode.DIGITAL_PROMOTION_EXPIRED_EXCEPTION,
            String.format(
                "The promotion code '%s' has expired.",
                promoCode
            )
        );
        break;

      default:
        break;
    }

    return response;
  }

  private String resolveOperaPromoCode(PromoKindResponse response,
      String clientPromoCode) {

    if (response == null) {
      return clientPromoCode;
    }

    return StringUtils.isNotBlank(response.getOperaPromoCode())
        ? response.getOperaPromoCode()
        : clientPromoCode;
  }

  private void updateReservationsWithResolvedPromo(List<Reservation> reservations,
      String originalPromoCode, String resolvedPromoCode) {

    if (resolvedPromoCode.equals(originalPromoCode)) {
      return;
    }

    reservations.forEach(reservation ->
        reservation.getRoomRates()
            .setPromotionCode(resolvedPromoCode)
    );
  }

  private void throwAndLogPromotionException(ErrorCode errorCode, String message) {
    PromotionException exception =
        new PromotionException(errorCode, message);
    ExceptionLogger.log(log, exception);
    throw exception;
  }

  private boolean isCityTaxSupportedChannel(BookingChannel channel) {
    return channel.isPi() || channel.isBb() || channel.isCcui();
  }

  private boolean isOccupancySupplementApplicable(String hotelId) {
    return unleashWrapper.isEnabled(unleashWrapper.featureFlag().getApplyOccupancySupplement())
        && BigDecimal.ZERO.compareTo(rulesOutPort.getSingleOccupancySupplementResponse(hotelId).getPricing()) != 0;
  }

  private boolean isAllowedFixedRates(BookingChannel bookingChannel) {
    return bookingChannel.isAllowedFixedRate()
        && authenticatedUserService.isUserAuthenticated()
        && (authenticatedUserService.getAuthenticatedUserAuthorities()
        .contains(distributionProperties.getFixedRateAuthority())
        || Optional.ofNullable(authenticatedUserService.getAuthenticatedUser().getToken()
            .getClaimAsStringList("permissions"))
        .map(permissions -> permissions.contains(distributionProperties.getFixedRateAuthority()))
        .orElse(false)
        || Optional.ofNullable(authenticatedUserService.getAuthenticatedUser().getToken().getClaimAsString("scope"))
        .map(scope -> scope.contains(distributionProperties.getFixedRateAuthority()))
        .orElse(false));
  }

  private void validateDistributionIataNumber(ReservationRequest createReservationRequest) {
    createReservationRequest.getReservations().forEach(reservation -> {
      if (!StringUtils.isBlank(reservation.getDistributionIATANumber())
          && reservation.getDistributionIATANumber().trim().length() != DISTRIBUTION_IATA_NUMBER_LENGTH) {
        var ex = new GenericBadRequestException(ErrorCode.DIGITAL_INVALID_IATA_NUMBER_EXCEPTION,
            String.format("Invalid IATA number: %s", reservation.getDistributionIATANumber()));
        ExceptionLogger.log(log, ex);
        throw ex;
      }
    });
  }

  @Override
  public ReservationsDetailsResponse getReservationsByBasketReference(
      String hotelId,
      String basketReference,
      int limit, int offset) {
    var basket = basketOutPort.getBasketById(basketReference);
    return hotelReservationOhipOutPort.getReservationsByBasketReference(hotelId,
        basket.getBookingReference(),
        limit, offset);
  }

  @Override
  public ConfirmReservationResponse confirmReservation(
      ConfirmReservationRequest confirmReservationRequest,
      Optional<DepositFoliosResponse> optionalDepositFoliosResponse) {

    if (PAY_NOW.equals(confirmReservationRequest.getPaymentOption())) {
      var reservationDetails = hotelReservationOhipOutPort
          .getReservationsByIds(confirmReservationRequest.getHotelId(),
              List.of(confirmReservationRequest.getReservationId()), false);
      if (reservationDetails.getBookingReference() != null) {
        var bookingRef = reservationDetails.getBookingReference();
        if (bookingRef.contains("-")) {
          bookingRef = bookingRef.split("-")[0];
        }
        var basket = basketOutPort.getBasketByReference(bookingRef);

        var deposit = reservationDetails.getAmountPaid();
        var outStandingBalance = reservationDetails.getBalanceOutstanding();
        if (!deposit.equals(BigDecimal.ZERO) && (!outStandingBalance.equals(BigDecimal.ZERO)) && basket.isPresent()) {

          if (PaymentOption.PAY_ON_ARRIVAL.equals(basket.get().getPaymentOption())) {
            ConfirmReservationResponse confirmReservationResponse = new ConfirmReservationResponse();
            confirmReservationResponse.setPartialPaid(true);
            return confirmReservationResponse;

          } else if (PaymentOption.PAY_NOW.equals(basket.get().getPaymentOption())) {
            return getConfirmResResponsePayNow(confirmReservationRequest, reservationDetails,
                basket.get(), optionalDepositFoliosResponse);
          }
        } else {
          getAndSaveCharges(confirmReservationRequest.getHotelId(),
              Set.of(confirmReservationRequest.getReservationId()), optionalDepositFoliosResponse);
        }
      } else {
        getAndSaveCharges(confirmReservationRequest.getHotelId(),
            Set.of(confirmReservationRequest.getReservationId()), optionalDepositFoliosResponse);
      }
    }
    return hotelReservationOhipOutPort.confirmReservation(confirmReservationRequest);
  }

  //To get the confirm Reservation response after posting deposits
  private ConfirmReservationResponse getConfirmResResponsePayNow(
      ConfirmReservationRequest confirmReservationRequest,
      ReservationByBasketRefResponse reservationDetails,
      BasketResponse basket,
      Optional<DepositFoliosResponse> optionalDepositFoliosResponse) {
    var dbDepositFolios = getDepositFoliosFromDb(
        List.of(confirmReservationRequest.getReservationId()));
    log.debug("dbDepositFolios values {} ", dbDepositFolios);

    DepositFoliosResponse generatedDepositFolios;
    if (optionalDepositFoliosResponse.isEmpty()) {
      generatedDepositFolios = hotelReservationOhipOutPort.getGeneratedDepositFolios(
          confirmReservationRequest.getHotelId(),
          Set.of(confirmReservationRequest.getReservationId()));
    } else {
      List<DepositFolio> currentDepositFolios = optionalDepositFoliosResponse.get()
          .getDepositFolios()
          .stream()
          .filter(dp -> dp.getReservationId().equals(confirmReservationRequest.getReservationId()))
          .toList();
      generatedDepositFolios = DepositFoliosResponse.builder()
          .depositFolios(currentDepositFolios)
          .build();
    }
    log.debug("generatedDepositFolios values {} ", generatedDepositFolios);

    var databaseDFMap = Optional.ofNullable(
        amendPayNowLogic.consolidateDepositFolios(dbDepositFolios)).orElse(null);
    log.debug("databaseDFMap values {} ", databaseDFMap);
    var generatedDFMap = generatedDepositFolios.getDepositFolios()
        .stream()
        .collect(Collectors.toMap(DepositFolio::getReservationId,
            value -> value.getCharges().stream().collect(
                Collectors.toMap(
                    dfKey -> dfKey.getReference() + "-" + dfKey.getTransactionCode(),
                    dfValue -> dfValue.getCurrencyAmount().getAmount()))));

    log.debug("generatedDFMap values {} ", generatedDFMap);
    var finalDepositMap = new HashMap<String, BigDecimal>();
    var dbCharges = new HashSet<String>();
    //To Handle new room booking amend
    if (databaseDFMap == null && generatedDFMap.containsKey(
        confirmReservationRequest.getReservationId())) {
      finalDepositMap.putAll(generatedDFMap.get(confirmReservationRequest.getReservationId()));
    } else {
      var reservationID = confirmReservationRequest.getReservationId();
      var generatedDFMapInternal = generatedDFMap.get(reservationID);
      var databaseDFMapInternal = (databaseDFMap != null) ? databaseDFMap.get(reservationID) : null;
      if (generatedDFMapInternal != null) {
        generatedDFMapInternal.forEach((generatedTransactionCode, generatedAmount) -> {
          if (databaseDFMapInternal != null
              && databaseDFMapInternal.get(generatedTransactionCode) != null) {
            if (generatedAmount.subtract(databaseDFMapInternal.get(generatedTransactionCode))
                .doubleValue() != 0) {
              BigDecimal value = generatedAmount.subtract(
                  databaseDFMapInternal.get(generatedTransactionCode));
              dbCharges.add(StringUtils.joinWith("_", generatedTransactionCode,
                  value.stripTrailingZeros().toPlainString()));
            }
          } else {
            finalDepositMap.put(generatedTransactionCode, generatedAmount);
          }
        });
      }
    }
    log.debug("DBCharges={}", dbCharges);
    for (String value : dbCharges) {
      var splitValue = value.split("_");
      if (splitValue.length >= 2) {
        finalDepositMap.put(splitValue[0], new BigDecimal(splitValue[1]));
      }
    }
    log.debug("FinalCharges={}", finalDepositMap);

    if (!finalDepositMap.keySet().isEmpty()) {
      var finalDepositFolioResponse = prepareAndSaveDepositFolioResponse(confirmReservationRequest, basket,
          generatedDepositFolios, finalDepositMap);
      basketOutPort.saveCharges(finalDepositFolioResponse);
    }

    return getConfirmReservationResponse(reservationDetails, confirmReservationRequest);
  }

  //Building Confirm Reservation Response
  private ConfirmReservationResponse getConfirmReservationResponse(
      ReservationByBasketRefResponse reservationDetails,
      ConfirmReservationRequest confirmReservationRequest) {
    log.debug("Entered Method getConfirmReservationResponse()");
    ConfirmReservationResponse confirmReservationRes = new ConfirmReservationResponse();

    confirmReservationRes.setReservationIdList(List.of(UniqueIDType.builder()
        .id(confirmReservationRequest.getReservationId()).type("Reservation").build()));
    confirmReservationRes.setHotelId(reservationDetails.getHotelId());
    confirmReservationRes.setRoomStay(ConfirmationRoomStay.builder()
        .arrivalDate(LocalDate.parse(reservationDetails.getReservationByIdList()
            .get(0).getRoomStay().getArrivalDate()))
        .departureDate(LocalDate.parse(reservationDetails.getReservationByIdList()
            .get(0).getRoomStay().getDepartureDate()))
        .build());
    confirmReservationRes.setReservationGuest(ConfirmationCustomer.builder()
        .givenName(
            reservationDetails.getReservationByIdList().get(0).getReservationGuestList().get(0)
                .getGivenName())
        .surName(reservationDetails.getReservationByIdList().get(0)
            .getReservationGuestList().get(0).getSurName()).build());
    confirmReservationRes
        .setReservationStatus(reservationDetails
            .getReservationByIdList().get(0).getReservationStatus());
    return confirmReservationRes;
  }

  //Building DepositFolio Response to post in OPERA
  private DepositFoliosResponse prepareAndSaveDepositFolioResponse(
      ConfirmReservationRequest confirmReservationRequest,
      BasketResponse basket, DepositFoliosResponse generatedDepositFolios,
      Map<String, BigDecimal> finalDepositMap) {
    log.debug("Entered Method prepareAndSaveDepositFolioResponse()");

    List<DepositFolioCharge> charges = new ArrayList<>();
    finalDepositMap.forEach((keyFinal, finalValue) ->
        charges.add(DepositFolioCharge
            .builder()
            .transactionCode(keyFinal.substring(keyFinal.lastIndexOf("-") + 1))
            .quantity(1)
            .reference(keyFinal.substring(0, keyFinal.lastIndexOf("-")))
            .currencyAmount(CurrencyAmount.builder()
                .amount(finalValue)
                .currencyCode(generatedDepositFolios
                    .getDepositFolios()
                    .get(0)
                    .getCharges()
                    .get(0).getCurrencyAmount()
                    .getCurrencyCode())
                .build())
            .build()));
    DepositFoliosResponse finalDepositFolioResponse = DepositFoliosResponse
        .builder()
        .depositFolios(List.of(DepositFolio.builder()
            .reservationId(confirmReservationRequest.getReservationId())
            .hotelId(confirmReservationRequest.getHotelId())
            .vatRegion(generatedDepositFolios.getDepositFolios().stream()
                .map(DepositFolio::getVatRegion).collect(Collectors.toSet()).toString())
            .paymentId(basket.getPaymentID())
            .defaultPaymentMethod(confirmReservationRequest.getPaymentMethod())
            .charges(charges)
            .build())).build();
    log.info("Save final finalDepositFolioResponse: {}: ", finalDepositFolioResponse);
    hotelReservationOhipOutPort.saveCharges(finalDepositFolioResponse);
    return finalDepositFolioResponse;
  }

  @Override
  public ReservationByBasketRefResponse getAllReservationsJustByBasketReference(
      String basketReference, Boolean priceBreakdownNeeded) {
    return this.getAllReservationsJustByBasketReference(basketReference, priceBreakdownNeeded,
        true);
  }

  public ReservationByBasketRefResponse getAllReservationsJustByBasketReference(
      String basketReference, Boolean priceBreakdownNeeded, Boolean rateInfoNeeded) {
    log.info("Entered get all reservations by basket reference with basketReference={}, "
            + "priceBreakdownNeeded={}, rateInfoNeeded={}.", sanitize(basketReference), priceBreakdownNeeded,
        rateInfoNeeded);

    var basket = basketOutPort.getBasketById(basketReference);
    var reservationsIds = basket.getItems().stream().map(BasketItemResponse::getSourceId).toList();
    return getReservationByBasketRefResponse(basket, priceBreakdownNeeded, rateInfoNeeded,
        reservationsIds);
  }

  private ReservationByBasketRefResponse getAllReservations(BasketResponse basket,
      Boolean priceBreakdownNeeded, Boolean rateInfoNeeded) {
    var reservationsIds = getReservationIds(basket);
    return getReservationByBasketRefResponse(basket, priceBreakdownNeeded, rateInfoNeeded,
        reservationsIds);
  }

  private boolean hasCityTaxIncluded(List<ReservationByIdResponse> reservationByIdList) {
    return Optional.of(reservationByIdList).stream()
        .flatMap(Collection::stream)
        .map(ReservationByIdResponse::getReservationPackageList)
        .filter(Objects::nonNull)
        .flatMap(Collection::stream)
        .anyMatch(packageItem -> CITYTAX.equals(packageItem.getPackageCode())
            && BigDecimal.ZERO.compareTo(packageItem.getUnitPrice()) < 0);
  }

  @Override
  public ReservationsPackagesResponse getReservationsPackagesByBasketRef(
      String hotelId, String basketReferenceId, boolean mealInclusiveRate) {
    return hotelReservationOhipOutPort.getReservationsPackagesByBasketRef(hotelId,
        basketReferenceId, mealInclusiveRate);
  }

  @Override
  public SaveReservationResponse updateReservationPackages(
      ReservationPackagesRequest reservationPackagesRequest) {
    return hotelReservationOhipOutPort.updateReservationPackages(reservationPackagesRequest);
  }

  @Override
  public SaveReservationResponse updateReservationPackagesById(
      UpdateReservationPackagesByIdRequest reservationPackagesByIdRequest, boolean isForAmend) {
    // verify previous room selections and currentRoomSelections reservationIds order and size MUST
    // be the same to go further, because the updateReservationPackages from OutPort requires reservationIds in the list
    // and opera is updating the packages as follows:
    // first reservation - remove first previousRoomSelections- add first roomSelections list
    List<String> reservationIdsForRoomSelectionPackages = getReservationIdsFromRoomSelectionsPackages(
        reservationPackagesByIdRequest.getRoomsSelections());

    List<String> reservationIdsForPreviousSelectionPackages = getReservationIdsFromRoomSelectionsPackages(
        reservationPackagesByIdRequest.getPreviousRoomsSelections());

    if (!reservationIdsForRoomSelectionPackages.equals(
        reservationIdsForPreviousSelectionPackages)) {
      var ex = new GenericBadRequestException(ErrorCode.DIGITAL_INCONSISTENT_PACKAGES_EXCEPTION,
          RESERVATION_PACKAGES_MUST_BE_IN_THE_SAME_ORDER);
      ExceptionLogger.log(log, ex);
      throw ex;
    }
    excludePackages(reservationPackagesByIdRequest);
    //reverted 55244 no filtering for amend update reservation -->
    // repate booking will fail at switch from UK to DE hotels
    if (!isForAmend) {
      PackagesRequest packagesRequest = PackagesRequest.builder()
          .adultsNumber(1)
          .childrenNumber(1)
          .startDate(reservationPackagesByIdRequest.getArrival())
          .endDate(reservationPackagesByIdRequest.getDeparture())
          .hotelId(reservationPackagesByIdRequest.getHotelId())
          .nightsNumber(calculateNumberOfNights(reservationPackagesByIdRequest.getArrival(),
              reservationPackagesByIdRequest.getDeparture()))
          .build();

      var roomsSelection = reservationPackagesByIdRequest.getRoomsSelections();
      if (roomsSelection.isEmpty()) {
        return hotelReservationOhipOutPort.updateReservationPackagesByReservationId(
            reservationPackagesByIdRequest);
      }

      PackagesResponse packagesResponse = hotelReservationOhipOutPort.getPackages(packagesRequest);
      reservationPackagesByIdRequest.getRoomsSelections()
          .forEach(roomsSelectionsByReservationId -> {
            List<PackagesSelection> interimPackages = roomsSelectionsByReservationId
                .getPackagesSelection()
                .stream()
                .filter(selectedPackage -> packagesResponse.getPackages().getMeals().stream()
                    .anyMatch(pack -> pack.getId().equals(selectedPackage.getId())))
                .toList();

            roomsSelectionsByReservationId.setPackagesSelection(interimPackages);
          });
    }

    return hotelReservationOhipOutPort.updateReservationPackagesByReservationId(
        reservationPackagesByIdRequest);
  }

  @Override
  public UpdateReservationPackagesByIdRequest buildUpdateReservationPackagesByIdSingleCall(
          UpdateReservationPackagesByIdRequest reservationPackagesByIdRequest) {
    validateReservationPackages(reservationPackagesByIdRequest);
    // remove twin room packages from current and prev package selections of the amend request
    // as twin room package cannot be removed from a reservation
    filterReservationPackages(reservationPackagesByIdRequest);

    hotelReservationOhipOutPort.updateReservationPackagesByReservationId(
        reservationPackagesByIdRequest);

    return reservationPackagesByIdRequest;
  }

  private static void filterReservationPackages(
      UpdateReservationPackagesByIdRequest reservationPackagesByIdRequest) {
    reservationPackagesByIdRequest.getRoomsSelections()
            .forEach(rs -> {
              if (Objects.nonNull(rs.getPackagesSelection()) && !rs.getPackagesSelection().isEmpty()) {
                rs.setPackagesSelection(rs.getPackagesSelection().stream()
                    .filter(ps -> !ps.getId().equals(HSATWN)).toList());
              }
            });
    reservationPackagesByIdRequest.getPreviousRoomsSelections()
            .forEach(prs -> {
              if (!prs.getPackagesSelection().isEmpty()) {
                prs.setPackagesSelection(prs.getPackagesSelection().stream()
                    .filter(ps -> !ps.getId().equals(HSATWN)).toList());
              }
            });
  }

  private void validateReservationPackages(UpdateReservationPackagesByIdRequest reservationPackagesByIdRequest) {
    List<String> reservationIdsForRoomSelectionPackages = getReservationIdsFromRoomSelectionsPackages(
            reservationPackagesByIdRequest.getRoomsSelections());

    List<String> reservationIdsForPreviousSelectionPackages = getReservationIdsFromRoomSelectionsPackages(
            reservationPackagesByIdRequest.getPreviousRoomsSelections());

    if (!reservationIdsForRoomSelectionPackages.equals(
            reservationIdsForPreviousSelectionPackages)) {
      log.error(RESERVATION_PACKAGES_MUST_BE_IN_THE_SAME_ORDER);
      throw new GenericBadRequestException(ErrorCode.DIGITAL_PACKAGES_ORDER,
          RESERVATION_PACKAGES_MUST_BE_IN_THE_SAME_ORDER);
    }
  }

  private Integer calculateNumberOfNights(String arrival, String departure) {

    LocalDate startDate = LocalDate.parse(arrival);
    LocalDate endDate = LocalDate.parse(departure);
    return Math.toIntExact(DAYS.between(startDate, endDate));
  }

  private List<String> getReservationIdsFromRoomSelectionsPackages(
      List<RoomsSelectionsByReservationId> roomsSelectionsByReservationIds) {
    List<String> reservationIds = new ArrayList<>();
    roomsSelectionsByReservationIds.forEach(roomsSelectionsByReservationId ->
        reservationIds.add(roomsSelectionsByReservationId.getReservationId()));
    return reservationIds;
  }

  @Override
  public ReservationGuestResponse createReservationGuest(
      String basketReference,
      ReservationGuestRequest reservationGuestRequest) {
    log.info("Entered createReservationGuest with basketReference={}, guest list size={}",
        basketReference, reservationGuestRequest.getStayingGuests().size());

    var basket = basketOutPort.getBasketById(basketReference);
    if (StatusEnum.PAY_PENDING.name().equals(basket.getStatus())) {
      log.info("Could not update guest details for basketReference={} when status is PAY_PENDING",
          basketReference);
    } else {
      var reservationsIds = basket.getItems().stream().map(BasketItemResponse::getSourceId)
          .toList();
      if (Boolean.FALSE.equals(reservationGuestRequest.getPreCheckIn())) {
        var guests = reservationGuestRequest.getStayingGuests();

        IntStream.range(0, guests.size())
            .forEach(i -> guests.get(i).setReservationId(reservationsIds.get(i)));
      }

      // Change booking type and additional UDFs if user logged in on GDP
      // Not applicable for CCUI (see findBookingType)
      if (!CCUI_BOOKING_CHANNEL.equals(basket.getChannel())
          && authenticatedUserService.isUserAuthenticated()) {
        authenticatedUserService.getCurrentUserAccount().ifPresent(account -> {
          reservationGuestRequest.setBookingType(basket.getChannel());

          if (BB_BOOKING_CHANNEL.equals(basket.getChannel())) {
            reservationGuestRequest.setUserAccountId(account.getEmployeeId());
            reservationGuestRequest.setCompanyAccountId(account.getCompanyId());
          } else if (PI_BOOKING_CHANNEL.equals(basket.getChannel())) {
            reservationGuestRequest.setUserAccountId(account.getCustomerId());
          }
        });
      }

      String arrivalDate = getArrivalDate(reservationGuestRequest.getHotelId(), reservationsIds);
      //Update the reason for stay to NTLEI or NTBUS if we want to remove the city tax from OPERA
      reservationGuestRequest.setReasonForStay(getReasonForStay(reservationGuestRequest.getHotelId(),
          COUNTRY_GB, LANGUAGE_EN, reservationGuestRequest.getReasonForStay(), arrivalDate));

      hotelReservationOhipOutPort.createReservationGuest(reservationGuestRequest);

      if (PI_BOOKING_CHANNEL.equals(basket.getChannel())
          && authenticatedUserService.isUserAuthenticated()
          && Objects.nonNull(reservationGuestRequest.getUpdateProfileConsent())
          && Boolean.TRUE.equals(reservationGuestRequest.getUpdateProfileConsent())) {
        hotelAccountOutPort.updateCustomer(reservationGuestRequest.getBooker(),
            authenticatedUserService.getAuthenticatedUser().getAccount().getCustomerId(),
            authenticatedUserService.getAuthenticatedUser().getToken().getTokenValue());
      }
    }
    return new ReservationGuestResponse(basketReference);
  }

  @Override
  public SaveReservationResponse updateReservationRateCode(
      UpdateRequest updateRateCodeRequest) {
    return hotelReservationOhipOutPort.updateReservationRateCode(updateRateCodeRequest);
  }

  @Override
  public SaveReservationResponse updateRoomType(
      UpdateRequest updateRoomTypeRequest) {
    return hotelReservationOhipOutPort.updateRoomType(updateRoomTypeRequest);
  }

  @Override
  public CancelReservationResponse cancelReservation(
      CancelReservationRequest cancelReservationRequest) {
    log.debug("Entered cancelReservation with basketReference={}",
        cancelReservationRequest.getBasketReference());

    String ccAgentId = null;
    if (!authenticatedUserService.isUserAuthenticated()) {
      ManageBookingUtils.validateToken(cancelReservationRequest.getToken(),
          cancelReservationRequest.getBasketReference());
    } else if (unleashWrapper.isEnabled(unleashWrapper.featureFlag().getCcuiAgentIdLog())) {
      var authenticatedUser = authenticatedUserService.getAuthenticatedUser();
      var bookingFlow = authenticatedUser.getToken().getClaim(BOOKING_FLOW_CLAIM);
      if (BookingChannel.CCUI_BOOKING_CHANNEL.equals(bookingFlow)) {
        ccAgentId = authenticatedUser.getAccount().getEmail();
      }
    }

    var basket = basketOutPort.getBasketById(cancelReservationRequest.getBasketReference());
    var reservationsIds = basket.getItems().stream().map(BasketItemResponse::getSourceId).toList();
    if (isNull(reservationsIds) || reservationsIds.isEmpty()) {
      var ex = new GenericReservationException(ErrorCode.DIGITAL_NULL_OPERA_RESERVATIONS_EXCEPTION,
          "No reservationsIds could be retrieved from Opera");
      ExceptionLogger.log(log, ex);
      throw ex;
    }
    var hotelId = basket.getHotelId();
    var reservations = hotelReservationOhipOutPort
        .getReservationsByIds(hotelId, reservationsIds, false);
    if (isNull(reservations) || isNull(reservations.getReservationByIdList())
        || reservations.getReservationByIdList().isEmpty()) {
      var ex = new GenericReservationException(ErrorCode.DIGITAL_OPERA_RESERVATIONS_EXCEPTION,
          "No reservations could be retrieved from Opera");
      ExceptionLogger.log(log, ex);
      throw ex;
    }
    reservations.setHotelId(hotelId);

    var amountToRefund = reservations.getAmountPaid();
    List<DepositFoliosResponse> prepaidDeposits = new ArrayList<>();
    if (PAY_NOW.equals(basket.getPaymentOption())) {
      prepaidDeposits = reservationsIds.stream()
          .map(basketOutPort::getCharges).filter(Objects::nonNull).toList();
      amountToRefund = getAmountToRefund(prepaidDeposits);

      setDefaultPaymentMethodInCancelRequest(cancelReservationRequest, basket, reservations);
    }

    //Check Opera reservation has a status that cannot be canceled
    if (reservations.getReservationByIdList().stream()
        .anyMatch(
            reservation -> reservation.getReservationStatus().equalsIgnoreCase(CANCELLED_STATUS)
                || reservation.getReservationStatus().equalsIgnoreCase(CHECKED_IN_STATUS)
            || reservation.getReservationStatus().equalsIgnoreCase(CHECKED_OUT_STATUS)
            || reservation.getReservationStatus().equalsIgnoreCase(NO_SHOW_STATUS)
        )) {
      var ex = new GenericReservationException(ErrorCode.DIGITAL_CANCEL_RESERVATION,
          "Reservation cannot be cancelled");
      ExceptionLogger.log(log, ex);
      throw ex;
    }

    //Update the CCUI Agent ID in the reservation before sending CANCEL request to ohip
    if (!CollectionUtils.isEmpty(reservationsIds) && Objects.nonNull(ccAgentId)) {
      hotelReservationOhipOutPort.updateReservationCcAgentId(
          UpdateReservationCcAgentIdRequest.builder()
              .hotelId(hotelId)
              .reservationIds(new HashSet<>(reservationsIds))
              .ccAgentId(ccAgentId)
              .clearFirst(false)
              .build());
    }

    final var cancelReservationResponse = processCancel(cancelReservationRequest, reservationsIds,
            PaymentUtils.getPaymentOption(basket, reservations), true, prepaidDeposits);

    processRefund(basket, reservations, amountToRefund, cancelReservationResponse.getRefundedDeposits());
    updateChargesAfterRefund(reservations, prepaidDeposits, basket.getChannel(), basket.getPaymentOption());

    var emailRequest = emailRequestBuilder(cancelReservationRequest.getBasketReference(), reservations,
        cancelReservationResponse.getRefundedDeposits(), false, basket.getPaymentID());
    basketOutPort.triggerEmailConfirmation(emailRequest);

    return cancelReservationResponse;
  }

  @Override
  public CancelReservationResponse cancelOnHoldReservation(
      CancelReservationRequest cancelOnHoldReservationRequest) {
    log.debug("Entered cancelOnHoldReservation with basketReference={}",
        cancelOnHoldReservationRequest.getBasketReference());

    var basket = basketOutPort.getBasketById(cancelOnHoldReservationRequest.getBasketReference());
    //Basket status check
    if (!basket.getStatus().equals(BasketStatusEnum.OPEN.toString())) {
      var ex = new GenericReservationException(ErrorCode.DIGITAL_BASKET_RIGHT_STATUS_EXCEPTION,
          "Basket does not have the right status or it does not exist.");
      ExceptionLogger.log(log, ex);
      throw ex;
    }
    var reservationsIds = basket.getItems().stream().map(BasketItemResponse::getSourceId).toList();
    if (isNull(reservationsIds) || reservationsIds.isEmpty()) {
      var ex = new GenericReservationException(ErrorCode.DIGITAL_BASKET_ATTACHED_EXCEPTION,
          "No on hold related reservationsIds are attached to the basket.");
      ExceptionLogger.log(log, ex);
      throw ex;
    }
    var reservations = hotelReservationOhipOutPort.getReservationsByIds(basket.getHotelId(),
        reservationsIds, false, false, false);

    if (isNull(reservations) || isNull(reservations.getReservationByIdList())
        || reservations.getReservationByIdList().isEmpty()) {
      var ex = new GenericReservationException(ErrorCode.DIGITAL_ON_HOLD_EXCEPTION,
          "No on hold related reservations could be retrieved from Opera.");
      ExceptionLogger.log(log, ex);
      throw ex;
    }

    //Check Opera reservation is not already canceled
    if (reservations.getReservationByIdList().stream()
        .anyMatch(
            reservation -> reservation.getReservationStatus().equalsIgnoreCase(CANCELLED_STATUS))) {
      var ex = new GenericReservationException(ErrorCode.DIGITAL_CANCELLED_EXCEPTION,
          "Reservation already cancelled");
      ExceptionLogger.log(log, ex);
      throw ex;
    }
    //Check that opera reservations in the booking are on hold
    var listOfIds = reservations.getReservationByIdList().stream()
        .filter(Objects::nonNull)
        .filter(ReservationByIdResponse::isOnHold)
        .toList();
    if (!listOfIds.isEmpty() && listOfIds.size() == reservationsIds.size()) {
      cancelOnHoldReservationRequest.setReservationIds(reservationsIds);
      return processCancel(cancelOnHoldReservationRequest, reservationsIds, null, false, null);
    }
    return null;
  }

  private CancelReservationResponse processCancel(
      CancelReservationRequest cancelReservationRequest, List<String> reservationsIds,
      PaymentOption paymentOption, boolean sendNotification,
      List<DepositFoliosResponse> prepaidDeposits) {
    cancelReservationRequest.setReservationIds(reservationsIds);
    cancelReservationRequest.setPaymentOption(paymentOption);

    var cancelReservationResponse =
        hotelReservationOhipOutPort.cancelReservation(
            cancelReservationRequest, prepaidDeposits);
    var depositsResponse = (cancelReservationResponse.getRefundedDeposits() == null
        || cancelReservationResponse.getRefundedDeposits().containsValue(null))
        ? null
        : cancelReservationResponse.getRefundedDeposits().values().stream()
        .map(DepositsResponse::getDeposits)
        .flatMap(List::stream)
        .toList();

    basketOutPort.cancelBasket(cancelReservationRequest.getBasketReference(), false,
        sendNotification, depositsResponse);
    return cancelReservationResponse;

  }

  private BigDecimal getAmountToRefund(List<DepositFoliosResponse> prepaidDeposits) {
    return prepaidDeposits.stream()
        .map(DepositFoliosResponse::getDepositFolios)
        .flatMap(Collection::stream)
        .map(DepositFolio::getCharges)
        .flatMap(Collection::stream)
        .map(DepositFolioCharge::getCurrencyAmount)
        .map(CurrencyAmount::getAmount)
        .reduce(BigDecimal.ZERO, BigDecimal::add);
  }

  private void processRefund(BasketResponse basket,
                             ReservationByBasketRefResponse reservations, BigDecimal amountToRefund,
                             Map<String, DepositsResponse> refundedDeposits) {
    if (BigDecimal.ZERO.equals(reservations.getAmountPaid())) {
      return;
    }

    if (BigDecimal.ZERO.compareTo(reservations.getAmountPaid()) < 0) {
      if (reservations.getAmountPaid().compareTo(amountToRefund) != 0) {
        log.info("The amountToRefund {} differs from the amountPaid {} for bookingReference {} !",
            amountToRefund, reservations.getAmountPaid(), basket.getBookingReference());
      }
      try {
        var refundResponse = basketOutPort.triggerRefundRequest(reservations, basket.getReference());
        if (Boolean.FALSE.equals(refundResponse.getRefunded())) {
          var exception = new CancelReservationException(
              ErrorCode.DIGITAL_CANCEL_REFUND_EXCEPTION,
              "Refund could not be processed.");
          ExceptionLogger.log(log, exception);
          throw exception;
        }
      } catch (Exception e) {

        var emailRequest = emailRequestBuilder(basket.getReference(), reservations, refundedDeposits, true,
            basket.getPaymentID());
        basketOutPort.triggerEmailConfirmation(emailRequest);
        throw e;
      }
    } else {
      var ex = new GenericReservationException(ErrorCode.DIGITAL_REFUND_REQUEST_EXCEPTION,
          "The refund request can't be triggered because the amountPaid is lower than 0 ");
      ExceptionLogger.log(log, ex);
      throw ex;
    }
  }

  private void updateChargesAfterRefund(ReservationByBasketRefResponse reservations,
                                        List<DepositFoliosResponse> prepaidDeposits,
                                        String bookingChannel,
                                        PaymentOption paymentOption) {
    var negativeAmount = reservations.getReservationByIdList().stream()
        .map(reservation -> reservation.getRateInfo().getSummary().getDeposit())
        .reduce(BigDecimal.ZERO, BigDecimal::add);

    if (BigDecimal.ZERO.equals(negativeAmount)
        && BookingChannel.DISTR_BOOKING_CHANNEL.equalsIgnoreCase(bookingChannel)) {
      negativeAmount = reservations.getReservationByIdList().stream()
          .map(reservation -> reservation.getDepositPolicies().stream()
              .map(depositPolicy -> depositPolicy.getAmountPaid().getAmount())
              .reduce(BigDecimal.ZERO, BigDecimal::add))
          .reduce(BigDecimal.ZERO, BigDecimal::add).negate();
    }

    if (BigDecimal.ZERO.equals(reservations.getAmountPaid()) || PAY_ON_ARRIVAL.equals(
        paymentOption)) {
      return;
    }

    if (BigDecimal.ZERO.compareTo(reservations.getAmountPaid()) < 0
        && reservations.getAmountPaid().negate().compareTo(negativeAmount) == 0) {
      prepaidDeposits.stream()
          .map(DepositFoliosResponse::getDepositFolios)
          .flatMap(Collection::stream)
          .map(this::mapDepositFolio)
          .forEach(depositFolio -> {
            DepositFoliosResponse depositFoliosResponse = DepositFoliosResponse.builder()
                .depositFolios(List.of(depositFolio))
                .build();
            basketOutPort.saveCharges(depositFoliosResponse);
          });
    } else {
      var ex = new GenericReservationException(ErrorCode.DIGITAL_AMOUNT_REFUND_EXCEPTION,
          "Charges can't be updated because amountToRefund differs from amountPaid!");
      ExceptionLogger.log(log, ex);
      throw ex;
    }
  }

  private DepositFolio mapDepositFolio(DepositFolio depositFolio) {
    var pairedCharges = findPairedCharges(depositFolio.getCharges());
    var unpairedCharges = depositFolio.getCharges().stream()
        .filter(charge -> !pairedCharges.contains(charge))
        .toList();

    unpairedCharges.forEach(charge ->
        charge.getCurrencyAmount().setAmount(charge.getCurrencyAmount().getAmount().negate()));
    return DepositFolio.builder()
        .reservationId(depositFolio.getReservationId())
        .charges(unpairedCharges)
        .build();
  }

  private List<DepositFolioCharge> findPairedCharges(List<DepositFolioCharge> charges) {
    var pairedCharges = new ArrayList<DepositFolioCharge>();
    var chargeAmountMap = new HashMap<String, List<DepositFolioCharge>>();

    for (DepositFolioCharge charge : charges) {
      BigDecimal absAmount = charge.getCurrencyAmount().getAmount().abs();
      String key = absAmount + DELIMITER + charge.getReference();
      chargeAmountMap.computeIfAbsent(key, k -> new ArrayList<>()).add(charge);
    }

    for (var chargeGroup : chargeAmountMap.values()) {
      var positiveCharges = chargeGroup.stream()
          .filter(charge -> charge.getCurrencyAmount().getAmount().signum() > 0)
          .toList();

      var negativeCharges = chargeGroup.stream()
          .filter(charge -> charge.getCurrencyAmount().getAmount().signum() < 0)
          .toList();

      int minSize = Math.min(positiveCharges.size(), negativeCharges.size());
      for (int i = 0; i < minSize; i++) {
        pairedCharges.add(positiveCharges.get(i));
        pairedCharges.add(negativeCharges.get(i));
      }
    }

    return pairedCharges;
  }


  @Override
  public CancelReservationResponse rollbackReservation(
      CancelReservationRequest cancelReservationRequest) {
    log.debug("Entered rollbackReservation with basketReference={}",
        cancelReservationRequest.getBasketReference());
    return hotelReservationOhipOutPort.cancelReservation(cancelReservationRequest, null);
  }

  private OhipReservationResponse toOhipReservation(
      ReservationByIdDetailsResponse reservationsDetailsResponse) {
    OhipReservationResponse ohipReservationResponse = new OhipReservationResponse();
    final List<OhipReservationCreationResponse> reservations = new LinkedList<>();
    ReservationDetails reservationInfo =
        reservationsDetailsResponse.getReservationIdDetailsResponse()
            .getReservations().getReservation().get(0);
    ohipReservationResponse.setHotelId(reservationInfo.getHotelId());
    ohipReservationResponse.setCurrencyCode(reservationsDetailsResponse.getCurrencyCode());
    ohipReservationResponse.setTotalCost(reservationsDetailsResponse.getTotalCost());


    reservationsDetailsResponse.getReservationIdDetailsResponse()
        .getReservations().getReservation().forEach(resInfo -> {
          OhipReservationCreationResponse ohipReservationCreationResponse =
              OhipReservationCreationResponse.builder()
                  .reservationId(resInfo.getReservationIdList().get(0).getId())
                  .createDateTime(DateTimeFormatter.ISO_LOCAL_DATE.format(LocalDate.now()))
                  .roomStay(resInfo.getRoomStay())
                  .build();

          reservations.add(ohipReservationCreationResponse);
        });
    ohipReservationResponse.setReservations(reservations);

    return ohipReservationResponse;
  }

  @Override
  public void updateDiscount(UpdateDiscountRequest updateDiscountRequest) {
    hotelReservationOhipOutPort.updateDiscount(updateDiscountRequest);
  }

  @Override
  public void updateCompanyQuestionAndAnswerDetails(
      CompanyQuestionAndAnswerDetailsRequest companyQuestionAndAnswerDetailsRequest) {
    hotelReservationOhipOutPort.updateCompanyQuestionAndAnswerDetails(
        companyQuestionAndAnswerDetailsRequest);
  }

  @Override
  public void updateBusinessItems(BusinessItemsRequest businessItemsRequest) {
    hotelReservationOhipOutPort.updateBusinessItems(businessItemsRequest);
  }

  private void updateBusinessItems(BasketResponse basket, String language, BusinessItems businessItems,
                                   ReservationByBasketRefResponse reservationByBasketRefResponse, String cardType) {
    var completableFutures =
        new ArrayList<CompletableFuture<Void>>(reservationByBasketRefResponse.getReservationByIdList().size());

    if (unleashWrapper.isEnabled(unleashWrapper.featureFlag().getSaveAllowancesInBasket())) {
      completableFutures.add(CompletableFuture.runAsync(concurrentTracer.wrap(() -> {
        String eTag = basketOutPort.updateAllowances(basket.getReference(),
            ReservationUtils.buildBasketBookingAllowances(businessItems.getBusinessAllowances()),
            basket.getETag());
        basket.setETag(eTag);
      })));
    }

    final var businessAllowanceRules = rulesOutPort.getBusinessAllowanceRules()
        .getBusinessAllowances();
    final var businessNotesResponse = contentOutPort.getBusinessNotes(language);

    completableFutures.addAll(reservationByBasketRefResponse.getReservationByIdList().stream()
        .map(reservation -> CompletableFuture.runAsync(concurrentTracer.wrap(() -> {
          final var packageCodes = reservation.getReservationPackageList().stream()
              .map(ReservationPackagesDetailsResponse::getPackageCode)
              .toList();
          final var finalBusinessItems =
              BusinessAllowancesUtils.getBusinessItems(businessItems, businessAllowanceRules,
                  businessNotesResponse,
                  packageCodes, cardType);
          var businessItemsRequest = BusinessItemsRequest.builder()
              .businessItems(finalBusinessItems)
              .reservationIds(List.of(reservation.getReservationId()))
              .hotelId(basket.getHotelId())
              .build();
          hotelReservationOhipOutPort.updateBusinessItems(businessItemsRequest);
        }))).toList());

    completableFutures.forEach(CompletableFuture::join);
  }
  
  private void updateBusinessItems(BasketResponse temporaryBasket, BasketResponse originalBasket,
                                   List<String> originalRsvIds, String language, Map<String, String> linkBetweenRsvIds,
                                   Set<String> onHoldOriginalRsvIds,
                                   HotelPaymentInformation hotelPaymentInformation,
                                   BookingAllowancesResponse bookingAllowances) {
    var completableFutures = new ArrayList<CompletableFuture<Void>>(originalRsvIds.size());
    
    if (unleashWrapper.isEnabled(unleashWrapper.featureFlag().getSaveAllowancesInBasket())) {
      completableFutures.add(CompletableFuture.runAsync(concurrentTracer.wrap(() -> {
        var eTag = basketOutPort.updateAllowances(originalBasket.getReference(),
            UpdateAllowancesRequest
                .builder()
                .bookingAllowances(bookingAllowances.getBookingAllowances())
                .build(),
            originalBasket.getETag());
        originalBasket.setETag(eTag);
      })));
    }
    
    var businessAllowanceRules = rulesOutPort.getBusinessAllowanceRules().getBusinessAllowances();
    var businessNotesResponse = contentOutPort.getBusinessNotes(language);
    var temporaryReservations = hotelReservationOhipOutPort
        .getReservationsByIds(temporaryBasket.getHotelId(),
            getReservationIds(temporaryBasket), false, false,
            false);
    var cardType = getCardType(temporaryReservations, hotelPaymentInformation);
    
    var initialBusinessItems = BusinessAllowancesUtils
        .filterPackageAllowancesAndGenerateBusinessItems(
            bookingAllowances.getBookingAllowances(), businessAllowanceRules);
    
    if (ACCOUNT_COMPANY.equals(originalBasket.getPaymentOption())) {
      ofNullable(temporaryReservations.getCustomReferenceNumber())
          .ifPresent(initialBusinessItems::setCustomReferenceNumber);
    }
    
    completableFutures.addAll(originalRsvIds
        .stream()
        .map(originalRsvId -> CompletableFuture.runAsync(concurrentTracer.wrap(() -> {
          var temporaryRsvId = linkBetweenRsvIds.get(originalRsvId);
          var temporaryRsvPackageCodes = temporaryReservations.getReservationByIdList()
              .stream()
              .filter(temporaryRsv -> nonNull(temporaryRsv.getReservationId()))
              .filter(temporaryRsv -> temporaryRsv.getReservationId().equals(temporaryRsvId))
              .flatMap(temporaryRsv -> temporaryRsv.getReservationPackageList().stream())
              .map(ReservationPackagesDetailsResponse::getPackageCode)
              .toList();
          var finalBusinessItems = BusinessAllowancesUtils.getBusinessItems(
              initialBusinessItems,
              businessAllowanceRules, businessNotesResponse, temporaryRsvPackageCodes, cardType);
          var businessItemsRequest = BusinessItemsRequest
              .builder()
              .businessItems(finalBusinessItems)
              .reservationIds(List.of(originalRsvId))
              .hotelId(temporaryBasket.getHotelId())
              .companyId(ACCOUNT_COMPANY.equals(originalBasket.getPaymentOption())
                  ? temporaryReservations.getCompanyId() : null)
              .build();
          hotelReservationOhipOutPort.updateBusinessItems(businessItemsRequest);
        })))
        .toList());
    completableFutures.forEach(CompletableFuture::join);
    
    if (CollectionUtils.isNotEmpty(onHoldOriginalRsvIds)) {
      // Move payment card from window 1 to window 2 for new rooms
      hotelReservationOhipOutPort.movePaymentDetails(temporaryBasket.getHotelId(), onHoldOriginalRsvIds);
      if (unleashWrapper.isEnabled(unleashWrapper.featureFlag().getSetCnpBookingAlerts())) {
        setCnpReservationAlerts(temporaryBasket.getHotelId(), onHoldOriginalRsvIds);
      }
    }
  }

  @Override
  public void updateReservationSpecialRequests(SpecialRequests specialRequestsEntity) {
    hotelReservationOhipOutPort.updateSpecialRequests(specialRequestsEntity);
  }

  @Override
  public UpdateReasonForStayResponse updateReasonForStay(
      UpdateReasonForStayRequest updateReasonForStayRequest) {
    log.debug("Entered updateReasonForStay with basketReference={}, hotelId={}, reasonForStay={}",
        updateReasonForStayRequest.getBasketReference(), updateReasonForStayRequest.getHotelId(),
        updateReasonForStayRequest.getReasonForStay());

    var basket =
        basketOutPort.getBasketById(updateReasonForStayRequest.getBasketReference());

    if (StatusEnum.PAY_PENDING.name().equals(basket.getStatus())) {
      log.info("Could not update reason for stay for basketReference={} when status is PAY_PENDING",
          basket.getReference());
    } else {
      var reservationsIds = basket.getItems().stream().map(BasketItemResponse::getSourceId)
          .toList();
      updateReasonForStayRequest.setReservationIds(reservationsIds);

      String country = StringUtils.isBlank(updateReasonForStayRequest.getCountry())
          ? COUNTRY_GB : updateReasonForStayRequest.getCountry().toLowerCase();

      String language = StringUtils.isBlank(updateReasonForStayRequest.getLanguage())
          ? LANGUAGE_EN : updateReasonForStayRequest.getLanguage().toLowerCase();

      String arrivalDate = StringUtils.isBlank(updateReasonForStayRequest.getArrivalDate())
          ? getArrivalDate(updateReasonForStayRequest.getHotelId(), getReservationIds(basket))
          : updateReasonForStayRequest.getArrivalDate();

      //Update the reason for stay to NTLEI or NTBUS if we want to remove the city tax from OPERA
      updateReasonForStayRequest.setReasonForStay(getReasonForStay(updateReasonForStayRequest.getHotelId(),
          country, language, updateReasonForStayRequest.getReasonForStay(), arrivalDate));

      hotelReservationOhipOutPort.updateReasonForStay(updateReasonForStayRequest);
    }
    return new UpdateReasonForStayResponse(updateReasonForStayRequest.getBasketReference());
  }

  @Override
  public UpdateReservationOverrideReasonsResponse updateReservationOverrideReasons(
      UpdateReservationOverrideReasonsRequest updateReservationOverrideReasonsRequest) {
    log.debug(
        "Entered updateReservationOverrideReasons with basketReference={}, hotelId={}, reasonCode={}, "
            + "reasonName={}, callerName={}, managerName={}",
        updateReservationOverrideReasonsRequest.getBasketReference(),
        updateReservationOverrideReasonsRequest.getHotelId(),
        updateReservationOverrideReasonsRequest.getReasonCode(),
        updateReservationOverrideReasonsRequest.getReasonName(),
        updateReservationOverrideReasonsRequest.getCallerName(),
        updateReservationOverrideReasonsRequest.getManagerName());

    var basket = basketOutPort.getBasketById(
        updateReservationOverrideReasonsRequest.getBasketReference());

    var reservationsIds = extractReservationIds(basket);

    updateReservationOverrideReasonsRequest.setReservationIds(reservationsIds);

    hotelReservationOhipOutPort.updateReservationOverrideReasons(
        updateReservationOverrideReasonsRequest);

    return new UpdateReservationOverrideReasonsResponse(
        updateReservationOverrideReasonsRequest.getBasketReference());
  }

  @Override
  public DepositsResponse getDepositsForReservationId(String hotelId, String reservationId) {
    return hotelReservationOhipOutPort.getDepositsForReservationId(hotelId, reservationId);
  }

  @Override
  public CancellationPoliciesResponse getCancellationPolicies(
      String basketReference, String hotelId, String rateCode, String arrivalDate) {
    Set<String> reservationsIds = null;
    if (StringUtils.isBlank(basketReference) && StringUtils.isBlank(rateCode)
        && StringUtils.isBlank(arrivalDate)) {
      var ex = new GenericReservationException(ErrorCode.DIGITAL_CANCEL_POLICIES_EXCEPTION,
          "Cannot fetch cancellation policies from Opera");
      ExceptionLogger.log(log, ex);
      throw ex;
    }
    if (!basketReference.isEmpty()) {
      var basket = basketOutPort.getBasketById(basketReference);
      reservationsIds = basket.getItems().stream().map(BasketItemResponse::getSourceId)
          .collect(Collectors.toSet());
    }
    return hotelReservationOhipOutPort.getCancellationPolicies(reservationsIds, hotelId, rateCode,
        arrivalDate);
  }

  @Override
  public MarketingPreferencesResponse getMarketingPreferences(String hotelId,
                                                              String reservationId) {
    return hotelReservationOhipOutPort.getMarketingPreferences(hotelId, reservationId);
  }

  private String findBookingType(ReservationRequest createReservationRequest) {
    String bookingType = createReservationRequest.getBookingChannel().getChannel();

    if ((!authenticatedUserService.isUserAuthenticated()
        && createReservationRequest.getBookingChannel().isPi())) {
      bookingType = BOOKING_TYPE_ANON;
    }

    if (createReservationRequest.getBookingChannel().isCcui()) {
      if (negotiatedRateUsed(createReservationRequest.getReservations())) {
        bookingType = BOOKING_TYPE_EMPTY;
      } else {
        bookingType = BOOKING_TYPE_ANON;
      }
    }
    return bookingType;
  }

  @Override
  public UpdateCnpReservationResponse updateCnpReservation(String basketReference,
                                                           UpdateCnpReservationRequest updateRequest) {

    var basket = basketOutPort.getBasketById(basketReference);
    var reservationIds = getReservationIds(basket);
    var reservationsByIds = hotelReservationOhipOutPort
        .getReservationsByIds(basket.getHotelId(), reservationIds, false,
            false, false);

    var language = StringUtils.isBlank(updateRequest.getLanguage())
        ? DEFAULT_LANGUAGE_EN : updateRequest.getLanguage().toLowerCase();
    var country = StringUtils.isBlank(updateRequest.getCountryCode())
        ? getCountryCodeFromLanguage(language)
        : updateRequest.getCountryCode().toLowerCase();
    var hotelPaymentInformation = contentOutPort.getHotelPaymentInformation(basket.getHotelId(),
        language, country);

    final var cardType = getCardType(reservationsByIds, hotelPaymentInformation);

    final var businessItems = BusinessAllowancesUtils.buildBusinessItemsFromBusinessAccountCnp(
        updateRequest.getBusinessAccount(), distributionProperties.getMeals());

    //Cleans up existing business notes as well
    deleteRoutingInstructions(basket.getHotelId(), Set.copyOf(reservationIds));

    updateBusinessItems(basket, language, businessItems, reservationsByIds, cardType);

    hotelReservationOhipOutPort.movePaymentDetails(basket.getHotelId(),
        new HashSet<>(reservationIds));

    updateReservationBooker(reservationIds, basket.getHotelId(), updateRequest.getBooker());

    return UpdateCnpReservationResponse
        .builder()
        .basketReference(basketReference)
        .build();
  }

  private String getCardType(ReservationByBasketRefResponse reservations,
      HotelPaymentInformation hotelPaymentInformation) {

    var pibaCardTypeCode = hotelPaymentInformation
        .getAcceptedCreditCards()
        .stream()
        .filter(c -> isPiba(c.getCodeOpera()))
        .findFirst();

    return (pibaCardTypeCode.isPresent()
        && reservations.getReservationByIdList().get(0).getPaymentCard() != null
        && pibaCardTypeCode.get().getCodeOperaCardType() != null && pibaCardTypeCode.get()
        .getCodeOperaCardType()
        .equalsIgnoreCase(
            reservations.getReservationByIdList().get(0).getPaymentCard().getCardType()))
        ? PIBA_CARD_TYPE_AEM_ID : CREDIT_CARD_TYPE_AEM_ID;
  }

  private String getCountryCodeFromLanguage(String language) {
    return "de".equalsIgnoreCase(language) ? "de" : "gb";
  }

  public void updateReservationBooker(List<String> reservationIds, String hotelId,
                                      BookerDetailsCnp booker) {
    if (booker != null) {
      var bookerDetailsCnpRequest = BookerDetailsCnpRequest.builder()
          .reservationIds(reservationIds)
          .hotelId(hotelId)
          .booker(booker)
          .build();

      hotelReservationOhipOutPort.updateReservationBooker(bookerDetailsCnpRequest);
    }
  }

  private boolean negotiatedRateUsed(List<Reservation> reservations) {
    if (reservations != null && reservations.size() > 0) {
      RoomRate roomRate = reservations.get(0).getRoomRates();

      if (roomRate.getRateDisplaySet() != null) {
        return businessBookerConfigProperties
            .getNegociatedRatePlanSet()
            .stream()
            .anyMatch(rateDisplaySet -> rateDisplaySet.equals(roomRate.getRateDisplaySet()));
      }
    }
    return false;
  }

  private List<String> getReservationIds(BasketResponse basket) {
    List<BasketItemResponse> basketItems = basket.getItems();

    return Optional.ofNullable(basketItems).map(
        item -> item.stream().map(BasketItemResponse::getSourceId).collect(Collectors.toSet())
            .stream()
            .toList()).orElse(emptyList());
  }

  @Override
  public void deleteRoutingInstructions(String hotelId, Set<String> reservationIds) {
    hotelReservationOhipOutPort.deleteRoutingInstructions(hotelId, reservationIds);
  }

  @Override
  public void updateEmailReservation(String basketReference,
                                     UpdateEmailReservationRequest updateRequest) {

    var basket = basketOutPort.getBasketById(basketReference);
    var reservationIds = getReservationIds(basket);

    var updateBookerEmailRequest = UpdateBookerEmailRequest.builder()
        .reservationIds(reservationIds)
        .hotelId(basket.getHotelId())
        .emailAddress(updateRequest.getEmail())
        .build();

    hotelReservationOhipOutPort.updateBookerEmail(updateBookerEmailRequest);
  }

  @Override
  public TempBookingRefResponse addNewRoomToExistingBasket(
      String tempBookingRef,
      ReservationRequest addNewRoomRequest,
      ReservationByBasketRefResponse reservationByBasketRefResponse) {

    authenticatedUserService.getCurrentUserAccount().ifPresent(userAccount -> {
      log.info("Authenticated access level : {}", userAccount.getAccessLevel());
      log.info("Empl ID = {}, and compId = {}", userAccount.getEmployeeId(), userAccount.getCompanyId());
      if ("SELF".equals(userAccount.getAccessLevel())) {
        var ex = new GenericBadRequestException(ErrorCode.DIGITAL_ADD_NEW_ROOM_EXCEPTION,
            "Self booker can't add new room");
        ExceptionLogger.log(log, ex);
        throw ex;
      }
      if (BB_BOOKING_CHANNEL.equals(addNewRoomRequest.getBookingChannel().getChannel())) {
        log.info("Authenticated BB user");
        addNewRoomRequest.getReservations()
            .forEach(r -> {
              r.setUserAccountId(userAccount.getEmployeeId());
              r.setCompanyAccountId(userAccount.getCompanyId());
            });
      }
    });

    log.info("Add new room to an existing basket = {}", addNewRoomRequest);

    var basket = basketOutPort.getBasketById(tempBookingRef);

    if (Boolean.FALSE.equals(addNewRoomRequest.getBookingChannel().isDistr())) {
      validateAuthenticationAndToken(addNewRoomRequest.getToken(), basket.getOriginalBasketId());
      checkBasketExpired(basket);
    }

    if (!validateMaxRoomsRule(addNewRoomRequest, basket)) {
      var ex = new GenericBadRequestException(ErrorCode.DIGITAL_BASKET_ADD_ROOM_EXCEPTION,
          "Too many rooms booked, please contact call center");
      ExceptionLogger.log(log, ex);
      throw ex;
    }
    var roomType = addNewRoomRequest.getReservations().get(0).getRoomRates().getPmsRoomType();
    var adults = addNewRoomRequest.getReservations().get(0).getAdultsNumber();
    var children = addNewRoomRequest.getReservations().get(0).getChildrenNumber();
    if (!validateRoomOccupancyRule(roomType, adults, children,
        addNewRoomRequest.getBookingChannel().getChannel(), basket.getHotelId())) {
      var ex = new GenericBadRequestException(ErrorCode.DIGITAL_BASKET_ADD_WRONG_ROOM_EXCEPTION,
          "Wrong room type for number of adults and children");
      ExceptionLogger.log(log, ex);
      throw ex;
    }

    var resByBasket =
        Objects.nonNull(reservationByBasketRefResponse) ? reservationByBasketRefResponse :
            getAllReservationsJustByBasketReference(tempBookingRef, false, false);

    var reservationToCreate = addNewRoomRequest.getReservations().get(0);
    extractRoomDetailsForReservation(addNewRoomRequest, resByBasket, reservationToCreate, basket);
    if (null != addNewRoomRequest.getIsOta() && addNewRoomRequest.getIsOta()) {
      checkAvailabilityForOtaAndCreateReservationWithEditRatePrice(
          addNewRoomRequest,
          reservationToCreate);
    }
    log.info("reservationToCreate: {}", reservationToCreate);
    
    if (BB_BOOKING_CHANNEL.equals(addNewRoomRequest.getBookingChannel().getChannel())) {
      reservationToCreate.setCompanyAccountId(addNewRoomRequest.getReservations().get(0).getCompanyAccountId());
      reservationToCreate.setUserAccountId(addNewRoomRequest.getReservations().get(0).getUserAccountId());
    }

    basket.setETag(basket.getETag().replace("\"", ""));

    int noOfRooms = addNewRoomRequest.getReservations().size();
    if (unleashWrapper.isEnabled(unleashWrapper.featureFlag().getApplyOccupancySupplement()) && noOfRooms > 1) {
      addNewRoomRequest.setGetReservationsByIds(true);
    }

    var internalReservations = hotelReservationOhipOutPort
        .createReservation(basket.getHotelId(),
            ReservationRequest.builder()
                .bookingChannel(addNewRoomRequest.getBookingChannel())
                .reservations(List.of(reservationToCreate))
                .build(),
            tempBookingRef);

    resByBasket.getReservationByIdList().stream()
            .map(resp -> resp.getAdditionalGuestInfo() == null
                    ? null : resp.getAdditionalGuestInfo().getPurposeOfStay())
            .filter(purpose -> purpose != null && !purpose.isBlank())
            .findFirst()
            .ifPresentOrElse(
                    purpose -> hotelReservationOhipOutPort.updateReasonForStay(
            UpdateReasonForStayRequest.builder()
                    .reasonForStay(purpose.trim())
                    .reservationIds(List.of(internalReservations.getReservations().get(0).getReservationId()))
                    .hotelId(resByBasket.getHotelId())
                    .basketReference(tempBookingRef)
                    .build()
                    ),
                    () -> log.debug("purpose of stay not found for hotelId={}", resByBasket.getHotelId()));

    Integer noOfAdults = null;
    boolean isOta = Boolean.TRUE.equals(addNewRoomRequest.getIsOta());
    if (unleashWrapper.isEnabled(unleashWrapper.featureFlag().getApplyOccupancySupplement()) && noOfRooms == 1) {
      noOfAdults = addNewRoomRequest.getReservations().get(0).getAdultsNumber();
    }

    basket = basketOutPort
        .addReservationsToBasket(tempBookingRef, basket.getETag(), internalReservations, false,
            isOccupancySupplementApplicable(resByBasket.getHotelId()), noOfAdults, isOta);

    if (Boolean.TRUE.equals(validateNoFlagAndNotDistributionCall(
        unleashWrapper.isEnabled(unleashWrapper.featureFlag().getApplyOccupancySupplement()),
        Boolean.TRUE.equals(addNewRoomRequest.getBookingChannel().isDistr())))) {
      copyBookingAllowances(basket, true, addNewRoomRequest.getBookingChannel().isDistr());
    }

    return TempBookingRefResponse.builder()
        .tempBookingRef(tempBookingRef)
        .tempReservationId(internalReservations.getReservations().get(0).getReservationId())
        .build();
  }

  private static void checkBasketExpired(BasketResponse basket) {
    if (!OPEN.getValue().equals(basket.getStatus())) {
      var ex = new GenericBadRequestException(ErrorCode.DIGITAL_BASKET_ADD_RELOAD_EXCEPTION,
          "Basket has expired, please reload session");
      ExceptionLogger.log(log, ex);
      throw ex;
    }
  }

  private static void extractRoomDetailsForReservation(
      ReservationRequest addNewRoomRequest,
      ReservationByBasketRefResponse resByBasket,
      Reservation reservationToCreate,
      BasketResponse basket) {
    var roomStayFromExistingReservation =
        resByBasket.getReservationByIdList().get(0).getRoomStay();
    var startDate = roomStayFromExistingReservation.getArrivalDate();
    var endDate = roomStayFromExistingReservation.getDepartureDate();
    var hotelId = resByBasket.getHotelId();

    Optional<Reservation> newRoomReservation = addNewRoomRequest.getReservations().stream().findFirst();
    if (newRoomReservation.isPresent() && addNewRoomRequest.getBookingChannel().isDistr()) {
      startDate = newRoomReservation.get().getRoomRates().getStartDate();
      endDate = newRoomReservation.get().getRoomRates().getEndDate();
    }

    reservationToCreate.setHotelId(hotelId);
    reservationToCreate.setArrival(startDate);
    reservationToCreate.setDeparture(endDate);
    reservationToCreate.getRoomRates()
        .setRatePlanCode(roomStayFromExistingReservation.getRatePlanCode());
    reservationToCreate.getRoomRates().setStartDate(startDate);
    reservationToCreate.getRoomRates().setEndDate(endDate);
    reservationToCreate.setLeadGuest(addNewRoomRequest.getReservations().get(0).getLeadGuest());
    reservationToCreate.setExternalReferenceId(basket.getBookingReference());
    reservationToCreate.setOperaCompanyId(resByBasket.getCompanyId());
    reservationToCreate.setDistributionIATANumber(addNewRoomRequest.getDistributionIATANumber());

    var preference = addNewRoomRequest.getReservations().get(0).getRoomRates()
        .getSpecialRequests();
    if (Objects.nonNull(preference) && !preference.isEmpty()) {
      reservationToCreate.getRoomRates().setSpecialRequests(preference);
    }
  }

  private void checkAvailabilityForOtaAndCreateReservationWithEditRatePrice(
      ReservationRequest addNewRoomRequest,
      Reservation reservationToCreate) {

    if (CollectionUtils.isNotEmpty(addNewRoomRequest.getReservations())) {
      try {
        Optional<Reservation> reservation = addNewRoomRequest.getReservations().stream().findFirst();

        if (reservation.isPresent()) {
          String hotelId = reservation.get().getHotelId();
          String pmsRoomType = reservation.get().getRoomRates().getPmsRoomType();
          String ratePlanCode = reservation.get().getRoomRates().getRatePlanCode();

          HotelAvailabilityByIdsV2Request availabilityRequest = HotelAvailabilityByIdsV2Request.builder()
              .hotelIds(new ArrayList<>(List.of(hotelId)))
              .rooms(
                  List.of(Room.builder().tag(pmsRoomType)
                      .numberOfRooms(addNewRoomRequest.getReservations().size())
                      .adults(reservation.get().getAdultsNumber())
                      .children(reservation.get().getChildrenNumber())
                      .build()))
              .arrivalDate(LocalDate.parse(reservation.get().getArrival()))
              .departureDate(
                  LocalDate.parse(reservation.get().getDeparture()))
              .bookingChannel(uk.co.whitbread.reservation.domain.model.availability.in.BookingChannel.builder()
                  .channel("DISTR")
                  .subchannel("AGENCY")
                  .language("EN")
                  .build())
              .isOTA(true)
              .rates(Rate.builder()
                  .ratePlanCodes(List.of(ratePlanCode))
                  .corporateRates(CorporateRate.builder()
                      .corporateId(null).build()).build())
              .vatNotRequired(false)
              .build();

          var hotelAvailabilityByIdsV2 = hotelAvailabilityOutPort.getHotelAvailabilitiesByIdsV2(availabilityRequest);

          List<RatePrice> avRatePrices = new ArrayList<>();

          hotelAvailabilityByIdsV2.getHotelAvailability().stream()
              .flatMap(hotelAvailabilityResultV2 ->
                  Optional.ofNullable(hotelAvailabilityResultV2.getRoomStays())
                      .orElseGet(List::of).stream()
              )
              .flatMap(roomStay ->
                  Optional.ofNullable(roomStay.getRoomTypes())
                      .orElseGet(List::of).stream()
              )
              .flatMap(roomTypeV2 ->
                  Optional.ofNullable(roomTypeV2.getRoomRates())
                      .orElseGet(List::of).stream()
              )
              .flatMap(roomRates ->
                  Optional.ofNullable(roomRates.getRoomRateInfo().getPriceInfo())
                      .orElseGet(List::of).stream()
              )
              .forEach(avRoomRate ->
                  avRatePrices.add(createRatePrices(avRoomRate.getAmountBeforeTax(),
                          avRoomRate.getStayDate(),
                          avRoomRate.getStayDate().plusDays(1)
                      )
                  )
          );

          var roomRate = RoomRate.builder()
              .ratePlanCode(ratePlanCode)
              .pmsRoomType(reservation.get().getRoomRates().getPmsRoomType())
              .operaRoomType(reservation.get().getRoomRates().getOperaRoomType())
              .startDate(reservation.get().getArrival())
              .endDate(reservation.get().getDeparture())
              .specialRequests(reservation.get().getRoomRates().getSpecialRequests())
              .cellCode(reservation.get().getRoomRates().getCellCode())
              .ratePrices(avRatePrices)
              .fixedRate(true)
              .build();

          reservationToCreate.setRoomRates(roomRate);
        }
      } catch (Exception e) {
        var ex = new AmendStayDateException(ErrorCode.DIGITAL_AMEND_DATE_EXCEPTION,
            "There is no availability for the requested period of time", e);
        ExceptionLogger.log(log, e);
        throw ex;
      }
    }
  }

  private static RatePrice createRatePrices(
      BigDecimal amountBeforeTax,
      LocalDate arrivalDate,
      LocalDate departureDate) {
    return RatePrice.builder()
        .priceStartDate(arrivalDate)
        .priceEndDate(departureDate)
        .amount(amountBeforeTax).build();

  }

  @Override
  public AmendStayDatesResponse amendStayDates(AmendStayDatesRequest amendStayDatesRequest,
      Boolean isNonRefundable) {

    Map<String, UpdateReservationsRequest> amendStayDateUpdateRequest = getAmendStayDateUpdateRequest(
            amendStayDatesRequest, isNonRefundable);
    String reference;
    var firstAmendStayDate = amendStayDateUpdateRequest.keySet().stream().findFirst();
    if (firstAmendStayDate.isPresent()) {
      reference = firstAmendStayDate.get();
    } else {
      var exception = new GenericBadRequestException(ErrorCode.DIGITAL_DATE_AMEND_EXCEPTION,
          "There was an error while trying to update the reservation.");
      ExceptionLogger.log(log, exception);
      throw exception;
    }
    UpdateReservationsRequest updateReservationsRequest = amendStayDateUpdateRequest.get(reference);
    String onHoldBasketReference = reference.equalsIgnoreCase("NULL") ? null : reference;

    // After the update on the tempo reservation/s took place successfully the
    // CLEANUP of the new basket and onHold reservations for reserving extra time slot (2 steps above)
    if (onHoldBasketReference != null) {
      reservationCleanUp.cleanupTempBasket(onHoldBasketReference);
    }

    //extend business allowances for the new staying period
    List<UpdateReservationRequest> updateReservationsRequestList = updateReservationsRequest.getReservations();

    return AmendStayDatesResponse.builder()
            .tempBasket(amendStayDatesRequest.getTempBookingRef())
            .build();
  }

  private Boolean validateNoFlagAndNotDistributionCall(Boolean flag, boolean isDistribution) {
    return !(flag && isDistribution);
  }

  private Map<String, UpdateReservationsRequest> getAmendStayDateUpdateRequest(
      AmendStayDatesRequest amendStayDatesRequest,
      Boolean isNonRefundable
  ) {
    return getAmendStayDateUpdateRequest(amendStayDatesRequest, null, isNonRefundable);
  }

  private Map<String, UpdateReservationsRequest> getAmendStayDateUpdateRequest(
          AmendStayDatesRequest amendStayDatesRequest,
          List<AmendDistributionStayDatesRequest> amendDistrStayDatesRequests,
          Boolean isNonRefundable
  ) {
    var bookingChannel = amendStayDatesRequest.getBookingChannel();

    BasketResponse basketResponse = basketOutPort.getBasketById(
        amendStayDatesRequest.getTempBookingRef());

    if (shouldValidateAuthAndToken(amendStayDatesRequest)) {
      validateAuthenticationAndToken(amendStayDatesRequest.getToken(), basketResponse.getOriginalBasketId());
    }

    validateMaxNightsRule(amendStayDatesRequest, bookingChannel.getChannel());

    var newArrivalDate = amendStayDatesRequest.getNewStartDate();
    var newDepartureDate = amendStayDatesRequest.getNewEndDate();

    ReservationByBasketRefResponse allReservations =
        getAllReservationsJustByBasketReference(amendStayDatesRequest.getTempBookingRef(), false,
            false);

    var reservationsRequest = ReservationRequest.builder()
        .bookingChannel(bookingChannel)
        .reservations(new ArrayList<>())
        .build();

    List<UpdateReservationRequest> updateReservationsRequestList = new ArrayList<>();

    //this returns the status for all reservation, doesn't need to be called for each reservation
    var isBookingNonRefundable = determineBookingNonRefundable(
        isNonRefundable, amendStayDatesRequest, basketResponse, bookingChannel);

    createReservationsRequestListToBeUpdated(
        amendStayDatesRequest,
        amendDistrStayDatesRequests,
        allReservations,
        newArrivalDate,
        newDepartureDate,
        isBookingNonRefundable,
        reservationsRequest,
        updateReservationsRequestList);

    String onHoldBasketReference = createOnHoldReservation(reservationsRequest);
    
    var reservationIdList = updateReservationsRequestList.stream()
        .map(UpdateReservationRequest::getReservationId).toList();
    
    //update routing instructions (delete routing) before updating reservation stay dates
    //change brought in after Opera version upgrade to 25.1.7.1 (CTECH-1545)
    final BusinessItemsRequest businessItemsRequest = removeBookingAllowances(amendStayDatesRequest.getTempBookingRef(),
        reservationIdList, allReservations.getHotelId(), true);

    // after the availability check on the extended period has been reserved through the
    // NEW reservations that were created just above, the NEXT step is to update the
    // existing TEMPORARY reservation with the WHOLE extended period interval with
    // overrideInventoryCheck flag(OHIP adapter level) (because we have reserved already the time slot/s)
    var updateReservationsRequest = UpdateReservationsRequest.builder()
        .bookingChannel(bookingChannel)
        .reservations(updateReservationsRequestList)
        .tempReservations(allReservations)
        .linkAmendReservations(basketResponse.getLinkAmendReservations())
        // reservationRates field is required for Distribution OTA Amend flow to ask Ohip/Opera to honour
        // the prices from the rates bellow
        .newRatesReservation(reservationsRequest.getReservations())
        .distributionIATANumber(amendStayDatesRequest.getDistributionIATANumber())
        .build();

    Map<String, UpdateReservationsRequest> onHoldRefernceAndStayDateUpdateRequestMap = new HashMap<>();
    onHoldRefernceAndStayDateUpdateRequestMap.put(
        onHoldBasketReference != null ? onHoldBasketReference : "NULL", updateReservationsRequest);

    log.debug("Created UpdateReservationsRequest={} for amendStayDatesRequest={} ",
        updateReservationsRequest, amendStayDatesRequest);

    updateReservations(amendStayDatesRequest, updateReservationsRequest);
    
    //add new routing instructions for the extended period after updating reservation stay dates
    //change brought in after Opera version upgrade to 25.1.7.1 (CTECH-1545)
    if (businessItemsRequest != null && CollectionUtils.isNotEmpty(businessItemsRequest.getReservationIds())
        && businessItemsRequest.getBusinessItems() != null) {
      updateBusinessItems(businessItemsRequest);
    }

    return onHoldRefernceAndStayDateUpdateRequestMap;
  }

  private boolean shouldValidateAuthAndToken(AmendStayDatesRequest amendStayDatesRequest) {
    return Boolean.FALSE.equals(amendStayDatesRequest.getBookingChannel().isDistr());
  }

  private Boolean determineBookingNonRefundable(
      Boolean isNonRefundable,
      AmendStayDatesRequest amendStayDatesRequest,
      BasketResponse basketResponse,
      BookingChannel bookingChannel) {
    if (isNull(isNonRefundable)) {
      final boolean isTempBookingRefRequired =
          Boolean.TRUE.equals(amendStayDatesRequest.getBookingChannel().isDistr());
      return isBookingNonRefundable(
          basketResponse.getHotelId(),
          isTempBookingRefRequired ? amendStayDatesRequest.getTempBookingRef() : basketResponse.getOriginalBasketId(),
          amendStayDatesRequest.getToken(),
          bookingChannel,
          null
      );
    }
    return isNonRefundable;
  }

  private String createOnHoldReservation(ReservationRequest reservationsRequest) {
    if (CollectionUtils.isNotEmpty(reservationsRequest.getReservations())) {
      try {
        ReservationResponse onHoldReservation = createReservation(reservationsRequest);
        return onHoldReservation.getBasketReference();
      } catch (Exception e) {
        var ex = new AmendStayDateException(ErrorCode.DIGITAL_AMEND_DATE_EXCEPTION,
            "There is no availability for the requested period of time", e);
        ExceptionLogger.log(log, e);
        throw ex;
      }
    }
    return null;
  }

  private void updateReservations(
      AmendStayDatesRequest amendStayDatesRequest,
      UpdateReservationsRequest updateReservationsRequest) {
    if (Boolean.FALSE.equals(amendStayDatesRequest.getBookingChannel().isDistr())) {
      hotelReservationOhipOutPort.updateReservations(updateReservationsRequest);
    } else {
      hotelReservationOhipOutPort.updateReservationsSingleCall(updateReservationsRequest);
    }
  }

  public BusinessItemsRequest bookingAllowancesSingleCall(List<String> reservationIds,
                                                          String originalBasketRef, String hotelId) {
    var bookingAllowances = getBookingAllowances(originalBasketRef);
    if (bookingAllowances != null && !bookingAllowances.getBookingAllowances().isEmpty()) {
      return getBookingAllowanceRequestV2(
              bookingAllowances, reservationIds, hotelId, true);

    }
    return null;
  }

  @Override
  public UpdateReservationsRequest buildRequestAmendStayDatesSingleCall(
      List<AmendDistributionStayDatesRequest> amendStayDatesRequests,
      Boolean isNonRefundable) {
    Map<String, UpdateReservationsRequest> amendStayDateUpdateRequest = getAmendStayDateUpdateRequest(
        amendStayDatesRequests.get(0).getAmendStayDatesRequest(), amendStayDatesRequests, isNonRefundable);
    var firstAmendStayDate = amendStayDateUpdateRequest.keySet().stream().findFirst();
    if (firstAmendStayDate.isPresent()) {
      return amendStayDateUpdateRequest.get(firstAmendStayDate.get());
    } else {
      var ex = new GenericBadRequestException(ErrorCode.DIGITAL_AMEND_DATE_EXCEPTION,
          "There was an error while trying to update the reservation.");
      ExceptionLogger.log(log, ex);
      throw ex;
    }
  }

  private void createReservationsRequestListToBeUpdated(
      AmendStayDatesRequest amendStayDatesRequest,
      List<AmendDistributionStayDatesRequest> amendDistributionStayDatesRequests,
      ReservationByBasketRefResponse allReservations,
      String newArrivalDate,
      String newDepartureDate,
      Boolean isBookingNonRefundable,
      ReservationRequest reservationsRequest,
      List<UpdateReservationRequest> updateReservationsRequestList) {
    for (ReservationByIdResponse basketReservation : allReservations.getReservationByIdList()) {
      var arrivalDate = basketReservation.getRoomStay().getArrivalDate();
      var departureDate = basketReservation.getRoomStay().getDepartureDate();
      var isStayIntervalShifted = this.isStayIntervalShifted(arrivalDate, departureDate,
          newArrivalDate, newDepartureDate);

      var wbRoomType = getWbRoomType(amendDistributionStayDatesRequests, basketReservation);
      var amendDistributionStayDateRequest = getAmendDistributionStayDateRequest(amendDistributionStayDatesRequests,
          basketReservation);

      if (isBookingNonRefundable && !isStayIntervalShifted) {
        var ex = new GenericBadRequestException(ErrorCode.DIGITAL_NON_REFUNDABLE_BOOKING_EXCEPTION,
            "Increasing/decreasing the no. of nights is forbidden for non-refundable bookings!");
        ExceptionLogger.log(log, ex);
        throw ex;
      }

      if (amendLogic.checkCreateOnHoldReservation(arrivalDate, departureDate, newArrivalDate,
          newDepartureDate)) {

        var amendOnHoldReservationIntervals = amendLogic.getAmendOnHoldReservationInterval(
            arrivalDate, departureDate, newArrivalDate, newDepartureDate);

        for (var amendInterval : amendOnHoldReservationIntervals) {
          var temporaryReservations =
              createOnHoldReservations(
                  amendStayDatesRequest,
                  List.of(amendInterval),
                  basketReservation,
                  allReservations.getHotelId(),
                  allReservations.getCompanyId(),
                  wbRoomType,
                  amendDistributionStayDateRequest
              );
          reservationsRequest.getReservations().addAll(temporaryReservations);
        }

      }

      var updateResReq = createUpdateReservationRequest(allReservations.getHotelId(),
          basketReservation.getReservationId(), amendStayDatesRequest.getNewStartDate(),
          amendStayDatesRequest.getNewEndDate());

      updateReservationsRequestList.add(updateResReq);
    }
  }

  private String getWbRoomType(
      List<AmendDistributionStayDatesRequest> amendDistributionStayDatesRequests,
      ReservationByIdResponse basketReservation) {
    if (Objects.nonNull(amendDistributionStayDatesRequests) && !amendDistributionStayDatesRequests.isEmpty()) {
      return amendDistributionStayDatesRequests.stream()
          .filter(amendDistributionStayDatesRequest -> amendDistributionStayDatesRequest.getExternalReference()
              .equals(basketReservation.getReservationId()))
          .filter(amendDistributionStayDatesRequest -> amendDistributionStayDatesRequest.getAdults()
              .equals(basketReservation.getRoomStay().getAdultsNumber()))
          .filter(amendDistributionStayDatesRequest -> amendDistributionStayDatesRequest.getChildren()
              .equals(basketReservation.getRoomStay().getChildrenNumber()))
          .findFirst()
          .map(AmendDistributionStayDatesRequest::getWbRoomType)
          .orElse(null);
    }
    return "";
  }

  private AmendDistributionStayDatesRequest getAmendDistributionStayDateRequest(
      List<AmendDistributionStayDatesRequest> amendDistributionStayDatesRequests,
      ReservationByIdResponse basketReservation) {
    if (Objects.nonNull(amendDistributionStayDatesRequests) && !amendDistributionStayDatesRequests.isEmpty()) {
      return amendDistributionStayDatesRequests.stream()
          .filter(amendDistributionStayDatesRequest -> amendDistributionStayDatesRequest.getExternalReference()
              .equals(basketReservation.getReservationId()))
          .findFirst()
          .orElse(null);
    }

    return null;
  }

  public boolean isStayIntervalShifted(String arrivalDate, String departureDate,
                                       String newArrivalDate, String newDepartureDate) {
    var arrivalDateLd = getLocalDateFromString(arrivalDate);
    var departureDateLd = getLocalDateFromString(departureDate);
    var newArrivalDateLd = getLocalDateFromString(newArrivalDate);
    var newDepartureDateLd = getLocalDateFromString(newDepartureDate);

    return DAYS.between(arrivalDateLd, departureDateLd) == DAYS.between(newArrivalDateLd,
        newDepartureDateLd);
  }

  public LocalDate getLocalDateFromString(String stringDate) {
    return LocalDate.parse(stringDate, DateTimeFormatter.ofPattern("yyyy-MM-dd"));
  }

  @Override
  public ReservationByBasketRefResponse confirmAmend(ConfirmAmendRequest confirmAmendRequest,
      boolean isRoomRatePlanCode) {

    BasketResponse temporaryBasket;
    BasketResponse originalBasket;
    ReservationByBasketRefResponse originalReservations;
    ReservationByBasketRefResponse updatedReservations;

    try {
      temporaryBasket = getTemporaryBasketByBasketId(confirmAmendRequest);
    } catch (BasketNotFoundException exception) {
      ExceptionLogger.log(log, exception, "Temporary basket reference has expired or is empty");
      throw exception;
    }

    try {
      originalBasket = basketOutPort.getBasketById(confirmAmendRequest.getOriginalBookingRef());
    } catch (BasketNotFoundException exception) {
      ExceptionLogger.log(log, exception, "Original basket reference not found");
      throw exception;
    }
    if (Objects.nonNull(originalBasket.getChannel())
        && originalBasket.getChannel().equals(DISTR_BOOKING_CHANNEL)
        && (originalBasket.getPaymentOption() == null)) {
      originalBasket.setPaymentOption(PAY_ON_ARRIVAL);
    }

    if (Objects.nonNull(temporaryBasket.getChannel())
        && originalBasket.getChannel().equals(DISTR_BOOKING_CHANNEL)
        && (temporaryBasket.getPaymentOption() == null)) {
      temporaryBasket.setPaymentOption(PAY_ON_ARRIVAL);
    }

    var isPN = PAY_NOW.name().equals(confirmAmendRequest.getPaymentOptionSelected());

    var initialOriginalRsvIds = getReservationIds(originalBasket);
    var temporaryRsvIds = getReservationIds(temporaryBasket);
    var linkBetweenRsvIds = new HashMap<>(temporaryBasket.getLinkAmendReservations());

    var temporaryRsvIdsToBeAdded = getReservationsToBeAdded(initialOriginalRsvIds, temporaryRsvIds,
        linkBetweenRsvIds);
    var originalRsvIdsToBeDeleted = getReservationsToBeDeleted(initialOriginalRsvIds,
        temporaryRsvIds, linkBetweenRsvIds);
    var initialOriginalRsvsNotDeleted = getReservationsNotDeleted(originalBasket,
        originalRsvIdsToBeDeleted);

    var language = StringUtils.isEmpty(confirmAmendRequest.getBookingChannel().getLanguage())
        ? DEFAULT_LANGUAGE_EN : confirmAmendRequest.getBookingChannel().getLanguage().toLowerCase();

    var depositFolioComputationResult = new DepositFolioComputationResult();

    var hotelId = originalBasket.getHotelId();
    var originalAbsoluteDeadline = getOriginalAbsoluteDeadline(initialOriginalRsvIds, hotelId);

    //as we are in the confirm amend process, it is safe to assume
    //that isBookingAmendable is always true
    //the below needs to be done before any sort of Update Reservation to ensure accurate data
    final boolean isAbsoluteDeadlineEnabled = unleashWrapper.isEnabled(
          unleashWrapper.featureFlag().getEnableAbsoluteDeadline());
    boolean isBookingNonRefundable = false;
    if (isAbsoluteDeadlineEnabled) {
      final String sanitizedOriginalBookingRef = confirmAmendRequest.getOriginalBookingRef()
            .replace("\n", "").replace("\r", "");
      final boolean isBookingCancellable = getIsBookingCancellable(
            hotelId,
            sanitizedOriginalBookingRef,
            initialOriginalRsvIds,
            originalRsvIdsToBeDeleted);
      log.info("Original Booking Ref:{}, isBookingAmendable:{}, isBookingCancellable:{}",
            sanitizedOriginalBookingRef, "true", isBookingCancellable);
      if (Boolean.FALSE.equals(isBookingCancellable)) {
        isBookingNonRefundable = true;
      }
    }

    try {
      originalBasket = addNewReservationsToBasket(originalBasket, temporaryBasket,
          linkBetweenRsvIds, temporaryRsvIdsToBeAdded);
      originalReservations = getAllReservations(originalBasket,
          false, true);
      var originalPaymentMethod = getOriginalPaymentMethod(originalReservations);

      var hotelPaymentInformation = contentOutPort.getHotelPaymentInformation(
          hotelId, language, getCountryCodeFromLanguage(language));
      var defaultPaymentMethod = PaymentUtils.getDefaultPaymentMethod(originalPaymentMethod,
          hotelPaymentInformation, confirmAmendRequest.getBookingChannel().getChannel());

      cancelDeletedReservations(confirmAmendRequest, originalBasket, initialOriginalRsvIds,
          linkBetweenRsvIds, originalRsvIdsToBeDeleted, defaultPaymentMethod);

      var originalBasketWithoutDeletedRsvs = copyOriginalBasket(originalBasket,
          originalRsvIdsToBeDeleted);

      var onHoldOriginalRsvIds = getOnHoldOriginalReservationIds(originalReservations);
      
      if (!onHoldOriginalRsvIds.isEmpty()) {
        confirmAddedReservations(originalBasketWithoutDeletedRsvs, onHoldOriginalRsvIds,
            originalReservations, confirmAmendRequest, defaultPaymentMethod);
      }

      depositFolioComputationResult.setMarkAsPayOnArrival(Boolean.FALSE);
      if (PAY_NOW.equals(originalBasket.getPaymentOption())) {
        depositFolioComputationResult = calculateDfsForPayNow(temporaryBasket,
            originalBasketWithoutDeletedRsvs,
            originalReservations,
            linkBetweenRsvIds,
            confirmAmendRequest.getPaymentOptionSelected(),
            confirmAmendRequest.getBookingChannel().getChannel(),
            hotelPaymentInformation);
      }

      updatePackages(temporaryBasket, originalBasketWithoutDeletedRsvs, originalReservations,
          isRoomRatePlanCode);
      
      final var originalReservationsToConfirm = getOriginalReservationsToConfirm(originalBasket,
          originalRsvIdsToBeDeleted);
      //delete routing instructions
      final BookingAllowancesResponse bookingAllowances = deleteBookingAllowances(temporaryBasket, originalBasket,
          originalReservationsToConfirm);

      hotelReservationOhipOutPort.confirmAmend(
          buildConfirmAmendOnReservationsRequest(confirmAmendRequest, temporaryBasket,
              hotelId, linkBetweenRsvIds, depositFolioComputationResult,
              initialOriginalRsvsNotDeleted));

      var originalBasketReservationIds = originalBasketWithoutDeletedRsvs.getItems().stream()
          .map(BasketItemResponse::getSourceId).toList();

      if (shouldUpdateCancellationPolicies(isAbsoluteDeadlineEnabled, isBookingNonRefundable)) {
        var updateCancellationPoliciesRequest = UpdateCancellationPoliciesRequest.builder()
            .hotelId(hotelId)
            .reservationIds(originalBasketReservationIds)
            .absoluteDeadline(originalAbsoluteDeadline)
            .build();
        hotelReservationOhipOutPort.updateCancellationPolicies(updateCancellationPoliciesRequest);
      }

      updateOccupancySupplement(originalBasket, temporaryBasket, initialOriginalRsvsNotDeleted,
          linkBetweenRsvIds);

      updatedReservations = hotelReservationOhipOutPort
          .getReservationsByIds(hotelId, originalBasketReservationIds, false, false, true);

      updatedReservations.setBasketReference(confirmAmendRequest.getOriginalBookingRef());
      
      //update routing instructions after confirming amend
      if (!Objects.isNull(bookingAllowances)
          && (CollectionUtils.isNotEmpty(bookingAllowances.getBookingAllowances()))) {
        
        updateBusinessItems(temporaryBasket, originalBasket, originalReservationsToConfirm,
            language, linkBetweenRsvIds, onHoldOriginalRsvIds, hotelPaymentInformation, bookingAllowances);
      }
      
    } catch (Exception e) {
      log.error(
          "Could not amend reservations for original basket reference={}, initial no of rooms={}, "
              + "amended no of rooms={}, linked reservations={}, channel={}",
          originalBasket.getReference(), initialOriginalRsvIds.size(), temporaryBasket.getItems().size(),
          temporaryBasket.getLinkAmendReservations(), confirmAmendRequest.getBookingChannel(), e);
      //When some error occurred at confirm, if selected payment method is PN throws AMEND_REVERT_EXCEPTION
      //otherwise (for POA) throws AMEND_CONFIRM_EXCEPTION
      var basketError = new BasketError();
      basketError.setCode(isPN ? AmendErrorCode.AMEND_REVERT_EXCEPTION.name()
          : AmendErrorCode.AMEND_CONFIRM_EXCEPTION.name());
      basketError.setType(isPN ? TypeEnum.AMEND_REVERT : TypeEnum.AMEND_CONFIRM);

      var isErroredBooking = PAY_NOW.equals(originalBasket.getPaymentOption());
      //mark the original basket as errored so the user can't retry again, update with the basketError
      basketOutPort.setErroredBooking(originalBasket.getReference(), isErroredBooking, basketError);
      //update the temporary basket with the basketError
      basketOutPort.setErroredBooking(temporaryBasket.getReference(), isErroredBooking,
          basketError);
      var exception = new AmendReservationException(isPN ? ErrorCode.DIGITAL_AMEND_REVERT_EXCEPTION
          : ErrorCode.DIGITAL_AMEND_CONFIRM_EXCEPTION,
          "Could not amend reservations", e);
      ExceptionLogger.log(log, exception);
      throw exception;
    }
    DepositFoliosResponse depositFoliosResponse = null;
    if (PAY_NOW.equals(originalBasket.getPaymentOption())) {
      try {
        depositFoliosResponse =
            saveDepositFoliosAmendPayNow(originalBasket, originalRsvIdsToBeDeleted,
                temporaryRsvIdsToBeAdded,
                depositFolioComputationResult);
      } catch (Exception e) {
        log.error(
            "Unable to post deposit folios for original basket reference={}, initial no of rooms={},"
                + "amended no of rooms={}, linked reservations={}, channel={}",
            originalBasket.getReference(), initialOriginalRsvIds.size(), temporaryBasket.getItems().size(),
            temporaryBasket.getLinkAmendReservations(), confirmAmendRequest.getBookingChannel(), e);
        //When some error occured at DF step, if selected payment method is PN throws AMEND_REVERT_EXCEPTION
        //otherwise (for POA) throws AMEND_DEPOSIT_FOLIOS_EXCEPTION
        var basketError = new BasketError();
        basketError.setCode(isPN ? AmendErrorCode.AMEND_REVERT_EXCEPTION.name()
            : AmendErrorCode.AMEND_DEPOSIT_FOLIOS_EXCEPTION.name());
        basketError.setType(isPN ? TypeEnum.AMEND_REVERT : TypeEnum.AMEND_DEPOSIT_FOLIOS);
        //mark the original basket as errored so the user can't retry again, update with the basketError
        basketOutPort.setErroredBooking(originalBasket.getReference(), true, basketError);
        //update the temporary basket with the basketError
        basketOutPort.setErroredBooking(temporaryBasket.getReference(), true, basketError);
        var exception = new AmendReservationException(
            isPN ? ErrorCode.DIGITAL_AMEND_DEPOSIT_REVERT_EXCEPTION
                : ErrorCode.DIGITAL_AMEND_DEPOSIT_FOLIOS_EXCEPTION,
            "Could not amend reservations", e);
        ExceptionLogger.log(log, exception);
        throw exception;
      }
      if (depositFolioComputationResult.getTotalRefundAmt().compareTo(BigDecimal.ZERO) > 0) {
        try {
          amendPayNowLogic.triggerRefund(originalReservations, depositFolioComputationResult,
              originalBasket.getReference(), temporaryBasket.getReference());
        } catch (AmendReservationException e) {
          EmailRequest emailRequest = emailRequestBuilder(confirmAmendRequest,
                originalReservations, depositFoliosResponse, true);
          basketOutPort.triggerEmailConfirmation(emailRequest);
          throw e;
        }
      }
    }
    try {
      // delete original reservations which were cancelled from the original basket
      reservationCleanUp.cleanupOriginalBasket(originalBasket, originalRsvIdsToBeDeleted);
    } catch (Exception e) {
      var basketError = new BasketError();
      basketError.setCode(AmendErrorCode.AMEND_CONFIRM_EXCEPTION.name());

      var isErroredBooking = PAY_NOW.equals(originalBasket.getPaymentOption());
      //mark the original basket as errored so the user can't retry again, update with the basketError
      basketOutPort.setErroredBooking(originalBasket.getReference(), isErroredBooking, basketError);
      var exception = new AmendReservationException(ErrorCode.DIGITAL_AMEND_DELETE_RESERV_EXCEPTION,
          "Could not delete reservations which were cancelled from the original basket", e);
      ExceptionLogger.log(log, exception);
      throw exception;
    }

    Set<String> allAmendedReservations = updatedReservations.getReservationByIdList().stream()
        .map(ReservationByIdResponse::getReservationId).collect(
            Collectors.toSet());
    allAmendedReservations.addAll(temporaryRsvIdsToBeAdded);
    allAmendedReservations.addAll(originalRsvIdsToBeDeleted);
    updateCcAgentId(confirmAmendRequest, hotelId, Collections.unmodifiableSet(allAmendedReservations));

    EmailRequest emailRequest = emailRequestBuilder(confirmAmendRequest,
        originalReservations, depositFoliosResponse);

    basketOutPort.triggerEmailConfirmation(emailRequest);

    log.info("Amend was successful for original basket reference={}, initial no of rooms={}, "
            + "amended no of rooms={}, linked reservations={}, channel={}",
        originalBasket.getReference(), initialOriginalRsvIds.size(), temporaryBasket.getItems().size(),
        temporaryBasket.getLinkAmendReservations(), confirmAmendRequest.getBookingChannel());

    return updatedReservations;
  }

  private String getOriginalAbsoluteDeadline(List<String> initialOriginalRsvIds, String hotelId) {
    var initialOriginalReservation = hotelReservationOhipOutPort
        .getReservationsByReservationId(initialOriginalRsvIds.get(0), hotelId);
    var roomStay = initialOriginalReservation.getReservationIdDetailsResponse().getReservations()
        .getReservation().get(0).getRoomStay();
    var originalCancellationPolicies = hotelReservationOhipOutPort
        .getCancellationPolicies(Set.copyOf(initialOriginalRsvIds), hotelId,
            roomStay.getRoomRates().get(0).getRatePlanCode(), roomStay.getArrivalDate().toString());
    return originalCancellationPolicies.getTime();
  }

  private boolean shouldUpdateCancellationPolicies(boolean isAbsoluteDeadlineEnabled,
      boolean isBookingNonRefundable) {

    log.info("Is Absolute Deadline Feature Enabled:{}, is Booking Non-Refundable:{}",
        isAbsoluteDeadlineEnabled, isBookingNonRefundable);

    return BooleanUtils.isTrue(isAbsoluteDeadlineEnabled)
        && BooleanUtils.isTrue(isBookingNonRefundable)
        || BooleanUtils.isFalse(isAbsoluteDeadlineEnabled)
        && BooleanUtils.isFalse(isBookingNonRefundable);
  }

  private void updateCcAgentId(ConfirmAmendRequest confirmAmendRequest,
      String hotelId, Set<String> reservationIds) {

    if (confirmAmendRequest.getBookingChannel().isCcui() && unleashWrapper.isEnabled(
        unleashWrapper.featureFlag().getCcuiAgentIdLog())) {
      hotelReservationOhipOutPort.updateReservationCcAgentId(
          UpdateReservationCcAgentIdRequest.builder().hotelId(hotelId)
              .reservationIds(reservationIds)
              .ccAgentId(confirmAmendRequest.getCcAgentId())
              .clearFirst(true)
              .build());
    }
  }

  private List<String> getOriginalReservationsToConfirm(BasketResponse originalBasket,
      List<String> originalReservationIdsToBeDeleted) {
    return originalBasket.getItems()
        .stream()
        .map(BasketItemResponse::getSourceId)
        .filter(sourceId -> !originalReservationIdsToBeDeleted.contains(sourceId))
        .toList();
  }

  private List<BasketItemResponse> getReservationsNotDeleted(
      BasketResponse originalBasket, List<String> originalReservationIdsToBeDeleted) {
    return originalBasket
        .getItems()
        .stream()
        .filter(item -> !originalReservationIdsToBeDeleted.contains(item.getSourceId()))
        .toList();
  }

  private List<String> getReservationsToBeAdded(List<String> originalReservations,
      List<String> tempReservations,
      Map<String, String> linkAmendReservations) {

    var oldReservations = originalReservations
        .stream()
        .map(linkAmendReservations::get)
        .toList();

    return tempReservations
        .stream()
        .filter(reservation -> !oldReservations.contains(reservation))
        .toList();
  }

  private ConfirmAmendOnReservationsRequest buildConfirmAmendOnReservationsRequest(
      ConfirmAmendRequest confirmAmendRequest,
      BasketResponse temporaryBasket, String hotelId,
      Map<String, String> linkBetweenReservationIds,
      DepositFolioComputationResult depositFolioComputationResult,
      List<BasketItemResponse> initialOriginalRsvsNotDeleted) {

    var originalNotDeletedReservationIds = initialOriginalRsvsNotDeleted
        .stream().map(BasketItemResponse::getSourceId).toList();
    var linksForOriginalNotDeletedReservationIds = linkBetweenReservationIds.entrySet().stream()
        .filter(entry -> originalNotDeletedReservationIds.contains(entry.getKey()))
        .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));

    return ConfirmAmendOnReservationsRequest.builder()
        .bookingChannel(confirmAmendRequest.getBookingChannel())
        .hotelId(hotelId)
        .tempReservations(
            temporaryBasket.getItems().stream().map(BasketItemResponse::getSourceId).filter(
                linksForOriginalNotDeletedReservationIds::containsValue).toList())
        .originalReservations(originalNotDeletedReservationIds)
        .linkAmendReservations(linksForOriginalNotDeletedReservationIds)
        .sendEmailConfirmation(
            Optional.ofNullable(confirmAmendRequest.getSendEmailConfirmation()).orElse(false))
        .sendEmailInvoice(
            Optional.ofNullable(confirmAmendRequest.getSendEmailInvoice()).orElse(false))
        .markAsPayOnArrival(depositFolioComputationResult.getMarkAsPayOnArrival())
        .clearCcAgentIdUdf(confirmAmendRequest.getBookingChannel().isCcui() && unleashWrapper.isEnabled(
            unleashWrapper.featureFlag().getCcuiAgentIdLog()))
        .build();
  }

  private Set<String> getOnHoldOriginalReservationIds(
      ReservationByBasketRefResponse originalReservations) {
    return originalReservations
        .getReservationByIdList()
        .stream()
        .filter(ReservationByIdResponse::isOnHold)
        .toList()
        .stream()
        .map(ReservationByIdResponse::getReservationId)
        .collect(Collectors.toSet());
  }

  private String getOriginalPaymentMethod(
      ReservationByBasketRefResponse originalReservations) {
    return originalReservations.getReservationByIdList()
        .stream()
        .findFirst()
        .map(ReservationByIdResponse::getPaymentCard)
        .map(ReservationPaymentCardType::getPaymentMethod)
        .orElse(null);
  }

  private EmailRequest emailRequestBuilder(ConfirmAmendRequest confirmAmendRequest,
                                           ReservationByBasketRefResponse originalResByBasket,
                                           DepositFoliosResponse depositFoliosResponse) {
    return emailRequestBuilder(confirmAmendRequest, originalResByBasket, depositFoliosResponse, false);
  }

  private EmailRequest emailRequestBuilder(ConfirmAmendRequest confirmAmendRequest,
                                           ReservationByBasketRefResponse originalResByBasket, DepositFoliosResponse
                                               depositFoliosResponse, boolean failedRefund) {
    return EmailRequest.builder()
        .bookingReference(confirmAmendRequest.getOriginalBookingRef())
        .email(StringUtils.isEmpty(confirmAmendRequest.getEmailAddress()) ? originalResByBasket
            .getReservationByIdList().get(0).getReservationBooker().getEmail()
            : confirmAmendRequest.getEmailAddress())
        .deposits(depositFoliosResponse == null
            ? null
            : createEmailNotificationDepositsList(depositFoliosResponse.getDepositFolios()))
        .type(OperationType.AMEND.getType())
        .failedRefund(failedRefund)
        .build();
  }

  private EmailRequest emailRequestBuilder(String bookingReference, ReservationByBasketRefResponse originalResByBasket,
                                           Map<String, DepositsResponse> refundedDeposits, boolean failedRefund,
                                           String paymentId) {
    return EmailRequest.builder()
        .bookingReference(bookingReference)
        .email(originalResByBasket.getReservationByIdList().get(0).getReservationBooker().getEmail())
        .deposits(refundedDeposits == null
            ? null
            : getDepositFolioList(refundedDeposits, paymentId))
        .type(OperationType.CANCEL.getType())
        .failedRefund(failedRefund)
        .build();
  }

  private List<Deposits> getDepositFolioList(Map<String, DepositsResponse> refundedDeposits,
                                             String paymentId) {

    var deposits = refundedDeposits.values().stream()
        .map(DepositsResponse::getDeposits)
        .flatMap(List::stream)
        .toList();
    deposits.forEach(deposit -> {
      if (isNull(deposit.getPaymentReference())) {
        deposit.setPaymentReference(paymentId);
      }
    });
    return deposits;
  }

  private BookingAllowancesResponse deleteBookingAllowances(BasketResponse temporaryBasket,
      BasketResponse originalBasket,
      List<String> originalRsvIds) {

    final var bookingAllowances = getBookingAllowances(temporaryBasket);

    if (Objects.isNull(bookingAllowances)
        || (CollectionUtils.isEmpty(bookingAllowances.getBookingAllowances())
        && !ACCOUNT_COMPANY.equals(originalBasket.getPaymentOption()))) {
      return new BookingAllowancesResponse();
    }
    
    deleteRoutingInstructions(temporaryBasket.getHotelId(), Set.copyOf(originalRsvIds));
    
    return bookingAllowances;
  }

  private BasketResponse addNewReservationsToBasket(BasketResponse originalBasket,
                                                    BasketResponse temporaryBasket,
                                                    HashMap<String, String> linkBetweenReservationIds,
                                                    List<String> reservationIdsToBeAdded) {
    if (!reservationIdsToBeAdded.isEmpty()) {
      var copyReservationsRequest = CopyReservationsRequest.builder()
          .hotelId(originalBasket.getHotelId())
          .externalReferenceId(originalBasket.getBookingReference())
          .reservationIds(Set.copyOf(reservationIdsToBeAdded))
          .build();

      var newlyOnHoldOriginalReservationIds = hotelReservationOhipOutPort
          .copyReservations(copyReservationsRequest);

      originalBasket = basketOutPort.addReservationsToBasket(originalBasket.getReference(),
          originalBasket.getETag().replace("\"", ""),
          newlyOnHoldOriginalReservationIds, isOccupancySupplementApplicable(originalBasket.getHotelId()),
          getOccupancySupplementMap(temporaryBasket, newlyOnHoldOriginalReservationIds));

      // put also the new added rooms in linkBetweenReservationIds map,
      // in order to send them to ohip service for updates
      var newLinkBetweenReservation = newlyOnHoldOriginalReservationIds
          .getLinkBetweenReservations();
      newLinkBetweenReservation.keySet().forEach(key ->
          linkBetweenReservationIds.put(newLinkBetweenReservation.get(key), key)
      );
    }
    return originalBasket;
  }

  private void cancelDeletedReservations(ConfirmAmendRequest confirmAmendRequest,
      BasketResponse originalBasket, List<String> initialOriginalRsvIds,
      HashMap<String, String> linkBetweenRsvIds, List<String> originalRsvIdsToBeDeleted,
      String defaultPaymentMethod) {

    if (!originalRsvIdsToBeDeleted.isEmpty()) {
      List<DepositFoliosResponse> prepaidDeposits = new ArrayList<>();

      if (PAY_NOW.equals(originalBasket.getPaymentOption())) {
        prepaidDeposits = initialOriginalRsvIds
            .stream()
            .map(basketOutPort::getCharges)
            .filter(Objects::nonNull)
            .toList();
      }

      var cancelReservationRequest = buildCancelReservationRequest(originalBasket,
          confirmAmendRequest, originalRsvIdsToBeDeleted, defaultPaymentMethod);
      hotelReservationOhipOutPort.cancelReservation(cancelReservationRequest, prepaidDeposits);

      for (String reservationId : originalRsvIdsToBeDeleted) {
        linkBetweenRsvIds.remove(reservationId);
      }
    }
  }

  private DepositFoliosResponse saveDepositFoliosAmendPayNow(
      BasketResponse originalBasket,
      List<String> originalReservationIdsToDelete,
      List<String> reservationIdsToBeAdded,
      DepositFolioComputationResult depositFolioComputationResult) {

    depositFolioComputationResult.setPaymentId(
        depositFolioComputationResult.getPaymentId() != null ? depositFolioComputationResult
            .getPaymentId() : originalBasket.getPaymentID());
    return amendPayNowLogic.saveDepositFolios(depositFolioComputationResult,
        originalBasket.getHotelId(), originalReservationIdsToDelete, reservationIdsToBeAdded);
  }

  private BasketResponse getTemporaryBasketByBasketId(ConfirmAmendRequest confirmAmendRequest) {
    BasketResponse temporaryBasket;
    temporaryBasket = basketOutPort.getBasketById(confirmAmendRequest.getTempBookingRef());

    var now = Instant.now().toEpochMilli();
    if (now - Long.parseLong(temporaryBasket.getETag()) >= MINUTES_OF_AVAILABLE_BASKET * 60000) {
      var exception = new GenericReservationException(
          ErrorCode.DIGITAL_TEMP_BASKET_HAS_EXPIRED_EXCEPTION,
          "Temporary basket reference has expired or is empty");
      ExceptionLogger.log(log, exception);
      throw exception;
    }
    return temporaryBasket;
  }

  private List<Deposits> createEmailNotificationDepositsList(List<DepositFolio> depositFolios) {
    return depositFolios.stream()
        .map(this::createEmailNotificationDeposit)
        .toList();
  }

  private Deposits createEmailNotificationDeposit(DepositFolio depositFolio) {
    return Deposits.builder()
        .paymentReference(depositFolio.getPaymentId())
        .postedAmount(CurrencyAmountType.builder()
            .amount(depositFolio.getCharges().stream()
                .map(charge -> charge.getCurrencyAmount().getAmount())
                .reduce(BigDecimal.ZERO, BigDecimal::add))
            .currencyCode(depositFolio.getCharges().get(0).getCurrencyAmount().getCurrencyCode())
            .build())
        .build();
  }

  /**
   * We have to take the original reservations ids from temporaryBasket.getLinkAmendReservations()
   * because we don't want to take the new added ones. If we take also the new added ones, in the
   * next method a deposit folio will be generated also for them, and we won't be able to compare
   * the temporary deposit folios with the original ones and to take the conclusion that new rooms
   * were added and to generate proper DFs for them or to use money from bucket for them !!! It is
   * important to calculate DF after the new added rooms are confirmed because we need payment
   * information, !! It is important to calculate them before confirmAmend to be trigger, as we need
   * to send "markAsPayOnArrival" boolean value, in order to update the reservations to
   * PAY_ON_ARRIVAL if it is necessary
   */
  private DepositFolioComputationResult calculateDfsForPayNow(BasketResponse temporaryBasket,
                                                              BasketResponse originalBasket,
                                                              ReservationByBasketRefResponse originalResByBasket,
                                                              Map<String, String> linkBetweenReservationIds,
                                                              String paymentOption,
                                                              String channel,
                                                              HotelPaymentInformation hotelPaymentMethodInfo) {
    List<String> originalReservationIds = temporaryBasket.getLinkAmendReservations().keySet()
        .stream().toList();
    String hotelId = originalBasket.getHotelId();
    return amendPayNowLogic.calculateDfForPayNow(originalReservationIds, hotelId,
        temporaryBasket,
        linkBetweenReservationIds,
        originalResByBasket,
        paymentOption,
        channel,
        hotelPaymentMethodInfo);
  }

  private void updatePackages(BasketResponse tempBasket,
                                                        BasketResponse originalBasket,
                                                        ReservationByBasketRefResponse originalResByBasket,
                                                        boolean isRoomRatePlanCode) {
    var previousPackageSelections = hotelReservationOhipOutPort.getReservationsPackagesByIds(
        originalBasket.getHotelId(),
        originalBasket.getItems().stream()
            .map(BasketItemResponse::getSourceId).toList());
    var updatedPackageSelections = hotelReservationOhipOutPort.getReservationsPackagesByIds(
        tempBasket.getHotelId(),
        tempBasket.getItems().stream()
            .map(BasketItemResponse::getSourceId).toList());

    final List<RoomsSelectionsByReservationId> previous = new ArrayList<>();
    final List<RoomsSelectionsByReservationId> updated = new ArrayList<>();
    AtomicBoolean isAnyPackageUpdated = new AtomicBoolean(false);

    originalBasket.getItems().forEach(
        basketItemResponse -> {
          var tempReservationId = tempBasket.getLinkAmendReservations()
              .get(basketItemResponse.getSourceId());
          if (tempReservationId != null) {
            var previousPackages = getPackageForReservation(
                previousPackageSelections, basketItemResponse.getSourceId());
            var updatedPackage = getPackageForReservation(
                updatedPackageSelections, tempReservationId);

            if (!areEqual(previousPackages, updatedPackage)) {
              isAnyPackageUpdated.set(true);

              var previousRoomSelectionsElement = getRoomSelectionsElement(
                  basketItemResponse.getSourceId(), Optional.of(previousPackages));
              previous.add(previousRoomSelectionsElement);

              var roomSelectionsElement = getRoomSelectionsElement(
                  basketItemResponse.getSourceId(), Optional.of(updatedPackage));
              // we have to exclude city tax packages because they are added in ohip layer
              var packageSelectionsWithoutCityTax = roomSelectionsElement.getPackagesSelection()
                  .stream()
                  .filter(packagesSelection -> !CITYTAX.equals(packagesSelection.getId()))
                  .toList();
              roomSelectionsElement.setPackagesSelection(packageSelectionsWithoutCityTax);
              updated.add(roomSelectionsElement);
            }
          }
        }
    );
    if (isAnyPackageUpdated.get() && isRoomRatePlanCode) {
      //update packages for existing rooms (the newly added rooms and the deleted do not need package update)
      UpdateReservationPackagesByIdRequest updatePackagesByReservation = new UpdateReservationPackagesByIdRequest();
      updatePackagesByReservation.setHotelId(originalBasket.getHotelId());
      updatePackagesByReservation.setBasketReference(originalBasket.getReference());

      var reservationsWithoutCityTax =
          originalResByBasket.getReservationByIdList()
              .stream()
              .filter(reservationByIdResponse -> !reservationByIdResponse.getReservationPackageList()
                  .isEmpty())
              .filter(reservationByIdResponse -> reservationByIdResponse.getReservationPackageList()
                  .stream().noneMatch(reservationPackage ->
                      CITYTAX.equals(reservationPackage.getPackageCode()))).toList();

      if (!reservationsWithoutCityTax.isEmpty()
          && !hasStringNullDates(reservationsWithoutCityTax.get(0))) {
        updatePackagesByReservation.setArrival(reservationsWithoutCityTax.get(0)
            .getReservationPackageList().get(0).getStartDate());
        updatePackagesByReservation.setDeparture(reservationsWithoutCityTax.get(0)
            .getReservationPackageList().get(0).getEndDate());
      } else {
        var roomStay = originalResByBasket.getReservationByIdList().get(0).getRoomStay();
        updatePackagesByReservation.setArrival(roomStay.getArrivalDate());
        updatePackagesByReservation.setDeparture(roomStay.getDepartureDate());
      }
      updatePackagesByReservation.setPreviousRoomsSelections(previous);
      updatePackagesByReservation.setRoomsSelections(updated);

      updateReservationPackagesById(updatePackagesByReservation, true);
    }
  }

  private boolean hasStringNullDates(ReservationByIdResponse reservation) {
    var firstPackage = reservation.getReservationPackageList().get(0);
    return "null".equals(firstPackage.getStartDate())
        || "null".equals(firstPackage.getEndDate());
  }

  protected static boolean areEqual(RoomsSelectionsByReservation previousRooms,
      RoomsSelectionsByReservation currentRooms) {
    if (previousRooms == currentRooms) {
      return true;
    }
    if (previousRooms == null || currentRooms == null) {
      return false;
    }
    if (Objects.isNull(previousRooms.getPackagesSelection())
        && Objects.isNull(currentRooms.getPackagesSelection())) {
      return true;
    }
    return Objects.nonNull(previousRooms.getPackagesSelection())
        && Objects.nonNull(currentRooms.getPackagesSelection())
        && CollectionUtils.isEqualCollection(previousRooms.getPackagesSelection(),
        currentRooms.getPackagesSelection());
  }

  private static CancelReservationRequest buildCancelReservationRequest(BasketResponse originalBasket,
      ConfirmAmendRequest confirmAmendRequest, List<String> reservationIds, String digitalPaymentMethod) {

    return CancelReservationRequest.builder()
        .basketReference(originalBasket.getReference())
        .reservationIds(reservationIds)
        .hotelId(originalBasket.getHotelId())
        .paymentOption(originalBasket.getPaymentOption())
        .token(confirmAmendRequest.getToken())
        .digitalPaymentMethod(digitalPaymentMethod)
        .build();
  }

  private void confirmAddedReservations(BasketResponse originalBasket,
      Set<String> onHoldOriginalRsvIds, ReservationByBasketRefResponse originalResByBasket,
      ConfirmAmendRequest confirmAmendRequest, String digitalPaymentMethod) {
    var originalReservations = originalResByBasket.getReservationByIdList();
    originalReservations.removeIf(ReservationByIdResponse::isOnHold);

    if (!onHoldOriginalRsvIds.isEmpty()) {
      DepositFoliosResponse depositFoliosResponse = hotelReservationOhipOutPort.getGeneratedDepositFolios(
          originalBasket.getHotelId(), onHoldOriginalRsvIds);

      onHoldOriginalRsvIds.parallelStream().forEach(reservationId -> {
        var confirmReservationRequest = buildConfirmReservationRequest(originalBasket,
            confirmAmendRequest, digitalPaymentMethod, originalReservations, reservationId);
        confirmReservation(confirmReservationRequest, Optional.ofNullable(depositFoliosResponse));
      });
    }
  }

  private static ConfirmReservationRequest buildConfirmReservationRequest(
      BasketResponse originalBasket, ConfirmAmendRequest confirmAmendRequest,
      String digitalPaymentMethod, List<ReservationByIdResponse> originalReservations,
      String reservationId) {

    var paymentCard = originalReservations.get(0).getPaymentCard();
    var paymentOption = confirmAmendRequest.getPaymentOptionSelected() != null
        ? PaymentOption.valueOf(confirmAmendRequest.getPaymentOptionSelected())
        : originalBasket.getPaymentOption();
    var isPayOnArrivalWithAcOrRwc = PAY_ON_ARRIVAL.equals(paymentOption)
        && (ACCOUNT_COMPANY.equals(originalBasket.getPaymentOption())
        || RESERVE_WITHOUT_CARD.equals(originalBasket.getPaymentOption()));

    return ConfirmReservationRequest.builder()
        .reservationId(reservationId)
        .hotelId(originalBasket.getHotelId())
        .paymentOption(PAY_ON_ARRIVAL.equals(paymentOption) && NO_CARD_OPTIONS.contains(
            originalBasket.getPaymentOption())
            ? originalBasket.getPaymentOption() : paymentOption)
        .paymentCard(isPayOnArrivalWithAcOrRwc ? null : buildPaymentCard(paymentCard))
        .paymentMethod(paymentCard.getPaymentMethod())
        .digitalPaymentMethod(PAY_NOW.name().equals(confirmAmendRequest.getPaymentOptionSelected())
            ? digitalPaymentMethod : null)
        .paymentType(paymentCard.getPaymentMethod())
        .pibaCardPresent(paymentCard.getPaymentMethod() != null
                && isPiba(paymentCard.getPaymentMethod()) ? Boolean.TRUE : null)
        .build();
  }

  private static PaymentCard buildPaymentCard(ReservationPaymentCardType paymentCard) {
    return PaymentCard.builder()
        .cardType(paymentCard.getCardType().toUpperCase())
        .token(paymentCard.getToken())
        .expirationDate(paymentCard.getExpirationDate().toString())
        .cardNumberLast4Digits(paymentCard.getCardNumberMasked()
            .substring(paymentCard.getCardNumberMasked().length() - 4))
        .cardHolderName(paymentCard.getCardHolderName())
        .build();
  }

  private List<Reservation> createOnHoldReservations(
      AmendStayDatesRequest amendStayDatesRequests,
      List<AmendOnHoldInterval> amendOnHoldReservationInterval,
      ReservationByIdResponse basketReservation,
      String hotelId,
      String companyId,
      String wbRoomType,
      AmendDistributionStayDatesRequest amendDistributionStayDatesRequest) {

    var retVal = new ArrayList<Reservation>();

    // create the needed on hold reservations
    amendOnHoldReservationInterval.forEach(amendOnHoldRes -> {
      List<RatePrice> avRatePrices = new ArrayList<>();
      if (Boolean.TRUE.equals(amendStayDatesRequests.getIsOta())
          && Objects.nonNull(wbRoomType) && !wbRoomType.isBlank()) {
        calculateRatePricesForAmendDistributionDates(
            wbRoomType,
            basketReservation,
            hotelId,
            companyId,
            amendOnHoldRes.getAmendArrivalDate(),
            amendOnHoldRes.getAmendDepartureDate(),
            avRatePrices
        );
      }

      String promotionCode = Optional.ofNullable(basketReservation)
          .map(ReservationByIdResponse::getRoomStay)
          .map(RoomStayByIdResponse::getPromotionCode)
          .orElse(null);
      var roomRate = RoomRate.builder()
          .startDate(amendOnHoldRes.getAmendArrivalDate())
          .endDate(amendOnHoldRes.getAmendDepartureDate())
          .ratePlanCode(basketReservation.getRoomStay().getRatePlanCode())
          .pmsRoomType(getPmsRoomTypeByChannel(amendStayDatesRequests,
              basketReservation, amendDistributionStayDatesRequest))
          .ratePrices(avRatePrices)
          .fixedRate(amendStayDatesRequests.getIsOta())
          .promotionCode(promotionCode)
          .build();

      var temporaryReservation = Reservation.builder()
          .hotelId(hotelId)
          .arrival(amendOnHoldRes.getAmendArrivalDate())
          .departure(amendOnHoldRes.getAmendDepartureDate())
          .adultsNumber(getAdultsNumberByChannel(amendStayDatesRequests,
              basketReservation, amendDistributionStayDatesRequest))
          .childrenNumber(getChildrenNumberByChannel(amendStayDatesRequests,
              basketReservation, amendDistributionStayDatesRequest))
          .cotRequired(getCotRequiredByChannel(amendStayDatesRequests,
              basketReservation, amendDistributionStayDatesRequest))
          .roomRates(roomRate)
          .build();

      retVal.add(temporaryReservation);

    });

    return retVal;
  }

  private String getPmsRoomTypeByChannel(AmendStayDatesRequest amendStayDatesRequests,
      ReservationByIdResponse basketReservation, AmendDistributionStayDatesRequest amendDistributionStayDatesRequest) {
    return verifyChannelAndRequest(amendStayDatesRequests, amendDistributionStayDatesRequest)
        && StringUtils.isNotBlank(amendDistributionStayDatesRequest.getWbRoomType())
        ? amendDistributionStayDatesRequest.getWbRoomType()
        : basketReservation.getRoomStay().getRoomType();
  }

  private Integer getAdultsNumberByChannel(AmendStayDatesRequest amendStayDatesRequests,
      ReservationByIdResponse basketReservation, AmendDistributionStayDatesRequest amendDistributionStayDatesRequest) {
    return verifyChannelAndRequest(amendStayDatesRequests, amendDistributionStayDatesRequest)
        && amendDistributionStayDatesRequest.getAdults() != null
        ? amendDistributionStayDatesRequest.getAdults()
        : basketReservation.getRoomStay().getAdultsNumber();
  }

  private Integer getChildrenNumberByChannel(AmendStayDatesRequest amendStayDatesRequests,
      ReservationByIdResponse basketReservation, AmendDistributionStayDatesRequest amendDistributionStayDatesRequest) {
    return verifyChannelAndRequest(amendStayDatesRequests, amendDistributionStayDatesRequest)
        && amendDistributionStayDatesRequest.getChildren() != null
        ? amendDistributionStayDatesRequest.getChildren()
        : basketReservation.getRoomStay().getChildrenNumber();
  }

  private Boolean getCotRequiredByChannel(AmendStayDatesRequest amendStayDatesRequests,
      ReservationByIdResponse basketReservation, AmendDistributionStayDatesRequest amendDistributionStayDatesRequest) {
    return verifyChannelAndRequest(amendStayDatesRequests, amendDistributionStayDatesRequest)
        && amendDistributionStayDatesRequest.getCotRequired() != null
        ? amendDistributionStayDatesRequest.getCotRequired()
        : basketReservation.getRoomStay().getCot();
  }

  private boolean verifyChannelAndRequest(AmendStayDatesRequest amendStayDatesRequests,
      AmendDistributionStayDatesRequest amendDistributionStayDatesRequest) {
    return amendStayDatesRequests.getBookingChannel().isDistr()
        && Objects.nonNull(amendDistributionStayDatesRequest);
  }

  private void calculateRatePricesForAmendDistributionDates(
      String wbRoomType,
      ReservationByIdResponse basketReservation,
      String hotelId,
      String companyId,
      String arrivalDate,
      String departureDate,
      List<RatePrice> avRatePrices) {
    HotelAvailabilityByIdsV2Request availabilityRequest = HotelAvailabilityByIdsV2Request.builder()
        .hotelIds(List.of(hotelId))
        .rooms(
            List.of(Room.builder().tag(wbRoomType)
                .numberOfRooms(1)
                .adults(basketReservation.getRoomStay().getAdultsNumber())
                .children(basketReservation.getRoomStay().getChildrenNumber())
                //.roomType(pmsRoomType) //TO DO
                .build()))
        .arrivalDate(LocalDate.parse(arrivalDate))
        .departureDate(LocalDate.parse(departureDate))
        .bookingChannel(uk.co.whitbread.reservation.domain.model.availability.in.BookingChannel.builder()
            .channel("DISTR")
            .subchannel("AGENCY")
            .language("EN")
            .build())
        .isOTA(true)
        .rates(Rate.builder()
            .ratePlanCodes(List.of(basketReservation.getRoomStay().getRatePlanCode()))
            .corporateRates(CorporateRate.builder()
                .corporateId(null).build()).build())
        .vatNotRequired(false)
        .build();
    var hotelAvailabilityByIdsV2 = hotelAvailabilityOutPort.getHotelAvailabilitiesByIdsV2(availabilityRequest);

    hotelAvailabilityByIdsV2.getHotelAvailability().stream()
        .flatMap(hotelAvailabilityResultV2 ->
            Optional.ofNullable(hotelAvailabilityResultV2.getRoomStays())
                .orElseGet(List::of).stream()
        )
        .flatMap(roomStay ->
            Optional.ofNullable(roomStay.getRoomTypes())
                .orElseGet(List::of).stream()
        )
        .flatMap(roomTypeV2 ->
            Optional.ofNullable(roomTypeV2.getRoomRates())
                .orElseGet(List::of).stream()
        )
        .flatMap(roomRates ->
            Optional.ofNullable(roomRates.getRoomRateInfo().getPriceInfo())
                .orElseGet(List::of).stream()
        )
        .forEach(avRoomRate ->
            avRatePrices.add(createRatePrices(avRoomRate.getAmountBeforeTax(),
                    avRoomRate.getStayDate(),
                    avRoomRate.getStayDate().plusDays(1)
                )
            )
    );
  }

  private void validateMaxNightsRule(AmendStayDatesRequest amendStayDatesRequest,
                                     String channelId) {
    log.trace("Validating MaxNightsRule");

    Integer numberOfNights;
    String channel = amendStayDatesRequest.getBookingChannel().getChannel();
    if (unleashWrapper.isEnabled(unleashWrapper.featureFlag().getAemSearchRules())) {
      var searchRules = contentOutPort.getSearchRules(channel, Optional.empty());
      numberOfNights = searchRules.getMaxNights();
    } else {
      var maxNightsRuleResponse = rulesOutPort.getMaxNightsRule(channelId);
      numberOfNights = maxNightsRuleResponse.getMaxNights();
    }
    final LocalDate arrivalDate = LocalDate.parse(amendStayDatesRequest.getNewStartDate(),
        DateTimeFormatter.ISO_LOCAL_DATE);
    final LocalDate departureDate = LocalDate.parse(amendStayDatesRequest.getNewEndDate(),
        DateTimeFormatter.ISO_LOCAL_DATE);
    if (DAYS.between(arrivalDate, departureDate) > numberOfNights) {
      log.error("Error while trying validate Max Nights Rule for amendStayDatesRequest={}",
          amendStayDatesRequest);
      var exception = new RulesAgentBadRequestException(ErrorCode.DIGITAL_NR_NIGHTS_EXCEPTION,
          "number of nights is not valid according to the rule");
      ExceptionLogger.log(log, exception);
      throw exception;
    }
  }

  @Override
  public CopyBookingResponse copyBooking(CopyBookingRequest copyBookingRequest) {
    log.debug("Entered copyBooking with copyBookingRequest={}", copyBookingRequest);

    var originalBasket = basketOutPort.getBasketById(
        copyBookingRequest.getOriginalBasketReference());

    validateAuthenticationAndToken(copyBookingRequest.getToken(),
        copyBookingRequest.getOriginalBasketReference());
    //DNRQ-57990 Workaround
    if (Objects.nonNull(originalBasket.getChannel())
        && originalBasket.getChannel().equals(DISTR_BOOKING_CHANNEL)
        && (originalBasket.getPaymentOption() == null)) {
      originalBasket.setPaymentOption(PAY_ON_ARRIVAL);
    }

    var paymentOption = ACCOUNT_COMPANY.equals(originalBasket.getPaymentOption())
        ? PaymentOptionEnum.ACCOUNT_COMPANY : null;

    adjustPackageGroupsIfNecessary(originalBasket);

    var copyBasket = basketOutPort.createBasket(originalBasket.getHotelId(),
        originalBasket.getReference(), null, null, paymentOption, null,
        originalBasket.getChannel(), null);

    var copyReservations = hotelReservationOhipOutPort.copyReservations(
        buildCopyReservationsRequest(originalBasket, extractReservationIds(originalBasket),
            copyBasket.getBookingReference()));

    copyBasket = basketOutPort.addReservationsToBasket(copyBasket.getReference(), copyBasket.getETag(),
        copyReservations, isOccupancySupplementApplicable(originalBasket.getHotelId()),
        getOccupancySupplementMap(originalBasket, copyReservations));

    //FIXME: WHY? etag already coming on put basket item
    // copyBasket = basketOutPort.getBasketById(copyBasket.getReference());

    copyBasket = basketOutPort.linkAmendReservationsInBasket(copyBasket.getReference(),
        copyBookingRequest.getBookingChannel().getChannel(),
        copyBasket.getETag().replace("\"", ""),
        copyReservations.getLinkBetweenReservations());

    //copy booking allowances from original reservations, if they exist
    copyBookingAllowances(originalBasket, copyBasket, true);

    if (Objects.nonNull(originalBasket.getPaymentOption())
        && PAY_NOW.toString()
        .equalsIgnoreCase(originalBasket.getPaymentOption().toString())) {

      var originalReservationIds = originalBasket.getItems().stream()
          .map(BasketItemResponse::getSourceId).toList();
      var originalResByBasket = getAllReservationsJustByBasketReference(
          originalBasket.getReference(), false, false);

      var originalDepositFolios = getDepositFoliosFromDb(originalReservationIds);
      originalDepositFolios.getDepositFolios().forEach(deposit -> {
        deposit.setHotelId(originalBasket.getHotelId());
        deposit.setVatRegion(
            originalResByBasket.getReservationByIdList().get(0).getCashiering().getTaxType()
                .getCode());
        deposit.setPaymentId(originalBasket.getPaymentID());
      });

      if (!Boolean.TRUE.equals(originalBasket.getIsCheckInOnlinePay())
          && originalDepositFolios.getDepositFolios().isEmpty()) {
        var ex = new ReservationNotFoundException(ErrorCode.DIGITAL_RESERVATION_CHARGES_EXCEPTION,
            String.format("No charges present for booking with basket reference %s",
                originalBasket.getBookingReference()));
        ExceptionLogger.log(log, ex);
        throw ex;
      }
    }

    log.debug("Booking successfully copied in basketReference={}", copyBasket.getReference());
    return CopyBookingResponse.builder()
        .copyBasketReference(copyBasket.getReference())
        .build();
  }

  /**
   * After update to Opera 24.4.3, this method corrects packages for reservations done in the previous version of Opera.
   * This means that for further package operations the following are required:
   * - the package group needs to be removed from the packages list if present
   * - packageGroup field needs to be populated if the packages are part of any group
   *
   * @param originalBasket BasketResponse for the original basket
   */
  private void adjustPackageGroupsIfNecessary(BasketResponse originalBasket) {
    List<String> reservationIds = extractReservationIds(originalBasket).stream().toList();
    var reservationsPackagesByIds =
        hotelReservationOhipOutPort.getReservationsPackagesByIds(originalBasket.getHotelId(), reservationIds);
    Map<String, Set<String>> packageGroups = packageProperties.getGroups();

    if (requiresPackageGroupUpdate(reservationsPackagesByIds, packageGroups)) {
      log.info("Current packages require package group update {}", reservationsPackagesByIds);

      List<RoomsSelections> previousRoomsSelections = reservationsPackagesByIds.getRoomsSelections().stream()
          .map(room -> {
            List<PackagesSelection> removedPackagesSelection = null;
            if (null != room.getPackagesSelection()) {
              removedPackagesSelection = room.getPackagesSelection().stream()
                  .map(p -> PackagesSelection.builder().noSelections(p.getNoOfSelections()).id(p.getId())
                      .packageGroup(p.getPackageGroup()).build()).toList();
            }
            return RoomsSelections.builder().packagesSelection(removedPackagesSelection).build();
          }).toList();
      log.info("Packages to be removed {}", previousRoomsSelections);

      List<RoomsSelections> newRoomsSelections = reservationsPackagesByIds.getRoomsSelections().stream()
          .map(room ->  RoomsSelections.builder()
                .packagesSelection(getUpdatedPackagesSelection(room, packageGroups)).build())
          .toList();
      log.info("Packages to be added {}", newRoomsSelections);

      ReservationByIdDetailsResponse reservationsByReservationId =
          hotelReservationOhipOutPort.getReservationsByReservationId(reservationIds.get(0),
              originalBasket.getHotelId());

      List<ReservationDetails> reservation = reservationsByReservationId.getReservationIdDetailsResponse()
          .getReservations().getReservation();
      if (CollectionUtils.isNotEmpty(reservation)) {
        hotelReservationOhipOutPort.updateReservationPackages(
            ReservationPackagesRequest.builder()
                .basketReference(originalBasket.getReference())
                .reservationsId(reservationIds)
                .hotelId(originalBasket.getHotelId())
                .arrival(String.valueOf(reservation.get(0).getRoomStay().getArrivalDate()))
                .departure(String.valueOf(reservation.get(0).getRoomStay().getDepartureDate()))
                .previousRoomsSelections(previousRoomsSelections)
                .roomsSelections(newRoomsSelections)
                .build());
      } else {
        ReservationNotFoundException reservationNotFoundException =
            new ReservationNotFoundException(ErrorCode.DIGITAL_RESERVATION_ID_EXCEPTION,
                "Error while trying to get reservation details for reservation id " + reservationIds.get(0));
        ExceptionLogger.log(log, reservationNotFoundException);
        throw reservationNotFoundException;
      }
    }
  }

  private static List<PackagesSelection> getUpdatedPackagesSelection(RoomsSelectionsByReservation room,
      Map<String, Set<String>> packageGroups) {
    List<PackagesSelection> updatedPackagesSelection = null;
    if (null != room.getPackagesSelection()) {
      updatedPackagesSelection = room.getPackagesSelection().stream()
          .filter(p -> !packageGroups.containsKey(p.getId()))
          .map(p -> {
            String newPackageGroup = packageGroups.entrySet().stream()
                .filter(entry -> entry.getValue().contains(p.getId()))
                .map(Entry::getKey)
                .findFirst().orElse(p.getPackageGroup());
            return PackagesSelection.builder().noSelections(p.getNoOfSelections()).id(p.getId())
                .packageGroup(StringUtils.isEmpty(p.getPackageGroup()) ? newPackageGroup : p.getPackageGroup()).build();
          }).toList();
    }
    return updatedPackagesSelection;
  }

  private static boolean requiresPackageGroupUpdate(
      ReservationsPackagesResponse reservationsPackagesByIds, Map<String, Set<String>> packageGroups) {

    return reservationsPackagesByIds.getRoomsSelections().stream()
        .filter(roomsSelections -> roomsSelections.getPackagesSelection() != null)
        .flatMap(roomsSelections -> roomsSelections.getPackagesSelection().stream())
        .anyMatch(packagesSelection -> {
          boolean isPackageGroupInList = packageGroups.containsKey(packagesSelection.getId());
          boolean isPackageGroupMissing = StringUtils.isBlank(packagesSelection.getPackageGroup())
              && packageGroups.values().stream().anyMatch(packages -> packages.contains(packagesSelection.getId()));
          return isPackageGroupInList || isPackageGroupMissing;
        });
  }

  private static Map<String, Boolean> getOccupancySupplementMap(BasketResponse originalBasket,
      CopyReservationsResponse copyReservations) {

    Map<String, Boolean> hasOccupancySupMap = null;

    if (Objects.nonNull(copyReservations.getLinkBetweenReservations())) {
      hasOccupancySupMap = copyReservations.getReservations().stream()
          .collect(Collectors.toMap(CopyReservationResponse::getReservationId,
              reservation -> originalBasket.getItems().stream()
                  .filter(item -> copyReservations.getLinkBetweenReservations().entrySet()
                      .stream()
                      .filter(entry -> reservation.getReservationId().equals(entry.getValue()))
                      .findFirst()
                      .map(Entry::getKey)
                      .get()
                      .equals(item.getSourceId()))
                  .findFirst()
                  .map(BasketItemResponse::getHasOccupancySup)
                  .orElse(false)));
    }
    return hasOccupancySupMap;
  }

  private DepositFoliosResponse getDepositFoliosFromDb(List<String> reservationIds) {
    List<DepositFolio> depositFolios = new ArrayList<>();
    reservationIds.forEach(reservationId -> {
      var originalDepositFoliosByResId = basketOutPort.getCharges(reservationId);
      if (Objects.nonNull(originalDepositFoliosByResId)
          && Objects.nonNull(originalDepositFoliosByResId.getDepositFolios())) {
        depositFolios.addAll(originalDepositFoliosByResId.getDepositFolios());
      }
    });
    return DepositFoliosResponse.builder()
        .depositFolios(depositFolios).build();
  }

  @Override
  public BookingAllowancesResponse getBookingAllowances(String basketReference) {
    log.debug("Entered getBookingAllowances for basketReference={}", sanitize(basketReference));
    var basket = basketOutPort.getBasketById(basketReference);

    return getBookingAllowances(basket);
  }

  private BookingAllowancesResponse getBookingAllowances(BasketResponse basket) {
    var reservationIds = extractReservationIds(basket);

    if (reservationIds.isEmpty()) {
      var exception = new GenericBadRequestException(
          ErrorCode.DIGITAL_NO_RESERVATION_BASKET_EXCEPTION,
          String.format("No reservations found for basketReference=%s", basket.getReference()));
      ExceptionLogger.log(log, exception);
      throw exception;
    }

    List<String> basketBookingAllowances = basket.getBookingAllowances() != null
        ? basket.getBookingAllowances().stream()
        .map(BookingAllowance::getAllowance)
        .toList() : List.of();

    var operaBookingAllowances = getBookingAllowancesFromPms(
        basket.getHotelId(),
        reservationIds.stream().toList().get(0),
        basketBookingAllowances);

    if (ACCOUNT_COMPANY.equals(basket.getPaymentOption())
        && unleashWrapper.isEnabled(unleashWrapper.featureFlag().getUseBasketAllowances())) {
      if (isNull(basket.getBookingAllowances()) || isNull(operaBookingAllowances)) {
        return null;
      }
      return BookingAllowancesResponse.builder()
          .bookingAllowances(basket.getBookingAllowances())
          .businessNotes(operaBookingAllowances.getBusinessNotes())
          .build();
    }

    return operaBookingAllowances;
  }

  private BookingAllowancesResponse getBookingAllowancesFromPms(String hotelId,
      String reservationId, List<String> basketBookingAllowances) {
    return hotelReservationOhipOutPort.getBookingAllowances(hotelId, reservationId, basketBookingAllowances);
  }

  private UpdateReservationsRequest getEditRoomUpdateRequest(
      UpdateReservationsRequest updateReservationRequest,
      BasketResponse basketResponse,
      String tempBasketRef,
      BookingChannel bookingChannel, Boolean isNonRefundable,
      Boolean isOta
  ) {

    validateRequest(updateReservationRequest, tempBasketRef, bookingChannel, isNonRefundable,
        basketResponse);

    var currentUserAccount = authenticatedUserService.getCurrentUserAccount();
    currentUserAccount.ifPresent(
            account -> updateReservationRequest.setCompanyId(account.getOperaCompanyId()));

    ReservationByBasketRefResponse reservationByIdTemp;
    try {
      reservationByIdTemp = getAllReservationsJustByBasketReference(tempBasketRef, false, true);
    } catch (HotelReservationNotFoundException ex) {
      log.error(
              "Error while trying to get reservations by ids for basket reference {}", sanitize(tempBasketRef),
              ex);
      throw ex;
    }
    validateBasketAndSelfBooker(updateReservationRequest, reservationByIdTemp, currentUserAccount, basketResponse);

    var reservationIdToUpdate = updateReservationRequest.getReservations().get(0)
            .getReservationId();
    var reservationByIdResponse = reservationByIdTemp.getReservationByIdList().stream()
            .filter(reservationById -> reservationById.getReservationId().equals(reservationIdToUpdate))
            .findFirst()
            .orElseThrow(() -> {
              var exception = new ReservationNotFoundException(
                  ErrorCode.DIGITAL_RESERVATION_ID_EXCEPTION,
                  String.format("Reservation with id %s was not found ", reservationIdToUpdate));
              ExceptionLogger.log(log, exception);
              return exception;
            });

    updateReservationRequest.getReservations().get(0).setHotelId(basketResponse.getHotelId());
    updateReservationRequest.getReservations().get(0).getRoomStay()
            .setArrivalDate(reservationByIdResponse.getRoomStay().getArrivalDate());
    updateReservationRequest.getReservations().get(0).getRoomStay()
            .setDepartureDate(reservationByIdResponse.getRoomStay().getDepartureDate());
    updateReservationRequest.setDistributionIATANumber(updateReservationRequest.getDistributionIATANumber());

    var roomType = updateReservationRequest.getReservations().get(0)
            .getRoomStay().getRoomRates().get(0).getRoomType();
    var adults = updateReservationRequest.getReservations().get(0)
            .getRoomStay().getRoomOccupancy().getAdultCount();
    var children = updateReservationRequest.getReservations().get(0)
            .getRoomStay().getRoomOccupancy().getChildCount();

    if (hotelReservationOhipOutPort.getWbRoomTypes().contains(roomType)
            && !validateRoomOccupancyRule(roomType, adults, children, bookingChannel.getChannel(),
            basketResponse.getHotelId())) {
      var exception = new GenericBadRequestException(ErrorCode.DIGITAL_WRONG_ROOM_EXCEPTION,
          "Wrong room type for number of adults and children according to room occupancy rule");
      ExceptionLogger.log(log, exception);
      throw exception;
    }

    // reset meals if the number of adults decreases
    if (adults < reservationByIdResponse.getRoomStay().getAdultsNumber()) {
      log.info("Going to rest meals after adults count changed, adults request {}", adults);
      updatePackagesToResetMeals(updateReservationRequest, basketResponse);
    }

    // update occupancy supplement if the number of adults changes
    updateOccupancySupplement(adults, reservationByIdResponse, basketResponse, isOta);

    updateReservationRequest.setTempReservations(reservationByIdTemp);

    var ratePlanCode = reservationByIdTemp.getReservationByIdList().get(0).getRoomStay()
            .getRatePlanCode();
    if (Strings.CI.equalsAny(ratePlanCode, EMPLOYEE_RATE_PLAN)) {
      updateReservationRequest.setCompanyId(companyProperties.getCompanyId());
    }

    return updateReservationRequest;
  }

  private void validateRequest(UpdateReservationsRequest updateReservationRequest, String tempBasketRef,
      BookingChannel bookingChannel, Boolean isNonRefundable, BasketResponse basketResponse) {

    if (Boolean.FALSE.equals(updateReservationRequest.getBookingChannel().isDistr())) {
      validateAuthenticationAndToken(updateReservationRequest.getToken(),
          basketResponse.getOriginalBasketId());
    }
    if (!CCUI_BOOKING_CHANNEL.equals(updateReservationRequest.getBookingChannel().getChannel())) {
      if (isNull(isNonRefundable)) {
        if (isBookingNonRefundable(
                basketResponse.getHotelId(),
                tempBasketRef,
                updateReservationRequest.getToken(), bookingChannel, null)) {
          var exception = new GenericBadRequestException(
                  ErrorCode.DIGITAL_AMEND_LOGIC_REFUND_EXCEPTION,
                  "Updating the room is forbidden for non-refundable bookings");
          ExceptionLogger.log(log, exception);
          throw exception;
        }
      } else if (Boolean.TRUE.equals(isNonRefundable)) {
        var exception = new GenericBadRequestException(DIGITAL_UPDATE_ROOM_EXCEPTION,
                "Updating the room is forbidden for non-refundable bookings");
        ExceptionLogger.log(log, exception);
        throw exception;
      }
    }
  }

  private void validateAuthenticationAndToken(String updateReservationRequest,
      String basketResponse) {
    if (!authenticatedUserService.isUserAuthenticated()) {
      ManageBookingUtils.validateToken(updateReservationRequest, basketResponse);
    }
  }

  private void validateBasketAndSelfBooker(UpdateReservationsRequest updateReservationRequest,
      ReservationByBasketRefResponse reservationByIdTemp, Optional<Account> currentUserAccount,
      BasketResponse basketResponse) {
    reservationByIdTemp.getReservationByIdList().forEach(reservationByIdResponse -> {
      if (amendLogic.isLeadGuestUpdated(reservationByIdResponse,
          updateReservationRequest.getReservations().get(0))) {
        currentUserAccount.ifPresent(userAccount -> {
          if ("SELF".equals(userAccount.getAccessLevel())) {
            var exception = new GenericBadRequestException(
                ErrorCode.DIGITAL_INCONSISTENT_BOOKING_EXCEPTION,
                "Self Booker can't edit credentials");
            ExceptionLogger.log(log, exception);
            throw exception;
          }
        });
      }
    });


  }

  @Override
  public TempBookingRefResponse editRoom(
          UpdateReservationsRequest request,
          String tempBasketRef,
          BookingChannel bookingChannel,
          Boolean isNonRefundable,
          Boolean isOta) {

    BasketResponse basketResponse = basketOutPort.getBasketById(tempBasketRef);

    UpdateReservationsRequest updateReservationRequest = getEditRoomUpdateRequest(
        request, basketResponse, tempBasketRef,
        bookingChannel, isNonRefundable, isOta);

    try {
      hotelReservationOhipOutPort.amendEditRoom(updateReservationRequest);
    } catch (HotelReservationOhipException e) {
      if (basketResponse.getChannel() != null && basketResponse.getChannel()
          .equalsIgnoreCase("DISTR")) {
        var message = String.format(
            "Couldn't edit your room due to non-availability of roomRate."
                + "Error while trying to update reservations for request updateReservationsRequest=%s",
            Arrays.toString(updateReservationRequest.getReservations().toArray()));
        ExceptionLogger.log(log, e, message);
        throw e;
      } else {
        var message = String.format(
            "Couldn't edit your room. "
                + "Error while trying to update reservations for request updateReservationsRequest={}",
            Arrays.toString(updateReservationRequest.getReservations().toArray()));
        ExceptionLogger.log(log, e, message);
        throw e;
      }
    }

    return TempBookingRefResponse.builder()
        .tempBookingRef(tempBasketRef)
        .build();
  }

  private void updateOccupancySupplement(Integer adults, ReservationByIdResponse reservationByIdResponse,
                                         BasketResponse basketResponse, Boolean isOta) {

    if (adults.equals(reservationByIdResponse.getRoomStay().getAdultsNumber())) {
      return;
    }

    if (unleashWrapper.isEnabled(unleashWrapper.featureFlag().getApplyOccupancySupplement())) {

      var occupancySupplementPricing = rulesOutPort.getSingleOccupancySupplementResponse(
          basketResponse.getHotelId()).getPricing();

      if (occupancySupplementPricing.compareTo(BigDecimal.ZERO) != 0) {

        reservationByIdResponse.getRoomStay().getRatesPerNight().forEach(ratePerNight -> {

          var actualRate = ratePerNight.getPricePerNight();
          var reservationDetailsFromBasket = basketResponse.getItems().stream()
              .filter(item -> reservationByIdResponse.getReservationId().equals(item.getSourceId()))
              .findFirst()
              .orElse(null);
          var hasOccSupplement = Optional.ofNullable(reservationDetailsFromBasket)
              .map(BasketItemResponse::getHasOccupancySup)
              .orElse(false);
          if (adults > reservationByIdResponse.getRoomStay().getAdultsNumber()
              && Boolean.FALSE.equals(hasOccSupplement) && Objects.nonNull(reservationDetailsFromBasket)) {
            ratePerNight.setPricePerNight(actualRate.add(occupancySupplementPricing));
            updateOccupancySupplement(basketResponse, reservationDetailsFromBasket, true);
          } else if (adults < reservationByIdResponse.getRoomStay().getAdultsNumber()
              && Boolean.TRUE.equals(hasOccSupplement)) {
            if (Boolean.TRUE.equals(isOta)) {
              updateOccupancySupplement(basketResponse, reservationDetailsFromBasket, true);
            } else {
              ratePerNight.setPricePerNight(actualRate.subtract(occupancySupplementPricing));
              updateOccupancySupplement(basketResponse, reservationDetailsFromBasket, false);
            }
          }
        });
      }
    }
  }

  private void updateOccupancySupplement(BasketResponse targetBasket,
                                                      BasketResponse temporaryBasket,
                                                      List<BasketItemResponse> initialItems,
                                                      Map linkBetweenReservationIds) {

    var itemsWithOccSupChanged = new ArrayList<BasketItemResponse>();
    initialItems.stream().forEach(item -> {
      var tempBasketItem = temporaryBasket.getItems()
          .stream().filter(tempItem -> tempItem.getSourceId().equals(linkBetweenReservationIds.get(item.getSourceId())))
          .findFirst();
      if (tempBasketItem.isPresent()) {
        var finalBasketItemOccSup = tempBasketItem.get().getHasOccupancySup() == null
            ? Boolean.FALSE : tempBasketItem.get().getHasOccupancySup();
        var initialBasketItemOccSup =  item.getHasOccupancySup() == null
            ? Boolean.FALSE : item.getHasOccupancySup();
        if (!initialBasketItemOccSup.equals(finalBasketItemOccSup)) {
          item.setHasOccupancySup(finalBasketItemOccSup);
          itemsWithOccSupChanged.add(item);
        }
      }
    });

    if (itemsWithOccSupChanged.isEmpty()) {
      return;
    }

    var newEtag = basketOutPort.updateOccupancySupplementFlag(targetBasket.getReference(),
        itemsWithOccSupChanged, targetBasket.getETag());
    targetBasket.setETag(newEtag);
  }

  private void updateOccupancySupplement(BasketResponse targetBasket,
      BasketItemResponse reservationDetailsFromBasket,
      boolean newOccupancySuppFlag) {

    var newEtag =
        basketOutPort.updateOccupancySupplementFlag(targetBasket.getReference(), reservationDetailsFromBasket,
            newOccupancySuppFlag, targetBasket.getETag());
    targetBasket.setETag(newEtag);
  }

  @Override
  public UpdateReservationsRequest editRoomSingleCall(
          UpdateReservationsRequest request,
          String originalBasketRef,
          BookingChannel bookingChannel,
          Boolean isNonRefundable,
          Boolean isOta) {

    BasketResponse basketResponse = basketOutPort.getBasketById(originalBasketRef);

    return getEditRoomUpdateRequest(
        request, basketResponse, originalBasketRef,
        bookingChannel, isNonRefundable, isOta);
  }

  private void updatePackagesToResetMeals(
      UpdateReservationsRequest updateReservationRequest,
      BasketResponse tempBasket) {

    // edit room request will always have only one reservation id that's why we take it using index 0
    var tempReservationToBeUpdated = updateReservationRequest.getReservations().get(0);

    // get the previous packages from temporary basket
    var previousTempPackageSelections = getReservationsPackagesByBasketRef(tempBasket.getHotelId(),
        tempBasket.getReference(), false);
    var previousPackages = getOptionalPackageForReservation(previousTempPackageSelections,
        tempReservationToBeUpdated.getReservationId());
    var previousRoomSelectionsElement = getRoomSelectionsElement(
        tempReservationToBeUpdated.getReservationId(),
        previousPackages);

    if (!previousRoomSelectionsElement.getPackagesSelection().isEmpty()
        && previousRoomSelectionsElement.getPackagesSelection() != null) {
      // build the request object for update packages
      var updatePackagesByReservation = UpdateReservationPackagesByIdRequest.builder()
          .hotelId(tempBasket.getHotelId())
          .basketReference(tempBasket.getReference())
          .arrival(tempReservationToBeUpdated.getRoomStay().getArrivalDate())
          .departure(tempReservationToBeUpdated.getRoomStay().getDepartureDate())
          .previousRoomsSelections(new LinkedList<>())
          .roomsSelections(new LinkedList<>())
          .build();
      updatePackagesByReservation.getPreviousRoomsSelections().add(previousRoomSelectionsElement);

      // we have to keep only the donation packages in the actual list
      var donationPackageIds = Set.of(ZCHRY, CHRTY, ZCHR10, ZCHR11, ZCHR12, ZCHR13);

      var packageSelectionsWithoutMeals = previousRoomSelectionsElement.getPackagesSelection()
          .stream()
          .filter(packagesSelection -> donationPackageIds.stream().anyMatch(packagesSelection.getId()::contains))
          .toList();

      updatePackagesByReservation.getRoomsSelections().add(RoomsSelectionsByReservationId.builder()
          .reservationId(tempReservationToBeUpdated.getReservationId())
          .packagesSelection(packageSelectionsWithoutMeals)
          .build());

      updateReservationPackagesById(updatePackagesByReservation, true);
    }
  }

  private boolean isTokenMandatory(final BookingChannel bookingChannel) {
    return Boolean.TRUE.equals(bookingChannel.isBb());
  }

  private boolean getIsBookingCancellable(final String hotelId,
      final String basketRef,
      final List<String> initialOriginalRsvIds,
      final List<String> originalRsvIdsToBeDeleted) {

    final var userDateTime = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssXXX").format(new Date());

    var absoluteDeadlineForRsvIds = new HashSet<>(initialOriginalRsvIds);
    absoluteDeadlineForRsvIds.removeAll(originalRsvIdsToBeDeleted);

    var cancelInformationResponse = hotelReservationOhipOutPort
        .getCancelInformation(hotelId, absoluteDeadlineForRsvIds, userDateTime);
    log.info("CancelInformationResponse={} for basketReference={} for reservationIds={}",
        cancelInformationResponse, basketRef, absoluteDeadlineForRsvIds);

    return cancelInformationResponse.getIsCancellable();
  }

  private boolean isBookingNonRefundable(String hotelId, String basketRef, String token,
                                         BookingChannel bookingChannel,
                                         ReservationByBasketRefResponse originalReservations) {
    var userDateTime = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssXXX").format(new Date());

    var manageBookingResponse = manageBookingInPort.getManageBookingInformation(hotelId,
        basketRef,
        userDateTime,
        token,
        bookingChannel, isTokenMandatory(bookingChannel), originalReservations);

    return Boolean.FALSE.equals(manageBookingResponse.getIsCancellable())
        && Boolean.TRUE.equals(manageBookingResponse.getIsAmendable());
  }

  private BusinessItemsRequest removeBookingAllowances(String basketRef, List<String> reservationIds, String hotelId,
                                     boolean deleteRoutingsFirst) {
    var bookingAllowances = getBookingAllowances(basketRef);

    return removeBookingAllowancesIfPresentV2(bookingAllowances, reservationIds, hotelId, deleteRoutingsFirst);
    
  }

  private void copyBookingAllowances(BasketResponse originalBasket, BasketResponse copyBasket,
                                     boolean deleteRoutingsFirst) {
    var bookingAllowances = getBookingAllowances(originalBasket);

    copyBookingAllowancesIfPresent(bookingAllowances, copyBasket, deleteRoutingsFirst);
  }

  private void copyBookingAllowances(BasketResponse copyBasket,
                                     boolean deleteRoutingsFirst, boolean distrFlag) {
    var bookingAllowances = new BookingAllowancesResponse();
    if (Boolean.TRUE.equals(distrFlag)) {
      bookingAllowances = getBookingAllowances(copyBasket.getReference());
    } else {
      bookingAllowances = getBookingAllowances(copyBasket.getOriginalBasketId());
    }

    copyBookingAllowancesIfPresent(bookingAllowances, copyBasket, deleteRoutingsFirst);
  }

  private BusinessItemsRequest getBookingAllowanceRequestV2(
          BookingAllowancesResponse bookingAllowances, List<String> reservationIds,
          String hotelId, boolean deleteRoutingsFirst) {

    if (deleteRoutingsFirst) {
      deleteRoutingInstructions(hotelId, Set.copyOf(reservationIds));
    }
    List<BusinessAllowance> businessAllowances = new ArrayList<>();
    bookingAllowances.getBookingAllowances().forEach(bookingAllowance ->
            businessAllowances.add(BusinessAllowance.builder()
                    .allowance(bookingAllowance.getAllowance())
                    .budget(bookingAllowance.getBudget() != null ? bookingAllowance.getBudget()
                            : new BigDecimal(0))
                    .isAuthorised(true)
                    .build())
    );

    return BusinessItemsRequest.builder()
            .reservationIds(reservationIds)
            .hotelId(hotelId)
            .businessItems(BusinessItems.builder()
                    .businessAllowances(businessAllowances)
                    .businessNotes(bookingAllowances.getBusinessNotes())
                    .build())
            .build();
  }

  private BusinessItemsRequest removeBookingAllowancesIfPresentV2(
          BookingAllowancesResponse bookingAllowances, List<String> reservationIds,
          String hotelId, boolean deleteRoutingsFirst) {
    BusinessItemsRequest bookingAllowanceRequest = new BusinessItemsRequest();
    if (Objects.nonNull(bookingAllowances) && !bookingAllowances.getBookingAllowances().isEmpty()) {
      bookingAllowanceRequest = getBookingAllowanceRequestV2(
              bookingAllowances, reservationIds, hotelId, deleteRoutingsFirst);
    }
    return bookingAllowanceRequest;
  }

  private void copyBookingAllowancesIfPresent(
      BookingAllowancesResponse bookingAllowances, BasketResponse targetBasket, boolean deleteRoutingsFirst) {
    if (Objects.nonNull(bookingAllowances) && CollectionUtils.isNotEmpty(bookingAllowances.getBookingAllowances())
        && StringUtils.isNotBlank(bookingAllowances.getBusinessNotes())) {

      if (unleashWrapper.isEnabled(unleashWrapper.featureFlag().getSaveAllowancesInBasket())) {
        var eTag = basketOutPort.updateAllowances(targetBasket.getReference(),
            UpdateAllowancesRequest.builder()
                .bookingAllowances(bookingAllowances.getBookingAllowances())
                .build(),
            targetBasket.getETag());
        targetBasket.setETag(eTag);
      }

      var reservationIds = extractReservationIds(targetBasket);
      if (deleteRoutingsFirst) {
        deleteRoutingInstructions(targetBasket.getHotelId(), reservationIds);
      }

      List<BusinessAllowance> businessAllowances = new ArrayList<>();
      bookingAllowances.getBookingAllowances().forEach(bookingAllowance ->
          businessAllowances.add(BusinessAllowance.builder()
              .allowance(bookingAllowance.getAllowance())
              .budget(bookingAllowance.getBudget() != null ? bookingAllowance.getBudget()
                  : new BigDecimal(0))
              .isAuthorised(true)
              .build())
      );

      updateBusinessItems(BusinessItemsRequest.builder()
          .reservationIds(List.copyOf(reservationIds))
          .hotelId(targetBasket.getHotelId())
          .businessItems(BusinessItems.builder()
              .businessAllowances(businessAllowances)
              .businessNotes(bookingAllowances.getBusinessNotes())
              .build())
          .build());
    }
  }

  @Override
  public TempBookingRefResponse removeRoom(String tempBookingRef, String reservationId,
                                           String token,
                                           boolean checkLastRoom, BookingChannel bookingChannel,
                                           Boolean isNonRefundable) {
    log.info("Entering removeRoom with tempBookingRef={} and reservationId={}", sanitize(tempBookingRef),
        sanitize(reservationId));

    var basket = basketOutPort.getBasketById(tempBookingRef);
    validateAuthenticationAndToken(token, basket.getOriginalBasketId());

    if (!"OPEN".equals(basket.getStatus())) {
      var exception = new GenericBadRequestException(
          ErrorCode.DIGITAL_BASKET_REMOVE_RELOAD_EXCEPTION,
          "Basket has expired, please reload session");
      ExceptionLogger.log(log, exception);
      throw exception;
    }

    if (isNull(isNonRefundable)) {
      if (isBookingNonRefundable(basket.getHotelId(), basket.getOriginalBasketId(), token,
          bookingChannel, null)) {
        var exception = new GenericBadRequestException(
            ErrorCode.DIGITAL_NULL_ROOM_REMOVAL_EXCEPTION,
            "Room removal is forbidden for non-refundable bookings");
        ExceptionLogger.log(log, exception);
        throw exception;
      }
    } else if (isNonRefundable) {
      var exception = new GenericBadRequestException(ErrorCode.DIGITAL_ROOM_REMOVAL_EXCEPTION,
          "Room removal is forbidden for non-refundable bookings");
      ExceptionLogger.log(log, exception);
      throw exception;
    }

    var basketItem = basket.getItems().stream().filter(i -> reservationId.equals(i.getSourceId()))
        .findFirst().orElseThrow(
            () -> {
              var exception = new GenericBadRequestException(
                  ErrorCode.DIGITAL_ROOM_NOT_IN_BASKET_REMOVAL,
                  "Room reservation is not part of the basket");
              ExceptionLogger.log(log, exception);
              throw exception;
            }
        );

    if (checkLastRoom && basket.getItems().size() <= 1) {
      var exception = new GenericBadRequestException(ErrorCode.DIGITAL_LAST_ROOM_REMOVAL,
          "The last room can not be removed");
      ExceptionLogger.log(log, exception);
      throw exception;
    }

    hotelReservationOhipOutPort.deleteReservation(basket.getHotelId(), basketItem.getSourceId());

    basketOutPort.removeItem(tempBookingRef, basketItem.getSourceId(),
        basket.getETag().replace("\"", ""));

    return TempBookingRefResponse.builder()
        .tempBookingRef(tempBookingRef)
        .build();
  }

  @Override
  public ReservationByBasketRefResponse getAllReservationsJustByBookingReferenceAuthenticated(
      String bookingReference, boolean isUserAuthenticated) {

    log.debug(String.format("Authenticated user entered get all reservations by booking reference"
        + "with bookingReference=%s", sanitize(bookingReference)));

    var basketOptional = basketOutPort.getBasketByReference(bookingReference);
    ReservationByBasketRefResponse reservations = null;
    BasketResponse basket = null;
    if (basketOptional.isEmpty()) {
      log.info(
          String.format("Basket with booking reference %s was not found!",
              sanitize(bookingReference)));
    } else {
      basket = basketOptional.get();
    }

    var isMigrated = false;
    if (basket == null || basket.getItems().isEmpty()) {
      if (bookingReference.matches("^(?=.*[A-Z])(?=.*[0-9])[A-Z0-9]+$")
          && !manageBookingLogic.operaUiRsv(bookingReference)) {
        var operaRes = hotelReservationOhipOutPort.getReservationsByExternalId(bookingReference);
        var channel = getChannelBasedOnSourceCode(operaRes);
        if (shouldCreateNewBasketForMigratedReservations(basket, operaRes, bookingReference,
            channel)) {
          log.info(String.format(
              "resNo = %s is an Opera booking. Proceeding with internal basket creation.",
              sanitize(bookingReference)));
          basket = createBasketForMigratedReservations(operaRes, bookingReference, channel);
          isMigrated = true;
        } else if (shouldUpdateBasketForMigratedReservations(basket, operaRes, bookingReference,
            channel)) {
          log.info(String.format(
              "resNo = %s is an Opera booking. Proceeding with updating the existing basket.",
              sanitize(bookingReference)));
          basket = basketOutPort.addReservationsToBasket(basket.getReference(),
              basket.getETag().replaceAll("[\"]", " ").trim(),
              (ReservationUtils.toOhipReservation(operaRes)), true,
              false);

          if (shouldSaveChargesForMigratedReservations(basket)) {
            getAndSaveCharges(basket.getHotelId(),
                basket.getItems().stream().map(BasketItemResponse::getSourceId)
                    .collect(Collectors.toSet()));
          }
          isMigrated = true;
        } else {
          var exception = new ReservationNotFoundException(
              ErrorCode.DIGITAL_RESERVATION_EXTIDS_EXCEPTION,
              String.format("Reservation with external id %s was not found ",
                  sanitize(bookingReference)));
          ExceptionLogger.log(log, exception);
          throw exception;
        }
      } else if (manageBookingLogic.operaUiRsv(bookingReference)) {
        if (unleashWrapper.isEnabled(
            unleashWrapper.featureFlag().getCcuiSearchByOperaConfirmation())) {

          bookingReference = manageBookingLogic.replaceHotelId(bookingReference);

          final var cdhSearchBookingsResponse =
              cdhSearchBookingInPort.searchBookingsFromCdh(CdhSearchBookingsRequest.builder()
                  .bookingReference(bookingReference).bookingsDatabaseSearch(true).pageNumber(1)
                  .pageSize(10)
                  .build());
          List<Rooms> rooms = cdhSearchBookingsResponse.getResults().stream()
              .map(CdhResults::getRooms).toList().get(0);
          var reservationId = rooms.stream().map(Rooms::getReservationId).toList().get(0);
          var hotelId = cdhSearchBookingsResponse.getResults().stream()
              .map(CdhResults::getHotelId)
              .toList().get(0);
          var operaResResId = hotelReservationOhipOutPort.getReservationsByReservationId(
              reservationId,
              hotelId);
          var operaResDetails = hotelReservationOhipOutPort.getReservationsByIds(hotelId,
              List.of(reservationId), false, true);
          if (shouldCreateNewBasketForMigratedReservations(basket, operaResResId,
              bookingReference)) {
            log.info(String.format(
                "resNo = %s is an Opera booking. Proceeding with internal basket creation.",
                sanitize(bookingReference)));
            basket = manageBookingLogic.createBasketForOperaUiCreatedReservations(hotelId,
                bookingReference,
                operaResDetails,
                false);

            hotelReservationOhipOutPort.updateReservationExternalReference(hotelId,
                List.of(reservationId),
                basket.getBookingReference());
            isMigrated = true;
          } else {
            var exception = new ReservationNotFoundException(
                ErrorCode.DIGITAL_RESERVATION_CONF_EXCEPTION,
                String.format("Reservation with Opera Confirmation Number %s was not found ",
                    sanitize(bookingReference)));
            ExceptionLogger.log(log, exception);
            throw exception;
          }
        } else {
          throw new ReservationNotFoundException(
              ErrorCode.OPERA_RESERVATION_ID_EXCEPTION,
              String.format("Reservation with Opera Confirmation Number %s was not found ",
                  sanitize(bookingReference)));
        }
      }
    }
    reservations = getReservationByBasketRefResponse(bookingReference, reservations, basket,
        isMigrated);

    var customerAccountId = Optional.ofNullable(reservations)
        .map(ReservationByBasketRefResponse::getReservationByIdList)
        .filter(CollectionUtils::isNotEmpty)
        .map(reservationByIdResponseList -> reservationByIdResponseList.get(0)
            .getUserDefinedFields())
        .map(UserDefinedFields::getCharacterUDFs)
        .filter(CollectionUtils::isNotEmpty)
        .flatMap(characterUDFs -> characterUDFs.stream()
            .filter(
                udf -> UserDefinedFieldsConstants.USER_ACCOUNT_ID_UDFC_35.equals(udf.getName()))
            .findFirst())
        .map(CharacterUDFs::getValue)
        .orElse(null);

    if (isUserAuthenticated) {
      checkPiUserIsAuthorizedToAccessReservations(customerAccountId);
    }

    return reservations;
  }

  private ReservationByBasketRefResponse getReservationByBasketRefResponse(String bookingReference,
      ReservationByBasketRefResponse reservations,
      BasketResponse basket, boolean isMigrated) {
    if (basket != null && basket.getItems() != null) {

      var reservationsIds = basket.getItems().stream().map(BasketItemResponse::getSourceId)
          .toList();
      reservations = hotelReservationOhipOutPort.getReservationsByIds(basket.getHotelId(),
          reservationsIds, false);
      reservations.setHotelId(basket.getHotelId());
      for (int i = 0; i < reservationsIds.size(); i++) {
        reservations.getReservationByIdList().get(i).setReservationId(reservationsIds.get(i));
      }
      reservations.setBasketReference(basket.getReference());

      if (!isMigrated && shouldSaveChargesForExistingReservations(basket, reservations)) {
        getAndSaveCharges(basket.getHotelId(),
            basket.getItems().stream().map(BasketItemResponse::getSourceId)
                .collect(Collectors.toSet()));
      }
    }
    return reservations;
  }

  private ReservationByBasketRefResponse getReservationByBasketRefResponse(BasketResponse basket,
      Boolean priceBreakdownNeeded, Boolean rateInfoNeeded, List<String> reservationsIds) {
    var reservations = hotelReservationOhipOutPort.getReservationsByIds(basket.getHotelId(),
        reservationsIds, priceBreakdownNeeded, false, rateInfoNeeded);

    reservations.setHotelId(basket.getHotelId());
    reservations.setChannel(basket.getChannel());
    for (int i = 0; i < reservationsIds.size(); i++) {
      reservations.getReservationByIdList().get(i).setReservationId(reservationsIds.get(i));
    }

    if (reservations.getReservationByIdList() != null && !reservations.getReservationByIdList().isEmpty()
        && reservations.getReservationByIdList().get(0).getPaymentCard() != null) {
      var paymentOption = getPaymentOptionForFolioView(
                reservations.getReservationByIdList().get(0).getPaymentCard().getPaymentMethod(),
                reservations.getReservationByIdList().get(0).getPaymentCard().getFolioView());
      reservations.setPaymentOption(paymentOption);
    }

    reservations.setBookingReference(basket.getBookingReference());
    reservations.setBasketReference(basket.getReference());
    reservations.setHasCityTax(hasCityTaxIncluded(reservations.getReservationByIdList()));
    reservations.setBasketStatus(basket.getStatus());
    reservations.setIdContext(basket.getIdContext());
    reservations.setPromoKind(basket.getPromoKind());
    reservations.setPromotionCode(basket.getPromotionCode());
    boolean upsellsEnabled = !reservationsIds.isEmpty()
        && hasUpsellsForCiol(basket, reservations.getReservationByIdList());
    reservations.setUpsellsAddonsEnabled(upsellsEnabled);
    return reservations;
  }

  public String getPaymentOptionForFolioView(String paymentMethod, Integer folioView) {
    if (paymentMethod == null) {
      return PaymentOptionFolioViewEnum.CC.name();
    }
    if ((BU.equalsIgnoreCase(paymentMethod) || BD.equalsIgnoreCase(paymentMethod)) && folioView != null) {
      switch (folioView) {
        case FOLIO_VIEW_CP:
          return PaymentOptionFolioViewEnum.PIBA_CP.name();
        case FOLIO_VIEW_CNP:
          return PaymentOptionFolioViewEnum.PIBA_CNP.name();
        default:
          break;
      }
    }
    return PaymentOptionFolioViewEnum.CC.name();
  }

  private void checkPiUserIsAuthorizedToAccessReservations(String customerAccountId) {
    authenticatedUserService.getCurrentUserAccount()
        .filter(currentUser -> currentUser.getCustomerId() != null
            && !Strings.CI.equalsAny(currentUser.getCustomerId(), customerAccountId))
        .ifPresent(currentUser -> {
          throw new AccessDeniedException("Forbidden");
        });
  }

  @Override
  public DepositFoliosResponse getGeneratedDepositFolios(String hotelId,
                                                         Set<String> reservationIds) {
    return hotelReservationOhipOutPort.getGeneratedDepositFolios(hotelId, reservationIds);
  }

  @Override
  public ReservationByBasketRefResponse amendDistribution(
      String basketReference,
      ReservationRequest amendDistributionRequest,
      UpdatedReservationsDistribution updateReservationsRequest,
      UpdateReservationPackagesByIdRequest reservationPackagesRequest,
      BookerDetailsCnp bookerDetails) {
    log.debug("Entering amendDistribution with basketReference={}, amendDistributionRequest={}, "
            + "updateReservationsRequest={} and reservationPackagesRequest={}",
        sanitize(basketReference), amendDistributionRequest, updateReservationsRequest,
        reservationPackagesRequest);

    amendDistributionLogicInPort.validateDistributionIataNumber(amendDistributionRequest);

    if (Boolean.TRUE.equals(amendDistributionRequest.getBookingChannel().isDistr())) {
      return amendDistributionSingleCall(
              basketReference,
              amendDistributionRequest,
              updateReservationsRequest,
              reservationPackagesRequest,
              bookerDetails);
    }

    boolean isRoomRatePlanCode = true;

    if (reservationPackagesRequest.getRoomsSelections().get(0).getPackagesSelection() == null) {
      isRoomRatePlanCode = false;
    }

    var basketResponse = amendDistributionLogicInPort.validateBasketByReference(
        basketReference);

    amendDistributionLogicInPort.validateDistributionReservations(amendDistributionRequest,
        basketResponse.getHotelId());

    var copyBookingResponse = createTemporaryBasket(basketReference, amendDistributionRequest);
    var originalReservations = getAllReservationsJustByBasketReference(basketReference,
        false);

    boolean bookingNonRefundable = isBookingNonRefundable(basketResponse.getHotelId(),
        basketResponse.getReference(),
        amendDistributionRequest.getToken(), amendDistributionRequest.getBookingChannel(),
        originalReservations);

    removeRoomDistribution(amendDistributionRequest, copyBookingResponse.getCopyBasketReference(),
        bookingNonRefundable);

    // var originalReservations = getAllReservationsJustByBasketReference(basketReference);
    var arrivalDate = originalReservations.getReservationByIdList().get(0).getRoomStay()
        .getArrivalDate();
    var departureDate = originalReservations.getReservationByIdList().get(0).getRoomStay()
        .getDepartureDate();

    updateStayDatesDistribution(amendDistributionRequest,
        copyBookingResponse.getCopyBasketReference(),
        arrivalDate, departureDate, bookingNonRefundable);

    arrivalDate = amendDistributionRequest.getReservations().get(0).getRoomRates().getStartDate();
    departureDate = amendDistributionRequest.getReservations().get(0).getRoomRates().getEndDate();

    editRoomDistribution(amendDistributionRequest, updateReservationsRequest,
        copyBookingResponse.getCopyBasketReference(), originalReservations, bookingNonRefundable);

    List<String> newIds = addNewRoomDistribution(amendDistributionRequest,
        copyBookingResponse.getCopyBasketReference(), originalReservations);

    if (isRoomRatePlanCode) {
      updatePackagesDistribution(reservationPackagesRequest,
          copyBookingResponse.getCopyBasketReference(),
          originalReservations, arrivalDate, departureDate, newIds);
    }

    updateReservationBooker(getReservationIds(basketResponse), basketResponse.getHotelId(),
        bookerDetails);

    List<SpecialRequests> amendSpecialRequests = getSpecialRequests(
            amendDistributionRequest, basketResponse, newIds);

    if (CollectionUtils.isNotEmpty(amendSpecialRequests)) {
      amendSpecialRequests.forEach(specialReq -> {
        log.info("SpecialRequests for the reservationId={} with SpecialRequests={}",
            specialReq.getReservationIds(),
            specialReq.getSpecialRequests());
        updateReservationSpecialRequests(specialReq);
      });
    }

    if (isNull(basketResponse.getPaymentOption())) {
      basketResponse.setPaymentOption(PAY_ON_ARRIVAL);
    }
    ReservationByBasketRefResponse confirmAmendResponse = confirmAmend(
        new ConfirmAmendRequest(basketReference, copyBookingResponse.getCopyBasketReference(),
            amendDistributionRequest.getToken(), amendDistributionRequest.getBookingChannel(),
            amendDistributionRequest.getSendEmailConfirmation(),
            amendDistributionRequest.getSendEmailInvoice(),
            basketResponse.getPaymentOption().toString(), "", ""), isRoomRatePlanCode);

    // delete temporary reservations from opera and from basket and delete copied basket
    reservationCleanUp.cleanupTempBasket(copyBookingResponse.getCopyBasketReference());

    return confirmAmendResponse;
  }

  public ReservationByBasketRefResponse amendDistributionSingleCall(
          String basketReference,
          ReservationRequest amendDistributionRequest,
          UpdatedReservationsDistribution updateReservationsRequest,
          UpdateReservationPackagesByIdRequest reservationPackagesRequest,
          BookerDetailsCnp bookerDetails) {
    log.debug("Entering amendDistributionSingleCall with basketReference={}, amendDistributionRequest={}, "
                    + "updateReservationsRequest={} and reservationPackagesRequest={}",
            sanitize(basketReference), amendDistributionRequest, updateReservationsRequest,
            reservationPackagesRequest);

    boolean isRoomRatePlanCode = true;

    if (reservationPackagesRequest.getRoomsSelections().get(0).getPackagesSelection() == null) {
      isRoomRatePlanCode = false;
    }

    var basketResponse = amendDistributionLogicInPort.validateBasketByReference(
            basketReference);

    amendDistributionLogicInPort.validateDistributionReservations(amendDistributionRequest,
            basketResponse.getHotelId());

    var originalReservations = getAllReservationsJustByBasketReference(basketReference,
            false);

    boolean bookingNonRefundable = isBookingNonRefundable(basketResponse.getHotelId(),
            basketResponse.getReference(),
            amendDistributionRequest.getToken(), amendDistributionRequest.getBookingChannel(),
            originalReservations);

    BasketResponse originalBasket  = basketOutPort.getBasketById(basketReference);

    var hotelPaymentMethodInfo =
        contentOutPort.getHotelPaymentInformation(originalBasket.getHotelId(),
            LANGUAGE_EN,
            COUNTRY_CODE_GB.toLowerCase());

    var arrivalDate = amendDistributionRequest.getReservations().get(0).getRoomRates().getStartDate();
    var departureDate = amendDistributionRequest.getReservations().get(0).getRoomRates().getEndDate();

    // remove room
    removeRoomDistributionSingleCall(
        amendDistributionRequest, basketResponse, originalReservations, hotelPaymentMethodInfo);

    AmendDistributionSingleCallRequest amendDistributionSingleCallRequest = new AmendDistributionSingleCallRequest();

    // stay date
    var originalArrivalDate = originalReservations.getReservationByIdList().stream().findFirst()
        .map(ReservationByIdResponse::getRoomStay).map(RoomStayByIdResponse::getArrivalDate).orElse(arrivalDate);
    var originalDepartureDate = originalReservations.getReservationByIdList().stream().findFirst()
        .map(ReservationByIdResponse::getRoomStay).map(RoomStayByIdResponse::getDepartureDate).orElse(departureDate);
    UpdateReservationsRequest stayDateUpdateRequestForSingleCall = updateStayDatesDistributionSingleCall(
            amendDistributionRequest,
            basketReference,
            originalArrivalDate, originalDepartureDate, bookingNonRefundable);
    amendDistributionSingleCallRequest.setStayDateUpdateRequest(stayDateUpdateRequestForSingleCall);

    // booking allowances
    BusinessItemsRequest bookingAllowancesRequest = null;
    if (stayDateUpdateRequestForSingleCall != null) {
      List<UpdateReservationRequest> reservations = stayDateUpdateRequestForSingleCall.getReservations();
      var reservationIdList = reservations.stream()
              .map(UpdateReservationRequest::getReservationId).toList();
      bookingAllowancesRequest = bookingAllowancesSingleCall(
              reservationIdList, basketReference, basketResponse.getHotelId());
    }
    amendDistributionSingleCallRequest.setBookingAllowancesRequest(bookingAllowancesRequest);

    // add new room
    List<String> newIds = addNewRoomDistribution(amendDistributionRequest,
            basketReference, originalReservations);

    var onHoldResToConfirm = new HashSet<>(newIds);
    var confirmAmendRequest = ConfirmAmendRequest.builder()
            .bookingChannel(amendDistributionRequest.getBookingChannel()).build();
    var digitalPaymentMethod = getDigitalPaymentMethod(confirmAmendRequest, originalReservations,
            hotelPaymentMethodInfo);

    if (!onHoldResToConfirm.isEmpty()) {
      confirmAddedReservations(originalBasket, onHoldResToConfirm, originalReservations,
              confirmAmendRequest, digitalPaymentMethod);
    }

    // edit room
    List<UpdateReservationsRequest> editRoomRequestForSingleCall = editRoomDistributionSingleCall(
        amendDistributionRequest, updateReservationsRequest,
        basketReference, originalReservations, bookingNonRefundable);
    amendDistributionSingleCallRequest.setEditRoomRequest(editRoomRequestForSingleCall);

    // update package
    UpdateReservationPackagesByIdRequest updateReservationPackagesByIdRequest = null;
    if (isRoomRatePlanCode) {
      updateReservationPackagesByIdRequest = updatePackagesDistributionSingleCall(reservationPackagesRequest,
          basketReference,
          originalReservations, arrivalDate, departureDate, newIds);
    }
    amendDistributionSingleCallRequest.setUpdateReservationPackagesByIdRequest(updateReservationPackagesByIdRequest);

    BasketResponse newBasket  = basketOutPort.getBasketById(basketReference);
    List<String> rsvIds = getReservationIds(newBasket);

    // update booker details
    amendDistributionSingleCallRequest.setBookerDetailsCnpRequest(
        bookerDetails == null ? null :
            BookerDetailsCnpRequest.builder()
            .reservationIds(rsvIds)
            .hotelId(basketResponse.getHotelId())
            .booker(bookerDetails)
            .build());
    //add booker details for new rooms
    if (bookerDetails == null && !newIds.isEmpty() && getBooker(originalReservations) != null) {
      amendDistributionSingleCallRequest.setBookerDetailsCnpRequest(
              BookerDetailsCnpRequest.builder()
                      .reservationIds(rsvIds)
                      .hotelId(basketResponse.getHotelId())
                      .booker(copyBooker(originalReservations))
                      .build()
      );
    }
    // special request
    List<SpecialRequests> amendSpecialRequests = getSpecialRequests(
            amendDistributionRequest, basketResponse, newIds);

    if (CollectionUtils.isNotEmpty(amendSpecialRequests)) {
      amendDistributionSingleCallRequest.setSpecialRequests(amendSpecialRequests);
    }

    if (isNull(basketResponse.getPaymentOption())) {
      basketResponse.setPaymentOption(PAY_ON_ARRIVAL);
    }
    hotelReservationOhipOutPort.confirmAmendForSingleCall(amendDistributionSingleCallRequest);

    // email triggered
    confirmAmendRequest.setOriginalBookingRef(basketReference);
    EmailRequest emailRequest = emailRequestBuilder(confirmAmendRequest, originalReservations, null);
    basketOutPort.triggerEmailConfirmation(emailRequest);

    var result = getAllReservationsJustByBasketReference(basketReference, false);
    result.setReservationByIdList(
        result.getReservationByIdList().stream().filter(reservationByIdResponse ->
            !reservationByIdResponse.getReservationStatus()
                .equalsIgnoreCase(CANCELLED_STATUS)).toList()
    );

    return result;
  }

  private ReservationBooker getBooker(ReservationByBasketRefResponse originalReservations) {
    if (originalReservations != null && originalReservations.getReservationByIdList() != null) {
      return originalReservations.getReservationByIdList().stream()
              .map(ReservationByIdResponse::getReservationBooker)
              .filter(Objects::nonNull)
              .findFirst()
              .orElse(null);
    }
    return null;
  }

  private BookerDetailsCnp copyBooker(ReservationByBasketRefResponse originalReservations) {
    ReservationBooker originalReservationBooker = getBooker(originalReservations);

    BookerDetailsCnp booker = new BookerDetailsCnp();
    if (originalReservationBooker != null) {
      booker.setTitle(originalReservationBooker.getTitle());
      booker.setFirstName(originalReservationBooker.getFirstName());
      booker.setLastName(originalReservationBooker.getLastName());
      booker.setMobile(originalReservationBooker.getMobile());
      booker.setLandline(originalReservationBooker.getLandline());
      booker.setEmailAddress(originalReservationBooker.getEmail());

      ReservationBookerAddress originalReservationAddress = originalReservationBooker.getAddress();
      BookerAddressCnp bookerAddressCnp = new BookerAddressCnp();
      if (originalReservationAddress != null) {
        booker.setCompanyName(originalReservationAddress.getCompanyName());
        bookerAddressCnp.setAddressLine1(originalReservationAddress.getAddressLine1());
        bookerAddressCnp.setAddressLine2(originalReservationAddress.getAddressLine2());
        bookerAddressCnp.setAddressLine3(originalReservationAddress.getAddressLine3());
        bookerAddressCnp.setAddressLine4(originalReservationAddress.getAddressLine4());
        bookerAddressCnp.setPostalCode(originalReservationAddress.getPostalCode());

        booker.setAddress(bookerAddressCnp);
      }
      return booker;
    }
    return null;
  }

  private List<SpecialRequests> getSpecialRequests(
          ReservationRequest amendDistributionRequest, BasketResponse basketResponse, List<String> newIds
  ) {
    var bookingNotes =
            (Objects.nonNull(amendDistributionRequest.getBookingNotes()) && CollectionUtils.isNotEmpty(
                    amendDistributionRequest.getBookingNotes()))
                    ? amendDistributionRequest.getBookingNotes() : null;

    // update special requests
    var extractExistingSpecialRequestsToAmend = amendDistributionLogicInPort.extractSpecialRequests(
            amendDistributionRequest.getReservations(), getReservationIds(basketResponse),
            basketResponse.getHotelId(), bookingNotes);

    var setSpecialRequestsForNewIds = amendDistributionLogicInPort
            .setSpecialRequestsForNewAmendRooms(
                    newIds, bookingNotes, basketResponse.getHotelId());

    return Stream.concat(extractExistingSpecialRequestsToAmend.stream(),
                    setSpecialRequestsForNewIds.stream())
            .toList();
  }

  @Override
  public MemosResponse createMemo(CreateMemoRequest createMemoRequest) {
    log.info("Entering createMemo with createMemoRequest={}", createMemoRequest);
    var basketResponse = basketOutPort.getBasketById(createMemoRequest.getBasketReference());
    createMemoRequest.setHotelId(basketResponse.getHotelId());
    createMemoRequest.setReservationIds(
        basketResponse.getItems().stream().map(BasketItemResponse::getSourceId).toList());

    return hotelReservationOhipOutPort.createMemo(createMemoRequest);

  }

  @Override
  public MemosResponse getMemos(String basketReference) {
    log.info("Entering getMemos with basketReference={}", sanitize(basketReference));
    var basketResponse = basketOutPort.getBasketById(basketReference);

    return hotelReservationOhipOutPort.getMemos(basketResponse.getHotelId(),
        basketResponse.getItems().stream().map(BasketItemResponse::getSourceId)
            .collect(Collectors.toSet()));

  }

  @Override
  public void attachProfileToReservations(
      AttachReservationProfileRequest attachReservationProfileRequest) {
    hotelReservationOhipOutPort.attachProfileToReservations(attachReservationProfileRequest);
  }

  @Override
  public void deleteRoutingInstruction(String hotelId, Set<String> reservationIds) {
    hotelReservationOhipOutPort.deleteRoutingInstructions(hotelId, reservationIds);
  }

  @Override
  public ConfirmReservationResponse updateReservation(
      UpdateReservationSingleCallRequest updateReservationRequest) {
    //Update the reason for stay to NTLEI or NTBUS if we want to remove the city tax from OPERA
    if (Objects.nonNull(updateReservationRequest)
        && Objects.nonNull(updateReservationRequest.getReservationGuestDetails())) {
      updateReservationRequest.getReservationGuestDetails().setReasonForStay(
          getReasonForStay(updateReservationRequest.getReservationGuestDetails().getHotelId(), COUNTRY_GB, LANGUAGE_EN,
              updateReservationRequest.getReservationGuestDetails().getReasonForStay(),
              updateReservationRequest.getReservationPackages().getArrival()));
    }
    return hotelReservationOhipOutPort.updateReservation(updateReservationRequest);
  }

  @Override
  public ReservationProfiles createProfiles(ReservationGuestRequest reservationGuestRequest) {
    return hotelReservationOhipOutPort.createProfiles(reservationGuestRequest);
  }

  @Override
  public PreCheckInResponse addAttachmentToReservation(
      ReservationFileAttachmentRequest reservationFileAttachmentRequest) {
    return hotelReservationOhipOutPort.addAttachmentToReservation(reservationFileAttachmentRequest);
  }

  @Override
  public PreCheckInResponse saveReservationPreCheckIn(PreCheckInRequest preCheckInRequest) {
    return hotelReservationOhipOutPort.saveReservationPreCheckIn(preCheckInRequest);
  }

  /**
   * Add or remove packages to reservation by specifying in which days those packages should be
   * added/removed.
   *
   * @param updateScheduledPackage contains the hotel/reservation ids and the packages that will be
   *                               added/removed
   * @return the basket reference of the booking
   */
  @Override
  public SaveReservationResponse updateReservationPackageScheduled(
      ReservationPackagesScheduledRequest updateScheduledPackage) {

    var reservationIds = updateScheduledPackage.getReservations().stream()
        .map(RoomReservationPackagesScheduledRequest::getReservationsId)
        .toList();

    var booking = hotelReservationOhipOutPort.getReservationsByIds(
        updateScheduledPackage.getHotelId(), reservationIds, false);
    var reservationList = booking.getReservationByIdList();

    var stayDates = getStayDates(reservationList);
    var arrival = stayDates.get(BOOKING_ARRIVAL);
    var departure = stayDates.get(BOOKING_DEPARTURE);

    verifyScheduleDates(updateScheduledPackage, arrival,
        departure);

    checkIfPackageIsAvailable(updateScheduledPackage, arrival.toString(), departure.toString());

    var basketResponse = getValidBasket(booking);

    hotelReservationOhipOutPort.updateReservationPackagesScheduled(updateScheduledPackage,
        arrival.toString(), departure.toString());

    return new SaveReservationResponse(basketResponse.getReference());
  }

  @Override
  public void linkReservationToLeisureCustomer(
      LinkReservationToLeisureCustomerRequest linkReservationToLeisureCustomerRequest) {
    log.info(
        "Entered linkReservationToLeisureCustomerRequest with basketReference={}",
        linkReservationToLeisureCustomerRequest.getBasketReference());

    final BasketResponse basket = basketOutPort.getBasketById(
            linkReservationToLeisureCustomerRequest.getBasketReference());
    linkReservationToLeisureCustomerRequest.setReservationIds(extractReservationIds(basket));
    linkReservationToLeisureCustomerRequest.setHotelId(basket.getHotelId());

    hotelReservationOhipOutPort.linkReservationToLeisureCustomer(
        linkReservationToLeisureCustomerRequest);
  }

  @Override
  public void updateReservationPreferences(ReservationPreferencesRequest reservationPreferencesRequest) {
    hotelReservationOhipOutPort.updateReservationPreferences(reservationPreferencesRequest);
  }

  @Override
  public void updateReservationAlerts(UpdateReservationAlertsRequest updateReservationsRequest) {
    hotelReservationOhipOutPort.updateReservationAlerts(updateReservationsRequest);
  }

  private void setCnpReservationAlerts(String hotelId, Set<String> reservationIds) {
    var alert = new Alert();
    alert.setCode(CNP_ALERT_CODE);
    alert.setArea(CNP_ALERT_AREA);
    alert.setDescription(CNP_ALERT_DESCRIPTION);
    alert.setScreenNotification(true);
    alert.setPrinterNotification(false);

    var updateAlertsRequest = new UpdateReservationAlertsRequest();
    updateAlertsRequest.setHotelId(hotelId);
    updateAlertsRequest.setReservationIds(reservationIds);
    updateAlertsRequest.setAlerts(List.of(alert));

    hotelReservationOhipOutPort.updateReservationAlerts(updateAlertsRequest);
  }

  private BasketResponse getValidBasket(ReservationByBasketRefResponse booking) {
    var basketResponse = basketOutPort.getBasketByReference(booking.getBookingReference());

    if (basketResponse.isEmpty() || StatusEnum.PAY_PENDING.getValue()
        .equals(basketResponse.get().getStatus())) {
      var exception = new SchedulePackageException(
          ErrorCode.DIGITAL_INCORRECT_SCHEDULE_PACKAGES_EXCEPTION,
          "Valid basket cannot be retrieved");
      ExceptionLogger.log(log, exception);
      throw exception;
    }
    return basketResponse.get();
  }

  private void verifyScheduleDates(
      ReservationPackagesScheduledRequest reservationPackagesScheduledRequest, LocalDate arrival,
      LocalDate departure) {

    var invalidScheduledDates = reservationPackagesScheduledRequest.getReservations().stream()
        .filter(res -> null != res.getAddPackages())
        .flatMap(res -> res.getAddPackages().stream())
        .filter(pkg -> nonNull(pkg.getScheduledDates()))
        .flatMap(packages -> packages.getScheduledDates().stream())
        .filter(date -> !isScheduledDateValid(date, arrival, departure)).toList();

    if (!invalidScheduledDates.isEmpty()) {
      var exception = new SchedulePackageException(
          ErrorCode.DIGITAL_INCORRECT_SCHEDULE_PACKAGES_EXCEPTION,
          "The following scheduled dates are outside the reservation period: " + String.join(", ",
              invalidScheduledDates.toString()));
      ExceptionLogger.log(log, exception);
      throw exception;
    }
  }

  private List<Reservation> getRoomIndexAdultsNumberRequestMap(ReservationRequest reservationRequest) {
    List<Reservation> roomIndexAdultsNumberRequestMap = new ArrayList<>();
    if (Objects.nonNull(reservationRequest.getReservations())) {
      roomIndexAdultsNumberRequestMap.addAll(reservationRequest.getReservations());
    }
    return roomIndexAdultsNumberRequestMap;
  }

  private void otaBusinessOccupancyApplied(ReservationRequest availRequest) {
    if (Objects.nonNull(availRequest.getReservations())) {
      availRequest.getReservations().forEach(roomDetail -> {
        if (roomDetail.getAdultsNumber().equals(1)) {
          roomDetail.setAdultsNumber(2);
        }
      });
    }
  }

  private CopyBookingResponse createTemporaryBasket(String basketReference,
                                                    ReservationRequest amendDistributionRequest) {
    return copyBooking(CopyBookingRequest.builder()
        .originalBasketReference(basketReference)
        .token(amendDistributionRequest.getToken())
        .bookingChannel(amendDistributionRequest.getBookingChannel())
        .build());
  }

  private String getDigitalPaymentMethod(ConfirmAmendRequest confirmAmendRequest,
      ReservationByBasketRefResponse originalResByBasket,
      HotelPaymentInformation hotelPaymentMethodInfo) {
    var paymentCard = originalResByBasket.getReservationByIdList().get(0).getPaymentCard();
    return PaymentUtils.getDefaultPaymentMethod(paymentCard.getPaymentMethod(),
        hotelPaymentMethodInfo,
        confirmAmendRequest.getBookingChannel().getChannel());
  }

  private void updateStayDatesDistribution(ReservationRequest amendDistributionRequest,
                                           String tempBasketRef, String arrivalDate, String departureDate,
                                           boolean isNonRefundable) {
    List<AmendStayDatesRequest> amendStayDatesRequests =
        amendDistributionLogicInPort.extractStayDates(arrivalDate, departureDate, tempBasketRef,
            amendDistributionRequest);

    if (CollectionUtils.isNotEmpty(amendStayDatesRequests)) {
      log.info("Amend stay dates for distribution with new start {} and end date {}",
          amendStayDatesRequests.get(0).getNewStartDate(),
          amendStayDatesRequests.get(0).getNewEndDate());
      amendStayDatesRequests.forEach(request -> {
        request.setDistributionIATANumber(amendDistributionRequest.getDistributionIATANumber());
        request.setWbRoomTypes(amendDistributionRequest.getReservations().stream().map(
            room -> room.getRoomRates().getPmsRoomType()).toList());
      });
      //pass OTA
      amendStayDatesRequests.get(0).setIsOta(amendDistributionRequest.getIsOta());
      //don't need to call amend stay dates for each reservation since it works for basket
      amendStayDates(amendStayDatesRequests.get(0), isNonRefundable);
      //amendStayDatesRequests.forEach(this::amendStayDates);
    }
  }

  private UpdateReservationsRequest updateStayDatesDistributionSingleCall(ReservationRequest amendDistributionRequest,
                                                                          String originalBasketRef, String arrivalDate,
                                                                          String departureDate,
                                                                          boolean isNonRefundable) {
    List<AmendStayDatesRequest> amendStayDatesRequests =
            amendDistributionLogicInPort.extractStayDates(arrivalDate, departureDate, originalBasketRef,
                    amendDistributionRequest);
    List<AmendDistributionStayDatesRequest> amendDistributionStayDatesRequests = new ArrayList<>();

    if (CollectionUtils.isNotEmpty(amendStayDatesRequests)) {
      log.info("Amend stay dates for distribution with new start {} and end date {}",
              amendStayDatesRequests.get(0).getNewStartDate(),
              amendStayDatesRequests.get(0).getNewEndDate());
      amendStayDatesRequests.forEach(request -> {
        request.setDistributionIATANumber(amendDistributionRequest.getDistributionIATANumber());
        AmendDistributionStayDatesRequest amendDistributionStayDatesRequest = new AmendDistributionStayDatesRequest();
        amendDistributionStayDatesRequest.setAmendStayDatesRequest(request);
        amendDistributionStayDatesRequests.add(amendDistributionStayDatesRequest);
      });

      for (int i = 0; i < amendDistributionStayDatesRequests.size(); i++) {
        var amendDistrRequest = amendDistributionStayDatesRequests.get(i);
        var amendRequest = amendDistributionRequest.getReservations().get(i);
        amendDistrRequest.setAdults(amendRequest.getAdultsNumber());
        amendDistrRequest.setChildren(amendRequest.getChildrenNumber());
        amendDistrRequest.setExternalReference(amendRequest.getExternalReferenceId());
        amendDistrRequest.setWbRoomType(amendRequest.getRoomRates().getPmsRoomType());
        amendDistrRequest.setIsOta(amendDistributionRequest.getIsOta());
        amendDistrRequest.setCotRequired(amendRequest.getCotRequired());
      }
      //don't need to call amend stay dates for each reservation since it works for basket
      return buildRequestAmendStayDatesSingleCall(
          amendDistributionStayDatesRequests, isNonRefundable);

    }
    return null;
  }

  private void editRoomDistribution(ReservationRequest amendDistributionRequest,
                                    UpdatedReservationsDistribution updateReservationsRequest, String tempBasketRef,
                                    ReservationByBasketRefResponse originalReservations, boolean isNonRefundable) {
    List<UpdateReservationsRequest> updatedReservationRequests = new ArrayList<>(
        amendDistributionLogicInPort.extractUpdatedReservations(
            originalReservations.getReservationByIdList(),
            updateReservationsRequest.getUpdatedReservations(),
            tempBasketRef));

    if (CollectionUtils.isNotEmpty(updatedReservationRequests)) {
      log.info("Entered edit room for distribution");
      updatedReservationRequests.forEach(updatedReservation ->
          editRoom(updatedReservation, tempBasketRef, amendDistributionRequest.getBookingChannel(),
              isNonRefundable, amendDistributionRequest.getIsOta())
      );
    }
  }

  private List<UpdateReservationsRequest> editRoomDistributionSingleCall(
          ReservationRequest amendDistributionRequest,
          UpdatedReservationsDistribution updateReservationsRequest,
          String originalBasketRef,
          ReservationByBasketRefResponse originalReservations,
          boolean isNonRefundable) {
    List<UpdateReservationsRequest> updatedReservationRequests =
            amendDistributionLogicInPort.extractUpdatedReservations(
                    originalReservations.getReservationByIdList(),
                    updateReservationsRequest.getUpdatedReservations(),
                    originalBasketRef);

    List<UpdateReservationsRequest> editRoomRequestsForSingleCall = new ArrayList<>();
    if (CollectionUtils.isNotEmpty(updatedReservationRequests)) {
      log.info("Entered edit room for distribution {} ", updatedReservationRequests);
      updatedReservationRequests.forEach(updatedReservation -> {
            UpdateReservationsRequest editRoomRequest = editRoomSingleCall(
                updatedReservation,
                originalBasketRef,
                amendDistributionRequest.getBookingChannel(),
                isNonRefundable,
                amendDistributionRequest.getIsOta());
            var operaRoomType = editRoomRequest.getReservations().get(0)
                .getRoomStay().getRoomRates().get(0).getOperaRoomType();
            if (operaRoomType != null) {
              editRoomRequest.getReservations().get(0)
                  .getRoomStay().getRoomRates().get(0)
                  .setRoomType(operaRoomType);
            }
            editRoomRequestsForSingleCall.add(
                    editRoomRequest
            );
          }
      );
    }

    try {
      editRoomRequestsForSingleCall.forEach(hotelReservationOhipOutPort::amendEditRoom);
    } catch (HotelReservationOhipException e) {
      var message = String.format(
          "Couldn't edit your room due to non-availability of roomRate."
              + "Error while trying to update reservations for request updateReservationsRequest=%s",
          Arrays.toString(editRoomRequestsForSingleCall.toArray()));
      ExceptionLogger.log(log, e, message);
      throw e;
    }

    return editRoomRequestsForSingleCall;
  }

  private void updatePackagesDistribution(
      UpdateReservationPackagesByIdRequest resPackagesRequest,
      String tempBasketRef, ReservationByBasketRefResponse originalReservations,
      String arrivalDate, String departureDate, List<String> newIds) {

    if (CollectionUtils.isNotEmpty(resPackagesRequest.getRoomsSelections())) {
      UpdateReservationPackagesByIdRequest reservationPackagesRequest = setRoomSelectionsForUpdatePackage(
              resPackagesRequest, tempBasketRef, originalReservations, arrivalDate, departureDate, newIds);
      if (!reservationPackagesRequest.getRoomsSelections()
          .equals(reservationPackagesRequest.getPreviousRoomsSelections())) {
        log.info("Entered update packages for distribution with request {}",
            reservationPackagesRequest);
        updateReservationPackagesById(reservationPackagesRequest, true);
      }
    }
  }

  private UpdateReservationPackagesByIdRequest setRoomSelectionsForUpdatePackage(
          UpdateReservationPackagesByIdRequest reservationPackagesRequest,
          String originalBasketRef, ReservationByBasketRefResponse originalReservations,
          String arrivalDate, String departureDate, List<String> newIds
  ) {
    final var tempReservations = getAllReservationsJustByBasketReference(originalBasketRef,
            false, false);
    reservationPackagesRequest.setBasketReference(originalBasketRef);
    reservationPackagesRequest.setHotelId(originalReservations.getHotelId());
    reservationPackagesRequest.setArrival(arrivalDate);
    reservationPackagesRequest.setDeparture(departureDate);
    amendDistributionLogicInPort.setRoomsSelections(reservationPackagesRequest, originalBasketRef,
            tempReservations, newIds);
    return reservationPackagesRequest;
  }

  private UpdateReservationPackagesByIdRequest updatePackagesDistributionSingleCall(
          UpdateReservationPackagesByIdRequest resPackagesRequest,
          String originalBasketRef,
          ReservationByBasketRefResponse originalReservations,
          String arrivalDate,
          String departureDate,
          List<String> newIds
  ) {

    if (CollectionUtils.isNotEmpty(resPackagesRequest.getRoomsSelections())) {
      UpdateReservationPackagesByIdRequest reservationPackagesRequest = setRoomSelectionsForUpdatePackage(
              resPackagesRequest, originalBasketRef, originalReservations, arrivalDate, departureDate, newIds
      );
      if (!reservationPackagesRequest.getRoomsSelections()
              .equals(reservationPackagesRequest.getPreviousRoomsSelections())) {
        /*
        verify previous room selections and currentRoomSelections reservationIds order and size
        MUST be the same to go further,
        because the updateReservationPackages from OutPort requires reservationIds in the list
        and opera is updating the packages as follows:
        first reservation - remove first previousRoomSelections- add first roomSelections list
        */
        return buildUpdateReservationPackagesByIdSingleCall(reservationPackagesRequest);
      }
    }
    return null;
  }

  private List<String> addNewRoomDistribution(ReservationRequest amendDistributionRequest,
                                              String tempBasketRef,
                                              ReservationByBasketRefResponse reservationByBasketRefResponse) {
    List<Reservation> newRooms =
        amendDistributionLogicInPort.extractNewRooms(amendDistributionRequest.getReservations());
    reservationByBasketRefResponse.setDistributionIATANumber(amendDistributionRequest.getDistributionIATANumber());

    if (CollectionUtils.isNotEmpty(newRooms)) {
      log.info("Entered add new room for distribution with {}", newRooms);
      amendDistributionRequest.getReservations().clear();
      return newRooms.stream().map(room -> {
        room.setDistributionIATANumber(amendDistributionRequest.getDistributionIATANumber());
        amendDistributionRequest.setReservations(List.of(room));
        var addRoomResponse = addNewRoomToExistingBasket(tempBasketRef, amendDistributionRequest,
            reservationByBasketRefResponse);
        return addRoomResponse.getTempReservationId();
      }).toList();
    }

    return emptyList();
  }

  private void removeRoomDistributionSingleCall(
      ReservationRequest amendDistributionRequest,
      BasketResponse originalBasket,
      ReservationByBasketRefResponse originalResByBasket,
      HotelPaymentInformation hotelPaymentMethodInfo) {
    List<String> initialOriginalReservationIds = originalBasket.getItems().stream()
            .map(BasketItemResponse::getSourceId)
            .toList();
    List<String> removedRoomsId = amendDistributionLogicInPort.extractRemovedRoomsIds(
            amendDistributionRequest.getReservations(), initialOriginalReservationIds);

    var paymentCard = originalResByBasket.getReservationByIdList().get(0).getPaymentCard();
    var digitalPaymentMethod = PaymentUtils.getDefaultPaymentMethod(paymentCard.getPaymentMethod(),
            hotelPaymentMethodInfo,
            amendDistributionRequest.getBookingChannel().getChannel());
    HashMap<String, String> linkBetweenReservationIds = new HashMap<>();
    removedRoomsId.forEach(removedId -> linkBetweenReservationIds.put(removedId, removedId));

    if (CollectionUtils.isNotEmpty(removedRoomsId)) {
      cancelDeletedReservations(
          ConfirmAmendRequest.builder().token(amendDistributionRequest.getToken()).build(),
          originalBasket,
          initialOriginalReservationIds,
          linkBetweenReservationIds,
          removedRoomsId,
          digitalPaymentMethod
      );
    }

    removedRoomsId.forEach(roomId ->
            basketOutPort.removeItem(originalBasket.getReference(), roomId, originalBasket.getETag()));
  }

  private void removeRoomDistribution(ReservationRequest amendDistributionRequest,
                                      String tempBasketRef, boolean isNonRefundable) {
    List<String> removedRoomsId = amendDistributionLogicInPort.extractRemovedRoomsIds(
        amendDistributionRequest.getReservations(), tempBasketRef);

    if (CollectionUtils.isNotEmpty(removedRoomsId)) {
      removedRoomsId.forEach(
          roomId -> removeRoom(tempBasketRef, roomId, amendDistributionRequest.getToken(), true,
              amendDistributionRequest.getBookingChannel(), isNonRefundable));
    }
  }

  private Set<String> extractReservationIds(BasketResponse basket) {
    return basket.getItems()
        .stream()
        .map(BasketItemResponse::getSourceId)
        .collect(Collectors.toSet());
  }

  private CopyReservationsRequest buildCopyReservationsRequest(BasketResponse originalBasket,
                                                               Set<String> originalReservationIds, String reference) {
    return CopyReservationsRequest
        .builder()
        .hotelId(originalBasket.getHotelId())
        .reservationIds(originalReservationIds)
        .externalReferenceId(reference)
        .build();
  }

  private boolean validateRoomOccupancyRule(String roomType, Integer adults, Integer children,
      String channelId, String hotelId) {
    log.trace("Validating RoomOccupancyRule");

    var hotelInformation = contentOutPort.getHotelInformation(hotelId, "gb", "en");
    var brandQueryParam = HUB_BRAND.equals(hotelInformation.getBrand()) ? HUB_BRAND : null;

    List<RoomOccupancy> maxRoomOccupancyResponse;
    if (unleashWrapper.isEnabled(unleashWrapper.featureFlag().getAemSearchRules())) {
      var searchRules = contentOutPort.getSearchRules(channelId, Optional.empty());
      maxRoomOccupancyResponse = searchRules.getRoomOccupancies();
    } else {
      var rulesOutPortResponse = rulesOutPort.getMaxRoomOccupancyRule(channelId,
          brandQueryParam);
      maxRoomOccupancyResponse = rulesOutPortResponse.getRoomOccupancies()
          .stream()
          .map(maxRoomOccupancyData -> new RoomOccupancy(
              maxRoomOccupancyData.getAcceptedRoomTypes(),
              maxRoomOccupancyData.getAdultsNumber(),
              maxRoomOccupancyData.getChildrenNumber()
          ))
          .toList();
    }

    return maxRoomOccupancyResponse
        .stream()
        .filter(
            maxRoomOccupancyData -> Objects.equals(maxRoomOccupancyData.getAdultsNumber(), adults)
                && Objects.equals(maxRoomOccupancyData.getChildrenNumber(), children))
        .map(RoomOccupancy::getAcceptedRoomTypes)
        .flatMap(Collection::stream)
        .toList()
        .contains(roomType);
  }

  private boolean validateMaxRoomsRule(ReservationRequest addNewRoomRequest,
                                       BasketResponse basket) {
    String channel =
        Strings.CI.equalsAny(EMPLOYEE_RATE_PLAN, addNewRoomRequest.getRatePlanCode())
            ? EMPLOYEE_RATE_PLAN : addNewRoomRequest.getBookingChannel().getChannel();

    Integer maxRooms;
    if (unleashWrapper.isEnabled(unleashWrapper.featureFlag().getAemSearchRules())) {
      var searchRules = contentOutPort.getSearchRules(channel, Optional.empty());

      if (unleashWrapper.isEnabled(unleashWrapper.featureFlag().getMaxRoomsAmend())) {
        maxRooms = searchRules.getMaxRoomsAmend();
      } else {
        maxRooms = searchRules.getMaxRooms();
      }
    } else {
      var maxRoomsResponse = rulesOutPort.getMaxRoomsRule(channel);
      maxRooms = maxRoomsResponse.getMaxRooms();
    }

    return basket.getItems().size() + 1 <= maxRooms;
  }


  private UpdateReservationRequest createUpdateReservationRequest(String hotelId,
                                                                  String reservationId,
                                                                  String newArrivalDate, String newDepartureDate) {
    var updRoomStay = UpdateRoomStayRequest.builder()
        .arrivalDate(newArrivalDate)
        .departureDate(newDepartureDate)
        .build();

    return UpdateReservationRequest.builder()
        .hotelId(hotelId)
        .reservationId(reservationId)
        .roomStay(updRoomStay)
        .build();
  }

  private List<String> getReservationsToBeDeleted(List<String> originalReservations,
      List<String> tempReservations,
      Map<String, String> linkAmendReservations) {

    return originalReservations
        .stream()
        .filter(reservation -> !tempReservations.contains(linkAmendReservations.get(reservation)))
        .toList();
  }

  private boolean isValidOperaReservation(
      ReservationsDetailsEnhancedResponse reservationsDetailsEnhancedResponse,
      String channel, String reservationNumber) {
    if (channel.equals(PI_BOOKING_CHANNEL)) {
      var reservations = reservationsDetailsEnhancedResponse.getReservations().getReservationInfo()
          .stream()
          .filter(roomStay -> isNull(roomStay.getRoomStay().getSourceCode())
              || !BUSINESS_BOOKER.contains(roomStay.getRoomStay().getSourceCode()))
          .toList();
      if (reservations.isEmpty()) {
        return false;
      }
    }
    if (CollectionUtils.isEmpty(
        reservationsDetailsEnhancedResponse.getReservations().getReservationInfo())) {
      log.info("Could not find resNo = {} in Opera.", reservationNumber);
      return false;
    }

    return true;
  }

  private boolean isValidOperaReservation(
      ReservationByIdDetailsResponse reservationIdDetailsResponse,
      String channel, String reservationNumber) {
    if (channel.equals(PI_BOOKING_CHANNEL)) {
      var reservations = reservationIdDetailsResponse.getReservationIdDetailsResponse()
          .getReservations().getReservation()
          .stream()
          .filter(roomStay -> isNull(roomStay.getRoomStay().getRoomRates().get(0).getSourceCode())
              || !BUSINESS_BOOKER.contains(roomStay.getRoomStay().getRoomRates().get(0).getSourceCode()))
          .toList();
      if (reservations.isEmpty()) {
        return false;
      }
    }
    if (CollectionUtils.isEmpty(
        reservationIdDetailsResponse.getReservationIdDetailsResponse()
            .getReservations().getReservation())) {
      log.info("Could not find resNo = {} in Opera.", reservationNumber);
      return false;
    }

    return true;
  }

  private boolean containsMigratedReservations(ReservationsDetailsEnhancedResponse operaReservation,
                                               String reservationNumber) {
    return operaReservation.getReservations().getReservationInfo().stream()
        .map(ReservationInfo::getExternalReferences)
        .flatMap(List::stream)
        .filter(ref -> ref.getId().split("-")[0].equals(reservationNumber))
        .anyMatch(ref -> ref.getIdContext().equals(EXT_REF_CONTENT_ID));
  }


  private BasketResponse createBasketForMigratedReservations(
      ReservationsDetailsEnhancedResponse operaReservation,
      String reservationNumber, String channel) {
    String hotelId = operaReservation.getReservations().getReservationInfo().get(0).getHotelId();
    PaymentOptionEnum paymentOption = PaymentUtils.getPaymentOption((operaReservation));
    String paymentId = null;

    if (paymentOption == PaymentOptionEnum.PAY_NOW) {
      Optional<UniqueIDType> optionalUniqueIDType = operaReservation.getReservations()
          .getReservationInfo()
          .stream()
          .map(ReservationInfo::getReservationIdList)
          .flatMap(List::stream)
          .filter(id -> id.getType().equals("Reservation"))
          .findAny();

      if (optionalUniqueIDType.isPresent()) {
        var depositsResponse = getDepositsForReservationId(hotelId,
            optionalUniqueIDType.get().getId());
        var depositOpt = Optional.ofNullable(depositsResponse.getDeposits()).stream()
            .flatMap(Collection::stream).findFirst();
        if (depositOpt.isPresent()) {
          paymentId = depositOpt.get().getPaymentReference();
        }
      }
    }

    var basketStatus = BasketStatusEnum.COMPLETED;
    if (operaReservation.getReservations().getReservationInfo().get(0)
        .getReservationStatus().equals(StatusEnum.CANCELLED.name())) {
      basketStatus = BasketStatusEnum.CANCELLED;
    }
    var basket = basketOutPort.createBasket(
        hotelId, null, basketStatus,
        reservationNumber, paymentOption, paymentId, channel, null);
    basket = basketOutPort.addReservationsToBasket(basket.getReference(),
        basket.getETag().replaceAll("[\"]", " ").trim(),
        (ReservationUtils.toOhipReservation(operaReservation)), true, false);

    if (shouldSaveChargesForMigratedReservations(basket)) {
      getAndSaveCharges(basket.getHotelId(),
          basket.getItems().stream().map(BasketItemResponse::getSourceId)
              .collect(Collectors.toSet()));
    }

    return basket;
  }

  private BasketResponse createBasketForMigratedReservations(
      ReservationByIdDetailsResponse operaReservation,
      String reservationNumber, String channel) {
    String hotelId = operaReservation.getReservationIdDetailsResponse()
        .getReservations().getReservation().get(0).getHotelId();
    PaymentOptionEnum paymentOption = PaymentUtils.getPaymentOptionResId((operaReservation));
    String paymentId = null;

    if (paymentOption == PaymentOptionEnum.PAY_NOW) {
      Optional<UniqueIDType> optionalUniqueIDType = operaReservation.getReservationIdDetailsResponse()
          .getReservations()
          .getReservation()
          .stream()
          .map(ReservationDetails::getReservationIdList)
          .flatMap(List::stream)
          .filter(id -> id.getType().equals("Reservation"))
          .findAny();

      if (optionalUniqueIDType.isPresent()) {
        var depositsResponse = getDepositsForReservationId(hotelId,
            optionalUniqueIDType.get().getId());
        var depositOpt = Optional.ofNullable(depositsResponse.getDeposits()).stream()
            .flatMap(Collection::stream).findFirst();
        if (depositOpt.isPresent()) {
          paymentId = depositOpt.get().getPaymentReference();
        }
      }
    }

    var basketStatus = BasketStatusEnum.COMPLETED;
    if (operaReservation.getReservationIdDetailsResponse()
        .getReservations().getReservation().get(0)
        .getReservationStatus().equals(StatusEnum.CANCELLED.name())) {
      basketStatus = BasketStatusEnum.CANCELLED;
    }
    var basket = basketOutPort.createBasket(
        hotelId, null, basketStatus,
        reservationNumber, paymentOption, paymentId, channel, null);
    basket = basketOutPort.addReservationsToBasket(basket.getReference(),
        basket.getETag().replaceAll("[\"]", " ").trim(),
        (toOhipReservation(operaReservation)), true, false);

    if (shouldSaveChargesForMigratedReservations(basket)) {
      getAndSaveCharges(basket.getHotelId(),
          basket.getItems().stream().map(BasketItemResponse::getSourceId)
              .collect(Collectors.toSet()));
    }

    return basket;
  }

  private boolean shouldCreateNewBasketForMigratedReservations(BasketResponse basket,
                                                               ReservationByIdDetailsResponse operaReservation,
                                                               String bookingReference) {
    var channel = getChannelBasedOnSourceCode(operaReservation);
    return basket == null
        && isValidOperaReservation(operaReservation, channel, bookingReference);
  }

  private boolean shouldCreateNewBasketForMigratedReservations(BasketResponse basket,
                                                               ReservationsDetailsEnhancedResponse operaReservation,
                                                               String bookingReference,
                                                               String channel) {
    return basket == null
        && isValidOperaReservation(operaReservation, channel, bookingReference)
        && containsMigratedReservations(operaReservation, bookingReference);
  }

  private boolean shouldUpdateBasketForMigratedReservations(BasketResponse basket,
                                                            ReservationsDetailsEnhancedResponse operaReservation,
                                                            String bookingReference,
                                                            String channel) {
    return basket != null
        && basket.getItems().isEmpty()
        && isValidOperaReservation(operaReservation, channel, bookingReference)
        && containsMigratedReservations(operaReservation, bookingReference);
  }

  private String getChannelBasedOnSourceCode(ReservationByIdDetailsResponse operaResponse) {
    String channel;
    String sourceCode = ofNullable(operaResponse)
        .map(ReservationByIdDetailsResponse::getReservationIdDetailsResponse)
        .map(ReservationIdDetailsResponse::getReservations)
        .map(ReservationId::getReservation)
        .filter(CollectionUtils::isNotEmpty)
        .map(list -> list.get(0))
        .map(ReservationDetails::getRoomStay)
        .map(RoomStay::getRoomRates)
        .filter(CollectionUtils::isNotEmpty)
        .map(list -> list.get(0))
        .map(RoomRates::getSourceCode)
        .orElse(null);

    if (sourceCode != null) {
      var channelRuleResponse = rulesOutPort.getChannelBasedOnSourceId(sourceCode);
      channel = Optional.ofNullable(channelRuleResponse)
          .map(ChannelRuleResponse::getRequestDetails)
          .map(ChannelRuleRequestDetails::getChannel)
          .orElse(null);
    } else {
      var exception = new InvalidSourceCodeException(ErrorCode.DIGITAL_NOT_FOUND_SOURCE_EXCEPTION,
          "The source code for this reservation was not found.");
      ExceptionLogger.log(log, exception);
      throw exception;
    }
    return channel;
  }

  private String getChannelBasedOnSourceCode(ReservationsDetailsEnhancedResponse operaResponse) {

    var channelRuleResponse =
        rulesOutPort.getChannelBasedOnSourceId(ReservationUtils.getReservationSourceCode(operaResponse));
    return Optional.ofNullable(channelRuleResponse)
        .map(ChannelRuleResponse::getRequestDetails)
        .map(ChannelRuleRequestDetails::getChannel)
        .orElse(null);
  }

  private boolean shouldSaveChargesForExistingReservations(BasketResponse basket,
                                                           ReservationByBasketRefResponse reservations) {

    if (!PAY_NOW.equals(basket.getPaymentOption())
        || reservations.getReservationByIdList().stream()
        .anyMatch(
            res -> BigDecimal.ZERO.compareTo(res.getRateInfo().getSummary().getGuestPay()) != 0)) {
      return false;
    }

    var hasCharges = reservations.getReservationByIdList().stream()
        .map(ReservationByIdResponse::getReservationId)
        .anyMatch(this::hasCharges);
    return !hasCharges;
  }

  private boolean hasCharges(String reservationId) {
    var charges = basketOutPort.getCharges(reservationId);
    return Objects.nonNull(charges) && CollectionUtils.isNotEmpty(charges.getDepositFolios());
  }

  private boolean shouldSaveChargesForMigratedReservations(BasketResponse basket) {
    return PAY_NOW.equals(basket.getPaymentOption())
        && basket.getItems().stream().noneMatch(item -> hasCharges(item.getSourceId()))
        &&
        BigDecimal.ZERO.compareTo(getTotal(amendLogic.getAmountFromRateInfo(basket).getGuestPay()))
            == 0;
  }

  private void getAndSaveCharges(String hotelId, Set<String> reservationsIds) {
    DepositFoliosResponse depositFolios = hotelReservationOhipOutPort
        .getGeneratedDepositFolios(hotelId, reservationsIds);
    basketOutPort.saveCharges(depositFolios);
  }

  private void getAndSaveCharges(String hotelId, Set<String> reservationsIds,
      Optional<DepositFoliosResponse> optionalDepositFoliosResponse) {
    DepositFoliosResponse depositFoliosResponse;
    if (optionalDepositFoliosResponse.isEmpty()) {
      depositFoliosResponse = hotelReservationOhipOutPort.getGeneratedDepositFolios(hotelId,
          reservationsIds);
    } else {
      List<DepositFolio> depositFolios = optionalDepositFoliosResponse.get()
          .getDepositFolios()
          .stream()
          .filter(dp -> reservationsIds.contains(dp.getReservationId()))
          .toList();
      depositFoliosResponse = DepositFoliosResponse.builder()
          .depositFolios(depositFolios)
          .build();
    }
    basketOutPort.saveCharges(depositFoliosResponse);
  }

  /*
   *  We need the original basket to have all the original reservation ids, including the cancelled
   *  ones, in order to be able to retrieve the amounts for the cancelled reservations from Opera,
   *  reason why in order to keep the initial logic a copy of the original basket was created which
   *  doesn't have the reservation id/ids of the cancelled reservations
   */
  private BasketResponse copyOriginalBasket(BasketResponse originalBasket,
                                            List<String> originalReservationIdsToDelete) {
    var objectMapper = new ObjectMapper();
    var basketItems = originalBasket.getItems().stream()
        .filter(item -> !originalReservationIdsToDelete.contains(item.getSourceId()))
        .toList();

    try {
      var originalBasketClone =
          objectMapper.readValue(objectMapper.writeValueAsString(originalBasket),
              BasketResponse.class);
      originalBasketClone.setItems(basketItems);
      return originalBasketClone;
    } catch (JsonProcessingException ex) {
      var exception = new GenericReservationException(ErrorCode.DIGITAL_CLONE_EXCEPTION,
          "An error occurred while deserializing original Basket",
          ex);
      ExceptionLogger.log(log, exception);
      throw exception;
    }
  }

  private void setDefaultPaymentMethodInCancelRequest(CancelReservationRequest cancelReservationRequest,
                                                      BasketResponse basket,
                                                      ReservationByBasketRefResponse reservations) {
    var rsvWithPaymentMethod = reservations.getReservationByIdList().stream()
        .filter(rsv -> rsv.getPaymentCard() != null && rsv.getPaymentCard().getPaymentMethod() != null).findFirst()
        .orElse(null);
    cancelReservationRequest.setDefaultPaymentMethod(rsvWithPaymentMethod != null
        ? PaymentUtils.getDefaultPaymentMethod(rsvWithPaymentMethod.getPaymentCard().getPaymentMethod(),
        contentOutPort.getHotelPaymentInformation(basket.getHotelId(), LANGUAGE_EN, COUNTRY_CODE_GB.toLowerCase()),
        basket.getChannel()) : null);
  }

  private void excludePackages(UpdateReservationPackagesByIdRequest reservationPackagesByIdRequest) {
    // exclude packages, which cannot be removed, from current and prev package selections of the amend request
    var excludedPackages = new ArrayList<>(
            Optional.ofNullable(promotionProperties.getPromotionalPackages())
                    .orElse(Collections.emptyList()));
    excludedPackages.add(HSATWN);
    reservationPackagesByIdRequest.getRoomsSelections()
        .forEach(rs -> rs.setPackagesSelection(rs.getPackagesSelection().stream()
            .filter(ps -> !excludedPackages.contains(ps.getId())).toList()));
    reservationPackagesByIdRequest.getPreviousRoomsSelections()
        .forEach(prs -> prs.setPackagesSelection(prs.getPackagesSelection().stream()
            .filter(ps -> !excludedPackages.contains(ps.getId())).toList()));
  }

  private boolean isScheduledDateValid(LocalDate scheduled, LocalDate arrivalDate,
      LocalDate departureDate) {

    return (scheduled.isEqual(arrivalDate) || scheduled.isAfter(arrivalDate)) && (
        scheduled.isEqual(departureDate) || scheduled.isBefore(departureDate));

  }

  /**
   * Checks if the packages specified in the request are available for the hotel.
   *
   * @param updateReservationRequest contains the packages specified in the request
   * @param arrival                  booking start date needed to get all available packages of the
   *                                 hotel
   * @param departure                booking end date needed to get all available packages of the
   *                                 hotel
   */
  private void checkIfPackageIsAvailable(
      ReservationPackagesScheduledRequest updateReservationRequest,
      String arrival, String departure) {


    var hasPackages = updateReservationRequest.getReservations().stream().anyMatch(
        request -> !request.getAddPackages().isEmpty() || !request.getRemovePackages().isEmpty());

    if (hasPackages) {

      /*
        We don't have to check the package for each reservation with the adult/children number
        from the reservation that is why we get all available packages for a hotel with
        hardcoded adults/children
       */
      PackagesRequest packagesRequest = PackagesRequest.builder()
          .adultsNumber(1)
          .childrenNumber(0)
          .startDate(arrival)
          .endDate(departure)
          .hotelId(updateReservationRequest.getHotelId())
          .nightsNumber(
              calculateNumberOfNights(arrival, departure))
          .build();

      var availablePackages = hotelReservationOhipOutPort.getPackages(packagesRequest).getPackages()
          .getMeals().stream()
          .map(Meal::getId)
          .collect(Collectors.toSet());
      var promotionalPackages = Stream.concat(
                      Stream.of(HSATWN),
                      Optional.ofNullable(promotionProperties.getPromotionalPackages())
                              .orElse(Collections.emptyList())
                              .stream())
              .filter(Objects::nonNull)
              .collect(Collectors.toSet());
      availablePackages.addAll(promotionalPackages);

      var allInvalidPackages = updateReservationRequest.getReservations()
          .stream()
          .flatMap(res -> getInvalidPackagesForReservation(availablePackages, res).stream())
          .collect(Collectors.toSet());

      if (!allInvalidPackages.isEmpty()) {
        var exception = new SchedulePackageException(
            ErrorCode.DIGITAL_INCORRECT_SCHEDULE_PACKAGES_EXCEPTION,
            "The following packages are not available for add/remove: " + String.join(", ",
                allInvalidPackages));
        ExceptionLogger.log(log, exception);
        throw exception;
      }
    }
  }

  /**
   * Collects specified packages in the request that don't match with the hotel's available
   * packages.
   *
   * @param availablePackages  all available packages of the hotel
   * @param requestReservation object containing packages specified in the request
   * @return a set of all packages not available for the hotel
   */
  private Set<String> getInvalidPackagesForReservation(Set<String> availablePackages,
      RoomReservationPackagesScheduledRequest requestReservation) {
    var invalidPackages = requestReservation.getAddPackages().stream()
        .filter(pkg -> nonNull(pkg) && nonNull(pkg.getId()))
        .map(PackagesSelectionScheduled::getId)
        .filter(pkgId -> !availablePackages.contains(pkgId))
        .collect(Collectors.toSet());

    var invalidRemovePackages = requestReservation.getRemovePackages().stream()
        .filter(pkg -> nonNull(pkg) && nonNull(pkg.getId()))
        .map(PackagesSelection::getId)
        .filter(pkgId -> !availablePackages.contains(pkgId))
        .collect(Collectors.toSet());

    invalidPackages.addAll(invalidRemovePackages);

    return invalidPackages;
  }

  /**
   * Get the arrival/departure for a booking, for a booking all reservations have the same
   * arrival/departure, so we take the values from the first reservation.
   *
   * @param reservations a list of reservations
   * @return a map with the arrival and departure dates
   */
  private Map<String, LocalDate> getStayDates(List<ReservationByIdResponse> reservations) {
    var arrival = reservations.get(0).getRoomStay().getArrivalDate();
    var departure = reservations.get(0).getRoomStay().getDepartureDate();
    return Map.of(BOOKING_ARRIVAL, LocalDate.parse(arrival), BOOKING_DEPARTURE, LocalDate.parse(departure));
  }

  private static boolean isPiba(String paymentType) {
    return HotelReservationConstants.PIBA_UK_CARD_TYPE.equalsIgnoreCase(paymentType)
        || HotelReservationConstants.PIBA_EURO_CARD_TYPE.equalsIgnoreCase(paymentType);
  }

  /**
   * Get the reason for stay based on booking date and effective date from city tax info.
   *
   * @param hotelId            the hotel id
   * @param language           the language code
   * @param country            the country code
   * @param reasonForStayCode  the original reason for stay code
   * @param arrivalDateValue   the arrival date of the booking
   * @return a String for the reason for stay
   */
  private String getReasonForStay(String hotelId, String country, String language, String reasonForStayCode,
                                  String arrivalDateValue) {
    if (!unleashWrapper.isEnabled(unleashWrapper.featureFlag().getReleasePiCcuiCityTaxUk())) {
      return reasonForStayCode;
    }

    HotelInfoResponse hotelInfoResponse = contentOutPort.getHotelInformation(hotelId, country, language);
    if (Objects.isNull(hotelInfoResponse) || Objects.isNull(hotelInfoResponse.getCityTax())) {
      return reasonForStayCode;
    }

    var cityTaxInfo = hotelInfoResponse.getCityTax();
    if (StringUtils.isBlank(cityTaxInfo.getBookingDateFrom()) || StringUtils.isBlank(cityTaxInfo.getEffectiveFrom())
              || StringUtils.isBlank(arrivalDateValue)) {
      return reasonForStayCode;
    }

    LocalDate bookingDateFrom;
    LocalDate effectiveFrom;
    LocalDate arrivalDate;
    try {
      bookingDateFrom = LocalDate.parse(cityTaxInfo.getBookingDateFrom());
      effectiveFrom = LocalDate.parse(cityTaxInfo.getEffectiveFrom());
      arrivalDate = LocalDate.parse(arrivalDateValue);
    } catch (DateTimeParseException ex) {
      log.error("Invalid date format in city tax information for hotelId {}: {}", hotelId, ex.getMessage());
      throw new DateTimeParseException(
          "Dates must be in right format: " + ex.getParsedString(),
          ex.getParsedString(),
          ex.getErrorIndex(),
          ex);
    }

    if ((LocalDate.now().isAfter(effectiveFrom)) && arrivalDate.isBefore(bookingDateFrom)) {
      String reasonForStay = ReasonForStay.valueOf(reasonForStayCode).getCode();
      log.info("Updated the reason for stay to {} for hotelId {}", reasonForStay, hotelId);
      return reasonForStay;
    }

    return reasonForStayCode;
  }

  private String getArrivalDate(String hotelId, List<String> reservationIds) {
    var reservationsByIds = hotelReservationOhipOutPort.getReservationsByIds(hotelId, reservationIds,
        false, false, false);
    return Optional.ofNullable(reservationsByIds.getReservationByIdList())
        .orElse(Collections.emptyList())
        .stream()
        .findFirst()
        .map(ReservationByIdResponse::getRoomStay)
        .map(RoomStayByIdResponse::getArrivalDate)
        .orElse(null);
  }

  private boolean hasUpsellsForCiol(BasketResponse basket,
                                    List<ReservationByIdResponse> reservationList) {
    if (ID_CONTEXT.equalsIgnoreCase(basket.getIdContext())) {
      return false;
    }
    return reservationList.stream()
        .map(ReservationByIdResponse::getPaymentCard)
        .filter(Objects::nonNull)
        .map(ReservationPaymentCardType::getCardType)
        .noneMatch(type -> List.of(HotelReservationConstants.PIBA_UK_CARD_TYPE,
            HotelReservationConstants.PIBA_EURO_CARD_TYPE).contains(type));
  }

  @Override
  public void saveDepositFolios(DepositFoliosRequest depositFoliosRequest) {
    hotelReservationOhipOutPort.saveDepositFolios(depositFoliosRequest);
  }
}
