package uk.co.whitbread.ohip.infrastructure.rest.client.reservation.utils.search;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.co.whitbread.ohip.domain.model.reservation.out.SearchBooking;
import uk.co.whitbread.ohip.domain.model.reservation.out.SearchBookingBooker;

@ExtendWith(MockitoExtension.class)
class BookerLastNameSearchBookingsResultsFilterTest {

  @InjectMocks
  private BookerLastNameSearchBookingsResultsFilter bookerLastNameSearchBookingsResultsFilter;

  @Test
  void filter__ShouldReturnOK() {
    //Arrange
    var filter = new BookerLastNameSearchBookingsResultsFilter("testFilter");
    var searchBooking = mock(SearchBooking.class);
    when(searchBooking.getBooker()).thenReturn(mock(SearchBookingBooker.class));
    when(searchBooking.getBooker().getLastName()).thenReturn("testFilter");
    //Act
    //Assert
    assertTrue(filter.filter(searchBooking));
  }

  @Test
  void filter_False_ShouldReturnOK() {
    //Arrange
    var filter = SearchBooking.builder().build();
    //Act
    var result = bookerLastNameSearchBookingsResultsFilter.filter(filter);
    //Assert
    Assertions.assertFalse(result);
  }
}