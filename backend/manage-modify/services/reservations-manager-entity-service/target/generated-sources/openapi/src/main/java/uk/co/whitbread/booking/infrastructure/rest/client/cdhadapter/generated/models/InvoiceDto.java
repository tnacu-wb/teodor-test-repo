package uk.co.whitbread.booking.infrastructure.rest.client.cdhadapter.generated.models;

import java.net.URI;
import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import org.springframework.lang.Nullable;
import uk.co.whitbread.booking.infrastructure.rest.client.cdhadapter.generated.models.InvoiceBookingDto;
import uk.co.whitbread.booking.infrastructure.rest.client.cdhadapter.generated.models.InvoiceChargesDto;
import uk.co.whitbread.booking.infrastructure.rest.client.cdhadapter.generated.models.InvoiceTotalsDto;
import uk.co.whitbread.booking.infrastructure.rest.client.cdhadapter.generated.models.InvoiceTransactionsDto;
import uk.co.whitbread.booking.infrastructure.rest.client.cdhadapter.generated.models.InvoiceVatBreakdownDto;
import org.openapitools.jackson.nullable.JsonNullable;
import java.time.OffsetDateTime;
import javax.validation.Valid;
import javax.validation.constraints.*;
import io.swagger.v3.oas.annotations.media.Schema;


import java.util.*;
import javax.annotation.Generated;

/**
 * InvoiceDto
 */

@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2026-08-25T07:52:45.778333+03:00[Europe/Bucharest]", comments = "Generator version: 7.12.0")
public class InvoiceDto {

  private @Nullable String arNumber;

  private @Nullable InvoiceBookingDto booking;

  private @Nullable String cashierNumber;

  private @Nullable InvoiceChargesDto charges;

  private @Nullable String currency;

  private @Nullable String customersPurchaseOrder;

  private @Nullable String customersReference;

  private @Nullable Object deposit;

  private @Nullable String folioWindowNumber;

  private @Nullable String invoiceNumber;

  private @Nullable String issuedDate;

  private @Nullable String origInvoiceNumForRefund;

  private @Nullable String paymentMethod;

  private @Nullable InvoiceTotalsDto totals;

  private @Nullable InvoiceTransactionsDto transactions;

  private @Nullable InvoiceVatBreakdownDto vatBreakdown;

  public InvoiceDto arNumber(String arNumber) {
    this.arNumber = arNumber;
    return this;
  }

  /**
   * Get arNumber
   * @return arNumber
   */
  
