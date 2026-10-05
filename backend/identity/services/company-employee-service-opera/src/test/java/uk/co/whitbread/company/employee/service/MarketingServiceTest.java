package uk.co.whitbread.company.employee.service;

import static org.mockito.Mockito.*;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.co.whitbread.company.employee.client.MarketingServiceOperaClient;
import uk.co.whitbread.company.employee.model.UpdatePreferencesRequest;

@ExtendWith(MockitoExtension.class)
class MarketingServiceTest {

  @Mock
  private MarketingServiceOperaClient marketingServiceOperaClient;

  @InjectMocks
  private MarketingService marketingService;

  @Test
  void updateMarketingOptIn_shouldCallClient_whenRequestIsNotNull() {
    UpdatePreferencesRequest request = new UpdatePreferencesRequest();

    marketingService.updateMarketingOptIn(request, "email@random.com");

    verify(marketingServiceOperaClient).updateMarketingOptIn(request);
  }

  @Test
  void updateMarketingOptIn_shouldNotCallClient_whenRequestIsNull() {
    marketingService.updateMarketingOptIn(null, "email@random.com");

    verifyNoInteractions(marketingServiceOperaClient);
  }

  @Test
  void updateMarketingOptIn_shouldLogWarning_whenClientThrowsException() {
    UpdatePreferencesRequest request = new UpdatePreferencesRequest();
    doThrow(new RuntimeException("Test exception"))
        .when(marketingServiceOperaClient).updateMarketingOptIn(request);

    marketingService.updateMarketingOptIn(request, "email@random.com");

    verify(marketingServiceOperaClient).updateMarketingOptIn(request);
  }
}