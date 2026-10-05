package uk.co.whitbread.infrastructure.rest.controller.availability.model.out;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RoomRateInfoV2Dto {

  private List<PriceInfoDto> priceInfo;
  private List<PackageInfoDto> packages;
}
