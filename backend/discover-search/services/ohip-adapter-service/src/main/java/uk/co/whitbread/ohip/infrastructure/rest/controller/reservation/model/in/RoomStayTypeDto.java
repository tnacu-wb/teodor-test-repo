package uk.co.whitbread.ohip.infrastructure.rest.controller.reservation.model.in;


import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.util.List;
import lombok.Data;

@Data
public class RoomStayTypeDto {

  @NotEmpty
  @Size(min = 1)
  @Valid
  private List<RoomRateTypeDto> roomRates;

  @NotNull
  @Schema(example = "2025-03-31", required = true)
  @Valid
  private String arrivalDate;

  @NotNull
  @Schema(example = "2025-03-31", required = true)
  @Valid
  private String departureDate;
}
