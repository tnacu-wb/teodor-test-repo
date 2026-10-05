package uk.co.whitbread.content.infrastructure.rest.client.countries.model.in;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CountryInfoAemDto {

  private String countryCode;
  @JsonProperty("countryCodeISO")
  private String countryCodeIso;
  private String countryLegend;
  private Boolean passportRequired;
  private String dialingCode;
  private String flagImg;
  private String nationality;  
}
