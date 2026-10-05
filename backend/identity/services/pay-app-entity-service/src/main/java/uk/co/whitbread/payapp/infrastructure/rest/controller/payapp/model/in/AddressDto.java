package uk.co.whitbread.payapp.infrastructure.rest.controller.payapp.model.in;

import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import uk.co.whitbread.payapp.infrastructure.rest.controller.payapp.model.validation.ModelValidator;

@Data
@EqualsAndHashCode(callSuper = false)
@Builder
@NoArgsConstructor
public class AddressDto extends ModelValidator<AddressDto> {

  @NotBlank
  private String addressLine1;

  @NotBlank
  private String addressLine2;

  private String addressLine3;

  private String addressLine4;

  private String postcode;

  private String countryCode;

  public AddressDto(String addressLine1, String addressLine2, String addressLine3, String addressLine4,
      String postcode, String countryCode) {
    this.addressLine1 = addressLine1;
    this.addressLine2 = addressLine2;
    this.addressLine3 = addressLine3;
    this.addressLine4 = addressLine4;
    this.postcode = postcode;
    this.countryCode = countryCode;
    this.validate();
  }

}
