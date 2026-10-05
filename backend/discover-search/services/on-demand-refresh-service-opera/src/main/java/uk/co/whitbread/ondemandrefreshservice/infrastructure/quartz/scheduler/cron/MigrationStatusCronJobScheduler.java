package uk.co.whitbread.ondemandrefreshservice.infrastructure.quartz.scheduler.cron;

import java.text.ParseException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.quartz.CronTrigger;
import org.quartz.JobDetail;
import org.quartz.JobKey;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.TriggerKey;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;
import uk.co.whitbread.ondemandrefreshservice.domain.ports.secondary.CronJobScheduler;
import uk.co.whitbread.ondemandrefreshservice.infrastructure.exceptions.OnDemandRefreshBatchException;
import uk.co.whitbread.ondemandrefreshservice.infrastructure.quartz.config.MigrationStatusSchedulerProperties;
import uk.co.whitbread.ondemandrefreshservice.infrastructure.quartz.job.HotelMigrationStatusRefreshCronJob;
import uk.co.whitbread.ondemandrefreshservice.infrastructure.util.SharedLogMessages;

@Component
@Slf4j
@RequiredArgsConstructor
public class MigrationStatusCronJobScheduler implements CronJobScheduler {

  private static final String TRIGGER_NAME = "HotelMigrationStatusRefreshCronJob_Trigger";
  private static final String GROUP = "HotelMigrationStatusRefreshCronJob";
  private static final String JOB_NAME = "%s_%s";
  private final MigrationStatusSchedulerProperties quartzProperties;
  private final Scheduler scheduler;
  private final CronJobCreator cronJobCreator;
  private final ApplicationContext context;
  private static final Class<HotelMigrationStatusRefreshCronJob> MIGRATION_STATUS_CRON_JOB
      = HotelMigrationStatusRefreshCronJob.class;

  @Override
  public void schedule() {
    log.info("Start MigrationStatusCronJobScheduler.schedule()");
    try {
      final CronTrigger trigger = cronJobCreator
          .createCronTrigger(
              TRIGGER_NAME, GROUP, String.valueOf(
                  quartzProperties.getScheduledRefreshCronJob()));
      log.debug("CronTrigger: {}", trigger);

      final JobDetail jobDetail = cronJobCreator.createJob(MIGRATION_STATUS_CRON_JOB, context,
          JOB_NAME, GROUP);
      log.debug("JobDetail: {}", jobDetail);

      final JobKey jobKey = new JobKey(JOB_NAME, GROUP);
      log.info("JobKey: {}", jobKey);

      scheduleJob(trigger, jobDetail, jobKey);

    } catch (SchedulerException | RuntimeException | ParseException e) {
      log.error(String.format(SharedLogMessages.ERROR_WHILE_SCHEDULING_JOB_MESSAGE,
          MIGRATION_STATUS_CRON_JOB.getName()), e);
      throw new OnDemandRefreshBatchException(
          SharedLogMessages.ERROR_WHILE_SCHEDULING_JOB_MESSAGE, e);
    }
  }

  @Override
  public void refreshCronTrigger() {
    log.info("Start MigrationStatusCronJobScheduler.refreshCronTrigger()");
    try {
      scheduler.pauseTrigger(new TriggerKey(MigrationStatusCronJobScheduler.TRIGGER_NAME, GROUP));
      scheduler.deleteJob(new JobKey(MigrationStatusCronJobScheduler.JOB_NAME, GROUP));
      schedule();
    } catch (Exception e) {
      log.error(SharedLogMessages.REFRESH_CRON_ERROR_MESSAGE, e);
    }
  }

  private void scheduleJob(CronTrigger cronTrigger, JobDetail jobDetail, JobKey jobKey)
      throws SchedulerException {
    if (!scheduler.checkExists(jobKey)) {
      log.info("JobKey: {} does not exist already", jobKey);
      log.info("Going to schedule job: {}", jobDetail);
      scheduler.scheduleJob(jobDetail, cronTrigger);
    }
  }
}
