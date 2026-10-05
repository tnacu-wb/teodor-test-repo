package uk.co.whitbread.hotel.ohip.adapter.generated.models.basket;

import java.net.URI;
import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import java.math.BigDecimal;
import org.springframework.lang.Nullable;
import org.openapitools.jackson.nullable.JsonNullable;
import java.time.OffsetDateTime;
import javax.validation.Valid;
import javax.validation.constraints.*;
import io.swagger.v3.oas.annotations.media.Schema;


import java.util.*;
import javax.annotation.Generated;

/**
 * UpdateDiscountRequestDto
 */

@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2026-08-25T07:52:44.119190+03:00[Europe/Bucharest]", comments = "Generator version: 7.12.0")
public class UpdateDiscountRequestDto {

  private String basketReference;

  private @Nullable BigDecimal discountAmount;

  public UpdateDiscountRequestDto() {
    super();
  }

  /**
   * Constructor with only required parameters
   */
  public UpdateDiscountRequestDto(String basketReference) {
    this.basketReference = basketReference;
  }

  public UpdateDiscountRequestDto basketReference(String basketReference) {
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

  public UpdateDiscountRequestDto discountAmount(BigDecimal discountAmount) {
    this.discountAmount = discountAmount;
    return this;
  }

  /**
   * Get discountAmount
   * minimum: 0.0
   * @return discountAmount
   */
  @Valid @DecimalMin("0.0") 
  @Schema(name = "discountAmount", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("discountAmount")
  public BigDecimal getDiscountAmount() {
    return discountAmount;
  }

  public void setDiscountAmount(BigDecimal discountAmount) {
    this.discountAmount = discountAmount;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    UpdateDiscountRequestDto updateDiscountRequestDto = (UpdateDiscountRequestDto) o;
    return Objects.equals(this.basketReference, updateDiscountRequestDto.basketReference) &&
        Objects.equals(this.discountAmount, updateDiscountRequestDto.discountAmount);
  }

  @Override
  public int hashCode() {
    return Objects.hash(basketReference, discountAmount);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class UpdateDiscountRequestDto {\n");
    sb.append("    basketReference: ").append(toIndentedString(basketReference)).append("\n");
    sb.append("    discountAmount: ").append(toIndentedString(discountAmount)).append("\n");
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

