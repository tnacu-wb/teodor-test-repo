package uk.co.whitbread.ohip.domain.logic;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.co.whitbread.ohip.domain.model.hotel.out.HotelInfo;
import uk.co.whitbread.ohip.domain.model.hotel.out.RoomTypeInfo;
import uk.co.whitbread.ohip.domain.model.hotel.out.RoomTypesInfo;
import uk.co.whitbread.ohip.domain.ports.secondary.HotelInfoOutPort;

@ExtendWith(MockitoExtension.class)
class HotelInfoInPortImplTest {

  @Mock
  private HotelInfoOutPort hotelInfoOutPort;

  @InjectMocks
  private HotelInfoInPortImpl hotelInfoInPort;

  @Test
  void testGetHotelInfo() {
    when(hotelInfoOutPort.getHotelInfo(anyString()))
        .thenReturn(HotelInfo.builder()
            .threeLetterId("ABC")
            .hotelTimeZone("Europe/London")
            .hotelCountryCode("GB")
            .currencyCode("GBP")
            .languageCode("E")
            .checkInTime("01/01/1970, 15:00")
            .checkOutTime("01/01/1970, 12:00").build());

    var response = hotelInfoInPort.getHotelInfo("TSTTST");

    assertNotNull(response);
    assertEquals("ABC", response.getThreeLetterId());
    assertEquals("Europe/London", response.getHotelTimeZone());
    assertEquals("GB", response.getHotelCountryCode());
    assertEquals("GBP", response.getCurrencyCode());
    assertEquals("E", response.getLanguageCode());
    assertEquals("01/01/1970, 15:00", response.getCheckInTime());
    assertEquals("01/01/1970, 12:00", response.getCheckOutTime());
  }

  @Test
  void testGetRoomTypesInfo() {
    when(hotelInfoOutPort.getRoomTypesInfo(anyString()))
        .thenReturn(RoomTypesInfo.builder()
            .hotelId("TSTTST")
            .roomType(List.of(RoomTypeInfo.builder()
                .roomClass("ST")
                .accessible(false)
                .roomType("DOUBLE")
                .build()))
            .build());

    var response = hotelInfoInPort.getRoomTypesInfo("TSTTST");

    assertNotNull(response);
    assertEquals("TSTTST", response.getHotelId());
    assertEquals("ST", response.getRoomType().get(0).getRoomClass());
    assertEquals(false, response.getRoomType().get(0).getAccessible());
    assertEquals("DOUBLE", response.getRoomType().get(0).getRoomType());
  }

}
