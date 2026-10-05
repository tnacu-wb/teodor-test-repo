package uk.co.whitbread.availabilitycacheservice.domain.logic;

import static uk.co.whitbread.availabilitycacheservice.domain.utils.SanitizingUtils.sanitize;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import uk.co.whitbread.availabilitycacheservice.domain.model.availabilities.Hotel;
import uk.co.whitbread.availabilitycacheservice.domain.ports.primary.HotelAvailabilitiesPort;
import uk.co.whitbread.availabilitycacheservice.domain.ports.primary.HotelDataProcessPort;
import uk.co.whitbread.availabilitycacheservice.domain.ports.primary.PaginationAndSortingByPricePort;
import uk.co.whitbread.availabilitycacheservice.domain.ports.secondary.HotelAvailabilitiesPersistencePort;
import uk.co.whitbread.availabilitycacheservice.infrastructure.adapters.ContentClientLookUpService;
import uk.co.whitbread.availabilitycacheservice.infrastructure.rest.request.SearchCriteria;
import uk.co.whitbread.availabilitycacheservice.infrastructure.rest.request.enums.SortType;
import uk.co.whitbread.availabilitycacheservice.infrastructure.util.CityTaxFeatureUtil;

@Slf4j
@AllArgsConstructor
public class HotelAvailabilitiesService implements HotelAvailabilitiesPort {

  private final HotelAvailabilitiesPersistencePort hotelAvailabilitiesPersistencePort;
  private final HotelDataProcessPort hotelDataProcessPort;
  private final PaginationAndSortingByPricePort paginationAndSortingByPrice;
  private final ContentClientLookUpService contentClientLookUpService;
  private final CityTaxFeatureUtil cityTaxFeatureUtil;

  @Override
  public List<Hotel> getHotelAvailabilities(final SearchCriteria searchCriteria) {
    try {
      log.trace("Availabilities requested for {} hotels with {}",
          searchCriteria.getHotelCodes().size(), sanitize(searchCriteria.toString()));

      List<Hotel> availableHotels = getAvailableHotels(searchCriteria);
      log.debug("Hotel availabilities for {} hotels, returned from DB.", availableHotels.size());

      hotelDataProcessPort.processHotelDtoData(searchCriteria, availableHotels);

      //Populate Hotel Domain with fields related to SearchCriteria
      availableHotels = populateHotelDataWithSearchCriteria(availableHotels, searchCriteria);

      if (searchCriteria.getSort() == SortType.PRICE) {
        availableHotels = sortAndPaginateAvailableHotels(availableHotels, searchCriteria);
      }
      log.trace("Successfully processed {} available hotels", availableHotels.size());
      return availableHotels;
    } catch (Exception ex) {
      log.error("Error getting hotel availabilities with arrival date {}",
          sanitize(searchCriteria.getArrival()));
      throw ex;
    }
  }

  private List<Hotel> getAvailableHotels(final SearchCriteria searchCriteria) {
    if (cityTaxFeatureUtil.isFeatureEnabled()) {
      String country = searchCriteria.getCountry();
      String language = searchCriteria.getLanguage();
      List<String> hotelCodes = searchCriteria.getHotelCodes();
      searchCriteria.setHotelsCityTaxInfo(contentClientLookUpService.getHotelsCityTaxInfo(
          country, language, hotelCodes));
    }

    return hotelAvailabilitiesPersistencePort.getHotelsByCodeAndAvailDateBetween(searchCriteria);
  }

  protected List<Hotel> populateHotelDataWithSearchCriteria(final List<Hotel> hotels,
      final SearchCriteria searchCriteria) {
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

  private List<Hotel> sortAndPaginateAvailableHotels(List<Hotel> availableHotels,
      SearchCriteria searchCriteria) {
    log.debug("SortType in request is {}, executing sort by price", searchCriteria.getSort());
    final List<Hotel> sortedHotels = paginationAndSortingByPrice.sortHotelsByPrice(availableHotels);
    return paginationAndSortingByPrice.paginateHotels(searchCriteria, sortedHotels);
  }

}
