package uk.co.whitbread.basket.infrastructure.rest.client.marketing;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import uk.co.whitbread.basket.domain.model.marketing.out.MarketingPreferences;
import uk.co.whitbread.basket.domain.ports.secondary.MarketingOutPort;
import uk.co.whitbread.basket.infrastructure.rest.client.marketing.mapper.MarketingMapper;
import uk.co.whitbread.basket.infrastructure.rest.client.marketing.service.MarketingClient;
import uk.co.whitbread.basket.infrastructure.rest.client.marketing.service.properties.MarketingClientProperties;

@Slf4j
@Component
@RequiredArgsConstructor
public class MarketingOutPortImpl implements MarketingOutPort {

  private final MarketingClient marketingClient;
  private final MarketingMapper marketingMapper;
  private final MarketingClientProperties marketingClientProperties;

  @Override
  public void updateMarketingInfo(MarketingPreferences marketingPreferences) {
    log.debug("Entered updateMarketingInfo with marketingPreferences={}", marketingPreferences);

    final var updatePreferencesRequest = marketingMapper.toUpdatePreferencesRequestDto(
        marketingPreferences,
        marketingClientProperties.getDefaultBrandCode(),
        marketingClientProperties.getDefaultLanguage(),
        marketingClientProperties.getDefaultJourney(),
        marketingClientProperties.getDefaultChannel());

    marketingClient.updateMarketingPreferences(marketingClientProperties.getDefaultContactType(),
        marketingPreferences.getContactValue(), updatePreferencesRequest);
  }
}
