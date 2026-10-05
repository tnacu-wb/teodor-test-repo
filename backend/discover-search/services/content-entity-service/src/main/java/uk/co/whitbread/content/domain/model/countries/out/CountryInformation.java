package uk.co.whitbread.content.domain.model.countries.out;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CountryInformation {

  private String countryCode;
  private String countryCodeLegacy;
  private String countryName;
  private Boolean passportRequired;
  private String dialingCode;
  private String flagSrc;
  private String nationality;  
}
