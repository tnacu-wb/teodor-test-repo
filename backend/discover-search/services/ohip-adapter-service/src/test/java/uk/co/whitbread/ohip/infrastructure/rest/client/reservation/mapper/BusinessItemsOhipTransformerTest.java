package uk.co.whitbread.ohip.infrastructure.rest.client.reservation.mapper;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static uk.co.whitbread.hotel.ohip.adapter.generated.models.ResProfileTypeType.RESERVATIONCONTACT;
import static uk.co.whitbread.ohip.infrastructure.rest.client.ohip.config.OhipConstants.UDFC_PURCHASE_ORDER_NAME;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.AmountType;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.GuestCountsType;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.HotelReservationType;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.HotelReservationTypeReservationProfiles;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.RatesType;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.ResGuestType;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.ResGuestTypeProfileInfo;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.ReservationPackageType;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.ReservationProfileType;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.RoomRateType;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.RoomStayType;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.TotalType;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.UniqueIDType;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.fof.CreditCardInfo;
import uk.co.whitbread.hotel.rules.agent.generated.models.BusinessAllowanceRuleDto;
import uk.co.whitbread.ohip.domain.model.reservation.in.BusinessAllowance;
import uk.co.whitbread.ohip.domain.model.reservation.in.BusinessItems;
import uk.co.whitbread.ohip.domain.model.reservation.out.ReservationAmounts;

@ExtendWith(MockitoExtension.class)
class BusinessItemsOhipTransformerTest {

  private static final String CAR_PARKING_ID = "142";
  private static final String DINNER_ID = "FBNA";
  private static final String CONTINENTAL_BREAKFAST_ID = "BREAK";
  private static final String CONTINENTAL_BREAKFAST_PACKAGE_ID = "BREAK";
  private static final String MEALDEAL_PACKAGE_ID = "FOOD";
  private static final String ALCOHOL_ID = "FB";
  private static final String GUEST_ID = "100100";
  private static final String PROFILE_ID_DISTR = "200200";
  private static final String COMPANY_ID = "12274860";
  public static final String PAYMENT_METHOD_VISA = "VA";
  public static final String TEST_CARD_NUMBER = "1234 3214 2134 4312";
  public static final String CARD_HOLDER_NAME = "Card Holder";
  public static final String CURRENCY_CODE_GBP = "GBP";
  public static final String HOTELCODE = "HOTELCODE";
  public static final String COMMENT_TYPE_RESERVATION = "RESERVATION";
  public static final String BUSINESS_NOTES_COMMENT_TITLE = "BUSINESS NOTES";
  @InjectMocks
  BusinessItemsOhipTransformer businessItemsOhipTransformer;

  @Test
  void injectRoutingInstructions__Success() {
    //Arrange
    var businessItems = mockBusinessItems();
    var hotelReservationType = mockHotelReservationType();
    var businessAllowances = mockBusinessAllowances();

    //Act
    var routingInstructions =
        businessItemsOhipTransformer.injectRoutingInstructions(businessItems, hotelReservationType,
            false, businessAllowances, null, 2);

    //Assert
    assertEquals(4, routingInstructions.size());
    assertEquals(GUEST_ID, routingInstructions.get(0).getFolio().getPayeeInfo().getPayeeId().getId());
    assertEquals(CAR_PARKING_ID,
        routingInstructions.get(0).getFolio().getInstructions().get(0).getTransactionCodes().get(0)
            .getTransactionCode());
    assertEquals(null,
        routingInstructions.get(0).getFolio().getInstructions().get(0).getCreditLimit());
    assertEquals(ALCOHOL_ID,
        routingInstructions.get(1).getFolio().getInstructions().get(0).getBillingInstructions()
            .get(0).getBillingCode());
    assertEquals("2022-03-02",
        routingInstructions.get(1).getFolio().getInstructions().get(0).getDuration().getTimeSpan()
            .getEndDate().toString());
    assertEquals(CONTINENTAL_BREAKFAST_ID,
        routingInstructions.get(2).getFolio().getInstructions().get(0).getBillingInstructions().get(0)
            .getBillingCode());
    assertEquals(MEALDEAL_PACKAGE_ID,
        routingInstructions.get(3).getFolio().getInstructions().get(0).getBillingInstructions().get(0)
            .getBillingCode());
    assertEquals(2, routingInstructions.get(0).getFolio().getFolioWindowNo());
  }

