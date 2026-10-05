package uk.co.whitbread.ohip.infrastructure.rest.client.reservation.mapper;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.NullSource;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.context.SpringBootTest;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.AmountType;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.CardNumberTypeType;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.CardProcessingType;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.CardTypeType;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.ConfigPackagePrimaryDetailsType;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.ConfigPostingAttributesType;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.EmailInfoType;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.EmailType;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.GuestCountsType;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.HotelReservationType;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.HotelReservationsType;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.PMSResStatusType;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.PackageCodeHeaderType;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.PackageConsumptionType;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.ProfileType;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.ProfileTypeEmails;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.RateInfo;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.RateInfoDetail;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.RatesType;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.ResCashieringType;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.ResGuestType;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.ResGuestTypeProfileInfo;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.ResPaymentCardType;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.Reservation;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.ReservationPackageScheduleType;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.ReservationPackageType;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.ReservationPaymentMethodType;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.ReservationPoliciesType;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.RoomRateType;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.RoomStayType;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.TaxType;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.TaxesType;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.TotalType;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.UniqueIDType;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.ResGuestAdditionalInfoType;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.crm.Profile;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.crm.PrivacyInfoType;
import uk.co.whitbread.ohip.domain.model.reservation.out.AdditionalGuestInfo;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.enterprise.HotelDetails;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.enterprise.HotelInfoType;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.enterprise.HotelInfoTypeGeneralInformation;
import uk.co.whitbread.ohip.domain.model.reservation.out.LightweightReservationById;
import uk.co.whitbread.ohip.domain.model.reservation.out.ReservationByBasketRefResponse;
import uk.co.whitbread.ohip.domain.model.reservation.out.ReservationById;
import uk.co.whitbread.ohip.domain.model.reservation.out.ReservationLightweightResponse;
import uk.co.whitbread.ohip.infrastructure.rest.client.availability.model.in.PriceBreakdownDto;

@ExtendWith(MockitoExtension.class)
@SpringBootTest(classes = ReservationResponseOhipMapperImpl.class)
class ReservationResponseOhipMapperTest {

  @InjectMocks
  private ReservationResponseOhipMapperImpl reservationResponseOhipMapper;

  @Test
  void testToReservationLightweightResponseModel_ShouldMapCorrectly() {
    // Arrange
    String hotelId = "HOTEL123";
    LightweightReservationById lightweightReservation = new LightweightReservationById();
    lightweightReservation.setHotelId(hotelId);

    List<Reservation> reservations = List.of(mockReservationLight());

    // Act
    ReservationLightweightResponse response = reservationResponseOhipMapper
        .toReservationLightweightResponseModel(reservations, mockHotelDetails(), hotelId);

    // Assert
    assertEquals(1, response.getReservationByIdList().size());
    assertEquals(hotelId, response.getReservationByIdList().get(0).getHotelId());
    assertEquals("23456789", response.getReservationByIdList().get(0).getReservationId());
    assertEquals("10:30", response.getReservationByIdList().get(0).getCheckInTime());
    assertEquals("11:30", response.getReservationByIdList().get(0).getCheckOutTime());
    assertEquals("test@yopmail.com", response.getReservationByIdList().get(0).getEmail());
    assertEquals(1, response.getReservationByIdList().get(0).getReservationPackageList().size());
    assertEquals("PKG123", response.getReservationByIdList().get(0)
        .getReservationPackageList().get(0).getPackageCode());
    assertEquals("Package Description", response.getReservationByIdList().get(0)
        .getReservationPackageList().get(0).getDescription());
  }

