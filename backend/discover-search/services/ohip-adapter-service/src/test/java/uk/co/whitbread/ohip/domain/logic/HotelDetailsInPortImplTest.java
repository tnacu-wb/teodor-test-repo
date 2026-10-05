package uk.co.whitbread.ohip.domain.logic;

import java.util.List;
import java.util.Set;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.co.whitbread.ohip.domain.logic.utils.HotelDetailsInPortImpl;
import uk.co.whitbread.ohip.domain.model.opera.out.HotelStatus;
import uk.co.whitbread.ohip.domain.ports.secondary.HotelDetailsOutPort;

@ExtendWith(MockitoExtension.class)
class HotelDetailsInPortImplTest {

  @Mock
  private HotelDetailsOutPort hotelDetailsOutPort;

  @InjectMocks
  private HotelDetailsInPortImpl hotelDetailsInPort;

  @Test
  void testGetHotelsMigrationStatus() {
    var hotelIds = Set.of("TSTTST1", "TSTTST2");
    var details1 = HotelStatus.builder()
        .hotelId("TSTTST1")
        .pmsSource("CODE1")
        .onSale(true)
        .build();
    var details2 = HotelStatus.builder()
        .hotelId("TSTTST2")
        .pmsSource("CODE2")
        .onSale(false)
        .build();
    var hotelStatusList = List.of(details1, details2);

    Mockito.when(hotelDetailsOutPort.getHotelsMigrationStatus(hotelIds)).thenReturn(hotelStatusList);

    var response = hotelDetailsInPort.getHotelsMigrationStatus(hotelIds);

    Assertions.assertNotNull(response);
    Assertions.assertEquals(2, response.size());
    Assertions.assertEquals("TSTTST1", response.get(0).getHotelId());
    Assertions.assertEquals("TSTTST2", response.get(1).getHotelId());
    Assertions.assertEquals("CODE1", response.get(0).getPmsSource());
    Assertions.assertEquals("CODE2", response.get(1).getPmsSource());
    Assertions.assertTrue(response.get(0).isOnSale());
    Assertions.assertFalse(response.get(1).isOnSale());
  }

}
