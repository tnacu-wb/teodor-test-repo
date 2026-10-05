package uk.co.whitbread.content.infrastructure.rest.client.roomtype;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;
import static uk.co.whitbread.content.domain.model.ErrorCode.AEM_ROOM_TYPE_EXCEPTION;

import java.util.Collections;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.co.whitbread.content.domain.model.roomtype.in.RoomTypeRequest;
import uk.co.whitbread.content.domain.model.roomtype.out.RoomType;
import uk.co.whitbread.content.domain.model.roomtype.out.RoomTypeInformation;
import uk.co.whitbread.content.infrastructure.rest.client.aem.exceptions.AemResponseException;
import uk.co.whitbread.content.infrastructure.rest.client.roomtype.adapter.RoomTypeAemClient;
import uk.co.whitbread.content.infrastructure.rest.client.roomtype.mapper.RoomTypeMapper;
import uk.co.whitbread.content.infrastructure.rest.client.roomtype.mapper.RoomTypeRequestMapper;
import uk.co.whitbread.content.infrastructure.rest.client.roomtype.model.in.RoomTypeDto;
import uk.co.whitbread.content.infrastructure.rest.client.roomtype.model.in.RoomTypeInformationDto;
import uk.co.whitbread.content.infrastructure.rest.client.roomtype.model.out.RoomTypeRequestAemDto;

@ExtendWith(MockitoExtension.class)
class RoomTypeOutPortImplTest {

  @InjectMocks
  private RoomTypeOutPortImpl roomTypeOutPort;

  @Mock
  private RoomTypeAemClient roomTypeAemClient;

  @Mock
  private RoomTypeRequestMapper roomTypeRequestMapper;

  @Mock
  private RoomTypeMapper roomTypeMapper;

  private Exception exception = new Exception();

  @Test
  void getRoomType_badInput_ShouldReturnException() {
    //Arrange
    var expectedMessage = "Unable to get room type.";
    when(roomTypeRequestMapper.toDto(any())).thenReturn(new RoomTypeRequestAemDto());
    when(roomTypeAemClient.getRoomType(any())).thenThrow(new AemResponseException(
        AEM_ROOM_TYPE_EXCEPTION, expectedMessage, exception));
    var request = new RoomTypeRequest("null", "null", "null", "null");
    //Act
    var actual = assertThrows(AemResponseException.class, () -> roomTypeOutPort.getRoomType(request)
    );

    //Assert
    assertThat(actual, notNullValue());
    assertThat(actual.getGlobalErrTextTemplate(), is(AEM_ROOM_TYPE_EXCEPTION.getMessage()));
    assertThat(actual.getMessage(), is(expectedMessage));
    assertThat(actual.getErrorCode(), is(AEM_ROOM_TYPE_EXCEPTION.getCode()));
  }

  @Test
  void getRoomType__ShouldReturnResourceNotFoundException() {
    //Arrange
    var expectedMessage = "Unable to get room type.";
    when(roomTypeRequestMapper.toDto(any())).thenReturn(new RoomTypeRequestAemDto());
    when(roomTypeAemClient.getRoomType(any())).thenThrow(
        new AemResponseException(AEM_ROOM_TYPE_EXCEPTION, expectedMessage, exception));
    var request = new RoomTypeRequest("null", "null", "null", "null");

    //Act
    var actual = assertThrows(AemResponseException.class,
        () -> roomTypeOutPort.getRoomType(request)
    );

    //Assert
    assertThat(actual, notNullValue());
    assertThat(actual.getDebugMessage(), is(expectedMessage));
  }

  @Test
  void getRoomType_success() {
    //Arrange
    when(roomTypeAemClient.getRoomType((any(RoomTypeRequestAemDto.class))))
        .thenReturn(getRoomType());
    when(roomTypeRequestMapper.toDto(any())).thenReturn(new RoomTypeRequestAemDto());
    when(roomTypeMapper.toDomainModel(any())).thenReturn(mockRoomType());

    //Act
    var roomType = roomTypeOutPort.getRoomType(getRoomTypeRequestGbEn());

    //Assert
    assertThat(roomType, notNullValue());
    assertEquals(1, roomType.getRoomTypes().size());
    assertEquals(Collections.singletonList("FMQUAD"),
        roomType.getRoomTypes().get(0).getRoomTypeCode());
    assertEquals("Family", roomType.getRoomTypes().get(0).getRoomCategory());
    assertEquals("Family Room", roomType.getRoomTypes().get(0).getRoomLabel());
    assertEquals("", roomType.getRoomTypes().get(0).getRoomInfoLabel());
    assertEquals("", roomType.getRoomTypes().get(0).getRoomInfo());
    assertEquals(
        "Our spacious family rooms are ideal for up to two adults plus two kids (aged 15 or under). Most feature a super-comfy double or kingsize Hypnos bed, plus a single sofa bed and a pull-out bed. We also provide cots at no extra cost.",
        roomType.getRoomTypes().get(0).getRoomDescription());
    assertEquals(
        "/content/dam/pi/websites/hotelimages/gb/en/non-hotel-specific/Bedrooms/ID4-family-room.jpg",
        roomType.getRoomTypes().get(0).getRoomImage());
    assertEquals(
        "/content/dam/pi/websites/hotelimages/gb/en/non-hotel-specific/Bedrooms/ID4-family-room.jpg",
        roomType.getRoomTypes().get(0).getGridImage());
    assertEquals("accessible", roomType.getRoomTypes().get(0).getGroupId());

    verify(roomTypeMapper).toDomainModel(any(RoomTypeDto.class));

    verifyNoMoreInteractions(roomTypeRequestMapper);
    verifyNoMoreInteractions(roomTypeMapper);
  }

