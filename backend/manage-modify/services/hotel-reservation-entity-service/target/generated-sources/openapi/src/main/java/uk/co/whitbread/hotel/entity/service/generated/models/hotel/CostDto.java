package uk.co.whitbread.hotel.entity.service.generated.models.hotel;

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
 * CostDto
 */

@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2026-08-27T18:51:33.749132+03:00[Europe/Bucharest]", comments = "Generator version: 7.12.0")
public class CostDto {

  private @Nullable String currencyCode;

  private @Nullable BigDecimal netTotal;

  public CostDto currencyCode(String currencyCode) {
    this.currencyCode = currencyCode;
    return this;
  }

  /**
   * Get currencyCode
   * @return currencyCode
   */
  
  @Schema(name = "currencyCode", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("currencyCode")
  public String getCurrencyCode() {
    return currencyCode;
  }

  public void setCurrencyCode(String currencyCode) {
    this.currencyCode = currencyCode;
  }

  public CostDto netTotal(BigDecimal netTotal) {
    this.netTotal = netTotal;
    return this;
  }

  /**
   * Get netTotal
   * @return netTotal
   */
  @Valid 
  @Schema(name = "netTotal", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("netTotal")
  public BigDecimal getNetTotal() {
    return netTotal;
  }

  public void setNetTotal(BigDecimal netTotal) {
    this.netTotal = netTotal;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    CostDto costDto = (CostDto) o;
    return Objects.equals(this.currencyCode, costDto.currencyCode) &&
        Objects.equals(this.netTotal, costDto.netTotal);
  }

  @Override
  public int hashCode() {
    return Objects.hash(currencyCode, netTotal);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class CostDto {\n");
    sb.append("    currencyCode: ").append(toIndentedString(currencyCode)).append("\n");
    sb.append("    netTotal: ").append(toIndentedString(netTotal)).append("\n");
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

