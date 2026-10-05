package uk.co.whitbread.digitalkey.infrastructure.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import uk.co.whitbread.digitalkey.domain.logic.CharacterUdfInPortImpl;
import uk.co.whitbread.digitalkey.domain.logic.CheckInPortImpl;
import uk.co.whitbread.digitalkey.domain.ports.primary.CharacterUdfInPort;
import uk.co.whitbread.digitalkey.domain.ports.primary.CheckInPort;
import uk.co.whitbread.digitalkey.domain.ports.secondary.CharacterUdfOutPort;
import uk.co.whitbread.digitalkey.domain.ports.secondary.CheckOutPort;
import uk.co.whitbread.digitalkey.infrastructure.rest.client.kiosk.service.KioskAdapterClient;
import uk.co.whitbread.digitalkey.infrastructure.rest.client.ohip.CheckOutPortImpl;
import uk.co.whitbread.digitalkey.infrastructure.rest.client.ohip.mapper.KioskCheckInRequestMapper;
import uk.co.whitbread.digitalkey.infrastructure.rest.client.ohip.service.CharacterUdfOutPortImpl;
import uk.co.whitbread.digitalkey.infrastructure.rest.client.ohip.service.OhipAdapterClient;

@Configuration
public class InfrastructureBeanConfig {


  @Bean
  public CheckInPort createKioskInPort(CheckOutPort checkInOutPort, CharacterUdfInPort characterUdfInPort) {
    return new CheckInPortImpl(checkInOutPort, characterUdfInPort);
  }

  @Bean
  public CheckOutPort createKioskOutPort(KioskCheckInRequestMapper kioskCheckInRequestMapper,
                                         OhipAdapterClient ohipAdapterClient,
                                         KioskAdapterClient kioskAdapterClient) {
    return new CheckOutPortImpl(kioskCheckInRequestMapper, ohipAdapterClient, kioskAdapterClient);
  }

  @Bean
  public CharacterUdfInPort createCharacterUdfInPort(CharacterUdfOutPort characterUdfOutPort) {
    return new CharacterUdfInPortImpl(characterUdfOutPort);
  }

  @Bean
  public CharacterUdfOutPort createCharacterUdfOutPort(OhipAdapterClient ohipAdapterClient) {
    return new CharacterUdfOutPortImpl(ohipAdapterClient);
  }

}