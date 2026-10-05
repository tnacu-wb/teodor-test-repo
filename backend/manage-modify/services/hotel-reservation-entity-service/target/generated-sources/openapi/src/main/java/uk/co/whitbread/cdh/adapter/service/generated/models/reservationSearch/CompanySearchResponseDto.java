package uk.co.whitbread.cdh.adapter.service.generated.models.reservationSearch;

import java.net.URI;
import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.springframework.lang.Nullable;
import uk.co.whitbread.cdh.adapter.service.generated.models.reservationSearch.CompaniesDto;
import org.openapitools.jackson.nullable.JsonNullable;
import java.time.OffsetDateTime;
import javax.validation.Valid;
import javax.validation.constraints.*;
import io.swagger.v3.oas.annotations.media.Schema;


import java.util.*;
import javax.annotation.Generated;

/**
 * CompanySearchResponseDto
 */

@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2026-08-27T18:51:32.868523+03:00[Europe/Bucharest]", comments = "Generator version: 7.12.0")
public class CompanySearchResponseDto {

  private @Nullable String continuationToken;

  @Valid
  private List<@Valid CompaniesDto> results = new ArrayList<>();

  private @Nullable Integer searchResults;

  private @Nullable Integer totalResults;

  public CompanySearchResponseDto continuationToken(String continuationToken) {
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

  public CompanySearchResponseDto results(List<@Valid CompaniesDto> results) {
    this.results = results;
    return this;
  }

  public CompanySearchResponseDto addResultsItem(CompaniesDto resultsItem) {
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
  public List<@Valid CompaniesDto> getResults() {
    return results;
  }

  public void setResults(List<@Valid CompaniesDto> results) {
    this.results = results;
  }

  public CompanySearchResponseDto searchResults(Integer searchResults) {
    this.searchResults = searchResults;
    return this;
  }

  /**
   * Get searchResults
   * @return searchResults
   */
  
  @Schema(name = "searchResults", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("searchResults")
  public Integer getSearchResults() {
    return searchResults;
  }

  public void setSearchResults(Integer searchResults) {
    this.searchResults = searchResults;
  }

  public CompanySearchResponseDto totalResults(Integer totalResults) {
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
    CompanySearchResponseDto companySearchResponseDto = (CompanySearchResponseDto) o;
    return Objects.equals(this.continuationToken, companySearchResponseDto.continuationToken) &&
        Objects.equals(this.results, companySearchResponseDto.results) &&
        Objects.equals(this.searchResults, companySearchResponseDto.searchResults) &&
        Objects.equals(this.totalResults, companySearchResponseDto.totalResults);
  }

  @Override
  public int hashCode() {
    return Objects.hash(continuationToken, results, searchResults, totalResults);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class CompanySearchResponseDto {\n");
    sb.append("    continuationToken: ").append(toIndentedString(continuationToken)).append("\n");
    sb.append("    results: ").append(toIndentedString(results)).append("\n");
    sb.append("    searchResults: ").append(toIndentedString(searchResults)).append("\n");
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

