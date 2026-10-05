package uk.co.whitbread.basket.generated.models.ohip;

import java.net.URI;
import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.springframework.lang.Nullable;
import uk.co.whitbread.basket.generated.models.ohip.PackageGroupsDto;
import org.openapitools.jackson.nullable.JsonNullable;
import java.time.OffsetDateTime;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import io.swagger.v3.oas.annotations.media.Schema;


import java.util.*;
import jakarta.annotation.Generated;

/**
 * PackagesGroupResponseDto
 */
@com.fasterxml.jackson.annotation.JsonIgnoreProperties(ignoreUnknown = true)

@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2026-09-04T08:10:10.951524+03:00[Europe/Bucharest]", comments = "Generator version: 7.12.0")
public class PackagesGroupResponseDto {

  @Valid
  private List<@Valid PackageGroupsDto> packagesGroup = new ArrayList<>();

  public PackagesGroupResponseDto packagesGroup(List<@Valid PackageGroupsDto> packagesGroup) {
    this.packagesGroup = packagesGroup;
    return this;
  }

  public PackagesGroupResponseDto addPackagesGroupItem(PackageGroupsDto packagesGroupItem) {
    if (this.packagesGroup == null) {
      this.packagesGroup = new ArrayList<>();
    }
    this.packagesGroup.add(packagesGroupItem);
    return this;
  }

  /**
   * Get packagesGroup
   * @return packagesGroup
   */
  @Valid 
  @Schema(name = "packagesGroup", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("packagesGroup")
  public List<@Valid PackageGroupsDto> getPackagesGroup() {
    return packagesGroup;
  }

  public void setPackagesGroup(List<@Valid PackageGroupsDto> packagesGroup) {
    this.packagesGroup = packagesGroup;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    PackagesGroupResponseDto packagesGroupResponseDto = (PackagesGroupResponseDto) o;
    return Objects.equals(this.packagesGroup, packagesGroupResponseDto.packagesGroup);
  }

  @Override
  public int hashCode() {
    return Objects.hash(packagesGroup);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class PackagesGroupResponseDto {\n");
    sb.append("    packagesGroup: ").append(toIndentedString(packagesGroup)).append("\n");
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

