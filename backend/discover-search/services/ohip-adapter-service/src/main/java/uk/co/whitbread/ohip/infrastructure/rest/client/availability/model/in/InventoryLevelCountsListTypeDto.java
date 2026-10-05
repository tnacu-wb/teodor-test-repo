package uk.co.whitbread.ohip.infrastructure.rest.client.availability.model.in;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class InventoryLevelCountsListTypeDto {

  @JsonProperty("inventoryCounts")
  private List<InventoryCountsTypeDto> inventoryCounts;

  @JsonProperty("code")
  private String code;

  @JsonProperty("sequence")
  private Integer sequence;

}
