package uk.co.whitbread.availabilitycacheservice.infrastructure.rest.response.gqt;

import java.util.Set;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RateDto {

  private boolean available;
  private String code;
  private String classification;
  private String currency;
  private Set<GqtRoomDto> rooms;

}
