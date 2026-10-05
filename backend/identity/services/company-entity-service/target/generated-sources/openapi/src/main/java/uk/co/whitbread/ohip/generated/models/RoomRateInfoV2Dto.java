package uk.co.whitbread.ohip.generated.models;

import java.net.URI;
import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.springframework.lang.Nullable;
import uk.co.whitbread.ohip.generated.models.PackageInfoDto;
import uk.co.whitbread.ohip.generated.models.PriceInfoDto;
import org.openapitools.jackson.nullable.JsonNullable;
import java.time.OffsetDateTime;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import io.swagger.v3.oas.annotations.media.Schema;


import java.util.*;
import jakarta.annotation.Generated;

/**
 * RoomRateInfoV2Dto
 */

@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2026-08-25T07:50:12.143180+03:00[Europe/Bucharest]", comments = "Generator version: 7.12.0")
public class RoomRateInfoV2Dto {

  @Valid
  private List<@Valid PackageInfoDto> packages = new ArrayList<>();

  @Valid
  private List<@Valid PriceInfoDto> priceInfo = new ArrayList<>();

  public RoomRateInfoV2Dto packages(List<@Valid PackageInfoDto> packages) {
    this.packages = packages;
    return this;
  }

  public RoomRateInfoV2Dto addPackagesItem(PackageInfoDto packagesItem) {
    if (this.packages == null) {
      this.packages = new ArrayList<>();
    }
    this.packages.add(packagesItem);
    return this;
  }

  /**
   * Get packages
   * @return packages
   */
  @Valid 
  @Schema(name = "packages", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("packages")
  public List<@Valid PackageInfoDto> getPackages() {
    return packages;
  }

  public void setPackages(List<@Valid PackageInfoDto> packages) {
    this.packages = packages;
  }

  public RoomRateInfoV2Dto priceInfo(List<@Valid PriceInfoDto> priceInfo) {
    this.priceInfo = priceInfo;
    return this;
  }

  public RoomRateInfoV2Dto addPriceInfoItem(PriceInfoDto priceInfoItem) {
    if (this.priceInfo == null) {
      this.priceInfo = new ArrayList<>();
    }
    this.priceInfo.add(priceInfoItem);
    return this;
  }

  /**
   * Get priceInfo
   * @return priceInfo
   */
  @Valid 
  @Schema(name = "priceInfo", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("priceInfo")
  public List<@Valid PriceInfoDto> getPriceInfo() {
    return priceInfo;
  }

  public void setPriceInfo(List<@Valid PriceInfoDto> priceInfo) {
    this.priceInfo = priceInfo;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    RoomRateInfoV2Dto roomRateInfoV2Dto = (RoomRateInfoV2Dto) o;
    return Objects.equals(this.packages, roomRateInfoV2Dto.packages) &&
        Objects.equals(this.priceInfo, roomRateInfoV2Dto.priceInfo);
  }

  @Override
  public int hashCode() {
    return Objects.hash(packages, priceInfo);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class RoomRateInfoV2Dto {\n");
    sb.append("    packages: ").append(toIndentedString(packages)).append("\n");
    sb.append("    priceInfo: ").append(toIndentedString(priceInfo)).append("\n");
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

