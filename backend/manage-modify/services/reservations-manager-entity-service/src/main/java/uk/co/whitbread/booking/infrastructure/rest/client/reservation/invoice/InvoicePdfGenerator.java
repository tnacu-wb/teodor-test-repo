package uk.co.whitbread.booking.infrastructure.rest.client.reservation.invoice;

import com.openhtmltopdf.pdfboxout.PdfRendererBuilder;
import java.io.ByteArrayOutputStream;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;
import uk.co.whitbread.booking.domain.model.exceptions.ErrorCode;
import uk.co.whitbread.booking.domain.model.invoice.InvoiceDownload;
import uk.co.whitbread.booking.domain.model.invoice.InvoiceMeta;
import uk.co.whitbread.booking.domain.model.invoice.Language;
import uk.co.whitbread.booking.infrastructure.rest.client.aws.AmazonClient;
import uk.co.whitbread.booking.infrastructure.rest.client.aws.exceptions.S3UploadException;
import uk.co.whitbread.booking.infrastructure.rest.client.aws.properties.S3Properties;
import uk.co.whitbread.booking.infrastructure.rest.client.cdhadapter.generated.models.InvoiceContainerDto;
import uk.co.whitbread.booking.infrastructure.rest.client.reservation.exceptions.PdfRenderException;

@Component
@RequiredArgsConstructor
@Slf4j
public class InvoicePdfGenerator {

  private static final String INVOICE_KEY_PREFIX = "invoices/";
  private static final String INVOICE_TEMPLATE_NAME = "invoice-template-thymeleaf";

  private final TemplateEngine templateEngine;
  private final InvoiceTemplateDataMapper invoiceTemplateDataMapper;
  private final AmazonClient amazonClient;
  private final S3Properties s3Properties;

  public InvoiceDownload generate(InvoiceContainerDto container, Language language, String hotelBrand) {
    InvoiceTemplateData data = invoiceTemplateDataMapper.toTemplateData(container, hotelBrand);
    Context context = new Context();
    context.setVariables(Map.ofEntries(
        Map.entry("hotel", data.hotel()),
        Map.entry("billing", data.billing()),
        Map.entry("booking", data.booking()),
        Map.entry("invoice", data.invoice()),
        Map.entry("transactions", data.transactions()),
        Map.entry("charges", data.charges()),
        Map.entry("vatBreakdown", data.vatBreakdown()),
        Map.entry("vatSummary", data.vatSummary()),
        Map.entry("deposits", data.deposits()),
        Map.entry("tse", data.tse()),
        Map.entry("legalOwnerText", data.legalOwnerText()),
        Map.entry("logoImage", data.logoImage())
    ));

    String fileName = buildFileName(data);
    String bucketName = s3Properties.getBucket().getInvoiceReportName();
    String objectKey = buildObjectKey(fileName);

    if (amazonClient.doesObjectExist(objectKey, bucketName)) {
      log.info("Invoice file [{}] already exists in S3 bucket [{}], skipping generation", objectKey, bucketName);
    } else {
      String html = templateEngine.process(INVOICE_TEMPLATE_NAME, context);
      byte[] pdfBytes = renderPdf(html);
      uploadToS3(fileName, objectKey, bucketName, pdfBytes);
    }

    String presignedUrl = amazonClient.createPresignedGetUrl(objectKey, bucketName);

    return InvoiceDownload.builder()
        .bookingRef(data.booking().bookingReference())
        .url(presignedUrl)
        .expiresAt(Instant.now().plus(s3Properties.getValidForMinutes(), ChronoUnit.MINUTES).toString())
        .fileName(fileName)
        .mimeType("application/pdf")
        .language(language)
        .invoiceMeta(InvoiceMeta.builder()
            .invoiceNumber(data.invoice().invoiceNumber())
            .issuedDate(data.invoice().issuedDate())
            .hotelId(data.hotel().hotelId())
            .build())
        .build();
  }

  private byte[] renderPdf(String html) {
    try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
      PdfRendererBuilder builder = new PdfRendererBuilder();
      builder.useFastMode();
      builder.withHtmlContent(html, null);
      builder.toStream(outputStream);
      builder.run();
      return outputStream.toByteArray();
    } catch (Exception ex) {
      throw new PdfRenderException(ErrorCode.PDF_RENDER_EXCEPTION.getMessage(),
          "Unable to render invoice PDF",
          ErrorCode.PDF_RENDER_EXCEPTION.getCode());
    }
  }

  private void uploadToS3(String fileName, String objectKey, String bucketName, byte[] pdfBytes) {
    try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
      outputStream.write(pdfBytes);
      amazonClient.uploadFileToS3bucket(fileName, objectKey, bucketName, outputStream);
    } catch (Exception ex) {
      throw new S3UploadException(ErrorCode.AWS_S3_UPLOAD_EXCEPTION.getMessage(),
          "Booking Invoice upload to S3 bucket failed for file: " + fileName + " and bucket: " + bucketName,
          ErrorCode.AWS_S3_UPLOAD_EXCEPTION.getCode());
    }
  }

  private String buildObjectKey(String fileName) {
    return INVOICE_KEY_PREFIX + fileName;
  }

  private String buildFileName(InvoiceTemplateData data) {
    String bookingReference = sanitizeFileNamePart(data.booking().bookingReference());
    String leadGuestLastName = sanitizeFileNamePart(data.booking().leadGuestLastName());
    return String.format("Invoice_%s_%s.pdf", bookingReference, leadGuestLastName);
  }

  private String sanitizeFileNamePart(String value) {
    if (value == null || value.isBlank()) {
      return "unknown";
    }
    return value.trim().replaceAll("[^a-zA-Z0-9\\-]", "_");
  }
}
