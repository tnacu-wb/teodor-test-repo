package uk.co.whitbread.basket.domain.model.ccuieckoh.in;


import lombok.Builder;
import lombok.Data;
import uk.co.whitbread.basket.domain.model.validation.SelfValidation;

@Data
@Builder
public class AddressCcui implements SelfValidation<AddressCcui> {

  private String line1;
  private String line2;
  private String line3;
  private String line4;
  private String cityName;
  private String countryCode;
  private String postalCode;
  private String companyName;
  private String addressType;

  public AddressCcui(String addressLine1, String addressLine2, String addressLine3,
      String addressLine4, String cityName, String country, String postalCode,
      String companyName, String addressType) {
    this.line1 = addressLine1;
    this.line2 = addressLine2;
    this.line3 = addressLine3;
    this.line4 = addressLine4;
    this.cityName = cityName;
    this.countryCode = country;
    this.postalCode = postalCode;
    this.companyName = companyName;
    this.addressType = addressType;
    this.validateSelf();
  }
}
