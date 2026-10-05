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
 * ReservationOverrideReasonsDto
 */

@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2026-08-27T18:50:34.031950+03:00[Europe/Bucharest]", comments = "Generator version: 7.12.0")
public class ReservationOverrideReasonsDto {

  private @Nullable String callerName;

  private @Nullable String managerName;

  private @Nullable String reasonCode;

  private @Nullable String reasonName;

  public ReservationOverrideReasonsDto callerName(String callerName) {
    this.callerName = callerName;
    return this;
  }

  /**
   * Get callerName
   * @return callerName
   */
  
  @Schema(name = "callerName", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("callerName")
  public String getCallerName() {
    return callerName;
  }

  public void setCallerName(String callerName) {
    this.callerName = callerName;
  }

  public ReservationOverrideReasonsDto managerName(String managerName) {
    this.managerName = managerName;
    return this;
  }

  /**
   * Get managerName
   * @return managerName
   */
  
  @Schema(name = "managerName", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("managerName")
  public String getManagerName() {
    return managerName;
  }

  public void setManagerName(String managerName) {
    this.managerName = managerName;
  }

  public ReservationOverrideReasonsDto reasonCode(String reasonCode) {
    this.reasonCode = reasonCode;
    return this;
  }

  /**
   * Get reasonCode
   * @return reasonCode
   */
  
  @Schema(name = "reasonCode", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("reasonCode")
  public String getReasonCode() {
    return reasonCode;
  }

  public void setReasonCode(String reasonCode) {
    this.reasonCode = reasonCode;
  }

  public ReservationOverrideReasonsDto reasonName(String reasonName) {
    this.reasonName = reasonName;
    return this;
  }

  /**
   * Get reasonName
   * @return reasonName
   */
  
  @Schema(name = "reasonName", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("reasonName")
  public String getReasonName() {
    return reasonName;
  }

  public void setReasonName(String reasonName) {
    this.reasonName = reasonName;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    ReservationOverrideReasonsDto reservationOverrideReasonsDto = (ReservationOverrideReasonsDto) o;
    return Objects.equals(this.callerName, reservationOverrideReasonsDto.callerName) &&
        Objects.equals(this.managerName, reservationOverrideReasonsDto.managerName) &&
        Objects.equals(this.reasonCode, reservationOverrideReasonsDto.reasonCode) &&
        Objects.equals(this.reasonName, reservationOverrideReasonsDto.reasonName);
  }

  @Override
  public int hashCode() {
    return Objects.hash(callerName, managerName, reasonCode, reasonName);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class ReservationOverrideReasonsDto {\n");
    sb.append("    callerName: ").append(toIndentedString(callerName)).append("\n");
    sb.append("    managerName: ").append(toIndentedString(managerName)).append("\n");
    sb.append("    reasonCode: ").append(toIndentedString(reasonCode)).append("\n");
    sb.append("    reasonName: ").append(toIndentedString(reasonName)).append("\n");
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

