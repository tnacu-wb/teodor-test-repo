package uk.co.whitbread.hotel.register.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import uk.co.whitbread.hotel.register.client.MarketingServiceOperaClient;
import uk.co.whitbread.hotel.register.model.UpdatePreferencesRequest;

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
      log.warn("Marketing optIn call failed: {}", ex.getMessage());
    }
  }

}
