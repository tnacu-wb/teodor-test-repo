package uk.co.whitbread.promo.infrastructure.s3;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;

import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import uk.co.whitbread.promo.infrastructure.adapter.s3.PromoBatchS3Uploader;
import uk.co.whitbread.promo.infrastructure.exception.PromoBatchFileUploadException;
import uk.co.whitbread.promo.infrastructure.repository.model.PromoBatchEntity;

@ExtendWith(MockitoExtension.class)
class PromoBatchS3UploaderTest {

  @Mock
  private S3Client s3Client;

  @Spy
  @InjectMocks
  private PromoBatchS3Uploader uploader;

  @Test
  void uploadExcel_shouldUploadFileAndReturnS3Key() {
    // Arrange
    UUID batchId = UUID.randomUUID();
    byte[] excelBytes = "excel-data".getBytes();

    PromoBatchEntity entity = new PromoBatchEntity();
    entity.setOperaPromoCode("FX10R");
    entity.setCampaignName("HEAPTI");

    // Act
    String key = uploader.uploadExcel(batchId, excelBytes, entity);

    // Assert
    assertEquals(
            "FX10R/" + batchId + "/HEAPTI.xlsx",
            key
    );
    ArgumentCaptor<PutObjectRequest> requestCaptor =
            ArgumentCaptor.forClass(PutObjectRequest.class);

    verify(s3Client).putObject(
            requestCaptor.capture(),
            any(RequestBody.class)
    );
    PutObjectRequest request = requestCaptor.getValue();
    assertEquals("FX10R/" + batchId + "/HEAPTI.xlsx", request.key());
    assertEquals("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet",
            request.contentType());
    assertEquals(excelBytes.length, request.contentLength());
  }

  @Test
  void uploadExcel_shouldThrowException_whenExcelIsEmpty() {
    UUID batchId = UUID.randomUUID();
    PromoBatchEntity entity = new PromoBatchEntity();

    assertThrows(
            PromoBatchFileUploadException.class,
            () -> uploader.uploadExcel(batchId, new byte[0], entity)
    );
  }

  @Test
  void uploadExcel_shouldThrowException_whenExcelIsNull() {
    UUID batchId = UUID.randomUUID();
    PromoBatchEntity entity = new PromoBatchEntity();

    assertThrows(
            PromoBatchFileUploadException.class,
            () -> uploader.uploadExcel(batchId, null, entity)
    );
  }

  @Test
  void uploadZip_shouldZipAndUploadAndReturnKey() {
    // Arrange
    UUID batchId = UUID.randomUUID();
    byte[] excelBytes = "excel".getBytes();

    PromoBatchEntity entity = new PromoBatchEntity();
    entity.setOperaPromoCode("FX10R");
    entity.setCampaignName("HEAPTI");

    // Act
    String key = uploader.uploadZip(batchId, excelBytes, entity);

    // Assert
    assertEquals("FX10R/" + batchId + "/HEAPTI.zip", key);

    ArgumentCaptor<PutObjectRequest> captor =
            ArgumentCaptor.forClass(PutObjectRequest.class);

    verify(s3Client).putObject(captor.capture(), any(RequestBody.class));

    PutObjectRequest request = captor.getValue();
    assertEquals(key, request.key());
    assertEquals("application/zip", request.contentType());
  }

  @Test
  void uploadZip_shouldThrowException_whenExcelIsEmpty() {
    UUID batchId = UUID.randomUUID();
    PromoBatchEntity entity = new PromoBatchEntity();

    assertThrows(
            PromoBatchFileUploadException.class,
            () -> uploader.uploadZip(batchId, new byte[0], entity)
    );
  }

}

