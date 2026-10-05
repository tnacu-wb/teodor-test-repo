package uk.co.whitbread.hotel.ohip.adapter.generated.models.basket;

import java.net.URI;
import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import java.util.HashMap;
import java.util.Map;
import org.springframework.lang.Nullable;
import org.openapitools.jackson.nullable.JsonNullable;
import java.time.OffsetDateTime;
import javax.validation.Valid;
import javax.validation.constraints.*;
import io.swagger.v3.oas.annotations.media.Schema;


import java.util.*;
import javax.annotation.Generated;

/**
 * AddBasketItemDto
 */

@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2026-08-25T07:52:44.119190+03:00[Europe/Bucharest]", comments = "Generator version: 7.12.0")
public class AddBasketItemDto {

  @Valid
  private Map<String, String> details = new HashMap<>();

  private @Nullable Boolean hasOccupancySup;

  private String sourceId;

  private String type;

  public AddBasketItemDto() {
    super();
  }

  /**
   * Constructor with only required parameters
   */
  public AddBasketItemDto(String sourceId, String type) {
    this.sourceId = sourceId;
    this.type = type;
  }

  public AddBasketItemDto details(Map<String, String> details) {
    this.details = details;
    return this;
  }

  public AddBasketItemDto putDetailsItem(String key, String detailsItem) {
    if (this.details == null) {
      this.details = new HashMap<>();
    }
    this.details.put(key, detailsItem);
    return this;
  }

  /**
   * Get details
   * @return details
   */
  
  @Schema(name = "details", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("details")
  public Map<String, String> getDetails() {
    return details;
  }

  public void setDetails(Map<String, String> details) {
    this.details = details;
  }

  public AddBasketItemDto hasOccupancySup(Boolean hasOccupancySup) {
    this.hasOccupancySup = hasOccupancySup;
    return this;
  }

  /**
   * Get hasOccupancySup
   * @return hasOccupancySup
   */
  
  @Schema(name = "hasOccupancySup", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("hasOccupancySup")
  public Boolean getHasOccupancySup() {
    return hasOccupancySup;
  }

  public void setHasOccupancySup(Boolean hasOccupancySup) {
    this.hasOccupancySup = hasOccupancySup;
  }

  public AddBasketItemDto sourceId(String sourceId) {
    this.sourceId = sourceId;
    return this;
  }

  /**
   * Get sourceId
   * @return sourceId
   */
  @NotNull 
  @Schema(name = "sourceId", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("sourceId")
  public String getSourceId() {
    return sourceId;
  }

  public void setSourceId(String sourceId) {
    this.sourceId = sourceId;
  }

  public AddBasketItemDto type(String type) {
    this.type = type;
    return this;
  }

  /**
   * Get type
   * @return type
   */
  @NotNull 
  @Schema(name = "type", requiredMode = Schema.RequiredMode.REQUIRED)
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
    AddBasketItemDto addBasketItemDto = (AddBasketItemDto) o;
    return Objects.equals(this.details, addBasketItemDto.details) &&
        Objects.equals(this.hasOccupancySup, addBasketItemDto.hasOccupancySup) &&
        Objects.equals(this.sourceId, addBasketItemDto.sourceId) &&
        Objects.equals(this.type, addBasketItemDto.type);
  }

  @Override
  public int hashCode() {
    return Objects.hash(details, hasOccupancySup, sourceId, type);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class AddBasketItemDto {\n");
    sb.append("    details: ").append(toIndentedString(details)).append("\n");
    sb.append("    hasOccupancySup: ").append(toIndentedString(hasOccupancySup)).append("\n");
    sb.append("    sourceId: ").append(toIndentedString(sourceId)).append("\n");
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

