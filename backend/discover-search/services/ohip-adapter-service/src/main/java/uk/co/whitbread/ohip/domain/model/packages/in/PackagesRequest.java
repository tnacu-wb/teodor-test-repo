package uk.co.whitbread.ohip.domain.model.packages.in;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Value;
import uk.co.whitbread.ohip.domain.model.validation.SelfValidation;


@Value
@Builder(toBuilder = true)
@EqualsAndHashCode(callSuper = false)
public class PackagesRequest implements SelfValidation<PackagesRequest> {

  @NotEmpty
  private String hotelId;
  @NotEmpty
  private String startDate;
  @NotEmpty
  private String endDate;
  @Positive
  private Integer adults;
  @NotNull
  @PositiveOrZero
  private Integer children;
  @NotNull
  @Positive
  private Integer nrNights;
  private String ratePlanCode;
  private Boolean mealInclusiveRate;

  public PackagesRequest(String hotelId, String startDate, String endDate, Integer adults,
      Integer children, Integer nrNights, String ratePlanCode, Boolean mealInclusiveRate) {
    this.hotelId = hotelId;
    this.startDate = startDate;
    this.endDate = endDate;
    this.adults = adults;
    this.children = children;
    this.nrNights = nrNights;
    this.ratePlanCode = ratePlanCode;
    this.mealInclusiveRate = mealInclusiveRate;
    this.validateSelf();
  }
}