  @Test
  void testToPaymentCardReservationModel_ShouldMapCorrectly() {
    // Arrange
    UniqueIDType uniqueIDType = new UniqueIDType();
    uniqueIDType.setId("id123");
    List<UniqueIDType> ids = List.of(uniqueIDType);
    ReservationPaymentMethodType paymentInformation = new ReservationPaymentMethodType();
    paymentInformation.setFolioView(1);
    paymentInformation.setPaymentMethod("BU");
    ResPaymentCardType paymentCard = new ResPaymentCardType();
    paymentCard.setCardType(CardTypeType.CU);
    paymentCard.setCardNumber("**** **** **** 1234");
    paymentCard.processing(CardProcessingType.MANUAL);
    paymentCard.setCardOrToken(CardNumberTypeType.TOKEN);
    var uniqueCardId = new UniqueIDType();
    uniqueCardId.setId("card123");
    paymentCard.setCardId(uniqueCardId);
    paymentInformation.setPaymentCard(paymentCard);

    Reservation reservationById = new Reservation();

    // Act
    var response = reservationResponseOhipMapper
        .toPaymentCardReservationModel(ids, paymentInformation, reservationById);

    // Assert
    assertEquals("id123", response.getIds().get(0).getId());
    assertEquals("card123", response.getPaymentCardType().getCardId().getId());
    assertEquals("BU", response.getPaymentCardType().getPaymentMethod());
    assertEquals("Cu", response.getPaymentCardType().getCardType());
  }

  private HotelDetails mockHotelDetails() {
    var hotelDetails = new HotelDetails();

    Calendar calendar = Calendar.getInstance();
    calendar.set(Calendar.YEAR, 2025);
    calendar.set(Calendar.MONTH, Calendar.OCTOBER);
    calendar.set(Calendar.DAY_OF_MONTH, 15);
    calendar.set(Calendar.HOUR_OF_DAY, 10);
    calendar.set(Calendar.MINUTE, 30);
    calendar.set(Calendar.SECOND, 0);
    var checkIn = calendar.getTime();
    var checkOut = new Date(checkIn.getTime() + 1000 * 60 * 60); // 1 h later
    HotelInfoTypeGeneralInformation hotelInfoTypeGeneralInformation = new HotelInfoTypeGeneralInformation();
    hotelInfoTypeGeneralInformation.setCheckInTime(checkIn);
    hotelInfoTypeGeneralInformation.setCheckOutTime(checkOut);

    var hotelInfoType = new HotelInfoType();
    hotelInfoType.setGeneralInformation(hotelInfoTypeGeneralInformation);
    hotelDetails.setHotelConfigInfo(hotelInfoType);

    return hotelDetails;
  }

  private Reservation mockReservationLight() {
    var reservationPackageType = getReservationPackageType();
    var hotelReservationType = new HotelReservationType();
    hotelReservationType.setReservationIdList(List.of(mockUniqueIDType()));
    hotelReservationType.setReservationPackages(List.of(reservationPackageType));
    hotelReservationType.setReservationGuests(List.of(createGuest()));
    var hotelReservationsType = new HotelReservationsType();
    hotelReservationsType.setReservation(List.of(hotelReservationType));

    Reservation reservation = new Reservation();
    reservation.setReservations(hotelReservationsType);

    return reservation;
  }

  private UniqueIDType mockUniqueIDType() {
    var uniqueIdType = new UniqueIDType();
    uniqueIdType.setType("Reservation");
    uniqueIdType.setId("23456789");
    return uniqueIdType;
  }

  private static ReservationPackageType getReservationPackageType() {
    var reservationPackageType = new ReservationPackageType();
    reservationPackageType.setPackageCode("PKG123");
    reservationPackageType.setConsumptionDetails(new PackageConsumptionType());
    reservationPackageType.setScheduleList(List.of(new ReservationPackageScheduleType()));
    PackageCodeHeaderType packageHeaderType = new PackageCodeHeaderType();
    var primaryDetails = new ConfigPackagePrimaryDetailsType();
    primaryDetails.setDescription("Package Description");
    packageHeaderType.setPrimaryDetails(primaryDetails);
    reservationPackageType.setPackageHeaderType(packageHeaderType);
    return reservationPackageType;
  }

  private ResGuestType createGuest() {
    EmailType emailType = new EmailType();
    emailType.setEmailAddress("test@yopmail.com");
    EmailInfoType emailInfo = new EmailInfoType();
    emailInfo.setEmail(emailType);
    ProfileTypeEmails emails = new ProfileTypeEmails();
    emails.setEmailInfo(List.of(emailInfo));
    ProfileType profileType = new ProfileType();
    profileType.setEmails(emails);
    ResGuestTypeProfileInfo profileInfo = new ResGuestTypeProfileInfo();
    profileInfo.setProfile(profileType);
    ResGuestType guest = new ResGuestType();
    guest.setProfileInfo(profileInfo);

    return guest;
  }

