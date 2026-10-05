package uk.co.whitbread.ondemandrefreshservice.domain.logic;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.ApplicationArguments;
import uk.co.whitbread.ondemandrefreshservice.infrastructure.quartz.scheduler.cron.HotelAvailabilityCronJobScheduler;

@ExtendWith(MockitoExtension.class)
public class StartupJobSchedulerSvcTest {

  @Mock
  private HotelAvailabilityCronJobScheduler hotelAvailabilityCronScheduler;
  private StartupJobSchedulerSvc startupJobSchedulerSvc;
  private ApplicationArguments nullApplicationArguments;

  @BeforeEach
  public void setup() {
    nullApplicationArguments = null;
    startupJobSchedulerSvc = new StartupJobSchedulerSvc(hotelAvailabilityCronScheduler);
  }

  @Test
  public void hotelAvailabilityCronSchedulerSvcCalledSuccessfullyTest(){
    Mockito.doNothing().when(hotelAvailabilityCronScheduler).schedule();
    Assertions.assertThatCode(() -> startupJobSchedulerSvc.run(nullApplicationArguments))
        .doesNotThrowAnyException();
  }

  @Test
  public void hotelAvailabilityCronSchedulerSvcCalledThrewExceptionTest(){
    Mockito.doThrow(new RuntimeException("Some error!")).when(hotelAvailabilityCronScheduler).schedule();
    Assertions.assertThatCode(() -> startupJobSchedulerSvc.run(nullApplicationArguments))
        .doesNotThrowAnyException();
  }

}
