package uk.co.whitbread.refund.processor.infrastructure.queue.model.refund;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BusinessSite {

  private String identifier;
  private String type;

}
