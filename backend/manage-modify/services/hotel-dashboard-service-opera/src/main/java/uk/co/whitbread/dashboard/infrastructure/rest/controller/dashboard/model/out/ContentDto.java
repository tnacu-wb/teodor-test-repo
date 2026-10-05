package uk.co.whitbread.dashboard.infrastructure.rest.controller.dashboard.model.out;

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
public class ContentDto {

  private String hotelImage;
  private String hotelName;
  private String hotelCode;
  private String hotelBrand;
  private boolean checkedIn;
  private MapDto map;
  private AddressDto address;
  private String confirmationNumber;
  private LocalDate arrivalDate;
  private LocalDate departureDate;
  private List<RoomDto> rooms;
  private int guests;
  private List<ActionDto> actions;
  private List<FrequentBookingDto> frequentBookings;
}
