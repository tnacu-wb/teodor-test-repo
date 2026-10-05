package uk.co.whitbread.basket.generated.models.ohip;

import java.net.URI;
import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import java.math.BigDecimal;
import org.springframework.lang.Nullable;
import org.openapitools.jackson.nullable.JsonNullable;
import java.time.OffsetDateTime;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import io.swagger.v3.oas.annotations.media.Schema;


import java.util.*;
import jakarta.annotation.Generated;

/**
 * BusinessAllowanceDto
 */
@com.fasterxml.jackson.annotation.JsonIgnoreProperties(ignoreUnknown = true)

@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2026-09-04T08:10:10.951524+03:00[Europe/Bucharest]", comments = "Generator version: 7.12.0")
public class BusinessAllowanceDto {

  private String allowance;

  private BigDecimal budget;

  private Boolean isAuthorised;

  public BusinessAllowanceDto() {
    super();
  }

  /**
   * Constructor with only required parameters
   */
  public BusinessAllowanceDto(String allowance, BigDecimal budget, Boolean isAuthorised) {
    this.allowance = allowance;
    this.budget = budget;
    this.isAuthorised = isAuthorised;
  }

  public BusinessAllowanceDto allowance(String allowance) {
    this.allowance = allowance;
    return this;
  }

  /**
   * Get allowance
   * @return allowance
   */
  @NotNull 
  @Schema(name = "allowance", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("allowance")
  public String getAllowance() {
    return allowance;
  }

  public void setAllowance(String allowance) {
    this.allowance = allowance;
  }

  public BusinessAllowanceDto budget(BigDecimal budget) {
    this.budget = budget;
    return this;
  }

  /**
   * Get budget
   * @return budget
   */
  @NotNull @Valid 
  @Schema(name = "budget", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("budget")
  public BigDecimal getBudget() {
    return budget;
  }

  public void setBudget(BigDecimal budget) {
    this.budget = budget;
  }

  public BusinessAllowanceDto isAuthorised(Boolean isAuthorised) {
    this.isAuthorised = isAuthorised;
    return this;
  }

  /**
   * Get isAuthorised
   * @return isAuthorised
   */
  @NotNull 
  @Schema(name = "isAuthorised", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("isAuthorised")
  public Boolean getIsAuthorised() {
    return isAuthorised;
  }

  public void setIsAuthorised(Boolean isAuthorised) {
    this.isAuthorised = isAuthorised;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    BusinessAllowanceDto businessAllowanceDto = (BusinessAllowanceDto) o;
    return Objects.equals(this.allowance, businessAllowanceDto.allowance) &&
        Objects.equals(this.budget, businessAllowanceDto.budget) &&
        Objects.equals(this.isAuthorised, businessAllowanceDto.isAuthorised);
  }

  @Override
  public int hashCode() {
    return Objects.hash(allowance, budget, isAuthorised);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class BusinessAllowanceDto {\n");
    sb.append("    allowance: ").append(toIndentedString(allowance)).append("\n");
    sb.append("    budget: ").append(toIndentedString(budget)).append("\n");
    sb.append("    isAuthorised: ").append(toIndentedString(isAuthorised)).append("\n");
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

