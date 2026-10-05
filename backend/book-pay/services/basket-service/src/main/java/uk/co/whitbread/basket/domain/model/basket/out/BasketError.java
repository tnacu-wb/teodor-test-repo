package uk.co.whitbread.basket.domain.model.basket.out;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BasketError {

  private String code;
  private String description;
  private BasketErrorType type;
}