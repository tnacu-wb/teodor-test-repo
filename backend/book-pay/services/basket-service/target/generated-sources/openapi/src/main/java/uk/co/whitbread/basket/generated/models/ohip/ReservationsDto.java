package uk.co.whitbread.basket.generated.models.ohip;

import java.net.URI;
import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.springframework.lang.Nullable;
import uk.co.whitbread.basket.generated.models.ohip.ReservationInfoDto;
import org.openapitools.jackson.nullable.JsonNullable;
import java.time.OffsetDateTime;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import io.swagger.v3.oas.annotations.media.Schema;


import java.util.*;
import jakarta.annotation.Generated;

/**
 * ReservationsDto
 */
@com.fasterxml.jackson.annotation.JsonIgnoreProperties(ignoreUnknown = true)

@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2026-09-04T08:10:10.951524+03:00[Europe/Bucharest]", comments = "Generator version: 7.12.0")
public class ReservationsDto {

  private @Nullable Boolean hasMore;

  private @Nullable Integer limit;

  private @Nullable Integer offset;

  @Valid
  private List<@Valid ReservationInfoDto> reservationInfo = new ArrayList<>();

  private @Nullable Integer totalPages;

  private @Nullable Integer totalResults;

  public ReservationsDto hasMore(Boolean hasMore) {
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

  public ReservationsDto limit(Integer limit) {
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

  public ReservationsDto offset(Integer offset) {
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

  public ReservationsDto reservationInfo(List<@Valid ReservationInfoDto> reservationInfo) {
    this.reservationInfo = reservationInfo;
    return this;
  }

  public ReservationsDto addReservationInfoItem(ReservationInfoDto reservationInfoItem) {
    if (this.reservationInfo == null) {
      this.reservationInfo = new ArrayList<>();
    }
    this.reservationInfo.add(reservationInfoItem);
    return this;
  }

  /**
   * Get reservationInfo
   * @return reservationInfo
   */
  @Valid 
  @Schema(name = "reservationInfo", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("reservationInfo")
  public List<@Valid ReservationInfoDto> getReservationInfo() {
    return reservationInfo;
  }

  public void setReservationInfo(List<@Valid ReservationInfoDto> reservationInfo) {
    this.reservationInfo = reservationInfo;
  }

  public ReservationsDto totalPages(Integer totalPages) {
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

  public ReservationsDto totalResults(Integer totalResults) {
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
    ReservationsDto reservationsDto = (ReservationsDto) o;
    return Objects.equals(this.hasMore, reservationsDto.hasMore) &&
        Objects.equals(this.limit, reservationsDto.limit) &&
        Objects.equals(this.offset, reservationsDto.offset) &&
        Objects.equals(this.reservationInfo, reservationsDto.reservationInfo) &&
        Objects.equals(this.totalPages, reservationsDto.totalPages) &&
        Objects.equals(this.totalResults, reservationsDto.totalResults);
  }

  @Override
  public int hashCode() {
    return Objects.hash(hasMore, limit, offset, reservationInfo, totalPages, totalResults);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class ReservationsDto {\n");
    sb.append("    hasMore: ").append(toIndentedString(hasMore)).append("\n");
    sb.append("    limit: ").append(toIndentedString(limit)).append("\n");
    sb.append("    offset: ").append(toIndentedString(offset)).append("\n");
    sb.append("    reservationInfo: ").append(toIndentedString(reservationInfo)).append("\n");
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

