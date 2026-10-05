package uk.co.whitbread.reservation.infrastructure.rest.controller.reservation.model.in;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class BookingCardInformationRequestDto {

  @NotEmpty
  @Schema(example = "AWMR378632", required = true)
  private String bookingReference;

  @NotEmpty
  @Schema(example = "2023-01-23", required = true)
  private String arrivalDate;

  @NotEmpty
  @Schema(example = "Doe", required = true)
  private String bookerLastName;

  @Schema(example = "en", defaultValue = "en")
  private String language;

  @Schema(example = "gb", defaultValue = "gb")
  private String country;

}
