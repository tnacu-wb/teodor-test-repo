package uk.co.whitbread.payments.infrastructure.rest.client.reservation;


import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static uk.co.whitbread.payments.infrastructure.rest.client.reservation.ReservationTestDataBuilder.createReservationWithErrorPolicy;
import static uk.co.whitbread.payments.infrastructure.rest.client.reservation.ReservationTestDataBuilder.createReservationWithoutDates;
import static uk.co.whitbread.payments.infrastructure.rest.client.reservation.ReservationTestDataBuilder.createReservationWithPolicy;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.stream.Stream;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.ArgumentCaptor;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;
import uk.co.whitbread.payments.domain.model.out.DepositsResponse;
import uk.co.whitbread.payments.domain.exception.PaymentMethodsException;
import uk.co.whitbread.payments.domain.model.out.PaymentPolicy;
import uk.co.whitbread.payments.infrastructure.rest.client.reservation.mapper.DepositFolioMapper;
import uk.co.whitbread.payments.infrastructure.rest.client.reservation.model.out.DepositsDto;
import uk.co.whitbread.payments.infrastructure.rest.client.reservation.model.out.DepositsResponseDto;
import uk.co.whitbread.payments.infrastructure.rest.client.reservation.model.out.ReservationListDto;
import uk.co.whitbread.payments.infrastructure.rest.client.reservation.service.ReservationClient;

@ExtendWith(MockitoExtension.class)
class ReservationsPortImplTest {

  public static final String BASKET_REFERENCE = "BASKET_123";
  public static final String HOTEL_ID = "HOTEL123";
  public static final LocalDate DEPARTURE_DATE = LocalDate.now();
  public static final LocalDate ARRIVAL_DATE = LocalDate.now().plusDays(3);

  @Mock
  private ReservationClient reservationClient;

  @Mock
  private DepositFolioMapper depositFolioMapper;

  @InjectMocks
  private ReservationsPortImpl underTest;


  @ParameterizedTest(name = "{0}")
  @MethodSource("provideReservationPolicyTestData")
  void findReservation_withDifferentPolicies_success(String testName, String policyCode,
      List<PaymentPolicy> expectedPolicies, boolean useCache) {
    // Arrange
    var reservationsDto = createReservationWithPolicy(policyCode, DEPARTURE_DATE, ARRIVAL_DATE);
    if (useCache) {
      given(reservationClient.getCachedReservations(BASKET_REFERENCE)).willReturn(reservationsDto);
    } else {
      given(reservationClient.findReservations(BASKET_REFERENCE)).willReturn(reservationsDto);
    }

    // Act
    var result = underTest.findReservations(BASKET_REFERENCE, useCache);

    // Assert
    assertThat(result, notNullValue());
    assertThat(result.getHotelPaymentPolicies(), containsInAnyOrder(expectedPolicies.toArray()));
    assertThat(result.getDepartureDate(), equalTo(DEPARTURE_DATE));
  }

  private static Stream<Arguments> provideReservationPolicyTestData() {
    return Stream.of(
        Arguments.of("Policy OA - Pay on Arrival", "OA", List.of(PaymentPolicy.PAY_ON_ARRIVAL), false),
        Arguments.of("Policy NL - Pay Now and Pay on Arrival", "NL",
            List.of(PaymentPolicy.PAY_ON_ARRIVAL, PaymentPolicy.PAY_NOW), false),
        Arguments.of("Policy RWC - Reserve Without Card", "RWC",
            List.of(PaymentPolicy.RESERVE_WITHOUT_CARD, PaymentPolicy.PAY_ON_ARRIVAL, PaymentPolicy.PAY_NOW), false),
        Arguments.of("Policy OA with Cache", "OA", List.of(PaymentPolicy.PAY_ON_ARRIVAL), true)
    );
  }

