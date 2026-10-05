package uk.co.whitbread.ohip.infrastructure.rest.client.reservation.utils.search;

import static java.util.Collections.singletonList;
import static uk.co.whitbread.ohip.infrastructure.rest.client.ohip.config.OhipConstants.ARRIVAL_DATE_PARAM;
import static uk.co.whitbread.ohip.infrastructure.rest.client.ohip.config.OhipConstants.BOOKER_COMMUNICATION_PARAM;
import static uk.co.whitbread.ohip.infrastructure.rest.client.ohip.config.OhipConstants.BOOKER_POSTCODE_PARAM;
import static uk.co.whitbread.ohip.infrastructure.rest.client.ohip.config.OhipConstants.BOOKER_PROFILE_NAME_PARAM;
import static uk.co.whitbread.ohip.infrastructure.rest.client.ohip.config.OhipConstants.BOOKER_PROFILE_TYPE_PARAM;
import static uk.co.whitbread.ohip.infrastructure.rest.client.ohip.config.OhipConstants.CANCELLATION_DATE_PARAM;
import static uk.co.whitbread.ohip.infrastructure.rest.client.ohip.config.OhipConstants.CONTACT_PROFILE_TYPE;
import static uk.co.whitbread.ohip.infrastructure.rest.client.ohip.config.OhipConstants.EXTERNAL_REFERENCE_IDS_PARAM;
import static uk.co.whitbread.ohip.infrastructure.rest.client.ohip.config.OhipConstants.HOTEL_ID_PARAM;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.commons.lang3.StringUtils;
import uk.co.whitbread.ohip.domain.model.reservation.in.BookingSearchCriteria;

public class BookingSearchInputBuilder {

  private BookingSearchInputBuilder() {
  }

  public static SearchBookingsInput buildSearchInput(BookingSearchCriteria bookingSearchCriteria) {

    // booking unique identifier (reference) has top priority
    if (StringUtils.isNotBlank(bookingSearchCriteria.getBookingReference()) || StringUtils.isNotBlank(
        bookingSearchCriteria.getThirdPartyBookingReferenceNumber())) {
      return SearchBookingsInput.builder().searchBookingsType(SearchBookingsType.RESERVATION_SEARCH)
          .searchFields(buildReservationSearchFields(bookingSearchCriteria))
          .filterChain(
              new SearchBookingsResultsFilterChain((buildReservationSearchResultsFilters(bookingSearchCriteria))))
          .build();
    }
    // searching by booker related excepting surname fields would avoid Opera limitations of 1000 records
    if (StringUtils.isNotBlank(bookingSearchCriteria.getBookerEmail()) || StringUtils.isNotBlank(
        bookingSearchCriteria.getBookerPhone()) || StringUtils.isNotBlank(bookingSearchCriteria.getCompanyName())) {
      return SearchBookingsInput.builder().searchBookingsType(SearchBookingsType.BOOKER_SEARCH)
          .searchFields(buildBookerSearchFields(bookingSearchCriteria))
          .filterChain(new SearchBookingsResultsFilterChain((buildBookerSearchResultsFilters(bookingSearchCriteria))))
          .build();
    }
    // if hotel id is provided we search around hotel
    if (StringUtils.isNotBlank(bookingSearchCriteria.getHotelId())) {
      return SearchBookingsInput.builder().searchBookingsType(SearchBookingsType.HOTEL_SEARCH)
          .searchFields(buildHotelSearchFields(bookingSearchCriteria))
          .filterChain(new SearchBookingsResultsFilterChain(buildHotelSearchResultsFilters(bookingSearchCriteria)))
          .build();
    }
    // search by reservation fields
    if (StringUtils.isNotBlank(bookingSearchCriteria.getArrivalDate()) || StringUtils.isNotBlank(
        bookingSearchCriteria.getCancellationDate())) {
      return SearchBookingsInput.builder().searchBookingsType(SearchBookingsType.RESERVATION_SEARCH)
          .searchFields(buildReservationSearchFields(bookingSearchCriteria))
          .filterChain(
              new SearchBookingsResultsFilterChain((buildReservationSearchResultsFilters(bookingSearchCriteria))))
          .build();
    }
    // searching by booker surname should be removed when limitation of minimum 3 search fields will be implemented
    if (StringUtils.isNotBlank(bookingSearchCriteria.getBookerLastName())) {
      return SearchBookingsInput.builder().searchBookingsType(SearchBookingsType.BOOKER_SEARCH)
          .searchFields(buildBookerSearchFields(bookingSearchCriteria))
          .filterChain(new SearchBookingsResultsFilterChain((buildBookerSearchResultsFilters(bookingSearchCriteria))))
          .build();
    }

    return SearchBookingsInput.builder().searchBookingsType(SearchBookingsType.INVALID)
        .searchFields(Collections.emptyMap()).build();
  }

