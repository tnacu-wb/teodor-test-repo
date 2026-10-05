package uk.co.whitbread.content.infrastructure.rest.controller.dlp;


import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.springframework.test.util.AssertionErrors.assertEquals;

import java.util.List;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;
import uk.co.whitbread.content.domain.model.dlp.in.DlpInformationRequest;
import uk.co.whitbread.content.domain.model.dlp.out.DlpInformation;
import uk.co.whitbread.content.domain.model.dlp.out.Hotel;
import uk.co.whitbread.content.domain.ports.primary.ContentInPort;
import uk.co.whitbread.content.infrastructure.rest.controller.dlp.model.in.DlpInformationRequestDto;
import uk.co.whitbread.content.infrastructure.rest.controller.dlp.model.mapper.ControllerDlpInformationMapper;
import uk.co.whitbread.content.infrastructure.rest.controller.dlp.model.out.DlpInformationDto;
import uk.co.whitbread.content.infrastructure.rest.controller.dlp.model.out.HotelDto;

@ExtendWith(MockitoExtension.class)
class DlpInformationControllerTest {

  @InjectMocks
  DlpInformationController dlpInformationController;

  @Mock
  private ContentInPort contentInPort;

  @Mock
  ControllerDlpInformationMapper dlpInformationMapper;

  @Test
  void getDlpInformation__ShouldReturnOk() {
    //Arrange
    DlpInformationRequestDto dlpInformationRequestDto = DlpInformationRequestDto.builder()
        .country("gb").language("en").dlpPath("/england/bedfordshire/luton").build();

    DlpInformationRequest dlpInformationRequest = DlpInformationRequest.builder()
        .country("gb").language("en").dlpPath("/england/bedfordshire/luton").build();

    Mockito.when(dlpInformationMapper.toDomainModel(dlpInformationRequestDto))
        .thenReturn(dlpInformationRequest);
    Mockito.when(contentInPort.getDlpInformation(dlpInformationRequest)).thenReturn(getDlpInformation());
    Mockito.when(dlpInformationMapper.toDto(getDlpInformation())).thenReturn(getDlpInformationDto());

    //Act
    final ResponseEntity<DlpInformationDto> response =
        dlpInformationController.getDlpInformation(dlpInformationRequestDto);

    //Assert
    Assertions.assertNotNull(response);
    assertEquals(response.toString(), 200, response.getStatusCode().value());
    final DlpInformationDto dlpInformationDto = response.getBody();
    assertThat(dlpInformationDto, notNullValue());
    final List<HotelDto> hotels = dlpInformationDto.getHotels();
    assertThat(hotels, hasSize(2));
    final HotelDto hotelDto1 = hotels.get(0);
    assertThat(hotelDto1.getCode(), is("ON"));
    assertThat(hotelDto1.getOrder(), is(1));
    final HotelDto hotelDto2 = hotels.get(1);
    assertThat(hotelDto2.getCode(), is("TW"));
    assertThat(hotelDto2.getOrder(), is(2));
  }

  private DlpInformationDto getDlpInformationDto() {
    HotelDto hotelDto1 = HotelDto.builder().code("ON").order(1).build();
    HotelDto hotelDto2 = HotelDto.builder().code("TW").order(2).build();
    return DlpInformationDto.builder().hotels(List.of(hotelDto1, hotelDto2)).build();
  }

  private DlpInformation getDlpInformation() {
    Hotel hotel1 = Hotel.builder().code("ON").order(1).build();
    Hotel hotel2 = Hotel.builder().code("TW").order(2).build();
    return DlpInformation.builder().hotels(List.of(hotel1, hotel2))
        .build();

  }
}