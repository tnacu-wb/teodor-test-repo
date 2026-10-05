package uk.co.whitbread.content.domain.logic;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

import java.util.Collections;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import uk.co.whitbread.content.domain.model.roomtype.in.RoomTypeRequest;
import uk.co.whitbread.content.domain.model.roomtype.out.RoomType;
import uk.co.whitbread.content.infrastructure.rest.client.aem.model.hotel.in.AemHotelInformationDto;
import uk.co.whitbread.content.infrastructure.rest.client.aem.model.hotel.in.HotelRoomConfiguration;
import uk.co.whitbread.content.infrastructure.rest.client.aem.model.hotel.in.TabItem;
import uk.co.whitbread.content.infrastructure.rest.client.content.aem.adapter.AemClient;
import uk.co.whitbread.content.infrastructure.rest.client.roomtype.RoomTypeOutPortImpl;
import uk.co.whitbread.content.infrastructure.rest.client.roomtype.adapter.RoomTypeAemClient;
import uk.co.whitbread.content.infrastructure.rest.client.roomtype.mapper.RoomTypeMapper;
import uk.co.whitbread.content.infrastructure.rest.client.roomtype.mapper.RoomTypeRequestMapper;
import uk.co.whitbread.content.infrastructure.rest.client.roomtype.model.in.RoomTypeDto;
import uk.co.whitbread.content.infrastructure.rest.client.roomtype.model.in.RoomTypeInformationDto;
import uk.co.whitbread.content.infrastructure.rest.client.roomtype.model.out.RoomTypeRequestAemDto;
@ExtendWith(MockitoExtension.class)
class RoomTypeOutPortImplTest {

  @Mock
  private RoomTypeAemClient roomTypeAemClient;

  @Mock
  private RoomTypeRequestMapper roomTypeRequestMapper;

  @Mock
  private RoomTypeMapper roomTypeMapper;

  @Mock
  private AemClient aemClient;

  @InjectMocks
  private RoomTypeOutPortImpl roomTypeOutPort;

  @Test
  void getRoomType__ShouldReturnOk() {
    // Arrange
    RoomTypeRequestAemDto roomTypeRequest = new RoomTypeRequestAemDto("en", "gb", "pi", null);
    RoomTypeRequest roomTypeRequest2 = new RoomTypeRequest("en", "gb", "pi", null);
    RoomTypeDto roomTypeDto = mockRoomTypeDto();
    RoomType roomType = mockRoomType();

    when(roomTypeRequestMapper.toDto(any())).thenReturn(roomTypeRequest);
    when(roomTypeAemClient.getRoomType(any())).thenReturn(roomTypeDto);
    when(roomTypeMapper.toDomainModel(any(RoomTypeDto.class))).thenReturn(roomType);

    // Act
    RoomType result = roomTypeOutPort.getRoomType(roomTypeRequest2);

    // Assert
    assertEquals(roomType, result);
    verify(roomTypeRequestMapper).toDto(roomTypeRequest2);
    verify(roomTypeMapper).toDomainModel(roomTypeDto);
  }

  @Test
  void getRoomTypeWithHotelId__ShouldReturnOk() {
    // Arrange
    RoomTypeRequestAemDto roomTypeRequest = new RoomTypeRequestAemDto("en", "gb", "pi", "LONLEI");
    RoomTypeRequest roomTypeRequest2 = new RoomTypeRequest("en", "gb", "pi", "LONLEI");
    RoomTypeDto roomTypeDto = mockRoomTypeDto();
    RoomType roomType = mockRoomTypeWithHotelId();
    AemHotelInformationDto hotelInformationDto = mockAemHotelInformationDto();

    when(roomTypeRequestMapper.toDto(any())).thenReturn(roomTypeRequest);
    when(roomTypeAemClient.getRoomType(any())).thenReturn(roomTypeDto);
    when(aemClient.getSingleHotelInformation(anyString(), anyString(), anyString())).thenReturn(hotelInformationDto);
    when(roomTypeMapper.toDomainModel(any(RoomTypeDto.class))).thenReturn(roomType);

    // Act
    RoomType result = roomTypeOutPort.getRoomType(roomTypeRequest2);

    // Assert
    assertEquals(roomType, result);
    verify(roomTypeRequestMapper).toDto(roomTypeRequest2);
    verify(roomTypeMapper).toDomainModel(roomTypeDto);
  }

