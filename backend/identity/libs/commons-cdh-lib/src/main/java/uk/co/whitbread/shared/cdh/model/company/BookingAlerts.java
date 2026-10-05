package uk.co.whitbread.shared.cdh.model.company;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Builder
@Getter
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
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
