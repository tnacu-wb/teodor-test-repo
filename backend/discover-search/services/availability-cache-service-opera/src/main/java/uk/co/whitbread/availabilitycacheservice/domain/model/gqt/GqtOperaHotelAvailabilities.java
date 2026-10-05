package uk.co.whitbread.availabilitycacheservice.domain.model.gqt;

import java.util.Set;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class GqtOperaHotelAvailabilities {

  private String hotelCode;

  private Set<Availabilities> availabilities;

}
