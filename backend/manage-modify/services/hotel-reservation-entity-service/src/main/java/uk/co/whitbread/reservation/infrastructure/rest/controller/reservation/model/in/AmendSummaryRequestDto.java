package uk.co.whitbread.reservation.infrastructure.rest.controller.reservation.model.in;


import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AmendSummaryRequestDto {
  @NotNull
  private String originalBasketRef;
  @NotNull
  private String tempBasketRef;
}
