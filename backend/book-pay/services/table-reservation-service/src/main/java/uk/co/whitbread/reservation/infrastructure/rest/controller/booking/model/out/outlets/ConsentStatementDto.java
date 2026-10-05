package uk.co.whitbread.reservation.infrastructure.rest.controller.booking.model.out.outlets;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ConsentStatementDto {

  private String text;
  private boolean isEnabled;
  private String url;

}
