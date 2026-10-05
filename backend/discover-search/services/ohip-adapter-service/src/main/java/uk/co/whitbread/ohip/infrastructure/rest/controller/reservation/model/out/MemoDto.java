package uk.co.whitbread.ohip.infrastructure.rest.controller.reservation.model.out;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MemoDto {

  private List<MemoIdsDto> ids;
  private String description;
  private String createdOn;
  private String createdBy;
  private String modifiedOn;
  private String modifiedBy;
  private String memoType;

}
