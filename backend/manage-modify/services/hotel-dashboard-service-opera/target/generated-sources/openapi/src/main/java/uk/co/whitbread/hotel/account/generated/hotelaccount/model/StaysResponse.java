package uk.co.whitbread.hotel.account.generated.hotelaccount.model;

import java.net.URI;
import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.jspecify.annotations.Nullable;
import uk.co.whitbread.hotel.account.generated.hotelaccount.model.Stay;
import uk.co.whitbread.hotel.account.generated.hotelaccount.model.StaysTypesTotals;
import org.openapitools.jackson.nullable.JsonNullable;
import java.time.OffsetDateTime;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import io.swagger.v3.oas.annotations.media.Schema;


import java.util.*;
import jakarta.annotation.Generated;

/**
 * StaysResponse
 */

@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2026-08-25T07:50:33.863804+03:00[Europe/Bucharest]", comments = "Generator version: 7.12.0")
public class StaysResponse {

  private @Nullable String continuationToken;

  private @Nullable Integer pageIndex;

  private @Nullable Integer pageSize;

  private @Nullable String sessionId;

  @Valid
  private List<@Valid Stay> stays = new ArrayList<>();

  private @Nullable Integer totalSize;

  private @Nullable StaysTypesTotals totals;

  public StaysResponse continuationToken(String continuationToken) {
    this.continuationToken = continuationToken;
    return this;
  }

  /**
   * Get continuationToken
   * @return continuationToken
   */
  
  @Schema(name = "continuationToken", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("continuationToken")
  public String getContinuationToken() {
    return continuationToken;
  }

  public void setContinuationToken(String continuationToken) {
    this.continuationToken = continuationToken;
  }

  public StaysResponse pageIndex(Integer pageIndex) {
    this.pageIndex = pageIndex;
    return this;
  }

  /**
   * Get pageIndex
   * @return pageIndex
   */
  
  @Schema(name = "pageIndex", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("pageIndex")
  public Integer getPageIndex() {
    return pageIndex;
  }

  public void setPageIndex(Integer pageIndex) {
    this.pageIndex = pageIndex;
  }

  public StaysResponse pageSize(Integer pageSize) {
    this.pageSize = pageSize;
    return this;
  }

  /**
   * Get pageSize
   * @return pageSize
   */
  
  @Schema(name = "pageSize", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("pageSize")
  public Integer getPageSize() {
    return pageSize;
  }

  public void setPageSize(Integer pageSize) {
    this.pageSize = pageSize;
  }

  public StaysResponse sessionId(String sessionId) {
    this.sessionId = sessionId;
    return this;
  }

  /**
   * Get sessionId
   * @return sessionId
   */
  
  @Schema(name = "sessionId", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("sessionId")
  public String getSessionId() {
    return sessionId;
  }

  public void setSessionId(String sessionId) {
    this.sessionId = sessionId;
  }

  public StaysResponse stays(List<@Valid Stay> stays) {
    this.stays = stays;
    return this;
  }

  public StaysResponse addStaysItem(Stay staysItem) {
    if (this.stays == null) {
      this.stays = new ArrayList<>();
    }
    this.stays.add(staysItem);
    return this;
  }

  /**
   * Get stays
   * @return stays
   */
  @Valid 
  @Schema(name = "stays", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("stays")
  public List<@Valid Stay> getStays() {
    return stays;
  }

  public void setStays(List<@Valid Stay> stays) {
    this.stays = stays;
  }

  public StaysResponse totalSize(Integer totalSize) {
    this.totalSize = totalSize;
    return this;
  }

  /**
   * Get totalSize
   * @return totalSize
   */
  
  @Schema(name = "totalSize", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("totalSize")
  public Integer getTotalSize() {
    return totalSize;
  }

  public void setTotalSize(Integer totalSize) {
    this.totalSize = totalSize;
  }

  public StaysResponse totals(StaysTypesTotals totals) {
    this.totals = totals;
    return this;
  }

  /**
   * Get totals
   * @return totals
   */
  @Valid 
  @Schema(name = "totals", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("totals")
  public StaysTypesTotals getTotals() {
    return totals;
  }

  public void setTotals(StaysTypesTotals totals) {
    this.totals = totals;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    StaysResponse staysResponse = (StaysResponse) o;
    return Objects.equals(this.continuationToken, staysResponse.continuationToken) &&
        Objects.equals(this.pageIndex, staysResponse.pageIndex) &&
        Objects.equals(this.pageSize, staysResponse.pageSize) &&
        Objects.equals(this.sessionId, staysResponse.sessionId) &&
        Objects.equals(this.stays, staysResponse.stays) &&
        Objects.equals(this.totalSize, staysResponse.totalSize) &&
        Objects.equals(this.totals, staysResponse.totals);
  }

  @Override
  public int hashCode() {
    return Objects.hash(continuationToken, pageIndex, pageSize, sessionId, stays, totalSize, totals);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class StaysResponse {\n");
    sb.append("    continuationToken: ").append(toIndentedString(continuationToken)).append("\n");
    sb.append("    pageIndex: ").append(toIndentedString(pageIndex)).append("\n");
    sb.append("    pageSize: ").append(toIndentedString(pageSize)).append("\n");
    sb.append("    sessionId: ").append(toIndentedString(sessionId)).append("\n");
    sb.append("    stays: ").append(toIndentedString(stays)).append("\n");
    sb.append("    totalSize: ").append(toIndentedString(totalSize)).append("\n");
    sb.append("    totals: ").append(toIndentedString(totals)).append("\n");
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

