package uk.co.whitbread.content.infrastructure.rest.client.meals.model.in;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MealsInfoResponseAemDto {

  private List<UpsellItemsAemDto> upsellItems;
  private List<SoftBundleAemDto> softBundles;
}
