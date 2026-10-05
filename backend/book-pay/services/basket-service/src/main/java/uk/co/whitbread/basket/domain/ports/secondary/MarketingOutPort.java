package uk.co.whitbread.basket.domain.ports.secondary;

import uk.co.whitbread.basket.domain.model.marketing.out.MarketingPreferences;

public interface MarketingOutPort {

  void updateMarketingInfo(MarketingPreferences marketingPreferences);

}
