package uk.co.whitbread.availabilitycacheservice.infrastructure.rest.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.util.Arrays;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import uk.co.whitbread.availabilitycacheservice.infrastructure.validation.opera.OperaArrivalDepartureDateConstraint;
import uk.co.whitbread.availabilitycacheservice.infrastructure.validation.opera.OperaDateFormat;
import uk.co.whitbread.availabilitycacheservice.infrastructure.validation.opera.OperaHotelCodeConstraint;
import uk.co.whitbread.availabilitycacheservice.infrastructure.validation.opera.OperaMaxNightsDateConstraint;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@OperaArrivalDepartureDateConstraint
@OperaMaxNightsDateConstraint(arrival = "arrival", departure = "departure")
@OperaHotelCodeConstraint
public class OperaHotelsSearchCriteria {

  @NotNull
  @Valid
  protected String[][] roomTypes;
  @NotNull
  private List<String> hotelCodes;
  @NotBlank
  @OperaDateFormat
  private String arrival;
  @NotBlank
  @OperaDateFormat
  private String departure;
  private boolean[] cot;
  private String language;
  private String country;
  @Min(1)
  @Max(value = 4, message = "Unable to add more rooms, If you’d like to book five rooms or more, please call us and "
      + "we’ll be happy to help")
  private int rooms;
  @Size(min = 1, message = "Adults array size must be equal to the number of rooms")
  @NotNull
  private int[] adults;
  @NotNull
  @Size(min = 1, message = "Children array size must be equal to the number of rooms")
  private int[] children;
  @NotNull
  @Size(min = 1)
  private int[] roomQty;

  private boolean flagMlos;

  @Override
  public String toString() {
    return "{"
        + "arrival='" + arrival + '\''
        + ", departure='" + departure + '\''
        + ", adults=" + Arrays.toString(adults)
        + ", children=" + Arrays.toString(children)
        + ", cot=" + Arrays.toString(cot)
        + ", roomTypes=" + Arrays.toString(roomTypes)
        + ", roomQty=" + Arrays.toString(roomQty)
        + ", rooms=" + rooms
        + ", language='" + language + '\''
        + ", country='" + country + '\''
        + ", hotelCodes=" + hotelCodes
        + ", flagMlos=" + flagMlos
        + '}';
  }

}
