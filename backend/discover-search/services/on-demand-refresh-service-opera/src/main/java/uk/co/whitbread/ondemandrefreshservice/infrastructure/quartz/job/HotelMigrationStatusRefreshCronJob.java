package uk.co.whitbread.ondemandrefreshservice.infrastructure.quartz.job;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.logstash.logback.marker.ObjectAppendingMarker;
import org.quartz.DisallowConcurrentExecution;
import org.quartz.JobExecutionContext;
import org.springframework.scheduling.quartz.QuartzJobBean;
import org.springframework.stereotype.Component;
import uk.co.whitbread.ondemandrefreshservice.domain.ports.secondary.HotelMigrationStatusRefreshOutPort;
import uk.co.whitbread.ondemandrefreshservice.infrastructure.exceptions.HotelMigrationStatusRefreshJobException;

@Component
@Slf4j
@DisallowConcurrentExecution
@RequiredArgsConstructor
public class HotelMigrationStatusRefreshCronJob extends QuartzJobBean {

  private final HotelMigrationStatusRefreshOutPort hotelMigrationStatusRefreshOutPort;

  @Override
  public void executeInternal(JobExecutionContext jobExecutionContext) {
    log.info(new ObjectAppendingMarker("jobGroup",
            jobExecutionContext.getJobDetail().getKey().getGroup()),
        "Start HotelMigrationStatusRefreshCronJob Execution For Opera Hotels With OnSale Flag as False");
    try {
      log.info(
          "Cron Job Name - {} started on Thread {} at current timestamp - {}",
          jobExecutionContext.getJobDetail().getKey().getName(),
          Thread.currentThread().getName(),
          LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
      hotelMigrationStatusRefreshOutPort.refreshOperaHotelMigrationStatus();
      log.info(new ObjectAppendingMarker("jobGroup",
              jobExecutionContext.getJobDetail().getKey().getGroup()),
          "End HotelMigrationStatusRefreshCronJob Execution For Opera Hotels With OnSale Flag as False");
    } catch (Exception e) {
      log.error("Error while executing cron job : {}", jobExecutionContext.getJobDetail());
      throw new HotelMigrationStatusRefreshJobException(
          "error Executing the HotelMigrationStatusRefreshCronJob", e);
    }
  }
}
