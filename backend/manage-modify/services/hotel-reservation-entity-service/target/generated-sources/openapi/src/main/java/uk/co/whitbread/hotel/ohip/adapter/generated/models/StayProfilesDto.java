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
import javax.validation.Valid;
import javax.validation.constraints.*;
import io.swagger.v3.oas.annotations.media.Schema;


import java.util.*;
import javax.annotation.Generated;

/**
 * StayProfilesDto
 */

@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2026-08-27T18:51:15.274731+03:00[Europe/Bucharest]", comments = "Generator version: 7.12.0")
public class StayProfilesDto {

  private @Nullable CheckInProfileDetailsDto profile;

  @Valid
  private List<@Valid CheckInProfileIdListDto> profileIdList = new ArrayList<>();

  private @Nullable String reservationProfileType;

  public StayProfilesDto profile(CheckInProfileDetailsDto profile) {
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

  public StayProfilesDto profileIdList(List<@Valid CheckInProfileIdListDto> profileIdList) {
    this.profileIdList = profileIdList;
    return this;
  }

  public StayProfilesDto addProfileIdListItem(CheckInProfileIdListDto profileIdListItem) {
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

  public StayProfilesDto reservationProfileType(String reservationProfileType) {
    this.reservationProfileType = reservationProfileType;
    return this;
  }

  /**
   * Get reservationProfileType
   * @return reservationProfileType
   */
  
  @Schema(name = "reservationProfileType", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("reservationProfileType")
  public String getReservationProfileType() {
    return reservationProfileType;
  }

  public void setReservationProfileType(String reservationProfileType) {
    this.reservationProfileType = reservationProfileType;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    StayProfilesDto stayProfilesDto = (StayProfilesDto) o;
    return Objects.equals(this.profile, stayProfilesDto.profile) &&
        Objects.equals(this.profileIdList, stayProfilesDto.profileIdList) &&
        Objects.equals(this.reservationProfileType, stayProfilesDto.reservationProfileType);
  }

  @Override
  public int hashCode() {
    return Objects.hash(profile, profileIdList, reservationProfileType);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class StayProfilesDto {\n");
    sb.append("    profile: ").append(toIndentedString(profile)).append("\n");
    sb.append("    profileIdList: ").append(toIndentedString(profileIdList)).append("\n");
    sb.append("    reservationProfileType: ").append(toIndentedString(reservationProfileType)).append("\n");
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

