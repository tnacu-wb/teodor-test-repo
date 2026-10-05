package uk.co.whitbread.content.entity.service.generated.models.content;

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
 * ArrowDto
 */

@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2026-08-25T07:49:46.057591+03:00[Europe/Bucharest]", comments = "Generator version: 7.12.0")
public class ArrowDto {

  private @Nullable String down;

  private @Nullable String left;

  private @Nullable String right;

  private @Nullable String up;

  public ArrowDto down(String down) {
    this.down = down;
    return this;
  }

  /**
   * Get down
   * @return down
   */
  
  @Schema(name = "down", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("down")
  public String getDown() {
    return down;
  }

  public void setDown(String down) {
    this.down = down;
  }

  public ArrowDto left(String left) {
    this.left = left;
    return this;
  }

  /**
   * Get left
   * @return left
   */
  
  @Schema(name = "left", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("left")
  public String getLeft() {
    return left;
  }

  public void setLeft(String left) {
    this.left = left;
  }

  public ArrowDto right(String right) {
    this.right = right;
    return this;
  }

  /**
   * Get right
   * @return right
   */
  
  @Schema(name = "right", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("right")
  public String getRight() {
    return right;
  }

  public void setRight(String right) {
    this.right = right;
  }

  public ArrowDto up(String up) {
    this.up = up;
    return this;
  }

  /**
   * Get up
   * @return up
   */
  
  @Schema(name = "up", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("up")
  public String getUp() {
    return up;
  }

  public void setUp(String up) {
    this.up = up;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    ArrowDto arrowDto = (ArrowDto) o;
    return Objects.equals(this.down, arrowDto.down) &&
        Objects.equals(this.left, arrowDto.left) &&
        Objects.equals(this.right, arrowDto.right) &&
        Objects.equals(this.up, arrowDto.up);
  }

  @Override
  public int hashCode() {
    return Objects.hash(down, left, right, up);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class ArrowDto {\n");
    sb.append("    down: ").append(toIndentedString(down)).append("\n");
    sb.append("    left: ").append(toIndentedString(left)).append("\n");
    sb.append("    right: ").append(toIndentedString(right)).append("\n");
    sb.append("    up: ").append(toIndentedString(up)).append("\n");
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

