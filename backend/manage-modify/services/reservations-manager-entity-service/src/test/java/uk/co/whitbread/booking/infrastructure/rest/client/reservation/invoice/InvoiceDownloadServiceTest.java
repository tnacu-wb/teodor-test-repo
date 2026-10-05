package uk.co.whitbread.booking.infrastructure.rest.client.reservation.invoice;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.co.whitbread.booking.domain.model.invoice.DownloadBookingInvoicesRequest;
import uk.co.whitbread.booking.domain.model.invoice.InvoiceDownload;
import uk.co.whitbread.booking.domain.model.invoice.InvoiceDownloadResponse;
import uk.co.whitbread.booking.domain.model.invoice.InvoiceMeta;
import uk.co.whitbread.booking.domain.model.invoice.Language;
import uk.co.whitbread.booking.infrastructure.rest.client.cdhadapter.exceptions.CdhBookingInvoiceException;
import uk.co.whitbread.booking.infrastructure.rest.client.cdhadapter.generated.models.InvoiceContainerDto;
import uk.co.whitbread.booking.infrastructure.rest.client.cdhadapter.generated.models.InvoiceDetailsDto;
import uk.co.whitbread.booking.infrastructure.rest.client.cdhadapter.generated.models.ReservationInvoicesResponseDto;
import uk.co.whitbread.booking.infrastructure.rest.client.cdhadapter.service.CdhAdapterClient;

@ExtendWith(MockitoExtension.class)
class InvoiceDownloadServiceTest {

  @Mock
  private CdhAdapterClient cdhAdapterClient;
  @Mock
  private InvoicePdfGenerator invoicePdfGenerator;
  @InjectMocks
  private InvoiceDownloadService invoiceDownloadService;

  @Test
  void generateInvoices_returnsGeneratedInvoiceForFirstBookingRef() {
    DownloadBookingInvoicesRequest request = DownloadBookingInvoicesRequest.builder()
        .bookingRef(List.of("GAN9859956"))
        .lang(Language.EN)
        .channel("PI")
        .subChannel("Online")
        .hotelBrand("HUB")
        .build();
    InvoiceContainerDto container = validContainer();
    ReservationInvoicesResponseDto response = new ReservationInvoicesResponseDto();
    response.setInvoices(List.of(container));

    InvoiceDownload download = InvoiceDownload.builder()
        .bookingRef("GAN9859956")
        .url("http://localhost/1")
        .expiresAt("2026-03-31T10:00:00Z")
        .fileName("Invoice_GAN9859956_user.pdf")
        .mimeType("application/pdf")
        .language(Language.EN)
        .invoiceMeta(InvoiceMeta.builder().invoiceNumber("INV-1").issuedDate("04.09.2025").hotelId("FRESUD").build())
        .build();

    when(cdhAdapterClient.getBookingInvoices("GAN9859956")).thenReturn(response);
    when(invoicePdfGenerator.generate(org.mockito.ArgumentMatchers.any(), eq(Language.EN), eq("HUB")))
        .thenReturn(download);

    InvoiceDownloadResponse result = invoiceDownloadService.generateInvoices(request);

    assertNotNull(result);
    assertEquals(1, result.invoices().size());
    assertEquals("GAN9859956", result.invoices().get(0).bookingRef());
  }

  @Test
  void generateInvoices_whenEmptyInvoicesFromCdh_throwsCdhBookingInvoiceException() {
    DownloadBookingInvoicesRequest request = DownloadBookingInvoicesRequest.builder()
        .bookingRef(List.of("GAN9859956"))
        .lang(Language.EN)
        .channel("BB")
        .subChannel("Online")
        .hotelBrand("HUB")
        .build();
    ReservationInvoicesResponseDto emptyResponse = new ReservationInvoicesResponseDto();
    emptyResponse.setInvoices(List.of());

    when(cdhAdapterClient.getBookingInvoices("GAN9859956")).thenReturn(emptyResponse);

    assertThrows(CdhBookingInvoiceException.class, () -> invoiceDownloadService.generateInvoices(request));
    verify(invoicePdfGenerator, never()).generate(org.mockito.ArgumentMatchers.any(), org.mockito.ArgumentMatchers.any(), org.mockito.ArgumentMatchers.any());
  }

