package uk.co.whitbread.account.infrastructure.rest.controller.account.model.in;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import uk.co.whitbread.shared.commons.validation.CompanyName;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class AddressDto {
  @JsonAlias("addressLine1")
  private String line1;
  @JsonAlias("addressLine2")
  private String line2;
  @JsonAlias("addressLine3")
  private String line3;
  @JsonAlias("cityName")
  private String line4;
  @JsonAlias("cityName")
  private String line5;
  @JsonAlias("postalCode")
  private String postCode;
  private String countryCode;
  @NotNull
  @JsonAlias("addressType")
  private AddressTypeDto type;
  @CompanyName
  private String companyName;
}
