package uk.co.whitbread.wallet.infrastructure.rest.client.content;

import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrowsExactly;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;
import static uk.co.whitbread.wallet.ErrorCode.DIGITAL_HOTEL_INFO_NOT_FOUND_EXCEPTION;
import static uk.co.whitbread.wallet.infrastructure.rest.client.config.WalletConstants.PREMIER_INN_DE_LINK;
import static uk.co.whitbread.wallet.infrastructure.rest.client.config.WalletConstants.PREMIER_INN_LINK;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.co.whitbread.basket.generated.models.content.AddressDto;
import uk.co.whitbread.basket.generated.models.content.HotelInformationDto;
import uk.co.whitbread.wallet.domain.exception.HotelInfoNotFoundException;
import uk.co.whitbread.wallet.domain.model.out.HotelInfo;
import uk.co.whitbread.wallet.infrastructure.rest.client.content.mapper.HotelInfoMapper;
import uk.co.whitbread.wallet.infrastructure.rest.client.content.service.ContentClient;

@ExtendWith({MockitoExtension.class})
class ContentOutPortImplTest {

  @InjectMocks
  private ContentOutPortImpl contentOutPortImpl;

  @Mock
  private ContentClient contentClient;
  @Mock
  private HotelInfoMapper hotelInfoMapper;

  @Test
  void getHotelInformation() {
    var addr = new AddressDto();
    addr.setCountry("gb");
    var hotelInformationDto = new HotelInformationDto();
    hotelInformationDto.setHotelId("FRAMTI");
    hotelInformationDto.setBrand("PID");
    hotelInformationDto.setAddress(addr);

    when(contentClient.getHotelInformation("gb", "en", "FRAMTI"))
        .thenReturn(hotelInformationDto);
    when(hotelInfoMapper.toHotelInfoModel(hotelInformationDto)).thenReturn(
        HotelInfo.builder().hotelId("FRAMTI").brand("PID").country("gb").build());

    var result = contentOutPortImpl.getHotelInformation("gb", "en", "FRAMTI");
    assertThat(result, instanceOf(HotelInfo.class));
    assertEquals(result.getHotelId(), hotelInformationDto.getHotelId());
    assertEquals(result.getBrand(), hotelInformationDto.getBrand());
    assertTrue(result.getLinks().startsWith(PREMIER_INN_LINK));
    assertEquals("gb", result.getCountry());
  }

  @Test
  void getHotelInformationForDE() {
    when(contentClient.getHotelInformation("de", "de", "FRAMTI"))
        .thenReturn(new HotelInformationDto());
    when(hotelInfoMapper.toHotelInfoModel(new HotelInformationDto())).thenReturn(
        HotelInfo.builder().hotelId("FRAMTI").country("de").build());

    var result = contentOutPortImpl.getHotelInformation("de", "de", "FRAMTI");
    assertTrue(result.getLinks().startsWith(PREMIER_INN_DE_LINK));
    assertEquals("de", result.getCountry());
  }

  @Test
  void getHotelInformationThrowsNotFoundException() {

    when(contentClient.getHotelInformation("gb", "en", "FRAMTI"))
        .thenReturn(null);

    var result = assertThrowsExactly(HotelInfoNotFoundException.class,
        () -> contentOutPortImpl.getHotelInformation("gb", "en", "FRAMTI"));
    assertEquals(DIGITAL_HOTEL_INFO_NOT_FOUND_EXCEPTION.getCode(),
        result.getErrorCode());
  }
}
