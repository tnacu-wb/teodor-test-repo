import { gql } from 'graphql-request';

export const PRE_CHECK_IN_STATUS = gql`
  mutation preCheckIn($hotelId: String!, $reservationId: String!, $arrivalTime: String!) {
    preCheckInStatus(
      preCheckInCriteria: {
        hotelId: $hotelId
        reservationId: $reservationId
        arrivalTime: $arrivalTime
      }
    ) {
      status
      message
    }
  }
`;
