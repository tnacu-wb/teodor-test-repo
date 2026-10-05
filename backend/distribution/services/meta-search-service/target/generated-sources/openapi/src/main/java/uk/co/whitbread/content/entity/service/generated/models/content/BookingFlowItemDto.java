package uk.co.whitbread.content.entity.service.generated.models.content;

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
 * BookingFlowItemDto
 */

@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2026-08-25T07:51:27.565654+03:00[Europe/Bucharest]", comments = "Generator version: 7.12.0")
public class BookingFlowItemDto {

  private @Nullable String bookingFlowPath;

  private @Nullable String bookingId;

  private @Nullable String rateCode;

  public BookingFlowItemDto bookingFlowPath(String bookingFlowPath) {
    this.bookingFlowPath = bookingFlowPath;
    return this;
  }

  /**
   * Get bookingFlowPath
   * @return bookingFlowPath
   */
  
  @Schema(name = "bookingFlowPath", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("bookingFlowPath")
  public String getBookingFlowPath() {
    return bookingFlowPath;
  }

  public void setBookingFlowPath(String bookingFlowPath) {
    this.bookingFlowPath = bookingFlowPath;
  }

  public BookingFlowItemDto bookingId(String bookingId) {
    this.bookingId = bookingId;
    return this;
  }

  /**
   * Get bookingId
   * @return bookingId
   */
  
  @Schema(name = "bookingId", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("bookingId")
  public String getBookingId() {
    return bookingId;
  }

  public void setBookingId(String bookingId) {
    this.bookingId = bookingId;
  }

  public BookingFlowItemDto rateCode(String rateCode) {
    this.rateCode = rateCode;
    return this;
  }

  /**
   * Get rateCode
   * @return rateCode
   */
  
  @Schema(name = "rateCode", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("rateCode")
  public String getRateCode() {
    return rateCode;
  }

  public void setRateCode(String rateCode) {
    this.rateCode = rateCode;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    BookingFlowItemDto bookingFlowItemDto = (BookingFlowItemDto) o;
    return Objects.equals(this.bookingFlowPath, bookingFlowItemDto.bookingFlowPath) &&
        Objects.equals(this.bookingId, bookingFlowItemDto.bookingId) &&
        Objects.equals(this.rateCode, bookingFlowItemDto.rateCode);
  }

  @Override
  public int hashCode() {
    return Objects.hash(bookingFlowPath, bookingId, rateCode);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class BookingFlowItemDto {\n");
    sb.append("    bookingFlowPath: ").append(toIndentedString(bookingFlowPath)).append("\n");
    sb.append("    bookingId: ").append(toIndentedString(bookingId)).append("\n");
    sb.append("    rateCode: ").append(toIndentedString(rateCode)).append("\n");
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

