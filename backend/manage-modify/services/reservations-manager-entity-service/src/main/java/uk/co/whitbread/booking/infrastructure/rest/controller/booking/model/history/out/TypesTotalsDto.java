package uk.co.whitbread.booking.infrastructure.rest.controller.booking.model.history.out;


import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class TypesTotalsDto {

  private int cancelled;
  private int upcoming;
  private int past;
  private int checkedIn;
}
