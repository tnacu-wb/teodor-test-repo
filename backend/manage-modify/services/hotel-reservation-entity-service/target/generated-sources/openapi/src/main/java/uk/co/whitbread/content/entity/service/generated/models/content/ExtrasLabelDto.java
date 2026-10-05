package uk.co.whitbread.content.entity.service.generated.models.content;

import java.net.URI;
import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.springframework.lang.Nullable;
import uk.co.whitbread.content.entity.service.generated.models.content.ExtrasDto;
import org.openapitools.jackson.nullable.JsonNullable;
import java.time.OffsetDateTime;
import javax.validation.Valid;
import javax.validation.constraints.*;
import io.swagger.v3.oas.annotations.media.Schema;


import java.util.*;
import javax.annotation.Generated;

/**
 * ExtrasLabelDto
 */

@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2026-08-27T18:51:28.563902+03:00[Europe/Bucharest]", comments = "Generator version: 7.12.0")
public class ExtrasLabelDto {

  @Valid
  private List<@Valid ExtrasDto> extrasLabels = new ArrayList<>();

  public ExtrasLabelDto extrasLabels(List<@Valid ExtrasDto> extrasLabels) {
    this.extrasLabels = extrasLabels;
    return this;
  }

  public ExtrasLabelDto addExtrasLabelsItem(ExtrasDto extrasLabelsItem) {
    if (this.extrasLabels == null) {
      this.extrasLabels = new ArrayList<>();
    }
    this.extrasLabels.add(extrasLabelsItem);
    return this;
  }

  /**
   * Get extrasLabels
   * @return extrasLabels
   */
  @Valid 
  @Schema(name = "extrasLabels", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("extrasLabels")
  public List<@Valid ExtrasDto> getExtrasLabels() {
    return extrasLabels;
  }

  public void setExtrasLabels(List<@Valid ExtrasDto> extrasLabels) {
    this.extrasLabels = extrasLabels;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    ExtrasLabelDto extrasLabelDto = (ExtrasLabelDto) o;
    return Objects.equals(this.extrasLabels, extrasLabelDto.extrasLabels);
  }

  @Override
  public int hashCode() {
    return Objects.hash(extrasLabels);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class ExtrasLabelDto {\n");
    sb.append("    extrasLabels: ").append(toIndentedString(extrasLabels)).append("\n");
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

