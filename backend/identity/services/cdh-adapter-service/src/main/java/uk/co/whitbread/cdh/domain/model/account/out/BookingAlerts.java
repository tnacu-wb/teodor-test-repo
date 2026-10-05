package uk.co.whitbread.cdh.domain.model.account.out;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BookingAlerts {
  @JsonProperty("BookingAlertHotels")
  private List<String> bookingAlertHotels;
  @JsonProperty("DayOfArrival")
  private boolean dayOfArrival;
  @JsonProperty("Frequency")
  private String frequency;
  @JsonProperty("PassThroughWeekend")
  private boolean passThroughWeekend;
  @JsonProperty("RateCaps")
  private RateCaps rateCaps;
  @JsonProperty("RecipientEmailAddresses")
  private List<String> recipientEmailAddresses;
  @JsonProperty("WeekendArrival")
  private boolean weekendArrival;
}
