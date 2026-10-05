package uk.co.whitbread.availabilitycacheservice.infrastructure.adapters.hotelprice;

import static org.assertj.core.api.AssertionsForClassTypes.assertThatExceptionOfType;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.co.whitbread.availabilitycacheservice.domain.model.snowdrop.HotelDetails;
import uk.co.whitbread.availabilitycacheservice.domain.model.snowdrop.ItemLatLon;
import uk.co.whitbread.availabilitycacheservice.domain.ports.secondary.SnowdropHotelLookupPort;
import uk.co.whitbread.availabilitycacheservice.infrastructure.exceptions.SnowdropLookupException;
import uk.co.whitbread.availabilitycacheservice.infrastructure.feign.SnowdropFeignClient;
import uk.co.whitbread.availabilitycacheservice.infrastructure.mapper.snowdrop.HotelDetailsMapper;

@ExtendWith(MockitoExtension.class)
class SnowdropHotelLookupServiceTest {

  private final static String PLACEID = "ChIJdd4hrwug2EcRmSrV3Vo6llI";
  private final static int RADIUS = 50;
  @Mock
  private SnowdropFeignClient snowdropFeignClient;
  @Mock
  private HotelDetailsMapper hotelDetailsMapper;
  private SnowdropHotelLookupPort snowdropHotelLookupPort;

  @BeforeEach
  void setup() {
    snowdropHotelLookupPort = new SnowdropHotelLookupService(snowdropFeignClient, hotelDetailsMapper);
  }

  @Test
  void shouldGetHotelsFromLocation() {
    List<HotelDetails> hotelDetailsList = buildHotelDetails();
    when(snowdropFeignClient.getHotelsFromSnowdrop(PLACEID, "50mi")).thenReturn(new ArrayList<>());
    when(hotelDetailsMapper.toModel(any())).thenReturn(hotelDetailsList);

    assertEquals(snowdropHotelLookupPort.getHotelsFromLocation(PLACEID, RADIUS), hotelDetailsList);
  }

  @Test
  void shouldThrowExceptionWhenLookingUpHotels() {
    doThrow(new SnowdropLookupException(
        "Failed to call snowdrop to get hotels by placeId=ChIJdd4hrwug2EcRmSrV3Vo6llI and radius=50mi"))
        .when(snowdropFeignClient).getHotelsFromSnowdrop(PLACEID, "50mi");

    assertThatExceptionOfType(SnowdropLookupException.class).isThrownBy(
        () -> snowdropHotelLookupPort.getHotelsFromLocation(PLACEID, RADIUS));
    verify(snowdropFeignClient).getHotelsFromSnowdrop(PLACEID, "50mi");
  }


  private List<HotelDetails> buildHotelDetails() {
    HotelDetails hotelDetails = HotelDetails.builder()
        .code("LONLEI")
        .distance(754.0)
        .name("London Leicester Square")
        .brand("PI")
        .location(new ItemLatLon(51.511143, -0.13035))
        .build();
    return Arrays.asList(hotelDetails);
  }

}
