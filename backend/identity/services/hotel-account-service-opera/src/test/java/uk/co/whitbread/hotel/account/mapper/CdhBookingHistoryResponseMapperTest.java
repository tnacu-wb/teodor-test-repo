package uk.co.whitbread.hotel.account.mapper;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.co.whitbread.cdh.adapter.service.generated.models.reservationSearch.BookerDto;
import uk.co.whitbread.cdh.adapter.service.generated.models.reservationSearch.CdhReservationSearchDto;
import uk.co.whitbread.cdh.adapter.service.generated.models.reservationSearch.GuestsDto;
import uk.co.whitbread.cdh.adapter.service.generated.models.reservationSearch.ResultsDto;
import uk.co.whitbread.cdh.adapter.service.generated.models.reservationSearch.RoomsDto;
import uk.co.whitbread.hotel.account.model.BookingStatus;
import uk.co.whitbread.hotel.account.model.PersonDetails;
import uk.co.whitbread.hotel.account.model.RoomTypes;
import uk.co.whitbread.hotel.account.model.Stay;
import uk.co.whitbread.hotel.account.model.StaysResponse;

@ExtendWith(MockitoExtension.class)
class CdhBookingHistoryResponseMapperTest {
  private CdhBookingHistoryResponseMapper mapper;

  @BeforeEach
  void setUp() {
    mapper = Mappers.getMapper(CdhBookingHistoryResponseMapper.class);
  }

  @Test
  void testToCdhBookingHistoryResponse() {
    CdhReservationSearchDto dto = new CdhReservationSearchDto();
    dto.setTotalResults(100);
    dto.setSearchResults(10);
    BookerDto booker = new BookerDto();
    booker.setFirstName("John");
    booker.setLastName("Doe");
    booker.emailAddress("john@doe.com");
    ResultsDto result = new ResultsDto();
    result.setBookingReference("REF123");
    result.setArrivalDate("2024-11-12T00:00:00+00:00");
    result.setDepartureDate("2024-11-14T00:00:00+00:00");
    result.setBookingDate("2024-11-10T00:00:00+00:00");
    result.setBooker(booker);

    RoomsDto rooms = new RoomsDto();
    rooms.setStatus("RESERVED");
    result.setRooms(Collections.singletonList(rooms));

    dto.setResults(List.of(result));

    StaysResponse response = mapper.toCdhBookingHistoryResponse(dto);

    assertNotNull(response);
    assertEquals(100, response.getTotalSize());
    assertEquals(10, response.getPageSize());
    assertEquals(1, response.getStays().size());
  }

  @Test
  void testToStay() {
    ResultsDto results = new ResultsDto();
    results.setBookingReference("REF123");
    results.setStatus("CANCELLED");
    results.setBartGuestHistoryNumber("GH123");
    results.setRateCategory("Standard");
    results.setArrivalDate("2023-10-01T00:00:00Z");
    results.setDepartureDate("2023-10-05T00:00:00Z");
    results.setBookingDate("2023-09-01T00:00:00Z");

    RoomsDto room = new RoomsDto();
    room.setStatus("Booked");
    GuestsDto guest = new GuestsDto();
    guest.setTitle("Mr.");
    guest.setFirstName("John");
    guest.setLastName("Doe");
    guest.setLeadGuest(true);
    room.setGuests(List.of(guest));
    results.setRooms(List.of(room));

    Stay stay = mapper.toStay(results);

    assertNotNull(stay);
    assertEquals("REF123", stay.getConfirmationNumber());
    assertTrue(stay.isCancelled());
    assertEquals("GH123", stay.getGuestHistoryNumber());
    assertEquals("Standard", stay.getRateName());
    assertEquals(LocalDate.of(2023, 10, 1), stay.getArrivalDate());
    assertEquals(LocalDate.of(2023, 10, 5), stay.getDepartureDate());
    assertEquals(LocalDate.of(2023, 9, 1), stay.getBookingDate());
    assertEquals("Mr. John Doe", stay.getLeadGuest());
  }