 @ParameterizedTest
 @NullSource
 @CsvSource({"1", "2"})
 void testToReservationByBasketRefResponseModel_ShouldSetRoomRateBreakdown(Integer folioView) {
    // Arrange
    List<Reservation> reservations = List.of(mockReservation(folioView));
    Map<String, RateInfo> rateInfos = mockRateInfos();
    Map<String, Profile> profiles = Map.of();
    HotelDetails hotelConfigs = mockHotelDetails();
    Map<String, PriceBreakdownDto> priceMap = Map.of("23456789", new PriceBreakdownDto());
    String purchaseOrderNumber = "PO12345";
    String customReferenceNumber = "CR12345";
    String channel = "DIGITAL";

    // Act
    ReservationByBasketRefResponse response = reservationResponseOhipMapper
        .toReservationByBasketRefResponseModel(
            reservations, rateInfos, profiles, hotelConfigs, null,
            null, priceMap, purchaseOrderNumber, customReferenceNumber, channel);

    // Assert
    assertNotNull(response);
    assertEquals(1, response.getReservationByIdList().size());
    ReservationById reservationById = response.getReservationByIdList().get(0);
    assertNotNull(reservationById.getRoomStay());
    assertEquals(2, reservationById.getRoomStay().getRatesPerNight().size());
    var firstNight = reservationById.getRoomStay().getRatesPerNight().get(0);
    assertEquals("2026-07-24", firstNight.getStartDate());
    assertEquals(77, firstNight.getCityTaxPerNight().intValue());
    assertEquals(15, firstNight.getCityTaxVat().intValue());
    assertEquals(12, firstNight.getCityTaxAmountBeforeTax().intValue());
    assertEquals(folioView, reservationById.getPaymentCard().getFolioView());
    var secondNight = reservationById.getRoomStay().getRatesPerNight().get(1);
    assertEquals("2026-07-25", secondNight.getStartDate());
    assertEquals(0, secondNight.getCityTaxPerNight().intValue());
    assertNull(secondNight.getCityTaxVat());
    assertNull(secondNight.getCityTaxAmountBeforeTax());
  }

  private Reservation mockReservation(Integer folioWindow) {
    var resLight = new Reservation();
    var resType = new HotelReservationsType();
    var reservation = new HotelReservationType();
    var roomStay = new RoomStayType();
    var roomRateType = mockRoomRateType(new BigDecimal("100.00"), LocalDate.of(2026, 7, 24));
    var roomRateType2 = mockRoomRateType(new BigDecimal("100.00"), LocalDate.of(2026, 7, 25));

    roomStay.setArrivalDate(LocalDate.of(2026, 7, 24));
    roomStay.setDepartureDate(LocalDate.of(2026, 7, 26));
    roomStay.setRoomRates(List.of(roomRateType, roomRateType2));
    var guestCountsType = new GuestCountsType();
    guestCountsType.setAdults(2);
    guestCountsType.setChildren(0);
    roomStay.setGuestCounts(guestCountsType);
    roomStay.setTotal(new TotalType());
    reservation.setRoomStay(roomStay);

    reservation.setReservationIdList(List.of(mockUniqueIDType()));
    reservation.setReservationGuests(List.of());
    reservation.setCashiering(new ResCashieringType());
    var resPolicy = new ReservationPoliciesType();
    resPolicy.setDepositPolicies(List.of());
    reservation.setReservationPolicies(resPolicy);
    ReservationPaymentMethodType reservationPaymentMethodType = new ReservationPaymentMethodType();
    reservationPaymentMethodType.setFolioView(folioWindow);
    reservation.setReservationPaymentMethods(List.of(reservationPaymentMethodType));
    reservation.setReservationStatus(PMSResStatusType.RESERVED);
    ReservationPackageType pack = mockReservationPackageType(LocalDate.of(2026, 7, 24),
        new BigDecimal("77.00"));

    reservation.setReservationPackages(List.of(pack));
    resType.setReservation(List.of(reservation));
    resLight.setReservations(resType);
    return resLight;
  }

