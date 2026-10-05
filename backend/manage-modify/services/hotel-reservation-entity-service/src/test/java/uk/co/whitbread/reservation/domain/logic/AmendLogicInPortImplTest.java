package uk.co.whitbread.reservation.domain.logic;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.read.ListAppender;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.Month;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;

import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.slf4j.LoggerFactory;
import uk.co.whitbread.reservation.domain.exceptions.BasketNotFoundException;
import uk.co.whitbread.reservation.domain.logic.utils.TokenUtils;
import uk.co.whitbread.reservation.domain.model.amend.in.AmendConfirmationPricesRequest;
import uk.co.whitbread.reservation.domain.model.amend.in.AmendPaymentPageRequest;
import uk.co.whitbread.reservation.domain.model.amend.in.ConfirmAmendLogicRequest;
import uk.co.whitbread.reservation.domain.model.amend.in.PaymentOption;
import uk.co.whitbread.reservation.domain.model.feature.FeatureFlag;
import uk.co.whitbread.reservation.domain.model.feature.FeatureFlag.Feature;
import uk.co.whitbread.reservation.domain.model.feature.UnleashWrapper;
import uk.co.whitbread.reservation.domain.model.in.AmendSummaryAmountRequest;
import uk.co.whitbread.reservation.domain.model.in.AmendSummaryRequest;
import uk.co.whitbread.reservation.domain.model.in.BookingChannel;
import uk.co.whitbread.reservation.domain.model.in.CustomerType;
import uk.co.whitbread.reservation.domain.model.in.EmailInfoType;
import uk.co.whitbread.reservation.domain.model.in.EmailType;
import uk.co.whitbread.reservation.domain.model.in.PersonNameType;
import uk.co.whitbread.reservation.domain.model.in.ProfileInfo;
import uk.co.whitbread.reservation.domain.model.in.ProfileType;
import uk.co.whitbread.reservation.domain.model.in.ProfileTypeEmails;
import uk.co.whitbread.reservation.domain.model.in.ReservationGuests;
import uk.co.whitbread.reservation.domain.model.in.UpdateReservationRequest;
import uk.co.whitbread.reservation.domain.model.index.header.data.out.AcceptedCreditCard;
import uk.co.whitbread.reservation.domain.model.index.header.data.out.HotelPaymentInformation;
import uk.co.whitbread.reservation.domain.model.out.AccountCompanyItems;
import uk.co.whitbread.reservation.domain.model.out.AddressResponse;
import uk.co.whitbread.reservation.domain.model.out.AmendSummaryAmountResponse;
import uk.co.whitbread.reservation.domain.model.out.BasketItemResponse;
import uk.co.whitbread.reservation.domain.model.out.BasketResponse;
import uk.co.whitbread.reservation.domain.model.out.BillingResponse;
import uk.co.whitbread.reservation.domain.model.out.BookingAllowance;
import uk.co.whitbread.reservation.domain.model.out.CcuiExtraItems;
import uk.co.whitbread.reservation.domain.model.out.DonationPackage;
import uk.co.whitbread.reservation.domain.model.out.DonationPackagesResponse;
import uk.co.whitbread.reservation.domain.model.out.GuestAddress;
import uk.co.whitbread.reservation.domain.model.out.HotelInfoResponse;
import uk.co.whitbread.reservation.domain.model.out.PackagesSelection;
import uk.co.whitbread.reservation.domain.model.out.ReservationBooker;
import uk.co.whitbread.reservation.domain.model.out.ReservationBookerAddress;
import uk.co.whitbread.reservation.domain.model.out.ReservationByBasketRefResponse;
import uk.co.whitbread.reservation.domain.model.out.ReservationByIdGuestsResponse;
import uk.co.whitbread.reservation.domain.model.out.ReservationByIdResponse;
import uk.co.whitbread.reservation.domain.model.out.ReservationCompany;
import uk.co.whitbread.reservation.domain.model.out.ReservationEmailNotificationsResponse;
import uk.co.whitbread.reservation.domain.model.out.ReservationPaymentCardType;
import uk.co.whitbread.reservation.domain.model.out.ReservationsPackagesResponse;
import uk.co.whitbread.reservation.domain.model.out.RoomStayByIdResponse;
import uk.co.whitbread.reservation.domain.model.out.RoomsSelectionsByReservation;
import uk.co.whitbread.reservation.domain.model.payment.out.AvailablePaymentType;
import uk.co.whitbread.reservation.domain.model.payment.out.InitiatePaymentResponse;
import uk.co.whitbread.reservation.domain.model.payment.out.PaymentStatus;
import uk.co.whitbread.reservation.domain.model.payment.out.PaymentType;
import uk.co.whitbread.reservation.domain.ports.secondary.BasketOutPort;
import uk.co.whitbread.reservation.domain.ports.secondary.ContentOutPort;
import uk.co.whitbread.reservation.domain.ports.secondary.HotelReservationOhipOutPort;
import uk.co.whitbread.shared.auth.security.AuthenticatedUserService;

@ExtendWith(MockitoExtension.class)
class AmendLogicInPortImplTest {

  private AmendLogicInPortImpl amendLogicInPort;
  private ListAppender<ILoggingEvent> listAppender;

  private static final String REFERENCE_ARRIVAL_DATE = "2024-02-10";
  private static final String REFERENCE_DEPARTURE_DATE = "2024-02-16";
  private static final String JOHN_DOE_EMAIL = "john.doe@example.com";
  private static final String MARY = "Mary";
  private static final String JANE = "Jane";
  private static final String JOHN = "John";
  private static final String DOE = "Doe";
  private static final String MS = "Ms";
  private static final String CARD_NUMBER = "XXXXXXXXXXXX1103";
  private static final String CARD_TOKEN = "4216333880397891103";
  private static final String PI_BOOKING_CHANNEL = "PI";
  private static final String CCUI_BOOKING_CHANNEL = "CCUI";
  private static final String TEST_FUTURE_ETAG = "4102444800000";
  private static final LocalDate EXPIRED_CARD_DATE = LocalDate.of(2024, Month.MARCH, 31);
  private static final LocalDate FUTURE_CARD_EXPIRY_DATE = LocalDate.of(2099, Month.DECEMBER, 31);

  @Mock
  private BasketOutPort basketOutPort;

  @Mock
  private HotelReservationOhipOutPort hotelReservationOhipOutPort;

  @Mock
  private AuthenticatedUserService authenticatedUserService;

  @Mock
  private ContentOutPort contentOutPort;

  @Mock
  private ReservationCleanup reservationCleanUp;

  @Mock
  private UnleashWrapper<FeatureFlag> unleashWrapper;

  @BeforeEach
  public void init() {
    amendLogicInPort = new AmendLogicInPortImpl(basketOutPort, hotelReservationOhipOutPort,
        authenticatedUserService, contentOutPort, reservationCleanUp, unleashWrapper);
    final var log = (Logger) LoggerFactory.getLogger(AmendLogicInPortImpl.class);
    listAppender = new ListAppender<>();
    listAppender.start();
    log.addAppender(listAppender);
  }

  @Test
  void checkCreateOnHoldReservation_Case1_ShouldReturnTrue() {
    // Arrange
    var newArrivalDate = "2024-02-10";
    var newDepartureDate = "2024-02-18";

    // Act
    var checkResult = amendLogicInPort.checkCreateOnHoldReservation(REFERENCE_ARRIVAL_DATE,
        REFERENCE_DEPARTURE_DATE, newArrivalDate, newDepartureDate);

    // Assert
    assertThat(checkResult, is(true));
  }

  @Test
  void checkCreateOnHoldReservation_Case2_ShouldReturnTrue() {
    // Arrange
    var newArrivalDate = "2024-02-08";
    var newDepartureDate = "2024-02-16";

    // Act
    var checkResult = amendLogicInPort.checkCreateOnHoldReservation(REFERENCE_ARRIVAL_DATE,
        REFERENCE_DEPARTURE_DATE, newArrivalDate, newDepartureDate);

    // Assert
    assertThat(checkResult, is(true));
  }

  @Test
  void checkCreateOnHoldReservation_Case3_ShouldReturnFalse() {
    // Arrange
    var newArrivalDate = "2024-02-11";
    var newDepartureDate = "2024-02-14";

    // Act
    var checkResult = amendLogicInPort.checkCreateOnHoldReservation(REFERENCE_ARRIVAL_DATE,
        REFERENCE_DEPARTURE_DATE, newArrivalDate, newDepartureDate);

    // Assert
    assertThat(checkResult, is(false));
  }

  @Test
  void checkCreateOnHoldReservation_Case4_ShouldReturnTrue() {
    // Arrange
    var newArrivalDate = "2024-02-08";
    var newDepartureDate = "2024-02-18";

    // Act
    var checkResult = amendLogicInPort.checkCreateOnHoldReservation(REFERENCE_ARRIVAL_DATE,
        REFERENCE_DEPARTURE_DATE, newArrivalDate, newDepartureDate);

    // Assert
    assertThat(checkResult, is(true));
  }

  @Test
  void checkCreateOnHoldReservation_Case5_ShouldReturnTrue() {
    // Arrange
    var newArrivalDate = "2024-02-18";
    var newDepartureDate = "2024-02-20";

    // Act
    var checkResult = amendLogicInPort.checkCreateOnHoldReservation(REFERENCE_ARRIVAL_DATE,
        REFERENCE_DEPARTURE_DATE, newArrivalDate, newDepartureDate);

    // Assert
    assertThat(checkResult, is(true));
  }

  @Test
  void checkCreateOnHoldReservation_Case6_ShouldReturnTrue() {
    // Arrange
    var newArrivalDate = "2024-02-04";
    var newDepartureDate = "2024-02-06";

    // Act
    var checkResult = amendLogicInPort.checkCreateOnHoldReservation(REFERENCE_ARRIVAL_DATE,
        REFERENCE_DEPARTURE_DATE, newArrivalDate, newDepartureDate);

    // Assert
    assertThat(checkResult, is(true));
  }

  @Test
  void checkCreateOnHoldReservation_Case7_ShouldReturnTrue() {
    // Arrange
    var newArrivalDate = "2024-02-08";
    var newDepartureDate = "2024-02-11";

    // Act
    var checkResult = amendLogicInPort.checkCreateOnHoldReservation(REFERENCE_ARRIVAL_DATE,
        REFERENCE_DEPARTURE_DATE, newArrivalDate, newDepartureDate);

    // Assert
    assertThat(checkResult, is(true));
  }

  @Test
  void checkCreateOnHoldReservation_Case8_ShouldReturnTrue() {
    // Arrange
    var newArrivalDate = "2024-02-13";
    var newDepartureDate = "2024-02-18";

    // Act
    var checkResult = amendLogicInPort.checkCreateOnHoldReservation(REFERENCE_ARRIVAL_DATE,
        REFERENCE_DEPARTURE_DATE, newArrivalDate, newDepartureDate);

    // Assert
    assertThat(checkResult, is(true));
  }

