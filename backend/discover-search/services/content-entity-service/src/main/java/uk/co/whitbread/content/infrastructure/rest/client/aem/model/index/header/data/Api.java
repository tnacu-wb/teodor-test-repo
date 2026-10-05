package uk.co.whitbread.content.infrastructure.rest.client.aem.model.index.header.data;

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
