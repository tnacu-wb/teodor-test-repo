package uk.co.whitbread.availabilitycacheservice.infrastructure.adapters;

import static uk.co.whitbread.availabilitycacheservice.domain.utils.SanitizingUtils.sanitize;
import static uk.co.whitbread.availabilitycacheservice.infrastructure.util.HotelAvailabilitiesUtil.buildSearchCriteriaFromOperaCriteria;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import uk.co.whitbread.availabilitycacheservice.domain.model.availabilities.Hotel;
import uk.co.whitbread.availabilitycacheservice.domain.ports.secondary.OperaHotelPersistenceAdapter;
import uk.co.whitbread.availabilitycacheservice.domain.ports.secondary.OperaHotelsPostProcessorPort;
import uk.co.whitbread.availabilitycacheservice.infrastructure.entity.HotelMigrationStatusEntity;
import uk.co.whitbread.availabilitycacheservice.infrastructure.model.hotelavailability.HotelAvailabilitiesResultSet;
import uk.co.whitbread.availabilitycacheservice.infrastructure.properties.HotelPriceProperties;
import uk.co.whitbread.availabilitycacheservice.infrastructure.repository.read.HotelAvailabilitiesJpaRepository;
import uk.co.whitbread.availabilitycacheservice.infrastructure.repository.read.HotelMigrationStatusReaderRepository;
import uk.co.whitbread.availabilitycacheservice.infrastructure.rest.request.OperaHotelsSearchCriteria;
import uk.co.whitbread.availabilitycacheservice.infrastructure.rest.request.SearchCriteria;

@Slf4j
@AllArgsConstructor
public class OperaHotelAvailPersistenceSvc implements OperaHotelPersistenceAdapter {

  private static final String PMS_SOURCE_OPERA = "OPERA";
  private final HotelAvailabilitiesJpaRepository hotelAvailabilitiesJpaRepository;
  private final OperaHotelsPostProcessorPort operaHotelsPostProcessorPort;

  private final HotelMigrationStatusReaderRepository hotelMigStatusReaderRepository;
  private final HotelPriceProperties hotelPriceProperties;

  @Override
  public List<Hotel> getHotelAvailabilitiesForOpera(
      final OperaHotelsSearchCriteria operaHotelsSearchCriteria) {

    log.debug(
        "processing started for the getHotelAvailabilitiesForOpera with operaHotelsSearchCriteria - {}",
        operaHotelsSearchCriteria);
    final LocalDate arrivalDate = LocalDate.parse(operaHotelsSearchCriteria.getArrival());
    final LocalDate departureDate = LocalDate.parse(operaHotelsSearchCriteria.getDeparture());

    //Get Migration Status from DB.
    final List<HotelMigrationStatusEntity> hotelMigStatusList =
        hotelMigStatusReaderRepository.getMigrationStatusForHotels(operaHotelsSearchCriteria.getHotelCodes());

    final List<String> onSaleFalseHotelCodes = hotelMigStatusList.stream()
        .filter(hotelMigStatus -> !hotelMigStatus.isOnSale())
        .map(HotelMigrationStatusEntity::getHotelCode).collect(Collectors.toList());
    log.debug("Excluded Hotels with onSale false are - {}", sanitize(onSaleFalseHotelCodes));
    if (!onSaleFalseHotelCodes.isEmpty()) {
      log.debug("As the onSale False hotels is not empty, "
              + "excluding these hotels from searchCriteria not to be considered for further processing - {}",
          onSaleFalseHotelCodes);
      operaHotelsSearchCriteria
          .setHotelCodes(operaHotelsSearchCriteria.getHotelCodes()
              .stream().filter(hotelCode -> !onSaleFalseHotelCodes.contains(hotelCode))
              .collect(Collectors.toList()));
      log.debug("operaHotelsSearchCriteria - {}", operaHotelsSearchCriteria);
      if (operaHotelsSearchCriteria.getHotelCodes().isEmpty()) {
        log.debug("After excluding the onSale False hotels, the hotelCodes list is Empty, "
            + "so no further processing required and returning empty List");
        return Collections.emptyList();
      }
    }

    final List<String> operaHotelCodes =
        hotelMigStatusList.stream()
            .filter(
                hotelMigStatus -> hotelMigStatus.getPmsSource().equals(PMS_SOURCE_OPERA) && hotelMigStatus.isOnSale())
            .map(HotelMigrationStatusEntity::getHotelCode).collect(Collectors.toList());

    final Set<String> operaRoomTypesFromCriteria =
        Arrays.stream(operaHotelsSearchCriteria.getRoomTypes())
            .flatMap(Arrays::stream)
            .collect(Collectors.toSet());

    return getHotelAvailabilitiesFromRepository(operaHotelCodes, operaRoomTypesFromCriteria,
        arrivalDate, departureDate, operaHotelsSearchCriteria);
  }

