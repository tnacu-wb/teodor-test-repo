package uk.co.whitbread.basket.processor.infrastructure.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import uk.co.whitbread.basket.processor.domain.logic.BasketOrderProcessInPortImpl;
import uk.co.whitbread.basket.processor.domain.ports.primary.BasketOrderProcessInPort;
import uk.co.whitbread.basket.processor.domain.ports.secondary.BasketAcknowledgeOutPort;
import uk.co.whitbread.basket.processor.domain.ports.secondary.BasketOrderProcessOutPort;

@Configuration
public class InfrastructureBeanConfig {

  @Bean
  public BasketOrderProcessInPort basketOrderProcessInPort(final BasketOrderProcessOutPort basketOrderProcessOutPort,
      final BasketAcknowledgeOutPort basketAcknowledgeOutPort) {
    return new BasketOrderProcessInPortImpl(basketOrderProcessOutPort, basketAcknowledgeOutPort);
  }

}
