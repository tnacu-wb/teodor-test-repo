package uk.co.whitbread.kiosk.domain.model.roomallocation.in;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@Builder(toBuilder = true)
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
@AllArgsConstructor
public class Criteria {

  private String hotelId;
  @NotNull
  @Valid
  private List<ReservationIdList> reservationIdList;
  private String roomId;
  private boolean updateRoomTypeCharged;
  private boolean roomNumberLocked;

}
