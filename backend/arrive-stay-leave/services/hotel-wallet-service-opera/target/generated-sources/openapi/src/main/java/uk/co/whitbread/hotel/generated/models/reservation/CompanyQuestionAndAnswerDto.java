package uk.co.whitbread.hotel.generated.models.reservation;

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
 * CompanyQuestionAndAnswerDto
 */

@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2026-09-04T08:11:18.711997+03:00[Europe/Bucharest]", comments = "Generator version: 7.12.0")
public class CompanyQuestionAndAnswerDto {

  private @Nullable String answer;

  private @Nullable String question;

  private @Nullable String questionHeader;

  public CompanyQuestionAndAnswerDto answer(String answer) {
    this.answer = answer;
    return this;
  }

  /**
   * Get answer
   * @return answer
   */
  
  @Schema(name = "answer", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("answer")
  public String getAnswer() {
    return answer;
  }

  public void setAnswer(String answer) {
    this.answer = answer;
  }

  public CompanyQuestionAndAnswerDto question(String question) {
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

  public CompanyQuestionAndAnswerDto questionHeader(String questionHeader) {
    this.questionHeader = questionHeader;
    return this;
  }

  /**
   * Get questionHeader
   * @return questionHeader
   */
  
  @Schema(name = "questionHeader", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("questionHeader")
  public String getQuestionHeader() {
    return questionHeader;
  }

  public void setQuestionHeader(String questionHeader) {
    this.questionHeader = questionHeader;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    CompanyQuestionAndAnswerDto companyQuestionAndAnswerDto = (CompanyQuestionAndAnswerDto) o;
    return Objects.equals(this.answer, companyQuestionAndAnswerDto.answer) &&
        Objects.equals(this.question, companyQuestionAndAnswerDto.question) &&
        Objects.equals(this.questionHeader, companyQuestionAndAnswerDto.questionHeader);
  }

  @Override
  public int hashCode() {
    return Objects.hash(answer, question, questionHeader);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class CompanyQuestionAndAnswerDto {\n");
    sb.append("    answer: ").append(toIndentedString(answer)).append("\n");
    sb.append("    question: ").append(toIndentedString(question)).append("\n");
    sb.append("    questionHeader: ").append(toIndentedString(questionHeader)).append("\n");
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

