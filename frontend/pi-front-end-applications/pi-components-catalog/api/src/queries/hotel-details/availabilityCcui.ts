import { gql } from 'graphql-request';

export const HOTEL_AVAILABILITY_CCUI_QUERY = gql`
  query hotelAvailabilityCcui(
    $hotelId: String!
    $arrival: String!
    $departure: String!
    $rooms: [RoomSearch!]!
    $brand: String
    $country: String
    $companyId: String
    $bookingChannel: BookingChannelCriteria!
    $promotionCode: String
    $promoKind: PromoKind
    $rateName: String
    $roomClass: String
    $isPromoBox: Boolean
  ) {
    hotelAvailability(
      availabilitySearchCriteria: {
        arrival: $arrival
        departure: $departure
        rooms: $rooms
        hotel: { identifier: $hotelId }
        bookingChannel: $bookingChannel
        companyId: $companyId
        promotionCode: $promotionCode
        promoKind: $promoKind
        isPromoBox: $isPromoBox
        rateName: $rateName
        roomClass: $roomClass
        brand: $brand
        country: $country
      }
    ) {
      hotelId
      startDate
      endDate
      available
      limitedAvailability
      mlos
      roomRates {
        ratePlanCode
        promotionCode
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
              baseRateAmount
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
        promoBox {
          title
          button
          whenInvalid
          whenMultipleRedeem
          whenSuccess
          whenEmpty
          whenCodeExpired
          whenUnavailable
          whenCodeAlreadyApplied
          whenMinRoomsNotMet
          whenMaxRoomsExceeded
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

export const RATE_INFORMATION_CCUI_QUERY = gql`
  query ratesInformationCcui(
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
