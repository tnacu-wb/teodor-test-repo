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
import uk.co.whitbread.hotel.ocd.adapter.generated.models.OfferAgeQualifyingCodeEnum;
import org.openapitools.jackson.nullable.JsonNullable;
import java.time.OffsetDateTime;
import javax.validation.Valid;
import javax.validation.constraints.*;
import io.swagger.v3.oas.annotations.media.Schema;


import java.util.*;
import javax.annotation.Generated;

/**
 * Guest count information to return in the search response
 */

@Schema(name = "OfferGuestCountType", description = "Guest count information to return in the search response")
@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2026-08-25T07:51:32.554166+03:00[Europe/Bucharest]", comments = "Generator version: 7.12.0")
public class OfferGuestCountType {

  private @Nullable OfferAgeQualifyingCodeEnum ageQualifyingCode;

  private @Nullable Integer count;

  @Valid
  private List<@Min(0) @Max(18)Integer> ages = new ArrayList<>();

  public OfferGuestCountType ageQualifyingCode(OfferAgeQualifyingCodeEnum ageQualifyingCode) {
    this.ageQualifyingCode = ageQualifyingCode;
    return this;
  }

  /**
   * Get ageQualifyingCode
   * @return ageQualifyingCode
   */
  @Valid 
  @Schema(name = "ageQualifyingCode", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("ageQualifyingCode")
  public OfferAgeQualifyingCodeEnum getAgeQualifyingCode() {
    return ageQualifyingCode;
  }

  public void setAgeQualifyingCode(OfferAgeQualifyingCodeEnum ageQualifyingCode) {
    this.ageQualifyingCode = ageQualifyingCode;
  }

  public OfferGuestCountType count(Integer count) {
    this.count = count;
    return this;
  }

  /**
   * Get count
   * minimum: 0
   * maximum: 100
   * @return count
   */
  @Min(0) @Max(100) 
  @Schema(name = "count", example = "1", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("count")
  public Integer getCount() {
    return count;
  }

  public void setCount(Integer count) {
    this.count = count;
  }

  public OfferGuestCountType ages(List<@Min(0) @Max(18)Integer> ages) {
    this.ages = ages;
    return this;
  }

  public OfferGuestCountType addAgesItem(Integer agesItem) {
    if (this.ages == null) {
      this.ages = new ArrayList<>();
    }
    this.ages.add(agesItem);
    return this;
  }

  /**
   * Get ages
   * @return ages
   */
  
  @Schema(name = "ages", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("ages")
  public List<@Min(0) @Max(18)Integer> getAges() {
    return ages;
  }

  public void setAges(List<@Min(0) @Max(18)Integer> ages) {
    this.ages = ages;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    OfferGuestCountType offerGuestCountType = (OfferGuestCountType) o;
    return Objects.equals(this.ageQualifyingCode, offerGuestCountType.ageQualifyingCode) &&
        Objects.equals(this.count, offerGuestCountType.count) &&
        Objects.equals(this.ages, offerGuestCountType.ages);
  }

  @Override
  public int hashCode() {
    return Objects.hash(ageQualifyingCode, count, ages);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class OfferGuestCountType {\n");
    sb.append("    ageQualifyingCode: ").append(toIndentedString(ageQualifyingCode)).append("\n");
    sb.append("    count: ").append(toIndentedString(count)).append("\n");
    sb.append("    ages: ").append(toIndentedString(ages)).append("\n");
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

