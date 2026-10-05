package uk.co.whitbread.ohip.infrastructure.rest.controller.packages.model.out;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PackagesResponseDto {

  private RestaurantDto restaurant;
  private PackagesDto packages;
  private Boolean hotelHasCityTaxForLeisure;
  private Boolean hotelHasCityTaxForBusiness;

}