  private ReservationPackageType mockReservationPackageType(LocalDate date, BigDecimal price) {
    var pack = new ReservationPackageType();
    pack.setPackageCode("CITYTAX");
    var packHeaderType = new PackageCodeHeaderType();
    packHeaderType.setPrimaryDetails(new ConfigPackagePrimaryDetailsType());
    pack.setPackageHeaderType(packHeaderType);
    var packScheduleType = new ReservationPackageScheduleType();
    packScheduleType.setConsumptionDate(date);
    packScheduleType.setComputedResvPrice(price);
    pack.setScheduleList(List.of(packScheduleType));
    pack.setConsumptionDetails(new PackageConsumptionType());
    return pack;
  }

  private RoomRateType mockRoomRateType(BigDecimal amount, LocalDate start) {
    var roomRateType =  new RoomRateType();
    roomRateType.setRatePlanCode("FLEXRATE");
    var ratesType = new RatesType();
    var amountType = new AmountType();
    var base = new TotalType();
    base.setAmountBeforeTax(amount);
    amountType.setBase(base);
    ratesType.setRate(List.of(amountType));
    roomRateType.setRates(ratesType);
    roomRateType.setStart(start);
    return roomRateType;
  }

  private Map<String, RateInfo> mockRateInfos() {
    var rateInfoWithoutPackages = mockRateInfo();
    rateInfoWithoutPackages.getDetail().setPackages(null);
    return Map.of("2026-07-24", mockRateInfo(),"2026-07-25", rateInfoWithoutPackages);
  }

  private RateInfo mockRateInfo() {
    var rateInfo = new RateInfo();
    RateInfoDetail rateInfoDetail = new RateInfoDetail();
    var revenue = new TotalType();
    revenue.setAmountBeforeTax(new BigDecimal("200.00"));

    TaxesType taxesType = new TaxesType();
    TaxType taxType = new TaxType();
    taxType.setAmount(new BigDecimal("20.00"));
    taxesType.setTax(List.of(taxType));
    revenue.setTaxes(taxesType);

    var pack1 = new TotalType();
    pack1.setAmountBeforeTax(new BigDecimal("12.00"));
    TaxesType taxesTypeCityTax = new TaxesType();
    TaxType taxTypeCityTax = new TaxType();
    taxTypeCityTax.setAmount(new BigDecimal("15.00"));
    taxesTypeCityTax.setTax(List.of(taxTypeCityTax));
    pack1.setTaxes(taxesTypeCityTax);
    pack1.setCode("CITYTAX");

    var pack2 = new TotalType();
    pack2.setAmountBeforeTax(new BigDecimal("150.00"));
    pack2.setCode("OTHER");

    rateInfoDetail.setRevenue(revenue);
    rateInfoDetail.setPackages(List.of(pack1, pack2));
    rateInfo.setDetail(rateInfoDetail);
    return rateInfo;
  }

  @Test
  void toAdditionalGuestInfoForModel_OptInEmailTrue_AcceptFutureMailingIsTrue() {
    var reservation = buildReservationForAdditionalGuestInfo();
    var bookerProfile = buildProfileWithOptInEmail(Boolean.TRUE);

    AdditionalGuestInfo result = reservationResponseOhipMapper
        .toAdditionalGuestInfoForModel(reservation, bookerProfile);

    assertEquals(Boolean.TRUE, result.getAcceptFutureMailing());
  }

  @Test
  void toAdditionalGuestInfoForModel_OptInEmailFalse_AcceptFutureMailingIsFalse() {
    var reservation = buildReservationForAdditionalGuestInfo();
    var bookerProfile = buildProfileWithOptInEmail(Boolean.FALSE);

    AdditionalGuestInfo result = reservationResponseOhipMapper
        .toAdditionalGuestInfoForModel(reservation, bookerProfile);

    assertEquals(Boolean.FALSE, result.getAcceptFutureMailing());
  }

