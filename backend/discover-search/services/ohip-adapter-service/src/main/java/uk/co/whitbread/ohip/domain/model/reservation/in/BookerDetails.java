package uk.co.whitbread.ohip.domain.model.reservation.in;

import jakarta.validation.constraints.NotEmpty;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import uk.co.whitbread.ohip.domain.model.validation.SelfValidation;

@Data
@Builder(toBuilder = true)
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
  private BookerAddress address;

  public BookerDetails(String title, String firstName, String lastName, String emailAddress,
      Boolean acceptFutureMailing, String mobile, String landline, String language,
      BookerAddress address) {
    this.title = title;
    this.firstName = firstName;
    this.lastName = lastName;
    this.emailAddress = emailAddress;
    this.acceptFutureMailing = acceptFutureMailing;
    this.mobile = mobile;
    this.landline = landline;
    this.language = language;
    this.address = address;
    this.validateSelf();
  }
}
