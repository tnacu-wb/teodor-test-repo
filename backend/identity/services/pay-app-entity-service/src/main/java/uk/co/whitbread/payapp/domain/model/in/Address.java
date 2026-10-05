package uk.co.whitbread.payapp.domain.model.in;

import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Value;
import uk.co.whitbread.payapp.domain.model.validation.DomainValidator;

@Value
@Builder
@EqualsAndHashCode(callSuper = false)
public class Address extends DomainValidator<Address> {

  @NotBlank
  String addressLine1;

  @NotBlank
  String addressLine2;

  String addressLine3;

  String addressLine4;

  String postcode;

  String countryCode;

  public Address(String addressLine1, String addressLine2, String addressLine3, String addressLine4,
      String postcode, String countryCode) {
    this.addressLine1 = addressLine1;
    this.addressLine2 = addressLine2;
    this.addressLine3 = addressLine3;
    this.addressLine4 = addressLine4;
    this.postcode = postcode;
    this.countryCode = countryCode;
    this.validateSelf();
  }

}
