package uk.co.whitbread.content.entity.service.generated.models.content;

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
 * CardStatusOptionsDto
 */

@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2026-08-25T07:49:46.057591+03:00[Europe/Bucharest]", comments = "Generator version: 7.12.0")
public class CardStatusOptionsDto {

  private @Nullable String activate;

  private @Nullable String active;

  private @Nullable String cancelled;

  private @Nullable String dispatching;

  private @Nullable String expired;

  public CardStatusOptionsDto activate(String activate) {
    this.activate = activate;
    return this;
  }

  /**
   * Get activate
   * @return activate
   */
  
  @Schema(name = "activate", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("activate")
  public String getActivate() {
    return activate;
  }

  public void setActivate(String activate) {
    this.activate = activate;
  }

  public CardStatusOptionsDto active(String active) {
    this.active = active;
    return this;
  }

  /**
   * Get active
   * @return active
   */
  
  @Schema(name = "active", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("active")
  public String getActive() {
    return active;
  }

  public void setActive(String active) {
    this.active = active;
  }

  public CardStatusOptionsDto cancelled(String cancelled) {
    this.cancelled = cancelled;
    return this;
  }

  /**
   * Get cancelled
   * @return cancelled
   */
  
  @Schema(name = "cancelled", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("cancelled")
  public String getCancelled() {
    return cancelled;
  }

  public void setCancelled(String cancelled) {
    this.cancelled = cancelled;
  }

  public CardStatusOptionsDto dispatching(String dispatching) {
    this.dispatching = dispatching;
    return this;
  }

  /**
   * Get dispatching
   * @return dispatching
   */
  
  @Schema(name = "dispatching", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("dispatching")
  public String getDispatching() {
    return dispatching;
  }

  public void setDispatching(String dispatching) {
    this.dispatching = dispatching;
  }

  public CardStatusOptionsDto expired(String expired) {
    this.expired = expired;
    return this;
  }

  /**
   * Get expired
   * @return expired
   */
  
  @Schema(name = "expired", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("expired")
  public String getExpired() {
    return expired;
  }

  public void setExpired(String expired) {
    this.expired = expired;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    CardStatusOptionsDto cardStatusOptionsDto = (CardStatusOptionsDto) o;
    return Objects.equals(this.activate, cardStatusOptionsDto.activate) &&
        Objects.equals(this.active, cardStatusOptionsDto.active) &&
        Objects.equals(this.cancelled, cardStatusOptionsDto.cancelled) &&
        Objects.equals(this.dispatching, cardStatusOptionsDto.dispatching) &&
        Objects.equals(this.expired, cardStatusOptionsDto.expired);
  }

  @Override
  public int hashCode() {
    return Objects.hash(activate, active, cancelled, dispatching, expired);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class CardStatusOptionsDto {\n");
    sb.append("    activate: ").append(toIndentedString(activate)).append("\n");
    sb.append("    active: ").append(toIndentedString(active)).append("\n");
    sb.append("    cancelled: ").append(toIndentedString(cancelled)).append("\n");
    sb.append("    dispatching: ").append(toIndentedString(dispatching)).append("\n");
    sb.append("    expired: ").append(toIndentedString(expired)).append("\n");
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

