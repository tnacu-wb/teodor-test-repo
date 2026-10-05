package uk.co.whitbread.cdh.domain.model.booking.out;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Invoice {

  @JsonProperty("InvoiceNumber")
  private String invoiceNumber;

  @JsonProperty("FolioWindowNumber")
  private String folioWindowNumber;

  @JsonProperty("OrigInvoiceNumForRefund")
  private String origInvoiceNumForRefund;

  @JsonProperty("IssuedDate")
  private String issuedDate;

  @JsonProperty("PaymentMethod")
  private String paymentMethod;

  @JsonProperty("ArNumber")
  private String arNumber;

  @JsonProperty("Currency")
  private String currency;

  @JsonProperty("CashierNumber")
  private String cashierNumber;

  @JsonProperty("CustomersPurchaseOrder")
  private String customersPurchaseOrder;

  @JsonProperty("CustomersReference")
  private String customersReference;

  @JsonProperty("Booking")
  private InvoiceBooking booking;

  @JsonProperty("Charges")
  private InvoiceCharges charges;

  @JsonProperty("Totals")
  private InvoiceTotals totals;

  @JsonProperty("Transactions")
  private InvoiceTransactions transactions;

  @JsonProperty("Deposit")
  private Object deposit;

  @JsonProperty("VATBreakdown")
  private InvoiceVatBreakdown vatBreakdown;
}
