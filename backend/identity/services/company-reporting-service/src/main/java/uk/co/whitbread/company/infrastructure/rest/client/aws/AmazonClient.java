package uk.co.whitbread.company.infrastructure.rest.client.aws;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.time.Duration;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.stereotype.Component;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.GetObjectPresignRequest;
import software.amazon.awssdk.services.s3.presigner.model.PresignedGetObjectRequest;
import uk.co.whitbread.company.infrastructure.rest.client.aws.properties.S3Properties;

@RequiredArgsConstructor
@Component
@Slf4j
public class AmazonClient {

  private static final String ATTACHMENT_FILENAME = "attachment; filename=";
  private static final String CONTENT_DISPOSITION = "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet";

  private final S3Properties s3Properties;

  private final S3Client s3Client;

  private final S3Presigner s3Presigner;

  public void uploadFileToS3bucket(String fileName, String key, String bucketName,
      ByteArrayOutputStream byteArrayOutputStream) {
    try {
      byte[] buffer = byteArrayOutputStream.toByteArray();

      InputStream inputStream = new ByteArrayInputStream(buffer);

      s3Client.putObject(PutObjectRequest.builder().bucket(bucketName)
          .key(key)
          .contentType(CONTENT_DISPOSITION)
          .contentDisposition(ATTACHMENT_FILENAME + fileName)
          .build(), RequestBody.fromInputStream(inputStream, buffer.length));
    } catch (Exception e) {
      log.error("Error occurred while uploading file to S3 bucket [{}]", e.getMessage());
      throw new IllegalArgumentException("Error occurred while uploading file to S3 bucket");
    }

  }

  public String createPresignedGetUrl(String keyName, String bucketName) {

    GetObjectRequest objectRequest = GetObjectRequest.builder()
        .bucket(bucketName)
        .key(keyName)
        .build();

    GetObjectPresignRequest presignRequest = GetObjectPresignRequest.builder()
        .signatureDuration(Duration.ofMinutes(
            s3Properties.getValidForMinutes()))  // The URL will expire in 5 minutes.
        .getObjectRequest(objectRequest)
        .build();

    PresignedGetObjectRequest presignedRequest = s3Presigner.presignGetObject(presignRequest);
    log.debug("Presigned URL: [{}]", presignedRequest.url().toString());
    log.debug("HTTP method: [{}]", presignedRequest.httpRequest().method());

    return s3Presigner.presignGetObject(presignRequest).url().toExternalForm();

  }

  public boolean checkFileAlreadyExists(String fileName, String bucketName) {
    // check in s3 bucket if file already exists using s3client
    if (fileName == null || bucketName == null) {
      log.error("File name or bucket name is null");
      throw new IllegalArgumentException("File name or bucket name is null");
    }
    var objectListing =
        s3Client.listObjects(b -> b.bucket(bucketName).prefix(fileName));

    return CollectionUtils.isNotEmpty(objectListing.contents());
  }
}