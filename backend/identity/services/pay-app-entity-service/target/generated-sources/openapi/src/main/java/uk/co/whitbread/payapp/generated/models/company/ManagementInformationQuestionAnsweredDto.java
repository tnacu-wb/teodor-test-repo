package uk.co.whitbread.payapp.generated.models.company;

import java.net.URI;
import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonTypeName;
import com.fasterxml.jackson.annotation.JsonValue;
import org.springframework.lang.Nullable;
import uk.co.whitbread.payapp.generated.models.company.ManagementInformationAnswerDto;
import org.openapitools.jackson.nullable.JsonNullable;
import java.time.OffsetDateTime;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import io.swagger.v3.oas.annotations.media.Schema;


import java.util.*;
import jakarta.annotation.Generated;

/**
 * ManagementInformationQuestionAnsweredDto
 */

@JsonTypeName("ManagementInformationQuestionAnswered")
@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2026-08-25T07:52:38.523560+03:00[Europe/Bucharest]", comments = "Generator version: 7.12.0")
public class ManagementInformationQuestionAnsweredDto {

  private Boolean active;

  private String answer;

  private String label;

  /**
   * Gets or Sets location
   */
  public enum LocationEnum {
    B("B"),
    
    R("R");

    private String value;

    LocationEnum(String value) {
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
    public static LocationEnum fromValue(String value) {
      for (LocationEnum b : LocationEnum.values()) {
        if (b.value.equals(value)) {
          return b;
        }
      }
      throw new IllegalArgumentException("Unexpected value '" + value + "'");
    }
  }

  private LocationEnum location;

  private String managementHeader;

  private ManagementInformationAnswerDto managementInformationAnswer;

  private Boolean mandatory;

  private @Nullable Integer positionId;

  private @Nullable String questionId;

  private @Nullable String type;

  public ManagementInformationQuestionAnsweredDto() {
    super();
  }

  /**
   * Constructor with only required parameters
   */
  public ManagementInformationQuestionAnsweredDto(Boolean active, String answer, String label, LocationEnum location, String managementHeader, ManagementInformationAnswerDto managementInformationAnswer, Boolean mandatory) {
    this.active = active;
    this.answer = answer;
    this.label = label;
    this.location = location;
    this.managementHeader = managementHeader;
    this.managementInformationAnswer = managementInformationAnswer;
    this.mandatory = mandatory;
  }

  public ManagementInformationQuestionAnsweredDto active(Boolean active) {
    this.active = active;
    return this;
  }

