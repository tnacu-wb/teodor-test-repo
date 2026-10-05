package uk.co.whitbread.cdh.domain.model.account.out;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CompanyAddress {

  @JsonProperty("AddressLine1")
  private String addressLine1;
  @JsonProperty("AddressLine2")
  private String addressLine2;
  @JsonProperty("AddressLine3")
  private String addressLine3;
  @JsonProperty("AddressLine4")
  private String addressLine4;
  @JsonProperty("AddressLine5")
  private String addressLine5;
  @JsonProperty("CountryCode")
  private String countryCode;
  @JsonProperty("PostCode")
  private String postCode;
}
