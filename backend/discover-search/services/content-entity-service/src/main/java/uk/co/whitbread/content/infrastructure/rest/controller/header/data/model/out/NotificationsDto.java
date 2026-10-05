package uk.co.whitbread.content.infrastructure.rest.controller.header.data.model.out;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NotificationsDto {
  private String emp01groupBookingMessage;
  private String groupBookingHeader;
  private String groupBookingMessage;
  private String ccuiGroupBookingMessage;
  private String noResults;
  private String availabilitiesErrorMessage;
  private String errorTitle;
  private String groupBookingFormPageMessage;
}