  @Test
  void injectRoutingInstructions_ShouldUseProvidedFolioWindow() {
    //Arrange
    var businessItems = mockBusinessItems();
    var hotelReservationType = mockHotelReservationType();
    var businessAllowances = mockBusinessAllowances();

    //Act
    var routingInstructions =
        businessItemsOhipTransformer.injectRoutingInstructions(businessItems, hotelReservationType,
            true, businessAllowances, null, 1);

    //Assert
    assertEquals(1, routingInstructions.get(0).getFolio().getFolioWindowNo());
  }

  @Test
  void injectRoutingInstructions_Window3__Success() {
    //Arrange
    var hotelReservationType = mockHotelReservationType();
    var businessAllowances = mockBusinessAllowances();

    //Act
    var routingInstructions =
        businessItemsOhipTransformer.buildRoutingInstructions(hotelReservationType,
            businessAllowances, 3);

    //Assert
    assertEquals(8, routingInstructions.size());
    assertEquals(PROFILE_ID_DISTR, routingInstructions.get(0).getFolio().getPayeeInfo().getPayeeId().getId());
    assertEquals(CAR_PARKING_ID,
        routingInstructions.get(0).getFolio().getInstructions().get(0).getTransactionCodes().get(0)
            .getTransactionCode());
    assertEquals(null,
        routingInstructions.get(0).getFolio().getInstructions().get(0).getCreditLimit());
    assertEquals(DINNER_ID,
        routingInstructions.get(1).getFolio().getInstructions().get(0).getBillingInstructions().get(0)
            .getBillingCode());
    assertEquals("2022-03-03",
        routingInstructions.get(0).getFolio().getInstructions().get(0).getDuration().getTimeSpan()
            .getEndDate().toString());
    assertEquals(CONTINENTAL_BREAKFAST_ID,
        routingInstructions.get(3).getFolio().getInstructions().get(0).getBillingInstructions().get(0)
            .getBillingCode());
    assertEquals(MEALDEAL_PACKAGE_ID,
        routingInstructions.get(5).getFolio().getInstructions().get(0).getBillingInstructions().get(0)
            .getBillingCode());
  }

  @Test
  void injectRoutingInstructions_No_Allowances_Window3__Success() {
    //Arrange
    var hotelReservationType = mockHotelReservationType();

    //Act
    var routingInstructions =
        businessItemsOhipTransformer.injectRoutingInstructions(hotelReservationType,
            null, 3);

    //Assert
    assertEquals(1, routingInstructions.size());
  }

  @Test
  void buildPaymentMethodType__Success() {
    //Arrange
    var cardInfo = mockCardInfo();
    var reservationAmounts = mockReservationAmounts();

    //Act
    var paymentMethodTypes =
        businessItemsOhipTransformer.buildPaymentMethodType(PAYMENT_METHOD_VISA, cardInfo,
            reservationAmounts);

    //Assert
    assertTrue(paymentMethodTypes.size() > 0);
    assertEquals(PAYMENT_METHOD_VISA, paymentMethodTypes.get(0).getPaymentMethod());
    assertEquals(3, paymentMethodTypes.get(0).getFolioView());
    assertEquals(LocalDate.now().plusYears(2), paymentMethodTypes.get(0).getPaymentCard().getExpirationDate());
    assertEquals(TEST_CARD_NUMBER, paymentMethodTypes.get(0).getPaymentCard().getCardNumber());
    assertEquals(CARD_HOLDER_NAME, paymentMethodTypes.get(0).getPaymentCard().getCardHolderName());
    assertEquals(CURRENCY_CODE_GBP, paymentMethodTypes.get(0).getBalance().getCurrencyCode());
    assertEquals(BigDecimal.valueOf(63), paymentMethodTypes.get(0).getBalance().getAmount());
  }