  @Test
  void getRoomType_multiple_codes_success() {
    //Arrange
    when(roomTypeAemClient.getRoomType((any(RoomTypeRequestAemDto.class))))
        .thenReturn(getRoomTypes());
    when(roomTypeRequestMapper.toDto(any())).thenReturn(new RoomTypeRequestAemDto());
    when(roomTypeMapper.toDomainModel(any())).thenReturn(mockRoomTypes());

    //Act
    var roomType = roomTypeOutPort.getRoomType(getRoomTypeRequestGbEn());

    //Assert
    assertThat(roomType, notNullValue());
    assertEquals(1, roomType.getRoomTypes().size());
    assertEquals(List.of("DOUBLE", "ZPLDBL"), roomType.getRoomTypes().get(0).getRoomTypeCode());
    assertEquals("Double", roomType.getRoomTypes().get(0).getRoomCategory());
    assertEquals("Double Room", roomType.getRoomTypes().get(0).getRoomLabel());
    assertEquals("", roomType.getRoomTypes().get(0).getRoomInfoLabel());
    assertEquals("", roomType.getRoomTypes().get(0).getRoomInfo());
    assertEquals(
        "A super-comfy bed, a power shower and free Wi-Fi – our double rooms have everything you’ll need for a great night’s sleep.",
        roomType.getRoomTypes().get(0).getRoomDescription());
    assertEquals(
        "/content/dam/pi/websites/hotelimages/gb/en/non-hotel-specific/ID4/ID4-Room-2.jpg",
        roomType.getRoomTypes().get(0).getRoomImage());
    assertEquals(
        "/content/dam/pi/websites/hotelimages/gb/en/non-hotel-specific/ID4/ID4-Room-2.jpg",
        roomType.getRoomTypes().get(0).getGridImage());
    assertEquals("double", roomType.getRoomTypes().get(0).getGroupId());

    verify(roomTypeMapper).toDomainModel(any(RoomTypeDto.class));

    verifyNoMoreInteractions(roomTypeRequestMapper);
    verifyNoMoreInteractions(roomTypeMapper);
  }

  private RoomTypeDto getRoomType() {
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

  private RoomTypeDto getRoomTypes() {
    return RoomTypeDto.builder()
        .roomTypes(Collections.singletonList(RoomTypeInformationDto.builder()
            .roomTypeCode("DOUBLE, ZPLDBL")
            .roomCategory("Double")
            .roomLabel("Double Room")
            .roomInfoLabel("")
            .roomInfo("")
            .gridImage(
                "/content/dam/pi/websites/hotelimages/gb/en/non-hotel-specific/ID4/ID4-Room-2.jpg")
            .roomDescription(
                "A super-comfy bed, a power shower and free Wi-Fi – our double rooms have everything you’ll need for a great night’s sleep.")
            .roomImage(
                "/content/dam/pi/websites/hotelimages/gb/en/non-hotel-specific/ID4/ID4-Room-2.jpg")
            .groupId("double")
            .build()))
        .build();

  }


  private RoomType mockRoomType() {
    return RoomType.builder()
        .roomTypes(Collections.singletonList(RoomTypeInformation.builder()
            .roomTypeCode(Collections.singletonList("FMQUAD"))
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

  private RoomType mockRoomTypes() {
    return RoomType.builder()
        .roomTypes(Collections.singletonList(RoomTypeInformation.builder()
            .roomTypeCode(List.of("DOUBLE", "ZPLDBL"))
            .roomCategory("Double")
            .roomLabel("Double Room")
            .roomInfoLabel("")
            .roomInfo("")
            .gridImage(
                "/content/dam/pi/websites/hotelimages/gb/en/non-hotel-specific/ID4/ID4-Room-2.jpg")
            .roomDescription(
                "A super-comfy bed, a power shower and free Wi-Fi – our double rooms have everything you’ll need for a great night’s sleep.")
            .roomImage(
                "/content/dam/pi/websites/hotelimages/gb/en/non-hotel-specific/ID4/ID4-Room-2.jpg")
            .groupId("double")
            .build()))
        .build();
  }


  private RoomTypeRequest getRoomTypeRequestGbEn() {
    return RoomTypeRequest.builder()
        .country("gb")
        .language("en")
        .brand("pi")
        .build();
  }

}
