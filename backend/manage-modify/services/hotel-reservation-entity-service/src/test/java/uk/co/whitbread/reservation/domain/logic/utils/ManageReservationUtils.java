package uk.co.whitbread.reservation.domain.logic.utils;

import static uk.co.whitbread.reservation.domain.logic.utils.UserDefinedFieldsConstants.USER_ACCOUNT_ID_UDFC_35;
import static uk.co.whitbread.reservation.domain.model.in.PaymentOption.PAY_NOW;
import static uk.co.whitbread.reservation.domain.model.in.PaymentOption.PAY_ON_ARRIVAL;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.apache.commons.lang3.StringUtils;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.basket.BasketDto;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.basket.BasketDto.StatusEnum;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.basket.BasketItemDto;
import uk.co.whitbread.reservation.domain.model.in.BookingChannel;
import uk.co.whitbread.reservation.domain.model.in.CancelReservationRequest;
import uk.co.whitbread.reservation.domain.model.in.FindBookingRequest;
import uk.co.whitbread.reservation.domain.model.in.PaymentOption;
import uk.co.whitbread.reservation.domain.model.index.header.data.out.BookingSearch;
import uk.co.whitbread.reservation.domain.model.index.header.data.out.Config;
import uk.co.whitbread.reservation.domain.model.index.header.data.out.Cookie;
import uk.co.whitbread.reservation.domain.model.index.header.data.out.DashboardRedirect;
import uk.co.whitbread.reservation.domain.model.index.header.data.out.IndexHeaderData;
import uk.co.whitbread.reservation.domain.model.out.AdditionalGuestInfoResponse;
import uk.co.whitbread.reservation.domain.model.out.BasketItemResponse;
import uk.co.whitbread.reservation.domain.model.out.BasketResponse;
import uk.co.whitbread.reservation.domain.model.out.BillingResponse;
import uk.co.whitbread.reservation.domain.model.out.BookingAllowance;
import uk.co.whitbread.reservation.domain.model.out.BookingAllowancesResponse;
import uk.co.whitbread.reservation.domain.model.out.BusinessAllowanceRule;
import uk.co.whitbread.reservation.domain.model.out.BusinessAllowanceRuleResponse;
import uk.co.whitbread.reservation.domain.model.out.CdhResults;
import uk.co.whitbread.reservation.domain.model.out.CdhSearchBookingsResponse;
import uk.co.whitbread.reservation.domain.model.out.ChannelRuleRequestDetails;
import uk.co.whitbread.reservation.domain.model.out.ChannelRuleResponse;
import uk.co.whitbread.reservation.domain.model.out.CharacterUDFs;
import uk.co.whitbread.reservation.domain.model.out.Classifications;
import uk.co.whitbread.reservation.domain.model.out.CurrencyAmount;
import uk.co.whitbread.reservation.domain.model.out.CurrencyAmountType;
import uk.co.whitbread.reservation.domain.model.out.DepositFolio;
import uk.co.whitbread.reservation.domain.model.out.DepositFolioCharge;
import uk.co.whitbread.reservation.domain.model.out.DepositFoliosResponse;
import uk.co.whitbread.reservation.domain.model.out.DepositPolicies;
import uk.co.whitbread.reservation.domain.model.out.DepositPoliciesResponse;
import uk.co.whitbread.reservation.domain.model.out.Deposits;
import uk.co.whitbread.reservation.domain.model.out.DepositsResponse;
import uk.co.whitbread.reservation.domain.model.out.ExternalReferenceType;
import uk.co.whitbread.reservation.domain.model.out.PackagesSelection;
import uk.co.whitbread.reservation.domain.model.out.RateInfo;
import uk.co.whitbread.reservation.domain.model.out.RateInfoSummary;
import uk.co.whitbread.reservation.domain.model.out.RatePlan;
import uk.co.whitbread.reservation.domain.model.out.RatePlansResponse;
import uk.co.whitbread.reservation.domain.model.out.ResCashieringType;
import uk.co.whitbread.reservation.domain.model.out.ReservationBooker;
import uk.co.whitbread.reservation.domain.model.out.ReservationByBasketRefResponse;
import uk.co.whitbread.reservation.domain.model.out.ReservationByIdDetailsResponse;
import uk.co.whitbread.reservation.domain.model.out.ReservationByIdGuestsResponse;
import uk.co.whitbread.reservation.domain.model.out.ReservationByIdResponse;
import uk.co.whitbread.reservation.domain.model.out.ReservationDetails;
import uk.co.whitbread.reservation.domain.model.out.ReservationId;
import uk.co.whitbread.reservation.domain.model.out.ReservationIdDetailsResponse;
import uk.co.whitbread.reservation.domain.model.out.ReservationInfo;
import uk.co.whitbread.reservation.domain.model.out.ReservationOverrideReasons;
import uk.co.whitbread.reservation.domain.model.out.ReservationPackagesDetailsResponse;
import uk.co.whitbread.reservation.domain.model.out.ReservationPaymentCardType;
import uk.co.whitbread.reservation.domain.model.out.ReservationPolicies;
import uk.co.whitbread.reservation.domain.model.out.ReservationTaxTypeInfo;
import uk.co.whitbread.reservation.domain.model.out.Reservations;
import uk.co.whitbread.reservation.domain.model.out.ReservationsDetailsEnhancedResponse;
import uk.co.whitbread.reservation.domain.model.out.ReservationsPackagesResponse;
import uk.co.whitbread.reservation.domain.model.out.RoomRates;
import uk.co.whitbread.reservation.domain.model.out.RoomStay;
import uk.co.whitbread.reservation.domain.model.out.RoomStayByIdResponse;
import uk.co.whitbread.reservation.domain.model.out.Rooms;
import uk.co.whitbread.reservation.domain.model.out.RoomsSelectionsByReservation;
import uk.co.whitbread.reservation.domain.model.out.UniqueIDType;
import uk.co.whitbread.reservation.domain.model.out.UserDefinedFields;
import uk.co.whitbread.shared.auth.account.Account;

public class ManageReservationUtils {

  private static final String BASKET_REFERENCE = "TST-16a014c5-d8a4-4418-8d15-2537660f08e9";
  private static final String BOOKING_REFERENCE = "TST1234567";

  private static final String BOOKING_REFERENCE_OPERA_RSV = "12345678";

  private static final String TEST_HOTEL_ID = "TestHotelId";
  private static final String HOTEL_ID = "TESTHOTEL";
  private static final String POLICY_CODE = "code";
  private static final String GIVEN_NAME = "Gate";
  private static final String SURNAME = "John";
  private static final String EMAIL = "email@mydomain.com";
  private static final String CARD_NUMBER = "XXXXXXXXXXXX1103";
  private static final String CARD_TOKEN = "4216333880397891103";
  private static final String CARD_TYPE = "Va";
  private static final String CARD_TYPE_PIBA = "ZZ";
  private static final String ARRIVAL_DATE = "2022-05-05";
  private static final String DEPARTURE_DATE = "2022-05-07";
  private static final String RATE_PLAN_CODE_FLEX = "FLEX";
  private static final String RATE_PLAN_CODE_NONFLEX = "NONFLEX";
  private static final String RATE_PLAN_CODE_STANDARD = "STANDARD";
  private static final String RATE_PLAN_CODE_ADVANCE = "ADVANCE";
  private static final String RATE_PLAN_CODE_SEMIFLEX = "SEMIFLEX";
  private static final String RATE_PLAN_CODE_TRAVEL_INDUSTRY_RATE = "FCDNLR30";
  private static final String ROOM_TYPE = "SINGLE";
  private static final String CELL_CODE = "cellCode";
  private static final String ROOM_NUMBER = "120";
  private static final String BOOKING_CHANNEL = "PI.com";
  private static final String REASON_NAME = "Illness";
  private static final String CALLER_NAME = "John Doe";
  private static final String MANAGER_NAME = "James Bond";
  private static final String SOURCE_CODE = "44";
  private static final String UNIQUE_ID_TYPE = "ABCD123456";
  private static final String PAYMENT_METHOD = "VA";
  private static final String A2C_GUARANTEE_OPERA_CODE = "CO";
  private static final Integer FOLIO_VIEW = 1;

