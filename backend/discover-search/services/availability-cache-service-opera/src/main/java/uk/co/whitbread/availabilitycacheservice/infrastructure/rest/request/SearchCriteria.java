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
import uk.co.whitbread.availabilitycacheservice.infrastructure.rest.request.enums.SortType;
import uk.co.whitbread.availabilitycacheservice.infrastructure.validation.ArrivalDepartureDateConstraint;
import uk.co.whitbread.availabilitycacheservice.infrastructure.validation.DateFormat;
import uk.co.whitbread.availabilitycacheservice.infrastructure.validation.HotelCodeConstraint;
import uk.co.whitbread.availabilitycacheservice.infrastructure.validation.MaxNightsDateConstraint;
import uk.co.whitbread.availabilitycacheservice.infrastructure.validation.OccupantsPerRoomConstraint;
import uk.co.whitbread.availabilitycacheservice.infrastructure.validation.RoomOccupantsConstraint;

@Data
@AllArgsConstructor
@NoArgsConstructor
@RoomOccupantsConstraint
@Builder
@OccupantsPerRoomConstraint
@ArrivalDepartureDateConstraint
@MaxNightsDateConstraint(arrival = "arrival", departure = "departure")
@HotelCodeConstraint
public class SearchCriteria {

  @NotNull
  @Valid
  @Size(min = 1, message = "Room Type array size must be equal to the number of rooms")
  protected String[] type;
  @NotNull
  private List<String> hotelCodes;
  @NotBlank
  @DateFormat
  private String arrival;
  @NotBlank
  @DateFormat
  private String departure;
  private boolean cot;
  private String language;
  private String country;
  @Min(1)
  @Max(value = 9, message = "Unable to add more rooms, If you’d like to book ten rooms or more, please call us and"
      + " we’ll be happy to help")
  private int rooms;
  @Size(min = 1, message = "Adults array size must be equal to the number of rooms")
  @NotNull
  private int[] adults;
  @NotNull
  @Size(min = 1, message = "Children array size must be equal to the number of rooms")
  private int[] children;
  private int page;

  private int size;

  private SortType sort = SortType.DISTANCE;

  private boolean flagMlos;

  private HotelsCityTaxInfo hotelsCityTaxInfo;

  @Override
  public String toString() {
    return "{"
        + "arrival='" + arrival + '\''
        + ", departure='" + departure + '\''
        + ", adults=" + Arrays.toString(adults)
        + ", children=" + Arrays.toString(children)
        + ", cot=" + cot
        + ", type=" + Arrays.toString(type)
        + ", rooms=" + rooms
        + ", language='" + language + '\''
        + ", country='" + country + '\''
        + ", page=" + page
        + ", size=" + size
        + ", sort=" + sort
        + ", hotelCodes=" + hotelCodes
        + ", flagMlos=" + flagMlos
        + ", hotelsCityTaxInfo=" + hotelsCityTaxInfo
        + '}';
  }
}
