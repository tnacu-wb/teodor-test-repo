package uk.co.whitbread.ondemandrefreshservice.domain.logic;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import uk.co.whitbread.ondemandrefreshservice.domain.model.SchedulerRequest;
import uk.co.whitbread.ondemandrefreshservice.domain.ports.primary.RefreshHotelAvailabilityInPort;
import uk.co.whitbread.ondemandrefreshservice.domain.ports.secondary.HotelMigrationStatusRefreshOutPort;
import uk.co.whitbread.ondemandrefreshservice.infrastructure.entity.HotelMigrationStatusEntity;
import uk.co.whitbread.ondemandrefreshservice.infrastructure.model.OnDemandProcessResponse;
import uk.co.whitbread.ondemandrefreshservice.infrastructure.quartz.config.SchedulerProperties;
import uk.co.whitbread.ondemandrefreshservice.infrastructure.quartz.scheduler.HotelAvailabilityOnDemandJobScheduler;
import uk.co.whitbread.ondemandrefreshservice.infrastructure.repository.read.HotelMigrationStatusJpaRepository;
import uk.co.whitbread.ondemandrefreshservice.infrastructure.repository.write.HotelMigrationStatusJpaRepositoryWriter;
import uk.co.whitbread.ondemandrefreshservice.infrastructure.util.SanitizingUtils;
import uk.co.whitbread.ondemandrefreshservice.infrastructure.validator.DateFormatValidator;
import uk.co.whitbread.ondemandrefreshservice.infrastructure.validator.HotelIdValidator;

import static uk.co.whitbread.ondemandrefreshservice.infrastructure.util.SanitizingUtils.sanitize;

@Component
@Slf4j
@RequiredArgsConstructor
public class ProcessRefreshHotelAvailability implements RefreshHotelAvailabilityInPort {

  private final SchedulerProperties schedulerProperties;
  private final HotelAvailabilityOnDemandJobScheduler hotelAvailabilityOnDemandJobScheduler;
  private final HotelMigrationStatusRefreshOutPort hmsRefreshOutPort;
  private final DateFormatValidator dateFormatValidator;
  private final HotelIdValidator hotelIdValidator;
  private final HotelMigrationStatusJpaRepository hmsJpaRepositoryReader;
  private static final String OPERA_PMS_SRC = "OPERA";

  private final HotelMigrationStatusJpaRepositoryWriter hmsJpaRepositoryWriter;

  @Override
  public OnDemandProcessResponse refreshOnDemandWithDates(Set<String> requestedHotelIds, String startDate,
      String endDate) {
    //Sanitize hotelIds to prevent log injection
    Set<String> sanitizedHotelIds = requestedHotelIds.stream()
            .map(SanitizingUtils::sanitize)
            .collect(Collectors.toSet());
    log.info("On Demand hotel availabilities refresh for requestedHotelIds : {}, startDate : {}, endDate : {}",
        sanitizedHotelIds, sanitize(startDate), sanitize(endDate));
    final OnDemandProcessResponse onDemandProcessResponse = validateInputData(requestedHotelIds, startDate, endDate);
    if (onDemandProcessResponse.getStatus() == null) {
      final Set<String> hotelIds = validateAndFilterOperaHotels(requestedHotelIds);
      if (!hotelIds.isEmpty()) {
        log.info("On demand refresh run for Opera hotels: {}", getHotelsListAsStringValue(hotelIds));
        processDatesAndSchedule(hotelIds, onDemandProcessResponse.getStartDate(),
            onDemandProcessResponse.getEndDate());
        onDemandProcessResponse.setMessage(
            "On demand refresh run for Opera hotels: " + getHotelsListAsStringValue(hotelIds));
      }
      return setOnDemandRefreshStatus(onDemandProcessResponse, requestedHotelIds, hotelIds);
    }
    return onDemandProcessResponse;
  }

  private Set<String> validateAndFilterOperaHotels(final Set<String> requestedHotelIds) {
    final Set<String> operaHotelsFrmDB =
        requestedHotelIds.stream().filter(hotel -> getOperaHotels().contains(hotel)).collect(Collectors.toSet());
    final Set<String> hotelIdsInput = new HashSet<>(requestedHotelIds);
    hotelIdsInput.removeAll(operaHotelsFrmDB);
    final Set<HotelMigrationStatusEntity> hotelMigStatusEntityLst = new HashSet<>();
    for (final String requestedHotelId : hotelIdsInput) {
      final HotelMigrationStatusEntity hotelMigStatusEntity = hmsRefreshOutPort.getHotelMigrationStatus(requestedHotelId);
      if (hotelMigStatusEntity != null && hotelMigStatusEntity.getPmsSource().equalsIgnoreCase(OPERA_PMS_SRC)) {
        hotelMigStatusEntityLst.add(hotelMigStatusEntity);
        operaHotelsFrmDB.add(hotelMigStatusEntity.getHotelCode());
      }
    }
    //save the latest data in DB
    log.info("Updating hotel migration status in DB for hotel codes:{}", hotelMigStatusEntityLst);
    hmsJpaRepositoryWriter.saveAll(hotelMigStatusEntityLst);
    return operaHotelsFrmDB;
  }


  private String getHotelsListAsStringValue(final Set<String> hotelValues) {
    return hotelValues.stream().map(Object::toString).collect(Collectors.joining(","));
  }

