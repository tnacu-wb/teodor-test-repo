package uk.co.whitbread.content.infrastructure.config;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import uk.co.whitbread.content.domain.ports.primary.ContentInPort;
import uk.co.whitbread.content.infrastructure.scheduler.HotelOpeningSoonScheduler;
import uk.co.whitbread.content.infrastructure.scheduler.HotelSearchFiltersScheduler;

@Configuration
@EnableScheduling
@ConditionalOnProperty(name = "content-entity.scheduler.enabled",
    havingValue = "true",
    matchIfMissing = true)
public class SchedulerConfig {

  @Bean
  public HotelSearchFiltersScheduler getHotelSearchFiltersScheduler(
      ContentInPort contentInPort) {
    return new HotelSearchFiltersScheduler(contentInPort);
  }

  @Bean
  public HotelOpeningSoonScheduler getHotelOpeningSoonScheduler(
          ContentInPort contentInPort) {
    return new HotelOpeningSoonScheduler(contentInPort);
  }
}
