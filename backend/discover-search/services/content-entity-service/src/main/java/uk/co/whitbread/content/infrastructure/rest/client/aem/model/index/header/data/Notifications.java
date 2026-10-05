package uk.co.whitbread.content.infrastructure.rest.client.aem.model.index.header.data;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Notifications {
  private String emp01groupBookingMessage;
  private String businessGroupBookingMessage;
  private String addAnotherRoom;
  private String groupBookingMessage;
  private String noResults;
  private String groupBookingHeader;
  private String ccuiGroupBookingMessage;
  private String availabilitiesErrorMessage;
  private String errorTitle;
  private String groupBookingFormPageMessage;
}
