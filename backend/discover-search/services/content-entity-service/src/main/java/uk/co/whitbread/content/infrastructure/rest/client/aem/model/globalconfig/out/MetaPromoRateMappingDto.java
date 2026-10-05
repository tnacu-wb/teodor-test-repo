package uk.co.whitbread.content.infrastructure.rest.client.aem.model.globalconfig.out;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class MetaPromoRateMappingDto {

  private String rate;
  private String code;
}
