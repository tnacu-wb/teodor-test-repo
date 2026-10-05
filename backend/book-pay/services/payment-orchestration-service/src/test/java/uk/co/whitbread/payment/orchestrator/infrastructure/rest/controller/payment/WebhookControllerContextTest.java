package uk.co.whitbread.payment.orchestrator.infrastructure.rest.controller.payment;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import tools.jackson.databind.json.JsonMapper;

/**
 * Spring context test for the webhook endpoint.
 *
 * <p>This is the regression guard for constructor wiring. {@link WebhookController} depends on a
 * Jackson mapper and the unit tests construct one by hand, so they cannot catch a dependency the
 * container has no bean for. Spring Boot 4 auto-configures the Jackson 3
 * {@code tools.jackson.databind.json.JsonMapper} only — Jackson 2 remains on the classpath
 * transitively but no {@code com.fasterxml.jackson.databind.ObjectMapper} bean exists — so
 * asking for the wrong mapper type compiles and passes unit tests while failing context startup,
 * which is what the {@code spring-boot:start} step of the build hits.
 *
 * <p>Runs under the {@code integration} profile, the same profile the build starts the
 * application with for OpenAPI generation: Temporal is excluded there and the workflow ports are
 * stubbed, so no external server is required.
 *
 * <p>Validates: Requirements 5.3, 5.5, 5.6
 */
@SpringBootTest(
    properties = {
        // Signature validation off so the request reaches deserialization and the port,
        // exercising the container-supplied mapper end to end.
        "integrations.datatrans.webhook.validation-enabled=false",
        "payment.security.allowed-return-url-hosts=premierinn.com,www.premierinn.com,premierinn.digital,localhost"
    })
@ActiveProfiles("integration")
class WebhookControllerContextTest {

  private static final String WEBHOOK_URL = "/api/payments/webhooks/datatrans";

  private static final String AUTHORIZED_PAYLOAD = """
      {
        "transactionId": "test-transaction-id",
        "merchantId": "test-merchant-id",
        "type": "payment",
        "status": "authorized",
        "currency": "GBP",
        "refno": "TEST-BOOKING-REF",
        "paymentMethod": "VIS",
        "authorizedAmount": 9900,
        "someFutureDatatransField": "ignored",
        "card": {
          "alias": "test-card-alias",
          "masked": "000000xxxxxx0000",
          "expiryMonth": "12",
          "expiryYear": "29",
          "info": {"brand": "TEST_BRAND"}
        }
      }""";

  @Autowired
  private WebApplicationContext webApplicationContext;

  private MockMvc mockMvc;

  @BeforeEach
  void setUp() {
    mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
  }

  @Test
  void webhookControllerBeanIsCreatedWithTheAutoConfiguredMapper() {
    assertThat(webApplicationContext.containsBean("webhookController")).isTrue();
    assertThat(webApplicationContext.getBean(WebhookController.class)).isNotNull();
    // The only mapper bean available to inject is the auto-configured Jackson 3 one.
    assertThat(webApplicationContext.getBeansOfType(JsonMapper.class)).isNotEmpty();
  }

  @Test
  void datatransWebhookEndpointIsReachableAndAcceptsAPayload() throws Exception {
    mockMvc.perform(post(WEBHOOK_URL)
            .param("basketId", "test-basket-id")
            .header("Datatrans-Signature", "t=1700000000000,s0=notchecked")
            .contentType(MediaType.APPLICATION_JSON)
            .content(AUTHORIZED_PAYLOAD))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.status").value("received"));
  }
}
