package uk.co.whitbread.ohip.domain.model.reservation.in;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AccompanyingGuestDetails {

  private String title;
  private String firstName;
  private String lastName;
  private String emailAddress;
  private String employeeAccountId;
  private String profileId;
  private StayingGuestAdditionalDetails additionalDetails;

}
