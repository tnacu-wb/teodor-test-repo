package uk.co.whitbread.basket.processor.domain.model.in;

import java.util.Map;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class BasketOrder {
  private String id;
  private String basketReference;
  private Map<String, String>  data;
}
