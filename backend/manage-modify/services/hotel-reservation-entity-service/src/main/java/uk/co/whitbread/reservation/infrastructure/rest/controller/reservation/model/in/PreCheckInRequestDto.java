package uk.co.whitbread.reservation.infrastructure.rest.controller.reservation.model.in;

import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.Schema.RequiredMode;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import java.time.LocalDate;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@AllArgsConstructor
@Builder
public class PreCheckInRequestDto {

  @NotEmpty(message = "hotelId is required")
  @Schema(example = "STAUIR", requiredMode = RequiredMode.REQUIRED)
  private String hotelId;
  @NotEmpty(message = "reservationId is required")
  @Schema(example = "123456", requiredMode = RequiredMode.REQUIRED)
  private String reservationId;
  @NotNull(message = "arrivalTime is required")
  @Schema(example = "2024-07-13", requiredMode = RequiredMode.REQUIRED)
  private LocalDate arrivalTime;
  @Pattern(regexp = "EN|DE", message = "Language must be either 'EN' or 'DE'")
  @Schema(example = "EN")
  private String language;
}
