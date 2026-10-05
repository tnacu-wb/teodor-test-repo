package uk.co.whitbread.basket.domain.logic;

import static java.math.BigDecimal.valueOf;
import static java.util.Arrays.asList;
import static java.util.Collections.emptyList;
import static java.util.Collections.emptySet;
import static java.util.List.of;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.comparesEqualTo;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.anySet;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;
import static uk.co.whitbread.basket.domain.logic.BasketInPortImpl.ALERT_AREA_CHECKIN;
import static uk.co.whitbread.basket.domain.logic.BasketInPortImpl.ALERT_CODE_CIOL;
import static uk.co.whitbread.basket.domain.logic.BasketInPortImpl.ALERT_DESCRIPTION_CIOL;
import static uk.co.whitbread.basket.domain.logic.BasketInPortImpl.BASKET_ITEM_TYPE_STAY;
import static uk.co.whitbread.basket.domain.logic.utils.BusinessAllowancesUtils.buildBusinessItemsFromBusinessAccount;
import static uk.co.whitbread.basket.domain.model.basket.out.BasketStatus.CIOL_FAILED;
import static uk.co.whitbread.basket.domain.model.basket.out.BasketStatus.COMPLETED;
import static uk.co.whitbread.basket.domain.model.basket.out.BasketStatus.FAILED;
import static uk.co.whitbread.basket.domain.model.basket.out.BasketStatus.PRE_CHECKED_IN;
import static uk.co.whitbread.basket.domain.model.basket.out.BasketStatus.PRE_CHECKED_OUT;
import static uk.co.whitbread.basket.domain.model.basket.out.BasketStatus.SECURE_FAILED;
import static uk.co.whitbread.basket.domain.model.payments.in.PaymentOption.PAY_NOW;

import io.micrometer.tracing.Tracer;
import java.math.BigDecimal;
import java.time.Clock;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.time.temporal.ChronoUnit;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.apache.commons.lang3.tuple.Pair;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.EnumSource;
import org.junit.jupiter.params.provider.NullSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.Arguments;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.co.whitbread.basket.domain.exception.BasketItemException;
import uk.co.whitbread.basket.domain.exception.BasketReferenceNotValidException;
import uk.co.whitbread.basket.domain.exception.BookingReferenceNotFoundException;
import uk.co.whitbread.basket.domain.exception.ErrorCode;
import uk.co.whitbread.basket.domain.exception.PreCheckinOutstandingBalanceException;
import uk.co.whitbread.basket.domain.exception.PreconditionFailedException;
import uk.co.whitbread.basket.domain.exception.UnSupportedCardTypeException;
import uk.co.whitbread.basket.domain.logic.config.DistributionProperties;
import uk.co.whitbread.basket.domain.logic.utils.BusinessAllowancesUtils;
import uk.co.whitbread.basket.domain.model.basket.in.AddBasketItem;
import uk.co.whitbread.basket.domain.model.basket.in.AddBasketItemRequest;
import uk.co.whitbread.basket.domain.model.basket.in.AddBasketItemType;
import uk.co.whitbread.basket.domain.model.basket.in.BookerAddress;
import uk.co.whitbread.basket.domain.model.basket.in.BookerDetails;
import uk.co.whitbread.basket.domain.model.basket.in.CancelBasketRequest;
import uk.co.whitbread.basket.domain.model.basket.in.Charge;
import uk.co.whitbread.basket.domain.model.basket.in.ChargeAmount;
import uk.co.whitbread.basket.domain.model.basket.in.ConfirmItemProcessingRequest;
import uk.co.whitbread.basket.domain.model.basket.in.CreateBasketRequest;
import uk.co.whitbread.basket.domain.model.basket.in.PackagesSelection;
import uk.co.whitbread.basket.domain.model.basket.in.PrepaidDeposit;
import uk.co.whitbread.basket.domain.model.basket.in.PrepaidDeposits;
import uk.co.whitbread.basket.domain.model.basket.in.PrepaidDepositsRequest;
import uk.co.whitbread.basket.domain.model.basket.in.PromotionsInformationRequest;
import uk.co.whitbread.basket.domain.model.basket.in.ReservationGuestRequest;
import uk.co.whitbread.basket.domain.model.basket.in.ReservationPackagesRequest;
import uk.co.whitbread.basket.domain.model.basket.in.RoomsSelections;
import uk.co.whitbread.basket.domain.model.basket.in.StayingGuest;
import uk.co.whitbread.basket.domain.model.basket.in.StayingGuestAddress;
import uk.co.whitbread.basket.domain.model.basket.in.StayingGuestDetails;
import uk.co.whitbread.basket.domain.model.basket.in.UpdateAllowancesRequest;
import uk.co.whitbread.basket.domain.model.basket.in.UpdateBasketItemSupplement;
import uk.co.whitbread.basket.domain.model.basket.out.Basket;
import uk.co.whitbread.basket.domain.model.basket.out.BasketError;
import uk.co.whitbread.basket.domain.model.basket.out.BasketErrorType;
import uk.co.whitbread.basket.domain.model.basket.out.BasketItem;
import uk.co.whitbread.basket.domain.model.basket.out.BasketStatus;
import uk.co.whitbread.basket.domain.model.basket.out.BookingAllowance;
import uk.co.whitbread.basket.domain.model.basket.out.CleanUpTime;
import uk.co.whitbread.basket.domain.model.basket.out.ConfirmReservationRequest;
import uk.co.whitbread.basket.domain.model.basket.out.ConfirmReservationResponse;
import uk.co.whitbread.basket.domain.model.basket.out.PaymentCard;
import uk.co.whitbread.basket.domain.model.basket.out.PromoKind;
import uk.co.whitbread.basket.domain.model.basket.out.ReservationProfiles;
import uk.co.whitbread.basket.domain.model.basket.out.ReservationInfoPaymentType;
import uk.co.whitbread.basket.domain.model.basket.out.ReservationPaymentCardType;
import uk.co.whitbread.basket.domain.model.content.out.AcceptedCreditCard;
import uk.co.whitbread.basket.domain.model.content.out.BusinessNote;
import uk.co.whitbread.basket.domain.model.content.out.BusinessNotesResponse;
import uk.co.whitbread.basket.domain.model.content.out.HotelPaymentInformation;
import uk.co.whitbread.basket.domain.model.content.out.Note;
import uk.co.whitbread.basket.domain.model.feature.FeatureFlag;
import uk.co.whitbread.basket.domain.model.feature.FeatureFlag.Feature;
import uk.co.whitbread.basket.domain.model.feature.UnleashWrapper;
import uk.co.whitbread.basket.domain.model.payments.in.Allowance;
import uk.co.whitbread.basket.domain.model.payments.in.Amount;
import uk.co.whitbread.basket.domain.model.payments.in.Billing;
import uk.co.whitbread.basket.domain.model.payments.in.Booking;
import uk.co.whitbread.basket.domain.model.payments.in.BusinessAccount;
import uk.co.whitbread.basket.domain.model.payments.in.BusinessSite;
import uk.co.whitbread.basket.domain.model.payments.in.Card;
import uk.co.whitbread.basket.domain.model.payments.in.Guest;
import uk.co.whitbread.basket.domain.model.payments.in.Payment;
import uk.co.whitbread.basket.domain.model.payments.in.PaymentOption;
import uk.co.whitbread.basket.domain.model.payments.in.PaymentRequest;
import uk.co.whitbread.basket.domain.model.payments.in.RoomType;
import uk.co.whitbread.basket.domain.model.payments.out.BasketPaymentStatus;
import uk.co.whitbread.basket.domain.model.promotion.out.RedeemPromoCodeResponse;
import uk.co.whitbread.basket.domain.model.reservation.in.ReservationAlertsRequest;
import uk.co.whitbread.basket.domain.model.reservation.out.Deposits;
import uk.co.whitbread.basket.domain.model.reservation.out.ReservationByBasketRefResponse;
import uk.co.whitbread.basket.domain.model.rules.out.BusinessAllowanceRule;
import uk.co.whitbread.basket.domain.model.rules.out.BusinessAllowanceRuleResponse;
import uk.co.whitbread.basket.domain.ports.primary.BasketInPort;
import uk.co.whitbread.basket.domain.ports.secondary.BasketOrderOutPort;
import uk.co.whitbread.basket.domain.ports.secondary.BasketOutPort;
import uk.co.whitbread.basket.domain.ports.secondary.BookingCompletedOutPort;
import uk.co.whitbread.basket.domain.ports.secondary.ContentOutPort;
import uk.co.whitbread.basket.domain.ports.secondary.DepositFolioOutPort;
import uk.co.whitbread.basket.domain.ports.secondary.HotelReservationOutPort;
import uk.co.whitbread.basket.domain.ports.secondary.MarketingOutPort;
import uk.co.whitbread.basket.domain.ports.secondary.PromoOutPort;
import uk.co.whitbread.basket.domain.ports.secondary.RefundOutPort;
import uk.co.whitbread.basket.domain.ports.secondary.RulesAgentOutPort;
import uk.co.whitbread.basket.generated.models.ohip.UdfsRequestDto;
import uk.co.whitbread.basket.generated.models.promotion.RedeemStatus;
import uk.co.whitbread.basket.generated.models.reservation.UniqueIDTypeDto;
import uk.co.whitbread.basket.infrastructure.rest.client.reservation.HotelReservationOutPortImpl;
import uk.co.whitbread.basket.infrastructure.rest.client.reservation.service.ReservationClient;
import uk.co.whitbread.basket.infrastructure.rest.controller.basket.mapper.UpdateReservationRequestMapper;
import uk.co.whitbread.basket.infrastructure.rest.controller.payments.mapper.PaymentMapper;
import uk.co.whitbread.shared.auth.security.AuthenticatedUserService;
import uk.co.whitbread.shared.commons.logging.trace.ConcurrentTracer;

@ExtendWith(MockitoExtension.class)
class BasketInPortImplTest {

  private static final String ERROR_MESSAGE = "Request resource is out of sync with server resource. "
      + "Error for basket with reference %s. Mismatching eTags: request eTag->\"%s\"; "
      + "basket eTag->\"%s\"";
  private static final String BASKET_NOT_COMPLETED_ERROR_MESSAGE = "Cannot confirm pre-check-in: "
      + "basket %s is not in completed state.";
  private static final Instant FIXED_INSTANT = Instant.parse("2024-10-21T15:00:00Z");
  private static final long CLEAN_UP_TIME_LONG = 5000;

  private final String BASKET_ID = "ABC-".concat(UUID.randomUUID().toString());
  private BasketInPortImpl basketInPort;
  @Mock
  private BasketOutPort basketOutPort;
  @Mock
  private DepositFolioOutPort depositFolioOutPort;
  @Mock
  private EmailNotificationService emailNotificationService;
  @Mock
  private RefundOutPort refundOutPort;
  @Mock
  private BasketOrderOutPort basketOrderOutPort;
  @Mock
  private BookingCompletedOutPort bookingCompletedOutPort;
  @Mock
  private HotelReservationOutPort hotelReservationOutPort;
  @Mock
  private HotelReservationOutPortImpl hotelReservationOutPortImpl;
  @Mock
  private MarketingOutPort marketingOutPort;
  @Spy
  private ConcurrentTracer concurrentTracer = new ConcurrentTracer(Tracer.NOOP);
  @Mock
  private AuthenticatedUserService authenticatedUserService;
  @Mock
  private RulesAgentOutPort rulesAgentOutPort;
  @Mock
  private ContentOutPort contentOutPort;
  @Mock
  private DistributionProperties distributionProperties;

  @Mock
  private ReservationClient reservationClient;
  private static Map<Integer, Allowance> mealsMap;
  @Mock
  private UpdateReservationRequestMapper updateReservationRequestMapper;

  @Mock
  private PaymentMapper paymentMapper;

  @Mock
  private BasketInPort basketInPortInt;

  @Mock
  private CleanUpTime cleanUpTime;

  @Mock
  private UnleashWrapper<FeatureFlag> unleashWrapper;

  @Mock
  private PromoOutPort promoOutPort;

  private MockedStatic<Instant> mockedStatic;

  @BeforeEach
  void setUp() {
    mealsMap = buildMealsMap();
    basketInPort = new BasketInPortImpl(60000L, cleanUpTime, basketOutPort,
        depositFolioOutPort, emailNotificationService, refundOutPort, basketOrderOutPort,
        bookingCompletedOutPort,
        hotelReservationOutPort, marketingOutPort, concurrentTracer, authenticatedUserService,
        60000L, rulesAgentOutPort, contentOutPort, distributionProperties, unleashWrapper,
        promoOutPort);
    mockedStatic = mockStatic(Instant.class, Mockito.CALLS_REAL_METHODS);
    FeatureFlag defaultFf = mock(FeatureFlag.class);
    Feature publishBookingCompletedFeature = mock(Feature.class);
    lenient().when(unleashWrapper.featureFlag()).thenReturn(defaultFf);
    lenient().when(defaultFf.getPublishDatatransBookingCompletedEvent()).thenReturn(publishBookingCompletedFeature);
    lenient().when(unleashWrapper.isEnabled(publishBookingCompletedFeature)).thenReturn(true);
  }

  @AfterEach
  public void destroy() {
    mockedStatic.close();
  }

  private static String getTimestampStr(String epochTime) {
    return String.valueOf(Instant.parse(epochTime).toEpochMilli());
  }

  @Test
  void testUpdateBasketItemOccupancy() {
    // Prepare test data
    UpdateBasketItemSupplement item = UpdateBasketItemSupplement.builder()
        .sourceId("123")
        .hasOccupancySup(true)
        .build();
    var updateBasketItemRequest = List.of(item);

    // Mocking the basket retrieval and update behavior
    var basketRef = "testBasketRef";
    Basket mockBasket = Basket.
        builder()
        .reference(basketRef)
        .createdAt("2022-02-01T12:26:56Z")
        .lastModifiedAt("2022-02-03T12:26:56Z")
        .items(List.of(BasketItem.builder().sourceId("123").hasOccupancySup(true).build(),
            BasketItem.builder().sourceId("456").hasOccupancySup(false).build()))
        .build();
    when(basketOutPort.getBasketById(basketRef)).thenReturn(mockBasket);
    when(basketOutPort.updateBasket(any(Basket.class))).thenReturn(mockBasket);

    // Invoke the method under test
    Basket result = basketInPort.updateBasketItemOccupancy(basketRef, updateBasketItemRequest,
        "1643891216000");

    // Verify the interactions
    verify(basketOutPort, times(1)).getBasketById(basketRef);
    verify(basketOutPort, times(1)).updateBasket(any(Basket.class));

    // Assert the expected results
    assertNotNull(result, "The result should not be null");
    assertEquals(basketRef, result.getReference(), "The basket reference should match");
  }

  @Test
  void testCreateBasket() {
    // Arrange
    when(basketOutPort.createBasket(any(CreateBasketRequest.class))).thenReturn(mockBasket());

    // Act
    CreateBasketRequest createBasketRequest = CreateBasketRequest.builder()
        .hotelId("TST")
        .userId("USR")
        .paymentOption(PaymentOption.PAY_ON_ARRIVAL)
        .build();

    Basket basket = basketInPort.createBasket(createBasketRequest);

    // Assert
    assertThat(basket.getHotelId(), is("TST"));
    assertThat(basket.getReference(), is("TST1231231"));
    assertThat(basket.getPaymentOption(), is("PAY_ON_ARRIVAL"));
    assertEquals("CTX", basket.getIdContext());
  }

  @Test
  void testUpdateBusinessAllowances_success() {
    // Arrange
    final var reference = "ABC123456";
    final var hotelId = "ABC";
    final var createdAt = "2022-02-03T12:26:56Z";
    final var userId = "userId";
    final var paymentId = "paymentId";

    final var basket = Basket.builder().reference(reference)
        .basketId(BASKET_ID)
        .hotelId(hotelId)
        .status(BasketStatus.OPEN)
        .userId(userId)
        .createdAt(createdAt)
        .sendMail(true)
        .paymentID(paymentId)
        .build();

    when(basketOutPort.getBasketById(BASKET_ID)).thenReturn(basket);
    when(basketOutPort.updateBasket(any(Basket.class))).thenAnswer(i -> i.getArguments()[0]);

    // Act
    final var allowance1 = BookingAllowance.builder().allowance("allowance1")
        .budget("25.0").build();
    final var allowance2 = BookingAllowance.builder().allowance("allowance2")
        .budget("10.0").build();

    final var updateAllowancesRequest = UpdateAllowancesRequest.builder()
        .bookingAllowances(List.of(allowance1, allowance2))
        .build();

    final var basketResult = basketInPort.updateAllowances(BASKET_ID, updateAllowancesRequest,
        getTimestampStr(createdAt));

    // Assert
    assertThat(basketResult.getCreatedAt(), is(createdAt));
    assertThat(basketResult.getHotelId(), is(hotelId));
    assertThat(basketResult.getReference(), is(reference));
    assertThat(basketResult.getUserId(), is(userId));
    assertThat(basketResult.getStatus(), is(BasketStatus.OPEN));
    assertThat(basketResult.getLastModifiedAt(), notNullValue());
    assertThat(basketResult.getPaymentID(), is(paymentId));

    final var allowances = basketResult.getBookingAllowances();
    assertThat(allowances, hasSize(2));
    assertThat(allowances, containsInAnyOrder(allowance1, allowance2));
  }