  // Hotel related search fields and filters
  private static Map<String, List<String>> buildHotelSearchFields(
      BookingSearchCriteria bookingSearchCriteria) {
    final var searchFields = new HashMap<String, List<String>>();

    if (StringUtils.isNotBlank(bookingSearchCriteria.getHotelId())) {
      searchFields.put(HOTEL_ID_PARAM, singletonList(bookingSearchCriteria.getHotelId()));
    }
    if (StringUtils.isNotBlank(bookingSearchCriteria.getBookingReference())) {
      searchFields.put(EXTERNAL_REFERENCE_IDS_PARAM, singletonList(bookingSearchCriteria.getBookingReference()));
    }
    if (StringUtils.isNotBlank(bookingSearchCriteria.getArrivalDate())) {
      searchFields.put(ARRIVAL_DATE_PARAM, singletonList(bookingSearchCriteria.getArrivalDate()));
    }
    if (StringUtils.isNotBlank(bookingSearchCriteria.getCancellationDate())) {
      searchFields.put(CANCELLATION_DATE_PARAM, singletonList(bookingSearchCriteria.getCancellationDate()));
    }
    if (StringUtils.isNotBlank(bookingSearchCriteria.getThirdPartyBookingReferenceNumber())) {
      searchFields.put(EXTERNAL_REFERENCE_IDS_PARAM,
          singletonList(bookingSearchCriteria.getThirdPartyBookingReferenceNumber()));
    }

    return searchFields;
  }

  private static List<SearchBookingsResultsFilter> buildHotelSearchResultsFilters(
      BookingSearchCriteria bookingSearchCriteria) {
    final var hotelSearchFilters = new ArrayList<SearchBookingsResultsFilter>();

    if (StringUtils.isNotBlank(bookingSearchCriteria.getBookerLastName())) {
      hotelSearchFilters.add(
          new BookerLastNameSearchBookingsResultsFilter(bookingSearchCriteria.getBookerLastName()));
    }
    if (StringUtils.isNotBlank(bookingSearchCriteria.getBookerEmail())) {
      hotelSearchFilters.add(new BookerEmailSearchBookingsResultsFilter(bookingSearchCriteria.getBookerEmail()));
    }
    if (StringUtils.isNotBlank(bookingSearchCriteria.getBookerPhone())) {
      hotelSearchFilters.add(new BookerPhoneSearchBookingsResultsFilter(bookingSearchCriteria.getBookerPhone()));
    }
    if (StringUtils.isNotBlank(bookingSearchCriteria.getBookerPostcode())) {
      hotelSearchFilters.add(
          new BookerPostcodeSearchBookingsResultsFilter(bookingSearchCriteria.getBookerPostcode()));
    }
    if (StringUtils.isNotBlank(bookingSearchCriteria.getGuestLastName())) {
      hotelSearchFilters.add(
          new GuestLastNameSearchBookingsResultsFilter(bookingSearchCriteria.getGuestLastName()));
    }
    if (StringUtils.isNotBlank(bookingSearchCriteria.getCompanyName())) {
      hotelSearchFilters.add(new CompanyNameSearchBookingsResultsFilter(bookingSearchCriteria.getCompanyName()));
    }

    hotelSearchFilters.add(new DepartureDateBookingsResultsFilter());

    return hotelSearchFilters;
  }

  // Reservation related search fields and filters
  private static Map<String, List<String>> buildReservationSearchFields(
      BookingSearchCriteria bookingSearchCriteria) {
    final var searchFields = new HashMap<String, List<String>>();

    if (StringUtils.isNotBlank(bookingSearchCriteria.getBookingReference())) {
      if (StringUtils.isNotBlank(bookingSearchCriteria.getThirdPartyBookingReferenceNumber())) {
        searchFields.put(EXTERNAL_REFERENCE_IDS_PARAM, Arrays.asList(bookingSearchCriteria.getBookingReference(),
            bookingSearchCriteria.getThirdPartyBookingReferenceNumber()));
      } else {
        searchFields.put(EXTERNAL_REFERENCE_IDS_PARAM, singletonList(bookingSearchCriteria.getBookingReference()));
      }
    } else if (StringUtils.isNotBlank(bookingSearchCriteria.getThirdPartyBookingReferenceNumber())) {
      searchFields.put(EXTERNAL_REFERENCE_IDS_PARAM,
          singletonList(bookingSearchCriteria.getThirdPartyBookingReferenceNumber()));
    }
    if (StringUtils.isNotBlank(bookingSearchCriteria.getArrivalDate())) {
      searchFields.put(ARRIVAL_DATE_PARAM, singletonList(bookingSearchCriteria.getArrivalDate()));
    }
    if (StringUtils.isNotBlank(bookingSearchCriteria.getCancellationDate())) {
      searchFields.put(CANCELLATION_DATE_PARAM, singletonList(bookingSearchCriteria.getCancellationDate()));
    }

    return searchFields;
  }