  @Test
  void toAdditionalGuestInfoForModel_NullBookerProfile_AcceptFutureMailingIsNull() {
    var reservation = buildReservationForAdditionalGuestInfo();

    AdditionalGuestInfo result = reservationResponseOhipMapper
        .toAdditionalGuestInfoForModel(reservation, null);

    assertNull(result.getAcceptFutureMailing());
  }

  @Test
  void toAdditionalGuestInfoForModel_NullPrivacyInfo_AcceptFutureMailingIsNull() {
    var reservation = buildReservationForAdditionalGuestInfo();
    var bookerProfile = buildProfileWithOptInEmail(null);

    AdditionalGuestInfo result = reservationResponseOhipMapper
        .toAdditionalGuestInfoForModel(reservation, bookerProfile);

    assertNull(result.getAcceptFutureMailing());
  }

  @Test
  void toReservationPackagesForModel_NotAddingPriceForAddToRateTrue(){
    var reservationPackage = buildPackageWithPostingAttributes(
            Boolean.TRUE,Boolean.FALSE, new BigDecimal(99), "ADDPIB");

    var hotelReservationType = new HotelReservationType();
    hotelReservationType.setReservationPackages(List.of(reservationPackage));
    var hotelReservationsType = new HotelReservationsType();
    hotelReservationsType.setReservation(List.of(hotelReservationType));
    var reservation = new Reservation();
    reservation.setReservations(hotelReservationsType);

    var result = reservationResponseOhipMapper.toReservationPackagesForModel(reservation);

    var resultPackage = result.getFirst();

    assertEquals(BigDecimal.ZERO, resultPackage.getUnitPrice());
    assertEquals(BigDecimal.ZERO, resultPackage.getComputedPrice());
  }

  @Test
  void toReservationPackagesForModel_IncludesPriceForAddToRateFalseAndPrintSeparateLineTrue() {
    var reservationPackage = buildPackageWithPostingAttributes(
            Boolean.FALSE, Boolean.TRUE, new BigDecimal(99), "ADDPIB");

    var hotelReservationType = new HotelReservationType();
    hotelReservationType.setReservationPackages(List.of(reservationPackage));
    var hotelReservationsType = new HotelReservationsType();
    hotelReservationsType.setReservation(List.of(hotelReservationType));
    var reservation = new Reservation();
    reservation.setReservations(hotelReservationsType);

    var result = reservationResponseOhipMapper.toReservationPackagesForModel(reservation);

    var resultPackage = result.getFirst();

    assertEquals(new BigDecimal(99), resultPackage.getUnitPrice());
    assertEquals(new BigDecimal(99), resultPackage.getComputedPrice());
  }

  @Test
  void toReservationPackagesForModel_NotAddingPriceForAddToRateFalseAndPrintSeparateLineFalse() {
    var reservationPackage = buildPackageWithPostingAttributes(
            Boolean.FALSE, Boolean.FALSE, new BigDecimal(99), "INCPIB");

    var hotelReservationType = new HotelReservationType();
    hotelReservationType.setReservationPackages(List.of(reservationPackage));
    var hotelReservationsType = new HotelReservationsType();
    hotelReservationsType.setReservation(List.of(hotelReservationType));
    var reservation = new Reservation();
    reservation.setReservations(hotelReservationsType);

    var result = reservationResponseOhipMapper.toReservationPackagesForModel(reservation);

    var resultPackage = result.getFirst();

    assertEquals(BigDecimal.ZERO, resultPackage.getUnitPrice());
    assertEquals(BigDecimal.ZERO, resultPackage.getComputedPrice());
  }

  @Test
  void toBillingForModel_ShouldPickLatestEmailAndMobileByHighestNumericId() {
    var bookerProfile = buildBillingProfile(
        List.of(
            buildCrmEmailInfo("1", "old.email@whitbread.com"),
            buildCrmEmailInfo("10", "latest.email@whitbread.com")),
        List.of(
            buildCrmTelephoneInfo("2", "MOBILE", "07111111111"),
            buildCrmTelephoneInfo("9", "MOBILE", "07999999999"),
            buildCrmTelephoneInfo("3", "HOME", "02000000000")));

    var response = reservationResponseOhipMapper.toBillingForModel(bookerProfile, "Whitbread");

    assertEquals("latest.email@whitbread.com", response.getEmail());
    assertEquals("07999999999", response.getTelephone());
    assertEquals("02000000000", response.getLandline());
  }

