package uk.co.whitbread.cdh.adapter.service.generated.models.reservationSearch;

import java.net.URI;
import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.springframework.lang.Nullable;
import org.openapitools.jackson.nullable.JsonNullable;
import java.time.OffsetDateTime;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import io.swagger.v3.oas.annotations.media.Schema;


import java.util.*;
import jakarta.annotation.Generated;

/**
 * AnswersDto
 */

@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2026-09-09T08:37:59.335673+03:00[Europe/Bucharest]", comments = "Generator version: 7.14.0")
public class AnswersDto {

  @Valid
  private List<String> answerValues = new ArrayList<>();

  private @Nullable String type;

  public AnswersDto answerValues(List<String> answerValues) {
    this.answerValues = answerValues;
    return this;
  }

  public AnswersDto addAnswerValuesItem(String answerValuesItem) {
    if (this.answerValues == null) {
      this.answerValues = new ArrayList<>();
    }
    this.answerValues.add(answerValuesItem);
    return this;
  }

  /**
   * Get answerValues
   * @return answerValues
   */
  
  @Schema(name = "answerValues", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("answerValues")
  public List<String> getAnswerValues() {
    return answerValues;
  }

  public void setAnswerValues(List<String> answerValues) {
    this.answerValues = answerValues;
  }

  public AnswersDto type(@Nullable String type) {
    this.type = type;
    return this;
  }

  /**
   * Get type
   * @return type
   */
  
  @Schema(name = "type", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("type")
  public @Nullable String getType() {
    return type;
  }

  public void setType(@Nullable String type) {
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
    AnswersDto answersDto = (AnswersDto) o;
    return Objects.equals(this.answerValues, answersDto.answerValues) &&
        Objects.equals(this.type, answersDto.type);
  }

  @Override
  public int hashCode() {
    return Objects.hash(answerValues, type);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class AnswersDto {\n");
    sb.append("    answerValues: ").append(toIndentedString(answerValues)).append("\n");
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

