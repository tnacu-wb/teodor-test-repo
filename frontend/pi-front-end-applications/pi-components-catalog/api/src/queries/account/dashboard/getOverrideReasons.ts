import { gql } from 'graphql-request';

export const GET_OVERRIDE_REASONS = gql`
  query GetOverrideReasons($hotelId: String!) {
    cancellationReasons(hotelId: $hotelId) {
      cancellationReasons {
        active
        code
        description
        name
        managerApprovalNeeded
      }
    }
  }
`;
