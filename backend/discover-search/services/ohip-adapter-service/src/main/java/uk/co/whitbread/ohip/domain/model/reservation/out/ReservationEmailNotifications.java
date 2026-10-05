package uk.co.whitbread.ohip.domain.model.reservation.out;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ReservationEmailNotifications {

  private boolean sendEmailConfirmation;
  private boolean sendEmailInvoice;
}
