package uk.co.whitbread.hotel.ohip.adapter.generated.models.basket;

import java.net.URI;
import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import java.math.BigDecimal;
import org.springframework.lang.Nullable;
import org.openapitools.jackson.nullable.JsonNullable;
import java.time.OffsetDateTime;
import javax.validation.Valid;
import javax.validation.constraints.*;
import io.swagger.v3.oas.annotations.media.Schema;


import java.util.*;
import javax.annotation.Generated;

/**
 * BusinessAllowance
 */

@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2026-08-27T18:51:25.444555+03:00[Europe/Bucharest]", comments = "Generator version: 7.12.0")
public class BusinessAllowance {

  private @Nullable String allowance;

  private @Nullable BigDecimal budget;

  private @Nullable Boolean isAuthorised;

  public BusinessAllowance allowance(String allowance) {
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

  public BusinessAllowance budget(BigDecimal budget) {
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

  public BusinessAllowance isAuthorised(Boolean isAuthorised) {
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
    BusinessAllowance businessAllowance = (BusinessAllowance) o;
    return Objects.equals(this.allowance, businessAllowance.allowance) &&
        Objects.equals(this.budget, businessAllowance.budget) &&
        Objects.equals(this.isAuthorised, businessAllowance.isAuthorised);
  }

  @Override
  public int hashCode() {
    return Objects.hash(allowance, budget, isAuthorised);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class BusinessAllowance {\n");
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

