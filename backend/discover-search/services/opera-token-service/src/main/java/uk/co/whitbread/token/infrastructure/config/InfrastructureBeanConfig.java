package uk.co.whitbread.token.infrastructure.config;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.validation.Validator;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.web.reactive.function.client.WebClient;
import uk.co.whitbread.token.domain.logic.TokenInPortImpl;
import uk.co.whitbread.token.domain.model.validation.ValidatorFactory;
import uk.co.whitbread.token.domain.ports.primary.TokenInPort;
import uk.co.whitbread.token.domain.ports.secondary.TokenOutPort;
import uk.co.whitbread.token.infrastructure.rest.client.token.TokenOutPortImpl;
import uk.co.whitbread.token.infrastructure.rest.client.token.mapper.OperaTokenRequestMapper;
import uk.co.whitbread.token.infrastructure.rest.client.token.ohip.OhipTokenClient;

@Configuration
public class InfrastructureBeanConfig {

  @Bean
  public ValidatorFactory validatorFactory(Validator validator) {
    return ValidatorFactory.getInstance(validator);
  }

  @Bean
  @ConditionalOnMissingBean
  public ObjectMapper objectMapper() {
    ObjectMapper mapper = new ObjectMapper().findAndRegisterModules();
    mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    mapper.configure(DeserializationFeature.FAIL_ON_NULL_FOR_PRIMITIVES, false);
    return mapper;
  }

  @Bean("ohipWebClient")
  public WebClient ohipWebClient() {
    return WebClient.builder().build();
  }

  @Bean
  public TokenOutPort createTokenOutPortBean(OhipTokenClient ohipTokenClient,
      OperaTokenRequestMapper operaTokenRequestMapper) {
    return new TokenOutPortImpl(ohipTokenClient, operaTokenRequestMapper);
  }

  @Bean
  public TokenInPort createTokenInPortBean(TokenOutPort tokenOutPort,
      ClientRegistrationRepository clientRegistrationRepository) {
    return new TokenInPortImpl(clientRegistrationRepository, tokenOutPort);
  }

}
