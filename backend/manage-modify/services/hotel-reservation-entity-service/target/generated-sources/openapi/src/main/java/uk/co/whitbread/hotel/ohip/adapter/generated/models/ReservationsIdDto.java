package uk.co.whitbread.hotel.ohip.adapter.generated.models;

import java.net.URI;
import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.springframework.lang.Nullable;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.ReservationIdDetailsCheckDto;
import org.openapitools.jackson.nullable.JsonNullable;
import java.time.OffsetDateTime;
import javax.validation.Valid;
import javax.validation.constraints.*;
import io.swagger.v3.oas.annotations.media.Schema;


import java.util.*;
import javax.annotation.Generated;

/**
 * ReservationsIdDto
 */

@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2026-08-27T18:51:15.274731+03:00[Europe/Bucharest]", comments = "Generator version: 7.12.0")
public class ReservationsIdDto {

  private @Nullable Boolean hasMore;

  private @Nullable Integer limit;

  private @Nullable Integer offset;

  @Valid
  private List<@Valid ReservationIdDetailsCheckDto> reservation = new ArrayList<>();

  private @Nullable Integer totalPages;

  private @Nullable Integer totalResults;

  public ReservationsIdDto hasMore(Boolean hasMore) {
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

  public ReservationsIdDto limit(Integer limit) {
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

  public ReservationsIdDto offset(Integer offset) {
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

  public ReservationsIdDto reservation(List<@Valid ReservationIdDetailsCheckDto> reservation) {
    this.reservation = reservation;
    return this;
  }

  public ReservationsIdDto addReservationItem(ReservationIdDetailsCheckDto reservationItem) {
    if (this.reservation == null) {
      this.reservation = new ArrayList<>();
    }
    this.reservation.add(reservationItem);
    return this;
  }

  /**
   * Get reservation
   * @return reservation
   */
  @Valid 
  @Schema(name = "reservation", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("reservation")
  public List<@Valid ReservationIdDetailsCheckDto> getReservation() {
    return reservation;
  }

  public void setReservation(List<@Valid ReservationIdDetailsCheckDto> reservation) {
    this.reservation = reservation;
  }

  public ReservationsIdDto totalPages(Integer totalPages) {
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

  public ReservationsIdDto totalResults(Integer totalResults) {
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
    ReservationsIdDto reservationsIdDto = (ReservationsIdDto) o;
    return Objects.equals(this.hasMore, reservationsIdDto.hasMore) &&
        Objects.equals(this.limit, reservationsIdDto.limit) &&
        Objects.equals(this.offset, reservationsIdDto.offset) &&
        Objects.equals(this.reservation, reservationsIdDto.reservation) &&
        Objects.equals(this.totalPages, reservationsIdDto.totalPages) &&
        Objects.equals(this.totalResults, reservationsIdDto.totalResults);
  }

  @Override
  public int hashCode() {
    return Objects.hash(hasMore, limit, offset, reservation, totalPages, totalResults);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class ReservationsIdDto {\n");
    sb.append("    hasMore: ").append(toIndentedString(hasMore)).append("\n");
    sb.append("    limit: ").append(toIndentedString(limit)).append("\n");
    sb.append("    offset: ").append(toIndentedString(offset)).append("\n");
    sb.append("    reservation: ").append(toIndentedString(reservation)).append("\n");
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

