package uk.co.whitbread.payments.infrastructure.rest.client.hotels;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.co.whitbread.payments.domain.model.out.HotelInfo;
import uk.co.whitbread.payments.infrastructure.rest.client.hotels.mapper.HotelInfoMapper;
import uk.co.whitbread.payments.infrastructure.rest.client.hotels.model.out.HotelInfoDto;
import uk.co.whitbread.payments.infrastructure.rest.client.hotels.service.HotelInfoClient;

@ExtendWith(MockitoExtension.class)
class HotelInfoPortImplTest {

  public static final String HOTEL_CODE = "DUNGOU";
  public static final String COUNTRY = "uk";
  public static final String LANGUAGE = "en";
  public static final HotelInfo HOTEL_INFO = new HotelInfo();

  @Mock
  private HotelInfoClient hotelInfoClient;
  @Mock
  private HotelInfoMapper hotelInfoMapper;
  @InjectMocks
  private HotelInfoPortImpl underTest;

  @Test
  void findHotelPaymentDetails__success() {
    // Arrange
    HotelInfoDto dto = new HotelInfoDto();
    given(hotelInfoClient.findHotelPaymentDetails(HOTEL_CODE, COUNTRY, LANGUAGE)).willReturn(dto);
    given(hotelInfoMapper.toHotelInfoModel(dto)).willReturn(HOTEL_INFO);

    // Act
    var result = underTest.findHotelPaymentDetails(HOTEL_CODE, COUNTRY, LANGUAGE);

    // Assert
    assertThat(result, notNullValue());
    assertThat(result, equalTo(HOTEL_INFO));

    verify(hotelInfoClient).findHotelPaymentDetails(HOTEL_CODE, COUNTRY, LANGUAGE);
    verify(hotelInfoMapper).toHotelInfoModel(any());
  }
}
