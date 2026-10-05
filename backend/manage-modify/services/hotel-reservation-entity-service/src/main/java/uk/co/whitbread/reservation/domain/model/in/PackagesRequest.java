package uk.co.whitbread.reservation.domain.model.in;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import lombok.Value;
import uk.co.whitbread.reservation.domain.model.validator.SelfValidation;

@Value
@Builder(toBuilder = true)
@ToString
@EqualsAndHashCode(callSuper = false)
public class PackagesRequest implements SelfValidation<PackagesRequest> {

  @NotEmpty
  private String hotelId;
  @NotEmpty
  private String startDate;
  @NotEmpty
  private String endDate;
  @Positive
  private Integer adultsNumber;
  @NotNull
  @PositiveOrZero
  private Integer childrenNumber;
  @NotNull
  @Positive
  private Integer nightsNumber;

  public PackagesRequest(String hotelId, String startDate, String endDate, Integer adultsNumber,
      Integer childrenNumber, Integer nightsNumber) {
    this.hotelId = hotelId;
    this.startDate = startDate;
    this.endDate = endDate;
    this.adultsNumber = adultsNumber;
    this.childrenNumber = childrenNumber;
    this.nightsNumber = nightsNumber;
    this.validateSelf();

  }
}
