package uk.co.whitbread.infrastructure.rest.client.hotel;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.co.whitbread.domain.model.hotel.out.HotelInfo;
import uk.co.whitbread.domain.model.hotel.out.HotelPreferences;
import uk.co.whitbread.domain.model.hotel.out.HotelPreferencesResponse;
import uk.co.whitbread.domain.model.hotel.out.RoomTypeInfo;
import uk.co.whitbread.domain.model.hotel.out.RoomTypesInfo;
import uk.co.whitbread.domain.ports.secondary.ContentServiceOutPort;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.HotelInfoDto;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.HotelPreferencesResponseDto;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.RoomTypesInfoDto;
import uk.co.whitbread.infrastructure.rest.client.hotel.mapper.HotelInfoMapper;
import uk.co.whitbread.infrastructure.rest.client.hotel.mapper.RoomTypesInfoMapper;
import uk.co.whitbread.infrastructure.rest.client.OhipClient;

@ExtendWith(MockitoExtension.class)
public class HotelInfoOutPortImplTest {

  @Mock
  private OhipClient ohipClient;

  @Mock
  private HotelInfoMapper hotelInfoMapper;

  @Mock
  private RoomTypesInfoMapper roomTypesInfoMapper;

  @Mock
  private ContentServiceOutPort contentServiceOutPort;

  @InjectMocks
  private HotelInfoOutPortImpl hotelInfoOutPort;

  @Test
  public void testGetHotelInfo() {
    var hotelInfoDto = mock(HotelInfoDto.class);

    when(ohipClient.getHotelInfo(anyString())).thenReturn(hotelInfoDto);
    when(hotelInfoMapper.toDomainModel(any(HotelInfoDto.class))).thenReturn(
        HotelInfo.builder().threeLetterId("ABC").build());

    var response = hotelInfoOutPort.getHotelInfo("TSTTST");

    assertNotNull(response);
    assertEquals(response.getThreeLetterId(), "ABC");
  }

  @Test
  void testGetRoomTypesInfo() {
    var ohipResponse = mock(RoomTypesInfoDto.class);

    when(ohipClient.getRoomTypesInfo(anyString())).thenReturn(ohipResponse);
    when(roomTypesInfoMapper.toDomainModel(any(RoomTypesInfoDto.class))).thenReturn(
        RoomTypesInfo.builder()
            .hotelId("MANOLD")
            .roomType(List.of(RoomTypeInfo.builder()
                .roomClass("ST")
                .accessible(false)
                .roomType("DOUBLE")
                .build()))
            .build());

    var response = hotelInfoOutPort.getRoomTypesInfoByHotelIds(List.of("MANOLD"));

    assertNotNull(response);
    assertEquals("MANOLD", response.get("MANOLD").getHotelId());
    assertEquals("ST", response.get("MANOLD").getRoomType().get(0).getRoomClass());
    assertEquals(false, response.get("MANOLD").getRoomType().get(0).getAccessible());
    assertEquals("DOUBLE", response.get("MANOLD").getRoomType().get(0).getRoomType());
  }

  @Test
  void getHotelPreferences_success() {
    var ohipResponse = mock(HotelPreferencesResponseDto.class);

    when(ohipClient.getPreferencesForGroup(anyString(),anyString())).thenReturn(ohipResponse);
    when(contentServiceOutPort.getPreferencesLabels(anyString(), anyString(), anyString(), anyString())).thenReturn(
        Map.of("booking.confirmation.specialOccasion.list","test"));
    when(hotelInfoMapper.toDomainModel(any(HotelPreferencesResponseDto.class)))
        .thenReturn(createHotelPreferenceResponse());

    var response = hotelInfoOutPort.getHotelPreferences("hotelId","group","en");

    assertNotNull(response);
  }

  private HotelPreferencesResponse createHotelPreferenceResponse() {

    var hotelPreferencesInvalidCode = HotelPreferences.builder()
        .code("code")
        .preferenceGroup("group")
        .description("description")
        .housekeeping(false)
        .orderSequence(2)
        .hotelId("hotelid")
        .build();
    var hotelPreferencesValidCode = HotelPreferences.builder()
        .code("BDAY")
        .preferenceGroup("group")
        .description("description")
        .housekeeping(false)
        .orderSequence(1)
        .hotelId("hotelid")
        .build();
    return new HotelPreferencesResponse(List.of(hotelPreferencesInvalidCode, hotelPreferencesValidCode));
  }
}
