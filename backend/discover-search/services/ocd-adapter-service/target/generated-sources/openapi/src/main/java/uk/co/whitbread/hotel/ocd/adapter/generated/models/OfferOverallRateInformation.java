package uk.co.whitbread.hotel.ocd.adapter.generated.models;

import java.net.URI;
import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.springframework.lang.Nullable;
import uk.co.whitbread.hotel.ocd.adapter.generated.models.OfferAdditionalGuestAmountType;
import uk.co.whitbread.hotel.ocd.adapter.generated.models.OfferRateMode;
import uk.co.whitbread.hotel.ocd.adapter.generated.models.OfferRateTimeUnit;
import uk.co.whitbread.hotel.ocd.adapter.generated.models.OfferTotalType;
import org.openapitools.jackson.nullable.JsonNullable;
import java.time.OffsetDateTime;
import javax.validation.Valid;
import javax.validation.constraints.*;
import io.swagger.v3.oas.annotations.media.Schema;


import java.util.*;
import javax.annotation.Generated;

/**
 * Rate plan information of the offer.
 */

@Schema(name = "OfferOverallRateInformation", description = "Rate plan information of the offer.")
@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2026-08-25T07:51:32.554166+03:00[Europe/Bucharest]", comments = "Generator version: 7.12.0")
public class OfferOverallRateInformation {

  private @Nullable OfferTotalType rateModeAmount;

  @Valid
  private List<OfferAdditionalGuestAmountType> additionalGuestAmounts = new ArrayList<>();

  private @Nullable OfferRateMode rateMode;

  private @Nullable OfferRateTimeUnit rateTimeUnit;

  public OfferOverallRateInformation rateModeAmount(OfferTotalType rateModeAmount) {
    this.rateModeAmount = rateModeAmount;
    return this;
  }

  /**
   * Get rateModeAmount
   * @return rateModeAmount
   */
  @Valid 
  @Schema(name = "rateModeAmount", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("rateModeAmount")
  public OfferTotalType getRateModeAmount() {
    return rateModeAmount;
  }

  public void setRateModeAmount(OfferTotalType rateModeAmount) {
    this.rateModeAmount = rateModeAmount;
  }

  public OfferOverallRateInformation additionalGuestAmounts(List<OfferAdditionalGuestAmountType> additionalGuestAmounts) {
    this.additionalGuestAmounts = additionalGuestAmounts;
    return this;
  }

  public OfferOverallRateInformation addAdditionalGuestAmountsItem(OfferAdditionalGuestAmountType additionalGuestAmountsItem) {
    if (this.additionalGuestAmounts == null) {
      this.additionalGuestAmounts = new ArrayList<>();
    }
    this.additionalGuestAmounts.add(additionalGuestAmountsItem);
    return this;
  }

  /**
   * List of charges for each additional adult and/or child.
   * @return additionalGuestAmounts
   */
  @Valid 
  @Schema(name = "additionalGuestAmounts", description = "List of charges for each additional adult and/or child.", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("additionalGuestAmounts")
  public List<OfferAdditionalGuestAmountType> getAdditionalGuestAmounts() {
    return additionalGuestAmounts;
  }

  public void setAdditionalGuestAmounts(List<OfferAdditionalGuestAmountType> additionalGuestAmounts) {
    this.additionalGuestAmounts = additionalGuestAmounts;
  }

  public OfferOverallRateInformation rateMode(OfferRateMode rateMode) {
    this.rateMode = rateMode;
    return this;
  }

  /**
   * Get rateMode
   * @return rateMode
   */
  @Valid 
  @Schema(name = "rateMode", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("rateMode")
  public OfferRateMode getRateMode() {
    return rateMode;
  }

  public void setRateMode(OfferRateMode rateMode) {
    this.rateMode = rateMode;
  }

  public OfferOverallRateInformation rateTimeUnit(OfferRateTimeUnit rateTimeUnit) {
    this.rateTimeUnit = rateTimeUnit;
    return this;
  }

  /**
   * Get rateTimeUnit
   * @return rateTimeUnit
   */
  @Valid 
  @Schema(name = "rateTimeUnit", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("rateTimeUnit")
  public OfferRateTimeUnit getRateTimeUnit() {
    return rateTimeUnit;
  }

  public void setRateTimeUnit(OfferRateTimeUnit rateTimeUnit) {
    this.rateTimeUnit = rateTimeUnit;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    OfferOverallRateInformation offerOverallRateInformation = (OfferOverallRateInformation) o;
    return Objects.equals(this.rateModeAmount, offerOverallRateInformation.rateModeAmount) &&
        Objects.equals(this.additionalGuestAmounts, offerOverallRateInformation.additionalGuestAmounts) &&
        Objects.equals(this.rateMode, offerOverallRateInformation.rateMode) &&
        Objects.equals(this.rateTimeUnit, offerOverallRateInformation.rateTimeUnit);
  }

  @Override
  public int hashCode() {
    return Objects.hash(rateModeAmount, additionalGuestAmounts, rateMode, rateTimeUnit);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class OfferOverallRateInformation {\n");
    sb.append("    rateModeAmount: ").append(toIndentedString(rateModeAmount)).append("\n");
    sb.append("    additionalGuestAmounts: ").append(toIndentedString(additionalGuestAmounts)).append("\n");
    sb.append("    rateMode: ").append(toIndentedString(rateMode)).append("\n");
    sb.append("    rateTimeUnit: ").append(toIndentedString(rateTimeUnit)).append("\n");
    sb.append("}");
    return sb.toString();
  }

  /**
   * Convert the given object to string with each line indented by 4 spaces
   * (except the first line).
   */
  private String toIndentedString(Object o) {
    if (o == null) {
      return "null";
    }
    return o.toString().replace("\n", "\n    ");
  }
}

