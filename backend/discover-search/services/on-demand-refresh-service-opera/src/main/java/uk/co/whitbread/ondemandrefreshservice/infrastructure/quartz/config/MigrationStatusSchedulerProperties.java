package uk.co.whitbread.ondemandrefreshservice.infrastructure.quartz.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties(prefix = "migrationstatusrefresh")
public class MigrationStatusSchedulerProperties {

    private String scheduledRefreshCronJob;
}
