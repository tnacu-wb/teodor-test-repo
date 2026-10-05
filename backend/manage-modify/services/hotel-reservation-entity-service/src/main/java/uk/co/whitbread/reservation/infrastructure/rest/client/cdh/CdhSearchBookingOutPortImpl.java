package uk.co.whitbread.reservation.infrastructure.rest.client.cdh;

import static uk.co.whitbread.reservation.domain.model.in.BookStatusEnum.CHECKEDIN;
import static uk.co.whitbread.reservation.domain.model.out.BookingStatus.UPCOMING;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;
import uk.co.whitbread.reservation.domain.model.in.BookStatusEnum;
import uk.co.whitbread.reservation.domain.model.in.CdhSearchBookingsRequest;
import uk.co.whitbread.reservation.domain.model.out.CdhResults;
import uk.co.whitbread.reservation.domain.model.out.CdhSearchBookingsResponse;
import uk.co.whitbread.reservation.domain.ports.secondary.CdhSearchBookingOutPort;
import uk.co.whitbread.reservation.infrastructure.rest.client.cdh.mapper.CdhSearchBookingsRequestMapper;
import uk.co.whitbread.reservation.infrastructure.rest.client.cdh.mapper.CdhSearchBookingsResponseMapper;
import uk.co.whitbread.reservation.infrastructure.rest.client.cdh.service.CdhAdapterClient;

@Slf4j
@RequiredArgsConstructor
@Component
public class CdhSearchBookingOutPortImpl implements CdhSearchBookingOutPort {

  private final CdhAdapterClient cdhAdapterClient;
  private final CdhSearchBookingsRequestMapper requestMapper;
  private final CdhSearchBookingsResponseMapper responseMapper;
  private static final String CHECKEDIN = "Checked-In";
  private static final String UPCOMING = "Upcoming";
  private static final String PAST = "Past";
  private static final String CANCELLED = "Cancelled";

  private static final Integer PAGE_SIZE = 50;

  @Override
  public CdhSearchBookingsResponse searchBookingsFromCdh(CdhSearchBookingsRequest cdhSearchBookingsRequest) {

    var requestDto = requestMapper.toDto(cdhSearchBookingsRequest);

    if (requestDto.getBookingReference().matches("^[A-Za-z]{6}\\d+$")) {
      requestDto.setBookingReference(
              requestDto.getBookingReference().replaceAll("^[A-Za-z]+", ""));
    }

    final int pageNumberFromApp = requestDto.getPageNumber();
    final int pageSizeFromApp = requestDto.getPageSize();
    /*
    This is to retrieve the max possible search results from CDH (ie, 50).
     */
    requestDto.setPageSize(PAGE_SIZE);
    if (requestDto.getPageNumber() != 1) {
      requestDto.setPageNumber(1);
    }

    if (Boolean.TRUE.equals(requestDto.getBookingsDatabaseSearch())
        && StringUtils.isBlank(requestDto.getBookingReference())) {
      requestDto.setBookingReference(null);
    }

    var response = cdhAdapterClient.searchReservation(requestDto);

    var responseModel = responseMapper.toModel(response,
        cdhSearchBookingsRequest.getPageSize(), cdhSearchBookingsRequest.getPageNumber(),
        cdhSearchBookingsRequest.getBookerLastName(), cdhSearchBookingsRequest.getGuestLastName());

    if (responseModel.getResults() != null) {
      responseModel.setSearchResults(responseModel.getResults().size());
      responseModel.setResults(setPaginationForCdhResults(cdhSearchBookingsRequest, responseModel));
      responseModel.setPageResults(responseModel.getResults().size());
    }
    responseModel.setHasMore((pageSizeFromApp * pageNumberFromApp) < responseModel.getSearchResults());

    responseModel.getResults().forEach(booking -> {
      String hotelId = booking.getHotelId();
      if (hotelId != null) {
        booking.setSourceSystem("OPERA");
      }
    });
    // if booking comes from opera ui send back operaConfNumber
    if (cdhSearchBookingsRequest.getBookingReference().matches("\\d+")
            || cdhSearchBookingsRequest.getBookingReference().matches("^[A-Za-z]{6}\\d+$")) {
      responseModel.setOperaConfNumber(cdhSearchBookingsRequest.getBookingReference());
    }

    return responseModel;
  }

  public List<CdhResults> setPaginationForCdhResults(CdhSearchBookingsRequest request,
                                                      CdhSearchBookingsResponse response) {

    response.setResults(sortBookingOnStatus_ArrivalDate(response));

    var skipCount = (request.getPageNumber() - 1) * request.getPageSize();

    return response.getResults().stream()
        .skip(skipCount)
        .limit(request.getPageSize())
        .toList();
  }

  public List<CdhResults> sortBookingOnStatus_ArrivalDate(CdhSearchBookingsResponse response) {

    List<CdhResults> sortedResults = new ArrayList<>();
    for (Integer statusSortOrder : BookStatusEnum.getBookingStatusOrder()) {
      getSortedResults(response, BookStatusEnum.getBookingStatus(statusSortOrder), sortedResults);
    }
    return sortedResults;
  }

  public List<CdhResults> getSortedResults(CdhSearchBookingsResponse response, String status,
                                           List<CdhResults> sortedResults) {
    LocalDate today = LocalDate.now();
    LocalDate oneYearAgo = today.minusDays(365);
    LocalDate oneYearAhead = today.plusDays(365);
    List<CdhResults> filteredCdhSearchResponse;
    switch (status) {
      case CHECKEDIN: {
        filteredCdhSearchResponse =  response.getResults().stream()
            .filter(responseMapper -> status.equals(responseMapper.getStatus()))
            .sorted(Comparator.comparing(CdhResults::getArrivalDate))
            .toList();
        break;
      }
      case UPCOMING: {
        filteredCdhSearchResponse = response.getResults().stream()
            .filter(responseMapper -> status.equals(responseMapper.getStatus()))
            .filter(s -> {
              LocalDate arrival = s.getArrivalDate();
              return !arrival.isBefore(today) && !arrival.isAfter(oneYearAhead);
            })
            .sorted(Comparator.comparing(CdhResults::getArrivalDate))
            .toList();
        break;
      }
      case PAST: {
        filteredCdhSearchResponse = response.getResults().stream()
            .filter(responseMapper -> status.equals(responseMapper.getStatus()))
            .filter(s -> {
              LocalDate departure = s.getDepartureDate();
              return !departure.isBefore(oneYearAgo) && departure.isBefore(today);
            })
            .sorted(Comparator.comparing(CdhResults::getDepartureDate).reversed())
            .toList();
        break;
      }
      case CANCELLED: {
        filteredCdhSearchResponse = response.getResults().stream()
            .filter(responseMapper -> status.equals(responseMapper.getStatus()))
            .filter(b -> b.getCancellationDate() != null && !b.getCancellationDate().isBefore(oneYearAgo))
            .sorted(Comparator.comparing(CdhResults::getArrivalDate).reversed())
            .toList();
        break;
      }
      default:
        filteredCdhSearchResponse =  response.getResults().stream()
            .filter(responseMapper -> status.equals(responseMapper.getStatus()))
            .sorted(Comparator.comparing(CdhResults::getArrivalDate))
            .toList();
        break;
    }
    sortedResults.addAll(filteredCdhSearchResponse);
    return sortedResults;
  }
}
