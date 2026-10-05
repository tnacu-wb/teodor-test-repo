package uk.co.whitbread.hotel.account.utils;

import static java.util.Optional.ofNullable;
import static uk.co.whitbread.hotel.account.model.BookingStatus.CANCELLED;
import static uk.co.whitbread.hotel.account.model.BookingStatus.FUTURE;
import static uk.co.whitbread.hotel.account.model.BookingStatus.PAST;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import lombok.experimental.UtilityClass;
import uk.co.whitbread.cdh.adapter.service.generated.models.reservationSearch.CdhReservationSearchCriteriaDto;
import uk.co.whitbread.hotel.account.model.BBStaysRequest;
import uk.co.whitbread.hotel.account.model.BaseStay;
import uk.co.whitbread.hotel.account.model.BookingStatus;
import uk.co.whitbread.hotel.account.model.Stay;
import uk.co.whitbread.hotel.account.model.StaysResponse;
import uk.co.whitbread.hotel.account.model.StaysTypesTotals;
import uk.co.whitbread.shared.auth.account.CdhEmployeeDetails;
import uk.co.whitbread.shared.cdh.model.Booking;
import uk.co.whitbread.shared.cdh.model.GetAccountBookingsQueryParamsV2;
import uk.co.whitbread.shared.cdh.model.GetCustomerAccountBookingsQueryParams;

@UtilityClass
public class BookingUtils {

  public static void filterStaysByBookingStatus(StaysResponse response,
      BookingStatus typeOfBooking) {
    var filteredStays = response.getStays().stream()
        .filter(stay -> typeOfBooking.equals(stay.getBookingStatus()))
        .collect(Collectors.toList());
    response.setStays(filteredStays);
    response.setPageSize(filteredStays.size());
    response.setTotalSize(filteredStays.size());
  }

  public static StaysResponse buildEmptyStaysResponse() {
    return StaysResponse.builder()
        .stays(Collections.emptyList())
        .totals(StaysTypesTotals.builder().build())
        .build();
  }

  public static GetCustomerAccountBookingsQueryParams buildPiCdhBookingsQueryParams(
      BBStaysRequest staysRequest, String customerAccountId) {
    var queryParams = GetCustomerAccountBookingsQueryParams.builder()
        .customerAccountId(customerAccountId)
        .bookingsDatabaseSearch("true")
        .build();
    if (Objects.isNull(staysRequest.getFilterType())) {
      return queryParams;
    }

    return addFilters(staysRequest, queryParams);
  }

  public static GetCustomerAccountBookingsQueryParams buildBbCdhBookingsQueryParams(
      BBStaysRequest staysRequest, CdhEmployeeDetails cdhEmployeeDetails) {
    var queryParams = GetCustomerAccountBookingsQueryParams.builder()
        .employeeAccountId(cdhEmployeeDetails.getEmployeeAccountId())
        .companyAccountId(cdhEmployeeDetails.getCompanyAccountId())
        .bookingsDatabaseSearch("true")
        .build();

    return addFilters(staysRequest, queryParams);
  }

  private GetCustomerAccountBookingsQueryParams addFilters(BBStaysRequest staysRequest,
      GetCustomerAccountBookingsQueryParams queryParams) {
    if (Objects.isNull(staysRequest.getFilterType())) {
      return queryParams;
    }
    switch (staysRequest.getFilterType()) {
      case CONFIRM_NUMBER:
        queryParams.setBookingReference(staysRequest.getFilterValue());
        break;
      case NAME:
        queryParams.setLastName(staysRequest.getFilterValue());
        break;
      case ARRIVAL_DATE:
        queryParams.setArrivalDateFrom(staysRequest.getFilterValue());
        String arrivalDateTo = LocalDate.parse(staysRequest.getFilterValue()).plusDays(1)
            .toString();
        queryParams.setArrivalDateTo(arrivalDateTo);
        break;
    }
    return queryParams;
  }

  public <T> T addFiltersV2(BBStaysRequest staysRequest, T queryParams) {
    if (Objects.isNull(staysRequest.getFilterType())) {
      return queryParams;
    }
    switch (staysRequest.getFilterType()) {
      case CONFIRM_NUMBER:
        if (queryParams instanceof CdhReservationSearchCriteriaDto cdhReservationSearchCriteriaDto) {
          cdhReservationSearchCriteriaDto.setBookingReference(staysRequest.getFilterValue());
        } else if (queryParams instanceof GetAccountBookingsQueryParamsV2 getAccountBookingsQueryParamsV2) {
          getAccountBookingsQueryParamsV2.setBookingReference(staysRequest.getFilterValue());
        }
        break;
      case NAME:
        if (queryParams instanceof CdhReservationSearchCriteriaDto cdhReservationSearchCriteriaDto) {
          cdhReservationSearchCriteriaDto.setLastName(staysRequest.getFilterValue());
        } else if (queryParams instanceof GetAccountBookingsQueryParamsV2 getAccountBookingsQueryParamsV2) {
          getAccountBookingsQueryParamsV2.setLastName(staysRequest.getFilterValue());
        }
        break;
      case ARRIVAL_DATE:
        String arrivalDateTo = LocalDate.parse(staysRequest.getFilterValue()).plusDays(1).toString();
        if (queryParams instanceof CdhReservationSearchCriteriaDto cdhReservationSearchCriteriaDto) {
          cdhReservationSearchCriteriaDto.setArrivalDateFrom(staysRequest.getFilterValue());
          cdhReservationSearchCriteriaDto.setArrivalDateTo(arrivalDateTo);
        } else if (queryParams instanceof GetAccountBookingsQueryParamsV2 getAccountBookingsQueryParamsV2) {
          getAccountBookingsQueryParamsV2.setArrivalDateFrom(staysRequest.getFilterValue());
          getAccountBookingsQueryParamsV2.setArrivalDateTo(arrivalDateTo);
        }
        break;
    }
    return queryParams;
  }

  public static StaysTypesTotals breakDownStaysTypesTotals(List<Stay> stays) {
    return StaysTypesTotals.builder()
        .cancelled(getStaysSizeByType(stays, CANCELLED))
        .past(getStaysSizeByType(stays, PAST))
        .upcoming(getStaysSizeByType(stays, FUTURE))
        .checkedIn((int) ofNullable(stays)
            .orElse(Collections.emptyList())
            .stream()
            .filter(BaseStay::isCheckedIn).count())
        .build();
  }

  public static int getStaysSizeByType(List<Stay> stays, BookingStatus bookingStatus) {
    if (stays == null) {
      return 0;
    }
    return (int) stays.stream().filter(stay -> {
      BookingStatus status = stay.getBookingStatus();
      return status != null && status.equals(bookingStatus);
    }).count();
  }

  public static boolean isPastBooking(Booking booking) {
    LocalDateTime departureDatetime = OffsetDateTime.parse(booking.getDepartureDate())
        .toLocalDateTime();

    return departureDatetime.isBefore(LocalDateTime.now());
  }

  public static boolean isPastBooking(String departureDate) {
    LocalDateTime departureDatetime = OffsetDateTime.parse(departureDate)
        .toLocalDateTime();
    return departureDatetime.isBefore(LocalDateTime.now());
  }
}
