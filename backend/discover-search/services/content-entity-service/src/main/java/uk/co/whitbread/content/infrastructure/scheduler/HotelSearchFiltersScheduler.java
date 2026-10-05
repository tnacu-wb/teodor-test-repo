package uk.co.whitbread.content.infrastructure.scheduler;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import uk.co.whitbread.content.domain.ports.primary.ContentInPort;

@Slf4j
@RequiredArgsConstructor
public class HotelSearchFiltersScheduler {

  private final ContentInPort contentInPort;

  @Scheduled(cron = "${content-entity.hotels.filters.refresh}")
  public void updateHotelsFiltersCacheJob() {
    log.info("Launching update hotels facilities job");
    contentInPort.updateHotelsFacilitiesCache();
  }
}
