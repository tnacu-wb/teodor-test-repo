package uk.co.whitbread.ondemandrefreshservice.infrastructure.quartz.job;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.logstash.logback.marker.ObjectAppendingMarker;
import org.apache.commons.lang3.StringUtils;
import org.quartz.DisallowConcurrentExecution;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.springframework.scheduling.quartz.QuartzJobBean;
import org.springframework.stereotype.Component;
import uk.co.whitbread.ondemandrefreshservice.domain.model.SchedulerRequest;
import uk.co.whitbread.ondemandrefreshservice.domain.ports.secondary.OperaHotelAvailabilityRefreshOutPort;
import uk.co.whitbread.ondemandrefreshservice.infrastructure.exceptions.OnDemandRefreshBatchException;
import uk.co.whitbread.ondemandrefreshservice.infrastructure.quartz.config.SchedulerProperties;
import uk.co.whitbread.ondemandrefreshservice.infrastructure.quartz.scheduler.HotelAvailabilityOnDemandJobScheduler;

@Component
@Slf4j
@DisallowConcurrentExecution
@RequiredArgsConstructor
public class HotelAvailabilityOnDemandJob extends QuartzJobBean {

  private static final String START_DATE = "START_DATE";
  private static final String END_DATE = "END_DATE";
  private static final String HOTEL_IDS = "HOTEL_IDS";

  private final OperaHotelAvailabilityRefreshOutPort operaHotelAvailabilityRefreshOutPort;
  private final SchedulerProperties schedulerProperties;
  private final HotelAvailabilityOnDemandJobScheduler hotelAvailabilityOnDemandJobScheduler;

  @Override
  protected void executeInternal(JobExecutionContext jobExecutionContext) {
    log.info(new ObjectAppendingMarker("jobGroup", jobExecutionContext.getJobDetail().getKey().getGroup()),
        "Start HotelAvailabilityOnDemandJob Execution");
    final JobDataMap jobData = jobExecutionContext.getJobDetail().getJobDataMap();

    if (StringUtils.isBlank(jobData.getString(START_DATE)) || StringUtils.isBlank(jobData.getString(END_DATE)) ||
        StringUtils.isBlank(jobData.getString(HOTEL_IDS))) {
      log.warn("Empty/Invalid on demand job inputs. Cannot proceed with job execution.");
      return;
    }

    final LocalDate startDate = LocalDate.parse(jobData.getString(START_DATE), DateTimeFormatter.ISO_LOCAL_DATE);
    final LocalDate endDate = LocalDate.parse(jobData.getString(END_DATE), DateTimeFormatter.ISO_LOCAL_DATE);
    final String hotelIds = jobData.getString(HOTEL_IDS);
    final String jobKeyName = jobExecutionContext.getJobDetail().getKey().getName();

    log.info(
        "HotelAvailabilityOnDemandJob Name - {} with Start date - {} & end date - {} & hotel Ids - {} started on Thread {}"
            + " at current timestamp - {}",
        jobKeyName, startDate, endDate, hotelIds, Thread.currentThread().getName(),
        LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));

    refreshHotelAvailabilities(jobExecutionContext, startDate, endDate, hotelIds, jobKeyName);
  }

  private void refreshHotelAvailabilities(JobExecutionContext jobExecutionContext, LocalDate startDate,
      LocalDate endDate,
      String hotelIds, String jobKeyName) {
    Set<String> hotelIdsSet = new HashSet<>();
    try {
      if (startDate.isBefore(LocalDate.now()) || endDate.isBefore(LocalDate.now())) {
        log.warn(
            "Input start date {} & end date {} are stale. Cannot proceed with job execution.",
            startDate, endDate);
        return;
      }
      hotelIdsSet = Arrays.stream(hotelIds.split(",")).map(String::trim).collect(Collectors.toSet());
      operaHotelAvailabilityRefreshOutPort.refreshHotelAvailabilities(hotelIdsSet, startDate, endDate);
      log.info(new ObjectAppendingMarker("jobGroup", jobExecutionContext.getJobDetail().getKey().getGroup()),
          "End HotelAvailabilityOnDemandJob Execution");
    } catch (Exception e) {
      log.error("As there is a failure executing job : {}, hotelIds : {}, start-date : {}, end-date : {}, ", jobKeyName,
          hotelIdsSet, startDate, endDate);
      //Reschedule the job
      rescheduleOnDemandRefreshJob(hotelIdsSet, startDate, endDate, jobKeyName);
      throw new OnDemandRefreshBatchException("Error Executing the HotelAvailabilityOnDemandJob", e);
    }
  }

  private void rescheduleOnDemandRefreshJob(final Set<String> hotelIds, final LocalDate startDate,
      final LocalDate endDate, final String jobKeyName) {
    log.info("Rescheduling the failed job with key:{}", jobKeyName);
    final LinkedHashSet<SchedulerRequest> schedulerRequests = new LinkedHashSet<>();
    final LocalDateTime jobTriggerTime = LocalDateTime.now().plusSeconds(schedulerProperties.getJobDelay());
    SchedulerRequest schedulerRequest = buildScheduleRequest(hotelIds, startDate, endDate, jobTriggerTime);
    schedulerRequests.add(schedulerRequest);
    hotelAvailabilityOnDemandJobScheduler.scheduleOnDemandRefreshJob(schedulerRequests);
  }

  private SchedulerRequest buildScheduleRequest(Set<String> hotelIds, LocalDate startDate, LocalDate endDate,
      LocalDateTime jobTriggerTime) {
    return SchedulerRequest.builder().startDate(startDate)
        .endDate(endDate)
        .jobTriggerTime(jobTriggerTime)
        .hotelIds(hotelIds)
        .reschedule(false)
        .build();
  }

}
