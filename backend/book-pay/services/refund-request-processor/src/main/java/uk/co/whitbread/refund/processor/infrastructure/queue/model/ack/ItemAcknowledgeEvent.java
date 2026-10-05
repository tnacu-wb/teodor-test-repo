package uk.co.whitbread.refund.processor.infrastructure.queue.model.ack;

import java.util.List;
import java.util.Map;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.Singular;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ItemAcknowledgeEvent {

  private String itemId;
  private String basketReference;
  @Singular
  private List<String> errors;
  private Integer status;
  private Map<String, String> data;
}
