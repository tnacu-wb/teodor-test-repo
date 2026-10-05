package uk.co.whitbread.hotel.ohip.adapter.generated.models.basket;

import java.net.URI;
import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import org.springframework.lang.Nullable;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.basket.CcuiExtraItemsDto;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.basket.PaymentCcuiRequestDto;
import org.openapitools.jackson.nullable.JsonNullable;
import java.time.OffsetDateTime;
import javax.validation.Valid;
import javax.validation.constraints.*;
import io.swagger.v3.oas.annotations.media.Schema;


import java.util.*;
import javax.annotation.Generated;

/**
 * CcuiPaymentRequestDto
 */

@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2026-08-27T18:51:25.444555+03:00[Europe/Bucharest]", comments = "Generator version: 7.12.0")
public class CcuiPaymentRequestDto {

  private @Nullable CcuiExtraItemsDto ccuiExtraItems;

  private @Nullable String paymentOption;

  private @Nullable PaymentCcuiRequestDto paymentRequest;

  private @Nullable String subPaymentType;

  private @Nullable Boolean useCache;

  public CcuiPaymentRequestDto ccuiExtraItems(CcuiExtraItemsDto ccuiExtraItems) {
    this.ccuiExtraItems = ccuiExtraItems;
    return this;
  }

  /**
   * Get ccuiExtraItems
   * @return ccuiExtraItems
   */
  @Valid 
  @Schema(name = "ccuiExtraItems", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("ccuiExtraItems")
  public CcuiExtraItemsDto getCcuiExtraItems() {
    return ccuiExtraItems;
  }

  public void setCcuiExtraItems(CcuiExtraItemsDto ccuiExtraItems) {
    this.ccuiExtraItems = ccuiExtraItems;
  }

  public CcuiPaymentRequestDto paymentOption(String paymentOption) {
    this.paymentOption = paymentOption;
    return this;
  }

  /**
   * Get paymentOption
   * @return paymentOption
   */
  
  @Schema(name = "paymentOption", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("paymentOption")
  public String getPaymentOption() {
    return paymentOption;
  }

  public void setPaymentOption(String paymentOption) {
    this.paymentOption = paymentOption;
  }

  public CcuiPaymentRequestDto paymentRequest(PaymentCcuiRequestDto paymentRequest) {
    this.paymentRequest = paymentRequest;
    return this;
  }

  /**
   * Get paymentRequest
   * @return paymentRequest
   */
  @Valid 
  @Schema(name = "paymentRequest", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("paymentRequest")
  public PaymentCcuiRequestDto getPaymentRequest() {
    return paymentRequest;
  }

  public void setPaymentRequest(PaymentCcuiRequestDto paymentRequest) {
    this.paymentRequest = paymentRequest;
  }

  public CcuiPaymentRequestDto subPaymentType(String subPaymentType) {
    this.subPaymentType = subPaymentType;
    return this;
  }

  /**
   * Get subPaymentType
   * @return subPaymentType
   */
  
  @Schema(name = "subPaymentType", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("subPaymentType")
  public String getSubPaymentType() {
    return subPaymentType;
  }

  public void setSubPaymentType(String subPaymentType) {
    this.subPaymentType = subPaymentType;
  }

  public CcuiPaymentRequestDto useCache(Boolean useCache) {
    this.useCache = useCache;
    return this;
  }

  /**
   * Get useCache
   * @return useCache
   */
  
  @Schema(name = "useCache", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("useCache")
  public Boolean getUseCache() {
    return useCache;
  }

  public void setUseCache(Boolean useCache) {
    this.useCache = useCache;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    CcuiPaymentRequestDto ccuiPaymentRequestDto = (CcuiPaymentRequestDto) o;
    return Objects.equals(this.ccuiExtraItems, ccuiPaymentRequestDto.ccuiExtraItems) &&
        Objects.equals(this.paymentOption, ccuiPaymentRequestDto.paymentOption) &&
        Objects.equals(this.paymentRequest, ccuiPaymentRequestDto.paymentRequest) &&
        Objects.equals(this.subPaymentType, ccuiPaymentRequestDto.subPaymentType) &&
        Objects.equals(this.useCache, ccuiPaymentRequestDto.useCache);
  }

  @Override
  public int hashCode() {
    return Objects.hash(ccuiExtraItems, paymentOption, paymentRequest, subPaymentType, useCache);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class CcuiPaymentRequestDto {\n");
    sb.append("    ccuiExtraItems: ").append(toIndentedString(ccuiExtraItems)).append("\n");
    sb.append("    paymentOption: ").append(toIndentedString(paymentOption)).append("\n");
    sb.append("    paymentRequest: ").append(toIndentedString(paymentRequest)).append("\n");
    sb.append("    subPaymentType: ").append(toIndentedString(subPaymentType)).append("\n");
    sb.append("    useCache: ").append(toIndentedString(useCache)).append("\n");
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

