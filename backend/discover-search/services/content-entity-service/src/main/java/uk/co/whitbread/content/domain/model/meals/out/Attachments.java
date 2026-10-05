package uk.co.whitbread.content.domain.model.meals.out;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Attachments {

  private String path;
  private String label;
  private String type;
}
