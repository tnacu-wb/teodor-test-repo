package uk.co.whitbread.reservation.infrastructure.rest.controller.amend.model.out;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class CardPresentDto {

  private boolean display;
  private boolean value;
}
