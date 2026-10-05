package uk.co.whitbread.availabilitycacheservice.domain.logic;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;
import uk.co.whitbread.availabilitycacheservice.domain.ports.primary.DataValidationPort;
import uk.co.whitbread.availabilitycacheservice.domain.ports.secondary.DataValidationPersistenceAdapter;
import uk.co.whitbread.availabilitycacheservice.infrastructure.model.hotelavailability.HotelAvailabilitiesResultSet;
import uk.co.whitbread.availabilitycacheservice.infrastructure.rest.request.DataValidationSearchRequest;

@Slf4j
@AllArgsConstructor
@Service
@ConditionalOnProperty(value = "data.validation.enabled", havingValue = "true")
public class DataValidationService implements DataValidationPort {

  private static final String DFLT_ROOM_TYPE = "DOUBLE";
  private static final String DFLT_RATE_CODE = "FLEXRATE";
  private final DataValidationPersistenceAdapter dataValidationPersistenceSvc;

  @Override
  public List<HotelAvailabilitiesResultSet> getHotelAvailabilities(final DataValidationSearchRequest searchCriteria) {
    if (searchCriteria == null || searchCriteria.getHotelCodes().size() == 0 || StringUtils.isEmpty(
        searchCriteria.getAvailabilityFromDate()) || StringUtils.isEmpty(searchCriteria.getAvailabilityUntilDate())) {
      log.info("Found null values for mandatory input fields, so returning emptyList");
      return Collections.emptyList();
    }
    if (searchCriteria.getHotelCodes().size() > 100) {
      log.info("Cannot have more than 100 hotel-codes in the input");
      return Collections.emptyList();
    }
    if (searchCriteria.getRoomTypes() == null) {
      searchCriteria.setRoomTypes(new HashSet<>(Arrays.asList(DFLT_ROOM_TYPE)));
    }
    if (searchCriteria.getRateCodes() == null) {
      searchCriteria.setRateCodes(new HashSet<>(Arrays.asList(DFLT_RATE_CODE)));
    }
    return dataValidationPersistenceSvc
        .getHotelAvailabilitiesForDataValidation(searchCriteria);
  }
}
