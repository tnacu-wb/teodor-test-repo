package uk.co.whitbread.content.infrastructure.rest.controller.globalconfig.model.out;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UpsellItemsExtrasDto {

  private String promoText;
  private String promoPackageCode;
  private String packageCode;

}
