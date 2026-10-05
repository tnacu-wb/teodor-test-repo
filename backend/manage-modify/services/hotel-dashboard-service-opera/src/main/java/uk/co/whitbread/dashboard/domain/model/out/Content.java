package uk.co.whitbread.dashboard.domain.model.out;

import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDate;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Content {

  private String hotelImage;
  private String hotelName;
  private String hotelBrand;
  private String hotelCode;
  private boolean checkedIn;
  private Map map;
  private Address address;
  private String confirmationNumber;
  @Schema(type = "string", format = "date", example = "2022-01-01")
  private LocalDate arrivalDate;
  @Schema(type = "string", format = "date", example = "2022-01-01")
  private LocalDate departureDate;
  private List<Room> rooms;
  private int guests;
  private List<Action> actions;
  private List<FrequentBooking> frequentBookings;
}
