package uk.co.whitbread.ohip.domain.model.packages.out;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Restaurant {

  private String logoUrl;
  private Boolean restaurantNotFound;
  private Boolean noMealsFound;

}
