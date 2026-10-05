package uk.co.whitbread.content.infrastructure.rest.controller.roomtype;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.springframework.test.util.AssertionErrors.assertEquals;
import static uk.co.whitbread.content.domain.model.ErrorCode.AEM_ROOM_TYPE_EXCEPTION;

import java.util.Collections;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;
import uk.co.whitbread.content.domain.model.roomtype.in.RoomTypeRequest;
import uk.co.whitbread.content.domain.model.roomtype.out.RoomType;
import uk.co.whitbread.content.domain.model.roomtype.out.RoomTypeInformation;
import uk.co.whitbread.content.domain.ports.primary.RoomTypeInPort;
import uk.co.whitbread.content.infrastructure.rest.client.aem.exceptions.AemResponseException;
import uk.co.whitbread.content.infrastructure.rest.controller.roomtype.mapper.RoomTypeDtoMapper;
import uk.co.whitbread.content.infrastructure.rest.controller.roomtype.mapper.RoomTypeRequestDtoMapper;
import uk.co.whitbread.content.infrastructure.rest.controller.roomtype.model.in.RoomTypeRequestDto;
import uk.co.whitbread.content.infrastructure.rest.controller.roomtype.model.out.RoomTypeDto;
import uk.co.whitbread.content.infrastructure.rest.controller.roomtype.model.out.RoomTypeInformationDto;

@ExtendWith(MockitoExtension.class)
class RoomTypeControllerTest {

  @InjectMocks
  private RoomTypeController roomTypeController;

  @Mock
  private RoomTypeInPort roomTypeInPort;
  @Mock
  private RoomTypeRequestDtoMapper roomTypeRequestDtoMapper;
  @Mock
  private RoomTypeDtoMapper roomTypeDtoMapper;

  @Test
  void getRoomType__ShouldReturnOK() {
    //Arrange
    var roomTypeRequestDto = getRoomTypeRequestDto();
    var roomTypeRequest = getRoomTypeRequest();
    Mockito.when(roomTypeRequestDtoMapper.toDomainModel(roomTypeRequestDto))
        .thenReturn(roomTypeRequest);
    Mockito.when(roomTypeInPort.getRoomType(roomTypeRequest))
        .thenReturn(getRoomType());
    Mockito.when(roomTypeDtoMapper.toDto(getRoomType()))
        .thenReturn(getRoomTypeDto());

    //act
    var request = roomTypeRequestDtoMapper.toDomainModel(roomTypeRequestDto);
    var roomTypeDto = roomTypeDtoMapper.toDto(
        roomTypeInPort.getRoomType(request));
    final ResponseEntity<RoomTypeDto> response = roomTypeController.getRoomTypeInformation(
        roomTypeRequestDto);

    RoomTypeInformationDto roomType = response.getBody().getRoomTypes().get(0);

    //Assert
    Assertions.assertNotNull(response);
    assertEquals(response.toString(), 200, response.getStatusCode().value());
    Assertions.assertNotNull(response.getBody());
    Assertions.assertEquals(Collections.singletonList("FMQUAD"), roomType.getRoomTypeCode());
    Assertions.assertEquals("Family", roomType.getRoomCategory());
    Assertions.assertEquals("Family Room", roomType.getRoomLabel());
    Assertions.assertEquals("", roomType.getRoomInfoLabel());
    Assertions.assertEquals("", roomType.getRoomInfo());
    Assertions.assertEquals(
        "/content/dam/pi/websites/hotelimages/gb/en/non-hotel-specific/Bedrooms/ID4-family-room.jpg",
        roomType.getGridImage());
    Assertions.assertEquals(
        "/content/dam/pi/websites/hotelimages/gb/en/non-hotel-specific/Bedrooms/ID4-family-room.jpg",
        roomType.getRoomImage());
    Assertions.assertEquals(
        "Our spacious family rooms are ideal for up to two adults plus two kids (aged 15 or under). Most feature a super-comfy double or kingsize Hypnos bed, plus a single sofa bed and a pull-out bed. We also provide cots at no extra cost.",
        roomType.getRoomDescription());
    Assertions.assertEquals("accessible", roomType.getGroupId());
  }

  @Test
  void getRoomType_ShouldReturnException() {
    //Arrange
    var exception = new AemResponseException(
        AEM_ROOM_TYPE_EXCEPTION,
        "message",
        new Exception());
    var roomTypeRequestDto = getRoomTypeRequestDto();
    var roomTypeRequest = getRoomTypeRequest();
    Mockito.when(roomTypeRequestDtoMapper.toDomainModel(roomTypeRequestDto))
        .thenReturn(roomTypeRequest);
    Mockito.when(roomTypeInPort.getRoomType(roomTypeRequest))
        .thenThrow(exception);

    //act
    var actual =
        assertThrows(AemResponseException.class,
            () -> roomTypeController.getRoomTypeInformation(getRoomTypeRequestDto()));

    //Assert
    assertThat(actual, notNullValue());
    assertThat(actual.getGlobalErrTextTemplate(),
        is(AEM_ROOM_TYPE_EXCEPTION.getMessage()));
    assertThat(actual.getMessage(), is("message"));
    assertThat(actual.getErrorCode(), is(AEM_ROOM_TYPE_EXCEPTION.getCode()));
  }

  private RoomTypeDto getRoomTypeDto() {
    return RoomTypeDto.builder()
        .roomTypes(Collections.singletonList(RoomTypeInformationDto.builder()
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

  private RoomType getRoomType() {
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

  private RoomTypeRequestDto getRoomTypeRequestDto() {
    return RoomTypeRequestDto.builder()
        .country("gb")
        .language("en")
        .brand("pi")
        .build();
  }

  private RoomTypeRequest getRoomTypeRequest() {
    return RoomTypeRequest.builder()
        .country("gb")
        .language("en")
        .brand("pi")
        .build();
  }
}
