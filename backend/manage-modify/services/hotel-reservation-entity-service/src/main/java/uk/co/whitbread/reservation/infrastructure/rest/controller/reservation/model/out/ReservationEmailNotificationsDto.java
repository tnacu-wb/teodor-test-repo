package uk.co.whitbread.reservation.infrastructure.rest.controller.reservation.model.out;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@AllArgsConstructor
public class ReservationEmailNotificationsDto {

  private boolean sendEmailConfirmation;
  private boolean sendEmailInvoice;
}
