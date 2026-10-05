package uk.co.whitbread.reservation.infrastructure.rest.controller.reservation.model.out;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class BookingAllowancesResponseDto {

  private List<BookingAllowanceDto> bookingAllowances;

}