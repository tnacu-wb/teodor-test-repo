package uk.co.whitbread.wallet.infrastructure.config;

import com.amazonaws.services.s3.AmazonS3;
import com.fasterxml.jackson.databind.ObjectMapper;
import de.brendamour.jpasskit.signing.IPKSigningUtil;
import de.brendamour.jpasskit.signing.PKSigningInformationUtil;
import jakarta.validation.Validator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;
import org.thymeleaf.TemplateEngine;
import uk.co.whitbread.wallet.domain.logic.HotelReservationInPortImpl;
import uk.co.whitbread.wallet.domain.logic.WalletGeneratorInPortImpl;
import uk.co.whitbread.wallet.domain.model.validation.ValidatorFactory;
import uk.co.whitbread.wallet.domain.ports.primary.HotelReservationInPort;
import uk.co.whitbread.wallet.domain.ports.primary.WalletGeneratorInPort;
import uk.co.whitbread.wallet.domain.ports.secondary.CertsRetrieverOutPort;
import uk.co.whitbread.wallet.domain.ports.secondary.ContentOutPort;
import uk.co.whitbread.wallet.domain.ports.secondary.HotelReservationOutPort;
import uk.co.whitbread.wallet.domain.properties.WalletProperties;
import uk.co.whitbread.wallet.infrastructure.certs.CertsRetrieverOutPortImpl;
import uk.co.whitbread.wallet.infrastructure.certs.properties.CertProperties;
import uk.co.whitbread.wallet.infrastructure.certs.properties.S3Properties;
import uk.co.whitbread.wallet.infrastructure.rest.client.content.ContentOutPortImpl;
import uk.co.whitbread.wallet.infrastructure.rest.client.content.mapper.HotelInfoMapper;
import uk.co.whitbread.wallet.infrastructure.rest.client.content.service.ContentClient;
import uk.co.whitbread.wallet.infrastructure.rest.client.reservations.HotelReservationOutPortImpl;
import uk.co.whitbread.wallet.infrastructure.rest.client.reservations.service.HotelReservationsClient;
import uk.co.whitbread.wallet.infrastructure.rest.client.reservations.service.mapper.ReservationDetailsMapper;

@Configuration
public class InfrastructureBeanConfig {

  @Bean
  public HotelReservationOutPort hotelReservationOutPort(
      HotelReservationsClient hotelReservationsConfig,
      ReservationDetailsMapper reservationDetailsMapper) {
    return new HotelReservationOutPortImpl(hotelReservationsConfig, reservationDetailsMapper);
  }

  @Bean
  public HotelReservationInPort hotelReservationInPort(
      HotelReservationOutPort hotelReservationOutPort) {
    return new HotelReservationInPortImpl(hotelReservationOutPort);
  }

  @Bean
  public ContentOutPort contentOutPort(ContentClient contentClient,
      HotelInfoMapper hotelInfoMapper) {
    return new ContentOutPortImpl(contentClient, hotelInfoMapper);
  }

  @Bean
  public CertsRetrieverOutPort certsRetrieverOutPort(CertProperties certProperties,
      S3Properties s3Properties, AmazonS3 amazonS3) {
    return new CertsRetrieverOutPortImpl(certProperties, s3Properties, amazonS3);
  }

  @Bean
  public WalletGeneratorInPort walletGeneratorInPort(WalletProperties walletProperties,
      ObjectMapper objectMapper, IPKSigningUtil ipkSigningUtil, TemplateEngine templateEngine,
      HotelReservationOutPort hotelReservationOutPort, ContentOutPort contentOutPort,
      CertsRetrieverOutPort certsRetrieverOutPort) {
    return new WalletGeneratorInPortImpl(walletProperties, objectMapper, ipkSigningUtil,
        templateEngine, hotelReservationOutPort, contentOutPort, new PKSigningInformationUtil(),
        certsRetrieverOutPort);
  }

  @Bean
  public Validator validator() {
    return new LocalValidatorFactoryBean();
  }

  @Bean
  public ValidatorFactory validatorFactory(Validator validator) {
    return ValidatorFactory.getInstance(validator);
  }
}
