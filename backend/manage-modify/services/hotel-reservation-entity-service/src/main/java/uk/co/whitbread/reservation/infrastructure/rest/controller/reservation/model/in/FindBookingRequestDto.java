package uk.co.whitbread.reservation.infrastructure.rest.controller.reservation.model.in;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import uk.co.whitbread.reservation.infrastructure.rest.controller.reservation.validation.DateFormat;


@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class FindBookingRequestDto {

  @NotEmpty
  @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
  @Size(min = 4, max = 20, message = " length is incorrect, it must be between 4 and 10 characters.")
  private String resNo;

  @NotEmpty
  @DateFormat
  @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
  private String arrivalDate;

  @NotEmpty
  @Size(max = 30, message = " length is exceeded, maximum length = 30 characters")
  @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
  private String lastName;

  @Schema(defaultValue = "gb")
  @Builder.Default()
  private String country = "gb";

  @Schema(defaultValue = "en")
  @Builder.Default()
  private String language = "en";

  @Schema(defaultValue = "false")
  @Builder.Default()
  private Boolean isOldBooking = false;

}
