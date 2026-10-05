package uk.co.whitbread.company.employee.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import uk.co.whitbread.company.employee.client.MarketingServiceOperaClient;
import uk.co.whitbread.company.employee.model.UpdatePreferencesRequest;

@Slf4j
@Service
@RequiredArgsConstructor
public class MarketingService {

  private final MarketingServiceOperaClient marketingServiceOperaClient;

  public void updateMarketingOptIn(UpdatePreferencesRequest request, String contactValue) {
    if (request == null) {
      log.debug("Skipping marketing opt-in update because request is null");
      return;
    }

    request.setContactValue(contactValue);
    invokeMarketingOptIn(request);
  }

  private void invokeMarketingOptIn(UpdatePreferencesRequest request) {
    try {
      marketingServiceOperaClient.updateMarketingOptIn(request);
    } catch (Exception ex) {
      log.warn("Marketing opt-in call failed", ex);
    }
  }

}