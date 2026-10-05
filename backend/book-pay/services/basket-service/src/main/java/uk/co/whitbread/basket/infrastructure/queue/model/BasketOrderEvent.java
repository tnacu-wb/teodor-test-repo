package uk.co.whitbread.basket.infrastructure.queue.model;

import java.util.Map;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class BasketOrderEvent {
  private String eventId;
  private String basketReference;
  private String bookingReference;
  private Map<String, String> data;
}
