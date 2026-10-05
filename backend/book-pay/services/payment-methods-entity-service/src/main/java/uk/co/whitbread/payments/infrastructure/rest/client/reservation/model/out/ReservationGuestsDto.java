package uk.co.whitbread.payments.infrastructure.rest.client.reservation.model.out;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class ReservationGuestsDto {
  private String givenName;
  private String surName;
}
