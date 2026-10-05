package uk.co.whitbread.promo.infrastructure.adapter.s3;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;
import java.nio.ByteBuffer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.UUID;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import uk.co.whitbread.promo.infrastructure.exception.ErrorCode;
import uk.co.whitbread.promo.infrastructure.exception.PromoBatchFileUploadException;
import uk.co.whitbread.promo.infrastructure.repository.model.PromoBatchEntity;

@Component
@Slf4j
public class PromoBatchS3Uploader {

  private final S3Client s3Client;
  private final String bucketName;

  public PromoBatchS3Uploader(
      S3Client s3Client,
      @Value("${aws.s3.promoBucketName}") String bucketName) {
    this.s3Client = s3Client;
    this.bucketName = bucketName;
  }

  public String uploadCsv(UUID batchId, byte[] csvBytes) {

    String key = "promo-batches/" + batchId + "/promo-codes.csv";

    PutObjectRequest request = PutObjectRequest.builder()
        .bucket(bucketName)
        .key(key)
        .contentType("text/csv")
        .build();

    s3Client.putObject(request, RequestBody.fromBytes(csvBytes));
    return key;
  }

  public String uploadZip(
          UUID batchId,
          byte[] zipBytes,
          PromoBatchEntity promoBatchEntity
  ) {
    validateNotEmpty(zipBytes);
    String zipKey = buildZipKey(
            promoBatchEntity.getOperaPromoCode(),
            batchId,
            promoBatchEntity.getCampaignName()
    );

    PutObjectRequest putObjectRequest =
            PutObjectRequest.builder()
                    .bucket(bucketName)
                    .key(zipKey)
                    .contentType("application/zip")
                    .contentLength((long) zipBytes.length)
                    .build();

    s3Client.putObject(
            putObjectRequest,
            RequestBody.fromByteBuffer(ByteBuffer.wrap(zipBytes))
    );
    return zipKey;
  }

  public String uploadExcel(
      UUID batchId,
      byte[] excelBytes,
      PromoBatchEntity promoBatchEntity
  ) {
    validateNotEmpty(excelBytes);
    String s3Key = buildExcelKey(promoBatchEntity.getOperaPromoCode(), batchId,
        promoBatchEntity.getCampaignName());

    PutObjectRequest putObjectRequest =
        PutObjectRequest.builder()
            .bucket(bucketName)
            .key(s3Key)
            .contentType(
                "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet")
            .contentLength((long) excelBytes.length)
            .build();

    s3Client.putObject(
        putObjectRequest,
        RequestBody.fromByteBuffer(ByteBuffer.wrap(excelBytes))
    );
    log.info("Excel Upload Successfully");
    return s3Key;
  }

  private String buildExcelKey(String operaPromoCode, UUID batchId, String campaignName) {
    return String.format(
        "%s/%s/%s.xlsx",
        sanitize(operaPromoCode),
        sanitize(batchId.toString()),
        sanitize(campaignName)
    );
  }

  private String sanitize(String input) {
    if (input == null) {
      return "NA";
    }

    return input
        .trim()
        .replaceAll("[\\\\/]+", "-")
        .replaceAll("\\s+", "_")
        .replaceAll("[^a-zA-Z0-9._-]", "");
  }

  private String buildZipKey(String operaPromoCode, UUID batchId, String campaignName) {
    return String.format(
        "%s/%s/%s.zip",
        sanitize(operaPromoCode),
        sanitize(batchId.toString()),
        sanitize(campaignName)
    );
  }

  public byte[] zipExcel(byte[] excelBytes, String excelFileName) {
    try (ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ZipOutputStream zos = new ZipOutputStream(baos)) {

      ZipEntry entry = new ZipEntry(excelFileName);
      zos.putNextEntry(entry);
      zos.write(excelBytes);
      zos.closeEntry();
      zos.finish();
      return baos.toByteArray();
    } catch (Exception ex) {
      log.error("Failed to zip excel", ex);
      throw new PromoBatchFileUploadException(
          ErrorCode.FILE_UPLOAD_FAILED,
          "Failed to zip excel"
      );
    }
  }

  private void validateNotEmpty(byte[] bytes) {
    if (bytes == null || bytes.length == 0) {
      throw new PromoBatchFileUploadException(
          ErrorCode.DIGITAL_PROMO_BATCH_EXCEPTION,
          "Excel file is empty"
      );
    }
  }
}