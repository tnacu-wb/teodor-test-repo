package uk.co.whitbread.cdh.adapter.service.generated.models.reservationSearch;

import java.net.URI;
import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import org.springframework.lang.Nullable;
import uk.co.whitbread.cdh.adapter.service.generated.models.reservationSearch.AnswersDto;
import org.openapitools.jackson.nullable.JsonNullable;
import java.time.OffsetDateTime;
import javax.validation.Valid;
import javax.validation.constraints.*;
import io.swagger.v3.oas.annotations.media.Schema;


import java.util.*;
import javax.annotation.Generated;

/**
 * QuestionsDto
 */

@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2026-08-27T18:51:32.868523+03:00[Europe/Bucharest]", comments = "Generator version: 7.12.0")
public class QuestionsDto {

  private @Nullable AnswersDto answers;

  private @Nullable String header;

  private @Nullable String id;

  private @Nullable String label;

  private @Nullable String location;

  private @Nullable Boolean mandatory;

  private @Nullable String position;

  public QuestionsDto answers(AnswersDto answers) {
    this.answers = answers;
    return this;
  }

  /**
   * Get answers
   * @return answers
   */
  @Valid 
  @Schema(name = "answers", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("answers")
  public AnswersDto getAnswers() {
    return answers;
  }

  public void setAnswers(AnswersDto answers) {
    this.answers = answers;
  }

  public QuestionsDto header(String header) {
    this.header = header;
    return this;
  }

  /**
   * Get header
   * @return header
   */
  
  @Schema(name = "header", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("header")
  public String getHeader() {
    return header;
  }

  public void setHeader(String header) {
    this.header = header;
  }

  public QuestionsDto id(String id) {
    this.id = id;
    return this;
  }

  /**
   * Get id
   * @return id
   */
  
  @Schema(name = "id", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("id")
  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

  public QuestionsDto label(String label) {
    this.label = label;
    return this;
  }

  /**
   * Get label
   * @return label
   */
  
  @Schema(name = "label", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("label")
  public String getLabel() {
    return label;
  }

  public void setLabel(String label) {
    this.label = label;
  }

  public QuestionsDto location(String location) {
    this.location = location;
    return this;
  }

  /**
   * Get location
   * @return location
   */
  
  @Schema(name = "location", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("location")
  public String getLocation() {
    return location;
  }

  public void setLocation(String location) {
    this.location = location;
  }

  public QuestionsDto mandatory(Boolean mandatory) {
    this.mandatory = mandatory;
    return this;
  }

  /**
   * Get mandatory
   * @return mandatory
   */
  
  @Schema(name = "mandatory", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("mandatory")
  public Boolean getMandatory() {
    return mandatory;
  }

  public void setMandatory(Boolean mandatory) {
    this.mandatory = mandatory;
  }

  public QuestionsDto position(String position) {
    this.position = position;
    return this;
  }

  /**
   * Get position
   * @return position
   */
  
  @Schema(name = "position", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("position")
  public String getPosition() {
    return position;
  }

  public void setPosition(String position) {
    this.position = position;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    QuestionsDto questionsDto = (QuestionsDto) o;
    return Objects.equals(this.answers, questionsDto.answers) &&
        Objects.equals(this.header, questionsDto.header) &&
        Objects.equals(this.id, questionsDto.id) &&
        Objects.equals(this.label, questionsDto.label) &&
        Objects.equals(this.location, questionsDto.location) &&
        Objects.equals(this.mandatory, questionsDto.mandatory) &&
        Objects.equals(this.position, questionsDto.position);
  }

  @Override
  public int hashCode() {
    return Objects.hash(answers, header, id, label, location, mandatory, position);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class QuestionsDto {\n");
    sb.append("    answers: ").append(toIndentedString(answers)).append("\n");
    sb.append("    header: ").append(toIndentedString(header)).append("\n");
    sb.append("    id: ").append(toIndentedString(id)).append("\n");
    sb.append("    label: ").append(toIndentedString(label)).append("\n");
    sb.append("    location: ").append(toIndentedString(location)).append("\n");
    sb.append("    mandatory: ").append(toIndentedString(mandatory)).append("\n");
    sb.append("    position: ").append(toIndentedString(position)).append("\n");
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

