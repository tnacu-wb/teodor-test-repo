package uk.co.whitbread.availabilitycacheservice.infrastructure.repository.read;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import uk.co.whitbread.availabilitycacheservice.infrastructure.entity.HotelMigrationStatusEntity;

@Repository
@Slf4j
@RequiredArgsConstructor
public class HotelMigrationStatusReaderRepository {

  private final HotelMigrationStatusReaderJpaRepository hotelMigStatusReaderJpaRepository;

  public List<HotelMigrationStatusEntity> getMigrationStatusForHotels(
      final List<String> hotelCodes) {

    log.debug("getMigrationStatusForHotels for hotelCodes - {} ", hotelCodes);
    if (hotelCodes.isEmpty()) {
      log.debug("HotelCodes is empty, so return empty List");
      return Collections.emptyList();
    }

    List<HotelMigrationStatusEntity> hotelMigrationStatusEntities =
        hotelMigStatusReaderJpaRepository.findAllById(hotelCodes);

    final Set<String> hotelsFromMigStatus = hotelMigrationStatusEntities.stream()
        .map(HotelMigrationStatusEntity::getHotelCode)
        .collect(Collectors.toSet());
    if (hotelsFromMigStatus.size() == hotelCodes.size()) {
      log.debug("All the hotel codes are present in DB, so return them - {} ", hotelCodes);
      return hotelMigrationStatusEntities;
    }
    return hotelMigrationStatusEntities;
  }

  public Optional<HotelMigrationStatusEntity> findHotelMigrationEntityByHotelCode(
      final String hotelCode) {
    return hotelMigStatusReaderJpaRepository.findById(hotelCode);
  }

}
