package uk.co.whitbread.ohip.infrastructure.rest.controller.reservation.model.in;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import java.util.Set;
import lombok.Data;

@Data
public class CopyReservationsRequestDto {

  @NotNull
  private String externalReferenceId;
  @NotNull
  private String hotelId;
  @NotEmpty
  private Set<String> reservationIds;
}

