package uk.co.whitbread.basket.domain.model.email.in;

import java.util.List;
import lombok.Builder;
import lombok.Data;
import uk.co.whitbread.basket.domain.model.reservation.out.Deposits;

@Data
@Builder
public class EmailRequest {

  private String bookingReference;
  private String email;
  private String emailRequestType;
  private List<Deposits> deposits;
  private boolean failedRefund;
}