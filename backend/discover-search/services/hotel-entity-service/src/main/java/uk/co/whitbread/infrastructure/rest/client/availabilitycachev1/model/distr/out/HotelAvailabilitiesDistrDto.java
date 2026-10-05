package uk.co.whitbread.infrastructure.rest.client.availabilitycachev1.model.distr.out;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class HotelAvailabilitiesDistrDto {

  private int total;

  private List<HotelOperaDistrDto> operaHotelAvailabilities;

}