  @Test
  void generateInvoices_whenNullResponseFromCdh_throwsCdhBookingInvoiceException() {
    DownloadBookingInvoicesRequest request = DownloadBookingInvoicesRequest.builder()
        .bookingRef(List.of("GAN9859956"))
        .lang(Language.EN)
        .channel("BB")
        .subChannel("Online")
        .hotelBrand("HUB")
        .build();

    when(cdhAdapterClient.getBookingInvoices("GAN9859956")).thenReturn(null);

    assertThrows(CdhBookingInvoiceException.class, () -> invoiceDownloadService.generateInvoices(request));
    verify(invoicePdfGenerator, never()).generate(org.mockito.ArgumentMatchers.any(), org.mockito.ArgumentMatchers.any(), org.mockito.ArgumentMatchers.any());
  }

  @Test
  void generateInvoices_whenInvalidContainer_throwsCdhBookingInvoiceException() {
    DownloadBookingInvoicesRequest request = DownloadBookingInvoicesRequest.builder()
        .bookingRef(List.of("GAN9859956"))
        .lang(Language.EN)
        .channel("BB")
        .subChannel("Online")
        .hotelBrand("HUB")
        .build();
    ReservationInvoicesResponseDto invalidContainerResponse = new ReservationInvoicesResponseDto();
    invalidContainerResponse.setInvoices(List.of(new InvoiceContainerDto()));

    when(cdhAdapterClient.getBookingInvoices("GAN9859956")).thenReturn(invalidContainerResponse);

    assertThrows(CdhBookingInvoiceException.class, () -> invoiceDownloadService.generateInvoices(request));
    verify(invoicePdfGenerator, never()).generate(org.mockito.ArgumentMatchers.any(), org.mockito.ArgumentMatchers.any(), org.mockito.ArgumentMatchers.any());
  }

  @Test
  void generateInvoices_withPIBrand_passesCorrectBrandToGenerator() {
    DownloadBookingInvoicesRequest request = DownloadBookingInvoicesRequest.builder()
        .bookingRef(List.of("GAN9859956"))
        .lang(Language.EN)
        .channel("PI")
        .subChannel("Online")
        .hotelBrand("PI")
        .build();
    InvoiceContainerDto container = validContainer();
    ReservationInvoicesResponseDto response = new ReservationInvoicesResponseDto();
    response.setInvoices(List.of(container));

    InvoiceDownload download = InvoiceDownload.builder()
        .bookingRef("GAN9859956")
        .url("http://localhost/1")
        .expiresAt("2026-03-31T10:00:00Z")
        .fileName("Invoice_GAN9859956_user.pdf")
        .mimeType("application/pdf")
        .language(Language.EN)
        .build();

    when(cdhAdapterClient.getBookingInvoices("GAN9859956")).thenReturn(response);
    when(invoicePdfGenerator.generate(org.mockito.ArgumentMatchers.any(), eq(Language.EN), eq("PI")))
        .thenReturn(download);

    InvoiceDownloadResponse result = invoiceDownloadService.generateInvoices(request);

    assertNotNull(result);
    verify(invoicePdfGenerator).generate(container, Language.EN, "PI");
  }

  @Test
  void generateInvoices_withZIPBrand_passesCorrectBrandToGenerator() {
    DownloadBookingInvoicesRequest request = DownloadBookingInvoicesRequest.builder()
        .bookingRef(List.of("GAN9859956"))
        .lang(Language.EN)
        .channel("PI")
        .subChannel("Online")
        .hotelBrand("ZIP")
        .build();
    InvoiceContainerDto container = validContainer();
    ReservationInvoicesResponseDto response = new ReservationInvoicesResponseDto();
    response.setInvoices(List.of(container));

    InvoiceDownload download = InvoiceDownload.builder()
        .bookingRef("GAN9859956")
        .url("http://localhost/1")
        .expiresAt("2026-03-31T10:00:00Z")
        .fileName("Invoice_GAN9859956_user.pdf")
        .mimeType("application/pdf")
        .language(Language.EN)
        .build();

    when(cdhAdapterClient.getBookingInvoices("GAN9859956")).thenReturn(response);
    when(invoicePdfGenerator.generate(org.mockito.ArgumentMatchers.any(), eq(Language.EN), eq("ZIP")))
        .thenReturn(download);

    InvoiceDownloadResponse result = invoiceDownloadService.generateInvoices(request);

    assertNotNull(result);
    verify(invoicePdfGenerator).generate(container, Language.EN, "ZIP");
  }

  private InvoiceContainerDto validContainer() {
    InvoiceDetailsDto details = new InvoiceDetailsDto();
    InvoiceContainerDto container = new InvoiceContainerDto();
    container.setInvoiceDetails(details);
    return container;
  }
}
