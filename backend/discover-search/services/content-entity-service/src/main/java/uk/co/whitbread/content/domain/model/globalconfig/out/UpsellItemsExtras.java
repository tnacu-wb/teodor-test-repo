package uk.co.whitbread.content.domain.model.globalconfig.out;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UpsellItemsExtras {

  private String promoText;
  private String promoPackageCode;
  private String packageCode;

}
