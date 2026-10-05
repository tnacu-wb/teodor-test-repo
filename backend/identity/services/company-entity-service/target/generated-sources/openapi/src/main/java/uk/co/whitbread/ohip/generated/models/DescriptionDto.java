package uk.co.whitbread.ohip.generated.models;

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
 * DescriptionDto
 */

@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2026-08-25T07:50:12.143180+03:00[Europe/Bucharest]", comments = "Generator version: 7.12.0")
public class DescriptionDto {

  private @Nullable String defaultText;

  public DescriptionDto defaultText(String defaultText) {
    this.defaultText = defaultText;
    return this;
  }

  /**
   * Get defaultText
   * @return defaultText
   */
  
  @Schema(name = "defaultText", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("defaultText")
  public String getDefaultText() {
    return defaultText;
  }

  public void setDefaultText(String defaultText) {
    this.defaultText = defaultText;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    DescriptionDto descriptionDto = (DescriptionDto) o;
    return Objects.equals(this.defaultText, descriptionDto.defaultText);
  }

  @Override
  public int hashCode() {
    return Objects.hash(defaultText);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class DescriptionDto {\n");
    sb.append("    defaultText: ").append(toIndentedString(defaultText)).append("\n");
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

