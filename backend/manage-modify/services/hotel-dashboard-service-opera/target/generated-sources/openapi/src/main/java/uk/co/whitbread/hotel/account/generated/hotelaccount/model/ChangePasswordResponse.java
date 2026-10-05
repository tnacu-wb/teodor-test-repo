package uk.co.whitbread.hotel.account.generated.hotelaccount.model;

import java.net.URI;
import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import org.jspecify.annotations.Nullable;
import org.openapitools.jackson.nullable.JsonNullable;
import java.time.OffsetDateTime;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import io.swagger.v3.oas.annotations.media.Schema;


import java.util.*;
import jakarta.annotation.Generated;

/**
 * ChangePasswordResponse
 */

@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2026-08-25T07:50:33.863804+03:00[Europe/Bucharest]", comments = "Generator version: 7.12.0")
public class ChangePasswordResponse {

  private Boolean passwordChanged;

  public ChangePasswordResponse() {
    super();
  }

  /**
   * Constructor with only required parameters
   */
  public ChangePasswordResponse(Boolean passwordChanged) {
    this.passwordChanged = passwordChanged;
  }

  public ChangePasswordResponse passwordChanged(Boolean passwordChanged) {
    this.passwordChanged = passwordChanged;
    return this;
  }

  /**
   * Get passwordChanged
   * @return passwordChanged
   */
  @NotNull 
  @Schema(name = "passwordChanged", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("passwordChanged")
  public Boolean getPasswordChanged() {
    return passwordChanged;
  }

  public void setPasswordChanged(Boolean passwordChanged) {
    this.passwordChanged = passwordChanged;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    ChangePasswordResponse changePasswordResponse = (ChangePasswordResponse) o;
    return Objects.equals(this.passwordChanged, changePasswordResponse.passwordChanged);
  }

  @Override
  public int hashCode() {
    return Objects.hash(passwordChanged);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class ChangePasswordResponse {\n");
    sb.append("    passwordChanged: ").append(toIndentedString(passwordChanged)).append("\n");
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

