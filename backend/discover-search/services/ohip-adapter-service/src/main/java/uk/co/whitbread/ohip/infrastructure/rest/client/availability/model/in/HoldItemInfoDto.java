package uk.co.whitbread.ohip.infrastructure.rest.client.availability.model.in;

import java.util.List;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class HoldItemInfoDto {

  private HeldByIdDto heldById;
  private String heldBy;
  private List<HoldItemInfoDetailDto> holdItemInfoList;
  private String hotelId;

}
