package uk.co.whitbread.dashboard.infrastructure.rest.controller.dashboard.model.out;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FrequentBookingDto {

  private String hotelImage;
  private String hotelName;
  private String hotelBrand;
  private String hotelCode;
}
