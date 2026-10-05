package uk.co.whitbread.basket.confirmation.processor.infrastructure.rest.client;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.co.whitbread.basket.confirmation.processor.domain.model.in.BasketAcknowledge;
import uk.co.whitbread.basket.confirmation.processor.infrastructure.rest.client.basket.BasketClient;
import uk.co.whitbread.basket.confirmation.processor.infrastructure.rest.client.mapper.ConfirmItemProcessingRequestBasketMapper;
import uk.co.whitbread.basket.confirmation.processor.infrastructure.rest.client.model.in.ConfirmItemProcessingRequestDto;

import java.util.Arrays;

@ExtendWith(MockitoExtension.class)
class BasketConfirmationProcessorOutPortImplTest {


  @InjectMocks
  private BasketConfirmationProcessorOutPortImpl basketConfirmationProcessorOutPort;

  @Mock
  private BasketClient basketClient;

  @Mock
  private ConfirmItemProcessingRequestBasketMapper confirmItemProcessingRequestBasketMapper;


  @Test
  void sendAcknowledge__Status204(){
    //Arrange
    when(confirmItemProcessingRequestBasketMapper.toDto(any())).thenReturn(mockConfirmItemProcessingRequestCompletedDto());

    doNothing().when(basketClient).sendAcknowledge(anyString(), anyString(), any());

    //Act
    basketConfirmationProcessorOutPort.sendAcknowledge(mockBasketAcknowledge());

    //Assert
    verify(basketClient, times(1)).sendAcknowledge(anyString(), anyString(), any());
  }

  @Test
  void sendAcknowledge__negativeAck(){
    //Arrange
    when(confirmItemProcessingRequestBasketMapper.toDto(any())).thenReturn(mockConfirmItemProcessingRequestFailedDto());

    doNothing().when(basketClient).sendAcknowledge(anyString(), anyString(), any());

    //Act
    basketConfirmationProcessorOutPort.sendAcknowledge(mockNegBasketAcknowledge());

    //Assert
    verify(basketClient, times(1)).sendAcknowledge(anyString(), anyString(), any());
  }

  private BasketAcknowledge mockBasketAcknowledge(){
    return BasketAcknowledge.builder()
        .itemId("123")
        .basketReference("333")
        .build();
  }

  private BasketAcknowledge mockNegBasketAcknowledge(){
      return BasketAcknowledge.builder()
              .itemId("338897")
              .basketReference("AWM5408857")
              .errors(Arrays.asList("Threre has been a failure: FAILED."))
              .status(1)
              .build();
  }

  private ConfirmItemProcessingRequestDto mockConfirmItemProcessingRequestCompletedDto(){
    return ConfirmItemProcessingRequestDto.builder()
        .description("COMPLETED")
        .status(0)
        .build();
  }

  private ConfirmItemProcessingRequestDto mockConfirmItemProcessingRequestFailedDto(){
    return ConfirmItemProcessingRequestDto.builder()
            .description("FAILED")
            .status(1)
            .build();
  }
}