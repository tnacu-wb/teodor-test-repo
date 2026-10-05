package uk.co.whitbread.promo.infrastructure.repository;

import static org.apache.commons.compress.utils.ArchiveUtils.sanitize;

import java.util.UUID;
import java.util.stream.Stream;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uk.co.whitbread.promo.infrastructure.adapter.s3.PromoBatchS3Uploader;
import uk.co.whitbread.promo.infrastructure.adapter.s3.PromoCodeExcelExporter;
import uk.co.whitbread.promo.infrastructure.repository.model.PromoBatchEntity;
import uk.co.whitbread.promo.infrastructure.repository.model.PromoCodeEntity;

@Service
@RequiredArgsConstructor
@Slf4j
public class PromoBatchS3UploaderService {

  private final PromoCodeRepository promoCodeRepository;
  private final PromoCodeExcelExporter promoCodeExcelExporter;
  private final PromoBatchS3Uploader promoBatchS3Uploader;

  @Transactional(readOnly = true)
  public String createAndUploadFile(
          UUID batchId,
          String password,
          PromoBatchEntity batch) {

    try (Stream<PromoCodeEntity> stream =
                 promoCodeRepository.streamByBatchId(batchId)) {

      byte[] excelBytes = promoCodeExcelExporter.exportWithPassword(stream, password);

      String excelFileName = sanitize(batch.getCampaignName()) + ".xlsx";
      byte[] zipBytes = promoBatchS3Uploader.zipExcel(excelBytes, excelFileName);

      return promoBatchS3Uploader.uploadZip(batchId, zipBytes, batch);
    }
  }

  public String createAndUploadGenericPromoFile(
          UUID batchId,
          String password,
          PromoBatchEntity batch) {

    PromoCodeEntity promoCode = PromoCodeEntity.builder()
            .code(batch.getPrefix())
            .build();

    try (Stream<PromoCodeEntity> stream = Stream.of(promoCode)) {

      byte[] excelBytes = promoCodeExcelExporter.exportWithPassword(stream, password);

      String excelFileName = sanitize(batch.getCampaignName()) + ".xlsx";
      byte[] zipBytes = promoBatchS3Uploader.zipExcel(excelBytes, excelFileName);

      return promoBatchS3Uploader.uploadZip(batchId, zipBytes, batch);
    }
  }
}
