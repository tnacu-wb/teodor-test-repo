package uk.co.whitbread.ohip.infrastructure.rest.client.reservation.utils.search;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.time.LocalDate;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.co.whitbread.ohip.domain.model.reservation.out.SearchBooking;

@ExtendWith(MockitoExtension.class)
class DepartureDateBookingsResultsFilterTest {
  @InjectMocks
  private DepartureDateBookingsResultsFilter dateBookingsResultsFilter;

  @Test
  void filter_isAfter_ShouldReturnTrue() {
    //Arrange
    var request = SearchBooking.builder().departureDate(LocalDate.now()).build();
    //Act
    var result = dateBookingsResultsFilter.filter(request);
    //Assert
    assertEquals(true, result);
  }

  @Test
  void filter__ShouldReturnFalse() {
    //Arrange
    var request = SearchBooking.builder().departureDate(LocalDate.now().minusYears(2)).build();
    //Act
    var result = dateBookingsResultsFilter.filter(request);
    //Assert
    assertEquals(false, result);
  }

  @Test
  void filter_isEqual_ShouldReturnTreu() {
    //Arrange
    var request = SearchBooking.builder().departureDate(LocalDate.now().minusYears(1)).build();
    //Act
    var result = dateBookingsResultsFilter.filter(request);
    //Assert
    assertEquals(true, result);
  }

}
