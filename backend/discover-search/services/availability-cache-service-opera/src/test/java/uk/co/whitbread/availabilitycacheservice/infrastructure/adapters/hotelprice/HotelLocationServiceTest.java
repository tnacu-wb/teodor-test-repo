package uk.co.whitbread.availabilitycacheservice.infrastructure.adapters.hotelprice;

import static mocks.HotelLocationEntityMock.buildHotelLocationEntities;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatExceptionOfType;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.Collections;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.co.whitbread.availabilitycacheservice.domain.model.snowdrop.HotelDetails;
import uk.co.whitbread.availabilitycacheservice.domain.ports.secondary.SnowdropHotelLookupPort;
import uk.co.whitbread.availabilitycacheservice.domain.ports.secondary.hotelprice.AemLocationPort;
import uk.co.whitbread.availabilitycacheservice.domain.ports.secondary.hotelprice.HotelLocationPersistencePort;
import uk.co.whitbread.availabilitycacheservice.domain.ports.secondary.hotelprice.HotelLocationPort;
import uk.co.whitbread.availabilitycacheservice.infrastructure.model.aem.Location;

@ExtendWith(MockitoExtension.class)

class HotelLocationServiceTest {

  @Mock
  private HotelLocationPersistencePort hotelLocationPersistencePort;

  @Mock
  private SnowdropHotelLookupPort snowdropHotelLookupPort;

  @Mock
  private AemLocationPort aemLocationPort;

  private HotelLocationPort hotelLocationPort;

  @BeforeEach
  void setup() {
    hotelLocationPort = new HotelLocationService(hotelLocationPersistencePort, snowdropHotelLookupPort,
        aemLocationPort);

    Location location1 = Location.builder().name("London").placeId("ChIJdd4hrwug2EcRmSrV3Vo6llI").radiusInMiles(24)
        .build();
    Location location2 = Location.builder().name("Bristol").placeId("ChIJYdizgWaDcUgRH9eaSy6y5I4").radiusInMiles(30)
        .build();

    when(aemLocationPort.getAemLocations()).thenReturn(Arrays.asList(location1, location2));
    when(snowdropHotelLookupPort.getHotelsFromLocation("ChIJdd4hrwug2EcRmSrV3Vo6llI", 24))
        .thenReturn(Collections.singletonList(HotelDetails.builder().code("LONISL").build()));
    when(snowdropHotelLookupPort.getHotelsFromLocation("ChIJYdizgWaDcUgRH9eaSy6y5I4", 30))
        .thenReturn(Collections.singletonList(HotelDetails.builder().code("LONWAN").build()));
  }

  @Test
  void testUpdateHotelLocationSuccess() {
    hotelLocationPort.updateHotelLocations();
    verify(hotelLocationPersistencePort, times(1)).update(buildHotelLocationEntities());
  }

  @Test
  void testUpdateHotelLocationWithError() {
    doThrow(new RuntimeException("Error while trying to update HOTEL_LOCATION table"))
        .when(hotelLocationPersistencePort).update(buildHotelLocationEntities());

    assertThatExceptionOfType(RuntimeException.class).isThrownBy(() -> hotelLocationPort.updateHotelLocations());
  }

}
