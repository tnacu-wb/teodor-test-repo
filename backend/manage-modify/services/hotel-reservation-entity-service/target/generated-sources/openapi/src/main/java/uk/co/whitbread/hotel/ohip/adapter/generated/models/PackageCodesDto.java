package uk.co.whitbread.hotel.ohip.adapter.generated.models;

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
 * PackageCodesDto
 */

@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2026-08-27T18:51:15.274731+03:00[Europe/Bucharest]", comments = "Generator version: 7.12.0")
public class PackageCodesDto {

  private @Nullable String packageCode;

  private @Nullable String packageDescription;

  public PackageCodesDto packageCode(String packageCode) {
    this.packageCode = packageCode;
    return this;
  }

  /**
   * Get packageCode
   * @return packageCode
   */
  
  @Schema(name = "packageCode", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("packageCode")
  public String getPackageCode() {
    return packageCode;
  }

  public void setPackageCode(String packageCode) {
    this.packageCode = packageCode;
  }

  public PackageCodesDto packageDescription(String packageDescription) {
    this.packageDescription = packageDescription;
    return this;
  }

  /**
   * Get packageDescription
   * @return packageDescription
   */
  
  @Schema(name = "packageDescription", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("packageDescription")
  public String getPackageDescription() {
    return packageDescription;
  }

  public void setPackageDescription(String packageDescription) {
    this.packageDescription = packageDescription;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    PackageCodesDto packageCodesDto = (PackageCodesDto) o;
    return Objects.equals(this.packageCode, packageCodesDto.packageCode) &&
        Objects.equals(this.packageDescription, packageCodesDto.packageDescription);
  }

  @Override
  public int hashCode() {
    return Objects.hash(packageCode, packageDescription);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class PackageCodesDto {\n");
    sb.append("    packageCode: ").append(toIndentedString(packageCode)).append("\n");
    sb.append("    packageDescription: ").append(toIndentedString(packageDescription)).append("\n");
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