  private List<Hotel> getHotelAvailabilitiesFromRepository(final List<String> operaHotelCodes,
      final Set<String> operaRoomTypesFromCriteria,
      final LocalDate arrivalDate, final LocalDate departureDate,
      final OperaHotelsSearchCriteria operaHotelsSearchCriteria) {

    log.info(
        "Process getHotelAvailabilitiesFromRepository - Invoking JPA repository with params - , "
            + "operaHotelCodes - {},\n"
            + "arrivalDate - {}, departureDate - {}, operaRoomTypesFromCriteria - {}",
        operaHotelCodes, arrivalDate, departureDate, operaRoomTypesFromCriteria);

    List<HotelAvailabilitiesResultSet> hotelAvailabilitiesResultSet = new ArrayList<>();
    if (!operaHotelCodes.isEmpty()) {
      log.info("As operaHotelCodes is not empty, invoking opera hotels JPA");
      hotelAvailabilitiesResultSet =
          hotelAvailabilitiesJpaRepository
              .findAvailabilitiesForOpera(operaHotelCodes,
                  arrivalDate, departureDate, operaRoomTypesFromCriteria);
    }
    log.debug("operaHotelsSearchCriteria - {},\n"
            + "hotelAvailabilitiesResultSet - {}", operaHotelsSearchCriteria, hotelAvailabilitiesResultSet);
    return processHotelAvailabilities(operaHotelsSearchCriteria, hotelAvailabilitiesResultSet);
  }

  private List<Hotel> processHotelAvailabilities(
      final OperaHotelsSearchCriteria operaHotelsSearchCriteria,
      final List<HotelAvailabilitiesResultSet> hotelAvailabilitiesResultSet) {

    final List<String> validRoomTypes = hotelPriceProperties.getRoomTypes();

    final Set<String> operaRoomTypes =
        Arrays.stream(operaHotelsSearchCriteria.getRoomTypes())
            .flatMap(Arrays::stream)
            .collect(Collectors.toSet());

    String[] operaRoomTypeValues = getOperaRoomTypes(operaHotelsSearchCriteria,
        operaRoomTypes, validRoomTypes);

    SearchCriteria searchCriteria = buildSearchCriteriaFromOperaCriteria(operaHotelsSearchCriteria,
        operaRoomTypeValues);

    final List<Hotel> hotels = operaHotelsPostProcessorPort.performOperaHotelsPostProcess(
        operaHotelsSearchCriteria,
        searchCriteria, hotelAvailabilitiesResultSet);

    return hotels.stream().peek(hotel -> hotel.setPmsSource(PMS_SOURCE_OPERA)).collect(Collectors.toList());
  }

  @Override
  public String[] getOperaRoomTypes(
      final OperaHotelsSearchCriteria operaHotelsSearchCriteria,
      final Set<String> operaRoomTypes,
      final List<String> validRoomTypes) {

    final String[][] roomTypes = operaHotelsSearchCriteria.getRoomTypes();
    final int[] roomQuantity = operaHotelsSearchCriteria.getRoomQty();
    String[] types;
    if (operaRoomTypes.size() == operaHotelsSearchCriteria.getRooms()) {
      types = new String[operaRoomTypes.size()];
      operaRoomTypes.toArray(types);
      return types;
    }

    int typesIndex = 0;
    int typesSize = Arrays.stream(roomQuantity).sum();
    types = new String[typesSize];
    for (int i = 0; i < roomQuantity.length; i++) {
      int roomCount = roomQuantity[i];
      String[] roomTypeOpera = roomTypes[i];
      String roomType = null;
      Optional<String> optionalOperaRoomType = Arrays.stream(roomTypeOpera).filter(roomType1 ->
          validRoomTypes.stream()
              .anyMatch(validOperaRoomType -> validOperaRoomType.equals(roomType1))).findFirst();

      if (optionalOperaRoomType.isPresent()) {
        roomType = optionalOperaRoomType.get();
      }

      for (int k = typesIndex, j = 0; j < roomCount; k++, j++) {
        types[k] = roomType;
        typesIndex++;
      }
    }
    return types;
  }

}
