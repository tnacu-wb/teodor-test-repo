package uk.co.whitbread.content.infrastructure.rest.client.aem.model.searchresults.data;

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