  @Test
  void toBillingForModel_ShouldPreferNumericIdsOverNullOrNonNumericIds() {
    var bookerProfile = buildBillingProfile(
        List.of(
            buildCrmEmailInfo("abc", "nonnumeric@whitbread.com"),
            buildCrmEmailInfo("7", "numeric@whitbread.com")),
        List.of(
            buildCrmTelephoneInfo(null, "MOBILE", "07111111111"),
            buildCrmTelephoneInfo("5", "MOBILE", "07555555555")));

    var response = reservationResponseOhipMapper.toBillingForModel(bookerProfile, "Whitbread");

    assertEquals("numeric@whitbread.com", response.getEmail());
    assertEquals("07555555555", response.getTelephone());
  }

  @Test
  void toBillingForModel_ShouldReturnEmptyContactFieldsWhenEmailAndTelephoneAreMissing() {
    var bookerProfile = buildBillingProfile(null, null);

    var response = reservationResponseOhipMapper.toBillingForModel(bookerProfile, "Whitbread");

    assertEquals("", response.getEmail());
    assertEquals("", response.getTelephone());
    assertEquals("", response.getLandline());
  }

  @Test
  void toBillingForModel_ShouldFallbackToLowerIdEmailWhenHighestIdEmailHasBlankAddress() {
    var bookerProfile = buildBillingProfile(
        List.of(
            buildCrmEmailInfo("5", "older.email@whitbread.com"),
            buildCrmEmailInfo("10", null)), // highest id but null address → filtered out
        null);

    var response = reservationResponseOhipMapper.toBillingForModel(bookerProfile, "Whitbread");

    // New behavior: filters out blank/null emails first, then picks max id from valid ones
    assertEquals("older.email@whitbread.com", response.getEmail());
  }

  @Test
  void toBillingForModel_ShouldFallbackToLowerIdEmailWhenHighestIdEmailHasBlankString() {
    var bookerProfile = buildBillingProfile(
        List.of(
            buildCrmEmailInfo("3", "valid.email@whitbread.com"),
            buildCrmEmailInfo("8", "   ")), // highest id but blank → filtered out
        null);

    var response = reservationResponseOhipMapper.toBillingForModel(bookerProfile, "Whitbread");

    // New behavior: filters out blank addresses, falls back to valid email
    assertEquals("valid.email@whitbread.com", response.getEmail());
  }

  @Test
  void toBillingForModel_ShouldFallbackToLowerIdTelephoneWhenHighestIdTelephoneHasBlankNumber() {
    var bookerProfile = buildBillingProfile(
        null,
        List.of(
            buildCrmTelephoneInfo("4", "MOBILE", "07111111111"),
            buildCrmTelephoneInfo("9", "MOBILE", "   "))); // highest id but blank → filtered out

    var response = reservationResponseOhipMapper.toBillingForModel(bookerProfile, "Whitbread");

    // New behavior: filters out blank phone numbers, falls back to valid telephone
    assertEquals("07111111111", response.getTelephone());
  }

  private Reservation buildReservationForAdditionalGuestInfo() {
    var resGuestAdditionalInfoType = new ResGuestAdditionalInfoType();
    resGuestAdditionalInfoType.setPurposeOfStay("LEI");
    var hotelReservationType = new HotelReservationType();
    hotelReservationType.setAdditionalGuestInfo(resGuestAdditionalInfoType);
    var hotelReservationsType = new HotelReservationsType();
    hotelReservationsType.setReservation(List.of(hotelReservationType));
    var reservation = new Reservation();
    reservation.setReservations(hotelReservationsType);
    return reservation;
  }

