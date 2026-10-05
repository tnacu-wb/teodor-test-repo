package uk.co.whitbread.basket.infrastructure.queue;

import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.verify;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.co.whitbread.basket.domain.model.basket.out.Basket;
import uk.co.whitbread.basket.domain.model.basket.out.BasketStatus;
import uk.co.whitbread.basket.infrastructure.queue.producer.BookingCompletedProducer;

@ExtendWith(MockitoExtension.class)
class BookingCompletedOutPortImplTest {

  private static final String BASKET_ID = "LONHOL-12345";

  @Mock
  private BookingCompletedProducer bookingCompletedProducer;

  @InjectMocks
  private BookingCompletedOutPortImpl underTest;

  @Test
  void publishBookingCompleted_whenBasketIsCompleted_shouldPublishEventWithStatusCompleted() {
    var basket = Basket.builder()
        .basketId(BASKET_ID)
        .status(BasketStatus.COMPLETED)
        .build();

    underTest.publishBookingCompleted(basket);

    verify(bookingCompletedProducer).sendBookingCompletedEvent(
        argThat(event ->
            BASKET_ID.equals(event.getBasketReference()) &&
            "COMPLETED".equals(event.getStatus())
        )
    );
  }

  @Test
  void publishBookingCompleted_whenBasketIsFailed_shouldPublishEventWithStatusFailed() {
    var basket = Basket.builder()
        .basketId(BASKET_ID)
        .status(BasketStatus.FAILED)
        .build();

    underTest.publishBookingCompleted(basket);

    verify(bookingCompletedProducer).sendBookingCompletedEvent(
        argThat(event ->
            BASKET_ID.equals(event.getBasketReference()) &&
            "FAILED".equals(event.getStatus())
        )
    );
  }

  @Test
  void publishBookingCompleted_whenBasketIsAnyNonCompletedStatus_shouldPublishEventWithStatusFailed() {
    var basket = Basket.builder()
        .basketId(BASKET_ID)
        .status(BasketStatus.PROCESSING)
        .build();

    underTest.publishBookingCompleted(basket);

    verify(bookingCompletedProducer).sendBookingCompletedEvent(
        argThat(event ->
            BASKET_ID.equals(event.getBasketReference()) &&
            "FAILED".equals(event.getStatus())
        )
    );
  }
}
