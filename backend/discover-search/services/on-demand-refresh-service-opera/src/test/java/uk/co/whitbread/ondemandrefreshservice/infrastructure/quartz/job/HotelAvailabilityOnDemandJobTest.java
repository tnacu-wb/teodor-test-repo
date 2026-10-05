package uk.co.whitbread.ondemandrefreshservice.infrastructure.quartz.job;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.quartz.*;
import uk.co.whitbread.ondemandrefreshservice.domain.ports.secondary.OperaHotelAvailabilityRefreshOutPort;
import uk.co.whitbread.ondemandrefreshservice.infrastructure.entity.HotelMigrationStatusEntity;
import uk.co.whitbread.ondemandrefreshservice.infrastructure.exceptions.OnDemandRefreshBatchException;
import uk.co.whitbread.ondemandrefreshservice.infrastructure.quartz.config.SchedulerProperties;
import uk.co.whitbread.ondemandrefreshservice.infrastructure.quartz.scheduler.HotelAvailabilityOnDemandJobScheduler;
import uk.co.whitbread.ondemandrefreshservice.infrastructure.repository.read.HotelMigrationStatusJpaRepository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class HotelAvailabilityOnDemandJobTest {

    @Mock
    private Scheduler scheduler;

    @Mock
    private OperaHotelAvailabilityRefreshOutPort operaHotalAvailabilityRefreshService;

    @Mock
    private SchedulerProperties schedulerProperties;

    @Mock
    private HotelAvailabilityOnDemandJobScheduler onDemandScheduler;

    @Mock
    private JobExecutionContext jobExecutionContext;

    @Mock
    private HotelMigrationStatusJpaRepository hotelMigrationRepo;

    @InjectMocks
    private HotelAvailabilityOnDemandJob onDemandJob;

    @Captor
    private ArgumentCaptor<JobDetail> jobDetailCaptor;

    @Captor
    private ArgumentCaptor<Trigger> triggerCaptor;


    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    private static final LocalDate startDate = LocalDate.now().plusDays(1);

    private static final LocalDate endDate = startDate.plusDays(90);

    private static final Set<String> hotelIds = new HashSet<>(Arrays.asList("OXFORD"));

    private static final String JOB_NAME = "HOTEL_AVAILABILITY_CRON_JOB";

    private static final String GROUP = "HotelAvailabilityRefreshCronJob";

    private static final String START_DATE = "START_DATE";

    private static final String END_DATE = "END_DATE";

    private static final String HOTEL_IDS = "HOTEL_IDS";

    @Test
    void executeInternal_Test_Success() {
        when(hotelMigrationRepo.findByPmsSource(Mockito.anyString())).thenReturn(buildMockData());
        final JobDetail mockJob = buildMockJob();
        when(jobExecutionContext.getJobDetail()).thenReturn(mockJob);
        Assertions.assertThatCode(() -> onDemandJob.executeInternal(jobExecutionContext))
                .doesNotThrowAnyException();
    }

    @Test
    void executeInternal_Test_rescheduleOnDemandRefreshJob() {
        when(hotelMigrationRepo.findByPmsSource(Mockito.anyString())).thenReturn(buildMockData());
        final JobDetail mockJob = buildMockJob();
        when(jobExecutionContext.getJobDetail()).thenReturn(mockJob);
        doThrow(NullPointerException.class).when(operaHotalAvailabilityRefreshService).refreshHotelAvailabilities(any(), any(), any());
        assertThrows(OnDemandRefreshBatchException.class,
                () -> onDemandJob.executeInternal(jobExecutionContext),
                "Error Executing the HotelAvailabilityOnDemandJob");

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
        final JobDataMap jobDataMap = new JobDataMap();
        jobDataMap.put(START_DATE, startDate.toString());
        jobDataMap.put(END_DATE, endDate.toString());
        jobDataMap.put(HOTEL_IDS, hotelIds.toString());
        return JobBuilder
                .newJob().ofType(HotelAvailabilityRefreshCronJob.class)
                .withIdentity(jobKey)
                .setJobData(jobDataMap)
                .build();
    }
}