  @Test
  void getAmendOnHoldReservationInterval_Case1_ShouldReturnOk() {
    // Arrange
    var newArrivalDate = "2024-02-10";
    var newDepartureDate = "2024-02-18";

    // Act
    var amendOnHoldInterval = amendLogicInPort.getAmendOnHoldReservationInterval(
        REFERENCE_ARRIVAL_DATE,
        REFERENCE_DEPARTURE_DATE, newArrivalDate, newDepartureDate);

    // Assert
    assertNotNull(amendOnHoldInterval);
    assertThat(amendOnHoldInterval, hasSize(1));
    assertNotNull(amendOnHoldInterval.get(0));
    assertThat(amendOnHoldInterval.get(0).getAmendArrivalDate(), equalTo(REFERENCE_DEPARTURE_DATE));
    assertThat(amendOnHoldInterval.get(0).getAmendDepartureDate(), equalTo(newDepartureDate));
  }

  @Test
  void getAmendOnHoldReservationInterval_Case2_ShouldReturnOk() {
    // Arrange
    var newArrivalDate = "2024-02-04";
    var newDepartureDate = "2024-02-16";

    // Act
    var amendOnHoldInterval = amendLogicInPort.getAmendOnHoldReservationInterval(
        REFERENCE_ARRIVAL_DATE,
        REFERENCE_DEPARTURE_DATE, newArrivalDate, newDepartureDate);

    // Assert
    assertNotNull(amendOnHoldInterval);
    assertThat(amendOnHoldInterval, hasSize(1));
    assertNotNull(amendOnHoldInterval.get(0));
    assertThat(amendOnHoldInterval.get(0).getAmendArrivalDate(), equalTo(newArrivalDate));
    assertThat(amendOnHoldInterval.get(0).getAmendDepartureDate(), equalTo(REFERENCE_ARRIVAL_DATE));
  }

  @Test
  void getAmendOnHoldReservationInterval_Case3_ShouldReturnOk() {
    // Arrange
    var newArrivalDate = "2024-02-11";
    var newDepartureDate = "2024-02-13";

    // Act
    var amendOnHoldInterval = amendLogicInPort.getAmendOnHoldReservationInterval(
        REFERENCE_ARRIVAL_DATE,
        REFERENCE_DEPARTURE_DATE, newArrivalDate, newDepartureDate);

    // Assert
    assertNotNull(amendOnHoldInterval);
    assertThat(amendOnHoldInterval, hasSize(0));
  }

  @Test
  void getAmendOnHoldReservationInterval_Case4_ShouldReturnOk() {
    // Arrange
    var newArrivalDate = "2024-02-04";
    var newDepartureDate = "2024-02-18";

    // Act
    var amendOnHoldInterval = amendLogicInPort.getAmendOnHoldReservationInterval(
        REFERENCE_ARRIVAL_DATE,
        REFERENCE_DEPARTURE_DATE, newArrivalDate, newDepartureDate);

    // Assert
    assertNotNull(amendOnHoldInterval);
    assertThat(amendOnHoldInterval, hasSize(2));
    assertNotNull(amendOnHoldInterval.get(0));
    assertThat(amendOnHoldInterval.get(0).getAmendArrivalDate(), equalTo(newArrivalDate));
    assertThat(amendOnHoldInterval.get(0).getAmendDepartureDate(), equalTo(REFERENCE_ARRIVAL_DATE));
    assertNotNull(amendOnHoldInterval.get(1));
    assertThat(amendOnHoldInterval.get(1).getAmendArrivalDate(), equalTo(REFERENCE_DEPARTURE_DATE));
    assertThat(amendOnHoldInterval.get(1).getAmendDepartureDate(), equalTo(newDepartureDate));
  }

  @Test
  void getAmendOnHoldReservationInterval_Case5_ShouldReturnOk() {
    // Arrange
    var newArrivalDate = "2024-02-18";
    var newDepartureDate = "2024-02-20";

    // Act
    var amendOnHoldInterval = amendLogicInPort.getAmendOnHoldReservationInterval(
        REFERENCE_ARRIVAL_DATE,
        REFERENCE_DEPARTURE_DATE, newArrivalDate, newDepartureDate);

    // Assert
    assertNotNull(amendOnHoldInterval);
    assertThat(amendOnHoldInterval, hasSize(1));
    assertNotNull(amendOnHoldInterval.get(0));
    assertThat(amendOnHoldInterval.get(0).getAmendArrivalDate(), equalTo(newArrivalDate));
    assertThat(amendOnHoldInterval.get(0).getAmendDepartureDate(), equalTo(newDepartureDate));
  }

  @Test
  void getAmendOnHoldReservationInterval_Case6_ShouldReturnOk() {
    // Arrange
    var newArrivalDate = "2024-02-04";
    var newDepartureDate = "2024-02-06";

    // Act
    var amendOnHoldInterval = amendLogicInPort.getAmendOnHoldReservationInterval(
        REFERENCE_ARRIVAL_DATE,
        REFERENCE_DEPARTURE_DATE, newArrivalDate, newDepartureDate);

    // Assert
    assertNotNull(amendOnHoldInterval);
    assertThat(amendOnHoldInterval, hasSize(1));
    assertNotNull(amendOnHoldInterval.get(0));
    assertThat(amendOnHoldInterval.get(0).getAmendArrivalDate(), equalTo(newArrivalDate));
    assertThat(amendOnHoldInterval.get(0).getAmendDepartureDate(), equalTo(newDepartureDate));
  }

  @Test
  void getAmendOnHoldReservationInterval_Case7_ShouldReturnOk() {
    // Arrange
    var newArrivalDate = "2024-02-08";
    var newDepartureDate = "2024-02-11";

    // Act
    var amendOnHoldInterval = amendLogicInPort.getAmendOnHoldReservationInterval(
        REFERENCE_ARRIVAL_DATE,
        REFERENCE_DEPARTURE_DATE, newArrivalDate, newDepartureDate);

    // Assert
    assertNotNull(amendOnHoldInterval);
    assertThat(amendOnHoldInterval, hasSize(1));
    assertNotNull(amendOnHoldInterval.get(0));
    assertThat(amendOnHoldInterval.get(0).getAmendArrivalDate(), equalTo(newArrivalDate));
    assertThat(amendOnHoldInterval.get(0).getAmendDepartureDate(), equalTo(REFERENCE_ARRIVAL_DATE));
  }

  @Test
  void getAmendOnHoldReservationInterval_Case8_ShouldReturnOk() {
    // Arrange
    var newArrivalDate = "2024-02-14";
    var newDepartureDate = "2024-02-18";

    // Act
    var amendOnHoldInterval = amendLogicInPort.getAmendOnHoldReservationInterval(
        REFERENCE_ARRIVAL_DATE,
        REFERENCE_DEPARTURE_DATE, newArrivalDate, newDepartureDate);

    // Assert
    assertNotNull(amendOnHoldInterval);
    assertThat(amendOnHoldInterval, hasSize(1));
    assertNotNull(amendOnHoldInterval.get(0));
    assertThat(amendOnHoldInterval.get(0).getAmendArrivalDate(), equalTo(REFERENCE_DEPARTURE_DATE));
    assertThat(amendOnHoldInterval.get(0).getAmendDepartureDate(), equalTo(newDepartureDate));
  }

  @Test
  void getLocalDateFromString__ShouldReturnOk() {
    // Arrange
    var newArrivalDate = "2024-02-14";

    // Act
    var newArrivalDateLd = amendLogicInPort.getLocalDateFromString(newArrivalDate);

    // Assert
    assertNotNull(newArrivalDateLd);
    assertThat(newArrivalDateLd.toString(), equalTo(newArrivalDate));
  }

  @Test
  void createAmendOnHoldInterval__ShouldReturnOk() {
    // Arrange
    var arrivalDate = "2024-02-14";
    var departureDate = "2024-02-14";

    // Act
    var amendOnHoldInterval = amendLogicInPort.createAmendOnHoldInterval(arrivalDate,
        departureDate);

    // Assert
    assertNotNull(amendOnHoldInterval);
    assertThat(amendOnHoldInterval.getAmendArrivalDate(), equalTo(arrivalDate));
    assertThat(amendOnHoldInterval.getAmendDepartureDate(), equalTo(departureDate));
  }

  @Test
  void getAmountFromRateInfo_ShouldReturnOk() {
    try (MockedStatic<TokenUtils> tokenUtilsMock = Mockito.mockStatic(TokenUtils.class)) {
      // Arrange
      when(basketOutPort.getBasketById("AWM3365195"))
          .thenReturn(getBasketResponse("MANOLD", "896423", null));

      var basketResponseTemp = getBasketResponse("MANOLD", "896424", null);
      basketResponseTemp.setLinkAmendReservations(Map.of("896423", "896424"));
      when(basketOutPort.getBasketById("AWM8057845"))
          .thenReturn(basketResponseTemp);

      when(hotelReservationOhipOutPort.getAmendSummaryDetails(
          createAmendSummaryRequest("MANOLD", "896423")))
          .thenReturn(getAmendSummaryResponse(3996L));

      when(hotelReservationOhipOutPort.getAmendSummaryDetails(
          createAmendSummaryRequest("MANOLD", "896424")))
          .thenReturn(getAmendSummaryResponse(4996L));

      when(hotelReservationOhipOutPort.getReservationsPackagesByIds(anyString(), anyList()))
          .thenReturn(getReservationsPackagesByIdsResponse("ZCHRY3"));

      when(hotelReservationOhipOutPort.getCharityPackagesDetails(anyString(), anyList()))
          .thenReturn(DonationPackagesResponse.builder().donationPackages(
              List.of(new DonationPackage("ZCHRY3", BigDecimal.TEN, "GBP"))).build());

      when(hotelReservationOhipOutPort.getReservationsByIds(anyString(), anyList(), anyBoolean(),
          anyBoolean(), anyBoolean()))
          .thenReturn(getReservationsResponse("Va"));

      tokenUtilsMock.when(() -> TokenUtils.isValid(anyString(), anyString()))
          .thenReturn(Boolean.TRUE);

      when(contentOutPort.getHotelPaymentInformation("MANOLD", "en", "gb"))
          .thenReturn(getPaymentResponse());

      // Act
      var amendSummaryDetails = amendLogicInPort.getAmendSummaryDetails(
          createAmendSummaryRequest(PI_BOOKING_CHANNEL));

      // Assert
      assertNotNull(amendSummaryDetails);
      assertEquals(BigDecimal.TEN, amendSummaryDetails.getCharitable());
      assertEquals(BigDecimal.valueOf(11988L), amendSummaryDetails.getPreviousTotal());
      assertEquals(BigDecimal.valueOf(11988L), amendSummaryDetails.getBalancePaid());
      assertEquals(BigDecimal.valueOf(-3996L), amendSummaryDetails.getRefund());
      assertEquals(BigDecimal.ZERO, amendSummaryDetails.getNonRefundable());
      assertEquals(BigDecimal.valueOf(14988L), amendSummaryDetails.getTotalCost());
      assertEquals(BigDecimal.ZERO, amendSummaryDetails.getBalanceAuthorised());
      assertNotNull(amendSummaryDetails.getPaymentCardDetails());
      assertEquals(CARD_NUMBER, amendSummaryDetails.getPaymentCardDetails().getCardNumberMasked());

    }
  }