  @Test
  void testUpdateBusinessAllowances_lastModifyTimestampPreconditionViolated() {
    // Arrange
    final var reference = "ABC123456";
    final var hotelId = "ABC";
    final var createdAt = "2022-02-03T12:26:56Z";
    final var userId = "userId";
    final var paymentId = "paymentId";

    final var basket = Basket.builder().reference(reference)
        .basketId(BASKET_ID)
        .hotelId(hotelId)
        .status(BasketStatus.OPEN)
        .userId(userId)
        .createdAt(createdAt)
        .sendMail(true)
        .paymentID(paymentId)
        .build();

    when(basketOutPort.getBasketById(BASKET_ID)).thenReturn(basket);

    // Act
    final var allowance1 = BookingAllowance.builder().allowance("allowance1")
        .budget("25.0").build();
    final var allowance2 = BookingAllowance.builder().allowance("allowance2")
        .budget("10.0").build();

    final var updateAllowancesRequest = UpdateAllowancesRequest.builder()
        .bookingAllowances(List.of(allowance1, allowance2))
        .build();

    final var requestModifyTimestamp = getTimestampStr("2022-01-03T12:26:56Z");
    final var message = String.format(ERROR_MESSAGE, reference, requestModifyTimestamp,
        getTimestampStr(createdAt));

    // Assert
    var exception = assertThrows(PreconditionFailedException.class,
        () -> basketInPort.updateAllowances(BASKET_ID, updateAllowancesRequest,
            requestModifyTimestamp));

    // Assert
    assertEquals(ErrorCode.DIGITAL_OUT_OF_SYNC_EXCEPTION.getCode(), exception.getErrorCode());
    assertEquals(ErrorCode.DIGITAL_OUT_OF_SYNC_EXCEPTION.getMessage(),
        exception.getGlobalErrTextTemplate());
    assertEquals(message, exception.getDebugMessage());
  }

  @Test
  void testAddItem_success() {
    // Arrange
    final var reference = "ABC123456";
    final var hotelId = "ABC";
    final var createdAt = "2022-02-03T12:26:56Z";
    final var userId = "userId";
    final var paymentId = "paymentId";

    final var basket = Basket.builder().reference(reference)
        .basketId(BASKET_ID)
        .hotelId(hotelId)
        .status(BasketStatus.OPEN)
        .userId(userId)
        .createdAt(createdAt)
        .sendMail(true)
        .paymentID(paymentId)
        .build();

    when(basketOutPort.getBasketById(BASKET_ID)).thenReturn(basket);
    when(basketOutPort.updateBasket(any(Basket.class))).thenAnswer(i -> i.getArguments()[0]);

    // Act
    final var sourceId1 = "sourceId1";
    final var sourceId2 = "sourceId2";
    final var type1 = "type1";
    final var type2 = "type2";
    final var lockingTime = "2022-02-04T12:26:56Z";
    final var detail11 = "d11";
    final var detail12 = "d12";
    final var detail21 = "d21";
    final var detail22 = "d22";
    final var confirmData11 = "c11";
    final var confirmData12 = "c12";
    final var confirmData21 = "c21";
    final var confirmData22 = "c22";

    final var item1 = AddBasketItem.builder().details(Map.of(detail11, detail12))
        .sourceId(sourceId1).type(type1).build();
    final var item2 = AddBasketItem.builder().details(Map.of(detail21, detail22))
        .sourceId(sourceId2).type(type2).build();

    final var itemType1 = AddBasketItemType.builder().type(type1)
        .confirmationData(List.of(confirmData11, confirmData12)).build();
    final var itemType2 = AddBasketItemType.builder().type(type2)
        .confirmationData(List.of(confirmData21, confirmData22)).build();

    final var addBasketItemRequest = AddBasketItemRequest.builder()
        .items(List.of(item1, item2))
        .itemTypes(List.of(itemType1, itemType2))
        .reference(BASKET_ID)
        .lockingTime(lockingTime)
        .build();
    when(cleanUpTime.getCleanUpTime(any(), any())).thenReturn(CLEAN_UP_TIME_LONG);

    final var basketResult = basketInPort.addBasketItem(addBasketItemRequest,
        getTimestampStr(createdAt));

    // Assert
    assertThat(basketResult.getCreatedAt(), is(createdAt));
    assertThat(basketResult.getHotelId(), is(hotelId));
    assertThat(basketResult.getReference(), is(reference));
    assertThat(basketResult.getUserId(), is(userId));
    assertThat(basketResult.getStatus(), is(BasketStatus.OPEN));
    assertThat(basketResult.getLastModifiedAt(), notNullValue());
    assertThat(basketResult.getLockingTime(), is(lockingTime));
    assertThat(basket.getSendMail(), is(true));
    assertThat(basketResult.getCleanUpTime(), is(CLEAN_UP_TIME_LONG));
    assertThat(basketResult.getPaymentID(), is(paymentId));

    final var items = basketResult.getItems();
    assertThat(items, hasSize(2));

    final var itemsMap = items.stream().collect(Collectors.toMap(
        BasketItem::getType, item -> Pair.of(item.getSourceId(), item.getDetails())));

    final var itemDataPair1 = itemsMap.get(type1);
    assertThat(itemDataPair1, notNullValue());
    assertThat(itemDataPair1.getLeft(), is(sourceId1));
//  assertThat(itemDataPair1.getRight(), containsInAnyOrder(detail11, detail12));

    final var itemDataPair2 = itemsMap.get(type2);
    assertThat(itemDataPair2, notNullValue());
    assertThat(itemDataPair2.getLeft(), is(sourceId2));
//  assertThat(itemDataPair2.getRight(), containsInAnyOrder(detail21, detail22));

    final var itemTypes = basketResult.getItemTypes();
    assertThat(itemTypes.keySet(), hasSize(2));

    final var confirmationData1 = itemTypes.get(type1);
    assertThat(confirmationData1, containsInAnyOrder(confirmData11, confirmData12));

    final var confirmationData2 = itemTypes.get(type2);
    assertThat(confirmationData2, containsInAnyOrder(confirmData21, confirmData22));
  }

  @Test
  void testAddItem_success_lastTypeConfirmDataOverridesPreviousConfirmData() {
    // Arrange
    final var reference = "ABC123456";
    final var hotelId = "ABC";
    final var createdAt = "2022-02-03T12:26:56Z";

    final var basket = Basket.builder()
        .basketId(BASKET_ID)
        .reference(reference)
        .hotelId(hotelId)
        .status(BasketStatus.OPEN)
        .createdAt(createdAt)
        .build();

    when(basketOutPort.getBasketById(BASKET_ID)).thenReturn(basket);
    when(basketOutPort.updateBasket(any(Basket.class))).thenAnswer(i -> i.getArguments()[0]);

    // Act
    final var type = "type";
    final var source = "pms";
    final var confirmData11 = "c11";
    final var confirmData12 = "c12";
    final var confirmData21 = "c21";
    final var confirmData22 = "c22";

    final var addBasketItem = AddBasketItem.builder().type(type)
        .sourceId(source).build();
    final var itemType1 = AddBasketItemType.builder().type(type)
        .confirmationData(List.of(confirmData11, confirmData12)).build();
    final var itemType2 = AddBasketItemType.builder().type(type)
        .confirmationData(List.of(confirmData21, confirmData22)).build();

    final var addBasketItemRequest = AddBasketItemRequest.builder()
        .items(Collections.singletonList(addBasketItem))
        .itemTypes(List.of(itemType1, itemType2))
        .reference(BASKET_ID)
        .build();

    final var basketResult = basketInPort.addBasketItem(addBasketItemRequest,
        getTimestampStr(createdAt));

    //Assert
    final var itemTypes = basketResult.getItemTypes();
    assertThat(itemTypes.keySet(), hasSize(1));

    final var confirmationData = itemTypes.get(type);
    assertThat(confirmationData, containsInAnyOrder(confirmData21, confirmData22));
  }

  @Test
  void testAddMigratedReservationItem_success() {
    // Arrange
    final var reference = "ABC123456";
    final var hotelId = "ABC";
    final var createdAt = "2022-02-03T12:26:56Z";

    final var basket = Basket.builder()
        .basketId(BASKET_ID)
        .reference(reference)
        .hotelId(hotelId)
        .status(BasketStatus.CANCELLED)
        .createdAt(createdAt)
        .build();

    when(basketOutPort.getBasketById(BASKET_ID)).thenReturn(basket);
    when(basketOutPort.updateBasket(any(Basket.class))).thenAnswer(i -> i.getArguments()[0]);

    // Act
    final var type = "type";
    final var source = "pms";
    final var confirmData11 = "c11";
    final var confirmData12 = "c12";
    final var confirmData21 = "c21";
    final var confirmData22 = "c22";

    final var addBasketItem = AddBasketItem.builder().type(type)
        .sourceId(source).build();
    final var itemType1 = AddBasketItemType.builder().type(type)
        .confirmationData(List.of(confirmData11, confirmData12)).build();
    final var itemType2 = AddBasketItemType.builder().type(type)
        .confirmationData(List.of(confirmData21, confirmData22)).build();

    final var addBasketItemRequest = AddBasketItemRequest.builder()
        .items(Collections.singletonList(addBasketItem))
        .itemTypes(List.of(itemType1, itemType2))
        .reference(BASKET_ID)
        .migratedReservation(true)
        .build();

    final var basketResult = basketInPort.addBasketItem(addBasketItemRequest,
        getTimestampStr(createdAt));

    //Assert
    final var itemTypes = basketResult.getItemTypes();
    assertThat(itemTypes.keySet(), hasSize(1));

    final var confirmationData = itemTypes.get(type);
    assertThat(confirmationData, containsInAnyOrder(confirmData21, confirmData22));
  }

  @Test
  void testAddItem_lastModifyTimestampPreconditionViolated() {
    // Arrange
    final var reference = "ABC123456";
    final var hotelId = "ABC";
    final var type = "type";
    final var source = "source";
    final var createdAt = "2022-02-03T12:26:56Z";
    final var confirmData11 = "c11";
    final var confirmData12 = "c12";
    final var confirmData21 = "c21";
    final var confirmData22 = "c22";
    final var requestModifyTimestamp = getTimestampStr("2022-01-03T12:26:56Z");
    var message = String.format(ERROR_MESSAGE, reference, requestModifyTimestamp,
        getTimestampStr(createdAt));

    final var addBasketItem = AddBasketItem.builder().type(type)
        .sourceId(source).build();

    final var itemType1 = AddBasketItemType.builder().type(type)
        .confirmationData(List.of(confirmData11, confirmData12)).build();
    final var itemType2 = AddBasketItemType.builder().type(type)
        .confirmationData(List.of(confirmData21, confirmData22)).build();

    final var basket = Basket.builder()
        .basketId(BASKET_ID)
        .reference(reference)
        .hotelId(hotelId)
        .status(BasketStatus.OPEN)
        .createdAt(createdAt)
        .build();

    when(basketOutPort.getBasketById(BASKET_ID)).thenReturn(basket);

    // Act
    final var addBasketItemRequest = AddBasketItemRequest.builder()
        .items(Collections.singletonList(addBasketItem))
        .itemTypes(List.of(itemType1, itemType2))
        .reference(BASKET_ID)
        .build();


    // Assert
    var exception = assertThrows(PreconditionFailedException.class,
        () -> basketInPort.addBasketItem(addBasketItemRequest, requestModifyTimestamp));
    assertEquals(ErrorCode.DIGITAL_OUT_OF_SYNC_EXCEPTION.getCode(), exception.getErrorCode());
    assertEquals(ErrorCode.DIGITAL_OUT_OF_SYNC_EXCEPTION.getMessage(),
        exception.getGlobalErrTextTemplate());
    assertEquals(message, exception.getDebugMessage());
  }

  @Test
  void testAddItem_basketStatusPreconditionViolated() {
    // Arrange
    final var hotelId = "ABC";
    final var type = "type";
    final var source = "source";
    final var createdAt = "2022-02-03T12:26:56Z";
    final var confirmData11 = "c11";
    final var confirmData12 = "c12";
    final var confirmData21 = "c21";
    final var confirmData22 = "c22";
    final var channel = "PI";

    final var addBasketItem = AddBasketItem.builder().type(type)
        .sourceId(source).build();
    final var itemType1 = AddBasketItemType.builder().type(type)
        .confirmationData(List.of(confirmData11, confirmData12)).build();
    final var itemType2 = AddBasketItemType.builder().type(type)
        .confirmationData(List.of(confirmData21, confirmData22)).build();
    final var basket =
        Basket.builder().basketId(BASKET_ID).hotelId(hotelId).channel(channel)
            .status(BasketStatus.CANCELLED)
            .createdAt(createdAt).build();

    when(basketOutPort.getBasketById(BASKET_ID)).thenReturn(basket);

    // Act
    final var addBasketItemRequest = AddBasketItemRequest.builder()
        .items(Collections.singletonList(addBasketItem))
        .itemTypes(List.of(itemType1, itemType2))
        .reference(BASKET_ID)
        .build();
    final var requestModifyTimestamp = getTimestampStr(createdAt);

    // Assert
    assertThrows(PreconditionFailedException.class,
        () -> basketInPort.addBasketItem(addBasketItemRequest, requestModifyTimestamp));
  }

  @Test
  void testAddItem_preconditionNotViolated() {
    // Arrange
    final var reference = "ABCR234567";
    final var hotelId = "ABC";
    final var type = "type";
    final var source = "source";
    final var createdAt = "2022-02-03T12:26:56Z";
    final var confirmData11 = "c11";
    final var confirmData12 = "c12";
    final var confirmData21 = "c21";
    final var confirmData22 = "c22";
    final var channel = "PI";

    final var addBasketItem = AddBasketItem.builder().type(type)
        .sourceId(source).build();
    final var itemType1 = AddBasketItemType.builder().type(type)
        .confirmationData(List.of(confirmData11, confirmData12)).build();
    final var itemType2 = AddBasketItemType.builder().type(type)
        .confirmationData(List.of(confirmData21, confirmData22)).build();
    final var basket = Basket.builder().reference(reference).basketId(BASKET_ID).hotelId(hotelId)
        .channel(channel).status(BasketStatus.OPEN).createdAt(createdAt).build();

    when(basketOutPort.getBasketById(BASKET_ID)).thenReturn(basket);

    // Act
    final var addBasketItemRequest = AddBasketItemRequest.builder()
        .items(Collections.singletonList(addBasketItem))
        .itemTypes(List.of(itemType1, itemType2))
        .reference(BASKET_ID)
        .build();
    final var requestModifyTimestamp = getTimestampStr(createdAt);

    basketInPort.addBasketItem(addBasketItemRequest, requestModifyTimestamp);

    // Assert
    assertDoesNotThrow(
        () -> new PreconditionFailedException(ErrorCode.DIGITAL_ITEM_NOT_ADDED_EXCEPTION,
            "tems not added to basket with basketReference=%s. Basket status is basketStatus=%s"));
  }

  @ParameterizedTest
  @CsvSource({"OPEN", "COMPLETED", "CIOL_FAILED", "SECURE_FAILED"})
  void testRemoveItem_success(String basketStatus) {
    // Arrange
    final var reference = "ABCABC123456";
    final var itemId1 = "123";
    final var itemId2 = "456";
    final var createdAt = "2022-02-03T12:26:56Z";
    final var confirmData11 = "c11";

    final var basket = Basket.builder().reference(reference)
        .basketId(BASKET_ID)
        .status(BasketStatus.valueOf(basketStatus))
        .itemType("STAY",
            Set.of(confirmData11))
        .item(BasketItem.builder()
            .type("STAY")
            .sourceId(itemId1)
            .build())
        .item(BasketItem.builder()
            .type("STAY")
            .sourceId(itemId2)
            .build())
        .createdAt(createdAt).build();

    when(basketOutPort.getBasketById(BASKET_ID)).thenReturn(basket);
    when(basketOutPort.updateBasket(any(Basket.class))).thenAnswer(i -> i.getArguments()[0]);

    // Act
    final var requestModifyTimestamp = getTimestampStr(createdAt);
    final var updatedBasketEntity = basketInPort.removeBasketItems(BASKET_ID, List.of(itemId1),
        requestModifyTimestamp);

    // Assert
    verify(basketOutPort, times(1)).updateBasket(any(Basket.class));
    assertThat(updatedBasketEntity.getItems().size(), is(1));
    assertThat(updatedBasketEntity.getItems().get(0).getSourceId(), is("456"));
    assertThat(updatedBasketEntity.getItemTypes().size(), is(1));
    assertThat(updatedBasketEntity.getItemTypes().containsKey("STAY"), is(true));
  }

  @Test
  void testRemoveItemAlsoRemovesType_success() {
    // Arrange
    final var reference = "ABC123456";
    final var itemId1 = "123";
    final var createdAt = "2022-02-03T12:26:56Z";

    final var basket = Basket.builder().reference(reference)
        .basketId(BASKET_ID)
        .status(BasketStatus.OPEN)
        .itemType("STAY", emptySet())
        .item(BasketItem.builder()
            .type("STAY")
            .sourceId(itemId1)
            .build())
        .createdAt(createdAt).build();

    when(basketOutPort.getBasketById(BASKET_ID)).thenReturn(basket);
    when(basketOutPort.updateBasket(any(Basket.class))).thenAnswer(i -> i.getArguments()[0]);

    // Act
    final var requestModifyTimestamp = getTimestampStr(createdAt);
    final var updatedBasketEntity = basketInPort.removeBasketItems(BASKET_ID, List.of(itemId1),
        requestModifyTimestamp);

    // Assert
    verify(basketOutPort, times(1)).updateBasket(any(Basket.class));
    assertThat(updatedBasketEntity.getItems().size(), is(0));
    assertThat(updatedBasketEntity.getItemTypes().size(), is(0));
  }

  @Test
  void testRemoveItem_statusPreconditionViolated_processing() {
    // Arrange
    final var reference = "ABC123456";
    final var itemId = "123";
    final var createdAt = "2022-02-03T12:26:56Z";

    final var basket = Basket.builder()
        .basketId(BASKET_ID)
        .reference(reference)
        .status(BasketStatus.PROCESSING)
        .itemType("STAY", emptySet())
        .item(BasketItem.builder()
            .type("STAY")
            .sourceId(itemId)
            .build())
        .createdAt(createdAt).build();

    when(basketOutPort.getBasketById(BASKET_ID)).thenReturn(basket);

    // Act
    final var requestModifyTimestamp = getTimestampStr(createdAt);

    // Assert
    assertThrows(PreconditionFailedException.class,
        () -> basketInPort.removeBasketItems(BASKET_ID, List.of(itemId), requestModifyTimestamp));
  }

  @Test
  void testRemoveItem_statusPreconditionViolated_cancelled() {
    // Arrange
    final var reference = "ABCABC123456";
    final var itemId = "123";
    final var createdAt = "2022-02-03T12:26:56Z";

    final var basket = Basket.builder()
        .basketId(BASKET_ID)
        .reference(reference)
        .status(BasketStatus.CANCELLED)
        .itemType("STAY", emptySet())
        .item(BasketItem.builder()
            .type("STAY")
            .sourceId(itemId)
            .build())
        .createdAt(createdAt).build();

    when(basketOutPort.getBasketById(BASKET_ID)).thenReturn(basket);

    // Act
    final var requestModifyTimestamp = getTimestampStr(createdAt);

    // Assert
    assertThrows(PreconditionFailedException.class,
        () -> basketInPort.removeBasketItems(BASKET_ID, List.of(itemId), requestModifyTimestamp));
  }

