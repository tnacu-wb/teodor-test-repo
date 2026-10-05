package uk.co.whitbread.digitalkey.infrastructure.rest.controller.digitalkey.model.in;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CheckInRequestDto {

  @NotBlank(message = "cannot be null or empty")
  private String reservationId;

  @NotBlank(message = "cannot be null or empty")
  private String hotelId;

}