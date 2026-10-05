package uk.co.whitbread.basket.confirmation.processor.infrastructure.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import uk.co.whitbread.basket.confirmation.processor.domain.logic.BasketConfirmationProcessorInPortImpl;
import uk.co.whitbread.basket.confirmation.processor.domain.ports.primary.BasketConfirmationProcessorInPort;
import uk.co.whitbread.basket.confirmation.processor.domain.ports.secondary.BasketConfirmationProcessorOutPort;

@Configuration
public class InfrastructureBeanConfig {

  @Bean
  public BasketConfirmationProcessorInPort basketConfirmationProcessorInPort(
      final BasketConfirmationProcessorOutPort basketConfirmationProcessorOutPort) {
    return new BasketConfirmationProcessorInPortImpl(basketConfirmationProcessorOutPort);
  }

}
