package uk.co.whitbread.hotel.ohip.adapter.generated.models;

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
 * AlertDto
 */

@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2026-08-27T18:51:15.274731+03:00[Europe/Bucharest]", comments = "Generator version: 7.12.0")
public class AlertDto {

  private @Nullable String area;

  private @Nullable String code;

  private @Nullable String description;

  private @Nullable String id;

  private @Nullable Boolean printerNotification;

  private @Nullable Boolean screenNotification;

  public AlertDto area(String area) {
    this.area = area;
    return this;
  }

  /**
   * Get area
   * @return area
   */
  
  @Schema(name = "area", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("area")
  public String getArea() {
    return area;
  }

  public void setArea(String area) {
    this.area = area;
  }

  public AlertDto code(String code) {
    this.code = code;
    return this;
  }

  /**
   * Get code
   * @return code
   */
  
  @Schema(name = "code", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("code")
  public String getCode() {
    return code;
  }

  public void setCode(String code) {
    this.code = code;
  }

  public AlertDto description(String description) {
    this.description = description;
    return this;
  }

  /**
   * Get description
   * @return description
   */
  
  @Schema(name = "description", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("description")
  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  public AlertDto id(String id) {
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

  public AlertDto printerNotification(Boolean printerNotification) {
    this.printerNotification = printerNotification;
    return this;
  }

  /**
   * Get printerNotification
   * @return printerNotification
   */
  
  @Schema(name = "printerNotification", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("printerNotification")
  public Boolean getPrinterNotification() {
    return printerNotification;
  }

  public void setPrinterNotification(Boolean printerNotification) {
    this.printerNotification = printerNotification;
  }

  public AlertDto screenNotification(Boolean screenNotification) {
    this.screenNotification = screenNotification;
    return this;
  }

  /**
   * Get screenNotification
   * @return screenNotification
   */
  
  @Schema(name = "screenNotification", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("screenNotification")
  public Boolean getScreenNotification() {
    return screenNotification;
  }

  public void setScreenNotification(Boolean screenNotification) {
    this.screenNotification = screenNotification;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    AlertDto alertDto = (AlertDto) o;
    return Objects.equals(this.area, alertDto.area) &&
        Objects.equals(this.code, alertDto.code) &&
        Objects.equals(this.description, alertDto.description) &&
        Objects.equals(this.id, alertDto.id) &&
        Objects.equals(this.printerNotification, alertDto.printerNotification) &&
        Objects.equals(this.screenNotification, alertDto.screenNotification);
  }

  @Override
  public int hashCode() {
    return Objects.hash(area, code, description, id, printerNotification, screenNotification);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class AlertDto {\n");
    sb.append("    area: ").append(toIndentedString(area)).append("\n");
    sb.append("    code: ").append(toIndentedString(code)).append("\n");
    sb.append("    description: ").append(toIndentedString(description)).append("\n");
    sb.append("    id: ").append(toIndentedString(id)).append("\n");
    sb.append("    printerNotification: ").append(toIndentedString(printerNotification)).append("\n");
    sb.append("    screenNotification: ").append(toIndentedString(screenNotification)).append("\n");
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

