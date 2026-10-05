package uk.co.whitbread.rules.entity.service.generated.models.agent;

import java.net.URI;
import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.springframework.lang.Nullable;
import uk.co.whitbread.rules.entity.service.generated.models.agent.BusinessAllowanceRuleDto;
import org.openapitools.jackson.nullable.JsonNullable;
import java.time.OffsetDateTime;
import javax.validation.Valid;
import javax.validation.constraints.*;
import io.swagger.v3.oas.annotations.media.Schema;


import java.util.*;
import javax.annotation.Generated;

/**
 * BusinessAllowanceRuleResponseDto
 */

@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2026-08-27T18:51:32.125466+03:00[Europe/Bucharest]", comments = "Generator version: 7.12.0")
public class BusinessAllowanceRuleResponseDto {

  @Valid
  private List<@Valid BusinessAllowanceRuleDto> businessAllowances = new ArrayList<>();

  public BusinessAllowanceRuleResponseDto businessAllowances(List<@Valid BusinessAllowanceRuleDto> businessAllowances) {
    this.businessAllowances = businessAllowances;
    return this;
  }

  public BusinessAllowanceRuleResponseDto addBusinessAllowancesItem(BusinessAllowanceRuleDto businessAllowancesItem) {
    if (this.businessAllowances == null) {
      this.businessAllowances = new ArrayList<>();
    }
    this.businessAllowances.add(businessAllowancesItem);
    return this;
  }

  /**
   * Get businessAllowances
   * @return businessAllowances
   */
  @Valid 
  @Schema(name = "businessAllowances", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("businessAllowances")
  public List<@Valid BusinessAllowanceRuleDto> getBusinessAllowances() {
    return businessAllowances;
  }

  public void setBusinessAllowances(List<@Valid BusinessAllowanceRuleDto> businessAllowances) {
    this.businessAllowances = businessAllowances;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    BusinessAllowanceRuleResponseDto businessAllowanceRuleResponseDto = (BusinessAllowanceRuleResponseDto) o;
    return Objects.equals(this.businessAllowances, businessAllowanceRuleResponseDto.businessAllowances);
  }

  @Override
  public int hashCode() {
    return Objects.hash(businessAllowances);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class BusinessAllowanceRuleResponseDto {\n");
    sb.append("    businessAllowances: ").append(toIndentedString(businessAllowances)).append("\n");
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

