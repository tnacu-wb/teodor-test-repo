package uk.co.whitbread.booking.infrastructure.rest.client.aws;


import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.time.Duration;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.HeadObjectRequest;
import software.amazon.awssdk.services.s3.model.NoSuchKeyException;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.GetObjectPresignRequest;
import software.amazon.awssdk.services.s3.presigner.model.PresignedGetObjectRequest;
import uk.co.whitbread.booking.domain.model.exceptions.ErrorCode;
import uk.co.whitbread.booking.infrastructure.rest.client.aws.exceptions.S3UploadException;
import uk.co.whitbread.booking.infrastructure.rest.client.aws.properties.S3Properties;

@RequiredArgsConstructor
@Component
@Slf4j
public class AmazonClient {

  private static final String ATTACHMENT_FILENAME = "attachment; filename=";
  private static final String PDF_CONTENT_TYPE = "application/pdf";

  private final S3Properties s3Properties;

  private final S3Client s3Client;

  private final S3Presigner s3Presigner;

  public void uploadFileToS3bucket(String fileName, String key, String bucketName,
      ByteArrayOutputStream byteArrayOutputStream) {
    uploadFileToS3bucket(fileName, key, bucketName, PDF_CONTENT_TYPE, byteArrayOutputStream);
  }

  public void uploadFileToS3bucket(String fileName, String key, String bucketName,
      String contentType, ByteArrayOutputStream byteArrayOutputStream) {
    try {
      byte[] buffer = byteArrayOutputStream.toByteArray();

      InputStream inputStream = new ByteArrayInputStream(buffer);

      s3Client.putObject(PutObjectRequest.builder()
          .bucket(bucketName)
          .key(key)
          .contentType(contentType)
          .contentDisposition(ATTACHMENT_FILENAME + fileName)
          .build(), RequestBody.fromInputStream(inputStream, buffer.length));
    } catch (Exception e) {
      log.error("Error occurred while uploading file [{}] to S3 bucket [{}]", fileName, bucketName,
          e);
      throw new S3UploadException(ErrorCode.AWS_S3_UPLOAD_EXCEPTION.getMessage(),
          "Booking Invoice upload to S3 bucket failed for file: " + fileName + " and bucket: " + bucketName,
          ErrorCode.AWS_S3_UPLOAD_EXCEPTION.getCode());
    }

  }

  public String createPresignedGetUrl(String keyName, String bucketName) {

    GetObjectRequest objectRequest = GetObjectRequest.builder()
        .bucket(bucketName)
        .key(keyName)
        .build();

    GetObjectPresignRequest presignRequest = GetObjectPresignRequest.builder()
        .signatureDuration(Duration.ofMinutes(s3Properties.getValidForMinutes()))
        .getObjectRequest(objectRequest)
        .build();

    PresignedGetObjectRequest presignedRequest = s3Presigner.presignGetObject(presignRequest);
    log.debug("Presigned URL: [{}]", presignedRequest.url());
    log.debug("HTTP method: [{}]", presignedRequest.httpRequest().method());

    return presignedRequest.url().toExternalForm();

  }

  public boolean doesObjectExist(String key, String bucketName) {
    try {
      s3Client.headObject(HeadObjectRequest.builder()
          .bucket(bucketName)
          .key(key)
          .build());
      log.debug("Object [{}] found in S3 bucket [{}]", key, bucketName);
      return true;
    } catch (NoSuchKeyException e) {
      log.debug("Object [{}] not found in S3 bucket [{}]", key, bucketName);
      return false;
    } catch (Exception e) {
      log.error("Error occurred while checking existence of object [{}] in S3 bucket [{}]", key, bucketName, e);
      throw e;
    }
  }


}
