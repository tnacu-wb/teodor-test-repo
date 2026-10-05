package uk.co.whitbread.hotel.account.service.cdh;

import static uk.co.whitbread.hotel.account.utils.BookingUtils.buildEmptyStaysResponse;
import static uk.co.whitbread.hotel.account.utils.BookingUtils.filterStaysByBookingStatus;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import uk.co.whitbread.cdh.adapter.service.generated.models.reservationSearch.CdhReservationSearchCriteriaDto;
import uk.co.whitbread.cdh.adapter.service.generated.models.reservationSearch.CdhReservationSearchDto;
import uk.co.whitbread.cdh.adapter.service.generated.models.reservationSearch.PriceDto;
import uk.co.whitbread.hotel.account.mapper.CdhBookingHistoryResponseMapper;
import uk.co.whitbread.hotel.account.model.BBStaysRequest;
import uk.co.whitbread.hotel.account.model.BookingStatus;
import uk.co.whitbread.hotel.account.model.Stay;
import uk.co.whitbread.hotel.account.model.StaysResponse;
import uk.co.whitbread.hotel.account.model.feature.FeatureFlag;
import uk.co.whitbread.hotel.account.model.feature.UnleashWrapper;
import uk.co.whitbread.hotel.account.properties.BookingHistoryProperties;

@Slf4j
@RequiredArgsConstructor
@Service
public abstract class CdhBookingService {

  private final CdhService cdhService;
  private final CdhBookingHistoryResponseMapper bookingHistoryResponseMapper;
  private final UnleashWrapper<FeatureFlag> unleashWrapper;
  private final BookingHistoryProperties bookingHistoryProperties;

  public StaysResponse getBookingsV2(CdhReservationSearchCriteriaDto queryParams,
                                     BBStaysRequest staysRequest, String email) {
    applyDateRanges(queryParams);
    var bookingsResponse = cdhService.getBookingHistoryV2(queryParams);
    if (bookingsResponse == null || bookingsResponse.getResults() == null || bookingsResponse.getResults().isEmpty()) {
      return buildEmptyStaysResponse();
    }
    updateTotalCost(bookingsResponse);
    StaysResponse response = bookingHistoryResponseMapper.toCdhBookingHistoryResponse(bookingsResponse);
    Integer pageIndex = queryParams.getPageNumber();
    if (pageIndex != null) {
      response.setPageIndex(pageIndex);
    }
    if (Objects.nonNull(staysRequest.getTypeOfBooking())) {
      filterStaysByBookingStatus(response, staysRequest.getTypeOfBooking());
    }
    if (unleashWrapper.isEnabled(unleashWrapper.featureFlag().getBookingHistoryPageOrdering())) {
      response.setStays(getSortedAndFilteredStays(response.getStays()));
    }
    return response;
  }

  private void updateTotalCost(CdhReservationSearchDto bookingsResponse) {
    bookingsResponse.getResults().forEach(stay -> {
      if (Objects.nonNull(stay.getActiveTotalCost())) {
        stay.setTotalCost(stay.getActiveTotalCost());
      } else {
        PriceDto price = new PriceDto();
        price.setAmount(new BigDecimal("0.0"));
        price.setCurrency(stay.getTotalCost().getCurrency());
        stay.setTotalCost(price);
      }
    });
  }
    
    private List<Stay> getSortedAndFilteredStays(List<Stay> stays) {
      
      //define sorting logic
      Map<BookingStatus, Comparator<Stay>> statusComparator = Map.of(
          BookingStatus.CHECKED_IN, Comparator.comparing(Stay::getArrivalDate),
          BookingStatus.FUTURE, Comparator.comparing(Stay::getArrivalDate),
          BookingStatus.PAST, Comparator.comparing(Stay::getDepartureDate).reversed(),
          BookingStatus.CANCELLED, Comparator.comparing(Stay::getArrivalDate).reversed()
      );
      
      //define desired order of statuses
      List<BookingStatus> statusOrder = List.of(BookingStatus.CHECKED_IN, BookingStatus.FUTURE, BookingStatus.PAST,
          BookingStatus.CANCELLED);
      
      List<Stay> sortedResults = new ArrayList<>();
      for (BookingStatus status : statusOrder) {
        Comparator<Stay> comparator = statusComparator.get(status);
        List<Stay> filtered = stays.stream()
            .filter(r -> status.equals(r.getBookingStatus()))
            .filter(r -> isDateValidForComparison(r, status))
            .sorted(comparator)
            .collect(Collectors.toList());
        sortedResults.addAll(filtered);
      }
      return sortedResults;
    }
  
  private void applyDateRanges(CdhReservationSearchCriteriaDto queryParams) {
    queryParams.setCancelledDays(bookingHistoryProperties.getCancelledDays());
    queryParams.setPastDays(bookingHistoryProperties.getPastDays());
    queryParams.setUpcomingDays(bookingHistoryProperties.getUpcomingDays());
  }

  private boolean isDateValidForComparison(Stay stay, BookingStatus status) {
    return switch (status) {
      case CHECKED_IN, FUTURE, CANCELLED -> stay.getArrivalDate() != null;
      case PAST -> stay.getDepartureDate() != null;
    };
  }
  
}
