package uk.co.whitbread.reservation.infrastructure.rest.controller.amend.model.out;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class HotelAvailabilityByIdsDto {

  private List<HotelAvailabilityDto> hotelAvailability;

}
