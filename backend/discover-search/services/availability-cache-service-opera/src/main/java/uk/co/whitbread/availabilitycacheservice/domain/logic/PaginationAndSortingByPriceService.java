package uk.co.whitbread.availabilitycacheservice.domain.logic;

import static java.util.stream.Collectors.toList;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import uk.co.whitbread.availabilitycacheservice.domain.model.Price;
import uk.co.whitbread.availabilitycacheservice.domain.model.availabilities.Hotel;
import uk.co.whitbread.availabilitycacheservice.domain.model.availabilities.RatePlan;
import uk.co.whitbread.availabilitycacheservice.domain.ports.primary.PaginationAndSortingByPricePort;
import uk.co.whitbread.availabilitycacheservice.infrastructure.rest.request.SearchCriteria;

@Slf4j
public class PaginationAndSortingByPriceService implements PaginationAndSortingByPricePort {

  @Value("${hotel.search.default.page:1}")
  private int defaultPage;

  @Value("${hotel.search.default.size:40}")
  private int defaultPageSize;

  @Override
  public List<Hotel> sortHotelsByPrice(final List<Hotel> hotels) {
    log.debug("Sorting hotels by PRICE");
    removeRatesWithoutPrice(hotels);
    return getLeastPriceForHotel(hotels).entrySet().stream()
        .sorted(Map.Entry.comparingByValue(Comparator.nullsLast(Comparator.naturalOrder())))
        .map(Map.Entry::getKey)
        .collect(Collectors.toList());
  }

  @Override
  public List<Hotel> paginateHotels(final SearchCriteria searchCriteria, final List<Hotel> hotels) {
    if (searchCriteria.getPage() <= 0) {
      searchCriteria.setPage(defaultPage);
    }

    if (searchCriteria.getSize() <= 0) {
      searchCriteria.setSize(defaultPageSize);
    }
    log.debug("Paginating hotels with page: {} size: {}", searchCriteria.getPage(), searchCriteria.getSize());
    return hotels.stream()
        .skip((long) (searchCriteria.getPage() - 1) * searchCriteria.getSize())
        .limit(searchCriteria.getSize())
        .collect(toList());
  }

  private void removeRatesWithoutPrice(final List<Hotel> hotels) {
    hotels.forEach(hotel -> {
      var ratePlans = hotel.getRates();
      var ratePlansWithoutPrice = new ArrayList<>();
      for (RatePlan ratePlan : ratePlans) {
        if (ratePlan.getTotalPrice() != null && ratePlan.getTotalPrice().getAmount() == null) {
          ratePlansWithoutPrice.add(ratePlan);
        }
      }
      log.debug("Removing {} number of ratePlans with amount=null for hotelCode: {}", ratePlansWithoutPrice.size(),
          hotel.getHotelCode());
      ratePlans.removeAll(ratePlansWithoutPrice);
    });
  }

  private Map<Hotel, BigDecimal> getLeastPriceForHotel(final List<Hotel> hotels) {
    final Map<Hotel, BigDecimal> leastPriceForHotelMap = new HashMap<>();
    hotels.forEach(hotel -> {
      Optional<Price> minTotalCost = hotel.getRates().stream()
          .map(RatePlan::getTotalPrice)
          .filter(price -> Objects.nonNull(price.getAmount()))
          .min(Comparator.comparing(Price::getAmount));

      minTotalCost.ifPresentOrElse(totalCost -> leastPriceForHotelMap.put(hotel, totalCost.getAmount()),
          () -> leastPriceForHotelMap.put(hotel, null));
    });
    leastPriceForHotelMap.forEach(((k, v) ->
        log.debug("Minimum rate for hotelCode {} : amount {}", k.getHotelCode(), v)));
    return leastPriceForHotelMap;
  }

}
