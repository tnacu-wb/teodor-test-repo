package uk.co.whitbread.cdh.domain.model.booking.out;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Totals {

  @JsonProperty("Upcoming")
  private Integer upcoming;

  @JsonProperty("CheckedIn")
  private Integer checkedIn;

  @JsonProperty("Past")
  private Integer past;

  @JsonProperty("Cancelled")
  private Integer cancelled;
}
