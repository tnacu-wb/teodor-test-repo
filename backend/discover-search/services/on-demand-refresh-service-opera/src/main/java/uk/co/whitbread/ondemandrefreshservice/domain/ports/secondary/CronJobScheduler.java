package uk.co.whitbread.ondemandrefreshservice.domain.ports.secondary;

public interface CronJobScheduler {
    void schedule();
    void refreshCronTrigger();
}
