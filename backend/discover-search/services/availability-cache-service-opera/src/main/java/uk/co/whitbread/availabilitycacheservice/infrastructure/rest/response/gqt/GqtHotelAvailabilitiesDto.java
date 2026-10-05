package uk.co.whitbread.availabilitycacheservice.infrastructure.rest.response.gqt;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@AllArgsConstructor
@NoArgsConstructor
public class GqtHotelAvailabilitiesDto {

  private List<GqtOperaHotelAvailabilitiesDto> gqtOperaHotelAvailabilities;

}
