package uk.co.whitbread.ohip.infrastructure.rest.controller.packages.model.out;

import java.math.BigDecimal;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MealDto {

  private String title;
  private String id;
  private BigDecimal price;
  private String idImg;
  private String idDesc;
  private String allergyInfoUrl;
  private String currency;
  private String inventoryItem;

}
