package uk.co.whitbread.content.entity.service.generated.models.content;

import java.net.URI;
import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.springframework.lang.Nullable;
import uk.co.whitbread.content.entity.service.generated.models.content.PaymentMethodDto;
import org.openapitools.jackson.nullable.JsonNullable;
import java.time.OffsetDateTime;
import javax.validation.Valid;
import javax.validation.constraints.*;
import io.swagger.v3.oas.annotations.media.Schema;


import java.util.*;
import javax.annotation.Generated;

/**
 * PaymentProviderDto
 */

@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2026-08-27T18:51:28.563902+03:00[Europe/Bucharest]", comments = "Generator version: 7.12.0")
public class PaymentProviderDto {

  @Valid
  private List<@Valid PaymentMethodDto> paymentMethods = new ArrayList<>();

  private @Nullable String providerId;

  public PaymentProviderDto paymentMethods(List<@Valid PaymentMethodDto> paymentMethods) {
    this.paymentMethods = paymentMethods;
    return this;
  }

  public PaymentProviderDto addPaymentMethodsItem(PaymentMethodDto paymentMethodsItem) {
    if (this.paymentMethods == null) {
      this.paymentMethods = new ArrayList<>();
    }
    this.paymentMethods.add(paymentMethodsItem);
    return this;
  }

  /**
   * Get paymentMethods
   * @return paymentMethods
   */
  @Valid 
  @Schema(name = "paymentMethods", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("paymentMethods")
  public List<@Valid PaymentMethodDto> getPaymentMethods() {
    return paymentMethods;
  }

  public void setPaymentMethods(List<@Valid PaymentMethodDto> paymentMethods) {
    this.paymentMethods = paymentMethods;
  }

  public PaymentProviderDto providerId(String providerId) {
    this.providerId = providerId;
    return this;
  }

  /**
   * Get providerId
   * @return providerId
   */
  
  @Schema(name = "providerId", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("providerId")
  public String getProviderId() {
    return providerId;
  }

  public void setProviderId(String providerId) {
    this.providerId = providerId;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    PaymentProviderDto paymentProviderDto = (PaymentProviderDto) o;
    return Objects.equals(this.paymentMethods, paymentProviderDto.paymentMethods) &&
        Objects.equals(this.providerId, paymentProviderDto.providerId);
  }

  @Override
  public int hashCode() {
    return Objects.hash(paymentMethods, providerId);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class PaymentProviderDto {\n");
    sb.append("    paymentMethods: ").append(toIndentedString(paymentMethods)).append("\n");
    sb.append("    providerId: ").append(toIndentedString(providerId)).append("\n");
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

