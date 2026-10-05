package uk.co.whitbread.reservation.domain.model.in;

import java.time.LocalDate;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.format.annotation.DateTimeFormat;
import uk.co.whitbread.reservation.domain.model.validator.SelfValidation;

@Data
@Builder(toBuilder = true)
@EqualsAndHashCode(callSuper = false)
public class StayingGuestAdditionalDetails implements
    SelfValidation<StayingGuestAdditionalDetails> {

  @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
  private LocalDate dob;
  private String nationality;
  private String passportNumber;

  public StayingGuestAdditionalDetails(LocalDate dob, String nationality, String passportNumber) {
    this.dob = dob;
    this.nationality = nationality;
    this.passportNumber = passportNumber;
    this.validateSelf();
  }
}
