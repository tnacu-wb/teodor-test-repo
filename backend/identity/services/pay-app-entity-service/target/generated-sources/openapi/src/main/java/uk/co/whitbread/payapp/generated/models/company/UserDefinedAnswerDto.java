package uk.co.whitbread.payapp.generated.models.company;

import java.net.URI;
import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonTypeName;
import org.springframework.lang.Nullable;
import org.openapitools.jackson.nullable.JsonNullable;
import java.time.OffsetDateTime;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import io.swagger.v3.oas.annotations.media.Schema;


import java.util.*;
import jakarta.annotation.Generated;

/**
 * UserDefinedAnswerDto
 */

@JsonTypeName("UserDefinedAnswer")
@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2026-08-25T07:52:38.523560+03:00[Europe/Bucharest]", comments = "Generator version: 7.12.0")
public class UserDefinedAnswerDto {

  private @Nullable String miAnswer;

  private @Nullable String miID;

  public UserDefinedAnswerDto miAnswer(String miAnswer) {
    this.miAnswer = miAnswer;
    return this;
  }

  /**
   * Get miAnswer
   * @return miAnswer
   */
  
  @Schema(name = "miAnswer", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("miAnswer")
  public String getMiAnswer() {
    return miAnswer;
  }

  public void setMiAnswer(String miAnswer) {
    this.miAnswer = miAnswer;
  }

  public UserDefinedAnswerDto miID(String miID) {
    this.miID = miID;
    return this;
  }

  /**
   * Get miID
   * @return miID
   */
  
  @Schema(name = "miID", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("miID")
  public String getMiID() {
    return miID;
  }

  public void setMiID(String miID) {
    this.miID = miID;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    UserDefinedAnswerDto userDefinedAnswer = (UserDefinedAnswerDto) o;
    return Objects.equals(this.miAnswer, userDefinedAnswer.miAnswer) &&
        Objects.equals(this.miID, userDefinedAnswer.miID);
  }

  @Override
  public int hashCode() {
    return Objects.hash(miAnswer, miID);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class UserDefinedAnswerDto {\n");
    sb.append("    miAnswer: ").append(toIndentedString(miAnswer)).append("\n");
    sb.append("    miID: ").append(toIndentedString(miID)).append("\n");
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