  @Test
  void getAmountFromRateInfo_ShouldReturnNonRefundable() {
    try (MockedStatic<TokenUtils> tokenUtilsMock = Mockito.mockStatic(TokenUtils.class)) {
      // Arrange
      var basketResponseTemp = getBasketResponse("MANOLD", "896425", null);
      basketResponseTemp.setLinkAmendReservations(Map.of("896425", "896425"));
      when(basketOutPort.getBasketById(anyString()))
          .thenReturn(getBasketResponse("HOTELTEST", "896425", null));

      when(hotelReservationOhipOutPort.getAmendSummaryDetails(any(AmendSummaryAmountRequest.class)))
          .thenReturn(getAmendSummaryResponse(100L));

      when(hotelReservationOhipOutPort.getReservationsPackagesByIds(anyString(), anyList()))
          .thenReturn(getReservationsPackagesByIdsResponse("BFADBF"));

      when(hotelReservationOhipOutPort.getReservationsByIds(anyString(), anyList(), anyBoolean(),
          anyBoolean(), anyBoolean()))
          .thenReturn(getReservationsResponse("MC"));

      tokenUtilsMock.when(() -> TokenUtils.isValid(anyString(), anyString()))
          .thenReturn(Boolean.TRUE);

      when(contentOutPort.getHotelPaymentInformation("HOTELTEST", "en", "gb"))
          .thenReturn(getPaymentResponseWithMastercard());

      // Act
      var amendSummaryDetails = amendLogicInPort.getAmendSummaryDetails(
          createAmendSummaryRequest(PI_BOOKING_CHANNEL));

      // Assert
      assertNotNull(amendSummaryDetails);
      assertEquals(BigDecimal.ZERO, amendSummaryDetails.getCharitable());
      assertEquals(BigDecimal.valueOf(0), amendSummaryDetails.getNonRefundable());
      assertNotNull(amendSummaryDetails.getPaymentCardDetails());
      assertEquals(CARD_TOKEN, amendSummaryDetails.getPaymentCardDetails().getToken());
      assertEquals("Mastercard Credit", amendSummaryDetails.getPaymentCardDetails().getCardName());
      assertEquals("image.jpg", amendSummaryDetails.getPaymentCardDetails().getCardLogoSrc());


    }
  }

  @Test
  void getAmountFromRateInfo_ShouldReturnBalanceAuthorised() {
    try (MockedStatic<TokenUtils> tokenUtilsMock = Mockito.mockStatic(TokenUtils.class)) {
      // Arrange
      when(basketOutPort.getBasketById(anyString()))
          .thenReturn(getBasketResponse("HOTELTEST", "896425", null));

      when(hotelReservationOhipOutPort.getAmendSummaryDetails(any(AmendSummaryAmountRequest.class)))
          .thenReturn(getAmendSummaryResponse(100L));

      when(hotelReservationOhipOutPort.getReservationsPackagesByIds(anyString(), anyList()))
          .thenReturn(getReservationsPackagesByIdsResponse("ZCHRY3"));

      when(hotelReservationOhipOutPort.getCharityPackagesDetails(anyString(), anyList()))
          .thenReturn(DonationPackagesResponse.builder().donationPackages(
              List.of(new DonationPackage("ZCHRY3", BigDecimal.TEN, "GBP"))).build());

      tokenUtilsMock.when(() -> TokenUtils.isValid(anyString(), anyString()))
          .thenReturn(Boolean.TRUE);

      when(hotelReservationOhipOutPort.getReservationsByIds(anyString(), anyList(), anyBoolean(),
          anyBoolean(), anyBoolean()))
          .thenReturn(getReservationsResponse("Zz"));

      when(contentOutPort.getHotelPaymentInformation("HOTELTEST", "en", "gb")).thenReturn(
          getPaymentResponse());

      // Act
      var amendSummaryDetails = amendLogicInPort.getAmendSummaryDetails(
          createAmendSummaryRequest(PI_BOOKING_CHANNEL));

      // Assert
      assertNotNull(amendSummaryDetails);
      assertEquals(BigDecimal.valueOf(300), amendSummaryDetails.getBalanceAuthorised());
      assertEquals(BigDecimal.TEN, amendSummaryDetails.getCharitable());
      assertEquals(BigDecimal.valueOf(0), amendSummaryDetails.getNonRefundable());
      assertNotNull(amendSummaryDetails.getPaymentCardDetails());
      assertEquals("Testerson", amendSummaryDetails.getPaymentCardDetails().getCardHolderName());
      assertEquals("1103", amendSummaryDetails.getPaymentCardDetails().getCardNumberLast4Digits());
    }
  }

  @Test
  void getPaymentOptions_ShouldSetAmendPaymentPageForPibaPayedReservation() {
    try (MockedStatic<TokenUtils> tokenUtilsMock = Mockito.mockStatic(TokenUtils.class)) {
      // Arrange
      when(basketOutPort.getBasketById("AWM3365195"))
          .thenReturn(getBasketResponse("MANOLD", "896423", null));

      var basketResponseTemp = getBasketResponse("MANOLD", "896424", null);
      basketResponseTemp.setLinkAmendReservations(Map.of("896423", "896424"));
      when(basketOutPort.getBasketById("AWM8057845"))
          .thenReturn(basketResponseTemp);

      when(hotelReservationOhipOutPort.getAmendSummaryDetails(
          createAmendSummaryRequest("MANOLD", "896423")))
          .thenReturn(AmendSummaryAmountResponse.builder()
              .net(BigDecimal.valueOf(3996))
              .deposit(Map.of("896423", BigDecimal.valueOf(3996).negate()))
              .outStandingCostOfStay(BigDecimal.valueOf(3996))
              .guestPay(Map.of("896423", BigDecimal.ZERO))
              .build());

      when(hotelReservationOhipOutPort.getAmendSummaryDetails(
          createAmendSummaryRequest("MANOLD", "896424")))
          .thenReturn(getAmendSummaryResponseForPaymentOptions(4996L, 5000L));

      when(hotelReservationOhipOutPort.getReservationsPackagesByIds(anyString(), anyList()))
          .thenReturn(getReservationsPackagesByIdsResponse("ZCHRY3"));

      when(hotelReservationOhipOutPort.getCharityPackagesDetails(anyString(), anyList()))
          .thenReturn(DonationPackagesResponse.builder().donationPackages(
              List.of(new DonationPackage("ZCHRY3", BigDecimal.TEN, "GBP"))).build());

      var reservationsResponse = getReservationsResponse("");
      reservationsResponse.getReservationByIdList().get(0).getPaymentCard().setPaymentMethod("BU");
      when(hotelReservationOhipOutPort.getReservationsByIds(anyString(), anyList(), anyBoolean(),
          anyBoolean(), anyBoolean()))
          .thenReturn(reservationsResponse);

      tokenUtilsMock.when(() -> TokenUtils.isValid(anyString(), anyString()))
          .thenReturn(Boolean.TRUE);

      when(contentOutPort.getHotelPaymentInformation("MANOLD", "en", "gb"))
          .thenReturn(getPaymentResponse());

      var featureFlag = new FeatureFlag();
      when(unleashWrapper.featureFlag())
          .thenReturn(featureFlag);
      when(unleashWrapper.isEnabled(featureFlag.getCcuiAmendPiba()))
          .thenReturn(true);

      // Act
      var amendSummaryDetails = amendLogicInPort.getAmendSummaryDetails(
          createAmendSummaryRequest(CCUI_BOOKING_CHANNEL));

      // Assert
      assertNotNull(amendSummaryDetails);
      assertEquals(BigDecimal.TEN, amendSummaryDetails.getCharitable());
      assertEquals(BigDecimal.valueOf(3996L), amendSummaryDetails.getPreviousTotal());
      assertEquals(BigDecimal.valueOf(3996L), amendSummaryDetails.getBalancePaid());
      assertEquals(BigDecimal.valueOf(-3996L), amendSummaryDetails.getRefund());
      assertEquals(BigDecimal.ZERO, amendSummaryDetails.getNonRefundable());
      assertEquals(BigDecimal.valueOf(5000L), amendSummaryDetails.getTotalCost());
      assertEquals(BigDecimal.ZERO, amendSummaryDetails.getBalanceAuthorised());
      assertEquals(true, amendSummaryDetails.getPaymentOptions().getPayOnArrival());
      assertEquals(false, amendSummaryDetails.getPaymentOptions().getPayNow());
      assertEquals(true, amendSummaryDetails.getNavigationOptions().getAmendPaymentPage());
    }

  }

  @Test
  void getPaymentOptions_ShouldSetAmendPaymentPageForA2cReservation() {
    try (MockedStatic<TokenUtils> tokenUtilsMock = Mockito.mockStatic(TokenUtils.class)) {
      // Arrange
      when(basketOutPort.getBasketById("AWM3365195"))
          .thenReturn(getBasketResponse("MANOLD", "896423", null));

      var basketResponseTemp = getBasketResponse("MANOLD", "896424", null);
      basketResponseTemp.setLinkAmendReservations(Map.of("896423", "896424"));
      when(basketOutPort.getBasketById("AWM8057845"))
          .thenReturn(basketResponseTemp);

      when(hotelReservationOhipOutPort.getAmendSummaryDetails(
          createAmendSummaryRequest("MANOLD", "896423")))
          .thenReturn(AmendSummaryAmountResponse.builder()
              .net(BigDecimal.valueOf(3996))
              .deposit(Map.of("896423", BigDecimal.valueOf(3996).negate()))
              .outStandingCostOfStay(BigDecimal.valueOf(3996))
              .guestPay(Map.of("896423", BigDecimal.ZERO))
              .build());

      when(hotelReservationOhipOutPort.getAmendSummaryDetails(
          createAmendSummaryRequest("MANOLD", "896424")))
          .thenReturn(getAmendSummaryResponseForPaymentOptions(4996L, 5000L));

      when(hotelReservationOhipOutPort.getReservationsPackagesByIds(anyString(), anyList()))
          .thenReturn(getReservationsPackagesByIdsResponse("ZCHRY3"));

      when(hotelReservationOhipOutPort.getCharityPackagesDetails(anyString(), anyList()))
          .thenReturn(DonationPackagesResponse.builder().donationPackages(
              List.of(new DonationPackage("ZCHRY3", BigDecimal.TEN, "GBP"))).build());

      var reservationsResponse = getReservationsResponse(null);
      reservationsResponse.getReservationByIdList().get(0).setPaymentCard(
          getA2cReservationPaymentCardType());
      when(hotelReservationOhipOutPort.getReservationsByIds(anyString(), anyList(), anyBoolean(),
          anyBoolean(), anyBoolean()))
          .thenReturn(reservationsResponse);

      tokenUtilsMock.when(() -> TokenUtils.isValid(anyString(), anyString()))
          .thenReturn(Boolean.TRUE);

      when(contentOutPort.getHotelPaymentInformation("MANOLD", "en", "gb"))
          .thenReturn(getPaymentResponse());

      // Act
      var amendSummaryDetails = amendLogicInPort.getAmendSummaryDetails(
          createAmendSummaryRequest(CCUI_BOOKING_CHANNEL));

      // Assert
      assertNotNull(amendSummaryDetails);
      assertEquals(BigDecimal.TEN, amendSummaryDetails.getCharitable());
      assertEquals(BigDecimal.valueOf(3996L), amendSummaryDetails.getPreviousTotal());
      assertEquals(BigDecimal.valueOf(3996L), amendSummaryDetails.getBalancePaid());
      assertEquals(BigDecimal.valueOf(-3996L), amendSummaryDetails.getRefund());
      assertEquals(BigDecimal.ZERO, amendSummaryDetails.getNonRefundable());
      assertEquals(BigDecimal.valueOf(5000L), amendSummaryDetails.getTotalCost());
      assertEquals(BigDecimal.ZERO, amendSummaryDetails.getBalanceAuthorised());
      assertEquals(true, amendSummaryDetails.getPaymentOptions().getPayOnArrival());
      assertEquals(false, amendSummaryDetails.getPaymentOptions().getPayNow());
      assertEquals(true, amendSummaryDetails.getNavigationOptions().getAmendPaymentPage());
    }
  }

