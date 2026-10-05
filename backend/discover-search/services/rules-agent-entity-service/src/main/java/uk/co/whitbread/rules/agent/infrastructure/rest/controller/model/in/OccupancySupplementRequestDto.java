package uk.co.whitbread.rules.agent.infrastructure.rest.controller.model.in;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OccupancySupplementRequestDto {

  @NotBlank
  private String hotelId;
}
