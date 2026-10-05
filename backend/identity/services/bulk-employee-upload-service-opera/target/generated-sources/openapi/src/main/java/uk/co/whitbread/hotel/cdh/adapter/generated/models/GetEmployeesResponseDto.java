package uk.co.whitbread.hotel.cdh.adapter.generated.models;

import java.net.URI;
import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.springframework.lang.Nullable;
import uk.co.whitbread.hotel.cdh.adapter.generated.models.GetEmployeeResponseDto;
import org.openapitools.jackson.nullable.JsonNullable;
import java.time.OffsetDateTime;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import io.swagger.v3.oas.annotations.media.Schema;


import java.util.*;
import jakarta.annotation.Generated;

/**
 * GetEmployeesResponseDto
 */

@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2026-08-25T07:49:35.320601+03:00[Europe/Bucharest]", comments = "Generator version: 7.12.0")
public class GetEmployeesResponseDto {

  private @Nullable String continuationToken;

  @Valid
  private List<@Valid GetEmployeeResponseDto> results = new ArrayList<>();

  private @Nullable Integer totalEmployeesInCompany;

  public GetEmployeesResponseDto continuationToken(String continuationToken) {
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

  public GetEmployeesResponseDto results(List<@Valid GetEmployeeResponseDto> results) {
    this.results = results;
    return this;
  }

  public GetEmployeesResponseDto addResultsItem(GetEmployeeResponseDto resultsItem) {
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
  public List<@Valid GetEmployeeResponseDto> getResults() {
    return results;
  }

  public void setResults(List<@Valid GetEmployeeResponseDto> results) {
    this.results = results;
  }

  public GetEmployeesResponseDto totalEmployeesInCompany(Integer totalEmployeesInCompany) {
    this.totalEmployeesInCompany = totalEmployeesInCompany;
    return this;
  }

  /**
   * Get totalEmployeesInCompany
   * @return totalEmployeesInCompany
   */
  
  @Schema(name = "totalEmployeesInCompany", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("totalEmployeesInCompany")
  public Integer getTotalEmployeesInCompany() {
    return totalEmployeesInCompany;
  }

  public void setTotalEmployeesInCompany(Integer totalEmployeesInCompany) {
    this.totalEmployeesInCompany = totalEmployeesInCompany;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    GetEmployeesResponseDto getEmployeesResponseDto = (GetEmployeesResponseDto) o;
    return Objects.equals(this.continuationToken, getEmployeesResponseDto.continuationToken) &&
        Objects.equals(this.results, getEmployeesResponseDto.results) &&
        Objects.equals(this.totalEmployeesInCompany, getEmployeesResponseDto.totalEmployeesInCompany);
  }

  @Override
  public int hashCode() {
    return Objects.hash(continuationToken, results, totalEmployeesInCompany);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class GetEmployeesResponseDto {\n");
    sb.append("    continuationToken: ").append(toIndentedString(continuationToken)).append("\n");
    sb.append("    results: ").append(toIndentedString(results)).append("\n");
    sb.append("    totalEmployeesInCompany: ").append(toIndentedString(totalEmployeesInCompany)).append("\n");
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

