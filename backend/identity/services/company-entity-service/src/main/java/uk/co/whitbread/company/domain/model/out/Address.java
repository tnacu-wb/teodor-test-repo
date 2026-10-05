package uk.co.whitbread.company.domain.model.out;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Value;

@Value
@Builder(toBuilder = true)
@EqualsAndHashCode(callSuper = false)
public class Address {
  String addressLine1;
  String addressLine2;
  String addressLine3;
  String addressLine4;
  String city;
  String country;
  String postalCode;
}