  @Test
  void testRemoveItem_statusPreconditionViolated_failed() {
    // Arrange
    final var reference = "ABC123456";
    final var itemId = "123";
    final var createdAt = "2022-02-03T12:26:56Z";

    final var basket = Basket.builder()
        .basketId(BASKET_ID)
        .reference(reference)
        .status(BasketStatus.FAILED)
        .itemType("STAY", emptySet())
        .item(BasketItem.builder()
            .type("STAY")
            .sourceId(itemId)
            .build())
        .createdAt(createdAt).build();

    when(basketOutPort.getBasketById(BASKET_ID)).thenReturn(basket);

    // Act
    final var requestModifyTimestamp = getTimestampStr(createdAt);

    // Assert
    assertThrows(PreconditionFailedException.class,
        () -> basketInPort.removeBasketItems(BASKET_ID, List.of(itemId), requestModifyTimestamp));
  }

  @Test
  void testRemoveItem_lastModifyTimestampPreconditionViolated() {
    // Arrange
    final var reference = "ABCABC123456";
    final var itemId = "123";
    final var createdAt = "2022-02-03T12:26:56Z";

    final var basket = Basket.builder().reference(reference)
        .basketId(BASKET_ID)
        .status(BasketStatus.OPEN)
        .itemType("STAY", emptySet())
        .item(BasketItem.builder()
            .type("STAY")
            .sourceId(itemId)
            .build())
        .createdAt(createdAt).build();

    when(basketOutPort.getBasketById(BASKET_ID)).thenReturn(basket);

    // Act
    final var requestModifyTimestamp = getTimestampStr("2022-01-03T12:26:56Z");

    // Assert
    assertThrows(PreconditionFailedException.class,
        () -> basketInPort.removeBasketItems(BASKET_ID, List.of(itemId), requestModifyTimestamp));
  }

  @Test
  void testConfirmItemProcessing_basketStillProcessing() {
    // Arrange
    final var reference = "ABC123456";
    final var itemId = "123";
    final var request = ConfirmItemProcessingRequest.builder()
        .status(0)
        .reqAction("COMMIT")
        .build();

    when(basketOutPort.getBasketById(any(String.class))).thenReturn(
        mockBasket(null, null));
    when(basketOutPort.updateBasket(any(Basket.class))).thenReturn(
        mock(Basket.class));

    ArgumentCaptor<Basket> captor = ArgumentCaptor.forClass(Basket.class);

    // Act
    basketInPort.confirmItemProcessing(reference, itemId, request);

    // Assert
    verify(basketOutPort, times(1)).updateBasket(captor.capture());
    final var basketEntityResult = captor.getValue();
    assertThat(basketEntityResult.getStatus(), is(BasketStatus.PROCESSING));
    assertThat(basketEntityResult.getItems().stream().filter(i -> i.getSourceId().equals("123"))
        .findFirst().get()
        .getAck(), is(0));
  }

  @Test
  void testConfirmItemProcessing_finalItemCompleted() {
    // Arrange
    final var itemId = "123";
    final var request = ConfirmItemProcessingRequest.builder()
        .status(0)
        .reqAction("COMMIT")
        .build();

    when(basketOutPort.getBasketById(any())).thenAnswer(inv -> {
      Basket b = mockBasket(null, 0);
      b.getItems().forEach(i -> i.setReqAction("COMMIT"));
      return b;
    });
    when(basketOutPort.updateBasket(any(Basket.class))).thenReturn(
        mock(Basket.class));
    doNothing().when(emailNotificationService)
        .sendEmailNotificationEvent(any(), any(), any(), anyBoolean());
    mockUnleash(true,false, true);
    ArgumentCaptor<Basket> captor = ArgumentCaptor.forClass(Basket.class);

    // Act
    basketInPort.confirmItemProcessing(BASKET_ID, itemId, request);

    // Assert
    verify(basketOutPort, times(1)).updateBasket(captor.capture());
    final var basketEntityResult = captor.getValue();
    assertThat(basketEntityResult.getStatus(), is(COMPLETED));
    assertThat(basketEntityResult.getItems().stream().map(BasketItem::getAck).toList(),
        containsInAnyOrder(0, 0));
    verify(bookingCompletedOutPort, times(1)).publishBookingCompleted(any(Basket.class));
  }

  @Test
  void testConfirmItemProcessing_finalItemCompletedIsCheckInOnline() throws InterruptedException {
    // Arrange
    final var itemId = "123";
    final var request = ConfirmItemProcessingRequest.builder()
        .status(0)
        .reqAction("COMMIT")
        .build();

    var basket = mockBasketIsCheckInOnline(null, 0);
    when(basketOutPort.getBasketById(any(String.class))).thenReturn(basket);
    when(basketOutPort.updateBasket(any(Basket.class))).thenReturn(
        mock(Basket.class));
    mockUnleash(true,true,true);
    Feature mobilePreRegistered = mock(Feature.class);

    when(unleashWrapper.featureFlag().getMobilePreRegisteredRepurpose())
        .thenReturn(mobilePreRegistered);

    when(unleashWrapper.isEnabled(mobilePreRegistered))
        .thenReturn(false);
    var latch = new CountDownLatch(1);
    doAnswer(invocationOnMock -> {
      latch.countDown();
      return null;
    }).when(hotelReservationOutPort).updateReservationAlerts(any());

    doNothing().when(basketOutPort).updateCharacterUdfs(any());

    ArgumentCaptor<Basket> captor = ArgumentCaptor.forClass(Basket.class);
    ArgumentCaptor<ReservationAlertsRequest> alertCaptor =
        ArgumentCaptor.forClass(ReservationAlertsRequest.class);
    ArgumentCaptor<UdfsRequestDto> udfsCaptor =
        ArgumentCaptor.forClass(UdfsRequestDto.class);

    // Act
    basketInPort.confirmItemProcessing(BASKET_ID, itemId, request);

    // Assert
    verify(basketOutPort, times(1)).updateBasket(captor.capture());
    final var basketEntityResult = captor.getValue();
    assertThat(basketEntityResult.getStatus(), is(PRE_CHECKED_IN));
    assertThat(basketEntityResult.getItems().stream().map(BasketItem::getAck).toList(),
        containsInAnyOrder(0, 0));
    boolean called = latch.await(1, TimeUnit.SECONDS);
    assertTrue(called);
    verify(hotelReservationOutPort, times(1))
        .updateReservationAlerts(alertCaptor.capture());
    var alertRequest = alertCaptor.getValue();
    assertEquals(basket.getHotelId(), alertRequest.getHotelId());
    var reservationIds = basket.getItems().stream()
        .filter(basketItem -> BASKET_ITEM_TYPE_STAY.equals(basketItem.getType()))
        .map(BasketItem::getSourceId)
        .toList();
    reservationIds.forEach(reservationId -> {
      assertTrue(alertRequest.getReservationIds().contains(reservationId));
    });
    var alert = alertRequest.getAlerts().get(0);
    assertEquals(ALERT_AREA_CHECKIN, alert.getArea());
    assertEquals(ALERT_CODE_CIOL, alert.getCode());
    assertEquals(ALERT_DESCRIPTION_CIOL, alert.getDescription());
    assertTrue(alert.getScreenNotification());
    assertFalse(alert.getPrinterNotification());

    verify(basketOutPort).updateCharacterUdfs(udfsCaptor.capture());
    assertUdf(udfsCaptor.getValue(), basket.getHotelId(), reservationIds);

    verify(emailNotificationService, Mockito.times(0))
        .sendEmailNotificationEvent(any(), any(), any(), anyBoolean());
    verifyNoInteractions(bookingCompletedOutPort);
  }

  @ParameterizedTest
  @ValueSource(booleans = {false, true})
  void testConfirmItemProcessing_payNow(boolean isCheckInOnline) {
    // Arrange
    final var itemId = "123";
    final var request = ConfirmItemProcessingRequest.builder()
        .status(1)
        .reqAction("COMMIT")
        .build();

    var basket = mockPayNowBasket(null, 0);
    basket.setIsCheckInOnlinePay(isCheckInOnline);
    when(basketOutPort.getBasketById(any(String.class))).thenReturn(basket);
    when(basketOutPort.updateBasket(any(Basket.class))).thenReturn(
        mock(Basket.class));

    if (!isCheckInOnline) {
      doNothing().when(emailNotificationService)
          .sendEmailNotificationEvent(any(), any(), any(), anyBoolean());
    }

    doNothing().when(refundOutPort).processRefund(any(), any(), any());
    mockCheckInOnlineFeatureFlag(isCheckInOnline);

    ArgumentCaptor<Basket> captor = ArgumentCaptor.forClass(Basket.class);

    // Act
    basketInPort.confirmItemProcessing(BASKET_ID, itemId, request);

    // Assert
    verify(basketOutPort, times(1)).updateBasket(captor.capture());
    final var basketEntityResult = captor.getValue();
    assertThat(basketEntityResult.getPaymentStatus(), is(BasketPaymentStatus.REFUNDING));
    assertEquals(isCheckInOnline ? CIOL_FAILED: FAILED, basketEntityResult.getStatus());

    if (isCheckInOnline) {
      verify(emailNotificationService, times(0))
          .sendEmailNotificationEvent(any(), any(), any(), anyBoolean());

      verify(basketOrderOutPort, times(0))
          .processOrder(any(), any(), any(), anyString());
    }
  }

  @ParameterizedTest
  @ValueSource(booleans = {false, true})
  void testConfirmItemProcessing_payNow_SecureFailed(boolean isSecureBooking) {
    // Arrange
    final var itemId = "123";
    final var request = ConfirmItemProcessingRequest.builder()
        .status(1)
        .reqAction("COMMIT")
        .build();

    var basket = mockPayNowBasket(null, 0);
    basket.setIsSecureBooking(isSecureBooking);
    when(basketOutPort.getBasketById(any(String.class))).thenReturn(basket);
    when(basketOutPort.updateBasket(any(Basket.class))).thenReturn(
        mock(Basket.class));

    if (!isSecureBooking) {
      doNothing().when(emailNotificationService)
          .sendEmailNotificationEvent(any(), any(), any(), anyBoolean());
    }

    doNothing().when(refundOutPort).processRefund(any(), any(), any());
    var mockedFeatureFlag = mock(FeatureFlag.class);
    when(unleashWrapper.featureFlag()).thenReturn(mockedFeatureFlag);
    when(unleashWrapper.isEnabled(unleashWrapper.featureFlag().getCheckInOnline()))
        .thenReturn(false);
    when(unleashWrapper.isEnabled(unleashWrapper.featureFlag().getSaveSecureBooking()))
        .thenReturn(isSecureBooking);

    ArgumentCaptor<Basket> captor = ArgumentCaptor.forClass(Basket.class);

    // Act
    basketInPort.confirmItemProcessing(BASKET_ID, itemId, request);

    // Assert
    verify(basketOutPort, times(1)).updateBasket(captor.capture());
    final var basketEntityResult = captor.getValue();
    assertThat(basketEntityResult.getPaymentStatus(), is(BasketPaymentStatus.REFUNDING));
    assertEquals(isSecureBooking ? SECURE_FAILED : FAILED, basketEntityResult.getStatus());

    if (isSecureBooking) {
      verify(emailNotificationService, times(0))
          .sendEmailNotificationEvent(any(), any(), any(), anyBoolean());

      verify(basketOrderOutPort, times(0))
          .processOrder(any(), any(), any(), anyString());
    }
  }

  @ParameterizedTest
  @ValueSource(booleans = {false, true})
  void testConfirmItemProcessing_itemFailed(boolean isCheckInOnline) {
    // Arrange
    final var itemId = "123";
    final var request = ConfirmItemProcessingRequest.builder()
        .status(1)
        .reqAction("COMMIT")
        .build();
    mockInstantNow();
    mockCheckInOnlineFeatureFlag(isCheckInOnline);

    when(basketOutPort.getBasketById(any(String.class))).thenReturn(
        mockBasketIsCheckInOnline(null, 0));
    when(basketOutPort.updateBasket(any(Basket.class))).thenReturn(
        mock(Basket.class));

    if (!isCheckInOnline) {
      doNothing().when(emailNotificationService)
          .sendEmailNotificationEvent(any(), any(), any(), anyBoolean());
    }

    when(cleanUpTime.getCleanUpTime(any())).thenReturn(CLEAN_UP_TIME_LONG);

    ArgumentCaptor<Basket> captor = ArgumentCaptor.forClass(Basket.class);

    // Act
    basketInPort.confirmItemProcessing(BASKET_ID, itemId, request);

    // Assert
    verify(basketOutPort, times(1)).updateBasket(captor.capture());
    final var basketEntityResult = captor.getValue();
    assertThat(basketEntityResult.getStatus(),
        is(isCheckInOnline ? BasketStatus.CIOL_FAILED : BasketStatus.FAILED));
    assertThat(basketEntityResult.getCleanUpTime(), is(CLEAN_UP_TIME_LONG));
    assertThat(basketEntityResult.getItems().stream().map(BasketItem::getAck).toList(),
        containsInAnyOrder(0, 1));

    if (isCheckInOnline) {
      verify(emailNotificationService, times(0))
          .sendEmailNotificationEvent(any(), any(), any(), anyBoolean());

      verify(basketOrderOutPort, times(0))
          .processOrder(any(), any(), any(), anyString());
    }
  }

  @Test
  void testConfirmItemProcessing_BasketItemException() {
    // Arrange
    final var itemId = "123";
    final var request = ConfirmItemProcessingRequest.builder()
        .status(1)
        .reqAction("COMMIT")
        .build();
    var basket = Basket.builder()
        .status(BasketStatus.PROCESSING)
        .basketId(BASKET_ID)
        .reference("BSK123")
        .items(List.of(BasketItem.builder().sourceId("101")
            .build()))
        .paymentOption("PAY_ON_ARRIVAL")
        .build();
    when(basketOutPort.getBasketById(any(String.class))).thenReturn(basket);

    // Act
    var exception = assertThrows(BasketItemException.class,
        () -> basketInPort.confirmItemProcessing(BASKET_ID, itemId, request));

    // Assert
    assertEquals(ErrorCode.DIGITAL_BASKET_ITEM_FAILURE_EXCEPTION.getMessage(),
        exception.getGlobalErrTextTemplate());
    assertEquals("Basket item processing failure", exception.getDebugMessage());
    assertEquals(ErrorCode.DIGITAL_BASKET_ITEM_FAILURE_EXCEPTION.getCode(),
        exception.getErrorCode());
  }

  @Test
  void testConfirmItemProcessing_itemRolledBack() {
    // Arrange
    final var reference = "ABC123456";
    final var request = ConfirmItemProcessingRequest.builder()
        .status(0)
        .reqAction("ROLLBACK")
        .build();

    Basket basket = Basket.builder()
        .reference(reference)
        .paymentOption("PAY_NOW")
        .status(BasketStatus.FAILED)
        .paymentID("12345")
        .basketId(BASKET_ID)
        .item(BasketItem.builder()
            .reqAction("CANCEL")
            .ack(0)
            .sourceId("123")
            .build()).build();

    when(basketOutPort.getBasketById(any(String.class))).thenReturn(basket);
    when(basketOutPort.updateBasket(any(Basket.class))).thenReturn(
        mock(Basket.class));

    ArgumentCaptor<Basket> captor = ArgumentCaptor.forClass(Basket.class);

    // Act
    basketInPort.confirmRefundProcessing(reference, request);

    // Assert
    verify(basketOutPort, times(1)).updateBasket(captor.capture());
    final var basketEntityResult = captor.getValue();
    assertThat(basketEntityResult.getStatus(), is(BasketStatus.FAILED));
    assertThat(basketEntityResult.getPaymentStatus(), is(BasketPaymentStatus.REFUNDED));
  }

  @Test
  void testConfirmItemProcessing_amend() {
    // Arrange
    final var reference = "ABC123456";
    final var request = ConfirmItemProcessingRequest.builder()
        .status(0)
        .reqAction("AMEND")
        .build();

    Basket basket = Basket.builder()
        .reference(reference)
        .paymentOption("PAY_NOW")
        .status(BasketStatus.PROCESSING)
        .paymentID("12345")
        .basketId(BASKET_ID)
        .originalBasketId("DEFG12345")
        .item(BasketItem.builder()
            .reqAction("COMMIT")
            .ack(0)
            .sourceId("123")
            .build()).build();

    when(basketOutPort.getBasketById(any(String.class))).thenReturn(basket);
    when(basketOutPort.updateBasket(any(Basket.class))).thenReturn(
        mock(Basket.class));

    ArgumentCaptor<Basket> captor = ArgumentCaptor.forClass(Basket.class);

    // Act
    basketInPort.confirmAmendProcessing(reference, request);

    // Assert
    verify(basketOutPort, times(1)).updateBasket(captor.capture());
    verifyNoInteractions(emailNotificationService);
    final var basketEntityResult = captor.getValue();
    assertThat(basketEntityResult.getStatus(), is(BasketStatus.AMENDED));
    assertThat(basketEntityResult.getPaymentStatus(), is(BasketPaymentStatus.COMPLETED));
  }

  @Test
  void testConfirmItemProcessing_change_pay_a2c() {
    // Arrange
    final var request = ConfirmItemProcessingRequest.builder()
        .status(0)
        .reqAction("CHANGE_PAY")
        .build();
    var basket = Basket.builder()
        .status(BasketStatus.PROCESSING)
        .basketId(BASKET_ID)
        .reference("BSK123")
        .items(List.of(BasketItem.builder().sourceId("123")
            .build()))
        .paymentOption("ACCOUNT_COMPANY")
        .build();

    when(basketOutPort.getBasketById(any(String.class))).thenReturn(basket);
    when(basketOutPort.updateBasket(any(Basket.class))).thenReturn(
        mock(Basket.class));

    ArgumentCaptor<Basket> captor = ArgumentCaptor.forClass(Basket.class);

    // Act
    basketInPort.confirmChangePaymentProcessing(BASKET_ID, request);

    // Assert
    verify(basketOutPort, times(1)).updateBasket(captor.capture());
    final var basketEntityResult = captor.getValue();
    assertThat(basketEntityResult.getStatus(), is(COMPLETED));
    assertThat(basketEntityResult.getPaymentStatus(), is(BasketPaymentStatus.COMPLETED));
    verify(bookingCompletedOutPort, times(1)).publishBookingCompleted(any(Basket.class));
  }

