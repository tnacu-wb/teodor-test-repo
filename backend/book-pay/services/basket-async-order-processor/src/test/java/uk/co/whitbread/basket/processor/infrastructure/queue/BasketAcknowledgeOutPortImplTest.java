package uk.co.whitbread.basket.processor.infrastructure.queue;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class BasketAcknowledgeOutPortImplTest {

  @Mock
  private BasketAcknowledgeProducer basketAcknowledgeProducer;

  @InjectMocks
  private BasketAcknowledgeOutPortImpl basketAcknowledgeOutPort;

  @Test
  void testSendAcknowledgeMessage_success() {
    // Arrange
    doNothing().when(basketAcknowledgeProducer).sendAckAsync(any(String.class), any(String.class), any(String.class), any(String.class));

    // Act
    basketAcknowledgeOutPort.sendAcknowledgeMessage("123", "123","COMMIT", "RESERVED");

    // Assert
    verify(basketAcknowledgeProducer, times(1)).sendAckAsync(any(String.class), any(String.class), any(String.class), any(String.class));
  }

}
