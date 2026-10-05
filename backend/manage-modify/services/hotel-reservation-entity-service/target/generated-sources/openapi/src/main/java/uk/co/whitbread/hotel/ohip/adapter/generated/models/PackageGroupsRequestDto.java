package uk.co.whitbread.hotel.ohip.adapter.generated.models;

import java.net.URI;
import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import java.util.LinkedHashSet;
import java.util.Set;
import org.springframework.lang.Nullable;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.PackageCodesRequestDto;
import org.openapitools.jackson.nullable.JsonNullable;
import java.time.OffsetDateTime;
import javax.validation.Valid;
import javax.validation.constraints.*;
import io.swagger.v3.oas.annotations.media.Schema;


import java.util.*;
import javax.annotation.Generated;

/**
 * PackageGroupsRequestDto
 */

@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2026-08-27T18:51:15.274731+03:00[Europe/Bucharest]", comments = "Generator version: 7.12.0")
public class PackageGroupsRequestDto {

  private String hotelId;

  @Valid
  private Set<@Valid PackageCodesRequestDto> packageCodeList = new LinkedHashSet<>();

  @Valid
  private Set<String> packageGroupList = new LinkedHashSet<>();

  public PackageGroupsRequestDto() {
    super();
  }

  /**
   * Constructor with only required parameters
   */
  public PackageGroupsRequestDto(String hotelId) {
    this.hotelId = hotelId;
  }

  public PackageGroupsRequestDto hotelId(String hotelId) {
    this.hotelId = hotelId;
    return this;
  }

  /**
   * Get hotelId
   * @return hotelId
   */
  @NotNull 
  @Schema(name = "hotelId", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("hotelId")
  public String getHotelId() {
    return hotelId;
  }

  public void setHotelId(String hotelId) {
    this.hotelId = hotelId;
  }

  public PackageGroupsRequestDto packageCodeList(Set<@Valid PackageCodesRequestDto> packageCodeList) {
    this.packageCodeList = packageCodeList;
    return this;
  }

  public PackageGroupsRequestDto addPackageCodeListItem(PackageCodesRequestDto packageCodeListItem) {
    if (this.packageCodeList == null) {
      this.packageCodeList = new LinkedHashSet<>();
    }
    this.packageCodeList.add(packageCodeListItem);
    return this;
  }

  /**
   * Get packageCodeList
   * @return packageCodeList
   */
  @Valid 
  @Schema(name = "packageCodeList", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("packageCodeList")
  public Set<@Valid PackageCodesRequestDto> getPackageCodeList() {
    return packageCodeList;
  }

  @JsonDeserialize(as = LinkedHashSet.class)
  public void setPackageCodeList(Set<@Valid PackageCodesRequestDto> packageCodeList) {
    this.packageCodeList = packageCodeList;
  }

  public PackageGroupsRequestDto packageGroupList(Set<String> packageGroupList) {
    this.packageGroupList = packageGroupList;
    return this;
  }

  public PackageGroupsRequestDto addPackageGroupListItem(String packageGroupListItem) {
    if (this.packageGroupList == null) {
      this.packageGroupList = new LinkedHashSet<>();
    }
    this.packageGroupList.add(packageGroupListItem);
    return this;
  }

  /**
   * Get packageGroupList
   * @return packageGroupList
   */
  
  @Schema(name = "packageGroupList", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("packageGroupList")
  public Set<String> getPackageGroupList() {
    return packageGroupList;
  }

  @JsonDeserialize(as = LinkedHashSet.class)
  public void setPackageGroupList(Set<String> packageGroupList) {
    this.packageGroupList = packageGroupList;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    PackageGroupsRequestDto packageGroupsRequestDto = (PackageGroupsRequestDto) o;
    return Objects.equals(this.hotelId, packageGroupsRequestDto.hotelId) &&
        Objects.equals(this.packageCodeList, packageGroupsRequestDto.packageCodeList) &&
        Objects.equals(this.packageGroupList, packageGroupsRequestDto.packageGroupList);
  }

  @Override
  public int hashCode() {
    return Objects.hash(hotelId, packageCodeList, packageGroupList);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class PackageGroupsRequestDto {\n");
    sb.append("    hotelId: ").append(toIndentedString(hotelId)).append("\n");
    sb.append("    packageCodeList: ").append(toIndentedString(packageCodeList)).append("\n");
    sb.append("    packageGroupList: ").append(toIndentedString(packageGroupList)).append("\n");
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

