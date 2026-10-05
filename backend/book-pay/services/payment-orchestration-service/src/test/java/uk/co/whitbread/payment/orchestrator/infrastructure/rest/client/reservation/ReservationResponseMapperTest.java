package uk.co.whitbread.payment.orchestrator.infrastructure.rest.client.reservation;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import uk.co.whitbread.payment.orchestrator.domain.exceptions.BasketNotFoundException;
import uk.co.whitbread.payment.orchestrator.domain.model.Reservation;
import uk.co.whitbread.payment.orchestrator.infrastructure.rest.client.reservation.dto.RateInfoDto;
import uk.co.whitbread.payment.orchestrator.infrastructure.rest.client.reservation.dto.RateInfoSummaryDto;
import uk.co.whitbread.payment.orchestrator.infrastructure.rest.client.reservation.dto.ReservationByBasketResponse;
import uk.co.whitbread.payment.orchestrator.infrastructure.rest.client.reservation.dto.ReservationByIdDto;

class ReservationResponseMapperTest {

  private ReservationResponseMapper mapper;

  private static final String BASKET_ID = "bsk-a1b2c3d4";
  private static final String BOOKING_REFERENCE = "PI-123456789";
  private static final String HOTEL_ID = "LONWAT";
  private static final String CURRENCY_CODE = "GBP";
  private static final BigDecimal TOTAL_COST_OF_STAY = new BigDecimal("89.00");

  @BeforeEach
  void setUp() {
    mapper = new ReservationResponseMapper() {};
  }

  @Nested
  class SuccessfulMapping {

    @Test
    void mapsFullResponseToReservationDomainModel() {
      ReservationByBasketResponse response = buildValidResponse();

      Reservation result = mapper.toDomain(BASKET_ID, response);

      assertThat(result.basketId()).isEqualTo(BASKET_ID);
      assertThat(result.hotelId()).isEqualTo(HOTEL_ID);
      assertThat(result.totalCostOfStay()).isEqualByComparingTo(TOTAL_COST_OF_STAY);
      assertThat(result.currencyCode()).isEqualTo(CURRENCY_CODE);
      assertThat(result.bookingReference()).isEqualTo(BOOKING_REFERENCE);
      assertThat(result.refno()).isEqualTo(BOOKING_REFERENCE);
    }

    @Test
    void extractsHotelIdFromTopLevelResponse() {
      String expectedHotelId = "MANPIC";
      ReservationByBasketResponse response = new ReservationByBasketResponse(
          List.of(buildReservationByIdDto(TOTAL_COST_OF_STAY, CURRENCY_CODE)),
          BOOKING_REFERENCE,
          BASKET_ID,
          expectedHotelId,
          CURRENCY_CODE,
          TOTAL_COST_OF_STAY,
          "RESERVED",
          "FULL_PAYMENT",
          "PI"
      );

      Reservation result = mapper.toDomain(BASKET_ID, response);

      assertThat(result.hotelId()).isEqualTo(expectedHotelId);
    }

    @Test
    void setsBookingReferenceAndRefnoBothToResponseBookingReference() {
      ReservationByBasketResponse response = buildValidResponse();

      Reservation result = mapper.toDomain(BASKET_ID, response);

      assertThat(result.bookingReference()).isEqualTo(BOOKING_REFERENCE);
      assertThat(result.refno()).isEqualTo(BOOKING_REFERENCE);
      assertThat(result.bookingReference()).isEqualTo(result.refno());
    }

    @Test
    void extractsTotalCostOfStayFromFirstReservationRateInfoSummary() {
      BigDecimal expectedAmount = new BigDecimal("149.50");
      ReservationByBasketResponse response = new ReservationByBasketResponse(
          List.of(buildReservationByIdDto(expectedAmount, CURRENCY_CODE)),
          BOOKING_REFERENCE,
          BASKET_ID,
          HOTEL_ID,
          CURRENCY_CODE,
          expectedAmount,
          "RESERVED",
          "FULL_PAYMENT",
          "PI"
      );

      Reservation result = mapper.toDomain(BASKET_ID, response);

      assertThat(result.totalCostOfStay()).isEqualByComparingTo(expectedAmount);
    }

