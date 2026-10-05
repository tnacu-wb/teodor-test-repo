package uk.co.whitbread.ohip.infrastructure.rest.controller.profile.model.out;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AddressDto {
  private String addressLine1;
  private String addressLine2;
  private String addressLine3;
  private String addressLine4;
  private String city;
  private String country;
  private String postalCode;
}
