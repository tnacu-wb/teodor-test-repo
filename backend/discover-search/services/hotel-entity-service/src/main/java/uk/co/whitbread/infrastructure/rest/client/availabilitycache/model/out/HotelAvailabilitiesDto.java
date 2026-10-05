package uk.co.whitbread.infrastructure.rest.client.availabilitycache.model.out;

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
