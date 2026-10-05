package uk.co.whitbread.hotel.ohip.adapter.generated.models;

import java.net.URI;
import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import org.springframework.lang.Nullable;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.ReservationTaxTypeInfoSingleCall;
import org.openapitools.jackson.nullable.JsonNullable;
import java.time.OffsetDateTime;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import io.swagger.v3.oas.annotations.media.Schema;


import java.util.*;
import jakarta.annotation.Generated;

/**
 * ResCashieringTypeSingleCall
 */

@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2026-08-27T18:50:34.031950+03:00[Europe/Bucharest]", comments = "Generator version: 7.12.0")
public class ResCashieringTypeSingleCall {

  private @Nullable ReservationTaxTypeInfoSingleCall taxType;

  public ResCashieringTypeSingleCall taxType(ReservationTaxTypeInfoSingleCall taxType) {
    this.taxType = taxType;
    return this;
  }

  /**
   * Get taxType
   * @return taxType
   */
  @Valid 
  @Schema(name = "taxType", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("taxType")
  public ReservationTaxTypeInfoSingleCall getTaxType() {
    return taxType;
  }

  public void setTaxType(ReservationTaxTypeInfoSingleCall taxType) {
    this.taxType = taxType;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    ResCashieringTypeSingleCall resCashieringTypeSingleCall = (ResCashieringTypeSingleCall) o;
    return Objects.equals(this.taxType, resCashieringTypeSingleCall.taxType);
  }

  @Override
  public int hashCode() {
    return Objects.hash(taxType);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class ResCashieringTypeSingleCall {\n");
    sb.append("    taxType: ").append(toIndentedString(taxType)).append("\n");
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

