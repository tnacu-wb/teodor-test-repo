package uk.co.whitbread.basket.domain.model.payments.in;

import jakarta.annotation.PostConstruct;
import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import uk.co.whitbread.basket.domain.model.validation.SelfValidation;

@Data
@AllArgsConstructor
@Builder
public class Address implements SelfValidation<Address> {

  @NotEmpty
  private String line1;
  private String line2;
  private String line3;
  private String line4;
  @NotEmpty
  private String cityName;
  @NotEmpty
  private String countryCode;
  @NotEmpty
  private String postalCode;
  private String companyName;
  private String addressType;

  public void validateAddress() {
    this.validateSelf();
  }
}
