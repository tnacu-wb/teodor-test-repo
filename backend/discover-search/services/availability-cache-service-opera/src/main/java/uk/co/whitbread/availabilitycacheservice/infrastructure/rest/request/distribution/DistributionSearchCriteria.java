package uk.co.whitbread.availabilitycacheservice.infrastructure.rest.request.distribution;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import uk.co.whitbread.availabilitycacheservice.infrastructure.validation.opera.OperaDateFormat;
import uk.co.whitbread.availabilitycacheservice.infrastructure.validation.opera.distribution.DistributionArrivalDepartureDateConstraint;
import uk.co.whitbread.availabilitycacheservice.infrastructure.validation.opera.distribution.DistributionHotelCodeConstraint;
import uk.co.whitbread.availabilitycacheservice.infrastructure.validation.opera.distribution.DistributionMaxNightsDateConstraint;
import uk.co.whitbread.availabilitycacheservice.infrastructure.validation.opera.distribution.DistributionMaxRoomConstraint;
import uk.co.whitbread.availabilitycacheservice.infrastructure.validation.opera.distribution.DistributionOccupantsPerRoomConstraint;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@DistributionArrivalDepartureDateConstraint
@DistributionMaxNightsDateConstraint(arrival = "arrival", departure = "departure")
@DistributionHotelCodeConstraint
@DistributionOccupantsPerRoomConstraint
@DistributionMaxRoomConstraint
public class DistributionSearchCriteria {

  @NotNull
  @Valid
  @Size(min = 1, message = "Room Type array size must be equal to the number of rooms")
  protected String[] roomTypes;
  @NotNull
  private List<String> hotelCodes;
  @NotBlank
  @OperaDateFormat
  private String arrival;
  @NotBlank
  @OperaDateFormat
  private String departure;
  @Size(min = 1, message = "Adults array size must be equal to the number of rooms")
  @NotNull
  private int[] adults;
  @NotNull
  @Size(min = 1, message = "Children array size must be equal to the number of rooms")
  private int[] children;
  @NotNull
  private boolean[] cot;
  @NotNull
  @Size(min = 1)
  private int[] roomQty;
  private String country;
  private String language;
  private int rooms;
  private Set<String> ratePlanCodes;

  @Override
  public String toString() {
    return "DistributionSearchCriteria{"
        + "hotelCodes=" + hotelCodes
        + ", arrival='" + arrival + '\''
        + ", departure='" + departure + '\''
        + ", adults=" + Arrays.toString(adults)
        + ", children=" + Arrays.toString(children)
        + ", cot=" + Arrays.toString(cot)
        + ", roomQty=" + Arrays.toString(roomQty)
        + ", country='" + country + '\''
        + ", language='" + language + '\''
        + ", roomTypes=" + Arrays.toString(roomTypes)
        + ", rooms=" + rooms
        + ", rateCodes=" + ratePlanCodes
        + '}';
  }
}
