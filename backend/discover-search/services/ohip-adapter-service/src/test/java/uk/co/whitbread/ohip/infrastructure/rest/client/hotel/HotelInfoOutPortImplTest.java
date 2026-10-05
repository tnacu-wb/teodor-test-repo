package uk.co.whitbread.ohip.infrastructure.rest.client.hotel;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.enterprise.HotelDetails;
import uk.co.whitbread.ohip.domain.model.hotel.out.HotelInfo;
import uk.co.whitbread.ohip.domain.model.hotel.out.RoomTypeInfo;
import uk.co.whitbread.ohip.domain.model.hotel.out.RoomTypesInfo;
import uk.co.whitbread.ohip.infrastructure.rest.client.availability.model.out.RoomTypesDto;
import uk.co.whitbread.ohip.infrastructure.rest.client.availability.model.out.RoomTypesResponseDto;
import uk.co.whitbread.ohip.infrastructure.rest.client.availability.ohip.OhipAvailabilityClient;
import uk.co.whitbread.ohip.infrastructure.rest.client.hotel.mapper.HotelInfoMapper;
import uk.co.whitbread.ohip.infrastructure.rest.client.hotel.mapper.RoomTypesInfoMapper;
import uk.co.whitbread.ohip.infrastructure.rest.client.hotel.ohip.OhipHotelConfigClient;

@ExtendWith(MockitoExtension.class)
class HotelInfoOutPortImplTest {

  @Mock
  private OhipHotelConfigClient ohipHotelConfigClient;

  @Mock
  private OhipAvailabilityClient ohipAvailabilityClient;

  @Mock
  private HotelInfoMapper hotelInfoMapper;

  @Mock
  private RoomTypesInfoMapper roomTypesInfoMapper;

  @InjectMocks
  private HotelInfoOutPortImpl hotelInfoOutPort;

  @Test
  void testGetHotelInfo() {
    var hotelDetails = mock(HotelDetails.class);

    when(ohipHotelConfigClient.getHotelConfig(anyString())).thenReturn(hotelDetails);
    when(hotelInfoMapper.toDomainModel(any(HotelDetails.class))).thenReturn(HotelInfo.builder()
            .threeLetterId("AWM")
            .hotelTimeZone("Europe/London")
            .hotelCountryCode("GB")
            .currencyCode("GBP")
            .languageCode("E")
            .checkInTime("01/01/1970, 15:00")
            .checkOutTime("01/01/1970, 12:00")
            .build());

    var response = hotelInfoOutPort.getHotelInfo("MANOLD");

    assertNotNull(response);
    assertEquals("AWM", response.getThreeLetterId());
    assertEquals("Europe/London", response.getHotelTimeZone());
    assertEquals("GB", response.getHotelCountryCode());
    assertEquals("GBP", response.getCurrencyCode());
    assertEquals("E", response.getLanguageCode());
    assertEquals("01/01/1970, 15:00", response.getCheckInTime());
    assertEquals("01/01/1970, 12:00", response.getCheckOutTime());
  }

  @Test
  void testGetRoomTypesInfo() {
    var ohipResponse = mock(RoomTypesResponseDto.class);
    when(ohipResponse.getRoomTypesSummary()).thenReturn(List.of(RoomTypesDto.builder()
            .hotelId("MANOLD")
        .build()));

    when(ohipAvailabilityClient.getRoomTypes(anyString())).thenReturn(ohipResponse);
    when(roomTypesInfoMapper.toDomainModel(any(RoomTypesDto.class))).thenReturn(
        RoomTypesInfo.builder()
        .hotelId("MANOLD")
        .roomType(List.of(RoomTypeInfo.builder()
            .roomClass("ST")
            .accessible(false)
            .roomType("DOUBLE")
            .build()))
        .build());

    var response = hotelInfoOutPort.getRoomTypesInfo("MANOLD");

    assertNotNull(response);
    assertEquals("MANOLD", response.getHotelId());
    assertEquals("ST", response.getRoomType().get(0).getRoomClass());
    assertEquals(false, response.getRoomType().get(0).getAccessible());
    assertEquals("DOUBLE", response.getRoomType().get(0).getRoomType());
  }

}