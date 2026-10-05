package uk.co.whitbread.hotel.ohip.adapter.generated.models;

import java.net.URI;
import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.springframework.lang.Nullable;
import org.openapitools.jackson.nullable.JsonNullable;
import java.time.OffsetDateTime;
import javax.validation.Valid;
import javax.validation.constraints.*;
import io.swagger.v3.oas.annotations.media.Schema;


import java.util.*;
import javax.annotation.Generated;

/**
 * CorporateRateDto
 */

@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2026-08-27T18:51:15.274731+03:00[Europe/Bucharest]", comments = "Generator version: 7.12.0")
public class CorporateRateDto {

  private @Nullable String corporateId;

  @Valid
  private List<String> ratePlanSets = new ArrayList<>();

  public CorporateRateDto corporateId(String corporateId) {
    this.corporateId = corporateId;
    return this;
  }

  /**
   * Get corporateId
   * @return corporateId
   */
  
  @Schema(name = "corporateId", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("corporateId")
  public String getCorporateId() {
    return corporateId;
  }

  public void setCorporateId(String corporateId) {
    this.corporateId = corporateId;
  }

  public CorporateRateDto ratePlanSets(List<String> ratePlanSets) {
    this.ratePlanSets = ratePlanSets;
    return this;
  }

  public CorporateRateDto addRatePlanSetsItem(String ratePlanSetsItem) {
    if (this.ratePlanSets == null) {
      this.ratePlanSets = new ArrayList<>();
    }
    this.ratePlanSets.add(ratePlanSetsItem);
    return this;
  }

  /**
   * Get ratePlanSets
   * @return ratePlanSets
   */
  
  @Schema(name = "ratePlanSets", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("ratePlanSets")
  public List<String> getRatePlanSets() {
    return ratePlanSets;
  }

  public void setRatePlanSets(List<String> ratePlanSets) {
    this.ratePlanSets = ratePlanSets;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    CorporateRateDto corporateRateDto = (CorporateRateDto) o;
    return Objects.equals(this.corporateId, corporateRateDto.corporateId) &&
        Objects.equals(this.ratePlanSets, corporateRateDto.ratePlanSets);
  }

  @Override
  public int hashCode() {
    return Objects.hash(corporateId, ratePlanSets);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class CorporateRateDto {\n");
    sb.append("    corporateId: ").append(toIndentedString(corporateId)).append("\n");
    sb.append("    ratePlanSets: ").append(toIndentedString(ratePlanSets)).append("\n");
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

