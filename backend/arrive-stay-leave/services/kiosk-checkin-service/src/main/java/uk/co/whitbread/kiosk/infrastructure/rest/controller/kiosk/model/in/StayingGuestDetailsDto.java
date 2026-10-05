package uk.co.whitbread.kiosk.infrastructure.rest.controller.kiosk.model.in;

import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StayingGuestDetailsDto {

  private String title;
  @NotEmpty
  private String firstName;
  @NotEmpty
  private String lastName;
  private GuestAddressDto address;
  private String nationality;
  private String passport;
  private String placeOfIssue;
  private String nextDestination;
}
