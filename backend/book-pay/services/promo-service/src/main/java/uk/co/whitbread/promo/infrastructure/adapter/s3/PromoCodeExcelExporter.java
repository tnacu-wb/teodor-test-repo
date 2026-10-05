package uk.co.whitbread.promo.infrastructure.adapter.s3;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import java.io.ByteArrayOutputStream;
import java.io.OutputStream;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Stream;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.poifs.crypt.EncryptionInfo;
import org.apache.poi.poifs.crypt.EncryptionMode;
import org.apache.poi.poifs.crypt.Encryptor;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.springframework.stereotype.Component;
import uk.co.whitbread.promo.infrastructure.repository.model.PromoCodeEntity;

@Slf4j
@Component
public class PromoCodeExcelExporter {

  @PersistenceContext
  private EntityManager entityManager;

  public byte[] exportWithPassword(
          Stream<PromoCodeEntity> promoCodes,
          String password
  ) {

    if (password == null || password.isBlank()) {
      throw new IllegalArgumentException("Password must be provided");
    }

    try (
            SXSSFWorkbook workbook = new SXSSFWorkbook(100);
            POIFSFileSystem fs = new POIFSFileSystem();
            ByteArrayOutputStream encryptedOut = new ByteArrayOutputStream()
    ) {

      workbook.setCompressTempFiles(true);

      Sheet sheet = workbook.createSheet("Promo Codes");
      sheet.createRow(0).createCell(0).setCellValue("Code");

      AtomicInteger rowIdx = new AtomicInteger(1);
      AtomicInteger counter = new AtomicInteger(0);

      promoCodes.forEach(promoCode -> {

        Row row = sheet.createRow(rowIdx.getAndIncrement());
        row.createCell(0).setCellValue(promoCode.getCode());

        if (counter.incrementAndGet() % 1000 == 0) {
          entityManager.clear();
        }
      });

      EncryptionInfo info = new EncryptionInfo(EncryptionMode.agile);
      Encryptor encryptor = info.getEncryptor();
      encryptor.confirmPassword(password);

      try (OutputStream os = encryptor.getDataStream(fs)) {
        workbook.write(os);
      }

      fs.writeFilesystem(encryptedOut);

      return encryptedOut.toByteArray();

    } catch (Exception ex) {
      throw new IllegalStateException("Excel encryption failed", ex);
    }
  }

}

