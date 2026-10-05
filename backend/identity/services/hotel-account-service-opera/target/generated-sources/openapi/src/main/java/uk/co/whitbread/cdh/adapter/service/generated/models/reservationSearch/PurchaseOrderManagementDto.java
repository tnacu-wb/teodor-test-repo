package uk.co.whitbread.cdh.adapter.service.generated.models.reservationSearch;

import java.net.URI;
import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import org.springframework.lang.Nullable;
import uk.co.whitbread.cdh.adapter.service.generated.models.reservationSearch.AnswersDto;
import org.openapitools.jackson.nullable.JsonNullable;
import java.time.OffsetDateTime;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import io.swagger.v3.oas.annotations.media.Schema;


import java.util.*;
import jakarta.annotation.Generated;

/**
 * PurchaseOrderManagementDto
 */

@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2026-09-09T08:37:59.335673+03:00[Europe/Bucharest]", comments = "Generator version: 7.14.0")
public class PurchaseOrderManagementDto {

  private @Nullable Boolean active;

  private @Nullable AnswersDto answers;

  private @Nullable String header;

  private @Nullable String id;

  private @Nullable String label;

  private @Nullable String location;

  private @Nullable Boolean mandatory;

  private @Nullable String type;

  public PurchaseOrderManagementDto active(@Nullable Boolean active) {
    this.active = active;
    return this;
  }

  /**
   * Get active
   * @return active
   */
  
  @Schema(name = "active", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("active")
  public @Nullable Boolean getActive() {
    return active;
  }

  public void setActive(@Nullable Boolean active) {
    this.active = active;
  }

  public PurchaseOrderManagementDto answers(@Nullable AnswersDto answers) {
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
  public @Nullable AnswersDto getAnswers() {
    return answers;
  }

  public void setAnswers(@Nullable AnswersDto answers) {
    this.answers = answers;
  }

  public PurchaseOrderManagementDto header(@Nullable String header) {
    this.header = header;
    return this;
  }

  /**
   * Get header
   * @return header
   */
  
  @Schema(name = "header", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("header")
  public @Nullable String getHeader() {
    return header;
  }

  public void setHeader(@Nullable String header) {
    this.header = header;
  }

  public PurchaseOrderManagementDto id(@Nullable String id) {
    this.id = id;
    return this;
  }

  /**
   * Get id
   * @return id
   */
  
  @Schema(name = "id", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("id")
  public @Nullable String getId() {
    return id;
  }

  public void setId(@Nullable String id) {
    this.id = id;
  }

  public PurchaseOrderManagementDto label(@Nullable String label) {
    this.label = label;
    return this;
  }

  /**
   * Get label
   * @return label
   */
  
  @Schema(name = "label", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("label")
  public @Nullable String getLabel() {
    return label;
  }

  public void setLabel(@Nullable String label) {
    this.label = label;
  }

  public PurchaseOrderManagementDto location(@Nullable String location) {
    this.location = location;
    return this;
  }

  /**
   * Get location
   * @return location
   */
  
  @Schema(name = "location", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("location")
  public @Nullable String getLocation() {
    return location;
  }

  public void setLocation(@Nullable String location) {
    this.location = location;
  }

  public PurchaseOrderManagementDto mandatory(@Nullable Boolean mandatory) {
    this.mandatory = mandatory;
    return this;
  }

  /**
   * Get mandatory
   * @return mandatory
   */
  
  @Schema(name = "mandatory", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("mandatory")
  public @Nullable Boolean getMandatory() {
    return mandatory;
  }

  public void setMandatory(@Nullable Boolean mandatory) {
    this.mandatory = mandatory;
  }

  public PurchaseOrderManagementDto type(@Nullable String type) {
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
    PurchaseOrderManagementDto purchaseOrderManagementDto = (PurchaseOrderManagementDto) o;
    return Objects.equals(this.active, purchaseOrderManagementDto.active) &&
        Objects.equals(this.answers, purchaseOrderManagementDto.answers) &&
        Objects.equals(this.header, purchaseOrderManagementDto.header) &&
        Objects.equals(this.id, purchaseOrderManagementDto.id) &&
        Objects.equals(this.label, purchaseOrderManagementDto.label) &&
        Objects.equals(this.location, purchaseOrderManagementDto.location) &&
        Objects.equals(this.mandatory, purchaseOrderManagementDto.mandatory) &&
        Objects.equals(this.type, purchaseOrderManagementDto.type);
  }

  @Override
  public int hashCode() {
    return Objects.hash(active, answers, header, id, label, location, mandatory, type);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class PurchaseOrderManagementDto {\n");
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

