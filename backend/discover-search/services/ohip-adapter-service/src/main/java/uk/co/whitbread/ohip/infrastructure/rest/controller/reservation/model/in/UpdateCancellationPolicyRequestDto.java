package uk.co.whitbread.ohip.infrastructure.rest.controller.reservation.model.in;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import java.util.Date;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UpdateCancellationPolicyRequestDto {

  @NotEmpty
  private String hotelId;

  @NotEmpty
  private String reservationId;

  @NotNull
  @Schema(example = "2015-10-20", required = true)
  private Date absoluteDeadline;
}