  @Test
  void testConfirmItemProcessing_amend_payOnArrival() {
    // Arrange
    final var reference = "ABC123456";
    final var request = ConfirmItemProcessingRequest.builder()
        .status(0)
        .reqAction("AMEND")
        .build();

    Basket basket = Basket.builder()
        .reference(reference)
        .paymentOption("PAY_NOW")
        .status(BasketStatus.PROCESSING)
        .paymentID("12345")
        .basketId(BASKET_ID)
        .originalBasketId("DEFG12345")
        .item(BasketItem.builder()
            .reqAction("COMMIT")
            .ack(0)
            .sourceId("123")
            .build()).build();

    when(basketOutPort.getBasketById(any(String.class))).thenReturn(basket);
    when(basketOutPort.updateBasket(any(Basket.class))).thenReturn(
        mock(Basket.class));

    ArgumentCaptor<Basket> captor = ArgumentCaptor.forClass(Basket.class);

    // Act
    basketInPort.confirmAmendProcessing(reference, request);

    // Assert
    verify(basketOutPort, times(1)).updateBasket(captor.capture());
    verifyNoInteractions(emailNotificationService);
    final var basketEntityResult = captor.getValue();
    assertThat(basketEntityResult.getStatus(), is(BasketStatus.AMENDED));
    assertThat(basketEntityResult.getPaymentStatus(), is(BasketPaymentStatus.COMPLETED));
  }

  @Test
  void testConfirmItemProcessing_COMPLETED() {
    // Arrange
    final var reference = "ABC123456";
    final var request = ConfirmItemProcessingRequest.builder()
        .status(0)
        .reqAction("AMEND")
        .build();

    Basket basket = Basket.builder()
        .reference(reference)
        .paymentOption("PAY_NOW")
        .status(BasketStatus.PROCESSING)
        .paymentID("12345")
        .basketId(BASKET_ID)
        .item(BasketItem.builder()
            .reqAction("COMMIT")
            .ack(0)
            .sourceId("123")
            .build()).build();

    when(basketOutPort.getBasketById(any(String.class))).thenReturn(basket);
    when(basketOutPort.updateBasket(any(Basket.class))).thenReturn(
        mock(Basket.class));

    ArgumentCaptor<Basket> captor = ArgumentCaptor.forClass(Basket.class);

    // Act
    basketInPort.confirmAmendProcessing(reference, request);

    // Assert
    verify(basketOutPort, times(1)).updateBasket(captor.capture());
    verifyNoInteractions(emailNotificationService);
    final var basketEntityResult = captor.getValue();
    assertThat(basketEntityResult.getStatus(), is(COMPLETED));
    assertThat(basketEntityResult.getPaymentStatus(), is(BasketPaymentStatus.COMPLETED));
    verify(bookingCompletedOutPort, times(1)).publishBookingCompleted(any(Basket.class));
  }


  @Test
  void testConfirmItemProcessing_change_pay_Failed() {
    // Arrange
    final var reference = "ABC123456";
    final var request = ConfirmItemProcessingRequest.builder()
        .status(1)
        .reqAction("AMEND")
        .build();

    Basket basket = Basket.builder()
        .reference(reference)
        .paymentOption("PAY_NOW")
        .status(BasketStatus.PROCESSING)
        .paymentID("12345")
        .basketId(BASKET_ID)
        .originalBasketId("DEFG12345")
        .totalCost("30")
        .item(BasketItem.builder()
            .reqAction("COMMIT")
            .ack(0)
            .sourceId("123")
            .build()).build();

    when(basketOutPort.getBasketById(any(String.class))).thenReturn(basket);
    when(basketOutPort.updateBasket(any(Basket.class))).thenReturn(
        mock(Basket.class));
    mockInstantNow();
    when(cleanUpTime.getCleanUpTime(any())).thenReturn(CLEAN_UP_TIME_LONG);

    ArgumentCaptor<Basket> captor = ArgumentCaptor.forClass(Basket.class);

    // Act
    basketInPort.confirmAmendProcessing(reference, request);

    // Assert
    verify(basketOutPort, times(1)).updateBasket(captor.capture());
    final var basketEntityResult = captor.getValue();
    assertThat(basketEntityResult.getStatus(), is(BasketStatus.AMEND_FAILED));
    assertThat(basketEntityResult.getPaymentStatus(), is(BasketPaymentStatus.REFUNDING));
    assertEquals(CLEAN_UP_TIME_LONG, basket.getCleanUpTime());
  }

  @Test
  void testConfirmChangePaymentProcessing_Failed() {
    // Arrange
    final var request = ConfirmItemProcessingRequest.builder()
        .status(1)
        .reqAction("CHANGE_PAY")
        .build();
    var basket = Basket.builder()
        .status(BasketStatus.PROCESSING)
        .basketId(BASKET_ID)
        .reference("BSK123")
        .items(List.of(BasketItem.builder().sourceId("123")
            .build()))
        .paymentOption("ACCOUNT_COMPANY")
        .build();

    when(basketOutPort.getBasketById(any(String.class))).thenReturn(basket);
    when(basketOutPort.updateBasket(any(Basket.class))).thenReturn(
        mock(Basket.class));
    mockInstantNow();
    when(cleanUpTime.getCleanUpTime(any())).thenReturn(CLEAN_UP_TIME_LONG);

    ArgumentCaptor<Basket> captor = ArgumentCaptor.forClass(Basket.class);

    // Act
    basketInPort.confirmChangePaymentProcessing(BASKET_ID, request);

    // Assert
    verify(basketOutPort, times(1)).updateBasket(captor.capture());
    final var basketEntityResult = captor.getValue();
    assertThat(basketEntityResult.getStatus(), is(BasketStatus.FAILED));
    assertEquals(CLEAN_UP_TIME_LONG, basket.getCleanUpTime());
    verify(bookingCompletedOutPort, times(1)).publishBookingCompleted(any(Basket.class));
  }

  @Test
  void testConfirmItemProcessing_Failed() {
    // Arrange
    final var reference = "ABC123456";
    final var request = ConfirmItemProcessingRequest.builder()
        .status(1)
        .reqAction("AMEND")
        .build();

    Basket basket = Basket.builder()
        .reference(reference)
        .paymentOption("PAY_NOW")
        .status(BasketStatus.PROCESSING)
        .paymentID("12345")
        .basketId(BASKET_ID)
        .totalCost("30")
        .item(BasketItem.builder()
            .reqAction("COMMIT")
            .ack(0)
            .sourceId("123")
            .build()).build();

    when(basketOutPort.getBasketById(any(String.class))).thenReturn(basket);
    when(basketOutPort.updateBasket(any(Basket.class))).thenReturn(
        mock(Basket.class));
    mockInstantNow();
    when(cleanUpTime.getCleanUpTime(any())).thenReturn(CLEAN_UP_TIME_LONG);

    ArgumentCaptor<Basket> captor = ArgumentCaptor.forClass(Basket.class);

    // Act
    basketInPort.confirmAmendProcessing(reference, request);

    // Assert
    verify(basketOutPort, times(1)).updateBasket(captor.capture());
    final var basketEntityResult = captor.getValue();
    assertThat(basketEntityResult.getStatus(), is(BasketStatus.FAILED));
    assertEquals(CLEAN_UP_TIME_LONG, basket.getCleanUpTime());
    assertThat(basketEntityResult.getPaymentStatus(), is(BasketPaymentStatus.REFUNDING));
    verify(bookingCompletedOutPort, times(1)).publishBookingCompleted(any(Basket.class));
  }

  @Test
  void testConfirmItemProcessing_itemRolledBackFailed() {
    // Arrange
    final var reference = "ABC123456";
    final var request = ConfirmItemProcessingRequest.builder()
        .status(1)
        .reqAction("ROLLBACK")
        .build();

    Basket basket = Basket.builder()
        .reference(reference)
        .basketId(BASKET_ID)
        .paymentOption("PAY_NOW")
        .status(BasketStatus.FAILED)
        .paymentID("12345")
        .item(BasketItem.builder()
            .reqAction("CANCEL")
            .ack(0)
            .sourceId("123")
            .build()).build();

    when(basketOutPort.getBasketById(any(String.class))).thenReturn(basket);
    when(basketOutPort.updateBasket(any(Basket.class))).thenReturn(
        mock(Basket.class));

    ArgumentCaptor<Basket> captor = ArgumentCaptor.forClass(Basket.class);

    // Act
    basketInPort.confirmRefundProcessing(BASKET_ID, request);

    // Assert
    verify(basketOutPort, times(1)).updateBasket(captor.capture());
    final var basketEntityResult = captor.getValue();
    assertThat(basketEntityResult.getStatus(), is(BasketStatus.FAILED));
    assertThat(basketEntityResult.getPaymentStatus(), is(BasketPaymentStatus.FAILED_REFUND));
  }

  @Test
  void testConfirmItemProcessing_itemMismatch() {
    // Arrange
    when(basketOutPort.getBasketById(any(String.class))).thenReturn(
        mockBasket(null, 1));

    // Act

    final var itemId = "899";
    final var request = ConfirmItemProcessingRequest.builder()
        .status(0)
        .build();

    // Assert
    assertThrows(
        BasketItemException.class,
        () -> basketInPort.confirmItemProcessing(BASKET_ID, itemId, request));
  }

  @Test
  void testDeleteBasket_success() {
    // Arrange
    final var reference = "ABC123456";
    final var hotelId = "ABC";
    final var sortKey = BASKET_ID.substring(3);
    final var threeLetterCode = "AAM";
    final var createdAt = "2022-02-03T12:26:56Z";
    final var userId = "userId";
    final var paymentId = "paymentId";

    final var basket = Basket.builder()
        .reference(reference)
        .basketId(BASKET_ID)
        .sortKey(sortKey)
        .hotelId(hotelId)
        .status(BasketStatus.OPEN)
        .userId(userId)
        .threeLetterHotelId(threeLetterCode)
        .createdAt(createdAt)
        .paymentID(paymentId)
        .build();

    when(basketOutPort.getBasketById(BASKET_ID)).thenReturn(basket);
    doNothing().when(basketOutPort).deleteBasket(threeLetterCode, sortKey);

    // Act
    final var requestModifyTimestamp = getTimestampStr(createdAt);
    basketInPort.deleteBasket(BASKET_ID, requestModifyTimestamp);

    // Assert
    verify(basketOutPort, times(1)).deleteBasket(threeLetterCode, sortKey);
  }

  @Test
  void testDeleteBasket_lastModifyTimestampPreconditionViolated() {
    // Arrange
    final var reference = "ABC123456";
    final var hotelId = "ABC";
    final var createdAt = "2022-02-03T12:26:56Z";

    final var basket = Basket.builder().basketId(BASKET_ID).reference(reference).hotelId(hotelId)
        .status(BasketStatus.OPEN).createdAt(createdAt).build();

    when(basketOutPort.getBasketById(BASKET_ID)).thenReturn(basket);

    // Act
    final var requestModifyTimestamp = getTimestampStr("2022-01-03T12:26:56Z");

    // Assert
    assertThrows(PreconditionFailedException.class,
        () -> basketInPort.deleteBasket(BASKET_ID, requestModifyTimestamp));
  }

  @Test
  void testCancelBasket_success() {
    // Arrange
    doNothing().when(basketOutPort).updateBasketStatus(any(), any(), any());
    mockInstantNow();

    // Act
    basketInPort.cancelBasket(CancelBasketRequest.builder()
        .basketReference(BASKET_ID)
        .isFailed(false)
        .sendEmail(null)
        .deposits(List.of(Deposits.builder()
            .paymentReference("1234")
            .build()))
        .build());

    // Assert
    verify(basketOutPort, times(1)).updateBasketStatus(BASKET_ID, BasketStatus.CANCELLED, Optional.of(FIXED_INSTANT));
  }

  @Test
  void testCancelBasketFailed_success() {
    // Arrange
    final var reference = "ABC123456";
    doNothing().when(basketOutPort).updateBasketStatus(any(), any(), any());
    mockInstantNow();

    // Act
    basketInPort.cancelBasket(CancelBasketRequest.builder()
        .basketReference(reference)
        .isFailed(true)
        .sendEmail(null)
        .build());

    // Assert
    verify(basketOutPort).updateBasketStatus(reference, BasketStatus.FAILED, Optional.of(FIXED_INSTANT));
    verify(emailNotificationService, times(0))
        .sendEmailNotificationEvent(any(), any(), any(), anyBoolean());
  }

  @Test
  void testCancelOpenBasket_success() {
    // Arrange
    final var reference = "ABC123456";
    mockInstantNow();

    // Act
    basketInPort.cancelBasket(CancelBasketRequest.builder()
        .basketReference(reference)
        .isFailed(false)
        .sendEmail(false)
        .build());

    // Assert
    verify(basketOutPort, times(1)).updateBasketStatus(reference, BasketStatus.CANCELLED, Optional.of(FIXED_INSTANT));
    verify(emailNotificationService, times(0)).sendEmailNotificationEvent(any(), any(), any(),
        anyBoolean());
  }

  @Test
  void testSendEmailNotificationOption() {
    // Arrange
    final var reference = "ABCABC123456";
    final var hotelId = "ABC";
    final var createdAt = "2022-02-03T12:26:56Z";
    final var userId = "userId";

    final var basket = Basket.builder().reference(reference)
        .basketId(BASKET_ID)
        .hotelId(hotelId).status(BasketStatus.OPEN).userId(userId)
        .createdAt(createdAt).build();

    when(basketOutPort.getBasketById(BASKET_ID)).thenReturn(basket);

    // Act
    basketInPort.sendEmailNotificationOption(BASKET_ID, true);

    // Assert
    verify(basketOutPort).updateBasket(basket);
  }

  @ParameterizedTest
  @ValueSource(booleans = {true, false})
  @NullSource
  void testCheckBasketStatus_isFailed(Boolean isCheckInOnlinePay) {
    //Arrange
    final var basket = Basket.builder().basketId(BASKET_ID)
        .status(BasketStatus.PAY_PENDING)
        .pollingStartedAt(Instant.parse("2023-05-03T08:43:36Z").truncatedTo(ChronoUnit.SECONDS)
            .toString())
        .isCheckInOnlinePay(isCheckInOnlinePay)
        .build();
    when(basketOutPort.getBasketById(BASKET_ID)).thenReturn(basket);

    //Act
    basketInPort.checkBasketStatus(BASKET_ID);

    //Assert
    if (Boolean.TRUE.equals(isCheckInOnlinePay)) {
      assertEquals(CIOL_FAILED, basket.getStatus());
    } else {
      assertEquals(FAILED, basket.getStatus());
    }
    assertEquals(BasketErrorType.TIMEOUT, basket.getBasketError().getType());
    verify(basketOutPort, times(1)).updateBasket(basket);
  }

  @ParameterizedTest
  @ValueSource(booleans = {true, false})
  void saveCharges_onlyNewCharges_success(Boolean prepaidBookingChargesTtlFf) {
    //Arrange
    when(depositFolioOutPort.getBasketPrepaidDeposit(anyString())).thenReturn(
        mockPrepaidDeposits());
    var mockedFeatureFlag = mock(FeatureFlag.class);
    when(unleashWrapper.featureFlag()).thenReturn(mockedFeatureFlag);
    var mockedFeaturePrepaidBookingChargesTtl = mock(FeatureFlag.Feature.class);
    when(mockedFeatureFlag.getPrepaidBookingChargesTtl()).thenReturn(mockedFeaturePrepaidBookingChargesTtl);
    when(unleashWrapper.isEnabled(mockedFeatureFlag.getPrepaidBookingChargesTtl())).thenReturn(
        prepaidBookingChargesTtlFf);

    //Act
    basketInPort.saveCharges(mockSaveChargesRequest());

    //Assert
    if (prepaidBookingChargesTtlFf) {
      verify(depositFolioOutPort).createBasketPrepaidDeposit(mockDepositTobeSaved());
      verify(depositFolioOutPort).updateBasketPrepaidDeposit(mockPrepaidDeposits());
    } else {
      verify(depositFolioOutPort).createBasketPrepaidDeposit(mockDepositTobeSaved());
    }
  }

  @ValueSource(booleans = {true, false})
  @ParameterizedTest
  void saveCharges_success(Boolean prepaidBookingChargesTtlFf) {
    //Arrange
    when(depositFolioOutPort.getBasketPrepaidDeposit(anyString())).thenReturn(null);
    var mockedFeatureFlag = mock(FeatureFlag.class);
    when(unleashWrapper.featureFlag()).thenReturn(mockedFeatureFlag);
    var mockedFeaturePrepaidBookingChargesTtl = mock(FeatureFlag.Feature.class);
    when(mockedFeatureFlag.getPrepaidBookingChargesTtl()).thenReturn(mockedFeaturePrepaidBookingChargesTtl);
    when(unleashWrapper.isEnabled(mockedFeatureFlag.getPrepaidBookingChargesTtl())).thenReturn(
        prepaidBookingChargesTtlFf);

    //Act
    basketInPort.saveCharges(mockSaveChargesRequest());

    //Assert
    verify(depositFolioOutPort).createBasketPrepaidDeposit(mockSaveChargesRequest());
  }

  @Test
  void saveCharges_emptyList() {
    //Arrange
    PrepaidDepositsRequest request = PrepaidDepositsRequest.builder()
        .prepaidDeposits(Collections.emptyList()).build();

    //Act
    basketInPort.saveCharges(request);

    //Assert
    verify(depositFolioOutPort, times(0)).createBasketPrepaidDeposit(
        any(PrepaidDepositsRequest.class));
    verify(depositFolioOutPort, times(0)).updateBasketPrepaidDeposit(
        any(PrepaidDeposits.class));
  }