  private static List<SearchBookingsResultsFilter> buildReservationSearchResultsFilters(
      BookingSearchCriteria bookingSearchCriteria) {
    final var reservationSearchFilters = new ArrayList<SearchBookingsResultsFilter>();

    if (StringUtils.isNotBlank(bookingSearchCriteria.getHotelId())) {
      reservationSearchFilters.add(
          new HotelIdSearchBookingsResultsFilter(bookingSearchCriteria.getHotelId()));
    }
    if (StringUtils.isNotBlank(bookingSearchCriteria.getBookerLastName())) {
      reservationSearchFilters.add(
          new BookerLastNameSearchBookingsResultsFilter(bookingSearchCriteria.getBookerLastName()));
    }
    if (StringUtils.isNotBlank(bookingSearchCriteria.getBookerEmail())) {
      reservationSearchFilters.add(new BookerEmailSearchBookingsResultsFilter(bookingSearchCriteria.getBookerEmail()));
    }
    if (StringUtils.isNotBlank(bookingSearchCriteria.getBookerPhone())) {
      reservationSearchFilters.add(new BookerPhoneSearchBookingsResultsFilter(bookingSearchCriteria.getBookerPhone()));
    }
    if (StringUtils.isNotBlank(bookingSearchCriteria.getBookerPostcode())) {
      reservationSearchFilters.add(
          new BookerPostcodeSearchBookingsResultsFilter(bookingSearchCriteria.getBookerPostcode()));
    }
    if (StringUtils.isNotBlank(bookingSearchCriteria.getCompanyName())) {
      reservationSearchFilters.add(new CompanyNameSearchBookingsResultsFilter(bookingSearchCriteria.getCompanyName()));
    }
    if (StringUtils.isNotBlank(bookingSearchCriteria.getGuestLastName())) {
      reservationSearchFilters.add(
          new GuestLastNameSearchBookingsResultsFilter(bookingSearchCriteria.getGuestLastName()));
    }

    reservationSearchFilters.add(new DepartureDateBookingsResultsFilter());

    return reservationSearchFilters;
  }

  // Booker related search fields and filters
  private static Map<String, List<String>> buildBookerSearchFields(
      BookingSearchCriteria bookingSearchCriteria) {
    final var searchFields = new HashMap<String, List<String>>();

    searchFields.put(BOOKER_PROFILE_TYPE_PARAM, singletonList(CONTACT_PROFILE_TYPE));

    if (StringUtils.isNotBlank(bookingSearchCriteria.getBookerLastName())) {
      searchFields.put(BOOKER_PROFILE_NAME_PARAM, singletonList(bookingSearchCriteria.getBookerLastName()));
    }
    if (StringUtils.isNotBlank(bookingSearchCriteria.getBookerPostcode())) {
      searchFields.put(BOOKER_POSTCODE_PARAM, singletonList(bookingSearchCriteria.getBookerPostcode()));
    }
    if (StringUtils.isNotBlank(bookingSearchCriteria.getBookerEmail())) {
      if (StringUtils.isNotBlank(bookingSearchCriteria.getBookerPhone())) {
        searchFields.put(BOOKER_COMMUNICATION_PARAM,
            Arrays.asList(bookingSearchCriteria.getBookerEmail(), bookingSearchCriteria.getBookerPhone()));
      } else {
        searchFields.put(BOOKER_COMMUNICATION_PARAM, singletonList(bookingSearchCriteria.getBookerEmail()));
      }
    } else if (StringUtils.isNotBlank(bookingSearchCriteria.getBookerPhone())) {
      searchFields.put(BOOKER_COMMUNICATION_PARAM, singletonList(bookingSearchCriteria.getBookerPhone()));
    }
    if (StringUtils.isNotBlank(bookingSearchCriteria.getCompanyName())) {
      searchFields.put(BOOKER_PROFILE_NAME_PARAM, singletonList(bookingSearchCriteria.getCompanyName()));
    }
    return searchFields;
  }

  private static List<SearchBookingsResultsFilter> buildBookerSearchResultsFilters(
      BookingSearchCriteria bookingSearchCriteria) {
    final var reservationSearchFilters = new ArrayList<SearchBookingsResultsFilter>();

    if (StringUtils.isNotBlank(bookingSearchCriteria.getGuestLastName())) {
      reservationSearchFilters.add(
          new GuestLastNameSearchBookingsResultsFilter(bookingSearchCriteria.getGuestLastName()));
    }
    reservationSearchFilters.add(new DepartureDateBookingsResultsFilter());

    return reservationSearchFilters;
  }

}
