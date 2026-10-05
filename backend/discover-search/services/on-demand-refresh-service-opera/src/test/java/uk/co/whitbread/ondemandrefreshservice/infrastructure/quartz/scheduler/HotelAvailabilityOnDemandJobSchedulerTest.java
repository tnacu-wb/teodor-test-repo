package uk.co.whitbread.ondemandrefreshservice.infrastructure.quartz.scheduler;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.quartz.*;
import uk.co.whitbread.ondemandrefreshservice.domain.model.SchedulerRequest;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.verify;
import static uk.co.whitbread.ondemandrefreshservice.infrastructure.util.LocalDateTimeToDateConverter.convertToDate;

@ExtendWith(MockitoExtension.class)
public class HotelAvailabilityOnDemandJobSchedulerTest {

  @Mock
  private Scheduler scheduler;

  @Captor
  private ArgumentCaptor<JobDetail> jobDetailCaptor;

  @Captor
  private ArgumentCaptor<Trigger> triggerCaptor;

  @InjectMocks
  private HotelAvailabilityOnDemandJobScheduler onDemandScheduler;

  private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

  private static final LocalDate startDate = LocalDate.now().plusDays(1);

  private static final LocalDate endDate = startDate.plusDays(90);

  private static final Set<String> hotelIds = new HashSet<>(Arrays.asList("OXFORD"));

  @BeforeEach
  public void setUp() throws Exception {
    onDemandScheduler = new HotelAvailabilityOnDemandJobScheduler(scheduler);
  }

  @AfterEach
  public void tearDown() throws Exception {
  }

  @Test
  public void scheduleOnDemandRefreshJobTest_Success() throws SchedulerException {
    final LocalDateTime jobTriggerTime = LocalDateTime.now().plusSeconds(5);
    final SchedulerRequest request = SchedulerRequest.builder()
        .startDate(startDate)
        .endDate(endDate)
        .jobTriggerTime(jobTriggerTime)
        .hotelIds(hotelIds).build();
    onDemandScheduler.scheduleOnDemandRefreshJob(new HashSet<>(Arrays.asList(request)));
    verify(scheduler, Mockito.times(1)).checkExists(Mockito.any(JobKey.class));
    verify(scheduler).scheduleJob(jobDetailCaptor.capture(), triggerCaptor.capture());
    final String jobKeyName = jobDetailCaptor.getValue().getKey().getName();
    assertNotNull(jobKeyName);
    Assertions.assertThat(triggerCaptor.getValue().getKey().getName()).isEqualTo(jobKeyName+"_Trigger");
    Assertions.assertThat(triggerCaptor.getValue().getStartTime()).isEqualTo(
        convertToDate(jobTriggerTime));
  }
}