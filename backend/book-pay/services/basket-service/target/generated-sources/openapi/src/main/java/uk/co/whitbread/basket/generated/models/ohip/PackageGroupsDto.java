package uk.co.whitbread.basket.generated.models.ohip;

import java.net.URI;
import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.springframework.lang.Nullable;
import uk.co.whitbread.basket.generated.models.ohip.PackageCodesDto;
import org.openapitools.jackson.nullable.JsonNullable;
import java.time.OffsetDateTime;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import io.swagger.v3.oas.annotations.media.Schema;


import java.util.*;
import jakarta.annotation.Generated;

/**
 * PackageGroupsDto
 */
@com.fasterxml.jackson.annotation.JsonIgnoreProperties(ignoreUnknown = true)

@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2026-09-04T08:10:10.951524+03:00[Europe/Bucharest]", comments = "Generator version: 7.12.0")
public class PackageGroupsDto {

  @Valid
  private List<@Valid PackageCodesDto> packageCodes = new ArrayList<>();

  private @Nullable String packageGroup;

  private @Nullable String packageGroupDescription;

  public PackageGroupsDto packageCodes(List<@Valid PackageCodesDto> packageCodes) {
    this.packageCodes = packageCodes;
    return this;
  }

  public PackageGroupsDto addPackageCodesItem(PackageCodesDto packageCodesItem) {
    if (this.packageCodes == null) {
      this.packageCodes = new ArrayList<>();
    }
    this.packageCodes.add(packageCodesItem);
    return this;
  }

  /**
   * Get packageCodes
   * @return packageCodes
   */
  @Valid 
  @Schema(name = "packageCodes", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("packageCodes")
  public List<@Valid PackageCodesDto> getPackageCodes() {
    return packageCodes;
  }

  public void setPackageCodes(List<@Valid PackageCodesDto> packageCodes) {
    this.packageCodes = packageCodes;
  }

  public PackageGroupsDto packageGroup(String packageGroup) {
    this.packageGroup = packageGroup;
    return this;
  }

  /**
   * Get packageGroup
   * @return packageGroup
   */
  
  @Schema(name = "packageGroup", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("packageGroup")
  public String getPackageGroup() {
    return packageGroup;
  }

  public void setPackageGroup(String packageGroup) {
    this.packageGroup = packageGroup;
  }

  public PackageGroupsDto packageGroupDescription(String packageGroupDescription) {
    this.packageGroupDescription = packageGroupDescription;
    return this;
  }

  /**
   * Get packageGroupDescription
   * @return packageGroupDescription
   */
  
  @Schema(name = "packageGroupDescription", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("packageGroupDescription")
  public String getPackageGroupDescription() {
    return packageGroupDescription;
  }

  public void setPackageGroupDescription(String packageGroupDescription) {
    this.packageGroupDescription = packageGroupDescription;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    PackageGroupsDto packageGroupsDto = (PackageGroupsDto) o;
    return Objects.equals(this.packageCodes, packageGroupsDto.packageCodes) &&
        Objects.equals(this.packageGroup, packageGroupsDto.packageGroup) &&
        Objects.equals(this.packageGroupDescription, packageGroupsDto.packageGroupDescription);
  }

  @Override
  public int hashCode() {
    return Objects.hash(packageCodes, packageGroup, packageGroupDescription);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class PackageGroupsDto {\n");
    sb.append("    packageCodes: ").append(toIndentedString(packageCodes)).append("\n");
    sb.append("    packageGroup: ").append(toIndentedString(packageGroup)).append("\n");
    sb.append("    packageGroupDescription: ").append(toIndentedString(packageGroupDescription)).append("\n");
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

