package uk.co.whitbread.reservation.infrastructure.rest.controller.amend.model.out;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class AvailablePaymentTypeDto {

  private boolean accountCompany;
  private boolean payOnArrival;
}
