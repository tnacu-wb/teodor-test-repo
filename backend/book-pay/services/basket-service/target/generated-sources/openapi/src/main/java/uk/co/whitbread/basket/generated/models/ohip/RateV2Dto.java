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
 * RateV2Dto
 */
@com.fasterxml.jackson.annotation.JsonIgnoreProperties(ignoreUnknown = true)

@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2026-09-04T08:10:10.951524+03:00[Europe/Bucharest]", comments = "Generator version: 7.12.0")
public class RateV2Dto {

  private @Nullable CorporateRateDto corporateRates;

  @Valid
  private List<String> ratePlanCodes = new ArrayList<>();

  public RateV2Dto corporateRates(CorporateRateDto corporateRates) {
    this.corporateRates = corporateRates;
    return this;
  }

  /**
   * Get corporateRates
   * @return corporateRates
   */
  @Valid 
  @Schema(name = "corporateRates", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("corporateRates")
  public CorporateRateDto getCorporateRates() {
    return corporateRates;
  }

  public void setCorporateRates(CorporateRateDto corporateRates) {
    this.corporateRates = corporateRates;
  }

  public RateV2Dto ratePlanCodes(List<String> ratePlanCodes) {
    this.ratePlanCodes = ratePlanCodes;
    return this;
  }

  public RateV2Dto addRatePlanCodesItem(String ratePlanCodesItem) {
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
    RateV2Dto rateV2Dto = (RateV2Dto) o;
    return Objects.equals(this.corporateRates, rateV2Dto.corporateRates) &&
        Objects.equals(this.ratePlanCodes, rateV2Dto.ratePlanCodes);
  }

  @Override
  public int hashCode() {
    return Objects.hash(corporateRates, ratePlanCodes);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class RateV2Dto {\n");
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

