package uk.co.whitbread.reservation.domain.model.payment.in;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RoomType {

  private String type;
  private String rate;
  private Integer adultsNumber;
}
