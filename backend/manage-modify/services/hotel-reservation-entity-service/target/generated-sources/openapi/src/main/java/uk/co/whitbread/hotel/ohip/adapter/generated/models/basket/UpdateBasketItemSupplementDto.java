package uk.co.whitbread.hotel.ohip.adapter.generated.models.basket;

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
 * UpdateBasketItemSupplementDto
 */

@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2026-08-27T18:51:25.444555+03:00[Europe/Bucharest]", comments = "Generator version: 7.12.0")
public class UpdateBasketItemSupplementDto {

  private Boolean hasOccupancySup;

  private String sourceId;

  public UpdateBasketItemSupplementDto() {
    super();
  }

  /**
   * Constructor with only required parameters
   */
  public UpdateBasketItemSupplementDto(Boolean hasOccupancySup, String sourceId) {
    this.hasOccupancySup = hasOccupancySup;
    this.sourceId = sourceId;
  }

  public UpdateBasketItemSupplementDto hasOccupancySup(Boolean hasOccupancySup) {
    this.hasOccupancySup = hasOccupancySup;
    return this;
  }

  /**
   * Get hasOccupancySup
   * @return hasOccupancySup
   */
  @NotNull 
  @Schema(name = "hasOccupancySup", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("hasOccupancySup")
  public Boolean getHasOccupancySup() {
    return hasOccupancySup;
  }

  public void setHasOccupancySup(Boolean hasOccupancySup) {
    this.hasOccupancySup = hasOccupancySup;
  }

  public UpdateBasketItemSupplementDto sourceId(String sourceId) {
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

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    UpdateBasketItemSupplementDto updateBasketItemSupplementDto = (UpdateBasketItemSupplementDto) o;
    return Objects.equals(this.hasOccupancySup, updateBasketItemSupplementDto.hasOccupancySup) &&
        Objects.equals(this.sourceId, updateBasketItemSupplementDto.sourceId);
  }

  @Override
  public int hashCode() {
    return Objects.hash(hasOccupancySup, sourceId);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class UpdateBasketItemSupplementDto {\n");
    sb.append("    hasOccupancySup: ").append(toIndentedString(hasOccupancySup)).append("\n");
    sb.append("    sourceId: ").append(toIndentedString(sourceId)).append("\n");
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

