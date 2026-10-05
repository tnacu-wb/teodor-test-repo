package uk.co.whitbread.content.infrastructure.rest.client.aem.model.globalconfig.out;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import uk.co.whitbread.content.infrastructure.rest.client.aem.model.pricefinderconfig.out.PriceFinderConfigDto;
import uk.co.whitbread.content.infrastructure.rest.controller.globalconfig.model.out.UpsellItemsExtrasDto;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GlobalConfigDto {

  private List<RoomClassOrderDto> roomClassConfig;
  private BookingWidgetConfigDto bookingWidgetConfig;
  private List<Offer> offers;
  private String brand;
  private RoomUpgradeOptionsDto roomUpgradeOptions;
  private List<String> hotelsWithCityTax;
  private PriceFinderConfigDto priceFinderConfig;
  private PromotionsConfigDto promotionsConfig;
  private List<UpsellItemsExtrasDto> upsellItemsExtras;
}
