import { gql } from 'graphql-request';

export const DASHBOARD_UPDATE_OVERRIDE_RESERVATION = gql`
  mutation updateReservationOverrideReasons(
    $basketReference: String!
    $hotelId: String!
    $reasonCode: String!
    $reasonName: String!
    $callerName: String!
    $managerName: String
  ) {
    updateReservationOverrideReasons(
      updateReservationOverrideReasonsCriteria: {
        basketReference: $basketReference
        hotelId: $hotelId
        reasonName: $reasonName
        callerName: $callerName
        managerName: $managerName
        reasonCode: $reasonCode
      }
    ) {
      basketReference
    }
  }
`;