  private RoomTypeDto mockRoomTypeDto() {
    return RoomTypeDto.builder()
            .roomTypes(Collections.singletonList(RoomTypeInformationDto.builder()
                    .roomTypeCode("FMQUAD")
                    .roomCategory("Family")
                    .roomLabel("Family Room")
                    .roomInfoLabel("")
                    .roomInfo("")
                    .gridImage(
                            "/content/dam/pi/websites/hotelimages/gb/en/non-hotel-specific/Bedrooms/ID4-family-room.jpg")
                    .roomDescription(
                            "Our spacious family rooms are ideal for up to two adults plus two kids (aged 15 or under). Most feature a super-comfy double or kingsize Hypnos bed, plus a single sofa bed and a pull-out bed. We also provide cots at no extra cost.")
                    .roomImage(
                            "/content/dam/pi/websites/hotelimages/gb/en/non-hotel-specific/Bedrooms/ID4-family-room.jpg")
                    .groupId("accessible")
                    .build()))
            .build();
  }

  private RoomType mockRoomTypeWithHotelId() {
    return RoomType.builder()
            .roomTypes(Collections.singletonList(
                    uk.co.whitbread.content.domain.model.roomtype.out.RoomTypeInformation.builder()
                            .roomTypeCode(Collections.singletonList("FMQUAD"))
                            .roomCategory("Family")
                            .roomLabel("Family Room")
                            .roomInfoLabel("")
                            .roomInfo("")
                            .gridImage("/content/dam/pi/websites/hotelimages/gb/en/non-hotel-specific/Bedrooms/new.jpg")
                            .roomDescription("Our spacious family rooms are ideal for up to two adults plus two kids (aged 15 or under). Most feature a super-comfy double or kingsize Hypnos bed, plus a single sofa bed and a pull-out bed. We also provide cots at no extra cost.")
                            .roomImage("/content/dam/pi/websites/hotelimages/gb/en/non-hotel-specific/Bedrooms/new.jpg")
                            .groupId("accessible")
                            .build()
            ))
            .build();
  }

  private RoomType mockRoomType() {
    return RoomType.builder()
            .roomTypes(Collections.singletonList(
                    uk.co.whitbread.content.domain.model.roomtype.out.RoomTypeInformation.builder()
                            .roomTypeCode(Collections.singletonList("FMQUAD"))
                            .roomCategory("Family")
                            .roomLabel("Family Room")
                            .roomInfoLabel("")
                            .roomInfo("")
                            .gridImage("/content/dam/pi/websites/hotelimages/gb/en/non-hotel-specific/Bedrooms/ID4-family-room.jpg")
                            .roomDescription("Our spacious family rooms are ideal for up to two adults plus two kids (aged 15 or under). Most feature a super-comfy double or kingsize Hypnos bed, plus a single sofa bed and a pull-out bed. We also provide cots at no extra cost.")
                            .roomImage("/content/dam/pi/websites/hotelimages/gb/en/non-hotel-specific/Bedrooms/new.jpg")
                            .groupId("accessible")
                            .build()
            ))
            .build();
  }

  private AemHotelInformationDto mockAemHotelInformationDto() {
    AemHotelInformationDto hotelInformationDto = new AemHotelInformationDto();
    hotelInformationDto.setHotelRoomConfiguration(HotelRoomConfiguration.builder()
            .tabItems(Collections.singletonList(
                    TabItem.builder()
                            .roomType("FMQUAD")
                            .roomTypeCode("FMQUAD")
                            .fileReference("/content/dam/pi/websites/hotelimages/gb/en/non-hotel-specific/Bedrooms/new.jpg")
                            .rateGridRoomDescription("Our spacious family rooms are ideal for up to two adults plus two kids (aged 15 or under). Most feature a super-comfy double or kingsize Hypnos bed, plus a single sofa bed and a pull-out bed. We also provide cots at no extra cost.")
                            .build()
            ))
            .build());
    return hotelInformationDto;
  }
}
