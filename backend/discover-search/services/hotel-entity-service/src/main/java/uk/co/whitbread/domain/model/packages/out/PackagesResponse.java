package uk.co.whitbread.domain.model.packages.out;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PackagesResponse {

  private Restaurant restaurant;
  private Packages packages;
  private Boolean hotelHasCityTaxForLeisure;
  private Boolean hotelHasCityTaxForBusiness;

}
