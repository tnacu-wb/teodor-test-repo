package uk.co.whitbread.hotel.ohip.adapter.generated.models;

import java.net.URI;
import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.springframework.lang.Nullable;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.AddressInfoTypeDto;
import org.openapitools.jackson.nullable.JsonNullable;
import java.time.OffsetDateTime;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import io.swagger.v3.oas.annotations.media.Schema;


import java.util.*;
import jakarta.annotation.Generated;

/**
 * ProfileTypeAddressesDto
 */

@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2026-08-27T18:50:34.031950+03:00[Europe/Bucharest]", comments = "Generator version: 7.12.0")
public class ProfileTypeAddressesDto {

  @Valid
  private @Nullable List<@Valid AddressInfoTypeDto> addressInfo;

  public ProfileTypeAddressesDto addressInfo(List<@Valid AddressInfoTypeDto> addressInfo) {
    this.addressInfo = addressInfo;
    return this;
  }

  public ProfileTypeAddressesDto addAddressInfoItem(AddressInfoTypeDto addressInfoItem) {
    if (this.addressInfo == null) {
      this.addressInfo = new ArrayList<>();
    }
    this.addressInfo.add(addressInfoItem);
    return this;
  }

  /**
   * Get addressInfo
   * @return addressInfo
   */
  @Valid 
  @Schema(name = "addressInfo", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("addressInfo")
  public List<@Valid AddressInfoTypeDto> getAddressInfo() {
    return addressInfo;
  }

  public void setAddressInfo(List<@Valid AddressInfoTypeDto> addressInfo) {
    this.addressInfo = addressInfo;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    ProfileTypeAddressesDto profileTypeAddressesDto = (ProfileTypeAddressesDto) o;
    return Objects.equals(this.addressInfo, profileTypeAddressesDto.addressInfo);
  }

  @Override
  public int hashCode() {
    return Objects.hash(addressInfo);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class ProfileTypeAddressesDto {\n");
    sb.append("    addressInfo: ").append(toIndentedString(addressInfo)).append("\n");
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

