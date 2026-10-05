package uk.co.whitbread.ohip.domain.model.reservation.in;

import jakarta.validation.constraints.NotEmpty;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import uk.co.whitbread.ohip.domain.model.validation.SelfValidation;

@Data
@Builder(toBuilder = true)
@EqualsAndHashCode(callSuper = false)
public class StayingGuestDetails implements SelfValidation<StayingGuestDetails> {

  private String title;
  @NotEmpty
  private String firstName;
  @NotEmpty
  private String lastName;
  private String employeeAccountId;
  private String emailAddress;
  private StayingGuestAddress address;
  private StayingGuestAdditionalDetails additionalDetails;
  private String profileId;
}