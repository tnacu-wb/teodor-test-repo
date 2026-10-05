package uk.co.whitbread.ohip.infrastructure.rest.controller.reservation.model.out;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@AllArgsConstructor
public class CancellationPoliciesResponseDto {

  private String time;
  private String text;
}