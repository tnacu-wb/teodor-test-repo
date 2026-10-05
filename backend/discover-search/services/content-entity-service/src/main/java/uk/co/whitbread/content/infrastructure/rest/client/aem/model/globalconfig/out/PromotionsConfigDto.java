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

public class PromotionsConfigDto {

  private List<PromotionItemsDto> promoItems;
  private PromoBoxDto promoBox;
  private List<MetaPromoRateMappingDto> metaPromoRateMapping;
}
