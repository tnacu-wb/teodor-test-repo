package uk.co.whitbread.cdh.adapter.service.generated.models.reservationSearch;

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
 * ValidationError
 */

@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2026-09-09T08:37:59.335673+03:00[Europe/Bucharest]", comments = "Generator version: 7.14.0")
public class ValidationError {

  private @Nullable String elementId;

  private @Nullable String errTextTemplate;

  public ValidationError elementId(@Nullable String elementId) {
    this.elementId = elementId;
    return this;
  }

  /**
   * Get elementId
   * @return elementId
   */
  
  @Schema(name = "elementId", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("elementId")
  public @Nullable String getElementId() {
    return elementId;
  }

  public void setElementId(@Nullable String elementId) {
    this.elementId = elementId;
  }

  public ValidationError errTextTemplate(@Nullable String errTextTemplate) {
    this.errTextTemplate = errTextTemplate;
    return this;
  }

  /**
   * Get errTextTemplate
   * @return errTextTemplate
   */
  
  @Schema(name = "errTextTemplate", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("errTextTemplate")
  public @Nullable String getErrTextTemplate() {
    return errTextTemplate;
  }

  public void setErrTextTemplate(@Nullable String errTextTemplate) {
    this.errTextTemplate = errTextTemplate;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    ValidationError validationError = (ValidationError) o;
    return Objects.equals(this.elementId, validationError.elementId) &&
        Objects.equals(this.errTextTemplate, validationError.errTextTemplate);
  }

  @Override
  public int hashCode() {
    return Objects.hash(elementId, errTextTemplate);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class ValidationError {\n");
    sb.append("    elementId: ").append(toIndentedString(elementId)).append("\n");
    sb.append("    errTextTemplate: ").append(toIndentedString(errTextTemplate)).append("\n");
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