  @Test
  void testCheckBasketStatus_isPayPending() {
    //Arrange
    final var basket = Basket.builder().reference(BASKET_ID).status(BasketStatus.PAY_PENDING)
        .build();
    final var pollingStartedAt = Instant.now().truncatedTo(ChronoUnit.SECONDS).toString();

    when(basketOutPort.getBasketById(BASKET_ID)).thenReturn(basket);
    doNothing().when(basketOutPort).updatePollingStartedAt(anyString(), anyString());

    //Act
    basketInPort.checkBasketStatus(BASKET_ID);

    //Assert
    assertNotNull(basket.getPollingStartedAt());
    assertEquals(basket.getStatus(), BasketStatus.PAY_PENDING);
    verify(basketOutPort, times(1)).updatePollingStartedAt(BASKET_ID, pollingStartedAt);
  }

  @Test
  void testGetPrepaidDeposit_success() {
    when(depositFolioOutPort.getBasketPrepaidDeposit("reservationId")).thenReturn(
        mockPrepaidDeposits());

    var deposits = basketInPort.getCharges("reservationId");

    assertThat(deposits, notNullValue());
    assertThat(deposits.getPrepaidDeposits().get(0).getCharges(), notNullValue());
  }

  @Test
  void testGetPrepaidDeposits_success() {
    when(depositFolioOutPort.getBasketPrepaidDeposits(List.of("reservationId1, reservationId2")))
        .thenReturn(mockPrepaidDeposits());

    var deposits = basketInPort.getCharges(List.of("reservationId1, reservationId2"));

    assertThat(deposits, notNullValue());
    assertThat(deposits.getPrepaidDeposits().get(0).getCharges(), notNullValue());
  }

  private PrepaidDeposits mockPrepaidDeposits() {
    PrepaidDeposit prepaidDeposit1 = PrepaidDeposit.builder().reservationId("resId1").build();
    Charge charge1 = Charge.builder()
        .transactionCode("9016")
        .postingQuantity(1)
        .postingReference("2023-12-21")
        .chargeAmount(mockChargeAmount(new BigDecimal(11), "USD"))
        .build();
    Charge charge2 = Charge.builder()
        .transactionCode("9026")
        .postingQuantity(2)
        .postingReference("2023-12-21")
        .chargeAmount(mockChargeAmount(new BigDecimal(12), "EUR"))
        .build();
    prepaidDeposit1.setCharges(List.of(charge1, charge2));

    PrepaidDeposit prepaidDeposit2 = PrepaidDeposit.builder().reservationId("resId1").build();
    Charge charge3 = Charge.builder()
        .transactionCode("9013")
        .postingQuantity(3)
        .postingReference("2023-12-22")
        .chargeAmount(mockChargeAmount(new BigDecimal(13), "USD"))
        .build();
    Charge charge4 = Charge.builder()
        .transactionCode("9014")
        .postingQuantity(4)
        .postingReference("2023-12-22")
        .chargeAmount(mockChargeAmount(new BigDecimal(14), "EUR"))
        .build();
    Charge charge5 = Charge.builder()
        .transactionCode("9015")
        .postingQuantity(5)
        .postingReference("2023-12-22")
        .chargeAmount(mockChargeAmount(new BigDecimal(15), "EUR"))
        .build();
    prepaidDeposit2.setCharges(List.of(charge3, charge4, charge5));

    return PrepaidDeposits.builder()
        .prepaidDeposits(List.of(prepaidDeposit1, prepaidDeposit2))
        .build();
  }

  private ChargeAmount mockChargeAmount(BigDecimal amount, String currencyCode) {
    return ChargeAmount.builder()
        .amount(amount)
        .currencyCode(currencyCode)
        .build();
  }

  private Basket mockBasketIsCheckInOnline(Integer ack1, Integer ack2) {
    Basket basket = mockBasket(ack1, ack2);
    basket.setIsCheckInOnlinePay(true);
    basket.setLockingTime("2026-10-21T15:00:00Z");
    return basket;
  }

  private Basket mockBasket(Integer ack1, Integer ack2) {
    final var basketItems = asList(
        BasketItem.builder()
            .ack(ack1)
            .type("STAY")
            .sourceId("123")
            .reqAction("COMMIT")
            .build(),
        BasketItem.builder()
            .ack(ack2)
            .type("STAY")
            .reqAction("COMMIT")
            .sourceId("567")
            .build()
    );

    return Basket.builder()
        .status(BasketStatus.PROCESSING)
        .basketId(BASKET_ID)
        .reference("BSK123")
        .items(basketItems)
        .paymentOption("PAY_ON_ARRIVAL")
        .build();
  }

  private Basket mockPayNowBasket(Integer ack1, Integer ack2) {
    final var basketItems = asList(
        BasketItem.builder()
            .ack(ack1)
            .type("STAY")
            .sourceId("123")
            .reqAction("COMMIT")
            .build(),
        BasketItem.builder()
            .ack(ack2)
            .type("STAY")
            .reqAction("COMMIT")
            .sourceId("567")
            .build()
    );

    return Basket.builder()
        .hotelId("HOTELID")
        .status(BasketStatus.PROCESSING)
        .basketId(BASKET_ID)
        .reference("BSK123")
        .items(basketItems)
        .paymentOption("PAY_NOW")
        .currency("GBP")
        .totalCost("123")
        .build();
  }

  private Basket mockBasket() {
    return Basket.builder()
        .hotelId("TST")
        .reference("TST1231231")
        .paymentOption("PAY_ON_ARRIVAL")
        .idContext("CTX")
        .build();
  }

  @Test
  void testCreateMigratedBasket() {
    // Arrange
    when(basketOutPort.createBasket(any(CreateBasketRequest.class))).thenReturn(
        mockMigratedBasket());

    // Act
    CreateBasketRequest createBasketRequest = CreateBasketRequest.builder()
        .hotelId("TST")
        .userId("USR")
        .migratedResNo("TSTR231231")
        .idContext("CTX")
        .paymentOption(PaymentOption.PAY_ON_ARRIVAL)
        .build();

    Basket basket = basketInPort.createBasket(createBasketRequest);

    // Assert
    assertThat(basket.getHotelId(), is("TST"));
    assertThat(basket.getReference(), is("TSTR231231"));
    assertThat(basket.getPaymentOption(), is(PaymentOption.PAY_ON_ARRIVAL.name()));
    assertEquals("CTX", basket.getIdContext());
  }

  private Basket mockMigratedBasket() {
    return Basket.builder()
        .hotelId("TST")
        .reference("TSTR231231")
        .paymentOption(PaymentOption.PAY_ON_ARRIVAL.name())
        .idContext("CTX")
        .build();
  }

  private PrepaidDepositsRequest mockSaveChargesRequest() {
    return PrepaidDepositsRequest.builder().prepaidDeposits(
        List.of(PrepaidDeposit.builder().reservationId("resId1").charges(List.of(Charge.builder()
                .postingQuantity(1)
                .transactionCode("9016")
                .postingReference("2023-12-21")
                .chargeAmount(mockChargeAmount(new BigDecimal(11), "USD"))
                .build(), Charge.builder()
                .transactionCode("9026")
                .postingQuantity(2)
                .postingReference("2023-12-21")
                .chargeAmount(mockChargeAmount(new BigDecimal(12), "EUR"))
                .build())).build(),
            PrepaidDeposit.builder().reservationId("resId1").charges(List.of(
                Charge.builder().transactionCode("9016").postingQuantity(1)
                    .postingReference("2023-12-21")
                    .chargeAmount(ChargeAmount.builder()
                        .amount(BigDecimal.TEN).build()).build(),
                Charge.builder().transactionCode("9026").postingQuantity(1)
                    .postingReference("2023-12-21")
                    .chargeAmount(ChargeAmount.builder()
                        .amount(BigDecimal.valueOf(19)).build()).build())).build()
        )).build();
  }

  private PrepaidDepositsRequest mockDepositTobeSaved() {
    return PrepaidDepositsRequest.builder()
        .prepaidDeposits(List.of(PrepaidDeposit.builder().reservationId("resId1").charges(List.of(
            Charge.builder().transactionCode("9016").postingQuantity(1)
                .postingReference("2023-12-21")
                .chargeAmount(ChargeAmount.builder()
                    .amount(BigDecimal.TEN).build()).build(),
            Charge.builder().transactionCode("9026").postingQuantity(1)
                .postingReference("2023-12-21")
                .chargeAmount(ChargeAmount.builder()
                    .amount(BigDecimal.valueOf(19)).build()).build())).build())).build();
  }

  @Test
  void testSetErroredBooking() {
    // Arrange
    final var reference = "ABCABC123456";
    final var hotelId = "ABC";
    final var createdAt = "2022-02-03T12:26:56Z";
    final var userId = "userId";

    final var basket = Basket.builder().reference(reference)
        .basketId(BASKET_ID)
        .hotelId(hotelId).status(BasketStatus.OPEN).userId(userId)
        .createdAt(createdAt).build();

    final var basketError = new BasketError("", "", BasketErrorType.AMEND_CONFIRM);
    when(basketOutPort.getBasketById(BASKET_ID)).thenReturn(basket);

    // Act
    basketInPort.setErroredBooking(BASKET_ID, true, basketError);

    // Assert
    verify(basketOutPort).updateBasket(basket);
  }

  @Test
  void testSetPreAuthCharges() {
    // Arrange
    final var reference = "ABCABC123456";
    final var hotelId = "ABC";
    final var createdAt = "2022-02-03T12:26:56Z";
    final var userId = "userId";

    final var basket = Basket.builder().reference(reference)
        .basketId(BASKET_ID)
        .hotelId(hotelId).status(BasketStatus.OPEN).userId(userId)
        .createdAt(createdAt).build();

    when(basketOutPort.getBasketById(BASKET_ID)).thenReturn(basket);

    // Act
    basketInPort.setPreAuthCharges(BASKET_ID, "preAuthCharges");

    // Assert
    verify(basketOutPort).getBasketById(BASKET_ID);
    verify(basketOutPort).updateBasket(basket);
  }

  @ParameterizedTest
  @EnumSource(value = BasketStatus.class, names = {"COMPLETED", "PRE_CHECKED_IN"})
  void testUpdateReservation_basketNoLongerValidCheckInOnlineFlow(final BasketStatus basketStatus) {
    //mock request
    final String reference = "ABC123456";
    final String hotelId = "Loneus";
    final String requestId = "111";
    var guestReservationRequest = createValidReservationGuestRequest();
    var updatePackageReservationRequest = createSavePackagesRequest();
    var paymentRequest = getPaymentRequest();
    var basket = mockBasket(reference);
    basket.setIsCheckInOnlinePay(true);
    basket.setStatus(basketStatus);
    mockCheckInOnlineFeatureFlag(true);

    when(basketOutPort.getBasketById(anyString())).thenReturn(basket);

    //Method Invocation
    assertThrows(BasketReferenceNotValidException.class,
        () -> basketInPort.updateReservation(reference, hotelId, requestId,
            guestReservationRequest, updatePackageReservationRequest, paymentRequest,
            "false"));
  }

  @Test
  void updateReservation_singleCall_UnSupportedCardType_DISTRChannel() {
    //mock request
    final String reference = "ABC123456";
    final String hotelId = "Loneus";
    final String requestId = "111";
    var guestReservationRequest = createValidReservationGuestRequest();
    var updatePackageReservationRequest = createSavePackagesRequest();
    var paymentRequest = getPaymentRequest();
    var basket = mockBasket(reference);
    basket.setChannel("DISTR");
    var hotelPaymentInfo = mockHotelPaymentInfoResponseWithNoCards();
    var businessItems = buildBusinessItemsFromBusinessAccount(
        paymentRequest.getBusinessAccount(),
        distributionProperties.getMeals());
    final var packageCodes = updatePackageReservationRequest.getRoomsSelections().get(0)
        .getPackagesSelection().stream()
        .map(PackagesSelection::getId)
        .toList();
    var resBusinessItems = BusinessAllowancesUtils.getBusinessItems(
        businessItems,
        mockBusinessAllowanceRules().getBusinessAllowances(),
        mockBusinessNotes(),
        packageCodes,
        paymentRequest.getBooking().getType());
    mockCheckInOnlineFeatureFlag(false);

    when(basketOutPort.getBasketById(anyString())).thenReturn(basket);
    when(rulesAgentOutPort.getBusinessAllowanceRules()).thenReturn(mockBusinessAllowanceRules());
    when(contentOutPort.getBusinessNotes(anyString())).thenReturn(mockBusinessNotes());
    when(contentOutPort.getHotelPaymentDetails(anyString(), anyString(), anyString())).thenReturn(
        hotelPaymentInfo);

    //Method Invocation
    assertThrows(UnSupportedCardTypeException.class,
        () -> basketInPort.updateReservation(reference, hotelId, requestId,
            guestReservationRequest, updatePackageReservationRequest, paymentRequest,
            "false"));
  }


  private HotelPaymentInformation mockHotelPaymentInfoResponseWithNoCards() {
    return HotelPaymentInformation.builder()
        .acceptedCreditCards(List.of()).build();
  }


  @ParameterizedTest
  @ValueSource(booleans = {true, false})
  void updateReservationTest(boolean isCheckInOnline) {
    //mock request
    final String reference = "ABC123456";
    final String hotelId = "Loneus";
    final String requestId = "111";
    final String reservationId = "101";
    var guestReservationRequest = createValidReservationGuestRequest();
    var updatePackageReservationRequest = createSavePackagesRequest();
    var paymentRequest = getPaymentRequest();
    var basket = mockBasket(reference);
    basket.setIsCheckInOnlinePay(isCheckInOnline);
    var hotelPaymentInfo = mockHotelPaymentInfoResponse();
    var confirmReservationResponse = getConfirmReservationResponse();
    var businessItems = buildBusinessItemsFromBusinessAccount(
        paymentRequest.getBusinessAccount(),
        distributionProperties.getMeals());
    var requestNotPayNow = ConfirmReservationRequest.builder()
        .hotelId("Loneus")
        .reservationId("101")
        .paymentOption(PAY_NOW)
        .paymentType("VA")
        .paymentCard(PaymentCard
            .builder()
            .token(paymentRequest.getPayment().getCard().getToken())
            .expirationDate("2024-01-31")
            .cardHolderName(paymentRequest.getPayment().getCard().getCardholderName())
            .build()
        )
        .pibaCardPresent(paymentRequest.getPayment().getPibaCardPresent())
        .build();
    final var packageCodes = updatePackageReservationRequest.getRoomsSelections().get(0)
        .getPackagesSelection().stream()
        .map(PackagesSelection::getId)
        .toList();
    var resBusinessItems = BusinessAllowancesUtils.getBusinessItems(
        businessItems,
        mockBusinessAllowanceRules().getBusinessAllowances(),
        mockBusinessNotes(),
        packageCodes,
        paymentRequest.getBooking().getType());

    when(basketOutPort.getBasketById(anyString())).thenReturn(basket);
    when(rulesAgentOutPort.getBusinessAllowanceRules()).thenReturn(mockBusinessAllowanceRules());
    when(contentOutPort.getBusinessNotes(anyString())).thenReturn(mockBusinessNotes());
    when(contentOutPort.getHotelPaymentDetails(anyString(), anyString(), anyString())).thenReturn(
        hotelPaymentInfo);
    when(hotelReservationOutPort.createProfileIds(guestReservationRequest))
        .thenReturn(ReservationProfiles.builder()
            .bookerProfileId("12345")
            .companyProfileId(null).build());
    doReturn(confirmReservationResponse).when(hotelReservationOutPort)
        .updateReservationRequest(guestReservationRequest
            , updatePackageReservationRequest,
            resBusinessItems, List.of(), List.of("SpecialRequirements"), hotelId
            , reservationId
            , requestNotPayNow);
    mockCheckInOnlineFeatureFlag(isCheckInOnline);

    //Method Invocation
    basketInPort.updateReservation(reference, hotelId, requestId,
        guestReservationRequest, updatePackageReservationRequest, paymentRequest,
        "false");

    assertEquals(basket.getStatus(), isCheckInOnline ? PRE_CHECKED_IN : COMPLETED);
  }

