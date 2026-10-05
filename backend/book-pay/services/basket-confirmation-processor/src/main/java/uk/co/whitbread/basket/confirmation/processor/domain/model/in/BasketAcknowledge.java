package uk.co.whitbread.basket.confirmation.processor.domain.model.in;

import java.util.List;
import java.util.Map;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class BasketAcknowledge {
  private String itemId;
  private String basketReference;
  private List<String> errors;
  private Integer status;
  private Map<String, String> data;
}
