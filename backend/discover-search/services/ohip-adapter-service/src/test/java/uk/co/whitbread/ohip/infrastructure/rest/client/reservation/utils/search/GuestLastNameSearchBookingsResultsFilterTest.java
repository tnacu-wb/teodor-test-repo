package uk.co.whitbread.ohip.infrastructure.rest.client.reservation.utils.search;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.co.whitbread.ohip.domain.model.reservation.out.SearchBooking;
import uk.co.whitbread.ohip.domain.model.reservation.out.SearchBookingReservation;
import uk.co.whitbread.ohip.domain.model.reservation.out.SearchBookingStayingGuest;

@ExtendWith(MockitoExtension.class)
class GuestLastNameSearchBookingsResultsFilterTest {

  @Test
  void filter__ShouldReturnOK() {
    //Arrange
    var searchBookingReservation = SearchBookingReservation.builder()
        .reservationId("TestReservationId")
        .confirmationId("TestConfirmationId")
        .cancellationId("TestCancellationId")
        .stayingGuest(SearchBookingStayingGuest.builder()
            .profileId("TestProfileId")
            .title("TestTitle")
            .firstName("TestFirstName")
            .lastName("TestLastName")
            .build())
        .build();
    var filter = new GuestLastNameSearchBookingsResultsFilter("TestLastName");
    var searchBooking = mock(SearchBooking.class);
    when(searchBooking.getReservations()).thenReturn(Arrays.asList(searchBookingReservation));
    //Act
    //Assert
    assertTrue(filter.filter(searchBooking));
  }

  @Test
  void filter_False_ShouldReturnOK() {
    //Arrange
    var filter = new GuestLastNameSearchBookingsResultsFilter("testFilter");
    var searchBooking = mock(SearchBooking.class);
    when(searchBooking.getReservations()).thenReturn(null);
    //Act
    //Assert
    assertFalse(filter.filter(searchBooking));
  }
}