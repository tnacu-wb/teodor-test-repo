package uk.co.whitbread.content.domain.model.globalconfig.out;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import uk.co.whitbread.content.domain.model.pricefinder.out.PriceFinderConfig;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class GlobalConfig {

  private SearchRules maxRoomsLim;
  private List<RoomClassOrder> roomClassConfig;
  private RoomUpgradeOptions roomUpgradeOptions;
  private List<String> hotelsWithCityTax;
  private PromotionsConfig promotionsConfig;
  private PriceFinderConfig priceFinderConfig;
  private String brand;
  private List<UpsellItemsExtras> upsellItemsExtras;
}
