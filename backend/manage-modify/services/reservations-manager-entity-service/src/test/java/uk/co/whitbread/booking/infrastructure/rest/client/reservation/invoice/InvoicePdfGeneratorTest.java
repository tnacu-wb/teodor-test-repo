package uk.co.whitbread.booking.infrastructure.rest.client.reservation.invoice;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.ByteArrayOutputStream;
import java.lang.reflect.Method;
import java.util.Collections;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.thymeleaf.TemplateEngine;
import uk.co.whitbread.booking.domain.model.invoice.InvoiceDownload;
import uk.co.whitbread.booking.domain.model.invoice.Language;
import uk.co.whitbread.booking.infrastructure.rest.client.aws.AmazonClient;
import uk.co.whitbread.booking.infrastructure.rest.client.aws.exceptions.S3UploadException;
import uk.co.whitbread.booking.infrastructure.rest.client.aws.properties.S3Properties;
import uk.co.whitbread.booking.infrastructure.rest.client.cdhadapter.generated.models.InvoiceContainerDto;

@ExtendWith(MockitoExtension.class)
class InvoicePdfGeneratorTest {

  private static final String BUCKET = "booking-invoice-local";
  private static final String PRESIGNED_URL = "http://localhost:4566/booking-invoice-local/invoices/Invoice_GAN9859956_user.pdf";

  @Mock
  private TemplateEngine templateEngine;
  @Mock
  private InvoiceTemplateDataMapper invoiceTemplateDataMapper;
  @Mock
  private AmazonClient amazonClient;
  @Mock
  private S3Properties s3Properties;
  @InjectMocks
  private InvoicePdfGenerator invoicePdfGenerator;

  @Test
  void generate_uploadsPdfAndBuildsResponse() {
    InvoiceContainerDto container = new InvoiceContainerDto();
    InvoiceTemplateData templateData = invoiceTemplateData();
    S3Properties.Bucket bucketProperties = bucketProperties();

    when(invoiceTemplateDataMapper.toTemplateData(container, "PI")).thenReturn(templateData);
    when(templateEngine.process(eq("invoice-template-thymeleaf"), any())).thenReturn(validHtml());
    when(s3Properties.getBucket()).thenReturn(bucketProperties);
    when(s3Properties.getValidForMinutes()).thenReturn(5);
    when(amazonClient.doesObjectExist("invoices/Invoice_GAN9859956_user.pdf", BUCKET)).thenReturn(false);
    when(amazonClient.createPresignedGetUrl("invoices/Invoice_GAN9859956_user.pdf", BUCKET))
        .thenReturn(PRESIGNED_URL);

    InvoiceDownload response = invoicePdfGenerator.generate(container, Language.EN, "PI");

    assertNotNull(response);
    assertEquals("GAN9859956", response.bookingRef());
    assertEquals(PRESIGNED_URL, response.url());
    assertEquals("Invoice_GAN9859956_user.pdf", response.fileName());
    assertEquals("application/pdf", response.mimeType());
    assertEquals(Language.EN, response.language());
    assertEquals("GANI159177", response.invoiceMeta().invoiceNumber());
    assertEquals("04.09.2025", response.invoiceMeta().issuedDate());
    assertEquals("FRESUD", response.invoiceMeta().hotelId());

    ArgumentCaptor<ByteArrayOutputStream> outputStreamCaptor = ArgumentCaptor.forClass(ByteArrayOutputStream.class);
    verify(amazonClient).uploadFileToS3bucket(
        eq("Invoice_GAN9859956_user.pdf"),
        eq("invoices/Invoice_GAN9859956_user.pdf"),
        eq(BUCKET),
        outputStreamCaptor.capture());
    verify(amazonClient).doesObjectExist("invoices/Invoice_GAN9859956_user.pdf", BUCKET);
    verify(amazonClient).createPresignedGetUrl("invoices/Invoice_GAN9859956_user.pdf", BUCKET);
    assertNotNull(outputStreamCaptor.getValue());
  }

