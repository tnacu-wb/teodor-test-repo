package uk.co.whitbread.booking.infrastructure.rest.client.ohip;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.co.whitbread.booking.domain.model.history.out.Booking;
import uk.co.whitbread.booking.infrastructure.rest.client.ohip.model.out.HotelInfoDto;
import uk.co.whitbread.booking.infrastructure.rest.client.ohip.service.OhipAdapterClient;

@ExtendWith(MockitoExtension.class)
class HotelInfoOutPortImplTest {

  @Mock
  private OhipAdapterClient ohipAdapterClient;

  @InjectMocks
  private HotelInfoOutPortImpl hotelInfoOutPort;

  @Test
  void getHotelInfo() {
    //Arrange
    var bookings = mockBookings();
    when(ohipAdapterClient.fetchMultipleHotelsInfo(any())).thenReturn(mockHotelInfoResponse());

    //Act
    var response = hotelInfoOutPort.getHotelInfo(bookings);
    //Assert
    assertNotNull(response);
    assertEquals(3,response.size());
  }


  private List<HotelInfoDto> mockHotelInfoResponse() {
   return List.of(
        new HotelInfoDto("utc","gb","testHotel1"),
        new HotelInfoDto("utc","de","testHotel2"),
        new HotelInfoDto("utc","ro","testHotel3"),
        new HotelInfoDto(null,"gb","testHotel4"),
        new HotelInfoDto("utc",null,"testHotel5"),
        new HotelInfoDto("utc","gb",null)
    );
  }

  private List<Booking> mockBookings() {
    var firstBooking = new Booking();
    firstBooking.setHotelCode("testHotel1");
    var secondBooking = new Booking();
    secondBooking.setHotelCode("testHotel2");
    var thirdBooking = new Booking();
    thirdBooking.setHotelCode("testHotel3");
    var fourthBooking = new Booking();
    fourthBooking.setHotelCode("testHotel4");
    var fifthBooking = new Booking();
    fifthBooking.setHotelCode("testHotel5");
    var sixthBooking = new Booking();
    sixthBooking.setHotelCode("testHotel6");
    return List.of(firstBooking, secondBooking, thirdBooking, fourthBooking, fifthBooking, sixthBooking);
  }
}