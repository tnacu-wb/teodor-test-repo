package uk.co.whitbread.content.entity.service.generated.models.content;

import java.net.URI;
import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.springframework.lang.Nullable;
import uk.co.whitbread.content.entity.service.generated.models.content.SubMenuLinkDto;
import org.openapitools.jackson.nullable.JsonNullable;
import java.time.OffsetDateTime;
import javax.validation.Valid;
import javax.validation.constraints.*;
import io.swagger.v3.oas.annotations.media.Schema;


import java.util.*;
import javax.annotation.Generated;

/**
 * BusinessAccountLinkDto
 */

@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2026-08-27T18:51:28.563902+03:00[Europe/Bucharest]", comments = "Generator version: 7.12.0")
public class BusinessAccountLinkDto {

  @Valid
  private List<@Valid SubMenuLinkDto> subMenuLinks = new ArrayList<>();

  private @Nullable String title;

  public BusinessAccountLinkDto subMenuLinks(List<@Valid SubMenuLinkDto> subMenuLinks) {
    this.subMenuLinks = subMenuLinks;
    return this;
  }

  public BusinessAccountLinkDto addSubMenuLinksItem(SubMenuLinkDto subMenuLinksItem) {
    if (this.subMenuLinks == null) {
      this.subMenuLinks = new ArrayList<>();
    }
    this.subMenuLinks.add(subMenuLinksItem);
    return this;
  }

  /**
   * Get subMenuLinks
   * @return subMenuLinks
   */
  @Valid 
  @Schema(name = "subMenuLinks", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("subMenuLinks")
  public List<@Valid SubMenuLinkDto> getSubMenuLinks() {
    return subMenuLinks;
  }

  public void setSubMenuLinks(List<@Valid SubMenuLinkDto> subMenuLinks) {
    this.subMenuLinks = subMenuLinks;
  }

  public BusinessAccountLinkDto title(String title) {
    this.title = title;
    return this;
  }

  /**
   * Get title
   * @return title
   */
  
  @Schema(name = "title", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("title")
  public String getTitle() {
    return title;
  }

  public void setTitle(String title) {
    this.title = title;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    BusinessAccountLinkDto businessAccountLinkDto = (BusinessAccountLinkDto) o;
    return Objects.equals(this.subMenuLinks, businessAccountLinkDto.subMenuLinks) &&
        Objects.equals(this.title, businessAccountLinkDto.title);
  }

  @Override
  public int hashCode() {
    return Objects.hash(subMenuLinks, title);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class BusinessAccountLinkDto {\n");
    sb.append("    subMenuLinks: ").append(toIndentedString(subMenuLinks)).append("\n");
    sb.append("    title: ").append(toIndentedString(title)).append("\n");
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

