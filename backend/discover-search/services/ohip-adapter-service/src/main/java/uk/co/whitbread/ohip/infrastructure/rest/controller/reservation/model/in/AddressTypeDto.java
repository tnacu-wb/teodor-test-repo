package uk.co.whitbread.ohip.infrastructure.rest.controller.reservation.model.in;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;
import lombok.Data;
import uk.co.whitbread.ohip.domain.model.profile.in.Country;


@Data
public class AddressTypeDto {

  @Schema
  private boolean validated;
  @Schema
  private List<String> addressLine;
  @Schema
  private String cityName;
  @Schema
  private String postalCode;
  @Schema
  private String state;
  @Schema
  private Country country;
  @Schema
  private String language;
  @Schema
  private String type;
  @Schema
  private boolean primaryInd;

}
