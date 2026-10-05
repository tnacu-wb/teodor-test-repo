package uk.co.whitbread.content.infrastructure.rest.controller.meal.model.out;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MealsInfoResponseDto {

  private List<UpsellItemsDto> upsellItems;
  private List<SoftBundleDto> softBundles;
}
