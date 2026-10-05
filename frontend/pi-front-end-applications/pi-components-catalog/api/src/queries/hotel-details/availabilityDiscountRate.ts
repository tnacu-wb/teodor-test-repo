import { gql } from 'graphql-request';

export const HOTEL_AVAILABILITY_DISCOUNT_RATE_QUERY = gql`
  query hotelAvailabilityDiscountRate(
    $hotelId: String!
    $arrival: String!
    $departure: String!
    $rooms: [RoomSearch!]!
    $companyId: String
    $bookingChannel: BookingChannelCriteria!
  ) {
    hotelAvailability(
      availabilitySearchCriteria: {
        arrival: $arrival
        departure: $departure
        rooms: $rooms
        hotel: { identifier: $hotelId }
        bookingChannel: $bookingChannel
        companyId: $companyId
      }
    ) {
      hotelId
      startDate
      endDate
      available
      limitedAvailability
      roomRates {
        ratePlanCode
        roomTypes {
          roomType
          adults
          children
          cotRequested
          rooms {
            pmsRoomType
            silentSubstitution
            cotAvailable
            roomClass
            specialRequests
            roomPriceBreakdown {
              totalNetAmount
              currencyCode
              packageCode
              packageAmount
              totalCityTaxAmount
              effectiveRateAmount
              dailyPrices {
                date
                netPrice
                effectiveRate
              }
            }
          }
        }
      }
    }
    hotelInventory(hotelId: $hotelId, dateRangeEnd: $departure, dateRangeStart: $arrival) {
      roomTypeInventories {
        availableCount
        code
      }
    }
  }
`;

export const DISCOUNT_RATE_INFORMATION_QUERY = gql`
  query ratesInformationDiscountRate(
    $brand: String!
    $country: String!
    $language: String!
    $hotelId: String!
    $channel: String!
    $ratePlans: [String]
  ) {
    ratesInformation: ratesInformationV2(
      brand: $brand
      country: $country
      language: $language
      hotelId: $hotelId
      channel: $channel
      ratePlans: $ratePlans
    ) {
      rateClassifications {
        additionalDescription
        ratePlanCode
        rateOrder
        rateNotes
        rateName
        rateLongDescription
        rateDescription
        rateClassification
        rateCategory
        rateTags
      }
    }
  }
`;
