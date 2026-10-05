package uk.co.whitbread.booking.domain.logic;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.co.whitbread.booking.domain.model.feature.FeatureFlag;
import uk.co.whitbread.booking.domain.model.feature.UnleashWrapper;
import uk.co.whitbread.booking.domain.model.history.out.Booking;
import uk.co.whitbread.booking.domain.model.history.out.BookingStatus;
import uk.co.whitbread.booking.domain.properties.DigitalKeyProperties;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class DigitalKeyTest {

  @InjectMocks
  private DigitalKeyFeature digitalKeyFeature;
  private DigitalKeyProperties digitalKeyProperties;
  private UnleashWrapper<FeatureFlag> unleashWrapper;

  @BeforeEach
  void setUp() {
    digitalKeyProperties = mock(DigitalKeyProperties.class);
    unleashWrapper = mock(UnleashWrapper.class);
    digitalKeyFeature = new DigitalKeyFeature(digitalKeyProperties, unleashWrapper);
  }

  @Test
  void testDigitalKeyAvailable_Success() {
    when(digitalKeyProperties.getDkHotels()).thenReturn(Set.of("FRAMTI"));
    when(digitalKeyProperties.getMaxRooms()).thenReturn(1);
    Booking booking = new Booking();
    booking.setHotelCode("FRAMTI");
    booking.setNoOfRooms(1);
    booking.setBookingStatus(BookingStatus.FUTURE);

    boolean result = digitalKeyFeature.isDigitalKeyAvailable(booking);
    assertTrue(result);
  }

  @Test
  void testDigitalKeyUnavailable_InvalidHotel() {
    when(digitalKeyProperties.getDkHotels()).thenReturn(Set.of("FRAMTI"));
    Booking booking = new Booking();
    booking.setHotelCode("INVALID");
    booking.setNoOfRooms(1);
    booking.setBookingStatus(BookingStatus.FUTURE);

    assertFalse(digitalKeyFeature.isDigitalKeyAvailable(booking));
  }

  @Test
  void testDigitalKeyUnavailable_TooManyRooms() {
    when(digitalKeyProperties.getDkHotels()).thenReturn(Set.of("FRAMTI"));
    when(digitalKeyProperties.getMaxRooms()).thenReturn(3);
    Booking booking = new Booking();
    booking.setHotelCode("FRAMTI");
    booking.setNoOfRooms(5); // exceeds maxRooms=2
    booking.setBookingStatus(BookingStatus.FUTURE);

    assertFalse(digitalKeyFeature.isDigitalKeyAvailable(booking));
  }

  @Test
  void testDigitalKeyUnavailable_InvalidBookingStatus() {
    when(digitalKeyProperties.getDkHotels()).thenReturn(Set.of("FRAMTI"));
    when(digitalKeyProperties.getMaxRooms()).thenReturn(1);

    Booking booking = new Booking();
    booking.setHotelCode("FRAMTI");
    booking.setNoOfRooms(1);
    booking.setBookingStatus(BookingStatus.CANCELLED);

    assertFalse(digitalKeyFeature.isDigitalKeyAvailable(booking));
  }
}