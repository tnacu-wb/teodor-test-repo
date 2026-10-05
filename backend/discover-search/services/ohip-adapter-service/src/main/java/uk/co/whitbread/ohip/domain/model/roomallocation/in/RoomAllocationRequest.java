package uk.co.whitbread.ohip.domain.model.roomallocation.in;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import uk.co.whitbread.ohip.domain.model.validation.SelfValidation;

@Data
@Builder(toBuilder = true)
@EqualsAndHashCode(callSuper = false)
@AllArgsConstructor
public class RoomAllocationRequest implements SelfValidation<RoomAllocationRequest> {

  private Criteria criteria;

}
