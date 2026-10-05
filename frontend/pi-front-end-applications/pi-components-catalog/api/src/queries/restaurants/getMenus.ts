import { gql } from 'graphql-request';

export const GET_MENUS = gql`
  query Menu(
    $from: String!
    $until: String!
    $siteId: String!
    $time: String!
    $ocassionId: String!
  ) {
    menu(from: $from, siteId: $siteId, until: $until, time: $time, ocassionId: $ocassionId) {
      menus {
        id
        name
        available
      }
    }
  }
`;