  @Test
  void generate_skipsPdfGenerationWhenObjectAlreadyExists() {
    InvoiceContainerDto container = new InvoiceContainerDto();
    InvoiceTemplateData templateData = invoiceTemplateData();
    S3Properties.Bucket bucketProperties = bucketProperties();

    when(invoiceTemplateDataMapper.toTemplateData(container, "PI")).thenReturn(templateData);
    when(s3Properties.getBucket()).thenReturn(bucketProperties);
    when(s3Properties.getValidForMinutes()).thenReturn(5);
    when(amazonClient.doesObjectExist("invoices/Invoice_GAN9859956_user.pdf", BUCKET)).thenReturn(true);
    when(amazonClient.createPresignedGetUrl("invoices/Invoice_GAN9859956_user.pdf", BUCKET))
        .thenReturn(PRESIGNED_URL);

    InvoiceDownload response = invoicePdfGenerator.generate(container, Language.EN, "PI");

    assertNotNull(response);
    assertEquals("GAN9859956", response.bookingRef());
    assertEquals(PRESIGNED_URL, response.url());
    assertEquals("Invoice_GAN9859956_user.pdf", response.fileName());

    verify(amazonClient).doesObjectExist("invoices/Invoice_GAN9859956_user.pdf", BUCKET);
    verify(templateEngine, never()).process(eq("invoice-template-thymeleaf"), any());
    verify(amazonClient, never()).uploadFileToS3bucket(any(), any(), any(), any(ByteArrayOutputStream.class));
    verify(amazonClient).createPresignedGetUrl("invoices/Invoice_GAN9859956_user.pdf", BUCKET);
  }

  @Test
  void generate_wrapsUploadFailures() {
    InvoiceContainerDto container = new InvoiceContainerDto();
    InvoiceTemplateData templateData = invoiceTemplateData();
    S3Properties.Bucket bucketProperties = bucketProperties();

    when(invoiceTemplateDataMapper.toTemplateData(container, "PI")).thenReturn(templateData);
    when(templateEngine.process(eq("invoice-template-thymeleaf"), any())).thenReturn(validHtml());
    when(s3Properties.getBucket()).thenReturn(bucketProperties);
    when(amazonClient.doesObjectExist("invoices/Invoice_GAN9859956_user.pdf", BUCKET)).thenReturn(false);
    doThrow(new IllegalArgumentException("upload failed"))
        .when(amazonClient)
        .uploadFileToS3bucket(eq("Invoice_GAN9859956_user.pdf"), eq("invoices/Invoice_GAN9859956_user.pdf"), eq(BUCKET), any(ByteArrayOutputStream.class));

    S3UploadException exception = assertThrows(S3UploadException.class,
        () -> invoicePdfGenerator.generate(container, Language.EN, "PI"));

    assertEquals("Booking Invoice upload to S3 bucket failed for file: Invoice_GAN9859956_user.pdf and bucket: booking-invoice-local",
        exception.getMessage());
    assertEquals(2000, exception.getErrorCode());
  }

  @Test
  void sanitizeFileNamePart_replacesUnsafeCharacters() throws Exception {
    Method method = InvoicePdfGenerator.class.getDeclaredMethod("sanitizeFileNamePart", String.class);
    method.setAccessible(true);

    String sanitized = (String) method.invoke(invoicePdfGenerator, "user / Test@example.com");

    assertEquals("user___Test_example_com", sanitized);
  }

  private S3Properties.Bucket bucketProperties() {
    S3Properties.Bucket bucket = new S3Properties.Bucket();
    bucket.setInvoiceReportName(BUCKET);
    return bucket;
  }

  private InvoiceTemplateData invoiceTemplateData() {
    return InvoiceTemplateData.builder()
        .hotel(new InvoiceTemplateData.HotelView(
            "Freiburg City Süd",
            "Heinrich-Von-Stephan-Str.19",
            "",
            "",
            "",
            "79100 Freiburg",
            "",
            "FRESUD"
        ))
        .billing(new InvoiceTemplateData.BillingView(
            "",
            "user user",
            "Whitbread Court, Porz Avenue",
            "Houghton Hall Park, Houghton Regis",
            "",
            "DUNSTABLE",
            "LU5 5XE",
            ""
        ))
        .booking(new InvoiceTemplateData.BookingView(
            "user user",
            "user",
            "",
            "22.01.2026",
            "23.01.2026",
            "78300446",
            "",
            "GAN9859956"
        ))
        .invoice(new InvoiceTemplateData.InvoiceView(
            "04.09.2025",
            "GANI159177",
            "",
            "",
            "",
            "VAT Invoice",
            "EUR",
            "€",
            "138.6",
            "138.6",
            "0"
        ))
        .transactions(Collections.emptyList())
        .charges(Collections.emptyList())
        .vatBreakdown(Collections.emptyList())
        .vatSummary(new InvoiceTemplateData.VatSummaryView("0", "0", "0"))
        .deposits(Collections.emptyList())
        .tse(new InvoiceTemplateData.TseView(false, "", "", "", "", "", "", "", "", ""))
        .legalOwnerText("Legal owner text")
        .logoImage("data:image/png;base64,AAAA")
        .build();
  }

  private String validHtml() {
    return "<!DOCTYPE html><html><head><title>Invoice</title></head><body><p>Invoice</p></body></html>";
  }
}
