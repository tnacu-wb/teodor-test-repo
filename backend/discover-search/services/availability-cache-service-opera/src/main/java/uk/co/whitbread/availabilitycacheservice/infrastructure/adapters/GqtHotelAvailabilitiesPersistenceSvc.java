package uk.co.whitbread.availabilitycacheservice.infrastructure.adapters;

import static uk.co.whitbread.availabilitycacheservice.domain.utils.SanitizingUtils.sanitize;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import uk.co.whitbread.availabilitycacheservice.domain.model.gqt.GqtOperaHotelAvailabilities;
import uk.co.whitbread.availabilitycacheservice.domain.model.gqt.GqtSearchPayload;
import uk.co.whitbread.availabilitycacheservice.domain.ports.secondary.GqtHotelAvailPostProcessorPort;
import uk.co.whitbread.availabilitycacheservice.domain.ports.secondary.GqtHotelAvailabilitiesPersistencePort;
import uk.co.whitbread.availabilitycacheservice.domain.utils.SanitizingUtils;
import uk.co.whitbread.availabilitycacheservice.infrastructure.entity.HotelMigrationStatusEntity;
import uk.co.whitbread.availabilitycacheservice.infrastructure.model.hotelavailability.HotelAvailabilitiesResultSet;
import uk.co.whitbread.availabilitycacheservice.infrastructure.repository.read.GqtHotelAvailabilitiesJpaRepository;
import uk.co.whitbread.availabilitycacheservice.infrastructure.repository.read.HotelMigrationStatusReaderRepository;

@Slf4j
@AllArgsConstructor
public class GqtHotelAvailabilitiesPersistenceSvc implements GqtHotelAvailabilitiesPersistencePort {

  private static final String PMS_SOURCE_OPERA = "OPERA";
  private final GqtHotelAvailabilitiesJpaRepository gqtHotelAvailabilitiesJpaRepository;

  private final HotelMigrationStatusReaderRepository hotelMigStatusReaderRepository;

  private final GqtHotelAvailPostProcessorPort gqtHotelAvailPostProcessorPort;

  @Override
  public List<GqtOperaHotelAvailabilities> getGqtHotelAvailabilitiesForOpera(
      final GqtSearchPayload gqtSearchPayload) {

    log.debug(
        "processing started for the getHotelAvailabilitiesForOpera with operaHotelsSearchCriteria - {}",
        gqtSearchPayload);
    final LocalDate arrivalDate = LocalDate.parse(gqtSearchPayload.getArrival());
    final LocalDate departureDate = LocalDate.parse(gqtSearchPayload.getDeparture());

    //Get Migration Status from DB.
    final List<HotelMigrationStatusEntity> hotelMigStatusList =
        hotelMigStatusReaderRepository.getMigrationStatusForHotels(
            gqtSearchPayload.getHotelCodes());

    final List<String> onSaleFalseHotelCodes = hotelMigStatusList.stream()
        .filter(hotelMigStatus -> !hotelMigStatus.isOnSale())
        .map(HotelMigrationStatusEntity::getHotelCode).collect(Collectors.toList());
    log.info("Hotels with onSale false are - {}", sanitize(onSaleFalseHotelCodes));

    if (!onSaleFalseHotelCodes.isEmpty()) {
      log.debug("As the onSale False hotels is not empty, "
              + "excluding these hotels from searchCriteria not to be considered for further processing - {}",
          onSaleFalseHotelCodes.stream().map(SanitizingUtils::sanitize).toList());

      gqtSearchPayload
          .setHotelCodes(gqtSearchPayload.getHotelCodes()
              .stream().filter(hotelCode -> !onSaleFalseHotelCodes.contains(hotelCode))
              .collect(Collectors.toList()));
      log.debug("After excluding the onSale False hotels, gqtSearchCriteria - {}",
          gqtSearchPayload);

      if (gqtSearchPayload.getHotelCodes().isEmpty()) {
        log.info("After excluding the onSale False hotels, the hotelCodes list is Empty, "
            + "so no further processing required and returning empty List");
        return Collections.emptyList();
      }
    }

    final List<String> operaHotelCodes =
        hotelMigStatusList.stream()
            .filter(hotelMigStatus -> hotelMigStatus.getPmsSource().equals(PMS_SOURCE_OPERA)
                && hotelMigStatus.isOnSale())
            .map(HotelMigrationStatusEntity::getHotelCode).collect(Collectors.toList());

    return getHotelAvailabilitiesFromRepository(
        operaHotelCodes, arrivalDate, departureDate, gqtSearchPayload);
  }

  private List<GqtOperaHotelAvailabilities> getHotelAvailabilitiesFromRepository(
      final List<String> operaHotelCodes,
      final LocalDate arrivalDate, final LocalDate departureDate,
      final GqtSearchPayload gqtSearchPayload) {

    log.info(
        "Process getHotelAvailabilitiesFromRepository - Invoking JPA repository with params - operaHotelCodes - {},\n"
            + "arrivalDate - {}, departureDate - {}, gqtSearchPayload - {} ",
        operaHotelCodes, arrivalDate, departureDate, gqtSearchPayload);

    List<HotelAvailabilitiesResultSet> hotelAvailabilitiesResultSet;
    if (operaHotelCodes.isEmpty()) {
      log.info("As operaHotelCodes is Empty returning empty list.");
      return Collections.emptyList();
    } else {
      log.info("As operaHotelCodes is not empty, invoking opera hotels JPA.");
      hotelAvailabilitiesResultSet =
          gqtHotelAvailabilitiesJpaRepository
              .findAvailabilitiesForGqtOpera(operaHotelCodes, arrivalDate, departureDate);
    }
    log.debug("hotelAvailabilitiesResultSet from the JPA- {}", hotelAvailabilitiesResultSet);

    return processHotelAvailabilities(hotelAvailabilitiesResultSet, gqtSearchPayload);

  }

  private List<GqtOperaHotelAvailabilities> processHotelAvailabilities(
      final List<HotelAvailabilitiesResultSet> hotelAvailabilitiesResultSet,
      final GqtSearchPayload gqtSearchPayload) {

    final List<GqtOperaHotelAvailabilities> gqtOperaHotelAvailabilities = gqtHotelAvailPostProcessorPort
        .processHotelResultSetToGqtHotelAvail(hotelAvailabilitiesResultSet, gqtSearchPayload);

    log.info("gqtOperaHotelAvailabilities: {}", gqtOperaHotelAvailabilities);

    return gqtOperaHotelAvailabilities;

  }

}
