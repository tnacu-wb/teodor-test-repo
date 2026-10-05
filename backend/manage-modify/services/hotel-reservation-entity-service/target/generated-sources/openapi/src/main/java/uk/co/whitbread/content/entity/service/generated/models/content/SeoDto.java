package uk.co.whitbread.content.entity.service.generated.models.content;

import java.net.URI;
import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.springframework.lang.Nullable;
import uk.co.whitbread.content.entity.service.generated.models.content.HreflangDto;
import org.openapitools.jackson.nullable.JsonNullable;
import java.time.OffsetDateTime;
import javax.validation.Valid;
import javax.validation.constraints.*;
import io.swagger.v3.oas.annotations.media.Schema;


import java.util.*;
import javax.annotation.Generated;

/**
 * SeoDto
 */

@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2026-08-27T18:51:28.563902+03:00[Europe/Bucharest]", comments = "Generator version: 7.12.0")
public class SeoDto {

  @Valid
  private List<@Valid HreflangDto> hreflangs = new ArrayList<>();

  private @Nullable String pageDescription;

  private @Nullable String pageTitle;

  public SeoDto hreflangs(List<@Valid HreflangDto> hreflangs) {
    this.hreflangs = hreflangs;
    return this;
  }

  public SeoDto addHreflangsItem(HreflangDto hreflangsItem) {
    if (this.hreflangs == null) {
      this.hreflangs = new ArrayList<>();
    }
    this.hreflangs.add(hreflangsItem);
    return this;
  }

  /**
   * Get hreflangs
   * @return hreflangs
   */
  @Valid 
  @Schema(name = "hreflangs", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("hreflangs")
  public List<@Valid HreflangDto> getHreflangs() {
    return hreflangs;
  }

  public void setHreflangs(List<@Valid HreflangDto> hreflangs) {
    this.hreflangs = hreflangs;
  }

  public SeoDto pageDescription(String pageDescription) {
    this.pageDescription = pageDescription;
    return this;
  }

  /**
   * Get pageDescription
   * @return pageDescription
   */
  
  @Schema(name = "pageDescription", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("pageDescription")
  public String getPageDescription() {
    return pageDescription;
  }

  public void setPageDescription(String pageDescription) {
    this.pageDescription = pageDescription;
  }

  public SeoDto pageTitle(String pageTitle) {
    this.pageTitle = pageTitle;
    return this;
  }

  /**
   * Get pageTitle
   * @return pageTitle
   */
  
  @Schema(name = "pageTitle", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("pageTitle")
  public String getPageTitle() {
    return pageTitle;
  }

  public void setPageTitle(String pageTitle) {
    this.pageTitle = pageTitle;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    SeoDto seoDto = (SeoDto) o;
    return Objects.equals(this.hreflangs, seoDto.hreflangs) &&
        Objects.equals(this.pageDescription, seoDto.pageDescription) &&
        Objects.equals(this.pageTitle, seoDto.pageTitle);
  }

  @Override
  public int hashCode() {
    return Objects.hash(hreflangs, pageDescription, pageTitle);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class SeoDto {\n");
    sb.append("    hreflangs: ").append(toIndentedString(hreflangs)).append("\n");
    sb.append("    pageDescription: ").append(toIndentedString(pageDescription)).append("\n");
    sb.append("    pageTitle: ").append(toIndentedString(pageTitle)).append("\n");
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

