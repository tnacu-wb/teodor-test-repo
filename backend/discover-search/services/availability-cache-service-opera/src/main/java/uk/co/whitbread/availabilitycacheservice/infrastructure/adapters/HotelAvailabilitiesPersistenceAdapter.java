package uk.co.whitbread.availabilitycacheservice.infrastructure.adapters;

import static uk.co.whitbread.availabilitycacheservice.domain.utils.SanitizingUtils.sanitize;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import uk.co.whitbread.availabilitycacheservice.domain.model.availabilities.Hotel;
import uk.co.whitbread.availabilitycacheservice.domain.ports.secondary.HotelAvailabilitiesPersistencePort;
import uk.co.whitbread.availabilitycacheservice.domain.ports.secondary.HotelAvailabilitiesPersistencePostProcessorPort;
import uk.co.whitbread.availabilitycacheservice.domain.utils.SanitizingUtils;
import uk.co.whitbread.availabilitycacheservice.infrastructure.model.hotelavailability.HotelAvailabilitiesResultSet;
import uk.co.whitbread.availabilitycacheservice.infrastructure.repository.read.HotelJpaRepository;
import uk.co.whitbread.availabilitycacheservice.infrastructure.rest.request.SearchCriteria;


@Slf4j
@AllArgsConstructor
public class HotelAvailabilitiesPersistenceAdapter implements HotelAvailabilitiesPersistencePort {

  private final HotelJpaRepository hotelJpaRepository;
  private final HotelAvailabilitiesPersistencePostProcessorPort hotelAvailabilitiesPostProcessor;


  @Override
  public List<Hotel> getHotelsByCodeAndAvailDateBetween(final SearchCriteria searchCriteria) {
    final LocalDate arrivalDate = LocalDate.parse(searchCriteria.getArrival());
    final LocalDate departureDate = LocalDate.parse(searchCriteria.getDeparture());

    List<String> hotelCodes = searchCriteria.getHotelCodes();

    log.debug(
        "Requesting hotels by code and availability date with arrival {} and departure Dates {} and hotelCodes {}",
        sanitize(arrivalDate), sanitize(departureDate),
        hotelCodes == null ? null :
            hotelCodes.stream()
                .map(SanitizingUtils::sanitize)
                .toList());

    final Set<String> uniqueRoomTypes = Arrays.stream(searchCriteria.getType()).collect(Collectors.toSet());

    log.trace("query execution start time - {}", LocalDateTime.now());

    final List<HotelAvailabilitiesResultSet> hotelAvailabilitiesResultSets = hotelJpaRepository
        .findByHotelCodesAndMultiRoomType(hotelCodes, arrivalDate,
            departureDate, uniqueRoomTypes);

    log.trace("query execution end time - {}", LocalDateTime.now());

    log.debug("availabilities result set count::" + hotelAvailabilitiesResultSets.size());

    return hotelAvailabilitiesPostProcessor
        .performPostProcessHotelAvailabilities(searchCriteria, hotelAvailabilitiesResultSets);
  }

}
