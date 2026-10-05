package uk.co.whitbread.account.infrastructure.rest.client.marketing;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import uk.co.whitbread.account.domain.model.in.MarketingPreferencesRequest;
import uk.co.whitbread.account.domain.model.in.MarketingPreferencesRequestV2;
import uk.co.whitbread.account.domain.ports.secondary.MarketingPreferencesClientPort;
import uk.co.whitbread.account.infrastructure.rest.client.marketing.mapper.MarketingPreferencesMapper;
import uk.co.whitbread.account.infrastructure.rest.client.marketing.service.MarketingPreferencesClient;

@Slf4j
@Service
@RequiredArgsConstructor
public class MarketingPreferencesClientPortImpl implements MarketingPreferencesClientPort {
  private final MarketingPreferencesClient marketingPreferencesClient;
  private final MarketingPreferencesMapper marketingPreferencesMapper;

  @Override
  public void updateMarketingPreferences(MarketingPreferencesRequest marketingPreferencesRequest) {
    marketingPreferencesClient.updateMarketingPreferences(
        marketingPreferencesMapper.toMarketingPreferencesRequestDto(marketingPreferencesRequest));
  }

  @Override
  public void updateMarketingPreferences(MarketingPreferencesRequestV2 marketingPreferencesRequest) {
    marketingPreferencesClient.updateMarketingPreferences(
        marketingPreferencesMapper.toMarketingPreferencesRequestDto(marketingPreferencesRequest));
  }
}
