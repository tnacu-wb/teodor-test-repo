import { gql } from 'graphql-request';

export const UPDATE_REASON_FOR_STAY = gql`
  mutation updateReasonForStay(
    $basketReference: String!
    $reasonForStay: String!
    $hotelId: String!
    $country: String
    $language: String
    $arrivalDate: String
  ) {
    updateReasonForStay(
      updateReasonForStayRequest: {
        basketReference: $basketReference
        hotelId: $hotelId
        reasonForStay: $reasonForStay
        country: $country
        language: $language
        arrivalDate: $arrivalDate
      }
    ) {
      basketReference
    }
  }
`;