  @Schema(name = "arNumber", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("arNumber")
  public String getArNumber() {
    return arNumber;
  }

  public void setArNumber(String arNumber) {
    this.arNumber = arNumber;
  }

  public InvoiceDto booking(InvoiceBookingDto booking) {
    this.booking = booking;
    return this;
  }

  /**
   * Get booking
   * @return booking
   */
  @Valid 
  @Schema(name = "booking", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("booking")
  public InvoiceBookingDto getBooking() {
    return booking;
  }

  public void setBooking(InvoiceBookingDto booking) {
    this.booking = booking;
  }

  public InvoiceDto cashierNumber(String cashierNumber) {
    this.cashierNumber = cashierNumber;
    return this;
  }

  /**
   * Get cashierNumber
   * @return cashierNumber
   */
  
  @Schema(name = "cashierNumber", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("cashierNumber")
  public String getCashierNumber() {
    return cashierNumber;
  }

  public void setCashierNumber(String cashierNumber) {
    this.cashierNumber = cashierNumber;
  }

  public InvoiceDto charges(InvoiceChargesDto charges) {
    this.charges = charges;
    return this;
  }

  /**
   * Get charges
   * @return charges
   */
  @Valid 
  @Schema(name = "charges", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("charges")
  public InvoiceChargesDto getCharges() {
    return charges;
  }

  public void setCharges(InvoiceChargesDto charges) {
    this.charges = charges;
  }

  public InvoiceDto currency(String currency) {
    this.currency = currency;
    return this;
  }

  /**
   * Get currency
   * @return currency
   */
  
  @Schema(name = "currency", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("currency")
  public String getCurrency() {
    return currency;
  }

  public void setCurrency(String currency) {
    this.currency = currency;
  }

  public InvoiceDto customersPurchaseOrder(String customersPurchaseOrder) {
    this.customersPurchaseOrder = customersPurchaseOrder;
    return this;
  }

  /**
   * Get customersPurchaseOrder
   * @return customersPurchaseOrder
   */
  
  @Schema(name = "customersPurchaseOrder", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("customersPurchaseOrder")
  public String getCustomersPurchaseOrder() {
    return customersPurchaseOrder;
  }

  public void setCustomersPurchaseOrder(String customersPurchaseOrder) {
    this.customersPurchaseOrder = customersPurchaseOrder;
  }

  public InvoiceDto customersReference(String customersReference) {
    this.customersReference = customersReference;
    return this;
  }

  /**
   * Get customersReference
   * @return customersReference
   */
  
  @Schema(name = "customersReference", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("customersReference")
  public String getCustomersReference() {
    return customersReference;
  }

  public void setCustomersReference(String customersReference) {
    this.customersReference = customersReference;
  }

  public InvoiceDto deposit(Object deposit) {
    this.deposit = deposit;
    return this;
  }

  /**
   * Get deposit
   * @return deposit
   */
  
  @Schema(name = "deposit", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("deposit")
  public Object getDeposit() {
    return deposit;
  }

  public void setDeposit(Object deposit) {
    this.deposit = deposit;
  }

  public InvoiceDto folioWindowNumber(String folioWindowNumber) {
    this.folioWindowNumber = folioWindowNumber;
    return this;
  }

  /**
   * Get folioWindowNumber
   * @return folioWindowNumber
   */
  
  @Schema(name = "folioWindowNumber", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("folioWindowNumber")
  public String getFolioWindowNumber() {
    return folioWindowNumber;
  }

  public void setFolioWindowNumber(String folioWindowNumber) {
    this.folioWindowNumber = folioWindowNumber;
  }

  public InvoiceDto invoiceNumber(String invoiceNumber) {
    this.invoiceNumber = invoiceNumber;
    return this;
  }

  /**
   * Get invoiceNumber
   * @return invoiceNumber
   */
  
  @Schema(name = "invoiceNumber", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("invoiceNumber")
  public String getInvoiceNumber() {
    return invoiceNumber;
  }

  public void setInvoiceNumber(String invoiceNumber) {
    this.invoiceNumber = invoiceNumber;
  }

  public InvoiceDto issuedDate(String issuedDate) {
    this.issuedDate = issuedDate;
    return this;
  }

  /**
   * Get issuedDate
   * @return issuedDate
   */
  
  @Schema(name = "issuedDate", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("issuedDate")
  public String getIssuedDate() {
    return issuedDate;
  }

  public void setIssuedDate(String issuedDate) {
    this.issuedDate = issuedDate;
  }

  public InvoiceDto origInvoiceNumForRefund(String origInvoiceNumForRefund) {
    this.origInvoiceNumForRefund = origInvoiceNumForRefund;
    return this;
  }

  /**
   * Get origInvoiceNumForRefund
   * @return origInvoiceNumForRefund
   */
  
  @Schema(name = "origInvoiceNumForRefund", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("origInvoiceNumForRefund")
  public String getOrigInvoiceNumForRefund() {
    return origInvoiceNumForRefund;
  }

  public void setOrigInvoiceNumForRefund(String origInvoiceNumForRefund) {
    this.origInvoiceNumForRefund = origInvoiceNumForRefund;
  }

  public InvoiceDto paymentMethod(String paymentMethod) {
    this.paymentMethod = paymentMethod;
    return this;
  }

  /**
   * Get paymentMethod
   * @return paymentMethod
   */
  
  @Schema(name = "paymentMethod", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("paymentMethod")
  public String getPaymentMethod() {
    return paymentMethod;
  }

  public void setPaymentMethod(String paymentMethod) {
    this.paymentMethod = paymentMethod;
  }

  public InvoiceDto totals(InvoiceTotalsDto totals) {
    this.totals = totals;
    return this;
  }

  /**
   * Get totals
   * @return totals
   */
  @Valid 
  @Schema(name = "totals", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("totals")
  public InvoiceTotalsDto getTotals() {
    return totals;
  }

  public void setTotals(InvoiceTotalsDto totals) {
    this.totals = totals;
  }

  public InvoiceDto transactions(InvoiceTransactionsDto transactions) {
    this.transactions = transactions;
    return this;
  }

  /**
   * Get transactions
   * @return transactions
   */
  @Valid 
  @Schema(name = "transactions", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("transactions")
  public InvoiceTransactionsDto getTransactions() {
    return transactions;
  }

  public void setTransactions(InvoiceTransactionsDto transactions) {
    this.transactions = transactions;
  }

  public InvoiceDto vatBreakdown(InvoiceVatBreakdownDto vatBreakdown) {
    this.vatBreakdown = vatBreakdown;
    return this;
  }

  /**
   * Get vatBreakdown
   * @return vatBreakdown
   */
  @Valid 
  @Schema(name = "vatBreakdown", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("vatBreakdown")
  public InvoiceVatBreakdownDto getVatBreakdown() {
    return vatBreakdown;
  }

  public void setVatBreakdown(InvoiceVatBreakdownDto vatBreakdown) {
    this.vatBreakdown = vatBreakdown;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    InvoiceDto invoiceDto = (InvoiceDto) o;
    return Objects.equals(this.arNumber, invoiceDto.arNumber) &&
        Objects.equals(this.booking, invoiceDto.booking) &&
        Objects.equals(this.cashierNumber, invoiceDto.cashierNumber) &&
        Objects.equals(this.charges, invoiceDto.charges) &&
        Objects.equals(this.currency, invoiceDto.currency) &&
        Objects.equals(this.customersPurchaseOrder, invoiceDto.customersPurchaseOrder) &&
        Objects.equals(this.customersReference, invoiceDto.customersReference) &&
        Objects.equals(this.deposit, invoiceDto.deposit) &&
        Objects.equals(this.folioWindowNumber, invoiceDto.folioWindowNumber) &&
        Objects.equals(this.invoiceNumber, invoiceDto.invoiceNumber) &&
        Objects.equals(this.issuedDate, invoiceDto.issuedDate) &&
        Objects.equals(this.origInvoiceNumForRefund, invoiceDto.origInvoiceNumForRefund) &&
        Objects.equals(this.paymentMethod, invoiceDto.paymentMethod) &&
        Objects.equals(this.totals, invoiceDto.totals) &&
        Objects.equals(this.transactions, invoiceDto.transactions) &&
        Objects.equals(this.vatBreakdown, invoiceDto.vatBreakdown);
  }

  @Override
  public int hashCode() {
    return Objects.hash(arNumber, booking, cashierNumber, charges, currency, customersPurchaseOrder, customersReference, deposit, folioWindowNumber, invoiceNumber, issuedDate, origInvoiceNumForRefund, paymentMethod, totals, transactions, vatBreakdown);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class InvoiceDto {\n");
    sb.append("    arNumber: ").append(toIndentedString(arNumber)).append("\n");
    sb.append("    booking: ").append(toIndentedString(booking)).append("\n");
    sb.append("    cashierNumber: ").append(toIndentedString(cashierNumber)).append("\n");
    sb.append("    charges: ").append(toIndentedString(charges)).append("\n");
    sb.append("    currency: ").append(toIndentedString(currency)).append("\n");
    sb.append("    customersPurchaseOrder: ").append(toIndentedString(customersPurchaseOrder)).append("\n");
    sb.append("    customersReference: ").append(toIndentedString(customersReference)).append("\n");
    sb.append("    deposit: ").append(toIndentedString(deposit)).append("\n");
    sb.append("    folioWindowNumber: ").append(toIndentedString(folioWindowNumber)).append("\n");
    sb.append("    invoiceNumber: ").append(toIndentedString(invoiceNumber)).append("\n");
    sb.append("    issuedDate: ").append(toIndentedString(issuedDate)).append("\n");
    sb.append("    origInvoiceNumForRefund: ").append(toIndentedString(origInvoiceNumForRefund)).append("\n");
    sb.append("    paymentMethod: ").append(toIndentedString(paymentMethod)).append("\n");
    sb.append("    totals: ").append(toIndentedString(totals)).append("\n");
    sb.append("    transactions: ").append(toIndentedString(transactions)).append("\n");
    sb.append("    vatBreakdown: ").append(toIndentedString(vatBreakdown)).append("\n");
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

