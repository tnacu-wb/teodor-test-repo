package uk.co.whitbread.basket.infrastructure.rest.client.hotel;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.co.whitbread.basket.domain.model.hotel.out.HotelInfo;
import uk.co.whitbread.basket.generated.models.hotel.HotelInfoDto;
import uk.co.whitbread.basket.infrastructure.rest.client.hotel.mapper.HotelInfoMapper;
import uk.co.whitbread.basket.infrastructure.rest.client.hotel.service.HotelInfoClient;

@ExtendWith(MockitoExtension.class)
class HotelInfoOutPortImplTest {

  @InjectMocks
  private HotelInfoOutPortImpl hotelInfoOutPort;
  @Mock
  private HotelInfoClient hotelInfoClient;
  @Mock
  private HotelInfoMapper hotelInfoMapper;

  @Test
  void testGetHotelInfo_success() {
    // Arrange
    when(hotelInfoClient.getHotelInfo("TSTTST")).thenReturn(new HotelInfoDto());
    when(hotelInfoMapper.toDomainModel(any(HotelInfoDto.class))).thenReturn(mockHotelInfo());

    // Act
    var response = hotelInfoOutPort.getHotelInfo("TSTTST");

    // Assert
    assertThat(response, notNullValue());
    assertThat(response.getThreeLetterId(), is("ABC"));
  }

  private HotelInfo mockHotelInfo() {
    return HotelInfo.builder()
        .threeLetterId("ABC")
        .build();
  }

}