  @Test
  void buildPaymentMethodType_NoCreditCard__Success() {
    //Arrange
    var cardInfo = mockCardInfoNoCC();
    var reservationAmounts = mockReservationAmounts();

    //Act
    var paymentMethodTypes =
        businessItemsOhipTransformer.buildPaymentMethodType(PAYMENT_METHOD_VISA, cardInfo,
            reservationAmounts);

    //Assert
    assertTrue(paymentMethodTypes.size() > 0);
    assertEquals(3, paymentMethodTypes.get(0).getFolioView());
    assertEquals(CURRENCY_CODE_GBP, paymentMethodTypes.get(0).getBalance().getCurrencyCode());
    assertEquals(BigDecimal.valueOf(63), paymentMethodTypes.get(0).getBalance().getAmount());
  }

  @Test
  void buildPaymentMethodType_NoCardInfo__Success() {
    //Arrange
    var reservationAmounts = mockReservationAmounts();

    //Act
    var paymentMethodTypes =
        businessItemsOhipTransformer.buildPaymentMethodType(PAYMENT_METHOD_VISA, null,
            reservationAmounts);

    //Assert
    assertTrue(paymentMethodTypes.size() > 0);
    assertEquals(3, paymentMethodTypes.get(0).getFolioView());
    assertEquals(CURRENCY_CODE_GBP, paymentMethodTypes.get(0).getBalance().getCurrencyCode());
    assertEquals(BigDecimal.valueOf(63), paymentMethodTypes.get(0).getBalance().getAmount());
  }

  @Test
  void injectComments__Success() {
    //Arrange
    var businessItems = mockBusinessItems();
    var hotelReservationType = mockHotelReservationType();

    //Act
    var comments = businessItemsOhipTransformer.injectComments(businessItems, hotelReservationType);

    //Assert
    assertTrue(comments.size() > 0);
    assertEquals(HOTELCODE, comments.get(0).getComment().getHotelId());
    assertEquals(COMMENT_TYPE_RESERVATION, comments.get(0).getComment().getType());
    assertEquals(BUSINESS_NOTES_COMMENT_TITLE, comments.get(0).getComment().getCommentTitle());
    assertTrue(comments.get(0).getComment().getInternal());
  }

  @Test
  void injectRoutingInstructionsDISTR__Success() {
    //Arrange
    var businessItems = mockBusinessItems();
    var hotelReservationType = mockHotelReservationType();
    var businessAllowances = mockBusinessAllowances();

    //Act
    var routingInstructions =
        businessItemsOhipTransformer.injectRoutingInstructions(businessItems, hotelReservationType,
            true, businessAllowances, null, 2);

    //Assert
    assertEquals(4, routingInstructions.size());
    assertEquals(PROFILE_ID_DISTR,
        routingInstructions.get(0).getFolio().getPayeeInfo().getPayeeId().getId());
    assertEquals(CAR_PARKING_ID,
        routingInstructions.get(0).getFolio().getInstructions().get(0).getTransactionCodes().get(0)
            .getTransactionCode());
    assertEquals(null,
        routingInstructions.get(0).getFolio().getInstructions().get(0).getCreditLimit());
    assertEquals(ALCOHOL_ID,
        routingInstructions.get(1).getFolio().getInstructions().get(0).getBillingInstructions()
            .get(0).getBillingCode());
    assertEquals("2022-03-02",
        routingInstructions.get(1).getFolio().getInstructions().get(0).getDuration().getTimeSpan()
            .getEndDate().toString());
    assertEquals(CONTINENTAL_BREAKFAST_ID,
        routingInstructions.get(2).getFolio().getInstructions().get(0).getBillingInstructions().get(0)
            .getBillingCode());
    assertEquals(MEALDEAL_PACKAGE_ID,
        routingInstructions.get(3).getFolio().getInstructions().get(0).getBillingInstructions().get(0)
            .getBillingCode());
    assertEquals(2, routingInstructions.get(0).getFolio().getFolioWindowNo());
  }

  @Test
  void injectRoutingInstructions_companyIdProfile_Success() {
    //Arrange
    var businessItems = mockBusinessItems();
    var hotelReservationType = mockHotelReservationType();
    var businessAllowances = mockBusinessAllowances();

    //Act
    var routingInstructions =
        businessItemsOhipTransformer.injectRoutingInstructions(businessItems, hotelReservationType,
            false, businessAllowances, COMPANY_ID, 2);

    //Assert
    assertEquals(GUEST_ID, routingInstructions.get(0).getFolio().getGuestInfo()
        .getProfileIdList().get(0).getId());
    assertEquals(COMPANY_ID, routingInstructions.get(0).getFolio().getPayeeInfo()
        .getPayeeId().getId());
  }

