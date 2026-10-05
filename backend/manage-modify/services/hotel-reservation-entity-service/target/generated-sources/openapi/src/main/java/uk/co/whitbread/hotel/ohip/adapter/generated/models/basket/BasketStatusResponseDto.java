package uk.co.whitbread.hotel.ohip.adapter.generated.models.basket;

import java.net.URI;
import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import org.springframework.lang.Nullable;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.basket.BasketErrorDto;
import org.openapitools.jackson.nullable.JsonNullable;
import java.time.OffsetDateTime;
import javax.validation.Valid;
import javax.validation.constraints.*;
import io.swagger.v3.oas.annotations.media.Schema;


import java.util.*;
import javax.annotation.Generated;

/**
 * BasketStatusResponseDto
 */

@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2026-08-27T18:51:25.444555+03:00[Europe/Bucharest]", comments = "Generator version: 7.12.0")
public class BasketStatusResponseDto {

  private @Nullable BasketErrorDto basketError;

  private String basketReference;

  /**
   * Gets or Sets basketStatus
   */
  public enum BasketStatusEnum {
    OPEN("OPEN"),
    
    PROCESSING("PROCESSING"),
    
    AMENDING("AMENDING"),
    
    COMPLETED("COMPLETED"),
    
    AMENDED("AMENDED"),
    
    PRE_CHECKED_IN("PRE_CHECKED_IN"),
    
    CANCELLED("CANCELLED"),
    
    PAY_PENDING("PAY_PENDING"),
    
    AMEND_FAILED("AMEND_FAILED"),
    
    FAILED("FAILED"),
    
    CIOL_FAILED("CIOL_FAILED"),
    
    PRE_CHECKED_OUT("PRE_CHECKED_OUT"),
    
    CIOL_RC_FAILED("CIOL_RC_FAILED"),
    
    SECURE_FAILED("SECURE_FAILED");

    private String value;

    BasketStatusEnum(String value) {
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
    public static BasketStatusEnum fromValue(String value) {
      for (BasketStatusEnum b : BasketStatusEnum.values()) {
        if (b.value.equals(value)) {
          return b;
        }
      }
      throw new IllegalArgumentException("Unexpected value '" + value + "'");
    }
  }

  private BasketStatusEnum basketStatus;

  private String createdAt;

  private @Nullable Boolean retryPayment;

  public BasketStatusResponseDto() {
    super();
  }

  /**
   * Constructor with only required parameters
   */
  public BasketStatusResponseDto(String basketReference, BasketStatusEnum basketStatus, String createdAt) {
    this.basketReference = basketReference;
    this.basketStatus = basketStatus;
    this.createdAt = createdAt;
  }

  public BasketStatusResponseDto basketError(BasketErrorDto basketError) {
    this.basketError = basketError;
    return this;
  }

  /**
   * Get basketError
   * @return basketError
   */
  @Valid 
  @Schema(name = "basketError", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("basketError")
  public BasketErrorDto getBasketError() {
    return basketError;
  }

  public void setBasketError(BasketErrorDto basketError) {
    this.basketError = basketError;
  }

  public BasketStatusResponseDto basketReference(String basketReference) {
    this.basketReference = basketReference;
    return this;
  }

  /**
   * Get basketReference
   * @return basketReference
   */
  @NotNull 
  @Schema(name = "basketReference", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("basketReference")
  public String getBasketReference() {
    return basketReference;
  }

  public void setBasketReference(String basketReference) {
    this.basketReference = basketReference;
  }

  public BasketStatusResponseDto basketStatus(BasketStatusEnum basketStatus) {
    this.basketStatus = basketStatus;
    return this;
  }

  /**
   * Get basketStatus
   * @return basketStatus
   */
  @NotNull 
  @Schema(name = "basketStatus", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("basketStatus")
  public BasketStatusEnum getBasketStatus() {
    return basketStatus;
  }

  public void setBasketStatus(BasketStatusEnum basketStatus) {
    this.basketStatus = basketStatus;
  }

  public BasketStatusResponseDto createdAt(String createdAt) {
    this.createdAt = createdAt;
    return this;
  }

  /**
   * Get createdAt
   * @return createdAt
   */
  @NotNull 
  @Schema(name = "createdAt", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("createdAt")
  public String getCreatedAt() {
    return createdAt;
  }

  public void setCreatedAt(String createdAt) {
    this.createdAt = createdAt;
  }

  public BasketStatusResponseDto retryPayment(Boolean retryPayment) {
    this.retryPayment = retryPayment;
    return this;
  }

  /**
   * Get retryPayment
   * @return retryPayment
   */
  
  @Schema(name = "retryPayment", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("retryPayment")
  public Boolean getRetryPayment() {
    return retryPayment;
  }

  public void setRetryPayment(Boolean retryPayment) {
    this.retryPayment = retryPayment;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    BasketStatusResponseDto basketStatusResponseDto = (BasketStatusResponseDto) o;
    return Objects.equals(this.basketError, basketStatusResponseDto.basketError) &&
        Objects.equals(this.basketReference, basketStatusResponseDto.basketReference) &&
        Objects.equals(this.basketStatus, basketStatusResponseDto.basketStatus) &&
        Objects.equals(this.createdAt, basketStatusResponseDto.createdAt) &&
        Objects.equals(this.retryPayment, basketStatusResponseDto.retryPayment);
  }

  @Override
  public int hashCode() {
    return Objects.hash(basketError, basketReference, basketStatus, createdAt, retryPayment);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class BasketStatusResponseDto {\n");
    sb.append("    basketError: ").append(toIndentedString(basketError)).append("\n");
    sb.append("    basketReference: ").append(toIndentedString(basketReference)).append("\n");
    sb.append("    basketStatus: ").append(toIndentedString(basketStatus)).append("\n");
    sb.append("    createdAt: ").append(toIndentedString(createdAt)).append("\n");
    sb.append("    retryPayment: ").append(toIndentedString(retryPayment)).append("\n");
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

