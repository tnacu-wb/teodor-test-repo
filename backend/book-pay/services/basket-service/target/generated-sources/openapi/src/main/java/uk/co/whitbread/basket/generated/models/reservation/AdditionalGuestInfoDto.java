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
 * AdditionalGuestInfoDto
 */
@com.fasterxml.jackson.annotation.JsonIgnoreProperties(ignoreUnknown = true)

@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2026-09-04T08:10:03.993735+03:00[Europe/Bucharest]", comments = "Generator version: 7.12.0")
public class AdditionalGuestInfoDto {

  private @Nullable String purposeOfStay;

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
    return Objects.equals(this.purposeOfStay, additionalGuestInfoDto.purposeOfStay);
  }

  @Override
  public int hashCode() {
    return Objects.hash(purposeOfStay);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class AdditionalGuestInfoDto {\n");
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