  public static final String OPERA_CONFIRMATION = "12345678";
  public static final String PACKAGE_GROUP = "MDP";
  public static final String CUSTOMER_ACCOUNT_ID = "CUST_f95588df-4ef0-45ca-a8d0-d312da548f01";
  public static final String EMPLOYEE_ACCOUNT_ID = "EMPL_48a03f2b-8d6a-462c-8d1a-b63377f9b497";
  public static final String COMPANY_ACCOUNT_ID = "COMP_3a1aabf8-9a9c-41e0-a601-c0ab92724c52";

  public static List<BasketItemResponse> mockBasketItemsListForCancel() {
    List<BasketItemResponse> itemResponseList = new LinkedList<>();
    itemResponseList.add(
            BasketItemResponse.builder().type("reservation").sourceId("12345").build());
    return itemResponseList;
  }

  public static RatePlansResponse mockRatePlans(String rate1, String rate2, String rate3) {
    return RatePlansResponse.builder()
        .ratePlans(List.of(
            RatePlan.builder()
                .ratePlanCode(rate1)
                .classifications(Classifications.builder().displaySet("NEG").build())
                .build(),
            RatePlan.builder()
                .ratePlanCode(rate2)
                .classifications(Classifications.builder().displaySet("NEG").build())
                .build(),
            RatePlan.builder()
                .ratePlanCode(rate3)
                .classifications(Classifications.builder().displaySet("NEG").build())
                .build()))
        .build();
  }

  public static RatePlansResponse mockRatePlans() {
    return RatePlansResponse.builder()
        .ratePlans(Collections.singletonList(
            RatePlan.builder()
                .ratePlanCode("FLEX")
                .classifications(Classifications.builder().displaySet("NEG").build())
                .build()))
        .build();
  }

  public static ReservationByBasketRefResponse mockReservationByBasketRefResponse() {
    return ReservationByBasketRefResponse.builder()
            .amountPaid(BigDecimal.valueOf(30))
            .totalCost(BigDecimal.TEN)
            .newTotal(BigDecimal.TEN)
            .previousTotal(BigDecimal.TEN)
            .balanceOutstanding(BigDecimal.TEN)
            .hotelId(HOTEL_ID)
            .policyCode(POLICY_CODE)
            .currencyCode("GBP")
            .reservationByIdList(
                    List.of(mockReservationByIdFlex("11111111"), mockReservationByIdFlex("22222222"),
                            mockReservationByIdFlex("33333333")))
            .build();
  }

  public static ReservationByBasketRefResponse mockReservationByBasketRefResponse3rdParty(
      String rate1, String rate2, String rate3, String sourceCode) {
    return ReservationByBasketRefResponse.builder()
        .amountPaid(BigDecimal.valueOf(30))
        .totalCost(BigDecimal.TEN)
        .newTotal(BigDecimal.TEN)
        .previousTotal(BigDecimal.TEN)
        .balanceOutstanding(BigDecimal.TEN)
        .hotelId(HOTEL_ID)
        .policyCode(POLICY_CODE)
        .currencyCode("GBP")
        .reservationByIdList(
            List.of(mockReservationById3rdParty("11111111", rate1, sourceCode),
                mockReservationById3rdParty("22222222", rate2, sourceCode),
                mockReservationById3rdParty("33333333", rate3, sourceCode)))
        .build();
  }

  public static ReservationByBasketRefResponse mockReservationByBasketRefTravelIndustryRateResponse() {
    return ReservationByBasketRefResponse.builder()
        .amountPaid(BigDecimal.valueOf(20))
        .totalCost(BigDecimal.TEN)
        .newTotal(BigDecimal.TEN)
        .previousTotal(BigDecimal.TEN)
        .balanceOutstanding(BigDecimal.TEN)
        .hotelId(HOTEL_ID)
        .policyCode(POLICY_CODE)
        .currencyCode("GBP")
        .reservationByIdList(
            List.of(mockReservationByIdAndRatePlanCode("1111111",
                RATE_PLAN_CODE_TRAVEL_INDUSTRY_RATE)))
        .build();
  }

  public static ReservationByBasketRefResponse mockReservationByBasketRefResponseCancelledStatus() {
    return ReservationByBasketRefResponse.builder()
            .amountPaid(BigDecimal.valueOf(30))
            .totalCost(BigDecimal.TEN)
            .newTotal(BigDecimal.TEN)
            .previousTotal(BigDecimal.TEN)
            .balanceOutstanding(BigDecimal.TEN)
            .hotelId(HOTEL_ID)
            .policyCode(POLICY_CODE)
            .currencyCode("GBP")
            .reservationByIdList(
                    List.of(mockReservationByIdCancelledStatus("1")))
            .build();
  }

  public static ReservationByBasketRefResponse mockReservationByBasketRefResponseMoreRooms() {
    return ReservationByBasketRefResponse.builder()
            .amountPaid(BigDecimal.valueOf(30))
            .totalCost(BigDecimal.TEN)
            .newTotal(BigDecimal.TEN)
            .previousTotal(BigDecimal.TEN)
            .balanceOutstanding(BigDecimal.TEN)
            .hotelId(HOTEL_ID)
            .policyCode(POLICY_CODE)
            .currencyCode("GBP")
            .reservationByIdList(
                    List.of(mockReservationByIdFlex("11111111"), mockReservationByIdFlex("22222222"),
                            mockReservationByIdFlex("33333333"), mockReservationByIdFlex("44444444"),
                            mockReservationByIdFlex("55555555")))
            .build();
  }

  public static BookingChannel mockBookingChannel() {
    var bookingChannel = new BookingChannel();
    bookingChannel.setChannel("PI");
    bookingChannel.setSubchannel("MOBILE");
    bookingChannel.setLanguage("EN");

    return bookingChannel;
  }

  public static BookingChannel mockBookingChannel(String channel, String subchannel) {
    var bookingChannel = new BookingChannel();
    bookingChannel.setChannel(channel);
    bookingChannel.setSubchannel(subchannel);
    bookingChannel.setLanguage("EN");
    return bookingChannel;
  }

  public static ReservationByIdResponse mockReservationByIdFlex(String reservationId) {
    return ReservationByIdResponse.builder()
            .reservationId(reservationId)
            .reservationGuestList(mockReservationGuestList())
            .reservationPackageList(mockReservationPackagesList())
            .reservationBooker(mockReservationBooker())
            .roomStay(mockRoomStay(RATE_PLAN_CODE_FLEX))
            .depositPolicies(mockDepositPolicies())
            .paymentCard(mockPaymentCard())
            .reservationOverrideReasons(mockReservationOverrideReasons())
            .reservationOverridden(true)
            .reservationStatus("Reserved")
            .guaranteeCode("NON")
            .balanceAmount(BigDecimal.valueOf(60))
            .rateInfo(mockRateInfoWithoutPayOnArrivalAmountLeft())
            .additionalGuestInfo(mockAdditionalGuestInfoResponse())
            .billing(BillingResponse.builder().lastName("John").email(EMAIL).build())
            .cashiering(ResCashieringType.builder()
                    .taxType(ReservationTaxTypeInfo.builder().code("UK").build()).build())
            .userDefinedFields(UserDefinedFields.builder()
                    .characterUDFs(List.of(CharacterUDFs.builder()
                            .name(USER_ACCOUNT_ID_UDFC_35)
                            .value(CUSTOMER_ACCOUNT_ID)
                            .build()))
                    .build())
            .build();
  }

  public static ReservationByIdResponse mockReservationById3rdParty(String reservationId, String rate, String sourceCode) {
    return ReservationByIdResponse.builder()
        .reservationId(reservationId)
        .reservationGuestList(mockReservationGuestList())
        .reservationPackageList(mockReservationPackagesList())
        .reservationBooker(mockReservationBooker())
        .roomStay(mockRoomStay(rate, sourceCode))
        .depositPolicies(mockDepositPolicies())
        .paymentCard(mockPaymentCard())
        .reservationOverrideReasons(mockReservationOverrideReasons())
        .reservationOverridden(true)
        .reservationStatus("Reserved")
        .guaranteeCode("NON")
        .balanceAmount(BigDecimal.valueOf(60))
        .rateInfo(mockRateInfoWithoutPayOnArrivalAmountLeft())
        .billing(BillingResponse.builder().lastName("John").email(EMAIL).build())
        .cashiering(ResCashieringType.builder()
            .taxType(ReservationTaxTypeInfo.builder().code("UK").build()).build())
        .userDefinedFields(UserDefinedFields.builder()
            .characterUDFs(List.of(CharacterUDFs.builder()
                .name(USER_ACCOUNT_ID_UDFC_35)
                .value(CUSTOMER_ACCOUNT_ID)
                .build()))
            .build())
        .build();
  }

