package uk.co.whitbread.cdh.infrastructure.rest.controller.account.model.out;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BookingAlertsDto {
  private List<String> bookingAlertHotels;
  private boolean dayOfArrival;
  private String frequency;
  private boolean passThroughWeekend;
  private RateCapsDto rateCaps;
  private List<String> recipientEmailAddresses;
  private boolean weekendArrival;
}
