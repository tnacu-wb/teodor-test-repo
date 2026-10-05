package uk.co.whitbread.dashboard.domain.logic.chain;

import static uk.co.whitbread.dashboard.domain.logic.utils.DashboardUtils.BOOKING_CHANNEL_CBT;
import static uk.co.whitbread.dashboard.domain.logic.utils.DashboardUtils.BOOKING_CHANNEL_MOBILE;
import static uk.co.whitbread.dashboard.domain.logic.utils.DashboardUtils.getHotelImage;

import java.time.LocalDate;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import uk.co.whitbread.dashboard.domain.external.hotelinfo.model.in.HotelInfo;
import uk.co.whitbread.dashboard.domain.model.RetrieveDashboardChainRequest;
import uk.co.whitbread.dashboard.domain.model.out.Content;
import uk.co.whitbread.dashboard.domain.model.out.DashboardElement;
import uk.co.whitbread.dashboard.domain.model.out.DashboardType;
import uk.co.whitbread.dashboard.domain.model.out.FrequentBooking;
import uk.co.whitbread.dashboard.domain.ports.secondary.HotelAccountOutPort;
import uk.co.whitbread.dashboard.domain.ports.secondary.HotelInfoOutPort;
import uk.co.whitbread.dashboard.domain.properties.DashboardProperties;
import uk.co.whitbread.hotel.account.generated.hotelaccount.model.BBStaysRequestV2;
import uk.co.whitbread.hotel.account.generated.hotelaccount.model.BBStaysRequestV2.SortOrderEnum;
import uk.co.whitbread.hotel.account.generated.hotelaccount.model.BBStaysRequestV2.TypeOfBookingEnum;
import uk.co.whitbread.hotel.account.generated.hotelaccount.model.Stay;
import uk.co.whitbread.hotel.account.generated.hotelaccount.model.StaysResponse;

@Order(3)
@RequiredArgsConstructor
@Component
@Slf4j
public class FrequentBookingChain extends DashboardChain {

  private static final String BOOKING_CHANNEL = "MOBILE";
  private static final int PAGE_INDEX = 1;
  private static final int PAGE_SIZE = Integer.MAX_VALUE;
  private static final String TYPE_OF_BOOKING = "PAST";
  private static final String SORT_ORDER = "DEFAULT";
  private final DashboardProperties properties;
  private final HotelAccountOutPort hotelAccountOutPort;
  private final HotelInfoOutPort hotelInfoOutPort;

  /**
   * For a hotel to be considered frequent, it needs to meet the following criteria: Number of past stays equals or
   * greater properties.getMinToBeFrequent() = .filter(stays -> stays.size() >= properties.getMinToBeFrequent())
   *
   * <p>The maximum of frequent bookings is properties.getMaxFrequent() =
   * .limit(properties.getMaxFrequent())
   *
   * <p>The frequent bookings are sorted by the following criteria Number of past stays =
   * .sorted((stays1, stays2) -> Long.compare(stays2.size(), stays1.size())) Latest arrival date  =
   * .sorted(this::compareLatestArrivalDate)
   *
   * @param request  request.token used to authenticate with hotel-account
   * @param response DashboardElement with list of frequent bookings. If there is no frequent booking or if token is
   *                 invalid it will return empty list
   */
  @Override
  protected void get(RetrieveDashboardChainRequest request, DashboardElement response) {

    if (StringUtils.isBlank(request.getToken())) {
      return;
    }

    log.debug("Fetching frequent bookings {}", request.getConfirmationNumber());
    String bookingChannel = (request.isBusiness()) ? BOOKING_CHANNEL_CBT : BOOKING_CHANNEL_MOBILE;
    BBStaysRequestV2 staysRequest = new BBStaysRequestV2().pageIndex(PAGE_INDEX)
        .pageSize(PAGE_SIZE)
        .typeOfBooking(TypeOfBookingEnum.PAST)
        .sortOrder(SortOrderEnum.DEFAULT)
        .business(request.isBusiness())
        .companyId(request.getCompanyId())
        .employeeId(request.getEmployeeId());

    StaysResponse staysResponse = hotelAccountOutPort.getAccountStays(staysRequest,
        request.getCustomerId(),
        request.getToken(),
        bookingChannel,
        request.getOrigin()
    );

    if (Objects.isNull(staysResponse)) {
      return;
    }

    java.util.Map<String, List<Stay>> groupByHotelCode = staysResponse
        .getStays()
        .stream()
        .collect(Collectors.groupingBy(Stay::getHotelCode));

    List<FrequentBooking> frequentBookings = groupByHotelCode.values()
        .stream()
        .filter(stays -> stays.size() >= properties.getMinToBeFrequent())
        .sorted((stays1, stays2) -> Long.compare(stays2.size(), stays1.size()))
        .sorted(this::compareLatestArrivalDate)
        .limit(properties.getMaxFrequent())
        .map(this::populateContentFrequentBookings)
        .toList();

    if (CollectionUtils.isEmpty(frequentBookings)) {
      return;
    }

    response.setContent(Content.builder().frequentBookings(frequentBookings).build());
    response.setType(DashboardType.FREQUENT_BOOKINGS);
  }

  private int compareLatestArrivalDate(List<Stay> stays1, List<Stay> stays2) {
    LocalDate latestDateFromStays1 = getLatestDateFromList(stays1);

    LocalDate latestDateFromStays2 = getLatestDateFromList(stays2);

    return latestDateFromStays2.compareTo(latestDateFromStays1);
  }

  private LocalDate getLatestDateFromList(List<Stay> stays) {
    return stays
        .stream()
        .map(Stay::getArrivalDate)
        .max(LocalDate::compareTo)
        .orElseThrow();
  }

  private FrequentBooking populateFrequentBooking(Stay stay) {
    HotelInfo infoResponse = hotelInfoOutPort.getHotelInfo(stay.getHotelCode());
    return FrequentBooking.builder()
        .hotelCode(stay.getHotelCode())
        .hotelName(infoResponse.getName())
        .hotelBrand(infoResponse.getBrand())
        .hotelImage(getHotelImage(infoResponse, properties.getEnvironment()))
        .build();
  }

  private FrequentBooking populateContentFrequentBookings(List<Stay> stays) {
    return stays.stream()
        .findAny()
        .map(this::populateFrequentBooking)
        .orElse(null);
  }
}
