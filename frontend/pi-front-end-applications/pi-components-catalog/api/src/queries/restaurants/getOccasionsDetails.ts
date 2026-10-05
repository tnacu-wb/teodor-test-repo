import { gql } from 'graphql-request';

export const GET_OCCASIONS_DETAILS = gql`
  query occasionsQuery($siteId: String!) {
    occasions(siteId: $siteId) {
      occasions {
        available
        id
        name
      }
    }
  }
`;
