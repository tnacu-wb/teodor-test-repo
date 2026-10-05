package uk.co.whitbread.basket.generated.models.marketing;

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
 * ContentPermission
 */
@com.fasterxml.jackson.annotation.JsonIgnoreProperties(ignoreUnknown = true)

@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2026-09-04T08:10:09.701357+03:00[Europe/Bucharest]", comments = "Generator version: 7.12.0")
public class ContentPermission {

  private @Nullable Boolean secondParty;

  private @Nullable Boolean thirdParty;

  public ContentPermission secondParty(Boolean secondParty) {
    this.secondParty = secondParty;
    return this;
  }

  /**
   * Get secondParty
   * @return secondParty
   */
  
  @Schema(name = "secondParty", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("secondParty")
  public Boolean getSecondParty() {
    return secondParty;
  }

  public void setSecondParty(Boolean secondParty) {
    this.secondParty = secondParty;
  }

  public ContentPermission thirdParty(Boolean thirdParty) {
    this.thirdParty = thirdParty;
    return this;
  }

  /**
   * Get thirdParty
   * @return thirdParty
   */
  
  @Schema(name = "thirdParty", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("thirdParty")
  public Boolean getThirdParty() {
    return thirdParty;
  }

  public void setThirdParty(Boolean thirdParty) {
    this.thirdParty = thirdParty;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    ContentPermission contentPermission = (ContentPermission) o;
    return Objects.equals(this.secondParty, contentPermission.secondParty) &&
        Objects.equals(this.thirdParty, contentPermission.thirdParty);
  }

  @Override
  public int hashCode() {
    return Objects.hash(secondParty, thirdParty);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class ContentPermission {\n");
    sb.append("    secondParty: ").append(toIndentedString(secondParty)).append("\n");
    sb.append("    thirdParty: ").append(toIndentedString(thirdParty)).append("\n");
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

