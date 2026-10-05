package uk.co.whitbread.account.domain.logic;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import uk.co.whitbread.account.domain.model.in.MarketingPreferencesRequest;
import uk.co.whitbread.account.domain.model.in.MarketingPreferencesRequestV2;
import uk.co.whitbread.account.domain.ports.primary.MarketingPreferencesPort;
import uk.co.whitbread.account.domain.ports.secondary.MarketingPreferencesClientPort;

@Slf4j
@RequiredArgsConstructor
public class MarketingPreferencesPortImpl implements MarketingPreferencesPort {
  private final MarketingPreferencesClientPort marketingPreferencesClientPort;

  @Override
  public void updateMarketingPreferences(MarketingPreferencesRequest marketingPreferencesRequest) {
    marketingPreferencesClientPort.updateMarketingPreferences(marketingPreferencesRequest);
  }

  @Override
  public void updateMarketingPreferences(MarketingPreferencesRequestV2 marketingPreferencesRequest) {
    marketingPreferencesClientPort.updateMarketingPreferences(marketingPreferencesRequest);
  }
}
