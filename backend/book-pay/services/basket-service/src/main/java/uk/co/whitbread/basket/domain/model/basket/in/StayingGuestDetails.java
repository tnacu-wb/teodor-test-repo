package uk.co.whitbread.basket.domain.model.basket.in;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.SuperBuilder;

@Data
@SuperBuilder(toBuilder = true)
@EqualsAndHashCode(callSuper = false)
public class StayingGuestDetails {

  private String title;
  @NotEmpty
  private String firstName;
  @NotEmpty
  private String lastName;
  private String employeeAccountId;
  private String emailAddress;
  @Valid
  private StayingGuestAddress address;
}
