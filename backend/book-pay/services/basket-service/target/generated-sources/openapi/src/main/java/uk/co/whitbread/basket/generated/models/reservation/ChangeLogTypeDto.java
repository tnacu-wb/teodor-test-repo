package uk.co.whitbread.basket.generated.models.reservation;

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
 * ChangeLogTypeDto
 */
@com.fasterxml.jackson.annotation.JsonIgnoreProperties(ignoreUnknown = true)

@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2026-09-04T08:10:03.993735+03:00[Europe/Bucharest]", comments = "Generator version: 7.12.0")
public class ChangeLogTypeDto {

  private @Nullable String actionDescription;

  private @Nullable String actionType;

  private @Nullable String date;

  private @Nullable String time;

  private @Nullable String user;

  public ChangeLogTypeDto actionDescription(String actionDescription) {
    this.actionDescription = actionDescription;
    return this;
  }

  /**
   * Get actionDescription
   * @return actionDescription
   */
  
  @Schema(name = "actionDescription", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("actionDescription")
  public String getActionDescription() {
    return actionDescription;
  }

  public void setActionDescription(String actionDescription) {
    this.actionDescription = actionDescription;
  }

  public ChangeLogTypeDto actionType(String actionType) {
    this.actionType = actionType;
    return this;
  }

  /**
   * Get actionType
   * @return actionType
   */
  
  @Schema(name = "actionType", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("actionType")
  public String getActionType() {
    return actionType;
  }

  public void setActionType(String actionType) {
    this.actionType = actionType;
  }

  public ChangeLogTypeDto date(String date) {
    this.date = date;
    return this;
  }

  /**
   * Get date
   * @return date
   */
  
  @Schema(name = "date", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("date")
  public String getDate() {
    return date;
  }

  public void setDate(String date) {
    this.date = date;
  }

  public ChangeLogTypeDto time(String time) {
    this.time = time;
    return this;
  }

  /**
   * Get time
   * @return time
   */
  
  @Schema(name = "time", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("time")
  public String getTime() {
    return time;
  }

  public void setTime(String time) {
    this.time = time;
  }

  public ChangeLogTypeDto user(String user) {
    this.user = user;
    return this;
  }

  /**
   * Get user
   * @return user
   */
  
  @Schema(name = "user", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("user")
  public String getUser() {
    return user;
  }

  public void setUser(String user) {
    this.user = user;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    ChangeLogTypeDto changeLogTypeDto = (ChangeLogTypeDto) o;
    return Objects.equals(this.actionDescription, changeLogTypeDto.actionDescription) &&
        Objects.equals(this.actionType, changeLogTypeDto.actionType) &&
        Objects.equals(this.date, changeLogTypeDto.date) &&
        Objects.equals(this.time, changeLogTypeDto.time) &&
        Objects.equals(this.user, changeLogTypeDto.user);
  }

  @Override
  public int hashCode() {
    return Objects.hash(actionDescription, actionType, date, time, user);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class ChangeLogTypeDto {\n");
    sb.append("    actionDescription: ").append(toIndentedString(actionDescription)).append("\n");
    sb.append("    actionType: ").append(toIndentedString(actionType)).append("\n");
    sb.append("    date: ").append(toIndentedString(date)).append("\n");
    sb.append("    time: ").append(toIndentedString(time)).append("\n");
    sb.append("    user: ").append(toIndentedString(user)).append("\n");
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