  private static ReservationPaymentCardType getA2cReservationPaymentCardType() {
    return ReservationPaymentCardType.builder()
        .cardType("")
        .expirationDate(null)
        .cardNumberMasked("")
        .token("")
        .cardHolderName(null)
        .paymentMethod("CA")
        .build();
  }

  @ParameterizedTest
  @ValueSource(booleans = {true, false})
  void getPaymentOptions_ShouldReturnOk(boolean isCardExpiredBeforeArrivalDate) {
    try (MockedStatic<TokenUtils> tokenUtilsMock = Mockito.mockStatic(TokenUtils.class)) {
      // Arrange
      when(basketOutPort.getBasketById("AWM3365195"))
          .thenReturn(getBasketResponse("MANOLD", "896423", null));

      var basketResponseTemp = getBasketResponse("MANOLD", "896424", null);
      basketResponseTemp.setLinkAmendReservations(Map.of("896423", "896424"));
      when(basketOutPort.getBasketById("AWM8057845"))
          .thenReturn(basketResponseTemp);

      when(hotelReservationOhipOutPort.getAmendSummaryDetails(
          createAmendSummaryRequest("MANOLD", "896423")))
          .thenReturn(AmendSummaryAmountResponse.builder()
              .net(BigDecimal.valueOf(3996))
              .deposit(Map.of("896423", BigDecimal.valueOf(3996).negate()))
              .outStandingCostOfStay(BigDecimal.valueOf(3996))
              .guestPay(Map.of("896423", BigDecimal.ZERO))
              .build());

      when(hotelReservationOhipOutPort.getAmendSummaryDetails(
          createAmendSummaryRequest("MANOLD", "896424")))
          .thenReturn(getAmendSummaryResponseForPaymentOptions(4996L, 5000L));

      when(hotelReservationOhipOutPort.getReservationsPackagesByIds(anyString(), anyList()))
          .thenReturn(getReservationsPackagesByIdsResponse("ZCHRY3"));

      when(hotelReservationOhipOutPort.getCharityPackagesDetails(anyString(), anyList()))
          .thenReturn(DonationPackagesResponse.builder().donationPackages(
              List.of(new DonationPackage("ZCHRY3", BigDecimal.TEN, "GBP"))).build());

      when(hotelReservationOhipOutPort.getReservationsByIds(anyString(), anyList(), anyBoolean(),
          anyBoolean(), anyBoolean()))
          .thenReturn(getReservationsResponse("Va"));

      if (isCardExpiredBeforeArrivalDate) {
        when(hotelReservationOhipOutPort.getReservationsByIds(anyString(), anyList(), anyBoolean(),
            anyBoolean(), anyBoolean()))
            .thenReturn(getReservationsResponseWithCardExpDateBeforeArrivalDate());
      } else {
        var mockedFeatureFlag = mock(FeatureFlag.class);
        when(unleashWrapper.featureFlag())
            .thenReturn(mockedFeatureFlag);
        var enableAmendPayNow = mock(Feature.class);
        when(mockedFeatureFlag.getEnableAmendPayNow())
            .thenReturn(enableAmendPayNow);
        when(unleashWrapper.isEnabled(any(Feature.class)))
            .thenReturn(true);
      }

      tokenUtilsMock.when(() -> TokenUtils.isValid(anyString(), anyString()))
          .thenReturn(Boolean.TRUE);

      when(contentOutPort.getHotelPaymentInformation("MANOLD", "en", "gb"))
          .thenReturn(getPaymentResponse());

      // Act
      var amendSummaryDetails = amendLogicInPort.getAmendSummaryDetails(
          createAmendSummaryRequest(PI_BOOKING_CHANNEL));

      // Assert
      assertNotNull(amendSummaryDetails);
      assertEquals(BigDecimal.TEN, amendSummaryDetails.getCharitable());
      assertEquals(BigDecimal.valueOf(3996L), amendSummaryDetails.getPreviousTotal());
      assertEquals(BigDecimal.valueOf(3996L), amendSummaryDetails.getBalancePaid());
      assertEquals(BigDecimal.ZERO, amendSummaryDetails.getNonRefundable());
      assertEquals(BigDecimal.ZERO, amendSummaryDetails.getBalanceAuthorised());
      assertEquals(true, amendSummaryDetails.getPaymentOptions().getPayOnArrival());

      assertEquals(!isCardExpiredBeforeArrivalDate,
          amendSummaryDetails.getPaymentOptions().getPayNow());
    }
  }

  @Test
  void getPaymentOptions_CCUI_ShouldReturnOk() {
    try (MockedStatic<TokenUtils> tokenUtilsMock = Mockito.mockStatic(TokenUtils.class)) {
      // Arrange
      when(basketOutPort.getBasketById("AWM3365195"))
          .thenReturn(getBasketResponse("MANOLD", "896423", null));

      var basketResponseTemp = getBasketResponse("MANOLD", "896424", null);
      basketResponseTemp.setLinkAmendReservations(Map.of("896423", "896424"));
      when(basketOutPort.getBasketById("AWM8057845"))
          .thenReturn(basketResponseTemp);

      when(hotelReservationOhipOutPort.getAmendSummaryDetails(
          createAmendSummaryRequest("MANOLD", "896423")))
          .thenReturn(AmendSummaryAmountResponse.builder()
              .net(BigDecimal.valueOf(3996))
              .deposit(Map.of("896423", BigDecimal.valueOf(3996).negate()))
              .outStandingCostOfStay(BigDecimal.valueOf(3996))
              .guestPay(Map.of("896423", BigDecimal.ZERO))
              .build());

      when(hotelReservationOhipOutPort.getAmendSummaryDetails(
          createAmendSummaryRequest("MANOLD", "896424")))
          .thenReturn(AmendSummaryAmountResponse.builder()
              .net(BigDecimal.valueOf(4996))
              .deposit(Map.of("896424", BigDecimal.valueOf(4996).negate()))
              .outStandingCostOfStay(BigDecimal.valueOf(4996))
              .guestPay(Map.of("896424", BigDecimal.valueOf(3997)))
              .build());

      when(hotelReservationOhipOutPort.getReservationsPackagesByIds(anyString(), anyList()))
          .thenReturn(getReservationsPackagesByIdsResponse("ZCHRY3"));

      when(hotelReservationOhipOutPort.getCharityPackagesDetails(anyString(), anyList()))
          .thenReturn(DonationPackagesResponse.builder().donationPackages(
              List.of(new DonationPackage("ZCHRY3", BigDecimal.TEN, "GBP"))).build());

      when(hotelReservationOhipOutPort.getReservationsByIds(anyString(), anyList(), anyBoolean(),
          anyBoolean(), anyBoolean()))
          .thenReturn(getReservationsResponse("Va"));

      tokenUtilsMock.when(() -> TokenUtils.isValid(anyString(), anyString()))
          .thenReturn(Boolean.TRUE);

      when(contentOutPort.getHotelPaymentInformation("MANOLD", "en", "gb"))
          .thenReturn(getPaymentResponse());

      // Act
      var amendSummaryDetails = amendLogicInPort.getAmendSummaryDetails(
          createAmendSummaryRequest(CCUI_BOOKING_CHANNEL));

      // Assert
      assertNotNull(amendSummaryDetails);
      assertEquals(BigDecimal.TEN, amendSummaryDetails.getCharitable());
      assertEquals(BigDecimal.valueOf(3996L), amendSummaryDetails.getPreviousTotal());
      assertEquals(BigDecimal.valueOf(3996L), amendSummaryDetails.getBalancePaid());
      assertEquals(BigDecimal.ZERO, amendSummaryDetails.getNonRefundable());
      assertEquals(BigDecimal.ZERO, amendSummaryDetails.getBalanceAuthorised());
      assertEquals(true, amendSummaryDetails.getPaymentOptions().getPayOnArrival());
      assertEquals(false, amendSummaryDetails.getPaymentOptions().getPayNow());
    }

  }

