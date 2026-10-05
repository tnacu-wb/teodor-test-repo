package uk.co.whitbread.ohip.infrastructure.rest.controller.availability.model.out;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class HotelAvailabilityResultDto {

  String hotelId;
  Boolean available;
  String arrivalDate;
  String departureDate;
  List<MultiRoomTypeDto> roomTypes;
}
