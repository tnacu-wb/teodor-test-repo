package uk.co.whitbread.promo.infrastructure.s3;

import static org.junit.jupiter.api.Assertions.*;

import java.nio.charset.StandardCharsets;
import java.util.List;

import org.junit.jupiter.api.Test;

import uk.co.whitbread.promo.infrastructure.adapter.s3.PromoCodeCsvExporter;
import uk.co.whitbread.promo.infrastructure.repository.model.PromoCodeEntity;

class PromoCodeCsvExporterTest {

  private final PromoCodeCsvExporter exporter = new PromoCodeCsvExporter();

  @Test
  void export_shouldReturnCsvBytes() {
    // Arrange
    PromoCodeEntity code = new PromoCodeEntity();
    code.setCode("FX10R");

    List<PromoCodeEntity> promoCodes = List.of(code);

    // Act
    byte[] result = exporter.export(promoCodes);

    // Assert
    assertNotNull(result);
    String csv = new String(result, StandardCharsets.UTF_8);

    assertTrue(csv.contains("FX10R"));
  }

  @Test
  void export_shouldHandleEmptyList() {
    byte[] result = exporter.export(List.of());
    assertNotNull(result);
    assertTrue(result.length > 0);
  }
}