  @Test
  void injectRoutingInstructions_DISTR_Success() {
    //Arrange
    var hotelReservationType = mockHotelReservationType();

    //Act
    var routingInstructions =
            businessItemsOhipTransformer.injectRoutingInstructions(hotelReservationType, null, 2);

    //Assert
    assertEquals(GUEST_ID, routingInstructions.get(0).getFolio().getGuestInfo()
            .getProfileIdList().get(0).getId());
    assertEquals(PROFILE_ID_DISTR,
            routingInstructions.get(0).getFolio().getPayeeInfo().getPayeeId().getId());
  }

  @Test
  void injectUserDefinedFields__Success() {
    //Arrange
    var businessItems = mockBusinessItems();

    //Act
    var udf = businessItemsOhipTransformer.injectUserDefinedFields(businessItems);

    //Assert
    assertEquals(1, udf.getCharacterUDFs().size());
    assertEquals(UDFC_PURCHASE_ORDER_NAME, udf.getCharacterUDFs().get(0).getName());
    assertEquals("10101010", udf.getCharacterUDFs().get(0).getValue());
  }

  private BusinessItems mockBusinessItems() {
    return BusinessItems.builder().purchaseOrderNumber("10101010")
        .customReferenceNumber("11111111")
        .businessNotes("Business Notes")
        .businessAllowances(List.of(
            BusinessAllowance.builder()
                .allowance("carParking")
                .budget(BigDecimal.ZERO)
                .isAuthorised(Boolean.TRUE).build(),
            BusinessAllowance.builder()
                .allowance("dinner")
                .budget(BigDecimal.ZERO)
                .isAuthorised(Boolean.TRUE).build(),
            BusinessAllowance.builder()
                    .allowance("alcohol")
                    .budget(BigDecimal.ZERO)
                    .isAuthorised(Boolean.TRUE).build(),
            BusinessAllowance.builder()
                .allowance("continentalBreakfast")
                .budget(BigDecimal.ZERO)
                .isAuthorised(Boolean.TRUE).build(),
            BusinessAllowance.builder()
                .allowance("BFADCT")
                .budget(BigDecimal.ZERO)
                .isAuthorised(Boolean.TRUE).build(),
            BusinessAllowance.builder()
                .allowance("MDBFST")
                .budget(BigDecimal.ZERO)
                .isAuthorised(Boolean.TRUE).build(),
            BusinessAllowance.builder()
                .allowance("MD2DIN")
                .budget(BigDecimal.ZERO)
                .isAuthorised(Boolean.TRUE).build(),
            BusinessAllowance.builder()
                .allowance("MDBEVA")
                .budget(BigDecimal.ZERO)
                .isAuthorised(Boolean.TRUE).build())
        ).build();
  }

  private CreditCardInfo mockCardInfo() {
    return new CreditCardInfo().creditCard(paymentCardType());
  }

  private CreditCardInfo mockCardInfoNoCC() {
    return new CreditCardInfo();
  }

  private ReservationAmounts mockReservationAmounts(){
    var resvAmts = new ReservationAmounts();
    resvAmts.setCurrencyCode("GBP");
    resvAmts.setDeposit(BigDecimal.valueOf(63));
    return resvAmts;
  }

  private uk.co.whitbread.hotel.ohip.adapter.generated.models.fof.ResPaymentCardType paymentCardType() {
    return new uk.co.whitbread.hotel.ohip.adapter.generated.models.fof.ResPaymentCardType()
        .cardId(new uk.co.whitbread.hotel.ohip.adapter.generated.models.fof.UniqueIDType())
        .cardNumber(TEST_CARD_NUMBER)
        .cardType(uk.co.whitbread.hotel.ohip.adapter.generated.models.fof.CardTypeType.VA)
        .expirationDate(LocalDate.now().plusYears(2))
        .cardHolderName(CARD_HOLDER_NAME);
  }

