package uk.co.whitbread.ohip.domain.model.rates.out;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@Builder
@NoArgsConstructor
@Data
public class Classifications {

  private String rateCategory;
  private String displaySet;
  private String marketCode;

}
