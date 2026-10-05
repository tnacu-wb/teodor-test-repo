package uk.co.whitbread.basket.generated.models.reservation;

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
 * PaymentRequiredDetailsDto
 */
@com.fasterxml.jackson.annotation.JsonIgnoreProperties(ignoreUnknown = true)

@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2026-09-04T08:10:03.993735+03:00[Europe/Bucharest]", comments = "Generator version: 7.12.0")
public class PaymentRequiredDetailsDto {

  private @Nullable String paymentRedirect;

  private @Nullable String sessionId;

  private @Nullable String template;

  public PaymentRequiredDetailsDto paymentRedirect(String paymentRedirect) {
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

  public PaymentRequiredDetailsDto sessionId(String sessionId) {
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

  public PaymentRequiredDetailsDto template(String template) {
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
    PaymentRequiredDetailsDto paymentRequiredDetailsDto = (PaymentRequiredDetailsDto) o;
    return Objects.equals(this.paymentRedirect, paymentRequiredDetailsDto.paymentRedirect) &&
        Objects.equals(this.sessionId, paymentRequiredDetailsDto.sessionId) &&
        Objects.equals(this.template, paymentRequiredDetailsDto.template);
  }

  @Override
  public int hashCode() {
    return Objects.hash(paymentRedirect, sessionId, template);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class PaymentRequiredDetailsDto {\n");
    sb.append("    paymentRedirect: ").append(toIndentedString(paymentRedirect)).append("\n");
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

