package uk.co.whitbread.booking.infrastructure.rest.client.reservation.model.out;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReservationGuestDto {

  private String givenName;
  private String surName;
  private String nameTitle;
  private String email;
}
