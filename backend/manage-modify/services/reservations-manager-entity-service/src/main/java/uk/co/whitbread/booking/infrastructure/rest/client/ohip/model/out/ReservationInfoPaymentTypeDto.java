package uk.co.whitbread.booking.infrastructure.rest.client.ohip.model.out;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
@JsonInclude(Include.NON_NULL)
public class ReservationInfoPaymentTypeDto {
  private List<UniqueIdTypeDto> ids;
  private ReservationPaymentCardTypeDto paymentCardType;
}
