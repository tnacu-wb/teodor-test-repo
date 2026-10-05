package uk.co.whitbread.payment.orchestrator.infrastructure.config;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.options;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

/**
 * Guards the CORS origin patterns registered by {@link CorsConfig}.
 *
 * <p>The regression this exists for is the old {@code *localhost*} pattern. It matched any
 * origin that merely contained the word, {@code https://evil-localhost.attacker.com} included,
 * so an attacker-controlled page could read authenticated payment responses from a browser.
 * The anchored {@code http(s)://localhost:[*]} patterns keep local development working without
 * handing out that hole.
 *
 * <p>Preflight requests go through the real application context so the assertions exercise
 * Spring's own origin matcher against the real registration rather than a copy of the rules.
 * Runs under the {@code integration} profile, which excludes Temporal.
 */
@SpringBootTest
@ActiveProfiles("integration")
class CorsConfigTest {

  private static final String API_PATH = "/api/payments/webhooks/datatrans";

  @Autowired
  private WebApplicationContext webApplicationContext;

  private MockMvc mockMvc;

  @BeforeEach
  void setUp() {
    mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
  }

  @ParameterizedTest
  @ValueSource(strings = {
      "https://www.premierinn.com",
      "https://book.premierinn.digital",
      "http://localhost:5173",
      "https://localhost:8443"
  })
  void allowsTrustedOrigins(String origin) throws Exception {
    mockMvc.perform(options(API_PATH)
            .header("Origin", origin)
            .header("Access-Control-Request-Method", "POST"))
        .andExpect(status().isOk())
        .andExpect(header().string("Access-Control-Allow-Origin", origin));
  }

  @ParameterizedTest
  @ValueSource(strings = {
      "https://evil-localhost.attacker.com",
      "https://localhost.attacker.com",
      "https://premierinn.digital.attacker.com",
      "http://attacker.com"
  })
  void rejectsUntrustedOrigins(String origin) throws Exception {
    mockMvc.perform(options(API_PATH)
            .header("Origin", origin)
            .header("Access-Control-Request-Method", "POST"))
        .andExpect(status().isForbidden())
        .andExpect(header().doesNotExist("Access-Control-Allow-Origin"));
  }
}
