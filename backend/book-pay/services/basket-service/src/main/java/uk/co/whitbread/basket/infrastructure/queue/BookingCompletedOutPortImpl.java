package uk.co.whitbread.basket.infrastructure.queue;

import static uk.co.whitbread.basket.domain.model.basket.out.BasketStatus.COMPLETED;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import uk.co.whitbread.basket.domain.model.basket.out.Basket;
import uk.co.whitbread.basket.domain.ports.secondary.BookingCompletedOutPort;
import uk.co.whitbread.basket.infrastructure.queue.model.BookingCompletedEvent;
import uk.co.whitbread.basket.infrastructure.queue.producer.BookingCompletedProducer;

@Slf4j
@AllArgsConstructor
public class BookingCompletedOutPortImpl implements BookingCompletedOutPort {

  private static final String STATUS_COMPLETED = "COMPLETED";
  private static final String STATUS_FAILED = "FAILED";

  private final BookingCompletedProducer bookingCompletedProducer;

  @Override
  public void publishBookingCompleted(final Basket basket) {
    var status = COMPLETED.equals(basket.getStatus()) ? STATUS_COMPLETED : STATUS_FAILED;
    var event = BookingCompletedEvent.builder()
        .basketReference(basket.getBasketId())
        .status(status)
        .build();
    log.info("Publishing booking completed event for basketReference={} with status={}",
        event.getBasketReference(), event.getStatus());
    bookingCompletedProducer.sendBookingCompletedEvent(event);
  }
}
