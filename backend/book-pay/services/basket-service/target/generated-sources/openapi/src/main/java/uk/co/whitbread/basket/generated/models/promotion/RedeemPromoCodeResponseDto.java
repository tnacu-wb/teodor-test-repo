package uk.co.whitbread.basket.generated.models.promotion;

import java.net.URI;
import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import org.springframework.lang.Nullable;
import uk.co.whitbread.basket.generated.models.promotion.RedeemStatus;
import org.openapitools.jackson.nullable.JsonNullable;
import java.time.OffsetDateTime;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import io.swagger.v3.oas.annotations.media.Schema;


import java.util.*;
import jakarta.annotation.Generated;

/**
 * RedeemPromoCodeResponseDto
 */
@com.fasterxml.jackson.annotation.JsonIgnoreProperties(ignoreUnknown = true)

@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2026-09-04T08:10:16.998949+03:00[Europe/Bucharest]", comments = "Generator version: 7.12.0")
public class RedeemPromoCodeResponseDto {

  private @Nullable Boolean success;

  private @Nullable RedeemStatus status;

  private @Nullable String promoCode;

  private @Nullable String message;

  public RedeemPromoCodeResponseDto success(Boolean success) {
    this.success = success;
    return this;
  }

  /**
   * Get success
   * @return success
   */
  
  @Schema(name = "success", example = "true", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("success")
  public Boolean getSuccess() {
    return success;
  }

  public void setSuccess(Boolean success) {
    this.success = success;
  }

  public RedeemPromoCodeResponseDto status(RedeemStatus status) {
    this.status = status;
    return this;
  }

  /**
   * Get status
   * @return status
   */
  @Valid 
  @Schema(name = "status", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("status")
  public RedeemStatus getStatus() {
    return status;
  }

  public void setStatus(RedeemStatus status) {
    this.status = status;
  }

  public RedeemPromoCodeResponseDto promoCode(String promoCode) {
    this.promoCode = promoCode;
    return this;
  }

  /**
   * Get promoCode
   * @return promoCode
   */
  
  @Schema(name = "promoCode", example = "27CR7HCV49", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("promoCode")
  public String getPromoCode() {
    return promoCode;
  }

  public void setPromoCode(String promoCode) {
    this.promoCode = promoCode;
  }

  public RedeemPromoCodeResponseDto message(String message) {
    this.message = message;
    return this;
  }

  /**
   * Get message
   * @return message
   */
  
  @Schema(name = "message", example = "Promo code redeemed successfully", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("message")
  public String getMessage() {
    return message;
  }

  public void setMessage(String message) {
    this.message = message;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    RedeemPromoCodeResponseDto redeemPromoCodeResponseDto = (RedeemPromoCodeResponseDto) o;
    return Objects.equals(this.success, redeemPromoCodeResponseDto.success) &&
        Objects.equals(this.status, redeemPromoCodeResponseDto.status) &&
        Objects.equals(this.promoCode, redeemPromoCodeResponseDto.promoCode) &&
        Objects.equals(this.message, redeemPromoCodeResponseDto.message);
  }

  @Override
  public int hashCode() {
    return Objects.hash(success, status, promoCode, message);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class RedeemPromoCodeResponseDto {\n");
    sb.append("    success: ").append(toIndentedString(success)).append("\n");
    sb.append("    status: ").append(toIndentedString(status)).append("\n");
    sb.append("    promoCode: ").append(toIndentedString(promoCode)).append("\n");
    sb.append("    message: ").append(toIndentedString(message)).append("\n");
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

