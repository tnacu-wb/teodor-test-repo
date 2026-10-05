package uk.co.whitbread.infrastructure.rest.controller.srp.model.out;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class HotelAvailabilitiesResponseDto {

  List<HotelAvailabilityResponseDto> hotelAvailabilities;
  Integer page;
  Integer pageSize;
  Integer total;
}
