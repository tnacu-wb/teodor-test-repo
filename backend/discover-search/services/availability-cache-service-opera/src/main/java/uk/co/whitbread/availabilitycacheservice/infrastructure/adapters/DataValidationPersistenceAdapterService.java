package uk.co.whitbread.availabilitycacheservice.infrastructure.adapters;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;
import uk.co.whitbread.availabilitycacheservice.domain.ports.secondary.DataValidationPersistenceAdapter;
import uk.co.whitbread.availabilitycacheservice.infrastructure.model.hotelavailability.HotelAvailabilitiesResultSet;
import uk.co.whitbread.availabilitycacheservice.infrastructure.repository.read.HotelAvailabilitiesJpaRepository;
import uk.co.whitbread.availabilitycacheservice.infrastructure.rest.request.DataValidationSearchRequest;

@Slf4j
@AllArgsConstructor
@Service
@ConditionalOnProperty(value = "data.validation.enabled", havingValue = "true")
public class DataValidationPersistenceAdapterService implements DataValidationPersistenceAdapter {

  private final HotelAvailabilitiesJpaRepository hotelAvailabilitiesJpaRepository;

  @Override
  public List<HotelAvailabilitiesResultSet> getHotelAvailabilitiesForDataValidation(
      final DataValidationSearchRequest searchCriteria) {
    final LocalDate availabilityFromDate = LocalDate.parse(searchCriteria.getAvailabilityFromDate());
    final LocalDate availabilityUntilDate = LocalDate.parse(searchCriteria.getAvailabilityUntilDate());
    final List<HotelAvailabilitiesResultSet> hotelAvailabilitiesFrmDB =
        hotelAvailabilitiesJpaRepository.findDataValidationHotelAvailabilities(searchCriteria.getHotelCodes(),
            availabilityFromDate, availabilityUntilDate, searchCriteria.getRoomTypes(), searchCriteria.getRateCodes());
    if (hotelAvailabilitiesFrmDB.isEmpty()) {
      log.info("No results returned from DB, so returning empty list");
      return Collections.emptyList();
    }
    return hotelAvailabilitiesFrmDB;
  }

}
