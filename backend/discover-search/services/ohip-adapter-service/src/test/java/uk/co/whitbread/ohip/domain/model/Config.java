package uk.co.whitbread.ohip.domain.model;

import jakarta.validation.Validation;
import org.hibernate.validator.HibernateValidator;
import org.hibernate.validator.HibernateValidatorConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import uk.co.whitbread.ohip.domain.model.validation.ValidatorFactory;
@Configuration
public class Config {
  @Bean("hibernateValidator")
  public ValidatorFactory validatorFactory() {
    HibernateValidatorConfiguration configuration = Validation.byProvider(HibernateValidator.class)
        .configure();
    var factory = configuration.failFast(true).buildValidatorFactory();
    return ValidatorFactory.getInstance(factory.getValidator());
  }
}
