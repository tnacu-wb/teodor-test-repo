package uk.co.whitbread.content.infrastructure.rest.client.content.model.in;

import java.util.Map;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CategoryLabelsDto {

  private String category;
  private Map<String, String> labels;
}
