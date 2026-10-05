package uk.co.whitbread.ohip.infrastructure.rest.client.reservation.utils.search;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.co.whitbread.ohip.domain.model.reservation.out.SearchBooking;

@ExtendWith(MockitoExtension.class)
class HotelIdSearchBookingsResultsFilterTest {

  @Test
  void filter__ShouldReturnOK() {
    //Arrange
    var filter = new HotelIdSearchBookingsResultsFilter("testHotelIdFilter");
    var searchBooking = mock(SearchBooking.class);
    when(searchBooking.getHotelId()).thenReturn("testHotelIdFilter");
    //Act
    //Assert
    assertTrue(filter.filter(searchBooking));
  }

  @Test
  void filter_False_ShouldReturnOK() {
    //Arrange
    var filter = new HotelIdSearchBookingsResultsFilter("testFilter");
    var searchBooking = mock(SearchBooking.class);
    when(searchBooking.getHotelId()).thenReturn(null);
    //Act
    //Assert
    assertFalse(filter.filter(searchBooking));
  }
}