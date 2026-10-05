package uk.co.whitbread.hotel.register.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import uk.co.whitbread.hotel.register.config.FeignConfig;
import uk.co.whitbread.hotel.register.model.UpdatePreferencesRequest;

@FeignClient(value = "${feign.marketing-service-opera.name}", url = "${feign.marketing-service-opera.url}", configuration = FeignConfig.class)
public interface MarketingServiceOperaClient {

  @PutMapping("/internal/marketing/newsletter")
  void updateMarketingOptIn(
      @RequestBody UpdatePreferencesRequest request);

}
