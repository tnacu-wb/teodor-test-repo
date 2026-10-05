package uk.co.whitbread.ondemandrefreshservice.infrastructure.adapters;



import static uk.co.whitbread.ondemandrefreshservice.infrastructure.config.OperaConstants.MIGRATION_CATEGORY_ONSALE;
import static uk.co.whitbread.ondemandrefreshservice.infrastructure.config.OperaConstants.MIGRATION_CATEGORY_PMS;
import static uk.co.whitbread.ondemandrefreshservice.infrastructure.config.OperaConstants.MIGRATION_ONSALE_FALSE;
import static uk.co.whitbread.ondemandrefreshservice.infrastructure.config.OperaConstants.MIGRATION_ONSALE_TRUE;
import static uk.co.whitbread.ondemandrefreshservice.infrastructure.config.OperaConstants.MIGRATION_PMS_SOURCE_BART;
import static uk.co.whitbread.ondemandrefreshservice.infrastructure.config.OperaConstants.MIGRATION_PMS_SOURCE_OPERA;
import static uk.co.whitbread.ondemandrefreshservice.infrastructure.util.SanitizingUtils.sanitize;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import uk.co.whitbread.ondemandrefreshservice.domain.ports.secondary.HotelMigrationStatusRefreshOutPort;
import uk.co.whitbread.ondemandrefreshservice.infrastructure.entity.HotelMigrationStatusEntity;
import uk.co.whitbread.ondemandrefreshservice.infrastructure.model.migrationstatus.OperaHotelDetails;
import uk.co.whitbread.ondemandrefreshservice.infrastructure.model.migrationstatus.OperaHotelDetailsList;
import uk.co.whitbread.ondemandrefreshservice.infrastructure.repository.read.HotelMigrationStatusJpaRepository;
import uk.co.whitbread.ondemandrefreshservice.infrastructure.repository.write.HotelMigrationStatusJpaRepositoryWriter;
import uk.co.whitbread.ondemandrefreshservice.infrastructure.rest.client.migration.opera.HotelMigrationStatusClient;

@Slf4j
@RequiredArgsConstructor
@Service
public class HotelMigrationStatusRefreshService implements HotelMigrationStatusRefreshOutPort {

  private final HotelMigrationStatusJpaRepository hotelMigrationStatusJpaRepository;
  private final HotelMigrationStatusClient hotelMigrationStatusClient;
  private final HotelMigrationStatusJpaRepositoryWriter hotelMigrationStatusJpaRepositoryWriter;

  @Override
  public void refreshOperaHotelMigrationStatus() {
    final Set<String> operaHotelCodes = hotelMigrationStatusJpaRepository.getOperaHotelCodes();
    if (operaHotelCodes == null || operaHotelCodes.isEmpty()) {
      log.info("Found no OPERA hotels in DB with on-sale flag as false.");
    } else {
      log.info("Number of OPERA hotels with  on-sale flag as false hotels, {}", operaHotelCodes.size());
      for (final String hotelCode : operaHotelCodes) {
        final OperaHotelDetailsList operaHotelDetailsList =
            getMigrationStatusFromOpera(hotelCode);
        log.debug("Opera Response: {}", operaHotelDetailsList);
        final OperaHotelDetails operaHotelDetails = processOperaResponse(operaHotelDetailsList, hotelCode);
        updatePmsSourceToDb(operaHotelDetails, hotelCode);
      }
    }
  }

  private void updatePmsSourceToDb(final OperaHotelDetails operaHotelDetail, final String hotelCode) {
    if (operaHotelDetail != null && operaHotelDetail.getCategory().equals(MIGRATION_CATEGORY_PMS)
        && operaHotelDetail.getCode().equals(MIGRATION_PMS_SOURCE_OPERA)) {
      Optional<HotelMigrationStatusEntity> optionalHotelMigrationStatusEntity
          = hotelMigrationStatusJpaRepositoryWriter.findById(hotelCode);
      if (optionalHotelMigrationStatusEntity.isPresent()) {
        HotelMigrationStatusEntity hotelMigrationStatusEntity
            = optionalHotelMigrationStatusEntity.get();
        hotelMigrationStatusEntity.setPmsSource(MIGRATION_PMS_SOURCE_OPERA);
        hotelMigrationStatusEntity.setOnSale(operaHotelDetail.isOnSale());
        hotelMigrationStatusEntity.setUpdatedOn(LocalDateTime.now());
        hotelMigrationStatusJpaRepositoryWriter.save(hotelMigrationStatusEntity);
      }
    }
  }