  @ParameterizedTest
  @CsvSource({",CA,false", "Va,DVA,true"})
  void getPaymentOptions_PI_ShouldReturnOk(String paymentType, String paymentMethod,
      Boolean isPayNowActive) {
    try (MockedStatic<TokenUtils> tokenUtilsMock = Mockito.mockStatic(TokenUtils.class)) {
      // Arrange
      when(basketOutPort.getBasketById("AWM3365195"))
          .thenReturn(getBasketResponse("MANOLD", "896423", null));

      var basketResponseTemp = getBasketResponse("MANOLD", "896424", null);
      basketResponseTemp.setLinkAmendReservations(Map.of("896423", "896424"));
      when(basketOutPort.getBasketById("AWM8057845"))
          .thenReturn(basketResponseTemp);

      when(hotelReservationOhipOutPort.getAmendSummaryDetails(
          createAmendSummaryRequest("MANOLD", "896423")))
          .thenReturn(AmendSummaryAmountResponse.builder()
              .net(BigDecimal.valueOf(3996))
              .deposit(Map.of("896423", BigDecimal.valueOf(3996).negate()))
              .outStandingCostOfStay(BigDecimal.valueOf(3996))
              .guestPay(Map.of("896423", BigDecimal.ZERO))
              .build());

      when(hotelReservationOhipOutPort.getAmendSummaryDetails(
          createAmendSummaryRequest("MANOLD", "896424")))
          .thenReturn(AmendSummaryAmountResponse.builder()
              .net(BigDecimal.valueOf(4996))
              .deposit(Map.of("896424", BigDecimal.valueOf(4996).negate()))
              .outStandingCostOfStay(BigDecimal.valueOf(4996))
              .guestPay(Map.of("896424", BigDecimal.valueOf(3997)))
              .build());

      when(hotelReservationOhipOutPort.getReservationsPackagesByIds(anyString(), anyList()))
          .thenReturn(getReservationsPackagesByIdsResponse("ZCHRY3"));

      when(hotelReservationOhipOutPort.getCharityPackagesDetails(anyString(), anyList()))
          .thenReturn(DonationPackagesResponse.builder().donationPackages(
              List.of(new DonationPackage("ZCHRY3", BigDecimal.TEN, "GBP"))).build());

      when(hotelReservationOhipOutPort.getReservationsByIds(anyString(), anyList(), anyBoolean(),
          anyBoolean(), anyBoolean()))
          .thenReturn(getReservationsResponse(paymentType, paymentMethod));

      tokenUtilsMock.when(() -> TokenUtils.isValid(anyString(), anyString()))
          .thenReturn(Boolean.TRUE);

      when(contentOutPort.getHotelPaymentInformation("MANOLD", "en", "gb"))
          .thenReturn(getPaymentResponse());

      if (!"CA".equalsIgnoreCase(paymentMethod)) {
        var mockedFeatureFlag = mock(FeatureFlag.class);
        when(unleashWrapper.featureFlag())
            .thenReturn(mockedFeatureFlag);
        when(mockedFeatureFlag.getEnableAmendPayNow())
            .thenReturn(mock(Feature.class));
        when(unleashWrapper.isEnabled(mockedFeatureFlag.getEnableAmendPayNow()))
            .thenReturn(true);
      }

      // Act
      var amendSummaryDetails = amendLogicInPort.getAmendSummaryDetails(
          createAmendSummaryRequest(PI_BOOKING_CHANNEL));

      // Assert
      assertNotNull(amendSummaryDetails);
      assertEquals(BigDecimal.TEN, amendSummaryDetails.getCharitable());
      assertEquals(BigDecimal.valueOf(3996L), amendSummaryDetails.getPreviousTotal());
      assertEquals(BigDecimal.valueOf(3996L), amendSummaryDetails.getBalancePaid());
      assertEquals(BigDecimal.ZERO, amendSummaryDetails.getNonRefundable());
      assertEquals(BigDecimal.ZERO, amendSummaryDetails.getBalanceAuthorised());
      assertEquals(true, amendSummaryDetails.getPaymentOptions().getPayOnArrival());

      assertEquals(isPayNowActive, amendSummaryDetails.getPaymentOptions().getPayNow());
    }

  }

  @Test
  void isLeadGuestUpdated_returnsFalse_whenNoUpdates() {
    // Arrange
    ReservationByIdResponse reservationByIdResponse = getReservationByIdResponse();
    UpdateReservationRequest updateReservationRequest = getUpdateReservationRequest();

    // Act
    boolean leadGuestUpdated = amendLogicInPort.isLeadGuestUpdated(reservationByIdResponse,
        updateReservationRequest);

    // Assert
    assertFalse(leadGuestUpdated);
  }

  @Test
  void isLeadGuestUpdated_returnsTrue_whenThereAreUpdates() {
    // Arrange
    ReservationByIdResponse reservationByIdResponse = getReservationByIdResponseWithNulls();
    UpdateReservationRequest updateReservationRequest = getUpdateReservationRequestWithNulls();

    // Act
    boolean leadGuestUpdated = amendLogicInPort.isLeadGuestUpdated(reservationByIdResponse,
        updateReservationRequest);

    // Assert
    assertTrue(leadGuestUpdated);
  }

  @Test
  void confirmAmendLogic_returnsPayment() {
    var amendSummaryAmountResponse = buildAmendSummaryAmountResponse();
    var initiatePaymentResponse = new InitiatePaymentResponse();

    when(authenticatedUserService.isUserAuthenticated()).thenReturn(true);
    when(basketOutPort.getBasketById(any())).thenReturn(getBasketResponse("LONEUS", "1233", null));
    when(hotelReservationOhipOutPort
        .getReservationsByIds(any(), anyList(), anyBoolean(), anyBoolean(), anyBoolean()))
        .thenReturn(getReservationsResponse("Va"));
    when(hotelReservationOhipOutPort.getAmendSummaryDetails(any())).thenReturn(
        amendSummaryAmountResponse);
    when(basketOutPort.initiatePayment(any(), any())).thenReturn(initiatePaymentResponse);

    var response = amendLogicInPort.confirmAmendLogic(ConfirmAmendLogicRequest.builder()
        .bookingChannel(BookingChannel.builder().channel("PI").build())
        .paymentOptionSelected(PaymentOption.PAY_NOW)
        .tempBookingRef("AKU-2ba0d8a5-07d7-45ee-ae22-ff5e94a4fc77")
        .build());

    assertEquals(initiatePaymentResponse, response.getPayment());
  }

  @ParameterizedTest
  @ValueSource(strings = {"RESERVE_WITHOUT_CARD", "ACCOUNT_COMPANY", "NEW_PIBA"})
  void confirmAmendLogicCCUI_success(String paymentOption) {

    var initiatePaymentResponse = new InitiatePaymentResponse();
    initiatePaymentResponse.setStatus(PaymentStatus.NOT_REQUIRED);
    when(authenticatedUserService.isUserAuthenticated()).thenReturn(true);
    when(basketOutPort.getBasketById(any())).thenReturn(getBasketResponse("LONEUS", "1233", null));

    var mockedFeatureFlag = mock(FeatureFlag.class);
    when(mockedFeatureFlag.getCcuiAgentIdLog()).thenReturn(mock(Feature.class));
    when(unleashWrapper.isEnabled(mockedFeatureFlag.getCcuiAgentIdLog())).thenReturn(false);

    if ("NEW_PIBA".equals(paymentOption)) {
      when(mockedFeatureFlag.getCcuiAmendPiba())
          .thenReturn(mock(Feature.class));
      when(unleashWrapper.isEnabled(mockedFeatureFlag.getCcuiAmendPiba()))
          .thenReturn(true);
    }

    when(unleashWrapper.featureFlag())
        .thenReturn(mockedFeatureFlag);

    var response = amendLogicInPort.confirmAmendLogic(ConfirmAmendLogicRequest.builder()
        .bookingChannel(BookingChannel.builder().channel("CCUI").build())
        .paymentOptionSelected(PaymentOption.PAY_ON_ARRIVAL)
        .paymentOption(paymentOption)
        .tempBookingRef("AKU-2ba0d8a5-07d7-45ee-ae22-ff5e94a4fc77")
        .build());

    assertEquals(initiatePaymentResponse, response.getPayment());
  }

  @Test
  void amendPaymentPage_returnsPibaBU() {

    var tempBasket =
        getBasketResponse("LONEUS", "1233",
            TEST_FUTURE_ETAG);
    when(authenticatedUserService.isUserAuthenticated()).thenReturn(true);
    when(basketOutPort.getBasketById(any())).thenReturn(tempBasket);
    when(hotelReservationOhipOutPort.getReservationsByIds(any(), anyList(),
        anyBoolean())).thenReturn(
        getReservationsResponsePibaBU());
    when(contentOutPort.getHotelInformation(any(), any(), any())).thenReturn(
        getHotelInfoResponse());

    var response = amendLogicInPort.amendPaymentPage(getPaymentPageRequest());

    assertNotNull(response);
    assertEquals(BigDecimal.ZERO, response.getDiscount());
    assertEquals(PaymentType.PIBA, response.getPaymentType());
  }

  @Test
  void amendPaymentPage_returnsPibaBD() {

    var tempBasket =
        getBasketResponse("LONEUS", "1233",
            TEST_FUTURE_ETAG);
    when(authenticatedUserService.isUserAuthenticated()).thenReturn(true);
    when(basketOutPort.getBasketById(any())).thenReturn(tempBasket);
    when(hotelReservationOhipOutPort.getReservationsByIds(any(), anyList(),
        anyBoolean())).thenReturn(
        getReservationsResponsePibaBD());
    when(contentOutPort.getHotelInformation(any(), any(), any())).thenReturn(
        getHotelInfoResponse());

    var response = amendLogicInPort.amendPaymentPage(getPaymentPageRequest());

    assertNotNull(response);
    assertEquals(BigDecimal.ZERO, response.getDiscount());
    assertEquals(PaymentType.PIBA, response.getPaymentType());
  }

  @ParameterizedTest
  @ValueSource(booleans = {true, false})
  void amendPaymentPage_returnsA2c(boolean useBasketAllowances) {
    var tempBasket = getBasketResponse("LONEUS", "1233",
        TEST_FUTURE_ETAG);
    tempBasket.setPaymentOption(
        uk.co.whitbread.reservation.domain.model.in.PaymentOption.ACCOUNT_COMPANY);

    if (useBasketAllowances) {
      tempBasket.setBookingAllowances(List.of(
          BookingAllowance.builder().allowance("premierInnBreakfast").build(),
          BookingAllowance.builder().allowance("carParking").build()));
    } else {
      tempBasket.setCcuiExtraItems(CcuiExtraItems.builder()
          .accountCompanyItems(AccountCompanyItems.builder()
              .charges("premierInnBreakfast,carParking")
              .build())
          .build());
    }
    var featureFlag = new FeatureFlag();

    when(unleashWrapper.featureFlag())
        .thenReturn(featureFlag);
    when(unleashWrapper.isEnabled(featureFlag.getUseBasketAllowances()))
        .thenReturn(useBasketAllowances);
    when(authenticatedUserService.isUserAuthenticated()).thenReturn(true);
    when(basketOutPort.getBasketById(any())).thenReturn(tempBasket);
    when(hotelReservationOhipOutPort.getReservationsByIds(any(), anyList(),
        anyBoolean())).thenReturn(
        getReservationsResponseA2c());
    when(contentOutPort.getHotelInformation(any(), any(), any())).thenReturn(
        getHotelInfoResponse());

    var response = amendLogicInPort.amendPaymentPage(getPaymentPageRequest());

    assertNotNull(response);
    assertEquals(BigDecimal.ZERO, response.getDiscount());
    assertEquals("Meine Addresse 12,Frankfurt", response.getA2cDetails().getAddress());
    assertEquals("12345", response.getA2cDetails().getPostcode());
    assertEquals("AMEROPA-REISEN GmbH", response.getA2cDetails().getName());
    assertEquals(PaymentType.ACCOUNT_COMPANY, response.getPaymentType());
    assertEquals("PI", response.getBrand());
    assertEquals("London Holborn", response.getHotelName());
    if (useBasketAllowances) {
      assertThat(response.getAllowances().getValues(), hasSize(2));
    } else {
      assertThat(response.getAllowances().getValues(), hasSize(0));
    }
    assertTrue(response.getPaymentOption().isPayOnArrival());
    assertFalse(response.getPaymentOption().isPayNow());
  }

