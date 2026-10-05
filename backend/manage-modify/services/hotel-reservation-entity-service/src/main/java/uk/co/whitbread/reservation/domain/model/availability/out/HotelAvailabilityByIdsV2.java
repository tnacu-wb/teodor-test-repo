package uk.co.whitbread.reservation.domain.model.availability.out;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class HotelAvailabilityByIdsV2 {

  List<HotelAvailabilityResultV2> hotelAvailability;
}
