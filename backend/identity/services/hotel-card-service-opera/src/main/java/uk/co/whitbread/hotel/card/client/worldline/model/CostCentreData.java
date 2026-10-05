package uk.co.whitbread.hotel.card.client.worldline.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class CostCentreData {

  private Integer accountUniqueCustomerId;
  private Integer costCentreUniqueCustomerId;
  private String costCentreCode;
  private String costCentreName;

}
