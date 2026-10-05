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
 * ChevronDto
 */

@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2026-08-27T18:51:28.563902+03:00[Europe/Bucharest]", comments = "Generator version: 7.12.0")
public class ChevronDto {

  private @Nullable String down;

  private @Nullable String downPurple;

  private @Nullable String left;

  private @Nullable String leftPurple;

  private @Nullable String right;

  private @Nullable String rightPurple;

  private @Nullable String up;

  private @Nullable String upPurple;

  public ChevronDto down(String down) {
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

  public ChevronDto downPurple(String downPurple) {
    this.downPurple = downPurple;
    return this;
  }

  /**
   * Get downPurple
   * @return downPurple
   */
  
  @Schema(name = "downPurple", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("downPurple")
  public String getDownPurple() {
    return downPurple;
  }

  public void setDownPurple(String downPurple) {
    this.downPurple = downPurple;
  }

  public ChevronDto left(String left) {
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

  public ChevronDto leftPurple(String leftPurple) {
    this.leftPurple = leftPurple;
    return this;
  }

  /**
   * Get leftPurple
   * @return leftPurple
   */
  
  @Schema(name = "leftPurple", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("leftPurple")
  public String getLeftPurple() {
    return leftPurple;
  }

  public void setLeftPurple(String leftPurple) {
    this.leftPurple = leftPurple;
  }

  public ChevronDto right(String right) {
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

  public ChevronDto rightPurple(String rightPurple) {
    this.rightPurple = rightPurple;
    return this;
  }

  /**
   * Get rightPurple
   * @return rightPurple
   */
  
  @Schema(name = "rightPurple", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("rightPurple")
  public String getRightPurple() {
    return rightPurple;
  }

  public void setRightPurple(String rightPurple) {
    this.rightPurple = rightPurple;
  }

  public ChevronDto up(String up) {
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

  public ChevronDto upPurple(String upPurple) {
    this.upPurple = upPurple;
    return this;
  }

  /**
   * Get upPurple
   * @return upPurple
   */
  
  @Schema(name = "upPurple", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("upPurple")
  public String getUpPurple() {
    return upPurple;
  }

  public void setUpPurple(String upPurple) {
    this.upPurple = upPurple;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    ChevronDto chevronDto = (ChevronDto) o;
    return Objects.equals(this.down, chevronDto.down) &&
        Objects.equals(this.downPurple, chevronDto.downPurple) &&
        Objects.equals(this.left, chevronDto.left) &&
        Objects.equals(this.leftPurple, chevronDto.leftPurple) &&
        Objects.equals(this.right, chevronDto.right) &&
        Objects.equals(this.rightPurple, chevronDto.rightPurple) &&
        Objects.equals(this.up, chevronDto.up) &&
        Objects.equals(this.upPurple, chevronDto.upPurple);
  }

  @Override
  public int hashCode() {
    return Objects.hash(down, downPurple, left, leftPurple, right, rightPurple, up, upPurple);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class ChevronDto {\n");
    sb.append("    down: ").append(toIndentedString(down)).append("\n");
    sb.append("    downPurple: ").append(toIndentedString(downPurple)).append("\n");
    sb.append("    left: ").append(toIndentedString(left)).append("\n");
    sb.append("    leftPurple: ").append(toIndentedString(leftPurple)).append("\n");
    sb.append("    right: ").append(toIndentedString(right)).append("\n");
    sb.append("    rightPurple: ").append(toIndentedString(rightPurple)).append("\n");
    sb.append("    up: ").append(toIndentedString(up)).append("\n");
    sb.append("    upPurple: ").append(toIndentedString(upPurple)).append("\n");
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

