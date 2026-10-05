package uk.co.whitbread.ohip.domain.model.reservation.in;

import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import java.time.LocalDate;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import uk.co.whitbread.ohip.domain.model.validation.SelfValidation;

@Data
@Builder(toBuilder = true)
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
public class StayingGuestAdditionalDetails implements
    SelfValidation<StayingGuestAdditionalDetails> {

  @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
  private LocalDate dob;
  @Size(max = 20, message = "Nationality must be at most 20 characters long")
  private String nationality;
  @Pattern(regexp = "^[a-zA-Z0-9]+$", message = "Passport number must contain only alphanumeric characters")
  private String passportNumber;

  public StayingGuestAdditionalDetails(LocalDate dob, String nationality, String passportNumber) {
    this.dob = dob;
    this.nationality = nationality;
    this.passportNumber = passportNumber;
    this.validateSelf();
  }
}
