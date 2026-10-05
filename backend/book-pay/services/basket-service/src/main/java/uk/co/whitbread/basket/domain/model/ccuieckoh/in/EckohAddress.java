package uk.co.whitbread.basket.domain.model.ccuieckoh.in;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EckohAddress {

  private String line1;
  private String line2;
  private String line3;
  private String line4;
  private String cityName;
  private String postalCode;
  private String countryCode;

}
