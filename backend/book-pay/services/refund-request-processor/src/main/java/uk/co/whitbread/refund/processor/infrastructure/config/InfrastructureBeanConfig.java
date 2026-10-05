package uk.co.whitbread.refund.processor.infrastructure.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import uk.co.whitbread.refund.processor.domain.logic.RefundRequestProcessInPortImpl;
import uk.co.whitbread.refund.processor.domain.ports.primary.RefundRequestProcessInPort;
import uk.co.whitbread.refund.processor.domain.ports.secondary.BasketAcknowledgeOutPort;
import uk.co.whitbread.refund.processor.domain.ports.secondary.RefundRequestProcessOutPort;

@Configuration
public class InfrastructureBeanConfig {

  @Bean
  public RefundRequestProcessInPort refundRequestProcessInPort(
      final RefundRequestProcessOutPort refundRequestProcessOutPort,
      final BasketAcknowledgeOutPort basketAcknowledgeOutPort) {
    return new RefundRequestProcessInPortImpl(refundRequestProcessOutPort, basketAcknowledgeOutPort);
  }

}
