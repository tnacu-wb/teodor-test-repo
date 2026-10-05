import { gql } from 'graphql-request';

export const UPDATE_ANCILLARIES_RATE_CODE = gql`
  mutation updateRateCode(
    $basketReferenceId: String!
    $rateCode: String!
    $hotelId: String!
    $startDate: String!
    $endDate: String!
    $roomType: [String!]!
    $currency: String!
    $adultsNumber: [Int]!
    $childrenNumber: [Int]!
  ) {
    updateRateCode(
      rateCodeCriteria: {
        basketReferenceId: $basketReferenceId
        rateCode: $rateCode
        hotelId: $hotelId
        startDate: $startDate
        endDate: $endDate
        roomType: $roomType
        currency: $currency
        adultsNumber: $adultsNumber
        childrenNumber: $childrenNumber
      }
    )
  }
`;
