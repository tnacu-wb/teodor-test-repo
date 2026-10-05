package uk.co.whitbread.account.domain.model.in;

import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import uk.co.whitbread.account.domain.model.validation.Phone;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ContactDetail {
  private String title;
  private String firstName;
  private String lastName;
  @NotNull
  private String email;
  @Phone
  private String telephone;
  @Phone
  private String mobile;
  @Valid
  @NotNull
  private Address address;
  private String nationality;
  @Valid
  private Passport passport;
  private String carRegistration;
  private String dialingCode;

}