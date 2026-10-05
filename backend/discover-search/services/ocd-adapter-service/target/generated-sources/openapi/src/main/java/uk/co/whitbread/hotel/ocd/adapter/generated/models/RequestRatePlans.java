package uk.co.whitbread.hotel.ocd.adapter.generated.models;

import java.net.URI;
import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import org.springframework.lang.Nullable;
import org.openapitools.jackson.nullable.JsonNullable;
import java.time.OffsetDateTime;
import javax.validation.Valid;
import javax.validation.constraints.*;
import io.swagger.v3.oas.annotations.media.Schema;


import java.util.*;
import javax.annotation.Generated;

/**
 * Collection of rate plans
 */

@Schema(name = "RequestRatePlans", description = "Collection of rate plans")
@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2026-08-25T07:51:32.554166+03:00[Europe/Bucharest]", comments = "Generator version: 7.12.0")
public class RequestRatePlans {

  private @Nullable String ratePlanCode;

  private @Nullable String accessCode;

  private @Nullable String ratePlanType;

  public RequestRatePlans ratePlanCode(String ratePlanCode) {
    this.ratePlanCode = ratePlanCode;
    return this;
  }

  /**
   * Rate Plan code
   * @return ratePlanCode
   */
  @Size(min = 1, max = 50) 
  @Schema(name = "ratePlanCode", description = "Rate Plan code", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("ratePlanCode")
  public String getRatePlanCode() {
    return ratePlanCode;
  }

  public void setRatePlanCode(String ratePlanCode) {
    this.ratePlanCode = ratePlanCode;
  }

  public RequestRatePlans accessCode(String accessCode) {
    this.accessCode = accessCode;
    return this;
  }

  /**
   * Channel rate access code for negotiated rates
   * @return accessCode
   */
  @Size(min = 1, max = 50) 
  @Schema(name = "accessCode", description = "Channel rate access code for negotiated rates", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("accessCode")
  public String getAccessCode() {
    return accessCode;
  }

  public void setAccessCode(String accessCode) {
    this.accessCode = accessCode;
  }

  public RequestRatePlans ratePlanType(String ratePlanType) {
    this.ratePlanType = ratePlanType;
    return this;
  }

  /**
   * Rate Plan type
   * @return ratePlanType
   */
  
  @Schema(name = "ratePlanType", description = "Rate Plan type", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("ratePlanType")
  public String getRatePlanType() {
    return ratePlanType;
  }

  public void setRatePlanType(String ratePlanType) {
    this.ratePlanType = ratePlanType;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    RequestRatePlans requestRatePlans = (RequestRatePlans) o;
    return Objects.equals(this.ratePlanCode, requestRatePlans.ratePlanCode) &&
        Objects.equals(this.accessCode, requestRatePlans.accessCode) &&
        Objects.equals(this.ratePlanType, requestRatePlans.ratePlanType);
  }

  @Override
  public int hashCode() {
    return Objects.hash(ratePlanCode, accessCode, ratePlanType);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class RequestRatePlans {\n");
    sb.append("    ratePlanCode: ").append(toIndentedString(ratePlanCode)).append("\n");
    sb.append("    accessCode: ").append(toIndentedString(accessCode)).append("\n");
    sb.append("    ratePlanType: ").append(toIndentedString(ratePlanType)).append("\n");
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

