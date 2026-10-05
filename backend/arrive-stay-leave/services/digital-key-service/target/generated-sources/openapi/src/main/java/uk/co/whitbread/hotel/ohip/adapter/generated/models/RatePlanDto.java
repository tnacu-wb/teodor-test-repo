package uk.co.whitbread.hotel.ohip.adapter.generated.models;

import java.net.URI;
import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import org.springframework.lang.Nullable;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.ClassificationsDto;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.PrimaryDetailsDto;
import org.openapitools.jackson.nullable.JsonNullable;
import java.time.OffsetDateTime;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import io.swagger.v3.oas.annotations.media.Schema;


import java.util.*;
import jakarta.annotation.Generated;

/**
 * RatePlanDto
 */

@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2026-08-27T18:50:34.031950+03:00[Europe/Bucharest]", comments = "Generator version: 7.12.0")
public class RatePlanDto {

  private @Nullable ClassificationsDto classifications;

  private @Nullable String hotelId;

  private @Nullable PrimaryDetailsDto primaryDetails;

  private @Nullable String ratePlanCode;

  public RatePlanDto classifications(ClassificationsDto classifications) {
    this.classifications = classifications;
    return this;
  }

  /**
   * Get classifications
   * @return classifications
   */
  @Valid 
  @Schema(name = "classifications", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("classifications")
  public ClassificationsDto getClassifications() {
    return classifications;
  }

  public void setClassifications(ClassificationsDto classifications) {
    this.classifications = classifications;
  }

  public RatePlanDto hotelId(String hotelId) {
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

  public RatePlanDto primaryDetails(PrimaryDetailsDto primaryDetails) {
    this.primaryDetails = primaryDetails;
    return this;
  }

  /**
   * Get primaryDetails
   * @return primaryDetails
   */
  @Valid 
  @Schema(name = "primaryDetails", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("primaryDetails")
  public PrimaryDetailsDto getPrimaryDetails() {
    return primaryDetails;
  }

  public void setPrimaryDetails(PrimaryDetailsDto primaryDetails) {
    this.primaryDetails = primaryDetails;
  }

  public RatePlanDto ratePlanCode(String ratePlanCode) {
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
    RatePlanDto ratePlanDto = (RatePlanDto) o;
    return Objects.equals(this.classifications, ratePlanDto.classifications) &&
        Objects.equals(this.hotelId, ratePlanDto.hotelId) &&
        Objects.equals(this.primaryDetails, ratePlanDto.primaryDetails) &&
        Objects.equals(this.ratePlanCode, ratePlanDto.ratePlanCode);
  }

  @Override
  public int hashCode() {
    return Objects.hash(classifications, hotelId, primaryDetails, ratePlanCode);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class RatePlanDto {\n");
    sb.append("    classifications: ").append(toIndentedString(classifications)).append("\n");
    sb.append("    hotelId: ").append(toIndentedString(hotelId)).append("\n");
    sb.append("    primaryDetails: ").append(toIndentedString(primaryDetails)).append("\n");
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

