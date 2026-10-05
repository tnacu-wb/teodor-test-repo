package uk.co.whitbread.ondemandrefreshservice.infrastructure.quartz.scheduler.cron;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.quartz.*;
import org.springframework.context.ApplicationContext;
import uk.co.whitbread.ondemandrefreshservice.infrastructure.exceptions.OnDemandRefreshBatchException;
import uk.co.whitbread.ondemandrefreshservice.infrastructure.quartz.config.MigrationStatusSchedulerProperties;
import uk.co.whitbread.ondemandrefreshservice.infrastructure.quartz.job.HotelMigrationStatusRefreshCronJob;

import java.text.ParseException;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;

@ExtendWith(MockitoExtension.class)
public class MigrationStatusCronJobSchedulerTest {

  private static final String TRIGGER_NAME = "HotelMigrationStatusRefreshCronJob_Trigger";
  private static final String GROUP = "HotelMigrationStatusRefreshCronJob";
  private static final String JOB_NAME = "%s_%s";
  public static final String SCHEDULED_REFRESH_CRON_JOB = "0 0 2 ? * * *";
  @Mock
  private ApplicationContext context;
  @Mock
  private MigrationStatusSchedulerProperties quartzProperties;
  @Mock
  private Scheduler scheduler;
  @Mock
  private CronTrigger cronTrigger;
  @Mock
  private JobDetail jobDetail;
  @Mock
  private CronJobCreator cronJobCreator;
  @InjectMocks
  private MigrationStatusCronJobScheduler migrationStatusCronJobScheduler;

  private boolean jobAlreadyExist;

  @Test
  public void shouldNotScheduleJobIfItAlreadyExists() throws SchedulerException, ParseException {
    jobAlreadyExist = true;
    Mockito.when(quartzProperties.getScheduledRefreshCronJob())
        .thenReturn(SCHEDULED_REFRESH_CRON_JOB);
    Mockito.when(cronJobCreator.createCronTrigger(TRIGGER_NAME, GROUP, SCHEDULED_REFRESH_CRON_JOB))
        .thenReturn(cronTrigger);
    Mockito.when(cronJobCreator.createJob(HotelMigrationStatusRefreshCronJob.class, context,
        JOB_NAME, GROUP))
        .thenReturn(jobDetail);
    final JobKey jobKey = new JobKey(JOB_NAME, GROUP);
    Mockito.when(scheduler.checkExists(jobKey))
        .thenReturn(jobAlreadyExist);
    migrationStatusCronJobScheduler.schedule();
    Mockito.verify(scheduler, never()).scheduleJob(any(), any());
    migrationStatusCronJobScheduler.schedule();
  }

  @Test
  public void shouldScheduleJobIfAlreadyDoesNotExist() throws SchedulerException, ParseException {
    jobAlreadyExist = false;
    Mockito.when(quartzProperties.getScheduledRefreshCronJob())
        .thenReturn(SCHEDULED_REFRESH_CRON_JOB);
    Mockito.when(cronJobCreator.createCronTrigger(TRIGGER_NAME, GROUP, SCHEDULED_REFRESH_CRON_JOB))
        .thenReturn(cronTrigger);
    Mockito.when(cronJobCreator.createJob(HotelMigrationStatusRefreshCronJob.class, context,
        JOB_NAME, GROUP))
        .thenReturn(jobDetail);
    final JobKey jobKey = new JobKey(JOB_NAME, GROUP);
    Mockito.when(scheduler.checkExists(jobKey))
        .thenReturn(jobAlreadyExist);
    migrationStatusCronJobScheduler.schedule();
    Mockito.verify(scheduler).scheduleJob(any(), any());
  }

  @Test
  public void errorShouldBeLoggedInCaseOfAnyException() throws ParseException {
    Mockito.when(quartzProperties.getScheduledRefreshCronJob())
        .thenReturn(SCHEDULED_REFRESH_CRON_JOB);
    Mockito.when(cronJobCreator.createCronTrigger(TRIGGER_NAME, GROUP, SCHEDULED_REFRESH_CRON_JOB))
        .thenThrow(new RuntimeException("Some Error"));
    assertThrows(OnDemandRefreshBatchException.class,
            () -> migrationStatusCronJobScheduler.schedule());
  }

  @Test
  public void refreshCronTrigger() throws SchedulerException, ParseException {
    Mockito.when(quartzProperties.getScheduledRefreshCronJob())
        .thenReturn(SCHEDULED_REFRESH_CRON_JOB);
    Mockito.when(cronJobCreator.createCronTrigger(TRIGGER_NAME, GROUP, SCHEDULED_REFRESH_CRON_JOB))
        .thenReturn(cronTrigger);
    migrationStatusCronJobScheduler.refreshCronTrigger();
    Mockito.verify(scheduler).pauseTrigger(new TriggerKey(TRIGGER_NAME, GROUP));
    Mockito.verify(scheduler).deleteJob(new JobKey(JOB_NAME, GROUP));
    Mockito.verify(scheduler).scheduleJob(any(), any());
  }

  @Test
  public void refreshCronTriggerShouldNotUpdateIfItAlreadyExists() throws SchedulerException, ParseException {
    Mockito.when(quartzProperties.getScheduledRefreshCronJob())
        .thenReturn(SCHEDULED_REFRESH_CRON_JOB);
    Mockito.when(cronJobCreator.createCronTrigger(TRIGGER_NAME, GROUP, SCHEDULED_REFRESH_CRON_JOB))
        .thenReturn(cronTrigger);
    final JobKey jobKey = new JobKey(JOB_NAME, GROUP);
    Mockito.when(scheduler.checkExists(jobKey))
        .thenReturn(true);
    migrationStatusCronJobScheduler.refreshCronTrigger();
    Mockito.verify(scheduler, never()).scheduleJob(any(), any());
  }

  @Test
  public void refreshCronTriggerShouldLogErrorInCaseOfException() throws ParseException {
    Mockito.when(quartzProperties.getScheduledRefreshCronJob())
        .thenReturn(SCHEDULED_REFRESH_CRON_JOB);
    Mockito.when(cronJobCreator.createCronTrigger(TRIGGER_NAME, GROUP, SCHEDULED_REFRESH_CRON_JOB))
        .thenThrow(new RuntimeException("Some Error"));
    migrationStatusCronJobScheduler.refreshCronTrigger();
    Assertions.assertThatCode(() -> migrationStatusCronJobScheduler.refreshCronTrigger())
        .doesNotThrowAnyException();
  }

  @Test
  public void refreshCronTriggerShouldLogErrorWhenSchedulerDeleteJobThrowsException() throws SchedulerException {
    Mockito.doNothing().when(scheduler).pauseTrigger(any());
    Mockito.when(scheduler.deleteJob(any()))
        .thenThrow(new RuntimeException("Some Error"));
    migrationStatusCronJobScheduler.refreshCronTrigger();
    Assertions.assertThatCode(() -> migrationStatusCronJobScheduler.refreshCronTrigger())
        .doesNotThrowAnyException();
  }

  @Test
  public void refreshCronTriggerShouldLogErrorWhenSchedulerPauseTriggerThrowsException() throws SchedulerException {
    Mockito.doThrow(new RuntimeException("Some Error")).when(scheduler).pauseTrigger(any());
    migrationStatusCronJobScheduler.refreshCronTrigger();
    Assertions.assertThatCode(() -> migrationStatusCronJobScheduler.refreshCronTrigger())
        .doesNotThrowAnyException();
  }
}
