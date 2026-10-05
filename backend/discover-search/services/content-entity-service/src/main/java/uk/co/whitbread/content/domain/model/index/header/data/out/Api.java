package uk.co.whitbread.content.domain.model.index.header.data.out;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Api {

  private Integer initialPageSize;
  private Integer lazyLoadPageSize;
  private Boolean connectivity;
  private Availabilities availabilities;
  private BeaconDodsworth beaconDodsworth;
  private Snowdrop snowdrop;
  private Availability availability;
  private Reservations reservations;
  private BookingChannel bookingChannel;
  private Tethering tethering;
}
