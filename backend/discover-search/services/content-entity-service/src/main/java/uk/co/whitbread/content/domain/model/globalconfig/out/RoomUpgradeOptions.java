package uk.co.whitbread.content.domain.model.globalconfig.out;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RoomUpgradeOptions {

  private List<RoomUpgrades> roomUpgrades;
  private String priceText;
  private String primaryButtonText;
  private String secondaryButtonText;
}
