import { gql } from 'graphql-request';

export const GET_OUTLETS = gql`
  query outletQuery($location: String!, $id: String!) {
    outlets(location: $location, id: $id) {
      companies {
        sites {
          id
        }
      }
    }
  }
`;
