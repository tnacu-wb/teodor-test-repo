package uk.co.whitbread.basket.generated.models.ohip;

import java.net.URI;
import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.springframework.lang.Nullable;
import uk.co.whitbread.basket.generated.models.ohip.CorporateRateDto;
import org.openapitools.jackson.nullable.JsonNullable;
import java.time.OffsetDateTime;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import io.swagger.v3.oas.annotations.media.Schema;


import java.util.*;
import jakarta.annotation.Generated;

/**
 * RateV3Dto
 */
@com.fasterxml.jackson.annotation.JsonIgnoreProperties(ignoreUnknown = true)

@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2026-09-04T08:10:10.951524+03:00[Europe/Bucharest]", comments = "Generator version: 7.12.0")
public class RateV3Dto {

  @Valid
  private List<@Valid CorporateRateDto> corporateRates = new ArrayList<>();

  @Valid
  private List<String> ratePlanCodes = new ArrayList<>();

  public RateV3Dto corporateRates(List<@Valid CorporateRateDto> corporateRates) {
    this.corporateRates = corporateRates;
    return this;
  }

  public RateV3Dto addCorporateRatesItem(CorporateRateDto corporateRatesItem) {
    if (this.corporateRates == null) {
      this.corporateRates = new ArrayList<>();
    }
    this.corporateRates.add(corporateRatesItem);
    return this;
  }

  /**
   * Get corporateRates
   * @return corporateRates
   */
  @Valid 
  @Schema(name = "corporateRates", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("corporateRates")
  public List<@Valid CorporateRateDto> getCorporateRates() {
    return corporateRates;
  }

  public void setCorporateRates(List<@Valid CorporateRateDto> corporateRates) {
    this.corporateRates = corporateRates;
  }

  public RateV3Dto ratePlanCodes(List<String> ratePlanCodes) {
    this.ratePlanCodes = ratePlanCodes;
    return this;
  }

  public RateV3Dto addRatePlanCodesItem(String ratePlanCodesItem) {
    if (this.ratePlanCodes == null) {
      this.ratePlanCodes = new ArrayList<>();
    }
    this.ratePlanCodes.add(ratePlanCodesItem);
    return this;
  }

  /**
   * Get ratePlanCodes
   * @return ratePlanCodes
   */
  
  @Schema(name = "ratePlanCodes", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("ratePlanCodes")
  public List<String> getRatePlanCodes() {
    return ratePlanCodes;
  }

  public void setRatePlanCodes(List<String> ratePlanCodes) {
    this.ratePlanCodes = ratePlanCodes;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    RateV3Dto rateV3Dto = (RateV3Dto) o;
    return Objects.equals(this.corporateRates, rateV3Dto.corporateRates) &&
        Objects.equals(this.ratePlanCodes, rateV3Dto.ratePlanCodes);
  }

  @Override
  public int hashCode() {
    return Objects.hash(corporateRates, ratePlanCodes);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class RateV3Dto {\n");
    sb.append("    corporateRates: ").append(toIndentedString(corporateRates)).append("\n");
    sb.append("    ratePlanCodes: ").append(toIndentedString(ratePlanCodes)).append("\n");
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

