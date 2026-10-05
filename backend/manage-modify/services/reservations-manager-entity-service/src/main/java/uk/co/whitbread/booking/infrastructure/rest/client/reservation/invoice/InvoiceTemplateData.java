package uk.co.whitbread.booking.infrastructure.rest.client.reservation.invoice;

import java.util.List;
import lombok.Builder;

@Builder
public record InvoiceTemplateData(
    HotelView hotel,
    BillingView billing,
    BookingView booking,
    InvoiceView invoice,
    List<TransactionView> transactions,
    List<ChargeView> charges,
    List<VatView> vatBreakdown,
    VatSummaryView vatSummary,
    List<DepositView> deposits,
    TseView tse,
    String legalOwnerText,
    String logoImage
) {

  public record HotelView(
      String name,
      String addressLine1,
      String addressLine2,
      String addressLine3,
      String addressLine4,
      String city,
      String postalCode,
      String hotelId
  ) {}

  public record BillingView(
      String companyName,
      String fullName,
      String addressLine1,
      String addressLine2,
      String addressLine3,
      String addressLine4,
      String cityPostalLine,
      String groupName
  ) {}

  public record BookingView(
      String guestName,
      String leadGuestLastName,
      String roomNumber,
      String arrivalDate,
      String departureDate,
      String confirmationReference,
      String groupName,
      String bookingReference
  ) {}

  public record InvoiceView(
      String issuedDate,
      String invoiceNumber,
      String customersReference,
      String customersPurchaseOrder,
      String arNumber,
      String folioType,
      String currency,
      String currencySymbol,
      String debitAmount,
      String creditAmount,
      String balanceAmount
  ) {}

  public record TransactionView(
      String dateTime,
      String type,
      String amount
  ) {}

  public record ChargeView(
      String dateTime,
      String itemDescription,
      String netAmount,
      String vatRate,
      String vatAmount,
      String grossAmount
  ) {}

  public record VatView(
      String rateDesc,
      String totalExclVat,
      String totalVat,
      String totalIncVat
  ) {}

  public record VatSummaryView(
      String totalNet,
      String totalAmount,
      String totalGross
  ) {}

  public record DepositView(
      String dateTime,
      String totalIncVat,
      String invoiceNumber,
      String vatAmount
  ) {}

  public record TseView(
      boolean present,
      String fn,
      String startDateTime,
      String endDateTime,
      String serial,
      String sign,
      String signCnt,
      String code,
      String sq,
      String tn
  ) {}
}