  @Test
  void amendPaymentPage_returnsRsvWithoutCard() {
    var tempBasket = getBasketResponse("LONEUS", "1233",
        TEST_FUTURE_ETAG);
    tempBasket.setPaymentOption(
        uk.co.whitbread.reservation.domain.model.in.PaymentOption.RESERVE_WITHOUT_CARD);
    var availablePaymentType = AvailablePaymentType.builder().accountCompany(true)
        .payOnArrival(true).build();

    when(authenticatedUserService.isUserAuthenticated()).thenReturn(true);
    when(basketOutPort.getBasketById(any())).thenReturn(tempBasket);
    when(hotelReservationOhipOutPort.getReservationsByIds(any(), anyList(),
        anyBoolean())).thenReturn(
        getReservationsResponseRsvWithoutCard());
    when(contentOutPort.getHotelInformation(any(), any(), any())).thenReturn(
        getHotelInfoResponse());

    var response = amendLogicInPort.amendPaymentPage(getPaymentPageRequest());

    assertNotNull(response);
    assertEquals(BigDecimal.ZERO, response.getDiscount());
    assertEquals("Meine Addresse 12,Frankfurt", response.getA2cDetails().getAddress());
    assertEquals("12345", response.getA2cDetails().getPostcode());
    assertEquals("AMEROPA-REISEN GmbH", response.getA2cDetails().getName());
    assertEquals(PaymentType.RESERVE_WITHOUT_CARD, response.getPaymentType());
    assertEquals("PI", response.getBrand());
    assertEquals("London Holborn", response.getHotelName());
    assertTrue(response.getPaymentOption().isPayOnArrival());
    assertFalse(response.getPaymentOption().isPayNow());
    assertEquals(availablePaymentType, response.getAvailablePaymentType());
  }

  @Test
  void amendPaymentPage_returnsA2cForDirectSettlement_whenDefaultPaymentMethodDsEnabled() {
    var tempBasket = getBasketResponse("LONEUS", "1233", TEST_FUTURE_ETAG);
    tempBasket.setPaymentOption(
        uk.co.whitbread.reservation.domain.model.in.PaymentOption.ACCOUNT_COMPANY);
    tempBasket.setBookingAllowances(List.of(
        BookingAllowance.builder().allowance("premierInnBreakfast").build()));

    var reservationsResponse = getReservationsResponseA2c();
    reservationsResponse.getReservationByIdList().get(0).getPaymentCard().setPaymentMethod("DS");

    var featureFlag = new FeatureFlag();
    when(unleashWrapper.featureFlag()).thenReturn(featureFlag);
    when(unleashWrapper.isEnabled(featureFlag.getSetDefaultPaymentMethodDs())).thenReturn(true);
    when(unleashWrapper.isEnabled(featureFlag.getUseBasketAllowances())).thenReturn(true);

    when(authenticatedUserService.isUserAuthenticated()).thenReturn(true);
    when(basketOutPort.getBasketById(any())).thenReturn(tempBasket);
    when(hotelReservationOhipOutPort.getReservationsByIds(any(), anyList(), anyBoolean()))
        .thenReturn(reservationsResponse);
    when(contentOutPort.getHotelInformation(any(), any(), any())).thenReturn(
        getHotelInfoResponse());

    var response = amendLogicInPort.amendPaymentPage(getPaymentPageRequest());

    assertNotNull(response);
    assertEquals(PaymentType.ACCOUNT_COMPANY, response.getPaymentType());
    assertTrue(response.getPaymentOption().isPayOnArrival());
    assertFalse(response.getPaymentOption().isPayNow());
  }

  @Test
  void amendPaymentPage_returnsReserveWithoutCardForDirectSettlement_whenDefaultPaymentMethodDsEnabled() {
    var tempBasket = getBasketResponse("LONEUS", "1233", TEST_FUTURE_ETAG);
    tempBasket.setPaymentOption(
        uk.co.whitbread.reservation.domain.model.in.PaymentOption.RESERVE_WITHOUT_CARD);

    var reservationsResponse = getReservationsResponseRsvWithoutCard();
    reservationsResponse.getReservationByIdList().get(0).getPaymentCard().setPaymentMethod("DS");

    var featureFlag = new FeatureFlag();
    when(unleashWrapper.featureFlag()).thenReturn(featureFlag);
    when(unleashWrapper.isEnabled(featureFlag.getSetDefaultPaymentMethodDs())).thenReturn(true);

    when(authenticatedUserService.isUserAuthenticated()).thenReturn(true);
    when(basketOutPort.getBasketById(any())).thenReturn(tempBasket);
    when(hotelReservationOhipOutPort.getReservationsByIds(any(), anyList(), anyBoolean()))
        .thenReturn(reservationsResponse);
    when(contentOutPort.getHotelInformation(any(), any(), any())).thenReturn(
        getHotelInfoResponse());

    var response = amendLogicInPort.amendPaymentPage(getPaymentPageRequest());

    assertNotNull(response);
    assertEquals(PaymentType.RESERVE_WITHOUT_CARD, response.getPaymentType());
    assertTrue(response.getPaymentOption().isPayOnArrival());
    assertFalse(response.getPaymentOption().isPayNow());
  }

  @Test
  void amendPaymentPage_returnsEmptyResponseForDirectSettlement_whenDefaultPaymentMethodDsDisabled() {
    var tempBasket = getBasketResponse("LONEUS", "1233", TEST_FUTURE_ETAG);
    tempBasket.setPaymentOption(
        uk.co.whitbread.reservation.domain.model.in.PaymentOption.ACCOUNT_COMPANY);

    var reservationsResponse = getReservationsResponseA2c();
    reservationsResponse.getReservationByIdList().get(0).getPaymentCard().setPaymentMethod("DS");

    var featureFlag = new FeatureFlag();
    when(unleashWrapper.featureFlag()).thenReturn(featureFlag);
    when(unleashWrapper.isEnabled(featureFlag.getSetDefaultPaymentMethodDs())).thenReturn(false);

    when(authenticatedUserService.isUserAuthenticated()).thenReturn(true);
    when(basketOutPort.getBasketById(any())).thenReturn(tempBasket);
    when(hotelReservationOhipOutPort.getReservationsByIds(any(), anyList(), anyBoolean()))
        .thenReturn(reservationsResponse);

    var response = amendLogicInPort.amendPaymentPage(getPaymentPageRequest());

    assertNotNull(response);
    assertNull(response.getPaymentType());
    assertNull(response.getPaymentOption());
  }


  private static AmendPaymentPageRequest getPaymentPageRequest() {
    return AmendPaymentPageRequest.builder()
        .originalBookingRef("AJK-8624b3f1-4f88-4cf9-9a3c-902a7b1d0fea")
        .bookingChannel(BookingChannel.builder()
            .channel(CCUI_BOOKING_CHANNEL)
            .language("DE")
            .build())
        .country("DE")
        .build();
  }

  private static HotelInfoResponse getHotelInfoResponse() {
    return HotelInfoResponse.builder()
        .hotelId("LONHOL")
        .name("London Holborn")
        .brand("PI")
        .build();
  }

  @Test
  void getAmendConfirmationPrices_success() {

    var tempBasket =
        getBasketResponse("LONEUS", "1233",
            TEST_FUTURE_ETAG);
    when(authenticatedUserService.isUserAuthenticated()).thenReturn(true);
    when(basketOutPort.getBasketById(any())).thenReturn(tempBasket);
    when(hotelReservationOhipOutPort.getReservationsByIds(any(), anyList(),
        anyBoolean())).thenReturn(
        getReservationsResponse("Va"));
    doNothing().when(reservationCleanUp).cleanupTempBasket(tempBasket);

    var response = amendLogicInPort.getAmendConfirmationPrices(
        AmendConfirmationPricesRequest.builder()
            .tempBookingRef("AKU-2ba0d8a5-07d7-45ee-ae22-ff5e94a4fc77")
            .originalBookingRef("AKU-2ba0d8a5-07d7-45ee-ae22-ff5e94a4fc88")
            .build());

    assertEquals(BigDecimal.TEN, response.getPreviousTotal());
    assertEquals(BigDecimal.valueOf(50), response.getNewTotalCost());
  }

  @Test
  void getAmendConfirmationPrices_success_WithToken() {
    try (MockedStatic<TokenUtils> tokenUtilsMock = Mockito.mockStatic(TokenUtils.class)) {
      var tempBasket =
          getBasketResponse("LONEUS", "1233",
              TEST_FUTURE_ETAG);
      when(authenticatedUserService.isUserAuthenticated()).thenReturn(false);
      when(basketOutPort.getBasketById(any())).thenReturn(tempBasket);
      when(hotelReservationOhipOutPort.getReservationsByIds(any(), anyList(),
          anyBoolean())).thenReturn(
          getReservationsResponse("Va"));
      tokenUtilsMock.when(() -> TokenUtils.isValid(anyString(), anyString()))
          .thenReturn(Boolean.TRUE);
      doNothing().when(reservationCleanUp).cleanupTempBasket(tempBasket);

      var response = amendLogicInPort.getAmendConfirmationPrices(
          AmendConfirmationPricesRequest.builder()
              .tempBookingRef("AKU-2ba0d8a5-07d7-45ee-ae22-ff5e94a4fc77")
              .originalBookingRef("AKU-2ba0d8a5-07d7-45ee-ae22-ff5e94a4fc88")
              .token(getMockedToken())
              .build());

      assertEquals(BigDecimal.TEN, response.getPreviousTotal());
      assertEquals(BigDecimal.valueOf(50), response.getNewTotalCost());
    }
  }

  @Test
  void getAmendConfirmationPrices_Fails_whenError() {

    when(authenticatedUserService.isUserAuthenticated()).thenReturn(true);
    AmendConfirmationPricesRequest amendConfirmationPricesRequest = AmendConfirmationPricesRequest.builder()
        .tempBookingRef("AKU-2ba0d8a5-07d7-45ee-ae22-ff5e94a4fc77")
        .originalBookingRef("AKU-2ba0d8a5-07d7-45ee-ae22-ff5e94a4fc88")
        .build();
    when(basketOutPort.getBasketById(amendConfirmationPricesRequest.getTempBookingRef())).thenThrow(
        BasketNotFoundException.class);

    assertThrows(BasketNotFoundException.class, () ->
        amendLogicInPort.getAmendConfirmationPrices(
            amendConfirmationPricesRequest));
    String message = listAppender.list.get(0).getFormattedMessage();
    assert (message).contains("Business exception error code:");
  }

  @Test
  void getAmendConfirmationPrices_Fails_whenTempBasketIsExpired() {
    var exc = new BasketNotFoundException("message", "debug message", null, 707);
    when(authenticatedUserService.isUserAuthenticated()).thenReturn(true);
    AmendConfirmationPricesRequest amendConfirmationPricesRequest = AmendConfirmationPricesRequest.builder()
        .tempBookingRef("AKU-2ba0d8a5-07d7-45ee-ae22-ff5e94a4fc77")
        .originalBookingRef("AKU-2ba0d8a5-07d7-45ee-ae22-ff5e94a4fc88")
        .build();
    when(basketOutPort.getBasketById(amendConfirmationPricesRequest.getTempBookingRef())).thenThrow(
        exc);
    assertThrows(BasketNotFoundException.class, () ->
        amendLogicInPort.getAmendConfirmationPrices(
            amendConfirmationPricesRequest));
    String message = listAppender.list.get(0).getFormattedMessage();
    assert (message).contains("has expired or is empty.");
  }

