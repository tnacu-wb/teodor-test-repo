package uk.co.whitbread.wallet.infrastructure.rest.client.reservations;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrowsExactly;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static uk.co.whitbread.wallet.ErrorCode.DIGITAL_BOOKING_NOT_FOUND_FOR_RESERVATION_EXCEPTION;
import static uk.co.whitbread.wallet.ErrorCode.DIGITAL_DETAILS_NOT_FOUND_FOR_BASKET_EXCEPTION;

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
import org.mockito.junit.jupiter.MockitoExtension;
import uk.co.whitbread.hotel.generated.models.reservation.FindBookingResponseDto;
import uk.co.whitbread.hotel.generated.models.reservation.ReservationByBasketRefResponseDto;
import uk.co.whitbread.hotel.generated.models.reservation.ReservationByIdDto;
import uk.co.whitbread.hotel.generated.models.reservation.ReservationByIdGuestsDto;
import uk.co.whitbread.hotel.generated.models.reservation.ReservationPackagesDetailsResponseDto;
import uk.co.whitbread.hotel.generated.models.reservation.RoomStayByIdDto;
import uk.co.whitbread.wallet.domain.exception.ReservationNotFoundException;
import uk.co.whitbread.wallet.domain.model.in.WalletRequest;
import uk.co.whitbread.wallet.domain.model.out.ReservationDetails;
import uk.co.whitbread.wallet.infrastructure.rest.client.reservations.service.HotelReservationsClient;
import uk.co.whitbread.wallet.infrastructure.rest.client.reservations.service.mapper.ReservationDetailsMapper;

@ExtendWith(MockitoExtension.class)
class HotelReservationOutPortImplTest {

  private static final String REFERENCE = "GAA-cd890646-a941-4ac9-90ba-e7d36211cfce";
  private static final String EARLYCHECKIN = "11:00";
  private static final String CHECKIN = "15:00";
  @InjectMocks
  private HotelReservationOutPortImpl hotelReservationOutPort;

  @Mock
  private HotelReservationsClient hotelReservationsClient;
  @Mock
  private ReservationDetailsMapper reservationDetailsMapper;

  @Test
  void findBookingTest() {
    var basketRequest = new WalletRequest("AKU5411146", "2024-10-01", "Test", "en", "gb", "PI", false);

    var findBookingResponseDto = new FindBookingResponseDto();
    findBookingResponseDto.setBasketReference("reference");

    when(hotelReservationsClient.getBasketReference(any())).thenReturn(findBookingResponseDto);

    var result = hotelReservationOutPort.findBooking(basketRequest);
    assertThat(result.getBasketReference(), is("reference"));
  }

  @Test
  void findBookingThrowsNotFoundException() {
    var basketRequest = new WalletRequest("AKU5411146", "2024-10-01", "Test", "en", "gb", "PI", false);
    when(hotelReservationsClient.getBasketReference(any())).thenReturn(null);

    var result = assertThrowsExactly(ReservationNotFoundException.class,
        () -> hotelReservationOutPort.findBooking(basketRequest));
    assertEquals(DIGITAL_BOOKING_NOT_FOUND_FOR_RESERVATION_EXCEPTION.getCode(),
        result.getErrorCode());
  }

