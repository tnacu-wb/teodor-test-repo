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
 * ManagementInformationQuestionDto
 */

@JsonTypeName("ManagementInformationQuestion")
@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2026-08-25T07:52:38.523560+03:00[Europe/Bucharest]", comments = "Generator version: 7.12.0")
public class ManagementInformationQuestionDto {

  private Boolean active;

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

  public ManagementInformationQuestionDto() {
    super();
  }

  /**
   * Constructor with only required parameters
   */
  public ManagementInformationQuestionDto(Boolean active, String label, LocationEnum location, String managementHeader, ManagementInformationAnswerDto managementInformationAnswer, Boolean mandatory) {
    this.active = active;
    this.label = label;
    this.location = location;
    this.managementHeader = managementHeader;
    this.managementInformationAnswer = managementInformationAnswer;
    this.mandatory = mandatory;
  }

  public ManagementInformationQuestionDto active(Boolean active) {
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

  public ManagementInformationQuestionDto label(String label) {
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

  public ManagementInformationQuestionDto location(LocationEnum location) {
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

  public ManagementInformationQuestionDto managementHeader(String managementHeader) {
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

  public ManagementInformationQuestionDto managementInformationAnswer(ManagementInformationAnswerDto managementInformationAnswer) {
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

  public ManagementInformationQuestionDto mandatory(Boolean mandatory) {
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

  public ManagementInformationQuestionDto positionId(Integer positionId) {
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

  public ManagementInformationQuestionDto questionId(String questionId) {
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

  public ManagementInformationQuestionDto type(String type) {
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
    ManagementInformationQuestionDto managementInformationQuestion = (ManagementInformationQuestionDto) o;
    return Objects.equals(this.active, managementInformationQuestion.active) &&
        Objects.equals(this.label, managementInformationQuestion.label) &&
        Objects.equals(this.location, managementInformationQuestion.location) &&
        Objects.equals(this.managementHeader, managementInformationQuestion.managementHeader) &&
        Objects.equals(this.managementInformationAnswer, managementInformationQuestion.managementInformationAnswer) &&
        Objects.equals(this.mandatory, managementInformationQuestion.mandatory) &&
        Objects.equals(this.positionId, managementInformationQuestion.positionId) &&
        Objects.equals(this.questionId, managementInformationQuestion.questionId) &&
        Objects.equals(this.type, managementInformationQuestion.type);
  }

  @Override
  public int hashCode() {
    return Objects.hash(active, label, location, managementHeader, managementInformationAnswer, mandatory, positionId, questionId, type);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class ManagementInformationQuestionDto {\n");
    sb.append("    active: ").append(toIndentedString(active)).append("\n");
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

