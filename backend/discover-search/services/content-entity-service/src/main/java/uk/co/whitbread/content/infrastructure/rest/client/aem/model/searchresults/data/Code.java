package uk.co.whitbread.content.infrastructure.rest.client.aem.model.searchresults.data;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Code {
  private String restaurant;
  private String airCon;
  private String chargeableOffsiteParking;
  private String chargeableOnsiteParking;
  private List<String> freeParking;
  private String lift;
  private String meet;
}
