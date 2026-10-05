package uk.co.whitbread.booking.infrastructure.rest.controller.booking.model.history.out;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class BookingResponseDto {

  private List<BookingDto> bookings;
  private int pageIndex;
  private int pageSize;
  private int totalSize;
  private TypesTotalsDto totals;
  private String continuationToken;
}
