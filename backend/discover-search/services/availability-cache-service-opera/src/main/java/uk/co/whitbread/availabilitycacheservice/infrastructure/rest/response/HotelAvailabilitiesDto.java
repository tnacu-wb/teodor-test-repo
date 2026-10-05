package uk.co.whitbread.availabilitycacheservice.infrastructure.rest.response;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class HotelAvailabilitiesDto {

  private int total;

  private int page;

  private int pageSize;

  private List<HotelDto> hotelAvailabilities;

}
