package uk.co.whitbread.ohip.infrastructure.rest.controller.reservation.model.in;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SpecialRequestsDto {

  @NotNull
  @Valid
  @Schema(required = true)
  private List<String> reservationIds;

  @NotNull
  @Valid
  @Schema(required = true)
  private String hotelId;

  @NotNull
  @Valid
  @Schema(required = true)
  private List<String> specialRequests;

  private List<String> bookingNotes;
}
