package uk.co.whitbread.reservation.domain.logic.utils;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;
import java.util.stream.Stream;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.co.whitbread.reservation.domain.model.in.BookingChannel;
import uk.co.whitbread.reservation.domain.model.in.FindBookingRequest;
import uk.co.whitbread.reservation.domain.model.out.BillingResponse;
import uk.co.whitbread.reservation.domain.model.out.ReservationByBasketRefResponse;
import uk.co.whitbread.reservation.domain.model.out.ReservationByIdResponse;
import uk.co.whitbread.reservation.domain.model.out.ReservationGuest;
import uk.co.whitbread.reservation.domain.model.out.ReservationInfo;
import uk.co.whitbread.reservation.domain.model.out.Reservations;
import uk.co.whitbread.reservation.domain.model.out.ReservationsDetailsEnhancedResponse;
import uk.co.whitbread.reservation.domain.model.out.RoomStay;
import uk.co.whitbread.reservation.domain.model.out.RoomStayByIdResponse;

@ExtendWith(MockitoExtension.class)
class ManageBookingUtilsTest {

  @ParameterizedTest
  @MethodSource("otaBookingProvider")
  void isOtaBooking_test(BookingChannel bookingChannel, final String idContext,
      final Set<String> providers, final Set<String> defSubchannel,
      final boolean isAcceptsOtaBooking, final boolean expected) {
    var current = ManageBookingUtils.isOtaBooking(bookingChannel, idContext, providers,
        defSubchannel,
        isAcceptsOtaBooking);
    assertEquals(expected, current);
  }

  @ParameterizedTest(name = "{0}")
  @MethodSource("provideRequestMatchesResDetailsTestCases")
  void requestMatchesResDetails_shouldBypassMatchesOpera_test(
      String testName,
      ReservationsDetailsEnhancedResponse operaReservation,
      boolean shouldBypassMatchesOpera,
      boolean expectedResult) {

    // Arrange
    FindBookingRequest findBookingRequest = FindBookingRequest.builder()
            .lastName("Smith")
            .arrivalDate("2024-01-15")
            .build();

    // Act
    boolean result = ManageBookingUtils.requestMatchesResDetails(
            findBookingRequest, operaReservation, shouldBypassMatchesOpera);

    // Assert
    assertEquals(expectedResult, result, testName);
  }

  @ParameterizedTest(name = "{0}")
  @MethodSource("provideRequestMatchesOperaResDetailsTestCases")
  void requestMatchesOperaResDetails_shouldBypassMatchesOpera_test(
      String testName,
      ReservationByBasketRefResponse operaReservations,
      boolean shouldBypassMatchesOpera,
      boolean expectedResult) {

    // Arrange
    FindBookingRequest findBookingRequest = FindBookingRequest.builder()
        .lastName("Smith")
        .arrivalDate("2026-01-15")
        .build();

    // Act
    boolean result = ManageBookingUtils.requestMatchesOperaResDetails(
        findBookingRequest, operaReservations, shouldBypassMatchesOpera);

    // Assert
    assertEquals(expectedResult, result, testName);
  }

  private static Stream<Arguments> provideRequestMatchesOperaResDetailsTestCases() {
    return Stream.of(
        // shouldBypassMatchesOpera = true scenarios
        Arguments.of(
            "Should return true when bypass enabled and valid reservation exists",
            createValidOperaReservationByBasketRef("Jones", "2024-01-20"),
            true,
            true
        ),
        Arguments.of(
            "Should return true when bypass enabled even with mismatched lastName",
            createValidOperaReservationByBasketRef("DifferentName", "2026-01-15"),
            true,
            true
        ),
        Arguments.of(
            "Should return true when bypass enabled even with mismatched arrivalDate",
            createValidOperaReservationByBasketRef("Smith", "2024-02-01"),
            true,
            true
        ),

        // shouldBypassMatchesOpera = true but null/empty reservation
        Arguments.of(
            "Should return false when bypass enabled but reservationByIdList is null",
            createOperaReservationByBasketRefWithNullList(),
            true,
            false
        ),
        Arguments.of(
            "Should return false when bypass enabled but reservationByIdList is empty",
            createOperaReservationByBasketRefWithEmptyList(),
            true,
            false
        ),

        // shouldBypassMatchesOpera = false scenarios
        Arguments.of(
            "Should return false when bypass disabled and lastName does not match",
            createValidOperaReservationByBasketRef("DifferentName", "2026-01-15"),
            false,
            false
        ),
        Arguments.of(
            "Should return false when bypass disabled and arrivalDate does not match",
            createValidOperaReservationByBasketRef("Smith", "2024-02-01"),
            false,
            false
        ),
        Arguments.of(
            "Should return true when bypass disabled and both lastName and arrivalDate match",
            createValidOperaReservationByBasketRef("Smith", "2026-01-15"),
            false,
            true
        )
    );
  }

  private static ReservationByBasketRefResponse createValidOperaReservationByBasketRef(
      String lastName, String arrivalDate) {
    var billing = BillingResponse.builder()
        .lastName(lastName)
        .build();
    var roomStay = RoomStayByIdResponse.builder()
        .arrivalDate(arrivalDate)
        .build();
    var reservationById = ReservationByIdResponse.builder()
        .billing(billing)
        .roomStay(roomStay)
        .reservationGuestList(List.of())
        .build();
    return ReservationByBasketRefResponse.builder()
        .reservationByIdList(List.of(reservationById))
        .build();
  }

