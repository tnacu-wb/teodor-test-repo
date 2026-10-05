package uk.co.whitbread.content.infrastructure.scheduler;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.co.whitbread.content.domain.ports.primary.ContentInPort;

@ExtendWith(MockitoExtension.class)
class HotelSearchFiltersSchedulerTests {

  @InjectMocks
  HotelSearchFiltersScheduler hotelSearchFiltersScheduler;

  @Mock
  ContentInPort contentInPort;

  @Test
  void test_updateHotelsFiltersCacheJob() {
    //Act
    hotelSearchFiltersScheduler.updateHotelsFiltersCacheJob();

    //Assert
    verify(contentInPort, times(1)).updateHotelsFacilitiesCache();
  }

}
