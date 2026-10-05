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
class HotelOpeningSoonSchedulerTests {

  @InjectMocks
  HotelOpeningSoonScheduler hotelOpeningSoonScheduler;

  @Mock
  ContentInPort contentInPort;

  @Test
  void test_updateHotelsOpeningSoonCacheJob() {
    //Act
    hotelOpeningSoonScheduler.updateHotelsOpeningSoonCacheJob();

    //Assert
    verify(contentInPort, times(1)).updateHotelsOpeningSoonCache();

  }

}
