package uk.co.whitbread.reservation.domain.model.in;

import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.SuperBuilder;

@Data
@SuperBuilder(toBuilder = true)
@EqualsAndHashCode(callSuper = false)
public class LeadGuest {

  @NotNull
  private String firstName;
  @NotNull
  private String title;
  @NotNull
  private String lastName;
  private String emailAddress;
  private String language;
  private GuestAddress address;

}
