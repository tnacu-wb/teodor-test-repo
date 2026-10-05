package uk.co.whitbread.ohip.generated.models;

import java.net.URI;
import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.springframework.lang.Nullable;
import uk.co.whitbread.ohip.generated.models.ProfileIdListDto;
import uk.co.whitbread.ohip.generated.models.ProfileNameDto;
import uk.co.whitbread.ohip.generated.models.RateInfoListDto;
import org.openapitools.jackson.nullable.JsonNullable;
import java.time.OffsetDateTime;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import io.swagger.v3.oas.annotations.media.Schema;


import java.util.*;
import jakarta.annotation.Generated;

/**
 * NegotiatedProfileDto
 */

@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2026-08-25T07:50:12.143180+03:00[Europe/Bucharest]", comments = "Generator version: 7.12.0")
public class NegotiatedProfileDto {

  @Valid
  private List<@Valid ProfileIdListDto> profileIdList = new ArrayList<>();

  private @Nullable ProfileNameDto profileName;

  private @Nullable String profileType;

  @Valid
  private List<@Valid RateInfoListDto> rateInfoList = new ArrayList<>();

  public NegotiatedProfileDto profileIdList(List<@Valid ProfileIdListDto> profileIdList) {
    this.profileIdList = profileIdList;
    return this;
  }

  public NegotiatedProfileDto addProfileIdListItem(ProfileIdListDto profileIdListItem) {
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
  public List<@Valid ProfileIdListDto> getProfileIdList() {
    return profileIdList;
  }

  public void setProfileIdList(List<@Valid ProfileIdListDto> profileIdList) {
    this.profileIdList = profileIdList;
  }

  public NegotiatedProfileDto profileName(ProfileNameDto profileName) {
    this.profileName = profileName;
    return this;
  }

  /**
   * Get profileName
   * @return profileName
   */
  @Valid 
  @Schema(name = "profileName", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("profileName")
  public ProfileNameDto getProfileName() {
    return profileName;
  }

  public void setProfileName(ProfileNameDto profileName) {
    this.profileName = profileName;
  }

  public NegotiatedProfileDto profileType(String profileType) {
    this.profileType = profileType;
    return this;
  }

  /**
   * Get profileType
   * @return profileType
   */
  
  @Schema(name = "profileType", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("profileType")
  public String getProfileType() {
    return profileType;
  }

  public void setProfileType(String profileType) {
    this.profileType = profileType;
  }

  public NegotiatedProfileDto rateInfoList(List<@Valid RateInfoListDto> rateInfoList) {
    this.rateInfoList = rateInfoList;
    return this;
  }

  public NegotiatedProfileDto addRateInfoListItem(RateInfoListDto rateInfoListItem) {
    if (this.rateInfoList == null) {
      this.rateInfoList = new ArrayList<>();
    }
    this.rateInfoList.add(rateInfoListItem);
    return this;
  }

  /**
   * Get rateInfoList
   * @return rateInfoList
   */
  @Valid 
  @Schema(name = "rateInfoList", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("rateInfoList")
  public List<@Valid RateInfoListDto> getRateInfoList() {
    return rateInfoList;
  }

  public void setRateInfoList(List<@Valid RateInfoListDto> rateInfoList) {
    this.rateInfoList = rateInfoList;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    NegotiatedProfileDto negotiatedProfileDto = (NegotiatedProfileDto) o;
    return Objects.equals(this.profileIdList, negotiatedProfileDto.profileIdList) &&
        Objects.equals(this.profileName, negotiatedProfileDto.profileName) &&
        Objects.equals(this.profileType, negotiatedProfileDto.profileType) &&
        Objects.equals(this.rateInfoList, negotiatedProfileDto.rateInfoList);
  }

  @Override
  public int hashCode() {
    return Objects.hash(profileIdList, profileName, profileType, rateInfoList);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class NegotiatedProfileDto {\n");
    sb.append("    profileIdList: ").append(toIndentedString(profileIdList)).append("\n");
    sb.append("    profileName: ").append(toIndentedString(profileName)).append("\n");
    sb.append("    profileType: ").append(toIndentedString(profileType)).append("\n");
    sb.append("    rateInfoList: ").append(toIndentedString(rateInfoList)).append("\n");
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

