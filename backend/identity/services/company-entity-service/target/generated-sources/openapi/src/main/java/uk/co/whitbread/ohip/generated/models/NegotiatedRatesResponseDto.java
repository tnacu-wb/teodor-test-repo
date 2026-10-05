package uk.co.whitbread.ohip.generated.models;

import java.net.URI;
import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.springframework.lang.Nullable;
import uk.co.whitbread.ohip.generated.models.NegotiatedRateDto;
import org.openapitools.jackson.nullable.JsonNullable;
import java.time.OffsetDateTime;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import io.swagger.v3.oas.annotations.media.Schema;


import java.util.*;
import jakarta.annotation.Generated;

/**
 * NegotiatedRatesResponseDto
 */

@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2026-08-25T07:50:12.143180+03:00[Europe/Bucharest]", comments = "Generator version: 7.12.0")
public class NegotiatedRatesResponseDto {

  @Valid
  private List<@Valid NegotiatedRateDto> negotiatedRates = new ArrayList<>();

  public NegotiatedRatesResponseDto negotiatedRates(List<@Valid NegotiatedRateDto> negotiatedRates) {
    this.negotiatedRates = negotiatedRates;
    return this;
  }

  public NegotiatedRatesResponseDto addNegotiatedRatesItem(NegotiatedRateDto negotiatedRatesItem) {
    if (this.negotiatedRates == null) {
      this.negotiatedRates = new ArrayList<>();
    }
    this.negotiatedRates.add(negotiatedRatesItem);
    return this;
  }

  /**
   * Get negotiatedRates
   * @return negotiatedRates
   */
  @Valid 
  @Schema(name = "negotiatedRates", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("negotiatedRates")
  public List<@Valid NegotiatedRateDto> getNegotiatedRates() {
    return negotiatedRates;
  }

  public void setNegotiatedRates(List<@Valid NegotiatedRateDto> negotiatedRates) {
    this.negotiatedRates = negotiatedRates;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    NegotiatedRatesResponseDto negotiatedRatesResponseDto = (NegotiatedRatesResponseDto) o;
    return Objects.equals(this.negotiatedRates, negotiatedRatesResponseDto.negotiatedRates);
  }

  @Override
  public int hashCode() {
    return Objects.hash(negotiatedRates);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class NegotiatedRatesResponseDto {\n");
    sb.append("    negotiatedRates: ").append(toIndentedString(negotiatedRates)).append("\n");
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

