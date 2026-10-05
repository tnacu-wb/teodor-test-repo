package uk.co.whitbread.basket.generated.models.reservation;

import java.net.URI;
import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.springframework.lang.Nullable;
import uk.co.whitbread.basket.generated.models.reservation.ChangeLogTypeDto;
import org.openapitools.jackson.nullable.JsonNullable;
import java.time.OffsetDateTime;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import io.swagger.v3.oas.annotations.media.Schema;


import java.util.*;
import jakarta.annotation.Generated;

/**
 * ChangeLogListTypeDto
 */
@com.fasterxml.jackson.annotation.JsonIgnoreProperties(ignoreUnknown = true)

@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2026-09-04T08:10:03.993735+03:00[Europe/Bucharest]", comments = "Generator version: 7.12.0")
public class ChangeLogListTypeDto {

  @Valid
  private List<@Valid ChangeLogTypeDto> activityLog = new ArrayList<>();

  private @Nullable Integer count;

  private @Nullable Boolean hasMore;

  private @Nullable Integer limit;

  private @Nullable Integer offset;

  private @Nullable Integer totalPages;

  private @Nullable Integer totalResults;

  public ChangeLogListTypeDto activityLog(List<@Valid ChangeLogTypeDto> activityLog) {
    this.activityLog = activityLog;
    return this;
  }

  public ChangeLogListTypeDto addActivityLogItem(ChangeLogTypeDto activityLogItem) {
    if (this.activityLog == null) {
      this.activityLog = new ArrayList<>();
    }
    this.activityLog.add(activityLogItem);
    return this;
  }

  /**
   * Get activityLog
   * @return activityLog
   */
  @Valid 
  @Schema(name = "activityLog", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("activityLog")
  public List<@Valid ChangeLogTypeDto> getActivityLog() {
    return activityLog;
  }

  public void setActivityLog(List<@Valid ChangeLogTypeDto> activityLog) {
    this.activityLog = activityLog;
  }

  public ChangeLogListTypeDto count(Integer count) {
    this.count = count;
    return this;
  }

  /**
   * Get count
   * @return count
   */
  
  @Schema(name = "count", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("count")
  public Integer getCount() {
    return count;
  }

  public void setCount(Integer count) {
    this.count = count;
  }

  public ChangeLogListTypeDto hasMore(Boolean hasMore) {
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

  public ChangeLogListTypeDto limit(Integer limit) {
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

  public ChangeLogListTypeDto offset(Integer offset) {
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

  public ChangeLogListTypeDto totalPages(Integer totalPages) {
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

  public ChangeLogListTypeDto totalResults(Integer totalResults) {
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
    ChangeLogListTypeDto changeLogListTypeDto = (ChangeLogListTypeDto) o;
    return Objects.equals(this.activityLog, changeLogListTypeDto.activityLog) &&
        Objects.equals(this.count, changeLogListTypeDto.count) &&
        Objects.equals(this.hasMore, changeLogListTypeDto.hasMore) &&
        Objects.equals(this.limit, changeLogListTypeDto.limit) &&
        Objects.equals(this.offset, changeLogListTypeDto.offset) &&
        Objects.equals(this.totalPages, changeLogListTypeDto.totalPages) &&
        Objects.equals(this.totalResults, changeLogListTypeDto.totalResults);
  }

  @Override
  public int hashCode() {
    return Objects.hash(activityLog, count, hasMore, limit, offset, totalPages, totalResults);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class ChangeLogListTypeDto {\n");
    sb.append("    activityLog: ").append(toIndentedString(activityLog)).append("\n");
    sb.append("    count: ").append(toIndentedString(count)).append("\n");
    sb.append("    hasMore: ").append(toIndentedString(hasMore)).append("\n");
    sb.append("    limit: ").append(toIndentedString(limit)).append("\n");
    sb.append("    offset: ").append(toIndentedString(offset)).append("\n");
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

