package uk.co.whitbread.availabilitycacheservice.infrastructure.rest.request.hotelprice;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import uk.co.whitbread.availabilitycacheservice.infrastructure.validation.DateFormat;
import uk.co.whitbread.availabilitycacheservice.infrastructure.validation.MaxDateRangeConstraint;
import uk.co.whitbread.availabilitycacheservice.infrastructure.validation.RoomTypeConstraint;

@Data
@AllArgsConstructor
@NoArgsConstructor
@RoomTypeConstraint(roomType = "roomType")
@MaxDateRangeConstraint(arrival = "arrival", departure = "departure")
@Builder
public class HotelPriceRequest {

  @NotNull
  protected String roomType;
  @NotNull
  private List<String> hotelCodes;
  @NotBlank
  @DateFormat
  private String arrival;
  @NotBlank
  @DateFormat
  private String departure;

}