    @Test
    void extractsCurrencyCodeFromFirstReservationRateInfoSummary() {
      String expectedCurrency = "EUR";
      ReservationByBasketResponse response = new ReservationByBasketResponse(
          List.of(buildReservationByIdDto(TOTAL_COST_OF_STAY, expectedCurrency)),
          BOOKING_REFERENCE,
          BASKET_ID,
          HOTEL_ID,
          expectedCurrency,
          TOTAL_COST_OF_STAY,
          "RESERVED",
          "FULL_PAYMENT",
          "PI"
      );

      Reservation result = mapper.toDomain(BASKET_ID, response);

      assertThat(result.currencyCode()).isEqualTo(expectedCurrency);
    }

    @Test
    void usesOnlyFirstReservationWhenMultipleReservationsPresent() {
      BigDecimal firstAmount = new BigDecimal("89.00");
      BigDecimal secondAmount = new BigDecimal("120.00");
      String firstCurrency = "GBP";
      String secondCurrency = "EUR";

      ReservationByIdDto firstReservation = buildReservationByIdDto(firstAmount, firstCurrency);
      ReservationByIdDto secondReservation = buildReservationByIdDto(secondAmount, secondCurrency);

      ReservationByBasketResponse response = new ReservationByBasketResponse(
          List.of(firstReservation, secondReservation),
          BOOKING_REFERENCE,
          BASKET_ID,
          HOTEL_ID,
          firstCurrency,
          firstAmount,
          "RESERVED",
          "FULL_PAYMENT",
          "PI"
      );

      Reservation result = mapper.toDomain(BASKET_ID, response);

      assertThat(result.totalCostOfStay()).isEqualByComparingTo(firstAmount);
      assertThat(result.currencyCode()).isEqualTo(firstCurrency);
    }
  }

  @Nested
  class EmptyOrNullReservationList {

    @Test
    void throwsBasketNotFoundExceptionWhenReservationByIdListIsEmpty() {
      ReservationByBasketResponse response = new ReservationByBasketResponse(
          Collections.emptyList(),
          BOOKING_REFERENCE,
          BASKET_ID,
          HOTEL_ID,
          CURRENCY_CODE,
          TOTAL_COST_OF_STAY,
          "RESERVED",
          "FULL_PAYMENT",
          "PI"
      );

      assertThatThrownBy(() -> mapper.toDomain(BASKET_ID, response))
          .isInstanceOf(BasketNotFoundException.class)
          .hasMessageContaining(BASKET_ID);
    }

    @Test
    void throwsBasketNotFoundExceptionWhenReservationByIdListIsNull() {
      ReservationByBasketResponse response = new ReservationByBasketResponse(
          null,
          BOOKING_REFERENCE,
          BASKET_ID,
          HOTEL_ID,
          CURRENCY_CODE,
          TOTAL_COST_OF_STAY,
          "RESERVED",
          "FULL_PAYMENT",
          "PI"
      );

      assertThatThrownBy(() -> mapper.toDomain(BASKET_ID, response))
          .isInstanceOf(BasketNotFoundException.class)
          .hasMessageContaining(BASKET_ID);
    }
  }

  private ReservationByBasketResponse buildValidResponse() {
    return new ReservationByBasketResponse(
        List.of(buildReservationByIdDto(TOTAL_COST_OF_STAY, CURRENCY_CODE)),
        BOOKING_REFERENCE,
        BASKET_ID,
        HOTEL_ID,
        CURRENCY_CODE,
        TOTAL_COST_OF_STAY,
        "RESERVED",
        "FULL_PAYMENT",
          "PI"
    );
  }

  private ReservationByIdDto buildReservationByIdDto(BigDecimal totalCostOfStay,
      String currencyCode) {
    RateInfoSummaryDto summary = new RateInfoSummaryDto(
        totalCostOfStay,
        currencyCode,
        totalCostOfStay,
        new BigDecimal("74.17"),
        null,
        null,
        null
    );
    RateInfoDto rateInfo = new RateInfoDto(summary);
    return new ReservationByIdDto("RES-001", rateInfo, "RESERVED");
  }
}
