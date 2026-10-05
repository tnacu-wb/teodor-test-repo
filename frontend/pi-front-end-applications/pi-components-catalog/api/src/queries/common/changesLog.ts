import { gql } from 'graphql-request';

export const RETRIEVE_CHANGES_LOG = gql`
  query retrieveChangesLog($hotelId: String!, $reservationId: String!, $limit: Int, $offset: Int) {
    retrieveChangesLog(
      hotelId: $hotelId
      reservationId: $reservationId
      limit: $limit
      offset: $offset
    ) {
      activityLog {
        activityLog {
          date
          time
          actionType
          actionDescription
          user
        }
        totalPages
        offset
        limit
        hasMore
        totalResults
      }
    }
  }
`;
