package uk.co.whitbread.domain.model.packages.out;

import java.math.BigDecimal;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Meal {

  private String name;
  private String id;
  private BigDecimal price;
  private String idImg;
  private String idDesc;
  private String allergyInfoSrc;
  private String currency;
  private BigDecimal basePrice;
  private Boolean isFree;
  private String inventoryItem;
  private String promoText;
}
