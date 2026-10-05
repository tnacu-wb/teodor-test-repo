package uk.co.whitbread.availabilitycacheservice.infrastructure.rest.response.gqt;

import java.util.Set;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class GqtOperaHotelAvailabilitiesDto {

  private String hotelCode;

  private Set<AvailabilitiesDto> availabilities;

}