  private Profile buildBillingProfile(
      List<uk.co.whitbread.hotel.ohip.adapter.generated.models.crm.EmailInfoType> emailInfo,
      List<uk.co.whitbread.hotel.ohip.adapter.generated.models.crm.TelephoneInfoType> telephoneInfo) {
    var profileDetails = new uk.co.whitbread.hotel.ohip.adapter.generated.models.crm.ProfileType();
    var customer = new uk.co.whitbread.hotel.ohip.adapter.generated.models.crm.CustomerType();
    var personName = new uk.co.whitbread.hotel.ohip.adapter.generated.models.crm.PersonNameType();
    personName.setNameTitle("Mr");
    personName.setGivenName("Test");
    personName.setSurname("User");
    customer.setPersonName(List.of(personName));
    profileDetails.setCustomer(customer);

    if (emailInfo != null) {
      var emails = new uk.co.whitbread.hotel.ohip.adapter.generated.models.crm.CompanyProfileTypeEmails();
      emails.setEmailInfo(emailInfo);
      profileDetails.setEmails(emails);
    }

    if (telephoneInfo != null) {
      var telephones =
          new uk.co.whitbread.hotel.ohip.adapter.generated.models.crm.CompanyProfileTypeTelephones();
      telephones.setTelephoneInfo(telephoneInfo);
      profileDetails.setTelephones(telephones);
    }

    var profile = new Profile();
    profile.setProfileDetails(profileDetails);
    return profile;
  }

  private uk.co.whitbread.hotel.ohip.adapter.generated.models.crm.EmailInfoType buildCrmEmailInfo(
      String id, String emailAddress) {
    var email = new uk.co.whitbread.hotel.ohip.adapter.generated.models.crm.EmailType();
    email.setEmailAddress(emailAddress);

    var emailInfo = new uk.co.whitbread.hotel.ohip.adapter.generated.models.crm.EmailInfoType();
    emailInfo.setId(id);
    emailInfo.setEmail(email);
    return emailInfo;
  }

  private uk.co.whitbread.hotel.ohip.adapter.generated.models.crm.TelephoneInfoType buildCrmTelephoneInfo(
      String id, String type, String phoneNumber) {
    var telephone = new uk.co.whitbread.hotel.ohip.adapter.generated.models.crm.TelephoneType();
    telephone.setPhoneNumber(phoneNumber);

    var telephoneInfo = new uk.co.whitbread.hotel.ohip.adapter.generated.models.crm.TelephoneInfoType();
    telephoneInfo.setId(id);
    telephoneInfo.setType(type);
    telephoneInfo.setTelephone(telephone);
    return telephoneInfo;
  }

  private Profile buildProfileWithOptInEmail(Boolean optInEmail) {
    var privacyInfo = new PrivacyInfoType();
    privacyInfo.setOptInEmail(optInEmail);
    var profileType = new uk.co.whitbread.hotel.ohip.adapter.generated.models.crm.ProfileType();
    profileType.setPrivacyInfo(privacyInfo);
    var profile = new Profile();
    profile.setProfileDetails(profileType);
    return profile;
  }

  private ReservationPackageType buildPackageWithPostingAttributes(
          Boolean addToRate, Boolean printSeparateLine, BigDecimal price, String packageCode){
      var postingAttributes = new ConfigPostingAttributesType();
      postingAttributes.setAddToRate(addToRate);
      postingAttributes.setPrintSeparateLine(printSeparateLine);

      var packageHeaderType = new PackageCodeHeaderType();
      packageHeaderType.setPrimaryDetails(new ConfigPackagePrimaryDetailsType());
      packageHeaderType.setPostingAttributes(postingAttributes);

      var scheduleType = new ReservationPackageScheduleType();
      scheduleType.setUnitPrice(price);
      scheduleType.setComputedResvPrice(price);

      var reservationPackage = new ReservationPackageType();
      reservationPackage.setPackageCode(packageCode);
      reservationPackage.setPackageHeaderType(packageHeaderType);
      reservationPackage.setScheduleList(List.of(scheduleType));
      reservationPackage.setConsumptionDetails(new PackageConsumptionType());

      return reservationPackage;
  }
}