package uk.co.whitbread.availabilitycacheservice.infrastructure.config;

import static org.springframework.cloud.contract.wiremock.WireMockSpring.options;

import com.github.tomakehurst.wiremock.WireMockServer;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;

@TestConfiguration
public class ContentServiceWireMockConfig {

  @Bean(initMethod = "start", destroyMethod = "stop")
  public WireMockServer mockContentService() {
    return new WireMockServer(options().dynamicPort());
  }
}
