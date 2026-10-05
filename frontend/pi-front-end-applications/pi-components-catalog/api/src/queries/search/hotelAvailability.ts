import { gql } from 'graphql-request';

export const GET_HOTEL_AVAILABILITY_QUERY = gql`
  query hotelAvailability(
    $hotelId: String!
    $arrival: String!
    $departure: String!
    $rooms: [RoomSearch!]!
    $bookingChannel: BookingChannelCriteria!
    $ratePlanCodes: [String]
    $originalBasketReference: String
    $brand: String
    $country: String
    $isPromoBox: Boolean
  ) {
    hotelAvailability(
      availabilitySearchCriteria: {
        arrival: $arrival
        departure: $departure
        rooms: $rooms
        hotel: { identifier: $hotelId }
        bookingChannel: $bookingChannel
        ratePlanCodes: $ratePlanCodes
        originalBasketReference: $originalBasketReference
        brand: $brand
        country: $country
        isPromoBox: $isPromoBox
      }
    ) {
      hotelId
      available
      roomRates {
        ratePlanCode
        cellCode
        roomTypes {
          roomType
          adults
          children
          rooms {
            pmsRoomType
            specialRequests
            roomPriceBreakdown {
              totalNetAmount
              currencyCode
            }
          }
        }
      }
      promotionsInformation {
        showPromo
        isWithinPromoWindow
        promotionCode
        landingPage
        promoBannerColour
        promoBannerIcon
        promoBannerTitle
        promoBannerSubtitle
        promoInvalidMessage
        promoExpiredMessage
        promoAmendMessage
        termsLink
        promoKind
        promoBoxStatus
        promoBoxMessageKey
        errorRateAndRoomMessage
        promoBannerVisibility
        promoBookingInfo {
          ratePlanCode
          promotionCode
        }
      }
    }
  }
`;
