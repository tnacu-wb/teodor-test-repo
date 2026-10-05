package uk.co.whitbread.account.infrastructure.rest.controller.account;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import uk.co.whitbread.account.domain.ports.primary.MarketingPreferencesPort;
import uk.co.whitbread.account.infrastructure.rest.client.marketing.mapper.MarketingPreferencesMapper;
import uk.co.whitbread.account.infrastructure.rest.client.marketing.service.MarketingPreferencesClient;
import uk.co.whitbread.account.infrastructure.rest.controller.account.model.in.CustomerDto;
import uk.co.whitbread.account.infrastructure.rest.controller.account.model.in.SourceDetailsDto;
import uk.co.whitbread.account.infrastructure.rest.controller.account.model.in.UpdatePreferencesRequestDto;

@ExtendWith(MockitoExtension.class)
class MarketingPreferencesControllerTest {
  private static final boolean OPT_IN = true;
  private static final boolean DOUBLE_OPT_IN = true;
  private static final String[] BRANDCODES = {"PINN"};
  private static final String CUSTOMER_ID = "sample@example.com";
  @Mock
  private CustomerDto customerDto;
  @Mock
  private SourceDetailsDto sourceDetailsDto;
  @Mock
  private MarketingPreferencesClient marketingPreferenceClient;
  @Mock
  private MarketingPreferencesPort marketingPreferencesPort;
  @Mock
  private MarketingPreferencesMapper marketingPreferencesMapper;
  @InjectMocks
  private MarketingPreferencesController underTest;

  @Test
  void validRequestUpdatePreferencesNoContentStatus(){
    // Arrange
    UpdatePreferencesRequestDto requestDto = updatePreferencesRequestDto();

    // Act
    ResponseEntity<Void> responseEntity = underTest.updateMarketingPreferences(requestDto);

    // Assert
    assertThat(responseEntity.getStatusCode(), is(HttpStatus.NO_CONTENT));
  }

  private UpdatePreferencesRequestDto updatePreferencesRequestDto(){
    return new UpdatePreferencesRequestDto(OPT_IN, DOUBLE_OPT_IN, BRANDCODES, customerDto, sourceDetailsDto);
  }
}
