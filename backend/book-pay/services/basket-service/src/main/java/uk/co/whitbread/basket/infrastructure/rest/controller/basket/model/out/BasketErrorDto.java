package uk.co.whitbread.basket.infrastructure.rest.controller.basket.model.out;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BasketErrorDto {

  private String code;
  private String description;
  private BasketErrorTypeDto type;
}
