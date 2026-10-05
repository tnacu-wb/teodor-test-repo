package uk.co.whitbread.reservation.domain.logic;

import static java.util.Arrays.asList;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anySet;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.Mockito.RETURNS_DEEP_STUBS;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;
import static uk.co.whitbread.hotel.ohip.adapter.generated.models.basket.PromoKind.SITE_WIDE;
import static uk.co.whitbread.hotel.ohip.adapter.generated.models.basket.PromoKind.UNIQUE;
import static uk.co.whitbread.reservation.ErrorCode.DIGITAL_RESERVATION_CHARGES_EXCEPTION;
import static uk.co.whitbread.reservation.domain.logic.HotelReservationInPortImpl.BOOKING_FLOW_CLAIM;
import static uk.co.whitbread.reservation.domain.logic.HotelReservationInPortImpl.areEqual;
import static uk.co.whitbread.reservation.domain.logic.utils.BusinessAllowanceUtilsTest.buildBusinessAllowanceRuleResponse;
import static uk.co.whitbread.reservation.domain.logic.utils.BusinessAllowanceUtilsTest.buildBusinessNotesResponse;
import static uk.co.whitbread.reservation.domain.logic.utils.ManageReservationUtils.PACKAGE_GROUP;
import static uk.co.whitbread.reservation.domain.logic.utils.ManageReservationUtils.mockBasketItemsList;
import static uk.co.whitbread.reservation.domain.model.in.BookingChannel.CCUI_BOOKING_CHANNEL;
import static uk.co.whitbread.reservation.domain.model.in.BookingChannel.PI_BOOKING_CHANNEL;
import static uk.co.whitbread.reservation.domain.model.in.PaymentOption.ACCOUNT_COMPANY;
import static uk.co.whitbread.reservation.domain.model.in.PaymentOption.PAY_NOW;
import static uk.co.whitbread.reservation.domain.model.in.PaymentOption.PAY_ON_ARRIVAL;
import static uk.co.whitbread.reservation.domain.model.in.PaymentOption.RESERVE_WITHOUT_CARD;

import io.micrometer.tracing.Tracer;
import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.apache.logging.log4j.util.Strings;
import org.jetbrains.annotations.Nullable;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.Answers;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InOrder;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.oauth2.jwt.Jwt;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.basket.BasketDto.StatusEnum;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.basket.BasketError;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.basket.BasketError.TypeEnum;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.basket.PromoKind;
import uk.co.whitbread.promo.service.generated.models.promotion.PromoCodeStatus;
import uk.co.whitbread.reservation.ErrorCode;
import uk.co.whitbread.reservation.domain.constants.HotelReservationConstants;
import uk.co.whitbread.reservation.domain.exceptions.AmendErrorCode;
import uk.co.whitbread.reservation.domain.exceptions.AmendReservationException;
import uk.co.whitbread.reservation.domain.exceptions.AmendStayDateException;
import uk.co.whitbread.reservation.domain.exceptions.BasketNotFoundException;
import uk.co.whitbread.reservation.domain.exceptions.BusinessItemsUpdateException;
import uk.co.whitbread.reservation.domain.exceptions.CancelReservationException;
import uk.co.whitbread.reservation.domain.exceptions.GenericBadRequestException;
import uk.co.whitbread.reservation.domain.exceptions.GenericReservationException;
import uk.co.whitbread.reservation.domain.exceptions.HotelReservationOhipException;
import uk.co.whitbread.reservation.domain.exceptions.InvalidTokenException;
import uk.co.whitbread.reservation.domain.exceptions.PromotionException;
import uk.co.whitbread.reservation.domain.exceptions.ReservationNotFoundException;
import uk.co.whitbread.reservation.domain.exceptions.SchedulePackageException;
import uk.co.whitbread.reservation.domain.logic.utils.ManageReservationUtils;
import uk.co.whitbread.reservation.domain.logic.utils.TokenUtils;
import uk.co.whitbread.reservation.domain.model.amend.in.AmendOnHoldInterval;
import uk.co.whitbread.reservation.domain.model.amend.in.DepositFolioComputationResult;
import uk.co.whitbread.reservation.domain.model.availability.out.HotelAvailabilityByIdsV2;
import uk.co.whitbread.reservation.domain.model.availability.out.HotelAvailabilityResultV2;
import uk.co.whitbread.reservation.domain.model.availability.out.PriceInfo;
import uk.co.whitbread.reservation.domain.model.availability.out.RoomRateInfoV2;
import uk.co.whitbread.reservation.domain.model.availability.out.RoomRateV2;
import uk.co.whitbread.reservation.domain.model.availability.out.RoomTypeV2;
import uk.co.whitbread.reservation.domain.model.feature.FeatureFlag;
import uk.co.whitbread.reservation.domain.model.feature.FeatureFlag.Feature;
import uk.co.whitbread.reservation.domain.model.feature.UnleashWrapper;
import uk.co.whitbread.reservation.domain.model.in.AmendDistributionStayDatesRequest;
import uk.co.whitbread.reservation.domain.model.in.AmendStayDatesRequest;
import uk.co.whitbread.reservation.domain.model.in.AttachReservationProfileRequest;
import uk.co.whitbread.reservation.domain.model.in.BookerAddress;
import uk.co.whitbread.reservation.domain.model.in.BookerDetails;
import uk.co.whitbread.reservation.domain.model.in.BookerDetailsCnp;
import uk.co.whitbread.reservation.domain.model.in.BookingChannel;
import uk.co.whitbread.reservation.domain.model.in.BusinessAccountCnp;
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
import uk.co.whitbread.reservation.domain.model.in.CreateMemoRequest;
import uk.co.whitbread.reservation.domain.model.in.CustomerType;
import uk.co.whitbread.reservation.domain.model.in.DepositFoliosRequest;
import uk.co.whitbread.reservation.domain.model.in.EmailRequest;
import uk.co.whitbread.reservation.domain.model.in.LeadGuest;
import uk.co.whitbread.reservation.domain.model.in.LinkReservationToLeisureCustomerRequest;
import uk.co.whitbread.reservation.domain.model.in.PackagesSelection;
import uk.co.whitbread.reservation.domain.model.in.PackagesSelectionScheduled;
import uk.co.whitbread.reservation.domain.model.in.PaymentOption;
import uk.co.whitbread.reservation.domain.model.in.PersonNameType;
import uk.co.whitbread.reservation.domain.model.in.PreCheckInRequest;
import uk.co.whitbread.reservation.domain.model.in.ProfileInfo;
import uk.co.whitbread.reservation.domain.model.in.ProfileType;
import uk.co.whitbread.reservation.domain.model.in.RatePrice;
import uk.co.whitbread.reservation.domain.model.in.Reservation;
import uk.co.whitbread.reservation.domain.model.in.ReservationFileAttachmentRequest;
import uk.co.whitbread.reservation.domain.model.in.ReservationGuestRequest;
import uk.co.whitbread.reservation.domain.model.in.ReservationGuests;
import uk.co.whitbread.reservation.domain.model.in.ReservationPackagesRequest;
import uk.co.whitbread.reservation.domain.model.in.ReservationPackagesScheduledRequest;
import uk.co.whitbread.reservation.domain.model.in.ReservationRequest;
import uk.co.whitbread.reservation.domain.model.in.RoomRate;
import uk.co.whitbread.reservation.domain.model.in.RoomReservationPackagesScheduledRequest;
import uk.co.whitbread.reservation.domain.model.in.RoomsSelections;
import uk.co.whitbread.reservation.domain.model.in.RoomsSelectionsByReservationId;
import uk.co.whitbread.reservation.domain.model.in.SpecialRequests;
import uk.co.whitbread.reservation.domain.model.in.StayingGuest;
import uk.co.whitbread.reservation.domain.model.in.StayingGuestAddress;
import uk.co.whitbread.reservation.domain.model.in.StayingGuestDetails;
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
import uk.co.whitbread.reservation.domain.model.in.UpdateRoomOccupancyRequest;
import uk.co.whitbread.reservation.domain.model.in.UpdateRoomRateRequest;
import uk.co.whitbread.reservation.domain.model.in.UpdateRoomStayRequest;
import uk.co.whitbread.reservation.domain.model.in.UpdatedReservationsDistribution;
import uk.co.whitbread.reservation.domain.model.index.header.data.out.AcceptedCreditCard;
import uk.co.whitbread.reservation.domain.model.index.header.data.out.HotelPaymentInformation;
import uk.co.whitbread.reservation.domain.model.out.AccountCompanyItems;
import uk.co.whitbread.reservation.domain.model.out.AmendSummaryAmountResponse;
import uk.co.whitbread.reservation.domain.model.out.BasketItemResponse;
import uk.co.whitbread.reservation.domain.model.out.BasketResponse;
import uk.co.whitbread.reservation.domain.model.out.BookingAllowance;
import uk.co.whitbread.reservation.domain.model.out.BookingAllowancesResponse;
import uk.co.whitbread.reservation.domain.model.out.BusinessAllowanceRule;
import uk.co.whitbread.reservation.domain.model.out.BusinessAllowanceRuleResponse;
import uk.co.whitbread.reservation.domain.model.out.BusinessNote;
import uk.co.whitbread.reservation.domain.model.out.BusinessNotesResponse;
import uk.co.whitbread.reservation.domain.model.out.CancelInformationResponse;
import uk.co.whitbread.reservation.domain.model.out.CancelReservationResponse;
import uk.co.whitbread.reservation.domain.model.out.CancellationPoliciesResponse;
import uk.co.whitbread.reservation.domain.model.out.CcuiExtraItems;
import uk.co.whitbread.reservation.domain.model.out.CdhResults;
import uk.co.whitbread.reservation.domain.model.out.CdhSearchBookingsResponse;
import uk.co.whitbread.reservation.domain.model.out.ChannelRuleRequestDetails;
import uk.co.whitbread.reservation.domain.model.out.ChannelRuleResponse;
import uk.co.whitbread.reservation.domain.model.out.ConfirmReservationResponse;
import uk.co.whitbread.reservation.domain.model.out.ConfirmationCustomer;
import uk.co.whitbread.reservation.domain.model.out.ConfirmationRoomStay;
import uk.co.whitbread.reservation.domain.model.out.CopyReservationResponse;
import uk.co.whitbread.reservation.domain.model.out.CopyReservationsResponse;
import uk.co.whitbread.reservation.domain.model.out.CurrencyAmount;
import uk.co.whitbread.reservation.domain.model.out.Customer;
import uk.co.whitbread.reservation.domain.model.out.DepositFolio;
import uk.co.whitbread.reservation.domain.model.out.DepositFolioCharge;
import uk.co.whitbread.reservation.domain.model.out.DepositFoliosResponse;
import uk.co.whitbread.reservation.domain.model.out.Deposits;
import uk.co.whitbread.reservation.domain.model.out.DepositsResponse;
import uk.co.whitbread.reservation.domain.model.out.GuestAddress;
import uk.co.whitbread.reservation.domain.model.out.HotelCityTax;
import uk.co.whitbread.reservation.domain.model.out.HotelInfoResponse;
import uk.co.whitbread.reservation.domain.model.out.ManageBookingResponse;
import uk.co.whitbread.reservation.domain.model.out.MarketingPreferencesResponse;
import uk.co.whitbread.reservation.domain.model.out.MaxNightsRuleResponse;
import uk.co.whitbread.reservation.domain.model.out.MaxRoomOccupancyData;
import uk.co.whitbread.reservation.domain.model.out.MaxRoomOccupancyResponse;
import uk.co.whitbread.reservation.domain.model.out.MaxRoomsRuleResponse;
import uk.co.whitbread.reservation.domain.model.out.Meal;
import uk.co.whitbread.reservation.domain.model.out.MemosResponse;
import uk.co.whitbread.reservation.domain.model.out.Note;
import uk.co.whitbread.reservation.domain.model.out.OhipReservationCreationResponse;
import uk.co.whitbread.reservation.domain.model.out.OhipReservationResponse;
import uk.co.whitbread.reservation.domain.model.out.Packages;
import uk.co.whitbread.reservation.domain.model.out.PackagesResponse;
import uk.co.whitbread.reservation.domain.model.out.PreCheckInResponse;
import uk.co.whitbread.reservation.domain.model.out.RatePerNight;
import uk.co.whitbread.reservation.domain.model.out.RefundResponse;
import uk.co.whitbread.reservation.domain.model.out.ReservationBooker;
import uk.co.whitbread.reservation.domain.model.out.ReservationBookerAddress;
import uk.co.whitbread.reservation.domain.model.out.ReservationByBasketRefResponse;
import uk.co.whitbread.reservation.domain.model.out.ReservationByIdDetailsResponse;
import uk.co.whitbread.reservation.domain.model.out.ReservationByIdGuestsResponse;
import uk.co.whitbread.reservation.domain.model.out.ReservationByIdResponse;
import uk.co.whitbread.reservation.domain.model.out.ReservationPaymentCardType;
import uk.co.whitbread.reservation.domain.model.out.ReservationsDetailsResponse;
import uk.co.whitbread.reservation.domain.model.out.ReservationsPackagesResponse;
import uk.co.whitbread.reservation.domain.model.out.RoomStay;
import uk.co.whitbread.reservation.domain.model.out.RoomStayByIdResponse;
import uk.co.whitbread.reservation.domain.model.out.Rooms;
import uk.co.whitbread.reservation.domain.model.out.RoomsSelectionsByReservation;
import uk.co.whitbread.reservation.domain.model.out.SaveReservationResponse;
import uk.co.whitbread.reservation.domain.model.out.SingleOccupancySupplementResponse;
import uk.co.whitbread.reservation.domain.model.out.TempBookingRefResponse;
import uk.co.whitbread.reservation.domain.model.promotion.out.PromoKindResponse;
import uk.co.whitbread.reservation.domain.model.searchrules.out.RoomOccupancy;
import uk.co.whitbread.reservation.domain.model.searchrules.out.SearchRules;
import uk.co.whitbread.reservation.domain.ports.primary.AmendDistributionLogicInPort;
import uk.co.whitbread.reservation.domain.ports.primary.AmendLogicInPort;
import uk.co.whitbread.reservation.domain.ports.primary.CdhSearchBookingInPort;
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
import uk.co.whitbread.reservation.infrastructure.rest.client.basket.exceptions.BasketDigitalException;
import uk.co.whitbread.shared.auth.account.Account;
import uk.co.whitbread.shared.auth.security.AuthenticatedUserService;
import uk.co.whitbread.shared.auth.security.model.CustomJwtAuthenticationToken;
import uk.co.whitbread.shared.commons.logging.trace.ConcurrentTracer;

@ExtendWith(MockitoExtension.class)
class HotelReservationInPortTest {

  private static final String HOTEL_ID = "TESTHOTEL";
  private static final String BASKET_REF_ONHOLD_RES = "ABC-e273fbe9-71fa-4603-a9fc-8d1da94e19a9";
  private static final String BASKET_REFERENCE = "TST-16a014c5-d8a4-4418-8d15-2537660f08e9";
  private static final String BOOKING_REFERENCE = "TST1234567";
  private static final String GIVEN_NAME = "Gate";
  private static final String SURNAME = "John";
  private static final String ARRIVAL_DATE = "2022-05-05";
  private static final String DEPARTURE_DATE = "2022-05-07";
  private static final String RATE_PLAN_CODE = "FLEX";
  private static final String ROOM_TYPE = "SINGLE";
  private static final String CELL_CODE = "cellCode";
  private static final String ROOM_NUMBER = "120";
  private static final String SOURCE_CODE = "44";
  private static final String BOOKING_CHANNEL = "PI.com";
  private static final String POLICY_CODE = "code";
  private static final String CARD_NUMBER = "XXXXXXXXXXXX1103";
  private static final String CARD_TOKEN = "4216333880397891103";
  private static final String CARD_TYPE = "Va";
  private static final String REASON_NAME = "Illness";
  private static final String CALLER_NAME = "John Doe";
  private static final String MANAGER_NAME = "James Bond";
  private static final String UNIQUE_ID_TYPE = "ABCD123456";
  private static final String PAYMENT_REFERENCE = "3CPReference";
  private static final List<String> RESERVATION_IDS = asList("res1", "res2", "res3");
  private static final String BOOKING_REFERENCE_2 = "TEST123456";
  private static final String BOOKING_REFERENCE_3 = "123456";

  private static final String BOOKING_TYPE_ANON = "ANON";
  private static final String BOOKING_TYPE_EMPTY = "";
  private static final String EMAIL = "email@mydomain.com";
  private static final String DISTR_FIXED_PRICE_AUTH = "SCOPE_RATE_PRICE::WRITE";
  private static final List<String> PROMO_PACKAGE = List.of("PROMO_PACKAGE");
  private static final String PREMIER_IN_BREAKFAST = "BFADBF";
  private static final String CONTINENTAL_BREAKFAST = "BFADCT";
  private static final String MEAL_DEAL_DINNER = "MD2DIN";
  private static final String MEAL_DEAL_DINNER_BEVERAGE = "MDBEVA";
  private static final String MEAL_DEAL_BREAKFAST_FOOD = "MDBFST";
  private static final String HSATWN = "HSATWN";
  private static final String SOURCE_ID_111111 = "111111";
  private static final String SOURCE_ID_222222 = "222222";
  private static final String SOURCE_ID_333333 = "333333";
  private static final String SOURCE_ID_444444 = "444444";
  private static final String LANGUAGE_EN = "EN";
  private static final String LANGUAGE = "en";
  private static final String COUNTRY = "gb";
  private static final String HOTEL_CODE_TEST = "hotelTest";
  private static final String BASKET_STATUS_OPEN = "OPEN";
  private static final String SUBCHANNEL_WEB = "WEB";
  private static final String TEMPORARY_BASKET_REF = "tempBasketRef";
  private static final String ORIGINAL_BASKET_REF = "originalBasketRef";
   private static final String OTHER_CUSTOMER_ACCOUNT_ID = "CUST_c412cba9-ae4a-4026-8c04-4304262ebf14";
  private static final String TOKEN = "token";
  private static final String MOCKED_TOKEN_VALUE = "mocked-token-value";
  private static final String CUSTOMER_123 = "customer123";

  @Mock
  private HotelReservationOhipOutPort reservationOutPort;

  @Mock
  private HotelAvailabilityOutPort availabilityOutPort;

  @Mock
  private BasketOutPort basketOutPort;

  @Mock
  private RulesOutPort rulesOutPort;

  @Mock
  private CdhSearchBookingInPort cdhSearchBookingInPort;

  @Mock
  private AmendLogicInPort amendLogicInPort;

  @Mock
  private ContentOutPort contentOutPort;

  @Mock
  private HotelAccountOutPort hotelAccountOutPort;

  @Mock
  private AuthenticatedUserService authenticatedUserService;

  @Mock
  private BusinessBookerConfigProperties businessBookerConfigProperties;

  @Mock
  private AmendDistributionLogicInPort amendDistributionLogicInPort;

  @Mock
  private ReservationCleanup reservationCleanup;

  @Mock
  private AmendPayNowLogic amendPayNowLogic;

  @Mock
  private CompanyProperties companyProperties;

  @Mock
  private DistributionProperties distributionProperties;

  @Mock
  private PackageProperties packageProperties;

  @Mock
  private ManageBookingInPortImpl manageBookingInPort;

  @Mock
  private ManageBookingLogic manageBookingLogic;

  private HotelReservationInPortImpl reservationInPort;

  @Captor
  ArgumentCaptor<ConfirmReservationRequest> confirmReservationCaptor;

  @Captor
  ArgumentCaptor<ConfirmAmendOnReservationsRequest> confirmAmendOnReservationsCaptor;

  @Captor
  ArgumentCaptor<UpdateCancellationPoliciesRequest> updateCancellationPoliciesCaptor;

  @Spy
  private ConcurrentTracer concurrentTracer = new ConcurrentTracer(Tracer.NOOP);

  @Mock
  private UnleashWrapper<FeatureFlag> unleashWrapper;

  @Mock
  private PromotionProperties promotionProperties;

  private MockedStatic<TokenUtils> tokenUtilsMock;

  @Mock
  private PromotionOutPort promotionOutPort;

  @Captor
  private ArgumentCaptor<UpdateReasonForStayRequest> reasonForStayCaptor;

  @Captor
  private ArgumentCaptor<ReservationRequest> reservationRequestCaptor;

  static Stream<Arguments> upsellsScenarios() {
    return Stream.of(
        // idContext, cardType, expectedUpsells, reservationsIdsEmpty
        Arguments.of("3rd Party", null, false, false),
        Arguments.of("OTHER", HotelReservationConstants.PIBA_UK_CARD_TYPE, false, false),
        Arguments.of("OTHER", HotelReservationConstants.PIBA_EURO_CARD_TYPE, false, false),
        Arguments.of("OTHER", null, true, false),
        Arguments.of("OTHER", null, false, true)
    );
  }

  @BeforeEach
  void init() {
    reservationInPort = new HotelReservationInPortImpl(reservationOutPort, availabilityOutPort,
        basketOutPort, rulesOutPort,
        cdhSearchBookingInPort, contentOutPort, hotelAccountOutPort, amendLogicInPort, manageBookingInPort,
        authenticatedUserService,
        businessBookerConfigProperties, amendDistributionLogicInPort, reservationCleanup,
        amendPayNowLogic, companyProperties, distributionProperties,
        concurrentTracer, Boolean.TRUE, unleashWrapper, promotionProperties, packageProperties, manageBookingLogic,
        promotionOutPort);
    tokenUtilsMock = Mockito.mockStatic(TokenUtils.class);
  }

  @AfterEach
  void afterEach() {
    tokenUtilsMock.close();
  }

  @Test
  void testCreateReservation_success() {
    // Arrange
    when(reservationOutPort.createReservation(anyString(), any(), anyString()))
        .thenReturn(mockReservationResponse());
    when(basketOutPort.createBasket(anyString(), anyString(), any()))
        .thenReturn(ManageReservationUtils.mockBasketResponse());
    when(basketOutPort.addReservationsToBasket(any(), any(),
        any(OhipReservationResponse.class), anyBoolean(), anyBoolean(), eq(null), anyBoolean()))
        .thenReturn(ManageReservationUtils.mockBasketResponse());

    var mockedFeatureFlag = mock(FeatureFlag.class);
    when(unleashWrapper.featureFlag())
        .thenReturn(mockedFeatureFlag);
    when(unleashWrapper.isEnabled(mockedFeatureFlag.getApplyOccupancySupplement()))
        .thenReturn(false);

    // Act
    var response = reservationInPort.createReservation(createValidHotelReservationRequest());

    // Assert
    assertThat(response.getBasketReference(), is(BASKET_REFERENCE));
  }

  @Test
  void testCreateReservation_with_citytax_for_pi_success() {
    // Arrange
    when(reservationOutPort.createReservation(anyString(), any(), anyString()))
        .thenReturn(mockReservationResponse());
    when(basketOutPort.createBasket(anyString(), anyString(), any()))
        .thenReturn(ManageReservationUtils.mockBasketResponse());
    when(basketOutPort.addReservationsToBasket(any(), any(),
        any(OhipReservationResponse.class), anyBoolean(), anyBoolean(), eq(1), anyBoolean()))
        .thenReturn(ManageReservationUtils.mockBasketResponse());
    when(rulesOutPort.getSingleOccupancySupplementResponse(any()))
        .thenReturn(new SingleOccupancySupplementResponse("HOTELTEST", new BigDecimal(10)));
    when(contentOutPort.getHotelInformation(any(), any(), any()))
        .thenReturn(HotelInfoResponse.builder()
            .cityTax(HotelCityTax.builder()
                .bookingDateFrom(LocalDate.now().plusDays(5).toString())
                .effectiveFrom("2025-10-01")
                .build())
            .brand(PI_BOOKING_CHANNEL)
            .build());

    var mockedFeatureFlag = mock(FeatureFlag.class);
    when(unleashWrapper.featureFlag())
        .thenReturn(mockedFeatureFlag);
    when(unleashWrapper.isEnabled(mockedFeatureFlag.getApplyOccupancySupplement()))
        .thenReturn(false);
    when(unleashWrapper.isEnabled(mockedFeatureFlag.getReleasePiCcuiCityTaxUk()))
        .thenReturn(true);

    // Act
    var response = reservationInPort.createReservation(createValidHotelReservationRequestForCityTax("PI"));

    assertThat(response.getBasketReference(), is(BASKET_REFERENCE));
    verify(reservationOutPort).createReservation(any(), reservationRequestCaptor.capture(), any());
    ReservationRequest captured = reservationRequestCaptor.getValue();

    assertEquals("NTLEI", captured.getReasonForStay());
  }

  @Test
  void testCreateReservation_with_citytax_for_pi_success_with_no_effectiveFromDate() {
    // Arrange
    when(reservationOutPort.createReservation(anyString(), any(), anyString()))
        .thenReturn(mockReservationResponse());
    when(basketOutPort.createBasket(anyString(), anyString(), any()))
        .thenReturn(ManageReservationUtils.mockBasketResponse());
    when(basketOutPort.addReservationsToBasket(any(), any(),
        any(OhipReservationResponse.class), anyBoolean(), anyBoolean(), eq(1), anyBoolean()))
        .thenReturn(ManageReservationUtils.mockBasketResponse());
    when(rulesOutPort.getSingleOccupancySupplementResponse(any()))
        .thenReturn(new SingleOccupancySupplementResponse("HOTELTEST", new BigDecimal(10)));
    when(contentOutPort.getHotelInformation(any(), any(), any()))
        .thenReturn(HotelInfoResponse.builder()
            .cityTax(HotelCityTax.builder()
                .bookingDateFrom(LocalDate.now().plusDays(5).toString())
                .effectiveFrom("")
                .build())
            .brand(PI_BOOKING_CHANNEL)
            .build());

    var mockedFeatureFlag = mock(FeatureFlag.class);
    when(unleashWrapper.featureFlag())
        .thenReturn(mockedFeatureFlag);
    when(unleashWrapper.isEnabled(mockedFeatureFlag.getApplyOccupancySupplement()))
        .thenReturn(false);
    when(unleashWrapper.isEnabled(mockedFeatureFlag.getReleasePiCcuiCityTaxUk()))
        .thenReturn(true);

    // Act
    var response = reservationInPort.createReservation(createValidHotelReservationRequestForCityTax("PI"));

    assertThat(response.getBasketReference(), is(BASKET_REFERENCE));
    verify(reservationOutPort).createReservation(any(), reservationRequestCaptor.capture(), any());
    ReservationRequest captured = reservationRequestCaptor.getValue();

    assertEquals("LEI", captured.getReasonForStay());
  }

  @Test
  void testCreateReservation_with_citytax_for_pi_success_with_no_bookingDateFrom() {
    // Arrange
    when(reservationOutPort.createReservation(anyString(), any(), anyString()))
        .thenReturn(mockReservationResponse());
    when(basketOutPort.createBasket(anyString(), anyString(), any()))
        .thenReturn(ManageReservationUtils.mockBasketResponse());
    when(basketOutPort.addReservationsToBasket(any(), any(),
        any(OhipReservationResponse.class), anyBoolean(), anyBoolean(), eq(1), anyBoolean()))
        .thenReturn(ManageReservationUtils.mockBasketResponse());
    when(rulesOutPort.getSingleOccupancySupplementResponse(any()))
        .thenReturn(new SingleOccupancySupplementResponse("HOTELTEST", new BigDecimal(10)));
    when(contentOutPort.getHotelInformation(any(), any(), any()))
        .thenReturn(HotelInfoResponse.builder()
            .cityTax(HotelCityTax.builder()
                .bookingDateFrom("")
                .effectiveFrom("2025-10-01")
                .build())
            .brand(PI_BOOKING_CHANNEL)
            .build());

    var mockedFeatureFlag = mock(FeatureFlag.class);
    when(unleashWrapper.featureFlag())
        .thenReturn(mockedFeatureFlag);
    when(unleashWrapper.isEnabled(mockedFeatureFlag.getApplyOccupancySupplement()))
        .thenReturn(false);
    when(unleashWrapper.isEnabled(mockedFeatureFlag.getReleasePiCcuiCityTaxUk()))
        .thenReturn(true);

    // Act
    var response = reservationInPort.createReservation(createValidHotelReservationRequestForCityTax("PI"));

    assertThat(response.getBasketReference(), is(BASKET_REFERENCE));
    verify(reservationOutPort).createReservation(any(), reservationRequestCaptor.capture(), any());
    ReservationRequest captured = reservationRequestCaptor.getValue();

    assertEquals("LEI", captured.getReasonForStay());
  }

  @Test
  void testCreateReservation_with_citytax_for_pi_success_negative_scenario() {
    // Arrange
    when(reservationOutPort.createReservation(anyString(), any(), anyString()))
        .thenReturn(mockReservationResponse());
    when(basketOutPort.createBasket(anyString(), anyString(), any()))
        .thenReturn(ManageReservationUtils.mockBasketResponse());
    when(basketOutPort.addReservationsToBasket(any(), any(),
        any(OhipReservationResponse.class), anyBoolean(), anyBoolean(), eq(1), anyBoolean()))
        .thenReturn(ManageReservationUtils.mockBasketResponse());
    when(rulesOutPort.getSingleOccupancySupplementResponse(any()))
        .thenReturn(new SingleOccupancySupplementResponse("HOTELTEST", new BigDecimal(10)));
    when(contentOutPort.getHotelInformation(any(), any(), any()))
        .thenReturn(HotelInfoResponse.builder()
            .cityTax(HotelCityTax.builder()
                .bookingDateFrom(LocalDate.now().plusDays(1).toString())
                .effectiveFrom("2025-10-01")
                .build())
            .brand(PI_BOOKING_CHANNEL)
            .build());

    var mockedFeatureFlag = mock(FeatureFlag.class);
    when(unleashWrapper.featureFlag())
        .thenReturn(mockedFeatureFlag);
    when(unleashWrapper.isEnabled(mockedFeatureFlag.getApplyOccupancySupplement()))
        .thenReturn(false);
    when(unleashWrapper.isEnabled(mockedFeatureFlag.getReleasePiCcuiCityTaxUk()))
        .thenReturn(true);

    // Act
    var response = reservationInPort.createReservation(createValidHotelReservationRequestForCityTax("PI"));

    assertThat(response.getBasketReference(), is(BASKET_REFERENCE));
    verify(reservationOutPort).createReservation(any(), reservationRequestCaptor.capture(), any());
    ReservationRequest captured = reservationRequestCaptor.getValue();

    assertEquals("LEI", captured.getReasonForStay());
  }

  @Test
  void testCreateReservation_with_citytax_for_bb_success() {
    // Arrange
    when(reservationOutPort.createReservation(anyString(), any(), anyString()))
        .thenReturn(mockReservationResponse());
    when(basketOutPort.createBasket(anyString(), anyString(), any()))
        .thenReturn(ManageReservationUtils.mockBasketResponse());
    when(basketOutPort.addReservationsToBasket(any(), any(),
        any(OhipReservationResponse.class), anyBoolean(), anyBoolean(), eq(1), anyBoolean()))
        .thenReturn(ManageReservationUtils.mockBasketResponse());
    when(rulesOutPort.getSingleOccupancySupplementResponse(any()))
        .thenReturn(new SingleOccupancySupplementResponse("HOTELTEST", new BigDecimal(10)));
    when(contentOutPort.getHotelInformation(any(), any(), any()))
        .thenReturn(HotelInfoResponse.builder()
            .cityTax(HotelCityTax.builder()
                .bookingDateFrom(LocalDate.now().plusDays(5).toString())
                .effectiveFrom("2025-10-01")
                .build())
            .brand(PI_BOOKING_CHANNEL)
            .build());

    var mockedFeatureFlag = mock(FeatureFlag.class);
    when(unleashWrapper.featureFlag())
        .thenReturn(mockedFeatureFlag);
    when(unleashWrapper.isEnabled(mockedFeatureFlag.getApplyOccupancySupplement()))
        .thenReturn(false);
    when(unleashWrapper.isEnabled(mockedFeatureFlag.getReleasePiCcuiCityTaxUk()))
        .thenReturn(true);

    // Act
    var response = reservationInPort.createReservation(createValidHotelReservationRequestForCityTax("BB"));

    assertThat(response.getBasketReference(), is(BASKET_REFERENCE));
    verify(reservationOutPort).createReservation(any(), reservationRequestCaptor.capture(), any());
    ReservationRequest captured = reservationRequestCaptor.getValue();

    assertEquals("NTLEI", captured.getReasonForStay());
  }

  @Test
  void testCreateReservation_with_citytax_for_ccui_success() {
    // Arrange
    when(reservationOutPort.createReservation(anyString(), any(), anyString()))
        .thenReturn(mockReservationResponse());
    when(basketOutPort.createBasket(anyString(), anyString(), any()))
        .thenReturn(ManageReservationUtils.mockBasketResponse());
    when(basketOutPort.addReservationsToBasket(any(), any(),
        any(OhipReservationResponse.class), anyBoolean(), anyBoolean(), eq(1), anyBoolean()))
        .thenReturn(ManageReservationUtils.mockBasketResponse());
    when(rulesOutPort.getSingleOccupancySupplementResponse(any()))
        .thenReturn(new SingleOccupancySupplementResponse("HOTELTEST", new BigDecimal(10)));
    when(contentOutPort.getHotelInformation(any(), any(), any()))
        .thenReturn(HotelInfoResponse.builder()
            .cityTax(HotelCityTax.builder()
                .bookingDateFrom(LocalDate.now().plusDays(5).toString())
                .effectiveFrom("2025-10-01")
                .build())
            .brand(PI_BOOKING_CHANNEL)
            .build());

    var mockedFeatureFlag = mock(FeatureFlag.class);
    when(unleashWrapper.featureFlag())
        .thenReturn(mockedFeatureFlag);
    when(unleashWrapper.isEnabled(mockedFeatureFlag.getApplyOccupancySupplement()))
        .thenReturn(false);
    when(unleashWrapper.isEnabled(mockedFeatureFlag.getReleasePiCcuiCityTaxUk()))
        .thenReturn(true);

    // Act
    var response = reservationInPort.createReservation(createValidHotelReservationRequestForCityTax("CCUI"));

    assertThat(response.getBasketReference(), is(BASKET_REFERENCE));
    verify(reservationOutPort).createReservation(any(), reservationRequestCaptor.capture(), any());
    ReservationRequest captured1 = reservationRequestCaptor.getValue();

    assertEquals("NTLEI", captured1.getReasonForStay());
  }

  @Test
  void testCreateReservation_invalidIataNumber_fail() {
    ReservationRequest invalidHotelReservationRequest = createValidHotelReservationRequest();
    invalidHotelReservationRequest.getReservations().forEach(reservation -> {
      reservation.setDistributionIATANumber("1234567");
    });

    // Assert
    Assertions.assertThrows(GenericBadRequestException.class,
        () -> reservationInPort.createReservation(invalidHotelReservationRequest),
        "Invalid IATA number:");
  }

  @Test
  void createReservationForBBUser_success() {
    // Arrange
    when(reservationOutPort.createReservation(anyString(), any(), anyString()))
        .thenReturn(mockReservationResponse());
    when(basketOutPort.createBasket(anyString(), anyString(), any()))
        .thenReturn(ManageReservationUtils.mockBasketResponse());
    when(basketOutPort.addReservationsToBasket(any(), any(),
        any(OhipReservationResponse.class), anyBoolean(), anyBoolean(), eq(null), anyBoolean()))
        .thenReturn(ManageReservationUtils.mockBasketResponse());
    when(authenticatedUserService.isUserAuthenticated()).thenReturn(Boolean.TRUE);
    var account = Account.builder()
        .employeeId("test_employee")
        .companyId("test_company")
        .build();
    when(authenticatedUserService.getCurrentUserAccount()).thenReturn(Optional.of(account));
    ArgumentCaptor<ReservationRequest> argCaptor = ArgumentCaptor.forClass(
        ReservationRequest.class);
    var createRequest = createValidHotelReservationRequest();
    createRequest.getBookingChannel().setChannel("BB");

    var mockedFeatureFlag = mock(FeatureFlag.class);
    when(unleashWrapper.featureFlag())
        .thenReturn(mockedFeatureFlag);
    when(unleashWrapper.isEnabled(mockedFeatureFlag.getApplyOccupancySupplement()))
        .thenReturn(false);

    // Act
    var response = reservationInPort.createReservation(createRequest);

    // Assert
    assertThat(response.getBasketReference(), is(BASKET_REFERENCE));
    verify(reservationOutPort).createReservation(any(), argCaptor.capture(), anyString());
    var createReservationRequest = argCaptor.getValue();
    assertThat(createReservationRequest.getReservations().get(0).getUserAccountId(),
        is("test_employee"));
    assertThat(createReservationRequest.getReservations().get(0).getCompanyAccountId(),
        is("test_company"));
  }

  @Test
  void createReservationForPIUser_success() {
    // Arrange
    when(reservationOutPort.createReservation(anyString(), any(), anyString()))
        .thenReturn(mockReservationResponse());
    when(basketOutPort.createBasket(anyString(), anyString(), any()))
        .thenReturn(ManageReservationUtils.mockBasketResponse());
    when(basketOutPort.addReservationsToBasket(any(), any(),
        any(OhipReservationResponse.class), anyBoolean(), anyBoolean(), eq(null), anyBoolean()))
        .thenReturn(ManageReservationUtils.mockBasketResponse());
    when(authenticatedUserService.isUserAuthenticated()).thenReturn(Boolean.TRUE);
    var account = Account.builder()
        .customerId("test_customer")
        .build();
    when(authenticatedUserService.getCurrentUserAccount()).thenReturn(Optional.of(account));
    ArgumentCaptor<ReservationRequest> argCaptor = ArgumentCaptor.forClass(
        ReservationRequest.class);
    var createRequest = createValidHotelReservationRequest();
    createRequest.getBookingChannel().setChannel(PI_BOOKING_CHANNEL);

    var mockedFeatureFlag = mock(FeatureFlag.class);
    when(unleashWrapper.featureFlag())
        .thenReturn(mockedFeatureFlag);
    when(unleashWrapper.isEnabled(mockedFeatureFlag.getApplyOccupancySupplement()))
        .thenReturn(false);

    // Act
    var response = reservationInPort.createReservation(createRequest);

    // Assert
    assertThat(response.getBasketReference(), is(BASKET_REFERENCE));
    verify(reservationOutPort).createReservation(any(), argCaptor.capture(), anyString());
    var createReservationRequest = argCaptor.getValue();
    assertThat(createReservationRequest.getReservations().get(0).getUserAccountId(),
        is("test_customer"));
  }

  @Test
  void testCreateReservationWithFixedRate_success() {
    // Arrange
    when(reservationOutPort.createReservation(anyString(), any(), anyString()))
        .thenReturn(mockReservationResponse());
    when(basketOutPort.createBasket(anyString(), anyString(), any()))
        .thenReturn(ManageReservationUtils.mockBasketResponse());
    when(basketOutPort.addReservationsToBasket(any(), any(),
        any(OhipReservationResponse.class), anyBoolean(), anyBoolean(), eq(null), anyBoolean()))
        .thenReturn(ManageReservationUtils.mockBasketResponse());
    when(authenticatedUserService.isUserAuthenticated())
        .thenReturn(true);
    when(authenticatedUserService.getAuthenticatedUserAuthorities())
        .thenReturn(List.of(DISTR_FIXED_PRICE_AUTH));
    when(distributionProperties.getFixedRateAuthority())
        .thenReturn(DISTR_FIXED_PRICE_AUTH);

    var mockedFeatureFlag = mock(FeatureFlag.class);
    when(unleashWrapper.featureFlag())
        .thenReturn(mockedFeatureFlag);
    when(unleashWrapper.isEnabled(mockedFeatureFlag.getApplyOccupancySupplement()))
        .thenReturn(false);

    // Act
    var request = createValidHotelReservationRequest();
    request.setBookingChannel(BookingChannel.builder()
        .channel("DISTR")
        .build());
    request.getReservations().forEach(res -> res.getRoomRates().setRatePrices(createRatePrices()));
    var response = reservationInPort.createReservation(request);

    // Assert
    assertThat(response.getBasketReference(), is(BASKET_REFERENCE));
    assertNotNull(request.getReservations().get(0).getRoomRates().getRatePrices());
  }

  @Test
  void testCreateReservationWithFixedRate_UnauthorizedChannel() {
    // Arrange
    when(reservationOutPort.createReservation(anyString(), any(), anyString()))
        .thenReturn(mockReservationResponse());
    when(basketOutPort.createBasket(anyString(), anyString(), any()))
        .thenReturn(ManageReservationUtils.mockBasketResponse());
    when(basketOutPort.addReservationsToBasket(any(), any(),
        any(OhipReservationResponse.class), anyBoolean(), anyBoolean(), eq(null), anyBoolean()))
        .thenReturn(ManageReservationUtils.mockBasketResponse());

    var mockedFeatureFlag = mock(FeatureFlag.class);
    when(unleashWrapper.featureFlag())
        .thenReturn(mockedFeatureFlag);
    when(unleashWrapper.isEnabled(mockedFeatureFlag.getApplyOccupancySupplement()))
        .thenReturn(false);

    // Act
    var request = createValidHotelReservationRequest();
    request.getReservations().forEach(res -> res.getRoomRates().setRatePrices(createRatePrices()));
    var response = reservationInPort.createReservation(request);

    // Assert
    assertThat(response.getBasketReference(), is(BASKET_REFERENCE));
    assertNull(request.getReservations().get(0).getRoomRates().getRatePrices());
  }

  @Test
  void testCreateReservationWithFixedRate_UnauthorizedUser() {
    // Arrange
    when(reservationOutPort.createReservation(anyString(), any(), anyString()))
        .thenReturn(mockReservationResponse());
    when(basketOutPort.createBasket(anyString(), anyString(), any()))
        .thenReturn(ManageReservationUtils.mockBasketResponse());
    when(basketOutPort.addReservationsToBasket(any(), any(),
        any(OhipReservationResponse.class), anyBoolean(), anyBoolean(), eq(null), anyBoolean()))
        .thenReturn(ManageReservationUtils.mockBasketResponse());
    when(authenticatedUserService.isUserAuthenticated())
        .thenReturn(true);
    when(authenticatedUserService.getAuthenticatedUserAuthorities())
        .thenReturn(List.of());
    var mockedToken = mock(CustomJwtAuthenticationToken.class);
    var mockedJwt = mock(Jwt.class);
    when(mockedToken.getToken()).thenReturn(mockedJwt);
    when(mockedJwt.getClaimAsStringList(eq("permissions"))).thenReturn(null);
    when(mockedJwt.getClaimAsString(eq("scope"))).thenReturn(null);
    when(authenticatedUserService.getAuthenticatedUser())
        .thenReturn(mockedToken);
    when(distributionProperties.getFixedRateAuthority())
        .thenReturn(DISTR_FIXED_PRICE_AUTH);

    var mockedFeatureFlag = mock(FeatureFlag.class);
    when(unleashWrapper.featureFlag())
        .thenReturn(mockedFeatureFlag);
    when(unleashWrapper.isEnabled(mockedFeatureFlag.getApplyOccupancySupplement()))
        .thenReturn(false);

    // Act
    var request = createValidHotelReservationRequest();
    request.setBookingChannel(BookingChannel.builder()
        .channel("DISTR")
        .build());
    request.getReservations().forEach(res -> res.getRoomRates().setRatePrices(createRatePrices()));
    var response = reservationInPort.createReservation(request);

    // Assert
    assertThat(response.getBasketReference(), is(BASKET_REFERENCE));
    assertNull(request.getReservations().get(0).getRoomRates().getRatePrices());
  }

  @Test
  void testCreateReservationWithFixedRate_AnonymousUser() {
    // Arrange
    when(reservationOutPort.createReservation(anyString(), any(), anyString()))
        .thenReturn(mockReservationResponse());
    when(basketOutPort.createBasket(anyString(), anyString(), any()))
        .thenReturn(ManageReservationUtils.mockBasketResponse());
    when(basketOutPort.addReservationsToBasket(any(), any(),
        any(OhipReservationResponse.class), anyBoolean(), anyBoolean(), eq(null), anyBoolean()))
        .thenReturn(ManageReservationUtils.mockBasketResponse());
    when(authenticatedUserService.isUserAuthenticated())
        .thenReturn(false);

    var mockedFeatureFlag = mock(FeatureFlag.class);
    when(unleashWrapper.featureFlag())
        .thenReturn(mockedFeatureFlag);
    when(unleashWrapper.isEnabled(mockedFeatureFlag.getApplyOccupancySupplement()))
        .thenReturn(false);

    // Act
    var request = createValidHotelReservationRequest();
    request.setBookingChannel(BookingChannel.builder()
        .channel("DISTR")
        .build());
    request.getReservations().forEach(res -> res.getRoomRates().setRatePrices(createRatePrices()));
    var response = reservationInPort.createReservation(request);

    // Assert
    assertThat(response.getBasketReference(), is(BASKET_REFERENCE));
    assertNull(request.getReservations().get(0).getRoomRates().getRatePrices());
  }

  private List<RatePrice> createRatePrices() {
    return List.of(RatePrice.builder()
        .priceStartDate(LocalDate.now())
        .priceEndDate(LocalDate.now().plusDays(1))
        .amount(BigDecimal.valueOf(100.0))
        .build());
  }

  @Test
  void givenCCUIChannel_whenCreateReservation_bookingTypeShouldBeANON() {
    // Arrange
    when(reservationOutPort.createReservation(anyString(), any(), anyString()))
        .thenReturn(mockReservationResponse());
    when(basketOutPort.createBasket(anyString(), anyString(), any()))
        .thenReturn(ManageReservationUtils.mockBasketResponse());
    when(basketOutPort.addReservationsToBasket(any(), any(),
        any(OhipReservationResponse.class), anyBoolean(), anyBoolean(), eq(null), anyBoolean()))
        .thenReturn(ManageReservationUtils.mockBasketResponse());
    when(authenticatedUserService.isUserAuthenticated()).thenReturn(Boolean.TRUE);
    ArgumentCaptor<ReservationRequest> argCaptor = ArgumentCaptor.forClass(
        ReservationRequest.class);
    var createRequest = createValidHotelReservationRequest();
    createRequest.getBookingChannel().setChannel(CCUI_BOOKING_CHANNEL);

    var mockedFeatureFlag = mock(FeatureFlag.class);
    when(unleashWrapper.featureFlag())
        .thenReturn(mockedFeatureFlag);
    when(unleashWrapper.isEnabled(mockedFeatureFlag.getApplyOccupancySupplement()))
        .thenReturn(false);

    // Act
    var response = reservationInPort.createReservation(createRequest);

    // Assert
    verify(reservationOutPort).createReservation(any(), argCaptor.capture(), anyString());
    var createReservationRequest = argCaptor.getValue();
    assertThat(createReservationRequest.getReservations().get(0).getBookingType(),
        is(BOOKING_TYPE_ANON));
  }

  @ParameterizedTest
  @ValueSource(booleans = {false, true})
  void givenNonExistingBasket_whenGetAllReservationsJustByBookingReferenceAuthenticated_ReservationNOTFOUND(
      boolean token) {

    var channel = mockPIChannelRuleResponse();

    when(basketOutPort.getBasketByReference(BOOKING_REFERENCE_2)).thenReturn(Optional.empty());
    when(reservationOutPort.getReservationsByExternalId(BOOKING_REFERENCE_2)).thenReturn(
        ManageReservationUtils.mockReservationNotFoundResponse());
    when(rulesOutPort.getChannelBasedOnSourceId(SOURCE_CODE)).thenReturn(channel);

    assertThrows(ReservationNotFoundException.class,
        () -> reservationInPort.getAllReservationsJustByBookingReferenceAuthenticated(
            BOOKING_REFERENCE_2, token));
  }

  @ParameterizedTest
  @ValueSource(booleans = {false, true})
  void givenNonExistingBasket_whenGetAllReservationsJustByBookingReferenceAuthenticated_ReservationNOTFOUNDs(
      boolean token) {
    CdhSearchBookingsRequest cdhSearchBookingsRequest = new CdhSearchBookingsRequest();
    cdhSearchBookingsRequest.setBookingReference(BOOKING_REFERENCE_3);
    cdhSearchBookingsRequest.setBookingsDatabaseSearch(true);
    cdhSearchBookingsRequest.setPageNumber(1);
    cdhSearchBookingsRequest.setPageSize(10);
    CdhSearchBookingsResponse cdhSearchBookingsResponse=new CdhSearchBookingsResponse();
    CdhResults cdhResults= CdhResults.builder().build();
    Rooms rooms=Rooms.builder().reservationId("12345").build();
    cdhResults.setRooms(Collections.singletonList(rooms));
    cdhResults.setHotelId(HOTEL_ID);
    cdhSearchBookingsResponse.setResults(Collections.singletonList(cdhResults));
    DepositsResponse depositsResponse = new DepositsResponse();
    Deposits deposits = Deposits.builder().paymentReference(PAYMENT_REFERENCE).build();
    depositsResponse.setDeposits(Collections.singletonList(deposits));
    var channel = mockPIChannelRuleResponse();
    var featureFlag = new FeatureFlag();
    var basket = ManageReservationUtils.mockBasketForOperaUiRsv(PAY_ON_ARRIVAL);
    basket.setHotelId(HOTEL_ID);

    when(unleashWrapper.featureFlag())
            .thenReturn(featureFlag);
    when(unleashWrapper.isEnabled(featureFlag.getCcuiSearchByOperaConfirmation()))
            .thenReturn(true);
    when(basketOutPort.getBasketByReference(BOOKING_REFERENCE_3)).thenReturn(Optional.empty());
    when(cdhSearchBookingInPort.searchBookingsFromCdh(cdhSearchBookingsRequest)).thenReturn(cdhSearchBookingsResponse);
    when(reservationOutPort.getReservationsByReservationId("12345", HOTEL_ID)).thenReturn(
        ManageReservationUtils.mockReservationIdResponseForPN());
    when(reservationOutPort.getReservationsByIds(HOTEL_ID, List.of("12345"), false, true))
            .thenReturn(ManageReservationUtils.mockReservationByBasketRefResponse());
    when(reservationOutPort.getReservationsByIds(HOTEL_ID, List.of("12345"), false))
            .thenReturn(ManageReservationUtils.mockReservationByBasketRefResponse());
    when(rulesOutPort.getChannelBasedOnSourceId(SOURCE_CODE)).thenReturn(channel);
    when(manageBookingLogic.operaUiRsv(BOOKING_REFERENCE_3)).thenReturn(true);
    when(manageBookingLogic.replaceHotelId(BOOKING_REFERENCE_3)).thenReturn(BOOKING_REFERENCE_3);
    when(manageBookingLogic.createBasketForOperaUiCreatedReservations(any(), any(),
        any(), eq(false))).thenReturn(basket);
    var response = reservationInPort.getAllReservationsJustByBookingReferenceAuthenticated(
        BOOKING_REFERENCE_3, token);

    assertReservationByBasketRefResponse(response);
  }

  @Test
  void givenCCUIChannel_whenCreateReservation_withNegotiatedRates_bookingTypeShouldBeEmpty() {
    // Arrange
    when(reservationOutPort.createReservation(anyString(), any(), anyString()))
        .thenReturn(mockReservationResponse());
    when(basketOutPort.createBasket(anyString(), anyString(), any()))
        .thenReturn(ManageReservationUtils.mockBasketResponse());
    when(basketOutPort.addReservationsToBasket(any(), any(),
        any(OhipReservationResponse.class), anyBoolean(), anyBoolean(), eq(null), anyBoolean()))
        .thenReturn(ManageReservationUtils.mockBasketResponse());
    when(authenticatedUserService.isUserAuthenticated()).thenReturn(Boolean.TRUE);
    when(businessBookerConfigProperties.getNegociatedRatePlanSet()).thenReturn(
        Collections.singleton("NEG"));

    ArgumentCaptor<ReservationRequest> argCaptor = ArgumentCaptor.forClass(
        ReservationRequest.class);
    var createRequest = createValidHotelReservationRequest();
    createRequest.getBookingChannel().setChannel(CCUI_BOOKING_CHANNEL);
    createRequest.getReservations().get(0).getRoomRates().setRateDisplaySet("NEG");

    var mockedFeatureFlag = mock(FeatureFlag.class);
    when(unleashWrapper.featureFlag())
        .thenReturn(mockedFeatureFlag);
    when(unleashWrapper.isEnabled(mockedFeatureFlag.getApplyOccupancySupplement()))
        .thenReturn(false);

    // Act
    var response = reservationInPort.createReservation(createRequest);

    // Assert
    verify(reservationOutPort).createReservation(any(), argCaptor.capture(), anyString());
    var createReservationRequest = argCaptor.getValue();
    assertThat(createReservationRequest.getReservations().get(0).getBookingType(),
        is(BOOKING_TYPE_EMPTY));
  }

  @ParameterizedTest
  @CsvSource({"10, true", "0, false"})
  void testCreateReservation_shouldSetOccSupplFlagOnBasketItem(BigDecimal occSupplPrice, boolean isSupplApplicable) {
    // Arrange
    when(reservationOutPort.createReservation(anyString(), any(), anyString()))
        .thenReturn(mockReservationResponse());
    when(basketOutPort.createBasket(anyString(), anyString(), any()))
        .thenReturn(ManageReservationUtils.mockBasketResponse());
    when(basketOutPort.addReservationsToBasket(any(), any(),
        any(OhipReservationResponse.class), anyBoolean(), anyBoolean(),anyInt(), anyBoolean()))
        .thenReturn(ManageReservationUtils.mockBasketResponse());
    when(rulesOutPort.getSingleOccupancySupplementResponse(any()))
        .thenReturn(new SingleOccupancySupplementResponse("TESTHOTEL", occSupplPrice));

    var mockedFeatureFlag = mock(FeatureFlag.class);
    when(unleashWrapper.featureFlag())
        .thenReturn(mockedFeatureFlag);
    when(unleashWrapper.isEnabled(mockedFeatureFlag.getApplyOccupancySupplement()))
        .thenReturn(true);
    var request = createValidHotelReservationRequest();
    request.setIsOta(true);

    // Act
    var response = reservationInPort.createReservation(request);

    // Assert
    assertThat(response.getBasketReference(), is(BASKET_REFERENCE));

    var argCaptor = ArgumentCaptor.forClass(OhipReservationResponse.class);
    verify(basketOutPort).addReservationsToBasket(any(), any(), argCaptor.capture(), eq(false), eq(isSupplApplicable), eq(1), eq(true));
  }

  @Test
  void testGetReservationsByExternalReference_success() {
    // Arrange
    when(basketOutPort.getBasketById(anyString())).thenReturn(ManageReservationUtils.mockBasketResponse());
    when(reservationOutPort.getReservationsByBasketReference(anyString(), anyString(), anyInt(),
        anyInt())).thenReturn(mockReservationsDetailsResponse());

    // Act
    var response = reservationInPort.getReservationsByBasketReference("TestId", BOOKING_REFERENCE,
        20, 0);

    // Assert
    verify(reservationOutPort).getReservationsByBasketReference("TestId", BOOKING_REFERENCE, 20, 0);
    assertNotNull(response);
  }

  @Test
  void getReservationsByIds__success() {
    when(basketOutPort.getBasketById(BASKET_REFERENCE)).thenReturn(ManageReservationUtils.mockBasketResponse());
    when(reservationOutPort.getReservationsByIds(HOTEL_ID, RESERVATION_IDS, false, false, true)).thenReturn(
        mockReservationByBasketRefResponseWithCityTax());

    var response = reservationInPort.getAllReservationsJustByBasketReference(BASKET_REFERENCE, false);

    assertReservationByBasketRefResponse(response);
    assertEquals("2023-10-20", response.getReservationByIdList().get(0)
        .getReservationPackageList().get(0).getStartDate());
    assertEquals("2023-10-20", response.getReservationByIdList().get(0)
        .getReservationPackageList().get(0).getEndDate());
  }

  @Test
  void getReservationsByIdsWithCityTax__success() {
    when(basketOutPort.getBasketById(BASKET_REFERENCE)).thenReturn(ManageReservationUtils.mockBasketResponse());
    when(reservationOutPort.getReservationsByIds(HOTEL_ID, RESERVATION_IDS, false, false, true)).thenReturn(
        ManageReservationUtils.mockReservationByBasketRefResponse());

    var response = reservationInPort.getAllReservationsJustByBasketReference(BASKET_REFERENCE, false);

    assertReservationByBasketRefResponse(response);
  }

  @ParameterizedTest
  @ValueSource(booleans = {false, true})
  void givenExistingBasket_whenGetAllReservationsJustByBookingReferenceAuthenticated_success(
      boolean token) {
    when(basketOutPort.getBasketByReference(BOOKING_REFERENCE)).thenReturn(
        Optional.of(ManageReservationUtils.mockBasketResponse()));
    when(reservationOutPort.getReservationsByIds(HOTEL_ID, RESERVATION_IDS, false)).thenReturn(
        ManageReservationUtils.mockReservationByBasketRefResponse());

    var response = reservationInPort.getAllReservationsJustByBookingReferenceAuthenticated(
        BOOKING_REFERENCE, token);

    assertReservationByBasketRefResponse(response);
    verify(basketOutPort, never()).saveCharges(any());
  }

  @ParameterizedTest
  @ValueSource(booleans = {false, true})
  void givenNonExistingBasket_whenGetAllReservationsJustByBookingReferenceAuthenticated_success(
      boolean token) {
    DepositsResponse depositsResponse = new DepositsResponse();
    Deposits deposits = Deposits.builder().paymentReference(PAYMENT_REFERENCE).build();
    depositsResponse.setDeposits(Collections.singletonList(deposits));

    when(basketOutPort.getBasketByReference(BOOKING_REFERENCE_2)).thenReturn(Optional.empty());
    when(reservationOutPort.getReservationsByExternalId(BOOKING_REFERENCE_2)).thenReturn(
        ManageReservationUtils.mockReservationEnhancedResponse(SOURCE_CODE));
    when(reservationOutPort.getDepositsForReservationId(HOTEL_ID, UNIQUE_ID_TYPE)).thenReturn(
        depositsResponse);
    when(basketOutPort.createBasket(any(), any(), any(), any(), any(), any(), any(), any())).thenReturn(
        ManageReservationUtils.mockCreateBasketResponse(BOOKING_REFERENCE));
    when(basketOutPort.addReservationsToBasket(any(), any(), any(OhipReservationResponse.class),
        anyBoolean(), anyBoolean())).thenReturn(ManageReservationUtils.mockCreateBasketResponse(BOOKING_REFERENCE));
    when(reservationOutPort.getReservationsByIds(HOTEL_ID, RESERVATION_IDS, false)).thenReturn(
        ManageReservationUtils.mockReservationByBasketRefResponse());
    when(rulesOutPort.getChannelBasedOnSourceId(SOURCE_CODE)).thenReturn(mockChannelRuleResponse());

    var response = reservationInPort.getAllReservationsJustByBookingReferenceAuthenticated(
        BOOKING_REFERENCE_2, token);

    assertReservationByBasketRefResponse(response);
  }

  @ParameterizedTest
  @CsvSource({"234345, true", "ABCDEF123455, true", "234345, false ", "ABCDEF123455, false"})
  void operaUiRes_getAllReservationsJustByBookingReferenceAuthenticated_FF_true(String basketRef,
      boolean token) {
    CdhSearchBookingsRequest cdhSearchBookingsRequest = new CdhSearchBookingsRequest();
    cdhSearchBookingsRequest.setBookingReference(basketRef);
    cdhSearchBookingsRequest.setBookingsDatabaseSearch(true);
    cdhSearchBookingsRequest.setPageNumber(1);
    cdhSearchBookingsRequest.setPageSize(10);
    CdhSearchBookingsResponse cdhSearchBookingsResponse=new CdhSearchBookingsResponse();
    CdhResults cdhResults= CdhResults.builder().build();
    Rooms rooms=Rooms.builder().reservationId("12345").build();
    cdhResults.setRooms(Collections.singletonList(rooms));
    cdhResults.setHotelId(HOTEL_ID);
    cdhSearchBookingsResponse.setResults(Collections.singletonList(cdhResults));

    DepositsResponse depositsResponse = new DepositsResponse();
    Deposits deposits = Deposits.builder().paymentReference(PAYMENT_REFERENCE).build();
    depositsResponse.setDeposits(Collections.singletonList(deposits));
    var featureFlag = new FeatureFlag();
    var basket = ManageReservationUtils.mockBasketForOperaUiRsv(PAY_NOW);
    basket.setHotelId(HOTEL_ID);

    when(unleashWrapper.featureFlag())
            .thenReturn(featureFlag);
    when(unleashWrapper.isEnabled(featureFlag.getCcuiSearchByOperaConfirmation()))
            .thenReturn(true);
    when(basketOutPort.getBasketByReference(basketRef)).thenReturn(Optional.empty());
    when(cdhSearchBookingInPort.searchBookingsFromCdh(cdhSearchBookingsRequest)).thenReturn(cdhSearchBookingsResponse);

    when(reservationOutPort.getReservationsByReservationId(anyString(), anyString()))
            .thenReturn(ManageReservationUtils.mockReservationIdResponsePOA());
    when(reservationOutPort.getReservationsByIds(HOTEL_ID, List.of("12345"), false, true)).thenReturn(
            ManageReservationUtils.mockReservationByBasketRefResponse());
    when(reservationOutPort.getReservationsByIds(HOTEL_ID, List.of("12345"), false))
            .thenReturn(ManageReservationUtils.mockReservationByBasketRefResponse());

    when(rulesOutPort.getChannelBasedOnSourceId(SOURCE_CODE)).thenReturn(mockChannelRuleResponse());
    when(manageBookingLogic.operaUiRsv(basketRef)).thenReturn(true);
    when(manageBookingLogic.replaceHotelId(basketRef)).thenReturn(basketRef);
    when(manageBookingLogic.createBasketForOperaUiCreatedReservations(any(), any(),
        any(), eq(false))).thenReturn(basket);

    var response = reservationInPort.getAllReservationsJustByBookingReferenceAuthenticated(
        basketRef, token);

    assertReservationByBasketRefResponse(response);
  }

  @ParameterizedTest
  @ValueSource(strings = {"234345", "ABCDEF123455"})
  void operaUiRes_getAllReservationsJustByBookingReferenceAuthenticated_FF_false(String basketRef) {
    var featureFlag = new FeatureFlag();

    when(unleashWrapper.featureFlag())
            .thenReturn(featureFlag);
    when(unleashWrapper.isEnabled(featureFlag.getCcuiSearchByOperaConfirmation()))
            .thenReturn(false);
    when(basketOutPort.getBasketByReference(basketRef)).thenReturn(Optional.empty());
    when(manageBookingLogic.operaUiRsv(basketRef)).thenReturn(true);

    assertThrows(ReservationNotFoundException.class, () ->
            reservationInPort.getAllReservationsJustByBookingReferenceAuthenticated(basketRef, true));
  }

  @ParameterizedTest
  @ValueSource(booleans = {true, false})
  void givenNonExistingBasket_whenGetAllReservationsByReservationIdJustByBookingReferenceAuthenticated_success(
      boolean token) {
    CdhSearchBookingsRequest cdhSearchBookingsRequest = new CdhSearchBookingsRequest();
    cdhSearchBookingsRequest.setBookingReference(BOOKING_REFERENCE_3);
    cdhSearchBookingsRequest.setBookingsDatabaseSearch(true);
    cdhSearchBookingsRequest.setPageNumber(1);
    cdhSearchBookingsRequest.setPageSize(10);
    CdhSearchBookingsResponse cdhSearchBookingsResponse=new CdhSearchBookingsResponse();
    CdhResults cdhResults= CdhResults.builder().build();
    Rooms rooms=Rooms.builder().reservationId("12345").build();
    cdhResults.setRooms(Collections.singletonList(rooms));
    cdhResults.setHotelId(HOTEL_ID);
    cdhSearchBookingsResponse.setResults(Collections.singletonList(cdhResults));

    DepositsResponse depositsResponse = new DepositsResponse();
    Deposits deposits = Deposits.builder().paymentReference(PAYMENT_REFERENCE).build();
    depositsResponse.setDeposits(Collections.singletonList(deposits));
    var featureFlag = new FeatureFlag();
    var basket = ManageReservationUtils.mockBasketForOperaUiRsv(PAY_NOW);
    basket.setHotelId(HOTEL_ID);

    when(unleashWrapper.featureFlag())
            .thenReturn(featureFlag);
    when(unleashWrapper.isEnabled(featureFlag.getCcuiSearchByOperaConfirmation()))
            .thenReturn(true);

    when(cdhSearchBookingInPort.searchBookingsFromCdh(cdhSearchBookingsRequest)).thenReturn(cdhSearchBookingsResponse);
    when(reservationOutPort.getReservationsByReservationId("12345",HOTEL_ID)).thenReturn(
        ManageReservationUtils.mockReservationIdResponseForPN());
    when(reservationOutPort.getReservationsByIds(HOTEL_ID, List.of("12345"), false, true))
            .thenReturn(ManageReservationUtils.mockReservationByBasketRefResponse());
    when(reservationOutPort.getReservationsByIds(HOTEL_ID, List.of("12345"), false))
            .thenReturn(ManageReservationUtils.mockReservationByBasketRefResponse());
    when(rulesOutPort.getChannelBasedOnSourceId(SOURCE_CODE)).thenReturn(mockChannelRuleResponse());
    when(manageBookingLogic.operaUiRsv(BOOKING_REFERENCE_3)).thenReturn(true);
    when(manageBookingLogic.replaceHotelId(BOOKING_REFERENCE_3)).thenReturn(BOOKING_REFERENCE_3);
    when(manageBookingLogic.createBasketForOperaUiCreatedReservations(any(), any(),
        any(), eq(false))).thenReturn(basket);
    var response = reservationInPort.getAllReservationsJustByBookingReferenceAuthenticated(
        BOOKING_REFERENCE_3, token);

    assertReservationByBasketRefResponse(response);
  }

  @ParameterizedTest
  @ValueSource(booleans = {true, false})
  void getAllReservationsJustByBookingRefAuth_shouldSaveChargesForExistingResWithPNAndNoChargesOrAmountLeft(
      boolean token) {
    //Arrange
    when(basketOutPort.getBasketByReference(BOOKING_REFERENCE)).thenReturn(
        Optional.of(ManageReservationUtils.mockBasketResponseWithPN()));
    when(reservationOutPort.getReservationsByIds(HOTEL_ID, List.of(BOOKING_REFERENCE), false)).thenReturn(
        ManageReservationUtils.mockReservationByBasketRefResponse());
    when(basketOutPort.getCharges(any())).thenReturn(DepositFoliosResponse.builder().build());
    //Act
    var response = reservationInPort.getAllReservationsJustByBookingReferenceAuthenticated(
        BOOKING_REFERENCE, token);

    //Assert
    assertNotNull(response);
    verify(basketOutPort).saveCharges(any());
  }

  @ParameterizedTest
  @ValueSource(booleans = {true, false})
  void getAllReservationsJustByBookingRefAuth_shouldNotSaveChargesForExistingResWithCharges(
      boolean token) {
    //Arrange
    when(basketOutPort.getBasketByReference(BOOKING_REFERENCE)).thenReturn(
        Optional.of(ManageReservationUtils.mockBasketResponseWithPN()));
    when(reservationOutPort.getReservationsByIds(HOTEL_ID, List.of(BOOKING_REFERENCE), false)).thenReturn(
        ManageReservationUtils.mockReservationByBasketRefResponse());
    when(basketOutPort.getCharges(any())).thenReturn(
        ManageReservationUtils.mockDepositFoliosResponse());
    //Act
    var response = reservationInPort.getAllReservationsJustByBookingReferenceAuthenticated(
        BOOKING_REFERENCE, token);

    //Assert
    assertNotNull(response);
    verify(basketOutPort, never()).saveCharges(any());
  }

  @ParameterizedTest
  @ValueSource(booleans = {true, false})
  void getAllReservationsJustByBookingRefAuth_shouldNotSaveChargesForExistingResWithAmountLeft(
      boolean token) {
    //Arrange
    when(basketOutPort.getBasketByReference(BOOKING_REFERENCE)).thenReturn(
        Optional.of(ManageReservationUtils.mockBasketResponseWithPN()));
    var resByBasket = ManageReservationUtils.mockReservationByBasketRefResponse();
    resByBasket.getReservationByIdList().get(0)
        .setRateInfo(ManageReservationUtils.mockRateInfoWithPayOnArrivalAmountLeft());
    when(reservationOutPort.getReservationsByIds(HOTEL_ID, List.of(BOOKING_REFERENCE),
        false)).thenReturn(
        resByBasket);
    //Act
    var response = reservationInPort.getAllReservationsJustByBookingReferenceAuthenticated(
        BOOKING_REFERENCE, token);

    //Assert
    assertNotNull(response);
    verify(basketOutPort, never()).saveCharges(any());
  }

  @ParameterizedTest
  @ValueSource(booleans = {true, false})
  void getAllReservationsJustByBookingRefAuth_shouldSaveChargesForMigratedResWithPNAndNoAmountLeft(
      boolean token) {
    //Arrange
    DepositsResponse depositsResponse = new DepositsResponse();
    Deposits deposits = Deposits.builder().paymentReference(PAYMENT_REFERENCE).build();
    depositsResponse.setDeposits(Collections.singletonList(deposits));
    var basket = ManageReservationUtils.mockBasketResponseWithPN();
    basket.setBookingReference(BOOKING_REFERENCE_2);
    basket.getItems().get(0).setSourceId(BOOKING_REFERENCE_2);
    var extRef = ManageReservationUtils.mockReservationEnhancedResponse(SOURCE_CODE);
    when(
        reservationOutPort.getReservationsByIds(HOTEL_ID, List.of(BOOKING_REFERENCE_2), false)).thenReturn(
        ManageReservationUtils.mockReservationByBasketRefResponse());
    when(basketOutPort.getBasketByReference(BOOKING_REFERENCE_2)).thenReturn(Optional.empty());
    when(reservationOutPort.getReservationsByExternalId(BOOKING_REFERENCE_2)).thenReturn(extRef);
    when(reservationOutPort.getDepositsForReservationId(HOTEL_ID, UNIQUE_ID_TYPE)).thenReturn(
        depositsResponse);
    when(basketOutPort.createBasket(any(), any(), any(), any(), any(), any(), any(), any())).thenReturn(
        basket);
    when(basketOutPort.addReservationsToBasket(any(), any(), any(OhipReservationResponse.class),
        anyBoolean(), anyBoolean())).thenReturn(basket);
    when(
        reservationOutPort.getReservationsByIds(HOTEL_ID, List.of(BOOKING_REFERENCE_2), false)).thenReturn(
        ManageReservationUtils.mockReservationByBasketRefResponse());
    when(rulesOutPort.getChannelBasedOnSourceId(SOURCE_CODE)).thenReturn(mockChannelRuleResponse());
    when(amendLogicInPort.getAmountFromRateInfo(any()))
        .thenReturn(
            AmendSummaryAmountResponse.builder().guestPay(Map.of("1", BigDecimal.ZERO)).build());
    //Act
    var response = reservationInPort.getAllReservationsJustByBookingReferenceAuthenticated(
        BOOKING_REFERENCE_2, token);

    //Assert
    assertNotNull(response);
    verify(basketOutPort).saveCharges(any());
  }

  @ParameterizedTest
  @ValueSource(booleans = {true, false})
  void whenGetAllReservationsJustByBookingReferenceAuthenticated_checkAccessForPiUser_success(
      boolean token) {
    when(basketOutPort.getBasketByReference(BOOKING_REFERENCE)).thenReturn(
        Optional.of(ManageReservationUtils.mockBasketResponse()));
    when(reservationOutPort.getReservationsByIds(HOTEL_ID, RESERVATION_IDS, false)).thenReturn(
        ManageReservationUtils.mockReservationByBasketRefResponse());
    var response = reservationInPort.getAllReservationsJustByBookingReferenceAuthenticated(
        BOOKING_REFERENCE, token);

    assertReservationByBasketRefResponse(response);
  }

  @ParameterizedTest
  @ValueSource(booleans = {true, false})
  void whenGetAllReservationsJustByBookingReferenceAuthenticated_checkAccessForPiUser_forbidden(
      boolean token) {
    when(basketOutPort.getBasketByReference(BOOKING_REFERENCE)).thenReturn(
        Optional.of(ManageReservationUtils.mockBasketResponse()));
    when(reservationOutPort.getReservationsByIds(HOTEL_ID, RESERVATION_IDS, false)).thenReturn(
        ManageReservationUtils.mockReservationByBasketRefResponse());

    if (token) {
      when(authenticatedUserService.getCurrentUserAccount()).thenReturn(
          Optional.of(mockAccount(OTHER_CUSTOMER_ACCOUNT_ID)));

      assertThrows(AccessDeniedException.class,
          () -> reservationInPort.getAllReservationsJustByBookingReferenceAuthenticated(
              BOOKING_REFERENCE, token));
    } else {
      var response = reservationInPort.getAllReservationsJustByBookingReferenceAuthenticated(
          BOOKING_REFERENCE, token);

      assertReservationByBasketRefResponse(response);
    }
  }

  @ParameterizedTest
  @CsvSource({"true", "false"})
  void getReservationsPackages_success(Boolean mealInclusiveRate) {
    //Arrange
    when(
        reservationOutPort.getReservationsPackagesByBasketRef("hotelId", "basketRef",
            mealInclusiveRate))
        .thenReturn(mockReservationsPackagesResponse());

    //Act
    var response = reservationInPort.getReservationsPackagesByBasketRef(
        "hotelId", "basketRef", mealInclusiveRate);

    var packagesSelection = response.getRoomsSelections().get(0).getPackagesSelection()
        .get(0);

    //Assert
    assertThat(response, notNullValue());
    assertEquals(1, packagesSelection.getNoOfSelections());
    assertEquals("MTEST", packagesSelection.getId());
  }

  @Test
  void saveReservation_success() {
    //Arrange
    var savePackages = createSavePackagesRequest();
    assertDoesNotThrow(() -> reservationOutPort.updateReservationPackages(savePackages));

    //Act
    reservationInPort.updateReservationPackages(savePackages);

    //Assert
  }

  @Test
  void createReservationGuest_success() {
    //Arrange
    when(contentOutPort.getHotelInformation(any(), any(), any()))
        .thenReturn(HotelInfoResponse.builder()
            .cityTax(HotelCityTax.builder()
                .bookingDateFrom(LocalDate.now().plusDays(5).toString())
                .effectiveFrom("2025-10-01")
                .build())
            .brand(PI_BOOKING_CHANNEL)
            .build());

    var mockedFeatureFlag = mock(FeatureFlag.class);
    when(unleashWrapper.featureFlag())
        .thenReturn(mockedFeatureFlag);
    when(unleashWrapper.isEnabled(mockedFeatureFlag.getReleasePiCcuiCityTaxUk()))
        .thenReturn(true);

    when(reservationOutPort.getReservationsByIds(any(), any(), eq(false), eq(false), eq(false)))
        .thenReturn(mockReservationByBasketRefOnHoldResponse());
    when(basketOutPort.getBasketById(anyString())).thenReturn(ManageReservationUtils.mockBasketResponse());

    //Act
    var response = reservationInPort.createReservationGuest("1234",
        createValidReservationGuestRequest());

    //Assert
    assertThat(response, notNullValue());
  }


  @Test
  void createReservationGuest_authenticated_PI_success() {
    //Arrange
    when(contentOutPort.getHotelInformation(any(), any(), any()))
        .thenReturn(HotelInfoResponse.builder()
            .cityTax(HotelCityTax.builder()
                .bookingDateFrom(LocalDate.now().plusDays(5).toString())
                .effectiveFrom("2025-10-01")
                .build())
            .brand(PI_BOOKING_CHANNEL)
            .build());

    var mockedFeatureFlag = mock(FeatureFlag.class);
    when(unleashWrapper.featureFlag())
        .thenReturn(mockedFeatureFlag);
    when(unleashWrapper.isEnabled(mockedFeatureFlag.getReleasePiCcuiCityTaxUk()))
        .thenReturn(true);

    when(reservationOutPort.getReservationsByIds(any(), any(), eq(false), eq(false), eq(false)))
        .thenReturn(mockReservationByBasketRefOnHoldResponse());
    when(basketOutPort.getBasketById(anyString())).thenReturn(ManageReservationUtils.mockBasketResponse(PI_BOOKING_CHANNEL));
    when(authenticatedUserService.isUserAuthenticated()).thenReturn(true);
    when(authenticatedUserService.getCurrentUserAccount())
        .thenReturn(ManageReservationUtils.mockCurrentUserAccount("test@test.com"));

    //Act
    var response = reservationInPort.createReservationGuest("1234",
        createValidReservationGuestRequest());

    //Assert
    assertThat(response, notNullValue());
  }

  @Test
  void createReservationGuest_authenticated_PI_saveBookerDetails_success() {
    // Arrange
    var basketReference = "mockBasketReference";
    var reservationGuestRequest = mock(ReservationGuestRequest.class);
    var bookerDetails = mock(BookerDetails.class);
    var jwt = mock(Jwt.class);

    var basketResponse = mock(BasketResponse.class);
    when(basketResponse.getChannel()).thenReturn(PI_BOOKING_CHANNEL);
    when(basketOutPort.getBasketById(basketReference)).thenReturn(basketResponse);
    when(reservationOutPort.getReservationsByIds(any(), any(), any(), any(), any()))
        .thenReturn(mockReservationByBasketRefOnHoldResponse());

    var mockedFeatureFlag = mock(FeatureFlag.class);
    when(unleashWrapper.featureFlag())
        .thenReturn(mockedFeatureFlag);
    when(unleashWrapper.isEnabled(mockedFeatureFlag.getReleasePiCcuiCityTaxUk()))
        .thenReturn(false);

    when(reservationGuestRequest.getStayingGuests()).thenReturn(List.of());
    when(reservationGuestRequest.getUpdateProfileConsent()).thenReturn(true);
    when(reservationGuestRequest.getBooker()).thenReturn(bookerDetails);

    when(authenticatedUserService.isUserAuthenticated()).thenReturn(true);
    when(jwt.getTokenValue()).thenReturn(MOCKED_TOKEN_VALUE);
    CustomJwtAuthenticationToken authUser = mock(CustomJwtAuthenticationToken.class);
    when(authUser.getToken()).thenReturn(jwt);
    when(authUser.getAccount()).thenReturn(Account.builder().customerId(CUSTOMER_123).build());
    when(authenticatedUserService.getAuthenticatedUser()).thenReturn(authUser);

    // Act
    var response = reservationInPort.createReservationGuest(basketReference, reservationGuestRequest);

    // Assert
    assertNotNull(response);
    verify(hotelAccountOutPort).updateCustomer(bookerDetails, CUSTOMER_123, MOCKED_TOKEN_VALUE);
  }

  @Test
  void createReservationGuest_authenticated_BB_success() {
    //Arrange
    when(contentOutPort.getHotelInformation(any(), any(), any()))
        .thenReturn(HotelInfoResponse.builder()
            .cityTax(HotelCityTax.builder()
                .bookingDateFrom(LocalDate.now().plusDays(5).toString())
                .effectiveFrom("2025-10-01")
                .build())
            .brand(PI_BOOKING_CHANNEL)
            .build());

    var mockedFeatureFlag = mock(FeatureFlag.class);
    when(unleashWrapper.featureFlag())
        .thenReturn(mockedFeatureFlag);
    when(unleashWrapper.isEnabled(mockedFeatureFlag.getReleasePiCcuiCityTaxUk()))
        .thenReturn(true);

    when(reservationOutPort.getReservationsByIds(any(), any(), eq(false), eq(false), eq(false)))
        .thenReturn(mockReservationByBasketRefOnHoldResponse());
    when(basketOutPort.getBasketById(anyString())).thenReturn(ManageReservationUtils.mockBasketResponse("BB"));
    when(authenticatedUserService.isUserAuthenticated()).thenReturn(true);
    when(authenticatedUserService.getCurrentUserAccount())
        .thenReturn(ManageReservationUtils.mockCurrentUserAccount("test@test.com"));

    //Act
    var response = reservationInPort.createReservationGuest("1234",
        createValidReservationGuestRequest());

    //Assert
    assertThat(response, notNullValue());
  }

  @Test
  void createReservationGuest_noUpdate() {
    //Arrange
    var basket = ManageReservationUtils.mockBasketResponse();
    basket.setStatus(StatusEnum.PAY_PENDING.getValue());

    //Assert
    verify(reservationOutPort, times(0)).createReservationGuest(
        createValidReservationGuestRequest());
  }

  @Test
  void createReservationGuest_preCheckInFalse() {
    //Arrange
    when(contentOutPort.getHotelInformation(any(), any(), any()))
        .thenReturn(HotelInfoResponse.builder()
            .cityTax(HotelCityTax.builder()
                .bookingDateFrom(LocalDate.now().plusDays(5).toString())
                .effectiveFrom("2025-10-01")
                .build())
            .brand(PI_BOOKING_CHANNEL)
            .build());

    var mockedFeatureFlag = mock(FeatureFlag.class);
    when(unleashWrapper.featureFlag())
        .thenReturn(mockedFeatureFlag);
    when(unleashWrapper.isEnabled(mockedFeatureFlag.getReleasePiCcuiCityTaxUk()))
        .thenReturn(true);

    when(reservationOutPort.getReservationsByIds(any(), any(), eq(false), eq(false), eq(false)))
        .thenReturn(mockReservationByBasketRefOnHoldResponse());
    when(basketOutPort.getBasketById(anyString())).thenReturn(
        ManageReservationUtils.mockBasketResponse(PI_BOOKING_CHANNEL));
    when(authenticatedUserService.isUserAuthenticated()).thenReturn(true);
    when(authenticatedUserService.getCurrentUserAccount())
        .thenReturn(ManageReservationUtils.mockCurrentUserAccount("test@test.com"));
    var reservationGuestRequest = createValidReservationGuestRequest();
    reservationGuestRequest.setPreCheckIn(false);
    //Act
    var response = reservationInPort.createReservationGuest("1234", reservationGuestRequest);

    //Assert
    assertThat(response, notNullValue());
    assertThat(reservationGuestRequest.getStayingGuests().get(0).getReservationId(), is("res1"));
  }

  @Test
  void createReservationGuest_preCheckInTrue() {
    // Arrange
    when(contentOutPort.getHotelInformation(any(), any(), any()))
        .thenReturn(HotelInfoResponse.builder()
            .cityTax(HotelCityTax.builder()
                .bookingDateFrom(LocalDate.now().plusDays(5).toString())
                .effectiveFrom("2025-10-01")
                .build())
            .brand(PI_BOOKING_CHANNEL)
            .build());

    var mockedFeatureFlag = mock(FeatureFlag.class);
    when(unleashWrapper.featureFlag())
        .thenReturn(mockedFeatureFlag);
    when(unleashWrapper.isEnabled(mockedFeatureFlag.getReleasePiCcuiCityTaxUk()))
        .thenReturn(true);

    when(reservationOutPort.getReservationsByIds(any(), any(), eq(false), eq(false), eq(false)))
        .thenReturn(mockReservationByBasketRefOnHoldResponse());
    when(basketOutPort.getBasketById(anyString())).thenReturn(
        ManageReservationUtils.mockBasketResponse(PI_BOOKING_CHANNEL));
    when(authenticatedUserService.isUserAuthenticated()).thenReturn(true);
    when(authenticatedUserService.getCurrentUserAccount())
        .thenReturn(ManageReservationUtils.mockCurrentUserAccount("test@test.com"));
    var reservationGuestRequest = createValidReservationGuestRequest();
    reservationGuestRequest.setPreCheckIn(true);
    reservationGuestRequest.setStayingGuests(
        List.of(StayingGuest.builder().reservationId("res1").build()));

    // Act
    var response = reservationInPort.createReservationGuest("1234", reservationGuestRequest);

    // Assert
    assertThat(response, notNullValue());
    assertThat(reservationGuestRequest.getStayingGuests().get(0).getReservationId(),
        is("res1"));
  }

  @Test
  void cancelOnHoldReservation_success() {

    //Arrange
    var cancelOnHoldReservationRequest = CancelReservationRequest.builder()
        .hotelId(HOTEL_ID)
        .basketReference(BASKET_REF_ONHOLD_RES)
        .build();
    when(basketOutPort.getBasketById(BASKET_REF_ONHOLD_RES)).thenReturn(
        mockBasketForCancelOnHoldResponse());
    when(reservationOutPort.getReservationsByIds(any(), any(), eq(false), eq(false), eq(false)))
        .thenReturn(mockReservationByBasketRefOnHoldResponse());
    when(reservationOutPort.cancelReservation(cancelOnHoldReservationRequest, null))
        .thenReturn(mockCancelOnHoldReservationResponse());
    doNothing().when(basketOutPort).cancelBasket(BASKET_REF_ONHOLD_RES, false, false, null);

    //Act
    var cancelReservationResponse = reservationInPort.cancelOnHoldReservation(
        cancelOnHoldReservationRequest);

    //Assert
    assertThat(cancelReservationResponse, notNullValue());
    assertEquals(BASKET_REF_ONHOLD_RES, cancelReservationResponse.getBasketReference());

  }

  @Test
  void changeRatePlanReservation_success() {
    //Arrange
    when(reservationOutPort.updateReservationRateCode(mockUpdateRequest()))
        .thenReturn(new SaveReservationResponse("HOTELCODE1001001"));

    //Act
    var response = reservationInPort.updateReservationRateCode(mockUpdateRequest());

    //Assert
    assertThat(response, notNullValue());
    assertThat(response.getBasketReference(), is("HOTELCODE1001001"));
  }

  @Test
  void updateRoomType_success() {
    //Arrange
    when(reservationOutPort.updateRoomType(mockUpdateRequest()))
        .thenReturn(new SaveReservationResponse("HOTELCODE1001001"));

    //Act
    var response = reservationInPort.updateRoomType(mockUpdateRequest());

    //Assert
    assertThat(response, notNullValue());
    assertThat(response.getBasketReference(), is("HOTELCODE1001001"));
  }

  @Test
  void cancelReservation_payOnArrival_success() {
    //Arrange
    var basket = ManageReservationUtils.mockBasketResponse();
    var reservations = ManageReservationUtils.mockReservationByBasketRefResponseWithZeroAmountPaid();
    when(basketOutPort.getBasketById(anyString())).thenReturn(basket);
    doNothing().when(basketOutPort).cancelBasket(basket.getReference(), false, true, null);
    when(reservationOutPort.getReservationsByIds(any(), any(), anyBoolean()))
        .thenReturn(reservations);
    when(reservationOutPort.cancelReservation(any(), any())).thenReturn(
        new CancelReservationResponse(basket.getReference(), null, null));
    tokenUtilsMock.when(() -> TokenUtils.isValid(anyString(), anyString()))
        .thenReturn(Boolean.TRUE);

    //Act
    var response = reservationInPort.cancelReservation(
        CancelReservationRequest.builder().basketReference(basket.getReference()).token(TOKEN)
            .build());

    //Assert
    assertThat(response, notNullValue());
    assertThat(response.getBasketReference(), is(basket.getReference()));
    verify(basketOutPort).cancelBasket(basket.getReference(), false, true, null);
    verify(basketOutPort).triggerEmailConfirmation(any());
  }

  @Test
  void cancelReservation_ccui_userAuthenticated_agentIdLogEnabled_success() {
    //Arrange
    var basket = ManageReservationUtils.mockBasketResponse();
    var reservations = ManageReservationUtils.mockReservationByBasketRefResponseWithZeroAmountPaid();
    when(basketOutPort.getBasketById(anyString())).thenReturn(basket);
    doNothing().when(basketOutPort).cancelBasket(basket.getReference(), false, true, null);
    when(reservationOutPort.getReservationsByIds(any(), any(), anyBoolean()))
        .thenReturn(reservations);
    when(reservationOutPort.cancelReservation(any(), any())).thenReturn(
        new CancelReservationResponse(basket.getReference(), null, List.of("reservationId")));
    when(unleashWrapper.featureFlag()).thenReturn(new FeatureFlag());
    when(unleashWrapper.isEnabled(unleashWrapper.featureFlag().getCcuiAgentIdLog())).thenReturn(
        true);
    when(authenticatedUserService.isUserAuthenticated()).thenReturn(true);
    CustomJwtAuthenticationToken jwtAuthenticationToken = mock(CustomJwtAuthenticationToken.class);
    Jwt jwt = mock(Jwt.class);
    when(jwtAuthenticationToken.getToken()).thenReturn(jwt);
    when(jwtAuthenticationToken.getAccount()).thenReturn(mock(Account.class, RETURNS_DEEP_STUBS));
    when(authenticatedUserService.getAuthenticatedUser()).thenReturn(jwtAuthenticationToken);
    when(jwtAuthenticationToken.getToken().getClaim(BOOKING_FLOW_CLAIM)).thenReturn(
        CCUI_BOOKING_CHANNEL);

    //Act
    var response = reservationInPort.cancelReservation(
        CancelReservationRequest.builder().basketReference(basket.getReference()).build());

    //Assert
    assertThat(response, notNullValue());
    assertThat(response.getBasketReference(), is(basket.getReference()));
    verify(basketOutPort).cancelBasket(basket.getReference(), false, true, null);
    verify(basketOutPort).triggerEmailConfirmation(any());
  }

  @Test
  void cancelReservation_payNow_withoutAmountPaid_success() {
    //Arrange
    var basket = ManageReservationUtils.mockPNBasketResponse();
    var reservations = ManageReservationUtils.mockReservationByBasketRefResponseWithZeroAmountPaid();
    var captor = ArgumentCaptor.forClass(EmailRequest.class);

    when(basketOutPort.getBasketById(anyString())).thenReturn(basket);
    doNothing().when(basketOutPort).cancelBasket(basket.getReference(), false, true, null);
    when(reservationOutPort.getReservationsByIds(any(), any(), anyBoolean()))
        .thenReturn(reservations);
    when(reservationOutPort.cancelReservation(any(), any())).thenReturn(
        new CancelReservationResponse(basket.getReference(), null, null));
    when(basketOutPort.getCharges(any())).thenReturn(DepositFoliosResponse.builder()
        .depositFolios(Collections.emptyList())
        .build());
    tokenUtilsMock.when(() -> TokenUtils.isValid(anyString(), anyString()))
        .thenReturn(Boolean.TRUE);

    //Act
    var response = reservationInPort.cancelReservation(
        CancelReservationRequest.builder()
            .basketReference(basket.getReference())
            .token(TOKEN)
            .build());

    //Assert
    assertThat(response, notNullValue());
    assertThat(response.getBasketReference(), is(basket.getReference()));
    verify(basketOutPort, never()).saveCharges(any());
    verify(basketOutPort).triggerEmailConfirmation(captor.capture());
    var emailRequest = captor.getValue();
    assertEquals(false, emailRequest.isFailedRefund());
  }

  @Test
  void cancelReservation_payNow_withDeposits_success() {
    //Arrange
    var basket = ManageReservationUtils.mockPNBasketResponse();
    var reservations = ManageReservationUtils.mockReservationByBasketRefResponseWithZeroAmountPaid();
    var depositsResponse = ManageReservationUtils.mockDepositsResponse();
    var refundedDeposits = ManageReservationUtils.mockRefundedDeposits(depositsResponse);

    when(basketOutPort.getBasketById(anyString())).thenReturn(basket);
    doNothing().when(basketOutPort)
        .cancelBasket(basket.getReference(), false, true, depositsResponse.getDeposits());
    when(reservationOutPort.getReservationsByIds(any(), any(), anyBoolean()))
        .thenReturn(reservations);
    when(reservationOutPort.cancelReservation(any(), any())).thenReturn(
        new CancelReservationResponse(basket.getReference(), refundedDeposits, null));
    when(basketOutPort.getCharges(any())).thenReturn(DepositFoliosResponse.builder()
        .depositFolios(Collections.emptyList())
        .build());
    tokenUtilsMock.when(() -> TokenUtils.isValid(anyString(), anyString()))
        .thenReturn(Boolean.TRUE);

    //Act
    var response = reservationInPort.cancelReservation(
        CancelReservationRequest.builder()
            .basketReference(basket.getReference())
            .token(TOKEN)
            .build());

    //Assert
    assertThat(response, notNullValue());
    assertThat(response.getBasketReference(), is(basket.getReference()));
    verify(basketOutPort, never()).saveCharges(any());
    verify(basketOutPort).triggerEmailConfirmation(any());
  }

  @Test
  void cancelReservation_migrated_payNow_defaultPaymentMethod_success() {
    //Arrange
    var basket = mockBasketResponsePN(PI_BOOKING_CHANNEL);
    var cancelRequest = ManageReservationUtils.mockCancelReservationRequest(basket);
    ReservationByBasketRefResponse reservations = mockReservationByBasketRefCancellationResponse();
    reservations.getReservationByIdList()
        .forEach(
            reservation -> {
              reservation.getRateInfo().getSummary().setDeposit(BigDecimal.ZERO);
              reservation.setGuaranteeCode("DRV");
              reservation.setPaymentCard(ManageReservationUtils.mockPaymentCard());
            });
    reservations.setAmountPaid(BigDecimal.ZERO);
    when(basketOutPort.getBasketById(anyString())).thenReturn(basket);
    var refundedDeposits = new HashMap<String, DepositsResponse>();
    var depositsResponse = ManageReservationUtils.mockDepositsResponse();
    refundedDeposits.put("123", depositsResponse);
    doNothing().when(basketOutPort)
        .cancelBasket(basket.getReference(), false, true, depositsResponse.getDeposits());
    when(reservationOutPort.getReservationsByIds(any(), any(), anyBoolean()))
        .thenReturn(reservations);
    when(reservationOutPort.cancelReservation(any(), any())).thenReturn(
        new CancelReservationResponse(basket.getReference(), refundedDeposits, null));
    when(basketOutPort.getCharges(any())).thenReturn(DepositFoliosResponse.builder()
        .depositFolios(Collections.emptyList())
        .build());
    when(contentOutPort.getHotelPaymentInformation(anyString(), anyString(),
        anyString())).thenReturn(mockHotelPaymentInfoResponse());
    tokenUtilsMock.when(() -> TokenUtils.isValid(anyString(), anyString()))
        .thenReturn(Boolean.TRUE);

    //Act
    var response = reservationInPort.cancelReservation(cancelRequest);

    //Assert
    assertThat(response, notNullValue());
    assertThat(response.getBasketReference(), is(basket.getReference()));
    assertThat(cancelRequest.getDefaultPaymentMethod(), is("DVA"));
    verify(basketOutPort, never()).saveCharges(any());
    verify(basketOutPort).triggerEmailConfirmation(any());
  }

  @Test
  void cancelReservation_payNow_withDiffBtwAmounts_fails() {
    //Arrange
    var basket = ManageReservationUtils.mockPNBasketResponse();
    var reservations = ManageReservationUtils.mockReservationByBasketRefResponse();
    reservations.getReservationByIdList()
        .forEach(
            reservation -> reservation.getRateInfo().getSummary().setDeposit(BigDecimal.ZERO));
    CancelReservationRequest cancelReservationRequest = CancelReservationRequest.builder()
        .basketReference(basket.getReference())
        .token(TOKEN)
        .build();

    when(basketOutPort.getBasketById(anyString())).thenReturn(basket);
    doNothing().when(basketOutPort).cancelBasket(basket.getReference(), false, true, null);
    when(reservationOutPort.getReservationsByIds(any(), any(), anyBoolean()))
        .thenReturn(reservations);
    when(reservationOutPort.cancelReservation(any(), any())).thenReturn(
        new CancelReservationResponse(basket.getReference(), null, null));
    when(basketOutPort.getCharges(any())).thenReturn(DepositFoliosResponse.builder()
        .depositFolios(Collections.emptyList()).build());
    tokenUtilsMock.when(() -> TokenUtils.isValid(anyString(), anyString()))
        .thenReturn(Boolean.TRUE);
    when(basketOutPort.triggerRefundRequest(any(ReservationByBasketRefResponse.class),
        any(String.class)))
        .thenReturn(mockRefundResponse(Boolean.TRUE));

    //Assert
    Assertions.assertThrows(GenericReservationException.class,
        () -> reservationInPort.cancelReservation(cancelReservationRequest),
        "Refund request can't be triggered because amountToRefund differs from amountPaid!");
  }

  @Test
  void cancelReservation_payNow_withDiffBtwAmountPaidAndNegativeAmount_fails() {
    //Arrange
    var basket = ManageReservationUtils.mockPNBasketResponse();
    ReservationByBasketRefResponse reservations = ManageReservationUtils.mockReservationByBasketRefResponse();
    reservations.getReservationByIdList()
        .forEach(
            reservation -> reservation.getRateInfo().getSummary().setDeposit(BigDecimal.ZERO));
    CancelReservationRequest cancelReservationRequest = CancelReservationRequest.builder()
        .basketReference(basket.getReference())
        .token(TOKEN)
        .build();

    when(basketOutPort.getBasketById(anyString())).thenReturn(basket);
    doNothing().when(basketOutPort).cancelBasket(basket.getReference(), false, true, null);
    when(reservationOutPort.getReservationsByIds(any(), any(), anyBoolean()))
        .thenReturn(reservations);
    when(reservationOutPort.cancelReservation(any(), any())).thenReturn(
        new CancelReservationResponse(basket.getReference(), null, null));
    when(basketOutPort.getCharges(any())).thenReturn(
        ManageReservationUtils.mockDepositFoliosResponse());
    tokenUtilsMock.when(() -> TokenUtils.isValid(anyString(), anyString()))
        .thenReturn(Boolean.TRUE);
    when(basketOutPort.triggerRefundRequest(any(ReservationByBasketRefResponse.class),
        any(String.class)))
        .thenReturn(mockRefundResponse(Boolean.TRUE));

    //Assert
    Assertions.assertThrows(GenericReservationException.class,
        () -> reservationInPort.cancelReservation(cancelReservationRequest),
        "Refund request can't be triggered because amountToRefund differs from amountPaid!");
  }

  @Test
  void cancelReservation_ShouldReturnExceptionForInvalidToken() {

    // Arrange
    CancelReservationRequest cancelReservationRequest = CancelReservationRequest.builder()
        .basketReference("").token("invalid token").build();

    // Act and assert
    Assertions.assertThrows(InvalidTokenException.class,
        () -> reservationInPort.cancelReservation(cancelReservationRequest));
  }

  @Test
  void cancelReservation_tokenNotValidatedIfUserAuthenticated() {
    //Arrange
    var basket = ManageReservationUtils.mockPNBasketResponse();
    when(basketOutPort.getBasketById(anyString())).thenReturn(basket);
    doNothing().when(basketOutPort).cancelBasket(basket.getReference(), false, true, null);
    when(reservationOutPort.getReservationsByIds(any(), any(), anyBoolean()))
        .thenReturn(ManageReservationUtils.mockReservationByBasketRefResponse());
    when(reservationOutPort.cancelReservation(any(), any())).thenReturn(
        new CancelReservationResponse(basket.getReference(), null, null));
    when(basketOutPort.getCharges(any())).thenReturn(
        ManageReservationUtils.mockDepositFoliosResponse());
    when(authenticatedUserService.isUserAuthenticated()).thenReturn(Boolean.TRUE);
    when(basketOutPort.triggerRefundRequest(any(ReservationByBasketRefResponse.class),
        any(String.class)))
        .thenReturn(mockRefundResponse(Boolean.TRUE));
    when(unleashWrapper.featureFlag()).thenReturn(new FeatureFlag());
    when(unleashWrapper.isEnabled(unleashWrapper.featureFlag().getCcuiAgentIdLog())).thenReturn(
        false);

    //Act
    reservationInPort.cancelReservation(
        CancelReservationRequest.builder().basketReference(basket.getReference()).token(TOKEN)
            .build());

    //Assert
    tokenUtilsMock.verify(() -> TokenUtils.isValid(anyString(), anyString()), times(0));
  }

  @ParameterizedTest
  @ValueSource(booleans = {false, true})
  void cancelReservation_failedRefundWithBasketException_shouldTriggerEmail(
      boolean basketException) {
    //Arrange
    var basket = ManageReservationUtils.mockPNBasketResponse();
    var reservations = ManageReservationUtils.mockReservationByBasketRefResponse();
    var depositsResponse = ManageReservationUtils.mockDepositsResponse();
    var refundedDeposits = ManageReservationUtils.mockRefundedDeposits(depositsResponse);
    var captor = ArgumentCaptor.forClass(EmailRequest.class);
    var cancelRequest = ManageReservationUtils.mockCancelReservationRequest(basket);

    when(basketOutPort.getBasketById(anyString())).thenReturn(basket);
    doNothing().when(basketOutPort)
        .cancelBasket(basket.getReference(), false, true, depositsResponse.getDeposits());
    when(reservationOutPort.getReservationsByIds(any(), any(), anyBoolean()))
        .thenReturn(reservations);
    when(reservationOutPort.cancelReservation(any(), any())).thenReturn(
        new CancelReservationResponse(basket.getReference(), refundedDeposits, null));
    when(basketOutPort.getCharges(any())).thenReturn(DepositFoliosResponse.builder()
        .depositFolios(Collections.emptyList())
        .build());
    tokenUtilsMock.when(() -> TokenUtils.isValid(anyString(), anyString()))
        .thenReturn(Boolean.TRUE);
    Class thrownException;
    if (basketException) {
      doThrow(new BasketDigitalException(ErrorCode.DIGITAL_BASKET_ITEM_EXCEPTION, "test"))
          .when(basketOutPort)
          .triggerRefundRequest(any(ReservationByBasketRefResponse.class), any(String.class));
      thrownException = BasketDigitalException.class;
    } else {
      when(basketOutPort.triggerRefundRequest(any(ReservationByBasketRefResponse.class),
          any(String.class)))
          .thenReturn(mockRefundResponse(Boolean.FALSE));
      thrownException = CancelReservationException.class;
    }

    //Act
    assertThrows(thrownException, () -> reservationInPort.cancelReservation(cancelRequest));

    //Assert
    verify(basketOutPort).triggerEmailConfirmation(captor.capture());
    var emailRequest = captor.getValue();
    assertEquals(true, emailRequest.isFailedRefund());
  }

  @Test
  void cancelReservation_distrChannelAndPreStayCharges_success() {
    //Arrange
    var basket = ManageReservationUtils.mockBasketResponse();
    basket.setChannel(BookingChannel.DISTR_BOOKING_CHANNEL);
    var reservations = ManageReservationUtils.mockReservationByBasketRefResponseWithPreStayAmountPaid();
    when(basketOutPort.getBasketById(anyString())).thenReturn(basket);
    doNothing().when(basketOutPort).cancelBasket(basket.getReference(), false, true, null);
    when(basketOutPort.triggerRefundRequest(any(ReservationByBasketRefResponse.class),
        any(String.class)))
        .thenReturn(mockRefundResponse(Boolean.TRUE));
    when(reservationOutPort.getReservationsByIds(any(), any(), anyBoolean()))
        .thenReturn(reservations);
    when(reservationOutPort.cancelReservation(any(), any())).thenReturn(
        new CancelReservationResponse(basket.getReference(), null, null));
    tokenUtilsMock.when(() -> TokenUtils.isValid(anyString(), anyString()))
        .thenReturn(Boolean.TRUE);

    //Act
    var response = reservationInPort.cancelReservation(
        CancelReservationRequest.builder().basketReference(basket.getReference()).token(TOKEN)
            .build());

    //Assert
    assertThat(response, notNullValue());
    assertThat(response.getBasketReference(), is(basket.getReference()));
    verify(basketOutPort).cancelBasket(basket.getReference(), false, true, null);
    verify(basketOutPort).triggerEmailConfirmation(any());
  }

  @Test
  void testUpdateCompanyQuestionAndAnswer_success() {
    // Assert
    assertDoesNotThrow(() -> reservationInPort
        .updateCompanyQuestionAndAnswerDetails(createCompanyQuestionAndAnswerDetailsRequest()));
  }

  @Test
  void testUpdateDiscount_success() {
    // Assert
    assertDoesNotThrow(() -> reservationInPort.updateDiscount(createUpdateDiscountRequest()));
  }

  @Test
  void updateBusinessItems_Success() {
    //Assert
    assertDoesNotThrow(() -> reservationInPort.updateBusinessItems(createBusinessItemsRequest()));
  }

  @Test
  void updateSpecialRequests_Success() {
    //Assert
    assertDoesNotThrow(
        () -> reservationInPort.updateReservationSpecialRequests(createSpecialRequests()));
  }

  private SpecialRequests createSpecialRequests() {
    return SpecialRequests.builder()
        .hotelId("MANOLD")
        .build();
  }

  @Test
  void updateReasonForStay_success() {

    //Arrange
    FeatureFlag mockedFeatureFlag = mock(FeatureFlag.class);
    final var updateReasonForStayRequest = createUpdateReasonForStayRequest();
    when(unleashWrapper.featureFlag()).thenReturn(mockedFeatureFlag);
    when(unleashWrapper.isEnabled(mockedFeatureFlag.getReleasePiCcuiCityTaxUk()))
        .thenReturn(Boolean.FALSE);
    when(basketOutPort.getBasketById(anyString())).thenReturn(
        ManageReservationUtils.mockBasketResponse());

    //Act
    var updateReasonForStayResponse = reservationInPort
        .updateReasonForStay(updateReasonForStayRequest);

    //Assert
    verify(reservationOutPort, times(1)).updateReasonForStay(updateReasonForStayRequest);
    assertThat(updateReasonForStayResponse, notNullValue());
    assertThat(updateReasonForStayResponse.getBasketReference(), is(BASKET_REFERENCE));

  }

  @Test
  void updateReasonForStay_noUpdate() {
    //Arrange
    final var updateReasonForStayRequest = createUpdateReasonForStayRequest();
    var basket = ManageReservationUtils.mockBasketResponse();
    basket.setStatus(StatusEnum.PAY_PENDING.getValue());

    //Assert
    verify(reservationOutPort, times(0)).updateReasonForStay(updateReasonForStayRequest);

  }

  @Test
  void updateReasonForStay_for_city_tax_success() {

    //Arrange
    FeatureFlag mockedFeatureFlag = mock(FeatureFlag.class);
    final var updateReasonForStayRequest = createUpdateReasonForStayRequest();
    when(unleashWrapper.featureFlag()).thenReturn(mockedFeatureFlag);
    when(unleashWrapper.isEnabled(mockedFeatureFlag.getReleasePiCcuiCityTaxUk()))
        .thenReturn(Boolean.TRUE);
    when(contentOutPort.getHotelInformation(any(), any(), any()))
        .thenReturn(HotelInfoResponse.builder()
            .cityTax(HotelCityTax.builder()
                .bookingDateFrom(LocalDate.now().plusDays(2).toString())
                .effectiveFrom("2025-09-10")
                .build())
            .brand(PI_BOOKING_CHANNEL)
            .build());
    when(basketOutPort.getBasketById(anyString())).thenReturn(
        ManageReservationUtils.mockBasketResponse());

    //Act
    var updateReasonForStayResponse = reservationInPort
        .updateReasonForStay(updateReasonForStayRequest);

    //Assert
    assertThat(updateReasonForStayResponse, notNullValue());
    assertThat(updateReasonForStayResponse.getBasketReference(), is(BASKET_REFERENCE));

    verify(reservationOutPort).updateReasonForStay(reasonForStayCaptor.capture());
    UpdateReasonForStayRequest captured = reasonForStayCaptor.getValue();

    assertEquals("NTLEI", captured.getReasonForStay());

  }

  @Test
  void updateReasonForStay_for_city_tax_negative_scenario_invalid_date() {

    //Arrange
    FeatureFlag mockedFeatureFlag = mock(FeatureFlag.class);
    final var updateReasonForStayRequest = createUpdateReasonForStayRequest();
    when(unleashWrapper.featureFlag()).thenReturn(mockedFeatureFlag);
    when(unleashWrapper.isEnabled(mockedFeatureFlag.getReleasePiCcuiCityTaxUk()))
        .thenReturn(Boolean.TRUE);
    when(contentOutPort.getHotelInformation(any(), any(), any()))
        .thenReturn(HotelInfoResponse.builder()
            .cityTax(HotelCityTax.builder()
                .bookingDateFrom(LocalDate.now().plusDays(2).toString())
                .effectiveFrom("10-09-2025")
                .build())
            .brand(PI_BOOKING_CHANNEL)
            .build());
    when(basketOutPort.getBasketById(anyString())).thenReturn(
        ManageReservationUtils.mockBasketResponse());

    //Act
    assertThrows(DateTimeParseException.class, () -> {
      reservationInPort.updateReasonForStay(updateReasonForStayRequest);
    });

  }

  @Test
  void updateReasonForStay_for_city_tax_negative_scenario() {

    //Arrange
    FeatureFlag mockedFeatureFlag = mock(FeatureFlag.class);
    final var updateReasonForStayRequest = createUpdateReasonForStayRequest();
    when(unleashWrapper.featureFlag()).thenReturn(mockedFeatureFlag);
    when(unleashWrapper.isEnabled(mockedFeatureFlag.getReleasePiCcuiCityTaxUk()))
        .thenReturn(Boolean.TRUE);
    when(contentOutPort.getHotelInformation(any(), any(), any()))
        .thenReturn(HotelInfoResponse.builder()
            .cityTax(HotelCityTax.builder()
                .bookingDateFrom("23-08-10")
                .effectiveFrom("")
                .build())
            .brand(PI_BOOKING_CHANNEL)
            .build());
    when(basketOutPort.getBasketById(anyString())).thenReturn(
        ManageReservationUtils.mockBasketResponse());

    //Act
    var updateReasonForStayResponse = reservationInPort
        .updateReasonForStay(updateReasonForStayRequest);

    //Assert
    assertThat(updateReasonForStayResponse, notNullValue());
    assertThat(updateReasonForStayResponse.getBasketReference(), is(BASKET_REFERENCE));

    verify(reservationOutPort).updateReasonForStay(reasonForStayCaptor.capture());
    UpdateReasonForStayRequest captured = reasonForStayCaptor.getValue();

    assertEquals("LEI", captured.getReasonForStay());

  }

  @Test
  void updateReasonForStay_for_city_tax_with_empty_arrival_date() {

    //Arrange
    FeatureFlag mockedFeatureFlag = mock(FeatureFlag.class);
    final var updateReasonForStayRequest = UpdateReasonForStayRequest.builder()
        .hotelId("LONEUS")
        .reasonForStay("LEI")
        .basketReference(BASKET_REFERENCE)
        .arrivalDate("")
        .build();
    when(unleashWrapper.featureFlag()).thenReturn(mockedFeatureFlag);
    when(unleashWrapper.isEnabled(mockedFeatureFlag.getReleasePiCcuiCityTaxUk()))
        .thenReturn(Boolean.TRUE);
    when(contentOutPort.getHotelInformation(any(), any(), any()))
        .thenReturn(HotelInfoResponse.builder()
            .cityTax(HotelCityTax.builder()
                .bookingDateFrom(LocalDate.now().plusDays(2).toString())
                .effectiveFrom("2025-09-10")
                .build())
            .brand(PI_BOOKING_CHANNEL)
            .build());
    when(basketOutPort.getBasketById(anyString())).thenReturn(
        ManageReservationUtils.mockBasketResponse());
    when(reservationOutPort.getReservationsByIds("LONEUS", List.of("res1", "res3", "res2"), false, false, false))
        .thenReturn(ManageReservationUtils.mockReservationByBasketRefResponse());

    //Act
    var updateReasonForStayResponse = reservationInPort
        .updateReasonForStay(updateReasonForStayRequest);

    //Assert
    assertThat(updateReasonForStayResponse, notNullValue());
    assertThat(updateReasonForStayResponse.getBasketReference(), is(BASKET_REFERENCE));

    verify(reservationOutPort).updateReasonForStay(reasonForStayCaptor.capture());
    UpdateReasonForStayRequest captured = reasonForStayCaptor.getValue();

    assertEquals("NTLEI", captured.getReasonForStay());

  }

  @Test
  void testUpdateReservationOverrideReasons_success() {
    //Arrange
    final var updateReservationOverrideReasonsRequest =
        createUpdateReservationOverrideReasonsRequest();
    when(basketOutPort.getBasketById(anyString())).thenReturn(
        ManageReservationUtils.mockBasketResponse());

    //Act
    var updateReservationOverrideReasonsResponse =
        reservationInPort.updateReservationOverrideReasons(updateReservationOverrideReasonsRequest);

    //Assert
    verify(reservationOutPort, times(1))
        .updateReservationOverrideReasons(updateReservationOverrideReasonsRequest);
    assertThat(updateReservationOverrideReasonsResponse, notNullValue());
    assertThat(updateReservationOverrideReasonsResponse.getBasketReference(), is("GBM6919649"));

  }

  @Test
  void testGetDepositsByResId_success() {
    String hotelID = "DAHMME";
    String bookingReference = "12345678";
    var depositsResponse = new DepositsResponse();
    depositsResponse.setDeposits(
        Collections.singletonList(Deposits.builder().paymentReference(PAYMENT_REFERENCE)
            .build()));
    // Arrange
    when(reservationOutPort.getDepositsForReservationId(hotelID, bookingReference))
        .thenReturn(depositsResponse);

    // Act
    var response = reservationInPort.getDepositsForReservationId(hotelID, bookingReference);

    // Assert
    verifyNoMoreInteractions(reservationOutPort);
    assertThat(response.getDeposits().get(0).getPaymentReference(), is(PAYMENT_REFERENCE));
  }

  @Test
  void getCancellationPolicies__ShouldReturnOk() {

    //Arrange
    String hotelId = "HOTELTEST";
    Set<String> reservationIds = Collections.singleton("147");
    when(basketOutPort.getBasketById("basketCancellation")).thenReturn(
        mockBasketForCancellationPoliciesResponse());
    when(reservationOutPort
        .getCancellationPolicies(reservationIds, hotelId, null, null))
        .thenReturn(createCancellationPolicies());

    //Act
    var cancellationPolicies = reservationInPort.getCancellationPolicies("basketCancellation",
        hotelId, null, null);

    //Assert
    verifyNoMoreInteractions(reservationOutPort);
    assertThat(cancellationPolicies.getText(),
        is("Cancellations after 1pm on the day of arrival charged 100% of 1 night"));
  }

  @ParameterizedTest
  @CsvSource({
      "true, true",
      "false, false"
  })
  void addNewRoomToExistingBasket__ShouldReturnOK(Boolean useBasketAllowances,
      Boolean aemSearchRules) {
    //Arrange
    FeatureFlag mockedFeatureFlag = mock(FeatureFlag.class);
    when(unleashWrapper.featureFlag())
        .thenReturn(mockedFeatureFlag);
    when(unleashWrapper.isEnabled(mockedFeatureFlag.getUseBasketAllowances()))
        .thenReturn(useBasketAllowances);
    when(unleashWrapper.isEnabled(mockedFeatureFlag.getAemSearchRules()))
        .thenReturn(aemSearchRules);

    var basketReference = TEMPORARY_BASKET_REF;
    var originalBasketReference = ORIGINAL_BASKET_REF;
    var operaCompanyId = "testCompanyId";
    var reservationRequest = createValidHotelReservationRequest();
    var roomOccupancyData = MaxRoomOccupancyData.builder()
        .adultsNumber(1)
        .childrenNumber(0)
        .acceptedRoomTypes(Arrays.asList("SDB", "DBLDBL"))
        .build();
    var maxRoomOccupancy = MaxRoomOccupancyResponse.builder()
        .channelId("TestChannelId")
        .roomOccupancies(List.of(roomOccupancyData))
        .generatedAt(new Date())
        .build();
    var basketItem = BasketItemResponse.builder()
        .type("reservation").sourceId("resId").build();
    var basket = ManageReservationUtils.mockBasketResponse();
    basket.setItems(List.of(basketItem));
    basket.setETag("abc/123");
    basket.setOriginalBasketId(originalBasketReference);
    basket.setPaymentOption(ACCOUNT_COMPANY);
    var originalBasket = ManageReservationUtils.mockBasketResponse();
    originalBasket.setItems(List.of(basketItem));
    originalBasket.setETag("abc/123");
    originalBasket.setPaymentOption(ACCOUNT_COMPANY);

    var reservationByBasketRefResponse = new ReservationByBasketRefResponse();
    reservationByBasketRefResponse.setReservationByIdList(
        List.of(ManageReservationUtils.mockReservationByIdFlex("11111111")));
    reservationByBasketRefResponse.setCompanyId(operaCompanyId);
    var ohipReservationResponse = mockOhipReservationResponse();
    var bookingAllowancesResponse = mockCreateBookingAllowancesResponse();
    if (useBasketAllowances) {
      originalBasket.setBookingAllowances(bookingAllowancesResponse.getBookingAllowances());
    }
    List<String> basketBookingAllowances  =
        originalBasket.getBookingAllowances() != null ?
            originalBasket.getBookingAllowances().stream()
                .map(BookingAllowance::getAllowance).toList() :
            List.of();

    when(basketOutPort.getBasketById(basketReference)).thenReturn(basket);
    when(basketOutPort.getBasketById(originalBasketReference)).thenReturn(originalBasket);
    when(contentOutPort.getHotelInformation(any(), any(), any()))
        .thenReturn(HotelInfoResponse.builder().brand(PI_BOOKING_CHANNEL).build());

    if (aemSearchRules) {
      when(contentOutPort.getSearchRules(anyString(), any())).thenReturn(
          SearchRules.builder()
              .maxRoomsAmend(4)
              .roomOccupancies(List.of(
                  RoomOccupancy.builder()
                      .adultsNumber(1)
                      .childrenNumber(0)
                      .acceptedRoomTypes(List.of("SDB", "DBLDBL"))
                      .build()))
              .build());
    } else {
      when(rulesOutPort.getMaxRoomsRule(any())).thenReturn(new MaxRoomsRuleResponse(4));
      when(rulesOutPort.getMaxRoomOccupancyRule(any(), any())).thenReturn(maxRoomOccupancy);
    }
    when(reservationOutPort.getReservationsByIds(anyString(), any(), anyBoolean(), anyBoolean(),
        anyBoolean()))
        .thenReturn(reservationByBasketRefResponse);
    tokenUtilsMock.when(() -> TokenUtils.isValid(anyString(), anyString()))
        .thenReturn(Boolean.TRUE);

    ArgumentCaptor<ReservationRequest> reservationRequestCaptor = ArgumentCaptor.forClass(
        ReservationRequest.class);
    when(reservationOutPort.createReservation(anyString(), any(), anyString()))
        .thenReturn(ohipReservationResponse);
    if (useBasketAllowances) {
      when(rulesOutPort.getSingleOccupancySupplementResponse(any()))
          .thenReturn(new SingleOccupancySupplementResponse("TESTHOTEL", BigDecimal.ZERO));
      when(basketOutPort.addReservationsToBasket(eq(basketReference), eq(basket.getETag()),
          eq(ohipReservationResponse), eq(false), eq(false), anyInt(), eq(false))).thenReturn(
          basket);
    } else {
      when(basketOutPort.addReservationsToBasket(eq(basketReference), eq(basket.getETag()),
          eq(ohipReservationResponse), eq(false), eq(false), eq(null), eq(false))).thenReturn(
          basket);
    }

    when(reservationOutPort.getBookingAllowances("TESTHOTEL", "resId", basketBookingAllowances))
        .thenReturn(bookingAllowancesResponse);

    //Act
    var reservationResponse = reservationInPort.addNewRoomToExistingBasket(
        TEMPORARY_BASKET_REF, reservationRequest, null);
    //Assert

    if (!useBasketAllowances) {
      verify(basketOutPort, never()).updateAllowances(any(), any(), any());
    } else {
      verify(basketOutPort, times(1)).updateAllowances(any(), any(), any());
    }

    verify(reservationOutPort, times(1)).updateBusinessItems(any(BusinessItemsRequest.class));
    assertEquals(TEMPORARY_BASKET_REF, reservationResponse.getTempBookingRef());
    verify(reservationOutPort).createReservation(any(), reservationRequestCaptor.capture(), anyString());
    var capturedReservationRequest = reservationRequestCaptor.getValue();
    assertEquals(capturedReservationRequest.getReservations().get(0).getOperaCompanyId(),
        operaCompanyId);
  }

  @ParameterizedTest
  @CsvSource({
          "true, true",
          "false, false"
  })
  void addNewRoomToExistingBasketForStayNull(Boolean useBasketAllowances,
                                                  Boolean aemSearchRules) {
    //Arrange
    FeatureFlag mockedFeatureFlag = mock(FeatureFlag.class);
    when(unleashWrapper.featureFlag())
            .thenReturn(mockedFeatureFlag);
    when(unleashWrapper.isEnabled(mockedFeatureFlag.getUseBasketAllowances()))
            .thenReturn(useBasketAllowances);
    when(unleashWrapper.isEnabled(mockedFeatureFlag.getAemSearchRules()))
            .thenReturn(aemSearchRules);

    var basketReference = TEMPORARY_BASKET_REF;
    var originalBasketReference = ORIGINAL_BASKET_REF;
    var operaCompanyId = "testCompanyId";
    var reservationRequest = createValidHotelReservationRequest();
    var roomOccupancyData = MaxRoomOccupancyData.builder()
            .adultsNumber(1)
            .childrenNumber(0)
            .acceptedRoomTypes(Arrays.asList("SDB", "DBLDBL"))
            .build();
    var maxRoomOccupancy = MaxRoomOccupancyResponse.builder()
            .channelId("TestChannelId")
            .roomOccupancies(List.of(roomOccupancyData))
            .generatedAt(new Date())
            .build();
    var basketItem = BasketItemResponse.builder()
            .type("reservation").sourceId("resId").build();
    var basket = ManageReservationUtils.mockBasketResponse();
    basket.setItems(List.of(basketItem));
    basket.setETag("abc/123");
    basket.setOriginalBasketId(originalBasketReference);
    basket.setPaymentOption(ACCOUNT_COMPANY);
    var originalBasket = ManageReservationUtils.mockBasketResponse();
    originalBasket.setItems(List.of(basketItem));
    originalBasket.setETag("abc/123");
    originalBasket.setPaymentOption(ACCOUNT_COMPANY);

    var reservationByBasketRefResponse = new ReservationByBasketRefResponse();
    reservationByBasketRefResponse.setReservationByIdList(
            List.of(ManageReservationUtils.mockReservationForStayNullByIdFlex("11111111")));
    reservationByBasketRefResponse.setCompanyId(operaCompanyId);
    var ohipReservationResponse = mockOhipReservationResponse();
    var bookingAllowancesResponse = mockCreateBookingAllowancesResponse();
    if (useBasketAllowances) {
      originalBasket.setBookingAllowances(bookingAllowancesResponse.getBookingAllowances());
    }
    List<String> basketBookingAllowances  =
            originalBasket.getBookingAllowances() != null ?
                    originalBasket.getBookingAllowances().stream()
                            .map(BookingAllowance::getAllowance).toList() :
                    List.of();

    when(basketOutPort.getBasketById(basketReference)).thenReturn(basket);
    when(basketOutPort.getBasketById(originalBasketReference)).thenReturn(originalBasket);
    when(contentOutPort.getHotelInformation(any(), any(), any()))
            .thenReturn(HotelInfoResponse.builder().brand(PI_BOOKING_CHANNEL).build());

    if (aemSearchRules) {
      when(contentOutPort.getSearchRules(anyString(), any())).thenReturn(
              SearchRules.builder()
                      .maxRoomsAmend(4)
                      .roomOccupancies(List.of(
                              RoomOccupancy.builder()
                                      .adultsNumber(1)
                                      .childrenNumber(0)
                                      .acceptedRoomTypes(List.of("SDB", "DBLDBL"))
                                      .build()))
                      .build());
    } else {
      when(rulesOutPort.getMaxRoomsRule(any())).thenReturn(new MaxRoomsRuleResponse(4));
      when(rulesOutPort.getMaxRoomOccupancyRule(any(), any())).thenReturn(maxRoomOccupancy);
    }
    when(reservationOutPort.getReservationsByIds(anyString(), any(), anyBoolean(), anyBoolean(),
            anyBoolean()))
            .thenReturn(reservationByBasketRefResponse);
    tokenUtilsMock.when(() -> TokenUtils.isValid(anyString(), anyString()))
            .thenReturn(Boolean.TRUE);

    ArgumentCaptor<ReservationRequest> reservationRequestCaptor = ArgumentCaptor.forClass(
            ReservationRequest.class);
    when(reservationOutPort.createReservation(anyString(), any(), anyString()))
            .thenReturn(ohipReservationResponse);
    if (useBasketAllowances) {
      when(rulesOutPort.getSingleOccupancySupplementResponse(any()))
              .thenReturn(new SingleOccupancySupplementResponse("TESTHOTEL", BigDecimal.ZERO));
      when(basketOutPort.addReservationsToBasket(eq(basketReference), eq(basket.getETag()),
              eq(ohipReservationResponse), eq(false), eq(false), anyInt(), eq(false))).thenReturn(
              basket);
    } else {
      when(basketOutPort.addReservationsToBasket(eq(basketReference), eq(basket.getETag()),
              eq(ohipReservationResponse), eq(false), eq(false), eq(null), eq(false))).thenReturn(
              basket);
    }

    when(reservationOutPort.getBookingAllowances("TESTHOTEL", "resId", basketBookingAllowances))
            .thenReturn(bookingAllowancesResponse);

    //Act
    var reservationResponse = reservationInPort.addNewRoomToExistingBasket(
            TEMPORARY_BASKET_REF, reservationRequest, null);
    //Assert

    if (!useBasketAllowances) {
      verify(basketOutPort, never()).updateAllowances(any(), any(), any());
    } else {
      verify(basketOutPort, times(1)).updateAllowances(any(), any(), any());
    }

    verify(reservationOutPort, times(1)).updateBusinessItems(any(BusinessItemsRequest.class));
    assertEquals(TEMPORARY_BASKET_REF, reservationResponse.getTempBookingRef());
    verify(reservationOutPort).createReservation(any(), reservationRequestCaptor.capture(), anyString());
    var capturedReservationRequest = reservationRequestCaptor.getValue();
    assertEquals(capturedReservationRequest.getReservations().get(0).getOperaCompanyId(),
            operaCompanyId);
  }

  @ParameterizedTest
  @CsvSource({
      "true, true, true",
      "true, true, false",
      "true, false, true",
      "true, false, false",
      "false, true, true",
      "false, true, false",
      "false, false, true",
      "false, false, false"
  })
  void addNewRoomToExistingBasket_WithSpecialRequests__ShouldReturnOK(Boolean useBasketAllowances,
      Boolean aemSearchRulesFf, Boolean maxRoomsAmendFf) {
    //Arrange
    var basketReference = ORIGINAL_BASKET_REF;
    var originalBasketReference = ORIGINAL_BASKET_REF;
    var reservationRequest = createValidHotelReservationRequest();
    reservationRequest.getBookingChannel().setChannel("DISTR");
    reservationRequest.getBookingChannel().setSubchannel("AGENCY");
    reservationRequest.getReservations().get(0).getRoomRates().setSpecialRequests(List.of("XPL"));
    var roomOccupancyData = MaxRoomOccupancyData.builder()
        .adultsNumber(1)
        .childrenNumber(0)
        .acceptedRoomTypes(Arrays.asList("SDB", "DBLDBL"))
        .build();
    var maxRoomOccupancy = MaxRoomOccupancyResponse.builder()
        .channelId("TestChannelId")
        .roomOccupancies(List.of(roomOccupancyData))
        .generatedAt(new Date())
        .build();
    var basketItem = BasketItemResponse.builder()
        .type("reservation").sourceId("resId").build();
    var basket = ManageReservationUtils.mockBasketResponse();
    basket.setItems(List.of(basketItem));
    basket.setETag("abc/123");
    basket.setOriginalBasketId(originalBasketReference);
    basket.setPaymentOption(ACCOUNT_COMPANY);
    var originalBasket = ManageReservationUtils.mockBasketResponse();
    originalBasket.setItems(List.of(basketItem));
    originalBasket.setETag("abc/123");
    originalBasket.setPaymentOption(ACCOUNT_COMPANY);

    var reservationByBasketRefResponse = new ReservationByBasketRefResponse();
    reservationByBasketRefResponse.setReservationByIdList(
        List.of(ManageReservationUtils.mockReservationByIdFlex("11111111")));
    var ohipReservationResponse = mockOhipReservationResponse();
    var bookingAllowancesResponse = mockCreateBookingAllowancesResponse();
    var mockedFeatureFlag = mock(FeatureFlag.class);
    List<String> basketBookingAllowances  =
        basket.getBookingAllowances() != null ?
            basket.getBookingAllowances().stream()
                .map(BookingAllowance::getAllowance).toList() :
            List.of();

    when(unleashWrapper.featureFlag()).thenReturn(mockedFeatureFlag);

    when(basketOutPort.getBasketById(basketReference)).thenReturn(basket);
    when(basketOutPort.getBasketById(BASKET_REFERENCE)).thenReturn(basket);
    when(basketOutPort.getBasketById(originalBasketReference)).thenReturn(originalBasket);
    when(contentOutPort.getHotelInformation(any(), any(), any())).thenReturn(
        HotelInfoResponse.builder().brand(PI_BOOKING_CHANNEL).build());
    if (aemSearchRulesFf) {
      var mockedFeatureMaxRoomAmend = mock(FeatureFlag.Feature.class);
      when(mockedFeatureFlag.getMaxRoomsAmend()).thenReturn(mockedFeatureMaxRoomAmend);
      when(unleashWrapper.isEnabled(mockedFeatureFlag.getMaxRoomsAmend())).thenReturn(
          maxRoomsAmendFf);
      when(contentOutPort.getSearchRules(anyString(), any())).thenReturn(
          SearchRules.builder()
              .maxRoomsAmend(4)
              .roomOccupancies(List.of(
                  RoomOccupancy.builder()
                      .adultsNumber(1)
                      .childrenNumber(0)
                      .acceptedRoomTypes(List.of("SDB", "DBLDBL"))
                      .build()))
              .build());

      if (maxRoomsAmendFf) {
        when(contentOutPort.getSearchRules(anyString(), any())).thenReturn(
            SearchRules.builder()
                .maxRoomsAmend(4)
                .roomOccupancies(List.of(
                    RoomOccupancy.builder()
                        .adultsNumber(1)
                        .childrenNumber(0)
                        .acceptedRoomTypes(List.of("SDB", "DBLDBL"))
                        .build()))
                .build());
      } else {
        when(contentOutPort.getSearchRules(anyString(), any())).thenReturn(
            SearchRules.builder()
                .maxRooms(4)
                .roomOccupancies(List.of(
                    RoomOccupancy.builder()
                        .adultsNumber(1)
                        .childrenNumber(0)
                        .acceptedRoomTypes(List.of("SDB", "DBLDBL"))
                        .build()))
                .build());
      }
    } else {
      when(rulesOutPort.getMaxRoomsRule(any())).thenReturn(new MaxRoomsRuleResponse(4));
      when(rulesOutPort.getMaxRoomOccupancyRule(any(), any())).thenReturn(maxRoomOccupancy);
    }

    when(reservationOutPort.getReservationsByIds(anyString(), any(), anyBoolean(), anyBoolean(),
        anyBoolean()))
        .thenReturn(reservationByBasketRefResponse);
    tokenUtilsMock.when(() -> TokenUtils.isValid(anyString(), anyString()))
        .thenReturn(Boolean.TRUE);
    when(reservationOutPort.createReservation(anyString(), any(), anyString()))
        .thenReturn(ohipReservationResponse);
    when(basketOutPort.addReservationsToBasket(eq(basketReference), eq(basket.getETag()),
        eq(ohipReservationResponse), eq(false), eq(false), eq(null), eq(false))).thenReturn(
        basket);

    var mockedFeatureApplyOccupancySupplement = mock(FeatureFlag.Feature.class);
    when(mockedFeatureFlag.getApplyOccupancySupplement()).thenReturn(
        mockedFeatureApplyOccupancySupplement);
    when(unleashWrapper.isEnabled(mockedFeatureFlag.getApplyOccupancySupplement())).thenReturn(
        false);

    var mockedFeatureUseBasketAllowances = mock(FeatureFlag.Feature.class);
    when(mockedFeatureFlag.getUseBasketAllowances()).thenReturn(mockedFeatureUseBasketAllowances);
    when(unleashWrapper.isEnabled(mockedFeatureFlag.getUseBasketAllowances())).thenReturn(
        useBasketAllowances);

    var mockedFeatureAemSearchRules = mock(FeatureFlag.Feature.class);
    when(mockedFeatureFlag.getAemSearchRules()).thenReturn(mockedFeatureAemSearchRules);
    when(unleashWrapper.isEnabled(mockedFeatureFlag.getAemSearchRules())).thenReturn(
        aemSearchRulesFf);


    when(reservationOutPort.getBookingAllowances("TESTHOTEL", "resId", basketBookingAllowances))
        .thenReturn(bookingAllowancesResponse);

    if (useBasketAllowances) {
      originalBasket.setBookingAllowances(bookingAllowancesResponse.getBookingAllowances());
    }

    //Act
    var reservationResponse = reservationInPort.addNewRoomToExistingBasket(
        ORIGINAL_BASKET_REF, reservationRequest, null);

    //Assert
    verify(unleashWrapper, times(2)).isEnabled(mockedFeatureFlag.getAemSearchRules());
    assertEquals(ORIGINAL_BASKET_REF, reservationResponse.getTempBookingRef());
  }

  @Test
  void addNewRoomToExistingBasket_withOcc_ShouldReturnOK() {
// Arrange
    FeatureFlag mockedFeatureFlag = mock(FeatureFlag.class);
    when(unleashWrapper.featureFlag()).thenReturn(mockedFeatureFlag);
    var useBasketAllowancesFeature = mock(FeatureFlag.Feature.class);
    when(mockedFeatureFlag.getUseBasketAllowances()).thenReturn(useBasketAllowancesFeature);
    when(unleashWrapper.isEnabled(useBasketAllowancesFeature)).thenReturn(false);
    var aemSearchRulesFeature = mock(FeatureFlag.Feature.class);
    when(mockedFeatureFlag.getAemSearchRules()).thenReturn(aemSearchRulesFeature);
    when(unleashWrapper.isEnabled(aemSearchRulesFeature)).thenReturn(false);
    when(unleashWrapper.isEnabled(mockedFeatureFlag.getApplyOccupancySupplement())).thenReturn(
        true);

    var basketReference = TEMPORARY_BASKET_REF;
    var originalBasketReference = ORIGINAL_BASKET_REF;
    var operaCompanyId = "testCompanyId";
    var reservationRequest = createValidHotelReservationRequest();

    //must be within 364 days period
    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    String today = LocalDate.now().format(formatter);
    String tomorrow = LocalDate.now().plusDays(1).format(formatter);
    reservationRequest.getReservations().get(0).setArrival(today);
    reservationRequest.getReservations().get(0).setDeparture(tomorrow);
    reservationRequest.getReservations().get(0).getRoomRates().setStartDate(today);
    reservationRequest.getReservations().get(0).getRoomRates().setEndDate(tomorrow);
    reservationRequest.setIsOta(true);
    var roomOccupancyData = MaxRoomOccupancyData.builder()
        .adultsNumber(1)
        .childrenNumber(0)
        .acceptedRoomTypes(Arrays.asList("SDB", "DBLDBL"))
        .build();
    var maxRoomOccupancy = MaxRoomOccupancyResponse.builder()
        .channelId("TestChannelId")
        .roomOccupancies(List.of(roomOccupancyData))
        .generatedAt(new Date())
        .build();
    var basketItem = BasketItemResponse.builder()
        .type("reservation").sourceId("resId").build();
    var basket = ManageReservationUtils.mockBasketResponse();
    basket.setItems(List.of(basketItem));
    basket.setETag("abc/123");
    basket.setOriginalBasketId(originalBasketReference);
    basket.setPaymentOption(ACCOUNT_COMPANY);
    var originalBasket = ManageReservationUtils.mockBasketResponse();
    originalBasket.setItems(List.of(basketItem));
    originalBasket.setETag("abc/123");
    originalBasket.setPaymentOption(ACCOUNT_COMPANY);

    var reservationByBasketRefResponse = new ReservationByBasketRefResponse();
    reservationByBasketRefResponse.setReservationByIdList(
        List.of(ManageReservationUtils.mockReservationByIdFlex("11111111")));
    reservationByBasketRefResponse.setCompanyId(operaCompanyId);
    reservationByBasketRefResponse.getReservationByIdList().get(0).getRoomStay()
        .setArrivalDate(today);
    reservationByBasketRefResponse.getReservationByIdList().get(0).getRoomStay()
        .setDepartureDate(tomorrow);

    var ohipReservationResponse = mockOhipReservationResponse();
    ohipReservationResponse.getReservations().get(0).setCreateDateTime(today);
    ohipReservationResponse.getReservations().get(0).getRoomStay()
        .setArrivalDate(LocalDate.parse(today));
    ohipReservationResponse.getReservations().get(0).getRoomStay()
        .setDepartureDate(LocalDate.parse(tomorrow));
    var bookingAllowancesResponse = mockCreateBookingAllowancesResponse();
    var availabilityResponse = createValidAvailabilityV2Response();
    List<String> basketBookingAllowances  =
        basket.getBookingAllowances() != null ?
            basket.getBookingAllowances().stream()
                .map(BookingAllowance::getAllowance).toList() :
            List.of();

    when(basketOutPort.getBasketById(basketReference)).thenReturn(basket);
    when(basketOutPort.getBasketById(originalBasketReference)).thenReturn(originalBasket);
    when(contentOutPort.getHotelInformation(any(), any(), any())).thenReturn(
        HotelInfoResponse.builder().brand(PI_BOOKING_CHANNEL).build());
    when(rulesOutPort.getMaxRoomOccupancyRule(any(), any())).thenReturn(maxRoomOccupancy);

    when(rulesOutPort.getMaxRoomsRule(any())).thenReturn(new MaxRoomsRuleResponse(4));
    when(rulesOutPort.getSingleOccupancySupplementResponse(any()))
        .thenReturn(new SingleOccupancySupplementResponse("TESTHOTEL", new BigDecimal(10)));

    when(reservationOutPort.getReservationsByIds(anyString(), any(), anyBoolean(), anyBoolean(),
        anyBoolean())).thenReturn(reservationByBasketRefResponse);
    tokenUtilsMock.when(() -> TokenUtils.isValid(anyString(), anyString()))
        .thenReturn(Boolean.TRUE);

    ArgumentCaptor<ReservationRequest> reservationRequestCaptor = ArgumentCaptor.forClass(
        ReservationRequest.class);
    when(reservationOutPort.createReservation(anyString(), any(), anyString())).thenReturn(
        ohipReservationResponse);
    when(availabilityOutPort.getHotelAvailabilitiesByIdsV2(any())).thenReturn(
        availabilityResponse);
    when(basketOutPort.addReservationsToBasket(eq(basketReference), eq(basket.getETag()),
        any(OhipReservationResponse.class), eq(false), eq(true), eq(1), eq(true))).thenReturn(
        basket);

    when(reservationOutPort.getBookingAllowances("TESTHOTEL", "resId", basketBookingAllowances))
        .thenReturn(bookingAllowancesResponse);

// Act
    var reservationResponse = reservationInPort.addNewRoomToExistingBasket(
        TEMPORARY_BASKET_REF, reservationRequest, null);

// Assert
    verify(reservationOutPort, times(1)).updateBusinessItems(any(BusinessItemsRequest.class));
    assertEquals(TEMPORARY_BASKET_REF, reservationResponse.getTempBookingRef());
    verify(reservationOutPort).createReservation(any(), reservationRequestCaptor.capture(), anyString());
    var capturedReservationRequest = reservationRequestCaptor.getValue();
    assertEquals(operaCompanyId,
        capturedReservationRequest.getReservations().get(0).getOperaCompanyId());
  }

  @Test
  void addNewRoomToExistingBasket_selfChange_ShouldThrowException() {
    //Arrange
    var accountBuilder = uk.co.whitbread.shared.auth.account.Account.builder();
    var account = accountBuilder.accessLevel("SUPER")
        .accessLevel("SELF").build();
    var reservationRequest = createValidHotelReservationRequest();

    when(authenticatedUserService.getCurrentUserAccount()).thenReturn(
        Optional.ofNullable(account));

    Exception exception = assertThrows(GenericBadRequestException.class,
        () -> reservationInPort.addNewRoomToExistingBasket(
            TEMPORARY_BASKET_REF, reservationRequest, null));
    String expectedMessage = "Self booker can't add new room";
    String actualMessage = exception.getMessage();
    assertTrue(actualMessage.contains(expectedMessage));
  }

  @Test
  void addNewRoomToExistingBasket_userNotAuthenticated_ShouldThrowException() {
    //Arrange
    var accountBuilder = uk.co.whitbread.shared.auth.account.Account.builder();
    var account = accountBuilder.accessLevel("SUPER").build();
    var basketReference = TEMPORARY_BASKET_REF;
    var reservationRequest = createValidHotelReservationRequest();
    var basket = ManageReservationUtils.mockBasketResponse();

    when(basketOutPort.getBasketById(basketReference)).thenReturn(basket);
    when(authenticatedUserService.getCurrentUserAccount()).thenReturn(
        Optional.ofNullable(account));
    when(authenticatedUserService.isUserAuthenticated()).thenReturn(false);

    Exception exception = assertThrows(InvalidTokenException.class,
        () -> reservationInPort.addNewRoomToExistingBasket(
            TEMPORARY_BASKET_REF, reservationRequest, null));
    String expectedMessage = "Invalid token";
    String actualMessage = exception.getMessage();
    assertTrue(actualMessage.contains(expectedMessage));
  }

  @ParameterizedTest
  @CsvSource({
      "true, true",
      "true, false",
      "false, true",
      "false, false"
  })
  void addNewRoomToExistingBasket_toManyRooms_ShouldThrowException(Boolean aemSearchRulesFf,
      Boolean maxRoomsAmendFf) {
    //Arrange
    var accountBuilder = uk.co.whitbread.shared.auth.account.Account.builder();
    var account = accountBuilder.accessLevel("SUPER").build();
    var basketReference = TEMPORARY_BASKET_REF;
    var reservationRequest = createValidEmployeeHotelReservationRequest();
    var basket = ManageReservationUtils.mockBasketResponseMultipleRooms(null);
    var mockedFeatureFlag = mock(FeatureFlag.class);

    when(unleashWrapper.featureFlag())
        .thenReturn(mockedFeatureFlag);


    var mockedFeatureAemSearchRules = mock(FeatureFlag.Feature.class);
    when(mockedFeatureFlag.getAemSearchRules()).thenReturn(mockedFeatureAemSearchRules);
    when(unleashWrapper.isEnabled(mockedFeatureFlag.getAemSearchRules())).thenReturn(
        aemSearchRulesFf);

    when(basketOutPort.getBasketById(basketReference)).thenReturn(basket);
    when(authenticatedUserService.getCurrentUserAccount()).thenReturn(
        Optional.ofNullable(account));
    when(authenticatedUserService.isUserAuthenticated()).thenReturn(true);

    if (aemSearchRulesFf) {
      var mockedFeatureMaxRoomAmend = mock(FeatureFlag.Feature.class);
      when(mockedFeatureFlag.getMaxRoomsAmend()).thenReturn(mockedFeatureMaxRoomAmend);
      when(unleashWrapper.isEnabled(mockedFeatureFlag.getMaxRoomsAmend())).thenReturn(
          maxRoomsAmendFf);

      if (maxRoomsAmendFf) {
        when(contentOutPort.getSearchRules(anyString(), any())).thenReturn(
            SearchRules.builder().maxRoomsAmend(1).build());
      } else {
        when(contentOutPort.getSearchRules(anyString(), any())).thenReturn(
            SearchRules.builder().maxRooms(1).build());
      }
    } else {
      when(rulesOutPort.getMaxRoomsRule(any())).thenReturn(new MaxRoomsRuleResponse(1));
    }

    //Act
    Exception exception = assertThrows(GenericBadRequestException.class,
        () -> reservationInPort.addNewRoomToExistingBasket(
            TEMPORARY_BASKET_REF, reservationRequest, null));
    String expectedMessage = "Too many rooms booked, please contact call center";
    String actualMessage = exception.getMessage();

    //Assert
    assertTrue(actualMessage.contains(expectedMessage));
  }

  @Test
  void addNewRoomToExistingBasket_ExpiredBasket_ShouldThrowException() {
    //Arrange
    var accountBuilder = uk.co.whitbread.shared.auth.account.Account.builder();
    var account = accountBuilder.accessLevel("SUPER").build();
    var basketReference = TEMPORARY_BASKET_REF;
    var reservationRequest = createValidHotelReservationRequest();
    var basket = ManageReservationUtils.mockBasketResponse();
    basket.setStatus("CLOSED");

    when(basketOutPort.getBasketById(basketReference)).thenReturn(basket);
    when(authenticatedUserService.getCurrentUserAccount()).thenReturn(
        Optional.ofNullable(account));
    tokenUtilsMock.when(() -> TokenUtils.isValid(anyString(), anyString()))
        .thenReturn(Boolean.TRUE);

    //Act
    //Assert
    Exception exception = assertThrows(GenericBadRequestException.class,
        () -> reservationInPort.addNewRoomToExistingBasket(
            TEMPORARY_BASKET_REF, reservationRequest, null));
    String expectedMessage = "Basket has expired, please reload session";
    String actualMessage = exception.getMessage();
    assertTrue(actualMessage.contains(expectedMessage));
  }

  @ParameterizedTest
  @CsvSource({
      "true, true",
      "true, false",
      "false, true",
      "false, false"
  })
  void addNewRoomToExistingBasket_RoomOccupancyRule_ShouldThrowException(Boolean aemSearchRulesFf,
      Boolean maxRoomsAmendFf) {
    //Arrange
    var accountBuilder = uk.co.whitbread.shared.auth.account.Account.builder();
    var account = accountBuilder.accessLevel("SUPER").build();
    var basketReference = TEMPORARY_BASKET_REF;
    var reservationRequest = createValidHotelReservationRequest();
    var basket = ManageReservationUtils.mockBasketResponse();
    var roomOccupancyData = MaxRoomOccupancyData.builder()
        .adultsNumber(1)
        .childrenNumber(0)
        .acceptedRoomTypes(List.of("DBLDBL"))
        .build();
    var maxRoomOccupancy = MaxRoomOccupancyResponse.builder()
        .channelId("TestChannelId")
        .roomOccupancies(List.of(roomOccupancyData))
        .generatedAt(new Date())
        .build();
    var mockedFeatureFlag = mock(FeatureFlag.class);

    when(unleashWrapper.featureFlag()).thenReturn(mockedFeatureFlag);


    var mockedFeatureAemSearchRules = mock(FeatureFlag.Feature.class);
    when(mockedFeatureFlag.getAemSearchRules()).thenReturn(mockedFeatureAemSearchRules);
    when(unleashWrapper.isEnabled(mockedFeatureFlag.getAemSearchRules())).thenReturn(
        aemSearchRulesFf);

    when(basketOutPort.getBasketById(basketReference)).thenReturn(basket);
    when(authenticatedUserService.getCurrentUserAccount()).thenReturn(
        Optional.ofNullable(account));

    if (aemSearchRulesFf) {
      var mockedFeatureMaxRoomAmend = mock(FeatureFlag.Feature.class);
      when(mockedFeatureFlag.getMaxRoomsAmend()).thenReturn(mockedFeatureMaxRoomAmend);
      when(unleashWrapper.isEnabled(mockedFeatureFlag.getMaxRoomsAmend())).thenReturn(
          maxRoomsAmendFf);
      when(contentOutPort.getSearchRules(anyString(), any())).thenReturn(
          SearchRules.builder()
              .maxRoomsAmend(4)
              .roomOccupancies(List.of(
                  RoomOccupancy.builder()
                      .adultsNumber(1)
                      .childrenNumber(0)
                      .acceptedRoomTypes(List.of("DBLDBL"))
                      .build()))
              .build());

      if (maxRoomsAmendFf) {
        when(contentOutPort.getSearchRules(anyString(), any())).thenReturn(
            SearchRules.builder()
                .maxRoomsAmend(4)
                .roomOccupancies(List.of(
                    RoomOccupancy.builder()
                        .adultsNumber(1)
                        .childrenNumber(0)
                        .acceptedRoomTypes(List.of("DBLDBL"))
                        .build()))
                .build());
      } else {
        when(contentOutPort.getSearchRules(anyString(), any())).thenReturn(
            SearchRules.builder()
                .maxRooms(4)
                .roomOccupancies(List.of(
                    RoomOccupancy.builder()
                        .adultsNumber(1)
                        .childrenNumber(0)
                        .acceptedRoomTypes(List.of("DBLDBL"))
                        .build()))
                .build());
      }
    } else {
      when(rulesOutPort.getMaxRoomsRule(any())).thenReturn(new MaxRoomsRuleResponse(4));
      when(rulesOutPort.getMaxRoomOccupancyRule(any(), any())).thenReturn(maxRoomOccupancy);
    }

    when(contentOutPort.getHotelInformation(any(), any(), any())).thenReturn(
        HotelInfoResponse.builder().brand(PI_BOOKING_CHANNEL).build());
    tokenUtilsMock.when(() -> TokenUtils.isValid(anyString(), anyString()))
        .thenReturn(Boolean.TRUE);

    //Act
    Exception exception = assertThrows(GenericBadRequestException.class,
        () -> reservationInPort.addNewRoomToExistingBasket(
            TEMPORARY_BASKET_REF, reservationRequest, null));
    String expectedMessage = "Wrong room type for number of adults and children";
    String actualMessage = exception.getMessage();

    //Assert
    assertTrue(actualMessage.contains(expectedMessage));
  }

  @Test
  void buildUpdateReservationPackagesByIdSingleCall_invalidReservationPackages() {
    //before
    var request = new UpdateReservationPackagesByIdRequest();
    request.setHotelId("hotelId");
    request.setArrival("2023-10-05");
    request.setDeparture("2023-10-07");
    request.setRoomsSelections(List.of(
        new RoomsSelectionsByReservationId("12345", List.of(new PackagesSelection("PROMO", 1)))));
    request.setPreviousRoomsSelections(
        List.of(new RoomsSelectionsByReservationId("21345",
            List.of(new PackagesSelection("HSATWN", 1)))));
    assertThrows(GenericBadRequestException.class,
        () -> reservationInPort.buildUpdateReservationPackagesByIdSingleCall(request));
  }

  @ParameterizedTest
  @ValueSource(booleans = {false, true})
  void amendStayDates_SingleReservationScenario1_Success(Boolean saveBasketAllowances) {
    var bookingAllowancesResponse = mockCreateBookingAllowancesResponse();
    var basketSingleReservationResponse = mockBasketSingleReservationResponse();
    var manageBookingResponse = mockManageBookingResponse();
    List<String> basketBookingAllowances =
        basketSingleReservationResponse.getBookingAllowances() != null ?
            basketSingleReservationResponse.getBookingAllowances().stream()
                .map(BookingAllowance::getAllowance).toList() :
            List.of();
    // Arrange
    when(rulesOutPort.getMaxNightsRule(any())).thenReturn(new MaxNightsRuleResponse(9));
    when(basketOutPort.getBasketById(anyString())).thenReturn(basketSingleReservationResponse);
    when(reservationOutPort.getReservationsByIds(any(), any(), anyBoolean(), anyBoolean(),
        anyBoolean())).thenReturn(
        mockResByBasketRefSingleResResponse());
    when(amendLogicInPort.checkCreateOnHoldReservation(anyString(),
        anyString(), anyString(), anyString())).thenReturn(true);
    when(amendLogicInPort.getAmendOnHoldReservationInterval(anyString(), anyString(), anyString(),
        anyString()))
        .thenReturn(List.of(createAmendOnHoldInterval("2022-05-07", "2022-05-08")));
    when(reservationOutPort.getBookingAllowances("TESTHOTEL", "res1", basketBookingAllowances))
        .thenReturn(bookingAllowancesResponse);

    //mocking create onHoldReservations
    when(reservationOutPort.createReservation(anyString(), any(), anyString()))
        .thenReturn(mockReservationResponse());
    when(basketOutPort.createBasket(anyString(), anyString(), any()))
        .thenReturn(mockBasketSingleReservationResponse());
    when(basketOutPort.addReservationsToBasket(any(), any(),
        any(OhipReservationResponse.class), anyBoolean(), anyBoolean(), eq(null), anyBoolean()))
        .thenReturn(ManageReservationUtils.mockBasketResponse());
    when(manageBookingInPort.getManageBookingInformation(anyString(), anyString(),
        anyString(), anyString(),
        any(), anyBoolean(), any())).thenReturn(manageBookingResponse);

    doNothing().when(reservationOutPort).updateReservations(any(UpdateReservationsRequest.class));

    tokenUtilsMock.when(() -> TokenUtils.isValid(anyString(), anyString()))
        .thenReturn(Boolean.TRUE);

    var mockedFeatureFlag = mock(FeatureFlag.class);
    when(unleashWrapper.featureFlag())
        .thenReturn(mockedFeatureFlag);
    when(unleashWrapper.isEnabled(mockedFeatureFlag.getSaveAllowancesInBasket()))
        .thenReturn(saveBasketAllowances);
    when(unleashWrapper.isEnabled(mockedFeatureFlag.getApplyOccupancySupplement()))
        .thenReturn(false);

    // Act
    reservationInPort.amendStayDates(mockAmendStayDatesRequest("2022-05-05", "2022-05-08"), null);

    // Assert
    verify(amendLogicInPort, times(1)).checkCreateOnHoldReservation(anyString(),
        anyString(), anyString(), anyString());
    verify(amendLogicInPort, times(1)).getAmendOnHoldReservationInterval(anyString(),
        anyString(), anyString(), anyString());
    verify(reservationOutPort, times(1)).updateReservations(any());
    
    //Verify call order
    InOrder inOrder = inOrder(reservationOutPort);
    //Ensure getBookingAllowances is called before updateReservations
    inOrder.verify(reservationOutPort).getBookingAllowances(eq("TESTHOTEL"), eq("res1"), anyList());
    inOrder.verify(reservationOutPort).updateReservations(any());
  }

  @ParameterizedTest
  @ValueSource(booleans = {false, true})
  void amendStayDates_SingleReservationScenario2_Success(Boolean saveBasketAllowances) {

    var bookingAllowancesResponse = mockCreateBookingAllowancesResponse();
    var basketSingleReservationResponse = mockBasketSingleReservationResponse();
    var manageBookingResponse = mockManageBookingResponse();
    List<String> basketBookingAllowances  =
        basketSingleReservationResponse.getBookingAllowances() != null ?
            basketSingleReservationResponse.getBookingAllowances().stream()
                .map(BookingAllowance::getAllowance).toList() :
            List.of();

    // Arrange
    when(rulesOutPort.getMaxNightsRule(any())).thenReturn(new MaxNightsRuleResponse(9));
    when(basketOutPort.getBasketById(anyString())).thenReturn(basketSingleReservationResponse);
    when(reservationOutPort.getReservationsByIds(any(), any(), eq(false), eq(false),
        eq(false))).thenReturn(
        mockResByBasketRefSingleResResponse());
    when(amendLogicInPort.checkCreateOnHoldReservation(anyString(),
        anyString(), anyString(), anyString())).thenReturn(true);
    when(amendLogicInPort.getAmendOnHoldReservationInterval(anyString(), anyString(), anyString(),
        anyString()))
        .thenReturn(List.of(createAmendOnHoldInterval("2022-05-04", "2022-05-05")));
    when(reservationOutPort.getBookingAllowances("TESTHOTEL", "res1", basketBookingAllowances))
        .thenReturn(bookingAllowancesResponse);

    //mocking create onHoldReservations
    when(reservationOutPort.createReservation(anyString(), any(), anyString()))
        .thenReturn(mockReservationResponse());
    when(basketOutPort.createBasket(anyString(), anyString(), any()))
        .thenReturn(mockBasketSingleReservationResponse());
    when(basketOutPort.addReservationsToBasket(any(), any(),
        any(OhipReservationResponse.class), anyBoolean(), anyBoolean(), eq(null), anyBoolean()))
        .thenReturn(ManageReservationUtils.mockBasketResponse());

    when(rulesOutPort.getMaxNightsRule(any())).thenReturn(new MaxNightsRuleResponse(14));
    when(manageBookingInPort.getManageBookingInformation(anyString(), anyString(),
        anyString(), anyString(),
        any(), anyBoolean(), any())).thenReturn(manageBookingResponse);

    doNothing().when(reservationOutPort).updateReservations(any(UpdateReservationsRequest.class));

    tokenUtilsMock.when(() -> TokenUtils.isValid(anyString(), anyString()))
        .thenReturn(Boolean.TRUE);

    var mockedFeatureFlag = mock(FeatureFlag.class);
    when(unleashWrapper.featureFlag())
        .thenReturn(mockedFeatureFlag);
    when(unleashWrapper.isEnabled(mockedFeatureFlag.getSaveAllowancesInBasket()))
        .thenReturn(saveBasketAllowances);
    when(unleashWrapper.isEnabled(mockedFeatureFlag.getApplyOccupancySupplement()))
        .thenReturn(false);

    // Act
    reservationInPort.amendStayDates(mockAmendStayDatesRequest("2022-05-04", "2022-05-07"), null);

    // Assert
    verify(amendLogicInPort, times(1)).checkCreateOnHoldReservation(anyString(),
        anyString(), anyString(), anyString());
    verify(amendLogicInPort, times(1)).getAmendOnHoldReservationInterval(anyString(),
        anyString(), anyString(), anyString());
    verify(reservationOutPort, times(1)).updateReservations(any());
  }

  @ParameterizedTest
  @ValueSource(booleans = {false, true})
  void amendStayDates_SingleReservationScenario3_Success(Boolean saveBasketAllowances) {

    var bookingAllowancesResponse = mockCreateBookingAllowancesResponse();
    var basketSingleReservationResponse = mockBasketSingleReservationResponse();
    var manageBookingResponse = mockManageBookingResponse();
    List<String> basketBookingAllowances  =
        basketSingleReservationResponse.getBookingAllowances() != null ?
            basketSingleReservationResponse.getBookingAllowances().stream()
                .map(BookingAllowance::getAllowance).toList() :
            List.of();
    // Arrange
    if (saveBasketAllowances) {
      when(contentOutPort.getSearchRules(anyString(), any())).thenReturn(
          SearchRules.builder().maxNights(9).build());
    } else {
      when(rulesOutPort.getMaxNightsRule(any())).thenReturn(new MaxNightsRuleResponse(9));
    }
    when(basketOutPort.getBasketById(anyString())).thenReturn(basketSingleReservationResponse);
    when(reservationOutPort.getReservationsByIds(any(), any(), eq(false), eq(false),
        eq(false))).thenReturn(
        mockResByBasketRefSingleResResponse());
    when(reservationOutPort.getBookingAllowances("TESTHOTEL", "res1", basketBookingAllowances))
        .thenReturn(bookingAllowancesResponse);
    when(manageBookingInPort.getManageBookingInformation(anyString(), anyString(),
        anyString(), anyString(),
        any(), anyBoolean(), any())).thenReturn(manageBookingResponse);

    doNothing().when(reservationOutPort).updateReservations(any(UpdateReservationsRequest.class));

    tokenUtilsMock.when(() -> TokenUtils.isValid(anyString(), anyString()))
        .thenReturn(Boolean.TRUE);

    var mockedFeatureFlag = mock(FeatureFlag.class);
    when(unleashWrapper.featureFlag())
        .thenReturn(mockedFeatureFlag);
    when(unleashWrapper.isEnabled(mockedFeatureFlag.getUseBasketAllowances()))
        .thenReturn(saveBasketAllowances);

    // Act
    reservationInPort.amendStayDates(mockAmendStayDatesRequest("2022-05-06", "2022-05-07"), null);

    // Assert
    verify(amendLogicInPort, times(1)).checkCreateOnHoldReservation(anyString(),
        anyString(), anyString(), anyString());
    verify(amendLogicInPort, times(0)).getAmendOnHoldReservationInterval(anyString(),
        anyString(), anyString(), anyString());
    verify(reservationOutPort, times(1)).updateReservations(any());
  }

  @ParameterizedTest
  @ValueSource(booleans = {false, true})
  void amendStayDates_SingleReservationScenario4_Success(Boolean saveBasketAllowances) {

    var bookingAllowancesResponse = mockCreateBookingAllowancesResponse();
    var basketSingleReservationResponse = mockBasketSingleReservationResponse();
    var manageBookingResponse = mockManageBookingResponse();
    List<String> bookingAllowances =
        basketSingleReservationResponse.getBookingAllowances() != null ?
            basketSingleReservationResponse.getBookingAllowances().stream()
                .map(BookingAllowance::getAllowance).toList() :
            List.of();
    // Arrange
    when(rulesOutPort.getMaxNightsRule(any())).thenReturn(new MaxNightsRuleResponse(9));
    when(basketOutPort.getBasketById(anyString())).thenReturn(basketSingleReservationResponse);
    when(reservationOutPort.getReservationsByIds(any(), any(), eq(false), eq(false),
        eq(false))).thenReturn(
        mockResByBasketRefSingleResResponse());
    when(amendLogicInPort.checkCreateOnHoldReservation(anyString(),
        anyString(), anyString(), anyString())).thenReturn(true);
    when(amendLogicInPort.getAmendOnHoldReservationInterval(anyString(), anyString(), anyString(),
        anyString()))
        .thenReturn(List.of(createAmendOnHoldInterval("2022-05-04", "2022-05-05"),
            createAmendOnHoldInterval("2022-05-07", "2022-05-08")));
    when(reservationOutPort.getBookingAllowances("TESTHOTEL", "res1", bookingAllowances))
        .thenReturn(bookingAllowancesResponse);

    //mocking create onHoldReservations
    when(reservationOutPort.createReservation(anyString(), any(), anyString()))
        .thenReturn(mockReservationResponse());
    when(basketOutPort.createBasket(anyString(), anyString(), any()))
        .thenReturn(mockBasketSingleReservationResponse());
    when(basketOutPort.addReservationsToBasket(any(), any(),
        any(OhipReservationResponse.class), anyBoolean(), anyBoolean(), eq(null), anyBoolean()))
        .thenReturn(ManageReservationUtils.mockBasketResponse());

    when(rulesOutPort.getMaxNightsRule(any())).thenReturn(new MaxNightsRuleResponse(14));
    when(manageBookingInPort.getManageBookingInformation(anyString(), anyString(),
        anyString(), anyString(),
        any(), anyBoolean(), any())).thenReturn(manageBookingResponse);

    doNothing().when(reservationOutPort).updateReservations(any(UpdateReservationsRequest.class));

    tokenUtilsMock.when(() -> TokenUtils.isValid(anyString(), anyString()))
        .thenReturn(Boolean.TRUE);

    var mockedFeatureFlag = mock(FeatureFlag.class);
    when(unleashWrapper.featureFlag())
        .thenReturn(mockedFeatureFlag);
    when(unleashWrapper.isEnabled(mockedFeatureFlag.getUseBasketAllowances()))
        .thenReturn(saveBasketAllowances);
    when(unleashWrapper.isEnabled(mockedFeatureFlag.getApplyOccupancySupplement()))
        .thenReturn(false);

    // Act
    reservationInPort.amendStayDates(mockAmendStayDatesRequest("2022-05-04", "2022-05-08"), null);

    // Assert
    verify(amendLogicInPort, times(1)).checkCreateOnHoldReservation(anyString(),
        anyString(), anyString(), anyString());
    verify(amendLogicInPort, times(1)).getAmendOnHoldReservationInterval(anyString(),
        anyString(), anyString(), anyString());
    verify(reservationOutPort, times(1)).updateReservations(any());
  }

  @ParameterizedTest
  @ValueSource(booleans = {false, true})
  void amendStayDates_SingleReservationScenario5_Success(Boolean saveBasketAllowances) {

    var bookingAllowancesResponse = mockCreateBookingAllowancesResponse();
    var basketSingleReservationResponse = mockBasketSingleReservationResponse();
    var manageBookingResponse = mockManageBookingResponse();
    List<String> basketBookingAllowances  =
        basketSingleReservationResponse.getBookingAllowances() != null ?
            basketSingleReservationResponse.getBookingAllowances().stream()
                .map(BookingAllowance::getAllowance).toList() :
            List.of();

    // Arrange
    when(rulesOutPort.getMaxNightsRule(any())).thenReturn(new MaxNightsRuleResponse(9));
    when(basketOutPort.getBasketById(anyString())).thenReturn(basketSingleReservationResponse);
    when(reservationOutPort.getReservationsByIds(any(), any(), eq(false), eq(false),
        eq(false))).thenReturn(
        mockResByBasketRefSingleResResponse());
    when(amendLogicInPort.checkCreateOnHoldReservation(anyString(),
        anyString(), anyString(), anyString())).thenReturn(true);
    when(amendLogicInPort.getAmendOnHoldReservationInterval(anyString(), anyString(), anyString(),
        anyString()))
        .thenReturn(List.of(createAmendOnHoldInterval("2022-05-08", "2022-05-09")));
    when(reservationOutPort.getBookingAllowances("TESTHOTEL", "res1", basketBookingAllowances))
        .thenReturn(bookingAllowancesResponse);

    //mocking create onHoldReservations
    when(reservationOutPort.createReservation(anyString(), any(), anyString()))
        .thenReturn(mockReservationResponse());
    when(basketOutPort.createBasket(anyString(), anyString(), any()))
        .thenReturn(mockBasketSingleReservationResponse());
    when(basketOutPort.addReservationsToBasket(any(), any(),
        any(OhipReservationResponse.class), anyBoolean(), anyBoolean(), eq(null), anyBoolean()))
        .thenReturn(ManageReservationUtils.mockBasketResponse());

    doNothing().when(reservationOutPort).updateReservations(any(UpdateReservationsRequest.class));
    when(manageBookingInPort.getManageBookingInformation(anyString(), anyString(),
        anyString(), anyString(),
        any(), anyBoolean(), any())).thenReturn(manageBookingResponse);

    tokenUtilsMock.when(() -> TokenUtils.isValid(anyString(), anyString()))
        .thenReturn(Boolean.TRUE);

    var mockedFeatureFlag = mock(FeatureFlag.class);
    when(unleashWrapper.featureFlag())
        .thenReturn(mockedFeatureFlag);
    when(unleashWrapper.isEnabled(mockedFeatureFlag.getUseBasketAllowances()))
        .thenReturn(saveBasketAllowances);
    when(unleashWrapper.isEnabled(mockedFeatureFlag.getApplyOccupancySupplement()))
        .thenReturn(false);

    // Act
    reservationInPort.amendStayDates(mockAmendStayDatesRequest("2022-05-08", "2022-05-09"), null);

    // Assert
    verify(amendLogicInPort, times(1)).checkCreateOnHoldReservation(anyString(),
        anyString(), anyString(), anyString());
    verify(amendLogicInPort, times(1)).getAmendOnHoldReservationInterval(anyString(),
        anyString(), anyString(), anyString());
    verify(reservationOutPort, times(1)).updateReservations(any());
  }

  @ParameterizedTest
  @ValueSource(booleans = {false, true})
  void amendStayDates_SingleReservationScenario6_Success(Boolean saveBasketAllowances) {
      var bookingAllowancesResponse = mockCreateBookingAllowancesResponse();
      var basketSingleReservationResponse = mockBasketSingleReservationResponse();
      var manageBookingResponse = mockManageBookingResponse();
      List<String> basketBookingAllowances  =
        basketSingleReservationResponse.getBookingAllowances() != null ?
            basketSingleReservationResponse.getBookingAllowances().stream()
                .map(BookingAllowance::getAllowance).toList() :
            List.of();

      // Arrange
      when(rulesOutPort.getMaxNightsRule(any())).thenReturn(new MaxNightsRuleResponse(9));
      when(basketOutPort.getBasketById(anyString())).thenReturn(basketSingleReservationResponse);
      when(reservationOutPort.getReservationsByIds(any(), any(), anyBoolean(), anyBoolean(), anyBoolean())).thenReturn(
          mockResByBasketRefSingleResResponse());
      when(amendLogicInPort.checkCreateOnHoldReservation(anyString(),
          anyString(), anyString(), anyString())).thenReturn(true);
      when(amendLogicInPort.getAmendOnHoldReservationInterval(anyString(), anyString(), anyString(),
          anyString()))
          .thenReturn(List.of(createAmendOnHoldInterval("2022-05-02", "2022-05-04")));
      when(reservationOutPort.getBookingAllowances("TESTHOTEL", "res1", basketBookingAllowances))
            .thenReturn(bookingAllowancesResponse);

      //mocking create onHoldReservations
      when(reservationOutPort.createReservation(anyString(), any(), anyString()))
          .thenReturn(mockReservationResponse());
      when(basketOutPort.createBasket(anyString(), anyString(), any()))
          .thenReturn(mockBasketSingleReservationResponse());
      when(basketOutPort.addReservationsToBasket(any(), any(),
          any(OhipReservationResponse.class), anyBoolean(), anyBoolean(), eq(null), anyBoolean()))
          .thenReturn(ManageReservationUtils.mockBasketResponse());

      doNothing().when(reservationOutPort).updateReservations(any(UpdateReservationsRequest.class));

      tokenUtilsMock.when(() -> TokenUtils.isValid(anyString(), anyString()))
          .thenReturn(Boolean.TRUE);

      when(rulesOutPort.getMaxNightsRule(any())).thenReturn(new MaxNightsRuleResponse(14));
      when(manageBookingInPort.getManageBookingInformation(anyString(), anyString(),
              anyString(), anyString(),
              any(), anyBoolean(), any())).thenReturn(manageBookingResponse);

      var mockedFeatureFlag = mock(FeatureFlag.class);
      when(unleashWrapper.featureFlag())
          .thenReturn(mockedFeatureFlag);
      when(unleashWrapper.isEnabled(mockedFeatureFlag.getUseBasketAllowances()))
          .thenReturn(saveBasketAllowances);
      when(unleashWrapper.isEnabled(mockedFeatureFlag.getApplyOccupancySupplement()))
          .thenReturn(false);

      // Act
      reservationInPort.amendStayDates(mockAmendStayDatesRequest("2022-05-02", "2022-05-04"), null);

      // Assert
      verify(amendLogicInPort, times(1)).checkCreateOnHoldReservation(anyString(),
          anyString(), anyString(), anyString());
      verify(amendLogicInPort, times(1)).getAmendOnHoldReservationInterval(anyString(),
          anyString(), anyString(), anyString());
      verify(reservationOutPort, times(1)).updateReservations(any());
  }

  @ParameterizedTest
  @ValueSource(booleans = {false, true})
  void amendStayDates_SingleReservationScenario7_Success(Boolean saveBasketAllowances) {
      var bookingAllowancesResponse = mockCreateBookingAllowancesResponse();
      var basketSingleReservationResponse = mockBasketSingleReservationResponse();
      var manageBookingResponse = mockManageBookingResponse();
      List<String> basketBookingAllowances  =
        basketSingleReservationResponse.getBookingAllowances() != null ?
            basketSingleReservationResponse.getBookingAllowances().stream()
                .map(BookingAllowance::getAllowance).toList() :
            List.of();
      // Arrange
      when(rulesOutPort.getMaxNightsRule(any())).thenReturn(new MaxNightsRuleResponse(9));
      when(basketOutPort.getBasketById(anyString())).thenReturn(basketSingleReservationResponse);
      when(reservationOutPort.getReservationsByIds(any(), any(), eq(false), eq(false), eq(false))).thenReturn(
          mockResByBasketRefSingleResResponse());
      when(amendLogicInPort.checkCreateOnHoldReservation(anyString(),
          anyString(), anyString(), anyString())).thenReturn(true);
      when(amendLogicInPort.getAmendOnHoldReservationInterval(anyString(), anyString(), anyString(),
          anyString()))
          .thenReturn(List.of(createAmendOnHoldInterval("2022-05-03", "2022-05-05")));
      when(reservationOutPort.getBookingAllowances("TESTHOTEL", "res1", basketBookingAllowances))
            .thenReturn(bookingAllowancesResponse);

      //mocking create onHoldReservations
      when(reservationOutPort.createReservation(anyString(), any(), anyString()))
          .thenReturn(mockReservationResponse());
      when(basketOutPort.createBasket(anyString(), anyString(), any()))
          .thenReturn(mockBasketSingleReservationResponse());
      when(basketOutPort.addReservationsToBasket(any(), any(),
          any(OhipReservationResponse.class), anyBoolean(), anyBoolean(), eq(null), anyBoolean()))
          .thenReturn(ManageReservationUtils.mockBasketResponse());

      doNothing().when(reservationOutPort).updateReservations(any(UpdateReservationsRequest.class));
      when(manageBookingInPort.getManageBookingInformation(anyString(), anyString(),
              anyString(), anyString(),
              any(), anyBoolean(), any())).thenReturn(manageBookingResponse);

      tokenUtilsMock.when(() -> TokenUtils.isValid(anyString(), anyString()))
          .thenReturn(Boolean.TRUE);

      var mockedFeatureFlag = mock(FeatureFlag.class);
      when(unleashWrapper.featureFlag())
          .thenReturn(mockedFeatureFlag);
      when(unleashWrapper.isEnabled(mockedFeatureFlag.getUseBasketAllowances()))
          .thenReturn(saveBasketAllowances);
      when(unleashWrapper.isEnabled(mockedFeatureFlag.getApplyOccupancySupplement()))
          .thenReturn(false);

      // Act
      reservationInPort.amendStayDates(mockAmendStayDatesRequest("2022-05-03", "2022-05-06"), null);

      // Assert
      verify(amendLogicInPort, times(1)).checkCreateOnHoldReservation(anyString(),
          anyString(), anyString(), anyString());
      verify(amendLogicInPort, times(1)).getAmendOnHoldReservationInterval(anyString(),
          anyString(), anyString(), anyString());
      verify(reservationOutPort, times(1)).updateReservations(any());
  }

  @ParameterizedTest
  @ValueSource(booleans = {false, true})
  void amendStayDates_SingleReservationScenario8_Success(Boolean saveBasketAllowances) {
      var bookingAllowancesResponse = mockCreateBookingAllowancesResponse();
      var basketSingleReservationResponse = mockBasketSingleReservationResponse();
      var manageBookingResponse = mockManageBookingResponse();
      List<String> basketBookingAllowances  =
        basketSingleReservationResponse.getBookingAllowances() != null ?
            basketSingleReservationResponse.getBookingAllowances().stream()
                .map(BookingAllowance::getAllowance).toList() :
            List.of();
      // Arrange
      when(rulesOutPort.getMaxNightsRule(any())).thenReturn(new MaxNightsRuleResponse(9));
      when(basketOutPort.getBasketById(anyString())).thenReturn(
              basketSingleReservationResponse);
      when(reservationOutPort.getReservationsByIds(any(), any(), eq(false), eq(false), eq(false))).thenReturn(
          mockResByBasketRefSingleResResponse());
      when(amendLogicInPort.checkCreateOnHoldReservation(anyString(),
          anyString(), anyString(), anyString())).thenReturn(true);
      when(amendLogicInPort.getAmendOnHoldReservationInterval(anyString(), anyString(), anyString(),
          anyString()))
          .thenReturn(List.of(createAmendOnHoldInterval("2022-05-07", "2022-05-09")));
      when(reservationOutPort.getBookingAllowances("TESTHOTEL", "res1", basketBookingAllowances))
            .thenReturn(bookingAllowancesResponse);

      //mocking create onHoldReservations
      when(reservationOutPort.createReservation(anyString(), any(), anyString()))
          .thenReturn(mockReservationResponse());
      when(basketOutPort.createBasket(anyString(), anyString(), any()))
          .thenReturn(mockBasketSingleReservationResponse());
      when(basketOutPort.addReservationsToBasket(any(), any(),
          any(OhipReservationResponse.class), anyBoolean(), anyBoolean(), eq(null), anyBoolean()))
          .thenReturn(ManageReservationUtils.mockBasketResponse());
      when(manageBookingInPort.getManageBookingInformation(anyString(), anyString(),
              anyString(), anyString(),
              any(), anyBoolean(), any())).thenReturn(manageBookingResponse);

      doNothing().when(reservationOutPort).updateReservations(any(UpdateReservationsRequest.class));

      tokenUtilsMock.when(() -> TokenUtils.isValid(anyString(), anyString()))
          .thenReturn(Boolean.TRUE);

      var mockedFeatureFlag = mock(FeatureFlag.class);
      when(unleashWrapper.featureFlag())
          .thenReturn(mockedFeatureFlag);
      when(unleashWrapper.isEnabled(mockedFeatureFlag.getUseBasketAllowances()))
          .thenReturn(saveBasketAllowances);
      when(unleashWrapper.isEnabled(mockedFeatureFlag.getApplyOccupancySupplement()))
          .thenReturn(false);

      // Act
      reservationInPort.amendStayDates(mockAmendStayDatesRequest("2022-05-06", "2022-05-09"), null);

      // Assert
      verify(amendLogicInPort, times(1)).checkCreateOnHoldReservation(anyString(),
          anyString(), anyString(), anyString());
      verify(amendLogicInPort, times(1)).getAmendOnHoldReservationInterval(anyString(),
          anyString(), anyString(), anyString());
      verify(reservationOutPort, times(1)).updateReservations(any());
  }

  @Test
  void amendStayDates_SingleReservationScenario8_ThrowsError() {
      var basketSingleReservationResponse = mockBasketSingleReservationResponse();
      var manageBookingResponse = mockManageBookingResponse();
      var mockedFeatureFlag = mock(FeatureFlag.class);

      when(unleashWrapper.featureFlag())
              .thenReturn(mockedFeatureFlag);

      // Arrange
      when(rulesOutPort.getMaxNightsRule(any())).thenReturn(new MaxNightsRuleResponse(9));
      when(basketOutPort.getBasketById(anyString())).thenReturn(basketSingleReservationResponse);
      when(reservationOutPort.getReservationsByIds(any(), any(), eq(false), eq(false), eq(false))).thenReturn(
          mockResByBasketRefSingleResResponse());
      when(amendLogicInPort.checkCreateOnHoldReservation(anyString(),
          anyString(), anyString(), anyString())).thenReturn(true);
      when(amendLogicInPort.getAmendOnHoldReservationInterval(anyString(), anyString(), anyString(),
          anyString()))
          .thenReturn(List.of(createAmendOnHoldInterval("2022-05-07", "2022-05-09")));
      tokenUtilsMock.when(() -> TokenUtils.isValid(anyString(), anyString()))
          .thenReturn(Boolean.TRUE);
      when(manageBookingInPort.getManageBookingInformation(anyString(), anyString(),
              anyString(), anyString(),
              any(), anyBoolean(), any())).thenReturn(manageBookingResponse);

        // Act & Assert
      Exception exception= assertThrows(AmendStayDateException.class,
              () -> reservationInPort.amendStayDates(
                      mockAmendStayDatesRequest("2022-05-06", "2022-05-09"), null));

      assertThat(exception.getMessage(), is("There is no availability for the requested period of time"));
  }

  @Test
  void amendStayDates_SingleReservationScenario8_ThrowsGenericBadRequestException() {
      var basketSingleReservationResponse = mockBasketSingleReservationResponse();
      var manageBookingResponse = mockManageBookingResponse();
      manageBookingResponse.setIsCancellable(Boolean.FALSE);
      var mockedFeatureFlag = mock(FeatureFlag.class);

      when(unleashWrapper.featureFlag())
              .thenReturn(mockedFeatureFlag);

      // Arrange
      when(rulesOutPort.getMaxNightsRule(any())).thenReturn(new MaxNightsRuleResponse(9));
      when(basketOutPort.getBasketById(anyString())).thenReturn(basketSingleReservationResponse);
      when(reservationOutPort.getReservationsByIds(any(), any(), eq(false), eq(false), eq(false))).thenReturn(
          mockResByBasketRefSingleResResponse());
      tokenUtilsMock.when(() -> TokenUtils.isValid(anyString(), anyString()))
          .thenReturn(Boolean.TRUE);
      when(manageBookingInPort.getManageBookingInformation(anyString(), anyString(),
              anyString(), anyString(),
              any(), anyBoolean(), any())).thenReturn(manageBookingResponse);


      // Act & Assert
      Assertions.assertThrows(GenericBadRequestException.class,
          () -> reservationInPort.amendStayDates(
              mockAmendStayDatesRequest("2022-05-06", "2022-05-09"), null));
  }

  @ParameterizedTest
  @ValueSource(booleans = {false, true})
  void amendStayDates_SingleReservationScenario9_Success(Boolean saveBasketAllowances) {
    BookingAllowancesResponse bookingAllowancesResponse = null;
    var basketSingleReservationResponse = mockBasketSingleReservationResponse();
    var manageBookingResponse = mockManageBookingResponse();
    List<String> basketBookingAllowances =
        basketSingleReservationResponse.getBookingAllowances() != null ?
            basketSingleReservationResponse.getBookingAllowances().stream()
                .map(BookingAllowance::getAllowance).toList() :
            List.of();
    // Arrange
    when(rulesOutPort.getMaxNightsRule(any())).thenReturn(new MaxNightsRuleResponse(9));
    when(basketOutPort.getBasketById(anyString())).thenReturn(basketSingleReservationResponse);
    when(reservationOutPort.getReservationsByIds(any(), any(), anyBoolean(), anyBoolean(),
        anyBoolean())).thenReturn(
        mockResByBasketRefSingleResResponse());
    when(amendLogicInPort.checkCreateOnHoldReservation(anyString(),
        anyString(), anyString(), anyString())).thenReturn(true);
    when(amendLogicInPort.getAmendOnHoldReservationInterval(anyString(), anyString(), anyString(),
        anyString()))
        .thenReturn(List.of(createAmendOnHoldInterval("2022-05-07", "2022-05-08")));
    when(reservationOutPort.getBookingAllowances("TESTHOTEL", "res1", basketBookingAllowances))
        .thenReturn(bookingAllowancesResponse);

    //mocking create onHoldReservations
    when(reservationOutPort.createReservation(anyString(), any(), anyString()))
        .thenReturn(mockReservationResponse());
    when(basketOutPort.createBasket(anyString(), anyString(), any()))
        .thenReturn(mockBasketSingleReservationResponse());
    when(basketOutPort.addReservationsToBasket(any(), any(),
        any(OhipReservationResponse.class), anyBoolean(), anyBoolean(), eq(null), anyBoolean()))
        .thenReturn(ManageReservationUtils.mockBasketResponse());
    when(manageBookingInPort.getManageBookingInformation(anyString(), anyString(),
        anyString(), anyString(),
        any(), anyBoolean(), any())).thenReturn(manageBookingResponse);

    doNothing().when(reservationOutPort).updateReservations(any(UpdateReservationsRequest.class));

    tokenUtilsMock.when(() -> TokenUtils.isValid(anyString(), anyString()))
        .thenReturn(Boolean.TRUE);

    var mockedFeatureFlag = mock(FeatureFlag.class);
    when(unleashWrapper.featureFlag())
        .thenReturn(mockedFeatureFlag);
    when(unleashWrapper.isEnabled(mockedFeatureFlag.getSaveAllowancesInBasket()))
        .thenReturn(saveBasketAllowances);
    when(unleashWrapper.isEnabled(mockedFeatureFlag.getApplyOccupancySupplement()))
        .thenReturn(false);

    // Act
    reservationInPort.amendStayDates(mockAmendStayDatesRequest("2022-05-05", "2022-05-08"), null);

    // Assert
    verify(amendLogicInPort, times(1)).checkCreateOnHoldReservation(anyString(),
        anyString(), anyString(), anyString());
    verify(amendLogicInPort, times(1)).getAmendOnHoldReservationInterval(anyString(),
        anyString(), anyString(), anyString());
    verify(reservationOutPort, times(1)).updateReservations(any());

    //Verify call order
    InOrder inOrder = inOrder(reservationOutPort);
    //Ensure getBookingAllowances is called before updateReservations
    inOrder.verify(reservationOutPort).getBookingAllowances(eq("TESTHOTEL"), eq("res1"), anyList());
    inOrder.verify(reservationOutPort).updateReservations(any());
  }

  @ParameterizedTest(name = "businessNotes=''{0}'' → allowances NOT copied")
  @NullAndEmptySource
  @ValueSource(strings = {"   "})
  void copyBookingAllowancesIfPresent_blankOrNullBusinessNotes_shouldNotCopyAllowances(
      String businessNotes) {
    // Arrange — use copyBooking which exercises copyBookingAllowancesIfPresent
    var bookingAllowancesResponse = BookingAllowancesResponse.builder()
        .bookingAllowances(Collections.singletonList(BookingAllowance.builder()
            .allowance("dinner")
            .budget(BigDecimal.TEN)
            .build()))
        .businessNotes(businessNotes)
        .build();
    var copyBookingRequest = mockCopyBookingRequest("basketRef");
    var basketResponse = ManageReservationUtils.mockBasketResponse();

    when(authenticatedUserService.isUserAuthenticated()).thenReturn(Boolean.TRUE);
    when(basketOutPort.createBasket(anyString(), anyString(), any(), any(), any(), any(), any(), any()))
        .thenReturn(ManageReservationUtils.mockBasketResponse());
    when(reservationOutPort.copyReservations(any())).thenReturn(mockCopyReservationsResponse());
    when(basketOutPort.addReservationsToBasket(any(), any(),
        any(CopyReservationsResponse.class), anyBoolean(), any(Map.class)))
        .thenReturn(ManageReservationUtils.mockBasketResponse());
    when(basketOutPort.getBasketById(any())).thenReturn(basketResponse);
    when(basketOutPort.linkAmendReservationsInBasket(any(), any(), any(), any()))
        .thenReturn(basketResponse);
    when(reservationOutPort.getBookingAllowances(anyString(), anyString(), anyList()))
        .thenReturn(bookingAllowancesResponse);
    when(reservationOutPort.getReservationsPackagesByIds(any(), any())).thenReturn(
        ManageReservationUtils.mockCreateReservationsPackagesResponse(false, ""));
    when(packageProperties.getGroups()).thenReturn(Map.of("MDP", Set.of("MDBFST", "MDBEVA", "MD2DIN")));
    // getSaveAllowancesInBasket() and getApplyOccupancySupplement() both return null on a raw mock,
    // so isEnabled(null)=true bleeds into the occupancy-supplement check; stub the rule to return
    // zero pricing so isOccupancySupplementApplicable() stays false and doesn't NPE.
    when(rulesOutPort.getSingleOccupancySupplementResponse(any()))
        .thenReturn(new SingleOccupancySupplementResponse(basketResponse.getHotelId(), BigDecimal.ZERO));
    var mockedFeatureFlag = mock(FeatureFlag.class);
    lenient().when(unleashWrapper.featureFlag()).thenReturn(mockedFeatureFlag);
    // Use true so that the never().updateAllowances assertion is meaningful:
    // if the businessNotes guard were absent, the basket allowance copy path would execute.
    when(unleashWrapper.isEnabled(mockedFeatureFlag.getSaveAllowancesInBasket()))
        .thenReturn(true);

    // Act
    var response = reservationInPort.copyBooking(copyBookingRequest);

    // Assert — blank/null businessNotes must prevent copyBookingAllowancesIfPresent from copying
    assertNotNull(response);
    verify(reservationOutPort, never()).updateBusinessItems(any());
    verify(basketOutPort, never()).updateAllowances(any(), any(), any());
  }

  @ParameterizedTest
  @ValueSource(booleans = {false, true})
  void amendStayDates_SingleReservationScenario_With_PromotionCode_Success(
      Boolean saveBasketAllowances) {

    var bookingAllowancesResponse = mockCreateBookingAllowancesResponse();
    var basketSingleReservationResponse = mockBasketSingleReservationResponse();
    var manageBookingResponse = mockManageBookingResponse();
    List<String> basketBookingAllowances  =
        basketSingleReservationResponse.getBookingAllowances() != null ?
            basketSingleReservationResponse.getBookingAllowances().stream()
                .map(BookingAllowance::getAllowance).toList() :
            List.of();

    // Arrange
    when(rulesOutPort.getMaxNightsRule(any())).thenReturn(new MaxNightsRuleResponse(9));
    when(basketOutPort.getBasketById(anyString())).thenReturn(basketSingleReservationResponse);
    var mockResByBasketRefSingleResResponse = mockResByBasketRefSingleResResponse();
    mockResByBasketRefSingleResResponse.getReservationByIdList().get(0).getRoomStay()
        .setPromotionCode("ST20RU");
    when(reservationOutPort.getReservationsByIds(any(), any(), eq(false), eq(false),
        eq(false))).thenReturn(
        mockResByBasketRefSingleResResponse);
    when(amendLogicInPort.checkCreateOnHoldReservation(anyString(),
        anyString(), anyString(), anyString())).thenReturn(true);
    when(amendLogicInPort.getAmendOnHoldReservationInterval(anyString(), anyString(), anyString(),
        anyString()))
        .thenReturn(List.of(createAmendOnHoldInterval("2025-10-04", "2025-10-05")));
    when(reservationOutPort.getBookingAllowances("TESTHOTEL", "res1", basketBookingAllowances))
        .thenReturn(bookingAllowancesResponse);

    //mocking create onHoldReservations
    when(reservationOutPort.createReservation(anyString(), any(), anyString()))
        .thenReturn(mockReservationResponse());
    when(basketOutPort.createBasket(anyString(), anyString(), any()))
        .thenReturn(mockBasketSingleReservationResponse());
    when(basketOutPort.addReservationsToBasket(any(), any(),
        any(OhipReservationResponse.class), anyBoolean(), anyBoolean(), eq(null), anyBoolean()))
        .thenReturn(ManageReservationUtils.mockBasketResponse());

    when(rulesOutPort.getMaxNightsRule(any())).thenReturn(new MaxNightsRuleResponse(14));
    when(manageBookingInPort.getManageBookingInformation(anyString(), anyString(),
        anyString(), anyString(),
        any(), anyBoolean(), any())).thenReturn(manageBookingResponse);

    doNothing().when(reservationOutPort).updateReservations(any(UpdateReservationsRequest.class));

    tokenUtilsMock.when(() -> TokenUtils.isValid(anyString(), anyString()))
        .thenReturn(Boolean.TRUE);

    var mockedFeatureFlag = mock(FeatureFlag.class);
    when(unleashWrapper.featureFlag())
        .thenReturn(mockedFeatureFlag);
    when(unleashWrapper.isEnabled(mockedFeatureFlag.getSaveAllowancesInBasket()))
        .thenReturn(saveBasketAllowances);
    when(unleashWrapper.isEnabled(mockedFeatureFlag.getApplyOccupancySupplement()))
        .thenReturn(false);

    // Act
    reservationInPort.amendStayDates(mockAmendStayDatesRequest("2025-10-04", "2025-10-07"), null);

    // Assert
    verify(amendLogicInPort, times(1)).checkCreateOnHoldReservation(anyString(),
        anyString(), anyString(), anyString());
    verify(amendLogicInPort, times(1)).getAmendOnHoldReservationInterval(anyString(),
        anyString(), anyString(), anyString());
    verify(reservationOutPort, times(1)).updateReservations(any());
  }

  @Test
  void updateReservationPackagesById_Success() {
    // Arrange
    var request = new UpdateReservationPackagesByIdRequest();
    request.setHotelId("hotelId");
    request.setArrival("2023-10-05");
    request.setDeparture("2023-10-07");
    request.setRoomsSelections(List.of(new RoomsSelectionsByReservationId("12345", List.of())));
    request.setPreviousRoomsSelections(
        List.of(new RoomsSelectionsByReservationId("12345", List.of())));

    when(reservationOutPort.updateReservationPackagesByReservationId(any())).thenReturn(
        new SaveReservationResponse("basketRef"));
    when(reservationOutPort.getPackages(any())).thenReturn(mockPackagesResponse());
    when(promotionProperties.getPromotionalPackages()).thenReturn(PROMO_PACKAGE);
    when(promotionProperties.getPromotionalPackages()).thenReturn(PROMO_PACKAGE);

    //Act
    var response = reservationInPort.updateReservationPackagesById(request, false);

    //Assert
    assertNotNull(response);
  }


  @ParameterizedTest
  @ValueSource(strings = {"PROMO_PACKAGE", HSATWN})
  void updateReservationPackagesById_excludePackages_Success(String packageCode) {
    // Arrange
    var request = new UpdateReservationPackagesByIdRequest();
    request.setHotelId("hotelId");
    request.setArrival("2023-10-05");
    request.setDeparture("2023-10-07");
    request.setRoomsSelections(List.of(new RoomsSelectionsByReservationId("12345",
        List.of(new PackagesSelection(packageCode, 1)))));
    request.setPreviousRoomsSelections(List.of(new RoomsSelectionsByReservationId("12345",
        List.of(new PackagesSelection(packageCode, 1)))));
    ArgumentCaptor<UpdateReservationPackagesByIdRequest> argCaptor = ArgumentCaptor.forClass(
        UpdateReservationPackagesByIdRequest.class);

    when(reservationOutPort.updateReservationPackagesByReservationId(any())).thenReturn(
        new SaveReservationResponse("basketRef"));
    when(reservationOutPort.getPackages(any())).thenReturn(mockPackagesResponse());
    when(promotionProperties.getPromotionalPackages()).thenReturn(PROMO_PACKAGE);

    //Act
    var response = reservationInPort.updateReservationPackagesById(request, false);

    //Assert
    assertNotNull(response);
    verify(reservationOutPort).updateReservationPackagesByReservationId(argCaptor.capture());
    var updateRequest = argCaptor.getValue();
    assertEquals(0, updateRequest.getRoomsSelections().get(0).getPackagesSelection().size());
    assertEquals(0, updateRequest.getPreviousRoomsSelections().get(0).getPackagesSelection().size());
  }



  @Test
  void updateReservationPackagesById_ThrowsError() {
    // Arrange
    var request = new UpdateReservationPackagesByIdRequest();
    request.setRoomsSelections(List.of(new RoomsSelectionsByReservationId("12345", List.of())));
    request.setPreviousRoomsSelections(
        List.of(new RoomsSelectionsByReservationId("12347", List.of())));

    //Act
    assertThrows(GenericBadRequestException.class,
        () -> reservationInPort.updateReservationPackagesById(request, false));
  }

  @Test
  void updateCnpReservation_Success() {
    // Arrange
    var basketRef = "basketRef";
    var businessAccountCnp = BusinessAccountCnp.builder()
        .purchaseOrder("purchaseOrder")
        .customerReference("customerReference")
        .cardNotPresentAuth("no")
        .breakfastCodeReq(11)
        .dinnerAllowance(BigDecimal.TEN)
        .alcoholAllowed("Yes")
        .carParkingAllowed("Yes")
        .wifiAllowed("Yes")
        .otherChargesAllowed("Yes")
        .build();
    var request = new UpdateCnpReservationRequest(LANGUAGE, COUNTRY, businessAccountCnp,
        new BookerDetailsCnp());

    when(basketOutPort.getBasketById(anyString())).thenReturn(ManageReservationUtils.mockBasketResponse("DISTR"));
    when(rulesOutPort.getBusinessAllowanceRules()).thenReturn(
        buildBusinessAllowanceRulesResponse());
    when(reservationOutPort.getReservationsByIds(anyString(), anyList(), eq(false), eq(false), eq(false)))
        .thenReturn(ManageReservationUtils.mockReservationByBasketRefResponse());
    when(contentOutPort.getBusinessNotes(anyString())).thenReturn(mockBusinessNotesResponseCnp());
    when(contentOutPort.getHotelPaymentInformation(anyString(), anyString(), anyString()))
        .thenReturn(mockHotelPaymentInformation());
    when(distributionProperties.getMeals()).thenReturn(mockMeals());
    var mockedFeatureFlag = mock(FeatureFlag.class);
    when(mockedFeatureFlag.getSaveAllowancesInBasket())
        .thenReturn(mock(Feature.class));
    when(unleashWrapper.featureFlag())
        .thenReturn(mockedFeatureFlag);
    when(unleashWrapper.isEnabled(mockedFeatureFlag.getSaveAllowancesInBasket()))
        .thenReturn(true);

    doNothing().when(reservationOutPort).updateBusinessItems(any());
    when(basketOutPort.updateAllowances(any(), any(), any())).thenReturn("1718800645000");

    //Act
    var response = reservationInPort.updateCnpReservation(basketRef, request);

    // Assert
    assertNotNull(response);
    verify(basketOutPort, times(1)).updateAllowances(any(), any(), any());
  }

  @Test
  void updateCnpReservationBD_Success() {
    // Arrange
    var basketRef = "basketRef";
    var businessAccountCnp = BusinessAccountCnp.builder()
        .purchaseOrder("purchaseOrder")
        .customerReference("customerReference")
        .cardNotPresentAuth("no")
        .breakfastCodeReq(11)
        .dinnerAllowance(BigDecimal.TEN)
        .alcoholAllowed("Yes")
        .carParkingAllowed("Yes")
        .wifiAllowed("Yes")
        .otherChargesAllowed("Yes")
        .build();
    var request = new UpdateCnpReservationRequest(LANGUAGE, COUNTRY, businessAccountCnp,
        new BookerDetailsCnp());

    when(basketOutPort.getBasketById(anyString())).thenReturn(ManageReservationUtils.mockBasketResponse("DISTR"));
    when(rulesOutPort.getBusinessAllowanceRules()).thenReturn(
        buildBusinessAllowanceRulesResponse());
    when(reservationOutPort.getReservationsByIds(anyString(), anyList(), anyBoolean(), anyBoolean(), anyBoolean()))
        .thenReturn(ManageReservationUtils.mockReservationByBasketRefResponse());
    when(contentOutPort.getBusinessNotes(anyString())).thenReturn(mockBusinessNotesResponseCnp());
    when(contentOutPort.getHotelPaymentInformation(anyString(), anyString(), anyString()))
        .thenReturn(mockHotelPaymentInformationBD());
    when(distributionProperties.getMeals()).thenReturn(mockMeals());
    var mockedFeatureFlag = mock(FeatureFlag.class);
    when(mockedFeatureFlag.getSaveAllowancesInBasket())
        .thenReturn(mock(Feature.class));
    when(unleashWrapper.featureFlag())
        .thenReturn(mockedFeatureFlag);
    when(unleashWrapper.isEnabled(mockedFeatureFlag.getSaveAllowancesInBasket()))
        .thenReturn(true);

    doNothing().when(reservationOutPort).updateBusinessItems(any());
    when(basketOutPort.updateAllowances(any(), any(), any())).thenReturn("1718800645000");

    //Act
    var response = reservationInPort.updateCnpReservation(basketRef, request);

    // Assert
    assertNotNull(response);
    verify(basketOutPort, times(1)).updateAllowances(any(), any(), any());
  }

  @Test
  void updateCnpReservation_ThrowsException() {
    // Arrange
    var basketRef = "basketRef";
    var businessAccountCnp = BusinessAccountCnp.builder()
        .purchaseOrder("purchaseOrder")
        .customerReference("customerReference")
        .cardNotPresentAuth("no")
        .breakfastCodeReq(11)
        .dinnerAllowance(BigDecimal.TEN)
        .alcoholAllowed("Yes")
        .carParkingAllowed("Yes")
        .wifiAllowed("Yes")
        .otherChargesAllowed("Yes")
        .build();
    var request = new UpdateCnpReservationRequest(LANGUAGE, COUNTRY, businessAccountCnp,
        new BookerDetailsCnp());

    when(basketOutPort.getBasketById(anyString())).thenReturn(ManageReservationUtils.mockBasketResponse("DISTR"));
    when(rulesOutPort.getBusinessAllowanceRules()).thenReturn(
        buildBusinessAllowanceRulesResponse());
    when(reservationOutPort.getReservationsByIds(anyString(), anyList(), eq(false), eq(false), eq(false)))
        .thenReturn(ManageReservationUtils.mockReservationByBasketRefResponse());
    when(contentOutPort.getBusinessNotes(anyString())).thenReturn(
        mockInvalidBusinessNotesResponseCnp());
    when(contentOutPort.getHotelPaymentInformation(anyString(), anyString(), anyString()))
        .thenReturn(mockHotelPaymentInformation());
    when(distributionProperties.getMeals()).thenReturn(mockMeals());
    var mockedFeatureFlag = mock(FeatureFlag.class);
    when(mockedFeatureFlag.getSaveAllowancesInBasket())
        .thenReturn(mock(Feature.class));
    when(unleashWrapper.featureFlag())
        .thenReturn(mockedFeatureFlag);
    when(unleashWrapper.isEnabled(mockedFeatureFlag.getSaveAllowancesInBasket()))
        .thenReturn(true);

    //Act & Assert
    var e = assertThrows(java.util.concurrent.CompletionException.class,
        () -> reservationInPort.updateCnpReservation(basketRef, request));
    assertEquals(BusinessItemsUpdateException.class, e.getCause().getClass());
  }


  @Test
  void deleteRoutingInstructions_success() {
    // Arrange
    final String hotelId = "HOTELTEST";
    final Set<String> reservationIds = Collections.singleton("147");

    doNothing().when(reservationOutPort).deleteRoutingInstructions(hotelId, reservationIds);

    // Act
    reservationInPort.deleteRoutingInstructions(hotelId, reservationIds);

    // Assert
    verify(reservationOutPort).deleteRoutingInstructions(hotelId, reservationIds);
  }

  @Test
  void linkReservationToLeisureCustomer_success() {
    //Arrange
    var linkReservationToLeisureCustomerRequest =
        LinkReservationToLeisureCustomerRequest.builder()
            .basketReference("BKS-1234a567-qq7b-2024-123s-aa5ssbn557gh")
            .customerAccountId("CUST-12234")
            .build();
    when(basketOutPort.getBasketById(anyString())).thenReturn(ManageReservationUtils.mockBasketResponse());

    //Act
    reservationInPort.linkReservationToLeisureCustomer(linkReservationToLeisureCustomerRequest);

    //Assert
    verify(reservationOutPort, times(1)).linkReservationToLeisureCustomer(argThat(argument ->
            "BKS-1234a567-qq7b-2024-123s-aa5ssbn557gh".equals(argument.getBasketReference()) &&
            "CUST-12234".equals(argument.getCustomerAccountId()) &&
            "TESTHOTEL".equals(argument.getHotelId()) &&
            Set.of("res1", "res2", "res3").equals(argument.getReservationIds())
    ));
  }

  private BusinessAllowanceRuleResponse buildBusinessAllowanceRulesResponse() {
    return BusinessAllowanceRuleResponse.builder().businessAllowances(
            List.of(
                BusinessAllowanceRule.builder().sourceId("BFADBF").aemId("premierInnBreakfast").sourceType("PACKAGE")
                    .pms("PMS").isNotesMandatory(true).build(),
                BusinessAllowanceRule.builder().sourceId("OBFBOX").aemId("boxedBreakfast").sourceType("PACKAGE")
                    .pms("PMS").isNotesMandatory(true).build(),
                BusinessAllowanceRule.builder().sourceId("BFADCT").aemId("continentalBreakfast").sourceType("PACKAGE")
                    .pms("PMS").isNotesMandatory(false).build(),
                BusinessAllowanceRule.builder().sourceId("MD2DIN").aemId("continentalBreakfast").sourceType("PACKAGE")
                    .pms("PMS").isNotesMandatory(false).build(),
                BusinessAllowanceRule.builder().sourceId("MDBFST").aemId("mealDeal").sourceType("PACKAGE")
                    .pms("PMS").isNotesMandatory(false).build(),
                BusinessAllowanceRule.builder().sourceId("MDBEVA").aemId("mealDeal").sourceType("PACKAGE")
                    .pms("PMS").isNotesMandatory(false).build(),
                BusinessAllowanceRule.builder().sourceId("CITYTAX").aemId("cityTax").sourceType("PACKAGE")
                    .pms("OP").targetId("CITY").isTransactionCode(false).isNotesMandatory(false).build(),
                BusinessAllowanceRule.builder().sourceId("dinner").aemId("dinner").sourceType("ALLOWANCE")
                    .pms("PMS").isNotesMandatory(true).build(),
                BusinessAllowanceRule.builder().sourceId("alcohol").aemId("alcohol").sourceType("ALLOWANCE")
                    .pms("PMS").isNotesMandatory(true).build(),
                BusinessAllowanceRule.builder().sourceId("mealDeal").aemId("mealDeal").sourceType("ALLOWANCE")
                    .pms("PMS").isNotesMandatory(false).build(),
                BusinessAllowanceRule.builder().sourceId("continentalBreakfast").aemId("continentalBreakfast")
                    .sourceType("ALLOWANCE").pms("PMS").isNotesMandatory(false).build(),
                BusinessAllowanceRule.builder().sourceId("premierInnBreakfast").aemId("premierInnBreakfast")
                    .sourceType("ALLOWANCE").pms("PMS").isNotesMandatory(false).build(),
                BusinessAllowanceRule.builder().sourceId("carParking").aemId("carParking").sourceType("ALLOWANCE")
                    .pms("PMS").isNotesMandatory(true).build(),
                BusinessAllowanceRule.builder().sourceId("otherCharges").aemId("otherCharges").sourceType("ALLOWANCE")
                    .pms("PMS").isNotesMandatory(false).build(),
                BusinessAllowanceRule.builder().sourceId("ultimateWifi").aemId("ultimateWifi").sourceType("ALLOWANCE")
                    .pms("PMS").isNotesMandatory(false).build(),
                BusinessAllowanceRule.builder().sourceId("accommodation").aemId("accommodation").sourceType("ACCOMMODATION")
                    .pms("PMS").isNotesMandatory(false).build()))
        .build();
  }

  private BusinessNotesResponse mockBusinessNotesResponseCnp() {
    return BusinessNotesResponse.builder()
        .headers(List.of(
            Note.builder().id("authorizedCharges").value(
                "Please charge the {cardType} card used to secure the reservation for all booked items which are "
                    + "included in the final total rate.").build())
        )
        .allowances(List.of(
            Note.builder().id("authorizedAllowances").value(
                "The following Allowances are to be charged to the {cardType} card used to secure the reservation "
                    + "if authorised:").build())
        )
        .businessNotes(List.of(
            BusinessNote.builder().id("carParking").lang(LANGUAGE).allow("Car Parking is Authorised.")
                .deny("Car Parking is NOT Authorised.")
                .build(),
            BusinessNote.builder().id("ultimateWifi").lang(LANGUAGE)
                .allow("Wi-Fi Access is authorised.")
                .deny("WiFi is NOT Authorised.")
                .build(),
            BusinessNote.builder().id("dinner").lang(LANGUAGE)
                .allow("{price} Dinner Allowance is Authorised.")
                .deny("Dinner Allowance is NOT Authorised.")
                .build(),
            BusinessNote.builder().id("alcohol").lang(LANGUAGE)
                .allow("Alcohol is Authorised with the evening meal.")
                .deny("Alcohol is NOT Authorised with the evening meal.")
                .build(),
            BusinessNote.builder().id("otherCharges").lang(LANGUAGE).allow("Other charges "
                    + "(Travel Refresh Kits, Newspapers, etc.) are Authorised.")
                .deny("Other charges (Travel Refresh Kits, Newspapers, etc.) are Not Authorised.")
                .build())
        )
        .packages(List.of(
            Note.builder().id("premierInnBreakfast")
                .value("Premier Inn Breakfast is Pre-Booked and Authorised.")
                .build())
        )
        .cardTypes(List.of(
                Note.builder().id("card").value("Credit").build(),
                Note.builder().id("piba").value("PIBA").build()
            )
        )
        .footers(
            List.of(
                Note.builder().id("cnpAuthorization").value(
                        "Any mCNP authorisation should be ignored in favour of this eCNP authorisation.")
                    .build()
            )
        )
        .build();
  }

  private BusinessNotesResponse mockInvalidBusinessNotesResponseCnp() {
    return BusinessNotesResponse.builder()
        .headers(List.of(
            Note.builder().id("authorizedCharges").value(
                "Please charge the {cardType} card used to secure the reservation for all booked items which are "
                    + "included in the final total rate.").build())
        )
        .allowances(List.of(
            Note.builder().id("authorizedAllowances").value(
                "The following Allowances are to be charged to the {cardType} card used to secure the reservation "
                    + "if authorised:").build())
        )
        .businessNotes(List.of(
            BusinessNote.builder().id("carParking").lang(LANGUAGE).allow("Car Parking is Authorised.")
                .deny("Car Parking is NOT Authorised.")
                .build(),
            BusinessNote.builder().id("otherCharges").lang(LANGUAGE).allow("Other charges "
                    + "(Travel Refresh Kits, Newspapers, etc.) are Authorised.")
                .deny("Other charges (Travel Refresh Kits, Newspapers, etc.) are Not Authorised.")
                .build())
        )
        .packages(List.of(
            Note.builder().id("premierInnBreakfast")
                .value("Premier Inn Breakfast is Pre-Booked and Authorised.")
                .build())
        )
        .cardTypes(List.of(
                Note.builder().id("card").value("Credit").build(),
                Note.builder().id("piba").value("PIBA").build()
            )
        )
        .footers(
            List.of(
                Note.builder().id("cnpAuthorization").value(
                        "Any mCNP authorisation should be ignored in favour of this eCNP authorisation.")
                    .build()
            )
        )
        .build();
  }

  private HotelPaymentInformation mockHotelPaymentInformation() {
    return HotelPaymentInformation.builder()
        .acceptedCreditCards(
            List.of(AcceptedCreditCard.builder()
                .codeOpera("BU")
                .codeOperaCardType("ZZ")
                .build()))
        .build();
  }
  private HotelPaymentInformation mockHotelPaymentInformationBD() {
    return HotelPaymentInformation.builder()
        .acceptedCreditCards(
            List.of(AcceptedCreditCard.builder()
                .codeOpera("BD")
                .codeOperaCardType("ZZ")
                .build()))
        .build();
  }

  private Map<Integer, String> mockMeals() {
    final var mealsMap = new HashMap<Integer, String>();
    mealsMap.put(11, "PREMIER_BREAKFAST");
    mealsMap.put(12, "CONTINENTAL_BREAKFAST");
    mealsMap.put(17, "MEAL_DEAL");
    mealsMap.put(18, "BOXED_BREAKFAST");

    return mealsMap;
  }

  private AmendOnHoldInterval createAmendOnHoldInterval(String arrivalDate,
      String departureDate) {
    return AmendOnHoldInterval.builder()
        .amendArrivalDate(arrivalDate)
        .amendDepartureDate(departureDate)
        .build();
  }

  private ReservationByBasketRefResponse mockResByBasketRefSingleResResponse() {
    return ReservationByBasketRefResponse.builder()
        .amountPaid(BigDecimal.TEN)
        .totalCost(BigDecimal.TEN)
        .newTotal(BigDecimal.TEN)
        .previousTotal(BigDecimal.TEN)
        .balanceOutstanding(BigDecimal.TEN)
        .hotelId("TestId")
        .policyCode("code")
        .currencyCode("GBP")
        .reservationByIdList(List.of(ManageReservationUtils.mockReservationByIdFlex("11111111")))
        .build();
  }

  private AmendStayDatesRequest mockAmendStayDatesRequest(String newStartDate, String newEndDate) {
    return AmendStayDatesRequest.builder()
        .tempBookingRef("AKU9415635")
        .newStartDate(newStartDate)
        .newEndDate(newEndDate)
        .bookingChannel(ManageReservationUtils.mockBookingChannel())
        .token(getMockedToken())
        .build();
  }

  private CancellationPoliciesResponse createCancellationPolicies() {
    var cancellationPolicies = new CancellationPoliciesResponse();

    cancellationPolicies.setText(
        "Cancellations after 1pm on the day of arrival charged 100% of 1 night");
    cancellationPolicies.setTime("2022-04-04T01:00:00+01:00");
    return cancellationPolicies;
  }

  @Test
  void testGetMarketingPreferences_success() {
    String hotelID = "DAHMME";
    String resNo = "12345678";

    var marketingPreferencesResponse = new MarketingPreferencesResponse();
    marketingPreferencesResponse.setContactValue("mail@mail.com");
    marketingPreferencesResponse.setOptIn(true);
    marketingPreferencesResponse.setCustomer(new Customer("Mr", "Sarah", "Smith", "GB", LANGUAGE));
    // Arrange
    when(reservationOutPort.getMarketingPreferences(hotelID, resNo)).thenReturn(
        marketingPreferencesResponse);

    // Act
    var response = reservationInPort.getMarketingPreferences(hotelID, resNo);

    // Assert
    verifyNoMoreInteractions(reservationOutPort);
    assertEquals("mail@mail.com", response.getContactValue());
    assertTrue(response.getOptIn());
    assertNotNull(response.getCustomer());
    assertEquals("Sarah", response.getCustomer().getFirstName());
  }

  @ParameterizedTest
  @ValueSource(booleans = {false, true})
  void getBookingAllowances_success(Boolean useBasketAllowances) {
    // Arrange
    String basketReference = "ABC1234567";
    var basket = ManageReservationUtils.mockCreateBasketResponse(BOOKING_REFERENCE);
    basket.setPaymentOption(ACCOUNT_COMPANY);
    var bookingAllowancesResponse = mockCreateBookingAllowancesResponse();
    when(basketOutPort.getBasketById(anyString())).thenReturn(basket);

    when(reservationOutPort.getBookingAllowances(anyString(), anyString(), anyList()))
        .thenReturn(bookingAllowancesResponse);
    if (useBasketAllowances) {
      basket.setBookingAllowances(bookingAllowancesResponse.getBookingAllowances());
    }

    var mockedFeatureFlag = mock(FeatureFlag.class);
    when(unleashWrapper.featureFlag())
        .thenReturn(mockedFeatureFlag);
    when(unleashWrapper.isEnabled(mockedFeatureFlag.getUseBasketAllowances()))
        .thenReturn(useBasketAllowances);

    // Act
    var response = reservationInPort.getBookingAllowances(basketReference);

    // Assert
    assertNotNull(response);
    assertEquals(1, response.getBookingAllowances().size());
    assertEquals("dinner", response.getBookingAllowances().get(0).getAllowance());
    assertEquals(BigDecimal.TEN, response.getBookingAllowances().get(0).getBudget());
  }

  @ParameterizedTest
  @ValueSource(booleans = {false, true})
  void getBookingAllowances_successWhenBasketAllowancesIsNull(Boolean useBasketAllowances) {
    // Arrange
    String basketReference = "ABC1234567";
    var basket = ManageReservationUtils.mockCreateBasketResponse(BOOKING_REFERENCE);
    basket.setPaymentOption(ACCOUNT_COMPANY);
    var bookingAllowancesResponse = mockCreateBookingAllowancesResponse();
    when(basketOutPort.getBasketById(anyString())).thenReturn(basket);

    when(reservationOutPort.getBookingAllowances(anyString(), anyString(), anyList()))
            .thenReturn(bookingAllowancesResponse);

    var mockedFeatureFlag = mock(FeatureFlag.class);
    when(unleashWrapper.featureFlag())
            .thenReturn(mockedFeatureFlag);
    when(unleashWrapper.isEnabled(mockedFeatureFlag.getUseBasketAllowances()))
            .thenReturn(useBasketAllowances);


    // Act
    var response = reservationInPort.getBookingAllowances(basketReference);

    // Assert
    if (useBasketAllowances) {
      assertNull(response);
    } else {
      assertNotNull(response);
      assertEquals(1, response.getBookingAllowances().size());
      assertEquals("dinner", response.getBookingAllowances().get(0).getAllowance());
      assertEquals(BigDecimal.TEN, response.getBookingAllowances().get(0).getBudget());
    }
  }

  @ParameterizedTest
  @ValueSource(booleans = {false, true})
  void getBookingAllowances_badRequest(Boolean useBasketAllowances) {
    // Arrange
    String basketReference = "ABC1234567";
    var basket = mockCreateNoItemsBasketResponse();
    basket.setPaymentOption(ACCOUNT_COMPANY);
    when(basketOutPort.getBasketById(anyString())).thenReturn(basket);

    if (useBasketAllowances) {
      basket.setBookingAllowances(null);
    }

    // Assert
    Assertions.assertThrows(GenericBadRequestException.class, () ->
        reservationInPort.getBookingAllowances(basketReference));
  }

  private BookingAllowancesResponse mockCreateBookingAllowancesResponse() {
    return BookingAllowancesResponse.builder()
        .bookingAllowances(Collections.singletonList(BookingAllowance.builder()
            .allowance("dinner")
            .budget(BigDecimal.TEN)
            .build()
        ))
        .businessNotes("Business Notes.")
        .build();
  }

  @Test
  void removeRoom_Success() {
      // Arrange
      String tempBookingRef = "TestId1234567";
      BasketResponse basketResponse = ManageReservationUtils.mockBasketResponse();
      basketResponse.setETag("1679667965000");
      var manageBookingResponse = mockManageBookingResponse();

      when(basketOutPort.getBasketById(anyString())).thenReturn(basketResponse);
      doNothing().when(reservationOutPort).deleteReservation(anyString(), anyString());

      tokenUtilsMock.when(() -> TokenUtils.isValid(anyString(), anyString()))
          .thenReturn(Boolean.TRUE);
      when(manageBookingInPort.getManageBookingInformation(anyString(), anyString(),
              anyString(), anyString(),
              any(), anyBoolean(), any())).thenReturn(manageBookingResponse);



      // Act
      var response = reservationInPort.removeRoom(tempBookingRef, "res1", getMockedToken(),
          true, new BookingChannel(PI_BOOKING_CHANNEL, SUBCHANNEL_WEB, LANGUAGE_EN), null);

      // Assert
      assertNotNull(response);
      assertEquals(tempBookingRef, response.getTempBookingRef());
  }

  @Test
  void removeRoom_BasketExpired_ShouldThrowException() {
      // Arrange
      String tempBookingRef = "TestId1234567";
      BasketResponse basketResponse = ManageReservationUtils.mockBasketResponse();
      basketResponse.setStatus("COMPLETED");

      when(basketOutPort.getBasketById(anyString())).thenReturn(basketResponse);
      tokenUtilsMock.when(() -> TokenUtils.isValid(anyString(), anyString()))
          .thenReturn(Boolean.TRUE);

      // Act and assert
      GenericBadRequestException exception = Assertions.assertThrows(
          GenericBadRequestException.class, () -> reservationInPort
              .removeRoom(tempBookingRef, "res1", getMockedToken(), true,
                  new BookingChannel(PI_BOOKING_CHANNEL, SUBCHANNEL_WEB, LANGUAGE_EN), null));

      String expectedMessage = "Basket has expired, please reload session";
      String actualMessage = exception.getMessage();
      assertTrue(actualMessage.contains(expectedMessage));
  }

  @Test
  void removeRoom_BookingNonRefundable_ShouldThrowException() {
       // Arrange
      String tempBookingRef = "TestId1234567";
      BasketResponse basketResponse = ManageReservationUtils.mockBasketResponse();
      basketResponse.setStatus(BASKET_STATUS_OPEN);
      var manageBookingResponse = mockManageBookingResponse();
      manageBookingResponse.setIsCancellable(Boolean.FALSE);

      when(basketOutPort.getBasketById(anyString())).thenReturn(basketResponse);
      tokenUtilsMock.when(() -> TokenUtils.isValid(anyString(), anyString()))
          .thenReturn(Boolean.TRUE);
      when(manageBookingInPort.getManageBookingInformation(anyString(), anyString(),
              anyString(), anyString(),
              any(), anyBoolean(), any())).thenReturn(manageBookingResponse);

      // Act and assert
      GenericBadRequestException exception = Assertions.assertThrows(
          GenericBadRequestException.class, () -> reservationInPort
              .removeRoom(tempBookingRef, "res1", getMockedToken(), true,
                  new BookingChannel(PI_BOOKING_CHANNEL, SUBCHANNEL_WEB, LANGUAGE_EN), null));

      String expectedMessage = "Room removal is forbidden for non-refundable bookings";
      String actualMessage = exception.getMessage();
      assertTrue(actualMessage.contains(expectedMessage));
  }

  @Test
  void removeRoom_WrongReservation_ShouldThrowException() {
      var manageBookingResponse = mockManageBookingResponse();

      // Arrange
      String tempBookingRef = "TestId1234567";
      BasketResponse basketResponse = ManageReservationUtils.mockBasketResponse();
      when(basketOutPort.getBasketById(anyString())).thenReturn(basketResponse);
      tokenUtilsMock.when(() -> TokenUtils.isValid(anyString(), anyString()))
          .thenReturn(Boolean.TRUE);
      when(manageBookingInPort.getManageBookingInformation(anyString(), anyString(),
              anyString(), anyString(),
              any(), anyBoolean(), any())).thenReturn(manageBookingResponse);



      // Act and assert
      GenericBadRequestException exception = Assertions.assertThrows(
          GenericBadRequestException.class, () -> reservationInPort
              .removeRoom(tempBookingRef, "wrongRes1", getMockedToken(), true,
                  new BookingChannel(PI_BOOKING_CHANNEL, SUBCHANNEL_WEB, LANGUAGE_EN), null));

      String expectedMessage = "Room reservation is not part of the basket";
      String actualMessage = exception.getMessage();
      assertTrue(actualMessage.contains(expectedMessage));
  }

  @Test
  void removeRoom_SingleReservation_ShouldThrowException() {
       // Arrange
      String tempBookingRef = "TestId1234567";
      BasketResponse basketResponse = ManageReservationUtils.mockBasketResponse();
      basketResponse.setItems(
          basketResponse.getItems().stream().filter(i -> "res1".equals(i.getSourceId())).collect(
              Collectors.toList()));
      var manageBookingResponse = mockManageBookingResponse();

      when(basketOutPort.getBasketById(anyString())).thenReturn(basketResponse);
      tokenUtilsMock.when(() -> TokenUtils.isValid(anyString(), anyString()))
          .thenReturn(Boolean.TRUE);
      when(manageBookingInPort.getManageBookingInformation(anyString(), anyString(),
              anyString(), anyString(),
              any(), anyBoolean(), any())).thenReturn(manageBookingResponse);


      // Act and assert
      GenericBadRequestException exception = Assertions.assertThrows(
          GenericBadRequestException.class,
          () -> reservationInPort.removeRoom(tempBookingRef, "res1", getMockedToken(), true,
              new BookingChannel(PI_BOOKING_CHANNEL, SUBCHANNEL_WEB, LANGUAGE_EN), null));

      String expectedMessage = "The last room can not be removed";
      String actualMessage = exception.getMessage();
      assertTrue(actualMessage.contains(expectedMessage));
  }

  @Test
  void editRoom_Success() {
    // Arrange
    var tempBasketRef = TEMPORARY_BASKET_REF;
    var bookingChannel = new BookingChannel(PI_BOOKING_CHANNEL, SUBCHANNEL_WEB, LANGUAGE_EN);
    var request = mockRequestEditRoom();
    var reservationByIdResponse =
        mockReservationByBasketRefResponseEditRoom(2).getReservationByIdList().get(0);
    var reservationPackagesResponse = mockReservationsPackagesResponse();
    var editRoomBasket = mockEditRoomBasket();
    var manageBookingResponse = mockManageBookingResponse();
    var mockedFeatureFlag = mock(FeatureFlag.class);

    when(unleashWrapper.featureFlag())
            .thenReturn(mockedFeatureFlag);

    when(basketOutPort.getBasketById(anyString())).thenReturn(editRoomBasket);
    when(authenticatedUserService.isUserAuthenticated()).thenReturn(true);
    when(reservationOutPort.getReservationsByIds(anyString(), anyList(), eq(false), eq(false), eq(true))).thenReturn(
        mockReservationByBasketRefResponseEditRoom(2));
    when(reservationInPort.getAllReservationsJustByBasketReference(tempBasketRef, false, true)).thenReturn(
        mockReservationByBasketRefResponseEditRoom(2));
    when(amendLogicInPort.isLeadGuestUpdated(reservationByIdResponse,
        request.getReservations().get(0))).thenReturn(true);
    when(authenticatedUserService.getCurrentUserAccount()).thenReturn(
        Optional.ofNullable(Account.builder().accessLevel("USER").build()));
    when(reservationOutPort.getWbRoomTypes()).thenReturn(List.of("DOUBLE"));
    when(contentOutPort.getHotelInformation(any(), any(), any())).thenReturn(
        HotelInfoResponse.builder().brand(PI_BOOKING_CHANNEL).build());
    when(rulesOutPort.getMaxRoomOccupancyRule(any(), any())).thenReturn(MaxRoomOccupancyResponse.builder()
        .roomOccupancies(List.of(MaxRoomOccupancyData.builder().adultsNumber(1).childrenNumber(0)
            .acceptedRoomTypes(List.of("DOUBLE")).build())).build());
    when(reservationOutPort.getReservationsPackagesByBasketRef(anyString(), anyString(), anyBoolean()))
        .thenReturn(reservationPackagesResponse);
    doNothing().when(reservationOutPort).amendEditRoom(any());
    when(manageBookingInPort.getManageBookingInformation(anyString(), anyString(),
            anyString(), anyString(),
            any(), anyBoolean(), any())).thenReturn(manageBookingResponse);

    mockedFeatureFlag = mock(FeatureFlag.class);
    when(unleashWrapper.featureFlag())
        .thenReturn(mockedFeatureFlag);
    when(unleashWrapper.isEnabled(mockedFeatureFlag.getApplyOccupancySupplement()))
        .thenReturn(false);


    // Act
    var response = reservationInPort.editRoom(request, tempBasketRef, bookingChannel, null, null);

    // Assert
    assertNotNull(response);
  }

  @ParameterizedTest
  @CsvSource({
      "1, 2, true",
      "2, 1, false"
  })
  void editRoom_Success_addOrRemoveOccupancySupplement(int initialAdultsNo, int requestedAdultsNo,
      Boolean aemSearchRulesFf) {
    // Arrange
    var tempBasketRef = TEMPORARY_BASKET_REF;
    var bookingChannel = new BookingChannel(PI_BOOKING_CHANNEL, SUBCHANNEL_WEB, LANGUAGE_EN);
    var request = mockRequestEditRoom();
    var reservationByIdResponse =
            mockReservationByBasketRefResponseEditRoom(initialAdultsNo).getReservationByIdList().get(0);
    var editRoomBasket = mockEditRoomBasket();
    var manageBookingResponse = mockManageBookingResponse();
    var mockedFeatureAemSearchRules = mock(FeatureFlag.Feature.class);
    var mockedFeatureFlag = mock(FeatureFlag.class);
    when(unleashWrapper.featureFlag()).thenReturn(mockedFeatureFlag);
    when(mockedFeatureFlag.getAemSearchRules()).thenReturn(mockedFeatureAemSearchRules);
    when(unleashWrapper.isEnabled(mockedFeatureFlag.getAemSearchRules())).thenReturn(
        aemSearchRulesFf);

    request.getReservations().get(0).getRoomStay().getRoomOccupancy().setAdultCount(requestedAdultsNo);

    when(basketOutPort.getBasketById(anyString())).thenReturn(editRoomBasket);
    when(authenticatedUserService.isUserAuthenticated()).thenReturn(true);
    when(reservationOutPort.getReservationsByIds(anyString(), anyList(), eq(false), eq(false), eq(true))).thenReturn(
            mockReservationByBasketRefResponseEditRoom(initialAdultsNo));
    when(reservationInPort.getAllReservationsJustByBasketReference(tempBasketRef, false, true)).thenReturn(
            mockReservationByBasketRefResponseEditRoom(initialAdultsNo));
    when(amendLogicInPort.isLeadGuestUpdated(reservationByIdResponse,
            request.getReservations().get(0))).thenReturn(true);
    when(authenticatedUserService.getCurrentUserAccount()).thenReturn(
            Optional.ofNullable(Account.builder().accessLevel("USER").build()));
    when(reservationOutPort.getWbRoomTypes()).thenReturn(List.of("DOUBLE"));
    when(contentOutPort.getHotelInformation(any(), any(), any())).thenReturn(
            HotelInfoResponse.builder().brand(PI_BOOKING_CHANNEL).build());

    if (aemSearchRulesFf) {
      when(contentOutPort.getSearchRules(anyString(), any())).thenReturn(
          SearchRules.builder()
              .roomOccupancies(List.of(
                  RoomOccupancy.builder()
                      .adultsNumber(requestedAdultsNo)
                      .childrenNumber(0)
                      .acceptedRoomTypes(List.of("DOUBLE"))
                      .build()))
              .build());
    } else {
      when(rulesOutPort.getMaxRoomOccupancyRule(any(), any())).thenReturn(
          MaxRoomOccupancyResponse.builder()
              .roomOccupancies(List.of(
                  MaxRoomOccupancyData.builder()
                      .adultsNumber(requestedAdultsNo)
                      .childrenNumber(0)
                      .acceptedRoomTypes(List.of("DOUBLE"))
                      .build()))
              .build());
    }

    when(rulesOutPort.getSingleOccupancySupplementResponse(editRoomBasket.getHotelId()))
            .thenReturn(new SingleOccupancySupplementResponse(editRoomBasket.getHotelId(), new BigDecimal(8)));
    doNothing().when(reservationOutPort).amendEditRoom(any());
    when(manageBookingInPort.getManageBookingInformation(anyString(), anyString(),
            anyString(), anyString(),
            any(), anyBoolean(), any())).thenReturn(manageBookingResponse);
    if (initialAdultsNo > requestedAdultsNo) {
      when(reservationOutPort.getReservationsPackagesByBasketRef(anyString(), anyString(), anyBoolean()))
          .thenReturn(mockReservationPackagesResponse());
    }

    when(unleashWrapper.isEnabled(mockedFeatureFlag.getApplyOccupancySupplement()))
        .thenReturn(true);

    // Act
    var response = reservationInPort.editRoom(request, tempBasketRef, bookingChannel, null, null);

    // Assert
    assertNotNull(response);
  }

  @ParameterizedTest
  @CsvSource({
      "2, 1, true, true",
      "2, 1, false, false"
  })
  void editRoom_Success_OccupancySupplementRemoveAnAdultIsOta(int initialAdultsNo, int requestedAdultsNo,
      Boolean isOta, Boolean aemSearchRulesFf) {
    // Arrange
    var tempBasketRef = TEMPORARY_BASKET_REF;
    var bookingChannel = new BookingChannel(PI_BOOKING_CHANNEL, SUBCHANNEL_WEB, LANGUAGE_EN);
    var request = mockRequestEditRoom();
    var reservationByIdResponse =
        mockReservationByBasketRefResponseEditRoom(initialAdultsNo).getReservationByIdList().get(0);
    var editRoomBasket = mockEditRoomBasketWithOccupancySupplement();
    var manageBookingResponse = mockManageBookingResponse();
    var mockedFeatureAemSearchRules = mock(FeatureFlag.Feature.class);
    var mockedFeatureFlag = mock(FeatureFlag.class);
    when(unleashWrapper.featureFlag()).thenReturn(mockedFeatureFlag);
    when(mockedFeatureFlag.getAemSearchRules()).thenReturn(mockedFeatureAemSearchRules);
    when(unleashWrapper.isEnabled(mockedFeatureFlag.getAemSearchRules())).thenReturn(
        aemSearchRulesFf);

    request.getReservations().get(0).getRoomStay().getRoomOccupancy().setAdultCount(requestedAdultsNo);

    when(basketOutPort.getBasketById(anyString())).thenReturn(editRoomBasket);
    when(authenticatedUserService.isUserAuthenticated()).thenReturn(true);
    when(reservationOutPort.getReservationsByIds(anyString(), anyList(), eq(false), eq(false), eq(true))).thenReturn(
        mockReservationByBasketRefResponseEditRoom(initialAdultsNo));
    when(reservationInPort.getAllReservationsJustByBasketReference(tempBasketRef, false, true)).thenReturn(
        mockReservationByBasketRefResponseEditRoom(initialAdultsNo));
    when(amendLogicInPort.isLeadGuestUpdated(reservationByIdResponse,
        request.getReservations().get(0))).thenReturn(true);
    when(authenticatedUserService.getCurrentUserAccount()).thenReturn(
        Optional.ofNullable(Account.builder().accessLevel("USER").build()));
    when(reservationOutPort.getWbRoomTypes()).thenReturn(List.of("DOUBLE"));
    when(contentOutPort.getHotelInformation(any(), any(), any())).thenReturn(
        HotelInfoResponse.builder().brand(PI_BOOKING_CHANNEL).build());

    if (aemSearchRulesFf) {
      when(contentOutPort.getSearchRules(anyString(), any())).thenReturn(
          SearchRules.builder()
              .roomOccupancies(List.of(
                  RoomOccupancy.builder()
                      .adultsNumber(requestedAdultsNo)
                      .childrenNumber(0)
                      .acceptedRoomTypes(List.of("DOUBLE"))
                      .build()))
              .build());
    } else {
      when(rulesOutPort.getMaxRoomOccupancyRule(any(), any())).thenReturn(
          MaxRoomOccupancyResponse.builder()
              .roomOccupancies(List.of(
                  MaxRoomOccupancyData.builder()
                      .adultsNumber(requestedAdultsNo)
                      .childrenNumber(0)
                      .acceptedRoomTypes(List.of("DOUBLE"))
                      .build()))
              .build());
    }

    when(rulesOutPort.getSingleOccupancySupplementResponse(editRoomBasket.getHotelId()))
        .thenReturn(new SingleOccupancySupplementResponse(editRoomBasket.getHotelId(), new BigDecimal(8)));
    doNothing().when(reservationOutPort).amendEditRoom(any());
    when(manageBookingInPort.getManageBookingInformation(anyString(), anyString(),
        anyString(), anyString(),
        any(), anyBoolean(), any())).thenReturn(manageBookingResponse);
    when(reservationOutPort.getReservationsPackagesByBasketRef(anyString(), anyString(), anyBoolean()))
          .thenReturn(mockReservationPackagesResponse());

    when(unleashWrapper.isEnabled(mockedFeatureFlag.getApplyOccupancySupplement()))
        .thenReturn(true);

    // Act
    var response = reservationInPort.editRoom(request, tempBasketRef, bookingChannel, null, isOta);

    // Assert
    assertNotNull(response);
  }

  @ParameterizedTest
  @ValueSource(ints = {1, 2})
  void editRoom_Success_sameNoOfAdults(int noOfAdults) {
    // Arrange
    var tempBasketRef = TEMPORARY_BASKET_REF;
    var bookingChannel = new BookingChannel(PI_BOOKING_CHANNEL, SUBCHANNEL_WEB, LANGUAGE_EN);
    var request = mockRequestEditRoom(noOfAdults);
    var reservationByIdResponse =
            mockReservationByBasketRefResponseEditRoom(noOfAdults).getReservationByIdList().get(0);
    var editRoomBasket = mockEditRoomBasket();
    var manageBookingResponse = mockManageBookingResponse();
    var mockedFeatureFlag = mock(FeatureFlag.class);

    when(unleashWrapper.featureFlag()).thenReturn(mockedFeatureFlag);
    when(basketOutPort.getBasketById(anyString())).thenReturn(editRoomBasket);
    when(authenticatedUserService.isUserAuthenticated()).thenReturn(true);
    when(reservationOutPort.getReservationsByIds(anyString(), anyList(), eq(false), eq(false), eq(true))).thenReturn(
            mockReservationByBasketRefResponseEditRoom(noOfAdults));
    when(reservationInPort.getAllReservationsJustByBasketReference(tempBasketRef, false, true)).thenReturn(
            mockReservationByBasketRefResponseEditRoom(noOfAdults));
    when(amendLogicInPort.isLeadGuestUpdated(reservationByIdResponse,
            request.getReservations().get(0))).thenReturn(true);
    when(authenticatedUserService.getCurrentUserAccount()).thenReturn(
            Optional.ofNullable(Account.builder().accessLevel("USER").build()));
    when(reservationOutPort.getWbRoomTypes()).thenReturn(List.of("DOUBLE"));
    when(contentOutPort.getHotelInformation(any(), any(), any())).thenReturn(
            HotelInfoResponse.builder().brand(PI_BOOKING_CHANNEL).build());
    when(rulesOutPort.getMaxRoomOccupancyRule(any(), any())).thenReturn(MaxRoomOccupancyResponse.builder()
            .roomOccupancies(List.of(MaxRoomOccupancyData.builder().adultsNumber(noOfAdults).childrenNumber(0)
                    .acceptedRoomTypes(List.of("DOUBLE")).build())).build());
    doNothing().when(reservationOutPort).amendEditRoom(any());
    when(manageBookingInPort.getManageBookingInformation(anyString(), anyString(),
            anyString(), anyString(),
            any(), anyBoolean(), any())).thenReturn(manageBookingResponse);


    // Act
    var response = reservationInPort.editRoom(request, tempBasketRef, bookingChannel, null, null);

    // Assert
    assertNotNull(response);
    verify(rulesOutPort, times(0)).getSingleOccupancySupplementResponse(anyString());
  }

  @Test
  void editRoom_Success_For_Employee_Booking() {
    // Arrange
    var tempBasketRef = TEMPORARY_BASKET_REF;
    var bookingChannel = new BookingChannel(PI_BOOKING_CHANNEL, SUBCHANNEL_WEB, LANGUAGE_EN);
    var request = mockRequestEditRoom();
    var reservationByIdResponse =
        mockReservationByBasketRefResponseEditRoomForEmployee(2).getReservationByIdList().get(0);
    var reservationPackagesResponse = mockReservationsPackagesResponse();
    var editRoomBasket = mockEditRoomBasket();
    var manageBookingResponse = mockManageBookingResponse();
    var mockedFeatureFlag = mock(FeatureFlag.class);

    when(unleashWrapper.featureFlag())
            .thenReturn(mockedFeatureFlag);
    when(basketOutPort.getBasketById(anyString())).thenReturn(editRoomBasket);
    when(authenticatedUserService.isUserAuthenticated()).thenReturn(true);
    when(reservationOutPort.getReservationsByIds(anyString(), anyList(), eq(false), eq(false), eq(true))).thenReturn(
        mockReservationByBasketRefResponseEditRoomForEmployee(2));
    when(reservationInPort.getAllReservationsJustByBasketReference(tempBasketRef, false, true)).thenReturn(
        mockReservationByBasketRefResponseEditRoomForEmployee(2));
    when(amendLogicInPort.isLeadGuestUpdated(reservationByIdResponse,
        request.getReservations().get(0))).thenReturn(true);
    when(authenticatedUserService.getCurrentUserAccount()).thenReturn(
        Optional.ofNullable(Account.builder().accessLevel("USER").build()));
    when(reservationOutPort.getWbRoomTypes()).thenReturn(List.of("DOUBLE"));
    when(contentOutPort.getHotelInformation(any(), any(), any())).thenReturn(
        HotelInfoResponse.builder().brand(PI_BOOKING_CHANNEL).build());
    when(rulesOutPort.getMaxRoomOccupancyRule(any(), any())).thenReturn(MaxRoomOccupancyResponse.builder()
        .roomOccupancies(List.of(MaxRoomOccupancyData.builder().adultsNumber(1).childrenNumber(0)
            .acceptedRoomTypes(List.of("DOUBLE")).build())).build());
    doNothing().when(reservationOutPort).amendEditRoom(any());
    when(reservationOutPort.getReservationsPackagesByBasketRef(anyString(), anyString(), anyBoolean()))
        .thenReturn(reservationPackagesResponse);
    when(companyProperties.getCompanyId()).thenReturn("123456");
    when(manageBookingInPort.getManageBookingInformation(anyString(), anyString(),
            anyString(), anyString(),
            any(), anyBoolean(), any())).thenReturn(manageBookingResponse);

    mockedFeatureFlag = mock(FeatureFlag.class);
    when(unleashWrapper.featureFlag())
        .thenReturn(mockedFeatureFlag);
    when(unleashWrapper.isEnabled(mockedFeatureFlag.getApplyOccupancySupplement()))
        .thenReturn(false);

    // Act
    var response = reservationInPort.editRoom(request, tempBasketRef, bookingChannel, null, null);

    // Assert
    assertNotNull(response);
  }

  @Test
  void editRoom_resetMeals_CharityPackages_Success() {
    // Arrange
    var tempBasketRef = TEMPORARY_BASKET_REF;
    var bookingChannel = new BookingChannel(PI_BOOKING_CHANNEL, SUBCHANNEL_WEB, LANGUAGE_EN);
    var request = mockRequestEditRoom();
    var reservationByIdResponse =
        mockReservationByBasketRefResponseEditRoom(2).getReservationByIdList().get(0);
    var reservationPackagesResponse = mockReservationsPackagesWithCharityResponse();
    var bookingAllowancesResponse = mockCreateBookingAllowancesResponse();
    var editRoomBasket = mockEditRoomBasket();
    var manageBookingResponse = mockManageBookingResponse();
    var mockedFeatureFlag = mock(FeatureFlag.class);

    when(unleashWrapper.featureFlag())
            .thenReturn(mockedFeatureFlag);

    when(basketOutPort.getBasketById(anyString())).thenReturn(editRoomBasket);
    when(authenticatedUserService.isUserAuthenticated()).thenReturn(true);
    when(reservationOutPort.getReservationsByIds(anyString(), anyList(), eq(false), eq(false), eq(true))).thenReturn(
        mockReservationByBasketRefResponseEditRoom(2));
    when(reservationInPort.getAllReservationsJustByBasketReference(tempBasketRef, false, true)).thenReturn(
        mockReservationByBasketRefResponseEditRoom(2));
    when(amendLogicInPort.isLeadGuestUpdated(reservationByIdResponse,
        request.getReservations().get(0))).thenReturn(true);
    when(authenticatedUserService.getCurrentUserAccount()).thenReturn(
        Optional.ofNullable(Account.builder().accessLevel("USER").build()));
    when(reservationOutPort.getWbRoomTypes()).thenReturn(List.of("DOUBLE"));
    when(contentOutPort.getHotelInformation(any(), any(), any())).thenReturn(
        HotelInfoResponse.builder().brand(PI_BOOKING_CHANNEL).build());
    when(rulesOutPort.getMaxRoomOccupancyRule(any(), any())).thenReturn(
        MaxRoomOccupancyResponse.builder()
            .roomOccupancies(
                List.of(MaxRoomOccupancyData.builder().adultsNumber(1).childrenNumber(0)
                    .acceptedRoomTypes(List.of("DOUBLE")).build())).build());
    doNothing().when(reservationOutPort).amendEditRoom(any());
    when(reservationOutPort.getReservationsPackagesByBasketRef(anyString(), anyString(), anyBoolean()))
        .thenReturn(reservationPackagesResponse);
    when(manageBookingInPort.getManageBookingInformation(anyString(), anyString(),
            anyString(), anyString(),
            any(), anyBoolean(), any())).thenReturn(manageBookingResponse);

    mockedFeatureFlag = mock(FeatureFlag.class);
    when(unleashWrapper.featureFlag())
        .thenReturn(mockedFeatureFlag);
    when(unleashWrapper.isEnabled(mockedFeatureFlag.getApplyOccupancySupplement()))
        .thenReturn(false);
    when(promotionProperties.getPromotionalPackages()).thenReturn(PROMO_PACKAGE);

    // Act
    var response = reservationInPort.editRoom(request, tempBasketRef, bookingChannel, null, null);

    // Assert
    assertNotNull(response);
  }

  @Test
  void editRoom_resetMeals_emptyPackageSelections_Success() {
    // Arrange
    var tempBasketRef = TEMPORARY_BASKET_REF;
    var bookingChannel = new BookingChannel(PI_BOOKING_CHANNEL, SUBCHANNEL_WEB, LANGUAGE_EN);
    var request = mockRequestEditRoom();
    var reservationByIdResponse =
        mockReservationByBasketRefResponseEditRoom(2).getReservationByIdList().get(0);
    var reservationPackagesResponse = mockReservationsPackagesWithEmptySelectionsResponse();
    var bookingAllowancesResponse = mockCreateBookingAllowancesResponse();
    var editRoomBasket = mockEditRoomBasket();
    var manageBookingResponse = mockManageBookingResponse();
    var mockedFeatureFlag = mock(FeatureFlag.class);

    when(unleashWrapper.featureFlag())
            .thenReturn(mockedFeatureFlag);
    when(basketOutPort.getBasketById(anyString())).thenReturn(editRoomBasket);
    when(authenticatedUserService.isUserAuthenticated()).thenReturn(true);
    when(reservationOutPort.getReservationsByIds(anyString(), anyList(), eq(false), eq(false), eq(true))).thenReturn(
        mockReservationByBasketRefResponseEditRoom(2));
    when(reservationInPort.getAllReservationsJustByBasketReference(tempBasketRef, false, true)).thenReturn(
        mockReservationByBasketRefResponseEditRoom(2));
    when(amendLogicInPort.isLeadGuestUpdated(reservationByIdResponse,
        request.getReservations().get(0))).thenReturn(true);
    when(authenticatedUserService.getCurrentUserAccount()).thenReturn(
        Optional.ofNullable(Account.builder().accessLevel("USER").build()));
    when(reservationOutPort.getWbRoomTypes()).thenReturn(List.of("DOUBLE"));
    when(contentOutPort.getHotelInformation(any(), any(), any())).thenReturn(
        HotelInfoResponse.builder().brand(PI_BOOKING_CHANNEL).build());
    when(rulesOutPort.getMaxRoomOccupancyRule(any(), any())).thenReturn(
        MaxRoomOccupancyResponse.builder()
            .roomOccupancies(
                List.of(MaxRoomOccupancyData.builder().adultsNumber(1).childrenNumber(0)
                    .acceptedRoomTypes(List.of("DOUBLE")).build())).build());
    doNothing().when(reservationOutPort).amendEditRoom(any());
    when(reservationOutPort.getReservationsPackagesByBasketRef(anyString(), anyString(), anyBoolean()))
        .thenReturn(reservationPackagesResponse);
    when(manageBookingInPort.getManageBookingInformation(anyString(), anyString(),
            anyString(), anyString(),
            any(), anyBoolean(), any())).thenReturn(manageBookingResponse);

    mockedFeatureFlag = mock(FeatureFlag.class);
    when(unleashWrapper.featureFlag())
        .thenReturn(mockedFeatureFlag);
    when(unleashWrapper.isEnabled(mockedFeatureFlag.getApplyOccupancySupplement()))
        .thenReturn(false);

    // Act
    var response = reservationInPort.editRoom(request, tempBasketRef, bookingChannel, null, null);

    // Assert
    assertNotNull(response);
  }

  @Test
  void editRoom_SelfBooker() {
    // Arrange
    var tempBasketRef = TEMPORARY_BASKET_REF;
    var bookingChannel = new BookingChannel(PI_BOOKING_CHANNEL, SUBCHANNEL_WEB, LANGUAGE_EN);
    var request = mockRequestEditRoom();
    var reservationByIdResponse =
        mockReservationByBasketRefResponseEditRoom(1).getReservationByIdList().get(0);
    var editRoomBasket = mockEditRoomBasket();
    var manageBookingResponse = mockManageBookingResponse();

    when(basketOutPort.getBasketById(anyString())).thenReturn(editRoomBasket);
    when(authenticatedUserService.isUserAuthenticated()).thenReturn(true);
    when(reservationOutPort.getReservationsByIds(anyString(), anyList(), eq(false), eq(false), eq(true))).thenReturn(
        mockReservationByBasketRefResponseEditRoom(1));
    when(reservationInPort.getAllReservationsJustByBasketReference(tempBasketRef, false, true)).thenReturn(
        mockReservationByBasketRefResponseEditRoom(1));
    when(amendLogicInPort.isLeadGuestUpdated(reservationByIdResponse,
        request.getReservations().get(0))).thenReturn(true);
    when(authenticatedUserService.getCurrentUserAccount()).thenReturn(
        Optional.ofNullable(Account.builder().accessLevel("SELF").build()));
    when(manageBookingInPort.getManageBookingInformation(anyString(), anyString(),
            anyString(), anyString(),
            any(), anyBoolean(), any())).thenReturn(manageBookingResponse);

    // Act & Assert
    assertThrows(GenericBadRequestException.class,
        () -> reservationInPort.editRoom(request, tempBasketRef, bookingChannel, null, null));
  }

  @Test
  void editRoom_ReservationNotFoundException() {
    // Arrange
    var tempBasketRef = TEMPORARY_BASKET_REF;
    var bookingChannel = new BookingChannel(PI_BOOKING_CHANNEL, SUBCHANNEL_WEB, LANGUAGE_EN);
    var request = mockRequestEditRoom();
    var editRoomBasket = mockEditRoomBasket();
    var manageBookingResponse = mockManageBookingResponse();

    when(basketOutPort.getBasketById(anyString())).thenReturn(editRoomBasket);
    when(authenticatedUserService.isUserAuthenticated()).thenReturn(true);
    when(reservationOutPort.getReservationsByIds(anyString(), anyList(), anyBoolean(), anyBoolean(), anyBoolean())).thenReturn(
        mockReservationByBasketRefResponseEditRoom(1));
    when(reservationInPort.getAllReservationsJustByBasketReference(tempBasketRef, false, true))
        .thenThrow(new ReservationNotFoundException( ErrorCode.DIGITAL_RESERVATION_ID_EXCEPTION, "test"));
    when(manageBookingInPort.getManageBookingInformation(anyString(), anyString(),
            anyString(), anyString(),
           any(), anyBoolean(), any())).thenReturn(manageBookingResponse);

    // Act & Assert
    assertThrows(ReservationNotFoundException.class,
        () -> reservationInPort.editRoom(request, tempBasketRef, bookingChannel, null, null));
  }

  @Test
  void editRoom_BookingNonRefundable_ShouldThrowException() {
       // Arrange
      var tempBasketRef = TEMPORARY_BASKET_REF;
      var basketResponse = mockEditRoomBasket();
      var request = mockRequestEditRoom();
      var manageBookingResponse = mockManageBookingResponse();
      manageBookingResponse.setIsCancellable(Boolean.FALSE);

      when(basketOutPort.getBasketById(anyString())).thenReturn(basketResponse);
      when(authenticatedUserService.isUserAuthenticated()).thenReturn(true);
      tokenUtilsMock.when(() -> TokenUtils.isValid(anyString(), anyString()))
          .thenReturn(Boolean.TRUE);
      when(manageBookingInPort.getManageBookingInformation(anyString(), anyString(),
              anyString(), anyString(),
              any(), anyBoolean(), any())).thenReturn(manageBookingResponse);

      // Act and assert
      GenericBadRequestException exception = Assertions.assertThrows(
          GenericBadRequestException.class, () -> reservationInPort
              .editRoom(request, tempBasketRef,
                  new BookingChannel(PI_BOOKING_CHANNEL, SUBCHANNEL_WEB, LANGUAGE_EN), null, null));

      String expectedMessage = "Updating the room is forbidden for non-refundable bookings";
      String actualMessage = exception.getMessage();
      assertTrue(actualMessage.contains(expectedMessage));
  }

  @Test
  void editRoom_BookingNonRefundable_CCUI_Success() {
      // Arrange
      var tempBasketRef = TEMPORARY_BASKET_REF;
      var bookingChannel = new BookingChannel(CCUI_BOOKING_CHANNEL, SUBCHANNEL_WEB, LANGUAGE_EN);
      var request = mockRequestEditRoom();
      var reservationByIdResponse =
              mockReservationByBasketRefResponseEditRoom(2).getReservationByIdList().get(0);
      var reservationPackagesResponse = mockReservationsPackagesResponse();
      var editRoomBasket = mockEditRoomBasket();
      var mockedFeatureFlag = mock(FeatureFlag.class);
      request.getBookingChannel().setChannel(CCUI_BOOKING_CHANNEL);

      when(unleashWrapper.featureFlag())
              .thenReturn(mockedFeatureFlag);

      when(basketOutPort.getBasketById(anyString())).thenReturn(editRoomBasket);
      when(authenticatedUserService.isUserAuthenticated()).thenReturn(true);
      when(reservationOutPort.getReservationsByIds(anyString(), anyList(), eq(false), eq(false), eq(true))).thenReturn(
              mockReservationByBasketRefResponseEditRoom(2));
      when(reservationInPort.getAllReservationsJustByBasketReference(tempBasketRef, false, true)).thenReturn(
              mockReservationByBasketRefResponseEditRoom(2));
      when(amendLogicInPort.isLeadGuestUpdated(reservationByIdResponse,
              request.getReservations().get(0))).thenReturn(true);
      when(authenticatedUserService.getCurrentUserAccount()).thenReturn(
              Optional.ofNullable(Account.builder().accessLevel("USER").build()));
      when(reservationOutPort.getWbRoomTypes()).thenReturn(List.of("DOUBLE"));
      when(contentOutPort.getHotelInformation(any(), any(), any())).thenReturn(
              HotelInfoResponse.builder().brand(CCUI_BOOKING_CHANNEL).build());
      when(rulesOutPort.getMaxRoomOccupancyRule(any(), any())).thenReturn(MaxRoomOccupancyResponse.builder()
              .roomOccupancies(List.of(MaxRoomOccupancyData.builder().adultsNumber(1).childrenNumber(0)
                      .acceptedRoomTypes(List.of("DOUBLE")).build())).build());
      when(reservationOutPort.getReservationsPackagesByBasketRef(anyString(), anyString(), anyBoolean()))
              .thenReturn(reservationPackagesResponse);
      doNothing().when(reservationOutPort).amendEditRoom(any());

      mockedFeatureFlag = mock(FeatureFlag.class);
      when(unleashWrapper.featureFlag())
              .thenReturn(mockedFeatureFlag);
      when(unleashWrapper.isEnabled(mockedFeatureFlag.getApplyOccupancySupplement()))
              .thenReturn(false);

      // Act
      var response = reservationInPort.editRoom(request, tempBasketRef, bookingChannel, null, null);

      // Assert
      assertNotNull(response);
  }

  @Test
  void editRoom_WrongRoom() {
    // Arrange
    var tempBasketRef = TEMPORARY_BASKET_REF;
    var bookingChannel = new BookingChannel(PI_BOOKING_CHANNEL, SUBCHANNEL_WEB, LANGUAGE_EN);
    var request = mockRequestEditRoom();
    var reservationByIdResponse =
        mockReservationByBasketRefResponseEditRoom(1).getReservationByIdList().get(0);
    var editRoomBasket = mockEditRoomBasket();
    var manageBookingResponse = mockManageBookingResponse();
    var mockedFeatureFlag = mock(FeatureFlag.class);

    when(unleashWrapper.featureFlag())
            .thenReturn(mockedFeatureFlag);

    when(basketOutPort.getBasketById(anyString())).thenReturn(editRoomBasket);
    when(authenticatedUserService.isUserAuthenticated()).thenReturn(true);
    when(reservationOutPort.getReservationsByIds(anyString(), anyList(), eq(false), eq(false), eq(true))).thenReturn(
        mockReservationByBasketRefResponseEditRoom(1));
    when(reservationInPort.getAllReservationsJustByBasketReference(tempBasketRef, false, true)).thenReturn(
        mockReservationByBasketRefResponseEditRoom(1));
    when(amendLogicInPort.isLeadGuestUpdated(reservationByIdResponse,
        request.getReservations().get(0))).thenReturn(true);
    when(authenticatedUserService.getCurrentUserAccount()).thenReturn(
        Optional.ofNullable(Account.builder().accessLevel("USER").build()));
    when(reservationOutPort.getWbRoomTypes()).thenReturn(List.of("DOUBLE"));
    when(contentOutPort.getHotelInformation(any(), any(), any())).thenReturn(
        HotelInfoResponse.builder().brand(PI_BOOKING_CHANNEL).build());
    when(rulesOutPort.getMaxRoomOccupancyRule(any(), any())).thenReturn(
        MaxRoomOccupancyResponse.builder()
            .roomOccupancies(
                List.of(MaxRoomOccupancyData.builder().adultsNumber(2).childrenNumber(0)
                    .acceptedRoomTypes(List.of("FAMILY")).build())).build());
    when(manageBookingInPort.getManageBookingInformation(anyString(), anyString(),
            anyString(), anyString(),
            any(), anyBoolean(), any())).thenReturn(manageBookingResponse);

    // Act & Assert
    assertThrows(GenericBadRequestException.class,
        () -> reservationInPort.editRoom(request, tempBasketRef, bookingChannel, null, null));
  }

  @Test
  void editRoom_BasketNotFound() {
    // Arrange
    var tempBasketRef = TEMPORARY_BASKET_REF;
    var bookingChannel = new BookingChannel(PI_BOOKING_CHANNEL, SUBCHANNEL_WEB, LANGUAGE_EN);
    var request = mockRequestEditRoom();

    when(basketOutPort.getBasketById(anyString())).thenThrow(BasketNotFoundException.class);

    assertThrows(BasketNotFoundException.class,
        () -> reservationInPort.editRoom(request, tempBasketRef, bookingChannel, null, null));
  }

  @Test
  void editRoom_errorOnEditRoomUpdateRequestBasketNotFound() {
    // Arrange
    var tempBasketRef = TEMPORARY_BASKET_REF;
    var bookingChannel = new BookingChannel(PI_BOOKING_CHANNEL, SUBCHANNEL_WEB, LANGUAGE_EN);
    var request = mockRequestEditRoom();

    var editRoomBasket = mockEditRoomBasket();
    when(basketOutPort.getBasketById(anyString())).thenReturn(editRoomBasket);
    
    when(basketOutPort.getBasketById(anyString())).thenThrow(GenericReservationException.class);

    assertThrows(GenericReservationException.class,
        () -> reservationInPort.editRoom(request, tempBasketRef, bookingChannel, null, null));
  }

  @Test
  void editRoom_HotelReservationOhipException() {
    // Arrange
    var tempBasketRef = TEMPORARY_BASKET_REF;
    var bookingChannel = new BookingChannel(PI_BOOKING_CHANNEL, SUBCHANNEL_WEB, LANGUAGE_EN);
    var request = mockRequestEditRoom();
    var reservationByIdResponse =
        mockReservationByBasketRefResponseEditRoom(1).getReservationByIdList().get(0);
    var editRoomBasket = mockEditRoomBasket();
    var manageBookingResponse = mockManageBookingResponse();
    var mockedFeatureFlag = mock(FeatureFlag.class);

    when(unleashWrapper.featureFlag())
            .thenReturn(mockedFeatureFlag);

    when(basketOutPort.getBasketById(anyString())).thenReturn(editRoomBasket);
    when(authenticatedUserService.isUserAuthenticated()).thenReturn(true);
    when(reservationOutPort.getReservationsByIds(anyString(), anyList(), anyBoolean(), anyBoolean(), anyBoolean())).thenReturn(
        mockReservationByBasketRefResponseEditRoom(1));
    when(reservationInPort.getAllReservationsJustByBasketReference(tempBasketRef, false, true)).thenReturn(
        mockReservationByBasketRefResponseEditRoom(1));
    when(amendLogicInPort.isLeadGuestUpdated(reservationByIdResponse,
        request.getReservations().get(0))).thenReturn(true);
    when(authenticatedUserService.getCurrentUserAccount()).thenReturn(
        Optional.ofNullable(Account.builder().accessLevel("USER").build()));
    when(reservationOutPort.getWbRoomTypes()).thenReturn(List.of("DOUBLE"));
    when(contentOutPort.getHotelInformation(any(), any(), any())).thenReturn(
        HotelInfoResponse.builder().brand(PI_BOOKING_CHANNEL).build());
    when(rulesOutPort.getMaxRoomOccupancyRule(any(), any())).thenReturn(
        MaxRoomOccupancyResponse.builder()
            .roomOccupancies(
                List.of(MaxRoomOccupancyData.builder().adultsNumber(1).childrenNumber(0)
                    .acceptedRoomTypes(List.of("DOUBLE")).build())).build());
    doThrow(HotelReservationOhipException.class).when(reservationOutPort).amendEditRoom(any());
    when(manageBookingInPort.getManageBookingInformation(anyString(), anyString(),
            anyString(), anyString(),
            any(), anyBoolean(), any())).thenReturn(manageBookingResponse);

    // Act & Assert
    assertThrows(HotelReservationOhipException.class,
        () -> reservationInPort.editRoom(request, tempBasketRef, bookingChannel, null, null));
  }

  @Test
  void rollbackReservation_Success() {
    // Arrange
    when(reservationOutPort.cancelReservation(any(), any())).thenReturn(
        new CancelReservationResponse());

    // Act
    var response = reservationInPort.rollbackReservation(
        CancelReservationRequest.builder().build());

    //Assert
    assertNotNull(response);
  }

  @ParameterizedTest
  @ValueSource(strings = {
      "PAY_ON_ARRIVAL",
      "RESERVE_WITHOUT_CARD"
  })
  void confirmAmend_ohipCallIsMadeOnlyForInitialOriginalReservations(
      PaymentOption originalBasketPaymentOption) {
    // Arrange
    var originalBasketItems = new ArrayList<BasketItemResponse>();
    originalBasketItems.add(BasketItemResponse.builder().sourceId(SOURCE_ID_111111).build());

    var originalBasket = BasketResponse.builder()
        .bookingReference(ORIGINAL_BASKET_REF)
        .reference(ORIGINAL_BASKET_REF)
        .hotelId(HOTEL_CODE_TEST)
        .items(originalBasketItems)
        .eTag(String.valueOf(Instant.now().toEpochMilli()))
        .paymentOption(originalBasketPaymentOption)
        .build();

    var bookingAllowancesResponse = mockCreateBookingAllowancesResponse();

    var tempBasket = BasketResponse.builder()
        .bookingReference(TEMPORARY_BASKET_REF)
        .reference(TEMPORARY_BASKET_REF)
        .status(BASKET_STATUS_OPEN)
        .items(List.of(BasketItemResponse.builder().sourceId(SOURCE_ID_222222).build(),
            BasketItemResponse.builder().sourceId(SOURCE_ID_333333).build()))
        .linkAmendReservations(Map.of(SOURCE_ID_111111, SOURCE_ID_222222))
        .hotelId(HOTEL_CODE_TEST)
        .originalBasketId(ORIGINAL_BASKET_REF)
        .eTag(String.valueOf(Instant.now().plus(1, ChronoUnit.MINUTES).toEpochMilli()))
        .paymentOption(PaymentOption.PAY_ON_ARRIVAL)
        .build();

    List<String> tempBasketBookingAllowances = tempBasket.getBookingAllowances() != null ?
        tempBasket.getBookingAllowances().stream()
            .map(BookingAllowance::getAllowance)
            .toList() : List.of();

    var reservationByIdResponse = new ArrayList<ReservationByIdResponse>();
    reservationByIdResponse.add(ManageReservationUtils.mockReservationByIdResponseConfirm(true));
    reservationByIdResponse.add(ManageReservationUtils.mockReservationByIdResponseConfirm(false));

    var basketByRefResponse = ReservationByBasketRefResponse.builder()
        .reservationByIdList(reservationByIdResponse)
        .build();

    when(basketOutPort.getBasketById(ORIGINAL_BASKET_REF)).thenReturn(originalBasket);
    when(basketOutPort.getBasketById(TEMPORARY_BASKET_REF)).thenReturn(tempBasket);
    when(reservationOutPort.getReservationsByIds(eq(HOTEL_CODE_TEST), anyList(), eq(false),
        eq(false), eq(true)))
        .thenReturn(basketByRefResponse);
    when(reservationOutPort.getReservationsByIds(eq(HOTEL_CODE_TEST), anyList(), eq(false),
        eq(false), eq(false)))
        .thenReturn(basketByRefResponse);
    CopyReservationsResponse copyReservationsResponse = mockCopyReservationsResponse();
    when(reservationOutPort.copyReservations(any())).thenReturn(
        copyReservationsResponse);
    when(basketOutPort.addReservationsToBasket(originalBasket.getReference(),
        originalBasket.getETag().replace("\"", ""),
        copyReservationsResponse, false,
        Map.of(copyReservationsResponse.getReservations().get(0).getReservationId(), false)))
        .then(invocation -> {
          originalBasket.getItems()
              .add(BasketItemResponse.builder().sourceId(SOURCE_ID_444444).build());
          return originalBasket;
        });
    when(reservationOutPort.getBookingAllowances(HOTEL_CODE_TEST, SOURCE_ID_222222, tempBasketBookingAllowances))
        .thenReturn(bookingAllowancesResponse);
    when(rulesOutPort.getBusinessAllowanceRules()).thenReturn(buildBusinessAllowanceRuleResponse());
    when(contentOutPort.getBusinessNotes(LANGUAGE)).thenReturn(buildBusinessNotesResponse());
    when(contentOutPort.getHotelPaymentInformation(tempBasket.getHotelId(), LANGUAGE, COUNTRY))
        .thenReturn(getPaymentResponse());
    when(reservationOutPort.confirmAmend(any())).thenReturn(basketByRefResponse);

    when(reservationOutPort.getReservationsPackagesByIds(any(), any()))
        .thenReturn(getMockedReservationsPackagesResponseTempBasket());

    var mockedFeatureFlag = mock(FeatureFlag.class);
    when(unleashWrapper.featureFlag())
        .thenReturn(mockedFeatureFlag);
    when(unleashWrapper.isEnabled(mockedFeatureFlag.getSaveAllowancesInBasket()))
        .thenReturn(false);

    stubCancellationPoliciesFetch();

    var request = new ConfirmAmendRequest(ORIGINAL_BASKET_REF, TEMPORARY_BASKET_REF, TOKEN,
        BookingChannel.builder().channel(PI_BOOKING_CHANNEL).subchannel(SUBCHANNEL_WEB).language(
            LANGUAGE_EN).build(),
        false, false, PaymentOption.PAY_ON_ARRIVAL.toString(), "", "");

    // Act
    var response = reservationInPort.confirmAmend(request, true);

    // Assert
    assertNotNull(response);
    verify(reservationOutPort).confirmAmend(confirmAmendOnReservationsCaptor.capture());
    var confirmAmendOnReservationsRequest = confirmAmendOnReservationsCaptor.getValue();
    assertThat(confirmAmendOnReservationsRequest.getOriginalReservations(), contains(
        SOURCE_ID_111111));
    var linkAmendReservations = confirmAmendOnReservationsRequest.getLinkAmendReservations();
    assertThat(linkAmendReservations.keySet(), contains(SOURCE_ID_111111));
    assertThat(linkAmendReservations.get(SOURCE_ID_111111), is(SOURCE_ID_222222));

    verify(reservationOutPort)
        .updateCancellationPolicies(updateCancellationPoliciesCaptor.capture());
    var updateCancellationPoliciesRequest = updateCancellationPoliciesCaptor.getValue();
    assertThat(updateCancellationPoliciesRequest.getHotelId(), is(HOTEL_CODE_TEST));
    assertThat(updateCancellationPoliciesRequest.getReservationIds(),
        containsInAnyOrder(SOURCE_ID_111111, SOURCE_ID_444444));
  }

  @ParameterizedTest
  @ValueSource(strings = {
      "PAY_ON_ARRIVAL",
      "RESERVE_WITHOUT_CARD"
  })
  void confirmAmend_SuccessAddReservation(PaymentOption originalBasketPaymentOption) {
    // Arrange
    String originalBasketRef = ORIGINAL_BASKET_REF;
    String tempBasketRef = TEMPORARY_BASKET_REF;

    var originalBasket = BasketResponse.builder()
        .bookingReference(originalBasketRef)
        .reference(originalBasketRef)
        .hotelId(HOTEL_CODE_TEST)
        .items(List.of(BasketItemResponse.builder().sourceId(SOURCE_ID_111111).build()))
        .eTag(String.valueOf(Instant.now().toEpochMilli()))
        .paymentOption(originalBasketPaymentOption)
        .build();

    var bookingAllowancesResponse = mockCreateBookingAllowancesResponse();

    var tempBasket = BasketResponse.builder()
        .bookingReference(tempBasketRef)
        .reference(tempBasketRef)
        .status(BASKET_STATUS_OPEN)
        .items(List.of(BasketItemResponse.builder().sourceId(SOURCE_ID_222222).build(),
            BasketItemResponse.builder().sourceId(SOURCE_ID_333333).build()))
        .linkAmendReservations(Map.of(SOURCE_ID_111111, SOURCE_ID_222222))
        .hotelId(HOTEL_CODE_TEST)
        .originalBasketId(originalBasketRef)
        .eTag(String.valueOf(Instant.now().plus(1, ChronoUnit.MINUTES).toEpochMilli()))
        .paymentOption(PaymentOption.PAY_ON_ARRIVAL)
        .build();

    var reservationByIdResponse = new ArrayList<ReservationByIdResponse>();
    reservationByIdResponse.add(ManageReservationUtils.mockReservationByIdResponseConfirm(true));
    reservationByIdResponse.add(ManageReservationUtils.mockReservationByIdResponseConfirm(false));

    var basketByRefResponse = ReservationByBasketRefResponse.builder()
        .reservationByIdList(reservationByIdResponse)
        .build();
    List<String> tempBasketBookingAllowances = tempBasket.getBookingAllowances() != null ?
        tempBasket.getBookingAllowances().stream()
            .map(BookingAllowance::getAllowance)
            .toList() : List.of();

    when(basketOutPort.getBasketById(originalBasketRef)).thenReturn(originalBasket);
    when(basketOutPort.getBasketById(tempBasketRef)).thenReturn(tempBasket);
    when(reservationOutPort.getReservationsByIds(eq(HOTEL_CODE_TEST), anyList(), eq(false),
        eq(false), eq(true)))
        .thenReturn(basketByRefResponse);
    when(reservationOutPort.getReservationsByIds(eq(HOTEL_CODE_TEST), anyList(), eq(false),
        eq(false), eq(false)))
        .thenReturn(basketByRefResponse);
    CopyReservationsResponse copyReservationsResponse = mockCopyReservationsResponse();
    when(reservationOutPort.copyReservations(any())).thenReturn(
        copyReservationsResponse);
    when(basketOutPort.addReservationsToBasket(originalBasket.getReference(),
        originalBasket.getETag().replace("\"", ""),
        copyReservationsResponse, false,
        Map.of(copyReservationsResponse.getReservations().get(0).getReservationId(), false)))
        .thenReturn(originalBasket);
    when(reservationOutPort.getBookingAllowances(HOTEL_CODE_TEST, SOURCE_ID_222222, tempBasketBookingAllowances))
          .thenReturn(bookingAllowancesResponse);
    when(rulesOutPort.getBusinessAllowanceRules()).thenReturn(buildBusinessAllowanceRuleResponse());
    when(contentOutPort.getBusinessNotes(LANGUAGE)).thenReturn(buildBusinessNotesResponse());
    when(contentOutPort.getHotelPaymentInformation(tempBasket.getHotelId(), LANGUAGE, COUNTRY))
        .thenReturn(getPaymentResponse());
    when(reservationOutPort.confirmAmend(any())).thenReturn(basketByRefResponse);

    when(reservationOutPort.getReservationsPackagesByIds(any(), any()))
        .thenReturn(getMockedReservationsPackagesResponseTempBasket());

    var mockedFeatureFlag = mock(FeatureFlag.class);
    when(unleashWrapper.featureFlag())
        .thenReturn(mockedFeatureFlag);
    when(unleashWrapper.isEnabled(mockedFeatureFlag.getSaveAllowancesInBasket()))
        .thenReturn(false);

    stubCancellationPoliciesFetch();

    var request = new ConfirmAmendRequest(originalBasketRef, tempBasketRef, TOKEN,
        BookingChannel.builder().channel(PI_BOOKING_CHANNEL).subchannel(SUBCHANNEL_WEB).language(
            LANGUAGE_EN).build(),
        false, false, PaymentOption.PAY_ON_ARRIVAL.toString(), "", "");

    // Act
    var response = reservationInPort.confirmAmend(request, true);

    // Assert
    assertNotNull(response);
    assertEquals(1, response.getReservationByIdList().size());
    assertEquals("2023-10-20", response.getReservationByIdList().get(0)
        .getReservationPackageList().get(0).getStartDate());
    verify(basketOutPort, times(1)).triggerEmailConfirmation(any());
    verify(amendPayNowLogic, never()).saveDepositFolios(any(), any(), any(), any());
    verify(reservationOutPort).updateBusinessItems(any(BusinessItemsRequest.class));
    verify(reservationOutPort).movePaymentDetails(eq(tempBasket.getHotelId()), anySet());
    verify(reservationOutPort).confirmReservation(confirmReservationCaptor.capture());
    var confirmAmendRequest = confirmReservationCaptor.getValue();
    assertEquals(originalBasketPaymentOption.equals(RESERVE_WITHOUT_CARD),
        Objects.isNull(confirmAmendRequest.getPaymentCard()));
    assertNull(confirmAmendRequest.getPibaCardPresent());
  }

  @Test
  void confirmAmend_SuccessAddReservation_shouldSetCnpAlerts_whenFeatureFlagEnabled() {
    // Arrange
    String originalBasketRef = ORIGINAL_BASKET_REF;
    String tempBasketRef = TEMPORARY_BASKET_REF;

    var originalBasket = BasketResponse.builder()
        .bookingReference(originalBasketRef)
        .reference(originalBasketRef)
        .hotelId(HOTEL_CODE_TEST)
        .items(List.of(BasketItemResponse.builder().sourceId(SOURCE_ID_111111).build()))
        .eTag(String.valueOf(Instant.now().toEpochMilli()))
        .paymentOption(PaymentOption.PAY_ON_ARRIVAL)
        .build();

    var bookingAllowancesResponse = mockCreateBookingAllowancesResponse();

    var tempBasket = BasketResponse.builder()
        .bookingReference(tempBasketRef)
        .reference(tempBasketRef)
        .status(BASKET_STATUS_OPEN)
        .items(List.of(BasketItemResponse.builder().sourceId(SOURCE_ID_222222).build(),
            BasketItemResponse.builder().sourceId(SOURCE_ID_333333).build()))
        .linkAmendReservations(Map.of(SOURCE_ID_111111, SOURCE_ID_222222))
        .hotelId(HOTEL_CODE_TEST)
        .originalBasketId(originalBasketRef)
        .eTag(String.valueOf(Instant.now().plus(1, ChronoUnit.MINUTES).toEpochMilli()))
        .paymentOption(PaymentOption.PAY_ON_ARRIVAL)
        .build();

    var reservationByIdResponse = new ArrayList<ReservationByIdResponse>();
    reservationByIdResponse.add(ManageReservationUtils.mockReservationByIdResponseConfirm(true));
    reservationByIdResponse.add(ManageReservationUtils.mockReservationByIdResponseConfirm(false));

    var basketByRefResponse = ReservationByBasketRefResponse.builder()
        .reservationByIdList(reservationByIdResponse)
        .build();
    List<String> tempBasketBookingAllowances = tempBasket.getBookingAllowances() != null ?
        tempBasket.getBookingAllowances().stream()
            .map(BookingAllowance::getAllowance)
            .toList() : List.of();

    when(basketOutPort.getBasketById(originalBasketRef)).thenReturn(originalBasket);
    when(basketOutPort.getBasketById(tempBasketRef)).thenReturn(tempBasket);
    when(reservationOutPort.getReservationsByIds(eq(HOTEL_CODE_TEST), anyList(), eq(false),
        eq(false), eq(true)))
        .thenReturn(basketByRefResponse);
    when(reservationOutPort.getReservationsByIds(eq(HOTEL_CODE_TEST), anyList(), eq(false),
        eq(false), eq(false)))
        .thenReturn(basketByRefResponse);
    CopyReservationsResponse copyReservationsResponse = mockCopyReservationsResponse();
    when(reservationOutPort.copyReservations(any())).thenReturn(
        copyReservationsResponse);
    when(basketOutPort.addReservationsToBasket(originalBasket.getReference(),
        originalBasket.getETag().replace("\"", ""),
        copyReservationsResponse, false,
        Map.of(copyReservationsResponse.getReservations().get(0).getReservationId(), false)))
        .thenReturn(originalBasket);
    when(reservationOutPort.getBookingAllowances(HOTEL_CODE_TEST, SOURCE_ID_222222, tempBasketBookingAllowances))
          .thenReturn(bookingAllowancesResponse);
    when(rulesOutPort.getBusinessAllowanceRules()).thenReturn(buildBusinessAllowanceRuleResponse());
    when(contentOutPort.getBusinessNotes(LANGUAGE)).thenReturn(buildBusinessNotesResponse());
    when(contentOutPort.getHotelPaymentInformation(tempBasket.getHotelId(), LANGUAGE, COUNTRY))
        .thenReturn(getPaymentResponse());
    when(reservationOutPort.confirmAmend(any())).thenReturn(basketByRefResponse);

    when(reservationOutPort.getReservationsPackagesByIds(any(), any()))
        .thenReturn(getMockedReservationsPackagesResponseTempBasket());

    var mockedFeatureFlag = mock(FeatureFlag.class);
    var setCnpAlertsFlag = mock(FeatureFlag.Feature.class);
    when(mockedFeatureFlag.getSetCnpBookingAlerts()).thenReturn(setCnpAlertsFlag);
    when(unleashWrapper.featureFlag())
        .thenReturn(mockedFeatureFlag);
    // all unstubbed getters on mockedFeatureFlag return null, so isEnabled(null) covers them
    when(unleashWrapper.isEnabled((FeatureFlag.Feature) null))
        .thenReturn(false);
    when(unleashWrapper.isEnabled(setCnpAlertsFlag))
        .thenReturn(true);

    stubCancellationPoliciesFetch();

    var request = new ConfirmAmendRequest(originalBasketRef, tempBasketRef, TOKEN,
        BookingChannel.builder().channel(PI_BOOKING_CHANNEL).subchannel(SUBCHANNEL_WEB).language(
            LANGUAGE_EN).build(),
        false, false, PaymentOption.PAY_ON_ARRIVAL.toString(), "", "");

    // Act
    reservationInPort.confirmAmend(request, true);

    // Assert
    verify(reservationOutPort).movePaymentDetails(eq(tempBasket.getHotelId()), anySet());
    var alertsCaptor = ArgumentCaptor.forClass(UpdateReservationAlertsRequest.class);
    verify(reservationOutPort).updateReservationAlerts(alertsCaptor.capture());
    var alertsRequest = alertsCaptor.getValue();
    assertEquals(HOTEL_CODE_TEST, alertsRequest.getHotelId());
    assertEquals(1, alertsRequest.getAlerts().size());
    var alert = alertsRequest.getAlerts().get(0);
    assertEquals("ECNP", alert.getCode());
    assertEquals("CHECKIN", alert.getArea());
    assertTrue(alert.isScreenNotification());
    assertFalse(alert.isPrinterNotification());
    assertTrue(alert.getDescription().contains("Payment Check Required"));
  }

  @Test
  void confirmAmend_SuccessAddReservation_shouldNotSetCnpAlerts_whenFeatureFlagDisabled() {
    // Arrange
    String originalBasketRef = ORIGINAL_BASKET_REF;
    String tempBasketRef = TEMPORARY_BASKET_REF;

    var originalBasket = BasketResponse.builder()
        .bookingReference(originalBasketRef)
        .reference(originalBasketRef)
        .hotelId(HOTEL_CODE_TEST)
        .items(List.of(BasketItemResponse.builder().sourceId(SOURCE_ID_111111).build()))
        .eTag(String.valueOf(Instant.now().toEpochMilli()))
        .paymentOption(PaymentOption.PAY_ON_ARRIVAL)
        .build();

    var bookingAllowancesResponse = mockCreateBookingAllowancesResponse();

    var tempBasket = BasketResponse.builder()
        .bookingReference(tempBasketRef)
        .reference(tempBasketRef)
        .status(BASKET_STATUS_OPEN)
        .items(List.of(BasketItemResponse.builder().sourceId(SOURCE_ID_222222).build(),
            BasketItemResponse.builder().sourceId(SOURCE_ID_333333).build()))
        .linkAmendReservations(Map.of(SOURCE_ID_111111, SOURCE_ID_222222))
        .hotelId(HOTEL_CODE_TEST)
        .originalBasketId(originalBasketRef)
        .eTag(String.valueOf(Instant.now().plus(1, ChronoUnit.MINUTES).toEpochMilli()))
        .paymentOption(PaymentOption.PAY_ON_ARRIVAL)
        .build();

    var reservationByIdResponse = new ArrayList<ReservationByIdResponse>();
    reservationByIdResponse.add(ManageReservationUtils.mockReservationByIdResponseConfirm(true));
    reservationByIdResponse.add(ManageReservationUtils.mockReservationByIdResponseConfirm(false));

    var basketByRefResponse = ReservationByBasketRefResponse.builder()
        .reservationByIdList(reservationByIdResponse)
        .build();
    List<String> tempBasketBookingAllowances = tempBasket.getBookingAllowances() != null ?
        tempBasket.getBookingAllowances().stream()
            .map(BookingAllowance::getAllowance)
            .toList() : List.of();

    when(basketOutPort.getBasketById(originalBasketRef)).thenReturn(originalBasket);
    when(basketOutPort.getBasketById(tempBasketRef)).thenReturn(tempBasket);
    when(reservationOutPort.getReservationsByIds(eq(HOTEL_CODE_TEST), anyList(), eq(false),
        eq(false), eq(true)))
        .thenReturn(basketByRefResponse);
    when(reservationOutPort.getReservationsByIds(eq(HOTEL_CODE_TEST), anyList(), eq(false),
        eq(false), eq(false)))
        .thenReturn(basketByRefResponse);
    CopyReservationsResponse copyReservationsResponse = mockCopyReservationsResponse();
    when(reservationOutPort.copyReservations(any())).thenReturn(
        copyReservationsResponse);
    when(basketOutPort.addReservationsToBasket(originalBasket.getReference(),
        originalBasket.getETag().replace("\"", ""),
        copyReservationsResponse, false,
        Map.of(copyReservationsResponse.getReservations().get(0).getReservationId(), false)))
        .thenReturn(originalBasket);
    when(reservationOutPort.getBookingAllowances(HOTEL_CODE_TEST, SOURCE_ID_222222, tempBasketBookingAllowances))
          .thenReturn(bookingAllowancesResponse);
    when(rulesOutPort.getBusinessAllowanceRules()).thenReturn(buildBusinessAllowanceRuleResponse());
    when(contentOutPort.getBusinessNotes(LANGUAGE)).thenReturn(buildBusinessNotesResponse());
    when(contentOutPort.getHotelPaymentInformation(tempBasket.getHotelId(), LANGUAGE, COUNTRY))
        .thenReturn(getPaymentResponse());
    when(reservationOutPort.confirmAmend(any())).thenReturn(basketByRefResponse);

    when(reservationOutPort.getReservationsPackagesByIds(any(), any()))
        .thenReturn(getMockedReservationsPackagesResponseTempBasket());

    var mockedFeatureFlag = mock(FeatureFlag.class);
    when(unleashWrapper.featureFlag())
        .thenReturn(mockedFeatureFlag);
    // all unstubbed getters on mockedFeatureFlag return null, so isEnabled(null) covers them all
    when(unleashWrapper.isEnabled((FeatureFlag.Feature) null))
        .thenReturn(false);

    stubCancellationPoliciesFetch();

    var request = new ConfirmAmendRequest(originalBasketRef, tempBasketRef, TOKEN,
        BookingChannel.builder().channel(PI_BOOKING_CHANNEL).subchannel(SUBCHANNEL_WEB).language(
            LANGUAGE_EN).build(),
        false, false, PaymentOption.PAY_ON_ARRIVAL.toString(), "", "");

    // Act
    reservationInPort.confirmAmend(request, true);

    // Assert
    verify(reservationOutPort).movePaymentDetails(eq(tempBasket.getHotelId()), anySet());
    verify(reservationOutPort, never()).updateReservationAlerts(any(UpdateReservationAlertsRequest.class));
  }

  @Test
  void confirmAmend_SuccessAddReservation_setsPibaCardPresentWhenPaymentMethodIsPiba() {
    // Arrange
    String originalBasketRef = ORIGINAL_BASKET_REF;
    String tempBasketRef = TEMPORARY_BASKET_REF;

    var originalBasket = BasketResponse.builder()
        .bookingReference(originalBasketRef)
        .reference(originalBasketRef)
        .hotelId(HOTEL_CODE_TEST)
        .items(List.of(BasketItemResponse.builder().sourceId(SOURCE_ID_111111).build()))
        .eTag(String.valueOf(Instant.now().toEpochMilli()))
        .paymentOption(PAY_ON_ARRIVAL)
        .build();

    var tempBasket = BasketResponse.builder()
        .bookingReference(tempBasketRef)
        .reference(tempBasketRef)
        .status(BASKET_STATUS_OPEN)
        .items(List.of(BasketItemResponse.builder().sourceId(SOURCE_ID_222222).build(),
            BasketItemResponse.builder().sourceId(SOURCE_ID_333333).build()))
        .linkAmendReservations(Map.of(SOURCE_ID_111111, SOURCE_ID_222222))
        .hotelId(HOTEL_CODE_TEST)
        .originalBasketId(originalBasketRef)
        .eTag(String.valueOf(Instant.now().plus(1, ChronoUnit.MINUTES).toEpochMilli()))
        .paymentOption(PAY_ON_ARRIVAL)
        .build();

    var reservationByIdResponse = new ArrayList<ReservationByIdResponse>();
    reservationByIdResponse.add(ManageReservationUtils.mockReservationByIdResponseConfirm(true));
    reservationByIdResponse.add(ManageReservationUtils.mockReservationByIdResponseConfirm(false));
    reservationByIdResponse.get(0).getPaymentCard()
        .setPaymentMethod(HotelReservationConstants.PIBA_UK_CARD_TYPE);
    reservationByIdResponse.get(1).getPaymentCard()
        .setPaymentMethod(HotelReservationConstants.PIBA_UK_CARD_TYPE);

    var basketByRefResponse = ReservationByBasketRefResponse.builder()
        .reservationByIdList(reservationByIdResponse)
        .build();

    when(basketOutPort.getBasketById(originalBasketRef)).thenReturn(originalBasket);
    when(basketOutPort.getBasketById(tempBasketRef)).thenReturn(tempBasket);
    when(reservationOutPort.getReservationsByIds(eq(HOTEL_CODE_TEST), anyList(), eq(false),
        eq(false), eq(true))).thenReturn(basketByRefResponse);
    when(reservationOutPort.getReservationsByIds(eq(HOTEL_CODE_TEST), anyList(), eq(false),
        eq(false), eq(false))).thenReturn(basketByRefResponse);

    CopyReservationsResponse copyReservationsResponse = mockCopyReservationsResponse();
    when(reservationOutPort.copyReservations(any())).thenReturn(copyReservationsResponse);
    when(basketOutPort.addReservationsToBasket(originalBasket.getReference(),
        originalBasket.getETag().replace("\"", ""), copyReservationsResponse, false,
        Map.of(copyReservationsResponse.getReservations().get(0).getReservationId(), false)))
        .thenReturn(originalBasket);
    when(reservationOutPort.getBookingAllowances(HOTEL_CODE_TEST, SOURCE_ID_222222, List.of()))
        .thenReturn(mockCreateBookingAllowancesResponse());
    when(rulesOutPort.getBusinessAllowanceRules()).thenReturn(buildBusinessAllowanceRuleResponse());
    when(contentOutPort.getBusinessNotes(LANGUAGE)).thenReturn(buildBusinessNotesResponse());
    when(contentOutPort.getHotelPaymentInformation(tempBasket.getHotelId(), LANGUAGE, COUNTRY))
        .thenReturn(getPaymentResponse());
    when(reservationOutPort.confirmAmend(any())).thenReturn(basketByRefResponse);
    when(reservationOutPort.getReservationsPackagesByIds(any(), any()))
        .thenReturn(getMockedReservationsPackagesResponseTempBasket());

    var mockedFeatureFlag = mock(FeatureFlag.class);
    when(unleashWrapper.featureFlag()).thenReturn(mockedFeatureFlag);
    when(unleashWrapper.isEnabled(mockedFeatureFlag.getSaveAllowancesInBasket()))
        .thenReturn(false);

    stubCancellationPoliciesFetch();

    var request = new ConfirmAmendRequest(originalBasketRef, tempBasketRef, TOKEN,
        BookingChannel.builder().channel(PI_BOOKING_CHANNEL).subchannel(SUBCHANNEL_WEB)
            .language(LANGUAGE_EN).build(),
        false, false, PaymentOption.PAY_ON_ARRIVAL.toString(), "", "");

    // Act
    reservationInPort.confirmAmend(request, true);

    // Assert
    verify(reservationOutPort).confirmReservation(confirmReservationCaptor.capture());
    var confirmAmendRequest = confirmReservationCaptor.getValue();
    assertTrue(confirmAmendRequest.getPibaCardPresent());
  }

  @ParameterizedTest
  @ValueSource(booleans = {false, true})
  void confirmAmend_SuccessAddReservation_AccountCompany(Boolean useBasketAllowances) {
    // Arrange
    String originalBasketRef = ORIGINAL_BASKET_REF;
    String tempBasketRef = TEMPORARY_BASKET_REF;
    var preAuthCharges = "preAuthCharges";

    var originalBasket = BasketResponse.builder()
        .bookingReference(originalBasketRef)
        .reference(originalBasketRef)
        .hotelId(HOTEL_CODE_TEST)
        .items(List.of(BasketItemResponse.builder().sourceId(SOURCE_ID_111111).build()))
        .eTag(String.valueOf(Instant.now().toEpochMilli()))
        .paymentOption(PaymentOption.ACCOUNT_COMPANY)
        .build();

    var bookingAllowancesResponse = mockCreateBookingAllowancesResponse();

    var tempBasket = BasketResponse.builder()
        .bookingReference(tempBasketRef)
        .reference(tempBasketRef)
        .status(BASKET_STATUS_OPEN)
        .items(List.of(BasketItemResponse.builder().sourceId(SOURCE_ID_222222).build(),
            BasketItemResponse.builder().sourceId(SOURCE_ID_333333).build()))
        .linkAmendReservations(Map.of(SOURCE_ID_111111, SOURCE_ID_222222))
        .hotelId(HOTEL_CODE_TEST)
        .originalBasketId(originalBasketRef)
        .ccuiExtraItems(CcuiExtraItems.builder()
            .accountCompanyItems(AccountCompanyItems.builder().charges(preAuthCharges).build())
            .build())
        .eTag(String.valueOf(Instant.now().plus(1, ChronoUnit.MINUTES).toEpochMilli()))
        .paymentOption(PaymentOption.ACCOUNT_COMPANY)
        .bookingAllowances(useBasketAllowances ? bookingAllowancesResponse.getBookingAllowances()
            : null)
        .build();

    List<String> tempBasketBookingAllowances = tempBasket.getBookingAllowances() != null ?
        tempBasket.getBookingAllowances().stream()
            .map(BookingAllowance::getAllowance)
            .toList() : List.of();

    var reservationByIdResponse = new ArrayList<ReservationByIdResponse>();
    reservationByIdResponse.add(ManageReservationUtils.mockReservationByIdResponseConfirm(true));
    reservationByIdResponse.add(ManageReservationUtils.mockReservationByIdResponseConfirm(false));

    var basketByRefResponse = ReservationByBasketRefResponse.builder()
        .reservationByIdList(reservationByIdResponse)
        .customReferenceNumber("AmendedReference")
        .build();

    when(basketOutPort.getBasketById(originalBasketRef)).thenReturn(originalBasket);
    when(basketOutPort.getBasketById(tempBasketRef)).thenReturn(tempBasket);
    when(reservationOutPort.getReservationsByIds(anyString(), anyList(), eq(false), eq(false), eq(false))).thenReturn(
        basketByRefResponse);
    when(reservationOutPort.getReservationsByIds(anyString(), anyList(), eq(false), eq(false), eq(true))).thenReturn(
        basketByRefResponse);
    var copyReservationsResponse = mockCopyReservationsResponse();
    when(reservationOutPort.copyReservations(any())).thenReturn(
        copyReservationsResponse);
    when(basketOutPort.addReservationsToBasket(originalBasket.getReference(),
        originalBasket.getETag().replace("\"", ""),
        copyReservationsResponse, false,
        Map.of(copyReservationsResponse.getReservations().get(0).getReservationId(), false))).thenReturn(originalBasket);

    when(reservationOutPort.getBookingAllowances(HOTEL_CODE_TEST, SOURCE_ID_222222, tempBasketBookingAllowances))
        .thenReturn(bookingAllowancesResponse);

    when(rulesOutPort.getBusinessAllowanceRules()).thenReturn(buildBusinessAllowanceRuleResponse());
    when(contentOutPort.getBusinessNotes("de")).thenReturn(buildBusinessNotesResponse());
    when(contentOutPort.getHotelPaymentInformation(any(), any(), any())).thenReturn(
        getPaymentResponse());
    when(reservationOutPort.confirmAmend(any())).thenReturn(basketByRefResponse);
    when(reservationOutPort.confirmReservation(any())).thenReturn(new ConfirmReservationResponse());
    when(reservationOutPort.getReservationsPackagesByIds(any(), any()))
        .thenReturn(getMockedReservationsPackagesResponseTempBasket());

    var mockedFeatureFlag = mock(FeatureFlag.class);
    when(unleashWrapper.featureFlag())
        .thenReturn(mockedFeatureFlag);
    when(unleashWrapper.isEnabled(mockedFeatureFlag.getUseBasketAllowances()))
        .thenReturn(useBasketAllowances);
    when(unleashWrapper.isEnabled(mockedFeatureFlag.getApplyOccupancySupplement()))
        .thenReturn(false);

    stubCancellationPoliciesFetch();

    var request = new ConfirmAmendRequest(originalBasketRef, tempBasketRef, TOKEN,
        BookingChannel.builder().channel(CCUI_BOOKING_CHANNEL).subchannel(SUBCHANNEL_WEB).language("DE").build(),
        false, false, PAY_ON_ARRIVAL.toString(), "", "");

    // Act
    var response = reservationInPort.confirmAmend(request, true);

    // Assert
    assertNotNull(response);
    assertEquals(1, response.getReservationByIdList().size());
    assertEquals("2023-10-20", response.getReservationByIdList().get(0)
        .getReservationPackageList().get(0).getStartDate());
    verify(basketOutPort, times(1)).triggerEmailConfirmation(any());
    verify(amendPayNowLogic, never()).saveDepositFolios(any(), any(), any(), any());
    verify(reservationOutPort).updateBusinessItems(any(BusinessItemsRequest.class));
    verify(reservationOutPort).movePaymentDetails(eq(tempBasket.getHotelId()), anySet());
    verify(reservationOutPort).confirmReservation(confirmReservationCaptor.capture());
    var confirmAmendRequest = confirmReservationCaptor.getValue();
    assertNull(confirmAmendRequest.getPaymentCard());
    assertNull(confirmAmendRequest.getPibaCardPresent());
    assertEquals(ACCOUNT_COMPANY, confirmAmendRequest.getPaymentOption());
  }

  private static Stream<Arguments> paymentOptionCompanyIdProvider() {
    return Stream.of(
        Arguments.of(ACCOUNT_COMPANY, "TestCompanyId"),
        Arguments.of(PAY_ON_ARRIVAL, null)
    );
  }

  @ParameterizedTest
  @MethodSource("paymentOptionCompanyIdProvider")
  @DisplayName("updateBusinessItems sets companyId based on payment option")
  void confirmAmend_updateBusinessItems_setsCompanyIdBasedOnPaymentOption(
      PaymentOption paymentOption, String expectedCompanyId) {
    // Arrange
    String originalBasketRef = ORIGINAL_BASKET_REF;
    String tempBasketRef = TEMPORARY_BASKET_REF;

    var originalBasket = BasketResponse.builder()
        .bookingReference(originalBasketRef)
        .reference(originalBasketRef)
        .hotelId(HOTEL_CODE_TEST)
        .items(List.of(BasketItemResponse.builder().sourceId(SOURCE_ID_111111).build()))
        .eTag(String.valueOf(Instant.now().toEpochMilli()))
        .paymentOption(paymentOption)
        .build();

    var bookingAllowancesResponse = mockCreateBookingAllowancesResponse();

    var tempBasket = BasketResponse.builder()
        .bookingReference(tempBasketRef)
        .reference(tempBasketRef)
        .status(BASKET_STATUS_OPEN)
        .items(List.of(BasketItemResponse.builder().sourceId(SOURCE_ID_222222).build(),
            BasketItemResponse.builder().sourceId(SOURCE_ID_333333).build()))
        .linkAmendReservations(Map.of(SOURCE_ID_111111, SOURCE_ID_222222))
        .hotelId(HOTEL_CODE_TEST)
        .originalBasketId(originalBasketRef)
        .eTag(String.valueOf(Instant.now().plus(1, ChronoUnit.MINUTES).toEpochMilli()))
        .paymentOption(paymentOption)
        .build();

    List<String> tempBasketBookingAllowances = tempBasket.getBookingAllowances() != null
        ? tempBasket.getBookingAllowances().stream()
            .map(BookingAllowance::getAllowance)
            .toList()
        : List.of();

    var reservationByIdResponse = new ArrayList<ReservationByIdResponse>();
    reservationByIdResponse.add(ManageReservationUtils.mockReservationByIdResponseConfirm(true));
    reservationByIdResponse.add(ManageReservationUtils.mockReservationByIdResponseConfirm(false));

    var basketByRefResponse = ReservationByBasketRefResponse.builder()
        .reservationByIdList(reservationByIdResponse)
        .companyId("TestCompanyId")
        .customReferenceNumber("TestCustomRef")
        .build();

    when(basketOutPort.getBasketById(originalBasketRef)).thenReturn(originalBasket);
    when(basketOutPort.getBasketById(tempBasketRef)).thenReturn(tempBasket);
    when(reservationOutPort.getReservationsByIds(anyString(), anyList(), eq(false), eq(false),
        eq(false))).thenReturn(basketByRefResponse);
    when(reservationOutPort.getReservationsByIds(anyString(), anyList(), eq(false), eq(false),
        eq(true))).thenReturn(basketByRefResponse);
    var copyReservationsResponse = mockCopyReservationsResponse();
    when(reservationOutPort.copyReservations(any())).thenReturn(copyReservationsResponse);
    when(basketOutPort.addReservationsToBasket(originalBasket.getReference(),
        originalBasket.getETag().replace("\"", ""),
        copyReservationsResponse, false,
        Map.of(copyReservationsResponse.getReservations().get(0).getReservationId(), false)))
        .thenReturn(originalBasket);
    when(reservationOutPort.getBookingAllowances(HOTEL_CODE_TEST, SOURCE_ID_222222,
        tempBasketBookingAllowances)).thenReturn(bookingAllowancesResponse);
    when(rulesOutPort.getBusinessAllowanceRules()).thenReturn(buildBusinessAllowanceRuleResponse());
    when(contentOutPort.getBusinessNotes(LANGUAGE)).thenReturn(buildBusinessNotesResponse());
    when(contentOutPort.getHotelPaymentInformation(any(), any(), any()))
        .thenReturn(getPaymentResponse());
    when(reservationOutPort.confirmAmend(any())).thenReturn(basketByRefResponse);
    when(reservationOutPort.getReservationsPackagesByIds(any(), any()))
        .thenReturn(getMockedReservationsPackagesResponseTempBasket());

    var mockedFeatureFlag = mock(FeatureFlag.class);
    when(unleashWrapper.featureFlag()).thenReturn(mockedFeatureFlag);
    when(unleashWrapper.isEnabled(mockedFeatureFlag.getSaveAllowancesInBasket()))
        .thenReturn(false);

    stubCancellationPoliciesFetch();

    var request = new ConfirmAmendRequest(originalBasketRef, tempBasketRef, TOKEN,
        BookingChannel.builder().channel(PI_BOOKING_CHANNEL).subchannel(SUBCHANNEL_WEB)
            .language(LANGUAGE_EN).build(),
        false, false, PAY_ON_ARRIVAL.toString(), "", "");

    // Act
    reservationInPort.confirmAmend(request, true);

    // Assert
    var businessItemsCaptor = ArgumentCaptor.forClass(BusinessItemsRequest.class);
    verify(reservationOutPort).updateBusinessItems(businessItemsCaptor.capture());
    var capturedRequest = businessItemsCaptor.getValue();
    assertEquals(expectedCompanyId, capturedRequest.getCompanyId());
  }

  @ParameterizedTest
  @ValueSource(booleans = {false, true})
  void confirmAmend_SuccessAddReservation_PayNowDeposits(Boolean saveBasketAllowances) {
    // Arrange
    String originalBasketRef = ORIGINAL_BASKET_REF;
    String tempBasketRef = TEMPORARY_BASKET_REF;

    var originalBasket = BasketResponse.builder()
        .bookingReference(originalBasketRef)
        .reference(originalBasketRef)
        .hotelId(HOTEL_CODE_TEST)
        .items(List.of(BasketItemResponse.builder().sourceId(SOURCE_ID_111111).build()))
        .eTag(String.valueOf(Instant.now().toEpochMilli()))
        .paymentOption(PAY_NOW)
        .build();

    var bookingAllowancesResponse = mockCreateBookingAllowancesResponse();

    var tempBasket = BasketResponse.builder()
        .bookingReference(tempBasketRef)
        .reference(tempBasketRef)
        .status(BASKET_STATUS_OPEN)
        .items(List.of(BasketItemResponse.builder().sourceId(SOURCE_ID_222222).build(),
            BasketItemResponse.builder().sourceId(SOURCE_ID_333333).build()))
        .linkAmendReservations(Map.of(SOURCE_ID_111111, SOURCE_ID_222222))
        .hotelId(HOTEL_CODE_TEST)
        .originalBasketId(originalBasketRef)
        .eTag(String.valueOf(Instant.now().plus(1, ChronoUnit.MINUTES).toEpochMilli()))
        .paymentOption(PAY_NOW)
        .build();

    List<String> tempBasketBookingAllowances = tempBasket.getBookingAllowances() != null ?
        tempBasket.getBookingAllowances().stream()
            .map(BookingAllowance::getAllowance)
            .toList() : List.of();

    var reservationByIdResponse = new ArrayList<ReservationByIdResponse>();
    reservationByIdResponse.add(ManageReservationUtils.mockReservationByIdResponseConfirm(true));
    reservationByIdResponse.add(ManageReservationUtils.mockReservationByIdResponseConfirm(false));

    var basketByRefResponse = ReservationByBasketRefResponse.builder()
        .reservationByIdList(reservationByIdResponse)
        .bookingReference("abcd")
        .build();

    BasketResponse basketRef = BasketResponse.builder()
        .hotelId(HOTEL_CODE_TEST)
        .paymentOption(PAY_NOW)
        .bookingReference("abcd")
        .build();

    when(reservationOutPort
        .getReservationsByIds(anyString(), anyList(), eq(false)))
        .thenReturn(getReservationByBasketRefRes());
    when(reservationOutPort
        .getReservationsByIds(anyString(), anyList(), eq(false), eq(false), eq(true)))
        .thenReturn(basketByRefResponse);
    when(reservationOutPort
        .getReservationsByIds(anyString(), anyList(), eq(false), eq(false), eq(false)))
        .thenReturn(basketByRefResponse);
    when(basketOutPort.getBasketById(originalBasketRef)).thenReturn(originalBasket);
    when(basketOutPort.getBasketById(tempBasketRef)).thenReturn(tempBasket);
    doReturn(Optional.ofNullable(basketRef)).when(basketOutPort).getBasketByReference(anyString());
    when(reservationOutPort.getGeneratedDepositFolios(anyString(), anySet()))
        .thenReturn(getDepositFoliosResponseByGeneratedFolio());
    when(basketOutPort.getCharges(anyString())).thenReturn(mockDepositFolioDB());
    var copyReservationsResponse = mockCopyReservationsResponse();
    when(reservationOutPort.copyReservations(any())).thenReturn(
        copyReservationsResponse);
    when(basketOutPort.addReservationsToBasket(originalBasket.getReference(),
        originalBasket.getETag().replace("\"", ""),
        copyReservationsResponse, false,
        Map.of(copyReservationsResponse.getReservations().get(0).getReservationId(), false))).thenReturn(originalBasket);
    when(reservationOutPort.getBookingAllowances(HOTEL_CODE_TEST, SOURCE_ID_222222, tempBasketBookingAllowances))
          .thenReturn(bookingAllowancesResponse);
    when(rulesOutPort.getBusinessAllowanceRules()).thenReturn(buildBusinessAllowanceRuleResponse());
    when(contentOutPort.getBusinessNotes(LANGUAGE)).thenReturn(buildBusinessNotesResponse());
    when(contentOutPort.getHotelPaymentInformation(tempBasket.getHotelId(), LANGUAGE, COUNTRY))
        .thenReturn(getPaymentResponse());
    when(reservationOutPort.confirmAmend(any())).thenReturn(basketByRefResponse);
    when(reservationOutPort.getReservationsPackagesByIds(any(), any()))
        .thenReturn(getMockedReservationsPackagesResponseTempBasket());
    when(amendPayNowLogic.calculateDfForPayNow(anyList(), any(),
        any(), any(), any(), anyString(), anyString(), any()))
        .thenReturn(DepositFolioComputationResult.builder()
            .markAsPayOnArrival(false)
            .totalRefundAmt(BigDecimal.ZERO)
            .build());
    when(amendPayNowLogic.saveDepositFolios(any(), any(), any(), anyList()))
        .thenReturn(DepositFoliosResponse.builder()
            .depositFolios(List.of(DepositFolio.builder()
                .paymentId("123")
                .reservationId("456")
                .charges(List.of(DepositFolioCharge.builder()
                    .currencyAmount(CurrencyAmount.builder()
                        .amount(BigDecimal.ZERO)
                        .currencyCode("GBP")
                        .build())
                    .build()))
                .build()))
            .build());

    var mockedFeatureFlag = mock(FeatureFlag.class);
    when(unleashWrapper.featureFlag())
        .thenReturn(mockedFeatureFlag);
    when(unleashWrapper.isEnabled(mockedFeatureFlag.getSaveAllowancesInBasket()))
        .thenReturn(saveBasketAllowances);
    when(unleashWrapper.isEnabled(mockedFeatureFlag.getApplyOccupancySupplement()))
        .thenReturn(false);

    stubCancellationPoliciesFetch();

    var request = new ConfirmAmendRequest(originalBasketRef, tempBasketRef, TOKEN,
        BookingChannel.builder().channel(PI_BOOKING_CHANNEL).subchannel(SUBCHANNEL_WEB).language(
            LANGUAGE_EN).build(),
        false, false, PAY_NOW.toString(), "", "");

    // Act
    var response = reservationInPort.confirmAmend(request, true);

    // Assert
    assertNotNull(response);
    assertEquals(1, response.getReservationByIdList().size());
    verify(basketOutPort, times(1)).triggerEmailConfirmation(any());
    verify(reservationOutPort).updateBusinessItems(any(BusinessItemsRequest.class));
  }

  private static DepositFoliosResponse getDepositFoliosResponseByGeneratedFolio() {
    var generatedDepositFolios = DepositFolio.builder()
        .reservationId("tempRes1")
        .hotelId("TESTHOTEL")
        .vatRegion("UK")
        .charges(List.of(
            DepositFolioCharge.builder()
                .transactionCode("9026")
                .quantity(1)
                .reference("2023-12-08")
                .currencyAmount(CurrencyAmount.builder()
                    .amount(BigDecimal.valueOf(9.99))
                    .currencyCode("GBP")
                    .build()).build(),
            DepositFolioCharge.builder()
                .transactionCode("9016")
                .quantity(1)
                .reference("2023-12-08")
                .currencyAmount(CurrencyAmount.builder()
                    .amount(BigDecimal.valueOf(999))
                    .currencyCode("GBP")
                    .build())
                .build()))
        .build();
    DepositFoliosResponse dp = new DepositFoliosResponse();
    dp.setDepositFolios(List.of(generatedDepositFolios));
    return dp;
  }

  private static ReservationByBasketRefResponse getReservationByBasketRefRes() {
    return ReservationByBasketRefResponse.builder()
        .hotelId(HOTEL_CODE_TEST)
        .bookingReference("abcd")
        .balanceOutstanding(BigDecimal.valueOf(50))
        .amountPaid(BigDecimal.valueOf(10))
        .reservationByIdList(List.of(ReservationByIdResponse.builder()
                .reservationId(SOURCE_ID_222222)
                .roomStay(RoomStayByIdResponse.builder()
                        .arrivalDate("2023-01-01")
                        .departureDate("2023-08-08")
                        .build())
                .reservationGuestList(
                        List.of(ReservationByIdGuestsResponse.builder()
                                .givenName("TESTGIVEN")
                                .surName("TESTSUR")
                                .build()))
                .reservationStatus("AMEND")
                .build()))
        .basketReference("abcd")
        .build();
  }

  @ParameterizedTest
  @ValueSource(booleans = {false, true})
  void confirmAmend_nullEmailFlags_SuccessAddReservation(Boolean saveBasketAllowances) {
    // Arrange
    String originalBasketRef = ORIGINAL_BASKET_REF;
    String tempBasketRef = TEMPORARY_BASKET_REF;

    var originalBasket = BasketResponse.builder()
        .bookingReference(originalBasketRef)
        .reference(originalBasketRef)
        .hotelId(HOTEL_CODE_TEST)
        .items(List.of(BasketItemResponse.builder().sourceId(SOURCE_ID_111111).build()))
        .eTag(String.valueOf(Instant.now().toEpochMilli()))
        .paymentOption(PaymentOption.PAY_ON_ARRIVAL)
        .build();

    var bookingAllowancesResponse = mockCreateBookingAllowancesResponse();

    var tempBasket = BasketResponse.builder()
        .bookingReference(tempBasketRef)
        .reference(tempBasketRef)
        .status(BASKET_STATUS_OPEN)
        .items(List.of(BasketItemResponse.builder().sourceId(SOURCE_ID_222222).build(),
            BasketItemResponse.builder().sourceId(SOURCE_ID_333333).build()))
        .linkAmendReservations(Map.of(SOURCE_ID_111111, SOURCE_ID_222222))
        .hotelId(HOTEL_CODE_TEST)
        .originalBasketId(originalBasketRef)
        .eTag(String.valueOf(Instant.now().plus(1, ChronoUnit.MINUTES).toEpochMilli()))
        .paymentOption(PaymentOption.PAY_ON_ARRIVAL)
        .build();

    List<String> tempBasketBookingAllowances = tempBasket.getBookingAllowances() != null ?
        tempBasket.getBookingAllowances().stream()
            .map(BookingAllowance::getAllowance)
            .toList() : List.of();

    var reservationByIdResponse = new ArrayList<ReservationByIdResponse>();
    reservationByIdResponse.add(ManageReservationUtils.mockReservationByIdResponseConfirm(true));
    reservationByIdResponse.add(ManageReservationUtils.mockReservationByIdResponseConfirm(false));

    var basketByRefResponse = ReservationByBasketRefResponse.builder()
        .reservationByIdList(reservationByIdResponse)
        .build();

    when(basketOutPort.getBasketById(originalBasketRef)).thenReturn(originalBasket);
    when(basketOutPort.getBasketById(tempBasketRef)).thenReturn(tempBasket);
    when(reservationOutPort.getReservationsByIds(anyString(), anyList(), eq(false), eq(false), eq(true))).thenReturn(
        basketByRefResponse);
    when(reservationOutPort.getReservationsByIds(anyString(), anyList(), eq(false), eq(false), eq(false))).thenReturn(
        basketByRefResponse);
    var copyReservationsResponse = mockCopyReservationsResponse();
    when(reservationOutPort.copyReservations(any())).thenReturn(
        copyReservationsResponse);
    when(basketOutPort.addReservationsToBasket(originalBasket.getReference(),
        originalBasket.getETag().replace("\"", ""),
        copyReservationsResponse, false,
        Map.of(copyReservationsResponse.getReservations().get(0).getReservationId(), false))).thenReturn(originalBasket);
    when(reservationOutPort.getBookingAllowances(HOTEL_CODE_TEST, SOURCE_ID_222222, tempBasketBookingAllowances))
          .thenReturn(bookingAllowancesResponse);
    when(rulesOutPort.getBusinessAllowanceRules()).thenReturn(buildBusinessAllowanceRuleResponse());
    when(contentOutPort.getBusinessNotes(LANGUAGE)).thenReturn(buildBusinessNotesResponse());
    when(contentOutPort.getHotelPaymentInformation(tempBasket.getHotelId(), LANGUAGE, COUNTRY))
        .thenReturn(getPaymentResponse());
    when(reservationOutPort.confirmAmend(any())).thenReturn(basketByRefResponse);

    when(reservationOutPort.getReservationsPackagesByIds(any(), any()))
        .thenReturn(getMockedReservationsPackagesResponseTempBasket());

    var mockedFeatureFlag = mock(FeatureFlag.class);
    when(unleashWrapper.featureFlag())
        .thenReturn(mockedFeatureFlag);
    when(unleashWrapper.isEnabled(mockedFeatureFlag.getUseBasketAllowances()))
        .thenReturn(saveBasketAllowances);
    when(unleashWrapper.isEnabled(mockedFeatureFlag.getApplyOccupancySupplement()))
        .thenReturn(false);

    stubCancellationPoliciesFetch();

    var request = new ConfirmAmendRequest(originalBasketRef, tempBasketRef, TOKEN,
        BookingChannel.builder().channel(PI_BOOKING_CHANNEL).subchannel(SUBCHANNEL_WEB).language(
            LANGUAGE_EN).build(),
        null, null, PAY_ON_ARRIVAL.toString(), "", "");

    // Act
    var response = reservationInPort.confirmAmend(request, true);

    // Assert
    assertNotNull(response);
    assertEquals(1, response.getReservationByIdList().size());
    verify(basketOutPort, times(1)).triggerEmailConfirmation(any());
    verify(amendPayNowLogic, never()).saveDepositFolios(any(), any(), any(), any());
    verify(reservationOutPort).updateBusinessItems(any(BusinessItemsRequest.class));
    verify(reservationOutPort).movePaymentDetails(eq(tempBasket.getHotelId()), anySet());
  }

  @Test
  void confirmAmend_SuccessAddReservation_payNow() {
    // Arrange
    String originalBasketRef = ORIGINAL_BASKET_REF;
    String tempBasketRef = TEMPORARY_BASKET_REF;

    var originalBasket = BasketResponse.builder()
        .bookingReference(originalBasketRef)
        .reference(originalBasketRef)
        .hotelId(HOTEL_CODE_TEST)
        .items(List.of(BasketItemResponse.builder().sourceId(SOURCE_ID_111111).hasOccupancySup(false).build()))
        .eTag(String.valueOf(Instant.now().toEpochMilli()))
        .paymentOption(PaymentOption.PAY_NOW)
        .build();

    var bookingAllowancesResponse = mockCreateBookingAllowancesResponse();

    var tempBasket = BasketResponse.builder()
        .bookingReference(tempBasketRef)
        .reference(tempBasketRef)
        .status(BASKET_STATUS_OPEN)
        .items(List.of(BasketItemResponse.builder().sourceId(SOURCE_ID_222222).hasOccupancySup(true).build(),
            BasketItemResponse.builder().sourceId(SOURCE_ID_333333).build()))
        .linkAmendReservations(Map.of(SOURCE_ID_111111, SOURCE_ID_222222))
        .hotelId(HOTEL_CODE_TEST)
        .originalBasketId(originalBasketRef)
        .eTag(String.valueOf(Instant.now().plus(1, ChronoUnit.MINUTES).toEpochMilli()))
        .paymentOption(PaymentOption.PAY_NOW)
        .build();

    var reservationByIdResponse = new ArrayList<ReservationByIdResponse>();
    reservationByIdResponse.add(ManageReservationUtils.mockReservationByIdResponseConfirm(true));
    reservationByIdResponse.add(ManageReservationUtils.mockReservationByIdResponseConfirm(false));

    var basketByRefResponse = ReservationByBasketRefResponse.builder()
        .reservationByIdList(reservationByIdResponse)
        .build();
    List<String> tempBasketBookingAllowances = tempBasket.getBookingAllowances() != null ?
        tempBasket.getBookingAllowances().stream()
            .map(BookingAllowance::getAllowance)
            .toList() : List.of();

    when(basketOutPort.getBasketById(originalBasketRef)).thenReturn(originalBasket);
    when(basketOutPort.getBasketById(tempBasketRef)).thenReturn(tempBasket);
    when(reservationOutPort.getReservationsByIds(anyString(), anyList(), anyBoolean(), anyBoolean(), anyBoolean())).thenReturn(
        basketByRefResponse);
    var copyReservationsResponse = mockCopyReservationsResponse();
    when(reservationOutPort.copyReservations(any())).thenReturn(
        copyReservationsResponse);
    when(basketOutPort.addReservationsToBasket(originalBasket.getReference(),
        originalBasket.getETag().replace("\"", ""),
        copyReservationsResponse, false,
        Map.of(copyReservationsResponse.getReservations().get(0).getReservationId(), false)))
        .thenReturn(originalBasket);
    when(reservationOutPort.getBookingAllowances(HOTEL_CODE_TEST, SOURCE_ID_222222, tempBasketBookingAllowances))
          .thenReturn(bookingAllowancesResponse);
    when(rulesOutPort.getBusinessAllowanceRules()).thenReturn(buildBusinessAllowanceRuleResponse());
    when(contentOutPort.getBusinessNotes(LANGUAGE)).thenReturn(buildBusinessNotesResponse());
    when(contentOutPort.getHotelPaymentInformation(tempBasket.getHotelId(), LANGUAGE, COUNTRY))
        .thenReturn(getPaymentResponse());
    when(reservationOutPort.confirmAmend(any())).thenReturn(basketByRefResponse);
    when(reservationOutPort.getReservationsPackagesByIds(any(), any()))
        .thenReturn(getMockedReservationsPackagesResponseTempBasket());
    when(amendPayNowLogic.calculateDfForPayNow(any(), any(), any(), any(), any(), any(), any(), any()))
        .thenReturn(DepositFolioComputationResult.builder()
            .totalRefundAmt(BigDecimal.valueOf(50))
            .markAsPayOnArrival(Boolean.FALSE)
            .build());
    when(amendPayNowLogic.triggerRefund(any(), any(), any(), any())).thenReturn(
        DepositFolioComputationResult.builder()
            .markAsPayOnArrival(Boolean.FALSE)
            .paymentId("2334454")
            .build());

    var mockedFeatureFlag = mock(FeatureFlag.class);
    when(unleashWrapper.featureFlag())
        .thenReturn(mockedFeatureFlag);
    when(unleashWrapper.isEnabled(mockedFeatureFlag.getUseBasketAllowances()))
        .thenReturn(false);

    stubCancellationPoliciesFetch();

    var request = new ConfirmAmendRequest(originalBasketRef, tempBasketRef, TOKEN,
        BookingChannel.builder().channel(PI_BOOKING_CHANNEL).subchannel(SUBCHANNEL_WEB).language(
            LANGUAGE_EN).build(),
        false, false, PAY_ON_ARRIVAL.toString(), "", "");

    // Act
    var response = reservationInPort.confirmAmend(request, true);

    // Assert
    assertNotNull(response);
    assertEquals(1, response.getReservationByIdList().size());
    verify(amendPayNowLogic).saveDepositFolios(any(), any(), any(), any());
    verify(amendPayNowLogic, times(1)).triggerRefund(any(), any(), any(), any());
    verify(reservationOutPort).updateBusinessItems(any(BusinessItemsRequest.class));
    verify(basketOutPort).updateOccupancySupplementFlag(any(), any(), any());
  }

  @ParameterizedTest
  @ValueSource(strings = {"PAY_ON_ARRIVAL", "PAY_NOW"})
  void confirmAmend_ThrowsAmendException_payNow(String argument) {
    // Arrange
    String originalBasketRef = ORIGINAL_BASKET_REF;
    String tempBasketRef = TEMPORARY_BASKET_REF;

    var originalBasket = BasketResponse.builder()
        .bookingReference(originalBasketRef)
        .reference(originalBasketRef)
        .hotelId(HOTEL_CODE_TEST)
        .items(List.of(BasketItemResponse.builder().sourceId(SOURCE_ID_111111).build()))
        .eTag(String.valueOf(Instant.now().toEpochMilli()))
        .paymentOption(PaymentOption.PAY_NOW)
        .build();
    var tempBasket = BasketResponse.builder()
        .bookingReference(tempBasketRef)
        .reference(tempBasketRef)
        .status(BASKET_STATUS_OPEN)
        .items(List.of(BasketItemResponse.builder().sourceId(SOURCE_ID_222222).build(),
            BasketItemResponse.builder().sourceId(SOURCE_ID_333333).build()))
        .linkAmendReservations(Map.of(SOURCE_ID_111111, SOURCE_ID_222222))
        .hotelId(HOTEL_CODE_TEST)
        .originalBasketId(originalBasketRef)
        .eTag(String.valueOf(Instant.now().plus(1, ChronoUnit.MINUTES).toEpochMilli()))
        .paymentOption(PaymentOption.PAY_NOW)
        .build();

    var reservationByIdResponse = new ArrayList<ReservationByIdResponse>();
    reservationByIdResponse.add(ManageReservationUtils.mockReservationByIdResponseConfirm(true));
    reservationByIdResponse.add(ManageReservationUtils.mockReservationByIdResponseConfirm(false));

    var basketByRefResponse = ReservationByBasketRefResponse.builder()
        .reservationByIdList(reservationByIdResponse)
        .build();
    var basketError = new BasketError();
    basketError.setCode(
        "PAY_ON_ARRIVAL".equals(argument) ? AmendErrorCode.AMEND_CONFIRM_EXCEPTION.name()
            : AmendErrorCode.AMEND_REVERT_EXCEPTION.name());
    basketError.setType(
        "PAY_ON_ARRIVAL".equals(argument) ? TypeEnum.AMEND_CONFIRM : TypeEnum.AMEND_REVERT);


    when(basketOutPort.getBasketById(originalBasketRef)).thenReturn(originalBasket);
    when(basketOutPort.getBasketById(tempBasketRef)).thenReturn(tempBasket);
    when(reservationOutPort.getReservationsByIds(anyString(), anyList(), anyBoolean())).thenReturn(
        basketByRefResponse);
    when(reservationOutPort.copyReservations(any())).thenReturn(
        mockCopyReservationsResponse());
    when(basketOutPort.addReservationsToBasket(originalBasket.getReference(),
        originalBasket.getETag().replace("\"", ""),
        mockCopyReservationsResponse(), false, null)).thenReturn(originalBasket);

    var mockedFeatureFlag = mock(FeatureFlag.class);
    when(unleashWrapper.featureFlag())
        .thenReturn(mockedFeatureFlag);
    when(unleashWrapper.isEnabled(mockedFeatureFlag.getApplyOccupancySupplement()))
        .thenReturn(false);

    stubCancellationPoliciesFetch();

    var request = new ConfirmAmendRequest(originalBasketRef, tempBasketRef, TOKEN,
        BookingChannel.builder().channel(PI_BOOKING_CHANNEL).subchannel(SUBCHANNEL_WEB).language(
            LANGUAGE_EN).build(),
        false, false, argument, "", "");

    // Act & Assert
    AmendReservationException amendReservationException = assertThrows(AmendReservationException.class, () -> reservationInPort.confirmAmend(request, true));
    assertEquals("PAY_ON_ARRIVAL".equals(argument) ? "AMEND_CONFIRM_EXCEPTION"
        : "AMEND_REVERT_EXCEPTION", amendReservationException.getGlobalErrTextTemplate());

    verify(basketOutPort).setErroredBooking(eq(originalBasketRef), eq(true), eq(basketError));
    verify(basketOutPort).setErroredBooking(eq(tempBasketRef), eq(true), eq(basketError));
  }

  @ParameterizedTest
  @CsvSource({"PAY_ON_ARRIVAL, false",
      "PAY_NOW, true",
      "RESERVE_WITHOUT_CARD, false",
      "ACCOUNT_COMPANY, false"})
  void confirmAmend_ThrowsAmendException_Validate_isErroredBooking(PaymentOption paymentOption, boolean expectedIsErroredBookingValue) {
    // Arrange
    String originalBasketRef = ORIGINAL_BASKET_REF;
    String tempBasketRef = TEMPORARY_BASKET_REF;

    var originalBasket = BasketResponse.builder()
        .bookingReference(originalBasketRef)
        .reference(originalBasketRef)
        .hotelId(HOTEL_CODE_TEST)
        .items(List.of(BasketItemResponse.builder().sourceId(SOURCE_ID_111111).build()))
        .eTag(String.valueOf(Instant.now().toEpochMilli()))
        .paymentOption(paymentOption)
        .build();
    var tempBasket = BasketResponse.builder()
        .bookingReference(tempBasketRef)
        .reference(tempBasketRef)
        .status(BASKET_STATUS_OPEN)
        .items(List.of(BasketItemResponse.builder().sourceId(SOURCE_ID_222222).build(),
            BasketItemResponse.builder().sourceId(SOURCE_ID_333333).build()))
        .linkAmendReservations(Map.of(SOURCE_ID_111111, SOURCE_ID_222222))
        .hotelId(HOTEL_CODE_TEST)
        .originalBasketId(originalBasketRef)
        .eTag(String.valueOf(Instant.now().plus(1, ChronoUnit.MINUTES).toEpochMilli()))
        .paymentOption(paymentOption)
        .build();

    var reservationByIdResponse = new ArrayList<ReservationByIdResponse>();
    reservationByIdResponse.add(ManageReservationUtils.mockReservationByIdResponseConfirm(true));
    reservationByIdResponse.add(ManageReservationUtils.mockReservationByIdResponseConfirm(false));

    var basketByRefResponse = ReservationByBasketRefResponse.builder()
        .reservationByIdList(reservationByIdResponse)
        .build();
    var basketError = new BasketError();
    basketError.setCode(
        "PAY_NOW".equals(paymentOption.toString()) ? AmendErrorCode.AMEND_REVERT_EXCEPTION.name()
            : AmendErrorCode.AMEND_CONFIRM_EXCEPTION.name());
    basketError.setType(
        "PAY_NOW".equals(paymentOption.toString()) ? TypeEnum.AMEND_REVERT : TypeEnum.AMEND_CONFIRM );

    when(basketOutPort.getBasketById(originalBasketRef)).thenReturn(originalBasket);
    when(basketOutPort.getBasketById(tempBasketRef)).thenReturn(tempBasket);
    when(reservationOutPort.getReservationsByIds(anyString(), anyList(), anyBoolean())).thenReturn(
        basketByRefResponse);
    when(reservationOutPort.copyReservations(any())).thenReturn(
        mockCopyReservationsResponse());
    when(basketOutPort.addReservationsToBasket(originalBasket.getReference(),
        originalBasket.getETag().replace("\"", ""),
        mockCopyReservationsResponse(), false, null)).thenReturn(originalBasket);

    var mockedFeatureFlag = mock(FeatureFlag.class);
    when(unleashWrapper.featureFlag())
        .thenReturn(mockedFeatureFlag);
    when(unleashWrapper.isEnabled(mockedFeatureFlag.getApplyOccupancySupplement()))
        .thenReturn(false);

    stubCancellationPoliciesFetch();

    var request = new ConfirmAmendRequest(originalBasketRef, tempBasketRef, TOKEN,
        BookingChannel.builder().channel(PI_BOOKING_CHANNEL).subchannel(SUBCHANNEL_WEB).language(
            LANGUAGE_EN).build(),
        false, false, paymentOption.toString(), "", "");

    // Act & Assert
    assertThrows(AmendReservationException.class, () -> reservationInPort.confirmAmend(request, true));

    verify(basketOutPort).setErroredBooking(eq(originalBasketRef), eq(expectedIsErroredBookingValue), eq(basketError));
    verify(basketOutPort).setErroredBooking(eq(tempBasketRef), eq(expectedIsErroredBookingValue), eq(basketError));
  }

  @ParameterizedTest
  @CsvSource({"PAY_ON_ARRIVAL, false",
      "PAY_NOW, true",
      "RESERVE_WITHOUT_CARD, false",
      "ACCOUNT_COMPANY, false"})
  void confirmAmend_ThrowsAmendException_DeleteCanceledReservations_Validate_isErroredBooking(PaymentOption paymentOption, boolean expectedIsErroredBookingValue) {
    // Arrange
    String originalBasketRef = ORIGINAL_BASKET_REF;
    String tempBasketRef = TEMPORARY_BASKET_REF;

    var originalBasket = BasketResponse.builder()
        .bookingReference(originalBasketRef)
        .reference(originalBasketRef)
        .hotelId(HOTEL_CODE_TEST)
        .items(List.of(BasketItemResponse.builder().sourceId(SOURCE_ID_111111).build()))
        .eTag(String.valueOf(Instant.now().toEpochMilli()))
        .paymentOption(paymentOption)
        .build();

    var bookingAllowancesResponse = mockCreateBookingAllowancesResponse();

    var tempBasket = BasketResponse.builder()
        .bookingReference(tempBasketRef)
        .reference(tempBasketRef)
        .status(BASKET_STATUS_OPEN)
        .items(List.of(BasketItemResponse.builder().sourceId(SOURCE_ID_222222).build(),
            BasketItemResponse.builder().sourceId(SOURCE_ID_333333).build()))
        .linkAmendReservations(Map.of(SOURCE_ID_111111, SOURCE_ID_222222))
        .hotelId(HOTEL_CODE_TEST)
        .originalBasketId(originalBasketRef)
        .eTag(String.valueOf(Instant.now().plus(1, ChronoUnit.MINUTES).toEpochMilli()))
        .paymentOption(paymentOption)
        .build();

    List<String> tempBasketBookingAllowances = tempBasket.getBookingAllowances() != null ?
        tempBasket.getBookingAllowances().stream()
            .map(BookingAllowance::getAllowance)
            .toList() : List.of();

    var reservationByIdResponse = new ArrayList<ReservationByIdResponse>();
    reservationByIdResponse.add(ManageReservationUtils.mockReservationByIdResponseConfirm(true));
    reservationByIdResponse.add(ManageReservationUtils.mockReservationByIdResponseConfirm(false));

    var basketByRefResponse = ReservationByBasketRefResponse.builder()
        .reservationByIdList(reservationByIdResponse)
        .build();

    var basketError = new BasketError();
    basketError.setCode(AmendErrorCode.AMEND_CONFIRM_EXCEPTION.name());

    when(reservationOutPort.getReservationsByIds(anyString(), anyList(), anyBoolean(), anyBoolean(),
        anyBoolean())).thenReturn(basketByRefResponse);

    if("PAY_NOW".equals(paymentOption.toString())){
      when(reservationOutPort.getReservationsByIds(anyString(), anyList(), anyBoolean()))
          .thenReturn(mockReservationByBasRes());
      when(basketOutPort.getBasketByReference(anyString()))
          .thenReturn(Optional.ofNullable(mockBasketResPayOnArrival()));
      when(amendPayNowLogic.calculateDfForPayNow(any(), any(), any(), any(), any(), any(), any(), any()))
          .thenReturn(DepositFolioComputationResult.builder()
              .totalRefundAmt(BigDecimal.valueOf(50))
              .markAsPayOnArrival(Boolean.FALSE)
              .build());
    }

    when(reservationOutPort
        .getReservationsByIds(anyString(), anyList(), anyBoolean(), anyBoolean(), anyBoolean()))
        .thenReturn(basketByRefResponse);
    when(basketOutPort.getBasketById(originalBasketRef)).thenReturn(originalBasket);
    when(basketOutPort.getBasketById(tempBasketRef)).thenReturn(tempBasket);

    when(reservationOutPort.copyReservations(any())).thenReturn(
        mockCopyReservationsResponse());

    var copyReservationsResponse = mockCopyReservationsResponse();
    when(reservationOutPort.copyReservations(any())).thenReturn(
        copyReservationsResponse);
    when(basketOutPort.addReservationsToBasket(originalBasket.getReference(),
        originalBasket.getETag().replace("\"", ""),
        copyReservationsResponse, false,
        Map.of(copyReservationsResponse.getReservations().get(0).getReservationId(), false)))
        .thenReturn(originalBasket);

    when(reservationOutPort.getBookingAllowances(HOTEL_CODE_TEST, SOURCE_ID_222222, tempBasketBookingAllowances))
        .thenReturn(bookingAllowancesResponse);
    when(rulesOutPort.getBusinessAllowanceRules()).thenReturn(buildBusinessAllowanceRuleResponse());
    when(contentOutPort.getBusinessNotes(LANGUAGE)).thenReturn(buildBusinessNotesResponse());
    when(contentOutPort.getHotelPaymentInformation(tempBasket.getHotelId(), LANGUAGE, COUNTRY))
        .thenReturn(getPaymentResponse());
    when(reservationOutPort.confirmAmend(any())).thenReturn(basketByRefResponse);
    when(reservationOutPort.getReservationsPackagesByIds(any(), any()))
        .thenReturn(getMockedReservationsPackagesResponseTempBasket());

    var mockedFeatureFlag = mock(FeatureFlag.class);
    when(unleashWrapper.featureFlag())
        .thenReturn(mockedFeatureFlag);
    when(unleashWrapper.isEnabled(mockedFeatureFlag.getUseBasketAllowances()))
        .thenReturn(false);

    stubCancellationPoliciesFetch();

    doThrow(AmendReservationException.class).when(reservationCleanup).cleanupOriginalBasket(originalBasket, List.of());

    var request = new ConfirmAmendRequest(originalBasketRef, tempBasketRef, TOKEN,
        BookingChannel.builder().channel(PI_BOOKING_CHANNEL).subchannel(SUBCHANNEL_WEB).language(
            LANGUAGE_EN).build(),
        false, false, paymentOption.toString(), "", "");

    // Act & Assert
    var exception = assertThrows(AmendReservationException.class, () -> reservationInPort.confirmAmend(request, true));

    verify(basketOutPort).setErroredBooking(eq(originalBasketRef), eq(expectedIsErroredBookingValue), eq(basketError));
    assertEquals("Could not delete reservations which were cancelled from the original basket", exception.getDebugMessage());
  }

  @ParameterizedTest
  @CsvSource({"PAY_ON_ARRIVAL, false",
      "PAY_ON_ARRIVAL, true",
      "PAY_NOW, false",
      "PAY_NOW, true"})
  void confirmAmend_ThrowsAmendExceptionDF_payNow(String argument, Boolean saveBasketAllowances) {
    // Arrange
    String originalBasketRef = ORIGINAL_BASKET_REF;
    String tempBasketRef = TEMPORARY_BASKET_REF;

    var originalBasket = BasketResponse.builder()
        .bookingReference(originalBasketRef)
        .reference(originalBasketRef)
        .hotelId(HOTEL_CODE_TEST)
        .items(List.of(BasketItemResponse.builder().sourceId(SOURCE_ID_111111).build()))
        .eTag(String.valueOf(Instant.now().toEpochMilli()))
        .paymentOption(PaymentOption.PAY_NOW)
        .build();
    var tempBasket = BasketResponse.builder()
        .bookingReference(tempBasketRef)
        .reference(tempBasketRef)
        .status(BASKET_STATUS_OPEN)
        .items(List.of(BasketItemResponse.builder().sourceId(SOURCE_ID_222222).build(),
            BasketItemResponse.builder().sourceId(SOURCE_ID_333333).build()))
        .linkAmendReservations(Map.of(SOURCE_ID_111111, SOURCE_ID_222222))
        .hotelId(HOTEL_CODE_TEST)
        .originalBasketId(originalBasketRef)
        .eTag(String.valueOf(Instant.now().plus(1, ChronoUnit.MINUTES).toEpochMilli()))
        .paymentOption(PaymentOption.PAY_NOW)
        .build();
    List<String> tempBasketBookingAllowances = tempBasket.getBookingAllowances() != null ?
        tempBasket.getBookingAllowances().stream()
            .map(BookingAllowance::getAllowance)
            .toList() : List.of();


    var reservationByIdResponse = new ArrayList<ReservationByIdResponse>();
    reservationByIdResponse.add(ManageReservationUtils.mockReservationByIdResponseConfirm(true));
    reservationByIdResponse.add(ManageReservationUtils.mockReservationByIdResponseConfirm(false));

    var basketByRefResponse = ReservationByBasketRefResponse.builder()
        .reservationByIdList(reservationByIdResponse)
        .build();
    var bookingAllowancesResponse = mockCreateBookingAllowancesResponse();
    var basketError = new BasketError();
    basketError.setCode(
        "PAY_ON_ARRIVAL".equals(argument) ? AmendErrorCode.AMEND_DEPOSIT_FOLIOS_EXCEPTION.name()
            : AmendErrorCode.AMEND_REVERT_EXCEPTION.name());
    basketError.setType(
        "PAY_ON_ARRIVAL".equals(argument) ? TypeEnum.AMEND_DEPOSIT_FOLIOS : TypeEnum.AMEND_REVERT);

    when(basketOutPort.getBasketById(originalBasketRef)).thenReturn(originalBasket);
    when(basketOutPort.getBasketById(tempBasketRef)).thenReturn(tempBasket);
    when(reservationOutPort.getReservationsByIds(anyString(), anyList(), eq(false), eq(false), eq(true)))
        .thenReturn(basketByRefResponse)
        .thenReturn(mockReservationByBasRes());
    when(reservationOutPort.getReservationsByIds(anyString(), anyList(), eq(false), eq(false), eq(false))).thenReturn(
        basketByRefResponse);
    var copyReservationsResponse = mockCopyReservationsResponse();
    when(reservationOutPort.copyReservations(any())).thenReturn(
        copyReservationsResponse);
    when(basketOutPort.addReservationsToBasket(originalBasket.getReference(),
        originalBasket.getETag().replace("\"", ""),
        copyReservationsResponse, false,
        Map.of(copyReservationsResponse.getReservations().get(0).getReservationId(), false))).thenReturn(originalBasket);
    when(reservationOutPort.getBookingAllowances(HOTEL_CODE_TEST, SOURCE_ID_222222, tempBasketBookingAllowances))
        .thenReturn(bookingAllowancesResponse);
    when(rulesOutPort.getBusinessAllowanceRules()).thenReturn(buildBusinessAllowanceRuleResponse());
    when(contentOutPort.getBusinessNotes(LANGUAGE)).thenReturn(buildBusinessNotesResponse());
    when(contentOutPort.getHotelPaymentInformation(tempBasket.getHotelId(), LANGUAGE, COUNTRY))
        .thenReturn(getPaymentResponse());
    when(reservationOutPort.confirmAmend(any())).thenReturn(basketByRefResponse);

    when(reservationOutPort.getReservationsPackagesByIds(any(), any()))
        .thenReturn(getMockedReservationsPackagesResponseTempBasket());

    when(amendPayNowLogic.calculateDfForPayNow(any(), any(), any(), any(), any(), any(), any(), any()))
        .thenReturn(DepositFolioComputationResult.builder()
            .totalRefundAmt(BigDecimal.valueOf(50))
            .markAsPayOnArrival(Boolean.FALSE)
            .build());
    when(amendPayNowLogic.saveDepositFolios(any(), any(), any(), any())).thenThrow(new RuntimeException());

    if("PAY_NOW".equals(argument)){
      when(basketOutPort.getBasketByReference(anyString()))
          .thenReturn(Optional.ofNullable(mockBasketResPayOnArrival()));
    }

    var mockedFeatureFlag = mock(FeatureFlag.class);
    when(unleashWrapper.featureFlag())
        .thenReturn(mockedFeatureFlag);
    when(unleashWrapper.isEnabled(mockedFeatureFlag.getUseBasketAllowances()))
        .thenReturn(saveBasketAllowances);
    when(unleashWrapper.isEnabled(mockedFeatureFlag.getApplyOccupancySupplement()))
        .thenReturn(false);

    stubCancellationPoliciesFetch();

    var request = new ConfirmAmendRequest(originalBasketRef, tempBasketRef, TOKEN,
        BookingChannel.builder().channel(PI_BOOKING_CHANNEL).subchannel(SUBCHANNEL_WEB).language(
            LANGUAGE_EN).build(),
        false, false, argument, "", "");

    // Act & Assert
    AmendReservationException amendReservationException = assertThrows(
        AmendReservationException.class, () -> reservationInPort.confirmAmend(request, true));

    assertEquals("PAY_ON_ARRIVAL".equals(argument) ? "AMEND_DEPOSIT_FOLIOS_EXCEPTION"
        : "AMEND_REVERT_EXCEPTION", amendReservationException.getGlobalErrTextTemplate());

    verify(basketOutPort).setErroredBooking(eq(originalBasketRef), eq(true), eq(basketError));
    verify(basketOutPort).setErroredBooking(eq(tempBasketRef), eq(true), eq(basketError));
  }

  private CopyReservationsResponse mockCopyReservationsResponse() {
    return CopyReservationsResponse.builder()
        .reservations(List.of(new CopyReservationResponse(SOURCE_ID_444444, "testDate")))
        .linkBetweenReservations(Collections.singletonMap("res1", SOURCE_ID_444444))
        .build();
  }

  private PackagesResponse mockPackagesResponse() {
    return PackagesResponse.builder()
        .packages(new Packages(List.of(new Meal("id", "3"))))
        .build();
  }

  @ParameterizedTest
  @ValueSource(booleans = {false, true})
  void confirmAmend_SuccessDeleteReservation(Boolean saveBasketAllowances) {
    // Arrange
    String originalBasketRef = ORIGINAL_BASKET_REF;
    String tempBasketRef = TEMPORARY_BASKET_REF;

    var originalBasket = BasketResponse.builder()
        .bookingReference(originalBasketRef)
        .reference(originalBasketRef)
        .hotelId(HOTEL_CODE_TEST)
        .items(List.of(BasketItemResponse.builder().sourceId(SOURCE_ID_111111).build(),
            BasketItemResponse.builder().sourceId(SOURCE_ID_333333).build()))
        .eTag(String.valueOf(Instant.now().toEpochMilli()))
        .paymentOption(PaymentOption.PAY_ON_ARRIVAL)
        .build();

    var bookingAllowancesResponse = mockCreateBookingAllowancesResponse();

    var tempBasket = BasketResponse.builder()
        .bookingReference(tempBasketRef)
        .reference(tempBasketRef)
        .status(BASKET_STATUS_OPEN)
        .items(List.of(BasketItemResponse.builder().sourceId(SOURCE_ID_222222).build()))
        .linkAmendReservations(Map.of(SOURCE_ID_111111, SOURCE_ID_222222))
        .hotelId(HOTEL_CODE_TEST)
        .originalBasketId(originalBasketRef)
        .paymentOption(PaymentOption.PAY_ON_ARRIVAL)
        .eTag(String.valueOf(Instant.now().plus(1, ChronoUnit.MINUTES).toEpochMilli()))
        .build();

    List<String> tempBasketBookingAllowances = tempBasket.getBookingAllowances() != null ?
        tempBasket.getBookingAllowances().stream()
            .map(BookingAllowance::getAllowance)
            .toList() : List.of();

    var reservationByIdResponse = new ArrayList<ReservationByIdResponse>();
    reservationByIdResponse.add(ManageReservationUtils.mockReservationByIdResponseConfirm(true));
    reservationByIdResponse.add(ManageReservationUtils.mockReservationByIdResponseConfirm(false));

    var basketByRefResponse = ReservationByBasketRefResponse.builder()
        .reservationByIdList(reservationByIdResponse)
        .build();
    var mockedFeatureFlag = mock(FeatureFlag.class);

    when(unleashWrapper.featureFlag())
            .thenReturn(mockedFeatureFlag);
    when(basketOutPort.getBasketById(originalBasketRef)).thenReturn(originalBasket);
    when(basketOutPort.getBasketById(tempBasketRef)).thenReturn(tempBasket);
    when(reservationOutPort.getReservationsByIds(anyString(), anyList(), eq(false), eq(false), eq(false))).thenReturn(
        basketByRefResponse);
    when(reservationOutPort.getReservationsByIds(anyString(), anyList(), eq(false), eq(false), eq(true))).thenReturn(
        basketByRefResponse);
    when(reservationOutPort.confirmAmend(any())).thenReturn(basketByRefResponse);
    when(reservationOutPort.getReservationsPackagesByIds(any(), anyList()))
        .thenReturn(mockReservationPackagesResponse());
    when(reservationOutPort.getBookingAllowances(HOTEL_CODE_TEST, SOURCE_ID_222222, tempBasketBookingAllowances))
          .thenReturn(bookingAllowancesResponse);
    when(rulesOutPort.getBusinessAllowanceRules()).thenReturn(buildBusinessAllowanceRuleResponse());
    when(contentOutPort.getBusinessNotes(LANGUAGE)).thenReturn(buildBusinessNotesResponse());
    when(contentOutPort.getHotelPaymentInformation(tempBasket.getHotelId(), LANGUAGE, COUNTRY))
        .thenReturn(getPaymentResponse());

    when(unleashWrapper.featureFlag())
        .thenReturn(mockedFeatureFlag);
    when(unleashWrapper.isEnabled(mockedFeatureFlag.getSaveAllowancesInBasket()))
        .thenReturn(saveBasketAllowances);
    when(unleashWrapper.isEnabled(mockedFeatureFlag.getEnableAbsoluteDeadline()))
        .thenReturn(true);
    var mockCancelInformationResponse = CancelInformationResponse
        .builder().isCancellable(false).build();
    when(reservationOutPort.getCancelInformation(anyString(), any(),
        anyString())).thenReturn(mockCancelInformationResponse);

    stubCancellationPoliciesFetch();

    var request = new ConfirmAmendRequest(originalBasketRef, tempBasketRef, TOKEN,
        BookingChannel.builder().channel(PI_BOOKING_CHANNEL).subchannel(SUBCHANNEL_WEB).language(
            LANGUAGE_EN).build(),
        false, false, PAY_ON_ARRIVAL.toString(), "", "");

    // Act
    var response = reservationInPort.confirmAmend(request, true);

    // Assert
    assertNotNull(response);
    assertEquals(1, response.getReservationByIdList().size());
    verify(reservationOutPort).updateBusinessItems(any(BusinessItemsRequest.class));
  }

  @Test
  void confirmAmend_SuccessDeleteReservation_PN_POA() {
    // Arrange
    String originalBasketRef = ORIGINAL_BASKET_REF;
    String tempBasketRef = TEMPORARY_BASKET_REF;

    var originalBasket = BasketResponse.builder()
        .bookingReference(originalBasketRef)
        .reference(originalBasketRef)
        .hotelId(HOTEL_CODE_TEST)
        .items(List.of(BasketItemResponse.builder().sourceId(SOURCE_ID_111111).build(),
            BasketItemResponse.builder().sourceId(SOURCE_ID_333333).build()))
        .eTag(String.valueOf(Instant.now().toEpochMilli()))
        .paymentOption(PAY_NOW)
        .build();
    var tempBasket = BasketResponse.builder()
        .bookingReference(tempBasketRef)
        .reference(tempBasketRef)
        .status(BASKET_STATUS_OPEN)
        .items(List.of(BasketItemResponse.builder().sourceId(SOURCE_ID_222222).build()))
        .linkAmendReservations(Map.of(SOURCE_ID_111111, SOURCE_ID_222222))
        .hotelId(HOTEL_CODE_TEST)
        .originalBasketId(originalBasketRef)
        .paymentOption(PAY_ON_ARRIVAL)
        .eTag(String.valueOf(Instant.now().plus(1, ChronoUnit.MINUTES).toEpochMilli()))
        .build();
    var bookingAllowancesResponse = mockCreateBookingAllowancesResponse();

    var reservationByIdResponse = new ArrayList<ReservationByIdResponse>();
    reservationByIdResponse.add(ManageReservationUtils.mockReservationByIdResponseConfirm(true));
    reservationByIdResponse.add(ManageReservationUtils.mockReservationByIdResponseConfirm(false));

    var basketByRefResponse = ReservationByBasketRefResponse.builder()
        .reservationByIdList(reservationByIdResponse)
        .build();
    var mockedFeatureFlag = mock(FeatureFlag.class);
    List<String> tempBasketBookingAllowances = tempBasket.getBookingAllowances() != null ?
        tempBasket.getBookingAllowances().stream()
            .map(BookingAllowance::getAllowance)
            .toList() : List.of();

    when(basketOutPort.getBasketById(originalBasketRef)).thenReturn(originalBasket);
    when(basketOutPort.getBasketById(tempBasketRef)).thenReturn(tempBasket);
    when(reservationOutPort.getReservationsByIds(anyString(), anyList(), anyBoolean(), anyBoolean(), anyBoolean()))
        .thenReturn(basketByRefResponse);
    when(amendPayNowLogic.calculateDfForPayNow(anyList(), any(),
        any(), any(), any(), anyString(), anyString(), any()))
        .thenReturn(DepositFolioComputationResult.builder()
            .markAsPayOnArrival(false)
            .totalRefundAmt(BigDecimal.ZERO)
            .build());
    when(reservationOutPort.confirmAmend(any())).thenReturn(basketByRefResponse);
    when(reservationOutPort.getReservationsPackagesByIds(any(), anyList()))
        .thenReturn(mockReservationPackagesResponse());
    when(basketOutPort.getCharges(any())).thenReturn(ManageReservationUtils.mockDepositFoliosResponse());
    when(reservationOutPort.getBookingAllowances(HOTEL_CODE_TEST, SOURCE_ID_222222, tempBasketBookingAllowances))
          .thenReturn(bookingAllowancesResponse);
    when(rulesOutPort.getBusinessAllowanceRules()).thenReturn(buildBusinessAllowanceRuleResponse());
    when(contentOutPort.getBusinessNotes(LANGUAGE)).thenReturn(buildBusinessNotesResponse());
    when(contentOutPort.getHotelPaymentInformation(tempBasket.getHotelId(), LANGUAGE, COUNTRY))
        .thenReturn(getPaymentResponse());
    when(unleashWrapper.featureFlag())
        .thenReturn(mockedFeatureFlag);
    when(unleashWrapper.isEnabled(mockedFeatureFlag.getSaveAllowancesInBasket()))
        .thenReturn(false);
    when(unleashWrapper.isEnabled(mockedFeatureFlag.getEnableAbsoluteDeadline()))
        .thenReturn(true);
    var mockCancelInformationResponse = CancelInformationResponse
        .builder().isCancellable(false).build();
    when(reservationOutPort.getCancelInformation(anyString(), any(),
        anyString())).thenReturn(mockCancelInformationResponse);

    stubCancellationPoliciesFetch();

    var request = new ConfirmAmendRequest(originalBasketRef, tempBasketRef, TOKEN,
        BookingChannel.builder().channel(PI_BOOKING_CHANNEL).subchannel(SUBCHANNEL_WEB).language(
            LANGUAGE_EN).build(),
        false, false, PAY_ON_ARRIVAL.toString(), "", "");

    // Act
    var response = reservationInPort.confirmAmend(request, true);

    // Assert
    assertNotNull(response);
    assertEquals(1, response.getReservationByIdList().size());
    verify(reservationOutPort).updateBusinessItems(any(BusinessItemsRequest.class));
  }

  @Test
  void confirmAmend_ThrowsExceptionBasketExpired() {
    // Arrange
    String originalBasketRef = ORIGINAL_BASKET_REF;
    String tempBasketRef = TEMPORARY_BASKET_REF;

    var tempBasket = BasketResponse.builder()
        .bookingReference(tempBasketRef)
        .status(BASKET_STATUS_OPEN)
        .items(List.of(BasketItemResponse.builder().sourceId(SOURCE_ID_222222).build(),
            BasketItemResponse.builder().sourceId(SOURCE_ID_333333).build()))
        .linkAmendReservations(Map.of(SOURCE_ID_222222, SOURCE_ID_111111))
        .hotelId(HOTEL_CODE_TEST)
        .originalBasketId(originalBasketRef)
        .eTag(String.valueOf(Instant.now().minus(2, ChronoUnit.HOURS).toEpochMilli()))
        .build();

    when(basketOutPort.getBasketById(tempBasketRef)).thenReturn(tempBasket);

    var request = new ConfirmAmendRequest(originalBasketRef, tempBasketRef, TOKEN,
        BookingChannel.builder().channel(PI_BOOKING_CHANNEL).subchannel(SUBCHANNEL_WEB).language(
            LANGUAGE_EN).build(),
        false, false, PAY_ON_ARRIVAL.toString(), "", "");

    // Act & Assert
    assertThrows(GenericReservationException.class, () -> reservationInPort.confirmAmend(request, true));
  }

  @Test
  void confirmAmend_ThrowsBasketNotFoundException() {
    // Arrange
    String originalBasketRef = ORIGINAL_BASKET_REF;
    String tempBasketRef = TEMPORARY_BASKET_REF;

    when(basketOutPort.getBasketById(tempBasketRef)).thenThrow(BasketNotFoundException.class);

    var request = new ConfirmAmendRequest(originalBasketRef, tempBasketRef, TOKEN,
        BookingChannel.builder().channel(PI_BOOKING_CHANNEL).subchannel(SUBCHANNEL_WEB).language(
            LANGUAGE_EN).build(),
        false, false, PAY_ON_ARRIVAL.toString(), "", "");

    // Act & Assert
    assertThrows(BasketNotFoundException.class, () -> reservationInPort.confirmAmend(request, true));
  }

  @Test
  void confirmAmend_ThrowsBasketNotFoundException2() {
    // Arrange
    String originalBasketRef = ORIGINAL_BASKET_REF;
    String tempBasketRef = TEMPORARY_BASKET_REF;

    var tempBasket = BasketResponse.builder()
        .bookingReference(tempBasketRef)
        .status(BASKET_STATUS_OPEN)
        .items(List.of(BasketItemResponse.builder().sourceId(SOURCE_ID_222222).build(),
            BasketItemResponse.builder().sourceId(SOURCE_ID_333333).build()))
        .linkAmendReservations(Map.of(SOURCE_ID_222222, SOURCE_ID_111111))
        .hotelId(HOTEL_CODE_TEST)
        .originalBasketId(originalBasketRef)
        .eTag(String.valueOf(Instant.now().plus(1, ChronoUnit.MINUTES).toEpochMilli()))
        .build();

    when(basketOutPort.getBasketById(tempBasketRef)).thenReturn(tempBasket);
    when(basketOutPort.getBasketById(originalBasketRef)).thenThrow(BasketNotFoundException.class);

    var request = new ConfirmAmendRequest(originalBasketRef, tempBasketRef, TOKEN,
        BookingChannel.builder().channel(PI_BOOKING_CHANNEL).subchannel(SUBCHANNEL_WEB).language(
            LANGUAGE_EN).build(),
        false, false, PAY_ON_ARRIVAL.toString(), "", "");

    // Act & Assert
    assertThrows(BasketNotFoundException.class, () -> reservationInPort.confirmAmend(request, true));
  }

  @Test
  void confirmAmend_updateCcAgentId_success() {

    // Arrange
    String originalBasketRef = ORIGINAL_BASKET_REF;
    String tempBasketRef = TEMPORARY_BASKET_REF;

    var originalBasket = BasketResponse.builder()
        .bookingReference(originalBasketRef)
        .reference(originalBasketRef)
        .hotelId(HOTEL_CODE_TEST)
        .items(List.of(BasketItemResponse.builder().sourceId(SOURCE_ID_111111).build()))
        .eTag(String.valueOf(Instant.now().toEpochMilli()))
        .paymentOption(PAY_ON_ARRIVAL)
        .build();

    var bookingAllowancesResponse = mockCreateBookingAllowancesResponse();

    var tempBasket = BasketResponse.builder()
        .bookingReference(tempBasketRef)
        .reference(tempBasketRef)
        .status(BASKET_STATUS_OPEN)
        .items(List.of(BasketItemResponse.builder().sourceId(SOURCE_ID_222222).build(),
            BasketItemResponse.builder().sourceId(SOURCE_ID_333333).build()))
        .linkAmendReservations(Map.of(SOURCE_ID_111111, SOURCE_ID_222222))
        .hotelId(HOTEL_CODE_TEST)
        .originalBasketId(originalBasketRef)
        .eTag(String.valueOf(Instant.now().plus(1, ChronoUnit.MINUTES).toEpochMilli()))
        .paymentOption(PaymentOption.PAY_ON_ARRIVAL)
        .build();

    List<String> tempBasketBookingAllowances = tempBasket.getBookingAllowances() != null ?
        tempBasket.getBookingAllowances().stream()
            .map(BookingAllowance::getAllowance)
            .toList() : List.of();

    var reservationByIdResponse = new ArrayList<ReservationByIdResponse>();
    reservationByIdResponse.add(ManageReservationUtils.mockReservationByIdResponseConfirm(true));
    reservationByIdResponse.add(ManageReservationUtils.mockReservationByIdResponseConfirm(false));

    var basketByRefResponse = ReservationByBasketRefResponse.builder()
        .reservationByIdList(reservationByIdResponse)
        .build();

    when(basketOutPort.getBasketById(originalBasketRef)).thenReturn(originalBasket);
    when(basketOutPort.getBasketById(tempBasketRef)).thenReturn(tempBasket);
    when(reservationOutPort.getReservationsByIds(anyString(), anyList(), eq(false), eq(false), eq(true))).thenReturn(
        basketByRefResponse);
    when(reservationOutPort
        .getReservationsByIds(anyString(), anyList(), eq(false), eq(false), eq(false)))
        .thenReturn(basketByRefResponse);
    CopyReservationsResponse copyReservationsResponse = mockCopyReservationsResponse();
    when(reservationOutPort.copyReservations(any())).thenReturn(
        copyReservationsResponse);
    when(basketOutPort.addReservationsToBasket(originalBasket.getReference(),
        originalBasket.getETag().replace("\"", ""),
        copyReservationsResponse, false,
        Map.of(copyReservationsResponse.getReservations().get(0).getReservationId(), false)))
        .thenReturn(originalBasket);
    when(reservationOutPort.getBookingAllowances(HOTEL_CODE_TEST, SOURCE_ID_222222, tempBasketBookingAllowances))
        .thenReturn(bookingAllowancesResponse);
    when(rulesOutPort.getBusinessAllowanceRules()).thenReturn(buildBusinessAllowanceRuleResponse());
    when(contentOutPort.getBusinessNotes(LANGUAGE)).thenReturn(buildBusinessNotesResponse());
    when(contentOutPort.getHotelPaymentInformation(tempBasket.getHotelId(), LANGUAGE, COUNTRY))
        .thenReturn(getPaymentResponse());
    when(reservationOutPort.confirmAmend(any())).thenReturn(basketByRefResponse);

    when(reservationOutPort.getReservationsPackagesByIds(any(), any()))
        .thenReturn(getMockedReservationsPackagesResponseTempBasket());

    var mockedFeatureFlag = mock(FeatureFlag.class);

    var saveAllowancesInBasketFeature = mock(Feature.class);
    when(mockedFeatureFlag.getSaveAllowancesInBasket()).thenReturn(saveAllowancesInBasketFeature);

    var ccuiAgentIdLogFeature = mock(Feature.class);
    when(mockedFeatureFlag.getCcuiAgentIdLog()).thenReturn(ccuiAgentIdLogFeature);

    var applyOccupancySupplementFeature = mock(Feature.class);
    when(mockedFeatureFlag.getApplyOccupancySupplement()).thenReturn(applyOccupancySupplementFeature);

    when(unleashWrapper.featureFlag()).thenReturn(mockedFeatureFlag);
    when(unleashWrapper.isEnabled(saveAllowancesInBasketFeature)).thenReturn(false);
    when(unleashWrapper.isEnabled(ccuiAgentIdLogFeature)).thenReturn(true);
    when(unleashWrapper.isEnabled(applyOccupancySupplementFeature)).thenReturn(false);
    when(unleashWrapper.isEnabled(mockedFeatureFlag.getEnableAbsoluteDeadline()))
        .thenReturn(true);
    var mockCancelInformationResponse = CancelInformationResponse
        .builder().isCancellable(false).build();
    when(reservationOutPort.getCancelInformation(anyString(), any(),
        anyString())).thenReturn(mockCancelInformationResponse);

    stubCancellationPoliciesFetch();

    var request = new ConfirmAmendRequest(originalBasketRef, tempBasketRef, TOKEN,
        BookingChannel.builder().channel(CCUI_BOOKING_CHANNEL).subchannel(SUBCHANNEL_WEB).language(
            LANGUAGE_EN).build(),
        false, false, PaymentOption.PAY_ON_ARRIVAL.toString(), "", "jonh.doe@wb.com");

    // Act
    var response = reservationInPort.confirmAmend(request, true);

    // Assert
    assertNotNull(response);
    assertEquals(1, response.getReservationByIdList().size());
    assertEquals("2023-10-20", response.getReservationByIdList().get(0)
        .getReservationPackageList().get(0).getStartDate());
    verify(basketOutPort, times(1)).triggerEmailConfirmation(any());
    verify(amendPayNowLogic, never()).saveDepositFolios(any(), any(), any(), any());
    verify(reservationOutPort).updateBusinessItems(any(BusinessItemsRequest.class));
    verify(reservationOutPort).movePaymentDetails(eq(tempBasket.getHotelId()), anySet());
    verify(reservationOutPort).confirmReservation(confirmReservationCaptor.capture());
    var confirmAmendRequest = confirmReservationCaptor.getValue();
    assertNull(confirmAmendRequest.getPibaCardPresent());
    verify(reservationOutPort).updateReservationCcAgentId(any(UpdateReservationCcAgentIdRequest.class));
  }

  @Test
  void amendDistributionSingleCall_roomSelections() {

    // Arrange
    String basketReference = "basketRef";
    var reservationRequest = mockReservationRequestStayDates();
    reservationRequest.getReservations().get(0).getRoomRates().setSpecialRequests(List.of("XPL"));
    var basketResponse = ManageReservationUtils.mockBasketResponse();
    basketResponse.setReference("reference");
    basketResponse.setETag("eTag");
    var request = new UpdateReservationPackagesByIdRequest();
    request.setRoomsSelections(List.of(new RoomsSelectionsByReservationId("12345", List.of())));
    request.setPreviousRoomsSelections(
        List.of(new RoomsSelectionsByReservationId("12345", List.of())));
    var reservationByBasketRef = ReservationByBasketRefResponse.builder()
        .reservationByIdList(
            List.of(ManageReservationUtils.mockReservationByIdResponseConfirm(true)))
        .build();
    var manageBookingResponse = mockManageBookingResponse();
    var mockedFeatureFlag = mock(FeatureFlag.class);
    
    var bookingAllowancesResponse = mockCreateBookingAllowancesResponse();
    var basketSingleReservationResponse = mockBasketSingleReservationResponse();
    List<String> basketBookingAllowances =
        basketSingleReservationResponse.getBookingAllowances() != null ?
            basketSingleReservationResponse.getBookingAllowances().stream()
                .map(BookingAllowance::getAllowance).toList() :
            List.of();

    HotelReservationInPortImpl hotelReservationInPort = Mockito.spy(reservationInPort);

    when(amendDistributionLogicInPort.validateBasketByReference(basketReference))
        .thenReturn(basketResponse);
    when(basketOutPort.getBasketById(any())).thenReturn(basketResponse);
    doReturn(reservationByBasketRef).when(hotelReservationInPort)
        .getAllReservationsJustByBasketReference(anyString(), anyBoolean());
    doReturn(reservationByBasketRef).when(hotelReservationInPort)
        .getAllReservationsJustByBasketReference(anyString(), anyBoolean(), eq(false));
    when(manageBookingInPort.getManageBookingInformation(anyString(), anyString(),
        anyString(), anyString(),
        any(), anyBoolean(), any())).thenReturn(manageBookingResponse);
    when(contentOutPort.getHotelPaymentInformation(basketResponse.getHotelId(),
        LANGUAGE, COUNTRY)).thenReturn(getPaymentResponse());
    when(amendDistributionLogicInPort.extractRemovedRoomsIds
        (reservationRequest.getReservations(), basketResponse.getItems().stream()
            .map(BasketItemResponse::getSourceId).toList()))
        .thenReturn(Collections.emptyList());
    when(amendDistributionLogicInPort.extractNewRooms(anyList()))
        .thenReturn(Collections.emptyList());
    when(unleashWrapper.featureFlag())
        .thenReturn(mockedFeatureFlag);
    tokenUtilsMock.when(() -> TokenUtils.isValid(anyString(), anyString()))
        .thenReturn(Boolean.TRUE);
    when(rulesOutPort.getMaxNightsRule(any())).thenReturn(new MaxNightsRuleResponse(1));
    when(reservationOutPort.getBookingAllowances("TESTHOTEL", "res1", basketBookingAllowances))
        .thenReturn(bookingAllowancesResponse);
    when(amendDistributionLogicInPort.extractStayDates(any(), any(), any(), any()))
        .thenReturn(List.of(mockAmendStayDatesRequest("2023-06-09", "2023-06-10")));

    // Act
    hotelReservationInPort.amendDistributionSingleCall(basketReference, reservationRequest,
        UpdatedReservationsDistribution.builder().build(), request, new BookerDetailsCnp());

    // Assert
    assertEquals(1, reservationRequest.getReservations().size());
    assertEquals("2023-06-09",
        reservationRequest.getReservations().get(0).getRoomRates().getStartDate());
    assertEquals("2023-06-10",
        reservationRequest.getReservations().get(0).getRoomRates().getEndDate());
  }

  @Test
  void amendDistributionSingleCall_differentRoomSelections() {
    // Arrange
    String basketReference = "basketRef";
    var reservationRequest = mockReservationRequestStayDates();
    reservationRequest.getReservations().get(0).getRoomRates().setSpecialRequests(List.of("XPL"));
    var basketResponse = ManageReservationUtils.mockBasketResponse();
    basketResponse.setReference("reference");
    basketResponse.setETag("eTag");
    var request = new UpdateReservationPackagesByIdRequest();
    request.setRoomsSelections(List.of(new RoomsSelectionsByReservationId("12345",
        List.of(new PackagesSelection("HSATWN", 1)))));
    request.setPreviousRoomsSelections(
        List.of(new RoomsSelectionsByReservationId("12345", List.of())));
    var reservationByBasketRef = ReservationByBasketRefResponse.builder()
        .reservationByIdList(List.of(ManageReservationUtils.mockReservationByIdResponseConfirm(true)))
        .build();
    var manageBookingResponse = mockManageBookingResponse();

    HotelReservationInPortImpl hotelReservationInPort = Mockito.spy(reservationInPort);
    when(amendDistributionLogicInPort.validateBasketByReference(basketReference))
        .thenReturn(basketResponse);
    when(basketOutPort.getBasketById(any())).thenReturn(basketResponse);
    doReturn(reservationByBasketRef).when(hotelReservationInPort)
        .getAllReservationsJustByBasketReference(anyString(), anyBoolean());
    doReturn(reservationByBasketRef).when(hotelReservationInPort)
        .getAllReservationsJustByBasketReference(anyString(), anyBoolean(), eq(false));
    when(manageBookingInPort.getManageBookingInformation(anyString(), anyString(),
        anyString(), anyString(),
        any(), anyBoolean(), any())).thenReturn(manageBookingResponse);

    // Act
    hotelReservationInPort.amendDistributionSingleCall(basketReference, reservationRequest,
        UpdatedReservationsDistribution.builder().build(), request, new BookerDetailsCnp());

    // Assert
    assertEquals(1, reservationRequest.getReservations().size());
    assertEquals(0, request.getRoomsSelections().get(0).getPackagesSelection().size());
  }

  @Test
  void amendDistributionSingleCall_addNewRoom_copyBookerDetails() {
    // Arrange
    String basketReference = "basketRef";
    var reservationRequest = mockReservationRequestStayDates();
    var basketResponse = ManageReservationUtils.mockBasketResponse();
    basketResponse.setReference("reference");
    basketResponse.setETag("eTag");
    var request = new UpdateReservationPackagesByIdRequest();
    request.setRoomsSelections(new ArrayList<>(List.of(new RoomsSelectionsByReservationId("12345", null))));
    request.setPreviousRoomsSelections(
            new ArrayList<>(List.of(new RoomsSelectionsByReservationId("12345", new ArrayList<>(List.of())))));
    var reservationByBasketRef = ReservationByBasketRefResponse.builder()
            .reservationByIdList(new ArrayList<>(List.of(ManageReservationUtils.mockReservationByIdResponseConfirm(false))))
            .build();
    reservationByBasketRef.getReservationByIdList().get(0).setReservationBooker(mockBooker());
    var manageBookingResponse = mockManageBookingResponse();

    HotelReservationInPortImpl hotelReservationInPort = Mockito.spy(reservationInPort);

    when(amendDistributionLogicInPort.validateBasketByReference(basketReference))
            .thenReturn(basketResponse);
    when(basketOutPort.getBasketById(any())).thenReturn(basketResponse);
    when(amendDistributionLogicInPort.extractRemovedRoomsIds
            (reservationRequest.getReservations(), basketResponse.getItems().stream()
                    .map(BasketItemResponse::getSourceId).toList()))
            .thenReturn(Collections.emptyList());
    doReturn(reservationByBasketRef).when(hotelReservationInPort)
            .getAllReservationsJustByBasketReference(anyString(), anyBoolean());
    when(amendDistributionLogicInPort.extractNewRooms(anyList()))
            .thenReturn(new ArrayList<>(List.of(mockReservationAddNewRoom())));
    doReturn(TempBookingRefResponse.builder()
            .tempBookingRef(TEMPORARY_BASKET_REF)
            .tempReservationId("reservationId")
            .build()).when(hotelReservationInPort)
            .addNewRoomToExistingBasket(anyString(), any(), any());

    when(manageBookingInPort.getManageBookingInformation(anyString(), anyString(),
            anyString(), anyString(),
            any(), anyBoolean(), any())).thenReturn(manageBookingResponse);

    // Act
    var result = hotelReservationInPort.amendDistributionSingleCall(basketReference, reservationRequest,
            UpdatedReservationsDistribution.builder().build(), request, null);

    // Assert
    assertNotNull(result.getReservationByIdList().get(0).getReservationBooker());
    assertEquals("Adam", result.getReservationByIdList().get(0).getReservationBooker().getFirstName());
  }

  @Test
  void amendDistributionSingleCall_samePackagesSelections() {
    // Arrange
    String basketReference = "basketRef";
    var reservationRequest = mockReservationRequestStayDates();
    reservationRequest.getReservations().get(0).getRoomRates().setSpecialRequests(List.of("XPL"));
    var basketResponse = ManageReservationUtils.mockBasketResponse();
    basketResponse.setReference("reference");
    basketResponse.setETag("eTag");
    var request = new UpdateReservationPackagesByIdRequest();
    request.setRoomsSelections(List.of(new RoomsSelectionsByReservationId("77777",
        List.of(new PackagesSelection("pkg1", 1)))));
    request.setPreviousRoomsSelections(
        List.of(new RoomsSelectionsByReservationId("77777",
            List.of(new PackagesSelection("pkg1", 1)))));
    var reservationByBasketRef = ReservationByBasketRefResponse.builder()
        .reservationByIdList(List.of(ManageReservationUtils.mockReservationByIdResponseConfirm(true)))
        .build();
    var manageBookingResponse = mockManageBookingResponse();

    HotelReservationInPortImpl hotelReservationInPort = Mockito.spy(reservationInPort);
    when(amendDistributionLogicInPort.validateBasketByReference(basketReference))
        .thenReturn(basketResponse);
    when(basketOutPort.getBasketById(any())).thenReturn(basketResponse);
    doReturn(reservationByBasketRef).when(hotelReservationInPort)
        .getAllReservationsJustByBasketReference(anyString(), anyBoolean());
    doReturn(reservationByBasketRef).when(hotelReservationInPort)
        .getAllReservationsJustByBasketReference(anyString(), anyBoolean(), eq(false));
    when(manageBookingInPort.getManageBookingInformation(anyString(), anyString(),
        anyString(), anyString(),
        any(), anyBoolean(), any())).thenReturn(manageBookingResponse);

    // Act
    hotelReservationInPort.amendDistributionSingleCall(basketReference, reservationRequest,
        UpdatedReservationsDistribution.builder().build(), request, new BookerDetailsCnp());

    // Assert
    assertEquals(1, reservationRequest.getReservations().size());
    assertEquals(1, request.getRoomsSelections().get(0).getPackagesSelection().size());
    assertEquals("pkg1", request.getRoomsSelections().get(0).getPackagesSelection().get(0).getId());
  }

  @Test
  void amendDistributionSingleCall_sameHSATWNPackagesSelections() {
    // Arrange
    String basketReference = "basketRef";
    var reservationRequest = mockReservationRequestStayDates();
    reservationRequest.getReservations().get(0).getRoomRates().setSpecialRequests(List.of("XPL"));
    var basketResponse = ManageReservationUtils.mockBasketResponse();
    basketResponse.setReference("reference");
    basketResponse.setETag("eTag");
    var request = new UpdateReservationPackagesByIdRequest();
    request.setRoomsSelections(List.of(new RoomsSelectionsByReservationId("77777",
        List.of(new PackagesSelection("HSATWN", 1)))));
    request.setPreviousRoomsSelections(
        List.of(new RoomsSelectionsByReservationId("77777",
            List.of(new PackagesSelection("HSATWN", 1)))));
    var reservationByBasketRef = ReservationByBasketRefResponse.builder()
        .reservationByIdList(List.of(ManageReservationUtils.mockReservationByIdResponseConfirm(true)))
        .build();
    var manageBookingResponse = mockManageBookingResponse();

    HotelReservationInPortImpl hotelReservationInPort = Mockito.spy(reservationInPort);
    when(amendDistributionLogicInPort.validateBasketByReference(basketReference))
        .thenReturn(basketResponse);
    when(basketOutPort.getBasketById(any())).thenReturn(basketResponse);
    doReturn(reservationByBasketRef).when(hotelReservationInPort)
        .getAllReservationsJustByBasketReference(anyString(), anyBoolean());
    doReturn(reservationByBasketRef).when(hotelReservationInPort)
        .getAllReservationsJustByBasketReference(anyString(), anyBoolean(), eq(false));
    when(manageBookingInPort.getManageBookingInformation(anyString(), anyString(),
        anyString(), anyString(),
        any(), anyBoolean(), any())).thenReturn(manageBookingResponse);

    // Act
    hotelReservationInPort.amendDistributionSingleCall(basketReference, reservationRequest,
        UpdatedReservationsDistribution.builder().build(), request, new BookerDetailsCnp());

    // Assert
    assertEquals(1, reservationRequest.getReservations().size());
    assertEquals(1, request.getRoomsSelections().get(0).getPackagesSelection().size());
    assertEquals("HSATWN",
        request.getRoomsSelections().get(0).getPackagesSelection().get(0).getId());
  }

  @Test
  void amendDistributionSingleCall_editRoomRequest_setsRoomType_whenOperaRoomTypePresent() {
    String basketReference = "basketRef";
    var reservationRequest = mockReservationRequestStayDates();
    var basketResponse = ManageReservationUtils.mockBasketResponse();
    basketResponse.setReference("reference");
    basketResponse.setETag("eTag");
    var reservationByBasketRef = ReservationByBasketRefResponse.builder()
        .reservationByIdList(List.of(ManageReservationUtils.mockReservationByIdResponseConfirm(true)))
        .build();
    var bookingAllowancesResponse = mockCreateBookingAllowancesResponse();
    var basketSingleReservationResponse = mockBasketSingleReservationResponse();
    List<String> basketBookingAllowances =
        basketSingleReservationResponse.getBookingAllowances() != null ?
            basketSingleReservationResponse.getBookingAllowances().stream()
                .map(BookingAllowance::getAllowance).toList() :
            List.of();
    
    var packagesRequest = new UpdateReservationPackagesByIdRequest();
    packagesRequest.setRoomsSelections(List.of(new RoomsSelectionsByReservationId("12345", null)));

    HotelReservationInPortImpl hotelReservationInPort = Mockito.spy(reservationInPort);
    
    when(reservationOutPort.getBookingAllowances("TESTHOTEL", "res1", basketBookingAllowances))
        .thenReturn(bookingAllowancesResponse);
    when(amendDistributionLogicInPort.validateBasketByReference(basketReference))
        .thenReturn(basketResponse);
    when(basketOutPort.getBasketById(any())).thenReturn(basketResponse);
    doReturn(reservationByBasketRef).when(hotelReservationInPort)
        .getAllReservationsJustByBasketReference(anyString(), anyBoolean());
    doReturn(reservationByBasketRef).when(hotelReservationInPort)
        .getAllReservationsJustByBasketReference(anyString(), anyBoolean(), eq(false));

    when(amendDistributionLogicInPort.extractUpdatedReservations(anyList(), isNull(), anyString()))
        .thenReturn(List.of(UpdateReservationsRequest.builder().build()));

    var updateRoomRateWithOpera = UpdateRoomRateRequest.builder()
        .roomType("INIT")
        .operaRoomType("DB")
        .build();
    var updateRoomStay = UpdateRoomStayRequest.builder()
        .roomRates(List.of(updateRoomRateWithOpera))
        .build();
    var updateReservation = UpdateReservationRequest.builder()
        .roomStay(updateRoomStay)
        .build();
    var editReturn = UpdateReservationsRequest.builder()
        .reservations(List.of(updateReservation))
        .build();
    lenient().doReturn(editReturn).when(hotelReservationInPort)
        .editRoomSingleCall(any(), anyString(), any(), any(), any());

    lenient().doReturn(new UpdateReservationPackagesByIdRequest()).when(hotelReservationInPort)
        .buildUpdateReservationPackagesByIdSingleCall(any());

    ArgumentCaptor<UpdateReservationsRequest> editCaptor = ArgumentCaptor.forClass(UpdateReservationsRequest.class);
    doNothing().when(reservationOutPort).amendEditRoom(editCaptor.capture());

    var mockedFeatureFlag = mock(FeatureFlag.class);
    lenient().when(unleashWrapper.featureFlag()).thenReturn(mockedFeatureFlag);
    var aemSearchRulesFf = mock(FeatureFlag.Feature.class);
    lenient().when(mockedFeatureFlag.getAemSearchRules()).thenReturn(aemSearchRulesFf);
    lenient().when(unleashWrapper.isEnabled(aemSearchRulesFf)).thenReturn(false);
    lenient().when(rulesOutPort.getMaxNightsRule(any())).thenReturn(new MaxNightsRuleResponse(9));
    var amendDistrFf = mock(FeatureFlag.Feature.class);
    lenient().when(unleashWrapper.isEnabled(amendDistrFf)).thenReturn(false);
    when(amendDistributionLogicInPort.extractStayDates(any(), any(), any(), any()))
        .thenReturn(List.of(mockAmendStayDatesRequest("2023-06-09", "2023-06-10")));
    when(contentOutPort.getHotelPaymentInformation(any(), any(), any())).thenReturn(getPaymentResponse());
    when(manageBookingInPort.getManageBookingInformation(anyString(), anyString(), anyString(), anyString(), any(), anyBoolean(), any()))
        .thenReturn(mockManageBookingResponse());
    tokenUtilsMock.when(() -> TokenUtils.isValid(anyString(), anyString())).thenReturn(Boolean.TRUE);

    hotelReservationInPort.amendDistributionSingleCall(basketReference, reservationRequest,
        UpdatedReservationsDistribution.builder().build(), packagesRequest, new BookerDetailsCnp());

    var sent = editCaptor.getValue();
    var roomRate = sent.getReservations().get(0).getRoomStay().getRoomRates().get(0);
    assertEquals("DB", roomRate.getRoomType());
  }

  @Test
  void amendDistributionSingleCall_editRoomRequest_keepsRoomType_whenOperaRoomTypeNull() {
    String basketReference = "basketRef";
    var reservationRequest = mockReservationRequestStayDates();
    var basketResponse = ManageReservationUtils.mockBasketResponse();
    basketResponse.setReference("reference");
    basketResponse.setETag("eTag");
    var reservationByBasketRef = ReservationByBasketRefResponse.builder()
        .reservationByIdList(List.of(ManageReservationUtils.mockReservationByIdResponseConfirm(true)))
        .build();
    
    var bookingAllowancesResponse = mockCreateBookingAllowancesResponse();
    var basketSingleReservationResponse = mockBasketSingleReservationResponse();
    List<String> basketBookingAllowances =
        basketSingleReservationResponse.getBookingAllowances() != null ?
            basketSingleReservationResponse.getBookingAllowances().stream()
                .map(BookingAllowance::getAllowance).toList() :
            List.of();

    var packagesRequest = new UpdateReservationPackagesByIdRequest();
    packagesRequest.setRoomsSelections(List.of(new RoomsSelectionsByReservationId("12345", null)));

    HotelReservationInPortImpl hotelReservationInPort = Mockito.spy(reservationInPort);

    when(amendDistributionLogicInPort.validateBasketByReference(basketReference))
        .thenReturn(basketResponse);
    when(basketOutPort.getBasketById(any())).thenReturn(basketResponse);
    doReturn(reservationByBasketRef).when(hotelReservationInPort)
        .getAllReservationsJustByBasketReference(anyString(), anyBoolean());
    doReturn(reservationByBasketRef).when(hotelReservationInPort)
        .getAllReservationsJustByBasketReference(anyString(), anyBoolean(), eq(false));

    when(amendDistributionLogicInPort.extractUpdatedReservations(anyList(), isNull(), anyString()))
        .thenReturn(List.of(UpdateReservationsRequest.builder().build()));

    var updateRoomRateWithNullOpera = UpdateRoomRateRequest.builder()
        .roomType("INIT")
        .operaRoomType(null)
        .build();
    var updateRoomStay = UpdateRoomStayRequest.builder()
        .roomRates(List.of(updateRoomRateWithNullOpera))
        .build();
    var updateReservation = UpdateReservationRequest.builder()
        .roomStay(updateRoomStay)
        .build();
    var editReturn = UpdateReservationsRequest.builder()
        .reservations(List.of(updateReservation))
        .build();
    lenient().doReturn(editReturn).when(hotelReservationInPort)
        .editRoomSingleCall(any(), anyString(), any(), any(), any());

    lenient().doReturn(new UpdateReservationPackagesByIdRequest()).when(hotelReservationInPort)
        .buildUpdateReservationPackagesByIdSingleCall(any());

    ArgumentCaptor<UpdateReservationsRequest> editCaptor = ArgumentCaptor.forClass(UpdateReservationsRequest.class);
    doNothing().when(reservationOutPort).amendEditRoom(editCaptor.capture());

    var mockedFeatureFlag2 = mock(FeatureFlag.class);
    when(unleashWrapper.featureFlag()).thenReturn(mockedFeatureFlag2);
    var aemSearchRulesFf2 = mock(FeatureFlag.Feature.class);
    when(mockedFeatureFlag2.getAemSearchRules()).thenReturn(aemSearchRulesFf2);
    when(unleashWrapper.isEnabled(aemSearchRulesFf2)).thenReturn(false);
    when(rulesOutPort.getMaxNightsRule(any())).thenReturn(new MaxNightsRuleResponse(9));
    when(reservationOutPort.getBookingAllowances("TESTHOTEL", "res1", basketBookingAllowances))
        .thenReturn(bookingAllowancesResponse);
    when(amendDistributionLogicInPort.extractStayDates(any(), any(), any(), any()))
        .thenReturn(List.of(mockAmendStayDatesRequest("2023-06-09", "2023-06-10")));
    when(contentOutPort.getHotelPaymentInformation(any(), any(), any())).thenReturn(getPaymentResponse());
    when(manageBookingInPort.getManageBookingInformation(anyString(), anyString(), anyString(), anyString(), any(), anyBoolean(), any()))
        .thenReturn(mockManageBookingResponse());
    tokenUtilsMock.when(() -> TokenUtils.isValid(anyString(), anyString())).thenReturn(Boolean.TRUE);

    hotelReservationInPort.amendDistributionSingleCall(basketReference, reservationRequest,
        UpdatedReservationsDistribution.builder().build(), packagesRequest, new BookerDetailsCnp());

    var sent = editCaptor.getValue();
    var roomRate = sent.getReservations().get(0).getRoomStay().getRoomRates().get(0);
    assertEquals("INIT", roomRate.getRoomType());
  }

  @Test
  void buildUpdateReservationPackagesByIdSingleCall_validateAndFilterWithSucces() {
    //Arrange
    var request = new UpdateReservationPackagesByIdRequest();
    request.setRoomsSelections(List.of(new RoomsSelectionsByReservationId("77777",
        List.of(new PackagesSelection("pkg1", 1)))));
    request.setPreviousRoomsSelections(
        List.of(new RoomsSelectionsByReservationId("77777",
            List.of(new PackagesSelection("pkg1", 1)))));

    HotelReservationInPortImpl hotelReservationInPort = Mockito.spy(reservationInPort);

    // Act
    var response = hotelReservationInPort.buildUpdateReservationPackagesByIdSingleCall(request);

    // Assert
    assertEquals(1, response.getRoomsSelections().get(0).getPackagesSelection().size());
    assertEquals("pkg1",
        request.getRoomsSelections().get(0).getPackagesSelection().get(0).getId());
  }

  @Test
  void buildUpdateReservationPackagesByIdSingleCall_validateAndFilterNoPackagesWithSucces() {
    //Arrange
    var request = new UpdateReservationPackagesByIdRequest();
    request.setRoomsSelections(List.of(new RoomsSelectionsByReservationId("77777",
        List.of())));
    request.setPreviousRoomsSelections(
        List.of(new RoomsSelectionsByReservationId("77777",
            List.of())));

    HotelReservationInPortImpl hotelReservationInPort = Mockito.spy(reservationInPort);

    // Act
    var response = hotelReservationInPort.buildUpdateReservationPackagesByIdSingleCall(request);

    // Assert
    assertEquals(0, response.getRoomsSelections().get(0).getPackagesSelection().size());
  }

  @Test
  void amendDistributionSingleCall_throwsException() {
    // Arrange
    String basketReference = "basketRef";
    var reservationRequest = mockReservationRequestStayDates();
    var basketResponse = ManageReservationUtils.mockBasketResponse();
    basketResponse.setReference("reference");
    basketResponse.setETag("eTag");
    var request = new UpdateReservationPackagesByIdRequest();
    request.setRoomsSelections(List.of(new RoomsSelectionsByReservationId("12345", List.of())));
    request.setPreviousRoomsSelections(
        List.of(new RoomsSelectionsByReservationId("12345", List.of())));
    var reservationByBasketRef = ReservationByBasketRefResponse.builder()
        .reservationByIdList(List.of(ManageReservationUtils.mockReservationByIdResponseConfirm(true)))
        .build();
    var manageBookingResponse = mockManageBookingResponse();

    HotelReservationInPortImpl hotelReservationInPort = Mockito.spy(reservationInPort);

    when(amendDistributionLogicInPort.validateBasketByReference(basketReference))
        .thenReturn(basketResponse);
    doReturn(reservationByBasketRef).when(hotelReservationInPort)
        .getAllReservationsJustByBasketReference(anyString(), anyBoolean());
    when(basketOutPort.getBasketById(basketReference)).thenThrow(BasketNotFoundException.class);
    when(manageBookingInPort.getManageBookingInformation(anyString(), anyString(),
        anyString(), anyString(),
        any(), anyBoolean(), any())).thenReturn(manageBookingResponse);

    // Assert
    assertThrows(BasketNotFoundException.class,
        () -> hotelReservationInPort.amendDistributionSingleCall(
            basketReference, reservationRequest, null,
            request, null
        ));

  }

  @Test
  void amendDistributionSingleCall_HotelReservationOhipException() {
    // Arrange

    String basketReference = "basketRef";
    var reservationRequest = mockReservationRequestStayDates();
    reservationRequest.getReservations().get(0).getRoomRates().setSpecialRequests(List.of("XPL"));
    var basketResponse = ManageReservationUtils.mockBasketResponse();
    basketResponse.setReference("reference");
    basketResponse.setETag("eTag");
    var request = new UpdateReservationPackagesByIdRequest();
    request.setRoomsSelections(List.of(new RoomsSelectionsByReservationId("res1", List.of())));
    request.setPreviousRoomsSelections(
        List.of(new RoomsSelectionsByReservationId("res1", List.of())));
    var reservationByBasketRef = ReservationByBasketRefResponse.builder()
        .reservationByIdList(
            List.of(ManageReservationUtils.mockReservationByIdResponseConfirm(true)))
        .build();
    var manageBookingResponse = mockManageBookingResponse();
    var updatedReservationRequests = mockRequestEditRoom();
    updatedReservationRequests.getReservations().get(0).setReservationId("res1");
    updatedReservationRequests.getReservations().get(0).getRoomStay().getRoomOccupancy()
        .setAdultCount(2);

    HotelReservationInPortImpl hotelReservationInPort = Mockito.spy(reservationInPort);

    when(amendDistributionLogicInPort.validateBasketByReference(basketReference))
        .thenReturn(basketResponse);
    when(basketOutPort.getBasketById(any())).thenReturn(basketResponse);
    doReturn(reservationByBasketRef).when(hotelReservationInPort)
        .getAllReservationsJustByBasketReference(anyString(), anyBoolean());
    doReturn(reservationByBasketRef).when(hotelReservationInPort)
        .getAllReservationsJustByBasketReference(anyString(), anyBoolean(), anyBoolean());
    when(manageBookingInPort.getManageBookingInformation(anyString(), anyString(),
        anyString(), anyString(),
        any(), anyBoolean(), any())).thenReturn(manageBookingResponse);
    when(amendDistributionLogicInPort.extractUpdatedReservations(any(), any(), any())).thenReturn(
        List.of(updatedReservationRequests));
    tokenUtilsMock.when(() -> TokenUtils.isValid(anyString(), anyString()))
        .thenReturn(Boolean.TRUE);
    doThrow(HotelReservationOhipException.class).when(reservationOutPort)
        .amendEditRoom(updatedReservationRequests);

    // Assert
    assertThrows(HotelReservationOhipException.class,
        () -> hotelReservationInPort.amendDistributionSingleCall(
            basketReference, reservationRequest,
            UpdatedReservationsDistribution.builder().build(),
            request, null
        ));
  }

  @Test
  void copyBookingTest_POA_PaymentType_Success() {
    // Arrange
    String basketReference = "basketRef";
    var bookingAllowancesResponse = mockCreateBookingAllowancesResponse();
    var copyBookingRequest = mockCopyBookingRequest(basketReference);
    var basketResponse = ManageReservationUtils.mockBasketResponse();

    when(authenticatedUserService.isUserAuthenticated()).thenReturn(Boolean.TRUE);
    when(basketOutPort.createBasket(anyString(), anyString(), any(), any(), any(), any(), any(), any()))
        .thenReturn(ManageReservationUtils.mockBasketResponse());
    var copyReservationsResponse = mockCopyReservationsResponse();
    var reservationId = copyReservationsResponse.getReservations().get(0).getReservationId();
    when(reservationOutPort.copyReservations(any())).thenReturn(
        copyReservationsResponse);
    when(basketOutPort.addReservationsToBasket(any(), any(),
        any(CopyReservationsResponse.class), anyBoolean(), any(Map.class)))
        .thenReturn(ManageReservationUtils.mockBasketResponse());
    when(basketOutPort.getBasketById(any())).thenReturn(basketResponse);
    when(basketOutPort.linkAmendReservationsInBasket(any(), any(), any(), any())).thenReturn(
        basketResponse);
    when(reservationOutPort.getBookingAllowances(anyString(), anyString(), anyList()))
        .thenReturn(bookingAllowancesResponse);
    when(reservationOutPort.getReservationsPackagesByIds(any(), any())).thenReturn(
        ManageReservationUtils.mockCreateReservationsPackagesResponse(false, ""));
    when(packageProperties.getGroups()).thenReturn(Map.of("MDP", Set.of("MDBFST", "MDBEVA", "MD2DIN")));

    var argCaptor = ArgumentCaptor.forClass(Map.class);

    var mockedFeatureFlag = mock(FeatureFlag.class);
    when(unleashWrapper.featureFlag())
        .thenReturn(mockedFeatureFlag);
    when(unleashWrapper.isEnabled(mockedFeatureFlag.getSaveAllowancesInBasket()))
        .thenReturn(false);

    // Act
    var response = reservationInPort.copyBooking(copyBookingRequest);

    // Assert
    assertNotNull(response);
    verify(basketOutPort).addReservationsToBasket(any(), any(), any(CopyReservationsResponse.class), anyBoolean(),
        argCaptor.capture());
    var hasOccupancySupMap = argCaptor.getValue();
    assertEquals(1, hasOccupancySupMap.keySet().size());
    assertTrue(hasOccupancySupMap.keySet().contains(reservationId));
    assertTrue((Boolean)hasOccupancySupMap.get(reservationId));
  }

  @ParameterizedTest
  @ValueSource(booleans = {true, false})
  void copyBookingTest_PN_PaymentType_Success(boolean isCheckInOnline) {
    // Arrange
    String basketReference = "basketRef";
    var bookingAllowancesResponse = mockCreateBookingAllowancesResponse();
    var copyBookingRequest = mockCopyBookingRequest(basketReference);
    var basketResponse = mockBasketResponsePN(null, isCheckInOnline);
    when(authenticatedUserService.isUserAuthenticated()).thenReturn(Boolean.TRUE);
    when(basketOutPort.createBasket(anyString(), anyString(), any(), any(), any(), any(), any(), any()))
        .thenReturn(mockBasketResponsePN());
    when(reservationOutPort.copyReservations(any())).thenReturn(
        mockCopyReservationsResponse());
    when(basketOutPort.addReservationsToBasket(any(), any(),
        any(CopyReservationsResponse.class), anyBoolean(), any(Map.class)))
        .thenReturn(mockBasketResponsePN());
    when(basketOutPort.getBasketById(any())).thenReturn(basketResponse);
    when(basketOutPort.linkAmendReservationsInBasket(any(), any(), any(), any())).thenReturn(
        basketResponse);
    when(reservationOutPort.getBookingAllowances(anyString(), anyString(), anyList()))
        .thenReturn(bookingAllowancesResponse);
    when(reservationOutPort.getReservationsByIds(any(), any(), anyBoolean(), anyBoolean(), anyBoolean())).thenReturn(
        ManageReservationUtils.mockReservationByBasketRefResponse());
    when(basketOutPort.getCharges(any()))
        .thenReturn(ManageReservationUtils.mockDepositFoliosResponse(!isCheckInOnline));
    when(reservationOutPort.getReservationsPackagesByIds(any(), any())).thenReturn(
        ManageReservationUtils.mockCreateReservationsPackagesResponse(false, ""));
    when(packageProperties.getGroups()).thenReturn(Map.of("MDP", Set.of("MDBFST", "MDBEVA", "MD2DIN")));

    var mockedFeatureFlag = mock(FeatureFlag.class);
    when(unleashWrapper.featureFlag())
        .thenReturn(mockedFeatureFlag);
    when(unleashWrapper.isEnabled(mockedFeatureFlag.getSaveAllowancesInBasket()))
        .thenReturn(false);

    // Act
    var response = reservationInPort.copyBooking(copyBookingRequest);

    // Assert
    assertNotNull(response);
    assertEquals(response.getCopyBasketReference(), ("TST-16a014c5-d8a4-4418-8d15-2537660f08e9"));
  }

  @Test
  void copyBookingTest_PN_PaymentType_NoChargesError() {
    // Arrange
    String basketReference = "basketRef";
    var bookingAllowancesResponse = mockCreateBookingAllowancesResponse();
    var copyBookingRequest = mockCopyBookingRequest(basketReference);
    var basketResponse = mockBasketResponsePN();
    when(authenticatedUserService.isUserAuthenticated()).thenReturn(Boolean.TRUE);
    when(basketOutPort.createBasket(anyString(), anyString(), any(), any(), any(), any(), any(), any()))
        .thenReturn(mockBasketResponsePN());
    when(reservationOutPort.copyReservations(any())).thenReturn(
        mockCopyReservationsResponse());
    when(basketOutPort.addReservationsToBasket(any(), any(),
        any(CopyReservationsResponse.class), anyBoolean(), any(Map.class)))
        .thenReturn(mockBasketResponsePN());
    when(basketOutPort.getBasketById(any())).thenReturn(basketResponse);
    when(basketOutPort.linkAmendReservationsInBasket(any(), any(), any(), any())).thenReturn(
        basketResponse);
    when(reservationOutPort.getBookingAllowances(anyString(), anyString(), anyList()))
        .thenReturn(bookingAllowancesResponse);
    when(reservationOutPort.getReservationsByIds(any(), any(), anyBoolean(), anyBoolean(), anyBoolean())).thenReturn(
        ManageReservationUtils.mockReservationByBasketRefResponse());
    when(basketOutPort.getCharges(any())).thenReturn(DepositFoliosResponse.builder().build());
    when(reservationOutPort.getReservationsPackagesByIds(any(), any())).thenReturn(
        ManageReservationUtils.mockCreateReservationsPackagesResponse(false, ""));
    when(packageProperties.getGroups()).thenReturn(Map.of("MDP", Set.of("MDBFST", "MDBEVA", "MD2DIN")));

    var mockedFeatureFlag = mock(FeatureFlag.class);
    when(unleashWrapper.featureFlag())
        .thenReturn(mockedFeatureFlag);
    when(unleashWrapper.isEnabled(mockedFeatureFlag.getSaveAllowancesInBasket()))
        .thenReturn(false);

    // Act & Assert
    ReservationNotFoundException exception = assertThrows(ReservationNotFoundException.class,
        () -> reservationInPort.copyBooking(copyBookingRequest));

    // Assert
    assertNotNull(exception);
    assertEquals(DIGITAL_RESERVATION_CHARGES_EXCEPTION.getCode(), exception.getErrorCode());
  }

  @Test
  void copyBookingTest_PN_PaymentType_NoChargesPresent() {
    // Arrange
    String basketReference = "basketRef";
    var bookingAllowancesResponse = mockCreateBookingAllowancesResponse();
    var copyBookingRequest = mockCopyBookingRequest(basketReference);
    var basketResponse = mockBasketResponsePN();
    when(authenticatedUserService.isUserAuthenticated()).thenReturn(Boolean.TRUE);
    when(basketOutPort.createBasket(anyString(), anyString(), any(), any(), any(), any(), any(), any()))
        .thenReturn(mockBasketResponsePN());
    when(reservationOutPort.copyReservations(any())).thenReturn(
        mockCopyReservationsResponse());
    when(basketOutPort.addReservationsToBasket(any(), any(),
        any(CopyReservationsResponse.class), anyBoolean(), any(Map.class)))
        .thenReturn(mockBasketResponsePN());
    when(basketOutPort.getBasketById(any())).thenReturn(basketResponse);
    when(basketOutPort.linkAmendReservationsInBasket(any(), any(), any(), any())).thenReturn(
        basketResponse);
    when(reservationOutPort.getBookingAllowances(anyString(), anyString(), anyList()))
          .thenReturn(bookingAllowancesResponse);
    when(reservationOutPort.getReservationsByIds(any(), any(), eq(false), eq(false), eq(false))).thenReturn(
        ManageReservationUtils.mockReservationByBasketRefResponse());
    when(reservationOutPort.getReservationsPackagesByIds(any(), any())).thenReturn(
        ManageReservationUtils.mockCreateReservationsPackagesResponse(false, ""));
    when(packageProperties.getGroups()).thenReturn(Map.of("MDP", Set.of("MDBFST", "MDBEVA", "MD2DIN")));

    var mockedFeatureFlag = mock(FeatureFlag.class);
    when(unleashWrapper.featureFlag()).thenReturn(mockedFeatureFlag);
    when(unleashWrapper.isEnabled(mockedFeatureFlag.getSaveAllowancesInBasket()))
        .thenReturn(false);

    // Assert
    assertThrows(ReservationNotFoundException.class,
        () -> reservationInPort.copyBooking(copyBookingRequest));
  }

  @Test
  void copyBookingTest_removePackageGroup() {
    // Arrange
    String basketReference = "basketRef";
    var bookingAllowancesResponse = mockCreateBookingAllowancesResponse();
    var copyBookingRequest = mockCopyBookingRequest(basketReference);
    var basketResponse = mockBasketResponsePN(null, false);
    when(authenticatedUserService.isUserAuthenticated()).thenReturn(Boolean.TRUE);
    when(basketOutPort.createBasket(anyString(), anyString(), any(), any(), any(), any(), any(), any()))
        .thenReturn(mockBasketResponsePN());
    when(reservationOutPort.copyReservations(any())).thenReturn(
        mockCopyReservationsResponse());
    when(basketOutPort.addReservationsToBasket(any(), any(),
        any(CopyReservationsResponse.class), anyBoolean(), any(Map.class)))
        .thenReturn(mockBasketResponsePN());
    when(basketOutPort.getBasketById(any())).thenReturn(basketResponse);
    when(basketOutPort.linkAmendReservationsInBasket(any(), any(), any(), any())).thenReturn(
        basketResponse);
    when(reservationOutPort.getBookingAllowances(anyString(), anyString(), anyList()))
        .thenReturn(bookingAllowancesResponse);
    when(reservationOutPort.getReservationsByIds(any(), any(), anyBoolean(), anyBoolean(), anyBoolean())).thenReturn(
        ManageReservationUtils.mockReservationByBasketRefResponse());
    when(basketOutPort.getCharges(any()))
        .thenReturn(ManageReservationUtils.mockDepositFoliosResponse(true));
    when(reservationOutPort.getReservationsPackagesByIds(any(), any())).thenReturn(
        ManageReservationUtils.mockCreateReservationsPackagesResponse(true, "MDP"));
    when(packageProperties.getGroups()).thenReturn(Map.of("MDP", Set.of("MDBFST", "MDBEVA", "MD2DIN")));
    when(reservationOutPort.getReservationsByReservationId(any(), any())).thenReturn(
        ManageReservationUtils.mockReservationIdResponseForPN());
    when(reservationOutPort.updateReservationPackages(any()))
        .thenReturn(new SaveReservationResponse(basketResponse.getReference()));

    var argCaptor = ArgumentCaptor.forClass(ReservationPackagesRequest.class);


    var mockedFeatureFlag = mock(FeatureFlag.class);
    when(unleashWrapper.featureFlag())
        .thenReturn(mockedFeatureFlag);
    when(unleashWrapper.isEnabled(mockedFeatureFlag.getSaveAllowancesInBasket()))
        .thenReturn(false);

    // Act
    reservationInPort.copyBooking(copyBookingRequest);

    // Assert
    verify(reservationOutPort).updateReservationPackages(argCaptor.capture());
    var reservationPackagesRequest = argCaptor.getValue();
    assertEquals(1, reservationPackagesRequest.getRoomsSelections().size());
    assertEquals(1, reservationPackagesRequest.getPreviousRoomsSelections().size());
    assertEquals(2, reservationPackagesRequest.getPreviousRoomsSelections().get(0)
        .getPackagesSelection().size());
    assertEquals(1, reservationPackagesRequest.getRoomsSelections().get(0).getPackagesSelection().size());
    assertNotEquals("MDP", reservationPackagesRequest.getRoomsSelections().get(0)
        .getPackagesSelection().get(0).getId());
    assertEquals("MDP", reservationPackagesRequest.getRoomsSelections().get(0)
        .getPackagesSelection().get(0).getPackageGroup());
  }

  @Test
  void copyBookingTest_throwsReservationNotFound() {
    // Arrange
    String basketReference = "basketRef";
    var copyBookingRequest = mockCopyBookingRequest(basketReference);
    var basketResponse = mockBasketResponsePN(null, false);
    when(authenticatedUserService.isUserAuthenticated()).thenReturn(Boolean.TRUE);
    when(basketOutPort.getBasketById(any())).thenReturn(basketResponse);
    when(reservationOutPort.getReservationsPackagesByIds(any(), any())).thenReturn(
        ManageReservationUtils.mockCreateReservationsPackagesResponse(true, "MDP"));
    when(packageProperties.getGroups()).thenReturn(Map.of("MDP", Set.of("MDBFST", "MDBEVA", "MD2DIN")));
    ReservationByIdDetailsResponse res = ManageReservationUtils.mockReservationIdResponseForPN();
    res.getReservationIdDetailsResponse().getReservations().setReservation(Collections.emptyList());
    when(reservationOutPort.getReservationsByReservationId(any(), any())).thenReturn(
        res);

    // Act & Assert
   assertThrows(ReservationNotFoundException.class, () -> reservationInPort.copyBooking(copyBookingRequest));
  }

  @Test
  void confirmAmend_RefundUnsuccessfulTriggerEmail() {
    // Arrange
    String originalBasketRef = ORIGINAL_BASKET_REF;
    String tempBasketRef = TEMPORARY_BASKET_REF;

    var originalBasket = BasketResponse.builder()
            .bookingReference(originalBasketRef)
            .reference(originalBasketRef)
            .hotelId(HOTEL_CODE_TEST)
            .items(List.of(BasketItemResponse.builder().sourceId(SOURCE_ID_111111).build()))
            .eTag(String.valueOf(Instant.now().toEpochMilli()))
            .paymentOption(PaymentOption.PAY_NOW)
            .build();

    var bookingAllowancesResponse = mockCreateBookingAllowancesResponse();

    var tempBasket = BasketResponse.builder()
            .bookingReference(tempBasketRef)
            .reference(tempBasketRef)
            .status(BASKET_STATUS_OPEN)
            .items(List.of(BasketItemResponse.builder().sourceId(SOURCE_ID_222222).build(),
                    BasketItemResponse.builder().sourceId(SOURCE_ID_333333).build()))
            .linkAmendReservations(Map.of(SOURCE_ID_111111, SOURCE_ID_222222))
            .hotelId(HOTEL_CODE_TEST)
            .originalBasketId(originalBasketRef)
            .eTag(String.valueOf(Instant.now().plus(1, ChronoUnit.MINUTES).toEpochMilli()))
            .paymentOption(PaymentOption.PAY_NOW)
            .build();

    List<String> tempBasketBookingAllowances = tempBasket.getBookingAllowances() != null ?
        tempBasket.getBookingAllowances().stream()
            .map(BookingAllowance::getAllowance)
            .toList() : List.of();

    var reservationByIdResponse = new ArrayList<ReservationByIdResponse>();
    reservationByIdResponse.add(ManageReservationUtils.mockReservationByIdResponseConfirm(true));
    reservationByIdResponse.add(ManageReservationUtils.mockReservationByIdResponseConfirm(false));

    var basketByRefResponse = ReservationByBasketRefResponse.builder()
            .reservationByIdList(reservationByIdResponse)
            .build();

    var mockedFeatureFlag = mock(FeatureFlag.class);

    when(unleashWrapper.featureFlag())
            .thenReturn(mockedFeatureFlag);
    when(basketOutPort.getBasketById(originalBasketRef)).thenReturn(originalBasket);
    when(basketOutPort.getBasketById(tempBasketRef)).thenReturn(tempBasket);
    when(reservationOutPort.getReservationsByIds(anyString(), anyList(), anyBoolean(), anyBoolean(), anyBoolean())).thenReturn(
            basketByRefResponse);
    var copyReservationsResponse = mockCopyReservationsResponse();
    when(reservationOutPort.copyReservations(any())).thenReturn(
        copyReservationsResponse);
    when(basketOutPort.addReservationsToBasket(originalBasket.getReference(),
            originalBasket.getETag().replace("\"", ""),
        copyReservationsResponse, false,
        Map.of(copyReservationsResponse.getReservations().get(0).getReservationId(), false))).thenReturn(originalBasket);
    when(reservationOutPort.getBookingAllowances(HOTEL_CODE_TEST, SOURCE_ID_222222, tempBasketBookingAllowances))
          .thenReturn(bookingAllowancesResponse);
    when(rulesOutPort.getBusinessAllowanceRules()).thenReturn(buildBusinessAllowanceRuleResponse());
    when(contentOutPort.getBusinessNotes(LANGUAGE)).thenReturn(buildBusinessNotesResponse());
    when(contentOutPort.getHotelPaymentInformation(tempBasket.getHotelId(), LANGUAGE, COUNTRY))
            .thenReturn(getPaymentResponse());
    when(reservationOutPort.confirmAmend(any())).thenReturn(basketByRefResponse);

    when(reservationOutPort.getReservationsPackagesByIds(any(), any()))
            .thenReturn(getMockedReservationsPackagesResponseTempBasket());

    when(amendPayNowLogic.calculateDfForPayNow(any(), any(), any(), any(), any(), any(), any(), any()))
            .thenReturn(DepositFolioComputationResult.builder()
                    .totalRefundAmt(BigDecimal.valueOf(50))
                    .markAsPayOnArrival(Boolean.FALSE)
                    .build());
    when(amendPayNowLogic.triggerRefund(any(), any(), any(), any())).thenReturn(
            DepositFolioComputationResult.builder()
                    .markAsPayOnArrival(Boolean.FALSE)
                    .paymentId("2334454")
                    .build());
    when(amendPayNowLogic.triggerRefund(any(), any(), any(), any())).thenThrow(AmendReservationException.class);
    when(unleashWrapper.featureFlag())
        .thenReturn(mockedFeatureFlag);
    when(unleashWrapper.isEnabled(mockedFeatureFlag.getSaveAllowancesInBasket()))
        .thenReturn(false);
    when(unleashWrapper.isEnabled(mockedFeatureFlag.getApplyOccupancySupplement()))
        .thenReturn(false);

    stubCancellationPoliciesFetch();

    var request = new ConfirmAmendRequest(originalBasketRef, tempBasketRef, TOKEN,
            BookingChannel.builder().channel(PI_BOOKING_CHANNEL).subchannel(SUBCHANNEL_WEB).language(
                LANGUAGE_EN).build(),
            false, false, PAY_ON_ARRIVAL.toString(), "", "");

    assertThrows(AmendReservationException.class, () -> reservationInPort.confirmAmend(request, true));
  }

  @Test
  void confirmAmend_RefundUnsuccessfulAndToggleDisablesDoNotTriggerEmail() {
    // Arrange
    String originalBasketRef = ORIGINAL_BASKET_REF;
    String tempBasketRef = TEMPORARY_BASKET_REF;

    var originalBasket = BasketResponse.builder()
        .bookingReference(originalBasketRef)
        .reference(originalBasketRef)
        .hotelId(HOTEL_CODE_TEST)
        .items(List.of(BasketItemResponse.builder().sourceId(SOURCE_ID_111111).build()))
        .eTag(String.valueOf(Instant.now().toEpochMilli()))
        .paymentOption(PaymentOption.PAY_NOW)
        .build();
    var tempBasket = BasketResponse.builder()
        .bookingReference(tempBasketRef)
        .reference(tempBasketRef)
        .status(BASKET_STATUS_OPEN)
        .items(List.of(BasketItemResponse.builder().sourceId(SOURCE_ID_222222).build(),
            BasketItemResponse.builder().sourceId(SOURCE_ID_333333).build()))
        .linkAmendReservations(Map.of(SOURCE_ID_111111, SOURCE_ID_222222))
        .hotelId(HOTEL_CODE_TEST)
        .originalBasketId(originalBasketRef)
        .eTag(String.valueOf(Instant.now().plus(1, ChronoUnit.MINUTES).toEpochMilli()))
        .paymentOption(PaymentOption.PAY_NOW)
        .build();

    List<String> tempBasketBookingAllowances = tempBasket.getBookingAllowances() != null ?
        tempBasket.getBookingAllowances().stream()
            .map(BookingAllowance::getAllowance)
            .toList() : List.of();
    var reservationByIdResponse = new ArrayList<ReservationByIdResponse>();
    reservationByIdResponse.add(ManageReservationUtils.mockReservationByIdResponseConfirm(true));
    reservationByIdResponse.add(ManageReservationUtils.mockReservationByIdResponseConfirm(false));

    var basketByRefResponse = ReservationByBasketRefResponse.builder()
        .reservationByIdList(reservationByIdResponse)
        .build();
    var bookingAllowancesResponse = mockCreateBookingAllowancesResponse();
    var mockedFeatureFlag = mock(FeatureFlag.class);

    when(unleashWrapper.featureFlag())
            .thenReturn(mockedFeatureFlag);

    when(basketOutPort.getBasketById(originalBasketRef)).thenReturn(originalBasket);
    when(basketOutPort.getBasketById(tempBasketRef)).thenReturn(tempBasket);
    when(reservationOutPort.getReservationsByIds(anyString(), anyList(), anyBoolean())).thenReturn(
        basketByRefResponse);
    when(reservationOutPort.copyReservations(any())).thenReturn(
        mockCopyReservationsResponse());
    when(basketOutPort.addReservationsToBasket(originalBasket.getReference(),
        originalBasket.getETag().replace("\"", ""),
        mockCopyReservationsResponse(), false, null)).thenReturn(originalBasket);
    when(reservationOutPort.getBookingAllowances(HOTEL_CODE_TEST, SOURCE_ID_111111, tempBasketBookingAllowances))
        .thenReturn(bookingAllowancesResponse);
    when(rulesOutPort.getBusinessAllowanceRules()).thenReturn(buildBusinessAllowanceRuleResponse());
    when(contentOutPort.getBusinessNotes(LANGUAGE)).thenReturn(buildBusinessNotesResponse());
    when(contentOutPort.getHotelPaymentInformation(tempBasket.getHotelId(), LANGUAGE, COUNTRY))
        .thenReturn(getPaymentResponse());
    when(reservationOutPort.confirmAmend(any())).thenReturn(basketByRefResponse);

    when(reservationOutPort.getReservationsPackagesByIds(any(), any()))
        .thenReturn(getMockedReservationsPackagesResponseTempBasket());

    when(reservationOutPort.updateReservationPackagesByReservationId(any()))
        .thenReturn(Mockito.mock(SaveReservationResponse.class));
    when(amendPayNowLogic.calculateDfForPayNow(any(), any(), any(), any(), any(), any(), any(), any()))
        .thenReturn(DepositFolioComputationResult.builder()
            .totalRefundAmt(BigDecimal.valueOf(50))
            .markAsPayOnArrival(Boolean.FALSE)
            .build());
    when(amendPayNowLogic.triggerRefund(any(), any(), any(), any())).thenReturn(
        DepositFolioComputationResult.builder()
            .markAsPayOnArrival(Boolean.FALSE)
            .paymentId("2334454")
            .build());
    when(amendPayNowLogic.triggerRefund(any(), any(), any(), any())).thenThrow(AmendReservationException.class);

    stubCancellationPoliciesFetch();

    var request = new ConfirmAmendRequest(originalBasketRef, tempBasketRef, TOKEN,
        BookingChannel.builder().channel(PI_BOOKING_CHANNEL).subchannel(SUBCHANNEL_WEB).language(
            LANGUAGE_EN).build(),
        false, false, PAY_ON_ARRIVAL.toString(), "", "");

    assertThrows(AmendReservationException.class, () -> reservationInPort.confirmAmend(request, true));
    verify(basketOutPort, never()).triggerEmailConfirmation(any());
  }

  @ParameterizedTest
  @MethodSource("upsellsScenarios")
  void testUpsellsViaPublicMethod(
      String idContext,
      String cardType,
      boolean expectedUpsells,
      boolean reservationsIdsEmpty) {

    // --- Basket setup ---
    BasketResponse basket = new BasketResponse();
    basket.setIdContext(idContext);
    basket.setHotelId("H1");
    basket.setChannel("WEB");
    basket.setBookingReference("BR1");
    basket.setReference("BASK1");
    basket.setStatus("OK");

    if (reservationsIdsEmpty) {
      basket.setItems(Collections.emptyList());
    } else {
      BasketItemResponse item = new BasketItemResponse();
      item.setSourceId("R1");
      basket.setItems(List.of(item));
    }

    when(basketOutPort.getBasketById("BASK1")).thenReturn(basket);

    // --- Reservation setup ---
    ReservationByIdResponse reservation = new ReservationByIdResponse();
    if (cardType != null) {
      ReservationPaymentCardType card = new ReservationPaymentCardType();
      card.setCardType(cardType);
      reservation.setPaymentCard(card);
    }

    ReservationByBasketRefResponse reservations = new ReservationByBasketRefResponse();
    reservations.setReservationByIdList(List.of(reservation));

    when(reservationOutPort.getReservationsByIds(any(), any(), any(), any(), any()))
        .thenReturn(reservations);

    // --- Act ---
    ReservationByBasketRefResponse result =
        reservationInPort.getAllReservationsJustByBasketReference("BASK1", true, true);

    // --- Assert ---
    assertEquals(expectedUpsells, result.getUpsellsAddonsEnabled(),
        "Mismatch for idContext=" + idContext
            + ", cardType=" + cardType
            + ", reservationsIdsEmpty=" + reservationsIdsEmpty);
  }


  private CopyBookingRequest mockCopyBookingRequest(String basketReference) {
    return CopyBookingRequest.builder()
        .bookingChannel(BookingChannel
            .builder()
            .channel(PI_BOOKING_CHANNEL)
            .language(LANGUAGE)
            .build())
        .token(getMockedToken())
        .originalBasketReference(basketReference)
        .build();
  }

  private Reservation mockReservationAddNewRoom() {
    return Reservation.builder()
        .hotelId("TESTHOTEL")
        .externalReferenceId(null)
        .adultsNumber(1)
        .childrenNumber(0)
        .roomRates(RoomRate.builder().startDate("2023-06-09").endDate("2023-06-10")
            .pmsRoomType("DB")
            .build())
        .leadGuest(LeadGuest.builder().title("Mr").firstName("John").lastName("Doe").build())
        .build();
  }

  private Reservation mockReservationEditRoom() {
    return Reservation.builder()
        .hotelId("TESTHOTEL")
        .externalReferenceId("1736673")
        .roomRates(RoomRate.builder().startDate("2023-06-09").endDate("2023-06-10").build())
        .leadGuest(LeadGuest.builder().title("Mr").firstName("George").lastName("Write").build())
        .build();
  }

  private ReservationRequest mockReservationRequestStayDates() {
    var reservations = new ArrayList<Reservation>();
    reservations.add(mockReservationEditRoom());
    return ReservationRequest.builder()
        .reservations(reservations)
        .token("test_1837749")
        .bookingChannel(BookingChannel.builder()
            .channel("DISTR")
            .subchannel("AGENCY")
            .language("N/A")
            .build())
            .token(getMockedToken())
        .build();
  }

  @Test
  void updateBookerEmail_success() {
    //Arrange
    String basketReference = "AWM12345";
    when(basketOutPort.getBasketById(basketReference)).thenReturn(ManageReservationUtils.mockBasketResponse());
    //Act
    reservationInPort.updateEmailReservation(basketReference, mockEmailReservationRequest());

    //Assert
    verify(reservationOutPort, times(1)).updateBookerEmail(mockUpdateBookerEmailRequest());
  }

  @Test
  void createMemo_Success() {
    // Arrange
    when(basketOutPort.getBasketById(any())).thenReturn(ManageReservationUtils.mockBasketResponse());
    when(reservationOutPort.createMemo(any())).thenReturn(new MemosResponse());

    // Act
    var response = reservationInPort.createMemo(
        CreateMemoRequest.builder().description("memo").build());

    // Assert
    assertNotNull(response);
  }

  @Test
  void getMemos_Success() {
    // Arrange
    when(basketOutPort.getBasketById(any())).thenReturn(ManageReservationUtils.mockBasketResponse());
    when(reservationOutPort.getMemos(any(), anySet())).thenReturn(new MemosResponse());

    // Act
    var response = reservationInPort.getMemos("basketReference");

    // Assert
    assertNotNull(response);
  }

  @Test
  void attachProfileToReservation_Success() {
    // Arrange
    doNothing().when(reservationOutPort).attachProfileToReservations(any());

    // Act
    reservationInPort.attachProfileToReservations(new AttachReservationProfileRequest());

    //Assert
    verify(reservationOutPort, times(1)).attachProfileToReservations(any());
  }

  @Test
  void deleteRoutingInstruction_Success() {
    // Arrange
    doNothing().when(reservationOutPort).deleteRoutingInstructions(anyString(), anySet());

    // Act
    reservationInPort.deleteRoutingInstructions("HOTEL_ID", new HashSet<>(List.of("1234")));

    //Assert
    verify(reservationOutPort, times(1)).deleteRoutingInstructions(anyString(), anySet());
  }

  private UpdateEmailReservationRequest mockEmailReservationRequest() {
    return UpdateEmailReservationRequest.builder().email("secondEmail@domain.uk").build();
  }

  private UpdateBookerEmailRequest mockUpdateBookerEmailRequest() {
    return UpdateBookerEmailRequest.builder()
        .hotelId("TESTHOTEL")
        .reservationIds(List.of("res1", "res3", "res2"))
        .emailAddress("secondEmail@domain.uk").build();
  }

  private UpdateReservationsRequest mockRequestEditRoom() {
    return mockRequestEditRoom(1);
  }

  private UpdateReservationsRequest mockRequestEditRoom(int adultsNo) {
    var request = new UpdateReservationsRequest();
    request.setReservations(List.of(
        UpdateReservationRequest.builder()
            .hotelId(HOTEL_ID)
            .reservationId("12345")
            .roomStay(UpdateRoomStayRequest.builder()
                .arrivalDate("2023-05-18")
                .departureDate("2023-05-22")
                .roomOccupancy(
                    UpdateRoomOccupancyRequest.builder().adultCount(adultsNo).childCount(0).build())
                .roomRates(List.of(UpdateRoomRateRequest.builder().roomType("DOUBLE").build()))
                .build())
            .reservationGuests(List.of(ReservationGuests.builder()
                .profileInfo(ProfileInfo.builder().profile(ProfileType.builder().customer(
                    CustomerType.builder().personName(List.of(
                        PersonNameType.builder().nameType("test").givenName("test").nameTitle("mr")
                            .surname("test").build())).build()).build()).build()).build()))
            .build()));
    request.setBookingChannel(BookingChannel.builder()
        .channel(PI_BOOKING_CHANNEL)
        .language(LANGUAGE_EN)
        .build());
    request.setToken(getMockedToken());
    return request;
  }

  private BasketResponse mockEditRoomBasket() {
    return BasketResponse.builder()
        .hotelId(HOTEL_ID)
        .bookingReference(BOOKING_REFERENCE)
        .reference(BASKET_REFERENCE)
        .createdAt(new Date().toString())
        .status(BASKET_STATUS_OPEN)
        .paymentOption(PaymentOption.PAY_ON_ARRIVAL)
        .eTag("TEST")
        .originalBasketId("AKU-48fe4074-de70-4436-99bd-760b4488903e")
        .itemTypes(Collections.emptySet())
        .items(List.of(BasketItemResponse.builder().type("reservation").sourceId("12345").build()))
        .build();
  }

  private BasketResponse mockEditRoomBasketWithOccupancySupplement() {
    return BasketResponse.builder()
        .hotelId(HOTEL_ID)
        .bookingReference(BOOKING_REFERENCE)
        .reference(BASKET_REFERENCE)
        .createdAt(new Date().toString())
        .status(BASKET_STATUS_OPEN)
        .paymentOption(PaymentOption.PAY_ON_ARRIVAL)
        .eTag("TEST")
        .originalBasketId("AKU-48fe4074-de70-4436-99bd-760b4488903e")
        .itemTypes(Collections.emptySet())
        .items(List.of(BasketItemResponse.builder().type("reservation").sourceId("12345")
            .hasOccupancySup(true).build()))
        .build();
  }

  private ReservationByBasketRefResponse mockReservationByBasketRefResponseEditRoom(
      Integer adultsNo) {
    List<RatePerNight> ratePerNights = new ArrayList<>();
    RatePerNight ratePerNight1 = RatePerNight.builder()
            .pricePerNight(new BigDecimal(100))
            .build();
    RatePerNight ratePerNight2 = RatePerNight.builder()
            .pricePerNight(new BigDecimal(120))
            .build();
    ratePerNights.add(ratePerNight1);
    ratePerNights.add(ratePerNight2);

    return ReservationByBasketRefResponse.builder()
        .hotelId(HOTEL_ID)
        .reservationByIdList(List.of(ReservationByIdResponse.builder()
            .roomStay(RoomStayByIdResponse.builder().arrivalDate("2023-05-20")
                .departureDate("2023-05-22").ratePlanCode("FLEXRATE").adultsNumber(adultsNo)
                    .ratesPerNight(ratePerNights)
                .build())
            .reservationGuestList(List.of(ReservationByIdGuestsResponse.builder()
                .givenName("test").surName("test").nameTitle("mr")
                .address(getReservationGuestAddress()).email("email").build()))
            .reservationId("12345")
            .guaranteeCode("CC")
            .build()))
        .amountPaid(new BigDecimal(1234))
        .build();
  }

  private ReservationByBasketRefResponse mockReservationByBasketRefResponseEditRoomForEmployee(
      Integer adultsNo) {
    List<RatePerNight> ratePerNights = new ArrayList<>();
    RatePerNight ratePerNight1 = RatePerNight.builder()
            .pricePerNight(new BigDecimal(100))
            .build();
    RatePerNight ratePerNight2 = RatePerNight.builder()
            .pricePerNight(new BigDecimal(120))
            .build();
    ratePerNights.add(ratePerNight1);
    ratePerNights.add(ratePerNight2);
    return ReservationByBasketRefResponse.builder()
        .hotelId(HOTEL_ID)
        .reservationByIdList(List.of(ReservationByIdResponse.builder()
            .roomStay(RoomStayByIdResponse.builder().arrivalDate("2023-05-20")
                .departureDate("2023-05-22").ratePlanCode("EMPLOYEE").adultsNumber(adultsNo)
                .ratesPerNight(ratePerNights)
                .build())
            .reservationGuestList(List.of(ReservationByIdGuestsResponse.builder()
                .givenName("test").surName("test").nameTitle("mr")
                .address(getReservationGuestAddress()).email("email").build()))
            .reservationId("12345")
            .guaranteeCode("CC")
            .build()))
        .amountPaid(new BigDecimal(1234))
        .build();
  }

  private GuestAddress getReservationGuestAddress() {
    return GuestAddress.builder()
        .addressLine1("First line")
        .cityName("Big City")
        .postalCode("PO5 TA1")
        .build();
  }

  private UpdateReasonForStayRequest createUpdateReasonForStayRequest() {
    return UpdateReasonForStayRequest.builder()
        .hotelId("TestId")
        .reasonForStay("LEI")
        .basketReference(BASKET_REFERENCE)
        .arrivalDate("2025-08-10")
        .build();
  }

  private ReservationBooker mockBooker() {
    return ReservationBooker.builder()
            .title("Mr")
            .firstName("Adam")
            .lastName("Smith")
            .mobile("0712345678")
            .address(mockAddress())
            .build();
  }

  private ReservationBookerAddress mockAddress() {
    return ReservationBookerAddress.builder()
            .postalCode("MZC AD")
            .addressType("HOME")
            .addressLine1("4 Brockley Avenue")
            .countryCode("UK")
            .cityName("London")
            .companyName("Company")
            .build();
  }

  private CompanyQuestionAndAnswerDetailsRequest createCompanyQuestionAndAnswerDetailsRequest() {
    return CompanyQuestionAndAnswerDetailsRequest.builder()
        .hotelId("TestHotelId")
        .reservationIds(Set.of("123456"))
        .build();
  }


  private UpdateDiscountRequest createUpdateDiscountRequest() {
    return UpdateDiscountRequest.builder()
        .discountAmount(BigDecimal.valueOf(10L))
        .currency("USD")
        .hotelId("TestHotelId")
        .reservationIds(List.of("123456", "1234578"))
        .build();
  }

  private BusinessItemsRequest createBusinessItemsRequest() {
    return BusinessItemsRequest.builder()
        .reservationIds(List.of("123456", "123457"))
        .hotelId("HOTELID")
        .businessItems(BusinessItems.builder()
            .purchaseOrderNumber("10101010")
            .customReferenceNumber("11010101")
            .businessAllowances(List.of(BusinessAllowance.builder()
                .allowance("ALLOWANCE")
                .budget(BigDecimal.ZERO)
                .isAuthorised(Boolean.TRUE)
                .build()))
            .build())
        .build();
  }

  private UpdateReservationOverrideReasonsRequest createUpdateReservationOverrideReasonsRequest() {
    return UpdateReservationOverrideReasonsRequest.builder()
            .hotelId("HOTElCODE")
            .basketReference("GBM6919649")
            .reasonCode("ILL")
            .reasonName(REASON_NAME)
            .callerName(CALLER_NAME)
            .managerName(MANAGER_NAME)
            .build();
  }

  private UpdateReservationCcAgentIdRequest createUpdateReservationCcAgentIdRequest() {
    return UpdateReservationCcAgentIdRequest.builder()
        .hotelId("HOTElCODE")
        .ccAgentId("jane.doe@wb.com")
        .build();
  }

  private UpdateRequest mockUpdateRequest() {
    var ratePlanChange = new UpdateRequest();
    ratePlanChange.setHotelId("HOTELCODE");
    ratePlanChange.setReservationIds(Collections.singletonList("100100"));
    ratePlanChange.setBasketReferenceId("HOTELCODE1001001");
    ratePlanChange.setStartDate("2022-03-01");
    ratePlanChange.setEndDate("2022-03-03");
    ratePlanChange.setCurrency("GBP");
    ratePlanChange.setRateCode("FLEXRATE");
    ratePlanChange.setRoomTypes(Collections.singletonList("DOUBLE"));
    ratePlanChange.setAdultsNumber(Collections.singletonList(1));
    ratePlanChange.setChildrenNumber(Collections.singletonList(0));

    return ratePlanChange;
  }

  private BasketResponse mockBasketResponsePN(String channel, Boolean isCheckInOnline) {
    return BasketResponse.builder()
        .hotelId(HOTEL_ID)
        .bookingReference(BOOKING_REFERENCE)
        .reference(BASKET_REFERENCE)
        .channel(channel)
        .createdAt(new Date().toString())
        .status(BASKET_STATUS_OPEN)
        .itemTypes(Collections.emptySet())
        .items(mockBasketItemsList())
        .paymentID("118932")
        .paymentOption(PAY_NOW)
        .originalBasketId("HOTELCODE1001001")
        .eTag("123")
        .isCheckInOnlinePay(isCheckInOnline)
        .build();
  }

  private BasketResponse mockBasketResponsePN(String channel) {
    return mockBasketResponsePN(channel, null);
  }

  private BasketResponse mockBasketResponsePN() {
    return mockBasketResponsePN(null, null);
  }

  private BasketResponse mockBasketSingleReservationResponse() {
    return BasketResponse.builder()
        .hotelId("TESTHOTEL")
        .bookingReference("1234567")
        .reference("TestId1234567")
        .createdAt(new Date().toString())
        .status(BASKET_STATUS_OPEN)
        .itemTypes(Collections.emptySet())
        .items(mockBasketSingleItemList())
        .paymentID("118932")
        .paymentOption(PaymentOption.PAY_ON_ARRIVAL)
        .eTag("1681976048000")
        .originalBasketId("HOTELCODE1001001")
        .build();
  }

  private BasketResponse mockCreateNoItemsBasketResponse() {
    return BasketResponse.builder()
        .hotelId(HOTEL_ID)
        .bookingReference(BOOKING_REFERENCE)
        .reference(BASKET_REFERENCE)
        .createdAt(new Date().toString())
        .status("COMPLETED")
        .eTag("TEST")
        .itemTypes(Collections.emptySet())
        .items(Collections.emptyList())
        .build();
  }

  private List<BasketItemResponse> mockBasketSingleItemList() {
    List<BasketItemResponse> itemResponseList = new LinkedList<>();
    itemResponseList.add(BasketItemResponse.builder().type("reservation").sourceId("res1").build());
    return itemResponseList;
  }



  private BasketResponse mockBasketForCancellationPoliciesResponse() {
    return BasketResponse.builder()
        .hotelId("HOTELTEST")
        .bookingReference("14hh7")
        .reference("basketCancellation")
        .createdAt(new Date().toString())
        .status(BASKET_STATUS_OPEN)
        .itemTypes(Collections.singleton("147"))
        .items(mockBasketItemsListForCancellationPolicies())
        .build();
  }


  private BasketResponse mockBasketForCancelOnHoldResponse() {
    return BasketResponse.builder()
        .hotelId("TestHotelId")
        .bookingReference(BOOKING_REFERENCE)
        .reference(BASKET_REFERENCE)
        .createdAt(new Date().toString())
        .status(BASKET_STATUS_OPEN)
        .itemTypes(Collections.emptySet())
        .items(ManageReservationUtils.mockBasketItemsListForCancel())
        .build();
  }

  private ReservationByBasketRefResponse mockReservationByBasketRefOnHoldResponse() {
    return ReservationByBasketRefResponse.builder()
        .amountPaid(BigDecimal.TEN)
        .totalCost(BigDecimal.TEN)
        .newTotal(BigDecimal.TEN)
        .previousTotal(BigDecimal.TEN)
        .balanceOutstanding(BigDecimal.TEN)
        .hotelId(HOTEL_ID)
        .policyCode(POLICY_CODE)
        .currencyCode("GBP")
        .reservationByIdList(Collections.singletonList(mockReservationByIdOnHoldRes()))
        .build();
  }

  private ReservationByIdResponse mockReservationByIdOnHoldRes() {
    return ReservationByIdResponse.builder()
        .onHold(true)
        .reservationStatus("NotCancelled")
        .build();
  }

  private List<BasketItemResponse> mockBasketItemsListForCancellationPolicies() {
    List<BasketItemResponse> itemResponseList = new LinkedList<>();
    itemResponseList.add(
        BasketItemResponse.builder().type("reservation").sourceId("147").build());
    return itemResponseList;
  }

  private OhipReservationResponse mockReservationResponse() {
    return mockOhipReservationResponse();
  }

  private ReservationsDetailsResponse mockReservationsDetailsResponse() {
    return new ReservationsDetailsResponse();
  }

  private ReservationByBasketRefResponse mockReservationByBasketRefResponseWithCityTax() {
    return ReservationByBasketRefResponse.builder()
        .amountPaid(BigDecimal.TEN)
        .totalCost(BigDecimal.TEN)
        .newTotal(BigDecimal.TEN)
        .previousTotal(BigDecimal.TEN)
        .balanceOutstanding(BigDecimal.TEN)
        .hotelId(HOTEL_ID)
        .policyCode(POLICY_CODE)
        .currencyCode("GBP")
        .reservationByIdList(
            List.of(ManageReservationUtils.mockReservationByIdWithCityTax(),
                    ManageReservationUtils.mockReservationByIdWithCityTax(),
                ManageReservationUtils.mockReservationByIdWithCityTax()))
        .build();
  }

  private ReservationByBasketRefResponse mockReservationByBasketRefCancellationResponse() {
    return ReservationByBasketRefResponse.builder()
        .amountPaid(BigDecimal.TEN)
        .totalCost(BigDecimal.TEN)
        .newTotal(BigDecimal.TEN)
        .previousTotal(BigDecimal.TEN)
        .balanceOutstanding(BigDecimal.TEN)
        .hotelId("HOTELTEST")
        .currencyCode("GBP")
        .reservationByIdList(Collections.singletonList(ManageReservationUtils.mockReservationByIdFlex("11111111")))
        .build();
  }

  private List<OhipReservationCreationResponse> mockReservationCreationResponse() {
    return Collections.singletonList(OhipReservationCreationResponse.builder()
        .reservationId("1234")
        .createDateTime("2022-06-19")
        .roomStay(mockRoomStay2())
        .build());
  }

  private RoomStay mockRoomStay2() {
    return RoomStay.builder()
        .arrivalDate(LocalDate.of(2022, 5, 5))
        .departureDate(LocalDate.of(2022, 5, 7))
        .adultCount(2)
        .childCount(0)
        .ratePlanCode("AXWS")
        .roomType("SINGLE")
        .build();
  }

  private OhipReservationResponse mockOhipReservationResponse() {
    return OhipReservationResponse.builder()
        .reservations(mockReservationCreationResponse())
        .totalCost(BigDecimal.ONE)
        .build();
  }

  private ReservationRequest createValidEmployeeHotelReservationRequest() {

    RoomRate roomRate = RoomRate.builder()
        .startDate("2015-10-20")
        .endDate("2015-10-21")
        .pmsRoomType("SDB")
        .ratePlanCode("EMPLOYEE")
        .cellCode("ABC")
        .build();

    Reservation res1 = Reservation.builder()
        .hotelId("HOTELTEST")
        .arrival("2015-10-20")
        .departure("2015-10-21")
        .adultsNumber(1)
        .childrenNumber(0)
        .roomRates(roomRate)
        .bookingNotes("bookingNotes")
        .gdsReferenceNumber("gdsReferenceNumber")
        .distributionUsername("distributionUsername")
        .distributionIATANumber("12345678")
        .bookingType(BOOKING_TYPE_ANON)
        .build();

    var bookingChannel = new BookingChannel();
    bookingChannel.setChannel(PI_BOOKING_CHANNEL);
    bookingChannel.setSubchannel(SUBCHANNEL_WEB);
    bookingChannel.setChannel("MOBILE");
    bookingChannel.setLanguage(LANGUAGE_EN);

    return ReservationRequest.builder()
        .reservations(List.of(res1))
        .bookingChannel(bookingChannel)
        .token(getMockedToken())
        .build();
  }

  private ReservationRequest createValidHotelReservationRequest() {

    RoomRate roomRate = RoomRate.builder()
        .startDate("2015-10-20")
        .endDate("2015-10-21")
        .pmsRoomType("SDB")
        .ratePlanCode("DAILY")
        .cellCode("ABC")
        .build();

    Reservation res1 = Reservation.builder()
        .hotelId("HOTELTEST")
        .arrival("2015-10-20")
        .departure("2015-10-21")
        .adultsNumber(1)
        .childrenNumber(0)
        .roomRates(roomRate)
        .bookingNotes("bookingNotes")
        .gdsReferenceNumber("gdsReferenceNumber")
        .distributionUsername("distributionUsername")
        .distributionIATANumber("12345678")
        .bookingType(BOOKING_TYPE_ANON)
        .build();

    var bookingChannel = new BookingChannel();
    bookingChannel.setChannel(PI_BOOKING_CHANNEL);
    bookingChannel.setSubchannel(SUBCHANNEL_WEB);
    bookingChannel.setChannel("PI");
    bookingChannel.setLanguage(LANGUAGE_EN);

    return ReservationRequest.builder()
        .reservations(List.of(res1))
        .bookingChannel(bookingChannel)
        .token(getMockedToken())
        .build();
  }

  private ReservationRequest createValidHotelReservationRequestForCityTax(String channel) {

    RoomRate roomRate = RoomRate.builder()
        .startDate(LocalDate.now().plusDays(2).toString())
        .endDate(LocalDate.now().plusDays(3).toString())
        .pmsRoomType("SDB")
        .ratePlanCode("DAILY")
        .cellCode("ABC")
        .build();

    Reservation res1 = Reservation.builder()
        .hotelId("HOTELTEST")
        .arrival(LocalDate.now().plusDays(2).toString())
        .departure(LocalDate.now().plusDays(3).toString())
        .adultsNumber(1)
        .childrenNumber(0)
        .roomRates(roomRate)
        .bookingNotes("bookingNotes")
        .gdsReferenceNumber("gdsReferenceNumber")
        .distributionUsername("distributionUsername")
        .distributionIATANumber("12345678")
        .bookingType(BOOKING_TYPE_ANON)
        .build();

    var bookingChannel = new BookingChannel();
    bookingChannel.setChannel(PI_BOOKING_CHANNEL);
    bookingChannel.setSubchannel(SUBCHANNEL_WEB);
    bookingChannel.setChannel(channel);
    bookingChannel.setLanguage(LANGUAGE_EN);

    return ReservationRequest.builder()
        .reservations(List.of(res1))
        .bookingChannel(bookingChannel)
        .token(getMockedToken())
        .build();
  }

  private HotelAvailabilityByIdsV2 createValidAvailabilityV2Response() {

    PriceInfo priceInfo = PriceInfo.builder()
        .amountAfterTax(BigDecimal.valueOf(70))
        .amountBeforeTax(BigDecimal.valueOf(65))
        .stayDate(LocalDate.now())
        .build();

    RoomRateV2 roomRateV2 = RoomRateV2.builder()
        .ratePlanCode("DAILY")
        .roomRateInfo(RoomRateInfoV2.builder()
            .priceInfo(List.of(priceInfo))
            .packages(Collections.emptyList())
            .build())
        .build();

    RoomTypeV2 roomTypeV2 = RoomTypeV2.builder()
        .adults("1")
        .tag("SDB")
        .numberOfRooms("1")
        .roomRates(List.of(roomRateV2))
        .build();

    uk.co.whitbread.reservation.domain.model.availability.out.RoomStay roomStay =
        uk.co.whitbread.reservation.domain.model.availability.out.RoomStay.builder()
            .roomClass("ST")
            .roomTypes(List.of(roomTypeV2))
            .build();

    HotelAvailabilityResultV2 availabilityResultV2 = HotelAvailabilityResultV2.builder()
        .hotelId("HOTELTEST")
        .roomStays(List.of(roomStay))
        .build();
    return HotelAvailabilityByIdsV2.builder()
        .hotelAvailability(List.of(availabilityResultV2))
        .build();
  }

  private ChannelRuleResponse mockPIChannelRuleResponse() {
    return ChannelRuleResponse.builder()
        .requestDetails(ChannelRuleRequestDetails.builder().channel(PI_BOOKING_CHANNEL).build())
        .build();
  }

  private ChannelRuleResponse mockCCUIChannelRuleResponse() {
    return ChannelRuleResponse.builder()
        .requestDetails(ChannelRuleRequestDetails.builder().channel(CCUI_BOOKING_CHANNEL).build())
        .build();
  }

  private ReservationGuestRequest createValidReservationGuestRequest() {

    final BookerAddress address = BookerAddress.builder()
        .postalCode("MZC AD")
        .addressType("HOME")
        .addressLine1("4 Brockley Avenue")
        .countryCode("UK")
        .cityName("London")
        .companyName("company")
        .build();
    final StayingGuestAddress stayingGuestAddress = StayingGuestAddress.builder()
        .postalCode("MZC AD")
        .addressType("HOME")
        .addressLine1("4 Brockley Avenue")
        .countryCode("UK")
        .cityName("London")
        .companyName("company")
        .build();

    final BookerDetails booker = BookerDetails.builder()
        .title("Mrs")
        .firstName("John")
        .lastName("McEnroe")
        .emailAddress("john.mcenroe@mail.com")
        .acceptFutureMailing(Boolean.FALSE)
        .mobile("+39567463783")
        .address(address)
        .build();

    final StayingGuestDetails stayingGuest = StayingGuestDetails.builder()
        .title("Mrs")
        .firstName("Debbie")
        .lastName("Doe")
        .address(stayingGuestAddress)
        .build();

    final StayingGuest stayingGuests = StayingGuest.builder()
        .sameAsBooker(false)
        .stayingGuestDetails(stayingGuest)
        .build();

    return ReservationGuestRequest.builder()
        .booker(booker)
        .stayingGuests(List.of(stayingGuests))
        .sendEmailConfirmation(true)
        .sendEmailInvoice(true)
        .build();
  }

  private ReservationPackagesRequest createSavePackagesRequest() {
    ReservationPackagesRequest reservationPackagesRequest = new ReservationPackagesRequest();

    reservationPackagesRequest.setBasketReference("TestId1234567");
    reservationPackagesRequest.setHotelId("HOTELCODE");
    reservationPackagesRequest.setArrival("2022-04-02");
    reservationPackagesRequest.setDeparture("2022-04-03");

    reservationPackagesRequest.setRoomsSelections(
        Collections.singletonList(createRoomsSelections()));
    return reservationPackagesRequest;
  }

  private PackagesSelection createPackagesSelection() {
    PackagesSelection packagesSelection = new PackagesSelection();

    packagesSelection.setNoSelections(1);
    packagesSelection.setId("MDP");

    return packagesSelection;
  }

  private RoomsSelections createRoomsSelections() {
    RoomsSelections roomsSelections = new RoomsSelections();

    roomsSelections.setPackagesSelection(Collections.singletonList(createPackagesSelection()));

    return roomsSelections;
  }

  private ReservationsPackagesResponse mockReservationsPackagesResponse() {
    return ReservationsPackagesResponse.builder()
        .roomsSelections(Collections.singletonList(
            uk.co.whitbread.reservation.domain.model.out.RoomsSelectionsByReservation.builder()
                .packagesSelection(Collections.singletonList(
                    uk.co.whitbread.reservation.domain.model.out.PackagesSelection.builder()
                        .noOfSelections(1).id("MTEST").build())).build())).build();

  }

  private ReservationsPackagesResponse mockReservationsPackagesWithCharityResponse() {
    List<uk.co.whitbread.reservation.domain.model.out.PackagesSelection> packagesSelection = new ArrayList<>();
    var zchryPackage = uk.co.whitbread.reservation.domain.model.out.PackagesSelection
        .builder()
        .noOfSelections(1)
        .id("ZCHRY1")
        .build();
    var chrtyPackage = uk.co.whitbread.reservation.domain.model.out.PackagesSelection
        .builder()
        .noOfSelections(1)
        .id("CHRTY")
        .build();
    packagesSelection.add(zchryPackage);
    packagesSelection.add(chrtyPackage);
    return ReservationsPackagesResponse.builder()
        .roomsSelections(Collections.singletonList(
            uk.co.whitbread.reservation.domain.model.out.RoomsSelectionsByReservation
                .builder()
                .reservationId("12345")
                .packagesSelection(packagesSelection)
                .build()))
        .build();

  }

  private ReservationsPackagesResponse mockReservationsPackagesWithEmptySelectionsResponse() {
    return ReservationsPackagesResponse.builder()
        .roomsSelections(new ArrayList<>())
        .build();
  }

  private CancelReservationResponse mockCancelOnHoldReservationResponse() {
    return CancelReservationResponse.builder()
        .basketReference(BASKET_REF_ONHOLD_RES)
        .build();
  }

  private ChannelRuleResponse mockChannelRuleResponse() {
    return ChannelRuleResponse.builder()
        .requestDetails(ChannelRuleRequestDetails.builder().channel(CCUI_BOOKING_CHANNEL).build())
        .build();
  }

  private void assertReservationByBasketRefResponse(ReservationByBasketRefResponse response) {
    var reservationGuest = response.getReservationByIdList().get(0).getReservationGuestList()
        .get(0);
    var roomStay = response.getReservationByIdList().get(0).getRoomStay();
    var depositPolicies = response.getReservationByIdList().get(0).getDepositPolicies().get(0);
    var paymentCard = response.getReservationByIdList().get(0).getPaymentCard();
    var reservationOverrideReasons =
        response.getReservationByIdList().get(0).getReservationOverrideReasons();

    //Assert
    assertNotNull(response);
    assertEquals(GIVEN_NAME, reservationGuest.getGivenName());
    assertEquals(SURNAME, reservationGuest.getSurName());

    assertEquals(ARRIVAL_DATE, roomStay.getArrivalDate());
    assertEquals(DEPARTURE_DATE, roomStay.getDepartureDate());
    assertEquals(2, roomStay.getAdultsNumber());
    assertEquals(0, roomStay.getChildrenNumber());
    assertEquals(RATE_PLAN_CODE, roomStay.getRatePlanCode());
    assertEquals(ROOM_TYPE, roomStay.getRoomType());
    assertEquals(CELL_CODE, roomStay.getCellCode());
    assertEquals(ROOM_NUMBER, roomStay.getRoomNumber());
    assertEquals(BOOKING_CHANNEL, roomStay.getBookingChannel());

    assertEquals("1", depositPolicies.getAmountDue().getCurrencyCode());
    assertEquals(BigDecimal.ONE, depositPolicies.getAmountDue().getAmount());
    assertEquals("1", depositPolicies.getAmountPaid().getCurrencyCode());
    assertEquals(BigDecimal.ONE, depositPolicies.getAmountPaid().getAmount());
    assertEquals(POLICY_CODE, depositPolicies.getPolicyCode());

    assertEquals(BigDecimal.TEN, response.getBalanceOutstanding());
    assertEquals(BigDecimal.TEN, response.getNewTotal());
    assertEquals(BigDecimal.TEN, response.getPreviousTotal());
    assertEquals(BigDecimal.TEN, response.getTotalCost());
    assertEquals(HOTEL_ID, response.getHotelId());
    assertEquals(POLICY_CODE, response.getPolicyCode());

    assertEquals(CARD_NUMBER, paymentCard.getCardNumberMasked());
    assertEquals(CARD_TOKEN, paymentCard.getToken());

    assertEquals(REASON_NAME, reservationOverrideReasons.getReasonName());
    assertEquals(CALLER_NAME, reservationOverrideReasons.getCallerName());
    assertEquals(MANAGER_NAME, reservationOverrideReasons.getManagerName());

    assertTrue(response.getReservationByIdList().get(0).isReservationOverridden());
    assertEquals(BigDecimal.valueOf(60),
        response.getReservationByIdList().get(0).getBalanceAmount());
    assertEquals(CARD_NUMBER, paymentCard.getCardNumberMasked());
    assertEquals(CARD_TOKEN, paymentCard.getToken());
    assertEquals(CARD_TYPE, paymentCard.getCardType());
  }

  private String getMockedToken() {
    return "32RQfSN6U9YLgjv+7NaYg4qyiXQs7O3hak/ZoA+hCL2V3M8tiFoi6wXBCBbKS2Qx5WBJuP8=";
  }

  private ReservationsPackagesResponse getMockedReservationsPackagesResponseTempBasket() {
    var packagesSelection = uk.co.whitbread.reservation.domain.model.out.PackagesSelection.builder()
        .id("ZCHRY3")
        .noOfSelections(1)
        .packageGroup(PACKAGE_GROUP)
        .build();

    var roomSelection1 = RoomsSelectionsByReservation.builder()
        .reservationId(SOURCE_ID_111111)
        .packagesSelection(List.of(packagesSelection))
        .build();

    var roomSelection2 = RoomsSelectionsByReservation.builder()
        .reservationId(SOURCE_ID_222222)
        .packagesSelection(List.of(packagesSelection))
        .build();

    var roomSelection3 = RoomsSelectionsByReservation.builder()
        .reservationId(SOURCE_ID_333333)
        .packagesSelection(List.of(packagesSelection))
        .build();

    return ReservationsPackagesResponse.builder()
        .roomsSelections(List.of(roomSelection1, roomSelection2, roomSelection3))
        .build();
  }

  private ReservationsPackagesResponse mockReservationPackagesResponse() {
    return ReservationsPackagesResponse.builder()
        .roomsSelections(List.of(RoomsSelectionsByReservation.builder()
            .reservationId(SOURCE_ID_222222)
            .build()))
        .build();
  }

  private ConfirmReservationRequest createConfirmReservationRequest() {
    return ConfirmReservationRequest.builder()
        .hotelId("TEST")
        .reservationId("123")
        .paymentOption(PAY_NOW)
        .build();
  }

  private static Account mockAccount(String customerAccountId) {
    return Account.builder()
        .customerId(customerAccountId)
        .email(EMAIL)
        .build();
  }

  @Test
  void testConfirmReservationCaseOne() {
    //CASE1:When confirmReservtion is for different paymentOpotion.
    // Arrange
    var requestNotPayNow = ConfirmReservationRequest.builder()
            .hotelId("TESTHOTEL")
            .reservationId("tempRes1")
            .paymentOption(PAY_ON_ARRIVAL)
            .build();
    when(reservationOutPort.confirmReservation(eq(requestNotPayNow)))
            .thenReturn(new ConfirmReservationResponse());

    // Act
    var response = reservationInPort.confirmReservation(requestNotPayNow, Optional.empty());
    // Assert
    assertThat(response, notNullValue());

  }

  @Test
  void testConfirmReservationCaseTwo() {
    //CASE2:When confirmReservtion is for PAY_NOW paymentOpotion.

    var confirmResReq = ConfirmReservationRequest.builder()
            .hotelId("TESTHOTEL")
            .reservationId("tempRes1").paymentOption(PAY_NOW).build();

    doReturn(mockReservationByBasRes()).when(reservationOutPort) //ref
            .getReservationsByIds(anyString(), anyList(), anyBoolean());
    when(basketOutPort.getBasketByReference(anyString()))
        .thenReturn(Optional.ofNullable(mockBasketResPayOnArrival()));

    //Act
    var response = reservationInPort.confirmReservation(confirmResReq, Optional.empty());

    assertNotNull(response);
    assertThat(response.isPartialPaid(), is(true));
  }

  private BasketResponse mockBasketResPayOnArrival() {
    return BasketResponse.builder()
        .hotelId("TESTHOTEL")
        .paymentOption(PAY_ON_ARRIVAL)
        .bookingReference("12223")
        .build();
  }

  @Test
  void testConfirmReservationCase3() {
    //CASE 3: WHEN PaymentOption.PAY_NOW.equals(basket.getPaymentOption()))
    //Arrange
    var confirmResReq = ConfirmReservationRequest.builder()
            .hotelId("TESTHOTEL")
            .reservationId("tempRes1").paymentOption(PAY_NOW).build();

    doReturn(mockReservationByBasRes()).when(reservationOutPort)
            .getReservationsByIds(anyString(), anyList(), anyBoolean());
    doNothing().when(basketOutPort).saveCharges(any());
    when(basketOutPort.getBasketByReference(anyString()))
        .thenReturn(Optional.ofNullable(mockBasketResPayNow()));
    doReturn(mockDepositFolioDB()).when(basketOutPort).getCharges(anyString());
    when(reservationOutPort.getGeneratedDepositFolios(anyString(), anySet())).thenReturn(mockDF());
    when(amendPayNowLogic.consolidateDepositFolios(mockDepositFolioDB()))
            .thenReturn(mockDepositFolioConsolidated());

    //Act
    var response = reservationInPort.confirmReservation(confirmResReq, Optional.empty());
    assertThat(response, notNullValue());
    //Assert
    verify(reservationOutPort, times(1))
            .getReservationsByIds(anyString(), anyList(), anyBoolean());
    verify(basketOutPort, times(1)).getCharges(anyString());
    verify(reservationOutPort, times(1))
            .getGeneratedDepositFolios(anyString(), anySet());
    verify(reservationOutPort).saveCharges(any(DepositFoliosResponse.class));
    verify(basketOutPort).saveCharges(any(DepositFoliosResponse.class));
  }

  private BasketResponse mockBasketResPayNow() {
    String basketReference = TEMPORARY_BASKET_REF;
    return BasketResponse.builder()
            .hotelId("TESTHOTEL")
            .paymentOption(PAY_NOW)
            .bookingReference(basketReference)
            .build();
  }

  @Test
  void testConfirmReservationCase4() {
    //Case 4: When reservationDetails.getBookingReference() != null: return Get and Save charges
    var confirmResReq = ConfirmReservationRequest.builder()
        .hotelId("TESTHOTEL")
        .reservationId("tempRes1").paymentOption(PAY_NOW).build();
    var objectBrEmpty = mockReservationByBasRes();
    objectBrEmpty.setBookingReference(null);
    doReturn(objectBrEmpty).when(reservationOutPort)
        .getReservationsByIds(anyString(), anyList(), anyBoolean());
    //Act
    var response = reservationInPort.confirmReservation(confirmResReq, Optional.empty());
    verify(reservationOutPort, times(1))
        .getReservationsByIds(anyString(), anyList(), anyBoolean());

  }

  @Test
  void testConfirmReservationCase4_depositFoliosResponseNotEmpty() {
    //Case 4: When reservationDetails.getBookingReference() != null: return Get and Save charges
    DepositFolio depositFolio1 = DepositFolio.builder()
        .reservationId("tempRes1")
        .charges(List.of(
            DepositFolioCharge.builder().transactionCode("9012").quantity(1).reference("2024-10-03")
                .build()))
        .build();
    DepositFolio depositFolio2 = DepositFolio.builder()
        .reservationId("tempRes2")
        .charges(List.of(
            DepositFolioCharge.builder().transactionCode("100").quantity(1).reference("2024-10-05")
                .build()))
        .build();
    DepositFolio depositFolio3 = DepositFolio.builder()
        .reservationId("tempRes3")
        .charges(List.of(
            DepositFolioCharge.builder().transactionCode("400").quantity(1).reference("2024-10-05")
                .build()))
        .build();

    DepositFoliosResponse depositFoliosResponse = DepositFoliosResponse.builder()
        .depositFolios(List.of(depositFolio1, depositFolio2, depositFolio3))
        .build();

    Optional<DepositFoliosResponse> optionalDepositFoliosResponse = Optional.of(
        depositFoliosResponse);

    var confirmResReq = ConfirmReservationRequest.builder()
        .hotelId("TESTHOTEL")
        .reservationId("tempRes1").paymentOption(PAY_NOW).build();
    var objectBrEmpty = mockReservationByBasRes();
    objectBrEmpty.setBookingReference(null);
    doReturn(objectBrEmpty).when(reservationOutPort)
        .getReservationsByIds(anyString(), anyList(), anyBoolean());
    //Act
    reservationInPort.confirmReservation(confirmResReq,
        optionalDepositFoliosResponse);
    verify(reservationOutPort, times(1))
        .getReservationsByIds(anyString(), anyList(), anyBoolean());

  }

  @Test
  void testConfirmReservationCase5() {
    //Case 4: When reservationDetails.getBookingReference() != null: return Get and Save charges
    var confirmResReq = ConfirmReservationRequest.builder()
        .hotelId("TESTHOTEL")
        .reservationId("tempRes1").paymentOption(PAY_NOW).build();
    var objectBrEmptyVal = mockReservationByBasRes();
    objectBrEmptyVal.setBalanceOutstanding(BigDecimal.ZERO);
    objectBrEmptyVal.setAmountPaid(BigDecimal.ZERO);
    doReturn(objectBrEmptyVal).when(reservationOutPort)
        .getReservationsByIds(anyString(), anyList(), eq(false));
    //Act
    var response = reservationInPort.confirmReservation(confirmResReq, Optional.empty());
    verify(reservationOutPort, times(1))
        .getReservationsByIds(anyString(), anyList(), eq(false));

  }

  private ReservationByBasketRefResponse mockReservationByBasRes() {
    String basketReference = TEMPORARY_BASKET_REF;
    ReservationByIdResponse reservationByIdResponse =
            ReservationByIdResponse
                    .builder()
                    .reservationId("tempRes1")
                    .roomStay(RoomStayByIdResponse.builder()
                            .arrivalDate("2023-01-01")
                            .departureDate("2023-08-08")
                            .build())
                    .reservationGuestList(
                            List.of(ReservationByIdGuestsResponse.builder()
                                    .givenName("TESTGIVEN")
                                    .surName("TESTSUR")
                                    .build()))
                    .reservationStatus("AMEND")
                    .build();

    return ReservationByBasketRefResponse.builder()
        .hotelId("TESTHOTEL")
        .balanceOutstanding(BigDecimal.valueOf(20))
        .amountPaid(BigDecimal.valueOf(10))
        .reservationByIdList(List.of(reservationByIdResponse))
        .basketReference(basketReference)
        .bookingReference("123456")
        .build();
  }

  private DepositFoliosResponse mockDepositFolioDB() {
    var dbDepositFolios = DepositFolio.builder()
            .hotelId("TESTHOTEL")
            .reservationId("tempRes1")
            .vatRegion("UK")
            .charges(List.of(
                    DepositFolioCharge.builder()
                            .transactionCode("9026")
                            .quantity(1)
                            .reference("2023-12-08")
                            .currencyAmount(CurrencyAmount.builder()
                                    .amount(BigDecimal.valueOf(11.99))
                                    .currencyCode("GBP")
                                    .build())
                            .build(),
                    DepositFolioCharge.builder()
                            .transactionCode("9016")
                            .quantity(1)
                            .reference("2023-12-08")
                            .currencyAmount(CurrencyAmount.builder()
                                    .amount(BigDecimal.valueOf(999))
                                    .currencyCode("GBP")
                                    .build())
                            .build(),
                    DepositFolioCharge.builder()
                            .transactionCode("9026")
                            .quantity(1)
                            .reference("2023-12-08")
                            .currencyAmount(CurrencyAmount.builder()
                                    .amount(BigDecimal.valueOf(-11.99))
                                    .currencyCode("GBP")
                                    .build())
                            .build()
            ))
            .build();

    return DepositFoliosResponse.builder()
            .depositFolios(List.of(dbDepositFolios)).build();
  }

  private DepositFoliosResponse mockDF() {
    var generatedDepositFolios = DepositFolio.builder()
            .reservationId("tempRes1")
            .hotelId("TESTHOTEL")
            .vatRegion("UK")
            .charges(List.of(
                    DepositFolioCharge.builder()
                            .transactionCode("9026")
                            .quantity(1)
                            .reference("2023-12-08")
                            .currencyAmount(CurrencyAmount.builder()
                                    .amount(BigDecimal.valueOf(9.99))
                                    .currencyCode("GBP")
                                    .build()).build(),
                    DepositFolioCharge.builder()
                            .transactionCode("9016")
                            .quantity(1)
                            .reference("2023-12-08")
                            .currencyAmount(CurrencyAmount.builder()
                                    .amount(BigDecimal.valueOf(999))
                                    .currencyCode("GBP")
                                    .build())
                            .build(),
                DepositFolioCharge.builder()
                    .transactionCode("9052")
                    .quantity(1)
                    .reference("2023-12-08")
                    .currencyAmount(CurrencyAmount.builder()
                        .amount(BigDecimal.valueOf(999))
                        .currencyCode("GBP")
                        .build())
                    .build()))
            .build();
    return DepositFoliosResponse.builder()
            .depositFolios(List.of(generatedDepositFolios)).build();
  }

  @Test
  void testUpdateReservationSingleCall_Success() {
    // Arrange
    when(reservationOutPort.updateReservation(any()))
        .thenReturn(new ConfirmReservationResponse());
    FeatureFlag mockedFeatureFlag = mock(FeatureFlag.class);
    when(unleashWrapper.featureFlag()).thenReturn(mockedFeatureFlag);
    when(unleashWrapper.isEnabled(mockedFeatureFlag.getReleasePiCcuiCityTaxUk()))
        .thenReturn(Boolean.TRUE);
    when(contentOutPort.getHotelInformation(any(), any(), any()))
        .thenReturn(HotelInfoResponse.builder().brand(PI_BOOKING_CHANNEL).build());

    var request = UpdateReservationSingleCallRequest.builder()
        .reservationGuestDetails(ReservationGuestRequest.builder().hotelId("LONEUS").reasonForStay("LEI").build())
        .reservationPackages(ReservationPackagesRequest.builder().arrival("2025-12-08").build())
        .build();
    // Act
    var response = reservationInPort.updateReservation(request);
    // Assert
    assertThat(response, notNullValue());
  }

  @Test
  void testUpdateReservationSingleCall_ThrowException() {
    // Arrange
    when(reservationOutPort.updateReservation(any()))
        .thenThrow(new HotelReservationOhipException("message",
            "An error was returned by OHIP Adapter!", new Exception(), 900));
    // Act
    Exception exception = assertThrows(HotelReservationOhipException.class,
        () -> reservationOutPort.updateReservation(any()));

    // Assert
    assertThat(exception, notNullValue());
    assertThat(exception.getMessage(), is("An error was returned by OHIP Adapter!"));
  }

  @Test
  void testAddAttachmentToReservation_Success() {
    // Arrange
    PreCheckInResponse expectedResponse = new PreCheckInResponse();
    expectedResponse.setStatus("Success");

    when(reservationOutPort.addAttachmentToReservation(any()))
        .thenReturn(expectedResponse);

    // Act
    PreCheckInResponse response = reservationInPort.addAttachmentToReservation(
        mockReservationFileAttachmentRequest());

    // Assert
    assertNotNull(response);
  }

  @Test
  void testAddAttachmentToReservation_ThrowException() {
    // Arrange
    HotelReservationOhipException addAttachmentException = new HotelReservationOhipException(
        "message",
        "An error was returned by OHIP Adapter!", new Exception(), 900);

    when(reservationOutPort.addAttachmentToReservation(any())).thenThrow(addAttachmentException);

    // Act
    ReservationFileAttachmentRequest request = mockReservationFileAttachmentRequest();
    HotelReservationOhipException thrownException = assertThrows(HotelReservationOhipException.class,
        () -> reservationInPort.addAttachmentToReservation(request));

    // Assert
    assertNotNull(thrownException);
    assertThat(thrownException.getMessage(), is("An error was returned by OHIP Adapter!"));
  }

  @Test
  void testSaveReservationPreCheckIn_Success() {
    // Arrange
    when(reservationInPort.saveReservationPreCheckIn(any()))
        .thenReturn(getPreCheckInResponse("Success", "Pre-CheckIn status saved successfully"));

    // Act
    PreCheckInResponse response = reservationInPort.saveReservationPreCheckIn(
        mockPreCheckInRequest());

    // Assert
    assertNotNull(response);
    assertEquals("Success", response.getStatus());
  }

  @Test
  void testSaveReservationPreCheckIn_Error() {
    // Arrange
    when(reservationInPort.saveReservationPreCheckIn(any()))
        .thenReturn(getPreCheckInResponse("Error", "Error in saving Pre-CheckIn status"));

    // Act
    PreCheckInResponse response = reservationInPort.saveReservationPreCheckIn(
        mockPreCheckInRequest());

    // Assert
    assertNotNull(response);
    assertEquals("Error", response.getStatus());
  }

  @Test
  void testSaveReservationPreCheckIn_ThrowException() {
    // Arrange
    HotelReservationOhipException preCheckInException = new HotelReservationOhipException("message",
        "An error was returned by OHIP Adapter!", new Exception(), 900);

    when(reservationInPort.saveReservationPreCheckIn(any())).thenThrow(preCheckInException);

    // Act
    PreCheckInRequest request = mockPreCheckInRequest();
    HotelReservationOhipException exception = assertThrows(HotelReservationOhipException.class,
        () -> reservationInPort.saveReservationPreCheckIn(request));

    // Assert
    assertNotNull(exception);
    assertThat(exception.getMessage(), is("An error was returned by OHIP Adapter!"));
  }

  @Test
  void updateReservationScheduled_Success() {
    // Arrange
    var request = createReservationPackagesScheduledRequest();
    var meals = List.of(new Meal("name", "pkg1"), new Meal("name", "pkg2"),
        new Meal("name", "pkg3"),new Meal("name","pkg4"));
    var pkgResponse = PackagesResponse.builder().packages(new Packages(meals)).build();
    when(reservationOutPort.getReservationsByIds("HotelId",
        List.of("11111111", "22222222", "33333333"), false)).thenReturn(
        ManageReservationUtils.mockReservationByBasketRefResponse());
    when(reservationOutPort.getPackages(any())).thenReturn(pkgResponse);
    when(basketOutPort.getBasketByReference(any())).thenReturn(
        Optional.of(ManageReservationUtils.mockBasketResponse()));
    when(promotionProperties.getPromotionalPackages()).thenReturn(PROMO_PACKAGE);

    // Act
    var response = reservationInPort.updateReservationPackageScheduled(request);

    //Assert
    assertNotNull(response);
  }

  @Test
  void updateReservationScheduled_IncorrectScheduleDates() {
    // Arrange
    var request = createReservationPackagesScheduledRequest(
        LocalDate.of(2015, 5, 6));
    when(reservationOutPort.getReservationsByIds("HotelId",
        List.of("11111111", "22222222", "33333333"), false)).thenReturn(
        ManageReservationUtils.mockReservationByBasketRefResponse());

    // Act
    SchedulePackageException exception = assertThrows(SchedulePackageException.class,
        () -> reservationInPort.updateReservationPackageScheduled(request));

    //Assert
    assertNotNull(exception);
    assertThat(exception.getMessage(),
        is("The following scheduled dates are outside the reservation period: [2015-05-06]"));
  }

  @Test
  void updateReservationScheduled_IncorrectPackages() {
    // Arrange
    var request = createReservationPackagesScheduledRequest();
    when(reservationOutPort.getReservationsByIds("HotelId",
        List.of("11111111", "22222222", "33333333"), false)).thenReturn(
        ManageReservationUtils.mockReservationByBasketRefResponse());
    when(reservationOutPort.getPackages(any())).thenReturn(mockPackagesResponse());

    // Act
    SchedulePackageException exception = assertThrows(SchedulePackageException.class,
        () -> reservationInPort.updateReservationPackageScheduled(request));

    //Assert
    assertNotNull(exception);
    assertEquals("The following packages are not available for add/remove:"
        + " pkg1, pkg2, pkg3",exception.getMessage());
  }

  @Test
  void updateReservationScheduled_EmptyPackagesSuccess() {
    // Arrange
    var request = createReservationPackagesScheduledRequest();
    request.getReservations().get(0).setAddPackages(List.of(new PackagesSelectionScheduled()));
    request.getReservations().get(2).setRemovePackages(List.of(new PackagesSelection()));
    var meals = List.of(new Meal("name", "pkg1"), new Meal("name", "pkg2"),
        new Meal("name", "pkg3"),new Meal("name","pkg4"));
    var pkgResponse = PackagesResponse.builder().packages(new Packages(meals)).build();
    when(reservationOutPort.getReservationsByIds("HotelId",
        List.of("11111111", "22222222", "33333333"), false)).thenReturn(
        ManageReservationUtils.mockReservationByBasketRefResponse());
    when(reservationOutPort.getPackages(any())).thenReturn(pkgResponse);
    when(basketOutPort.getBasketByReference(any())).thenReturn(
        Optional.of(ManageReservationUtils.mockBasketResponse()));
    when(promotionProperties.getPromotionalPackages()).thenReturn(PROMO_PACKAGE);

    // Act
    var response = reservationInPort.updateReservationPackageScheduled(request);

    //Assert
    assertNotNull(response);
  }

  @Test
  void updateReservationScheduled_InvalidBasket() {
    // Arrange
    var request = createReservationPackagesScheduledRequest();
    request.getReservations().get(0).setAddPackages(List.of(new PackagesSelectionScheduled()));
    request.getReservations().get(2).setRemovePackages(List.of(new PackagesSelection()));
    var meals = List.of(new Meal("name", "pkg1"), new Meal("name", "pkg2"),
        new Meal("name", "pkg3"),new Meal("name","pkg4"));
    var pkgResponse = PackagesResponse.builder().packages(new Packages(meals)).build();
    when(reservationOutPort.getReservationsByIds("HotelId",
        List.of("11111111", "22222222", "33333333"), false)).thenReturn(
        ManageReservationUtils.mockReservationByBasketRefResponse());
    when(reservationOutPort.getPackages(any())).thenReturn(pkgResponse);
    when(basketOutPort.getBasketByReference(any())).thenReturn(Optional.empty());
    when(promotionProperties.getPromotionalPackages()).thenReturn(PROMO_PACKAGE);

    // Act
    SchedulePackageException exception = assertThrows(SchedulePackageException.class,
        () -> reservationInPort.updateReservationPackageScheduled(request));

    //Assert
    assertNotNull(exception);
    assertEquals("Valid basket cannot be retrieved",exception.getMessage());
  }

  @Test
  void buildRequestAmendStayDatesSingleCall_isOta_success() {
    // Arrange

    var basketSingleReservationResponse = mockBasketSingleReservationResponse();
    var manageBookingResponse = mockManageBookingResponse();
    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    String today = LocalDate.now().format(formatter);
    String tomorrow = LocalDate.now().plusDays(1).format(formatter);
    var amendStayDateUpdateRequest = mockAmendStayDatesRequest(today, tomorrow);
    amendStayDateUpdateRequest.setIsOta(true);
    amendStayDateUpdateRequest.setWbRoomTypes(List.of("DOUBLE"));
    amendStayDateUpdateRequest.getBookingChannel().setChannel("DISTR");
    AmendDistributionStayDatesRequest amendDistributionStayDatesRequest = new AmendDistributionStayDatesRequest();
    amendDistributionStayDatesRequest.setAmendStayDatesRequest(amendStayDateUpdateRequest);
    amendDistributionStayDatesRequest.setAdults(2);
    amendDistributionStayDatesRequest.setChildren(0);
    amendDistributionStayDatesRequest.setWbRoomType("DB");
    amendDistributionStayDatesRequest.setExternalReference("res1");
    amendDistributionStayDatesRequest.setIsOta(true);
    var availabilityResponse = createValidAvailabilityV2Response();
    
    var bookingAllowancesResponse = mockCreateBookingAllowancesResponse();
    List<String> basketBookingAllowances =
        basketSingleReservationResponse.getBookingAllowances() != null ?
            basketSingleReservationResponse.getBookingAllowances().stream()
                .map(BookingAllowance::getAllowance).toList() :
            List.of();

    // Arrange
    when(rulesOutPort.getMaxNightsRule(any())).thenReturn(new MaxNightsRuleResponse(9));
    when(basketOutPort.getBasketById(anyString())).thenReturn(
        basketSingleReservationResponse);
    when(reservationOutPort.getReservationsByIds(any(), any(), eq(false), eq(false),
        eq(false))).thenReturn(
        mockResByBasketRefSingleResResponse());
    when(amendLogicInPort.checkCreateOnHoldReservation(anyString(),
        anyString(), anyString(), anyString())).thenReturn(true);
    when(amendLogicInPort.getAmendOnHoldReservationInterval(anyString(), anyString(), anyString(),
        anyString()))
        .thenReturn(List.of(createAmendOnHoldInterval(today, tomorrow)));
    when(reservationOutPort.getBookingAllowances("TESTHOTEL", "res1", basketBookingAllowances))
        .thenReturn(bookingAllowancesResponse);

    //mocking create onHoldReservations
    when(reservationOutPort.createReservation(anyString(), any(), anyString()))
        .thenReturn(mockReservationResponse());
    when(basketOutPort.createBasket(anyString(), anyString(), any()))
        .thenReturn(mockBasketSingleReservationResponse());
    when(basketOutPort.addReservationsToBasket(any(), any(),
        any(OhipReservationResponse.class), anyBoolean(), anyBoolean(), eq(null), anyBoolean()))
        .thenReturn(ManageReservationUtils.mockBasketResponse());
    when(manageBookingInPort.getManageBookingInformation(anyString(), anyString(),
        anyString(), anyString(),
        any(), anyBoolean(), any())).thenReturn(manageBookingResponse);
    when(availabilityOutPort.getHotelAvailabilitiesByIdsV2(any())).thenReturn(
        availabilityResponse);
    when(authenticatedUserService.isUserAuthenticated()).thenReturn(Boolean.TRUE);
    when(authenticatedUserService.getAuthenticatedUserAuthorities())
        .thenReturn(List.of(DISTR_FIXED_PRICE_AUTH));
    when(distributionProperties.getFixedRateAuthority())
        .thenReturn(DISTR_FIXED_PRICE_AUTH);

    doNothing().when(reservationOutPort).updateReservationsSingleCall(any(UpdateReservationsRequest.class));

    tokenUtilsMock.when(() -> TokenUtils.isValid(anyString(), anyString()))
        .thenReturn(Boolean.TRUE);

    var mockedFeatureFlag = mock(FeatureFlag.class);
    when(unleashWrapper.featureFlag())
        .thenReturn(mockedFeatureFlag);
    when(unleashWrapper.isEnabled(mockedFeatureFlag.getApplyOccupancySupplement()))
        .thenReturn(false);

    // Act
    var updateReservationsRequest = reservationInPort.buildRequestAmendStayDatesSingleCall(
        List.of(amendDistributionStayDatesRequest), null);

    // Assert
    verify(amendLogicInPort, times(1)).checkCreateOnHoldReservation(anyString(),
        anyString(), anyString(), anyString());
    verify(amendLogicInPort, times(1)).getAmendOnHoldReservationInterval(anyString(),
        anyString(), anyString(), anyString());
    verify(reservationOutPort, times(1)).updateReservationsSingleCall(any());

    var ratePrices = updateReservationsRequest.getNewRatesReservation().get(0).getRoomRates()
        .getRatePrices();
    BigDecimal expectedPriceInfoAmount = availabilityResponse.getHotelAvailability().get(0)
        .getRoomStays()
        .get(0).getRoomTypes().get(0).getRoomRates().get(0).getRoomRateInfo().getPriceInfo()
        .get(0).getAmountBeforeTax();

    assertNotNull(ratePrices);
    assertFalse(ratePrices.isEmpty());
    assertEquals(today, ratePrices.get(0).getPriceStartDate().format(formatter));
    assertEquals(tomorrow, ratePrices.get(0).getPriceEndDate().format(formatter));
    assertEquals(expectedPriceInfoAmount, ratePrices.get(0).getAmount());
  }

  @Test
  void createReservation_addPromotionToBasket_success() {
    // Arrange
    var promoBasket = ManageReservationUtils.mockBasketResponse();
    promoBasket.setPromoKind(SITE_WIDE);
    promoBasket.setPromotionCode("TEST");
    when(reservationOutPort.createReservation(anyString(), any(), anyString()))
        .thenReturn(mockReservationResponse());
    when(basketOutPort.createBasket(anyString(), anyString(), any()))
        .thenReturn(ManageReservationUtils.mockBasketResponse());
    when(basketOutPort.addReservationsToBasket(any(), any(),
        any(OhipReservationResponse.class), anyBoolean(), anyBoolean(), eq(null), anyBoolean()))
        .thenReturn(ManageReservationUtils.mockBasketResponse());
    when(authenticatedUserService.isUserAuthenticated()).thenReturn(Boolean.TRUE);
    when(basketOutPort.addPromotionToBasket(any(), any(), any(), any()))
        .thenReturn(promoBasket);

    var account = Account.builder()
        .customerId("test_customer")
        .build();
    when(authenticatedUserService.getCurrentUserAccount()).thenReturn(Optional.of(account));
    ArgumentCaptor<ReservationRequest> argCaptor = ArgumentCaptor.forClass(
        ReservationRequest.class);
    var createRequest = createValidHotelReservationRequest();
    createRequest.getBookingChannel().setChannel(PI_BOOKING_CHANNEL);
    createRequest.getReservations().get(0).getRoomRates().setPromotionCode("TEST");
    createRequest.getReservations().get(0).getRoomRates().setPromoKind(PromoKind.SITE_WIDE);

    var mockedFeatureFlag = mock(FeatureFlag.class);
    when(unleashWrapper.featureFlag())
        .thenReturn(mockedFeatureFlag);
    when(unleashWrapper.isEnabled(mockedFeatureFlag.getApplyOccupancySupplement()))
        .thenReturn(false);

    // Act
    var response = reservationInPort.createReservation(createRequest);

    // Assert
    assertThat(response.getBasketReference(), is(BASKET_REFERENCE));
    verify(reservationOutPort).createReservation(any(), argCaptor.capture(), anyString());
    var createReservationRequest = argCaptor.getValue();
    assertThat(createReservationRequest.getReservations().get(0).getUserAccountId(),
        is("test_customer"));
    assertThat(createReservationRequest.getReservations().get(0).getRoomRates().getPromotionCode(),
        is("TEST"));
    assertThat(createReservationRequest.getReservations().get(0).getRoomRates().getPromoKind(),
        is(PromoKind.SITE_WIDE));
  }

  @DisplayName("Parameterized test for RoomsSelectionsByReservation.equals()")
  @ParameterizedTest(name = "{index} => obj1={0}, obj2={1}, expected={2}")
  @MethodSource("provideRoomsSelectionsByReservationObjectsForEqualsTest")
  void testIfTwoRoomsSelectionsByReservationAreEqual(RoomsSelectionsByReservation obj1,
      RoomsSelectionsByReservation obj2, boolean expected) {
    if (expected) {
      var result = areEqual(obj1, obj2);
      assertTrue(result);
    } else {
      var result = areEqual(obj1, obj2);
      assertFalse(result);
    }
  }

  @Test
  void updateReservationAlerts_success() {
    // Arrange
    doNothing().when(reservationOutPort)
        .updateReservationAlerts(any(UpdateReservationAlertsRequest.class));

    // Act
    reservationInPort.updateReservationAlerts(new UpdateReservationAlertsRequest());

    //Assert
    verify(reservationOutPort, times(1)).updateReservationAlerts(
        any(UpdateReservationAlertsRequest.class));
  }

  @Test
  void confirmAmend_nonRefundableReservationFlag() {
    // Arrange
    String originalBasketRef = ORIGINAL_BASKET_REF;
    String tempBasketRef = TEMPORARY_BASKET_REF;

    var originalBasket = BasketResponse.builder()
        .bookingReference(originalBasketRef)
        .reference(originalBasketRef)
        .hotelId(HOTEL_CODE_TEST)
        .items(List.of(BasketItemResponse.builder().sourceId(SOURCE_ID_111111).hasOccupancySup(false).build()))
        .eTag(String.valueOf(Instant.now().toEpochMilli()))
        .paymentOption(PaymentOption.PAY_NOW)
        .build();

    var bookingAllowancesResponse = mockCreateBookingAllowancesResponse();

    var tempBasket = BasketResponse.builder()
        .bookingReference(tempBasketRef)
        .reference(tempBasketRef)
        .status(BASKET_STATUS_OPEN)
        .items(List.of(BasketItemResponse.builder().sourceId(SOURCE_ID_222222).hasOccupancySup(true).build(),
            BasketItemResponse.builder().sourceId(SOURCE_ID_333333).build()))
        .linkAmendReservations(Map.of(SOURCE_ID_111111, SOURCE_ID_222222))
        .hotelId(HOTEL_CODE_TEST)
        .originalBasketId(originalBasketRef)
        .eTag(String.valueOf(Instant.now().plus(1, ChronoUnit.MINUTES).toEpochMilli()))
        .paymentOption(PaymentOption.PAY_NOW)
        .build();

    List<String> tempBasketBookingAllowances = tempBasket.getBookingAllowances() != null ?
        tempBasket.getBookingAllowances().stream()
            .map(BookingAllowance::getAllowance)
            .toList() : List.of();

    var reservationByIdResponse = new ArrayList<ReservationByIdResponse>();
    reservationByIdResponse.add(ManageReservationUtils.mockReservationByIdResponseConfirm(true));
    reservationByIdResponse.add(ManageReservationUtils.mockReservationByIdResponseConfirm(false));

    var basketByRefResponse = ReservationByBasketRefResponse.builder()
        .reservationByIdList(reservationByIdResponse)
        .build();

    when(basketOutPort.getBasketById(originalBasketRef)).thenReturn(originalBasket);
    when(basketOutPort.getBasketById(tempBasketRef)).thenReturn(tempBasket);
    when(reservationOutPort.getReservationsByIds(anyString(), anyList(), anyBoolean(), anyBoolean(), anyBoolean())).thenReturn(
        basketByRefResponse);
    var copyReservationsResponse = mockCopyReservationsResponse();
    when(reservationOutPort.copyReservations(any())).thenReturn(
        copyReservationsResponse);
    when(basketOutPort.addReservationsToBasket(originalBasket.getReference(),
        originalBasket.getETag().replace("\"", ""),
        copyReservationsResponse, false,
        Map.of(copyReservationsResponse.getReservations().get(0).getReservationId(), false)))
        .thenReturn(originalBasket);
    when(reservationOutPort.getBookingAllowances(HOTEL_CODE_TEST, SOURCE_ID_222222, tempBasketBookingAllowances))
        .thenReturn(bookingAllowancesResponse);
    when(rulesOutPort.getBusinessAllowanceRules()).thenReturn(buildBusinessAllowanceRuleResponse());
    when(contentOutPort.getBusinessNotes(LANGUAGE)).thenReturn(buildBusinessNotesResponse());
    when(contentOutPort.getHotelPaymentInformation(tempBasket.getHotelId(), LANGUAGE, COUNTRY))
        .thenReturn(getPaymentResponse());
    when(reservationOutPort.confirmAmend(any())).thenReturn(basketByRefResponse);
    when(reservationOutPort.getReservationsPackagesByIds(any(), any()))
        .thenReturn(getMockedReservationsPackagesResponseTempBasket());
    when(amendPayNowLogic.calculateDfForPayNow(any(), any(), any(), any(), any(), any(), any(), any()))
        .thenReturn(DepositFolioComputationResult.builder()
            .totalRefundAmt(BigDecimal.valueOf(50))
            .markAsPayOnArrival(Boolean.FALSE)
            .build());
    when(amendPayNowLogic.triggerRefund(any(), any(), any(), any())).thenReturn(
        DepositFolioComputationResult.builder()
            .markAsPayOnArrival(Boolean.FALSE)
            .paymentId("2334454")
            .build());

    stubCancellationPoliciesFetch();

    SingleOccupancySupplementResponse mockSingleOccupancy = mock(SingleOccupancySupplementResponse.class);
    when(rulesOutPort.getSingleOccupancySupplementResponse(any())).thenReturn(mockSingleOccupancy);
    when(mockSingleOccupancy.getPricing()).thenReturn(BigDecimal.ZERO);

    var mockedFeatureFlag = mock(FeatureFlag.class);
    when(unleashWrapper.featureFlag())
        .thenReturn(mockedFeatureFlag);
    when(unleashWrapper.isEnabled(mockedFeatureFlag.getEnableAbsoluteDeadline()))
        .thenReturn(true);

    var mockCancelInformationResponse = CancelInformationResponse
        .builder().isCancellable(false).build();
    when(reservationOutPort.getCancelInformation(anyString(), any(),
        anyString())).thenReturn(mockCancelInformationResponse);

    var request = new ConfirmAmendRequest(originalBasketRef, tempBasketRef, TOKEN,
        BookingChannel.builder().channel(PI_BOOKING_CHANNEL).subchannel(SUBCHANNEL_WEB).language(
            LANGUAGE_EN).build(),
        false, false, PAY_ON_ARRIVAL.toString(), "", "");

    // Act
    var response = reservationInPort.confirmAmend(request, true);

    // Assert
    assertNotNull(response);
    assertEquals(1, response.getReservationByIdList().size());
    verify(amendPayNowLogic).saveDepositFolios(any(), any(), any(), any());
    verify(amendPayNowLogic, times(1)).triggerRefund(any(), any(), any(), any());
    verify(reservationOutPort).updateBusinessItems(any(BusinessItemsRequest.class));
    verify(basketOutPort).updateOccupancySupplementFlag(any(), any(), any());
  }

  @Test
  void createReservation_uniquePromo_operaCodeOverridesClientCode() {
    // Arrange
    when(basketOutPort.createBasket(any(), any(), any()))
        .thenReturn(ManageReservationUtils.mockBasketResponse());

    when(reservationOutPort.createReservation(any(), any(), any()))
        .thenReturn(mockReservationResponse());

    when(basketOutPort.addReservationsToBasket(
        any(), any(), any(), anyBoolean(), anyBoolean(), any(), anyBoolean()))
        .thenReturn(ManageReservationUtils.mockBasketResponse());

    PromoKindResponse promoResponse = mock(PromoKindResponse.class);
    when(promoResponse.getUniquePromoCodeStatus())
        .thenReturn(PromoCodeStatus.ISSUED);
    when(promoResponse.getOperaPromoCode())
        .thenReturn("OPERA999");

    when(promotionOutPort.getPromoKind("CLIENT123"))
        .thenReturn(promoResponse);

    when(basketOutPort.addPromotionToBasket(any(), any(), any(), any()))
        .thenReturn(ManageReservationUtils.mockBasketResponse());

    ArgumentCaptor<ReservationRequest> captor =
        ArgumentCaptor.forClass(ReservationRequest.class);

    ReservationRequest request = createValidHotelReservationRequest();
    request.getReservations().get(0).getRoomRates().setPromoKind(PromoKind.UNIQUE);
    request.getReservations().get(0).getRoomRates().setPromotionCode("CLIENT123");

    var mockedFeatureFlag = mock(FeatureFlag.class);
    when(unleashWrapper.featureFlag())
        .thenReturn(mockedFeatureFlag);
    when(unleashWrapper.isEnabled(mockedFeatureFlag.getApplyOccupancySupplement()))
        .thenReturn(false);

    // Act
    reservationInPort.createReservation(request);

    // Assert
    verify(reservationOutPort)
        .createReservation(any(), captor.capture(), any());

    ReservationRequest passedRequest = captor.getValue();

    assertThat(
        passedRequest.getReservations().get(0)
            .getRoomRates().getPromotionCode(),
        is("OPERA999"));
  }

  @Test
  void createReservation_uniquePromo_redeemed_throwsException() {
    // Arrange
    when(basketOutPort.createBasket(any(), any(), any()))
        .thenReturn(ManageReservationUtils.mockBasketResponse());

    PromoKindResponse promoResponse = mock(PromoKindResponse.class);
    when(promoResponse.getUniquePromoCodeStatus())
        .thenReturn(PromoCodeStatus.REDEEMED);

    when(promotionOutPort.getPromoKind("USED123"))
        .thenReturn(promoResponse);

    ReservationRequest request = createValidHotelReservationRequest();
    request.getReservations().get(0).getRoomRates().setPromoKind(PromoKind.UNIQUE);
    request.getReservations().get(0).getRoomRates().setPromotionCode("USED123");

    var mockedFeatureFlag = mock(FeatureFlag.class);
    when(unleashWrapper.featureFlag())
        .thenReturn(mockedFeatureFlag);
    when(unleashWrapper.isEnabled(mockedFeatureFlag.getApplyOccupancySupplement()))
        .thenReturn(false);

    // Act + Assert
    PromotionException ex = assertThrows(
        PromotionException.class,
        () -> reservationInPort.createReservation(request)
    );

    assertThat(
        ex.getErrorCode(),
        is(ErrorCode.DIGITAL_PROMOTION_ALREADY_USED_EXCEPTION.getCode()));
  }

  @Test
  void createReservation_uniquePromo_expired_throwsException() {
    // Arrange
    when(basketOutPort.createBasket(any(), any(), any()))
        .thenReturn(ManageReservationUtils.mockBasketResponse());

    PromoKindResponse promoResponse = mock(PromoKindResponse.class);
    when(promoResponse.getUniquePromoCodeStatus())
        .thenReturn(PromoCodeStatus.EXPIRED);

    when(promotionOutPort.getPromoKind("EXPIRED123"))
        .thenReturn(promoResponse);

    ReservationRequest request = createValidHotelReservationRequest();
    request.getReservations().get(0).getRoomRates().setPromoKind(PromoKind.UNIQUE);
    request.getReservations().get(0).getRoomRates().setPromotionCode("EXPIRED123");

    var mockedFeatureFlag = mock(FeatureFlag.class);
    when(unleashWrapper.featureFlag())
        .thenReturn(mockedFeatureFlag);
    when(unleashWrapper.isEnabled(mockedFeatureFlag.getApplyOccupancySupplement()))
        .thenReturn(false);

    // Act + Assert
    PromotionException ex = assertThrows(
        PromotionException.class,
        () -> reservationInPort.createReservation(request)
    );

    assertThat(
        ex.getErrorCode(),
        is(ErrorCode.DIGITAL_PROMOTION_EXPIRED_EXCEPTION.getCode()));
  }

  @Test
  void createReservation_uniquePromo_promoServiceReturnsNull_throwsError() {
    // Arrange
    when(basketOutPort.createBasket(any(), any(), any()))
        .thenReturn(ManageReservationUtils.mockBasketResponse());

    when(promotionOutPort.getPromoKind("CLIENT123"))
        .thenReturn(null);

    ReservationRequest request = createValidHotelReservationRequest();
    request.getReservations().get(0).getRoomRates().setPromoKind(PromoKind.UNIQUE);
    request.getReservations().get(0).getRoomRates().setPromotionCode("CLIENT123");

    var mockedFeatureFlag = mock(FeatureFlag.class);
    when(unleashWrapper.featureFlag())
        .thenReturn(mockedFeatureFlag);
    when(unleashWrapper.isEnabled(mockedFeatureFlag.getApplyOccupancySupplement()))
        .thenReturn(false);

    // Act + Assert
    PromotionException ex = assertThrows(
        PromotionException.class,
        () -> reservationInPort.createReservation(request)
    );

    assertThat(
        ex.getErrorCode(),
        is(ErrorCode.DIGITAL_PROMOTION_NOT_FOUND.getCode()));
  }

  @Test
  void createReservation_uniquePromo_noOperaPromo_usesClientPromoCode() {
    // Arrange
    when(basketOutPort.createBasket(any(), any(), any()))
        .thenReturn(ManageReservationUtils.mockBasketResponse());

    when(reservationOutPort.createReservation(any(), any(), any()))
        .thenReturn(mockReservationResponse());

    when(basketOutPort.addReservationsToBasket(
        any(), any(), any(), anyBoolean(), anyBoolean(), any(), anyBoolean()))
        .thenReturn(ManageReservationUtils.mockBasketResponse());

    PromoKindResponse promoResponse = mock(PromoKindResponse.class);
    when(promoResponse.getUniquePromoCodeStatus())
        .thenReturn(PromoCodeStatus.ISSUED);
    when(promoResponse.getOperaPromoCode())
        .thenReturn(null);

    when(promotionOutPort.getPromoKind("CLIENT123"))
        .thenReturn(promoResponse);

    when(basketOutPort.addPromotionToBasket(any(), any(), any(), any()))
        .thenReturn(ManageReservationUtils.mockBasketResponse());

    ArgumentCaptor<ReservationRequest> captor =
        ArgumentCaptor.forClass(ReservationRequest.class);

    ReservationRequest request = createValidHotelReservationRequest();
    request.getReservations().get(0).getRoomRates().setPromoKind(PromoKind.UNIQUE);
    request.getReservations().get(0).getRoomRates().setPromotionCode("CLIENT123");

    var mockedFeatureFlag = mock(FeatureFlag.class);
    when(unleashWrapper.featureFlag())
        .thenReturn(mockedFeatureFlag);
    when(unleashWrapper.isEnabled(mockedFeatureFlag.getApplyOccupancySupplement()))
        .thenReturn(false);

    // Act
    reservationInPort.createReservation(request);

    // Assert
    verify(reservationOutPort)
        .createReservation(any(), captor.capture(), any());

    assertThat(
        captor.getValue().getReservations().get(0)
            .getRoomRates().getPromotionCode(),
        is("CLIENT123"));
  }

  private void stubCancellationPoliciesFetch() {
    var reservationByIdDetailsResponse = mock(ReservationByIdDetailsResponse.class,
        Answers.RETURNS_DEEP_STUBS);
    var cancellationPoliciesResponse = mock(CancellationPoliciesResponse.class,
        Answers.RETURNS_DEEP_STUBS);
    when(reservationOutPort.getReservationsByReservationId(any(), any())).thenReturn(
        reservationByIdDetailsResponse);
    when(reservationOutPort.getCancellationPolicies(any(), any(), any(), any()))
        .thenReturn(cancellationPoliciesResponse);
  }

  private static Stream<Arguments> provideRoomsSelectionsByReservationObjectsForEqualsTest() {
    return Stream.of(
        Arguments.of(null, null, true),
        Arguments.of(new RoomsSelectionsByReservation(), null, false),
        Arguments.of(null, new RoomsSelectionsByReservation(), false),
        //Test when one reservation has packagesSelection null
        Arguments.of(RoomsSelectionsByReservation.builder().
                reservationId("123")
                .packagesSelection(null)
                .build(),
            RoomsSelectionsByReservation.builder().
                reservationId("456")
                .packagesSelection(Collections.singletonList(
                    buildPackagesSelection(PREMIER_IN_BREAKFAST, 1)))
                .build(),
            false),
        //Test when originalReservation has the same package as tempReservation
        Arguments.of(RoomsSelectionsByReservation.builder().
                reservationId("123")
                .packagesSelection(Collections.singletonList(
                    buildPackagesSelection(PREMIER_IN_BREAKFAST, 1)))
                .build(),
            RoomsSelectionsByReservation.builder().
                reservationId("456")
                .packagesSelection(Collections.singletonList(
                    buildPackagesSelection(PREMIER_IN_BREAKFAST, 1)))
                .build(),
            true),
        //Test when originalReservation has the same package as tempReservation but different noOfSelections
        Arguments.of(RoomsSelectionsByReservation.builder().
                reservationId("123")
                .packagesSelection(Collections.singletonList(
                    buildPackagesSelection(PREMIER_IN_BREAKFAST, 1)))
                .build(),
            RoomsSelectionsByReservation.builder().
                reservationId("456")
                .packagesSelection(Collections.singletonList(
                    buildPackagesSelection(PREMIER_IN_BREAKFAST, 2)))
                .build(),
            false),
        //Test when originalReservation has different package than tempReservation
        Arguments.of(RoomsSelectionsByReservation.builder().
                reservationId("123")
                .packagesSelection(Collections.singletonList(
                    buildPackagesSelection(PREMIER_IN_BREAKFAST, 1)))
                .build(),
            RoomsSelectionsByReservation.builder().
                reservationId("456")
                .packagesSelection(Collections.singletonList(
                    buildPackagesSelection(CONTINENTAL_BREAKFAST, 1)))
                .build(),
            false),
        //Test when originalReservation has different package than tempReservation
        Arguments.of(RoomsSelectionsByReservation.builder().
                reservationId("123")
                .packagesSelection(Collections.singletonList(
                    buildPackagesSelection(PREMIER_IN_BREAKFAST, 1)))
                .build(),
            RoomsSelectionsByReservation.builder().
                reservationId("456")
                .packagesSelection(List.of(buildPackagesSelection(MEAL_DEAL_DINNER, 1),
                    buildPackagesSelection(MEAL_DEAL_DINNER_BEVERAGE, 1),
                    buildPackagesSelection(MEAL_DEAL_BREAKFAST_FOOD, 1)))
                .build(),
            false),
        //Test when originalReservation has the same packages as tempReservation but in different order
        Arguments.of(RoomsSelectionsByReservation.builder().
                reservationId("123")
                .packagesSelection(List.of(buildPackagesSelection(MEAL_DEAL_DINNER, 1),
                    buildPackagesSelection(MEAL_DEAL_DINNER_BEVERAGE, 1),
                    buildPackagesSelection(MEAL_DEAL_BREAKFAST_FOOD, 1)))
                .build(),
            RoomsSelectionsByReservation.builder().
                reservationId("456")
                .packagesSelection(List.of(buildPackagesSelection(MEAL_DEAL_BREAKFAST_FOOD, 1),
                    buildPackagesSelection(MEAL_DEAL_DINNER, 1),
                    buildPackagesSelection(MEAL_DEAL_DINNER_BEVERAGE, 1)))
                .build(),
            true)
    );
  }

  private static uk.co.whitbread.reservation.domain.model.out.PackagesSelection buildPackagesSelection(
      String packageCode, int noOfSelections) {
    return uk.co.whitbread.reservation.domain.model.out.PackagesSelection
        .builder()
        .id(packageCode)
        .noOfSelections(noOfSelections)
        .build();
  }

  private PreCheckInRequest mockPreCheckInRequest() {
    return PreCheckInRequest.builder()
        .arrivalTime(LocalDate.of(1996, 7, 13))
        .hotelId("STUAIR")
        .reservationId("123456")
        .build();
  }

  private PreCheckInResponse getPreCheckInResponse(String status, String message) {
    return PreCheckInResponse.builder()
        .status(status)
        .message(message)
        .build();
  }

  private ReservationFileAttachmentRequest mockReservationFileAttachmentRequest() {
    return ReservationFileAttachmentRequest.builder()
        .fileAttachment("Base64 string")
        .description("Test attachment")
        .fileName("REG_RES1234567_ID232323_P76767676.pdf")
        .global(false)
        .reservationId("1613333")
        .overwriteExistingFile(true)
        .hotelId("STUAIR")
        .build();
  }

  private Map<String, Map<String, BigDecimal>> mockDepositFolioConsolidated() {
    Map<String, Map<String, BigDecimal>> dfConsolidated = new HashMap<>();
    Map<String, BigDecimal> refMap = new HashMap<>();
    refMap.put("2023-12-08-9016", BigDecimal.valueOf(999.0));
    refMap.put("2023-12-08-9026", BigDecimal.valueOf(0.0));
    dfConsolidated.put("tempRes1", refMap);
    return dfConsolidated;
  }

  private ConfirmReservationResponse getConfirmReservationResponse
          (ReservationByBasketRefResponse reservationDetails, ConfirmReservationRequest confirmReservationRequest) {

    ConfirmReservationResponse confirmReservationRes = new ConfirmReservationResponse();
    var confirmResReq = ConfirmReservationRequest.builder()
            .hotelId("TESTHOTEL")
            .reservationId("tempRes1").build();
    confirmReservationRes.getReservationIdList().get(0).setId(confirmResReq.getReservationId());
    confirmReservationRes.setHotelId(reservationDetails.getHotelId());
    confirmReservationRes.setRoomStay(ConfirmationRoomStay.builder()
            .arrivalDate(LocalDate.parse(reservationDetails.getReservationByIdList()
                    .get(0).getRoomStay().getArrivalDate()))
            .departureDate(LocalDate.parse(reservationDetails.getReservationByIdList()
                    .get(0).getRoomStay().getDepartureDate()))
            .build());

    confirmReservationRes.setReservationGuest(ConfirmationCustomer.builder()
            .givenName(reservationDetails.getReservationByIdList().get(0).getReservationGuestList()
                    .get(0).getGivenName())
            .surName(reservationDetails.getReservationByIdList().get(0)
                    .getReservationGuestList().get(0).getSurName()).build());

    confirmReservationRes.setReservationStatus(reservationDetails.getReservationByIdList()
            .get(0).getReservationStatus());
    return confirmReservationRes;
  }

  private HotelPaymentInformation getPaymentResponse() {
    return HotelPaymentInformation.builder()
        .acceptedCreditCards(
            List.of(AcceptedCreditCard.builder()
                .codeOpera("BU")
                .codeOperaCardType("ZZ")
                .build()))
        .build();
  }

  private ManageBookingResponse mockManageBookingResponse() {
    return ManageBookingResponse.builder()
            .isCancellable(Boolean.TRUE)
            .isAmendable(Boolean.TRUE)
            .isRuleCompliant(Boolean.TRUE)
            .build();
  }

  private HotelPaymentInformation mockHotelPaymentInfoResponse() {
    return HotelPaymentInformation.builder()
        .acceptedCreditCards(List.of(
            AcceptedCreditCard.builder().code("DL").codeOpera("VA").build(),
            AcceptedCreditCard.builder().code("EL").codeOpera("VA").build()
        )).paymentMethodsOpera(Map.of("PI_VA", "DVA", "PI_MC", "DMC")).build();
  }

  private static RefundResponse mockRefundResponse(boolean isSuccessful) {
    return RefundResponse.builder()
        .refunded(isSuccessful)
        .build();
  }

  private ReservationPackagesScheduledRequest createReservationPackagesScheduledRequest() {
    return createReservationPackagesScheduledRequest(LocalDate.of(2022, 5, 5));
  }

  private ReservationPackagesScheduledRequest createReservationPackagesScheduledRequest(
      LocalDate pkgSchedule) {
    PackagesSelectionScheduled addPackage1 = new PackagesSelectionScheduled("pkg1", 2,
        Arrays.asList(pkgSchedule, LocalDate.of(2022, 5, 6)));

    PackagesSelectionScheduled addPackage2 = new PackagesSelectionScheduled("pkg2", 1,
        List.of(LocalDate.of(2022, 5, 5)));

    PackagesSelectionScheduled addPackage3 = new PackagesSelectionScheduled("pkg3", 1,
        List.of(LocalDate.of(2022, 5, 5)));

    PackagesSelection removePackage1 = new PackagesSelection("pkg1", 1);
    PackagesSelection removePackage2 = new PackagesSelection("pkg2", 2);
    PackagesSelection removePackage3 = new PackagesSelection("pkg3", 2);

    RoomReservationPackagesScheduledRequest roomReservation = new RoomReservationPackagesScheduledRequest(
        "11111111", Arrays.asList(addPackage1),
        Arrays.asList(removePackage1));

    RoomReservationPackagesScheduledRequest roomReservation2 = new RoomReservationPackagesScheduledRequest(
        "22222222", Arrays.asList(addPackage2),
        Arrays.asList(removePackage2));

    RoomReservationPackagesScheduledRequest roomReservation3 = new RoomReservationPackagesScheduledRequest(
        "33333333", Arrays.asList(addPackage3),
        Arrays.asList(removePackage3));

    return new ReservationPackagesScheduledRequest("HotelId",
        List.of(roomReservation, roomReservation2, roomReservation3));
  }

  @Nullable
  private String getEncryptedText(String token) {
    String encryptedText;
    if (!Strings.isEmpty(token)) {
      tokenUtilsMock.close();
      encryptedText = TokenUtils.getToken(token);
    } else {
      encryptedText = null;
    }
    return encryptedText;
  }

  @Test
  void debugRoomTypeValidation_UserScenario_ShouldShowValidationBehavior() {
    var reservationRequest = createUserReservationRequest();
    var basket = ManageReservationUtils.mockBasketResponse();
    basket.setHotelId("GATGAT");
    var mockAccount = uk.co.whitbread.shared.auth.account.Account.builder()
        .accessLevel("SUPER")
        .build();

    var mockedFeatureFlag = mock(FeatureFlag.class);
    var mockedFeatureAemSearchRules = mock(FeatureFlag.Feature.class);
    
    when(unleashWrapper.featureFlag()).thenReturn(mockedFeatureFlag);
    when(mockedFeatureFlag.getAemSearchRules()).thenReturn(mockedFeatureAemSearchRules);
    when(unleashWrapper.isEnabled(mockedFeatureFlag.getAemSearchRules())).thenReturn(true);

    // Mock the search rules response that the user got from the content service
    when(contentOutPort.getSearchRules(anyString(), any())).thenReturn(createUserSearchRules());
    
    when(basketOutPort.getBasketById(TEMPORARY_BASKET_REF)).thenReturn(basket);
    when(authenticatedUserService.getCurrentUserAccount()).thenReturn(Optional.ofNullable(mockAccount));
    when(contentOutPort.getHotelInformation(any(), any(), any())).thenReturn(
        HotelInfoResponse.builder().brand("PI").build());
    
    tokenUtilsMock.when(() -> TokenUtils.isValid(anyString(), anyString())).thenReturn(Boolean.TRUE);

    Exception exception = assertThrows(GenericBadRequestException.class,
        () -> reservationInPort.addNewRoomToExistingBasket(
            TEMPORARY_BASKET_REF, reservationRequest, null));
    
    String expectedMessage = "Wrong room type for number of adults and children";
    String actualMessage = exception.getMessage();
    
    System.out.println("=== DEBUG INFO ===");
    System.out.println("Request room type: " + reservationRequest.getReservations().get(0).getRoomRates().getPmsRoomType());
    System.out.println("Request adults: " + reservationRequest.getReservations().get(0).getAdultsNumber());
    System.out.println("Request children: " + reservationRequest.getReservations().get(0).getChildrenNumber());
    System.out.println("Channel: " + reservationRequest.getBookingChannel().getChannel());
    System.out.println("Exception message: " + actualMessage);
    System.out.println("==================");

    assertTrue(actualMessage.contains(expectedMessage));
  }

  private ReservationRequest createUserReservationRequest() {
    RoomRate roomRate = RoomRate.builder()
        .startDate("2025-12-21")
        .endDate("2025-12-22")
        .pmsRoomType("DOUBLE")
        .ratePlanCode("FLEXRATE")
        .cellCode("ABC")
        .promoKind(UNIQUE)
        .promotionCode("PROMOCODE")
        .build();

    LeadGuest leadGuest = LeadGuest.builder()
        .firstName("Mudita")
        .lastName("Goyala")
        .title("Mr")
        .emailAddress("rahul.kumar1@whitbread.com")
        .build();

    Reservation reservation = Reservation.builder()
        .hotelId("GATGAT")
        .arrival("2025-12-21")
        .departure("2025-12-22")
        .adultsNumber(2)
        .childrenNumber(0)
        .roomRates(roomRate)
        .leadGuest(leadGuest)
        .externalReferenceId("AJK-1d671e97-1227-4a53-88fc-bc328bd1b947")
        .bookingType(BOOKING_TYPE_ANON)
        .build();

    BookingChannel bookingChannel = BookingChannel.builder()
        .channel("DISTR")
        .subchannel("AGENCY")
        .language("en")
        .build();

    return ReservationRequest.builder()
        .reservations(List.of(reservation))
        .bookingChannel(bookingChannel)
        .token(getMockedToken())
        .isOta(true)
        .build();
  }

  private SearchRules createUserSearchRules() {
    return SearchRules.builder()
        .maxNights(99)
        .maxRooms(9)
        .maxRoomsAmend(9)
        .maxArrivalDate(364)
        .roomOccupancies(List.of(
            RoomOccupancy.builder()
                .adultsNumber(2)
                .childrenNumber(0)
                .acceptedRoomTypes(List.of("DB", "TWIN", "DIS", "FAM"))
                .build(),
            RoomOccupancy.builder()
                .adultsNumber(1)
                .childrenNumber(0)
                .acceptedRoomTypes(List.of("SB", "DB", "TWIN", "DIS", "FAM"))
                .build(),
            RoomOccupancy.builder()
                .adultsNumber(1)
                .childrenNumber(1)
                .acceptedRoomTypes(List.of("DB", "TWIN", "DIS", "FAM"))
                .build(),
            RoomOccupancy.builder()
                .adultsNumber(2)
                .childrenNumber(1)
                .acceptedRoomTypes(List.of("DIS", "FAM"))
                .build()
        ))
        .build();
  }

  @ParameterizedTest
  @CsvSource(value = {
            "NULL, NULL, CC",
            "BU, 1, PIBA_CP",
            "bd, 2, PIBA_CNP",
            "OTHER, NULL, CC",
            "BU, NULL, CC",
            "OTHER, 1, CC",
            "OTHER, 3, CC"
    }, nullValues = {"NULL"})
  void testGetPaymentOptionForFolioView(String paymentMethod, Integer folioView, String expected){
    String result = reservationInPort.getPaymentOptionForFolioView(paymentMethod, folioView);
    assertEquals(expected, result);
  }

  @Test
  void createDepositFolios_shouldSaveDepositFolios() {
    // Arrange
    DepositFoliosRequest depositFoliosRequest = Mockito.mock(DepositFoliosRequest.class);
    // Act
    reservationInPort.saveDepositFolios(depositFoliosRequest);
    // Assert
    Mockito.verify(reservationOutPort, Mockito.times(1)).saveDepositFolios(depositFoliosRequest);
    Mockito.verifyNoMoreInteractions(reservationOutPort);
  }
}
