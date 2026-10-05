package uk.co.whitbread.content.infrastructure.rest.controller.searchresults.data.model.out;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import uk.co.whitbread.content.infrastructure.rest.controller.header.data.model.out.BrandDto;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SrpGlobalDto {
  private BrandDto brand;
}
