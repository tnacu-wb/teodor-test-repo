package uk.co.whitbread.basket.generated.models.content;

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
 * LinksDto
 */
@com.fasterxml.jackson.annotation.JsonIgnoreProperties(ignoreUnknown = true)

@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2026-09-04T08:10:06.327224+03:00[Europe/Bucharest]", comments = "Generator version: 7.12.0")
public class LinksDto {

  private @Nullable String detailsPage;

  public LinksDto detailsPage(String detailsPage) {
    this.detailsPage = detailsPage;
    return this;
  }

  /**
   * Get detailsPage
   * @return detailsPage
   */
  
  @Schema(name = "detailsPage", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("detailsPage")
  public String getDetailsPage() {
    return detailsPage;
  }

  public void setDetailsPage(String detailsPage) {
    this.detailsPage = detailsPage;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    LinksDto linksDto = (LinksDto) o;
    return Objects.equals(this.detailsPage, linksDto.detailsPage);
  }

  @Override
  public int hashCode() {
    return Objects.hash(detailsPage);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class LinksDto {\n");
    sb.append("    detailsPage: ").append(toIndentedString(detailsPage)).append("\n");
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