  public static ReservationByIdResponse mockReservationByIdAndRatePlanCode(
      String reservationId, String ratePlanCode) {
    return ReservationByIdResponse.builder()
        .reservationId(reservationId)
        .reservationGuestList(mockReservationGuestList())
        .reservationPackageList(mockReservationPackagesList())
        .reservationBooker(mockReservationBooker())
        .roomStay(mockRoomStay(ratePlanCode))
        .depositPolicies(mockDepositPolicies())
        .paymentCard(mockPaymentCard())
        .reservationOverrideReasons(mockReservationOverrideReasons())
        .reservationOverridden(true)
        .reservationStatus("Reserved")
        .guaranteeCode("NON")
        .balanceAmount(BigDecimal.valueOf(60))
        .rateInfo(mockRateInfoWithoutPayOnArrivalAmountLeft())
        .billing(BillingResponse.builder().lastName("John").email(EMAIL).build())
        .cashiering(ResCashieringType.builder()
            .taxType(ReservationTaxTypeInfo.builder().code("UK").build()).build())
        .build();
  }

  public static ReservationByIdResponse mockReservationByIdCancelledStatus(String reservationId) {
    return ReservationByIdResponse.builder()
            .reservationId(reservationId)
            .reservationGuestList(mockReservationGuestList())
            .reservationPackageList(mockReservationPackagesList())
            .reservationBooker(mockReservationBooker())
            .roomStay(mockRoomStay(RATE_PLAN_CODE_FLEX))
            .depositPolicies(mockDepositPolicies())
            .paymentCard(mockPaymentCard())
            .reservationOverrideReasons(mockReservationOverrideReasons())
            .reservationOverridden(true)
            .reservationStatus("Cancelled")
            .guaranteeCode("NON")
            .balanceAmount(BigDecimal.valueOf(60))
            .rateInfo(mockRateInfoWithoutPayOnArrivalAmountLeft())
            .billing(BillingResponse.builder().lastName("John").email(EMAIL).build())
            .cashiering(ResCashieringType.builder()
                    .taxType(ReservationTaxTypeInfo.builder().code("UK").build()).build())
            .build();
  }

  private static List<ReservationByIdGuestsResponse> mockReservationGuestList() {
    return Collections.singletonList(ReservationByIdGuestsResponse.builder()
            .surName(SURNAME)
            .givenName(GIVEN_NAME)
            .build());
  }

  private static List<ReservationPackagesDetailsResponse> mockReservationPackagesList() {
    return List.of(
            ReservationPackagesDetailsResponse.builder().packageCode("BFADBF").build()
    );
  }

  private static RateInfo mockRateInfoWithoutPayOnArrivalAmountLeft() {
    return RateInfo.builder().summary(
                    RateInfoSummary.builder()
                            .guestPay(BigDecimal.ZERO)
                            .deposit(BigDecimal.TEN.negate())
                            .build())
            .build();
  }

  private static ReservationBooker mockReservationBooker() {
    return ReservationBooker.builder()
            .email("test@wb.com")
            .build();
  }

  private static RoomStayByIdResponse mockRoomStay(String ratePlanCode) {
    return RoomStayByIdResponse.builder()
            .arrivalDate(ARRIVAL_DATE)
            .departureDate(DEPARTURE_DATE)
            .adultsNumber(2)
            .childrenNumber(0)
            .ratePlanCode(ratePlanCode)
            .roomType(ROOM_TYPE)
            .cellCode(CELL_CODE)
            .roomNumber(ROOM_NUMBER)
            .bookingChannel(BOOKING_CHANNEL)
            .sourceCode(SOURCE_CODE)
            .build();
  }

  private static RoomStayByIdResponse mockRoomStay(String ratePlanCode, String sourceCode) {
    return RoomStayByIdResponse.builder()
        .arrivalDate(ARRIVAL_DATE)
        .departureDate(DEPARTURE_DATE)
        .adultsNumber(2)
        .childrenNumber(0)
        .ratePlanCode(ratePlanCode)
        .roomType(ROOM_TYPE)
        .cellCode(CELL_CODE)
        .roomNumber(ROOM_NUMBER)
        .bookingChannel(BOOKING_CHANNEL)
        .sourceCode(sourceCode)
        .build();
  }

  private static List<DepositPoliciesResponse> mockDepositPolicies() {
    return Collections.singletonList(DepositPoliciesResponse.builder()
            .amountDue(uk.co.whitbread.reservation.domain.model.out.CurrencyAmountType.builder()
                    .currencyCode("1")
                    .amount(BigDecimal.ONE)
                    .build())
            .amountPaid(uk.co.whitbread.reservation.domain.model.out.CurrencyAmountType.builder()
                    .currencyCode("1")
                    .amount(BigDecimal.ONE)
                    .build())
            .policyCode(POLICY_CODE)
            .build());
  }

  public static ReservationPaymentCardType mockPaymentCard() {
    return ReservationPaymentCardType.builder()
            .cardNumberMasked(CARD_NUMBER)
            .token(CARD_TOKEN)
            .expirationDate(LocalDate.of(2026, 5, 31))
            .cardType(CARD_TYPE)
            .paymentMethod(PAYMENT_METHOD)
            .folioView(FOLIO_VIEW)
            .build();
  }

  public static ReservationPaymentCardType mockPaymentCardPIBA() {
    return ReservationPaymentCardType.builder()
        .cardNumberMasked(CARD_NUMBER)
        .token(CARD_TOKEN)
        .expirationDate(LocalDate.of(2026, 5, 31))
        .cardType(CARD_TYPE_PIBA)
        .paymentMethod(PAYMENT_METHOD)
        .folioView(FOLIO_VIEW)
        .build();
  }

  private static ReservationOverrideReasons mockReservationOverrideReasons() {
    return ReservationOverrideReasons.builder()
            .reasonCode("ILL")
            .reasonName(REASON_NAME)
            .callerName(CALLER_NAME)
            .managerName(MANAGER_NAME)
            .build();
  }

  public static Optional<Account> mockCurrentUserAccount(String email) {
    return Optional.of(Account.builder()
            .employeeId(EMPLOYEE_ACCOUNT_ID)
            .companyId(COMPANY_ACCOUNT_ID)
            .customerId(CUSTOMER_ACCOUNT_ID)
            .email(email)
            .build());
  }

  public static BasketResponse mockBasketResponse() {
    return mockBasketResponse(null);
  }

  public static BasketResponse mockBasketResponse(String channel) {
    return BasketResponse.builder()
            .hotelId(HOTEL_ID)
            .bookingReference(BOOKING_REFERENCE)
            .reference(BASKET_REFERENCE)
            .channel(channel)
            .createdAt(new Date().toString())
            .status("OPEN")
            .itemTypes(Collections.emptySet())
            .items(mockBasketItemsList())
            .paymentID("118932")
            .paymentOption(PaymentOption.PAY_ON_ARRIVAL)
            .originalBasketId("HOTELCODE1001001")
            .eTag("123")
            .build();
  }

  public static BasketResponse mockBasketResponseMultipleRooms(String channel) {
    return BasketResponse.builder()
        .hotelId(HOTEL_ID)
        .bookingReference(BOOKING_REFERENCE)
        .reference(BASKET_REFERENCE)
        .channel(channel)
        .createdAt(new Date().toString())
        .status("OPEN")
        .itemTypes(Collections.emptySet())
        .items(mockBasketMultipleItemsList())
        .paymentID("118932")
        .paymentOption(PaymentOption.PAY_ON_ARRIVAL)
        .originalBasketId("HOTELCODE1001001")
        .eTag("123")
        .build();
  }

  public static BasketResponse mockBasketResponseWithPackages(PaymentOption payment) {
    Map<String, String> map = new HashMap();
    map.put("refOrig", "res1");
    return BasketResponse.builder()
        .hotelId(HOTEL_ID)
        .bookingReference(BOOKING_REFERENCE)
        .reference(BASKET_REFERENCE)
        .channel("PI")
        .createdAt(new Date().toString())
        .status("OPEN")
        .itemTypes(Collections.emptySet())
        .items(mockBasketItemsList())
        .paymentID("118932")
        .paymentOption(payment)
        .originalBasketId("refOrig")
        .eTag("123")
        .linkAmendReservations(map)
        .build();
  }


