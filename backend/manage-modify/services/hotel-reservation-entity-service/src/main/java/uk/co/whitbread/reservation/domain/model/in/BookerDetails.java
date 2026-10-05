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
public class BookerDetails implements SelfValidation<BookerDetails> {

  private String title;
  @NotEmpty
  private String firstName;
  @NotEmpty
  private String lastName;
  private String emailAddress;
  private Boolean acceptFutureMailing;
  private String mobile;
  private String landline;
  private String language;
  @Valid
  private BookerAddress address;

}
