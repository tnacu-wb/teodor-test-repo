package uk.co.whitbread.reservation.domain.model.index.header.data.out;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Notifications {

  private String businessGroupBookingMessage;
  private String addAnotherRoom;
  private String groupBookingMessage;
  private String noResults;
  private String groupBookingHeader;
  private String ccuiGroupBookingMessage;
  private String availabilitiesErrorMessage;
  private String errorTitle;
}
