package uk.co.whitbread.hotel.generated.models.basket;

import java.net.URI;
import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import org.springframework.lang.Nullable;
import uk.co.whitbread.hotel.generated.models.basket.CurrencyAmountTypeDto;
import org.openapitools.jackson.nullable.JsonNullable;
import java.time.OffsetDateTime;
import javax.validation.Valid;
import javax.validation.constraints.*;
import io.swagger.v3.oas.annotations.media.Schema;


import java.util.*;
import javax.annotation.Generated;

/**
 * DepositsDto
 */

@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2026-09-04T08:11:22.312200+03:00[Europe/Bucharest]", comments = "Generator version: 7.12.0")
public class DepositsDto {

  private @Nullable String paymentReference;

  private @Nullable CurrencyAmountTypeDto postedAmount;

  public DepositsDto paymentReference(String paymentReference) {
    this.paymentReference = paymentReference;
    return this;
  }

  /**
   * Get paymentReference
   * @return paymentReference
   */
  
  @Schema(name = "paymentReference", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("paymentReference")
  public String getPaymentReference() {
    return paymentReference;
  }

  public void setPaymentReference(String paymentReference) {
    this.paymentReference = paymentReference;
  }

  public DepositsDto postedAmount(CurrencyAmountTypeDto postedAmount) {
    this.postedAmount = postedAmount;
    return this;
  }

  /**
   * Get postedAmount
   * @return postedAmount
   */
  @Valid 
  @Schema(name = "postedAmount", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("postedAmount")
  public CurrencyAmountTypeDto getPostedAmount() {
    return postedAmount;
  }

  public void setPostedAmount(CurrencyAmountTypeDto postedAmount) {
    this.postedAmount = postedAmount;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    DepositsDto depositsDto = (DepositsDto) o;
    return Objects.equals(this.paymentReference, depositsDto.paymentReference) &&
        Objects.equals(this.postedAmount, depositsDto.postedAmount);
  }

  @Override
  public int hashCode() {
    return Objects.hash(paymentReference, postedAmount);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class DepositsDto {\n");
    sb.append("    paymentReference: ").append(toIndentedString(paymentReference)).append("\n");
    sb.append("    postedAmount: ").append(toIndentedString(postedAmount)).append("\n");
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

