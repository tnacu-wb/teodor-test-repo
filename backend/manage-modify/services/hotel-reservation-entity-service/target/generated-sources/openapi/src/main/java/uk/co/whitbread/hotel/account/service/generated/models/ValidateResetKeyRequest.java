package uk.co.whitbread.hotel.account.service.generated.models;

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
 * ValidateResetKeyRequest
 */

@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2026-08-27T18:51:35.247020+03:00[Europe/Bucharest]", comments = "Generator version: 7.12.0")
public class ValidateResetKeyRequest {

  private String resetKey;

  public ValidateResetKeyRequest() {
    super();
  }

  /**
   * Constructor with only required parameters
   */
  public ValidateResetKeyRequest(String resetKey) {
    this.resetKey = resetKey;
  }

  public ValidateResetKeyRequest resetKey(String resetKey) {
    this.resetKey = resetKey;
    return this;
  }

  /**
   * Get resetKey
   * @return resetKey
   */
  @NotNull 
  @Schema(name = "resetKey", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("resetKey")
  public String getResetKey() {
    return resetKey;
  }

  public void setResetKey(String resetKey) {
    this.resetKey = resetKey;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    ValidateResetKeyRequest validateResetKeyRequest = (ValidateResetKeyRequest) o;
    return Objects.equals(this.resetKey, validateResetKeyRequest.resetKey);
  }

  @Override
  public int hashCode() {
    return Objects.hash(resetKey);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class ValidateResetKeyRequest {\n");
    sb.append("    resetKey: ").append(toIndentedString(resetKey)).append("\n");
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

