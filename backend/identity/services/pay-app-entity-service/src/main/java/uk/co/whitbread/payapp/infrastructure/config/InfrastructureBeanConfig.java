package uk.co.whitbread.payapp.infrastructure.config;

import jakarta.validation.Validator;
import java.util.concurrent.Executor;
import org.springframework.cache.CacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.KafkaTemplate;
import uk.co.whitbread.payapp.domain.logic.PayAppInPortImpl;
import uk.co.whitbread.payapp.domain.model.validation.ValidatorFactory;
import uk.co.whitbread.payapp.domain.ports.primary.PayAppInPort;
import uk.co.whitbread.payapp.domain.ports.secondary.CdhOutPort;
import uk.co.whitbread.payapp.domain.ports.secondary.EmailNotificationOutPort;
import uk.co.whitbread.payapp.domain.ports.secondary.PayAppOutPort;
import uk.co.whitbread.payapp.infrastructure.queue.EmailNotificationOutPortImpl;
import uk.co.whitbread.payapp.infrastructure.queue.model.ShareAppEmailNotificationEvent;
import uk.co.whitbread.payapp.infrastructure.queue.producer.EmailNotificationProducer;
import uk.co.whitbread.payapp.infrastructure.rest.client.cdh.CdhAdapterClient;
import uk.co.whitbread.payapp.infrastructure.rest.client.cdh.CdhClient;
import uk.co.whitbread.payapp.infrastructure.rest.client.cdh.CdhOutPortImpl;
import uk.co.whitbread.payapp.infrastructure.rest.client.cdh.mapper.PibaTetheredGuidResponseMapper;
import uk.co.whitbread.payapp.infrastructure.rest.client.company.CompanyClient;
import uk.co.whitbread.payapp.infrastructure.rest.client.payapp.PayAppOutPortImpl;
import uk.co.whitbread.payapp.infrastructure.rest.client.payapp.mapper.AddApplicationCardResponseMapper;
import uk.co.whitbread.payapp.infrastructure.rest.client.payapp.mapper.AppCompanyDetailsLookupMapper;
import uk.co.whitbread.payapp.infrastructure.rest.client.payapp.mapper.AppInitRequestMapper;
import uk.co.whitbread.payapp.infrastructure.rest.client.payapp.mapper.AppPreCheckResponseMapper;
import uk.co.whitbread.payapp.infrastructure.rest.client.payapp.mapper.ApplicationDetailsResponseMapper;
import uk.co.whitbread.payapp.infrastructure.rest.client.payapp.mapper.GetApplicationCardsResponseMapper;
import uk.co.whitbread.payapp.infrastructure.rest.client.payapp.mapper.GetUserPreferencesResponseMapper;
import uk.co.whitbread.payapp.infrastructure.rest.client.payapp.mapper.SubmitApplicationRequestMapper;
import uk.co.whitbread.payapp.infrastructure.rest.client.payapp.mapper.SubmitApplicationResponseMapper;
import uk.co.whitbread.payapp.infrastructure.rest.client.payapp.mapper.UpdateAppCompanyDetailsRequestMapper;
import uk.co.whitbread.payapp.infrastructure.rest.client.payapp.mapper.UpdateAppContactDetailsRequestMapper;
import uk.co.whitbread.payapp.infrastructure.rest.client.payapp.mapper.UpdateApplicationRequestMapper;
import uk.co.whitbread.payapp.infrastructure.rest.client.worldline.WorldlineClient;
import uk.co.whitbread.payapp.infrastructure.rest.client.worldline.properties.WorldlineProperties;
import uk.co.whitbread.payapp.infrastructure.rest.controller.payapp.mapper.ApplicationDetailsMapper;
import uk.co.whitbread.payapp.infrastructure.security.JwtUtils;
import uk.co.whitbread.shared.auth.security.AuthenticatedUserService;
import uk.co.whitbread.shared.commons.logging.trace.ConcurrentTracer;

@Configuration
public class InfrastructureBeanConfig {

  @Bean
  public ValidatorFactory validatorFactory(Validator validator) {
    return ValidatorFactory.getInstance(validator);
  }

  @Bean
  PayAppInPort getPayAppInPort(PayAppOutPort payAppOutPort, CdhOutPort cdhOutPort) {
    return new PayAppInPortImpl(payAppOutPort, cdhOutPort);
  }

  @Bean
  JwtUtils getJwtUtils(AuthenticatedUserService authenticatedUserService) {
    return new JwtUtils(authenticatedUserService);
  }

  @Bean
  PayAppOutPort getPayAppOutPort(final WorldlineClient worldlineClient, final CdhClient cdhClient,
      final CompanyClient companyClient,
      final AppInitRequestMapper appInitRequestMapper,
      final UpdateAppContactDetailsRequestMapper updateAppContactDetailsRequestMapper,
      final UpdateAppCompanyDetailsRequestMapper updateAppCompanyDetailsRequestMapper,
      final ApplicationDetailsMapper applicationDetailsMapper,
      final AppCompanyDetailsLookupMapper appCompanyDetailsLookupMapper,
      final GetUserPreferencesResponseMapper getUserPreferencesResponseMapper,
      final ApplicationDetailsResponseMapper applicationDetailsResponseMapper,
      final GetApplicationCardsResponseMapper getApplicationCardsResponseMapper,
      final AddApplicationCardResponseMapper addApplicationCardResponseMapper,
      final SubmitApplicationResponseMapper submitApplicationResponseMapper,
      final SubmitApplicationRequestMapper submitApplicationRequestMapper,
      final AppPreCheckResponseMapper appPreCheckResponseMapper,
      final WorldlineProperties worldlineProperties,
      final AuthenticatedUserService authenticatedUserService,
      final CdhAdapterClient cdhAdapterClient,
      final JwtUtils jwtUtils,
      final EmailNotificationOutPort emailNotificationOutPort,
      final UpdateApplicationRequestMapper updateApplicationRequestMapper,
      final Executor worldlineExecutor,
      final CacheManager cacheManager1Hour) {
    return new PayAppOutPortImpl(worldlineClient, cdhClient, companyClient,
        appInitRequestMapper,
        updateAppContactDetailsRequestMapper,
        updateAppCompanyDetailsRequestMapper,
        applicationDetailsMapper,
        appCompanyDetailsLookupMapper,
        getUserPreferencesResponseMapper,
        applicationDetailsResponseMapper,
        getApplicationCardsResponseMapper,
        addApplicationCardResponseMapper,
        submitApplicationResponseMapper,
        submitApplicationRequestMapper,
        appPreCheckResponseMapper,
        worldlineProperties,
        authenticatedUserService,
        cdhAdapterClient,
        jwtUtils,
        emailNotificationOutPort,
        updateApplicationRequestMapper,
        worldlineExecutor,
        cacheManager1Hour
    );
  }

  @Bean
  CdhOutPort getCdhOutPort(final CdhClient cdhClient,
      final PibaTetheredGuidResponseMapper pibaTetheredGuidResponseMapper) {
    return new CdhOutPortImpl(cdhClient, pibaTetheredGuidResponseMapper);
  }

  @Bean
  EmailNotificationOutPort getEmailNotificationOutPort(
      final EmailNotificationProducer emailNotificationProducer) {
    return new EmailNotificationOutPortImpl(emailNotificationProducer);
  }

  @Bean
  EmailNotificationProducer getEmailNotificationProducer(
      KafkaTemplate<String, ShareAppEmailNotificationEvent> kafkaTemplate,
      ConcurrentTracer concurrentTracer) {
    return new EmailNotificationProducer(kafkaTemplate, concurrentTracer);
  }
}
