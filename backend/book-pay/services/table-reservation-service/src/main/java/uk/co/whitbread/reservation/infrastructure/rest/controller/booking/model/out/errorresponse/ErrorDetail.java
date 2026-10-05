package uk.co.whitbread.reservation.infrastructure.rest.controller.booking.model.out.errorresponse;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ErrorDetail {

  private String field;
  private String message;

}
