package uk.co.whitbread.reservation.infrastructure.rest.controller.reservation.model.out;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class MarketingPreferencesResponseDto {

  private Boolean optIn;
  private CustomerDto customer;
  private String contactValue;

}
