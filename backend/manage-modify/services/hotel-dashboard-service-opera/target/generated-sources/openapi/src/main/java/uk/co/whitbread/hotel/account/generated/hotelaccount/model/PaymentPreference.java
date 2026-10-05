package uk.co.whitbread.hotel.account.generated.hotelaccount.model;

import java.net.URI;
import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import org.jspecify.annotations.Nullable;
import uk.co.whitbread.hotel.account.generated.hotelaccount.model.PaymentCard;
import org.openapitools.jackson.nullable.JsonNullable;
import java.time.OffsetDateTime;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import io.swagger.v3.oas.annotations.media.Schema;


import java.util.*;
import jakarta.annotation.Generated;

/**
 * PaymentPreference
 */

@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2026-08-25T07:50:33.863804+03:00[Europe/Bucharest]", comments = "Generator version: 7.12.0")
public class PaymentPreference {

  private @Nullable Boolean electronicInvoiceRequired;

  private @Nullable PaymentCard paymentCard;

  public PaymentPreference electronicInvoiceRequired(Boolean electronicInvoiceRequired) {
    this.electronicInvoiceRequired = electronicInvoiceRequired;
    return this;
  }

  /**
   * Get electronicInvoiceRequired
   * @return electronicInvoiceRequired
   */
  
  @Schema(name = "electronicInvoiceRequired", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("electronicInvoiceRequired")
  public Boolean getElectronicInvoiceRequired() {
    return electronicInvoiceRequired;
  }

  public void setElectronicInvoiceRequired(Boolean electronicInvoiceRequired) {
    this.electronicInvoiceRequired = electronicInvoiceRequired;
  }

  public PaymentPreference paymentCard(PaymentCard paymentCard) {
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
  public PaymentCard getPaymentCard() {
    return paymentCard;
  }

  public void setPaymentCard(PaymentCard paymentCard) {
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
    PaymentPreference paymentPreference = (PaymentPreference) o;
    return Objects.equals(this.electronicInvoiceRequired, paymentPreference.electronicInvoiceRequired) &&
        Objects.equals(this.paymentCard, paymentPreference.paymentCard);
  }

  @Override
  public int hashCode() {
    return Objects.hash(electronicInvoiceRequired, paymentCard);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class PaymentPreference {\n");
    sb.append("    electronicInvoiceRequired: ").append(toIndentedString(electronicInvoiceRequired)).append("\n");
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

