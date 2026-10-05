package uk.co.whitbread.ohip.generated.models;

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
 * CommentDetailsDto
 */

@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2026-08-25T07:50:12.143180+03:00[Europe/Bucharest]", comments = "Generator version: 7.12.0")
public class CommentDetailsDto {

  private @Nullable String commentTitle;

  private @Nullable String textValue;

  private @Nullable String type;

  public CommentDetailsDto commentTitle(String commentTitle) {
    this.commentTitle = commentTitle;
    return this;
  }

  /**
   * Get commentTitle
   * @return commentTitle
   */
  
  @Schema(name = "commentTitle", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("commentTitle")
  public String getCommentTitle() {
    return commentTitle;
  }

  public void setCommentTitle(String commentTitle) {
    this.commentTitle = commentTitle;
  }

  public CommentDetailsDto textValue(String textValue) {
    this.textValue = textValue;
    return this;
  }

  /**
   * Get textValue
   * @return textValue
   */
  
  @Schema(name = "textValue", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("textValue")
  public String getTextValue() {
    return textValue;
  }

  public void setTextValue(String textValue) {
    this.textValue = textValue;
  }

  public CommentDetailsDto type(String type) {
    this.type = type;
    return this;
  }

  /**
   * Get type
   * @return type
   */
  
  @Schema(name = "type", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("type")
  public String getType() {
    return type;
  }

  public void setType(String type) {
    this.type = type;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    CommentDetailsDto commentDetailsDto = (CommentDetailsDto) o;
    return Objects.equals(this.commentTitle, commentDetailsDto.commentTitle) &&
        Objects.equals(this.textValue, commentDetailsDto.textValue) &&
        Objects.equals(this.type, commentDetailsDto.type);
  }

  @Override
  public int hashCode() {
    return Objects.hash(commentTitle, textValue, type);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class CommentDetailsDto {\n");
    sb.append("    commentTitle: ").append(toIndentedString(commentTitle)).append("\n");
    sb.append("    textValue: ").append(toIndentedString(textValue)).append("\n");
    sb.append("    type: ").append(toIndentedString(type)).append("\n");
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