  @ParameterizedTest
  @MethodSource
  void getReservationDetailsTest(List<ReservationByIdDto> reservations, String checkInTime,
      String idContext, ReservationByIdGuestsDto guestsDto) {

    var reservationByBasketRefResponseDto = new ReservationByBasketRefResponseDto();
    reservationByBasketRefResponseDto.setReservationByIdList(reservations);
    reservationByBasketRefResponseDto.setHotelId("FRAMTI");
    reservationByBasketRefResponseDto.setBookingReference("AKU5411146");
    reservationByBasketRefResponseDto.setIdContext(idContext);

    ReservationDetails reservation =  ReservationDetails.builder()
        .checkInTime(
            !reservations.isEmpty() && reservations.get(0).getRoomStay() != null
                ? reservations.get(0).getRoomStay().getCheckInTime() : null)
        .build();

    if ("3rd Party".equals(reservationByBasketRefResponseDto.getIdContext()) && guestsDto != null) {
      var reservationDto = new ReservationByIdDto();
      reservationDto.setReservationGuestList(List.of(guestsDto));
      reservationByBasketRefResponseDto.setReservationByIdList(List.of(reservationDto));
    }else{
      reservation.setTitle("mr");
      reservation.setFirstName("abc");
      reservation.setLastName("def");
    }

    when(hotelReservationsClient.getReservationDetails(any())).thenReturn(
        reservationByBasketRefResponseDto);
    when(reservationDetailsMapper.toModel(any())).thenReturn(reservation);

    var result = hotelReservationOutPort.getReservationDetails(REFERENCE, EARLYCHECKIN);

    assertNotNull(result);
    assertNotNull(result.getConfirmationNumber());
    assertEquals("AKU5411146", result.getConfirmationNumber());
    assertEquals("FRAMTI", result.getHotelId());
    assertEquals(checkInTime, result.getCheckInTime());

    if ("3rd Party".equals(reservationByBasketRefResponseDto.getIdContext()) && guestsDto != null) {
      if (guestsDto.getGivenName() != null) {
        assertEquals(
            guestsDto.getGivenName().substring(0, 1).toUpperCase() + guestsDto.getGivenName()
                .substring(1), result.getTitle());
      }
      if (guestsDto.getSurName() != null) {
        assertEquals(guestsDto.getSurName().substring(0, 1).toUpperCase() + guestsDto.getSurName()
            .substring(1), result.getLastName());
      }
    } else {
      assertEquals("Mr", result.getTitle());
      assertEquals("abc", result.getFirstName());
      assertEquals("Def", result.getLastName());
    }
  }


  @Test
  void getReservationDetailsThrowsNotFoundException() {
    when(hotelReservationsClient.getReservationDetails(any())).thenReturn(null);

    var result = assertThrowsExactly(ReservationNotFoundException.class,
        () -> hotelReservationOutPort.getReservationDetails(REFERENCE, EARLYCHECKIN));
    assertEquals(DIGITAL_DETAILS_NOT_FOUND_FOR_BASKET_EXCEPTION.getCode(), result.getErrorCode());
  }


  @Test
  void getReservationDetailsWithNewReservationTest() {

    var reservationByBasketRefResponseDto = new ReservationByBasketRefResponseDto();
    reservationByBasketRefResponseDto.setReservationByIdList(List.of(new ReservationByIdDto()));

    when(hotelReservationsClient.getReservationDetails(any())).thenReturn(
            reservationByBasketRefResponseDto);
    when(reservationDetailsMapper.toModel(any())).thenReturn(
            ReservationDetails.builder().firstName("John").lastName("Dough").build());

    var result = hotelReservationOutPort.getReservationDetails(REFERENCE, EARLYCHECKIN);
    assertNotNull(result);
   }


  @Test
  void getReservationDetailsTest_skipThirdPartyGuestLogic_whenTitleAndLastNamePresent() {
    var reservationByBasketRefResponseDto = new ReservationByBasketRefResponseDto();
    reservationByBasketRefResponseDto.setReservationByIdList(List.of(new ReservationByIdDto()));
    reservationByBasketRefResponseDto.setHotelId("FRAMTI");
    reservationByBasketRefResponseDto.setBookingReference("AKU5411146");
    reservationByBasketRefResponseDto.setIdContext("3rd Party");
    ReservationDetails detailsWithNames = ReservationDetails.builder()
        .firstName("John")
        .title("Mr")
        .lastName("Smith")
        .build();

    when(hotelReservationsClient.getReservationDetails(any())).thenReturn(
        reservationByBasketRefResponseDto);
    when(reservationDetailsMapper.toModel(any())).thenReturn(detailsWithNames);

    var result = hotelReservationOutPort.getReservationDetails(REFERENCE, EARLYCHECKIN);

    assertNotNull(result);
    assertEquals("Mr", result.getTitle());
    assertEquals("Smith", result.getLastName());
    assertEquals("AKU5411146", result.getConfirmationNumber());
    assertEquals("FRAMTI", result.getHotelId());
  }