  private static AmendSummaryAmountResponse buildAmendSummaryAmountResponse() {
    var guestPay = new HashMap<String, BigDecimal>();
    guestPay.put("3109771", BigDecimal.ZERO);
    var deposit = new HashMap<String, BigDecimal>();
    deposit.put("3109771", new BigDecimal(-999));

    return AmendSummaryAmountResponse
        .builder()
        .net(new BigDecimal(999))
        .deposit(deposit)
        .totalCostOfStay(new BigDecimal(999))
        .outStandingCostOfStay(BigDecimal.ZERO)
        .guestPay(guestPay)
        .build();
  }

  @Test
  void confirmAmendLogic_returnsStatusNOT_REQUIRED() {
    //Arrange
    when(authenticatedUserService.isUserAuthenticated()).thenReturn(true);
    when(basketOutPort.getBasketById(any())).thenReturn(BasketResponse.builder().build());
    doNothing().when(basketOutPort).processAmend(any(), any(), any());

    //Act
    var response = amendLogicInPort.confirmAmendLogic(ConfirmAmendLogicRequest.builder()
        .bookingChannel(BookingChannel.builder().channel("PI").build())
        .paymentOptionSelected(PaymentOption.PAY_ON_ARRIVAL)
        .tempBookingRef("AKU-2ba0d8a5-07d7-45ee-ae22-ff5e94a4fc77")
        .build());

    //Assert
    assertNotNull(response);
    assertEquals(PaymentStatus.NOT_REQUIRED, response.getPayment().getStatus());
  }

  @Test
  void confirmAmendLogic_preCheckIn() {
    var tempBasket =
        getBasketResponse("LONEUS", "1233",
            TEST_FUTURE_ETAG);
    when(authenticatedUserService.isUserAuthenticated()).thenReturn(true);
    when(basketOutPort.getBasketById(any())).thenReturn(tempBasket);

    //Act
    var response = amendLogicInPort.confirmAmendLogic(ConfirmAmendLogicRequest.builder()
        .bookingChannel(BookingChannel.builder().channel("PI").build())
        .paymentOptionSelected(PaymentOption.PAY_ON_ARRIVAL)
        .tempBookingRef("AKU-2ba0d8a5-07d7-45ee-ae22-ff5e94a4fc77")
        .preCheckIn(List.of("1234"))
        .build());
    assertNotNull(response);
    assertEquals(PaymentStatus.NOT_REQUIRED, response.getPayment().getStatus());
  }

  @Test
  void deleteRegCardPdfAndPreCheckInStatus_noReservationIds() {
    // Arrange
    var basket = BasketResponse
        .builder()
        .hotelId("STUAIR")
        .items(Collections.emptyList())
        .originalBasketId("AKU-2ba0d8a5-07d7-45ee-ae22-ff5e94a4fc7a")
        .eTag(TEST_FUTURE_ETAG)
        .build();
    when(authenticatedUserService.isUserAuthenticated()).thenReturn(true);
    when(basketOutPort.getBasketById(any())).thenReturn(basket);

    // Act
    var response = amendLogicInPort.confirmAmendLogic(ConfirmAmendLogicRequest.builder()
        .bookingChannel(BookingChannel.builder().channel("PI").build())
        .paymentOptionSelected(PaymentOption.PAY_ON_ARRIVAL)
        .tempBookingRef("AKU-2ba0d8a5-07d7-45ee-ae22-ff5e94a4fc7a")
        .preCheckIn(Collections.emptyList())
        .build());

    // Assert
    assertNotNull(response);
    assertEquals(PaymentStatus.NOT_REQUIRED, response.getPayment().getStatus());
    verify(hotelReservationOhipOutPort, never()).deleteRegCardAttachment(any(), any());
    verify(hotelReservationOhipOutPort, never()).deleteReservationPreCheckIn(any(), any());
  }

  @Test
  void deleteRegCardPdfAndPreCheckInStatus_basketNotFound() {
    // Arrange
    var basketResponse = getBasketResponse("STUAIR", "896423", null);
    basketResponse.setReference("1234");
    when(authenticatedUserService.isUserAuthenticated()).thenReturn(true);
    when(basketOutPort.getBasketById(any()))
        .thenReturn(basketResponse);

    // Act
    var response = amendLogicInPort.confirmAmendLogic(ConfirmAmendLogicRequest.builder()
        .bookingChannel(BookingChannel.builder().channel("PI").build())
        .paymentOptionSelected(PaymentOption.PAY_ON_ARRIVAL)
        .preCheckIn(List.of("896423"))
        .build());

    // Assert
    assertNotNull(response);
    assertEquals(PaymentStatus.NOT_REQUIRED, response.getPayment().getStatus());
  }

  private BasketResponse getBasketResponse(String hotelId, String reservationId, String eTag) {
    var basketItemResponse = BasketItemResponse.builder()
        .sourceId(reservationId)
        .build();

    return BasketResponse
        .builder()
        .hotelId(hotelId)
        .items(Collections.singletonList(basketItemResponse))
        .originalBasketId("AKU-2ba0d8a5-07d7-45ee-ae22-ff5e94a4fc7a")
        .linkAmendReservations(Map.of("1234", "1235", reservationId, reservationId))
        .eTag(eTag)
        .build();
  }

  private AmendSummaryAmountRequest createAmendSummaryRequest(String hotelId,
      String reservationId) {
    List<String> listOfReservationIds1 = Collections.singletonList(reservationId);
    return AmendSummaryAmountRequest
        .builder()
        .hotelId(hotelId)
        .reservationIds(listOfReservationIds1)
        .build();
  }

  private AmendSummaryAmountResponse getAmendSummaryResponse(Long value) {
    return AmendSummaryAmountResponse.builder()
        .net(BigDecimal.valueOf(value * 3))
        .deposit(Map.of("1234", BigDecimal.valueOf(value).negate(),
            "896423", BigDecimal.valueOf(value).negate(),
            "896425", BigDecimal.valueOf(value).negate()))
        .totalCostOfStay(BigDecimal.valueOf(value * 3))
        .outStandingCostOfStay(BigDecimal.valueOf(value * 3))
        .guestPay(Map.of("1234", BigDecimal.valueOf(value),
            "896423", BigDecimal.valueOf(value).negate(),
            "896425", BigDecimal.valueOf(value).negate()))
        .build();
  }

  private ReservationsPackagesResponse getReservationsPackagesByIdsResponse(String packageCode) {
    return ReservationsPackagesResponse.builder()
        .roomsSelections(List.of(RoomsSelectionsByReservation.builder().packagesSelection(List.of(
            PackagesSelection.builder().id(packageCode).noOfSelections(1).build())).build()))
        .build();
  }

  private ReservationByBasketRefResponse getReservationsResponse(String cardType) {
    return this.getReservationsResponse(cardType, null);
  }

  private ReservationByBasketRefResponse getReservationsResponse(String cardType,
      String paymentMethod) {
    return ReservationByBasketRefResponse.builder()
        .reservationByIdList(List.of(ReservationByIdResponse.builder()
            .paymentCard(ReservationPaymentCardType.builder()
                .cardType(cardType)
                .paymentMethod(paymentMethod)
                .expirationDate(getFutureCardExpiryDate())
                .cardNumberMasked(CARD_NUMBER)
                .token(CARD_TOKEN)
                .cardHolderName("Testerson")
                .build())
            .billing(BillingResponse.builder()
                .address(AddressResponse.builder()
                    .countryCode("GB")
                    .build())
                .build())
            .roomStay(RoomStayByIdResponse.builder()
                .ratePlanCode("FLEXRATE")
                .roomType("DB")
                .arrivalDate("2024-02-11")
                .adultsNumber(1)
                .build())
            .guaranteeCode("CO")
            .build()))
        .previousTotal(BigDecimal.TEN)
        .newTotal(BigDecimal.valueOf(50))
        .build();
  }

  private ReservationByBasketRefResponse getReservationsResponseWithCardExpDateBeforeArrivalDate() {
    return ReservationByBasketRefResponse.builder()
        .reservationByIdList(List.of(ReservationByIdResponse.builder()
            .paymentCard(ReservationPaymentCardType.builder()
                .cardType("Va")
                .expirationDate(EXPIRED_CARD_DATE)
                .cardNumberMasked(CARD_NUMBER)
                .token(CARD_TOKEN)
                .cardHolderName("Testerson")
                .build())
            .billing(BillingResponse.builder()
                .address(AddressResponse.builder()
                    .countryCode("GB")
                    .build())
                .build())
            .roomStay(RoomStayByIdResponse.builder()
                .ratePlanCode("FLEXRATE")
                .roomType("DB")
                .arrivalDate("2024-05-23")
                .adultsNumber(1)
                .build())
            .build()))
        .build();
  }

  private ReservationByBasketRefResponse getReservationsResponsePibaBU() {
    return ReservationByBasketRefResponse.builder()
        .reservationByIdList(List.of(ReservationByIdResponse.builder()
            .paymentCard(ReservationPaymentCardType.builder()
                .cardType("ZZ")
                .expirationDate(getFutureCardExpiryDate())
                .cardNumberMasked(CARD_NUMBER)
                .token(CARD_TOKEN)
                .cardHolderName("Testerson")
                .paymentMethod("BU")
                .build())
            .billing(BillingResponse.builder()
                .address(AddressResponse.builder()
                    .countryCode("GB")
                    .build())
                .build())
            .roomStay(RoomStayByIdResponse.builder()
                .ratePlanCode("FLEXRATE")
                .roomType("DB")
                .adultsNumber(1)
                .build())
            .reservationEmailNotifications(ReservationEmailNotificationsResponse.builder()
                .sendEmailConfirmation(Boolean.TRUE)
                .build())
            .reservationBooker(ReservationBooker.builder()
                .email("email")
                .build())
            .build()))
        .previousTotal(BigDecimal.TEN)
        .newTotal(BigDecimal.valueOf(50))
        .isCnp(Boolean.FALSE)
        .discount(BigDecimal.ZERO)
        .build();
  }

  private ReservationByBasketRefResponse getReservationsResponsePibaBD() {
    return ReservationByBasketRefResponse.builder()
        .reservationByIdList(List.of(ReservationByIdResponse.builder()
            .paymentCard(ReservationPaymentCardType.builder()
                .cardType("ZZ")
                .expirationDate(getFutureCardExpiryDate())
                .cardNumberMasked(CARD_NUMBER)
                .token(CARD_TOKEN)
                .cardHolderName("Testerson")
                .paymentMethod("BD")
                .build())
            .billing(BillingResponse.builder()
                .address(AddressResponse.builder()
                    .countryCode("GB")
                    .build())
                .build())
            .roomStay(RoomStayByIdResponse.builder()
                .ratePlanCode("FLEXRATE")
                .roomType("DB")
                .adultsNumber(1)
                .build())
            .reservationEmailNotifications(ReservationEmailNotificationsResponse.builder()
                .sendEmailConfirmation(Boolean.TRUE)
                .build())
            .reservationBooker(ReservationBooker.builder()
                .email("email")
                .build())
            .build()))
        .previousTotal(BigDecimal.TEN)
        .newTotal(BigDecimal.valueOf(50))
        .isCnp(Boolean.FALSE)
        .discount(BigDecimal.ZERO)
        .build();
  }

