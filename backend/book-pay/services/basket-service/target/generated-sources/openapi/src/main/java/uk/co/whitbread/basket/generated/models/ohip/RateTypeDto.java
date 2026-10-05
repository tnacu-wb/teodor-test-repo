package uk.co.whitbread.basket.generated.models.ohip;

import java.net.URI;
import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.springframework.lang.Nullable;
import uk.co.whitbread.basket.generated.models.ohip.AmountTypeDto;
import org.openapitools.jackson.nullable.JsonNullable;
import java.time.OffsetDateTime;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import io.swagger.v3.oas.annotations.media.Schema;


import java.util.*;
import jakarta.annotation.Generated;

/**
 * RateTypeDto
 */
@com.fasterxml.jackson.annotation.JsonIgnoreProperties(ignoreUnknown = true)

@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2026-09-04T08:10:10.951524+03:00[Europe/Bucharest]", comments = "Generator version: 7.12.0")
public class RateTypeDto {

  @Valid
  private List<@Valid AmountTypeDto> rate = new ArrayList<>();

  public RateTypeDto() {
    super();
  }

  /**
   * Constructor with only required parameters
   */
  public RateTypeDto(List<@Valid AmountTypeDto> rate) {
    this.rate = rate;
  }

  public RateTypeDto rate(List<@Valid AmountTypeDto> rate) {
    this.rate = rate;
    return this;
  }

  public RateTypeDto addRateItem(AmountTypeDto rateItem) {
    if (this.rate == null) {
      this.rate = new ArrayList<>();
    }
    this.rate.add(rateItem);
    return this;
  }

  /**
   * Get rate
   * @return rate
   */
  @NotNull @Valid @Size(min = 1, max = 2147483647) 
  @Schema(name = "rate", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("rate")
  public List<@Valid AmountTypeDto> getRate() {
    return rate;
  }

  public void setRate(List<@Valid AmountTypeDto> rate) {
    this.rate = rate;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    RateTypeDto rateTypeDto = (RateTypeDto) o;
    return Objects.equals(this.rate, rateTypeDto.rate);
  }

  @Override
  public int hashCode() {
    return Objects.hash(rate);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class RateTypeDto {\n");
    sb.append("    rate: ").append(toIndentedString(rate)).append("\n");
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

