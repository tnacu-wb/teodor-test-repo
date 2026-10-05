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
 * NotificationsResponse
 */

@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2026-08-27T18:51:35.247020+03:00[Europe/Bucharest]", comments = "Generator version: 7.12.0")
public class NotificationsResponse {

  private @Nullable Boolean profileUpdateRequired;

  private @Nullable String status;

  private @Nullable Boolean usersAwaitingApproval;

  public NotificationsResponse profileUpdateRequired(Boolean profileUpdateRequired) {
    this.profileUpdateRequired = profileUpdateRequired;
    return this;
  }

  /**
   * Get profileUpdateRequired
   * @return profileUpdateRequired
   */
  
  @Schema(name = "profileUpdateRequired", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("profileUpdateRequired")
  public Boolean getProfileUpdateRequired() {
    return profileUpdateRequired;
  }

  public void setProfileUpdateRequired(Boolean profileUpdateRequired) {
    this.profileUpdateRequired = profileUpdateRequired;
  }

  public NotificationsResponse status(String status) {
    this.status = status;
    return this;
  }

  /**
   * Get status
   * @return status
   */
  
  @Schema(name = "status", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("status")
  public String getStatus() {
    return status;
  }

  public void setStatus(String status) {
    this.status = status;
  }

  public NotificationsResponse usersAwaitingApproval(Boolean usersAwaitingApproval) {
    this.usersAwaitingApproval = usersAwaitingApproval;
    return this;
  }

  /**
   * Get usersAwaitingApproval
   * @return usersAwaitingApproval
   */
  
  @Schema(name = "usersAwaitingApproval", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("usersAwaitingApproval")
  public Boolean getUsersAwaitingApproval() {
    return usersAwaitingApproval;
  }

  public void setUsersAwaitingApproval(Boolean usersAwaitingApproval) {
    this.usersAwaitingApproval = usersAwaitingApproval;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    NotificationsResponse notificationsResponse = (NotificationsResponse) o;
    return Objects.equals(this.profileUpdateRequired, notificationsResponse.profileUpdateRequired) &&
        Objects.equals(this.status, notificationsResponse.status) &&
        Objects.equals(this.usersAwaitingApproval, notificationsResponse.usersAwaitingApproval);
  }

  @Override
  public int hashCode() {
    return Objects.hash(profileUpdateRequired, status, usersAwaitingApproval);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class NotificationsResponse {\n");
    sb.append("    profileUpdateRequired: ").append(toIndentedString(profileUpdateRequired)).append("\n");
    sb.append("    status: ").append(toIndentedString(status)).append("\n");
    sb.append("    usersAwaitingApproval: ").append(toIndentedString(usersAwaitingApproval)).append("\n");
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

