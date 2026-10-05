package uk.co.whitbread.content.entity.service.generated.models.content;

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
 * BookingFlowItemDto
 */

@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2026-08-27T18:51:28.563902+03:00[Europe/Bucharest]", comments = "Generator version: 7.12.0")
public class BookingFlowItemDto {

  private @Nullable String bookingFlowPath;

  private @Nullable String bookingId;

  private @Nullable String bookingIdBB;

  private @Nullable String rateCategory;

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

  public BookingFlowItemDto bookingIdBB(String bookingIdBB) {
    this.bookingIdBB = bookingIdBB;
    return this;
  }

  /**
   * Get bookingIdBB
   * @return bookingIdBB
   */
  
  @Schema(name = "bookingIdBB", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("bookingIdBB")
  public String getBookingIdBB() {
    return bookingIdBB;
  }

  public void setBookingIdBB(String bookingIdBB) {
    this.bookingIdBB = bookingIdBB;
  }

  public BookingFlowItemDto rateCategory(String rateCategory) {
    this.rateCategory = rateCategory;
    return this;
  }

  /**
   * Get rateCategory
   * @return rateCategory
   */
  
  @Schema(name = "rateCategory", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("rateCategory")
  public String getRateCategory() {
    return rateCategory;
  }

  public void setRateCategory(String rateCategory) {
    this.rateCategory = rateCategory;
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
        Objects.equals(this.bookingIdBB, bookingFlowItemDto.bookingIdBB) &&
        Objects.equals(this.rateCategory, bookingFlowItemDto.rateCategory) &&
        Objects.equals(this.rateCode, bookingFlowItemDto.rateCode);
  }

  @Override
  public int hashCode() {
    return Objects.hash(bookingFlowPath, bookingId, bookingIdBB, rateCategory, rateCode);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class BookingFlowItemDto {\n");
    sb.append("    bookingFlowPath: ").append(toIndentedString(bookingFlowPath)).append("\n");
    sb.append("    bookingId: ").append(toIndentedString(bookingId)).append("\n");
    sb.append("    bookingIdBB: ").append(toIndentedString(bookingIdBB)).append("\n");
    sb.append("    rateCategory: ").append(toIndentedString(rateCategory)).append("\n");
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

