package uk.co.whitbread.infrastructure.rest.client.availabilitycachev1.model.out;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class HotelAvailabilitiesDto {

  private int total;

  private List<HotelOperaDto> operaHotelAvailabilities;

}
