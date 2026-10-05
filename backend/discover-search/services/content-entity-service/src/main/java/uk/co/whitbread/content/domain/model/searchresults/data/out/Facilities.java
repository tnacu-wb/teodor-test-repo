package uk.co.whitbread.content.domain.model.searchresults.data.out;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Facilities {

  private String premierPlusRoom;
  private String standardExtraRoom;
  private String businessRoom;
  private String parking;
  private String noParking;
  private String freeParking;
}
