package uk.co.whitbread.basket.processor.infrastructure.queue;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Map;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.co.whitbread.basket.processor.domain.model.in.BasketOrder;
import uk.co.whitbread.basket.processor.domain.ports.primary.BasketOrderProcessInPort;
import uk.co.whitbread.basket.processor.infrastructure.queue.mapper.BasketOrderMapper;
import uk.co.whitbread.basket.processor.infrastructure.queue.model.order.BasketOrderEvent;
import uk.co.whitbread.basket.processor.infrastructure.queue.model.order.BasketRequestAction;

@ExtendWith(MockitoExtension.class)
class BasketOrderConsumerTest {
  @Mock
  private BasketOrderProcessInPort basketOrderProcessInPort;
  @Mock
  private BasketOrderMapper basketOrderMapper;
  @Spy
  private BasketOrderEvent basketOrderEvent;
  @Mock
  private BasketOrder basketOrder;
  private final String REQ_ACTION = "reqAction";

  @InjectMocks
  private BasketOrderConsumer basketOrderConsumer;

  @Test
  void testOrderRecord_success() {
    //Arrange
    basketOrderEvent.setData(Map.of(REQ_ACTION, BasketRequestAction.COMMIT.getReqAction()));
    when(basketOrderMapper.toDomain(basketOrderEvent)).thenReturn(basketOrder);

    //Act
    basketOrderConsumer.onOrder(basketOrderEvent);
    // Assert
    verify(basketOrderProcessInPort, times(1)).processOrder(basketOrder);
  }

  @Test
  void testOrderRecord_CancelRequestAction() {
    //Arrange
    basketOrderEvent.setData(Map.of(REQ_ACTION, BasketRequestAction.CANCEL.getReqAction()));
    when(basketOrderMapper.toDomain(basketOrderEvent)).thenReturn(basketOrder);

    //Act
    basketOrderConsumer.onOrder(basketOrderEvent);
    // Assert
    verify(basketOrderProcessInPort, times(1)).processCancelOrder(basketOrder);
  }

  @Test
  void testOrderRecord_AmendRequestAction() {
    //Arrange
    basketOrderEvent.setData(Map.of(REQ_ACTION, BasketRequestAction.AMEND.getReqAction()));
    when(basketOrderMapper.toDomain(basketOrderEvent)).thenReturn(basketOrder);

    //Act
    basketOrderConsumer.onOrder(basketOrderEvent);
    // Assert
    verify(basketOrderProcessInPort, times(1)).processAmend(basketOrder);
  }

  @Test
  void testOrderRecord_FailedEvent() {
    //Arrange
    when(basketOrderMapper.toDomain(basketOrderEvent)).thenReturn(basketOrder);

    //Act
    basketOrderConsumer.dltHandler(basketOrderEvent);
    // Assert
    verify(basketOrderProcessInPort, times(1)).handleFailedOrder(basketOrder);
  }

  @Test
  void testOrderRecord_ChangePaymentRequestAction() {
    //Arrange
    basketOrderEvent.setData(Map.of(REQ_ACTION, BasketRequestAction.CHANGE_PAY.getReqAction()));
    when(basketOrderMapper.toDomain(basketOrderEvent)).thenReturn(basketOrder);

    //Act
    basketOrderConsumer.onOrder(basketOrderEvent);
    // Assert
    verify(basketOrderProcessInPort, times(1)).processOrder(basketOrder);
  }
}
