package uk.co.whitbread.reservation.domain.model.out;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@AllArgsConstructor
public class ConfirmationCustomer {

  private String givenName;
  private String surName;
}
