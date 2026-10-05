package uk.co.whitbread.content.infrastructure.rest.client.aem.model.searchresults.data;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Label {
  private String restaurant;
  private String airCon;
  private String header;
  private String chargeableOffsiteParking;
  private String chargeableOnsiteParking;
  private String freeParking;
  private String parking;
  private String lift;
  private String meet;
  private String apply;
  private String reset;
  private String facilities;
}
