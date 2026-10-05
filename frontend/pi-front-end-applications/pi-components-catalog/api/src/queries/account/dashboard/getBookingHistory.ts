import { gql } from 'graphql-request';

export const GET_BOOKING_HISTORY = gql`
  query getBookingHistory(
    $business: Boolean!
    $sortOrder: String!
    $pageSize: Int
    $pageIndex: Int
    $filterValue: String
    $filterType: String
    $bookingChannel: BookingChannelCriteria!
    $continuationToken: String
  ) {
    bookingHistory(
      bookingHistoryRequest: {
        business: $business
        sortOrder: $sortOrder
        pageSize: $pageSize
        pageIndex: $pageIndex
        filterValue: $filterValue
        filterType: $filterType
        bookingChannel: $bookingChannel
        continuationToken: $continuationToken
      }
    ) {
      continuationToken
      pageIndex
      pageSize
      totalSize
      totals {
        cancelled
        checkedIn
        past
        upcoming
      }
      bookings {
        rateName
        arrivalDate
        departureDate
        bookedBy
        leadGuest
        bookingReference
        historyRecordNumber
        bookingStatus
        hotelName
        noOfNights
        sourceSystem
        totalCost {
          amount
          currency
        }
        leadGuestSurname
        hotelCode
      }
    }
  }
`;
