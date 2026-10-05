package uk.co.whitbread.availabilitycacheservice.infrastructure.mapper.pricefinder;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;
import org.mapstruct.Mapper;
import uk.co.whitbread.availabilitycacheservice.domain.model.pricefinder.CalendarPriceFinderHotelAvailabilities;
import uk.co.whitbread.availabilitycacheservice.domain.model.pricefinder.LowestRate;
import uk.co.whitbread.availabilitycacheservice.domain.model.pricefinder.PriceFinderHotelAvailabilities;
import uk.co.whitbread.availabilitycacheservice.domain.model.pricefinder.PriceFinderOperaHotelAvailabilities;
import uk.co.whitbread.availabilitycacheservice.infrastructure.rest.response.pricefinder.model.out.CalendarPriceFinderHotelAvailabilitiesDto;
import uk.co.whitbread.availabilitycacheservice.infrastructure.rest.response.pricefinder.model.out.LowestRatesDto;
import uk.co.whitbread.availabilitycacheservice.infrastructure.rest.response.pricefinder.model.out.PriceFinderHotelAvailabilitiesDto;
import uk.co.whitbread.availabilitycacheservice.infrastructure.rest.response.pricefinder.model.out.PriceFinderOperaHotelAvailabilitiesDto;

@Mapper(componentModel = "spring")
public interface PriceFinderHotelAvailabilitiesMapper {

  List<PriceFinderOperaHotelAvailabilitiesDto> toPriceFinderOperaHotelAvailabilitiesDtoList(
      List<PriceFinderOperaHotelAvailabilities> priceFinderOperaHotelAvailabilities);

  PriceFinderHotelAvailabilitiesDto toPriceFinderAvailabilitiesResponseDto(
      PriceFinderHotelAvailabilities priceFinderOperaHotelAvailabilities);

  CalendarPriceFinderHotelAvailabilitiesDto toCalendarPriceFinderHotelAvailabilitiesDto(
      CalendarPriceFinderHotelAvailabilities calendarPriceFinderHotelAvailabilities);

  default SortedSet<LowestRatesDto> lowestRateSortedSetToLowestRatesDtoSortedSet(
      SortedSet<LowestRate> sortedSet) {
    if (sortedSet == null) {
      return Collections.emptySortedSet();
    }

    SortedSet<LowestRatesDto> lowestRatesDtos = new TreeSet<>(
        Comparator.comparing(LowestRatesDto::getAvailableDate));
    for (LowestRate lowestRate : sortedSet) {
      lowestRatesDtos.add(lowestRateToLowestRatesDto(lowestRate));
    }

    return lowestRatesDtos;
  }

  LowestRatesDto lowestRateToLowestRatesDto(LowestRate lowestRate);
}
