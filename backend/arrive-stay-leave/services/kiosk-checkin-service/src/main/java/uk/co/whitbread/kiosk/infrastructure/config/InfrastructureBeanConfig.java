package uk.co.whitbread.kiosk.infrastructure.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import uk.co.whitbread.kiosk.domain.logic.KioskInPortImpl;
import uk.co.whitbread.kiosk.domain.logic.config.PaymentTypeConfig;
import uk.co.whitbread.kiosk.domain.ports.primary.KioskInPort;
import uk.co.whitbread.kiosk.domain.ports.secondary.KioskOutPort;
import uk.co.whitbread.kiosk.infrastructure.rest.client.ohip.KioskOutPortImpl;
import uk.co.whitbread.kiosk.infrastructure.rest.client.ohip.mapper.KioskCheckInRequestMapper;
import uk.co.whitbread.kiosk.infrastructure.rest.client.ohip.mapper.OhipProfileRequestMapper;
import uk.co.whitbread.kiosk.infrastructure.rest.client.ohip.service.OhipAdapterClient;
import uk.co.whitbread.kiosk.infrastructure.rest.client.ohip.service.properties.CommentTypeProperties;
import uk.co.whitbread.kiosk.infrastructure.rest.client.ohip.service.properties.PreferenceProperties;
import uk.co.whitbread.kiosk.infrastructure.rest.client.reservation.service.ReservationClient;

@Configuration
public class InfrastructureBeanConfig {


  @Bean
  public KioskInPort createKioskInPort(KioskOutPort checkInOutPort,
      PaymentTypeConfig paymentTypeConfig) {
    return new KioskInPortImpl(checkInOutPort, paymentTypeConfig);
  }

  @Bean
  public KioskOutPort createKioskOutPort(ReservationClient reservationClient,
      KioskCheckInRequestMapper kioskCheckInRequestMapper, OhipAdapterClient ohipAdapterClient,
      PreferenceProperties preferenceProperties, CommentTypeProperties commentTypeProperties,
      OhipProfileRequestMapper ohipProfileRequestMapper) {
    return new KioskOutPortImpl(kioskCheckInRequestMapper, ohipAdapterClient, reservationClient,
        preferenceProperties, commentTypeProperties, ohipProfileRequestMapper);
  }
}
