package uk.co.whitbread.reservation.infrastructure.rest.controller.reservation.model.in;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class BookerAddressCnpDto {

  @Schema(example = "EC1A 1BB")
  private String postalCode;
  @Schema(example = "4 Brockley Avenue")
  private String addressLine1;
  @Schema(example = "London District 2")
  private String addressLine2;
  @Schema(example = "Greater London - sub district 2")
  private String addressLine3;
  @Schema(example = "Greater London - street 22")
  private String addressLine4;
}