  private ReservationByBasketRefResponse getReservationsResponseA2c() {
    return ReservationByBasketRefResponse.builder()
        .reservationByIdList(List.of(ReservationByIdResponse.builder()
            .paymentCard(ReservationPaymentCardType.builder()
                .cardType("")
                .cardNumberMasked("")
                .token("")
                .cardHolderName("")
                .paymentMethod("CA")
                .build())
            .reservationEmailNotifications(ReservationEmailNotificationsResponse.builder()
                .sendEmailConfirmation(Boolean.TRUE)
                .build())
            .reservationBooker(ReservationBooker.builder()
                .email("email")
                .build())
            .reservationCompany(ReservationCompany.builder()
                .address(ReservationBookerAddress.builder()
                    .companyName("AMEROPA-REISEN GmbH")
                    .addressLine1("Meine Addresse 12")
                    .cityName("Frankfurt")
                    .postalCode("12345")
                    .build())
                .build())
            .build()))
        .previousTotal(BigDecimal.TEN)
        .newTotal(BigDecimal.valueOf(50))
        .isCnp(Boolean.FALSE)
        .discount(BigDecimal.ZERO)
        .build();
  }

  private ReservationByBasketRefResponse getReservationsResponseRsvWithoutCard() {
    return ReservationByBasketRefResponse.builder()
        .reservationByIdList(List.of(ReservationByIdResponse.builder()
            .paymentCard(ReservationPaymentCardType.builder()
                .cardType("")
                .cardNumberMasked("")
                .token("")
                .cardHolderName("")
                .paymentMethod("CA")
                .build())
            .reservationEmailNotifications(ReservationEmailNotificationsResponse.builder()
                .sendEmailConfirmation(Boolean.TRUE)
                .build())
            .reservationBooker(ReservationBooker.builder()
                .email("email")
                .build())
            .reservationCompany(ReservationCompany.builder()
                .address(ReservationBookerAddress.builder()
                    .companyName("AMEROPA-REISEN GmbH")
                    .addressLine1("Meine Addresse 12")
                    .cityName("Frankfurt")
                    .postalCode("12345")
                    .build())
                .build())
            .billing(BillingResponse.builder()
                .firstName("John")
                .lastName("Testerson")
                .build())
            .build()))
        .previousTotal(BigDecimal.TEN)
        .newTotal(BigDecimal.valueOf(50))
        .isCnp(Boolean.FALSE)
        .discount(BigDecimal.ZERO)
        .build();
  }


  private AmendSummaryRequest createAmendSummaryRequest(String bookingChannel) {
    return AmendSummaryRequest.builder()
        .originalBasketRef("AWM3365195")
        .copyBasketRef("AWM8057845")
        .bookingChannel(BookingChannel.builder()
            .channel(bookingChannel)
            .subchannel("WEB")
            .language("EN")
            .build())
        .token(getMockedToken())
        .country("GB")
        .build();
  }

  private LocalDate getFutureCardExpiryDate() {
    return FUTURE_CARD_EXPIRY_DATE;
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

  private HotelPaymentInformation getPaymentResponseWithMastercard() {
    return HotelPaymentInformation.builder()
        .acceptedCreditCards(
            List.of(AcceptedCreditCard.builder()
                .codeOpera("MC")
                .codeOperaCardType("MC")
                .name("Mastercard Credit")
                .schemeLogo("image.jpg")
                .build()))
        .build();
  }

  private String getMockedToken() {
    return "32RQfSN6U9YLgjv+7NaYg4qyiXQs7O3hak/ZoA+hCL2V3M8tiFoi6wXBCBbKS2Qx5WBJuP8=";
  }

  private AmendSummaryAmountResponse getAmendSummaryResponseForPaymentOptions(Long value1,
      Long value2) {
    return AmendSummaryAmountResponse.builder()
        .net(BigDecimal.valueOf(value1))
        .deposit(Map.of("896423", BigDecimal.valueOf(value1).negate()))
        .totalCostOfStay(BigDecimal.valueOf(value2))
        .outStandingCostOfStay(BigDecimal.valueOf(value1))
        .guestPay(Map.of("896423", BigDecimal.valueOf(value2)))
        .build();
  }

  private ReservationByIdResponse getReservationByIdResponse() {
    return ReservationByIdResponse.builder()
        .reservationGuestList(List.of(ReservationByIdGuestsResponse.builder()
            .givenName(MARY)
            .surName(JANE)
            .address(getReservationGuestAddress())
            .build()))
        .build();
  }

  private GuestAddress getReservationGuestAddress() {
    return GuestAddress.builder()
        .addressLine1("First line")
        .cityName("Big City")
        .postalCode("PO5 TA1")
        .build();
  }

  private ReservationByIdResponse getReservationByIdResponseWithNulls() {
    return ReservationByIdResponse.builder()
        .reservationGuestList(List.of(ReservationByIdGuestsResponse.builder()
            .givenName(JOHN)
            .surName(DOE)
            .address(getReservationGuestAddress())
            .build()))
        .build();
  }

  private UpdateReservationRequest getUpdateReservationRequest() {
    var reservationGuests = new ArrayList<ReservationGuests>();
    var guest = ReservationGuests.builder()
        .profileInfo(ProfileInfo.builder()
            .profile(ProfileType.builder()
                .customer(CustomerType.builder()
                    .personName(List.of(
                        PersonNameType.builder()
                            .givenName(MARY)
                            .surname(JANE)
                            .build()))
                    .build())
                .build())
            .build())
        .build();
    reservationGuests.add(guest);
    return UpdateReservationRequest.builder()
        .reservationGuests(reservationGuests)
        .build();
  }

  private UpdateReservationRequest getUpdateReservationRequestWithNulls() {
    var reservationGuests = new ArrayList<ReservationGuests>();
    var guest = ReservationGuests.builder()
        .profileInfo(ProfileInfo.builder()
            .profile(ProfileType.builder()
                .customer(CustomerType.builder()
                    .personName(List.of(
                        PersonNameType.builder()
                            .givenName(JOHN)
                            .surname(DOE)
                            .nameTitle(MS)
                            .build()))
                    .build())
                .emails(ProfileTypeEmails.builder()
                    .emailInfo(List.of(
                        EmailInfoType.builder()
                            .email(EmailType.builder()
                                .emailAddress(JOHN_DOE_EMAIL)
                                .build())
                            .build()))
                    .build())
                .build())
            .build())
        .build();
    reservationGuests.add(guest);
    return UpdateReservationRequest.builder()
        .reservationGuests(reservationGuests)
        .build();
  }

  @Test
  void getPaymentOptionsFeatureOnPayNowShouldReturnFalse() {
    try (MockedStatic<TokenUtils> tokenUtilsMock = Mockito.mockStatic(TokenUtils.class)) {
      // Arrange
      when(basketOutPort.getBasketById("AWM3365195"))
          .thenReturn(getBasketResponse("MANOLD", "896423", null));

      var basketResponseTemp = getBasketResponse("MANOLD", "896424", null);
      basketResponseTemp.setLinkAmendReservations(Map.of("896423", "896424"));
      when(basketOutPort.getBasketById("AWM8057845"))
          .thenReturn(basketResponseTemp);

      when(hotelReservationOhipOutPort.getAmendSummaryDetails(
          createAmendSummaryRequest("MANOLD", "896423")))
          .thenReturn(AmendSummaryAmountResponse.builder()
              .net(BigDecimal.valueOf(3996))
              .deposit(Map.of("896423", BigDecimal.valueOf(3996).negate()))
              .outStandingCostOfStay(BigDecimal.valueOf(3996))
              .guestPay(Map.of("896423", BigDecimal.ZERO))
              .build());

      when(hotelReservationOhipOutPort.getAmendSummaryDetails(
          createAmendSummaryRequest("MANOLD", "896424")))
          .thenReturn(AmendSummaryAmountResponse.builder()
              .net(BigDecimal.valueOf(4996))
              .deposit(Map.of("896424", BigDecimal.valueOf(4996).negate()))
              .outStandingCostOfStay(BigDecimal.valueOf(4996))
              .guestPay(Map.of("896424", BigDecimal.valueOf(3997)))
              .build());

      when(hotelReservationOhipOutPort.getReservationsPackagesByIds(anyString(), anyList()))
          .thenReturn(getReservationsPackagesByIdsResponse("ZCHRY3"));

      when(hotelReservationOhipOutPort.getCharityPackagesDetails(anyString(), anyList()))
          .thenReturn(DonationPackagesResponse.builder().donationPackages(
              List.of(new DonationPackage("ZCHRY3", BigDecimal.TEN, "GBP"))).build());

      when(hotelReservationOhipOutPort.getReservationsByIds(anyString(), anyList(), anyBoolean(),
          anyBoolean(), anyBoolean()))
          .thenReturn(getReservationsResponse("VA", "DVA"));

      tokenUtilsMock.when(() -> TokenUtils.isValid(anyString(), anyString()))
          .thenReturn(Boolean.TRUE);

      when(contentOutPort.getHotelPaymentInformation("MANOLD", "en", "gb"))
          .thenReturn(getPaymentResponse());
      var mockedFeatureFlag = mock(FeatureFlag.class);
      when(unleashWrapper.featureFlag())
          .thenReturn(mockedFeatureFlag);
      when(mockedFeatureFlag.getEnableAmendPayNow())
          .thenReturn(mock(Feature.class));
      when(unleashWrapper.isEnabled(mockedFeatureFlag.getEnableAmendPayNow()))
          .thenReturn(false);

      // Act
      var amendSummaryDetails = amendLogicInPort.getAmendSummaryDetails(
          createAmendSummaryRequest(PI_BOOKING_CHANNEL));

      // Assert
      assertNotNull(amendSummaryDetails);
      assertEquals(BigDecimal.TEN, amendSummaryDetails.getCharitable());
      assertEquals(BigDecimal.valueOf(3996L), amendSummaryDetails.getPreviousTotal());
      assertEquals(BigDecimal.valueOf(3996L), amendSummaryDetails.getBalancePaid());
      assertEquals(BigDecimal.ZERO, amendSummaryDetails.getNonRefundable());
      assertEquals(BigDecimal.ZERO, amendSummaryDetails.getBalanceAuthorised());
      assertEquals(true, amendSummaryDetails.getPaymentOptions().getPayOnArrival());

      assertFalse(amendSummaryDetails.getPaymentOptions().getPayNow());
    }
  }
}