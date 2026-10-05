package uk.co.whitbread.hotel.generated.models.basket;

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
 * PaymentRequiredDetails
 */

@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2026-09-04T08:11:22.312200+03:00[Europe/Bucharest]", comments = "Generator version: 7.12.0")
public class PaymentRequiredDetails {

  private @Nullable String paymentRedirect;

  private @Nullable String providerUrl;

  private @Nullable String sessionId;

  private @Nullable String template;

  public PaymentRequiredDetails paymentRedirect(String paymentRedirect) {
    this.paymentRedirect = paymentRedirect;
    return this;
  }

  /**
   * Get paymentRedirect
   * @return paymentRedirect
   */
  
  @Schema(name = "paymentRedirect", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("paymentRedirect")
  public String getPaymentRedirect() {
    return paymentRedirect;
  }

  public void setPaymentRedirect(String paymentRedirect) {
    this.paymentRedirect = paymentRedirect;
  }

  public PaymentRequiredDetails providerUrl(String providerUrl) {
    this.providerUrl = providerUrl;
    return this;
  }

  /**
   * Get providerUrl
   * @return providerUrl
   */
  
  @Schema(name = "providerUrl", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("providerUrl")
  public String getProviderUrl() {
    return providerUrl;
  }

  public void setProviderUrl(String providerUrl) {
    this.providerUrl = providerUrl;
  }

  public PaymentRequiredDetails sessionId(String sessionId) {
    this.sessionId = sessionId;
    return this;
  }

  /**
   * Get sessionId
   * @return sessionId
   */
  
  @Schema(name = "sessionId", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("sessionId")
  public String getSessionId() {
    return sessionId;
  }

  public void setSessionId(String sessionId) {
    this.sessionId = sessionId;
  }

  public PaymentRequiredDetails template(String template) {
    this.template = template;
    return this;
  }

  /**
   * Get template
   * @return template
   */
  
  @Schema(name = "template", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("template")
  public String getTemplate() {
    return template;
  }

  public void setTemplate(String template) {
    this.template = template;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    PaymentRequiredDetails paymentRequiredDetails = (PaymentRequiredDetails) o;
    return Objects.equals(this.paymentRedirect, paymentRequiredDetails.paymentRedirect) &&
        Objects.equals(this.providerUrl, paymentRequiredDetails.providerUrl) &&
        Objects.equals(this.sessionId, paymentRequiredDetails.sessionId) &&
        Objects.equals(this.template, paymentRequiredDetails.template);
  }

  @Override
  public int hashCode() {
    return Objects.hash(paymentRedirect, providerUrl, sessionId, template);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class PaymentRequiredDetails {\n");
    sb.append("    paymentRedirect: ").append(toIndentedString(paymentRedirect)).append("\n");
    sb.append("    providerUrl: ").append(toIndentedString(providerUrl)).append("\n");
    sb.append("    sessionId: ").append(toIndentedString(sessionId)).append("\n");
    sb.append("    template: ").append(toIndentedString(template)).append("\n");
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

