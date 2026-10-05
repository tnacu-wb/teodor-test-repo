package uk.co.whitbread.content.infrastructure.rest.controller.globalconfig.model.out;

import java.util.List;
import lombok.Builder;
import lombok.Data;
import uk.co.whitbread.content.infrastructure.rest.client.aem.model.globalconfig.out.PromotionsConfigResponseDto;
import uk.co.whitbread.content.infrastructure.rest.controller.pricefinder.model.out.PriceFinderConfigDto;

@Data
@Builder
public class GlobalConfigDto {

  private SearchRulesDto maxRoomsLim;
  private List<RoomClassOrderDto> roomClassConfig;
  private RoomUpgradeOptionsDto roomUpgradeOptions;
  private PromotionsConfigResponseDto promotionsConfig;
  private PriceFinderConfigDto priceFinderConfig;
  private List<String> hotelsWithCityTax;
  private List<UpsellItemsExtrasDto> upsellItemsExtras;
}
