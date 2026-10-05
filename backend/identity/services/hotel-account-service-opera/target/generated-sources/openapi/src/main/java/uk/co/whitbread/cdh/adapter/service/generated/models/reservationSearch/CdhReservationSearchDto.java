package uk.co.whitbread.cdh.adapter.service.generated.models.reservationSearch;

import java.net.URI;
import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.springframework.lang.Nullable;
import uk.co.whitbread.cdh.adapter.service.generated.models.reservationSearch.ResultsDto;
import uk.co.whitbread.cdh.adapter.service.generated.models.reservationSearch.TotalsDto;
import org.openapitools.jackson.nullable.JsonNullable;
import java.time.OffsetDateTime;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import io.swagger.v3.oas.annotations.media.Schema;


import java.util.*;
import jakarta.annotation.Generated;

/**
 * CdhReservationSearchDto
 */

@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2026-09-09T08:37:59.335673+03:00[Europe/Bucharest]", comments = "Generator version: 7.14.0")
public class CdhReservationSearchDto {

  private @Nullable String continuationToken;

  @Valid
  private List<@Valid ResultsDto> results = new ArrayList<>();

  private @Nullable Integer searchResults;

  private @Nullable Integer totalResults;

  private @Nullable Integer totalSize;

  private @Nullable TotalsDto totals;

  public CdhReservationSearchDto continuationToken(@Nullable String continuationToken) {
    this.continuationToken = continuationToken;
    return this;
  }

  /**
   * Get continuationToken
   * @return continuationToken
   */
  
  @Schema(name = "continuationToken", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("continuationToken")
  public @Nullable String getContinuationToken() {
    return continuationToken;
  }

  public void setContinuationToken(@Nullable String continuationToken) {
    this.continuationToken = continuationToken;
  }

  public CdhReservationSearchDto results(List<@Valid ResultsDto> results) {
    this.results = results;
    return this;
  }

  public CdhReservationSearchDto addResultsItem(ResultsDto resultsItem) {
    if (this.results == null) {
      this.results = new ArrayList<>();
    }
    this.results.add(resultsItem);
    return this;
  }

  /**
   * Get results
   * @return results
   */
  @Valid 
  @Schema(name = "results", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("results")
  public List<@Valid ResultsDto> getResults() {
    return results;
  }

  public void setResults(List<@Valid ResultsDto> results) {
    this.results = results;
  }

  public CdhReservationSearchDto searchResults(@Nullable Integer searchResults) {
    this.searchResults = searchResults;
    return this;
  }

  /**
   * Get searchResults
   * @return searchResults
   */
  
  @Schema(name = "searchResults", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("searchResults")
  public @Nullable Integer getSearchResults() {
    return searchResults;
  }

  public void setSearchResults(@Nullable Integer searchResults) {
    this.searchResults = searchResults;
  }

  public CdhReservationSearchDto totalResults(@Nullable Integer totalResults) {
    this.totalResults = totalResults;
    return this;
  }

  /**
   * Get totalResults
   * @return totalResults
   */
  
  @Schema(name = "totalResults", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("totalResults")
  public @Nullable Integer getTotalResults() {
    return totalResults;
  }

  public void setTotalResults(@Nullable Integer totalResults) {
    this.totalResults = totalResults;
  }

  public CdhReservationSearchDto totalSize(@Nullable Integer totalSize) {
    this.totalSize = totalSize;
    return this;
  }

  /**
   * Get totalSize
   * @return totalSize
   */
  
  @Schema(name = "totalSize", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("totalSize")
  public @Nullable Integer getTotalSize() {
    return totalSize;
  }

  public void setTotalSize(@Nullable Integer totalSize) {
    this.totalSize = totalSize;
  }

  public CdhReservationSearchDto totals(@Nullable TotalsDto totals) {
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
  public @Nullable TotalsDto getTotals() {
    return totals;
  }

  public void setTotals(@Nullable TotalsDto totals) {
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
    CdhReservationSearchDto cdhReservationSearchDto = (CdhReservationSearchDto) o;
    return Objects.equals(this.continuationToken, cdhReservationSearchDto.continuationToken) &&
        Objects.equals(this.results, cdhReservationSearchDto.results) &&
        Objects.equals(this.searchResults, cdhReservationSearchDto.searchResults) &&
        Objects.equals(this.totalResults, cdhReservationSearchDto.totalResults) &&
        Objects.equals(this.totalSize, cdhReservationSearchDto.totalSize) &&
        Objects.equals(this.totals, cdhReservationSearchDto.totals);
  }

  @Override
  public int hashCode() {
    return Objects.hash(continuationToken, results, searchResults, totalResults, totalSize, totals);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class CdhReservationSearchDto {\n");
    sb.append("    continuationToken: ").append(toIndentedString(continuationToken)).append("\n");
    sb.append("    results: ").append(toIndentedString(results)).append("\n");
    sb.append("    searchResults: ").append(toIndentedString(searchResults)).append("\n");
    sb.append("    totalResults: ").append(toIndentedString(totalResults)).append("\n");
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