  public static ReservationsPackagesResponse mockedReservationsPackage(String idPackage, String originRef) {
    var packagesSelection = uk.co.whitbread.reservation.domain.model.out.PackagesSelection.builder()
        .id(idPackage)
        .noOfSelections(1)
        .build();

    var roomSelection1 = RoomsSelectionsByReservation.builder()
        .reservationId(originRef)
        .packagesSelection(List.of(packagesSelection))
        .build();
    return ReservationsPackagesResponse.builder()
        .roomsSelections(List.of(roomSelection1))
        .build();
  }

  public static BasketResponse mockPNBasketResponse() {
    var basket = mockBasketResponse(null);
    basket.setPaymentOption(PAY_NOW);
    return basket;
  }

  public static List<BasketItemResponse> mockBasketItemsList() {
    List<BasketItemResponse> itemResponseList = new LinkedList<>();
    itemResponseList.add(BasketItemResponse.builder().type("reservation").sourceId("res1")
        .hasOccupancySup(Boolean.TRUE).build());
    itemResponseList.add(BasketItemResponse.builder().type("reservation").sourceId("res2").build());
    itemResponseList.add(BasketItemResponse.builder().type("reservation").sourceId("res3").build());
    return itemResponseList;
  }

  public static List<BasketItemResponse> mockBasketMultipleItemsList() {
    List<BasketItemResponse> itemResponseList = new LinkedList<>();
    itemResponseList.add(BasketItemResponse.builder().type("reservation").sourceId("res1")
        .hasOccupancySup(Boolean.TRUE).build());
    itemResponseList.add(BasketItemResponse.builder().type("reservation").sourceId("res2").build());
    itemResponseList.add(BasketItemResponse.builder().type("reservation").sourceId("res3").build());
    itemResponseList.add(BasketItemResponse.builder().type("reservation").sourceId("res4").build());
    itemResponseList.add(BasketItemResponse.builder().type("reservation").sourceId("res5").build());
    itemResponseList.add(BasketItemResponse.builder().type("reservation").sourceId("res6").build());
    return itemResponseList;
  }

  public static BasketResponse mockBasketResponseWithPN() {
    return BasketResponse.builder()
            .bookingReference(BOOKING_REFERENCE)
            .hotelId(HOTEL_ID)
            .reference(BASKET_REFERENCE)
            .items(Collections.singletonList(
                    BasketItemResponse.builder()
                            .sourceId(BOOKING_REFERENCE)
                            .build()))
            .paymentOption(PAY_NOW)
            .eTag("eTag")
            .build();
  }

  public static RateInfo mockRateInfoWithPayOnArrivalAmountLeft() {
    return RateInfo.builder().summary(
                    RateInfoSummary.builder().guestPay(BigDecimal.ONE).build())
            .build();
  }

  public static DepositFoliosResponse mockDepositFoliosResponse() {
    return mockDepositFoliosResponse(true);
  }

  public static DepositFoliosResponse mockDepositFoliosResponse(boolean mockDepositFolios) {
    var builder = DepositFoliosResponse.builder();
    if (mockDepositFolios) {
      builder.depositFolios(List.of(mockPrepaidDeposit()));
    }
    return builder.build();
  }

  private static DepositFolio mockPrepaidDeposit() {
    return DepositFolio.builder()
            .reservationId("2348901")
            .charges(List.of(DepositFolioCharge.builder()
                    .transactionCode("9026")
                    .quantity(1)
                    .reference("2023-08-17")
                    .currencyAmount(CurrencyAmount.builder()
                            .amount(BigDecimal.valueOf(10))
                            .currencyCode("GBP")
                            .build())
                    .build()))
            .build();
  }

  public static ReservationsDetailsEnhancedResponse mockReservationEnhancedResponse(String sourceCode) {
    return ReservationsDetailsEnhancedResponse.builder()
            .billing(BillingResponse.builder().lastName("John").build())
            .totalCost(BigDecimal.valueOf(123))
            .amountPaid(BigDecimal.valueOf(23))
            .reservations(Reservations.builder()
                    .reservationInfo(List.of(ReservationInfo.builder()
                            .externalReferences(
                                    List.of(
                                            ExternalReferenceType.builder().id("TEST123456-ABC").idContext("BART_OHIP")
                                                    .build()))
                            .hotelId(HOTEL_ID)
                            .roomStay(RoomStay.builder()
                                    .arrivalDate(LocalDate.of(2022, 05, 05))
                                    .sourceCode(sourceCode)
                                    .build())
                            .reservationStatus(BasketDto.StatusEnum.COMPLETED.name())
                            .reservationIdList(
                                    List.of(UniqueIDType.builder().id(UNIQUE_ID_TYPE).type("Reservation").build()))
                            .build()))
                    .build())
            .build();
  }

  public static BasketResponse mockCreateBasketResponse(String bookingRef) {
    return BasketResponse.builder()
            .hotelId(HOTEL_ID)
            .bookingReference(bookingRef)
            .reference(BASKET_REFERENCE)
            .createdAt(new Date().toString())
            .status("COMPLETED")
            .eTag("TEST")
            .itemTypes(Collections.emptySet())
            .items(mockBasketItemsList())
            .paymentOption(PAY_ON_ARRIVAL)
            .build();
  }

  public static BasketDto mockCreateBasketDtoResponse(String bookingRef) {
    BasketDto basketDto = new BasketDto();
    basketDto.setStatus(StatusEnum.valueOf("COMPLETED"));
    basketDto.setHotelId(HOTEL_ID);
    basketDto.bookingReference(bookingRef);
    basketDto.setReference(BASKET_REFERENCE);
    basketDto.setCreatedAt(new Date().toString());
    basketDto.setItems(mockBasketItemsDtoList());
    basketDto.setItemTypes(Collections.emptySet());
    basketDto.setPaymentOption(String.valueOf(PaymentOption.PAY_ON_ARRIVAL));
    return basketDto;
  }

  public static BasketDto mockCreateBasketDtoResponseWithoutHotelId(String bookingRef) {
    BasketDto basketDto = new BasketDto();
    basketDto.setStatus(StatusEnum.valueOf("COMPLETED"));
    basketDto.bookingReference(bookingRef);
    basketDto.setReference(BASKET_REFERENCE);
    basketDto.setCreatedAt(new Date().toString());
    basketDto.setItems(mockBasketItemsDtoList());
    basketDto.setItemTypes(Collections.emptySet());
    basketDto.setPaymentOption(String.valueOf(PaymentOption.PAY_ON_ARRIVAL));
    return basketDto;
  }

  public static List<BasketItemDto> mockBasketItemsDtoList() {
    List<BasketItemDto> basketItemDtoList = new LinkedList<>();
    basketItemDtoList.add(new BasketItemDto().sourceId("res1").type("reservation"));
    basketItemDtoList.add(new BasketItemDto().sourceId("res2") .type("reservation"));
    basketItemDtoList.add(new BasketItemDto().sourceId("res3") .type("reservation"));
    return basketItemDtoList;
  }

  public static BasketResponse mockCreateBasketIdContextResponse(String bookingRef,
      String idContext) {
    var basket = mockCreateBasketResponse(bookingRef);
    basket.setIdContext(idContext);
    return basket;
  }

  public static BasketDto mockCreateBasketReservationIdContextResponse(String bookingRef,
                                                                 String idContext) {
    var basket = mockCreateBasketDtoResponse(bookingRef);
    basket.setIdContext(idContext);
    return basket;
  }

  public static ReservationByIdResponse mockReservationByIdResponseConfirm(boolean onHold) {
    return ReservationByIdResponse.builder()
        .reservationId("res1")
            .roomStay(RoomStayByIdResponse.builder()
                    .arrivalDate("2022-12-01")
                    .departureDate("2022-12-01")
                    .adultsNumber(2)
                    .roomType("roomType")
                    .roomPrice(BigDecimal.TEN)
                    .ratePlanCode("ratePlanCode")
                    .cellCode("cellCode")
                    .sourceCode("44")
                    .build())
            .reservationGuestList(List.of(ReservationByIdGuestsResponse.builder()
                    .nameTitle("nameTitle")
                    .surName("surName")
                    .givenName("givenName")
                    .email("email")
                    .build()))
            .onHold(onHold)
            .reservationStatus("Completed")
            .paymentCard(ReservationPaymentCardType.builder()
                    .cardType("cardType")
                    .token("token")
                    .expirationDate(LocalDate.now())
                    .cardNumberMasked("cardNumberMasked")
                    .build())
            .cashiering(ResCashieringType.builder().taxType(ReservationTaxTypeInfo.builder().code("UK")
                            .build())
                    .build())
            .reservationBooker(ReservationBooker.builder().email("test@whitbread.com").build())
            .guaranteeCode("CASH")
            .reservationPackageList(List.of(ReservationPackagesDetailsResponse.builder()
                    .packageCode("PIBBEV")
                    .startDate("2023-10-20")
                    .endDate("2023-10-20")
                    .packageGroup(PACKAGE_GROUP)
                .build()))
            .build();
  }

