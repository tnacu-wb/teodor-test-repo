package uk.co.whitbread.availabilitycacheservice.domain.logic;

import static uk.co.whitbread.availabilitycacheservice.domain.utils.SanitizingUtils.sanitize;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import uk.co.whitbread.availabilitycacheservice.domain.model.availabilities.Hotel;
import uk.co.whitbread.availabilitycacheservice.domain.ports.primary.OperaHotelAvailabilitiesPort;
import uk.co.whitbread.availabilitycacheservice.domain.ports.secondary.OperaHotelPersistenceAdapter;
import uk.co.whitbread.availabilitycacheservice.infrastructure.rest.request.OperaHotelsSearchCriteria;

@Slf4j
@AllArgsConstructor
public class OperaHotelAvailabilitiesService implements OperaHotelAvailabilitiesPort {

  private final OperaHotelPersistenceAdapter operaHotelPersistenceAdapter;

  @Override
  public List<Hotel> getOperaHotelAvailabilities(
      final OperaHotelsSearchCriteria operaHotelsSearchCriteria) {
    try {
      log.trace("Availabilities requested for {} hotels with {}",
          operaHotelsSearchCriteria.getHotelCodes().size(),
          operaHotelsSearchCriteria);

      List<Hotel> availableHotels = operaHotelPersistenceAdapter
          .getHotelAvailabilitiesForOpera(operaHotelsSearchCriteria);
      log.debug("Hotel availabilities for {} hotels, returned from DB.", availableHotels.size());

      if (availableHotels.isEmpty()) {
        log.debug(
            "Zero hotels returned from Db, so return empty list for operaHotelsSearchCriteria - {} ",
            operaHotelsSearchCriteria);
        return Collections.emptyList();
      }

      //Populate Hotel Domain with Arrival Date
      availableHotels = populateHotelDataWithArrivalToday(availableHotels,
          operaHotelsSearchCriteria);

      log.trace("Successfully processed {} available hotels", availableHotels.size());
      return availableHotels;
    } catch (Exception ex) {
      log.error("Error getting hotel availabilities with arrival date {}",
          sanitize(operaHotelsSearchCriteria.getArrival()));
      throw ex;
    }
  }

  protected List<Hotel> populateHotelDataWithArrivalToday(final List<Hotel> hotels,
      final OperaHotelsSearchCriteria searchCriteria) {
    if (!hotels.isEmpty()) {
      boolean arrivalDateToday = LocalDate.now()
          .isEqual(LocalDate.parse(searchCriteria.getArrival()));
      log.trace("arrivalDateToday:: {}", arrivalDateToday);
      return hotels.stream().map(hotel -> {
        hotel.setArrivalDateToday(arrivalDateToday);
        return hotel;
      }).collect(Collectors.toList());
    }
    log.trace("hotels List is empty and returning empty List back..!!");
    return Collections.emptyList();
  }

}
