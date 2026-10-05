package uk.co.whitbread.availabilitycacheservice.infrastructure.adapters.hotelprice;

import static mocks.HotelLocationEntityMock.buildHotelLocationEntities;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatExceptionOfType;
import static org.mockito.Mockito.doThrow;

import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.co.whitbread.availabilitycacheservice.domain.ports.secondary.hotelprice.HotelLocationPersistencePort;
import uk.co.whitbread.availabilitycacheservice.infrastructure.entity.HotelLocationEntity;
import uk.co.whitbread.availabilitycacheservice.infrastructure.repository.read.HotelLocationJpaRepository;

@ExtendWith(MockitoExtension.class)

class HotelLocationPersistenceAdapterTest {

  @Mock
  private HotelLocationJpaRepository hotelLocationJpaRepository;

  private HotelLocationPersistencePort hotelLocationPersistencePort;

  @BeforeEach
  void setup() {
    hotelLocationPersistencePort = new HotelLocationPersistenceAdapter(hotelLocationJpaRepository);
  }

  @Test
  void shouldSaveAndUpdateHotelLocation() {
    List<HotelLocationEntity> hotelLocationEntities = buildHotelLocationEntities();
    hotelLocationPersistencePort.update(hotelLocationEntities);

    Mockito.verify(hotelLocationJpaRepository).deleteAllInBatch();
    Mockito.verify(hotelLocationJpaRepository).saveAll(hotelLocationEntities);

  }

  @Test
  void saveAndUpdateShouldReturnError() {
    doThrow(new RuntimeException("Error while trying to update HOTEL_LOCATION table"))
        .when(hotelLocationJpaRepository).deleteAllInBatch();

    assertThatExceptionOfType(RuntimeException.class).isThrownBy(
        () -> hotelLocationPersistencePort.update(buildHotelLocationEntities()));
  }

}
