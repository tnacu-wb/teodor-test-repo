package uk.co.whitbread.content.entity.service.generated.models.content;

import java.net.URI;
import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import org.springframework.lang.Nullable;
import uk.co.whitbread.content.entity.service.generated.models.content.CookiePoliciesDto;
import org.openapitools.jackson.nullable.JsonNullable;
import java.time.OffsetDateTime;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import io.swagger.v3.oas.annotations.media.Schema;


import java.util.*;
import jakarta.annotation.Generated;

/**
 * CookiePoliciesInformationDto
 */

@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2026-08-25T07:51:27.565654+03:00[Europe/Bucharest]", comments = "Generator version: 7.12.0")
public class CookiePoliciesInformationDto {

  private @Nullable CookiePoliciesDto cookiePolicies;

  public CookiePoliciesInformationDto cookiePolicies(CookiePoliciesDto cookiePolicies) {
    this.cookiePolicies = cookiePolicies;
    return this;
  }

  /**
   * Get cookiePolicies
   * @return cookiePolicies
   */
  @Valid 
  @Schema(name = "cookiePolicies", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("cookiePolicies")
  public CookiePoliciesDto getCookiePolicies() {
    return cookiePolicies;
  }

  public void setCookiePolicies(CookiePoliciesDto cookiePolicies) {
    this.cookiePolicies = cookiePolicies;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    CookiePoliciesInformationDto cookiePoliciesInformationDto = (CookiePoliciesInformationDto) o;
    return Objects.equals(this.cookiePolicies, cookiePoliciesInformationDto.cookiePolicies);
  }

  @Override
  public int hashCode() {
    return Objects.hash(cookiePolicies);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class CookiePoliciesInformationDto {\n");
    sb.append("    cookiePolicies: ").append(toIndentedString(cookiePolicies)).append("\n");
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

