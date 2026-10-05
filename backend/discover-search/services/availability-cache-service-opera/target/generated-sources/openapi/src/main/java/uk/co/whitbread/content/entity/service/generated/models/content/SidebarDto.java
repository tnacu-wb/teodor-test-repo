package uk.co.whitbread.content.entity.service.generated.models.content;

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
 * SidebarDto
 */

@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2026-08-25T07:49:41.570079+03:00[Europe/Bucharest]", comments = "Generator version: 7.12.0")
public class SidebarDto {

  private @Nullable String collapse;

  private @Nullable String expand;

  public SidebarDto collapse(String collapse) {
    this.collapse = collapse;
    return this;
  }

  /**
   * Get collapse
   * @return collapse
   */
  
  @Schema(name = "collapse", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("collapse")
  public String getCollapse() {
    return collapse;
  }

  public void setCollapse(String collapse) {
    this.collapse = collapse;
  }

  public SidebarDto expand(String expand) {
    this.expand = expand;
    return this;
  }

  /**
   * Get expand
   * @return expand
   */
  
  @Schema(name = "expand", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("expand")
  public String getExpand() {
    return expand;
  }

  public void setExpand(String expand) {
    this.expand = expand;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    SidebarDto sidebarDto = (SidebarDto) o;
    return Objects.equals(this.collapse, sidebarDto.collapse) &&
        Objects.equals(this.expand, sidebarDto.expand);
  }

  @Override
  public int hashCode() {
    return Objects.hash(collapse, expand);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class SidebarDto {\n");
    sb.append("    collapse: ").append(toIndentedString(collapse)).append("\n");
    sb.append("    expand: ").append(toIndentedString(expand)).append("\n");
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

