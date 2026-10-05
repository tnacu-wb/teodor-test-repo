package uk.co.whitbread.reservation.domain.model.out;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AddressResponse {

  private String companyName;
  private String countryCode;
  private String cityName;
  private String line1;
  private String line2;
  private String line3;
  private String line4;
  private String postalCode;

}
