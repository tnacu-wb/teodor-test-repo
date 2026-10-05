package uk.co.whitbread.reservation.infrastructure.rest.controller.reservation.model.in;

import lombok.Builder;
import lombok.Data;
import uk.co.whitbread.shared.commons.validation.CompanyName;

@Data
@Builder
public class BookerAddressDto {

  private String addressType;
  private String postalCode;
  private String addressLine1;
  private String addressLine2;
  private String addressLine3;
  private String addressLine4;
  private String countryCode;
  private String cityName;
  @CompanyName
  private String companyName;

}
