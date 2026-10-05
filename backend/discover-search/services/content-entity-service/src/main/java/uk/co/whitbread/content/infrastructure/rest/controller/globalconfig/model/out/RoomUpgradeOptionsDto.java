package uk.co.whitbread.content.infrastructure.rest.controller.globalconfig.model.out;

import java.util.List;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class RoomUpgradeOptionsDto {

  private List<RoomUpgradesDto> roomUpgrades;
  private String priceText;
  private String primaryButtonText;
  private String secondaryButtonText;
}
