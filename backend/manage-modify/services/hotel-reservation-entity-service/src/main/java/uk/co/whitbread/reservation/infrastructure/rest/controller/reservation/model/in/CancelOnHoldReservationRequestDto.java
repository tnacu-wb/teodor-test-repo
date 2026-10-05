package uk.co.whitbread.reservation.infrastructure.rest.controller.reservation.model.in;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CancelOnHoldReservationRequestDto {

  @NotEmpty
  @Valid
  @JsonProperty("basketReference")
  @Schema(example = "ABC12345", required = true)
  private String basketReference;

  private List<String> reservationIds;

  @NotEmpty
  @Schema(example = "LONSTM", required = true)
  private String hotelId;
}
