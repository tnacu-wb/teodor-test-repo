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
import uk.co.whitbread.ondemandrefreshservice.domain.ports.secondary.OperaHotelAvailabilityRefreshOutPort;
import uk.co.whitbread.ondemandrefreshservice.infrastructure.entity.HotelMigrationStatusEntity;
import uk.co.whitbread.ondemandrefreshservice.infrastructure.exceptions.OnDemandRefreshBatchException;
import uk.co.whitbread.ondemandrefreshservice.infrastructure.repository.read.HotelMigrationStatusJpaRepository;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class HotelAvailabilityRefreshCronJobTest {

  @Mock
  private OperaHotelAvailabilityRefreshOutPort operaHotalAvailabilityRefreshService;

  @Mock
  private HotelMigrationStatusJpaRepository hotelMigrationRepo;

  @Mock
  private JobExecutionContext jobExecutionContext;

  private HotelAvailabilityRefreshCronJob hotelRefreshCronJob;

  private static final String JOB_NAME = "HOTEL_AVAILABILITY_CRON_JOB";

  private static final String GROUP = "HotelAvailabilityRefreshCronJob";

  @BeforeEach
  void setUp() throws Exception {
    hotelRefreshCronJob = new HotelAvailabilityRefreshCronJob(operaHotalAvailabilityRefreshService,hotelMigrationRepo);
  }

  @AfterEach
  void tearDown() throws Exception {
  }

  @Test
  void executeInternal_Test_Success() {
    when(hotelMigrationRepo.findByPmsSource(Mockito.anyString())).thenReturn(buildMockData());
    final JobDetail mockJob = buildMockJob();
    when(jobExecutionContext.getJobDetail()).thenReturn(mockJob);
    Assertions.assertThatCode(() -> hotelRefreshCronJob.executeInternal(jobExecutionContext))
        .doesNotThrowAnyException();
  }

  @Test
  void executeInternal_Test_NoMigratedHotels_Exception() {
    final Set<HotelMigrationStatusEntity> emptyMigratedHotels = new HashSet<>();
    when(hotelMigrationRepo.findByPmsSource(Mockito.anyString())).thenReturn(emptyMigratedHotels);
    final JobDetail mockJob = buildMockJob();
    when(jobExecutionContext.getJobDetail()).thenReturn(mockJob);

    assertThrows(OnDemandRefreshBatchException.class,
            () -> hotelRefreshCronJob.executeInternal(jobExecutionContext),
            "No migrated hotels found in the DB");

  }

  private Set<HotelMigrationStatusEntity> buildMockData(){
    final Set<HotelMigrationStatusEntity> migratedHotels = new HashSet<>();
    migratedHotels.add(mockHotelMigrationStatusEntity("TKINPT", "OPERA", true));
    migratedHotels.add(mockHotelMigrationStatusEntity("PDUBAI", "OPERA", false));
    return migratedHotels;
  }

  private HotelMigrationStatusEntity mockHotelMigrationStatusEntity(final String hotelCode, final String pmsSource, final boolean onSale){
    return HotelMigrationStatusEntity.builder().hotelCode(hotelCode).pmsSource(pmsSource).onSale(onSale).updatedOn(
        LocalDateTime.now()).build();
  }

  private JobDetail buildMockJob(){
    final JobKey jobKey = new JobKey(JOB_NAME, GROUP);
    return JobBuilder
        .newJob().ofType(HotelAvailabilityRefreshCronJob.class)
        .withIdentity(jobKey)
        .build();
  }

}