package uk.co.whitbread.digitalkey.infrastructure.rest.controller.digitalkey.model.out;

import java.math.BigDecimal;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ReservationByBasketRefResponseDto {

  private List<ReservationByIdDto> reservationByIdList;
  private String hotelId;
  private BigDecimal balanceOutstanding;
}
