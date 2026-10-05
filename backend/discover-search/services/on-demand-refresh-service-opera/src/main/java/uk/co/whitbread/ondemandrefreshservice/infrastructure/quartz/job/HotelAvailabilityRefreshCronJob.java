package uk.co.whitbread.ondemandrefreshservice.infrastructure.quartz.job;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.logstash.logback.marker.ObjectAppendingMarker;
import org.quartz.DisallowConcurrentExecution;
import org.quartz.JobExecutionContext;
import org.springframework.scheduling.quartz.QuartzJobBean;
import org.springframework.stereotype.Component;
import uk.co.whitbread.ondemandrefreshservice.domain.ports.secondary.OperaHotelAvailabilityRefreshOutPort;
import uk.co.whitbread.ondemandrefreshservice.infrastructure.entity.HotelMigrationStatusEntity;
import uk.co.whitbread.ondemandrefreshservice.infrastructure.exceptions.OnDemandRefreshBatchException;
import uk.co.whitbread.ondemandrefreshservice.infrastructure.repository.read.HotelMigrationStatusJpaRepository;

@Component
@Slf4j
@DisallowConcurrentExecution
@RequiredArgsConstructor
public class HotelAvailabilityRefreshCronJob extends QuartzJobBean {

  private final OperaHotelAvailabilityRefreshOutPort operaHotelAvailabilityRefreshOutPort;

  private final HotelMigrationStatusJpaRepository hotelMigrationRepo;

  private static final String OPERA_PMS_SRC = "OPERA";

  @Override
  public void executeInternal(JobExecutionContext jobExecutionContext) {
    log.info(new ObjectAppendingMarker("jobGroup", jobExecutionContext.getJobDetail().getKey().getGroup()),
        "Start HotelAvailabilityRefreshCronJob Execution");
    //endDate is same as the startDate because nightly cron job needs to refresh only for the 365th day from the current date
    final LocalDate startDate = LocalDate.now().plusYears(1);
    final Set<String> migratedHotels = getMigratedHotels();
    if (!migratedHotels.isEmpty()) {
      try {
        log.info(
            "Cron Job Name - {} with Start date - {} & end date - {} & migratedHotels {} started on Thread {}" +
                " at current timestamp - {}",
            jobExecutionContext.getJobDetail().getKey().getName(), startDate, startDate, migratedHotels,
            Thread.currentThread().getName(), LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
        operaHotelAvailabilityRefreshOutPort.refreshHotelAvailabilities(migratedHotels, startDate, startDate);
        log.info(new ObjectAppendingMarker("jobGroup",
                jobExecutionContext.getJobDetail().getKey().getGroup()),
            "End HotelAvailabilityRefreshCronJob Execution");
      } catch (Exception e) {
        log.error("Error while executing cron job : {}", jobExecutionContext.getJobDetail());
        throw new OnDemandRefreshBatchException("error Executing the HotelAvailabilityRefreshJob", e);
      }
    } else {
      log.info(
          "No migrated hotels found in the DB....exiting hotel availabilities refresh");
      throw new OnDemandRefreshBatchException("No migrated hotels found in the DB");
    }
  }

  private Set<String> getMigratedHotels() {
    return hotelMigrationRepo.findByPmsSource(OPERA_PMS_SRC).stream().filter(HotelMigrationStatusEntity::isOnSale)
        .map(HotelMigrationStatusEntity::getHotelCode).collect(Collectors.toSet());
  }
}
