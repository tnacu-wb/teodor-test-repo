package uk.co.whitbread.availabilitycacheservice.domain.model.gqt;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import uk.co.whitbread.availabilitycacheservice.infrastructure.rest.request.HotelsCityTaxInfo;
import uk.co.whitbread.availabilitycacheservice.infrastructure.validation.DateFormat;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class GqtSearchPayload {

  @NotNull
  private List<String> hotelCodes;

  @NotBlank
  @DateFormat
  private String arrival;

  @NotBlank
  @DateFormat
  private String departure;

  private String country;

  private String language;

  private HotelsCityTaxInfo hotelsCityTaxInfo;

  @Override
  public String toString() {
    return "GqtSearchPayload{"
        + "hotelCodes=" + hotelCodes
        + ", arrival='" + arrival + '\''
        + ", departure='" + departure + '\''
        + ", country='" + country + '\''
        + ", language='" + language + '\''
        + ", hotelsCityTaxInfo=" + hotelsCityTaxInfo
        + '}';
  }
}
