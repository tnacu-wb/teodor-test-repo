package uk.co.whitbread.dashboard.domain.model.in;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class FindBookingResponse {

  private String cookieName;
  private String sourcePms;
  private String ref;
  private String basketReference;
  private String redirectBase;
  private String token;
  private String minutesTillExpiry;
  private String operaConfNumber;
  private String hotelId;
}