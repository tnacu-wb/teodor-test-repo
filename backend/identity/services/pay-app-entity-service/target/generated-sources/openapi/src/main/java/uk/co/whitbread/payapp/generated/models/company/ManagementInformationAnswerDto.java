package uk.co.whitbread.payapp.generated.models.company;

import java.net.URI;
import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonTypeName;
import com.fasterxml.jackson.annotation.JsonValue;
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
 * ManagementInformationAnswerDto
 */

@JsonTypeName("ManagementInformationAnswer")
@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2026-08-25T07:52:38.523560+03:00[Europe/Bucharest]", comments = "Generator version: 7.12.0")
public class ManagementInformationAnswerDto {

  /**
   * Gets or Sets answerType
   */
  public enum AnswerTypeEnum {
    F("F"),
    
    U("U");

    private String value;

    AnswerTypeEnum(String value) {
      this.value = value;
    }

    @JsonValue
    public String getValue() {
      return value;
    }

    @Override
    public String toString() {
      return String.valueOf(value);
    }

    @JsonCreator
    public static AnswerTypeEnum fromValue(String value) {
      for (AnswerTypeEnum b : AnswerTypeEnum.values()) {
        if (b.value.equals(value)) {
          return b;
        }
      }
      throw new IllegalArgumentException("Unexpected value '" + value + "'");
    }
  }

  private @Nullable AnswerTypeEnum answerType;

  @Valid
  private List<String> answers = new ArrayList<>();

  public ManagementInformationAnswerDto answerType(AnswerTypeEnum answerType) {
    this.answerType = answerType;
    return this;
  }

  /**
   * Get answerType
   * @return answerType
   */
  
  @Schema(name = "answerType", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("answerType")
  public AnswerTypeEnum getAnswerType() {
    return answerType;
  }

  public void setAnswerType(AnswerTypeEnum answerType) {
    this.answerType = answerType;
  }

  public ManagementInformationAnswerDto answers(List<String> answers) {
    this.answers = answers;
    return this;
  }

  public ManagementInformationAnswerDto addAnswersItem(String answersItem) {
    if (this.answers == null) {
      this.answers = new ArrayList<>();
    }
    this.answers.add(answersItem);
    return this;
  }

  /**
   * Get answers
   * @return answers
   */
  
  @Schema(name = "answers", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("answers")
  public List<String> getAnswers() {
    return answers;
  }

  public void setAnswers(List<String> answers) {
    this.answers = answers;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    ManagementInformationAnswerDto managementInformationAnswer = (ManagementInformationAnswerDto) o;
    return Objects.equals(this.answerType, managementInformationAnswer.answerType) &&
        Objects.equals(this.answers, managementInformationAnswer.answers);
  }

  @Override
  public int hashCode() {
    return Objects.hash(answerType, answers);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class ManagementInformationAnswerDto {\n");
    sb.append("    answerType: ").append(toIndentedString(answerType)).append("\n");
    sb.append("    answers: ").append(toIndentedString(answers)).append("\n");
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

