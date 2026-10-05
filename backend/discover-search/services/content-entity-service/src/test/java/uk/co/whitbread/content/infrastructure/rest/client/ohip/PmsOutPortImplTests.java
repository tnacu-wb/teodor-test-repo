package uk.co.whitbread.content.infrastructure.rest.client.ohip;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.co.whitbread.content.domain.model.hotel.out.HotelInfo;
import uk.co.whitbread.content.infrastructure.rest.client.aem.mapper.HotelInfoMapper;
import uk.co.whitbread.content.infrastructure.rest.client.content.exceptions.OhipException;
import uk.co.whitbread.content.infrastructure.rest.client.ohip.model.out.HotelInfoDto;

@ExtendWith(MockitoExtension.class)
class OhipOutPortImplTests {

  @InjectMocks
  private OhipOutPortImpl ohipOutPort;

  @Mock
  private OhipAdapterClient ohipAdapterClient;

  @Mock
  private HotelInfoMapper hotelInfoMapper;

  @Test
  void getHotelInfo_ShouldReturnOK() {
    //Arrange
    when(ohipAdapterClient.getHotelInfo(any())).thenReturn(mockHotelInfoDto());
    when(hotelInfoMapper.toDomainModel(any())).thenReturn(mockHotelInfo());

    //Act
    var hotelInfo = ohipOutPort.getHotelInfo("MANOLD");

    //Assert
    assertThat(hotelInfo, notNullValue());

  }

  @Test
  void getHotelInfo_ShouldReturnResourceNotFound() {
    //Arrange
    var errorCode = 1;
    var message = "message";
    var debugMess = "debug";
    when(ohipAdapterClient.getHotelInfo(any())).thenThrow(
        new OhipException(message, debugMess, new Exception(), errorCode));

    //Act
    var actual = assertThrows(OhipException.class, () -> ohipOutPort.getHotelInfo("MANOLD"));

    //Assert
    assertThat(actual, notNullValue());
    assertThat(actual.getGlobalErrTextTemplate(), is(message));
    assertThat(actual.getMessage(), is(debugMess));
    assertThat(actual.getErrorCode(), is(errorCode));
  }

  private HotelInfoDto mockHotelInfoDto() {
    return HotelInfoDto.builder()
        .currencyCode("GBP")
        .languageCode("E")
        .hotelTimeZone("Europe/London")
        .checkInTime("01/01/1970, 15:00")
        .checkOutTime("01/01/1970, 12:00")
        .build();
  }

  private HotelInfo mockHotelInfo() {
    return HotelInfo.builder()
        .currencyCode("GBP")
        .languageCode("E")
        .hotelTimeZone("Europe/London")
        .checkInTime("01/01/1970, 15:00")
        .checkOutTime("01/01/1970, 12:00")
        .build();
  }

}