  public static ReservationByIdResponse mockReservationByIdNonFlex() {
    return ReservationByIdResponse.builder()
            .reservationGuestList(mockReservationGuestList())
            .reservationBooker(mockReservationBooker())
            .roomStay(mockRoomStay(RATE_PLAN_CODE_NONFLEX))
            .depositPolicies(mockDepositPolicies())
            .paymentCard(mockPaymentCard())
            .reservationOverrideReasons(mockReservationOverrideReasons())
            .reservationPackageList(new ArrayList<>())
            .reservationOverridden(false)
            .reservationStatus("Reserved")
            .guaranteeCode("NON")
            .balanceAmount(BigDecimal.valueOf(60))
            .rateInfo(mockRateInfoWithoutPayOnArrivalAmountLeft())
            .billing(BillingResponse.builder().lastName("John").email(EMAIL).build())
            .cashiering(ResCashieringType.builder()
                    .taxType(ReservationTaxTypeInfo.builder().code("UK").build()).build())
            .build();
  }

  public static ReservationByIdResponse mockReservationByIdFlex() {
    return ReservationByIdResponse.builder()
            .reservationGuestList(mockReservationGuestList())
            .reservationBooker(mockReservationBooker())
            .roomStay(mockRoomStay(RATE_PLAN_CODE_FLEX))
            .depositPolicies(mockDepositPolicies())
            .paymentCard(mockPaymentCard())
            .reservationOverrideReasons(mockReservationOverrideReasons())
            .reservationPackageList(new ArrayList<>())
            .reservationOverridden(false)
            .reservationStatus("Reserved")
            .guaranteeCode("NON")
            .balanceAmount(BigDecimal.valueOf(60))
            .rateInfo(mockRateInfoWithoutPayOnArrivalAmountLeft())
            .billing(BillingResponse.builder().lastName("John").email(EMAIL).build())
            .cashiering(ResCashieringType.builder()
                    .taxType(ReservationTaxTypeInfo.builder().code("UK").build()).build())
            .build();
  }

  public static ReservationByIdResponse mockReservationByIdRandomRatePlanCode() {
    return ReservationByIdResponse.builder()
            .reservationGuestList(mockReservationGuestList())
            .reservationBooker(mockReservationBooker())
            .roomStay(mockRoomStay("random"))
            .depositPolicies(mockDepositPolicies())
            .paymentCard(mockPaymentCard())
            .reservationOverrideReasons(mockReservationOverrideReasons())
            .reservationPackageList(new ArrayList<>())
            .reservationOverridden(false)
            .reservationStatus("Reserved")
            .guaranteeCode("NON")
            .balanceAmount(BigDecimal.valueOf(60))
            .rateInfo(mockRateInfoWithoutPayOnArrivalAmountLeft())
            .billing(BillingResponse.builder().lastName("John").email(EMAIL).build())
            .cashiering(ResCashieringType.builder()
                    .taxType(ReservationTaxTypeInfo.builder().code("UK").build()).build())
            .build();
  }

  public static ReservationByIdResponse mockReservationByIdPIBA() {
    return ReservationByIdResponse.builder()
        .reservationGuestList(mockReservationGuestList())
        .reservationBooker(mockReservationBooker())
        .roomStay(mockRoomStay(RATE_PLAN_CODE_NONFLEX))
        .depositPolicies(mockDepositPolicies())
        .paymentCard(mockPaymentCardPIBA())
        .reservationOverrideReasons(mockReservationOverrideReasons())
        .reservationPackageList(new ArrayList<>())
        .reservationOverridden(false)
        .reservationStatus("Reserved")
        .guaranteeCode("NON")
        .balanceAmount(BigDecimal.valueOf(60))
        .rateInfo(mockRateInfoWithoutPayOnArrivalAmountLeft())
        .billing(BillingResponse.builder().lastName("John").email(EMAIL).build())
        .cashiering(ResCashieringType.builder()
            .taxType(ReservationTaxTypeInfo.builder().code("UK").build()).build())
        .build();
  }

  public static ReservationByIdResponse mockReservationByIdSemiFlex() {
    return ReservationByIdResponse.builder()
        .reservationGuestList(mockReservationGuestList())
        .reservationBooker(mockReservationBooker())
        .roomStay(mockRoomStay(RATE_PLAN_CODE_SEMIFLEX))
        .depositPolicies(mockDepositPolicies())
        .paymentCard(mockPaymentCardPIBA())
        .reservationOverrideReasons(mockReservationOverrideReasons())
        .reservationPackageList(new ArrayList<>())
        .reservationOverridden(false)
        .reservationStatus("Reserved")
        .guaranteeCode("NON")
        .balanceAmount(BigDecimal.valueOf(60))
        .rateInfo(mockRateInfoWithoutPayOnArrivalAmountLeft())
        .billing(BillingResponse.builder().lastName("John").email(EMAIL).build())
        .cashiering(ResCashieringType.builder()
            .taxType(ReservationTaxTypeInfo.builder().code("UK").build()).build())
        .build();
  }

  public static ReservationByIdResponse mockReservationByIdAdvanceFlex() {
    return ReservationByIdResponse.builder()
            .reservationGuestList(mockReservationGuestList())
            .reservationBooker(mockReservationBooker())
            .roomStay(mockRoomStay(RATE_PLAN_CODE_ADVANCE))
            .depositPolicies(mockDepositPolicies())
            .paymentCard(mockPaymentCardPIBA())
            .reservationOverrideReasons(mockReservationOverrideReasons())
            .reservationPackageList(new ArrayList<>())
            .reservationOverridden(false)
            .reservationStatus("Reserved")
            .guaranteeCode("NON")
            .balanceAmount(BigDecimal.valueOf(60))
            .rateInfo(mockRateInfoWithoutPayOnArrivalAmountLeft())
            .billing(BillingResponse.builder().lastName("John").email(EMAIL).build())
            .cashiering(ResCashieringType.builder()
                    .taxType(ReservationTaxTypeInfo.builder().code("UK").build()).build())
            .build();
  }

  public static ReservationByIdResponse mockReservationByIdStandardFlex() {
    return ReservationByIdResponse.builder()
            .reservationGuestList(mockReservationGuestList())
            .reservationBooker(mockReservationBooker())
            .roomStay(mockRoomStay(RATE_PLAN_CODE_STANDARD))
            .depositPolicies(mockDepositPolicies())
            .paymentCard(mockPaymentCardPIBA())
            .reservationOverrideReasons(mockReservationOverrideReasons())
            .reservationPackageList(new ArrayList<>())
            .reservationOverridden(false)
            .reservationStatus("Reserved")
            .guaranteeCode("NON")
            .balanceAmount(BigDecimal.valueOf(60))
            .rateInfo(mockRateInfoWithoutPayOnArrivalAmountLeft())
            .billing(BillingResponse.builder().lastName("John").email(EMAIL).build())
            .cashiering(ResCashieringType.builder()
                    .taxType(ReservationTaxTypeInfo.builder().code("UK").build()).build())
            .build();
  }

  public static ReservationByIdDetailsResponse mockReservationIdResponseInvalidSourceCode() {
    return ReservationByIdDetailsResponse.builder()
        .billing(BillingResponse.builder().lastName("John").build())
        .totalCost(BigDecimal.valueOf(123))
        .amountPaid(BigDecimal.valueOf(23))
        .reservationIdDetailsResponse(mockReservationByIdResponseInvalidSourceCode()).build();
  }

