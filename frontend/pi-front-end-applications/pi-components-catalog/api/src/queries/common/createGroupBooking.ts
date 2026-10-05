import { gql } from 'graphql-request';

export const CREATE_GROUP_BOOKING = gql`
  mutation createGroupBooking(
    $hotelCode: String!
    $createGroupBookingCriteria: CreateGroupBookingCriteria!
  ) {
    createGroupBooking(
      hotelCode: $hotelCode
      createGroupBookingCriteria: $createGroupBookingCriteria
    ) {
      ticketNumber
      incidentId
    }
  }
`;
