package uk.co.whitbread.kiosk.domain.model.checkin.in;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PassportDetails {

  private String passportNumber;
  private String placeOfIssue;
  private String nextDestination;

}
