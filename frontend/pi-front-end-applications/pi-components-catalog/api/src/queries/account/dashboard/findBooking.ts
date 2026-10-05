import { gql } from 'graphql-request';

export const FIND_BOOKING = gql`
  query findBooking($findBookingCriteria: FindBookingCriteria!) {
    findBooking(findBookingCriteria: $findBookingCriteria) {
      cookieName
      minutesTillExpiry
      redirectBase
      ref
      sourcePms
      token
      basketReference
      operaConfNumber
    }
  }
`;
