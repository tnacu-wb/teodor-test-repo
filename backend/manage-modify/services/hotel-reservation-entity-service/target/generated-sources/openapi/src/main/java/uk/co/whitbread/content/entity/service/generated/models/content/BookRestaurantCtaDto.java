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
 * BookRestaurantCtaDto
 */

@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2026-08-27T18:51:28.563902+03:00[Europe/Bucharest]", comments = "Generator version: 7.12.0")
public class BookRestaurantCtaDto {

  private @Nullable String bookingCardCtaLink;

  private @Nullable String bookingCardCtaText;

  private @Nullable String bookingCardTrackingId;

  public BookRestaurantCtaDto bookingCardCtaLink(String bookingCardCtaLink) {
    this.bookingCardCtaLink = bookingCardCtaLink;
    return this;
  }

  /**
   * Get bookingCardCtaLink
   * @return bookingCardCtaLink
   */
  
  @Schema(name = "bookingCardCtaLink", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("bookingCardCtaLink")
  public String getBookingCardCtaLink() {
    return bookingCardCtaLink;
  }

  public void setBookingCardCtaLink(String bookingCardCtaLink) {
    this.bookingCardCtaLink = bookingCardCtaLink;
  }

  public BookRestaurantCtaDto bookingCardCtaText(String bookingCardCtaText) {
    this.bookingCardCtaText = bookingCardCtaText;
    return this;
  }

  /**
   * Get bookingCardCtaText
   * @return bookingCardCtaText
   */
  
  @Schema(name = "bookingCardCtaText", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("bookingCardCtaText")
  public String getBookingCardCtaText() {
    return bookingCardCtaText;
  }

  public void setBookingCardCtaText(String bookingCardCtaText) {
    this.bookingCardCtaText = bookingCardCtaText;
  }

  public BookRestaurantCtaDto bookingCardTrackingId(String bookingCardTrackingId) {
    this.bookingCardTrackingId = bookingCardTrackingId;
    return this;
  }

  /**
   * Get bookingCardTrackingId
   * @return bookingCardTrackingId
   */
  
  @Schema(name = "bookingCardTrackingId", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("bookingCardTrackingId")
  public String getBookingCardTrackingId() {
    return bookingCardTrackingId;
  }

  public void setBookingCardTrackingId(String bookingCardTrackingId) {
    this.bookingCardTrackingId = bookingCardTrackingId;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    BookRestaurantCtaDto bookRestaurantCtaDto = (BookRestaurantCtaDto) o;
    return Objects.equals(this.bookingCardCtaLink, bookRestaurantCtaDto.bookingCardCtaLink) &&
        Objects.equals(this.bookingCardCtaText, bookRestaurantCtaDto.bookingCardCtaText) &&
        Objects.equals(this.bookingCardTrackingId, bookRestaurantCtaDto.bookingCardTrackingId);
  }

  @Override
  public int hashCode() {
    return Objects.hash(bookingCardCtaLink, bookingCardCtaText, bookingCardTrackingId);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class BookRestaurantCtaDto {\n");
    sb.append("    bookingCardCtaLink: ").append(toIndentedString(bookingCardCtaLink)).append("\n");
    sb.append("    bookingCardCtaText: ").append(toIndentedString(bookingCardCtaText)).append("\n");
    sb.append("    bookingCardTrackingId: ").append(toIndentedString(bookingCardTrackingId)).append("\n");
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

