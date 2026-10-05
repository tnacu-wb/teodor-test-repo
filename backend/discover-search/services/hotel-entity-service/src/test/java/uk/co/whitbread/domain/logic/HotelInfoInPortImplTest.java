package uk.co.whitbread.domain.logic;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.co.whitbread.domain.model.hotel.out.HotelInfo;
import uk.co.whitbread.domain.model.hotel.out.RoomTypeInfo;
import uk.co.whitbread.domain.model.hotel.out.RoomTypesInfo;
import uk.co.whitbread.domain.ports.secondary.HotelInfoOutPort;

@ExtendWith(MockitoExtension.class)
public class HotelInfoInPortImplTest {

  @Mock
  private HotelInfoOutPort hotelInfoOutPort;

  @InjectMocks
  private HotelInfoInPortImpl hotelInfoInPort;

  @Test
  public void testGetHotelInfo() {
    when(hotelInfoOutPort.getHotelInfo(anyString())).thenReturn(
        HotelInfo.builder().threeLetterId("ABC").build());

    var response = hotelInfoInPort.getHotelInfo("TSTTST");

    assertNotNull(response);
    assertEquals(response.getThreeLetterId(), "ABC");
  }

  @Test
  void testGetRoomTypesInfo() {
    when(hotelInfoOutPort.getRoomTypesInfoByHotelIds(anyList()))
        .thenReturn(Map.of("TSTTST", RoomTypesInfo.builder()
            .hotelId("TSTTST")
            .roomType(List.of(RoomTypeInfo.builder()
                .roomClass("ST")
                .accessible(false)
                .roomType("DOUBLE")
                .build()))
            .build()));

    var response = hotelInfoInPort.getRoomTypesInfo(List.of("TSTTST"));

    assertNotNull(response);
    assertEquals("TSTTST", response.get("TSTTST").getHotelId());
    assertEquals("ST", response.get("TSTTST").getRoomType().get(0).getRoomClass());
    assertEquals(false, response.get("TSTTST").getRoomType().get(0).getAccessible());
    assertEquals("DOUBLE", response.get("TSTTST").getRoomType().get(0).getRoomType());
  }
}
