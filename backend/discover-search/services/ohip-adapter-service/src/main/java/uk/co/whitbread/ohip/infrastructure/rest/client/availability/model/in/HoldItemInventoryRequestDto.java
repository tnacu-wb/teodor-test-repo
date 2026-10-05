package uk.co.whitbread.ohip.infrastructure.rest.client.availability.model.in;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class HoldItemInventoryRequestDto {

  private HoldItemInfoDto holdItemInfo;

}
