package uk.co.whitbread.ohip.infrastructure.rest.controller.roomallocation.model.in;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CriteriaDto {

  @Schema(example = "LONSTM")
  private String hotelId;
  @NotNull
  @Valid
  @Schema(required = true)
  private List<ReservationIdListDto> reservationIdList;
  private String roomId;
  private boolean updateRoomTypeCharged;
  private boolean roomNumberLocked;

}
