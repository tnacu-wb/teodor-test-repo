package uk.co.whitbread.dashboard.domain.model.out;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FrequentBooking {

  private String hotelImage;
  private String hotelName;
  private String hotelBrand;
  private String hotelCode;
}
