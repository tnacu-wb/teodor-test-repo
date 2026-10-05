package uk.co.whitbread.avail.business.events.infrastructure.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.config.CorsRegistry;
import org.springframework.web.reactive.config.WebFluxConfigurer;

@Configuration
public class WebConfig implements WebFluxConfigurer {

  @Override
  public void addCorsMappings(CorsRegistry registry) {
    registry.addMapping("/**")
        .allowedOriginPatterns("*.premierinn.digital", "*.premierinn.com", "*localhost*")
        .allowedMethods("GET", "POST", "PUT", "OPTIONS", "HEAD")
        .maxAge(86400);
  }
}
