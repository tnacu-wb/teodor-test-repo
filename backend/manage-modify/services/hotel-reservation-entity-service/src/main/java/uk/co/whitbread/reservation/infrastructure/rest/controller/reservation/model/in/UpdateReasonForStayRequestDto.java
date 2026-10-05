package uk.co.whitbread.reservation.infrastructure.rest.controller.reservation.model.in;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UpdateReasonForStayRequestDto {

  @NotEmpty
  @Schema(required = true)
  private String basketReference;
  @NotEmpty
  @Schema(required = true)
  private String hotelId;
  @NotEmpty
  @Schema(example = "LEI", required = true)
  private String reasonForStay;
  private String arrivalDate;
  private String country;
  private String language;
}
