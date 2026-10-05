package uk.co.whitbread.spending.infrastructure.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {
  @Override
  public void addCorsMappings(CorsRegistry registry) {
    registry.addMapping("/**")
        .allowedOriginPatterns("*.premierinn.digital", "*.premierinn.com", "*localhost*")
        .allowedMethods("GET", "OPTIONS", "HEAD")
        .exposedHeaders("Content-Disposition")
        .maxAge(86400);
  }
}
