package uk.co.whitbread.hotel.ocd.adapter.generated.models;

import java.net.URI;
import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import java.time.OffsetDateTime;
import java.util.Date;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.lang.Nullable;
import uk.co.whitbread.hotel.ocd.adapter.generated.models.DepositOffsetDropTime;
import uk.co.whitbread.hotel.ocd.adapter.generated.models.OffsetTimeUnit;
import org.openapitools.jackson.nullable.JsonNullable;
import java.time.OffsetDateTime;
import javax.validation.Valid;
import javax.validation.constraints.*;
import io.swagger.v3.oas.annotations.media.Schema;


import java.util.*;
import javax.annotation.Generated;

/**
 * The deadline associated to the deposit rule.
 */

@Schema(name = "OfferDepositPolicyDeadlineType", description = "The deadline associated to the deposit rule.")
@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2026-08-25T07:51:32.554166+03:00[Europe/Bucharest]", comments = "Generator version: 7.12.0")
public class OfferDepositPolicyDeadlineType {

  private @Nullable Integer offsetUnitMultiplier;

  private @Nullable OffsetTimeUnit offsetTimeUnit;

  private @Nullable DepositOffsetDropTime offsetDropTime;

  @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
  private @Nullable Date absoluteDeadline;

  public OfferDepositPolicyDeadlineType offsetUnitMultiplier(Integer offsetUnitMultiplier) {
    this.offsetUnitMultiplier = offsetUnitMultiplier;
    return this;
  }

  /**
   * The number of days before the arrival date when the deposit is due.
   * @return offsetUnitMultiplier
   */
  
  @Schema(name = "offsetUnitMultiplier", example = "5", description = "The number of days before the arrival date when the deposit is due.", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("offsetUnitMultiplier")
  public Integer getOffsetUnitMultiplier() {
    return offsetUnitMultiplier;
  }

  public void setOffsetUnitMultiplier(Integer offsetUnitMultiplier) {
    this.offsetUnitMultiplier = offsetUnitMultiplier;
  }

  public OfferDepositPolicyDeadlineType offsetTimeUnit(OffsetTimeUnit offsetTimeUnit) {
    this.offsetTimeUnit = offsetTimeUnit;
    return this;
  }

  /**
   * Get offsetTimeUnit
   * @return offsetTimeUnit
   */
  @Valid 
  @Schema(name = "offsetTimeUnit", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("offsetTimeUnit")
  public OffsetTimeUnit getOffsetTimeUnit() {
    return offsetTimeUnit;
  }

  public void setOffsetTimeUnit(OffsetTimeUnit offsetTimeUnit) {
    this.offsetTimeUnit = offsetTimeUnit;
  }

  public OfferDepositPolicyDeadlineType offsetDropTime(DepositOffsetDropTime offsetDropTime) {
    this.offsetDropTime = offsetDropTime;
    return this;
  }

  /**
   * Get offsetDropTime
   * @return offsetDropTime
   */
  @Valid 
  @Schema(name = "offsetDropTime", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("offsetDropTime")
  public DepositOffsetDropTime getOffsetDropTime() {
    return offsetDropTime;
  }

  public void setOffsetDropTime(DepositOffsetDropTime offsetDropTime) {
    this.offsetDropTime = offsetDropTime;
  }

  public OfferDepositPolicyDeadlineType absoluteDeadline(Date absoluteDeadline) {
    this.absoluteDeadline = absoluteDeadline;
    return this;
  }

  /**
   * The date and time the deposit rule will take effect.
   * @return absoluteDeadline
   */
  @Valid 
  @Schema(name = "absoluteDeadline", example = "2021-04-01T15:46:40.134Z", description = "The date and time the deposit rule will take effect.", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("absoluteDeadline")
  public Date getAbsoluteDeadline() {
    return absoluteDeadline;
  }

  public void setAbsoluteDeadline(Date absoluteDeadline) {
    this.absoluteDeadline = absoluteDeadline;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    OfferDepositPolicyDeadlineType offerDepositPolicyDeadlineType = (OfferDepositPolicyDeadlineType) o;
    return Objects.equals(this.offsetUnitMultiplier, offerDepositPolicyDeadlineType.offsetUnitMultiplier) &&
        Objects.equals(this.offsetTimeUnit, offerDepositPolicyDeadlineType.offsetTimeUnit) &&
        Objects.equals(this.offsetDropTime, offerDepositPolicyDeadlineType.offsetDropTime) &&
        Objects.equals(this.absoluteDeadline, offerDepositPolicyDeadlineType.absoluteDeadline);
  }

  @Override
  public int hashCode() {
    return Objects.hash(offsetUnitMultiplier, offsetTimeUnit, offsetDropTime, absoluteDeadline);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class OfferDepositPolicyDeadlineType {\n");
    sb.append("    offsetUnitMultiplier: ").append(toIndentedString(offsetUnitMultiplier)).append("\n");
    sb.append("    offsetTimeUnit: ").append(toIndentedString(offsetTimeUnit)).append("\n");
    sb.append("    offsetDropTime: ").append(toIndentedString(offsetDropTime)).append("\n");
    sb.append("    absoluteDeadline: ").append(toIndentedString(absoluteDeadline)).append("\n");
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