  @Test
  public void updateReservationTest_WhenMultiRoom_SinglePackage() {
    //mock request
    final String reference = "ABC123456";
    final String hotelId = "Loneus";
    final String requestId = "111";
    final String reservationId = "101";
    var guestReservationRequest = createMultipleRoomGuestDetails();
    var updatePackageReservationRequest = createSavePackagesRequest();
    var packageSecondRoom = ReservationPackagesRequest.builder()
        .arrivalDate("2022-04-02")
        .departureDate("2022-04-03")
        .reservationsId(List.of("102"))
        .roomsSelections(Collections.<RoomsSelections>emptyList())
        .hotelId("Loneus")
        .build();
    var paymentRequest = getPaymentRequest();
    var basket = mockBasketMultipleItems(reference);
    var hotelPaymentInfo = mockHotelPaymentInfoResponse();
    var confirmReservationResponse = getConfirmReservationResponse();
    var businessItems = buildBusinessItemsFromBusinessAccount(
        paymentRequest.getBusinessAccount(),
        distributionProperties.getMeals());
    var singleGuestDetails = ReservationGuestRequest.builder()
        .booker(guestReservationRequest.getBooker())
        .basketReference(reference)
        .bookerProfileId("12345")
        .stayingGuests(List.of(guestReservationRequest.getStayingGuests().get(0)))
        .sendEmailConfirmation(true)
        .sendEmailInvoice(true)
        .hotelId("Loneus")
        .build();
    var requestNotPayNow = ConfirmReservationRequest.builder()
        .hotelId("Loneus")
        .reservationId("101")
        .paymentOption(PAY_NOW)
        .paymentType("VA")
        .paymentCard(PaymentCard
            .builder()
            .token(paymentRequest.getPayment().getCard().getToken())
            .expirationDate("2024-01-31")
            .cardHolderName(paymentRequest.getPayment().getCard().getCardholderName())
            .build()
        )
        .pibaCardPresent(paymentRequest.getPayment().getPibaCardPresent())
        .build();
    var requestNotPayNowSecondRoom = ConfirmReservationRequest.builder()
        .hotelId("Loneus")
        .reservationId("102")
        .paymentOption(PAY_NOW)
        .paymentType("VA")
        .paymentCard(PaymentCard
            .builder()
            .token(paymentRequest.getPayment().getCard().getToken())
            .expirationDate("2024-01-31")
            .cardHolderName(paymentRequest.getPayment().getCard().getCardholderName())
            .build()
        )
        .pibaCardPresent(paymentRequest.getPayment().getPibaCardPresent())
        .build();
    final var packageCodes = updatePackageReservationRequest.getRoomsSelections().get(0)
        .getPackagesSelection().stream()
        .map(PackagesSelection::getId)
        .toList();
    var resBusinessItems = BusinessAllowancesUtils.getBusinessItems(
        businessItems,
        mockBusinessAllowanceRules().getBusinessAllowances(),
        mockBusinessNotes(),
        packageCodes,
        paymentRequest.getBooking().getType());

    when(basketOutPort.getBasketById(anyString())).thenReturn(basket);
    when(rulesAgentOutPort.getBusinessAllowanceRules()).thenReturn(mockBusinessAllowanceRules());
    when(contentOutPort.getBusinessNotes(anyString())).thenReturn(mockBusinessNotes());
    when(contentOutPort.getHotelPaymentDetails(anyString(), anyString(), anyString())).thenReturn(
        hotelPaymentInfo);
    when(hotelReservationOutPort.createProfileIds(guestReservationRequest))
        .thenReturn(ReservationProfiles.builder()
            .bookerProfileId("12345")
            .companyProfileId(null).build());
    doReturn(confirmReservationResponse).when(hotelReservationOutPort)
        .updateReservationRequest(singleGuestDetails
            , updatePackageReservationRequest,
            resBusinessItems, List.of(), List.of("SpecialRequirements"), hotelId
            , reservationId
            , requestNotPayNow);
    //To Validate 2nd room param also success
    doReturn(confirmReservationResponse).when(hotelReservationOutPort)
        .updateReservationRequest(singleGuestDetails
            , packageSecondRoom,
            resBusinessItems, List.of(), List.of("SpecialRequirements"), hotelId
            , "102"
            , requestNotPayNowSecondRoom);
    mockCheckInOnlineFeatureFlag(false);

    //Method Invocation
    basketInPort.updateReservation(reference, hotelId, requestId,
        guestReservationRequest, updatePackageReservationRequest, paymentRequest,
        "false");

    assertEquals("RESERVED", confirmReservationResponse.getReservationStatus());
  }

  @Test
  public void updateReservation_check_InputBookingNotesIsPassed() {
    //mock request
    final String reference = "ABC123456";
    final String hotelId = "Loneus";
    final String requestId = "111";
    final String reservationId = "101";
    var guestReservationRequest = createValidReservationGuestRequest();
    var updatePackageReservationRequest = createSavePackagesRequest();
    var paymentRequest = getPaymentRequest();
    var basket = mockBasket(reference);
    var hotelPaymentInfo = mockHotelPaymentInfoResponse();
    var confirmReservationResponse = getConfirmReservationResponse();
    var businessItems = buildBusinessItemsFromBusinessAccount(
        paymentRequest.getBusinessAccount(),
        distributionProperties.getMeals());
    var requestNotPayNow = ConfirmReservationRequest.builder()
        .hotelId("Loneus")
        .reservationId("101")
        .paymentOption(PAY_NOW)
        .paymentType("VA")
        .paymentCard(PaymentCard
            .builder()
            .token(paymentRequest.getPayment().getCard().getToken())
            .expirationDate("2024-01-31")
            .cardHolderName(paymentRequest.getPayment().getCard().getCardholderName())
            .build()
        )
        .pibaCardPresent(paymentRequest.getPayment().getPibaCardPresent())
        .build();
    final var packageCodes = updatePackageReservationRequest.getRoomsSelections().get(0)
        .getPackagesSelection().stream()
        .map(PackagesSelection::getId)
        .toList();
    var resBusinessItems = BusinessAllowancesUtils.getBusinessItems(
        businessItems,
        mockBusinessAllowanceRules().getBusinessAllowances(),
        mockBusinessNotes(),
        packageCodes,
        paymentRequest.getBooking().getType());

    when(basketOutPort.getBasketById(anyString())).thenReturn(basket);
    when(rulesAgentOutPort.getBusinessAllowanceRules()).thenReturn(mockBusinessAllowanceRules());
    when(contentOutPort.getBusinessNotes(anyString())).thenReturn(mockBusinessNotes());
    when(contentOutPort.getHotelPaymentDetails(anyString(), anyString(), anyString())).thenReturn(
        hotelPaymentInfo);
    when(hotelReservationOutPort.createProfileIds(guestReservationRequest))
        .thenReturn(ReservationProfiles.builder()
            .bookerProfileId("12345")
            .companyProfileId(null).build());
    doReturn(confirmReservationResponse).when(hotelReservationOutPort)
        .updateReservationRequest(guestReservationRequest
            , updatePackageReservationRequest,
            resBusinessItems, List.of(), List.of("SpecialRequirements"), hotelId
            , reservationId
            , requestNotPayNow);
    mockCheckInOnlineFeatureFlag(false);

    //Method Invocation
    basketInPort.updateReservation(reference, hotelId, requestId,
        guestReservationRequest, updatePackageReservationRequest, paymentRequest,
        "false");
    //Checking the value still exists and not lost
    assertEquals(List.of("SpecialRequirements"), paymentRequest.getBookingNotes());
  }

  @Test
  public void updateReservationStatusFailedTest() {
    //mock request
    final String reference = "ABC123456";
    final String hotelId = "Loneus";
    final String requestId = "111";
    final String reservationId = "101";
    var guestReservationRequest = createValidReservationGuestRequest();
    var updatePackageReservationRequest = createSavePackagesRequest();
    var paymentRequest = getPaymentRequest();
    var basket = mockBasket(reference);
    var hotelPaymentInfo = mockHotelPaymentInfoResponse();
    var confirmReservationResponse = getConfirmReservationResponse();
    confirmReservationResponse.setReservationStatus("FAILED");
    var businessItems = buildBusinessItemsFromBusinessAccount(
        paymentRequest.getBusinessAccount(),
        distributionProperties.getMeals());
    var requestNotPayNow = ConfirmReservationRequest.builder()
        .hotelId("Loneus")
        .reservationId("101")
        .paymentOption(PAY_NOW)
        .paymentType("VA")
        .paymentCard(PaymentCard
            .builder()
            .token(paymentRequest.getPayment().getCard().getToken())
            .expirationDate("2024-01-31")
            .cardHolderName(paymentRequest.getPayment().getCard().getCardholderName())
            .build()
        )
        .pibaCardPresent(paymentRequest.getPayment().getPibaCardPresent())
        .build();
    final var packageCodes = updatePackageReservationRequest.getRoomsSelections().get(0)
        .getPackagesSelection().stream()
        .map(PackagesSelection::getId)
        .toList();
    var resBusinessItems = BusinessAllowancesUtils.getBusinessItems(
        businessItems,
        mockBusinessAllowanceRules().getBusinessAllowances(),
        mockBusinessNotes(),
        packageCodes,
        paymentRequest.getBooking().getType());

    mockCheckInOnlineFeatureFlag(false);
    mockInstantNow();
    when(cleanUpTime.getCleanUpTime(any())).thenReturn(CLEAN_UP_TIME_LONG);
    when(basketOutPort.getBasketById(anyString())).thenReturn(basket);
    when(rulesAgentOutPort.getBusinessAllowanceRules()).thenReturn(mockBusinessAllowanceRules());
    when(contentOutPort.getBusinessNotes(anyString())).thenReturn(mockBusinessNotes());
    when(contentOutPort.getHotelPaymentDetails(anyString(), anyString(), anyString())).thenReturn(
        hotelPaymentInfo);
    when(hotelReservationOutPort.createProfileIds(guestReservationRequest))
        .thenReturn(ReservationProfiles.builder()
            .bookerProfileId("12345")
            .companyProfileId(null).build());
    doReturn(confirmReservationResponse).when(hotelReservationOutPort)
        .updateReservationRequest(guestReservationRequest
            , updatePackageReservationRequest,
            resBusinessItems, List.of(), List.of("SpecialRequirements"), hotelId
            , reservationId
            , requestNotPayNow);
    //Method Invocation
    basketInPort.updateReservation(reference, hotelId, requestId,
        guestReservationRequest, updatePackageReservationRequest, paymentRequest,
        "false");

    assertEquals(BasketStatus.FAILED, basket.getStatus());
    assertEquals(CLEAN_UP_TIME_LONG, basket.getCleanUpTime());
  }

  @Test
  public void updateReservationBasketIsExpiredTest() {
    final String reference = "ABC123456";
    final String hotelId = "Loneus";
    final String requestId = "111";
    var guestReservationRequest = createValidReservationGuestRequest();
    var updatePackageReservationRequest = createSavePackagesRequest();
    var paymentRequest = getPaymentRequest();
    var basket = mockBasket(reference);
    basket.setCreatedAt("2022-01-21T12:26:56Z");
    mockCheckInOnlineFeatureFlag(false);
    //Method Invocation
    when(basketOutPort.getBasketById(anyString())).thenReturn(basket);
    assertThrows(BasketReferenceNotValidException.class, () ->
        basketInPort.updateReservation(reference, hotelId, requestId,
            guestReservationRequest, updatePackageReservationRequest, paymentRequest,
            "false"));
  }

  @Test
  public void updateReservationBasketAnyBlankTest() {

    final String reference = "ABC123456";
    final String hotelId = " ";
    final String requestId = "111";
    var guestReservationRequest = createValidReservationGuestRequest();
    var updatePackageReservationRequest = createSavePackagesRequest();
    var paymentRequest = getPaymentRequest();
    var basket = mockBasket(reference);
    basket.setHotelId(hotelId);
    mockCheckInOnlineFeatureFlag(false);

    //Method Invocation
    when(basketOutPort.getBasketById(anyString())).thenReturn(basket);
    assertThrows(BasketReferenceNotValidException.class, () ->
        basketInPort.updateReservation(reference, hotelId, requestId,
            guestReservationRequest, updatePackageReservationRequest, paymentRequest,
            "false"));
  }


  @Test
  void testDeleteBasket_preconditionFailed() {
    // Arrange
    final var createdAt = "2022-02-03T12:26:56Z";
    final var requestModifyTimestamp = getTimestampStr(createdAt);
    final var basket = Basket.builder()
        .basketId(BASKET_ID)
        .reference("ABC123456")
        .hotelId("ABC")
        .status(BasketStatus.PROCESSING)
        .createdAt("2023-02-03T12:26:56Z")
        .build();
    var message = String.format(ERROR_MESSAGE, basket.getReference(), requestModifyTimestamp,
        getTimestampStr("2023-02-03T12:26:56Z"));

    when(basketOutPort.getBasketById(BASKET_ID)).thenReturn(basket);

    // Act

    var exception = assertThrows(PreconditionFailedException.class,
        () -> basketInPort.deleteBasket(BASKET_ID, requestModifyTimestamp));

    // Assert
    assertEquals(ErrorCode.DIGITAL_OUT_OF_SYNC_EXCEPTION.getCode(), exception.getErrorCode());
    assertEquals(ErrorCode.DIGITAL_OUT_OF_SYNC_EXCEPTION.getMessage(),
        exception.getGlobalErrTextTemplate());
    assertEquals(message, exception.getDebugMessage());
  }

  @Test
  void testPreCheckInBasket() {
    // Arrange
    final String basketReference = "ABC123";
    final var basket = mockBasket(basketReference);
    basket.setStatus(COMPLETED);
    basket.setPaymentStatus(BasketPaymentStatus.COMPLETED);
    mockCheckInOnlineFeatureFlag(false);

    when(basketOutPort.getBasketById(basketReference)).thenReturn(basket);
    when(hotelReservationOutPort.getReservationsByBasketReference(anyString(), anyString(),
        anyBoolean()))
        .thenReturn(mockReservation(BigDecimal.ZERO));

    // Act
    var result = basketInPort.preCheckInBasket(basketReference, Boolean.FALSE);

    // Assert
    assertEquals(COMPLETED, result.getBasketStatus());
  }

  @Test
  void testPreCheckInBasket_forCheckInOnline() throws InterruptedException {
    // Arrange
    final String basketReference = "ABC123";
    final var basket = mockBasket(basketReference);
    basket.setStatus(COMPLETED);
    basket.setPaymentStatus(BasketPaymentStatus.COMPLETED);
    basket.getItems().forEach(item -> item.setType(BASKET_ITEM_TYPE_STAY));
    mockCheckInOnlineFeatureFlag(true);

    when(basketOutPort.getBasketById(basketReference)).thenReturn(basket);
    ArgumentCaptor<Basket> captor = ArgumentCaptor.forClass(Basket.class);
    ArgumentCaptor<ReservationAlertsRequest> alertCaptor =
        ArgumentCaptor.forClass(ReservationAlertsRequest.class);
    ArgumentCaptor<UdfsRequestDto> udfsCaptor =
        ArgumentCaptor.forClass(UdfsRequestDto.class);

    when(cleanUpTime.getCleanUpTime(any())).thenReturn(CLEAN_UP_TIME_LONG);
    var latch = new CountDownLatch(1);
    doAnswer(invocationOnMock -> {
      latch.countDown();
      return null;
    }).when(hotelReservationOutPort).updateReservationAlerts(any());
    when(hotelReservationOutPort.getReservationsByBasketReference(anyString(), anyString(),
        anyBoolean()))
        .thenReturn(mockReservation(BigDecimal.ZERO));

    doNothing().when(basketOutPort).updateCharacterUdfs(any());

    // Act
    var result = basketInPort.preCheckInBasket(basketReference, Boolean.TRUE);

    // Assert
    assertEquals(PRE_CHECKED_IN, result.getBasketStatus());
    verify(basketOutPort, times(1)).updateBasket(captor.capture());
    final Basket updatedBasket = captor.getValue();
    assertEquals(PRE_CHECKED_IN, updatedBasket.getStatus());
    assertEquals(BasketPaymentStatus.COMPLETED, updatedBasket.getPaymentStatus());
    assertEquals(CLEAN_UP_TIME_LONG, basket.getCleanUpTime());
    boolean called = latch.await(1, TimeUnit.SECONDS);
    assertTrue(called);
    verify(hotelReservationOutPort, times(1))
        .updateReservationAlerts(alertCaptor.capture());
    var alertRequest = alertCaptor.getValue();
    assertEquals(basket.getHotelId(), alertRequest.getHotelId());
    var reservationIds = basket.getItems().stream()
        .filter(basketItem -> BASKET_ITEM_TYPE_STAY.equals(basketItem.getType()))
        .map(BasketItem::getSourceId)
        .toList();
    reservationIds.forEach(reservationId -> {
      assertTrue(alertRequest.getReservationIds().contains(reservationId));
    });
    var alert = alertRequest.getAlerts().get(0);
    assertEquals(ALERT_AREA_CHECKIN, alert.getArea());
    assertEquals(ALERT_CODE_CIOL, alert.getCode());
    assertEquals(ALERT_DESCRIPTION_CIOL, alert.getDescription());
    assertTrue(alert.getScreenNotification());
    assertFalse(alert.getPrinterNotification());

    verify(basketOutPort).updateCharacterUdfs(udfsCaptor.capture());
    assertUdf(udfsCaptor.getValue(), basket.getHotelId(), reservationIds);
  }

  @Test
  void testPreCheckInBasket_preconditionFailedException() {
    // Arrange
    final String basketReference = "ABC123";
    final var basket = mockBasket(basketReference);
    mockCheckInOnlineFeatureFlag(true);
    var message = String.format(BASKET_NOT_COMPLETED_ERROR_MESSAGE, basket.getReference());

    when(basketOutPort.getBasketById(basketReference)).thenReturn(basket);
    when(hotelReservationOutPort.getReservationsByBasketReference(anyString(), anyString(),
        anyBoolean()))
        .thenReturn(mockReservation(BigDecimal.ZERO));

    // Act
    var exception = assertThrows(PreconditionFailedException.class,
        () -> basketInPort.preCheckInBasket(basketReference, Boolean.FALSE));

    // Assert
    assertEquals(ErrorCode.DIGITAL_BASKET_NOT_COMPLETED_EXCEPTION.getCode(), exception.getErrorCode());
    assertEquals(ErrorCode.DIGITAL_BASKET_NOT_COMPLETED_EXCEPTION.getMessage(),
        exception.getGlobalErrTextTemplate());
    assertEquals(message, exception.getDebugMessage());
  }

  @Test
  void testPreCheckInBasket_outstandingBalance() {
    // Arrange
    final String basketReference = "ABC123";
    final var basket = mockBasket(basketReference);
    basket.setStatus(COMPLETED);
    basket.setPaymentStatus(BasketPaymentStatus.COMPLETED);
    var message = String.format("Cannot confirm pre-check-in: "
        + "basket %s has outstanding balance.", basket.getReference());

    when(basketOutPort.getBasketById(basketReference)).thenReturn(basket);
    when(hotelReservationOutPort.getReservationsByBasketReference(anyString(), anyString(),
        anyBoolean()))
        .thenReturn(mockReservation(BigDecimal.ONE));

    // Act
    var exception = assertThrows(PreCheckinOutstandingBalanceException.class,
        () -> basketInPort.preCheckInBasket(basketReference, Boolean.FALSE));

    // Assert
    assertEquals(ErrorCode.DIGITAL_BASKET_NOT_COMPLETED_EXCEPTION.getCode(), exception.getErrorCode());
    assertEquals(ErrorCode.DIGITAL_BASKET_NOT_COMPLETED_EXCEPTION.getMessage(),
        exception.getGlobalErrTextTemplate());
    assertEquals(message, exception.getDebugMessage());
  }

