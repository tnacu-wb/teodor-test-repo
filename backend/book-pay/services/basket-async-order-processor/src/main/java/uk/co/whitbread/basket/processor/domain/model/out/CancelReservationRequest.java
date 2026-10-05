package uk.co.whitbread.basket.processor.domain.model.out;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@AllArgsConstructor
@Builder
public class CancelReservationRequest {

  private String hotelId;
  private String basketReference;
  private List<String> reservationIds;
  private PaymentOption paymentOption;

}
