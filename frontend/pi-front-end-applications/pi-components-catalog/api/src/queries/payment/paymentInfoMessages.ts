import { gql } from 'graphql-request';

export const GET_PAYMENT_INFO_MESSAGES_QUERY = gql`
  query getPaymentInfoMessages(
    $country: String!
    $hotelId: String!
    $language: String!
    $rateCode: String!
    $bookingChannel: String!
  ) {
    paymentInfoMessages(
      bookingFlowCriteria: {
        country: $country
        hotelId: $hotelId
        language: $language
        rateCode: $rateCode
        bookingChannel: $bookingChannel
      }
    ) {
      paymentType
      messages
    }
  }
`;
