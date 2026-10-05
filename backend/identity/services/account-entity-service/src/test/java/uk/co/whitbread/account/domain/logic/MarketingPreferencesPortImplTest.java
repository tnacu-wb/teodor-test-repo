package uk.co.whitbread.account.domain.logic;

import static org.mockito.Mockito.*;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.co.whitbread.account.domain.model.in.*;
import uk.co.whitbread.account.domain.ports.secondary.MarketingPreferencesClientPort;

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
  private MarketingPreferencesClientPort marketingPreferencesPort;
  @InjectMocks
  private MarketingPreferencesPortImpl underTest;

  @Test
  void updateMarketingPreferences(){
    // Arrange
    lenient().doNothing().when(marketingPreferencesPort).updateMarketingPreferences(marketingPreferencesRequest());

    // Act
    underTest.updateMarketingPreferences(marketingPreferencesRequest());

    // Assert
    verify(marketingPreferencesPort, times(1)).updateMarketingPreferences(marketingPreferencesRequest());
  }

  private MarketingPreferencesRequest marketingPreferencesRequest() {
    return new MarketingPreferencesRequest(OPT_IN, DOUBLE_OPT_IN, BRANDCODES, customer, sourceDetails());
  }

  private SourceDetails sourceDetails() {
    return new SourceDetails(CHANNEL, JOURNEY, LOCALE);
  }

  @Test
  void updateMarketingPreferencesV2() {
    // Arrange
    lenient().doNothing().when(marketingPreferencesPort).updateMarketingPreferences(marketingPreferencesRequestV2());

    // Act
    underTest.updateMarketingPreferences(marketingPreferencesRequestV2());

    // Assert
    verify(marketingPreferencesPort, times(1)).updateMarketingPreferences(marketingPreferencesRequestV2());
  }

  private MarketingPreferencesRequestV2 marketingPreferencesRequestV2() {
    return MarketingPreferencesRequestV2.builder()
        .optIn(OPT_IN)
        .doubleOptIn(DOUBLE_OPT_IN)
        .brandCodes(BRANDCODES)
        .customer(customer)
        .sourceDetails(sourceDetails())
        .contactValue(CUSTOMER_ID)
        .build();
  }
}
