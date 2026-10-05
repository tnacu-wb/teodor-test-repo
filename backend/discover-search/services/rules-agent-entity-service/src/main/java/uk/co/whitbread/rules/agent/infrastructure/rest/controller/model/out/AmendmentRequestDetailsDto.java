package uk.co.whitbread.rules.agent.infrastructure.rest.controller.model.out;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AmendmentRequestDetailsDto {

  private String rateType;
  private String arrivalDate;
  private String hotelLocalDateTime;
  private String hotelCountryCode;
}