  @ParameterizedTest(name = "{0}")
  @MethodSource("provideDateExtractionExceptionTestData")
  void extractDate_throwException(String testName, String methodName, String expectedErrorMessage) {
    // Arrange
    var reservations = createReservationWithoutDates();

    // Act
    var exception = assertThrows(PaymentMethodsException.class,
        () -> ReflectionTestUtils.invokeMethod(underTest, methodName, reservations));

    // Assert
    String actualMessage = exception.getMessage();
    assertTrue(actualMessage.contains(expectedErrorMessage));
  }

  private static Stream<Arguments> provideDateExtractionExceptionTestData() {
    return Stream.of(
        Arguments.of("Extract Departure Date Exception", "departureDate",
            "Error while extracting the departure date for reservation"),
        Arguments.of("Extract Arrival Date Exception", "arrivalDate",
            "Error while extracting the arrivalDate for reservation")
    );
  }

  @Test
  void extractPaymentPolicy_throwException() {
    // Arrange
    var reservations = createReservationWithErrorPolicy(DEPARTURE_DATE, ARRIVAL_DATE);
    var errorMessage = String.format("Error while getting payment policies. Unsupported policy code %s",
        reservations.getPolicyCode());
    when(reservationClient.findReservations(BASKET_REFERENCE)).thenReturn(reservations);

    // Act
    var exception = assertThrows(PaymentMethodsException.class, () ->
        underTest.findReservations(BASKET_REFERENCE, false));

    // Assert
    String actualMessage = exception.getMessage();
    assertTrue(actualMessage.contains(errorMessage));
  }

  @ParameterizedTest(name = "{0}")
  @MethodSource("provideBasketReservationTestData")
  void findBasketReservations_withVariousScenarios(String testName,
      ReservationListDto reservationsDto,
      String expectedPaymentMethod,
      String expectedHotelId,
      BigDecimal expectedTotalCost,
      BigDecimal expectedOutstandingBalance,
      String expectedCurrencyCode,
      int expectedRateInfoListSize) {
    // Arrange
    given(reservationClient.findReservations(BASKET_REFERENCE)).willReturn(reservationsDto);

    // Act
    var result = underTest.findBasketReservations(BASKET_REFERENCE);

    // Assert
    assertThat(result, notNullValue());
    assertThat(result.getHotelId(), equalTo(expectedHotelId));
    assertThat(result.getTotalCost(), equalTo(expectedTotalCost));
    assertThat(result.getOutstandingBalance(), equalTo(expectedOutstandingBalance));
    assertThat(result.getCurrencyCode(), equalTo(expectedCurrencyCode));
    assertThat(result.getRateInfoList(), notNullValue());
    assertThat(result.getRateInfoList(), hasSize(expectedRateInfoListSize));
    if (expectedPaymentMethod != null) {
      assertEquals(expectedPaymentMethod, result.getPaymentMethod());
    } else {
      assertNull(result.getPaymentMethod());
    }
  }

