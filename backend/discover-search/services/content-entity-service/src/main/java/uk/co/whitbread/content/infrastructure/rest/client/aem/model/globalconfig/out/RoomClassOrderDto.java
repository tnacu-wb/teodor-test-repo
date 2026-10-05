package uk.co.whitbread.content.infrastructure.rest.client.aem.model.globalconfig.out;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RoomClassOrderDto {

  private String code;
  private int order;
  private List<String> availableUpgrades;
}
