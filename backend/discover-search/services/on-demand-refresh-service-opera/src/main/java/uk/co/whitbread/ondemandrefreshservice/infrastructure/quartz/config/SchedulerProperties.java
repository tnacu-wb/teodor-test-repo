package uk.co.whitbread.ondemandrefreshservice.infrastructure.quartz.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties(prefix = "ondemandrefresh")
public class SchedulerProperties {
    private int interval;
    private String scheduledRefreshCronJob;
    private int maxDays;
    private int maxRange;
    private long jobDelay;
}
