package uk.co.whitbread.account.infrastructure.config;

import jakarta.validation.Validator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import uk.co.whitbread.account.domain.logic.AccountRegistrationPortImpl;
import uk.co.whitbread.account.domain.logic.MarketingPreferencesPortImpl;
import uk.co.whitbread.account.domain.model.validation.ValidatorFactory;
import uk.co.whitbread.account.domain.ports.secondary.CustomerRegistrationPort;
import uk.co.whitbread.account.domain.ports.secondary.MarketingPreferencesClientPort;

@Configuration
public class InfrastructureBeanConfig {

  @Bean
  public ValidatorFactory validatorFactory(Validator validator) {
    return ValidatorFactory.getInstance(validator);
  }

  @Bean
  public AccountRegistrationPortImpl accountRegistrationPort(CustomerRegistrationPort customerRegistraionPort) {
    return new AccountRegistrationPortImpl(customerRegistraionPort);
  }

  @Bean
  public MarketingPreferencesPortImpl
      marketingPreferencesPort(MarketingPreferencesClientPort updateMarketingPreferencesPort) {
    return new MarketingPreferencesPortImpl(updateMarketingPreferencesPort);
  }
}
