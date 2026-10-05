package uk.co.whitbread.ohip.generated.models;

import java.net.URI;
import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.springframework.lang.Nullable;
import uk.co.whitbread.ohip.generated.models.SearchBookingDto;
import org.openapitools.jackson.nullable.JsonNullable;
import java.time.OffsetDateTime;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import io.swagger.v3.oas.annotations.media.Schema;


import java.util.*;
import jakarta.annotation.Generated;

/**
 * SearchBookingsResponseDto
 */

@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2026-08-25T07:50:12.143180+03:00[Europe/Bucharest]", comments = "Generator version: 7.12.0")
public class SearchBookingsResponseDto {

  @Valid
  private List<@Valid SearchBookingDto> bookings = new ArrayList<>();

  private @Nullable Boolean hasMore;

  private @Nullable Integer limit;

  private @Nullable Integer offset;

  private @Nullable Boolean responseLimitExceeded;

  private @Nullable Integer totalPages;

  private @Nullable Integer totalResults;

  public SearchBookingsResponseDto bookings(List<@Valid SearchBookingDto> bookings) {
    this.bookings = bookings;
    return this;
  }

  public SearchBookingsResponseDto addBookingsItem(SearchBookingDto bookingsItem) {
    if (this.bookings == null) {
      this.bookings = new ArrayList<>();
    }
    this.bookings.add(bookingsItem);
    return this;
  }

  /**
   * Get bookings
   * @return bookings
   */
  @Valid 
  @Schema(name = "bookings", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("bookings")
  public List<@Valid SearchBookingDto> getBookings() {
    return bookings;
  }

  public void setBookings(List<@Valid SearchBookingDto> bookings) {
    this.bookings = bookings;
  }

  public SearchBookingsResponseDto hasMore(Boolean hasMore) {
    this.hasMore = hasMore;
    return this;
  }

  /**
   * Get hasMore
   * @return hasMore
   */
  
  @Schema(name = "hasMore", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("hasMore")
  public Boolean getHasMore() {
    return hasMore;
  }

  public void setHasMore(Boolean hasMore) {
    this.hasMore = hasMore;
  }

  public SearchBookingsResponseDto limit(Integer limit) {
    this.limit = limit;
    return this;
  }

  /**
   * Get limit
   * @return limit
   */
  
  @Schema(name = "limit", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("limit")
  public Integer getLimit() {
    return limit;
  }

  public void setLimit(Integer limit) {
    this.limit = limit;
  }

  public SearchBookingsResponseDto offset(Integer offset) {
    this.offset = offset;
    return this;
  }

  /**
   * Get offset
   * @return offset
   */
  
  @Schema(name = "offset", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("offset")
  public Integer getOffset() {
    return offset;
  }

  public void setOffset(Integer offset) {
    this.offset = offset;
  }

  public SearchBookingsResponseDto responseLimitExceeded(Boolean responseLimitExceeded) {
    this.responseLimitExceeded = responseLimitExceeded;
    return this;
  }

  /**
   * Get responseLimitExceeded
   * @return responseLimitExceeded
   */
  
  @Schema(name = "responseLimitExceeded", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("responseLimitExceeded")
  public Boolean getResponseLimitExceeded() {
    return responseLimitExceeded;
  }

  public void setResponseLimitExceeded(Boolean responseLimitExceeded) {
    this.responseLimitExceeded = responseLimitExceeded;
  }

  public SearchBookingsResponseDto totalPages(Integer totalPages) {
    this.totalPages = totalPages;
    return this;
  }

  /**
   * Get totalPages
   * @return totalPages
   */
  
  @Schema(name = "totalPages", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("totalPages")
  public Integer getTotalPages() {
    return totalPages;
  }

  public void setTotalPages(Integer totalPages) {
    this.totalPages = totalPages;
  }

  public SearchBookingsResponseDto totalResults(Integer totalResults) {
    this.totalResults = totalResults;
    return this;
  }

  /**
   * Get totalResults
   * @return totalResults
   */
  
  @Schema(name = "totalResults", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("totalResults")
  public Integer getTotalResults() {
    return totalResults;
  }

  public void setTotalResults(Integer totalResults) {
    this.totalResults = totalResults;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    SearchBookingsResponseDto searchBookingsResponseDto = (SearchBookingsResponseDto) o;
    return Objects.equals(this.bookings, searchBookingsResponseDto.bookings) &&
        Objects.equals(this.hasMore, searchBookingsResponseDto.hasMore) &&
        Objects.equals(this.limit, searchBookingsResponseDto.limit) &&
        Objects.equals(this.offset, searchBookingsResponseDto.offset) &&
        Objects.equals(this.responseLimitExceeded, searchBookingsResponseDto.responseLimitExceeded) &&
        Objects.equals(this.totalPages, searchBookingsResponseDto.totalPages) &&
        Objects.equals(this.totalResults, searchBookingsResponseDto.totalResults);
  }

  @Override
  public int hashCode() {
    return Objects.hash(bookings, hasMore, limit, offset, responseLimitExceeded, totalPages, totalResults);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class SearchBookingsResponseDto {\n");
    sb.append("    bookings: ").append(toIndentedString(bookings)).append("\n");
    sb.append("    hasMore: ").append(toIndentedString(hasMore)).append("\n");
    sb.append("    limit: ").append(toIndentedString(limit)).append("\n");
    sb.append("    offset: ").append(toIndentedString(offset)).append("\n");
    sb.append("    responseLimitExceeded: ").append(toIndentedString(responseLimitExceeded)).append("\n");
    sb.append("    totalPages: ").append(toIndentedString(totalPages)).append("\n");
    sb.append("    totalResults: ").append(toIndentedString(totalResults)).append("\n");
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

