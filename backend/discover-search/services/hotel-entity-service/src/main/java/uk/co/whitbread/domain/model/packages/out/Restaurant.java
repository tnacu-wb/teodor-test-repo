package uk.co.whitbread.domain.model.packages.out;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Restaurant {

  private String logoSrc;
  private Boolean restaurantNotFound;
  private Boolean noMealsFound;

}
