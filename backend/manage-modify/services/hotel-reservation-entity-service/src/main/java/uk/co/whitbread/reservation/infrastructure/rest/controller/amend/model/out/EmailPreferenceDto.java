package uk.co.whitbread.reservation.infrastructure.rest.controller.amend.model.out;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class EmailPreferenceDto {

  private boolean display;
  private boolean send;
  private String emailAddress;
}
