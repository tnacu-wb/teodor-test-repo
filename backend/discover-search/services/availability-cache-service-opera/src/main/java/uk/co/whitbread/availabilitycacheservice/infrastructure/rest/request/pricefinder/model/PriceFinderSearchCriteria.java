package uk.co.whitbread.availabilitycacheservice.infrastructure.rest.request.pricefinder.model;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import uk.co.whitbread.availabilitycacheservice.infrastructure.validation.ArrivalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PriceFinderSearchCriteria {

  @NotNull
  private List<String> hotelCodes;

  @Min(value = 1, message = "Number of rooms must be 1 or greater!")
  private int rooms = 1;

  @NotBlank
  @ArrivalDate
  private String arrival;

  private String departure;

  private String country;

  private String language;

  private boolean showMinimumNights;

  @Override
  public String toString() {
    return "PriceFinderSearchCriteria{"
        + "hotelCodes=" + hotelCodes
        + ", rooms='" + rooms + '\''
        + ", arrival='" + arrival + '\''
        + ", departure='" + departure + '\''
        + ", country='" + country + '\''
        + ", language='" + language + '\''
        + ", showMinimumNights='" + showMinimumNights + '\''
        + '}';
  }
}
