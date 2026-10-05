package uk.co.whitbread.ohip.domain.model.reservation.out;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.UniqueIDType;


@Data
@Builder
@AllArgsConstructor
public class ReservationsPaymentCardType {
  private List<UniqueIDType> ids;
  private ReservationPaymentCardType paymentCardType;
}
