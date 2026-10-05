package uk.co.whitbread.hotel.ohip.adapter.generated.models;

import java.net.URI;
import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import org.springframework.lang.Nullable;
import org.openapitools.jackson.nullable.JsonNullable;
import java.time.OffsetDateTime;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import io.swagger.v3.oas.annotations.media.Schema;


import java.util.*;
import jakarta.annotation.Generated;

/**
 * AdditionalGuestInfo
 */

@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2026-08-27T18:50:34.031950+03:00[Europe/Bucharest]", comments = "Generator version: 7.12.0")
public class AdditionalGuestInfo {

  private @Nullable String purposeOfStay;

  public AdditionalGuestInfo purposeOfStay(String purposeOfStay) {
    this.purposeOfStay = purposeOfStay;
    return this;
  }

  /**
   * Get purposeOfStay
   * @return purposeOfStay
   */
  
  @Schema(name = "purposeOfStay", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("purposeOfStay")
  public String getPurposeOfStay() {
    return purposeOfStay;
  }

  public void setPurposeOfStay(String purposeOfStay) {
    this.purposeOfStay = purposeOfStay;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    AdditionalGuestInfo additionalGuestInfo = (AdditionalGuestInfo) o;
    return Objects.equals(this.purposeOfStay, additionalGuestInfo.purposeOfStay);
  }

  @Override
  public int hashCode() {
    return Objects.hash(purposeOfStay);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class AdditionalGuestInfo {\n");
    sb.append("    purposeOfStay: ").append(toIndentedString(purposeOfStay)).append("\n");
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

