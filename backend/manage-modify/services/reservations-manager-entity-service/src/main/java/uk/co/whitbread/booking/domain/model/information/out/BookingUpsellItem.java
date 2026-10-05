package uk.co.whitbread.booking.domain.model.information.out;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class BookingUpsellItem {

  private String roomId;
  private BookingPrice subtotal;
  private BookingPrice unitCost;
}