  private static ReservationByBasketRefResponse createOperaReservationByBasketRefWithNullList() {
    return ReservationByBasketRefResponse.builder()
        .reservationByIdList(null)
        .build();
  }

  private static ReservationByBasketRefResponse createOperaReservationByBasketRefWithEmptyList() {
    return ReservationByBasketRefResponse.builder()
        .reservationByIdList(List.of())
        .build();
  }

  private static Stream<Arguments> provideRequestMatchesResDetailsTestCases() {
      return Stream.of(
          // shouldBypassMatchesOpera = true scenarios
          Arguments.of(
                  "Should return true when bypass enabled and valid reservation exists",
                  createValidOperaReservation("Jones", "2024-01-20"),
                  true,
                  true
          ),
          Arguments.of(
                  "Should return true when bypass enabled even with mismatched lastName",
                  createValidOperaReservation("DifferentName", "2024-01-15"),
                  true,
                  true
          ),
          Arguments.of(
                  "Should return true when bypass enabled even with mismatched arrivalDate",
                  createValidOperaReservation("Smith", "2024-02-01"),
                  true,
                  true
          ),

          // shouldBypassMatchesOpera = true but null/empty reservation
          Arguments.of(
                  "Should return false when bypass enabled but operaReservation is null",
                  null,
                  true,
                  false
          ),
          Arguments.of(
                  "Should return false when bypass enabled but reservations is null",
                  createOperaReservationWithNullReservations(),
                  true,
                  false
          ),
          Arguments.of(
                  "Should return false when bypass enabled but reservationInfo is null",
                  createOperaReservationWithNullReservationInfo(),
                  true,
                  false
          ),
          Arguments.of(
                  "Should return false when bypass enabled but reservationInfo is empty",
                  createOperaReservationWithEmptyReservationInfo(),
                  true,
                  false
          ),

          // shouldBypassMatchesOpera = false scenarios (validates lastName and arrivalDate)
          Arguments.of(
                  "Should return false when bypass disabled and lastName does not match",
                  createValidOperaReservation("DifferentName", "2024-01-15"),
                  false,
                  false
          ),
          Arguments.of(
                  "Should return false when bypass disabled and arrivalDate does not match",
                  createValidOperaReservation("Smith", "2024-02-01"),
                  false,
                  false
          ),
          Arguments.of(
                  "Should return true when bypass disabled and both lastName and arrivalDate match",
                  createValidOperaReservation("Smith", "2024-01-15"),
                  false,
                  true
          )
  );
  }

  private static ReservationsDetailsEnhancedResponse createValidOperaReservation(
          String lastName, String arrivalDate) {
      var reservationGuest = ReservationGuest.builder()
              .surname(lastName)
              .build();
      var roomStay = RoomStay.builder()
              .arrivalDate(LocalDate.parse(arrivalDate))
              .build();
      var reservationInfo = ReservationInfo.builder()
              .reservationGuest(reservationGuest)
              .roomStay(roomStay)
              .build();
      var reservations = Reservations.builder()
              .reservationInfo(List.of(reservationInfo))
              .build();
      return ReservationsDetailsEnhancedResponse.builder()
              .reservations(reservations)
              .build();
  }

  private static ReservationsDetailsEnhancedResponse createOperaReservationWithNullReservations() {
      return ReservationsDetailsEnhancedResponse.builder()
              .reservations(null)
              .build();
  }

  private static ReservationsDetailsEnhancedResponse createOperaReservationWithNullReservationInfo() {
      var reservations = Reservations.builder()
              .reservationInfo(null)
              .build();
      return ReservationsDetailsEnhancedResponse.builder()
              .reservations(reservations)
              .build();
  }

  private static ReservationsDetailsEnhancedResponse createOperaReservationWithEmptyReservationInfo() {
      var reservations = Reservations.builder()
              .reservationInfo(List.of())
              .build();
      return ReservationsDetailsEnhancedResponse.builder()
              .reservations(reservations)
              .build();
  }

  static Stream<Arguments> otaBookingProvider() {
    return Stream.of(
        Arguments.of(ManageReservationUtils.mockBookingChannel("PI", "MOBILE"), "BOOKING.COM",
            Set.of("BOOKING.COM"), Set.of("PI.MOBILE"), true, false),
        Arguments.of(ManageReservationUtils.mockBookingChannel("PI", "MOBILE"), "BOOKING.COM",
            Set.of("EXPEDIA"), Set.of("PI.MOBILE"), true, true),
        Arguments.of(ManageReservationUtils.mockBookingChannel("PI", "MOBILE"), "BOOKING.COM",
            Set.of(), Set.of("PI.MOBILE"), true, true),
        Arguments.of(ManageReservationUtils.mockBookingChannel("PI", "MOBILE"), "BOOKING.COM",
            null, Set.of("PI.MOBILE"), true, true),
        Arguments.of(ManageReservationUtils.mockBookingChannel("PI", "MOBILE"), "BOOKING.COM",
            Set.of("EXPEDIA"), Set.of("PI.WEB"), true, false),
        Arguments.of(ManageReservationUtils.mockBookingChannel("PI", "MOBILE"), "BOOKING.COM",
            Set.of("EXPEDIA"), Set.of(), true, false),
        Arguments.of(ManageReservationUtils.mockBookingChannel("PI", "MOBILE"), "BOOKING.COM",
            Set.of("EXPEDIA"), null, true, false),
        Arguments.of(ManageReservationUtils.mockBookingChannel("PI", "MOBILE"), "BOOKING.COM",
            Set.of("EXPEDIA"), Set.of(), false, false)
    );
  }
}