  private OnDemandProcessResponse setOnDemandRefreshStatus(OnDemandProcessResponse onDemandProcessResponse,
      final Set<String> requestedHotelIds, final Set<String> hotelIds) {
    if (hotelIds.isEmpty()) {
      log.info("On demand job is not run because all the hotels in the input are BART hotels: {}",
          getHotelsListAsStringValue(requestedHotelIds));
      onDemandProcessResponse.setMessage(
          "On demand job is not run because all the hotels in the input are BART hotels: " +
              getHotelsListAsStringValue(requestedHotelIds));
      onDemandProcessResponse.setStatus(HttpStatus.OK);
    } else if (requestedHotelIds.equals(hotelIds)) {
      onDemandProcessResponse.setStatus(HttpStatus.ACCEPTED);
    } else {
      onDemandProcessResponse.setStatus(HttpStatus.PARTIAL_CONTENT);
    }
    return onDemandProcessResponse;
  }

  private void processDatesAndSchedule(Set<String> hotelIds, LocalDate startDate, LocalDate endDate) {
    // Get configured interval period
    final int intervalDays = schedulerProperties.getInterval();
    log.info("Configured date range for on-demand hotel availabilities refresh job: {}", intervalDays);

    // If given dates are stale dates then return without process
    if (startDate.isBefore(LocalDate.now()) || endDate.isBefore(LocalDate.now())) {
      log.warn("Received Start Date:{} & End Date: {} is in the past. Cannot refresh hotel availabilities", startDate,
          endDate);
      return;
    }
    // Calculate total days from the given date range
    final long totalDays = ChronoUnit.DAYS.between(startDate, endDate);
    // Default Date range to refresh configured as interval
    if (totalDays <= intervalDays) {
      // If the current date range days count less than configured interval days then add in SET and complete
      log.info("refreshOnDemandWithDates less than configured interval days startDate {} endDate {}",
          startDate, endDate);
      final LinkedHashSet<SchedulerRequest> schedulerRequests = new LinkedHashSet<>();
      final LocalDateTime jobTriggerTime = LocalDateTime.now().plusSeconds(schedulerProperties.getJobDelay());
      SchedulerRequest schedulerRequest = buildScheduleRequest(hotelIds, startDate, endDate, jobTriggerTime);
      schedulerRequests.add(schedulerRequest);
      hotelAvailabilityOnDemandJobScheduler.scheduleOnDemandRefreshJob(schedulerRequests);

    } else {
      // If given date range greater than configured interval days then Split to date ranges
      splitDateRangesWithIntervalAndSchedule(hotelIds, startDate, endDate, intervalDays);
    }
  }

  private void splitDateRangesWithIntervalAndSchedule(final Set<String> hotelIds, final LocalDate inputStartDate,
      final LocalDate inputEndDate, final int intervalDays) {
    log.info("refreshOnDemandWithDates greater than configured interval days startDate {} endDate {}",
        inputStartDate, inputEndDate);
    final LinkedHashSet<SchedulerRequest> schedulerRequests = new LinkedHashSet<>();
    boolean isScheduleJobRequired = true;
    LocalDate refreshStartDate = inputStartDate;
    LocalDate refreshEndDate = inputStartDate.plusDays(intervalDays);
    LocalDateTime previousJobTriggerTime = LocalDateTime.now();
    // Loop the split date range till reach end date
    while (isScheduleJobRequired) {
      log.info("refreshOnDemandWithDates date range processed in SET are startDate {} endDate {}",
          refreshStartDate, refreshEndDate);
      final LocalDateTime jobTriggerTime = previousJobTriggerTime.plusSeconds(schedulerProperties.getJobDelay());
      schedulerRequests.add(buildScheduleRequest(hotelIds, refreshStartDate, refreshEndDate, jobTriggerTime));
      if (refreshEndDate.equals(inputEndDate)) {
        isScheduleJobRequired = false;
        continue;
      }
      refreshStartDate = getNextStartDate(refreshEndDate);
      refreshEndDate = getNextEndDate(intervalDays, inputEndDate, refreshStartDate, refreshEndDate);
      previousJobTriggerTime = jobTriggerTime;
    }
    hotelAvailabilityOnDemandJobScheduler.scheduleOnDemandRefreshJob(schedulerRequests);
  }

  private LocalDate getNextStartDate(final LocalDate currentEndDate) {
    return currentEndDate;
  }

  private LocalDate getNextEndDate(final int intervalDays, final LocalDate endDate, final LocalDate currentStartDate,
      final LocalDate currentEndDate) {
    if (currentEndDate.isAfter(endDate) || currentStartDate.plusDays(intervalDays).isAfter(endDate)) {
      return endDate;
    }
    return currentStartDate.plusDays(intervalDays);
  }

  @Override
  public void refreshOnDemandWithoutDates() {
    log.info("ProcessRefreshHotelAvailability called refreshOnDemandWithoutDates");
    LinkedHashSet<SchedulerRequest> schedulerRequests = new LinkedHashSet<>();
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

  private OnDemandProcessResponse validateInputData(Set<String> hotelIds, String startDate, String endDate) {
    final OnDemandProcessResponse onDemandProcessResponse = new OnDemandProcessResponse();
    // Validate Hotel Ids
    final String errorMessage = hotelIdValidator.validateHotelIds(hotelIds);
    if (StringUtils.isNotEmpty(errorMessage)) {
      log.debug("Error Message : {}", errorMessage);
      onDemandProcessResponse.setMessage(errorMessage);
      onDemandProcessResponse.setStatus(HttpStatus.BAD_REQUEST);
      return onDemandProcessResponse;
    }
    // Validate Input Dates
    return dateFormatValidator.validateInputDates(startDate, endDate);
  }

  private Set<String> getOperaHotels() {
    return hmsJpaRepositoryReader.findByPmsSource(OPERA_PMS_SRC).stream().map(HotelMigrationStatusEntity::getHotelCode)
        .collect(Collectors.toSet());
  }
}
