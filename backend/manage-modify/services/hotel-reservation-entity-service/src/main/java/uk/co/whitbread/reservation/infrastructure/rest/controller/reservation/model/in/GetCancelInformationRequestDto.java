package uk.co.whitbread.reservation.infrastructure.rest.controller.reservation.model.in;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class GetCancelInformationRequestDto {

  @NotBlank
  @Schema(example = "LONSTM", required = true)
  private String hotelId;

  @Schema(required = true)
  private String basketReference;

  @Schema(required = true)
  private String userDateTime;

  private String token;
}
