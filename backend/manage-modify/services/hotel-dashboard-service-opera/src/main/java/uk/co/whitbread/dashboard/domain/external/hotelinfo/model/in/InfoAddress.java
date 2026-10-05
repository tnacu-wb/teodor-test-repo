package uk.co.whitbread.dashboard.domain.external.hotelinfo.model.in;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class InfoAddress {

  private String addressline1;
  private String addressline2;
  private String addressline3;
  private String addressline4;
  private String addressline5;
  private String country;
  private String postcode;

}