  public static ReservationIdDetailsResponse mockReservationByIdResponseInvalidSourceCode() {
    return ReservationIdDetailsResponse.builder()
        .reservations(ReservationId.builder()
            .reservation(List.of(ReservationDetails.builder()
                .hotelId(HOTEL_ID)
                .reservationPolicies(ReservationPolicies.builder().depositPolicies(List.of(
                    DepositPolicies.builder().amountPaid(CurrencyAmountType.builder()
                        .amount(BigDecimal.ONE).build()).build())).build())
                .roomStay(RoomStay.builder()
                    .roomRates(List.of(RoomRates.builder().build()))
                    .sourceCode(SOURCE_CODE)
                    .build())
                .build()))
            .build())
        .build();
  }

  public static ReservationByIdDetailsResponse mockReservationIdResponseForPN() {
    return ReservationByIdDetailsResponse.builder()
        .billing(BillingResponse.builder().lastName("John").build())
        .totalCost(BigDecimal.valueOf(123))
        .amountPaid(BigDecimal.valueOf(23))
        .reservationIdDetailsResponse(mockReservationByIdResponseForPN()).build();
  }

  public static ReservationIdDetailsResponse mockReservationByIdResponseForPN() {
    return ReservationIdDetailsResponse.builder()
        .reservations(ReservationId.builder()
            .reservation(List.of(ReservationDetails.builder()
                .hotelId(HOTEL_ID)
                .reservationPolicies(ReservationPolicies.builder().depositPolicies(List.of(
                    DepositPolicies.builder().amountPaid(CurrencyAmountType.builder()
                        .amount(BigDecimal.ONE).build()).build())).build())
                .roomStay(RoomStay.builder()
                    .arrivalDate(LocalDate.of(2022, 05, 05))
                    .roomRates(List.of(RoomRates.builder().sourceCode("44").build()))
                    .sourceCode(SOURCE_CODE)
                    .build())
                .reservationStatus(StatusEnum.COMPLETED.name())
                .reservationIdList(
                    List.of(UniqueIDType.builder().id(UNIQUE_ID_TYPE).type("Reservation").build()))
                .build()))
            .build())
        .build();
  }

  public static ReservationByIdDetailsResponse mockReservationIdResponsePOA() {
    return ReservationByIdDetailsResponse.builder()
        .billing(BillingResponse.builder().lastName("John").build())
        .totalCost(BigDecimal.valueOf(123))
        .amountPaid(BigDecimal.valueOf(23))
        .reservationIdDetailsResponse(mockReservationByIdResponsePOA()).build();
  }

  public static ReservationIdDetailsResponse mockReservationByIdResponsePOA() {
    return ReservationIdDetailsResponse.builder()
        .reservations(ReservationId.builder()
            .reservation(List.of(ReservationDetails.builder()
                .hotelId(HOTEL_ID)
                .reservationPolicies(ReservationPolicies.builder().depositPolicies(List.of(
                    DepositPolicies.builder().amountPaid(CurrencyAmountType.builder()
                        .amount(BigDecimal.ZERO).build()).build())).build())
                .roomStay(RoomStay.builder()
                    .arrivalDate(LocalDate.of(2022, 05, 05))
                    .roomRates(List.of(RoomRates.builder().sourceCode("44").build()))
                    .sourceCode(SOURCE_CODE)
                    .build())
                .reservationStatus(BasketDto.StatusEnum.COMPLETED.name())
                .reservationIdList(
                    List.of(UniqueIDType.builder().id(UNIQUE_ID_TYPE).type("Reservation").build()))
                .build()))
            .build())
        .build();
  }
  public static ReservationByIdResponse mockReservationByIdEmployeeRate() {
    return ReservationByIdResponse.builder()
            .reservationGuestList(mockReservationGuestList())
            .reservationBooker(mockReservationBooker())
            .roomStay(mockRoomStay(RATE_PLAN_CODE_FLEX))
            .depositPolicies(mockDepositPolicies())
            .paymentCard(mockPaymentCard())
            .reservationOverrideReasons(mockReservationOverrideReasons())
            .reservationOverridden(true)
            .roomStay(RoomStayByIdResponse.builder().ratePlanCode("EMPLOYEE")
                    .arrivalDate(LocalDate.now().toString())
                    .sourceCode("44")
                    .departureDate(LocalDate.now().plusDays(2).toString()).build())
            .reservationStatus("Reserved")
            .guaranteeCode("NON")
            .balanceAmount(BigDecimal.valueOf(60))
            .build();
  }

  public static ReservationByIdResponse mockReservationByIdWithCityTax() {
    return ReservationByIdResponse.builder()
            .reservationGuestList(mockReservationGuestList())
            .reservationBooker(mockReservationBooker())
            .roomStay(mockRoomStay(RATE_PLAN_CODE_FLEX))
            .depositPolicies(mockDepositPolicies())
            .paymentCard(mockPaymentCard())
            .reservationOverrideReasons(mockReservationOverrideReasons())
            .reservationPackageList(List.of(ReservationPackagesDetailsResponse.builder()
                    .packageCode("CITYTAX")
                    .unitPrice(BigDecimal.valueOf(123))
                    .startDate("2023-10-20")
                    .endDate("2023-10-20")
                    .build()))
            .reservationOverridden(true)
            .reservationStatus("Reserved")
            .guaranteeCode("NON")
            .balanceAmount(BigDecimal.valueOf(60))
            .build();
  }

  public static BasketResponse mockBasketResponseSource() {
    return mockBasketResponseSource(null);
  }

  public static BasketResponse mockBasketResponseSource(String channel) {
    return BasketResponse.builder()
        .hotelId(HOTEL_ID)
        .bookingReference(BOOKING_REFERENCE)
        .reference(BASKET_REFERENCE)
        .channel(channel)
        .createdAt(new Date().toString())
        .status("OPEN")
        .itemTypes(Collections.emptySet())
        .items(mockBasketItemsSourceList())
        .paymentID("118932")
        .paymentOption(PaymentOption.PAY_ON_ARRIVAL)
        .originalBasketId("HOTELCODE1001001")
        .eTag("123")
        .build();
  }

  public static List<BasketItemResponse> mockBasketItemsSourceList() {
    List<BasketItemResponse> itemResponseList = new LinkedList<>();
    itemResponseList.add(BasketItemResponse.builder().type("reservation").sourceId("44").build());
    itemResponseList.add(BasketItemResponse.builder().type("reservation").sourceId("45").build());
    itemResponseList.add(BasketItemResponse.builder().type("reservation").sourceId("46").build());
    return itemResponseList;
  }

  public static ReservationsDetailsEnhancedResponse mockReservationNotFoundResponse() {
    return ReservationsDetailsEnhancedResponse.builder()
        .billing(BillingResponse.builder().lastName("John").build())
        .totalCost(BigDecimal.valueOf(123))
        .amountPaid(BigDecimal.valueOf(23))
        .reservations(Reservations.builder()
            .reservationInfo(List.of(ReservationInfo.builder()
                .externalReferences(
                    List.of(
                        ExternalReferenceType.builder().id("TEST123456-ABC").idContext("OKK")
                            .build()))
                .hotelId(HOTEL_ID)
                .roomStay(RoomStay.builder()
                    .arrivalDate(LocalDate.of(2022, 05, 05))
                    .sourceCode(SOURCE_CODE)
                    .build())
                .reservationStatus(BasketDto.StatusEnum.COMPLETED.name())
                .reservationIdList(
                    List.of(UniqueIDType.builder().id(UNIQUE_ID_TYPE).type("Reservation").build()))
                .build()))
            .build())
        .build();
  }

  public static ReservationByBasketRefResponse mockReservationByBasketRefResponseWithZeroAmountPaid() {
    var reservations = ManageReservationUtils.mockReservationByBasketRefResponse();
    reservations.getReservationByIdList()
        .forEach(
            reservation -> reservation.getRateInfo().getSummary().setDeposit(BigDecimal.ZERO));
    reservations.setAmountPaid(BigDecimal.ZERO);
    return reservations;
  }

  public static ReservationByBasketRefResponse mockReservationByBasketRefResponseWithPreStayAmountPaid() {
    var reservations = ManageReservationUtils.mockReservationByBasketRefResponse();
    reservations.getReservationByIdList()
        .forEach(reservation -> {
          reservation.setDepositPolicies(mockDepositPolicies());
          reservation.getRateInfo().getSummary().setDeposit(BigDecimal.ZERO);
        });
    reservations.setAmountPaid(BigDecimal.valueOf(3));
    return reservations;
  }

  public static DepositsResponse mockDepositsResponse() {
    var depositsResponse = new DepositsResponse();
    depositsResponse.setDeposits(List.of(Deposits.builder()
        .paymentReference("789D")
        .build()));
    return depositsResponse;
  }

