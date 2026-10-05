package uk.co.whitbread.ohip.generated.models;

import java.net.URI;
import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import org.springframework.lang.Nullable;
import uk.co.whitbread.ohip.generated.models.CheckInProfileInfoDto;
import org.openapitools.jackson.nullable.JsonNullable;
import java.time.OffsetDateTime;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import io.swagger.v3.oas.annotations.media.Schema;


import java.util.*;
import jakarta.annotation.Generated;

/**
 * CheckInReservationGuestsDto
 */

@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2026-08-25T07:50:12.143180+03:00[Europe/Bucharest]", comments = "Generator version: 7.12.0")
public class CheckInReservationGuestsDto {

  private @Nullable CheckInProfileInfoDto profileInfo;

  public CheckInReservationGuestsDto profileInfo(CheckInProfileInfoDto profileInfo) {
    this.profileInfo = profileInfo;
    return this;
  }

  /**
   * Get profileInfo
   * @return profileInfo
   */
  @Valid 
  @Schema(name = "profileInfo", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("profileInfo")
  public CheckInProfileInfoDto getProfileInfo() {
    return profileInfo;
  }

  public void setProfileInfo(CheckInProfileInfoDto profileInfo) {
    this.profileInfo = profileInfo;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    CheckInReservationGuestsDto checkInReservationGuestsDto = (CheckInReservationGuestsDto) o;
    return Objects.equals(this.profileInfo, checkInReservationGuestsDto.profileInfo);
  }

  @Override
  public int hashCode() {
    return Objects.hash(profileInfo);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class CheckInReservationGuestsDto {\n");
    sb.append("    profileInfo: ").append(toIndentedString(profileInfo)).append("\n");
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

