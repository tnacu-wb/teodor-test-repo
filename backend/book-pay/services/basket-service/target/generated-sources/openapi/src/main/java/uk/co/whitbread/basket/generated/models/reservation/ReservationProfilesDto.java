package uk.co.whitbread.basket.generated.models.reservation;

import java.net.URI;
import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import org.springframework.lang.Nullable;
import org.openapitools.jackson.nullable.JsonNullable;
import java.time.OffsetDateTime;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import io.swagger.v3.oas.annotations.media.Schema;


import java.util.*;
import jakarta.annotation.Generated;

/**
 * ReservationProfilesDto
 */
@com.fasterxml.jackson.annotation.JsonIgnoreProperties(ignoreUnknown = true)

@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2026-09-04T08:10:03.993735+03:00[Europe/Bucharest]", comments = "Generator version: 7.12.0")
public class ReservationProfilesDto {

  private @Nullable String bookerProfileId;

  private @Nullable String companyProfileId;

  public ReservationProfilesDto bookerProfileId(String bookerProfileId) {
    this.bookerProfileId = bookerProfileId;
    return this;
  }

  /**
   * Get bookerProfileId
   * @return bookerProfileId
   */
  
  @Schema(name = "bookerProfileId", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("bookerProfileId")
  public String getBookerProfileId() {
    return bookerProfileId;
  }

  public void setBookerProfileId(String bookerProfileId) {
    this.bookerProfileId = bookerProfileId;
  }

  public ReservationProfilesDto companyProfileId(String companyProfileId) {
    this.companyProfileId = companyProfileId;
    return this;
  }

  /**
   * Get companyProfileId
   * @return companyProfileId
   */
  
  @Schema(name = "companyProfileId", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("companyProfileId")
  public String getCompanyProfileId() {
    return companyProfileId;
  }

  public void setCompanyProfileId(String companyProfileId) {
    this.companyProfileId = companyProfileId;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    ReservationProfilesDto reservationProfilesDto = (ReservationProfilesDto) o;
    return Objects.equals(this.bookerProfileId, reservationProfilesDto.bookerProfileId) &&
        Objects.equals(this.companyProfileId, reservationProfilesDto.companyProfileId);
  }

  @Override
  public int hashCode() {
    return Objects.hash(bookerProfileId, companyProfileId);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class ReservationProfilesDto {\n");
    sb.append("    bookerProfileId: ").append(toIndentedString(bookerProfileId)).append("\n");
    sb.append("    companyProfileId: ").append(toIndentedString(companyProfileId)).append("\n");
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

