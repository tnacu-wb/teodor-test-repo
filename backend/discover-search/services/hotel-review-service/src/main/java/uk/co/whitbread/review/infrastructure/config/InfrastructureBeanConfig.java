package uk.co.whitbread.review.infrastructure.config;

import jakarta.validation.Validator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import uk.co.whitbread.review.domain.logic.ReviewInPortImpl;
import uk.co.whitbread.review.domain.model.validation.ValidatorFactory;
import uk.co.whitbread.review.domain.ports.primary.ReviewInPort;
import uk.co.whitbread.review.domain.ports.secondary.ReviewOutPort;
import uk.co.whitbread.review.infrastructure.rest.client.review.ReviewOutPortImpl;
import uk.co.whitbread.review.infrastructure.rest.client.review.TripAdvisorClient;
import uk.co.whitbread.review.infrastructure.rest.client.review.mapper.ReviewResponseMapper;
import uk.co.whitbread.shared.commons.logging.trace.ConcurrentTracer;

@Configuration
public class InfrastructureBeanConfig {

  @Bean
  public ValidatorFactory validatorFactory(Validator validator) {
    return ValidatorFactory.getInstance(validator);
  }

  @Bean
  public ReviewInPort reviewInPort(ReviewOutPort reviewOutPort) {
    return new ReviewInPortImpl(reviewOutPort);
  }

  @Bean
  public ReviewOutPort reviewOutPort(TripAdvisorClient tripAdvisorClient, ConcurrentTracer concurrentTracer,
                                     ReviewResponseMapper reviewResponseMapper) {
    return new ReviewOutPortImpl(tripAdvisorClient, concurrentTracer, reviewResponseMapper);
  }


}
