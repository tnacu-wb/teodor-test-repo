package uk.co.whitbread.cdh.adapter.generated.cdhadapter.model;

import java.net.URI;
import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import org.jspecify.annotations.Nullable;
import uk.co.whitbread.cdh.adapter.generated.cdhadapter.model.AnswersDto;
import org.openapitools.jackson.nullable.JsonNullable;
import java.time.OffsetDateTime;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import io.swagger.v3.oas.annotations.media.Schema;


import java.util.*;
import jakarta.annotation.Generated;

/**
 * CustomerReferenceManagementDto
 */

@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2026-08-25T07:50:33.100036+03:00[Europe/Bucharest]", comments = "Generator version: 7.12.0")
public class CustomerReferenceManagementDto {

  private @Nullable Boolean active;

  private @Nullable AnswersDto answers;

  private @Nullable String header;

  private @Nullable String id;

  private @Nullable String label;

  private @Nullable String location;

  private @Nullable Boolean mandatory;

  private @Nullable String type;

  public CustomerReferenceManagementDto active(Boolean active) {
    this.active = active;
    return this;
  }

  /**
   * Get active
   * @return active
   */
  
  @Schema(name = "active", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("active")
  public Boolean getActive() {
    return active;
  }

  public void setActive(Boolean active) {
    this.active = active;
  }

  public CustomerReferenceManagementDto answers(AnswersDto answers) {
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

  public CustomerReferenceManagementDto header(String header) {
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

  public CustomerReferenceManagementDto id(String id) {
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

  public CustomerReferenceManagementDto label(String label) {
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

  public CustomerReferenceManagementDto location(String location) {
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

  public CustomerReferenceManagementDto mandatory(Boolean mandatory) {
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

  public CustomerReferenceManagementDto type(String type) {
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
    CustomerReferenceManagementDto customerReferenceManagementDto = (CustomerReferenceManagementDto) o;
    return Objects.equals(this.active, customerReferenceManagementDto.active) &&
        Objects.equals(this.answers, customerReferenceManagementDto.answers) &&
        Objects.equals(this.header, customerReferenceManagementDto.header) &&
        Objects.equals(this.id, customerReferenceManagementDto.id) &&
        Objects.equals(this.label, customerReferenceManagementDto.label) &&
        Objects.equals(this.location, customerReferenceManagementDto.location) &&
        Objects.equals(this.mandatory, customerReferenceManagementDto.mandatory) &&
        Objects.equals(this.type, customerReferenceManagementDto.type);
  }

  @Override
  public int hashCode() {
    return Objects.hash(active, answers, header, id, label, location, mandatory, type);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class CustomerReferenceManagementDto {\n");
    sb.append("    active: ").append(toIndentedString(active)).append("\n");
    sb.append("    answers: ").append(toIndentedString(answers)).append("\n");
    sb.append("    header: ").append(toIndentedString(header)).append("\n");
    sb.append("    id: ").append(toIndentedString(id)).append("\n");
    sb.append("    label: ").append(toIndentedString(label)).append("\n");
    sb.append("    location: ").append(toIndentedString(location)).append("\n");
    sb.append("    mandatory: ").append(toIndentedString(mandatory)).append("\n");
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

