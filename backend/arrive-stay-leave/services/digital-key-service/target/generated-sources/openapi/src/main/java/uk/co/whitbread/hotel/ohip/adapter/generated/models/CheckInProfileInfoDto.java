package uk.co.whitbread.hotel.ohip.adapter.generated.models;

import java.net.URI;
import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.springframework.lang.Nullable;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.CheckInProfileDetailsDto;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.CheckInProfileIdListDto;
import org.openapitools.jackson.nullable.JsonNullable;
import java.time.OffsetDateTime;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import io.swagger.v3.oas.annotations.media.Schema;


import java.util.*;
import jakarta.annotation.Generated;

/**
 * CheckInProfileInfoDto
 */

@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2026-08-27T18:50:34.031950+03:00[Europe/Bucharest]", comments = "Generator version: 7.12.0")
public class CheckInProfileInfoDto {

  private @Nullable CheckInProfileDetailsDto profile;

  @Valid
  private @Nullable List<@Valid CheckInProfileIdListDto> profileIdList;

  public CheckInProfileInfoDto profile(CheckInProfileDetailsDto profile) {
    this.profile = profile;
    return this;
  }

  /**
   * Get profile
   * @return profile
   */
  @Valid 
  @Schema(name = "profile", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("profile")
  public CheckInProfileDetailsDto getProfile() {
    return profile;
  }

  public void setProfile(CheckInProfileDetailsDto profile) {
    this.profile = profile;
  }

  public CheckInProfileInfoDto profileIdList(List<@Valid CheckInProfileIdListDto> profileIdList) {
    this.profileIdList = profileIdList;
    return this;
  }

  public CheckInProfileInfoDto addProfileIdListItem(CheckInProfileIdListDto profileIdListItem) {
    if (this.profileIdList == null) {
      this.profileIdList = new ArrayList<>();
    }
    this.profileIdList.add(profileIdListItem);
    return this;
  }

  /**
   * Get profileIdList
   * @return profileIdList
   */
  @Valid 
  @Schema(name = "profileIdList", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("profileIdList")
  public List<@Valid CheckInProfileIdListDto> getProfileIdList() {
    return profileIdList;
  }

  public void setProfileIdList(List<@Valid CheckInProfileIdListDto> profileIdList) {
    this.profileIdList = profileIdList;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    CheckInProfileInfoDto checkInProfileInfoDto = (CheckInProfileInfoDto) o;
    return Objects.equals(this.profile, checkInProfileInfoDto.profile) &&
        Objects.equals(this.profileIdList, checkInProfileInfoDto.profileIdList);
  }

  @Override
  public int hashCode() {
    return Objects.hash(profile, profileIdList);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class CheckInProfileInfoDto {\n");
    sb.append("    profile: ").append(toIndentedString(profile)).append("\n");
    sb.append("    profileIdList: ").append(toIndentedString(profileIdList)).append("\n");
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

