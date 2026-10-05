package uk.co.whitbread.hotel.reservation.generated.models;

import java.net.URI;
import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import org.springframework.lang.Nullable;
import org.openapitools.jackson.nullable.JsonNullable;
import java.time.OffsetDateTime;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import io.swagger.v3.oas.annotations.media.Schema;


import java.util.*;
import jakarta.annotation.Generated;

/**
 * PaymentOptionsDto
 */

@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2026-09-04T08:09:52.163805+03:00[Europe/Bucharest]", comments = "Generator version: 7.12.0")
public class PaymentOptionsDto {

  private @Nullable Boolean payNow;

  private @Nullable Boolean payOnArrival;

  public PaymentOptionsDto payNow(Boolean payNow) {
    this.payNow = payNow;
    return this;
  }

  /**
   * Get payNow
   * @return payNow
   */
  
  @Schema(name = "payNow", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("payNow")
  public Boolean getPayNow() {
    return payNow;
  }

  public void setPayNow(Boolean payNow) {
    this.payNow = payNow;
  }

  public PaymentOptionsDto payOnArrival(Boolean payOnArrival) {
    this.payOnArrival = payOnArrival;
    return this;
  }

  /**
   * Get payOnArrival
   * @return payOnArrival
   */
  
  @Schema(name = "payOnArrival", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("payOnArrival")
  public Boolean getPayOnArrival() {
    return payOnArrival;
  }

  public void setPayOnArrival(Boolean payOnArrival) {
    this.payOnArrival = payOnArrival;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    PaymentOptionsDto paymentOptionsDto = (PaymentOptionsDto) o;
    return Objects.equals(this.payNow, paymentOptionsDto.payNow) &&
        Objects.equals(this.payOnArrival, paymentOptionsDto.payOnArrival);
  }

  @Override
  public int hashCode() {
    return Objects.hash(payNow, payOnArrival);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class PaymentOptionsDto {\n");
    sb.append("    payNow: ").append(toIndentedString(payNow)).append("\n");
    sb.append("    payOnArrival: ").append(toIndentedString(payOnArrival)).append("\n");
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

