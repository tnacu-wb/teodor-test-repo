package uk.co.whitbread.account.infrastructure.rest.client.marketing;

import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.co.whitbread.account.domain.logic.MarketingPreferencesPortImpl;
import uk.co.whitbread.account.domain.model.in.Customer;
import uk.co.whitbread.account.domain.model.in.MarketingPreferencesRequest;
import uk.co.whitbread.account.domain.model.in.SourceDetails;
import uk.co.whitbread.account.domain.ports.secondary.MarketingPreferencesClientPort;
import uk.co.whitbread.account.infrastructure.rest.client.marketing.model.in.MarketingPreferencesRequestDto;
import uk.co.whitbread.account.infrastructure.rest.client.marketing.service.MarketingPreferencesClient;
import uk.co.whitbread.account.infrastructure.rest.controller.account.model.in.CustomerDto;
import uk.co.whitbread.account.infrastructure.rest.controller.account.model.in.SourceDetailsDto;

@ExtendWith(MockitoExtension.class)
class MarketingPreferencesPortImplTest {
  private static final boolean OPT_IN = true;
  private static final boolean DOUBLE_OPT_IN = true;
  private static final String[] BRANDCODES = {"PINN"};
  private static final String CHANNEL = "WEB";
  private static final String JOURNEY = "JOURNEY";
  private static final String LOCALE = "en_GB";
  private static final String CUSTOMER_ID = "sample@example.com";
  @Mock
  private Customer customer;
  @Mock
  private CustomerDto customerDto;
  @Mock
  private MarketingPreferencesClient marketingPreferencesClient;
  @Mock
  private MarketingPreferencesClientPort marketingPreferencesClientPort;
  @InjectMocks
  private MarketingPreferencesPortImpl underTest;

  @Test
  void updateMarketingPreferences(){
    // Arrange
    lenient().when(marketingPreferencesClient.updateMarketingPreferences(marketingPreferencesRequestDto())).thenReturn("");

    // Act
    underTest.updateMarketingPreferences(marketingPreferencesRequest());

    // Assert
    verify(marketingPreferencesClientPort, times(1)).updateMarketingPreferences(marketingPreferencesRequest());
  }

  private MarketingPreferencesRequest marketingPreferencesRequest() {
    return new MarketingPreferencesRequest(OPT_IN, DOUBLE_OPT_IN, BRANDCODES, customer, sourceDetails());
  }

  private MarketingPreferencesRequestDto marketingPreferencesRequestDto(){
    return new MarketingPreferencesRequestDto(OPT_IN, DOUBLE_OPT_IN, BRANDCODES, customerDto, sourceDetailsDto());
  }

  private SourceDetailsDto sourceDetailsDto() {
    return new SourceDetailsDto(CHANNEL, JOURNEY, LOCALE);
  }

  private SourceDetails sourceDetails() {
    return new SourceDetails(CHANNEL, JOURNEY, LOCALE);
  }
}
