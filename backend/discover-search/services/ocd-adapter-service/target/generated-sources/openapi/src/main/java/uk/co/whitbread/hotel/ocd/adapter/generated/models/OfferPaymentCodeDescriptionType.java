package uk.co.whitbread.hotel.ocd.adapter.generated.models;

import java.net.URI;
import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import org.springframework.lang.Nullable;
import org.openapitools.jackson.nullable.JsonNullable;
import java.time.OffsetDateTime;
import javax.validation.Valid;
import javax.validation.constraints.*;
import io.swagger.v3.oas.annotations.media.Schema;


import java.util.*;
import javax.annotation.Generated;

/**
 * Payment type entity which contains a code and description
 */

@Schema(name = "OfferPaymentCodeDescriptionType", description = "Payment type entity which contains a code and description")
@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2026-08-25T07:51:32.554166+03:00[Europe/Bucharest]", comments = "Generator version: 7.12.0")
public class OfferPaymentCodeDescriptionType {

  private @Nullable String paymentCode;

  private @Nullable String cardType;

  private @Nullable String paymentDescription;

  public OfferPaymentCodeDescriptionType paymentCode(String paymentCode) {
    this.paymentCode = paymentCode;
    return this;
  }

  /**
   * The code of the the payment type.
   * @return paymentCode
   */
  @Pattern(regexp = "'[A-Z0-9.]{0,20}'") @Size(min = 0, max = 20) 
  @Schema(name = "paymentCode", example = "AX", description = "The code of the the payment type.", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("paymentCode")
  public String getPaymentCode() {
    return paymentCode;
  }

  public void setPaymentCode(String paymentCode) {
    this.paymentCode = paymentCode;
  }

  public OfferPaymentCodeDescriptionType cardType(String cardType) {
    this.cardType = cardType;
    return this;
  }

  /**
   * The code of the the payment type.
   * @return cardType
   */
  @Pattern(regexp = "'[A-Z0-9.]{0,20}'") @Size(min = 0, max = 20) 
  @Schema(name = "cardType", example = "AX", description = "The code of the the payment type.", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("cardType")
  public String getCardType() {
    return cardType;
  }

  public void setCardType(String cardType) {
    this.cardType = cardType;
  }

  public OfferPaymentCodeDescriptionType paymentDescription(String paymentDescription) {
    this.paymentDescription = paymentDescription;
    return this;
  }

  /**
   * Description of the payment type.
   * @return paymentDescription
   */
  @Size(min = 0, max = 256) 
  @Schema(name = "paymentDescription", example = "Bank Card Name", description = "Description of the payment type.", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("paymentDescription")
  public String getPaymentDescription() {
    return paymentDescription;
  }

  public void setPaymentDescription(String paymentDescription) {
    this.paymentDescription = paymentDescription;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    OfferPaymentCodeDescriptionType offerPaymentCodeDescriptionType = (OfferPaymentCodeDescriptionType) o;
    return Objects.equals(this.paymentCode, offerPaymentCodeDescriptionType.paymentCode) &&
        Objects.equals(this.cardType, offerPaymentCodeDescriptionType.cardType) &&
        Objects.equals(this.paymentDescription, offerPaymentCodeDescriptionType.paymentDescription);
  }

  @Override
  public int hashCode() {
    return Objects.hash(paymentCode, cardType, paymentDescription);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class OfferPaymentCodeDescriptionType {\n");
    sb.append("    paymentCode: ").append(toIndentedString(paymentCode)).append("\n");
    sb.append("    cardType: ").append(toIndentedString(cardType)).append("\n");
    sb.append("    paymentDescription: ").append(toIndentedString(paymentDescription)).append("\n");
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

