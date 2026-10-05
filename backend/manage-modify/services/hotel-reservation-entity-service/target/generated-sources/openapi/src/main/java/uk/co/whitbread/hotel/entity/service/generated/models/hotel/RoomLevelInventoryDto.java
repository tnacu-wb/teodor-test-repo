package uk.co.whitbread.hotel.entity.service.generated.models.hotel;

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
 * RoomLevelInventoryDto
 */

@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2026-08-27T18:51:33.749132+03:00[Europe/Bucharest]", comments = "Generator version: 7.12.0")
public class RoomLevelInventoryDto {

  private @Nullable Integer availableCount;

  private @Nullable String code;

  public RoomLevelInventoryDto availableCount(Integer availableCount) {
    this.availableCount = availableCount;
    return this;
  }

  /**
   * Get availableCount
   * @return availableCount
   */
  
  @Schema(name = "availableCount", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("availableCount")
  public Integer getAvailableCount() {
    return availableCount;
  }

  public void setAvailableCount(Integer availableCount) {
    this.availableCount = availableCount;
  }

  public RoomLevelInventoryDto code(String code) {
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

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    RoomLevelInventoryDto roomLevelInventoryDto = (RoomLevelInventoryDto) o;
    return Objects.equals(this.availableCount, roomLevelInventoryDto.availableCount) &&
        Objects.equals(this.code, roomLevelInventoryDto.code);
  }

  @Override
  public int hashCode() {
    return Objects.hash(availableCount, code);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class RoomLevelInventoryDto {\n");
    sb.append("    availableCount: ").append(toIndentedString(availableCount)).append("\n");
    sb.append("    code: ").append(toIndentedString(code)).append("\n");
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