  private HotelReservationType mockHotelReservationType() {
    UniqueIDType reservationIdItem = new UniqueIDType();
    reservationIdItem.setId("100100");
    reservationIdItem.setType("Reservation");

    UniqueIDType companyIdItem = new UniqueIDType();
    companyIdItem.setId("12274860");
    companyIdItem.setType("Reservation");

    var hotelReservationType = new HotelReservationType();
    hotelReservationType.setHotelId("HOTELCODE");
    hotelReservationType.setReservationIdList(List.of(reservationIdItem, companyIdItem));

    var mdpBfstPackage = new ReservationPackageType();
    mdpBfstPackage.setPackageCode("MDBFST");
    var mdpDinPackage = new ReservationPackageType();
    mdpBfstPackage.setPackageCode("MD2DIN");
    var mdpEvaPackage = new ReservationPackageType();
    mdpBfstPackage.setPackageCode("MDBEVA");

    hotelReservationType.setReservationPackages(Arrays.asList(mdpBfstPackage, mdpDinPackage, mdpEvaPackage));

    var resGuestType = new ResGuestType();
    var resGuestProfileType = new ResGuestTypeProfileInfo();
    resGuestProfileType.addProfileIdListItem(reservationIdItem);
    resGuestType.setProfileInfo(resGuestProfileType);

    hotelReservationType.addReservationGuestsItem(resGuestType);

    RoomStayType roomStayType = new RoomStayType();
    RoomRateType roomRatesTmp = new RoomRateType();
    GuestCountsType guestCounts = new GuestCountsType();
    guestCounts.setAdults(2);
    RatesType rates = new RatesType();
    TotalType base = new TotalType();
    base.amountBeforeTax(BigDecimal.valueOf(200));

    AmountType amountType = new AmountType();
    amountType.setBase(base);
    amountType.setStart(LocalDate.of(2022, 3, 1));
    amountType.setEnd(LocalDate.of(2022, 3, 3));
    rates.addRateItem(amountType);
    roomRatesTmp.setRoomType("DOUBLE");

    roomRatesTmp.setRatePlanCode("FLEXRATE");
    roomRatesTmp.setRates(rates);
    roomStayType.addRoomRatesItem(roomRatesTmp);
    roomStayType.setArrivalDate(LocalDate.of(2022, 3, 1));
    roomStayType.setDepartureDate(LocalDate.of(2022, 3, 3));
    roomStayType.setGuestCounts(guestCounts);

    hotelReservationType.setRoomStay(roomStayType);
    hotelReservationType.setHotelId("HOTELCODE");

    var reservationProfiles = new HotelReservationTypeReservationProfiles();
    var reservationProfileType = new ReservationProfileType();
    var profileId = new UniqueIDType();
    profileId.setId("200200");
    reservationIdItem.setType("Profile");
    reservationProfileType.setReservationProfileType(RESERVATIONCONTACT);

    var companyProfileType = new ReservationProfileType();
    var companyProfileId = new UniqueIDType();
    companyProfileId.setId("12274860");
    companyIdItem.setType("Profile");

    reservationProfileType.setProfileIdList(List.of(profileId));
    companyProfileType.setProfileIdList(List.of(companyProfileId));
    reservationProfiles.setReservationProfile(List.of(reservationProfileType, companyProfileType));
    hotelReservationType.setReservationProfiles(reservationProfiles);

    return hotelReservationType;
  }

