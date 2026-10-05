package uk.co.whitbread.basket.confirmation.processor.infrastructure.queue.model.in;

import java.util.List;
import java.util.Map;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BasketAcknowledgeEventDto {
  private String itemId;
  private String basketReference;
  private List<String> errors;
  private Integer status;
  private Map<String, String> data;
}
