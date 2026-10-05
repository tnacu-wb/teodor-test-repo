package uk.co.whitbread.payments.infrastructure.rest.client.reservation;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import uk.co.whitbread.payments.infrastructure.rest.client.reservation.model.out.PaymentCardDto;
import uk.co.whitbread.payments.infrastructure.rest.client.reservation.model.out.RateInfoDto;
import uk.co.whitbread.payments.infrastructure.rest.client.reservation.model.out.RateInfoSummaryDto;
import uk.co.whitbread.payments.infrastructure.rest.client.reservation.model.out.ReservationDto;
import uk.co.whitbread.payments.infrastructure.rest.client.reservation.model.out.ReservationListDto;
import uk.co.whitbread.payments.infrastructure.rest.client.reservation.model.out.RoomStayDto;

/**
 * Utility class for building test data for ReservationsPortImpl tests.
 * Contains static factory methods for creating various ReservationListDto instances
 * with different configurations for testing purposes.
 */
public final class ReservationTestDataBuilder {

  private ReservationTestDataBuilder() {
    // Utility class - prevent instantiation
  }

  /**
   * Creates a ReservationListDto with the specified policy code and standard dates.
   *
   * @param policyCode the policy code to set (e.g., "OA", "NL", "RWC")
   * @param departureDate the departure date
   * @param arrivalDate the arrival date
   * @return configured ReservationListDto
   */
  public static ReservationListDto createReservationWithPolicy(String policyCode,
      LocalDate departureDate, LocalDate arrivalDate) {
    var roomStay = new RoomStayDto();
    roomStay.setDepartureDate(departureDate.toString());
    roomStay.setArrivalDate(arrivalDate.toString());

    var reservationDto = new ReservationDto();
    reservationDto.setRoomStay(roomStay);

    var dto = new ReservationListDto();
    dto.setPolicyCode(policyCode);
    dto.setReservationByIdList(List.of(reservationDto));
    return dto;
  }

  /**
   * Creates a ReservationListDto with an invalid policy code for error testing.
   *
   * @param departureDate the departure date
   * @param arrivalDate the arrival date
   * @return ReservationListDto with invalid policy code "ABCD"
   */
  public static ReservationListDto createReservationWithErrorPolicy(LocalDate departureDate,
      LocalDate arrivalDate) {
    return createReservationWithPolicy("ABCD", departureDate, arrivalDate);
  }

  /**
   * Creates a ReservationListDto without departure and arrival dates.
   * Used for testing date extraction exception scenarios.
   *
   * @return ReservationListDto with no dates
   */
  public static ReservationListDto createReservationWithoutDates() {
    var reservationDto = new ReservationDto();

    var dto = new ReservationListDto();
    dto.setReservationByIdList(List.of(reservationDto));
    return dto;
  }

  /**
   * Creates a ReservationListDto with a payment card containing the specified payment method.
   *
   * @param paymentMethod the payment method (e.g., "CREDIT_CARD", "DEBIT_CARD")
   * @return ReservationListDto with payment card and rate info
   */
  public static ReservationListDto createReservationWithPaymentMethod(String paymentMethod) {
    var paymentCard = PaymentCardDto.builder()
        .paymentMethod(paymentMethod)
        .cardHolderName("John Doe")
        .cardNumberMasked("****1234")
        .build();

    var rateInfoSummary = RateInfoSummaryDto.builder()
        .guestPay(BigDecimal.valueOf(30.00))
        .routing(BigDecimal.valueOf(70.00))
        .currencyCode("GBP")
        .build();

    var rateInfo = new RateInfoDto();
    rateInfo.setSummary(rateInfoSummary);

    var reservationDto = new ReservationDto();
    reservationDto.setReservationId("RES001");
    reservationDto.setPaymentCard(paymentCard);
    reservationDto.setRateInfo(rateInfo);

    var roomStay = new RoomStayDto();
    roomStay.setDepartureDate(LocalDate.now().toString());
    reservationDto.setRoomStay(roomStay);

    var dto = new ReservationListDto();
    dto.setHotelId("HOTEL123");
    dto.setTotalCost(BigDecimal.valueOf(100.00));
    dto.setBalanceOutstanding(BigDecimal.valueOf(50.00));
    dto.setCurrencyCode("GBP");
    dto.setPolicyCode("OA");
    dto.setReservationByIdList(List.of(reservationDto));
    return dto;
  }

