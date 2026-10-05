package uk.co.whitbread.payments.infrastructure.rest.client.hotels.model.out;

import lombok.Data;

@Data
public class AddressDto {
  private String addressLine1;
  private String addressLine2;
  private String addressLine3;
  private String country;
  private String postalCode;
}