  private List<BusinessAllowanceRuleDto> mockBusinessAllowances() {
    BusinessAllowanceRuleDto carParkingAllowanceRule = new BusinessAllowanceRuleDto();
    carParkingAllowanceRule.setPms("OP");
    carParkingAllowanceRule.setSourceId("carParking");
    carParkingAllowanceRule.setSourceType("ALLOWANCE");
    carParkingAllowanceRule.setTargetId(CAR_PARKING_ID);
    carParkingAllowanceRule.setIsTransactionCode(true);
    carParkingAllowanceRule.setIsApplicableDaily(true);

    BusinessAllowanceRuleDto dinnerAllowanceRule = new BusinessAllowanceRuleDto();
    dinnerAllowanceRule.setPms("OP");
    dinnerAllowanceRule.setSourceId("dinner");
    dinnerAllowanceRule.setSourceType("ALLOWANCE");
    dinnerAllowanceRule.setTargetId(DINNER_ID);
    dinnerAllowanceRule.setIsTransactionCode(false);
    dinnerAllowanceRule.setIsApplicableDaily(true);

    BusinessAllowanceRuleDto alcoholAllowanceRule = new BusinessAllowanceRuleDto();
    alcoholAllowanceRule.setPms("OP");
    alcoholAllowanceRule.setSourceId("alcohol");
    alcoholAllowanceRule.setSourceType("ALLOWANCE");
    alcoholAllowanceRule.setTargetId(ALCOHOL_ID);
    alcoholAllowanceRule.setIsTransactionCode(false);
    alcoholAllowanceRule.setIsApplicableDaily(true);

    BusinessAllowanceRuleDto continentalBreakfastAllowanceRule = new BusinessAllowanceRuleDto();
    continentalBreakfastAllowanceRule.setPms("OP");
    continentalBreakfastAllowanceRule.setSourceId("continentalBreakfast");
    continentalBreakfastAllowanceRule.setSourceType("ALLOWANCE");
    continentalBreakfastAllowanceRule.setTargetId(CONTINENTAL_BREAKFAST_ID);
    continentalBreakfastAllowanceRule.setIsTransactionCode(false);
    continentalBreakfastAllowanceRule.setIsApplicableDaily(true);

    BusinessAllowanceRuleDto continentalBreakfastPackageAllowanceRule = new BusinessAllowanceRuleDto();
    continentalBreakfastPackageAllowanceRule.setPms("OP");
    continentalBreakfastPackageAllowanceRule.setSourceId("BFADCT");
    continentalBreakfastPackageAllowanceRule.setSourceType("PACKAGE");
    continentalBreakfastPackageAllowanceRule.setTargetId(CONTINENTAL_BREAKFAST_PACKAGE_ID);
    continentalBreakfastPackageAllowanceRule.setIsTransactionCode(false);
    continentalBreakfastPackageAllowanceRule.setIsApplicableDaily(false);

    BusinessAllowanceRuleDto mealDealBfstPackageAllowanceRule = new BusinessAllowanceRuleDto();
    mealDealBfstPackageAllowanceRule.setPms("OP");
    mealDealBfstPackageAllowanceRule.setSourceId("MDBFST");
    mealDealBfstPackageAllowanceRule.setSourceType("PACKAGE");
    mealDealBfstPackageAllowanceRule.setTargetId(MEALDEAL_PACKAGE_ID);
    mealDealBfstPackageAllowanceRule.setIsTransactionCode(false);
    mealDealBfstPackageAllowanceRule.setIsApplicableDaily(false);

    BusinessAllowanceRuleDto mealDealDinPackageAllowanceRule = new BusinessAllowanceRuleDto();
    mealDealDinPackageAllowanceRule.setPms("OP");
    mealDealDinPackageAllowanceRule.setSourceId("MD2DIN");
    mealDealDinPackageAllowanceRule.setSourceType("PACKAGE");
    mealDealDinPackageAllowanceRule.setTargetId(MEALDEAL_PACKAGE_ID);
    mealDealDinPackageAllowanceRule.setIsTransactionCode(false);
    mealDealDinPackageAllowanceRule.setIsApplicableDaily(false);

    BusinessAllowanceRuleDto mealDealEvaPackageAllowanceRule = new BusinessAllowanceRuleDto();
    mealDealEvaPackageAllowanceRule.setPms("OP");
    mealDealEvaPackageAllowanceRule.setSourceId("MDBEVA");
    mealDealEvaPackageAllowanceRule.setSourceType("PACKAGE");
    mealDealEvaPackageAllowanceRule.setTargetId(MEALDEAL_PACKAGE_ID);
    mealDealEvaPackageAllowanceRule.setIsTransactionCode(false);
    mealDealEvaPackageAllowanceRule.setIsApplicableDaily(false);

    return List.of(carParkingAllowanceRule, dinnerAllowanceRule, alcoholAllowanceRule,
        continentalBreakfastAllowanceRule, continentalBreakfastPackageAllowanceRule, mealDealBfstPackageAllowanceRule,
        mealDealDinPackageAllowanceRule, mealDealEvaPackageAllowanceRule);
  }

}
