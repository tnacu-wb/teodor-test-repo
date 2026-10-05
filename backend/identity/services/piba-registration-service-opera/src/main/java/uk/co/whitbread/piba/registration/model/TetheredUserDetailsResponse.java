package uk.co.whitbread.piba.registration.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TetheredUserDetailsResponse {
  private TetheredUserDetailsOverview tetheredUserOverview;
  private TetheredUserAccountOverview customerAccountOverview;
}
