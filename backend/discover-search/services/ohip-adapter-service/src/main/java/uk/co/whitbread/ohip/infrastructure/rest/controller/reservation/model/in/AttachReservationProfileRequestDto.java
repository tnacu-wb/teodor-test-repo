package uk.co.whitbread.ohip.infrastructure.rest.controller.reservation.model.in;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import java.util.Set;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@SuppressWarnings("common-java:DuplicatedBlocks")
public class AttachReservationProfileRequestDto {

  @NotNull
  @Valid
  @Schema
  private Set<String> reservationIds;

  @NotNull
  @Valid
  @Schema
  private String hotelId;

  @NotNull
  @Valid
  @Schema
  private String profileId;

}
