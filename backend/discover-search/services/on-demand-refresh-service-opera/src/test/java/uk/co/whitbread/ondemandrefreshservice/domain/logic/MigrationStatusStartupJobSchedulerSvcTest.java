package uk.co.whitbread.ondemandrefreshservice.domain.logic;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.ApplicationArguments;
import uk.co.whitbread.ondemandrefreshservice.infrastructure.quartz.scheduler.cron.MigrationStatusCronJobScheduler;

@ExtendWith(MockitoExtension.class)
public class MigrationStatusStartupJobSchedulerSvcTest {

  @Mock
  private MigrationStatusCronJobScheduler migrationStatusCronJobScheduler;
  private MigrationStatusStartupJobSchedulerSvc migrationStatusStartupJobSchedulerSvc;
  private ApplicationArguments nullApplicationArguments;

  @BeforeEach
  public void setup() {
    nullApplicationArguments = null;
    migrationStatusStartupJobSchedulerSvc =
        new MigrationStatusStartupJobSchedulerSvc(migrationStatusCronJobScheduler);
  }

  @Test
  public void hotelAvailabilityCronSchedulerSvcCalledSuccessfullyTest(){
    Mockito.doNothing().when(migrationStatusCronJobScheduler).schedule();
    Assertions.assertThatCode(() -> migrationStatusStartupJobSchedulerSvc.run(nullApplicationArguments))
        .doesNotThrowAnyException();
  }

  @Test
  public void hotelAvailabilityCronSchedulerSvcCalledThrewExceptionTest(){
    Mockito.doThrow(new RuntimeException("Some error!"))
        .when(migrationStatusCronJobScheduler).schedule();
    Assertions.assertThatCode(() -> migrationStatusStartupJobSchedulerSvc.run(nullApplicationArguments))
        .doesNotThrowAnyException();
  }

}
