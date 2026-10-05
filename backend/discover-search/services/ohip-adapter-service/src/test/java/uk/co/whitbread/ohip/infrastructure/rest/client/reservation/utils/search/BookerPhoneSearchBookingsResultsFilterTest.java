package uk.co.whitbread.ohip.infrastructure.rest.client.reservation.utils.search;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.co.whitbread.ohip.domain.model.reservation.out.SearchBooking;
import uk.co.whitbread.ohip.domain.model.reservation.out.SearchBookingBooker;

@ExtendWith(MockitoExtension.class)
class BookerPhoneSearchBookingsResultsFilterTest {

  @Test
  void filter_TestMobile_ShouldReturnOK() {
    //Arrange
    var filter = new BookerPhoneSearchBookingsResultsFilter("testFilter");
    var searchBooking = mock(SearchBooking.class);
    when(searchBooking.getBooker()).thenReturn(mock(SearchBookingBooker.class));
    when(searchBooking.getBooker().getMobile()).thenReturn("testFilter");
    //Act
    //Assert
    assertTrue(filter.filter(searchBooking));
  }

  @Test
  void filter_TestLandline_ShouldReturnOK() {
    //Arrange
    var filter = new BookerPhoneSearchBookingsResultsFilter("testFilter");
    var searchBooking = mock(SearchBooking.class);
    when(searchBooking.getBooker()).thenReturn(mock(SearchBookingBooker.class));
    when(searchBooking.getBooker().getMobile()).thenReturn("notTestFilter");
    when(searchBooking.getBooker().getLandline()).thenReturn("testFilter");
    //Act
    //Assert
    assertTrue(filter.filter(searchBooking));
  }

  @Test
  void filter_False_ShouldReturnOK() {
    //Arrange
    var filter = new BookerPhoneSearchBookingsResultsFilter("testFilter");
    var searchBooking = mock(SearchBooking.class);
    when(searchBooking.getBooker()).thenReturn(null);
    //Act
    //Assert
    assertFalse(filter.filter(searchBooking));
  }
}