package uk.co.whitbread.basket.infrastructure.rest.controller.ccui.eckoh.model.in;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EckohAddressDto {

  private String line1;
  private String line2;
  private String line3;
  private String line4;
  private String postalCode;
  private String countryCode;

}
