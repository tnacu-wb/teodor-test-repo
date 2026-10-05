package uk.co.whitbread.hotel.account.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

  @Override
  public void addCorsMappings(CorsRegistry registry) {
    registry.addMapping("/**")
        .allowedOriginPatterns("*.premierinn.digital", "*.premierinn.com", "*localhost*")
        .allowedMethods("GET", "POST", "PUT", "OPTIONS", "HEAD")
        .maxAge(86400);
  }

}
