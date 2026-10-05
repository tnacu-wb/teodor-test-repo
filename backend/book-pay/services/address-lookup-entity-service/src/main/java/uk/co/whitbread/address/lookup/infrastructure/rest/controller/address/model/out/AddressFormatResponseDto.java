package uk.co.whitbread.address.lookup.infrastructure.rest.controller.address.model.out;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@AllArgsConstructor
public class AddressFormatResponseDto {

  private String addressLine1;
  private String addressLine2;
  private String addressLine3;
  private String addressLine4;
  private String addressLine5;
  private String companyName;
  private String label;
  private String postalCode;
  private String country;

}
