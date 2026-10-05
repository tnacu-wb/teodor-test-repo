package uk.co.whitbread.content.infrastructure.scheduler;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import uk.co.whitbread.content.domain.ports.primary.ContentInPort;

@Slf4j
@RequiredArgsConstructor
public class HotelOpeningSoonScheduler {

  private final ContentInPort contentInPort;

  @Scheduled(cron = "${content-entity.hotels.opening-soon.refresh}")
  public void updateHotelsOpeningSoonCacheJob() {
    log.info("Launching update Opening Soon hotels cache job");
    contentInPort.updateHotelsOpeningSoonCache();
  }
}
