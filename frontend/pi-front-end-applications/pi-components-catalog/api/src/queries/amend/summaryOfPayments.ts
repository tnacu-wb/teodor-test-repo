import { gql } from 'graphql-request';

export const GET_SUMMARY_OF_PAYMENTS = gql`
  query AmendSummary(
    $originalBasketRef: String!
    $copyBasketRef: String!
    $token: String!
    $bookingChannel: BookingChannelCriteria!
    $country: String!
  ) {
    amendSummary(
      originalBasketRef: $originalBasketRef
      copyBasketRef: $copyBasketRef
      token: $token
      bookingChannel: $bookingChannel
      country: $country
    ) {
      charitable
      previousTotal
      balancePaid
      payOnArrival
      refund
      nonRefundable
      totalCost
      balanceAuthorised
      navigationOptions {
        amendPaymentPage
      }
      paymentOptions {
        payNow
        payOnArrival
      }
      paymentCardDetails {
        cardNumberMasked
        token
        expirationDate
        cardType
        cardHolderName
        cardNumberLast4Digits
        cardLogoSrc
        cardName
      }
    }
  }
`;
