package uk.co.whitbread.reservation.infrastructure.rest.controller.reservation.model.in;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class FindBookingKioskRequestDto {

  @NotEmpty
  @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
  @Size(min = 4, max = 20, message = " length is incorrect, it must be between 4 and 20 characters.")
  private String resNo;

  @Schema(defaultValue = "gb")
  @Builder.Default()
  private String country = "gb";

  @Schema(defaultValue = "en")
  @Builder.Default()
  private String language = "en";

}