  public static HashMap<String, DepositsResponse> mockRefundedDeposits(
      DepositsResponse depositsResponse) {
    var refundedDeposits = new HashMap<String, DepositsResponse>();
    refundedDeposits.put("123", depositsResponse);
    return refundedDeposits;
  }

  public static CancelReservationRequest mockCancelReservationRequest(BasketResponse basket) {
    return CancelReservationRequest.builder()
        .basketReference(basket.getReference())
        .token("token")
        .build();
  }

  public static ReservationByBasketRefResponse mockReservationByBasketRefResponse_OperaConfirmation(String guaranteeCode) {
    var rsv = mockReservationByIdFlex("11111111");
    rsv.setGuaranteeCode(guaranteeCode);
    if (A2C_GUARANTEE_OPERA_CODE.equals(guaranteeCode)) {
      rsv.setBookingAllowancesResponse(BookingAllowancesResponse.builder()
          .bookingAllowances(Collections.singletonList(BookingAllowance.builder()
              .allowance("dinner")
              .budget(BigDecimal.TEN)
              .build()))
          .build());
    }
    return ReservationByBasketRefResponse.builder()
        .amountPaid(BigDecimal.ZERO)
        .totalCost(BigDecimal.TEN)
        .hotelId(HOTEL_ID)
        .policyCode(POLICY_CODE)
        .currencyCode("GBP")
        .reservationByIdList(List.of(rsv))
        .build();
  }
  public static ReservationByBasketRefResponse mockReservationByBasketRefResponse_PrePaid_OperaConfirmation() {
    var rsv = mockReservationByIdFlex("11111111");
    rsv.setGuaranteeCode("DRV");
    rsv.setDepositFoliosResponse(mockDepositFoliosResponse());
    return ReservationByBasketRefResponse.builder()
        .totalCost(BigDecimal.valueOf(123))
        .amountPaid(BigDecimal.valueOf(23))
        .hotelId(HOTEL_ID)
        .policyCode(POLICY_CODE)
        .currencyCode("GBP")
        .reservationByIdList(List.of(rsv))
        .build();
  }

  public static BasketResponse mockBasketForOperaUiRsv(PaymentOption paymentOption) {
    return BasketResponse.builder()
            .hotelId(TEST_HOTEL_ID)
            .bookingReference(BOOKING_REFERENCE_OPERA_RSV)
            .reference(BASKET_REFERENCE)
            .createdAt(new Date().toString())
            .status("OPEN")
            .paymentOption(paymentOption)
            .itemTypes(Collections.emptySet())
            .items(ManageReservationUtils.mockBasketItemsListForCancel())
            .build();
  }

  public static BusinessAllowanceRuleResponse mockBusinessAllowanceRuleResponse() {
    return BusinessAllowanceRuleResponse.builder()
            .businessAllowances(List.of(BusinessAllowanceRule.builder()
                    .sourceId("premierInnBreakfast")
                    .sourceType("ALLOWANCE")
                    .targetId("BREAK")
                    .build()))
            .build();
  }

  public static ChannelRuleResponse mockChannelRuleResponse(String channel) {
    return ChannelRuleResponse.builder()
            .requestDetails(ChannelRuleRequestDetails.builder().channel(channel).build())
            .build();
  }

  public static CdhSearchBookingsResponse mockCdhSearchBookingsResponse() {
    CdhSearchBookingsResponse cdhSearchBookingsResponse = new CdhSearchBookingsResponse();
    CdhResults cdhResults = CdhResults.builder().build();
    Rooms rooms = Rooms.builder().reservationId("11111111").build();
    cdhResults.setRooms(Collections.singletonList(rooms));
    cdhResults.setHotelId("TESTHOTEL");
    cdhSearchBookingsResponse.setResults(Collections.singletonList(cdhResults));
    return cdhSearchBookingsResponse;
  }

  public static IndexHeaderData mockIndexHeader() {
    return IndexHeaderData.builder()
            .config(
                    Config.builder().bookingSearch(
                                    BookingSearch.builder().dashboardRedirect(
                                                    DashboardRedirect.builder()
                                                        .cookie(Cookie.builder().name("Test")
                                                            .minutesTillExpiry("30").build())
                                                        .operaUrl("testUrl").build())
                                            .build())
                            .build())
            .build();
  }

  public static FindBookingRequest mockFindBookingRequestByOperaConfirmation() {
    return FindBookingRequest.builder()
            .resNo(OPERA_CONFIRMATION)
            .lastName("John")
            .arrivalDate("2022-05-05")
            .country("gb")
            .language("en")
            .build();
  }

  public static RoomStayByIdResponse mockRoomStayWithCurrentDate(LocalDate arrivalDate) {
    return RoomStayByIdResponse.builder()
        .arrivalDate(arrivalDate.toString())
        .departureDate(LocalDate.now().plusDays(1).toString())
        .adultsNumber(2)
        .childrenNumber(0)
        .ratePlanCode(RATE_PLAN_CODE_FLEX)
        .roomType(ROOM_TYPE)
        .cellCode(CELL_CODE)
        .roomNumber(ROOM_NUMBER)
        .bookingChannel(BOOKING_CHANNEL)
        .sourceCode(SOURCE_CODE)
        .build();
  }

  public static ReservationByIdResponse mockReservationByIdFlexCurrentDates(String reservationId,
      LocalDate arrivalDate) {
    return ReservationByIdResponse.builder()
        .reservationId(reservationId)
        .reservationGuestList(mockReservationGuestList())
        .reservationPackageList(mockReservationPackagesList())
        .reservationBooker(mockReservationBooker())
        .roomStay(mockRoomStayWithCurrentDate(arrivalDate))
        .depositPolicies(mockDepositPolicies())
        .paymentCard(mockPaymentCard())
        .reservationOverrideReasons(mockReservationOverrideReasons())
        .reservationOverridden(true)
        .reservationStatus("Reserved")
        .guaranteeCode("NON")
        .balanceAmount(BigDecimal.valueOf(60))
        .rateInfo(mockRateInfoWithoutPayOnArrivalAmountLeft())
        .billing(BillingResponse.builder().lastName("John").email(EMAIL).build())
        .cashiering(ResCashieringType.builder()
            .taxType(ReservationTaxTypeInfo.builder().code("UK").build()).build())
        .build();
  }

  public static ReservationByBasketRefResponse mockReservationByBasketRefResponseCurrentDateRoom(
      LocalDate arrivalDate, String hotelId) {
    return ReservationByBasketRefResponse.builder()
        .amountPaid(BigDecimal.valueOf(30))
        .totalCost(BigDecimal.TEN)
        .newTotal(BigDecimal.TEN)
        .previousTotal(BigDecimal.TEN)
        .balanceOutstanding(BigDecimal.TEN)
        .hotelId(hotelId)
        .policyCode(POLICY_CODE)
        .currencyCode("GBP")
        .reservationByIdList(
            List.of(mockReservationByIdFlexCurrentDates("12345", arrivalDate)))
        .build();
  }

  public static ReservationByBasketRefResponse mockReservationByBasketRefResponseNoReservation(
      List<ReservationByIdResponse> reservationByIdList) {
    return ReservationByBasketRefResponse.builder()
        .amountPaid(BigDecimal.valueOf(30))
        .totalCost(BigDecimal.TEN)
        .newTotal(BigDecimal.TEN)
        .previousTotal(BigDecimal.TEN)
        .balanceOutstanding(BigDecimal.TEN)
        .hotelId(HOTEL_ID)
        .policyCode(POLICY_CODE)
        .currencyCode("GBP")
        .reservationByIdList(reservationByIdList)
        .build();
  }

  public static ReservationByBasketRefResponse mockReservationByBasketRefFiveRooms() {
    return ReservationByBasketRefResponse.builder()
        .amountPaid(BigDecimal.valueOf(30))
        .totalCost(BigDecimal.TEN)
        .newTotal(BigDecimal.TEN)
        .previousTotal(BigDecimal.TEN)
        .balanceOutstanding(BigDecimal.TEN)
        .hotelId(HOTEL_ID)
        .policyCode(POLICY_CODE)
        .currencyCode("GBP")
        .reservationByIdList(
            List.of(mockReservationByIdFlexCurrentDates("12345", LocalDate.now()),
                mockReservationByIdFlexCurrentDates("54321", LocalDate.now()),
                mockReservationByIdFlexCurrentDates("54321", LocalDate.now()),
                mockReservationByIdFlexCurrentDates("54321", LocalDate.now()),
                mockReservationByIdFlexCurrentDates("54321", LocalDate.now())))
        .build();
  }

