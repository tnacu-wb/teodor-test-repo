package uk.co.whitbread.content.infrastructure.rest.client.booking.model.in;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AemRateInformationDto {
  private RatesConfigCommonDto ratesConfigCommon;
  private List<RateClassificationDto> rateClassifications;
}
