package uk.co.whitbread.cdh.generated.models;

import java.net.URI;
import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import org.springframework.lang.Nullable;
import uk.co.whitbread.cdh.generated.models.PaymentPreferencePaymentCardDto;
import org.openapitools.jackson.nullable.JsonNullable;
import java.time.OffsetDateTime;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import io.swagger.v3.oas.annotations.media.Schema;


import java.util.*;
import jakarta.annotation.Generated;

/**
 * PaymentPreferenceDto
 */

@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2026-08-25T07:50:16.086993+03:00[Europe/Bucharest]", comments = "Generator version: 7.12.0")
public class PaymentPreferenceDto {

  private @Nullable PaymentPreferencePaymentCardDto paymentCard;

  public PaymentPreferenceDto paymentCard(PaymentPreferencePaymentCardDto paymentCard) {
    this.paymentCard = paymentCard;
    return this;
  }

  /**
   * Get paymentCard
   * @return paymentCard
   */
  @Valid 
  @Schema(name = "paymentCard", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("paymentCard")
  public PaymentPreferencePaymentCardDto getPaymentCard() {
    return paymentCard;
  }

  public void setPaymentCard(PaymentPreferencePaymentCardDto paymentCard) {
    this.paymentCard = paymentCard;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    PaymentPreferenceDto paymentPreferenceDto = (PaymentPreferenceDto) o;
    return Objects.equals(this.paymentCard, paymentPreferenceDto.paymentCard);
  }

  @Override
  public int hashCode() {
    return Objects.hash(paymentCard);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class PaymentPreferenceDto {\n");
    sb.append("    paymentCard: ").append(toIndentedString(paymentCard)).append("\n");
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

