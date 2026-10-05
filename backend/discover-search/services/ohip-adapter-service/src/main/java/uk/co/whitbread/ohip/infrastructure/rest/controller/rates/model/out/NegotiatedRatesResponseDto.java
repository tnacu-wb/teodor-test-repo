package uk.co.whitbread.ohip.infrastructure.rest.controller.rates.model.out;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@Builder
@NoArgsConstructor
@Data
public class NegotiatedRatesResponseDto {
  private List<NegotiatedRateDto> negotiatedRates;
}
