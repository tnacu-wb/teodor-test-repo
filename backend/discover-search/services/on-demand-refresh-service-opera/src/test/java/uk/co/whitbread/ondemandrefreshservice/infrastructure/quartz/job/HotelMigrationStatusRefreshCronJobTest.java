package uk.co.whitbread.ondemandrefreshservice.infrastructure.quartz.job;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.quartz.JobBuilder;
import org.quartz.JobDetail;
import org.quartz.JobExecutionContext;
import org.quartz.JobKey;
import uk.co.whitbread.ondemandrefreshservice.domain.ports.secondary.HotelMigrationStatusRefreshOutPort;

import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class HotelMigrationStatusRefreshCronJobTest {

  @Mock
  private HotelMigrationStatusRefreshOutPort hotelMigrationStatusRefreshOutPort;

  @Mock
  private JobExecutionContext jobExecutionContext;

  private HotelMigrationStatusRefreshCronJob hotelMigrationStatusRefreshCronJob;

  private static final String JOB_NAME = "MIGRATION_STATUS_REFRESH_CRON_JOB";

  private static final String GROUP = "HotelMigrationStatusRefreshCronJob";

  @BeforeEach
  void setUp() throws Exception {
    hotelMigrationStatusRefreshCronJob =
        new HotelMigrationStatusRefreshCronJob(hotelMigrationStatusRefreshOutPort);
  }

  @AfterEach
  void tearDown() throws Exception {
  }

  @Test
  void executeInternal_Test_Success() {
    Mockito.doNothing().when(hotelMigrationStatusRefreshOutPort).refreshOperaHotelMigrationStatus();
    final JobDetail mockJob = buildMockJob();
    when(jobExecutionContext.getJobDetail()).thenReturn(mockJob);
    Assertions.assertThatCode(() -> hotelMigrationStatusRefreshCronJob.executeInternal(jobExecutionContext))
        .doesNotThrowAnyException();
  }

  private JobDetail buildMockJob(){
    final JobKey jobKey = new JobKey(JOB_NAME, GROUP);
    return JobBuilder
        .newJob().ofType(HotelAvailabilityRefreshCronJob.class)
        .withIdentity(jobKey)
        .build();
  }

}
