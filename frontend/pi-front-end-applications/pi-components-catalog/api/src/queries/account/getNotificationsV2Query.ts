import { gql } from 'graphql-request';

export const getNotificationsV2Query = () => gql`
  query getNotificationsV2 {
    getNotificationsV2 {
      profileUpdateRequired
    }
  }
`;
