package uk.co.whitbread.reservation.infrastructure.rest.controller.amend.model.out;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class PreAuthChargesDto {

  private boolean display;
  private List<String> charges;
}
