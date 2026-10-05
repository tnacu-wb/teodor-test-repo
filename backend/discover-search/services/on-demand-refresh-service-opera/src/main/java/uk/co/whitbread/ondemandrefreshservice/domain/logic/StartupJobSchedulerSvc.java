package uk.co.whitbread.ondemandrefreshservice.domain.logic;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import uk.co.whitbread.ondemandrefreshservice.infrastructure.quartz.scheduler.cron.HotelAvailabilityCronJobScheduler;
import uk.co.whitbread.ondemandrefreshservice.infrastructure.util.SharedLogMessages;

@Slf4j
@Component
@RequiredArgsConstructor
@Order(2)
public class StartupJobSchedulerSvc implements ApplicationRunner {

  private static final String ERROR_MESSAGE = "Error while trying to HotelAvailabilityCronJobScheduler.";

  private final HotelAvailabilityCronJobScheduler hotelAvailabilityCronScheduler;

  @Override
  public void run(ApplicationArguments args){
    log.info("Application started: {}", args);
    try {
      hotelAvailabilityCronScheduler.schedule();
      log.trace(SharedLogMessages.SCHEDULER_CALLED);
    } catch (Exception e) {
      log.error(ERROR_MESSAGE, e);
    }
  }
}
