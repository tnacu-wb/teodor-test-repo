import { gql } from 'graphql-request';

export const GET_PROMO_INFORMATION = gql`
  query PromotionsInformation(
    $country: String!
    $language: String!
    $channel: String!
    $brand: String!
    $promotionCode: String
    $bookingDate: String
    $stayStartDate: String!
    $stayEndDate: String!
    $basketReference: String
    $isPromoBox: Boolean
    $rateName: String
    $roomClass: String
  ) {
    promotionsInformation(
      promotionsInformationCriteria: {
        country: $country
        language: $language
        channel: $channel
        brand: $brand
        promotionCode: $promotionCode
        bookingDate: $bookingDate
        stayStartDate: $stayStartDate
        stayEndDate: $stayEndDate
        basketReference: $basketReference
        isPromoBox: $isPromoBox
        rateName: $rateName
        roomClass: $roomClass
      }
    ) {
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
        whenCodeAlreadyApplied
        whenUnavailable
        whenCodeExpired
        whenMinRoomsNotMet
        whenMaxRoomsExceeded
      }
      promoKind
      promoBoxStatus
      promoBoxMessageKey
      errorRateAndRoomMessage
      promoBannerVisibility
    }
  }
`;
