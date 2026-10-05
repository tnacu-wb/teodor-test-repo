package uk.co.whitbread.ohip.domain.model.reservation.out;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ReservationByIdGuestsResponse {
  private String givenName;
  private String surName;
  private String nameTitle;
  private String email;
  private String type;
  private GuestAddressSingleCall address;
}