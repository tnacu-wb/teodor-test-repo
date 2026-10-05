package uk.co.whitbread.ohip.infrastructure.rest.controller.packages.model.out;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RestaurantDto {

  private String logoUrl;
  private Boolean restaurantNotFound;
  private Boolean noMealsFound;

}