  @Test
  void testToRoomTypes() {
    RoomsDto room = new RoomsDto();
    room.setStatus("Booked");
    GuestsDto guest = new GuestsDto();
    guest.setTitle("Mr.");
    guest.setFirstName("John");
    guest.setLastName("Doe");
    guest.setLeadGuest(true);
    room.setGuests(List.of(guest));

    RoomTypes roomTypes = mapper.toRoomTypes(room);

    assertNotNull(roomTypes);
    assertEquals("Mr. John Doe", roomTypes.getLeadGuest());
  }

  @Test
  void testToPersonDetails() {
    GuestsDto guest = new GuestsDto();
    guest.setTitle("Mr.");
    guest.setFirstName("John");
    guest.setLastName("Doe");
    guest.setLeadGuest(true);

    PersonDetails personDetails = mapper.toPersonDetails(List.of(guest));

    assertNotNull(personDetails);
    assertEquals("Mr.", personDetails.getTitle());
    assertEquals("John", personDetails.getFirstName());
    assertEquals("Doe", personDetails.getLastName());
  }

  @Test
  void testToLocalDate() {
    LocalDate date = mapper.toLocalDate("2023-10-01T00:00:00Z");

    assertNotNull(date);
    assertEquals(LocalDate.of(2023, 10, 1), date);
  }

  @Test
  void testToCancelled() {
    assertTrue(mapper.toCancelled("CANCELLED"));
    assertFalse(mapper.toCancelled("BOOKED"));
  }

  @Test
  void testToCheckedIn() {
    assertTrue(mapper.toCheckedIn("ARRIVED"));
    assertTrue(mapper.toCheckedIn("CHECKEDIN"));
    assertFalse(mapper.toCheckedIn("BOOKED"));
  }

  @Test
  void testToBookingStatus() {
    ResultsDto results = new ResultsDto();
    results.setStatus("ARRIVED");

    BookingStatus status = mapper.toBookingStatus(results);

    assertNotNull(status);
    assertEquals(BookingStatus.CHECKED_IN, status);
  }

  @Test
  void testToBookedBy() {
    BookerDto booker = new BookerDto();
    booker.setFirstName("John");
    booker.setLastName("Doe");

    String bookedBy = mapper.toBookedBy(booker);

    assertNotNull(bookedBy);
    assertEquals("John Doe", bookedBy);
  }

  @Test
  void testToStayLeadGuest() {
    RoomsDto room = new RoomsDto();
    GuestsDto guest = new GuestsDto();
    guest.setTitle("Mr.");
    guest.setFirstName("John");
    guest.setLastName("Doe");
    guest.setLeadGuest(true);
    room.setGuests(List.of(guest));
    room.setStatus("RESERVED");

    String leadGuest = mapper.toStayLeadGuest(List.of(room));

    assertNotNull(leadGuest);
    assertEquals("Mr. John Doe", leadGuest);
  }

  @Test
  void testToStayLeadGuestLastName() {
    RoomsDto room = new RoomsDto();
    GuestsDto guest = new GuestsDto();
    guest.setTitle("Mr.");
    guest.setFirstName("John");
    guest.setLastName("Doe");
    guest.setLeadGuest(true);
    room.setGuests(List.of(guest));
    room.setStatus("RESERVED");

    String leadGuestLastName = mapper.toStayLeadGuestLastName(List.of(room));

    assertNotNull(leadGuestLastName);
    assertEquals("Doe", leadGuestLastName);
  }

  @Test
  void testToRoomLeadGuest() {
    GuestsDto guest = new GuestsDto();
    guest.setTitle("Mr.");
    guest.setFirstName("John");
    guest.setLastName("Doe");
    guest.setLeadGuest(true);

    String leadGuest = mapper.toRoomLeadGuest(List.of(guest));

    assertNotNull(leadGuest);
    assertEquals("Mr. John Doe", leadGuest);
  }

}