  /**
   * Get active
   * @return active
   */
  @NotNull 
  @Schema(name = "active", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("active")
  public Boolean getActive() {
    return active;
  }

  public void setActive(Boolean active) {
    this.active = active;
  }

  public ManagementInformationQuestionAnsweredDto answer(String answer) {
    this.answer = answer;
    return this;
  }

  /**
   * Get answer
   * @return answer
   */
  @NotNull 
  @Schema(name = "answer", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("answer")
  public String getAnswer() {
    return answer;
  }

  public void setAnswer(String answer) {
    this.answer = answer;
  }

  public ManagementInformationQuestionAnsweredDto label(String label) {
    this.label = label;
    return this;
  }

  /**
   * Get label
   * @return label
   */
  @NotNull 
  @Schema(name = "label", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("label")
  public String getLabel() {
    return label;
  }

  public void setLabel(String label) {
    this.label = label;
  }

  public ManagementInformationQuestionAnsweredDto location(LocationEnum location) {
    this.location = location;
    return this;
  }

  /**
   * Get location
   * @return location
   */
  @NotNull 
  @Schema(name = "location", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("location")
  public LocationEnum getLocation() {
    return location;
  }

  public void setLocation(LocationEnum location) {
    this.location = location;
  }

  public ManagementInformationQuestionAnsweredDto managementHeader(String managementHeader) {
    this.managementHeader = managementHeader;
    return this;
  }

  /**
   * Get managementHeader
   * @return managementHeader
   */
  @NotNull 
  @Schema(name = "managementHeader", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("managementHeader")
  public String getManagementHeader() {
    return managementHeader;
  }

  public void setManagementHeader(String managementHeader) {
    this.managementHeader = managementHeader;
  }

  public ManagementInformationQuestionAnsweredDto managementInformationAnswer(ManagementInformationAnswerDto managementInformationAnswer) {
    this.managementInformationAnswer = managementInformationAnswer;
    return this;
  }

  /**
   * Get managementInformationAnswer
   * @return managementInformationAnswer
   */
  @NotNull @Valid 
  @Schema(name = "managementInformationAnswer", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("managementInformationAnswer")
  public ManagementInformationAnswerDto getManagementInformationAnswer() {
    return managementInformationAnswer;
  }

  public void setManagementInformationAnswer(ManagementInformationAnswerDto managementInformationAnswer) {
    this.managementInformationAnswer = managementInformationAnswer;
  }

  public ManagementInformationQuestionAnsweredDto mandatory(Boolean mandatory) {
    this.mandatory = mandatory;
    return this;
  }

  /**
   * Get mandatory
   * @return mandatory
   */
  @NotNull 
  @Schema(name = "mandatory", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("mandatory")
  public Boolean getMandatory() {
    return mandatory;
  }

  public void setMandatory(Boolean mandatory) {
    this.mandatory = mandatory;
  }

  public ManagementInformationQuestionAnsweredDto positionId(Integer positionId) {
    this.positionId = positionId;
    return this;
  }

  /**
   * Get positionId
   * @return positionId
   */
  
  @Schema(name = "positionId", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("positionId")
  public Integer getPositionId() {
    return positionId;
  }

  public void setPositionId(Integer positionId) {
    this.positionId = positionId;
  }

  public ManagementInformationQuestionAnsweredDto questionId(String questionId) {
    this.questionId = questionId;
    return this;
  }

  /**
   * Get questionId
   * @return questionId
   */
  
  @Schema(name = "questionId", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("questionId")
  public String getQuestionId() {
    return questionId;
  }

  public void setQuestionId(String questionId) {
    this.questionId = questionId;
  }

  public ManagementInformationQuestionAnsweredDto type(String type) {
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
    ManagementInformationQuestionAnsweredDto managementInformationQuestionAnswered = (ManagementInformationQuestionAnsweredDto) o;
    return Objects.equals(this.active, managementInformationQuestionAnswered.active) &&
        Objects.equals(this.answer, managementInformationQuestionAnswered.answer) &&
        Objects.equals(this.label, managementInformationQuestionAnswered.label) &&
        Objects.equals(this.location, managementInformationQuestionAnswered.location) &&
        Objects.equals(this.managementHeader, managementInformationQuestionAnswered.managementHeader) &&
        Objects.equals(this.managementInformationAnswer, managementInformationQuestionAnswered.managementInformationAnswer) &&
        Objects.equals(this.mandatory, managementInformationQuestionAnswered.mandatory) &&
        Objects.equals(this.positionId, managementInformationQuestionAnswered.positionId) &&
        Objects.equals(this.questionId, managementInformationQuestionAnswered.questionId) &&
        Objects.equals(this.type, managementInformationQuestionAnswered.type);
  }

  @Override
  public int hashCode() {
    return Objects.hash(active, answer, label, location, managementHeader, managementInformationAnswer, mandatory, positionId, questionId, type);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class ManagementInformationQuestionAnsweredDto {\n");
    sb.append("    active: ").append(toIndentedString(active)).append("\n");
    sb.append("    answer: ").append(toIndentedString(answer)).append("\n");
    sb.append("    label: ").append(toIndentedString(label)).append("\n");
    sb.append("    location: ").append(toIndentedString(location)).append("\n");
    sb.append("    managementHeader: ").append(toIndentedString(managementHeader)).append("\n");
    sb.append("    managementInformationAnswer: ").append(toIndentedString(managementInformationAnswer)).append("\n");
    sb.append("    mandatory: ").append(toIndentedString(mandatory)).append("\n");
    sb.append("    positionId: ").append(toIndentedString(positionId)).append("\n");
    sb.append("    questionId: ").append(toIndentedString(questionId)).append("\n");
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

