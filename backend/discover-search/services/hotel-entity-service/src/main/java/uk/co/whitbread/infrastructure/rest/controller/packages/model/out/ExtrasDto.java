package uk.co.whitbread.infrastructure.rest.controller.packages.model.out;

import java.math.BigDecimal;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ExtrasDto {

  private String id;
  private String name;
  private String description;
  private String imageSrc;
  private BigDecimal price;
  private String currency;
  private Integer available;
  private Integer order;
  private BigDecimal basePrice;
  private Boolean isFree;
  private String promoText;
}
