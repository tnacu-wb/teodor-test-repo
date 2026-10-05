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
 * NotificationDto
 */

@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2026-08-27T18:51:28.563902+03:00[Europe/Bucharest]", comments = "Generator version: 7.12.0")
public class NotificationDto {

  private @Nullable String alert;

  private @Nullable String error;

  private @Nullable String info;

  private @Nullable String question;

  private @Nullable String success;

  public NotificationDto alert(String alert) {
    this.alert = alert;
    return this;
  }

  /**
   * Get alert
   * @return alert
   */
  
  @Schema(name = "alert", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("alert")
  public String getAlert() {
    return alert;
  }

  public void setAlert(String alert) {
    this.alert = alert;
  }

  public NotificationDto error(String error) {
    this.error = error;
    return this;
  }

  /**
   * Get error
   * @return error
   */
  
  @Schema(name = "error", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("error")
  public String getError() {
    return error;
  }

  public void setError(String error) {
    this.error = error;
  }

  public NotificationDto info(String info) {
    this.info = info;
    return this;
  }

  /**
   * Get info
   * @return info
   */
  
  @Schema(name = "info", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("info")
  public String getInfo() {
    return info;
  }

  public void setInfo(String info) {
    this.info = info;
  }

  public NotificationDto question(String question) {
    this.question = question;
    return this;
  }

  /**
   * Get question
   * @return question
   */
  
  @Schema(name = "question", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("question")
  public String getQuestion() {
    return question;
  }

  public void setQuestion(String question) {
    this.question = question;
  }

  public NotificationDto success(String success) {
    this.success = success;
    return this;
  }

  /**
   * Get success
   * @return success
   */
  
  @Schema(name = "success", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("success")
  public String getSuccess() {
    return success;
  }

  public void setSuccess(String success) {
    this.success = success;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    NotificationDto notificationDto = (NotificationDto) o;
    return Objects.equals(this.alert, notificationDto.alert) &&
        Objects.equals(this.error, notificationDto.error) &&
        Objects.equals(this.info, notificationDto.info) &&
        Objects.equals(this.question, notificationDto.question) &&
        Objects.equals(this.success, notificationDto.success);
  }

  @Override
  public int hashCode() {
    return Objects.hash(alert, error, info, question, success);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class NotificationDto {\n");
    sb.append("    alert: ").append(toIndentedString(alert)).append("\n");
    sb.append("    error: ").append(toIndentedString(error)).append("\n");
    sb.append("    info: ").append(toIndentedString(info)).append("\n");
    sb.append("    question: ").append(toIndentedString(question)).append("\n");
    sb.append("    success: ").append(toIndentedString(success)).append("\n");
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

