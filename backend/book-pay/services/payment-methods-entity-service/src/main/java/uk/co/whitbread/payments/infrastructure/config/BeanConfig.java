package uk.co.whitbread.payments.infrastructure.config;

import jakarta.validation.Validator;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import uk.co.whitbread.payments.domain.logic.PaymentCcuiMethodsPortImpl;
import uk.co.whitbread.payments.domain.logic.PaymentMethodsPortImpl;
import uk.co.whitbread.payments.domain.logic.common.PaymentMethodsCommon;
import uk.co.whitbread.payments.domain.model.feature.FeatureFlag;
import uk.co.whitbread.payments.domain.model.feature.UnleashWrapper;
import uk.co.whitbread.payments.domain.model.validation.ValidatorFactory;
import uk.co.whitbread.payments.domain.ports.secondary.BasketPort;
import uk.co.whitbread.payments.domain.ports.secondary.CustomerAccountPort;
import uk.co.whitbread.payments.domain.ports.secondary.DefaultPaymentMethodsPort;
import uk.co.whitbread.payments.domain.ports.secondary.HotelInfoPort;
import uk.co.whitbread.payments.domain.ports.secondary.ReservationsPort;
import uk.co.whitbread.payments.domain.ports.secondary.TokenPort;
import uk.co.whitbread.payments.infrastructure.repository.paymentmethods.DefaultPaymentMethodsYamlFilePortImpl;
import uk.co.whitbread.payments.infrastructure.repository.paymentmethods.mapper.DefaultPaymentMethodsMapper;
import uk.co.whitbread.payments.infrastructure.repository.paymentmethods.model.out.DefaultPaymentMethodsDto;
import uk.co.whitbread.payments.infrastructure.rest.client.customers.CustomerAccountPortImpl;
import uk.co.whitbread.payments.infrastructure.rest.client.customers.mapper.AccountMapper;
import uk.co.whitbread.payments.infrastructure.rest.client.customers.mapper.CompanyMapper;
import uk.co.whitbread.payments.infrastructure.rest.client.customers.service.CompanyServiceClient;
import uk.co.whitbread.payments.infrastructure.rest.client.customers.service.HotelAccountClient;
import uk.co.whitbread.payments.infrastructure.rest.client.customers.service.HotelCardClient;
import uk.co.whitbread.payments.infrastructure.rest.client.hotels.HotelInfoPortImpl;
import uk.co.whitbread.payments.infrastructure.rest.client.hotels.mapper.HotelInfoMapper;
import uk.co.whitbread.payments.infrastructure.rest.client.hotels.service.HotelInfoClient;
import uk.co.whitbread.payments.infrastructure.rest.client.reservation.ReservationsPortImpl;
import uk.co.whitbread.payments.infrastructure.rest.client.reservation.mapper.DepositFolioMapper;
import uk.co.whitbread.payments.infrastructure.rest.client.reservation.service.ReservationClient;
import uk.co.whitbread.payments.infrastructure.rest.client.token.TokenPortImpl;
import uk.co.whitbread.payments.infrastructure.rest.client.token.service.TokenClient;
import uk.co.whitbread.shared.auth.security.AuthenticatedUserService;

@Configuration
@EnableConfigurationProperties(DataTransProperties.class)
public class BeanConfig {

  @Bean
  public ValidatorFactory validatorFactory(Validator validator) {
    return ValidatorFactory.getInstance(validator);
  }

  @Bean
  public HotelInfoPort hotelInfoPort(final HotelInfoClient hotelInfoClient,
                                     final HotelInfoMapper hotelInfoMapper) {
    return new HotelInfoPortImpl(hotelInfoClient, hotelInfoMapper);
  }

  @Bean
  public PaymentMethodsCommon paymentMethodsCommon() {
    return new PaymentMethodsCommon();
  }

  @Bean
  public CustomerAccountPort customerAccountPort(final HotelCardClient hotelCardClient,
                                                 final HotelAccountClient hotelAccountClient,
                                                 final CompanyServiceClient companyServiceClient,
                                                 final AccountMapper accountMapper,
                                                 final CompanyMapper companyMapper) {
    return new CustomerAccountPortImpl(hotelCardClient, hotelAccountClient, companyServiceClient, accountMapper,
            companyMapper);
  }

  @Bean
  public DefaultPaymentMethodsPort defaultPaymentMethodsPort(
      @Qualifier("main") final DefaultPaymentMethodsDto config,
      final DefaultPaymentMethodsMapper mapper) {
    return new DefaultPaymentMethodsYamlFilePortImpl(config, mapper);
  }

  @Bean
  public DefaultPaymentMethodsPort defaultPaymentCcuiMethodsPort(
      @Qualifier("ccui") final DefaultPaymentMethodsDto config,
      final DefaultPaymentMethodsMapper mapper) {
    return new DefaultPaymentMethodsYamlFilePortImpl(config, mapper);
  }

  @Bean
  public ReservationsPort reservationPort(final ReservationClient reservationClient,
      DepositFolioMapper depositFolioMapper) {
    return new ReservationsPortImpl(reservationClient, depositFolioMapper);
  }

  @Bean
  public TokenPort tokenPort(final TokenClient tokenClient) {
    return new TokenPortImpl(tokenClient);
  }


  @Bean
  public PaymentMethodsPortImpl paymentMethodsPortImpl(final HotelInfoPort hotelInfoPort,
                                                       final CustomerAccountPort customerAccountPort,
                                                       final DefaultPaymentMethodsPort defaultPaymentMethodsPort,
                                                       final ReservationsPort reservationPort,
                                                       final PaymentMethodsCommon paymentMethodsCommon,
                                                       final AuthenticatedUserService authenticatedUserService,
                                                       final TokenPort tokenPort,
                                                       final BasketPort basketPort,
                                                       @Value("${piba.euro.flag}") final Boolean flag,
                                                       final UnleashWrapper<FeatureFlag> unleashWrapper,
                                                       final DataTransProperties dataTransProperties) {
    return new PaymentMethodsPortImpl(hotelInfoPort, customerAccountPort, defaultPaymentMethodsPort,
        reservationPort, paymentMethodsCommon, authenticatedUserService, tokenPort, basketPort,
        flag, unleashWrapper, dataTransProperties.toDomainConfig());

  }

  @Bean
  public PaymentCcuiMethodsPortImpl paymentCcuiMethodsPortImpl(final HotelInfoPort hotelInfoPort,
                                                               final DefaultPaymentMethodsPort
                                                                   defaultPaymentCcuiMethodsPort,
                                                               final ReservationsPort reservationPort,
                                                               final PaymentMethodsCommon paymentMethodsCommon,
                                                               @Value("${piba.euro.flag}") final Boolean flag,
                                                               final UnleashWrapper<FeatureFlag> unleashWrapper,
                                                               final DataTransProperties dataTransProperties) {
    return new PaymentCcuiMethodsPortImpl(hotelInfoPort, defaultPaymentCcuiMethodsPort,
        reservationPort, paymentMethodsCommon, flag, unleashWrapper, dataTransProperties.toDomainConfig());
  }
}
