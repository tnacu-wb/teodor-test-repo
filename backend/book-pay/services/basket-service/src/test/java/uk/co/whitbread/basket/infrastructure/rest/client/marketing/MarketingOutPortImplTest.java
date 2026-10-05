package uk.co.whitbread.basket.infrastructure.rest.client.marketing;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.co.whitbread.basket.domain.model.marketing.out.Customer;
import uk.co.whitbread.basket.domain.model.marketing.out.MarketingPreferences;
import uk.co.whitbread.basket.generated.models.marketing.UpdatePreferencesRequest;
import uk.co.whitbread.basket.infrastructure.rest.client.marketing.mapper.MarketingMapper;
import uk.co.whitbread.basket.infrastructure.rest.client.marketing.service.MarketingClient;
import uk.co.whitbread.basket.infrastructure.rest.client.marketing.service.properties.MarketingClientProperties;

@ExtendWith(MockitoExtension.class)
public class MarketingOutPortImplTest {

  @InjectMocks
  private MarketingOutPortImpl marketingOutPort;
  @Mock
  private MarketingClientProperties marketingClientProperties;
  @Mock
  private MarketingClient marketingClient;
  @Mock
  private MarketingMapper marketingMapper;

  @Test
  void testUpdateMarketingInfo() {
    var marketingPreferences = createMarketingPreferencesForTesting();
    when(marketingClientProperties.getDefaultBrandCode()).thenReturn("DefaultBrandCode");
    when(marketingClientProperties.getDefaultChannel()).thenReturn("DefaultChannel");
    when(marketingClientProperties.getDefaultLanguage()).thenReturn("en");
    when(marketingClientProperties.getDefaultJourney()).thenReturn("DefaultJourney");
    when(marketingClientProperties.getDefaultContactType()).thenReturn("DefaultContactType");
    when(marketingMapper.toUpdatePreferencesRequestDto(any(),
        any(), any(), any(), any()))
        .thenReturn(createUpdatePreferenceRequest());

    marketingOutPort.updateMarketingInfo(marketingPreferences);

    verify(marketingClient, times(1)).updateMarketingPreferences(any(), any(),
      any());

  }

  private UpdatePreferencesRequest createUpdatePreferenceRequest() {
   return new UpdatePreferencesRequest();
  }

  private static MarketingPreferences createMarketingPreferencesForTesting() {
    return MarketingPreferences.builder()
        .optIn(false)
        .contactValue("ContactValue@uk.com")
        .customer(Customer.builder()
            .countryOfResidence("UK")
            .firstName("Test")
            .lastName("Testerson")
            .language("en")
            .title("Mr")
            .build())
        .build();
  }
}
