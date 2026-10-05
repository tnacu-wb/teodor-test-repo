package uk.co.whitbread.basket.processor.domain.logic;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.HashMap;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.co.whitbread.basket.processor.domain.model.in.BasketOrder;
import uk.co.whitbread.basket.processor.domain.model.out.CancelReservationRequest;
import uk.co.whitbread.basket.processor.domain.model.out.CancelReservationResponse;
import uk.co.whitbread.basket.processor.domain.model.out.ConfirmAmendRequest;
import uk.co.whitbread.basket.processor.domain.model.out.ConfirmReservationRequest;
import uk.co.whitbread.basket.processor.domain.model.out.ConfirmReservationResponse;
import uk.co.whitbread.basket.processor.infrastructure.queue.BasketAcknowledgeOutPortImpl;
import uk.co.whitbread.basket.processor.infrastructure.rest.client.BasketOrderProcessOutPortImpl;

@ExtendWith(MockitoExtension.class)
class BasketOrderProcessInPortImplTest {

  @InjectMocks
  private BasketOrderProcessInPortImpl basketOrderProcessInPort;

  @Mock
  private BasketOrderProcessOutPortImpl basketOrderProcessOutPort;

  @Mock
  private BasketAcknowledgeOutPortImpl basketAcknowledgeOutPort;

  @Test
  void testProcessOrder_success() {
    // Arrange
    when(basketOrderProcessOutPort.confirmReservation(any(ConfirmReservationRequest.class)))
        .thenReturn(mockConfirmationResponse());
    doNothing().when(basketAcknowledgeOutPort)
        .sendAcknowledgeMessage(any(String.class), any(String.class), any(String.class),
            any(String.class));
    // Act
    var data = new HashMap<String, String>();
    data.put("sourceId", "123");
    data.put("paymentOption", "PAY_ON_ARRIVAL");
    data.put("reqAction", "COMMIT");
    data.put("email", "USER@WHITBREAD.COM");

    var order = BasketOrder.builder()
        .basketReference("REF123")
        .data(data)
        .build();
    basketOrderProcessInPort.processOrder(order);
    // Assert
    verify(basketOrderProcessOutPort, times(1)).confirmReservation(any(ConfirmReservationRequest.class));
  }

  @Test
  void testHandleFailedOrder_success() {
    // Arrange
    doNothing().when(basketAcknowledgeOutPort)
        .sendAcknowledgeMessage(any(String.class), any(String.class), any(String.class),
            any(String.class));

    // Act
    var data = new HashMap<String, String>();
    data.put("sourceId", "123");
    data.put("paymentOption", "PAY_ON_ARRIVAL");
    data.put("reqAction", "COMMIT");

    var order = BasketOrder.builder()
        .basketReference("REF123")
        .data(data)
        .build();
    basketOrderProcessInPort.handleFailedOrder(order);
    // Assert
    verify(basketAcknowledgeOutPort, times(1))
        .sendAcknowledgeMessage(any(String.class), any(String.class), any(String.class),
            any(String.class));
  }

  private ConfirmReservationResponse mockConfirmationResponse() {
    return new ConfirmReservationResponse("123", "RESERVED");
  }

  @Test
  void testProcessCancelOrder_success() {
    // Arrange
    when(basketOrderProcessOutPort.cancelReservation(any(CancelReservationRequest.class)))
        .thenReturn(mockCancelationResponse());
    doNothing().when(basketAcknowledgeOutPort)
        .sendAcknowledgeMessage(any(String.class), any(String.class), any(String.class), any(String.class));
    // Act
    var data = new HashMap<String, String>();
    data.put("sourceId", "123");
    data.put("paymentOption", "PAY_ON_ARRIVAL");
    data.put("reqAction", "COMMIT");

    var order = BasketOrder.builder()
        .basketReference("REF123")
        .data(data)
        .build();
    basketOrderProcessInPort.processCancelOrder(order);
    // Assert
    verify(basketOrderProcessOutPort, times(1)).cancelReservation(any(CancelReservationRequest.class));
  }

  @Test
  void testProcessAmend_success() {
    // Arrange
    when(basketOrderProcessOutPort.confirmAmend(any(ConfirmAmendRequest.class)))
            .thenReturn(Boolean.TRUE);
    doNothing().when(basketAcknowledgeOutPort)
            .sendAcknowledgeMessage(any(String.class), any(String.class), any(String.class),
                    any(String.class));
    // Act
    var data = new HashMap<String, String>();
    data.put("token", "1234567788");
    data.put("language", "EN");
    data.put("reqAction", "AMEND");
    data.put("channel", "PI");
    data.put("subChannel", "WEB");
    data.put("tempBookingRef", "temp-basket-id");
    data.put("originalBookingRef", "original-basket-id");
    data.put("ccAgentId", "jane.doe@wb.com");

    final String id = String.join("#","AMEND", "temp-basket-id");

    final BasketOrder amendPayload = BasketOrder
            .builder()
            .id(id)
            .basketReference("temp-basket-id")
            .data(data)
            .build();

    basketOrderProcessInPort.processAmend(amendPayload);
    // Assert
    verify(basketOrderProcessOutPort, times(1)).confirmAmend(any(ConfirmAmendRequest.class));
  }

  private CancelReservationResponse mockCancelationResponse() {
    return new CancelReservationResponse("12345");
  }

}
