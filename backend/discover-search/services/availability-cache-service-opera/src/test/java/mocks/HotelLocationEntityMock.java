package mocks;

import java.util.Arrays;
import java.util.List;
import uk.co.whitbread.availabilitycacheservice.infrastructure.entity.HotelLocationEntity;


public class HotelLocationEntityMock {

  public static List<HotelLocationEntity> buildHotelLocationEntities() {
    HotelLocationEntity locationEntity1 = HotelLocationEntity.builder()
        .placeId("ChIJdd4hrwug2EcRmSrV3Vo6llI")
        .hotelCode("LONISL")
        .build();
    HotelLocationEntity locationEntity2 = HotelLocationEntity.builder()
        .placeId("ChIJYdizgWaDcUgRH9eaSy6y5I4")
        .hotelCode("LONWAN")
        .build();
    return Arrays.asList(locationEntity1, locationEntity2);
  }

}