  /**
   * Creates a ReservationListDto without a payment card.
   *
   * @return ReservationListDto with null payment card
   */
  public static ReservationListDto createReservationWithoutPaymentCard() {
    var rateInfoSummary = RateInfoSummaryDto.builder()
        .guestPay(BigDecimal.valueOf(30.00))
        .routing(BigDecimal.valueOf(70.00))
        .currencyCode("GBP")
        .build();

    var rateInfo = new RateInfoDto();
    rateInfo.setSummary(rateInfoSummary);

    var reservationDto = new ReservationDto();
    reservationDto.setReservationId("RES001");
    reservationDto.setPaymentCard(null);
    reservationDto.setRateInfo(rateInfo);

    var roomStay = new RoomStayDto();
    roomStay.setDepartureDate(LocalDate.now().toString());
    reservationDto.setRoomStay(roomStay);

    var dto = new ReservationListDto();
    dto.setHotelId("HOTEL123");
    dto.setTotalCost(BigDecimal.valueOf(100.00));
    dto.setBalanceOutstanding(BigDecimal.valueOf(50.00));
    dto.setCurrencyCode("GBP");
    dto.setPolicyCode("OA");
    dto.setReservationByIdList(List.of(reservationDto));
    return dto;
  }

  /**
   * Creates a ReservationListDto with a payment card but null payment method.
   *
   * @return ReservationListDto with payment card containing null payment method
   */
  public static ReservationListDto createReservationWithPaymentCardButNullPaymentMethod() {
    var paymentCard = PaymentCardDto.builder()
        .paymentMethod(null)
        .cardHolderName("John Doe")
        .cardNumberMasked("****1234")
        .build();

    var rateInfoSummary = RateInfoSummaryDto.builder()
        .guestPay(BigDecimal.valueOf(30.00))
        .routing(BigDecimal.valueOf(70.00))
        .currencyCode("GBP")
        .build();

    var rateInfo = new RateInfoDto();
    rateInfo.setSummary(rateInfoSummary);

    var reservationDto = new ReservationDto();
    reservationDto.setReservationId("RES001");
    reservationDto.setPaymentCard(paymentCard);
    reservationDto.setRateInfo(rateInfo);

    var roomStay = new RoomStayDto();
    roomStay.setDepartureDate(LocalDate.now().toString());
    reservationDto.setRoomStay(roomStay);

    var dto = new ReservationListDto();
    dto.setHotelId("HOTEL123");
    dto.setTotalCost(BigDecimal.valueOf(100.00));
    dto.setBalanceOutstanding(BigDecimal.valueOf(50.00));
    dto.setCurrencyCode("GBP");
    dto.setPolicyCode("OA");
    dto.setReservationByIdList(List.of(reservationDto));
    return dto;
  }

  /**
   * Creates a ReservationListDto with multiple reservations.
   * First reservation has no payment card, second has a DEBIT_CARD payment method.
   *
   * @return ReservationListDto with two reservations
   */
  public static ReservationListDto createReservationWithMultipleReservations() {
    // First reservation without payment card
    var rateInfoSummary1 = RateInfoSummaryDto.builder()
        .guestPay(BigDecimal.valueOf(30.00))
        .routing(BigDecimal.valueOf(70.00))
        .currencyCode("GBP")
        .build();

    var rateInfo1 = new RateInfoDto();
    rateInfo1.setSummary(rateInfoSummary1);

    var reservationDto1 = new ReservationDto();
    reservationDto1.setReservationId("RES001");
    reservationDto1.setPaymentCard(null);
    reservationDto1.setRateInfo(rateInfo1);

    var roomStay = new RoomStayDto();
    roomStay.setDepartureDate(LocalDate.now().toString());
    reservationDto1.setRoomStay(roomStay);



    // Second reservation with payment card
    var paymentCard2 = PaymentCardDto.builder()
        .paymentMethod("DEBIT_CARD")
        .cardHolderName("Jane Smith")
        .cardNumberMasked("****5678")
        .build();

    var rateInfoSummary2 = RateInfoSummaryDto.builder()
        .guestPay(BigDecimal.valueOf(40.00))
        .routing(BigDecimal.valueOf(60.00))
        .currencyCode("GBP")
        .build();

    var rateInfo2 = new RateInfoDto();
    rateInfo2.setSummary(rateInfoSummary2);

    var reservationDto2 = new ReservationDto();
    reservationDto2.setReservationId("RES002");
    reservationDto2.setPaymentCard(paymentCard2);
    reservationDto2.setRateInfo(rateInfo2);

    var roomStay1 = new RoomStayDto();
    roomStay1.setDepartureDate(LocalDate.now().toString());
    reservationDto2.setRoomStay(roomStay1);


    var dto = new ReservationListDto();
    dto.setHotelId("HOTEL123");
    dto.setTotalCost(BigDecimal.valueOf(100.00));
    dto.setBalanceOutstanding(BigDecimal.valueOf(50.00));
    dto.setCurrencyCode("GBP");
    dto.setPolicyCode("OA");
    dto.setReservationByIdList(List.of(reservationDto1, reservationDto2));
    return dto;
  }

