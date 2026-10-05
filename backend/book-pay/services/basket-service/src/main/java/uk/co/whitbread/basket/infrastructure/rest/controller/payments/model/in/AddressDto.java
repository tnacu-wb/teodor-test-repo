package uk.co.whitbread.basket.infrastructure.rest.controller.payments.model.in;

import static uk.co.whitbread.basket.infrastructure.rest.controller.payments.model.validation.ValidBillingAddressValidator.LENGTH_INTERVAL;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import uk.co.whitbread.basket.domain.model.validation.SelfValidation;
import uk.co.whitbread.basket.infrastructure.rest.controller.payments.model.validation.ValidBillingAddress;
import uk.co.whitbread.shared.commons.validation.CompanyName;

@Data
@Builder
@NoArgsConstructor
@ValidBillingAddress
public class AddressDto implements SelfValidation<AddressDto> {

  @NotEmpty
  @NotNull
  @Size(max = 100, message = LENGTH_INTERVAL)
  private String addressLine1;
  @Size(max = 100, message = LENGTH_INTERVAL)
  private String addressLine2;
  @Size(max = 100, message = LENGTH_INTERVAL)
  private String addressLine3;
  @Size(max = 100, message = LENGTH_INTERVAL)
  private String addressLine4;
  private String cityName;
  private String country;
  private String postalCode;
  private String channel;
  @CompanyName
  private String companyName;
  private String addressType;

  public AddressDto(String addressLine1, String addressLine2, String addressLine3,
      String addressLine4, String cityName, String country, String postalCode, String channel,
      String companyName, String addressType) {
    this.addressLine1 = addressLine1;
    this.addressLine2 = addressLine2;
    this.addressLine3 = addressLine3;
    this.addressLine4 = addressLine4;
    this.cityName = cityName;
    this.country = country;
    this.postalCode = postalCode;
    this.channel = channel;
    this.companyName = companyName;
    this.addressType = addressType;
  }
}
