import { gql } from 'graphql-request';

export const AMEND_STAY_DATES = gql`
  mutation AmendStayDates(
    $tempBookingRef: String!
    $newStartDate: String!
    $newEndDate: String!
    $channel: Channel!
    $subchannel: String!
    $token: String!
    $language: String
    $brand: String
    $country: String
    $originalBasketReference: String
  ) {
    changeBookingDates(
      amendStayDatesCriteria: {
        tempBookingRef: $tempBookingRef
        newStartDate: $newStartDate
        newEndDate: $newEndDate
        bookingChannel: { channel: $channel, subchannel: $subchannel, language: $language }
        token: $token
        brand: $brand
        country: $country
        originalBasketReference: $originalBasketReference
      }
    ) {
      tempBasket
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
        appPromoBannerTitle
        appPromoBannerSubtitle
        appPromoInvalidMessage
        appPromoExpiredMessage
        appPromoAmendMessage
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
