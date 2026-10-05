package uk.co.whitbread.kiosk.infrastructure.rest.controller.kiosk.model.in;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ReservationCommentsDto {

  private String commentType;
  private String comment;

}