  @ParameterizedTest
  @CsvSource({"COMPLETED", "PRE_CHECKED_IN", "CIOL_FAILED"})
  void testPreCheckOut_success(String status) {
    // Arrange
    final String basketReference = "TEST123123";
    final var basket = mockBasket(basketReference);
    basket.setStatus(BasketStatus.valueOf(status));
    basket.setPaymentStatus(BasketPaymentStatus.COMPLETED);
    final var updatedBasket = mockBasket(basketReference);
    updatedBasket.setStatus(PRE_CHECKED_OUT);
    updatedBasket.setPaymentStatus(BasketPaymentStatus.COMPLETED);
    when(basketOutPort.getBasketById(basketReference)).thenReturn(basket);
    when(basketOutPort.updateBasket(basket)).thenReturn(updatedBasket);

    // Act
    var result = basketInPort.preCheckOutBasket(basketReference);

    // Assert
    assertNotNull(result);
    assertEquals(BasketStatus.PRE_CHECKED_OUT, result.getBasketStatus());
    assertEquals("TEST123123", result.getBasketReference());
  }

  @ParameterizedTest
  @CsvSource({"PRE_CHECKED_OUT", "CANCELLED", "FAILED", "OPEN", "CIOL_RC_FAILED"})
  void testPreCheckOut_invalidBasketStatus(String status) {
    // Arrange
    final String basketReference = "TEST123123";
    final var basket = mockBasket(basketReference);
    basket.setStatus(BasketStatus.valueOf(status));
    basket.setPaymentStatus(BasketPaymentStatus.COMPLETED);
    when(basketOutPort.getBasketById(basketReference)).thenReturn(basket);


    // Act
    var exception = assertThrows(PreconditionFailedException.class,
        () -> basketInPort.preCheckOutBasket(basketReference));

    // Assert
    assertNotNull(exception);
    assertEquals(String.format("Cannot perform check-out: booking TEST123123 has basket status %s.",status),
        exception.getMessage());
  }

  @Test
  void testChangeStatus_invalidBasketStatus() {
    // Arrange
    final String bookingRef = "TEST123123";

    // Act
    var exception = assertThrows(IllegalArgumentException.class,
        () -> basketInPort.changeStatus(bookingRef,"WrongStatus"));

    // Assert
    assertNotNull(exception);
  }

  @Test
  void testChangeStatus_basketNotFound() {
    // Arrange
    final String bookingRef = "TEST123123";
    final var basket = mockBasket(bookingRef);
    basket.setStatus(BasketStatus.valueOf("PRE_CHECKED_IN"));
    basket.setPaymentStatus(BasketPaymentStatus.COMPLETED);
    when(basketOutPort.getBasketByReference(bookingRef)).thenReturn(Optional.empty());

    // Act
    var exception = assertThrows(BookingReferenceNotFoundException.class,
        () -> basketInPort.changeStatus(bookingRef, "CIOL_FAILED"));

    // Assert
    assertNotNull(exception);
    assertEquals("Basket with booking reference=TEST123123", exception.getMessage());
  }

  @Test
  void testChangeStatus_success() {
    // Arrange
    final String bookingRef = "TEST123123";
    final var basket = mockBasket(bookingRef);
    basket.setStatus(BasketStatus.valueOf("PRE_CHECKED_IN"));
    basket.setPaymentStatus(BasketPaymentStatus.COMPLETED);
    var updatedBasket = mockBasket();
    updatedBasket.setStatus(CIOL_FAILED);
    when(basketOutPort.getBasketByReference(bookingRef)).thenReturn(Optional.of(basket));
    when(basketOutPort.updateBasket(basket)).thenReturn(updatedBasket);


    // Act
    var response = basketInPort.changeStatus(bookingRef,"CIOL_FAILED");

    // Assert
    assertNotNull(response);
    assertEquals(CIOL_FAILED,response.getStatus());

  }

  @Test
  void testConfirmItemProcessing_secureBooking_successFlow_basketCompletes() {
    // Arrange
    final String itemId = "123";
    final var request = ConfirmItemProcessingRequest.builder()
        .status(0)
        .reqAction("COMMIT")
        .build();

    var basket = mockPayNowBasket(0, 0);
    basket.setIsSecureBooking(true);

    when(basketOutPort.getBasketById(anyString())).thenReturn(basket);
    when(basketOutPort.updateBasket(any())).thenReturn(basket);
    mockUnleash(true, false, true);


    // Act
    basketInPort.confirmItemProcessing(BASKET_ID, itemId, request);

    // Assert
    ArgumentCaptor<Basket> captor = ArgumentCaptor.forClass(Basket.class);
    verify(basketOutPort, times(1)).updateBasket(captor.capture());
    verify(refundOutPort, times(0)).processRefund(any(), any(), any());

    assertTrue(captor.getValue().getIsSecureBooking());
  }

  @Test
  void testConfirmItemProcessing_secureBooking_payNow_shouldRefundOnly() {
    // Arrange
    final var itemId = "123";
    final var request = ConfirmItemProcessingRequest.builder()
        .status(1)
        .reqAction("COMMIT")
        .errors(List.of("Payment failure"))
        .build();

    var basket = mockPayNowBasket(null, 0);
    basket.setIsSecureBooking(true);

    when(basketOutPort.getBasketById(anyString())).thenReturn(basket);
    when(basketOutPort.updateBasket(any(Basket.class))).thenReturn(basket);
    var mockedFeatureFlag = mock(FeatureFlag.class);
    when(unleashWrapper.featureFlag()).thenReturn(mockedFeatureFlag);
    when(unleashWrapper.isEnabled(unleashWrapper.featureFlag().getSaveSecureBooking()))
        .thenReturn(true);
    when(unleashWrapper.isEnabled(unleashWrapper.featureFlag().getCheckInOnline()))
        .thenReturn(false);

    doNothing().when(refundOutPort).processRefund(any(), any(), any());

    // Act
    basketInPort.confirmItemProcessing(BASKET_ID, itemId, request);

    // Assert
    verify(refundOutPort, times(1)).processRefund(any(), any(), any());
    verify(basketOutPort, times(1)).updateBasket(any());
  }

  @Test
  void testConfirmItemProcessing_secureBooking_TriggerEmailNotification() {
    final var itemId = "123";
    final var request = ConfirmItemProcessingRequest.builder()
        .status(0)
        .reqAction("COMMIT")
        .errors(List.of("Payment Success"))
        .build();

    var basket = mockPayNowBasket(null, 0);
    basket.setIsSecureBooking(true);

    when(basketOutPort.getBasketById(anyString())).thenReturn(basket);
    when(basketOutPort.updateBasket(any(Basket.class))).thenReturn(basket);
    var mockedFeatureFlag = mock(FeatureFlag.class);
    when(unleashWrapper.featureFlag()).thenReturn(mockedFeatureFlag);
    mockUnleash(true, false, true);

    // Act
    basketInPort.confirmItemProcessing(BASKET_ID, itemId, request);

    // Assert
    verify(basketOutPort, times(1)).updateBasket(basket);
    verify(emailNotificationService, times(1))
        .sendEmailNotificationEvent(any(), any(), any(), anyBoolean());
  }

  @Test
  void testConfirmItemProcessing_secureBooking_ShouldCancelReservations() {
    final var itemId = "123";
    final var request = ConfirmItemProcessingRequest.builder()
        .status(1)
        .reqAction("COMMIT")
        .errors(List.of("Payment failure"))
        .build();

    var basket = mockPayNowBasket(null, 0);
    basket.setIsSecureBooking(true);

    when(basketOutPort.getBasketById(anyString())).thenReturn(basket);
    when(basketOutPort.updateBasket(any(Basket.class))).thenReturn(basket);
    var mockedFeatureFlag = mock(FeatureFlag.class);
    when(unleashWrapper.featureFlag()).thenReturn(mockedFeatureFlag);
    when(unleashWrapper.isEnabled(unleashWrapper.featureFlag().getCheckInOnline())).thenReturn(
        false);
    when(unleashWrapper.isEnabled(unleashWrapper.featureFlag().getSaveSecureBooking())).thenReturn(
        true);
    doNothing().when(refundOutPort).processRefund(any(), any(), any());

    // Act
    basketInPort.confirmItemProcessing(BASKET_ID, itemId, request);

    // Assert
    verify(emailNotificationService, times(0))
        .sendEmailNotificationEvent(any(), any(), any(), anyBoolean());
  }

  @Test
  void test_addPromotionToBasket_success2() {
    // Arrange
    PromotionsInformationRequest promotionsInfoRequest = PromotionsInformationRequest.builder()
        .promotionCode("TEST")
        .promoKind(PromoKind.SITE_WIDE)
        .build();

    var basketRef = "testBasketRef";
    String originalLastModified = "2022-02-03T12:26:56Z";

    Basket mockBasket = Basket.builder()
        .reference(basketRef)
        .createdAt("2022-02-01T12:26:56Z")
        .lastModifiedAt(originalLastModified)
        .build();

    when(basketOutPort.getBasketById(basketRef)).thenReturn(mockBasket);
    when(basketOutPort.updateBasket(any(Basket.class))).thenAnswer(
        invocation -> invocation.getArgument(0));

    String requestModifyTimestamp = String.valueOf(
        Instant.parse(originalLastModified).toEpochMilli());

    // Act
    Basket result = basketInPort.addPromotionToBasket(basketRef, promotionsInfoRequest,
        requestModifyTimestamp);

    verify(basketOutPort, times(1)).getBasketById(basketRef);
    ArgumentCaptor<Basket> captor = ArgumentCaptor.forClass(Basket.class);
    verify(basketOutPort, times(1)).updateBasket(captor.capture());

    Basket updatedBasket = captor.getValue();

    // Assert
    assertNotNull(result, "The result should not be null");
    assertEquals(basketRef, result.getReference(), "The basket reference should match");
    assertThat(updatedBasket.getPromoKind(), comparesEqualTo(PromoKind.SITE_WIDE));
    assertEquals("TEST", updatedBasket.getPromotionCode());
    assertNotNull(updatedBasket.getLastModifiedAt(), "lastModifiedAt should be updated");
    assertNotEquals(originalLastModified, updatedBasket.getLastModifiedAt(),
        "lastModifiedAt should be updated to a new timestamp");
  }

  @Test
  void test_addPromotionToBasket_basketNotFound() {
    // Arrange
    PromotionsInformationRequest promotionsInfoRequest = PromotionsInformationRequest.builder()
        .promotionCode("")
        .promoKind(PromoKind.SITE_WIDE)
        .build();
    final String bookingRef = "TEST123123";
    // Act
    var exception = assertThrows(BookingReferenceNotFoundException.class,
        () -> basketInPort.addPromotionToBasket(bookingRef, promotionsInfoRequest, ""));

    // Assert
    assertNotNull(exception);
    assertEquals("Basket with basket reference=TEST123123 not found", exception.getMessage());
  }

  @Test
  void test_addPromotionToBasket_updateThrows_causesPreconditionFailedException() {
    // Arrange
    PromotionsInformationRequest promotionsInfoRequest = PromotionsInformationRequest.builder()
        .promotionCode("TEST")
        .promoKind(PromoKind.SITE_WIDE)
        .build();

    var basketRef = "testBasketRef";
    String originalLastModified = "2022-02-03T12:26:56Z";

    Basket mockBasket = Basket.builder()
        .reference(basketRef)
        .createdAt("2022-02-01T12:26:56Z")
        .lastModifiedAt(originalLastModified)
        .build();

    when(basketOutPort.getBasketById(basketRef)).thenReturn(mockBasket);
    when(basketOutPort.updateBasket(any(Basket.class)))
        .thenThrow(new RuntimeException("simulated persistence failure"));

    String requestModifyTimestamp = String.valueOf(
        Instant.parse(originalLastModified).toEpochMilli());

    // Act & Assert
    PreconditionFailedException thrown = assertThrows(PreconditionFailedException.class,
        () -> basketInPort.addPromotionToBasket(basketRef, promotionsInfoRequest,
            requestModifyTimestamp));

    String expectedMessage = String.format(
        "Failed to add promotion details for basketReference: %s", basketRef);

    assertNotNull(thrown);
    assertEquals(expectedMessage, thrown.getMessage(),
        "Exception message should match the one created in catch block");

    // Verify
    verify(basketOutPort, times(1)).getBasketById(basketRef);
    verify(basketOutPort, times(1)).updateBasket(any(Basket.class));
  }

  @Test
  void testConfirmItemProcessing_redeem_unique_promotionCode() {
    // Arrange
    final var itemId = "123";
    final var request = ConfirmItemProcessingRequest.builder()
        .status(0)
        .reqAction("COMMIT")
        .errors(List.of("Payment Success"))
        .build();

    var redeemResponse = RedeemPromoCodeResponse.builder()
        .promoCode("TEST").status(RedeemStatus.REDEEMED).build();

    var basket = mockPayNowBasket(null, 0);
    basket.setPromoKind(PromoKind.UNIQUE);
    basket.setPromotionCode("TEST");

    when(basketOutPort.getBasketById(anyString())).thenReturn(basket);
    when(basketOutPort.updateBasket(any(Basket.class))).thenReturn(basket);
    var mockedFeatureFlag = mock(FeatureFlag.class);
    var redeemFeature = mock(Feature.class);
    when(unleashWrapper.featureFlag()).thenReturn(mockedFeatureFlag);
    when(mockedFeatureFlag.getRedeemPromoCode()).thenReturn(redeemFeature);
    when(unleashWrapper.isEnabled(unleashWrapper.featureFlag().getCheckInOnline())).thenReturn(
        false);
    when(unleashWrapper.isEnabled(unleashWrapper.featureFlag().getSaveSecureBooking())).thenReturn(
        false);
    when(unleashWrapper.isEnabled(unleashWrapper.featureFlag().getRedeemPromoCode())).thenReturn(true);
    when(promoOutPort.redeemPromoCode(anyString(), anyString())).thenReturn(redeemResponse);
    when(unleashWrapper.isEnabled(redeemFeature)).thenReturn(true);

    // Act
    basketInPort.confirmItemProcessing(BASKET_ID, itemId, request);

    // Assert
    verify(basketOutPort, times(1)).updateBasket(basket);
    verify(promoOutPort, times(1)).redeemPromoCode(anyString(),anyString());
  }

  @Test
  void testConfirmItemProcessing_redeem_unique_promotionCode_failure() {
    // Arrange
    final var itemId = "123";
    final var request = ConfirmItemProcessingRequest.builder()
        .status(0)
        .reqAction("COMMIT")
        .errors(List.of("Payment Success"))
        .build();

    var redeemResponse = RedeemPromoCodeResponse.builder()
        .promoCode("TEST").status(RedeemStatus.ALREADY_REDEEMED).build();

    var basket = mockPayNowBasket(null, 0);
    basket.setPromoKind(PromoKind.UNIQUE);
    basket.setPromotionCode("TEST");

    when(basketOutPort.getBasketById(anyString())).thenReturn(basket);
    when(basketOutPort.updateBasket(any(Basket.class))).thenReturn(basket);
    var mockedFeatureFlag = mock(FeatureFlag.class);
    var redeemFeature = mock(Feature.class);
    when(unleashWrapper.featureFlag()).thenReturn(mockedFeatureFlag);
    when(mockedFeatureFlag.getRedeemPromoCode()).thenReturn(redeemFeature);
    when(unleashWrapper.isEnabled(unleashWrapper.featureFlag().getCheckInOnline())).thenReturn(
        false);
    when(unleashWrapper.isEnabled(unleashWrapper.featureFlag().getSaveSecureBooking())).thenReturn(
        false);
    when(unleashWrapper.isEnabled(redeemFeature)).thenReturn(true);

    when(promoOutPort.redeemPromoCode(anyString(), anyString())).thenReturn(redeemResponse);

    // Act
    basketInPort.confirmItemProcessing(BASKET_ID, itemId, request);

    // Assert
    verify(basketOutPort, times(1)).updateBasket(basket);
    verify(promoOutPort, times(1)).redeemPromoCode(anyString(), anyString());
  }

  @Test
  void testChangeIdContext_invalidBasketStatus() {
    // Arrange
    final String bookingRef = "TEST123123";

    // Act
    var exception = assertThrows(BookingReferenceNotFoundException.class,
        () -> basketInPort.changeIdContext(bookingRef,""));

    // Assert
    assertNotNull(exception);
  }

  @Test
  void testChangeIdContext_basketNotFound() {
    // Arrange
    final String bookingRef = "TEST123123";
    when(basketOutPort.getBasketByReference(bookingRef)).thenReturn(Optional.empty());
    final String idContext = "aaa";
    // Act
    var exception = assertThrows(BookingReferenceNotFoundException.class,
        () -> basketInPort.changeIdContext(bookingRef, idContext));

    // Assert
    assertNotNull(exception);
    assertEquals("Basket with booking reference=TEST123123", exception.getMessage());
  }

  @Test
  void testIdContext_success() {
    // Arrange
    final String bookingRef = "TEST123123";
    final String idContext = "aaa";
    final var basket = mockBasket(bookingRef);
    basket.setIdContext("bbb");
    var updatedBasket = mockBasket();
    updatedBasket.setIdContext(idContext);
    when(basketOutPort.getBasketByReference(bookingRef)).thenReturn(Optional.of(basket));
    when(basketOutPort.updateBasket(basket)).thenReturn(updatedBasket);


    // Act
    var response = basketInPort.changeIdContext(bookingRef,idContext);

    // Assert
    assertNotNull(response);
    assertEquals(idContext,response.getIdContext());

  }

