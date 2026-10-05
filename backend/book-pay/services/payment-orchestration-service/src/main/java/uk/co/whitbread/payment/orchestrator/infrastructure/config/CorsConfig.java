package uk.co.whitbread.payment.orchestrator.infrastructure.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * CORS configuration for the Payment Orchestration Service.
 *
 * <p>Allows browser-based frontend clients to call payment APIs
 * cross-origin during local development and deployed environments.
 *
 * <p>The localhost patterns are anchored on purpose. A substring pattern such as
 * {@code *localhost*} matches any origin that merely contains the word — an attacker-controlled
 * {@code https://evil-localhost.attacker.com} would be handed our CORS headers. Spring's
 * {@code allowedOriginPatterns} understands {@code [*]} as a port wildcard, so the two anchored
 * patterns below cover every local dev-server port and nothing else.
 */
@Configuration
public class CorsConfig implements WebMvcConfigurer {

  @Override
  public void addCorsMappings(CorsRegistry registry) {
    registry.addMapping("/api/**")
        .allowedOriginPatterns(
            "*.premierinn.digital",
            "*.premierinn.com",
            "http://localhost:[*]",
            "https://localhost:[*]")
        .allowedMethods("GET", "POST", "PUT", "OPTIONS", "HEAD")
        .maxAge(86400);
  }
}