  @ParameterizedTest
  @MethodSource("getThirdPartyGuestNullCases")
  void getThirdPartyGuest_returnsNullGuestName(List<ReservationByIdDto> reservationByIdList) {
    var reservationByBasketRefResponseDto = new ReservationByBasketRefResponseDto();
    reservationByBasketRefResponseDto.setReservationByIdList(reservationByIdList);
    reservationByBasketRefResponseDto.setHotelId("FRAMTI");
    reservationByBasketRefResponseDto.setBookingReference("AKU5411146");
    reservationByBasketRefResponseDto.setIdContext("3rd Party");

    ReservationDetails details = ReservationDetails.builder()
        .firstName("John")
        .title(null)
        .lastName(null)
        .build();

    when(hotelReservationsClient.getReservationDetails(any())).thenReturn(
        reservationByBasketRefResponseDto);
    if (reservationByIdList != null) {
      when(reservationDetailsMapper.toModel(any())).thenReturn(details);
    }

    var result = hotelReservationOutPort.getReservationDetails(REFERENCE, EARLYCHECKIN);

    assertNotNull(result);
    assertNull(result.getTitle());
    assertNull(result.getLastName());
  }

  static Stream<Arguments> getReservationDetailsTest() {

    ReservationPackagesDetailsResponseDto packageDto = new ReservationPackagesDetailsResponseDto();
    packageDto.setPackageCode("test");

    ReservationPackagesDetailsResponseDto packageHSCKIN = new ReservationPackagesDetailsResponseDto();
    packageHSCKIN.setPackageCode("HSCKIN");

    return Stream.of(
        Arguments.of(Collections.EMPTY_LIST, null, null, null),
        Arguments.of(List.of(new ReservationByIdDto()), null, null, null),
        Arguments.of(List.of(buildReservation(packageDto)), CHECKIN, "3rd Party",
            buildGuest("Mr", "testFN", "testLN")),
        Arguments.of(List.of(buildReservation(packageDto)), CHECKIN, "3rd Party", null),
        Arguments.of(List.of(buildReservation(packageHSCKIN)), EARLYCHECKIN, null, null, null),
        Arguments.of(List.of(buildReservation(packageHSCKIN), buildReservation(packageDto)),
            CHECKIN, "other", buildGuest("Mr", "testFN", "testLN")),
        Arguments.of(List.of(buildReservation(packageHSCKIN), buildReservation(packageHSCKIN)),
            EARLYCHECKIN, null, null, null)
    );
  }

  private static ReservationByIdDto buildReservation(
      ReservationPackagesDetailsResponseDto packageDto) {
    RoomStayByIdDto room = new RoomStayByIdDto();
    room.setCheckInTime(CHECKIN);
    ReservationByIdDto reservation = new ReservationByIdDto();
    reservation.setRoomStay(room);
    reservation.setReservationPackageList(List.of(packageDto));
    return reservation;
  }

  private static ReservationByIdGuestsDto buildGuest(String title, String firstName, String lastName) {
    var guestInfo = new ReservationByIdGuestsDto();
    guestInfo.setNameTitle(title);
    guestInfo.setGivenName(firstName);
    guestInfo.setSurName(lastName);
    return guestInfo;
  }

  static Stream<Arguments> getThirdPartyGuestNullCases() {
    ReservationByIdDto withNullGuestList = new ReservationByIdDto();
    withNullGuestList.setReservationGuestList(null);
    ReservationByIdDto withEmptyGuestList = new ReservationByIdDto();
    withEmptyGuestList.setReservationGuestList(Collections.emptyList());
    return Stream.of(
        Arguments.of((List<ReservationByIdDto>) null),
        Arguments.of(Collections.emptyList()),
        Arguments.of(List.of(withNullGuestList)),
        Arguments.of(List.of(withEmptyGuestList))
    );
  }
}
