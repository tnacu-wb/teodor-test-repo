package uk.co.whitbread.domain.model.packages.in;

import jakarta.annotation.Nullable;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import lombok.Value;
import uk.co.whitbread.domain.model.validation.SelfValidation;

@Value
@Builder(toBuilder = true)
@ToString
@EqualsAndHashCode(callSuper = false)
@Data
public class PackagesRequest implements SelfValidation<PackagesRequest> {

  @NotEmpty
  String hotelId;
  @NotEmpty
  String startDate;
  @NotEmpty
  String endDate;
  @Positive
  Integer adultsNumber;
  @NotNull
  @PositiveOrZero
  Integer childrenNumber;
  @NotNull
  @Positive
  Integer nightsNumber;
  String ratePlanCode;
  String language;
  String country;

  @Nullable
  String channel;

  @Nullable
  Boolean isManageBookingPage;

  @Nullable
  Boolean mealInclusiveRate;

  @Nullable
  String packageSelections;

  String basketReference;

  Boolean isCiol;


  @SuppressWarnings("squid:S107")
  public PackagesRequest(String hotelId, String startDate, String endDate, Integer adultsNumber,
      Integer childrenNumber, Integer nightsNumber, String ratePlanCode, String language, String country,
                         String channel, Boolean isManageBookingPage, Boolean mealInclusiveRate,
                         String packageSelections, String basketReference, Boolean isCiol) {
    this.hotelId = hotelId;
    this.startDate = startDate;
    this.endDate = endDate;
    this.adultsNumber = adultsNumber;
    this.childrenNumber = childrenNumber;
    this.nightsNumber = nightsNumber;
    this.ratePlanCode = ratePlanCode;
    this.language = language;
    this.country = country;
    this.channel = channel;
    this.isManageBookingPage = isManageBookingPage;
    this.mealInclusiveRate = mealInclusiveRate;
    this.packageSelections = packageSelections;
    this.basketReference = basketReference;
    this.isCiol = isCiol;
    this.validateSelf();
  }
}
