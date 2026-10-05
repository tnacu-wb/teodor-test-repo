package uk.co.whitbread.ohip.domain.model.roomallocation.in;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import uk.co.whitbread.ohip.domain.model.validation.SelfValidation;

@Data
@Builder(toBuilder = true)
@EqualsAndHashCode(callSuper = false)
@AllArgsConstructor
public class Criteria implements SelfValidation<Criteria> {

  @Schema(example = "LONSTM")
  private String hotelId;
  @NotNull
  @Valid
  @Schema(required = true)
  private List<ReservationIdList> reservationIdList;
  private String roomId;
  private boolean updateRoomTypeCharged;
  private boolean roomNumberLocked;

}
