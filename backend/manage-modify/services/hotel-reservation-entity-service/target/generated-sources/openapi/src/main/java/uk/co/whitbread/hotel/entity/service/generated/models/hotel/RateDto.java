package uk.co.whitbread.hotel.entity.service.generated.models.hotel;

import java.net.URI;
import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.springframework.lang.Nullable;
import uk.co.whitbread.hotel.entity.service.generated.models.hotel.CorporateRateDto;
import org.openapitools.jackson.nullable.JsonNullable;
import java.time.OffsetDateTime;
import javax.validation.Valid;
import javax.validation.constraints.*;
import io.swagger.v3.oas.annotations.media.Schema;


import java.util.*;
import javax.annotation.Generated;

/**
 * RateDto
 */

@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2026-08-27T18:51:33.749132+03:00[Europe/Bucharest]", comments = "Generator version: 7.12.0")
public class RateDto {

  private @Nullable CorporateRateDto corporateRates;

  @Valid
  private List<String> ratePlanCodes = new ArrayList<>();

  public RateDto corporateRates(CorporateRateDto corporateRates) {
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

  public RateDto ratePlanCodes(List<String> ratePlanCodes) {
    this.ratePlanCodes = ratePlanCodes;
    return this;
  }

  public RateDto addRatePlanCodesItem(String ratePlanCodesItem) {
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
    RateDto rateDto = (RateDto) o;
    return Objects.equals(this.corporateRates, rateDto.corporateRates) &&
        Objects.equals(this.ratePlanCodes, rateDto.ratePlanCodes);
  }

  @Override
  public int hashCode() {
    return Objects.hash(corporateRates, ratePlanCodes);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class RateDto {\n");
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

