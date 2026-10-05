package uk.co.whitbread.rules.agent.domain.model.out;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;
import java.time.LocalDateTime;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Value;
import uk.co.whitbread.rules.agent.domain.model.validation.ModelValidator;

@Value
@Builder(toBuilder = true)
@EqualsAndHashCode(callSuper = false)
public class AmendmentRequestDetails extends ModelValidator<AmendmentRequestDetails> {

  @NotEmpty
  String rateType;
  @NotNull
  LocalDate arrivalDate;
  @NotNull
  LocalDateTime hotelLocalDateTime;
  @NotEmpty
  String hotelCountryCode;

  public AmendmentRequestDetails(String rateType, LocalDate arrivalDate,
      LocalDateTime hotelLocalDateTime, String hotelCountryCode) {
    this.rateType = rateType;
    this.arrivalDate = arrivalDate;
    this.hotelLocalDateTime = hotelLocalDateTime;
    this.hotelCountryCode = hotelCountryCode;
    this.validateSelf();
  }
}
