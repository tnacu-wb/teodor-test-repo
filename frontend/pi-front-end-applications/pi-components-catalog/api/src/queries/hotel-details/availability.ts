import { gql } from 'graphql-request';

export const HOTEL_AVAILABILITY_QUERY = gql`
  query hotelAvailability(
    $hotelId: String!
    $arrival: String!
    $departure: String!
    $rooms: [RoomSearch!]!
    $brand: String!
    $country: String!
    $language: String!
    $bookingChannel: BookingChannelCriteria!
    $channel: String
    $ratePlanCodes: [String]
    $promotionCode: String
    $promoKind: PromoKind
    $softBundle: String
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
        ratePlanCodes: $ratePlanCodes
        promotionCode: $promotionCode
        country: $country
        promoKind: $promoKind
        softBundle: $softBundle
        isPromoBox: $isPromoBox
        rateName: $rateName
        roomClass: $roomClass
        brand: $brand
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
        cellCode
        roomTypes {
          roomType
          roomNumber
          adults
          children
          cotRequested
          rooms {
            roomType
            pmsRoomType
            silentSubstitution
            cotAvailable
            roomClass
            specialRequests
            isSubstitution
            substitution
            roomPriceBreakdown {
              totalNetAmount
              totalRoomNetAmount
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
                roomNetPrice
              }
            }
            numberOfRoomsAvailable
            softBundles {
              isOptional
              softBundleContent {
                name
                description
                id
                imageSrc
                attachments {
                  label
                  path
                  type
                }
                price
                strikeThrough
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
    ratesInformation: ratesInformationV2(
      brand: $brand
      country: $country
      language: $language
      hotelId: $hotelId
      channel: $channel
    ) {
      rateClassifications {
        rateClassification
        rateDescription
        rateName
        rateOrder
        rateCategory
        rateTags
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
