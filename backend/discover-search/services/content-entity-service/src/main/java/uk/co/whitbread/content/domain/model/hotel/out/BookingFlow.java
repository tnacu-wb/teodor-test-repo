package uk.co.whitbread.content.domain.model.hotel.out;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BookingFlow {

  private List<BookingFlowItem> bookingFlowItems;
  private List<TargetBookingFlowItem> targetBookingFlowItems;

}
