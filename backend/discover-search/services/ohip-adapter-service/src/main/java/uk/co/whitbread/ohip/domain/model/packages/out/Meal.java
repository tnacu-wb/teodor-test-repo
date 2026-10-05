package uk.co.whitbread.ohip.domain.model.packages.out;

import java.math.BigDecimal;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
public class Meal {

  private String title;
  private String id;
  private BigDecimal price;
  private String idImg;
  private String idDesc;
  private String allergyInfoUrl;
  private String currency;
  private String inventoryItem;

}
