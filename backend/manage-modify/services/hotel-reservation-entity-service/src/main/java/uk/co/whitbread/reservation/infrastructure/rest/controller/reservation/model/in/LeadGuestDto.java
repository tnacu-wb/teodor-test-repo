package uk.co.whitbread.reservation.infrastructure.rest.controller.reservation.model.in;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LeadGuestDto {

  @NotNull
  private String title;
  @NotNull
  private String firstName;
  @NotNull
  private String lastName;
  private String emailAddress;
  private String language;
  private GuestAddressDto address;

}
