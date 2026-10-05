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
 * ReservationOverrideReasonDto
 */

@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2026-08-27T18:50:34.031950+03:00[Europe/Bucharest]", comments = "Generator version: 7.12.0")
public class ReservationOverrideReasonDto {

  private String callerName;

  private @Nullable String managerName;

  private String reasonCode;

  private String reasonName;

  public ReservationOverrideReasonDto() {
    super();
  }

  /**
   * Constructor with only required parameters
   */
  public ReservationOverrideReasonDto(String callerName, String reasonCode, String reasonName) {
    this.callerName = callerName;
    this.reasonCode = reasonCode;
    this.reasonName = reasonName;
  }

  public ReservationOverrideReasonDto callerName(String callerName) {
    this.callerName = callerName;
    return this;
  }

  /**
   * Get callerName
   * @return callerName
   */
  @NotNull 
  @Schema(name = "callerName", example = "Jane Doe", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("callerName")
  public String getCallerName() {
    return callerName;
  }

  public void setCallerName(String callerName) {
    this.callerName = callerName;
  }

  public ReservationOverrideReasonDto managerName(String managerName) {
    this.managerName = managerName;
    return this;
  }

  /**
   * Get managerName
   * @return managerName
   */
  
  @Schema(name = "managerName", example = "John Smith", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("managerName")
  public String getManagerName() {
    return managerName;
  }

  public void setManagerName(String managerName) {
    this.managerName = managerName;
  }

  public ReservationOverrideReasonDto reasonCode(String reasonCode) {
    this.reasonCode = reasonCode;
    return this;
  }

  /**
   * Get reasonCode
   * @return reasonCode
   */
  @NotNull 
  @Schema(name = "reasonCode", example = "ILL", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("reasonCode")
  public String getReasonCode() {
    return reasonCode;
  }

  public void setReasonCode(String reasonCode) {
    this.reasonCode = reasonCode;
  }

  public ReservationOverrideReasonDto reasonName(String reasonName) {
    this.reasonName = reasonName;
    return this;
  }

  /**
   * Get reasonName
   * @return reasonName
   */
  @NotNull 
  @Schema(name = "reasonName", example = "MA Illness", requiredMode = Schema.RequiredMode.REQUIRED)
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
    ReservationOverrideReasonDto reservationOverrideReasonDto = (ReservationOverrideReasonDto) o;
    return Objects.equals(this.callerName, reservationOverrideReasonDto.callerName) &&
        Objects.equals(this.managerName, reservationOverrideReasonDto.managerName) &&
        Objects.equals(this.reasonCode, reservationOverrideReasonDto.reasonCode) &&
        Objects.equals(this.reasonName, reservationOverrideReasonDto.reasonName);
  }

  @Override
  public int hashCode() {
    return Objects.hash(callerName, managerName, reasonCode, reasonName);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class ReservationOverrideReasonDto {\n");
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

