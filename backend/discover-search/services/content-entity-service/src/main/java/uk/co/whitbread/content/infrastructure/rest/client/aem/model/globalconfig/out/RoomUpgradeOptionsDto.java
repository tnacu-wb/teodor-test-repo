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
public class RoomUpgradeOptionsDto {

  private List<RoomUpgradesDto> roomUpgrades;
  private String priceText;
  private String primaryButtonText;
  private String secondaryButtonText;
}
