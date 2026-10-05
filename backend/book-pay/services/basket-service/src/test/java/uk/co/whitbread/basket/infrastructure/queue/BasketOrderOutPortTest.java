package uk.co.whitbread.basket.infrastructure.queue;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.co.whitbread.basket.domain.model.basket.out.Basket;
import uk.co.whitbread.basket.domain.model.payments.in.PaymentsConfirmation;
import uk.co.whitbread.basket.domain.model.payments.out.BasketRequestAction;
import uk.co.whitbread.basket.domain.model.payments.out.BookingConfirmationDetails;
import uk.co.whitbread.basket.infrastructure.queue.producer.BasketOrderProducer;

@ExtendWith(MockitoExtension.class)
class BasketOrderOutPortTest {
    @InjectMocks
    private BasketOrderOutPortImpl basketOrderOutPort;

    @Mock
    private BasketOrderProducer basketOrderProducer;

    @Test
    void testProcessBasket() {
        // Arrange
        doNothing().when(basketOrderProducer).sendOrderAsync(any(Basket.class), anyString(), any(
            BookingConfirmationDetails.class), any());

        // Act
        basketOrderOutPort.processOrder(Basket.builder().build(), BasketRequestAction.COMMIT.getReqAction(), BookingConfirmationDetails.builder().build(), null);

        // Assert
        verify(basketOrderProducer, times(1))
                .sendOrderAsync(any(Basket.class), anyString(), any(BookingConfirmationDetails.class), any());
    }

    @Test
    void testProcessAmend() {
        // Arrange
        doNothing().when(basketOrderProducer).sendAmendAsync(any(Basket.class), any(PaymentsConfirmation.class), anyString());

        // Act
        basketOrderOutPort.processAmend(Basket.builder().build(), PaymentsConfirmation.builder().build(), BasketRequestAction.AMEND.getReqAction());

        // Assert
        verify(basketOrderProducer, times(1))
                .sendAmendAsync(any(Basket.class), any(PaymentsConfirmation.class), anyString());
    }
}
