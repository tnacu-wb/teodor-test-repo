package uk.co.whitbread.basket.domain.model.basket.in;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.SuperBuilder;

@Data
@SuperBuilder(toBuilder = true)
@EqualsAndHashCode(callSuper = false)
public class BookerDetails {
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
