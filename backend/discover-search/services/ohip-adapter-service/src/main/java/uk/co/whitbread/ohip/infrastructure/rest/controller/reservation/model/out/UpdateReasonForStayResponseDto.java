package uk.co.whitbread.ohip.infrastructure.rest.controller.reservation.model.out;

import java.util.List;
import lombok.Data;

@Data
public class UpdateReasonForStayResponseDto {

  private String hotelId;
  private List<String> reservationIds;

}
