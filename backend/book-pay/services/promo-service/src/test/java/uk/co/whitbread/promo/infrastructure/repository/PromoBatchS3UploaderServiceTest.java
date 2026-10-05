package uk.co.whitbread.promo.infrastructure.repository;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.UUID;
import java.util.stream.Stream;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import uk.co.whitbread.promo.infrastructure.adapter.s3.PromoBatchS3Uploader;
import uk.co.whitbread.promo.infrastructure.adapter.s3.PromoCodeExcelExporter;
import uk.co.whitbread.promo.infrastructure.repository.model.PromoBatchEntity;
import uk.co.whitbread.promo.infrastructure.repository.model.PromoCodeEntity;

@ExtendWith(MockitoExtension.class)
class PromoBatchS3UploaderServiceTest {

    @Mock
    private PromoCodeRepository promoCodeRepository;

    @Mock
    private PromoCodeExcelExporter promoCodeExcelExporter;

    @Mock
    private PromoBatchS3Uploader promoBatchS3Uploader;

    @InjectMocks
    private PromoBatchS3UploaderService service;

    @Test
    void createAndUploadFile_shouldUploadZipAndReturnS3Key() {

        // Arrange
        UUID batchId = UUID.randomUUID();
        String password = "secret123";

        PromoBatchEntity batch = PromoBatchEntity.builder()
                .batchId(batchId)
                .campaignName("Summer Promo")
                .build();

        Stream<PromoCodeEntity> promoCodes =
                Stream.of(PromoCodeEntity.builder().code("ABC123").build());

        byte[] excelBytes = "excel-bytes".getBytes();
        byte[] zipBytes = "zip-bytes".getBytes();
        String expectedS3Key = "s3/path/promo.zip";

        when(promoCodeRepository.streamByBatchId(batchId))
                .thenReturn(promoCodes);

        when(promoCodeExcelExporter.exportWithPassword(any(), eq(password)))
                .thenReturn(excelBytes);

        when(promoBatchS3Uploader.zipExcel(any(byte[].class), anyString()))
                .thenReturn(zipBytes);

        when(promoBatchS3Uploader.uploadZip(batchId, zipBytes, batch))
                .thenReturn(expectedS3Key);

        // Act
        String actualS3Key =
                service.createAndUploadFile(batchId, password, batch);

        // Assert
        assertEquals(expectedS3Key, actualS3Key);

        verify(promoCodeRepository)
                .streamByBatchId(batchId);

        verify(promoCodeExcelExporter)
                .exportWithPassword(any(), eq(password));

        verify(promoBatchS3Uploader)
                .zipExcel(excelBytes, "Summer Promo.xlsx");

        verify(promoBatchS3Uploader)
                .uploadZip(batchId, zipBytes, batch);
    }

    @Test
    void createAndUploadFile_shouldPropagateException_whenExcelExportFails() {

        // Arrange
        UUID batchId = UUID.randomUUID();
        String password = "secret123";

        PromoBatchEntity batch = PromoBatchEntity.builder()
                .batchId(batchId)
                .campaignName("Summer Promo")
                .build();

        Stream<PromoCodeEntity> promoCodes =
                Stream.of(PromoCodeEntity.builder().code("ABC123").build());

        when(promoCodeRepository.streamByBatchId(batchId))
                .thenReturn(promoCodes);

        when(promoCodeExcelExporter.exportWithPassword(any(), anyString()))
                .thenThrow(new RuntimeException("Excel export failed"));

        // Act + Assert
        RuntimeException exception = assertThrows(
                RuntimeException.class,
                () -> service.createAndUploadFile(batchId, password, batch)
        );

        assertEquals("Excel export failed", exception.getMessage());
    }

    @Test
    void createAndUploadGenericPromoFile_shouldUploadZipAndReturnS3Key() {

        // Arrange
        UUID batchId = UUID.randomUUID();
        String password = "secret123";

        PromoBatchEntity batch = PromoBatchEntity.builder()
                .batchId(batchId)
                .campaignName("Summer Promo")
                .prefix("SALE10")
                .build();

        byte[] excelBytes = "excel-bytes".getBytes();
        byte[] zipBytes = "zip-bytes".getBytes();
        String expectedS3Key = "s3/path/promo.zip";

        when(promoCodeExcelExporter.exportWithPassword(any(), eq(password)))
                .thenReturn(excelBytes);

        when(promoBatchS3Uploader.zipExcel(any(byte[].class), anyString()))
                .thenReturn(zipBytes);

        when(promoBatchS3Uploader.uploadZip(batchId, zipBytes, batch))
                .thenReturn(expectedS3Key);

        // Act
        String actualS3Key =
                service.createAndUploadGenericPromoFile(batchId, password, batch);

        // Assert
        assertEquals(expectedS3Key, actualS3Key);

        verify(promoCodeExcelExporter)
                .exportWithPassword(any(), eq(password));

        verify(promoBatchS3Uploader)
                .zipExcel(excelBytes, "Summer Promo.xlsx");

        verify(promoBatchS3Uploader)
                .uploadZip(batchId, zipBytes, batch);
    }

    @Test
    void createAndUploadGenericPromoFile_shouldPropagateException_whenExcelExportFails() {

        // Arrange
        UUID batchId = UUID.randomUUID();
        String password = "secret123";

        PromoBatchEntity batch = PromoBatchEntity.builder()
                .batchId(batchId)
                .campaignName("Summer Promo")
                .prefix("SALE10")
                .build();

        when(promoCodeExcelExporter.exportWithPassword(any(), anyString()))
                .thenThrow(new RuntimeException("Excel export failed"));

        // Act + Assert
        RuntimeException exception = assertThrows(
                RuntimeException.class,
                () -> service.createAndUploadGenericPromoFile(batchId, password, batch)
        );

        assertEquals("Excel export failed", exception.getMessage());
    }
}
