package uk.co.whitbread.reservation.domain.model.in;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.SuperBuilder;
import uk.co.whitbread.reservation.domain.model.validator.SelfValidation;

@Data
@SuperBuilder(toBuilder = true)
@EqualsAndHashCode(callSuper = false)
public class StayingGuestDetails implements SelfValidation<StayingGuestDetails> {

  private String title;
  @NotEmpty
  private String firstName;
  @NotEmpty
  private String lastName;
  private String employeeAccountId;
  private String emailAddress;
  @Valid
  private StayingGuestAddress address;
  private StayingGuestAdditionalDetails additionalDetails;
  private String profileId;
}
