package uk.co.whitbread.account.domain.ports.primary;

import uk.co.whitbread.account.domain.model.in.MarketingPreferencesRequest;
import uk.co.whitbread.account.domain.model.in.MarketingPreferencesRequestV2;

public interface MarketingPreferencesPort {

  void updateMarketingPreferences(MarketingPreferencesRequest marketingPreferencesRequest);

  void updateMarketingPreferences(MarketingPreferencesRequestV2 marketingPreferencesRequest);
}
