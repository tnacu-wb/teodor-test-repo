package uk.co.whitbread.hotel.ohip.adapter.generated.models;

import java.net.URI;
import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import org.springframework.lang.Nullable;
import org.openapitools.jackson.nullable.JsonNullable;
import java.time.OffsetDateTime;
import javax.validation.Valid;
import javax.validation.constraints.*;
import io.swagger.v3.oas.annotations.media.Schema;


import java.util.*;
import javax.annotation.Generated;

/**
 * AdditionalGuestInfoDto
 */

@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2026-08-27T18:51:15.274731+03:00[Europe/Bucharest]", comments = "Generator version: 7.12.0")
public class AdditionalGuestInfoDto {

  private @Nullable Boolean acceptFutureMailing;

  private @Nullable String purposeOfStay;

  public AdditionalGuestInfoDto acceptFutureMailing(Boolean acceptFutureMailing) {
    this.acceptFutureMailing = acceptFutureMailing;
    return this;
  }

  /**
   * Get acceptFutureMailing
   * @return acceptFutureMailing
   */
  
  @Schema(name = "acceptFutureMailing", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("acceptFutureMailing")
  public Boolean getAcceptFutureMailing() {
    return acceptFutureMailing;
  }

  public void setAcceptFutureMailing(Boolean acceptFutureMailing) {
    this.acceptFutureMailing = acceptFutureMailing;
  }

  public AdditionalGuestInfoDto purposeOfStay(String purposeOfStay) {
    this.purposeOfStay = purposeOfStay;
    return this;
  }

  /**
   * Get purposeOfStay
   * @return purposeOfStay
   */
  
  @Schema(name = "purposeOfStay", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("purposeOfStay")
  public String getPurposeOfStay() {
    return purposeOfStay;
  }

  public void setPurposeOfStay(String purposeOfStay) {
    this.purposeOfStay = purposeOfStay;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    AdditionalGuestInfoDto additionalGuestInfoDto = (AdditionalGuestInfoDto) o;
    return Objects.equals(this.acceptFutureMailing, additionalGuestInfoDto.acceptFutureMailing) &&
        Objects.equals(this.purposeOfStay, additionalGuestInfoDto.purposeOfStay);
  }

  @Override
  public int hashCode() {
    return Objects.hash(acceptFutureMailing, purposeOfStay);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class AdditionalGuestInfoDto {\n");
    sb.append("    acceptFutureMailing: ").append(toIndentedString(acceptFutureMailing)).append("\n");
    sb.append("    purposeOfStay: ").append(toIndentedString(purposeOfStay)).append("\n");
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

