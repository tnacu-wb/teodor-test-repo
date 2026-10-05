package uk.co.whitbread.booking.infrastructure.rest.client.booking.model.information.out;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class StayBusinessAccountDto {

  private StayPriceDto dinnerAllowance;
}
