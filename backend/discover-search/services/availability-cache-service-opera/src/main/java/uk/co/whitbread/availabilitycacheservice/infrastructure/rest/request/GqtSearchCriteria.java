package uk.co.whitbread.availabilitycacheservice.infrastructure.rest.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import uk.co.whitbread.availabilitycacheservice.infrastructure.validation.DateFormat;
import uk.co.whitbread.availabilitycacheservice.infrastructure.validation.gqt.GqtArrivalDepartureDateConstraint;
import uk.co.whitbread.availabilitycacheservice.infrastructure.validation.gqt.GqtHotelCodeConstraint;
import uk.co.whitbread.availabilitycacheservice.infrastructure.validation.gqt.GqtMaxNightsDateConstraint;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@GqtArrivalDepartureDateConstraint
@GqtMaxNightsDateConstraint(arrival = "arrival", departure = "departure")
@GqtHotelCodeConstraint
public class GqtSearchCriteria {

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

  @Override
  public String toString() {
    return "GqtSearchCriteria{"
        + "hotelCodes=" + hotelCodes
        + ", arrival='" + arrival + '\''
        + ", departure='" + departure + '\''
        + ", country='" + country + '\''
        + ", language='" + language + '\''
        + '}';
  }
}
