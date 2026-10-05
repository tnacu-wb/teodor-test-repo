import { gql } from 'graphql-request';

export const SAVE_ANCILLARIES_BOOKING_INFO = gql`
  mutation saveBookingInformation($ancillariesCriteria: AncillariesCriteria!) {
    saveReservation(ancillariesCriteria: $ancillariesCriteria)
  }
`;
