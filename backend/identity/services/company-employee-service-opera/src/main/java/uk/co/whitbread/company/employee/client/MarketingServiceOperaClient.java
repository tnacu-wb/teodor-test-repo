package uk.co.whitbread.company.employee.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import uk.co.whitbread.company.employee.model.UpdatePreferencesRequest;

@FeignClient(value = "${feign.marketing-service-opera.name}", url = "${feign.marketing-service-opera.url}", configuration = FeignErrorDecodeConfig.class)
public interface MarketingServiceOperaClient {

  @PutMapping("/internal/marketing/newsletter")
  void updateMarketingOptIn(
      @RequestBody UpdatePreferencesRequest request);

}