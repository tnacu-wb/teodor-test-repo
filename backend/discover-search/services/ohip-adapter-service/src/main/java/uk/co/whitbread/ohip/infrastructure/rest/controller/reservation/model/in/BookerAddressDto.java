package uk.co.whitbread.ohip.infrastructure.rest.controller.reservation.model.in;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public class BookerAddressDto {

  @Schema(example = "HOME")
  private String addressType;
  @Schema(example = "WC2N 5DU")
  private String postalCode;
  @Schema(example = "4 Brockley Avenue")
  private String addressLine1;
  private String addressLine2;
  private String addressLine3;
  private String addressLine4;
  @Schema(example = "UK")
  private String countryCode;
  private String cityName;
  private String companyName;

}