package uk.co.whitbread.content.domain.model.pricefinder.out;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class InfoMessages {
  private String messageTitle;
  private String messageSubtitle;
  private String messageType;
  private Integer messageOrder;
}