  @Test
  void testConfirmItemProcessing_IsCheckInOnlineWithPreregister() throws InterruptedException {
    // Arrange
    final var itemId = "123";
    final var request = ConfirmItemProcessingRequest.builder()
        .status(0)
        .reqAction("COMMIT")
        .build();

    var basket = mockBasketIsCheckInOnline(null, 0);
    when(basketOutPort.getBasketById(any(String.class))).thenReturn(basket);
    when(basketOutPort.updateBasket(any(Basket.class))).thenReturn(
        mock(Basket.class));
    mockUnleash(true,true,true);
    Feature mobilePreRegistered = mock(Feature.class);

    when(unleashWrapper.featureFlag().getMobilePreRegisteredRepurpose())
        .thenReturn(mobilePreRegistered);

    when(unleashWrapper.isEnabled(mobilePreRegistered))
        .thenReturn(true);
    var latch = new CountDownLatch(1);
    doAnswer(invocationOnMock -> {
      latch.countDown();
      return null;
    }).when(hotelReservationOutPort).updateReservationAlerts(any());

    doNothing().when(basketOutPort).updateCharacterUdfs(any());

    ArgumentCaptor<Basket> captor = ArgumentCaptor.forClass(Basket.class);
    ArgumentCaptor<ReservationAlertsRequest> alertCaptor =
        ArgumentCaptor.forClass(ReservationAlertsRequest.class);

    // Act
    basketInPort.confirmItemProcessing(BASKET_ID, itemId, request);

    // Assert
    verify(basketOutPort, times(1)).updateBasket(captor.capture());
    final var basketEntityResult = captor.getValue();
    assertThat(basketEntityResult.getStatus(), is(PRE_CHECKED_IN));
    assertThat(basketEntityResult.getItems().stream().map(BasketItem::getAck).toList(),
        containsInAnyOrder(0, 0));
    boolean called = latch.await(1, TimeUnit.SECONDS);
    assertTrue(called);
    verify(hotelReservationOutPort, times(1))
        .updateReservationAlerts(alertCaptor.capture());
    var alertRequest = alertCaptor.getValue();
    assertEquals(basket.getHotelId(), alertRequest.getHotelId());
    var reservationIds = basket.getItems().stream()
        .filter(basketItem -> BASKET_ITEM_TYPE_STAY.equals(basketItem.getType()))
        .map(BasketItem::getSourceId)
        .toList();
    reservationIds.forEach(reservationId -> {
      assertTrue(alertRequest.getReservationIds().contains(reservationId));
    });
    var alert = alertRequest.getAlerts().get(0);
    assertEquals(ALERT_AREA_CHECKIN, alert.getArea());
    assertEquals(ALERT_CODE_CIOL, alert.getCode());
    assertEquals(ALERT_DESCRIPTION_CIOL, alert.getDescription());
    assertTrue(alert.getScreenNotification());
    assertFalse(alert.getPrinterNotification());
    verify(emailNotificationService, Mockito.times(0))
        .sendEmailNotificationEvent(any(), any(), any(), anyBoolean());

  }

  private static ConfirmReservationResponse getConfirmReservationResponse() {
    return ConfirmReservationResponse
        .builder()
        .hotelId("Loneus")
        .reservationStatus("RESERVED").build();
  }

  private static UniqueIDTypeDto mockIdType(String id, String type) {
    UniqueIDTypeDto idTypeDto = new UniqueIDTypeDto();
    idTypeDto.setId(id);
    idTypeDto.setType(type);
    return idTypeDto;

  }

  private static BusinessNotesResponse mockBusinessNotes() {
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
            BusinessNote.builder().id("carParking").lang("en").allow("Car Parking is Authorised.")
                .deny("Car Parking is NOT Authorised.")
                .build(),
            BusinessNote.builder().id("ultimateWifi").lang("en")
                .allow("Wi-Fi Access is authorised.")
                .deny("WiFi is NOT Authorised.")
                .build(),
            BusinessNote.builder().id("dinner").lang("en")
                .allow("{price} Dinner Allowance is Authorised.")
                .deny("Dinner Allowance is NOT Authorised.")
                .build(),
            BusinessNote.builder().id("alcohol").lang("en")
                .allow("Alcohol is Authorised with the evening meal.")
                .deny("Alcohol is NOT Authorised with the evening meal.")
                .build(),
            BusinessNote.builder().id("otherCharges").lang("en").allow("Other charges "
                    + "(Travel Refresh Kits, Newspapers, etc.) are Authorised.")
                .deny("Other charges (Travel Refresh Kits, Newspapers, etc.) are Not Authorised.")
                .build())
        )
        .packages(List.of(
            Note.builder().id("premierInnBreakfast")
                .value("Premier Inn Breakfast is Pre-Booked and Authorised.")
                .build(),
            Note.builder().id("continentalBreakfast")
                .value("Continental Breakfast is Pre-Booked and Authorised.")
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

  private BusinessAllowanceRuleResponse mockBusinessAllowanceRules() {
    return BusinessAllowanceRuleResponse.builder().businessAllowances(
            List.of(
                BusinessAllowanceRule.builder().sourceId("BFADBF").aemId("premierInnBreakfast")
                    .sourceType("PACKAGE")
                    .pms("PMS").targetId("155").isTransactionCode(true).isNotesMandatory(false).build(),
                BusinessAllowanceRule.builder().sourceId("BFADCT").aemId("continentalBreakfast")
                    .sourceType("PACKAGE")
                    .pms("PMS").targetId("11").isTransactionCode(true).isNotesMandatory(false).build(),
                BusinessAllowanceRule.builder().sourceId("dinner").aemId("dinner")
                    .sourceType("ALLOWANCE")
                    .pms("PMS").targetId("FBNA").isTransactionCode(false).isNotesMandatory(true)
                    .build(),
                BusinessAllowanceRule.builder().sourceId("alcohol").aemId("alcohol")
                    .sourceType("ALLOWANCE")
                    .pms("PMS").targetId("FB").isTransactionCode(false).isNotesMandatory(true).build(),
                BusinessAllowanceRule.builder().sourceId("carParking").aemId("carParking")
                    .sourceType("ALLOWANCE")
                    .pms("PMS").targetId("PARK").isTransactionCode(false).isNotesMandatory(true)
                    .build(),
                BusinessAllowanceRule.builder().sourceId("otherCharges").aemId("otherCharges")
                    .sourceType("ALLOWANCE")
                    .pms("PMS").targetId("PARK").isTransactionCode(false).isNotesMandatory(false)
                    .build(),
                BusinessAllowanceRule.builder().sourceId("ultimateWifi").aemId("ultimateWifi")
                    .sourceType("ALLOWANCE")
                    .pms("PMS").targetId("WIFI").isTransactionCode(false).isNotesMandatory(false)
                    .build())
        )
        .build();
  }

  private static Map<Integer, Allowance> buildMealsMap() {

    final var mealsMapResult = new HashMap<Integer, Allowance>();
    mealsMapResult.put(11, Allowance.PREMIER_BREAKFAST);
    mealsMapResult.put(12, Allowance.CONTINENTAL_BREAKFAST);
    mealsMapResult.put(17, Allowance.MEAL_DEAL);
    mealsMapResult.put(18, Allowance.BOXED_BREAKFAST);

    return mealsMapResult;
  }

  private Basket mockBasket(String reference) {
    return Basket.builder().reference(reference)
        .basketId("ABC-".concat(UUID.randomUUID().toString()))
        .hotelId("Loneus")
        .channel("CCUI_BOOKING_CHANNEL")
        .subChannel("MOBILE")
        .status(BasketStatus.OPEN).
        items(List.of(BasketItem.builder().sourceId("101")
            .build()))
        .userId("userId")
        .createdAt(Instant.now().plus(1, ChronoUnit.DAYS).toString())
        .sendMail(true)
        .paymentOption(PAY_NOW.name())
        .paymentID("paymentId")
        .lockingTime("2024-10-21T15:00:00Z")
        .build();
  }

  private Basket mockBasketMultipleItems(String reference) {
    return Basket.builder().reference(reference)
        .basketId("ABC-".concat(UUID.randomUUID().toString()))
        .hotelId("Loneus")
        .channel("CCUI_BOOKING_CHANNEL")
        .status(BasketStatus.OPEN).
        items(List.of(BasketItem.builder().sourceId("101")
            .build(), BasketItem.builder().sourceId("102")
            .build()))
        .userId("userId")
        .createdAt(Instant.now().plus(1, ChronoUnit.DAYS).toString())
        .sendMail(true)
        .paymentOption(PAY_NOW.name())
        .paymentID("paymentId")
        .build();
  }

  private static PaymentRequest getPaymentRequest() {
    return PaymentRequest.builder()
        .requestId("111")
        .payment(Payment.builder()
            .amount(Amount.builder()
                .currency("GBP")
                .minorUnits(valueOf(1000))
                .build())
            .billing(Billing.builder()
                .address(uk.co.whitbread.basket.domain.model.payments.in.Address.builder()
                    .line1("120 Holborn")
                    .line2("")
                    .line3("")
                    .line4("")
                    .countryCode("gb")
                    .postalCode("EC1N 2TD")
                    .build())
                .email("email@whitbread.com")
                .firstName("Samuel")
                .lastName("Whitbread")
                .telephone("0777777777")
                .title("Mr")
                .build())
            .card(Card.builder()
                .cardholderName("Samuel Whitbread")
                .cardType("VS")
                .cnpRequired(false)
                .expiryMonth("01")
                .expiryYear("24")
                .logoUrl("https://www.premierinn.com/logo")
                .token("4943056398164344242")
                .type("type")
                .build())
            .environment("https://www.premierinn.com")
            .subType("ECOMM")
            .type("CARD")
            .pibaCardPresent(false)
            .build())
        .booking(Booking.builder()
            .arrivalDate("2022-05-17")
            .businessSite(BusinessSite.builder()
                .identifier("LONHOL")
                .location("London")
                .name("London Holborn Premier Inn")
                .type("HOTEL")
                .build())
            .channel("PI")
            .departureDate("2022-05-20")
            .journey("BOOKING")
            .language("en")
            .leadGuest(Guest.builder()
                .name("Samuel Whitbread")
                .previousBookings(2)
                .registered(true)
                .registeredSince(LocalDate.of(2022, 4, 1))
                .build())
            .reference("reference")
            .rooms(of(RoomType.builder()
                .adultsNumber(1)
                .rate("SV344")
                .type("DB")
                .build()))
            .type(PAY_NOW.name())
            .build())
        .businessAccount(
            BusinessAccount.builder().customerReference("123456").purchaseOrder("7891011")
                .carParkingAllowed("Yes").build())
        .bookingNotes(List.of("SpecialRequirements"))
        .build();
  }

  private ReservationGuestRequest createMultipleRoomGuestDetails() {
    var multiResGuestDetails = createValidReservationGuestRequest();

    return ReservationGuestRequest.builder()
        .booker(multiResGuestDetails.getBooker())
        .basketReference(multiResGuestDetails.getBasketReference())
        .stayingGuests(List.of(multiResGuestDetails.getStayingGuests().get(0),
            multiResGuestDetails.getStayingGuests().get(0)))
        .sendEmailInvoice(true)
        .sendEmailConfirmation(true)
        .bookerProfileId("12345")
        .build();
  }

  private ReservationGuestRequest createValidReservationGuestRequest() {

    BookerAddress address = BookerAddress.builder()
        .postalCode("MZC AD")
        .addressType("HOME")
        .addressLine1("4 Brockley Avenue")
        .countryCode("UK")
        .cityName("London")
        .companyName("company")
        .build();
    StayingGuestAddress stayingGuestAddress = StayingGuestAddress.builder()
        .postalCode("MZC AD")
        .addressType("HOME")
        .addressLine1("4 Brockley Avenue")
        .countryCode("UK")
        .cityName("London")
        .companyName("company")
        .build();

    BookerDetails booker = BookerDetails.builder()
        .title("Mrs")
        .firstName("John")
        .lastName("McEnroe")
        .emailAddress("john.mcenroe@mail.com")
        .acceptFutureMailing(Boolean.FALSE)
        .mobile("+39567463783")
        .address(address)
        .build();

    StayingGuestDetails stayingGuest = StayingGuestDetails.builder()
        .title("Mrs")
        .firstName("Debbie")
        .lastName("Doe")
        .address(stayingGuestAddress)
        .build();

    StayingGuest stayingGuests = StayingGuest.builder()
        .reservationId("101")
        .sameAsBooker(false)
        .stayingGuestDetails(stayingGuest)
        .build();
    return ReservationGuestRequest.builder()
        .hotelId("Loneus")
        .booker(booker)
        .stayingGuests(List.of(stayingGuests))
        .sendEmailConfirmation(true)
        .sendEmailInvoice(true)
        .bookerProfileId("12345")
        .companyProfileId(null)
        .build();
  }

  private ReservationPackagesRequest createSavePackagesRequest() {
    ReservationPackagesRequest reservationPackagesRequest = new ReservationPackagesRequest();

    reservationPackagesRequest.setReservationsId(List.of("101"));
    reservationPackagesRequest.setHotelId("Loneus");
    reservationPackagesRequest.setArrivalDate("2022-04-02");
    reservationPackagesRequest.setDepartureDate("2022-04-03");

    reservationPackagesRequest.setRoomsSelections(
        Collections.singletonList(createRoomsSelections()));
    return reservationPackagesRequest;
  }

  private RoomsSelections createRoomsSelections() {
    RoomsSelections roomsSelections = new RoomsSelections();

    roomsSelections.setPackagesSelection(Collections.singletonList(createPackagesSelection()));

    return roomsSelections;
  }

  private PackagesSelection createPackagesSelection() {
    PackagesSelection packagesSelection = new PackagesSelection();

    packagesSelection.setNoOfSelections(1);
    packagesSelection.setId("MDP");

    return packagesSelection;
  }

  private HotelPaymentInformation mockHotelPaymentInfoResponse() {
    return HotelPaymentInformation.builder()
        .acceptedCreditCards(List.of(
            AcceptedCreditCard.builder().code("DL").code3CP("VS").codeOpera("VA").build(),
            AcceptedCreditCard.builder().code("EL").code3CP("VS").codeOpera("VA").build()
        )).build();
  }

  private void mockInstantNow() {
    var clock = Clock.fixed(FIXED_INSTANT, ZoneOffset.UTC);
    var mockedInstant = Instant.now(clock);
    mockedStatic.when(Instant::now).thenReturn(mockedInstant);
  }

  private void mockCheckInOnlineFeatureFlag(boolean isOn) {
    var mockedFeatureFlag = mock(FeatureFlag.class);
    when(unleashWrapper.featureFlag()).thenReturn(mockedFeatureFlag);
    when(mockedFeatureFlag.getCheckInOnline()).thenReturn(mock(Feature.class));
    when(unleashWrapper.isEnabled(mockedFeatureFlag.getCheckInOnline())).thenReturn(isOn);
  }

  private void mockUnleash(boolean redeemOn, boolean ciolOn, boolean secureOn) {
    FeatureFlag ff = mock(FeatureFlag.class);

    Feature redeem = mock(Feature.class);
    Feature ciol = mock(Feature.class);
    Feature secure = mock(Feature.class);
    Feature publishBookingCompleted = mock(Feature.class);

    when(unleashWrapper.featureFlag()).thenReturn(ff);

    when(ff.getRedeemPromoCode()).thenReturn(redeem);
    when(ff.getCheckInOnline()).thenReturn(ciol);
    when(ff.getSaveSecureBooking()).thenReturn(secure);
    when(ff.getPublishDatatransBookingCompletedEvent()).thenReturn(publishBookingCompleted);

    when(unleashWrapper.isEnabled(redeem)).thenReturn(redeemOn);
    when(unleashWrapper.isEnabled(ciol)).thenReturn(ciolOn);
    when(unleashWrapper.isEnabled(secure)).thenReturn(secureOn);
    when(unleashWrapper.isEnabled(publishBookingCompleted)).thenReturn(true);
  }


  private ReservationByBasketRefResponse mockReservation(BigDecimal balance) {
    return ReservationByBasketRefResponse.builder()
        .totalCost(BigDecimal.TEN)
        .balanceOutstanding(balance)
        .currencyCode("GB")
        .reservationByIdList(emptyList())
        .hotelId("MANOLD")
        .build();
  }

  private void assertUdf(UdfsRequestDto udfsRequestDto, String hotelId, List<String> reservationIds){
    assertEquals(hotelId, udfsRequestDto.getHotelId());
    assertEquals(reservationIds, udfsRequestDto.getReservationIds().stream().toList());
    udfsRequestDto.getUdfs().forEach(udf -> assertEquals("UDFC20", udf.getName()));
    udfsRequestDto.getUdfs().forEach(udf -> assertEquals("CIOL_COMPLETED", udf.getValue()));
  }

  @ParameterizedTest(name = "{0}")
  @MethodSource("provideSkipOBScenarios")
  void testIsSkipOutstandingBalanceParameterized(String name, List<ReservationInfoPaymentType> paymentTypes,
                                                   List<BasketItem> items, boolean inputFlag, boolean expected) {
        Basket basket = new Basket();
        basket.setHotelId("HOTEL100");
        basket.setItems(items);
        boolean shouldStub = paymentTypes != null && items != null && items.stream()
                .anyMatch(i -> i.getSourceId() != null);
        if (shouldStub) {
          when(basketOutPort.getPaymentType(eq("HOTEL100"), anySet())).thenReturn(paymentTypes);
        }
        boolean result = basketInPort.isSkipOutstandingBalance(basket, inputFlag);
        assertEquals(expected, result);
  }

  static Stream<Arguments> provideSkipOBScenarios() {
        var typeBU = new ReservationInfoPaymentType(new ReservationPaymentCardType("BU", 2));
        var typeBD = new ReservationInfoPaymentType(new ReservationPaymentCardType("BD", 2));
        var wrongMethod = new ReservationInfoPaymentType(new ReservationPaymentCardType("XX", 2));
        var wrongFolio = new ReservationInfoPaymentType(new ReservationPaymentCardType("BU", 123));
        var nullCard = new ReservationInfoPaymentType(null);

        BasketItem validItem = new BasketItem("ROOM", "SRC1", null, null, null, null);
        BasketItem nullSrcItem = new BasketItem("ROOM", null, null, null, null, null);

        return Stream.of(
                // 1. BU + correct folio → TRUE
                Arguments.of("BU valid", of(typeBU), of(validItem), false, true),

                // 2. BD + correct folio → TRUE
                Arguments.of("BD valid", of(typeBD), of(validItem), false, true),

                // 3. Wrong method → FALSE
                Arguments.of("Wrong method", of(wrongMethod), of(validItem), false, false),

                // 4. Wrong folio → FALSE
                Arguments.of("Wrong folio", of(wrongFolio), of(validItem), false, false),

                // 5. Null card → FALSE
                Arguments.of("Null card", of(nullCard), of(validItem), false, false),

                // 6. Empty items → FALSE
                Arguments.of("Empty items", of(typeBU), of(), false, false),

                // 7. SourceId null → sourceIds empty → FALSE
                Arguments.of("Null source id", of(typeBU), of(nullSrcItem), false, false),

                // 8. paymentTypes null → FALSE
                Arguments.of("Null payment types", null, of(validItem), false, false),

                // 9. Input skipOutstandingBalance TRUE → always FALSE
                Arguments.of("Input flag true", of(wrongMethod), of(validItem), true, false)
        );
  }
}