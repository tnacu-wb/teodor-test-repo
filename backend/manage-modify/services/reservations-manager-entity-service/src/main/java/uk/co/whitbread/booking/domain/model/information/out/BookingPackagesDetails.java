package uk.co.whitbread.booking.domain.model.information.out;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class BookingPackagesDetails {

  private BookingPrice totalPrice;
  private String packageCode;
  private String description;
  private Integer noSelections;
}
