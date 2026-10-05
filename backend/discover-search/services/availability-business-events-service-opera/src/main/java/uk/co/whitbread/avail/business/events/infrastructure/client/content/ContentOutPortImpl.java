package uk.co.whitbread.avail.business.events.infrastructure.client.content;

import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import uk.co.whitbread.avail.business.events.domain.ports.secondary.ContentOutPort;
import uk.co.whitbread.avail.business.events.infrastructure.client.content.service.ContentClient;

@Slf4j
@RequiredArgsConstructor
@Component
public class ContentOutPortImpl implements ContentOutPort {

  private final ContentClient contentClient;

  @Override
  public List<String> getHotelsWithCityTax() {
    var globalConfig = contentClient.getGlobalConfig(null, null);
    return globalConfig.getHotelsWithCityTax();
  }
}