  public static ReservationByBasketRefResponse mockReservationByBasketRefResponseWithBooker(ReservationBooker booker) {
    var firstRoom = mockReservationByIdFlexCurrentDates("12345", LocalDate.now());
    firstRoom.setReservationBooker(booker);

    return ReservationByBasketRefResponse.builder()
        .amountPaid(BigDecimal.valueOf(30))
        .totalCost(BigDecimal.TEN)
        .newTotal(BigDecimal.TEN)
        .previousTotal(BigDecimal.TEN)
        .balanceOutstanding(BigDecimal.TEN)
        .hotelId(HOTEL_ID)
        .policyCode(POLICY_CODE)
        .currencyCode("GBP")
        .reservationByIdList(
            List.of(firstRoom))
        .build();
  }

  public static ReservationByBasketRefResponse mockReservationByBasketRefResponseWithPiba(
      LocalDate arrivalDate, String hotelId, ReservationPaymentCardType card) {
    var reservation = mockReservationByIdFlexCurrentDates("12345", arrivalDate);
    reservation.setPaymentCard(card);
    return ReservationByBasketRefResponse.builder()
        .amountPaid(BigDecimal.valueOf(30))
        .totalCost(BigDecimal.TEN)
        .newTotal(BigDecimal.TEN)
        .previousTotal(BigDecimal.TEN)
        .balanceOutstanding(BigDecimal.TEN)
        .hotelId(hotelId)
        .policyCode(POLICY_CODE)
        .currencyCode("GBP")
        .reservationByIdList(
            List.of(reservation))
        .build();
  }

  public static ReservationsPackagesResponse mockCreateReservationsPackagesResponse(boolean withPackagesSelection,
      String packageGroup) {
    List<PackagesSelection> packagesSelection = withPackagesSelection ?
        List.of(
            PackagesSelection.builder()
                .id("MDBFST")
                .noOfSelections(1)
                .build(),
            PackagesSelection.builder()
                .id(StringUtils.isNotBlank(packageGroup) ? packageGroup : "MD2DIN")
                .noOfSelections(1)
                .build()) : null;

    return ReservationsPackagesResponse.builder()
        .roomsSelections(List.of(
            RoomsSelectionsByReservation.builder()
                .reservationId("3213123132")
                .packagesSelection(packagesSelection)
                .build()
        ))
        .build();
  }

  public static RoomStayByIdResponse mockRoomStayWithDepartureDate(LocalDate departureDate) {
    return RoomStayByIdResponse.builder()
        .arrivalDate(LocalDate.now().toString())
        .departureDate(departureDate.toString())
        .adultsNumber(2)
        .childrenNumber(0)
        .ratePlanCode(RATE_PLAN_CODE_FLEX)
        .roomType(ROOM_TYPE)
        .cellCode(CELL_CODE)
        .roomNumber(ROOM_NUMBER)
        .bookingChannel(BOOKING_CHANNEL)
        .sourceCode(SOURCE_CODE)
        .build();
  }

  public static ReservationByIdResponse mockReservationByIdFlexWithDepartureDate(String reservationId,
      LocalDate departureDate, String packageCode) {
    var packages = mockReservationPackagesListWithCode(packageCode);
    return ReservationByIdResponse.builder()
        .reservationId(reservationId)
        .reservationGuestList(mockReservationGuestList())
        .reservationPackageList(packages)
        .reservationBooker(mockReservationBooker())
        .roomStay(mockRoomStayWithDepartureDate(departureDate))
        .depositPolicies(mockDepositPolicies())
        .paymentCard(mockPaymentCard())
        .reservationOverrideReasons(mockReservationOverrideReasons())
        .reservationOverridden(true)
        .reservationStatus("Reserved")
        .guaranteeCode("NON")
        .balanceAmount(BigDecimal.valueOf(60))
        .rateInfo(mockRateInfoWithoutPayOnArrivalAmountLeft())
        .billing(BillingResponse.builder().lastName("John").email(EMAIL).build())
        .cashiering(ResCashieringType.builder()
            .taxType(ReservationTaxTypeInfo.builder().code("UK").build()).build())
        .build();
  }

  public static ReservationByBasketRefResponse mockReservationByBasketRefResponseWithDepartureDate(
      LocalDate departureDate, String status) {
    return ReservationByBasketRefResponse.builder()
        .amountPaid(BigDecimal.valueOf(30))
        .totalCost(BigDecimal.TEN)
        .basketStatus(status)
        .newTotal(BigDecimal.TEN)
        .previousTotal(BigDecimal.TEN)
        .balanceOutstanding(BigDecimal.TEN)
        .hotelId(HOTEL_ID)
        .policyCode(POLICY_CODE)
        .currencyCode("GBP")
        .reservationByIdList(
            List.of(mockReservationByIdFlexWithDepartureDate("12345", departureDate, "BFADBF")))
        .build();
  }

  public static BasketResponse mockBasketResponseWithStatus(String status) {
    var basket = mockBasketResponse("PI");
    basket.setStatus(status);
    return basket;
  }

  private static List<ReservationPackagesDetailsResponse> mockReservationPackagesListWithCode(String code) {
    return List.of(
        ReservationPackagesDetailsResponse.builder().packageCode(code).build()
    );
  }

  public static ReservationByBasketRefResponse mockReservationByBasketRefResponseWithDepartureDateAndPackageCode(
      LocalDate departureDate, String status, String packageCode) {
    return ReservationByBasketRefResponse.builder()
        .amountPaid(BigDecimal.valueOf(30))
        .totalCost(BigDecimal.TEN)
        .basketStatus(status)
        .newTotal(BigDecimal.TEN)
        .previousTotal(BigDecimal.TEN)
        .balanceOutstanding(BigDecimal.TEN)
        .hotelId(HOTEL_ID)
        .policyCode(POLICY_CODE)
        .currencyCode("GBP")
        .reservationByIdList(
            List.of(mockReservationByIdFlexWithDepartureDate("12345", departureDate, packageCode)))
        .build();
  }

  public static ReservationByBasketRefResponse mockReservationByBasketRefResponseThirdParty(
      LocalDate arrivalDate, String hotelId) {
    return ReservationByBasketRefResponse.builder()
        .amountPaid(BigDecimal.valueOf(30))
        .totalCost(BigDecimal.TEN)
        .newTotal(BigDecimal.TEN)
        .previousTotal(BigDecimal.TEN)
        .balanceOutstanding(BigDecimal.TEN)
        .hotelId(hotelId)
        .policyCode(POLICY_CODE)
        .currencyCode("GBP")
        .reservationByIdList(
            List.of(mockReservationByIdFlexCurrentDates("12345", arrivalDate)))
        .build();
  }

  private static AdditionalGuestInfoResponse mockAdditionalGuestInfoResponse() {
    return AdditionalGuestInfoResponse.builder()
            .purposeOfStay("Leisure")
            .build();
  }

  public static ReservationByIdResponse mockReservationForStayNullByIdFlex(String reservationId) {
    return ReservationByIdResponse.builder()
            .reservationId(reservationId)
            .reservationGuestList(mockReservationGuestList())
            .reservationPackageList(mockReservationPackagesList())
            .reservationBooker(mockReservationBooker())
            .roomStay(mockRoomStay(RATE_PLAN_CODE_FLEX))
            .depositPolicies(mockDepositPolicies())
            .paymentCard(mockPaymentCard())
            .reservationOverrideReasons(mockReservationOverrideReasons())
            .reservationOverridden(true)
            .reservationStatus("Reserved")
            .guaranteeCode("NON")
            .balanceAmount(BigDecimal.valueOf(60))
            .rateInfo(mockRateInfoWithoutPayOnArrivalAmountLeft())
            .billing(BillingResponse.builder().lastName("John").email(EMAIL).build())
            .cashiering(ResCashieringType.builder()
                    .taxType(ReservationTaxTypeInfo.builder().code("UK").build()).build())
            .userDefinedFields(UserDefinedFields.builder()
                    .characterUDFs(List.of(CharacterUDFs.builder()
                            .name(USER_ACCOUNT_ID_UDFC_35)
                            .value(CUSTOMER_ACCOUNT_ID)
                            .build()))
                    .build())
            .build();
  }

}
