import { gql } from 'graphql-request';

export const SEARCH_BOOKINGS_RESULTS = gql`
  query GetSearchBookingsResults(
    $bookingReference: String
    $bookerLastName: String
    $guestLastName: String
    $bookerPostcode: String
    $hotelId: String
    $bookerEmail: String
    $bookerPhone: String
    $arrivalDate: String
    $cancellationDate: String
    $companyName: String
    $thirdPartyBookingReferenceNumber: String
    $limit: Int
    $offset: Int
  ) {
    searchBookings(
      searchBookingsCriteria: {
        bookingReference: $bookingReference
        bookerLastName: $bookerLastName
        guestLastName: $guestLastName
        bookerPostcode: $bookerPostcode
        hotelId: $hotelId
        bookerEmail: $bookerEmail
        bookerPhone: $bookerPhone
        arrivalDate: $arrivalDate
        cancellationDate: $cancellationDate
        companyName: $companyName
        thirdPartyBookingReferenceNumber: $thirdPartyBookingReferenceNumber
        limit: $limit
        offset: $offset
      }
    ) {
      bookings {
        bookingReference
        currencyCode
        hotelId
        sourcePms
        status
        hotelName
        stayingGuests {
          firstName
          lastName
          title
        }
        totalCost
        booker {
          firstName
          lastName
          title
        }
        arrivalDate
        departureDate
      }
      limit
      hasMore
      offset
      totalPages
      totalResults
      responseLimitExceeded
    }
  }
`;

export const ENHANCED_SEARCH_BOOKINGS_RESULTS = gql`
  query GetEnhancedSearchBookingsResults(
    $bookingReference: String
    $bookerLastName: String
    $guestLastName: String
    $bookerPostcode: String
    $hotelId: String
    $bookerEmail: String
    $bookerPhone: String
    $arrivalDateFrom: String
    $arrivalDateTo: String
    $cancellationDate: String
    $companyName: String
    $thirdPartyBookingReferenceNumber: String
    $pageSize: Int
    $pageNumber: Int
    $bookingsDatabaseSearch: Boolean
  ) {
    searchBookingsCcui(
      searchBookingsCcuiCriteria: {
        bookingReference: $bookingReference
        bookerLastName: $bookerLastName
        arrivalDateFrom: $arrivalDateFrom
        arrivalDateTo: $arrivalDateTo
        guestLastName: $guestLastName
        bookerEmail: $bookerEmail
        hotelId: $hotelId
        bookerPostcode: $bookerPostcode
        bookerPhone: $bookerPhone
        cancellationDate: $cancellationDate
        companyName: $companyName
        thirdPartyBookingReferenceNumber: $thirdPartyBookingReferenceNumber
        pageSize: $pageSize
        pageNumber: $pageNumber
        bookingsDatabaseSearch: $bookingsDatabaseSearch
      }
    ) {
      results {
        bookingReference
        status
        hotelId
        hotelName
        sourceSystem
        arrivalDate
        departureDate
        totalCost
        currencyCode
        booker {
          firstName
          lastName
          title
        }
        guests {
          title
          firstName
          lastName
        }
      }
      operaConfNumber
      cdhSearchResults
      searchResults
      pageResults
      hasMore
      responseLimitExceeded
    }
  }
`;
