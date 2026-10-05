package uk.co.whitbread.digitalkey.infrastructure.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import uk.co.whitbread.digitalkey.domain.logic.DigitalKeyInPortImpl;
import uk.co.whitbread.digitalkey.domain.ports.primary.DigitalKeyInPort;
import uk.co.whitbread.digitalkey.domain.ports.secondary.AxpOutPort;
import uk.co.whitbread.digitalkey.infrastructure.rest.client.AxpOutPortImpl;
import uk.co.whitbread.digitalkey.infrastructure.rest.client.ohip.service.OhipAdapterClient;
import uk.co.whitbread.digitalkey.infrastructure.rest.client.service.AxpClient;
import uk.co.whitbread.digitalkey.infrastructure.rest.controller.axp.mapper.AxpMapper;

@Configuration
public class DigitalKeyServiceConfig {

  @Bean
  public AxpOutPort axpOutPort(
      AxpClient axpClient,
      AxpMapper axpMapper,
      OhipAdapterClient ohipAdapterClient) {

    return new AxpOutPortImpl(axpClient, axpMapper, ohipAdapterClient);
  }

  @Bean
  public DigitalKeyInPort digitalKeyInPort(
      AxpOutPort axpOutPort
  ) {
    return new DigitalKeyInPortImpl(axpOutPort);
  }

}
