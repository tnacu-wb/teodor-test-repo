package uk.co.whitbread.hotel.generated.models.basket;

import java.net.URI;
import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import org.springframework.lang.Nullable;
import uk.co.whitbread.hotel.generated.models.basket.ChargeAmountDto;
import org.openapitools.jackson.nullable.JsonNullable;
import java.time.OffsetDateTime;
import javax.validation.Valid;
import javax.validation.constraints.*;
import io.swagger.v3.oas.annotations.media.Schema;


import java.util.*;
import javax.annotation.Generated;

/**
 * ChargeDto
 */

@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2026-09-04T08:11:22.312200+03:00[Europe/Bucharest]", comments = "Generator version: 7.12.0")
public class ChargeDto {

  private @Nullable ChargeAmountDto chargeAmount;

  private @Nullable Integer postingQuantity;

  private @Nullable String postingReference;

  private @Nullable String transactionCode;

  public ChargeDto chargeAmount(ChargeAmountDto chargeAmount) {
    this.chargeAmount = chargeAmount;
    return this;
  }

  /**
   * Get chargeAmount
   * @return chargeAmount
   */
  @Valid 
  @Schema(name = "chargeAmount", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("chargeAmount")
  public ChargeAmountDto getChargeAmount() {
    return chargeAmount;
  }

  public void setChargeAmount(ChargeAmountDto chargeAmount) {
    this.chargeAmount = chargeAmount;
  }

  public ChargeDto postingQuantity(Integer postingQuantity) {
    this.postingQuantity = postingQuantity;
    return this;
  }

  /**
   * Get postingQuantity
   * @return postingQuantity
   */
  
  @Schema(name = "postingQuantity", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("postingQuantity")
  public Integer getPostingQuantity() {
    return postingQuantity;
  }

  public void setPostingQuantity(Integer postingQuantity) {
    this.postingQuantity = postingQuantity;
  }

  public ChargeDto postingReference(String postingReference) {
    this.postingReference = postingReference;
    return this;
  }

  /**
   * Get postingReference
   * @return postingReference
   */
  
  @Schema(name = "postingReference", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("postingReference")
  public String getPostingReference() {
    return postingReference;
  }

  public void setPostingReference(String postingReference) {
    this.postingReference = postingReference;
  }

  public ChargeDto transactionCode(String transactionCode) {
    this.transactionCode = transactionCode;
    return this;
  }

  /**
   * Get transactionCode
   * @return transactionCode
   */
  
  @Schema(name = "transactionCode", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("transactionCode")
  public String getTransactionCode() {
    return transactionCode;
  }

  public void setTransactionCode(String transactionCode) {
    this.transactionCode = transactionCode;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    ChargeDto chargeDto = (ChargeDto) o;
    return Objects.equals(this.chargeAmount, chargeDto.chargeAmount) &&
        Objects.equals(this.postingQuantity, chargeDto.postingQuantity) &&
        Objects.equals(this.postingReference, chargeDto.postingReference) &&
        Objects.equals(this.transactionCode, chargeDto.transactionCode);
  }

  @Override
  public int hashCode() {
    return Objects.hash(chargeAmount, postingQuantity, postingReference, transactionCode);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class ChargeDto {\n");
    sb.append("    chargeAmount: ").append(toIndentedString(chargeAmount)).append("\n");
    sb.append("    postingQuantity: ").append(toIndentedString(postingQuantity)).append("\n");
    sb.append("    postingReference: ").append(toIndentedString(postingReference)).append("\n");
    sb.append("    transactionCode: ").append(toIndentedString(transactionCode)).append("\n");
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

