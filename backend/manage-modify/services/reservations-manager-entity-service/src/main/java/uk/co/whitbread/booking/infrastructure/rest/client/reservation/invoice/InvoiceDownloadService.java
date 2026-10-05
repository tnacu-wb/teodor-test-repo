package uk.co.whitbread.booking.infrastructure.rest.client.reservation.invoice;

import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import uk.co.whitbread.booking.domain.model.exceptions.ErrorCode;
import uk.co.whitbread.booking.domain.model.invoice.DownloadBookingInvoicesRequest;
import uk.co.whitbread.booking.domain.model.invoice.InvoiceDownload;
import uk.co.whitbread.booking.domain.model.invoice.InvoiceDownloadResponse;
import uk.co.whitbread.booking.domain.model.invoice.Language;
import uk.co.whitbread.booking.infrastructure.rest.client.cdhadapter.exceptions.CdhBookingInvoiceException;
import uk.co.whitbread.booking.infrastructure.rest.client.cdhadapter.generated.models.InvoiceContainerDto;
import uk.co.whitbread.booking.infrastructure.rest.client.cdhadapter.generated.models.ReservationInvoicesResponseDto;
import uk.co.whitbread.booking.infrastructure.rest.client.cdhadapter.service.CdhAdapterClient;

@Slf4j
@Service
@RequiredArgsConstructor
public class InvoiceDownloadService {

  private final CdhAdapterClient cdhAdapterClient;
  private final InvoicePdfGenerator invoicePdfGenerator;

  public InvoiceDownloadResponse generateInvoices(DownloadBookingInvoicesRequest request) {
    String bookingRef = request.bookingRef().stream()
        .findFirst()
        .orElseThrow(() -> new CdhBookingInvoiceException(
            ErrorCode.BOOKING_INVOICE_INVALID_REQUEST.getMessage(),
            "Download booking invoices bookingRef must not be empty",
            ErrorCode.BOOKING_INVOICE_INVALID_REQUEST.getCode()));

    InvoiceDownload invoice = generateInvoice(bookingRef, request.lang(), request.hotelBrand());

    return InvoiceDownloadResponse.builder()
        .invoices(List.of(invoice))
        .build();
  }

  private InvoiceDownload generateInvoice(String bookingRef,
      Language language,
      String hotelBrand) {
    ReservationInvoicesResponseDto cdhResponse = cdhAdapterClient.getBookingInvoices(bookingRef);
    if (cdhResponse == null || cdhResponse.getInvoices() == null || cdhResponse.getInvoices().isEmpty()) {
      log.warn("Empty or null invoice response from CDH for booking reference: {}", bookingRef);
      throw new CdhBookingInvoiceException(
          ErrorCode.BOOKING_INVOICE_CDH_EMPTY_RESPONSE.getMessage(),
          "No invoices returned from CDH for booking reference: " + bookingRef,
          ErrorCode.BOOKING_INVOICE_CDH_EMPTY_RESPONSE.getCode());
    }

    List<InvoiceContainerDto> invoices = cdhResponse.getInvoices();
    InvoiceContainerDto container = invoices.get(invoices.size() - 1);
    if (container == null || container.getInvoiceDetails() == null) {
      log.warn("Invoice container or details missing from CDH response for booking reference: {}", bookingRef);
      throw new CdhBookingInvoiceException(
          ErrorCode.BOOKING_INVOICE_CDH_EMPTY_RESPONSE.getMessage(),
          "Invoice container or details missing from CDH for booking reference: " + bookingRef,
          ErrorCode.BOOKING_INVOICE_CDH_EMPTY_RESPONSE.getCode());
    }

    return invoicePdfGenerator.generate(container, language, hotelBrand);
  }
}
