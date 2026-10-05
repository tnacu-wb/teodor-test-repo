package uk.co.whitbread.booking.infrastructure.rest.client.cdhadapter.generated.models;

import java.net.URI;
import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import org.springframework.lang.Nullable;
import org.openapitools.jackson.nullable.JsonNullable;
import java.time.OffsetDateTime;
import javax.validation.Valid;
import javax.validation.constraints.*;
import io.swagger.v3.oas.annotations.media.Schema;


import java.util.*;
import javax.annotation.Generated;

/**
 * InvoiceChargeDto
 */

@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2026-08-25T07:52:45.778333+03:00[Europe/Bucharest]", comments = "Generator version: 7.12.0")
public class InvoiceChargeDto {

  private @Nullable String creditAmount;

  private @Nullable String dateTime;

  private @Nullable String debitAmount;

  private @Nullable String itemDescription;

  private @Nullable String netAmount;

  private @Nullable String postingRemark;

  private @Nullable String quantity;

  private @Nullable String transactionCode;

  private @Nullable String vatAmount;

  private @Nullable String vatRate;

  private @Nullable String vatRateDesc;

  public InvoiceChargeDto creditAmount(String creditAmount) {
    this.creditAmount = creditAmount;
    return this;
  }

  /**
   * Get creditAmount
   * @return creditAmount
   */
  
  @Schema(name = "creditAmount", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("creditAmount")
  public String getCreditAmount() {
    return creditAmount;
  }

  public void setCreditAmount(String creditAmount) {
    this.creditAmount = creditAmount;
  }

  public InvoiceChargeDto dateTime(String dateTime) {
    this.dateTime = dateTime;
    return this;
  }

  /**
   * Get dateTime
   * @return dateTime
   */
  
  @Schema(name = "dateTime", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("dateTime")
  public String getDateTime() {
    return dateTime;
  }

  public void setDateTime(String dateTime) {
    this.dateTime = dateTime;
  }

  public InvoiceChargeDto debitAmount(String debitAmount) {
    this.debitAmount = debitAmount;
    return this;
  }

  /**
   * Get debitAmount
   * @return debitAmount
   */
  
  @Schema(name = "debitAmount", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("debitAmount")
  public String getDebitAmount() {
    return debitAmount;
  }

  public void setDebitAmount(String debitAmount) {
    this.debitAmount = debitAmount;
  }

  public InvoiceChargeDto itemDescription(String itemDescription) {
    this.itemDescription = itemDescription;
    return this;
  }

  /**
   * Get itemDescription
   * @return itemDescription
   */
  
  @Schema(name = "itemDescription", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("itemDescription")
  public String getItemDescription() {
    return itemDescription;
  }

  public void setItemDescription(String itemDescription) {
    this.itemDescription = itemDescription;
  }

  public InvoiceChargeDto netAmount(String netAmount) {
    this.netAmount = netAmount;
    return this;
  }

  /**
   * Get netAmount
   * @return netAmount
   */
  
  @Schema(name = "netAmount", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("netAmount")
  public String getNetAmount() {
    return netAmount;
  }

  public void setNetAmount(String netAmount) {
    this.netAmount = netAmount;
  }

  public InvoiceChargeDto postingRemark(String postingRemark) {
    this.postingRemark = postingRemark;
    return this;
  }

  /**
   * Get postingRemark
   * @return postingRemark
   */
  
  @Schema(name = "postingRemark", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("postingRemark")
  public String getPostingRemark() {
    return postingRemark;
  }

  public void setPostingRemark(String postingRemark) {
    this.postingRemark = postingRemark;
  }

  public InvoiceChargeDto quantity(String quantity) {
    this.quantity = quantity;
    return this;
  }

  /**
   * Get quantity
   * @return quantity
   */
  
  @Schema(name = "quantity", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("quantity")
  public String getQuantity() {
    return quantity;
  }

  public void setQuantity(String quantity) {
    this.quantity = quantity;
  }

  public InvoiceChargeDto transactionCode(String transactionCode) {
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

  public InvoiceChargeDto vatAmount(String vatAmount) {
    this.vatAmount = vatAmount;
    return this;
  }

  /**
   * Get vatAmount
   * @return vatAmount
   */
  
  @Schema(name = "vatAmount", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("vatAmount")
  public String getVatAmount() {
    return vatAmount;
  }

  public void setVatAmount(String vatAmount) {
    this.vatAmount = vatAmount;
  }

  public InvoiceChargeDto vatRate(String vatRate) {
    this.vatRate = vatRate;
    return this;
  }

  /**
   * Get vatRate
   * @return vatRate
   */
  
  @Schema(name = "vatRate", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("vatRate")
  public String getVatRate() {
    return vatRate;
  }

  public void setVatRate(String vatRate) {
    this.vatRate = vatRate;
  }

  public InvoiceChargeDto vatRateDesc(String vatRateDesc) {
    this.vatRateDesc = vatRateDesc;
    return this;
  }

  /**
   * Get vatRateDesc
   * @return vatRateDesc
   */
  
  @Schema(name = "vatRateDesc", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("vatRateDesc")
  public String getVatRateDesc() {
    return vatRateDesc;
  }

  public void setVatRateDesc(String vatRateDesc) {
    this.vatRateDesc = vatRateDesc;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    InvoiceChargeDto invoiceChargeDto = (InvoiceChargeDto) o;
    return Objects.equals(this.creditAmount, invoiceChargeDto.creditAmount) &&
        Objects.equals(this.dateTime, invoiceChargeDto.dateTime) &&
        Objects.equals(this.debitAmount, invoiceChargeDto.debitAmount) &&
        Objects.equals(this.itemDescription, invoiceChargeDto.itemDescription) &&
        Objects.equals(this.netAmount, invoiceChargeDto.netAmount) &&
        Objects.equals(this.postingRemark, invoiceChargeDto.postingRemark) &&
        Objects.equals(this.quantity, invoiceChargeDto.quantity) &&
        Objects.equals(this.transactionCode, invoiceChargeDto.transactionCode) &&
        Objects.equals(this.vatAmount, invoiceChargeDto.vatAmount) &&
        Objects.equals(this.vatRate, invoiceChargeDto.vatRate) &&
        Objects.equals(this.vatRateDesc, invoiceChargeDto.vatRateDesc);
  }

  @Override
  public int hashCode() {
    return Objects.hash(creditAmount, dateTime, debitAmount, itemDescription, netAmount, postingRemark, quantity, transactionCode, vatAmount, vatRate, vatRateDesc);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class InvoiceChargeDto {\n");
    sb.append("    creditAmount: ").append(toIndentedString(creditAmount)).append("\n");
    sb.append("    dateTime: ").append(toIndentedString(dateTime)).append("\n");
    sb.append("    debitAmount: ").append(toIndentedString(debitAmount)).append("\n");
    sb.append("    itemDescription: ").append(toIndentedString(itemDescription)).append("\n");
    sb.append("    netAmount: ").append(toIndentedString(netAmount)).append("\n");
    sb.append("    postingRemark: ").append(toIndentedString(postingRemark)).append("\n");
    sb.append("    quantity: ").append(toIndentedString(quantity)).append("\n");
    sb.append("    transactionCode: ").append(toIndentedString(transactionCode)).append("\n");
    sb.append("    vatAmount: ").append(toIndentedString(vatAmount)).append("\n");
    sb.append("    vatRate: ").append(toIndentedString(vatRate)).append("\n");
    sb.append("    vatRateDesc: ").append(toIndentedString(vatRateDesc)).append("\n");
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

