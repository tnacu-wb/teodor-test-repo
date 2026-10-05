package uk.co.whitbread.refund.processor.generated.models.payments;

import java.net.URI;
import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonTypeName;
import org.springframework.lang.Nullable;
import uk.co.whitbread.refund.processor.generated.models.payments.RefundResponseProviderResponseDto;
import org.openapitools.jackson.nullable.JsonNullable;
import java.time.OffsetDateTime;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import io.swagger.v3.oas.annotations.media.Schema;


import java.util.*;
import jakarta.annotation.Generated;

/**
 * RefundResponseDto
 */

@JsonTypeName("RefundResponse")
@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2026-09-04T08:12:20.597747+03:00[Europe/Bucharest]", comments = "Generator version: 7.12.0")
public class RefundResponseDto {

  private @Nullable String paymentId;

  private @Nullable Boolean refunded;

  private @Nullable String requestId;

  private @Nullable String refundId;

  private @Nullable RefundResponseProviderResponseDto providerResponse;

  public RefundResponseDto paymentId(String paymentId) {
    this.paymentId = paymentId;
    return this;
  }

  /**
   * Get paymentId
   * @return paymentId
   */
  
  @Schema(name = "paymentId", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("paymentId")
  public String getPaymentId() {
    return paymentId;
  }

  public void setPaymentId(String paymentId) {
    this.paymentId = paymentId;
  }

  public RefundResponseDto refunded(Boolean refunded) {
    this.refunded = refunded;
    return this;
  }

  /**
   * Get refunded
   * @return refunded
   */
  
  @Schema(name = "refunded", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("refunded")
  public Boolean getRefunded() {
    return refunded;
  }

  public void setRefunded(Boolean refunded) {
    this.refunded = refunded;
  }

  public RefundResponseDto requestId(String requestId) {
    this.requestId = requestId;
    return this;
  }

  /**
   * Get requestId
   * @return requestId
   */
  
  @Schema(name = "requestId", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("requestId")
  public String getRequestId() {
    return requestId;
  }

  public void setRequestId(String requestId) {
    this.requestId = requestId;
  }

  public RefundResponseDto refundId(String refundId) {
    this.refundId = refundId;
    return this;
  }

  /**
   * Get refundId
   * @return refundId
   */
  
  @Schema(name = "refundId", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("refundId")
  public String getRefundId() {
    return refundId;
  }

  public void setRefundId(String refundId) {
    this.refundId = refundId;
  }

  public RefundResponseDto providerResponse(RefundResponseProviderResponseDto providerResponse) {
    this.providerResponse = providerResponse;
    return this;
  }

  /**
   * Get providerResponse
   * @return providerResponse
   */
  @Valid 
  @Schema(name = "ProviderResponse", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("ProviderResponse")
  public RefundResponseProviderResponseDto getProviderResponse() {
    return providerResponse;
  }

  public void setProviderResponse(RefundResponseProviderResponseDto providerResponse) {
    this.providerResponse = providerResponse;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    RefundResponseDto refundResponse = (RefundResponseDto) o;
    return Objects.equals(this.paymentId, refundResponse.paymentId) &&
        Objects.equals(this.refunded, refundResponse.refunded) &&
        Objects.equals(this.requestId, refundResponse.requestId) &&
        Objects.equals(this.refundId, refundResponse.refundId) &&
        Objects.equals(this.providerResponse, refundResponse.providerResponse);
  }

  @Override
  public int hashCode() {
    return Objects.hash(paymentId, refunded, requestId, refundId, providerResponse);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class RefundResponseDto {\n");
    sb.append("    paymentId: ").append(toIndentedString(paymentId)).append("\n");
    sb.append("    refunded: ").append(toIndentedString(refunded)).append("\n");
    sb.append("    requestId: ").append(toIndentedString(requestId)).append("\n");
    sb.append("    refundId: ").append(toIndentedString(refundId)).append("\n");
    sb.append("    providerResponse: ").append(toIndentedString(providerResponse)).append("\n");
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

