package uk.co.whitbread.hotel.ohip.adapter.generated.models;

import java.net.URI;
import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.springframework.lang.Nullable;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.NegotiatedProfileDto;
import org.openapitools.jackson.nullable.JsonNullable;
import java.time.OffsetDateTime;
import javax.validation.Valid;
import javax.validation.constraints.*;
import io.swagger.v3.oas.annotations.media.Schema;


import java.util.*;
import javax.annotation.Generated;

/**
 * NegotiatedRateDto
 */

@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2026-08-27T18:51:15.274731+03:00[Europe/Bucharest]", comments = "Generator version: 7.12.0")
public class NegotiatedRateDto {

  private @Nullable String hotelId;

  @Valid
  private List<@Valid NegotiatedProfileDto> negotiatedProfile = new ArrayList<>();

  private @Nullable String ratePlanCode;

  public NegotiatedRateDto hotelId(String hotelId) {
    this.hotelId = hotelId;
    return this;
  }

  /**
   * Get hotelId
   * @return hotelId
   */
  
  @Schema(name = "hotelId", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("hotelId")
  public String getHotelId() {
    return hotelId;
  }

  public void setHotelId(String hotelId) {
    this.hotelId = hotelId;
  }

  public NegotiatedRateDto negotiatedProfile(List<@Valid NegotiatedProfileDto> negotiatedProfile) {
    this.negotiatedProfile = negotiatedProfile;
    return this;
  }

  public NegotiatedRateDto addNegotiatedProfileItem(NegotiatedProfileDto negotiatedProfileItem) {
    if (this.negotiatedProfile == null) {
      this.negotiatedProfile = new ArrayList<>();
    }
    this.negotiatedProfile.add(negotiatedProfileItem);
    return this;
  }

  /**
   * Get negotiatedProfile
   * @return negotiatedProfile
   */
  @Valid 
  @Schema(name = "negotiatedProfile", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("negotiatedProfile")
  public List<@Valid NegotiatedProfileDto> getNegotiatedProfile() {
    return negotiatedProfile;
  }

  public void setNegotiatedProfile(List<@Valid NegotiatedProfileDto> negotiatedProfile) {
    this.negotiatedProfile = negotiatedProfile;
  }

  public NegotiatedRateDto ratePlanCode(String ratePlanCode) {
    this.ratePlanCode = ratePlanCode;
    return this;
  }

  /**
   * Get ratePlanCode
   * @return ratePlanCode
   */
  
  @Schema(name = "ratePlanCode", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("ratePlanCode")
  public String getRatePlanCode() {
    return ratePlanCode;
  }

  public void setRatePlanCode(String ratePlanCode) {
    this.ratePlanCode = ratePlanCode;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    NegotiatedRateDto negotiatedRateDto = (NegotiatedRateDto) o;
    return Objects.equals(this.hotelId, negotiatedRateDto.hotelId) &&
        Objects.equals(this.negotiatedProfile, negotiatedRateDto.negotiatedProfile) &&
        Objects.equals(this.ratePlanCode, negotiatedRateDto.ratePlanCode);
  }

  @Override
  public int hashCode() {
    return Objects.hash(hotelId, negotiatedProfile, ratePlanCode);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class NegotiatedRateDto {\n");
    sb.append("    hotelId: ").append(toIndentedString(hotelId)).append("\n");
    sb.append("    negotiatedProfile: ").append(toIndentedString(negotiatedProfile)).append("\n");
    sb.append("    ratePlanCode: ").append(toIndentedString(ratePlanCode)).append("\n");
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

