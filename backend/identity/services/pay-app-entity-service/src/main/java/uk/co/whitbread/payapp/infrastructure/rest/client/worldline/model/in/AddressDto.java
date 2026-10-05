package uk.co.whitbread.payapp.infrastructure.rest.client.worldline.model.in;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AddressDto {

  private String addressLine1;

  private String addressLine2;

  private String addressLine3;

  private String addressLine4;

  private String postcode;

  private String countryCode;

}
