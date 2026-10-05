package uk.co.whitbread.ohip.infrastructure.rest.controller.reservation.model.in;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import java.util.Set;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UpdateCustomReferenceNumberRequestDto {

  @NotEmpty
  @Valid
  @Schema(required = true)
  private Set<String> reservationIds;

  @NotEmpty
  @Valid
  @Schema(required = true)
  private String hotelId;

  @NotNull
  @Valid
  private String customReferenceNumber;
}
