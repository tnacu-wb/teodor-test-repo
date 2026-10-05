package uk.co.whitbread.basket.generated.models.reservation;

import java.net.URI;
import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import org.springframework.lang.Nullable;
import uk.co.whitbread.basket.generated.models.reservation.CurrencyAmountDto;
import org.openapitools.jackson.nullable.JsonNullable;
import java.time.OffsetDateTime;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import io.swagger.v3.oas.annotations.media.Schema;


import java.util.*;
import jakarta.annotation.Generated;

/**
 * DepositFolioChargeDto
 */
@com.fasterxml.jackson.annotation.JsonIgnoreProperties(ignoreUnknown = true)

@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2026-09-04T08:10:03.993735+03:00[Europe/Bucharest]", comments = "Generator version: 7.12.0")
public class DepositFolioChargeDto {

  private CurrencyAmountDto currencyAmount;

  private Integer quantity;

  private String reference;

  private String transactionCode;

  public DepositFolioChargeDto() {
    super();
  }

  /**
   * Constructor with only required parameters
   */
  public DepositFolioChargeDto(CurrencyAmountDto currencyAmount, Integer quantity, String reference, String transactionCode) {
    this.currencyAmount = currencyAmount;
    this.quantity = quantity;
    this.reference = reference;
    this.transactionCode = transactionCode;
  }

  public DepositFolioChargeDto currencyAmount(CurrencyAmountDto currencyAmount) {
    this.currencyAmount = currencyAmount;
    return this;
  }

  /**
   * Get currencyAmount
   * @return currencyAmount
   */
  @NotNull @Valid 
  @Schema(name = "currencyAmount", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("currencyAmount")
  public CurrencyAmountDto getCurrencyAmount() {
    return currencyAmount;
  }

  public void setCurrencyAmount(CurrencyAmountDto currencyAmount) {
    this.currencyAmount = currencyAmount;
  }

  public DepositFolioChargeDto quantity(Integer quantity) {
    this.quantity = quantity;
    return this;
  }

  /**
   * Get quantity
   * @return quantity
   */
  @NotNull 
  @Schema(name = "quantity", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("quantity")
  public Integer getQuantity() {
    return quantity;
  }

  public void setQuantity(Integer quantity) {
    this.quantity = quantity;
  }

  public DepositFolioChargeDto reference(String reference) {
    this.reference = reference;
    return this;
  }

  /**
   * Get reference
   * @return reference
   */
  @NotNull 
  @Schema(name = "reference", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("reference")
  public String getReference() {
    return reference;
  }

  public void setReference(String reference) {
    this.reference = reference;
  }

  public DepositFolioChargeDto transactionCode(String transactionCode) {
    this.transactionCode = transactionCode;
    return this;
  }

  /**
   * Get transactionCode
   * @return transactionCode
   */
  @NotNull 
  @Schema(name = "transactionCode", requiredMode = Schema.RequiredMode.REQUIRED)
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
    DepositFolioChargeDto depositFolioChargeDto = (DepositFolioChargeDto) o;
    return Objects.equals(this.currencyAmount, depositFolioChargeDto.currencyAmount) &&
        Objects.equals(this.quantity, depositFolioChargeDto.quantity) &&
        Objects.equals(this.reference, depositFolioChargeDto.reference) &&
        Objects.equals(this.transactionCode, depositFolioChargeDto.transactionCode);
  }

  @Override
  public int hashCode() {
    return Objects.hash(currencyAmount, quantity, reference, transactionCode);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class DepositFolioChargeDto {\n");
    sb.append("    currencyAmount: ").append(toIndentedString(currencyAmount)).append("\n");
    sb.append("    quantity: ").append(toIndentedString(quantity)).append("\n");
    sb.append("    reference: ").append(toIndentedString(reference)).append("\n");
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

