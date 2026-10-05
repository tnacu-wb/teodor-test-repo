package uk.co.whitbread.booking.infrastructure.rest.client.aws;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.ByteArrayOutputStream;
import java.net.URI;
import java.net.URL;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.http.SdkHttpMethod;
import software.amazon.awssdk.http.SdkHttpRequest;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.HeadObjectRequest;
import software.amazon.awssdk.services.s3.model.HeadObjectResponse;
import software.amazon.awssdk.services.s3.model.NoSuchKeyException;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.GetObjectPresignRequest;
import software.amazon.awssdk.services.s3.presigner.model.PresignedGetObjectRequest;
import uk.co.whitbread.booking.infrastructure.rest.client.aws.exceptions.S3UploadException;
import uk.co.whitbread.booking.infrastructure.rest.client.aws.properties.S3Properties;

@ExtendWith(MockitoExtension.class)
class AmazonClientTest {

  private static final String FILE_NAME = "invoice.pdf";
  private static final String KEY = "invoices/invoice.pdf";
  private static final String BUCKET = "booking-invoice-local";
  private static final String PDF_CONTENT_TYPE = "application/pdf";
  private static final String CUSTOM_CONTENT_TYPE = "application/test";

  @Mock
  private S3Properties s3Properties;
  @Mock
  private S3Client s3Client;
  @Mock
  private S3Presigner s3Presigner;
  @InjectMocks
  private AmazonClient amazonClient;

  @Test
  void uploadFileToS3bucket_usesDefaultPdfContentType() throws Exception {
    ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
    outputStream.write("pdf-content".getBytes());
    ArgumentCaptor<PutObjectRequest> requestCaptor = ArgumentCaptor.forClass(PutObjectRequest.class);

    amazonClient.uploadFileToS3bucket(FILE_NAME, KEY, BUCKET, outputStream);

    verify(s3Client).putObject(requestCaptor.capture(), any(RequestBody.class));
    PutObjectRequest request = requestCaptor.getValue();
    assertEquals(BUCKET, request.bucket());
    assertEquals(KEY, request.key());
    assertEquals(PDF_CONTENT_TYPE, request.contentType());
    assertEquals("attachment; filename=" + FILE_NAME, request.contentDisposition());
  }

  @Test
  void uploadFileToS3bucket_usesProvidedContentType() throws Exception {
    ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
    outputStream.write("custom-content".getBytes());
    ArgumentCaptor<PutObjectRequest> requestCaptor = ArgumentCaptor.forClass(PutObjectRequest.class);

    amazonClient.uploadFileToS3bucket(FILE_NAME, KEY, BUCKET, CUSTOM_CONTENT_TYPE, outputStream);

    verify(s3Client).putObject(requestCaptor.capture(), any(RequestBody.class));
    PutObjectRequest request = requestCaptor.getValue();
    assertEquals(CUSTOM_CONTENT_TYPE, request.contentType());
  }

  @Test
  void uploadFileToS3bucket_wrapsSdkExceptions() {
    ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
    when(s3Client.putObject(any(PutObjectRequest.class), any(RequestBody.class)))
        .thenThrow(new RuntimeException("boom"));

    S3UploadException exception = assertThrows(S3UploadException.class,
        () -> amazonClient.uploadFileToS3bucket(FILE_NAME, KEY, BUCKET, outputStream));

    assertEquals("Booking Invoice upload to S3 bucket failed for file: invoice.pdf and bucket: booking-invoice-local",
        exception.getMessage());
    assertEquals(2000, exception.getErrorCode());
  }

  @Test
  void doesObjectExist_returnsTrueWhenObjectExists() {
    when(s3Client.headObject(any(HeadObjectRequest.class)))
        .thenReturn(mock(HeadObjectResponse.class));

    boolean exists = amazonClient.doesObjectExist(KEY, BUCKET);

    assertEquals(true, exists);
    verify(s3Client).headObject(any(HeadObjectRequest.class));
  }

  @Test
  void doesObjectExist_returnsFalseWhenObjectDoesNotExist() {
    when(s3Client.headObject(any(HeadObjectRequest.class)))
        .thenThrow(NoSuchKeyException.builder().message("Not Found").build());

    boolean exists = amazonClient.doesObjectExist(KEY, BUCKET);

    assertEquals(false, exists);
    verify(s3Client).headObject(any(HeadObjectRequest.class));
  }

  @Test
  void doesObjectExist_rethrowsUnexpectedExceptions() {
    when(s3Client.headObject(any(HeadObjectRequest.class)))
        .thenThrow(new RuntimeException("error"));

    RuntimeException exception = assertThrows(RuntimeException.class,
        () -> amazonClient.doesObjectExist(KEY, BUCKET));

    assertEquals("error", exception.getMessage());
    verify(s3Client).headObject(any(HeadObjectRequest.class));
  }

  @Test
  void createPresignedGetUrl_returnsPresignedUrl() throws Exception {
    when(s3Properties.getValidForMinutes()).thenReturn(5);
    PresignedGetObjectRequest presignedRequest = mock(PresignedGetObjectRequest.class);
    when(presignedRequest.url()).thenReturn(new URL("http://localhost:4566/booking-invoice-local/invoices/invoice.pdf"));
    when(presignedRequest.httpRequest()).thenReturn(
        SdkHttpRequest.builder().method(SdkHttpMethod.GET).uri(URI.create("http://localhost:4566")).build());
    when(s3Presigner.presignGetObject(any(GetObjectPresignRequest.class))).thenReturn(presignedRequest);

    String url = amazonClient.createPresignedGetUrl(KEY, BUCKET);

    assertEquals("http://localhost:4566/booking-invoice-local/invoices/invoice.pdf", url);
    verify(s3Presigner).presignGetObject(any(GetObjectPresignRequest.class));
  }
}
