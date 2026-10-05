package uk.co.whitbread.basket.processor.infrastructure.queue.model.order;

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
  private Map<String, String> data;

}
