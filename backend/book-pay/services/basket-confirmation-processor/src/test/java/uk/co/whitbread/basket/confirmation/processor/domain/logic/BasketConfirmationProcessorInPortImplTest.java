package uk.co.whitbread.basket.confirmation.processor.domain.logic;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.co.whitbread.basket.confirmation.processor.domain.model.in.BasketAcknowledge;
import uk.co.whitbread.basket.confirmation.processor.domain.ports.secondary.BasketConfirmationProcessorOutPort;

import java.util.Arrays;

@ExtendWith(MockitoExtension.class)
class BasketConfirmationProcessorInPortImplTest {

  @InjectMocks
  private BasketConfirmationProcessorInPortImpl basketConfirmationProcessorInPort;

  @Mock
  private BasketConfirmationProcessorOutPort basketConfirmationProcessorOutPort;

  @Test
  void processAcknowledge__Status204(){
    //Arrange
    doNothing().when(basketConfirmationProcessorOutPort).sendAcknowledge(any());

    //Act
    basketConfirmationProcessorInPort.processAcknowledge(mockBasketAcknowledge());

    //Assert
    verify(basketConfirmationProcessorOutPort, times(1)).sendAcknowledge(any());
  }

  @Test
  void processAcknowledge__negativeAck(){
    //Arrange
    doNothing().when(basketConfirmationProcessorOutPort).sendAcknowledge(any());

    //Act
    basketConfirmationProcessorInPort.processAcknowledge(mockNegativeAck());

    //Assert
    verify(basketConfirmationProcessorOutPort, times(1)).sendAcknowledge(any());
  }

  private BasketAcknowledge mockBasketAcknowledge(){
      return BasketAcknowledge.builder()
              .itemId("338897")
              .basketReference("AWM5408857")
              .errors(Arrays.asList("Threre has been a failure: FAILED."))
              .status(1)
              .build();
  }

  private BasketAcknowledge mockNegativeAck(){
    return BasketAcknowledge.builder()
            .itemId("338897")
            .basketReference("AWM5408857")
            .errors(Arrays.asList("Threre has been a failure: FAILED."))
            .status(1)
            .build();
  }


}