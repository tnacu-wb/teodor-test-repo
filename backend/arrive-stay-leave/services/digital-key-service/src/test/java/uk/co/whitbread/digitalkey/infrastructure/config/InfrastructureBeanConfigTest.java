package uk.co.whitbread.digitalkey.infrastructure.config;

import org.junit.jupiter.api.Test;
import uk.co.whitbread.digitalkey.domain.logic.CheckInPortImpl;
import uk.co.whitbread.digitalkey.domain.ports.primary.CharacterUdfInPort;
import uk.co.whitbread.digitalkey.domain.ports.primary.CheckInPort;
import uk.co.whitbread.digitalkey.domain.ports.secondary.CheckOutPort;

import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.mock;

class InfrastructureBeanConfigTest {

  private final InfrastructureBeanConfig config = new InfrastructureBeanConfig();

  @Test
  void testCreateKioskInPort() {
    CheckOutPort checkOutPort = mock(CheckOutPort.class);
    CharacterUdfInPort characterUdfInPort = mock(CharacterUdfInPort.class);
    CheckInPort result = config.createKioskInPort(checkOutPort, characterUdfInPort);
    assertNotNull(result);
    assertInstanceOf(CheckInPortImpl.class, result);
  }

}