  /**
   * Creates a ReservationListDto with null RateInfo.
   * Used for testing rate info filtering logic.
   *
   * @return ReservationListDto with null rate info
   */
  public static ReservationListDto createReservationWithNullRateInfo() {
    var paymentCard = PaymentCardDto.builder()
        .paymentMethod("CREDIT_CARD")
        .cardHolderName("John Doe")
        .cardNumberMasked("****1234")
        .build();

    var reservationDto = new ReservationDto();
    reservationDto.setReservationId("RES001");
    reservationDto.setPaymentCard(paymentCard);
    reservationDto.setRateInfo(null);

    var roomStay = new RoomStayDto();
    roomStay.setDepartureDate(LocalDate.now().toString());
    reservationDto.setRoomStay(roomStay);


    var dto = new ReservationListDto();
    dto.setHotelId("HOTEL123");
    dto.setTotalCost(BigDecimal.valueOf(100.00));
    dto.setBalanceOutstanding(BigDecimal.valueOf(50.00));
    dto.setCurrencyCode("GBP");
    dto.setPolicyCode("OA");
    dto.setReservationByIdList(List.of(reservationDto));
    return dto;
  }

  /**
   * Creates a ReservationListDto with null RateInfo summary.
   * Used for testing rate info summary filtering logic.
   *
   * @return ReservationListDto with null rate info summary
   */
  public static ReservationListDto createReservationWithNullRateInfoSummary() {
    var paymentCard = PaymentCardDto.builder()
        .paymentMethod("CREDIT_CARD")
        .cardHolderName("John Doe")
        .cardNumberMasked("****1234")
        .build();

    var rateInfo = new RateInfoDto();
    rateInfo.setSummary(null);

    var reservationDto = new ReservationDto();
    reservationDto.setReservationId("RES001");
    reservationDto.setPaymentCard(paymentCard);
    reservationDto.setRateInfo(rateInfo);

    var roomStay = new RoomStayDto();
    roomStay.setDepartureDate(LocalDate.now().toString());
    reservationDto.setRoomStay(roomStay);


    var dto = new ReservationListDto();
    dto.setHotelId("HOTEL123");
    dto.setTotalCost(BigDecimal.valueOf(100.00));
    dto.setBalanceOutstanding(BigDecimal.valueOf(50.00));
    dto.setCurrencyCode("GBP");
    dto.setPolicyCode("OA");
    dto.setReservationByIdList(List.of(reservationDto));
    return dto;
  }

  /**
   * Creates a complete ReservationListDto with multiple rooms and full details.
   * Contains two reservations with different payment methods and rate info.
   *
   * @return Complete ReservationListDto with two reservations
   */
  public static ReservationListDto createCompleteReservation() {
    // First reservation
    var paymentCard1 = PaymentCardDto.builder()
        .paymentMethod("VIRTUAL_CARD")
        .cardHolderName("John Doe")
        .cardNumberMasked("****1234")
        .build();

    var rateInfoSummary1 = RateInfoSummaryDto.builder()
        .guestPay(BigDecimal.valueOf(50.00))
        .routing(BigDecimal.valueOf(50.00))
        .currencyCode("EUR")
        .build();

    var rateInfo1 = new RateInfoDto();
    rateInfo1.setSummary(rateInfoSummary1);

    var reservationDto1 = new ReservationDto();
    reservationDto1.setReservationId("RES001");
    reservationDto1.setPaymentCard(paymentCard1);
    reservationDto1.setRateInfo(rateInfo1);

    var roomStay = new RoomStayDto();
    roomStay.setDepartureDate(LocalDate.now().toString());
    reservationDto1.setRoomStay(roomStay);


    // Second reservation
    var paymentCard2 = PaymentCardDto.builder()
        .paymentMethod("CREDIT_CARD")
        .cardHolderName("Jane Smith")
        .cardNumberMasked("****5678")
        .build();

    var rateInfoSummary2 = RateInfoSummaryDto.builder()
        .guestPay(BigDecimal.valueOf(60.00))
        .routing(BigDecimal.valueOf(40.00))
        .currencyCode("EUR")
        .build();

    var rateInfo2 = new RateInfoDto();
    rateInfo2.setSummary(rateInfoSummary2);

    var reservationDto2 = new ReservationDto();
    reservationDto2.setReservationId("RES002");
    reservationDto2.setPaymentCard(paymentCard2);
    reservationDto2.setRateInfo(rateInfo2);

    var roomStay2 = new RoomStayDto();
    roomStay2.setDepartureDate(LocalDate.now().toString());
    reservationDto2.setRoomStay(roomStay2);


    var dto = new ReservationListDto();
    dto.setHotelId("HOTEL456");
    dto.setTotalCost(BigDecimal.valueOf(200.00));
    dto.setBalanceOutstanding(BigDecimal.valueOf(100.00));
    dto.setCurrencyCode("EUR");
    dto.setPolicyCode("OA");
    dto.setReservationByIdList(List.of(reservationDto1, reservationDto2));
    return dto;
  }
}

