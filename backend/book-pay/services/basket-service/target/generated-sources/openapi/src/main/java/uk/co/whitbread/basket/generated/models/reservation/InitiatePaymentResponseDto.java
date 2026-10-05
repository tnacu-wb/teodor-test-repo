package uk.co.whitbread.basket.generated.models.reservation;

import java.net.URI;
import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import org.springframework.lang.Nullable;
import uk.co.whitbread.basket.generated.models.reservation.PaymentRequiredDetailsDto;
import org.openapitools.jackson.nullable.JsonNullable;
import java.time.OffsetDateTime;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import io.swagger.v3.oas.annotations.media.Schema;


import java.util.*;
import jakarta.annotation.Generated;

/**
 * InitiatePaymentResponseDto
 */
@com.fasterxml.jackson.annotation.JsonIgnoreProperties(ignoreUnknown = true)

@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2026-09-04T08:10:03.993735+03:00[Europe/Bucharest]", comments = "Generator version: 7.12.0")
public class InitiatePaymentResponseDto {

  private @Nullable PaymentRequiredDetailsDto paymentRequiredDetails;

  /**
   * Gets or Sets status
   */
  public enum StatusEnum {
    NOT_REQUIRED("NOT_REQUIRED"),
    
    PAYMENT_REQUIRED("PAYMENT_REQUIRED");

    private String value;

    StatusEnum(String value) {
      this.value = value;
    }

    @JsonValue
    public String getValue() {
      return value;
    }

    @Override
    public String toString() {
      return String.valueOf(value);
    }

    @JsonCreator
    public static StatusEnum fromValue(String value) {
      for (StatusEnum b : StatusEnum.values()) {
        if (b.value.equals(value)) {
          return b;
        }
      }
      throw new IllegalArgumentException("Unexpected value '" + value + "'");
    }
  }

  private @Nullable StatusEnum status;

  public InitiatePaymentResponseDto paymentRequiredDetails(PaymentRequiredDetailsDto paymentRequiredDetails) {
    this.paymentRequiredDetails = paymentRequiredDetails;
    return this;
  }

  /**
   * Get paymentRequiredDetails
   * @return paymentRequiredDetails
   */
  @Valid 
  @Schema(name = "paymentRequiredDetails", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("paymentRequiredDetails")
  public PaymentRequiredDetailsDto getPaymentRequiredDetails() {
    return paymentRequiredDetails;
  }

  public void setPaymentRequiredDetails(PaymentRequiredDetailsDto paymentRequiredDetails) {
    this.paymentRequiredDetails = paymentRequiredDetails;
  }

  public InitiatePaymentResponseDto status(StatusEnum status) {
    this.status = status;
    return this;
  }

  /**
   * Get status
   * @return status
   */
  
  @Schema(name = "status", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("status")
  public StatusEnum getStatus() {
    return status;
  }

  public void setStatus(StatusEnum status) {
    this.status = status;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    InitiatePaymentResponseDto initiatePaymentResponseDto = (InitiatePaymentResponseDto) o;
    return Objects.equals(this.paymentRequiredDetails, initiatePaymentResponseDto.paymentRequiredDetails) &&
        Objects.equals(this.status, initiatePaymentResponseDto.status);
  }

  @Override
  public int hashCode() {
    return Objects.hash(paymentRequiredDetails, status);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class InitiatePaymentResponseDto {\n");
    sb.append("    paymentRequiredDetails: ").append(toIndentedString(paymentRequiredDetails)).append("\n");
    sb.append("    status: ").append(toIndentedString(status)).append("\n");
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

