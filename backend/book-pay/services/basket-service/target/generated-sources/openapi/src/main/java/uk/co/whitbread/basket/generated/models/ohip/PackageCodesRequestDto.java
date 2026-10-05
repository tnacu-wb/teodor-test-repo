package uk.co.whitbread.basket.generated.models.ohip;

import java.net.URI;
import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import java.util.LinkedHashSet;
import java.util.Set;
import org.springframework.lang.Nullable;
import org.openapitools.jackson.nullable.JsonNullable;
import java.time.OffsetDateTime;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import io.swagger.v3.oas.annotations.media.Schema;


import java.util.*;
import jakarta.annotation.Generated;

/**
 * PackageCodesRequestDto
 */
@com.fasterxml.jackson.annotation.JsonIgnoreProperties(ignoreUnknown = true)

@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2026-09-04T08:10:10.951524+03:00[Europe/Bucharest]", comments = "Generator version: 7.12.0")
public class PackageCodesRequestDto {

  @Valid
  private Set<String> packageCodes = new LinkedHashSet<>();

  public PackageCodesRequestDto packageCodes(Set<String> packageCodes) {
    this.packageCodes = packageCodes;
    return this;
  }

  public PackageCodesRequestDto addPackageCodesItem(String packageCodesItem) {
    if (this.packageCodes == null) {
      this.packageCodes = new LinkedHashSet<>();
    }
    this.packageCodes.add(packageCodesItem);
    return this;
  }

  /**
   * Get packageCodes
   * @return packageCodes
   */
  
  @Schema(name = "packageCodes", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("packageCodes")
  public Set<String> getPackageCodes() {
    return packageCodes;
  }

  @JsonDeserialize(as = LinkedHashSet.class)
  public void setPackageCodes(Set<String> packageCodes) {
    this.packageCodes = packageCodes;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    PackageCodesRequestDto packageCodesRequestDto = (PackageCodesRequestDto) o;
    return Objects.equals(this.packageCodes, packageCodesRequestDto.packageCodes);
  }

  @Override
  public int hashCode() {
    return Objects.hash(packageCodes);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class PackageCodesRequestDto {\n");
    sb.append("    packageCodes: ").append(toIndentedString(packageCodes)).append("\n");
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

