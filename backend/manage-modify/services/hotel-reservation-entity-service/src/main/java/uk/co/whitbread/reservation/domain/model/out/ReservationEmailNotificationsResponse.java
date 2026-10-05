package uk.co.whitbread.reservation.domain.model.out;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ReservationEmailNotificationsResponse {

  private boolean sendEmailConfirmation;
  private boolean sendEmailInvoice;
}
