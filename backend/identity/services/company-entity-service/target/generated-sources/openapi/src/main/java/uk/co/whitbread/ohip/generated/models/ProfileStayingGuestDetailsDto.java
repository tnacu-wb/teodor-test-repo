package uk.co.whitbread.ohip.generated.models;

import java.net.URI;
import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.springframework.lang.Nullable;
import uk.co.whitbread.ohip.generated.models.GuestDetailsDto;
import org.openapitools.jackson.nullable.JsonNullable;
import java.time.OffsetDateTime;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import io.swagger.v3.oas.annotations.media.Schema;


import java.util.*;
import jakarta.annotation.Generated;

/**
 * ProfileStayingGuestDetailsDto
 */

@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2026-08-25T07:50:12.143180+03:00[Europe/Bucharest]", comments = "Generator version: 7.12.0")
public class ProfileStayingGuestDetailsDto {

  @Valid
  private List<@Valid GuestDetailsDto> guestDetails = new ArrayList<>();

  public ProfileStayingGuestDetailsDto guestDetails(List<@Valid GuestDetailsDto> guestDetails) {
    this.guestDetails = guestDetails;
    return this;
  }

  public ProfileStayingGuestDetailsDto addGuestDetailsItem(GuestDetailsDto guestDetailsItem) {
    if (this.guestDetails == null) {
      this.guestDetails = new ArrayList<>();
    }
    this.guestDetails.add(guestDetailsItem);
    return this;
  }

  /**
   * Get guestDetails
   * @return guestDetails
   */
  @Valid 
  @Schema(name = "guestDetails", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("guestDetails")
  public List<@Valid GuestDetailsDto> getGuestDetails() {
    return guestDetails;
  }

  public void setGuestDetails(List<@Valid GuestDetailsDto> guestDetails) {
    this.guestDetails = guestDetails;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    ProfileStayingGuestDetailsDto profileStayingGuestDetailsDto = (ProfileStayingGuestDetailsDto) o;
    return Objects.equals(this.guestDetails, profileStayingGuestDetailsDto.guestDetails);
  }

  @Override
  public int hashCode() {
    return Objects.hash(guestDetails);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class ProfileStayingGuestDetailsDto {\n");
    sb.append("    guestDetails: ").append(toIndentedString(guestDetails)).append("\n");
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

