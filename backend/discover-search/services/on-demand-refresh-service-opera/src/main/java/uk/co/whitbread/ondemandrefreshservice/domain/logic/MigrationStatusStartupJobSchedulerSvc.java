package uk.co.whitbread.ondemandrefreshservice.domain.logic;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import uk.co.whitbread.ondemandrefreshservice.infrastructure.quartz.scheduler.cron.MigrationStatusCronJobScheduler;
import uk.co.whitbread.ondemandrefreshservice.infrastructure.util.SharedLogMessages;

@Slf4j
@Component
@RequiredArgsConstructor
@Order(1)
public class MigrationStatusStartupJobSchedulerSvc implements ApplicationRunner {

  private static final String ERROR_MESSAGE
      = "Error while trying to invoke MigrationStatusCronJobScheduler.";

  private final MigrationStatusCronJobScheduler migrationStatusCronJobScheduler;

  @Override
  public void run(ApplicationArguments args){
    log.info("MigrationStatusStartupJobSchedulerSvc Application Runner started: {}", args);
    try {
      migrationStatusCronJobScheduler.schedule();
      log.trace(SharedLogMessages.MIGRATION_STATUS_SCHEDULER_CALLED);
    } catch (Exception e) {
      log.error(ERROR_MESSAGE, e);
    }
  }
}
