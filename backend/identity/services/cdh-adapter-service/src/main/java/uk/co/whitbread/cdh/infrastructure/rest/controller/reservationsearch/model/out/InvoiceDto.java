package uk.co.whitbread.cdh.infrastructure.rest.controller.reservationsearch.model.out;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class InvoiceDto {

  private String invoiceNumber;
  private String folioWindowNumber;
  private String origInvoiceNumForRefund;
  private String issuedDate;
  private String paymentMethod;
  private String arNumber;
  private String currency;
  private String cashierNumber;
  private String customersPurchaseOrder;
  private String customersReference;
  private InvoiceBookingDto booking;
  private InvoiceChargesDto charges;
  private InvoiceTotalsDto totals;
  private InvoiceTransactionsDto transactions;
  private Object deposit;
  private InvoiceVatBreakdownDto vatBreakdown;
}