  private static Stream<Arguments> provideBasketReservationTestData() {
    return Stream.of(
        Arguments.of(
            "With Payment Method - CREDIT_CARD",
            ReservationTestDataBuilder.createReservationWithPaymentMethod("CREDIT_CARD"),
            "CREDIT_CARD",
            "HOTEL123",
            BigDecimal.valueOf(100.00),
            BigDecimal.valueOf(50.00),
            "GBP",
            1
        ),
        Arguments.of(
            "With Null Payment Card",
            ReservationTestDataBuilder.createReservationWithoutPaymentCard(),
            null,
            "HOTEL123",
            BigDecimal.valueOf(100.00),
            BigDecimal.valueOf(50.00),
            "GBP",
            1
        ),
        Arguments.of(
            "With Payment Card But Null Payment Method",
            ReservationTestDataBuilder.createReservationWithPaymentCardButNullPaymentMethod(),
            null,
            "HOTEL123",
            BigDecimal.valueOf(100.00),
            BigDecimal.valueOf(50.00),
            "GBP",
            1
        ),
        Arguments.of(
            "With Multiple Reservations - First Payment Method",
            ReservationTestDataBuilder.createReservationWithMultipleReservations(),
            "DEBIT_CARD",
            "HOTEL123",
            BigDecimal.valueOf(100.00),
            BigDecimal.valueOf(50.00),
            "GBP",
            2
        ),
        Arguments.of(
            "With Null RateInfo",
            ReservationTestDataBuilder.createReservationWithNullRateInfo(),
            "CREDIT_CARD",
            "HOTEL123",
            BigDecimal.valueOf(100.00),
            BigDecimal.valueOf(50.00),
            "GBP",
            0
        ),
        Arguments.of(
            "With Null RateInfo Summary",
            ReservationTestDataBuilder.createReservationWithNullRateInfoSummary(),
            "CREDIT_CARD",
            "HOTEL123",
            BigDecimal.valueOf(100.00),
            BigDecimal.valueOf(50.00),
            "GBP",
            0
        ),
        Arguments.of(
            "Complete Reservation with Multiple Rooms",
            ReservationTestDataBuilder.createCompleteReservation(),
            "VIRTUAL_CARD",
            "HOTEL456",
            BigDecimal.valueOf(200.00),
            BigDecimal.valueOf(100.00),
            "EUR",
            2
        )
    );
  }

  @Test
  void getDepositFolios_withMultipleReservationIds_mergesDepositsAndMapsResult() {
    List<String> reservationIds = List.of("RES-1", "RES-2");

    var response1 = new DepositsResponseDto(List.of(new DepositsDto("pay-1", null)));
    var response2 = new DepositsResponseDto(List.of(
        new DepositsDto("pay-2", null),
        new DepositsDto("pay-3", null)));
    var mappedResult = new DepositsResponse(Collections.emptyList());

    when(reservationClient.getDepositFolios(HOTEL_ID, "RES-1")).thenReturn(response1);
    when(reservationClient.getDepositFolios(HOTEL_ID, "RES-2")).thenReturn(response2);
    when(depositFolioMapper.toDepositFolioModel(org.mockito.ArgumentMatchers.any(DepositsResponseDto.class)))
        .thenReturn(mappedResult);

    var result = underTest.getDepositFolios(HOTEL_ID, reservationIds);

    assertThat(result, equalTo(mappedResult));
    verify(reservationClient).getDepositFolios(HOTEL_ID, "RES-1");
    verify(reservationClient).getDepositFolios(HOTEL_ID, "RES-2");

    ArgumentCaptor<DepositsResponseDto> captor = ArgumentCaptor.forClass(DepositsResponseDto.class);
    verify(depositFolioMapper).toDepositFolioModel(captor.capture());
    assertThat(captor.getValue().getDeposits(), hasSize(3));
    assertThat(
        captor.getValue().getDeposits().stream().map(DepositsDto::getPaymentReference).toList(),
        containsInAnyOrder("pay-1", "pay-2", "pay-3"));
  }

  @Test
  void getDepositFolios_withNullAndEmptyResponses_mapsEmptyDepositsList() {
    List<String> reservationIds = List.of("RES-1", "RES-2");

    when(reservationClient.getDepositFolios(HOTEL_ID, "RES-1")).thenReturn(null);
    when(reservationClient.getDepositFolios(HOTEL_ID, "RES-2"))
        .thenReturn(new DepositsResponseDto(null));
    when(depositFolioMapper.toDepositFolioModel(org.mockito.ArgumentMatchers.any(DepositsResponseDto.class)))
        .thenReturn(new DepositsResponse(Collections.emptyList()));

    underTest.getDepositFolios(HOTEL_ID, reservationIds);

    ArgumentCaptor<DepositsResponseDto> captor = ArgumentCaptor.forClass(DepositsResponseDto.class);
    verify(depositFolioMapper).toDepositFolioModel(captor.capture());
    assertThat(captor.getValue().getDeposits(), hasSize(0));
  }


}
