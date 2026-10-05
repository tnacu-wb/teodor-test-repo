package uk.co.whitbread.hotel.ohip.adapter.generated.models;

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
 * ChangeLogTypeDto
 */

@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2026-08-27T18:51:15.274731+03:00[Europe/Bucharest]", comments = "Generator version: 7.12.0")
public class ChangeLogTypeDto {

  private @Nullable String actionDescription;

  private @Nullable String actionType;

  private @Nullable String logDate;

  private @Nullable String logUserName;

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

  public ChangeLogTypeDto logDate(String logDate) {
    this.logDate = logDate;
    return this;
  }

  /**
   * Get logDate
   * @return logDate
   */
  
  @Schema(name = "logDate", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("logDate")
  public String getLogDate() {
    return logDate;
  }

  public void setLogDate(String logDate) {
    this.logDate = logDate;
  }

  public ChangeLogTypeDto logUserName(String logUserName) {
    this.logUserName = logUserName;
    return this;
  }

  /**
   * Get logUserName
   * @return logUserName
   */
  
  @Schema(name = "logUserName", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("logUserName")
  public String getLogUserName() {
    return logUserName;
  }

  public void setLogUserName(String logUserName) {
    this.logUserName = logUserName;
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
        Objects.equals(this.logDate, changeLogTypeDto.logDate) &&
        Objects.equals(this.logUserName, changeLogTypeDto.logUserName);
  }

  @Override
  public int hashCode() {
    return Objects.hash(actionDescription, actionType, logDate, logUserName);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class ChangeLogTypeDto {\n");
    sb.append("    actionDescription: ").append(toIndentedString(actionDescription)).append("\n");
    sb.append("    actionType: ").append(toIndentedString(actionType)).append("\n");
    sb.append("    logDate: ").append(toIndentedString(logDate)).append("\n");
    sb.append("    logUserName: ").append(toIndentedString(logUserName)).append("\n");
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

