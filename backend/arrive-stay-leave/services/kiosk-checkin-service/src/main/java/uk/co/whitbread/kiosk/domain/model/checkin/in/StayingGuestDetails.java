package uk.co.whitbread.kiosk.domain.model.checkin.in;

import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StayingGuestDetails {

  private String title;
  @NotEmpty
  private String firstName;
  @NotEmpty
  private String lastName;
  private GuestAddress address;
  private String nationality;
  private String passport;
  private String placeOfIssue;
  private String nextDestination;
}
