package uk.co.whitbread.basket.generated.models.reservation;

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
 * BusinessAllowanceCcuiDto
 */
@com.fasterxml.jackson.annotation.JsonIgnoreProperties(ignoreUnknown = true)

@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2026-09-04T08:10:03.993735+03:00[Europe/Bucharest]", comments = "Generator version: 7.12.0")
public class BusinessAllowanceCcuiDto {

  private @Nullable String allowance;

  private @Nullable BigDecimal budget;

  private @Nullable Boolean isAuthorised;

  public BusinessAllowanceCcuiDto allowance(String allowance) {
    this.allowance = allowance;
    return this;
  }

  /**
   * Get allowance
   * @return allowance
   */
  
  @Schema(name = "allowance", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("allowance")
  public String getAllowance() {
    return allowance;
  }

  public void setAllowance(String allowance) {
    this.allowance = allowance;
  }

  public BusinessAllowanceCcuiDto budget(BigDecimal budget) {
    this.budget = budget;
    return this;
  }

  /**
   * Get budget
   * @return budget
   */
  @Valid 
  @Schema(name = "budget", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("budget")
  public BigDecimal getBudget() {
    return budget;
  }

  public void setBudget(BigDecimal budget) {
    this.budget = budget;
  }

  public BusinessAllowanceCcuiDto isAuthorised(Boolean isAuthorised) {
    this.isAuthorised = isAuthorised;
    return this;
  }

  /**
   * Get isAuthorised
   * @return isAuthorised
   */
  
  @Schema(name = "isAuthorised", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
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
    BusinessAllowanceCcuiDto businessAllowanceCcuiDto = (BusinessAllowanceCcuiDto) o;
    return Objects.equals(this.allowance, businessAllowanceCcuiDto.allowance) &&
        Objects.equals(this.budget, businessAllowanceCcuiDto.budget) &&
        Objects.equals(this.isAuthorised, businessAllowanceCcuiDto.isAuthorised);
  }

  @Override
  public int hashCode() {
    return Objects.hash(allowance, budget, isAuthorised);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class BusinessAllowanceCcuiDto {\n");
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

