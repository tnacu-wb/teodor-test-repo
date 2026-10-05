package uk.co.whitbread.refund.processor.infrastructure.queue.model.refund;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Booking {
  private String type;
  private String journey;
  private String channel;
  private BusinessSite businessSite;
}
