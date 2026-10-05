package uk.co.whitbread.hotel.reservation.generated.models;

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
 * CopyBookingResponseDto
 */

@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2026-09-04T08:09:52.163805+03:00[Europe/Bucharest]", comments = "Generator version: 7.12.0")
public class CopyBookingResponseDto {

  private @Nullable String copyBasketReference;

  public CopyBookingResponseDto copyBasketReference(String copyBasketReference) {
    this.copyBasketReference = copyBasketReference;
    return this;
  }

  /**
   * Get copyBasketReference
   * @return copyBasketReference
   */
  
  @Schema(name = "copyBasketReference", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("copyBasketReference")
  public String getCopyBasketReference() {
    return copyBasketReference;
  }

  public void setCopyBasketReference(String copyBasketReference) {
    this.copyBasketReference = copyBasketReference;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    CopyBookingResponseDto copyBookingResponseDto = (CopyBookingResponseDto) o;
    return Objects.equals(this.copyBasketReference, copyBookingResponseDto.copyBasketReference);
  }

  @Override
  public int hashCode() {
    return Objects.hash(copyBasketReference);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class CopyBookingResponseDto {\n");
    sb.append("    copyBasketReference: ").append(toIndentedString(copyBasketReference)).append("\n");
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

