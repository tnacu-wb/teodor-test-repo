import { gql } from 'graphql-request';

export const DASHBOARD_CANCEL_RESERVATION = gql`
  mutation cancelReservation($cancellationCriteria: CancellationCriteria!) {
    cancelReservation(cancellationCriteria: $cancellationCriteria) {
      basketReference
    }
  }
`;