  private OperaHotelDetailsList getMigrationStatusFromOpera(final String hotelCode) {
    OperaHotelDetailsList operaHotelDetailsList = null;
    try {
      operaHotelDetailsList =
          hotelMigrationStatusClient.getHotelMigrationStatus(hotelCode);
    } catch (Exception e) {
      log.error(
          "Exception occurred while getting migration status from "
              + "opera for hotel: {} with message {}", hotelCode, e.getMessage());
    }
    return operaHotelDetailsList;
  }

  @Override
  public HotelMigrationStatusEntity getHotelMigrationStatus(final String hotelCode) {
    log.debug("Get hotel migration status for hotelCode :: {}", sanitize(hotelCode));
    HotelMigrationStatusEntity hotelMigStatusEntity = null;
    try {
      final OperaHotelDetailsList operaHotelDetailsList = hotelMigrationStatusClient.getHotelMigrationStatus(
          hotelCode);
      log.debug("Hotel Migration Status from Opera : {}", operaHotelDetailsList);
      final OperaHotelDetails hotelDetailsWithPmsSrc = processOperaResponse(operaHotelDetailsList, hotelCode);
      log.debug("HotelMigrationStatus value - {}", hotelDetailsWithPmsSrc);
      if (hotelDetailsWithPmsSrc.getCategory().equals(MIGRATION_CATEGORY_PMS)
          && hotelDetailsWithPmsSrc.getCode().equals(MIGRATION_PMS_SOURCE_OPERA)) {
        hotelMigStatusEntity = buildHotelMigrationStatusEntity(hotelCode,
            hotelDetailsWithPmsSrc.isOnSale(),
            MIGRATION_PMS_SOURCE_OPERA);
      } else {
        hotelMigStatusEntity = buildHotelMigrationStatusEntity(hotelCode,
            hotelDetailsWithPmsSrc.isOnSale(),
            MIGRATION_PMS_SOURCE_BART);
      }
    } catch (final Exception e) {
      log.error("Exception - {} ", e.getStackTrace());
    }
    return hotelMigStatusEntity;
  }

  private OperaHotelDetails processOperaResponse(final OperaHotelDetailsList operaHotelDetailsList,
      final String hotelCode) {
    OperaHotelDetails hotelDetailsWithPmsSrc = null;
    boolean isOnSale;
    if (operaHotelDetailsList != null && operaHotelDetailsList.getHotelDetails() != null) {
      final List<OperaHotelDetails> hotelDetails = operaHotelDetailsList.getHotelDetails();
      final OperaHotelDetails hotelDetailsWithOnSaleFlag = hotelDetails.stream()
          .filter(
              hotelDetail -> hotelDetail.getCategory().equals(MIGRATION_CATEGORY_ONSALE))
          .findFirst()
          .orElse(buildOperaHotelDetails(hotelCode,
              MIGRATION_CATEGORY_ONSALE,
              MIGRATION_ONSALE_FALSE));
      isOnSale = hotelDetailsWithOnSaleFlag.getCode().equalsIgnoreCase(MIGRATION_ONSALE_TRUE);
      hotelDetailsWithPmsSrc = hotelDetails.stream()
          .filter(hotelDetail -> hotelDetail.getCategory().equals(MIGRATION_CATEGORY_PMS))
          .findFirst()
          .orElse(buildOperaHotelDetails(hotelCode,
              MIGRATION_CATEGORY_PMS,
              MIGRATION_PMS_SOURCE_BART));
      hotelDetailsWithPmsSrc.setOnSale(isOnSale);
    }
    return hotelDetailsWithPmsSrc;
  }

  private HotelMigrationStatusEntity buildHotelMigrationStatusEntity(final String hotelCode, final boolean onSale,
      final String pmsSource) {
    return HotelMigrationStatusEntity.builder()
        .hotelCode(hotelCode)
        .pmsSource(pmsSource)
        .onSale(onSale)
        .updatedOn(LocalDateTime.now())
        .build();
  }

  private OperaHotelDetails buildOperaHotelDetails(final String hotelCode, final String category,
      final String code) {
    return OperaHotelDetails.builder()
        .hotelId(hotelCode)
        .category(category)
        .code(code)
        .build();
  }
}

