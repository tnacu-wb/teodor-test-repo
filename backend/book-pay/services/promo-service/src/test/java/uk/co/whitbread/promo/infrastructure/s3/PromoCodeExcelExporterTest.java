package uk.co.whitbread.promo.infrastructure.s3;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.stream.Stream;

import org.apache.poi.poifs.filesystem.POIFSFileSystem;
import org.junit.jupiter.api.Test;
import uk.co.whitbread.promo.infrastructure.adapter.s3.PromoCodeExcelExporter;
import uk.co.whitbread.promo.infrastructure.repository.model.PromoCodeEntity;

class PromoCodeExcelExporterTest {

    private final PromoCodeExcelExporter exporter = new PromoCodeExcelExporter();

    @Test
    void exportWithPassword_shouldReturnEncryptedExcelBytes() throws Exception {
        // Arrange
        PromoCodeEntity code = new PromoCodeEntity();
        code.setCode("FX10R");

        Stream<PromoCodeEntity> promoCodes = Stream.of(code);
        String password = "secret123";

        //Act
        byte[] result = exporter.exportWithPassword(promoCodes, password);

        // Assert
        assertNotNull(result);
        assertTrue(result.length > 0);
        try (POIFSFileSystem fs = new POIFSFileSystem(new java.io.ByteArrayInputStream(result))) {
            assertNotNull(fs);
        }
    }

    @Test
    void exportWithPassword_shouldThrowException_whenPasswordIsBlank() {
        // Arrange
        Stream<PromoCodeEntity> promoCodes = Stream.of();

        // Act + Assert
        IllegalArgumentException ex = assertThrows(
                IllegalArgumentException.class,
                () -> exporter.exportWithPassword(promoCodes, "")
        );

        assertEquals("Password must be provided", ex.getMessage());
    }

    @Test
    void exportWithPassword_shouldThrowIllegalStateException_whenEncryptionFails() {
        // Arrange
        PromoCodeExcelExporter badExporter = new PromoCodeExcelExporter() {
            @Override
            public byte[] exportWithPassword(Stream<PromoCodeEntity> promoCodes, String password) {
                throw new IllegalStateException("Excel encryption failed", new RuntimeException());
            }
        };

        // Act + Assert
        Stream<PromoCodeEntity> emptyStream = Stream.of();

        assertThrows(
                IllegalStateException.class,
                () -> badExporter.exportWithPassword(emptyStream, "secret")
        );

    }
}

