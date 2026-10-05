package uk.co.whitbread.account.infrastructure.rest.controller.account;

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.not;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import uk.co.whitbread.account.domain.model.in.MarketingPreferencesRequest;
import uk.co.whitbread.account.domain.ports.primary.MarketingPreferencesPort;
import uk.co.whitbread.account.infrastructure.exception.ErrorCode;
import uk.co.whitbread.account.infrastructure.exception.ServiceException;
import uk.co.whitbread.account.infrastructure.rest.client.marketing.mapper.MarketingPreferencesMapper;
import uk.co.whitbread.account.infrastructure.rest.client.marketing.service.MarketingPreferencesClient;
import uk.co.whitbread.account.infrastructure.rest.controller.account.model.in.UpdatePreferencesRequestDto;
import uk.co.whitbread.commons.exceptions.advice.BusinessExceptionHandler;
import uk.co.whitbread.commons.exceptions.advice.GlobalExceptionHandler;

@ExtendWith(MockitoExtension.class)
class MarketingPreferencesControllerErrorResponseTest {

  private MockMvc mockMvc;

  @Mock
  private MarketingPreferencesMapper marketingPreferencesMapper;

  @Mock
  private MarketingPreferencesPort marketingPreferencesPort;

  @InjectMocks
  private MarketingPreferencesController marketingPreferencesController;

  @BeforeEach
  void setUp() {
    mockMvc = MockMvcBuilders
        .standaloneSetup(marketingPreferencesController)
        .setControllerAdvice(new BusinessExceptionHandler(), new GlobalExceptionHandler())
        .build();
  }

  @Test
  void updateMarketingPreferences_WhenDownstreamAuthFails_ReturnsSanitized500Body() throws Exception {
    when(marketingPreferencesMapper.toMarketingPreferencesRequestModel(any(UpdatePreferencesRequestDto.class)))
        .thenReturn(MarketingPreferencesRequest.builder().build());
    doThrow(new ServiceException(
        ErrorCode.UPDATE_MARKETING_PREFERENCES_EXCEPTION,
        MarketingPreferencesClient.WEBCLIENT_SERVICE_AUTHENTICATION_ERROR)).when(marketingPreferencesPort)
        .updateMarketingPreferences(any(MarketingPreferencesRequest.class));

    mockMvc.perform(put("/v1/account/preferences")
            .contentType(MediaType.APPLICATION_JSON)
            .content("""
                {
                  "optIn": true,
                  "doubleOptIn": true,
                  "brandCodes": ["PINN"],
                  "customer": {
                    "countryOfResidence": "GB",
                    "customerId": "auth401@example.com",
                    "language": "en"
                  }
                }
                """))
        .andExpect(status().isInternalServerError())
        .andExpect(jsonPath("$.errCode").value(539))
        .andExpect(jsonPath("$.globalErrTextTemplate").value("internal.server.exception"))
        .andExpect(jsonPath("$.debugMessage").value(MarketingPreferencesClient.WEBCLIENT_SERVICE_AUTHENTICATION_ERROR))
        .andExpect(jsonPath("$.debugMessage", not(containsString("token"))))
        .andExpect(jsonPath("$.debugMessage", not(containsString("Unauthorized"))));
  }
}
